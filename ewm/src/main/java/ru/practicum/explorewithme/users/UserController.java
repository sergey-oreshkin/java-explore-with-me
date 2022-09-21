package ru.practicum.explorewithme.users;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.users.db.User;
import ru.practicum.explorewithme.users.dto.UserDto;
import ru.practicum.explorewithme.users.service.UserMapper;
import ru.practicum.explorewithme.users.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserServiceImpl userService;

    private final UserMapper mapper;

    @GetMapping
    public List<UserDto> get(@RequestParam(name = "idx", required = false) List<Long> idx,
                             @RequestParam(name = "from", required = false) Integer from,
                             @RequestParam(name = "size", required = false) Integer size) {
        System.out.println("idx - "+idx);
        return mapper.toDto(userService.get(from, size, idx));
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
