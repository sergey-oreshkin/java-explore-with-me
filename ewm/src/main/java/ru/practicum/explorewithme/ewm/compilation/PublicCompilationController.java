package ru.practicum.explorewithme.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewm.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.ewm.compilation.service.CompilationMapper;
import ru.practicum.explorewithme.ewm.compilation.service.CompilationService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {

    private final CompilationMapper mapper;

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                       @RequestParam(name = "size", defaultValue = "10") Integer size,
                                       @RequestParam(name = "pinned", required = false) Boolean pinned) {
        return mapper.toDto(compilationService.getAll(from, size, pinned));
    }

    @GetMapping("{compId}")
    public CompilationDto get(@PathVariable @NotNull Long compId) {
        return mapper.toDto(compilationService.getById(compId));
    }
}
