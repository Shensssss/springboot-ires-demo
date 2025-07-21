package tw.idv.shen.web.appointment.controller;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import tw.idv.shen.web.appointment.dao.AppointmentDAO;
import tw.idv.shen.web.appointment.entity.Appointment;
import tw.idv.shen.web.appointment.service.AppointmentService;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicService;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService service;

    @Autowired
    private AppointmentDAO appointmentDAO;

    @Autowired
    private ClinicService clinicService;

    @GetMapping("/apiToday")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTodayAppointments(
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "date", required = false) String dateStr,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();
        Date baseDate;

        // 解析日期參數
        if (dateStr != null) {
            try {
                baseDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            } catch (Exception e) {
                response.put("status", "error");
                response.put("message", "日期格式錯誤，應為 yyyy-MM-dd");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            baseDate = new Date();
        }

        java.sql.Date queryDate = new java.sql.Date(normalizeDate(baseDate).getTime());
        int timePeriod = 1;
        List<Appointment> appointments;
        Clinic clinic = (Clinic) session.getAttribute("clinic");

        if (period != null && !period.isBlank()) {
            // 有指定時段
            if ("afternoon".equalsIgnoreCase(period)) {
                timePeriod = 2;
            } else if ("evening".equalsIgnoreCase(period)) {
                timePeriod = 3;
            }

            if (clinic != null && clinic.getClinicId() > 0) {
                int clinicId = clinic.getClinicId();
                appointments = service.getAppointmentsByClinicDateAndPeriod(clinicId, queryDate, timePeriod);
            } else {
                appointments = service.getAppointmentsByDateAndPeriod(queryDate, timePeriod);
            }
        } else {
            // 沒指定時段，從 session 判斷診所與時段
            if (clinic == null || clinic.getClinicId() == null) {
                response.put("status", "error");
                response.put("message", "Session 未包含 clinicId，請重新登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            int clinicId = clinic.getClinicId();
            timePeriod = service.resolveTimePeriod(clinic, LocalTime.now());

            appointments = service.getAppointmentsByClinicDateAndPeriod(clinicId, queryDate, timePeriod);
        }

        response.put("status", "success");
        response.put("message", "查詢成功");
        response.put("timePeriod", timePeriod);
        response.put("data", appointments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<?> getAppointmentHistory(HttpSession session, @RequestParam int patientId) {
        Clinic clinic = (Clinic) session.getAttribute("clinic");
        Integer clinicId = (clinic != null) ? clinic.getClinicId() : null;

        List<Appointment> list = service.getHistoryByPatientId(patientId, clinicId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/reserve")
    @ResponseBody
    public ResponseEntity<String> reserve(HttpServletRequest request, @RequestBody List<Appointment> appointments) {
        HttpSession session = request.getSession(false);
        Integer clinicId = null;
        appointments.forEach(a -> System.out.println(a));
        if (session != null && session.getAttribute("clinic") != null) {
            Clinic clinic = (Clinic) session.getAttribute("clinic");
            clinicId = clinic.getClinicId();
        } else if (!appointments.isEmpty()) {
            clinicId = appointments.get(0).getClinicId();
        }

        if (clinicId == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
                    .body("clinicId 不存在");
        }

        try {
            service.reserveAppointments(clinicId, appointments);
            return ResponseEntity
                    .ok() // 200
                    .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
                    .body("預約成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400
                    .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
                    .body("錯誤：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
                    .body("預約時發生錯誤：" + e.getMessage());
        }
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<?> update(@RequestBody Appointment appointment) {
        if (appointment.getAppointmentId() == null) {
            return ResponseEntity.badRequest().body("缺少 appointmentId");
        }

        Appointment updated = service.updateAppointment(appointment);
        if (updated == null) {
            return ResponseEntity.status(500).body("更新失敗");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "更新成功");

        Appointment newAppointment = appointmentDAO.selectById(updated.getAppointmentId());
        response.put("updateTime", newAppointment.getUpdateTime());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable("id") String appointmentId) {
        boolean success = service.deleteAppointment(appointmentId);
        return success ? ResponseEntity.ok("刪除成功") : ResponseEntity.status(500).body("刪除失敗");
    }

    private Date normalizeDate(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(sdf.format(date));
        } catch (Exception e) {
            return date;
        }
    }
}