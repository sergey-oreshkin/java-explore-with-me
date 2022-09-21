package ru.practicum.explorewithme.users.service;

import ru.practicum.explorewithme.users.db.User;

import java.util.List;

public interface UserService {
    List<User> get(Integer from, Integer size, List<Long> idx);

    User create(User user);

    void delete(Long id);
}
