package ru.practicum.explorewithme.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BaseClient {

    protected final RestTemplate restTemplate;

    protected <T> ResponseEntity<Object> post(String path, T body){
        return makeAndSendRequest(HttpMethod.POST, path,null, body);
    }

    protected ResponseEntity<Object> get(String path, Map<String, Object> parameters){
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body){
        HttpEntity<T> httpEntity = new HttpEntity<>(body, getDefaultHeaders());
        ResponseEntity<Object> serverResponse;
        try{
            if (parameters != null){
                serverResponse = restTemplate.exchange(path, method, httpEntity, Object.class, parameters);
            } else {
                serverResponse = restTemplate.exchange(path, method, httpEntity, Object.class);
            }
        }catch (HttpStatusCodeException ex){
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsByteArray());
        }
        if (serverResponse.getStatusCode().is2xxSuccessful()) {
            return serverResponse;
        }
        return ResponseEntity.status(serverResponse.getStatusCode()).body(serverResponse.getBody());
    }

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
