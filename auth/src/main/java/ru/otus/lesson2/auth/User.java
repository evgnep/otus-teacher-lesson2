package ru.otus.lesson2.auth;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
	private String name;
	private String password;
	private boolean admin;
	private boolean privilegedUser;
}
