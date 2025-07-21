package tw.idv.shen.web.clinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import tw.idv.shen.core.pojo.Core;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.RegisterService;

@Controller
@RequestMapping("clinic")
public class ResetPasswordController{ 
	@Autowired
	private RegisterService registerService;
	
	@PostMapping("resetPassword")
	@ResponseBody
	public Core resetPassword(HttpServletRequest request, @RequestBody Clinic reqClinic) {
	    Core core = new Core();

	    Clinic sessionClinic = (Clinic) request.getSession().getAttribute("clinic");
	    if (sessionClinic == null) {
	        core.setSuccessful(false);
	        core.setMessage("Session 過期或未驗證");
	        return core;
	    }

	    // 強制以 session 的帳號為準
	    reqClinic.setAccount(sessionClinic.getAccount());

	    String errMsg = registerService.resetPassword(reqClinic);
	    core.setSuccessful(errMsg == null);
	    core.setMessage(errMsg);
	    return core;
	}
}