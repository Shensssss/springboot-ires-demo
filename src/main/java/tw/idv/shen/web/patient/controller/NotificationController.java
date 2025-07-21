package tw.idv.shen.web.patient.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.idv.shen.web.appointment.entity.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import tw.idv.shen.core.pojo.Core;
import tw.idv.shen.web.appointment.service.NotificationService;
import tw.idv.shen.web.patient.entity.Patient;

@RestController("patientNotificationController")
@RequestMapping("/notification")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	// 取得已發送的通知
	@GetMapping("/patient")
	public List<Map<String, Object>> getMyNotifications(HttpSession session) {
		Patient patient = (Patient) session.getAttribute("patient");
		List<Notification> notifications = notificationService.findByPatientId(patient.getPatientId());

		List<Map<String, Object>> result = new ArrayList<>();

		for (Notification notification : notifications) {
			Map<String, Object> data = new HashMap<>();
			data.put("type", notification.getNotificationType());
			data.put("message", notification.getMessage());
			data.put("notificationId", notification.getNotificationId());
			data.put("clinicName", notification.getAppointment().getClinic().getClinicName());

			result.add(data);
		}

		return result;
	}

	@DeleteMapping("/{notificationId}")
	@ResponseBody
	public Core remove(@PathVariable String notificationId) {
		Core core = new Core();

		if (notificationId == null) {
			core.setStatusCode(400);
			core.setMessage("請提供 notificationId");
			return core;
		}

		if (notificationService.remove(notificationId)) {
			core.setStatusCode(200);
			core.setMessage("刪除成功");
			core.setSuccessful(true);
		} else {
			core.setStatusCode(404);
			core.setMessage("找不到通知");
			core.setSuccessful(false);
		}

		return core;
	}
}
