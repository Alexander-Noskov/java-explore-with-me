package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final CompilationMapper compilationMapper;


    @Override
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        CompilationEntity compilationEntity = compilationMapper.toEntity(newCompilationDto);

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            compilationEntity.setEvents(eventService.getAllEventsByIds(newCompilationDto.getEvents()));
        }

        return compilationMapper.toCompilationDto(compilationRepository.save(compilationEntity));
    }

    @Override
    public void deleteById(Long id) {
        getById(id);
        compilationRepository.deleteById(id);
    }

    @Override
    public CompilationDto updateById(Long id, UpdateCompilationDto updateCompilationDto) {
        CompilationEntity compilationEntity = getById(id);
        if (updateCompilationDto.getEvents() != null) {
            compilationEntity.setEvents(eventService.getAllEventsByIds(updateCompilationDto.getEvents()));
        }
        if (updateCompilationDto.getPinned() != null) {
            compilationEntity.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getTitle() != null) {
            compilationEntity.setTitle(updateCompilationDto.getTitle());
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(compilationEntity));
    }

    @Override
    public CompilationDto getDtoById(Long id) {
        return compilationMapper.toCompilationDto(getById(id));
    }

    @Override
    public List<CompilationDto> getAllByParam(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.getCompilationsByParam(pinned, pageable).stream()
                .map(compilationMapper::toCompilationDto)
                .toList();
    }

    private CompilationEntity getById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
    }
}
