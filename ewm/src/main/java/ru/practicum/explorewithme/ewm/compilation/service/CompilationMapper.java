package ru.practicum.explorewithme.ewm.compilation.service;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewm.compilation.db.Compilation;
import ru.practicum.explorewithme.ewm.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.ewm.event.factory.EventFactory;
import ru.practicum.explorewithme.ewm.event.service.EventMapper;

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
