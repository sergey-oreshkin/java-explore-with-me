package ru.practicum.explorewithme.request.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.request.db.Request;
import ru.practicum.explorewithme.request.db.RequestRepository;

@Component
@RequiredArgsConstructor
public class RequestFactoryImpl implements RequestFactory {

    private final RequestRepository requestRepository;

    @Override
    public Request getById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found", String.format("id=%d", id)));
    }
}
