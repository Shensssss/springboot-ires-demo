package tw.idv.shen.web.doctor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import tw.idv.shen.core.pojo.Core;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.doctor.entity.Schedule;
import tw.idv.shen.web.doctor.service.ScheduleService;

@RestController
@RequestMapping("doctor")
public class ScheduleController {
	
	@Autowired
	private ScheduleService scheduleService;
	
//	@PostMapping("addSchedule")
//	public ResponseEntity<Core> addSchedule(@RequestBody Schedule schedule, HttpSession session) {
//	    
//		Core core = new Core();
//        Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");
//        
//        if (loggedInClinic == null) {
//            core.setStatusCode(401);
//            core.setMessage("診所尚未登入");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
//        }
//        
//        schedule.setClinic(loggedInClinic);
//	    int result = scheduleService.addSchedule(schedule);
//	    if(result == 1){
//	        core.setMessage("休假資料儲存成功");
//	        core.setStatusCode(200);
//	        core.setData(scheduleService.showSchedule(schedule.getDoctor().getDoctorId()));
//	        return ResponseEntity.ok(core);
//
//	    } else {
//	        core.setMessage("休假資料新增失敗");
//	        core.setStatusCode(400);
//	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(core);
//	    }
//	}
	
	@PostMapping("addDateOff")
    public ResponseEntity<Core> addDateOff(@RequestBody Schedule schedule, HttpSession session) {
        Core core = new Core();
        Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");

        if (loggedInClinic == null) {
            core.setStatusCode(401);
            core.setMessage("診所尚未登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
        }

        schedule.setClinic(loggedInClinic);
        int result = scheduleService.addDateOff(schedule);
        if (result == 1) {
            core.setStatusCode(200);
            core.setMessage("單日休假新增成功");
            return ResponseEntity.ok(core);
        } else {
            core.setStatusCode(400);
            core.setMessage("單日休假新增失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(core);
        }
    }

    @PutMapping("editDateOff")
    public ResponseEntity<Core> editDateOff(@RequestBody Schedule schedule, HttpSession session) {
        Core core = new Core();
        Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");

        if (loggedInClinic == null) {
            core.setStatusCode(401);
            core.setMessage("診所尚未登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
        }

        int result = scheduleService.editDateOff(schedule);
        if (result == 1) {
            core.setStatusCode(200);
            core.setMessage("單日休假修改成功");
            return ResponseEntity.ok(core);
        } else {
            core.setStatusCode(400);
            core.setMessage("單日休假修改失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(core);
        }
    }

    @PostMapping("addWeeklyOff")
    public ResponseEntity<Core> addWeeklyOff(@RequestBody Schedule schedule, HttpSession session) {
        Core core = new Core();
        Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");

        if (loggedInClinic == null) {
            core.setStatusCode(401);
            core.setMessage("診所尚未登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
        }

        schedule.setClinic(loggedInClinic);
        int result = scheduleService.addWeeklyOff(schedule);
        if (result == 1) {
            core.setStatusCode(200);
            core.setMessage("週期性休假新增成功");
            return ResponseEntity.ok(core);
        } else {
            core.setStatusCode(400);
            core.setMessage("週期性休假新增失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(core);
        }
    }

    @PutMapping("editWeeklyOff")
    public ResponseEntity<Core> editWeeklyOff(@RequestBody Schedule schedule, HttpSession session) {
        Core core = new Core();
        Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");

        if (loggedInClinic == null) {
            core.setStatusCode(401);
            core.setMessage("診所尚未登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
        }

        int result = scheduleService.editWeeklyOff(schedule);
        if (result == 1) {
            core.setStatusCode(200);
            core.setMessage("週期性請假修改成功");
            return ResponseEntity.ok(core);
        } else {
            core.setStatusCode(400);
            core.setMessage("週期性請假修改失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(core);
        }
    }
	
	@GetMapping("getSchedule/{doctorId}")
	public ResponseEntity<Core> getSchedule(@PathVariable Integer doctorId, HttpSession session) {
		Core core = new Core();
		Clinic loggedInClinic = (Clinic) session.getAttribute("clinic");

		if (loggedInClinic == null) {
			core.setStatusCode(401);
			core.setMessage("診所尚未登入");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(core);
		}

        List<Schedule> schedules = scheduleService.showSchedule(doctorId);
        core.setStatusCode(200);
        core.setMessage("查詢成功");
        core.setData(schedules);
        return ResponseEntity.ok(core);
    }

}
