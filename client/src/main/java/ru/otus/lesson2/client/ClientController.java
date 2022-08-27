package ru.otus.lesson2.client;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@EnableConfigurationProperties(Paths.class)
@Slf4j
public class ClientController {
    private final Paths paths;
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    {
        restTemplate.getInterceptors().add(new AuthorizationInterceptor());
    }

    @GetMapping("/queryToken")
    public String queryToken(String user, String password) {
        var url = UriComponentsBuilder.fromHttpUrl(paths.getAuth() + "/authenticate")
                .queryParam("user", user)
                .queryParam("password", password)
                .toUriString();

        log.info("Query: {}", url);

        token = new RestTemplate().getForObject(url, String.class);

        log.info("Result: {}", token);

        return currentToken();
    }

    @GetMapping("/currentToken")
    public String currentToken() {
        if (token == null)
            return "no token";

        var lastPointPos = token.lastIndexOf('.'); // отрезаем подпись - нам не надо ее проверять
        return Jwts.parserBuilder().build().parseClaimsJwt(token.substring(0, lastPointPos+1)).toString();
    }

    @GetMapping("/fakeToken")
    public String fakeToken() {
        token = Jwts.builder()
                .setSubject("hacker")
                .addClaims(Map.of(
                        "admin", true,
                        "privileged", true))
                .signWith(SignatureAlgorithm.HS256, "password012345678901234567890123456789".getBytes(StandardCharsets.UTF_8))
                .compact();

        return currentToken();
    }

    @GetMapping("/calculate")
    public Integer calculate(Integer a, Integer b) {
        var url = UriComponentsBuilder.fromHttpUrl(paths.getBusiness() + "/calculate")
                .queryParam("a", a)
                .queryParam("b", b)
                .toUriString();


        log.info("Query: {} with token {}", url, token);
        var result = restTemplate.getForObject(url, Answer.class);
        log.info("Result: {}", result);

        return result.getAnswer();
    }

    @Data
    public static class Answer {
        private String user;
        private Integer answer;
    }

    private class AuthorizationInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().add("Authorization", "Bearer " + token);
            var response = execution.execute(request, body);
            return response;
        }
    }
}
