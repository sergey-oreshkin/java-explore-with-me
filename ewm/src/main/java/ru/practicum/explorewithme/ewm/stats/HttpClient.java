package ru.practicum.explorewithme.ewm.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.explorewithme.ewm.exception.HttpClientException;
import ru.practicum.explorewithme.ewm.stats.dto.HitsDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface HttpClient {
    void hit(HttpServletRequest request) throws HttpClientException;

    List<HitsDto> get(Map<String, String> parameters) throws JsonProcessingException, HttpClientException;
}
