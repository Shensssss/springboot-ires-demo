package tw.idv.shen.web.appointment.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.idv.shen.web.appointment.service.clinicNotificationService;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicService;

@Controller
@RequestMapping("clinic/clinicNotification")
public class clinicNotificationController {
	@Autowired
	private clinicNotificationService service;

	@Autowired
	private ClinicService clinicService;

	@PostMapping("/Search")
	@ResponseBody
	public List<Object[]> getDoctorName(@RequestBody Map<String, Object> payload) {
		String clinic_account = (String) payload.get("clinic_account");
		System.out.println("Clinic ID: " + clinic_account);

		// 先用clinic_account 抓回原本的物件
		List<Clinic> reqclinic = clinicService.getClinicByAccount(clinic_account);
		if (reqclinic == null || reqclinic.isEmpty()) {
			throw new RuntimeException("找不到診所資料");
		}
		// 抓回id
		int clinic_id = reqclinic.get(0).getClinicId();

		// 秀出此診所的所有通知
		List<Object[]> listNotifications = service.selectNotificationMsgByClinicId(clinic_id);

		// 通知object 定義欄位
		Map<String, Integer> columnIndexMap = Map.of("appointmentId", 0, "patientName", 1, "message", 2,
				"notificationType", 3, "readStatus", 4, "sentDatetime", 5);

		// get 欄位的數字
		int messageIndex = columnIndexMap.get("message");
		int patientNameIndex = columnIndexMap.get("patientName");

		// 替換message
		for (Object[] row : listNotifications) {
			String originalMessage = (String) row[messageIndex];
			row[messageIndex] = originalMessage.replace("您", (String) row[patientNameIndex]);
		}

		return listNotifications;
	}

	@PostMapping("/updateReadStatus")
	@ResponseBody
	public int updateReadStatus(@RequestBody Map<String, Object> payload) {
		String appointment_id = (String) payload.get("appointment_id");
		System.out.println("Appointment ID: " + appointment_id);

		return service.updateReadStatus(appointment_id);
	}

}
