package tw.idv.shen.web.patient.service.impl;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.idv.shen.web.appointment.dao.AppointmentDAO;
import tw.idv.shen.web.appointment.entity.Appointment;
import tw.idv.shen.web.clinic.dao.ClinicDAO;
import tw.idv.shen.web.patient.dao.PatientDao;
import tw.idv.shen.web.patient.entity.Patient;
import tw.idv.shen.web.patient.service.PatientService;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientServiceImpl.class);

    @Autowired
    private PatientDao dao;

    @Autowired
    private ClinicDAO clinicDAO;

    @Autowired
    private AppointmentDAO appointmentDAO;

    @Override
    public Patient register(Patient patient) {
        if (patient.getEmail() == null) {
            patient.setMessage("使用者Email不得為空");
            patient.setSuccessful(false);
            return patient;
        }
        if (patient.getPassword() == null || patient.getPassword().length() < 6) {
            patient.setMessage("密碼長度必須大於6");
            patient.setSuccessful(false);
            return patient;
        }
        if (patient.getName() == null) {
            patient.setMessage("使用者名稱不得為空");
            patient.setSuccessful(false);
            return patient;
        }
        if (patient.getGender() == 0) {
            patient.setMessage("使用者性別不得為空");
            patient.setSuccessful(false);
            return patient;
        }
        if (patient.getBirthday() == null) {
            patient.setMessage("使用者生日不得為空");
            patient.setSuccessful(false);
            return patient;
        }

        if (dao.selectByEmail(patient.getEmail()) != null) {
            patient.setMessage("此信箱已被註冊");
            patient.setSuccessful(false);
            return patient;
        }

        // 檢查手機是否已存在與格式
        if (patient.getPhone() != null && !patient.getPhone().isEmpty()) {

            if (!patient.getPhone().matches("^09\\d{8}$")) {
                patient.setMessage("電話格式錯誤");
                patient.setSuccessful(false);
                return patient;
            }

            Patient existsByPhone = dao.findByPhone(patient.getPhone());
            if (existsByPhone != null) {
                patient.setMessage("此電話已被註冊");
                patient.setSuccessful(false);
                return patient;
            }
        }
        dao.insert(patient);
        patient.setMessage("成功註冊");
        patient.setSuccessful(true);
        return patient;
    }

    @Override
    public Patient login(Patient patient) {
        String email = patient.getEmail();
        String password = patient.getPassword();
        if (email == null && password == null) {
            patient.setMessage("使用者信箱或密碼不得為空");
            patient.setSuccessful(false);
            return patient;
        }
        patient = dao.selectForLogin(email, password);
        if (patient == null) {
            Patient errorPatient = new Patient();
            errorPatient.setMessage("信箱或密碼錯誤");
            errorPatient.setSuccessful(false);
            return errorPatient;
        }
        patient.setMessage("成功登入");
        patient.setSuccessful(true);
        return patient;
    }

    @Override
    public Patient findById(int patientId) {
        return dao.findById(patientId);
    }

    @Override
    public void updatePatient(Patient patient) {
        dao.update(patient);
    }

    @Override
    public Patient edit(Patient patient) {
        updatePatient(patient);
        return findById(patient.getPatientId());
    }

    @Override
    public List<Patient> clinicSearch(String name, String birthday, String phone) {

        if (birthday != null && (phone == null || phone.isEmpty())) {
            return dao.searchedByNameAndBirthday(name, birthday);
        } else if ((birthday == null || birthday.isEmpty()) && phone != null) {
            return dao.searchedByNameAndPhone(name, phone);
        } else if (birthday != null && phone != null) {
            return dao.searchedByNameAndBirthdayAndPhone(name, birthday, phone); // 生日和電話條件
        } else {
            throw new IllegalArgumentException("查詢條件不足");
        }
    }

    public Map<String, Object> getReservedPatientsWithKeyword(Integer clinicId, String keyword, int page,
            int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Patient> patients = dao.findReservedPatientsByKeyword(keyword, offset, pageSize, clinicId);
        long total = dao.countReservedPatientsByKeyword(keyword, clinicId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Patient p : patients) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getPatientId());
            map.put("name", p.getName());
            map.put("phone", p.getPhone());
            result.add(map);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("patients", result);
        response.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return response;
    }

    @Override
    public Patient findByPhone(String phone) {
        return dao.findByPhone(phone);
    }

    @Override
    public boolean checkIn(Patient patient, String code) {
        Objects.requireNonNull(code, "QR code 不可為 null");

        String date = code.substring(0, 8);
        String agencyId = code.substring(8, 18);
        int timePeriod = Integer.parseInt(code.substring(18));

        Date appointmentDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setLenient(false);
            appointmentDate = new java.sql.Date(sdf.parse(date).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Invalid QR code date format: " + date, e);
        }

        Integer clinicId = clinicDAO.findClinicIdByAgencyId(agencyId);
        if (clinicId == null) {
            throw new IllegalArgumentException("找不到對應診所");
        }

        Appointment appointment = appointmentDAO.findByClinicIdPatientIdDateTimePeriod(clinicId, patient.getPatientId(),
                appointmentDate, timePeriod);
        if (appointment == null) {
            log.info("報到失敗：查無預約，clinicId={}, patientId={}, date={}, timePeriod={}",
                    clinicId, patient.getPatientId(), appointmentDate, timePeriod);
        } else {
            log.info("查到預約：appointmentId={}, status={}", appointment.getAppointmentId(), appointment.getStatus());

            if (appointment.getStatus() != 0) {
                log.info("報到失敗：該預約已經是 status={}, 無法報到", appointment.getStatus());
            }
        }

        if (appointment != null && appointment.getStatus() == 0) {
            appointment.setStatus(1);
            appointmentDAO.update(appointment);
            return true;
        }
        return false;
    }

    public int clinicEditPatientNotes(int patientId, String newNotes) {
        return dao.updateNotes(patientId, newNotes);
    }
}
