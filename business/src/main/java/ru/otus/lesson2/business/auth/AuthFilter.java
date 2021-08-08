package ru.otus.lesson2.business.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.otus.lesson2.keys.KeysParser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthFilter extends OncePerRequestFilter {
	private final static String
		AUTH = "Authorization",
		BEARER = "Bearer ";

	private final JwtParser jwtParser;

	public AuthFilter(String publicKey) {
		jwtParser = Jwts.parserBuilder()
				.setSigningKey(KeysParser.getPublicKeyFromString(publicKey))
				.build();
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		var header = request.getHeader(AUTH);
		if (header == null)
			return true;

		return !header.startsWith(BEARER);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		var token = request.getHeader(AUTH).substring(BEARER.length());

		Claims claims;
		try {
			claims = jwtParser.parseClaimsJws(token).getBody();
		}
		catch (Exception e) {
			setErrorResponse(HttpStatus.FORBIDDEN, response, e);
			return;
		}

		SecurityContextHolder.getContext().setAuthentication(new ClaimsAuthentication(claims));

		filterChain.doFilter(request, response);
	}

	@SneakyThrows
	private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex){
		response.setStatus(status.value());
		response.setContentType("application/json");
		log.error("Auth error", ex);
		response.getWriter().format("{\"error\": \"%s\"}", ex.getMessage());
	}
}
