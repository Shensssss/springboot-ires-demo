package tw.idv.shen.web.patient.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.patient.entity.Patient;
import tw.idv.shen.web.patient.service.PatientService;

@Controller
@RequestMapping("patient")
public class getPatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/patientList")
    @ResponseBody
    public Map<String, Object> getPatients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String keyword,
            HttpSession session
    ) {
        Clinic clinic = (Clinic) session.getAttribute("clinic");
        if (clinic == null) {
            throw new RuntimeException("未登入診所");
        }

        int pageSize = 10;
        return patientService.getReservedPatientsWithKeyword(
                clinic.getClinicId(), keyword, page, pageSize
        );
    }

    @GetMapping("/findByPhone")
    @ResponseBody
    public ResponseEntity<?> findByPhone(@RequestParam String phone) {
        Patient patient = patientService.findByPhone(phone);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到病患");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("patientId", patient.getPatientId());
        result.put("name", patient.getName());
        return ResponseEntity.ok(result);
    }
}
