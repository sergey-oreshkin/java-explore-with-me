package ru.practicum.explorewithme.ewm.users;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewm.users.db.User;
import ru.practicum.explorewithme.ewm.users.dto.UserDto;
import ru.practicum.explorewithme.ewm.users.service.UserMapper;
import ru.practicum.explorewithme.ewm.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    private final UserMapper mapper;

    @GetMapping
    public List<UserDto> get(@RequestParam(name = "ids", required = false) List<Long> ids,
                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return mapper.toDto(userService.get(from, size, ids));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = mapper.toEntity(userDto);
        return mapper.toDto(userService.create(user));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable @NotNull Long id) {
        userService.delete(id);
    }
}
