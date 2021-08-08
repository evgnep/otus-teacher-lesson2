package ru.otus.lesson2.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {
	private static final List<User> users = List.of(
			new User().setName("vasya").setPassword("123").setAdmin(true).setPrivilegedUser(true),
			new User().setName("petya").setPassword("123").setPrivilegedUser(true),
			new User().setName("stepa").setPassword("123"));

	private final JwtCreator jwtCreator;

	@GetMapping("/authenticate")
	public String authenticate(String user, String password) {
		return users.stream()
				.filter(e -> e.getName().equals(user) && e.getPassword().equals(password))
				.findFirst()
				.map(jwtCreator::createJwt)
				.orElseThrow(AuthError::new);
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	public static class AuthError extends RuntimeException {};
}
