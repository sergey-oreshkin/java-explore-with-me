package ru.practicum.explorewithme.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.users.db.UserRepository;
import ru.practicum.explorewithme.users.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;

    public List<UserDto> getAll() {
        return null;
    }

    public UserDto create(UserDto userDto) {
        return null;
    }

    public void delete(Long id) {

    }
}
