package ru.practicum.explorewithme.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewm.compilation.db.Compilation;
import ru.practicum.explorewithme.ewm.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.ewm.compilation.service.CompilationMapper;
import ru.practicum.explorewithme.ewm.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationController {

    private final CompilationMapper mapper;

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto create(@Valid @RequestBody NewCompilationDto compilationDto) {
        Compilation compilation = mapper.toEntity(compilationDto);
        return mapper.toDto(compilationService.create(compilation));
    }

    @DeleteMapping("{compId}")
    public void delete(@PathVariable @NotNull Long compId) {
        compilationService.delete(compId);
    }

    @PatchMapping("{compId}/pin")
    public void pin(@PathVariable @NotNull Long compId) {
        compilationService.pin(compId, true);
    }

    @DeleteMapping("{compId}/pin")
    public void unpin(@PathVariable @NotNull Long compId) {
        compilationService.pin(compId, false);
    }

    @PatchMapping("{compId}/events/{eventId}")
    public void addEvent(@PathVariable @NotNull Long compId, @PathVariable @NotNull Long eventId) {
        compilationService.addEvent(compId, eventId);
    }

    @DeleteMapping("{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable @NotNull Long compId, @PathVariable @NotNull Long eventId) {
        compilationService.deleteEvent(compId, eventId);
    }
}
