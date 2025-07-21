package tw.idv.shen.web.clinic.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import tw.idv.shen.core.pojo.Core;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicInfoService;

@RestController
@RequestMapping("clinic/clinicInfo")
public class ClinicInfoController{ 
	@Autowired
	private ClinicInfoService clinicInfoService;
	
	@PutMapping("editBasicInfo")
	public ResponseEntity<Core> editBasicInfo(@RequestBody Clinic clinic, HttpSession session) {
        Core core = new Core();

        Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");
        if (loggedInClinic == null) {
            core.setStatusCode(401);
            core.setMessage("診所尚未登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
        }

        clinic.setClinicId(loggedInClinic.getClinicId());
        int result = clinicInfoService.editClinic(clinic);

        if (result == 1) {
            core.setStatusCode(200);
            core.setMessage("基本資料更新成功");
            return ResponseEntity.ok(core);
        } else {
            core.setStatusCode(400);
            core.setMessage("基本資料更新失敗, 請聯絡管理員");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(core);
        }
    }
	
	@PutMapping("editBusinessHours")
	public ResponseEntity<Core> editBusinessHours(@RequestBody Map<String, Object> requestBody, HttpSession session) {
        Core core = new Core();

        Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");
        if (loggedInClinic == null) {
            core.setStatusCode(401);
            core.setMessage("診所尚未登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
        }

        Clinic clinic = new Clinic();
        clinic.setClinicId(loggedInClinic.getClinicId());
        clinic.setMorning((String) requestBody.get("morning"));
        clinic.setAfternoon((String) requestBody.get("afternoon"));
        clinic.setNight((String) requestBody.get("night"));
        clinic.setWeekMorning((String) requestBody.get("weekMorning"));
        clinic.setWeekAfternoon((String) requestBody.get("weekAfternoon"));
        clinic.setWeekNight((String) requestBody.get("weekNight"));

        int result = clinicInfoService.editBusinessHours(clinic);

        if (result == 1) {
            core.setStatusCode(200);
            core.setMessage("營業時間更新成功");
            return ResponseEntity.ok(core);
        } else {
            core.setStatusCode(400);
            core.setMessage("營業時間更新失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(core);
        }
    }
	
	@GetMapping("showInfo")
	public ResponseEntity<Core> showInfo(HttpSession session) {
	    Core core = new Core();

	    Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");
	    if (loggedInClinic == null) {
	        core.setStatusCode(401);
	        core.setMessage("診所尚未登入");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
	    }

	    Integer clinicId = loggedInClinic.getClinicId();
	    Clinic clinic = clinicInfoService.getClinicById(clinicId);
	    if (clinic == null) {
	        core.setStatusCode(404);
	        core.setMessage("查無診所資料");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(core);
	    }

	    core.setStatusCode(200);
	    core.setMessage("查詢成功");
	    core.setData(clinic);
	    return ResponseEntity.ok(core);
	}
	
	@GetMapping("getOpenPeriod")
	public ResponseEntity<Core> getOpenPeriod(HttpSession session) {
	    Core core = new Core();

	    Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");
	    if (loggedInClinic == null) {
	        core.setStatusCode(401);
	        core.setMessage("診所尚未登入");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
	    }

	    Map<String, String> openPeriod = clinicInfoService.getOpenPeriod(loggedInClinic.getClinicId());
	    if (openPeriod == null) {
	        core.setStatusCode(404);
	        core.setMessage("查無診所營業資料");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(core);
	    }

	    core.setStatusCode(200);
	    core.setMessage("營業時段查詢成功");
	    core.setData(openPeriod);
	    return ResponseEntity.ok(core);
	}
}