package ru.otus.lesson2.auth;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.lesson2.keys.KeysParser;

import java.security.interfaces.RSAPrivateKey;
import java.util.Map;

@Service
public class JwtCreator {
	private final RSAPrivateKey privateKey;

	public JwtCreator(@Value("${otus.auth.private}") String privateKey) {
		this.privateKey = KeysParser.getPrivateKeyFromString(privateKey);
	}

	public String createJwt(User user) {
		return Jwts.builder()
				.setSubject(user.getName())
				.addClaims(Map.of(
						"admin", user.isAdmin(),
						"privileged", user.isPrivilegedUser()))
				.signWith(privateKey)
				.compact();
	}
}
