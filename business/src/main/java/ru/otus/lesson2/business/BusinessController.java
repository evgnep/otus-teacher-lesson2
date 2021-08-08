package ru.otus.lesson2.business;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.lesson2.business.auth.AuthError;
import ru.otus.lesson2.business.auth.Role;

@RestController
public class BusinessController {

	@GetMapping("/calculate")
	public Answer calculate(Integer a, Integer b, Authentication authentication) {
		if (authentication.getAuthorities().contains(Role.ADMIN)) {

		}
		else if (authentication.getAuthorities().contains(Role.PRIVILEGED)) {
			if (a < 0 || a > 100 || b < 0 || b > 100)
				throw new AuthError("Недостаточно прав - привилегированный пользователь");
		}
		else {
			if (a < 0 || a > 10 || b < 0 || b > 10)
				throw new AuthError("Недостаточно прав - пользователь");
		}

		var res = new Answer();
		res.setUser(authentication.getName());
		res.setAnswer(a + b);
		return res;
	}


	@Data
	public static class Answer {
		private String user;
		private Integer answer;
	}
}
