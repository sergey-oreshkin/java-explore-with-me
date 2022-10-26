package ru.practicum.explorewithme.ewm.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.ewm.exception.HttpClientException;
import ru.practicum.explorewithme.ewm.stats.dto.HitsDto;
import ru.practicum.explorewithme.ewm.stats.dto.StatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Service
public class StatsClient implements HttpClient {

    public static final long DEFAULT_PERIOD_IN_DAYS = 365;

    public final DateTimeFormatter formatter;
    private final String getPath;
    private final String postPath;
    private final String defaultAppName;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    @Autowired
    public StatsClient(@Value("${stats-server.url}") String baseUrl,
                       @Value("${stats-server.getPath}") String getPath,
                       @Value("${stats-server.postPath}") String postPath,
                       @Value("${app.name}") String appName,
                       @Value("${app.format.date-time}") String dateTimeFormat,
                       RestTemplateBuilder builder,
                       ObjectMapper objectMapper) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.getPath = getPath;
        this.postPath = postPath;
        this.defaultAppName = appName;
        this.objectMapper = objectMapper;
        this.formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
    }

    @Override
    public void hit(HttpServletRequest request) throws HttpClientException {
        final HttpEntity<StatsDto> requestBody = new HttpEntity<>(
                StatsDto.builder()
                        .app(defaultAppName)
                        .uri(request.getRequestURI())
                        .ip(request.getRemoteAddr())
                        .timestamp(LocalDateTime.now()).build()
        );

        final ResponseEntity<String> response = restTemplate.postForEntity(postPath, requestBody, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new HttpClientException(format("Stats server response code is %d, error: %s",
                    response.getStatusCode().value(), response.getBody()));
        }
    }

    @Override
    public List<HitsDto> get(Map<String, String> parameters) throws JsonProcessingException, HttpClientException {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        if (parameters.get("start") == null) {
            String startTime = LocalDateTime.now().minusDays(DEFAULT_PERIOD_IN_DAYS).format(formatter);
            parameters.put("start", startTime);
        }
        if (parameters.get("end") == null) {
            String endTime = LocalDateTime.now().format(formatter);
            parameters.put("end", endTime);
        }

        final ResponseEntity<String> response = restTemplate.getForEntity(getUriWithParams(getPath, parameters), String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            final String body = response.getBody();
            return objectMapper.readValue(body, new TypeReference<List<HitsDto>>() {
            });
        }
        throw new HttpClientException(format("Stats server response code is %d, error: %s",
                response.getStatusCode().value(), response.getBody()));
    }

    private String getUriWithParams(String uri, Map<String, String> params) {
        if (params == null) return uri;
        final StringBuilder result = new StringBuilder(uri);
        result.append("?");
        params.forEach((k, v) -> {
            result.append(k).append("=").append(v).append("&");

        });
        return result.toString();
    }
}
