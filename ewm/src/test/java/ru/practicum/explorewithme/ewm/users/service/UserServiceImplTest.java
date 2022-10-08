package ru.practicum.explorewithme.ewm.users.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import ru.practicum.explorewithme.ewm.common.OffsetLimitPageable;
import ru.practicum.explorewithme.ewm.exception.ConflictException;
import ru.practicum.explorewithme.ewm.users.db.User;
import ru.practicum.explorewithme.ewm.users.db.UserRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final long DEFAULT_ID = 1L;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    User user = User.builder().id(DEFAULT_ID).build();

    @Test
    void get_shouldInvokeUserRepositoryFindAllA_whenIdxIsNull() {
        when(userRepository.findAll(any(OffsetLimitPageable.class))).thenReturn(Page.empty());

        userService.get(0, 10, null);

        verify(userRepository, only()).findAll(any(OffsetLimitPageable.class));
    }

    @Test
    void get_shouldInvokeUserRepositoryFindAllByIdIn_whenIdxIsNotNull() {
        when(userRepository.findAllByIdIn(eq(Collections.emptyList()), any(OffsetLimitPageable.class))).thenReturn(List.of(user));

        userService.get(0, 10, Collections.emptyList());

        verify(userRepository, only()).findAllByIdIn(eq(Collections.emptyList()), any(OffsetLimitPageable.class));
    }

    @Test
    void create_shouldReturnTheSameUser() {
        when(userRepository.save(user)).thenReturn(user);

        var result = userService.create(user);

        verify(userRepository, only()).save(user);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void create_shouldTrowConflictException_whenRepositoryThrowDataIntegrityViolationException() {
        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(ConflictException.class, () -> userService.create(user));
    }

    @Test
    void delete_shouldInvokeUserRepositoryDelete() {
        userService.delete(anyLong());

        verify(userRepository, only()).deleteById(anyLong());
    }
}