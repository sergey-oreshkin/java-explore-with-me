package ru.practicum.explorewithme.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.client.BaseClient;
import ru.practicum.explorewithme.stats.dto.StatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    public static final long DEFAULT_DAYS_AGO = 365;

    private final String getPath;
    private final String postPath;
    private final String appName;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String url, @Value("${stats-server.getPath}") String getPath,
                       @Value("${stats-server.postPath}") String postPath, @Value("${app.name}") String appName, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .build());
        this.getPath = getPath;
        this.postPath = postPath;
        this.appName = appName;
    }

    public void hit(HttpServletRequest request) {
        post(postPath, StatsDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()));
    }

    public ResponseEntity<Object> get(Map<String, Object> parameters) {
        if (parameters.get("start") == null) {
            parameters.put("start", LocalDateTime.now().minusDays(DEFAULT_DAYS_AGO));
        }
        if (parameters.get("end") == null) {
            parameters.put("end", LocalDateTime.now());
        }
        return get(getPath, parameters);
    }

}
