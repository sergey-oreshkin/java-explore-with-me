package ru.practicum.explorewithme.users.service;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.users.db.User;
import ru.practicum.explorewithme.users.dto.UserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    List<UserDto> toDto(List<User> users);
    User toEntity(UserDto userDto);
}
