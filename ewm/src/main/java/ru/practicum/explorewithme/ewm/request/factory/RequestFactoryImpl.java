package ru.practicum.explorewithme.ewm.request.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.ewm.exception.NotFoundException;
import ru.practicum.explorewithme.ewm.request.db.Request;
import ru.practicum.explorewithme.ewm.request.db.RequestRepository;

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
