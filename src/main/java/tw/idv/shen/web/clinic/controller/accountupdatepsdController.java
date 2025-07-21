package tw.idv.shen.web.clinic.controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicService;

@Controller
@RequestMapping("clinic/accountupdatepsd")
public class accountupdatepsdController {

	@Autowired
	private ClinicService service;

	@PostMapping(value = "/api", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String clinicaccountupdatepsd(@RequestBody Map<String, Object> payload) {

		System.out.println("Clinic ID: " + payload.get("clinic_id"));
		System.out.println("oPassword: " + payload.get("oPassword"));
		System.out.println("nPassword: " + payload.get("nPassword"));
		System.out.println("confirmPassword: " + payload.get("confirmPassword"));

		Integer clinic_id = (Integer) payload.get("clinic_id");
		String oPassword = (String) payload.get("oPassword");
		String nPassword = (String) payload.get("nPassword");
		String confirmPassword = (String) payload.get("confirmPassword");

		System.out.println("再轉一次Clinic ID: " + clinic_id);
		// 先用clinic_id 抓回原本的物件
		Clinic reqclinic = service.selectById(clinic_id);
		if (reqclinic == null) {
			System.out.println("錯誤：找不到診所 ID " + clinic_id);
			
	    }
		else {
			System.out.println("有診所 ID，原本的密碼: " + reqclinic.getPassword());
			
		}
		// 抓回原本的密碼
		String clinic_psd = reqclinic.getPassword();
		
		System.out.println("測試OK");
		// 驗證密碼 原本的密碼 要等於 網頁的密碼
		if (Objects.equals(clinic_psd, oPassword)) {
			// 驗證新密碼 新密碼 要等於 確認密碼
			if (Objects.equals(nPassword, confirmPassword)) {
				reqclinic.setPassword(confirmPassword);
				System.out.println("執行結果" + service.editPsd(reqclinic));
				return service.editPsd(reqclinic)
						;
			} else {
				return "新密碼與確認密碼不符";
			}
		} else {
			return "舊密碼錯誤";
		}
	}

}
