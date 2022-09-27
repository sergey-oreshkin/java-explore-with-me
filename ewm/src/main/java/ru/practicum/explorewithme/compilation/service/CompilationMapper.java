package ru.practicum.explorewithme.compilation.service;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.compilation.db.Compilation;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.event.factory.EventFactory;
import ru.practicum.explorewithme.event.service.EventMapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {EventFactory.class, EventMapper.class}
)
public interface CompilationMapper {

    Compilation toEntity(NewCompilationDto compilationDto);

    CompilationDto toDto(Compilation compilation);

    List<CompilationDto> toDto(List<Compilation> compilations);
}
