package tw.idv.shen.web.clinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.RegisterService;




@Controller
@RequestMapping("clinic")
public class ForgetPasswordController{ 
	@Autowired
	private RegisterService registerService;
	
	@PostMapping("forgetPassword")
	@ResponseBody
	public Clinic findPassword(HttpServletRequest request, @RequestBody(required = false) Clinic clinic) {
		if(clinic == null) {
			clinic = new Clinic();
			clinic.setSuccessful(false);
			clinic.setMessage("無會員資料");
			return clinic;
		}
		clinic = registerService.findPassword(clinic);
		if (clinic != null && clinic.isSuccessful()) {
			if(request.getSession(false) != null) {
				request.changeSessionId();
			}
			 final HttpSession session = request.getSession();
			 session.setAttribute("clinic", clinic);
		}
		return clinic;
	}
}	