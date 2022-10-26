package ru.practicum.explorewithme.ewm.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewm.common.OffsetLimitPageable;
import ru.practicum.explorewithme.ewm.exception.ConflictException;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;
import ru.practicum.explorewithme.ewm.users.db.User;
import ru.practicum.explorewithme.ewm.users.db.UserRepository;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> get(Integer from, Integer size, @Nullable List<Long> idx) {
        final Pageable pageable = OffsetLimitPageable.of(from, size);
        if (idx == null) {
            return userRepository.findAll(pageable).getContent();
        }
        return userRepository.findAllByIdIn(idx, pageable);
    }

    @Override
    @Transactional
    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Email already in use", format("Email=%s", user.getEmail()));
        }
    }

    @Override
    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("User not found", format("Id=%d", id));
        }
    }
}
