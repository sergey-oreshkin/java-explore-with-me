package ru.practicum.explorewithme.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.common.OffsetLimitPageable;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.users.db.User;
import ru.practicum.explorewithme.users.db.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;

    public List<User> get(Integer from, Integer size, List<Long> idx) {
        Pageable pageable = OffsetLimitPageable.of(from, size);
        if (idx == null) {
            return userRepository.findAll(pageable).getContent();
        }
        return userRepository.findAllByIdIn(idx, pageable);
    }

    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Email or name already in use",
                    String.format("email=%s, name=%s", user.getEmail(), user.getName()));
        }
    }

    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("User not found", String.format("id=%d", id));
        }
    }
}
