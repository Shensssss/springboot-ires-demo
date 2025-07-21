package tw.idv.shen.web.major.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import tw.idv.shen.core.pojo.Core;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.major.entity.Major;
import tw.idv.shen.web.major.service.ClinicMajorService;

@Controller
@RequestMapping(value = "clinicMajor")
public class ClinicMajorController {
	@Autowired
	private ClinicMajorService service;
	
	@GetMapping("list")
	@ResponseBody
	public List<Clinic> findClinicsByMajorIdOrAll(@RequestParam(required = false) Integer majorId){
        return service.getClinicsByMajorId(majorId);
	}
	
	@GetMapping("major")
	@ResponseBody
	public List<Major> findMajorByClinicId(@RequestParam(required = false) Integer clinicId){
		return service.getMajorByClinicId(clinicId);
	}
	
	@PutMapping("edit")
	@ResponseBody
	public Core editClinicMajor(@RequestBody List<Integer> selectedMajorIds, HttpSession session) {
		Core core = new Core();
		Clinic loggedinClinic = (Clinic) session.getAttribute("clinic");
	    if (loggedinClinic == null) {
	        core.setStatusCode(401);
	        core.setMessage("診所尚未登入");
	        return core;
	    }

	    int result = service.editClinicMajor(loggedinClinic.getClinicId(), selectedMajorIds);
	    if(result == 1) {
	    	core.setStatusCode(200);
		    core.setMessage("診所專科更新成功");
		    return core;
	    }else {
	    	core.setStatusCode(400);
	        core.setMessage("發生錯誤，請聯絡管理員");
	        return core;
	    }
	}
}
