package ru.practicum.explorewithme.users.factory;

import ru.practicum.explorewithme.users.db.User;

public interface UserFactory {
    User getById(Long id);
}
