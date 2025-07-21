package tw.idv.shen.web.patient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import jakarta.servlet.http.HttpSession;
import tw.idv.shen.web.patient.entity.Patient;
import tw.idv.shen.web.patient.service.PatientService;

@RestController
@RequestMapping("account")
public class AccountController {

	@Autowired
	private PatientService patientService;

	// 取得病患的帳戶資訊
	@GetMapping("patient")
	public Patient getInfo(@SessionAttribute(name = "patient", required = false) Patient patient) {
	    return patient;
	}

	// 回傳更新後的病患資料
	@PutMapping("patient")
	public Patient edit(@SessionAttribute("patient") Patient patient,
	                    @RequestBody Patient reqPatient,
	                    HttpSession session) {
	    reqPatient.setPatientId(patient.getPatientId());
	    patientService.edit(reqPatient);
	    Patient updated = patientService.findById(reqPatient.getPatientId());
	    session.setAttribute("patient", updated);
	    return updated;
	}
}