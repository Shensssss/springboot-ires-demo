package tw.idv.shen.web.appointment.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.idv.shen.web.appointment.service.reservequeryService;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicService;
import tw.idv.shen.web.doctor.entity.Doctor;
import tw.idv.shen.web.doctor.service.DoctorService;

@Controller
@RequestMapping("clinic/reservequery")
public class reservequeryController {
	@Autowired
	private reservequeryService service;

	@Autowired
	private ClinicService clinicService;
	
	@Autowired
	private DoctorService doctorService;

	@GetMapping("/SearchDoctor")
	@ResponseBody
	public List<Map<String, Object>> getDoctorName(@RequestParam String clinic_account){

		System.out.println("Clinic ID: " + clinic_account);

		// 先用clinic_account 抓回原本的物件
		List<Clinic> reqclinic = clinicService.getClinicByAccount(clinic_account);
	    if (reqclinic == null || reqclinic.isEmpty()) {
	        throw new RuntimeException("找不到診所資料");
	    }
		// 抓回id
		int clinic_id = reqclinic.get(0).getClinicId();
		
		// 秀出此診所的所有醫生
		List<Doctor>listDoctors = doctorService.showAllDoctors(clinic_id);
		
		List<Map<String, Object>> result = new ArrayList<>();
	    for (Doctor d : listDoctors) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("doctor_id", d.getDoctorId());
	        map.put("doctor_name", d.getDoctorName());
	        System.out.println("doctor_id"+ d.getDoctorId());
	        System.out.println("doctor_id"+ d.getDoctorName());
	        result.add(map);
	    }
	    return result;
	}

	@PostMapping("/result")
	@ResponseBody
	public List<Object[]> getTodayAppointments(@RequestBody Map<String, Object> payload) {

		System.out.println("Clinic ID: " + payload.get("clinic_id"));
		System.out.println("Doctor ID: " + payload.get("doctor_id"));
		System.out.println("Time Period: " + payload.get("time_period"));
		System.out.println("DateS: " + payload.get("dateS"));
		System.out.println("DateE: " + payload.get("dateE"));
		
		// 部分欄位允許無值，因此會帶入0
		int clinic_id = (int) payload.get("clinic_id");
		int doctor_id = (int) payload.get("doctor_id");
		int time_period = (int) payload.get("time_period");

		// string 轉 date
		Date dateS = normalizeDate((String) payload.get("dateS"));
		Date dateE = normalizeDate((String) payload.get("dateE"));

//		System.out.println("要跑getAppointmentsByclinicid_doctorid_DateAndPeriod()");
		return service.getAppointmentsByclinicid_doctorid_DateAndPeriod(clinic_id, doctor_id, dateS,dateE, time_period);
	}

	// 日期格式轉換
	private Date normalizeDate(String strdate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(strdate);
		} catch (Exception e) {
			throw new RuntimeException("日期格式錯誤，應為 yyyy-MM-dd，例如：2025-06-24", e);
		}
	}

}
