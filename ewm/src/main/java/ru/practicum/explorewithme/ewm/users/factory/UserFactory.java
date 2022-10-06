package ru.practicum.explorewithme.ewm.users.factory;

import ru.practicum.explorewithme.ewm.users.db.User;

public interface UserFactory {
    User getById(Long id);
}
