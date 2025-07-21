package tw.idv.shen.web.patient.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import tw.idv.shen.web.patient.entity.Patient;
import tw.idv.shen.web.patient.service.PatientService;

@RestController
@RequestMapping("checkIn")
public class CheckInController {

	@Autowired
	private PatientService patientService;

	@PostMapping
	public Map<String, Object> checkIn(@SessionAttribute Patient patient, @RequestBody Map<String, String> payload) {
	    String code = payload.get("appointmentId");
	    boolean updated = patientService.checkIn(patient, code);
	    return Map.of(
	        "success", updated,
	        "message", updated ? "報到成功" : "報到失敗，可能預約不存在或已報到"
	    );
	}
}
