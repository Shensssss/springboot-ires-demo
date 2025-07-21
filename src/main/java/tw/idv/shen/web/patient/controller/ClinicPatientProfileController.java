package tw.idv.shen.web.patient.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import tw.idv.shen.core.pojo.Core;
import tw.idv.shen.web.appointment.service.AppointmentService;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.patient.entity.Patient;
import tw.idv.shen.web.patient.service.PatientService;

@RestController
@RequestMapping("clinic")
public class ClinicPatientProfileController {
	@Autowired
	private PatientService patientService;
	@Autowired
	private AppointmentService appointmentService;
	
	@GetMapping("searchPatient")
	public ResponseEntity<Core> searchedPatient(
			@RequestParam("name") String name,
			@RequestParam(value = "birthday", required = false)String birthday,
			@RequestParam(value = "phone", required = false) String phone
			, HttpSession session){
		
		Core core = new Core();
		Boolean loggedin = (Boolean) session.getAttribute("loggedin");
		
		if (loggedin == null || !loggedin) {
	        core.setStatusCode(401);
	        core.setMessage("診所尚未登入");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
	    }else {
	    	if(name == null || name.isEmpty() || 
				((phone == null || phone.isEmpty()) && (birthday == null || birthday.isEmpty()))) {
				core.setStatusCode(400);
				core.setMessage("查詢條件不足");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(core);
			}else {
				List<Patient> patient = patientService.clinicSearch(name, birthday, phone);
				if(patient == null || patient.isEmpty()) {
		        	core.setStatusCode(404);
		        	core.setMessage("查無此病人");
		        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(core);
				}else {
					core.setStatusCode(200);
					core.setMessage("載入病人資料成功");
					core.setData(patient);
					return ResponseEntity.ok(core);
				}
			}
	    }
	}
	
			
	@PutMapping("editPatientNotes/{id}")
	public ResponseEntity<Core> editPatientNotes(@PathVariable("id") int patientId, @RequestBody Map<String, String> reqBody) {
	    String newNotes = reqBody.get("notes");
	    Core core = new Core();
	    
	    Patient patient = patientService.findById(patientId);
	    if (patient == null) {
	    	core.setStatusCode(404);
	        core.setMessage("查無此病人資料");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(core);
	    }

	    if (newNotes == null || newNotes.trim().isEmpty()) {
	    	newNotes = "";
	    }

	    int result = patientService.clinicEditPatientNotes(patientId, newNotes);
	    if (result > 0) {
	        core.setMessage("備註更新成功");
	        core.setStatusCode(200);
	        return ResponseEntity.ok(core);
	    } else {
	        core.setMessage("更新失敗，請聯絡管理員");
	        core.setStatusCode(500);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(core);
	    }
	}

	@GetMapping("appointmentHistory/{id}")
	@ResponseBody
	public ResponseEntity<Core> getAppointmentHistory(@PathVariable("id") int patientId, HttpSession session) {
		Core core = new Core();

		Patient patient = patientService.findById(patientId);
		if (patient == null) {
			core.setStatusCode(404);
			core.setMessage("查無此病人資料");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(core);
		}

		Clinic clinic = (Clinic) session.getAttribute("clinic");
		Integer clinicId = (clinic != null) ? clinic.getClinicId() : null;

		core.setStatusCode(200);
		core.setMessage("預約歷史載入成功");
		core.setData(appointmentService.getHistoryByPatientId(patientId, clinicId));

		return ResponseEntity.ok(core);
	}
}
