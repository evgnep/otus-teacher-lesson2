package ru.otus.lesson2.business.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
	ADMIN("admin"),
	PRIVILEGED("privileged");

	private final String name;

	@Override
	public String getAuthority() {
		return name;
	}
}
