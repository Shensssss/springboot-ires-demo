package tw.idv.shen.web.appointment.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.idv.shen.web.appointment.entity.Appointment;
import tw.idv.shen.web.appointment.entity.Notification;
import tw.idv.shen.web.appointment.service.AppointmentService;
import tw.idv.shen.web.appointment.service.NotificationService;
import tw.idv.shen.web.patient.entity.Patient;
import tw.idv.shen.web.patient.service.PatientService;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/create")
    @ResponseBody
    public List<String> createNotifications(@RequestBody List<Map<String, Object>> inputList) {

        List<String> resultMessages = new ArrayList<>();

        for (Map<String, Object> item : inputList) {
            try {
                int patientId = ((Number) item.get("patientId")).intValue();
                String appointmentId = (String) item.get("appointmentId");
                String message = (String) item.get("message");
                String notificationType = (String) item.get("notificationType");

                Patient patient = patientService.findById(patientId);
                Appointment appointment = appointmentService.findById(appointmentId);

                Notification n = new Notification();
                n.setNotificationId(UUID.randomUUID().toString());
                n.setPatient(patient);
                n.setAppointment(appointment);
                n.setMessage(message);
                n.setSentDatetime(new Timestamp(System.currentTimeMillis()));
                n.setReadStatus(false);
                n.setNotificationType(notificationType);

                String result = notificationService.createNotification(n);
                resultMessages.add(result);

            } catch (Exception e) {
                resultMessages.add("發生錯誤：" + e.getMessage());
                e.printStackTrace();
            }
        }

        return resultMessages;
    }
}