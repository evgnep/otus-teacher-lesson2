package ru.otus.lesson2.client;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@EnableConfigurationProperties(Paths.class)
@Slf4j
public class ClientController {
    private final Paths paths;
    private String token;

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
        // TODO token = ...

        return currentToken();
    }

    @GetMapping("/calculate")
    public Integer calculate(Integer a, Integer b) {
        var url = UriComponentsBuilder.fromHttpUrl(paths.getBusiness() + "/calculate")
                .queryParam("a", a)
                .queryParam("b", b)
                .toUriString();

        var headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        var entity = new HttpEntity<>(headers);

        log.info("Query: {} with token {}", url, token);
        var result = new RestTemplate().exchange(url, HttpMethod.GET, entity, Answer.class);
        log.info("Result: {}", result);

        return result.getBody().getAnswer();
    }

    @Data
    public static class Answer {
        private String user;
        private Integer answer;
    }
}
