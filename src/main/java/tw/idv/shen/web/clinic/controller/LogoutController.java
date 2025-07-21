package tw.idv.shen.web.clinic.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;


	@RestController
	@RequestMapping("clinic/logout")
	
	public class LogoutController {
	@DeleteMapping
	 public void logout(HttpSession session) {
        session.invalidate();
	}
	
}
