package tw.idv.shen.web.clinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.idv.shen.core.pojo.Core;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.RegisterService;

@RestController
@RequestMapping("clinic/register")
public class RegisterController {
	@Autowired
	private RegisterService registerService;

	@PostMapping
	public Core register(@RequestBody(required = false) Clinic clinic) {
		Core core = new Core();
		if(clinic != null) {
			String errMsg = registerService.register(clinic);
			core.setSuccessful(errMsg == null);
			if (errMsg != null) {
				core.setMessage(errMsg);
			}
		} else {
			core.setSuccessful(false);
			core.setMessage("無會員資料");
		}
		return core;
	}
}