package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto add(NewCompilationDto newCompilationDto);

    void deleteById(Long id);

    CompilationDto updateById(Long id, UpdateCompilationDto updateCompilationDto);

    CompilationDto getDtoById(Long id);

    List<CompilationDto> getAllByParam(Boolean pinned, Integer from, Integer size);
}
