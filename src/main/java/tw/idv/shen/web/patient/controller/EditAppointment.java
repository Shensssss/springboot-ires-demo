package tw.idv.shen.web.patient.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import tw.idv.shen.core.pojo.Core;
import tw.idv.shen.web.appointment.dao.AppointmentDAO;
import tw.idv.shen.web.appointment.entity.Appointment;
import tw.idv.shen.web.appointment.service.AppointmentService;
import tw.idv.shen.web.doctor.dao.DoctorDao;
import tw.idv.shen.web.doctor.entity.Doctor;

@RestController
@RequestMapping("/editAppointment")
public class EditAppointment {

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private DoctorDao doctorDao;

	@Autowired
	private AppointmentDAO appointmentDAO;

	@GetMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> getAppointment(@PathVariable String id) {
		Appointment appointment = appointmentDAO.selectById(id);
		if (appointment == null)
			return ResponseEntity.status(404).body("預約不存在");

		Doctor doctor = doctorDao.selectById(appointment.getDoctorId());
		String doctorName = (doctor != null) ? doctor.getDoctorName() : "未知醫師";
		List<Doctor> doctors = doctorDao.selectAllByClinicId(appointment.getClinic().getClinicId());

		Map<String, Object> result = new HashMap<>();
		result.put("appointmentId", appointment.getAppointmentId());
		result.put("appointmentDate", new SimpleDateFormat("yyyy-MM-dd").format(appointment.getAppointmentDate()));
		result.put("timePeriod", appointment.getTimePeriod());
		result.put("doctorId", appointment.getDoctorId());
		result.put("doctorName", doctorName);
		result.put("doctorList", doctors);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/allDoctor")
	public ResponseEntity<Core> getAllDoctorsByClinicId(@RequestParam int clinicId) {
		Core core = new Core();
		try {
			List<Doctor> doctors = doctorDao.selectAllByClinicId(clinicId);

			core.setStatusCode(200);
			core.setMessage("醫師資料載入成功");
			core.setData(doctors);

			return ResponseEntity.ok(core);
		} catch (Exception e) {
			core.setStatusCode(500);
			core.setMessage("載入醫師資料時發生錯誤：" + e.getMessage());
			return ResponseEntity.status(500).body(core);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<?> update(@RequestBody Appointment appointment) {
		if (appointment.getAppointmentId() == null) {
			return ResponseEntity.badRequest().body("缺少 appointmentId");
		}

		Appointment updated = appointmentService.updateAppointment(appointment);
		if (updated == null) {
			return ResponseEntity.status(500).body("更新失敗");
		}

		Map<String, Object> response = new HashMap<>();
		response.put("message", "更新成功");
		response.put("updateTime", appointmentDAO.selectById(updated.getAppointmentId()).getUpdateTime());

		return ResponseEntity.ok(response);
	}
}
