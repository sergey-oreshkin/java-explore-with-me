package ru.practicum.explorewithme.users.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.users.db.User;
import ru.practicum.explorewithme.users.db.UserRepository;

@RequiredArgsConstructor
@Component
public class UserFactoryImpl implements UserFactory {

    private final UserRepository userRepository;

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", String.format("id=%d", id)));
    }
}
