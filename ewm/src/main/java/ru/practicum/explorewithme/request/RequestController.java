package ru.practicum.explorewithme.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.request.db.Request;
import ru.practicum.explorewithme.request.dto.RequestDto;
import ru.practicum.explorewithme.request.service.RequestMapper;
import ru.practicum.explorewithme.request.service.RequestServiceImpl;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestServiceImpl requestService;

    private final RequestMapper mapper;

    @PostMapping
    public RequestDto create(@PathVariable @NotNull Long userId, @RequestParam Long eventId) {
        Request request = mapper.getEntity(userId, eventId);
        return mapper.toDto(requestService.create(request));
    }

    @GetMapping
    public List<RequestDto> getAll(@PathVariable @NotNull Long userId) {
        return mapper.toDto(requestService.getAll(userId));
    }

    @PatchMapping("{requestId}/cancel")
    public RequestDto cancel(@PathVariable @NotNull Long userId, @PathVariable @NotNull Long requestId) {
        return mapper.toDto(requestService.cancel(userId, requestId));
    }
}
