package tw.idv.shen.web.clinic.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import tw.idv.shen.web.clinic.entity.CallNumber;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicService;

@Controller
@RequestMapping("/callNumber")
public class CallNumberController {

    @Autowired
    private ClinicService clinicService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/init")
    public ResponseEntity<?> initCallNumber(
            @RequestParam Integer doctorId,
            @RequestParam Integer timePeriod,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false) Integer consultationStatus,
            HttpSession session) {

        try {
            Clinic clinic = (Clinic) session.getAttribute("clinic");
            if (clinic == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入或 session 遺失");
            }

            Integer clinicId = clinic.getClinicId();
            CallNumber result = clinicService.findOrCreateCallNumber(clinicId, doctorId, timePeriod, date);

            if (number != null) {
                result.setNumber(number);
            }

            if (consultationStatus != null) {
                result.setConsultationStatus(consultationStatus);
            }

            result.setUpdateId("admin");
            result.setUpdateTime(LocalDateTime.now());
            clinicService.saveCallNumber(result);

            String json = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);
            System.out.println("JSON輸出：\n" + json);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("後端處理錯誤：" + e.getMessage());
        }
    }

    @GetMapping("/listByClinic")
    @ResponseBody
    public List<CallNumber> getCallNumbersByClinicId(
            @RequestParam Integer clinicId,
            @RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        return clinicService.findCallNumbersByClinicId(clinicId, parsedDate);
    }
}
