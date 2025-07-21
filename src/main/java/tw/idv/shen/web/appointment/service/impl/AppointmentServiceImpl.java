package tw.idv.shen.web.appointment.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import tw.idv.shen.web.appointment.entity.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.idv.shen.core.util.CommonUtil;
import tw.idv.shen.web.appointment.dao.AppointmentDAO;
import tw.idv.shen.web.appointment.entity.Appointment;
import tw.idv.shen.web.appointment.service.AppointmentService;
import tw.idv.shen.web.appointment.service.NotificationService;
import tw.idv.shen.web.clinic.dao.ClinicDAO;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.doctor.dao.DoctorDao;
import tw.idv.shen.web.doctor.entity.Doctor;
import tw.idv.shen.web.patient.entity.Patient;
import tw.idv.shen.web.patient.service.PatientService;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentDAO appointmentDAO;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorDao doctorDao;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ClinicDAO clinicDAO;

    @Override
    public void saveOrUpdate(Appointment appointment) {
        if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
            appointmentDAO.insert(appointment);
        } else {
            appointmentDAO.update(appointment);
        }
    }

    @Override
    public Appointment findById(String appointmentId) {
        return appointmentDAO.selectById(appointmentId);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentDAO.selectAll();
    }

    @Override
    public void deleteById(String appointmentId) {
        appointmentDAO.deleteById(appointmentId);
    }

    @Override
    public List<Appointment> getAppointmentsByDate(Date date) {
        return appointmentDAO.findByDate(date);
    }

    @Override
    public List<Appointment> getAppointmentsByDateAndPeriod(Date date, int timePeriod) {
        return appointmentDAO.findByDateAndPeriod(date, timePeriod);
    }

    @Override
    public List<Appointment> getAppointmentsByClinicDateAndPeriod(Integer clinicId, Date date, int timePeriod) {
        return appointmentDAO.findByClinicDateAndPeriod(clinicId, date, timePeriod);
    }

    public List<Appointment> getHistoryByPatientId(int patientId, Integer clinicId) {
        return appointmentDAO.findByPatientIdAndClinicId(patientId, clinicId);
    }

    @Override
    public void save(Appointment appointment) {
        appointmentDAO.insert(appointment);
    }

    @Override
    public Appointment updateAppointment(Appointment a) {
        if (a.getAppointmentId() == null) return null;

        Appointment origin = appointmentDAO.selectById(a.getAppointmentId());
        if (origin == null) return null;

        boolean dateChanged = false, periodChanged = false, doctorChanged = false;

        if (a.getAppointmentDate() != null && !a.getAppointmentDate().equals(origin.getAppointmentDate())) {
            dateChanged = true;
            origin.setAppointmentDate(a.getAppointmentDate());
        }
        if (a.getTimePeriod() != null && !a.getTimePeriod().equals(origin.getTimePeriod())) {
            periodChanged = true;
            origin.setTimePeriod(a.getTimePeriod());
        }
        if (a.getDoctorId() != null && !a.getDoctorId().equals(origin.getDoctorId())) {
            doctorChanged = true;
            origin.setDoctorId(a.getDoctorId());
        }

        if (dateChanged || periodChanged || doctorChanged) {
            int newReserveNo = commonUtil.getNextReserveNo(
                    origin.getClinicId(),
                    origin.getDoctorId(),
                    origin.getAppointmentDate(),
                    origin.getTimePeriod()
            );
            origin.setReserveNo(newReserveNo);
        }

        if (a.getStatus() != null) origin.setStatus(a.getStatus());
        if (a.getNotes() != null) origin.setNotes(a.getNotes());

        appointmentDAO.update(origin);
        return origin;
    }


    public boolean deleteAppointment(String id) {
        Appointment a = appointmentDAO.selectById(id);
        if (a == null) {
            return false;
        }
        appointmentDAO.deleteById(a.getAppointmentId());
        return true;
    }

    public void reserveAppointments(Integer clinicId, List<Appointment> appointments) {
        for (Appointment a : appointments) {

            if (a.getDoctorId() == null) {
                throw new IllegalArgumentException("缺少必要欄位：doctorId");
            }

            if (a.getPatientId() == null) {
                throw new IllegalArgumentException("缺少必要欄位：patientId");
            }

            if (a.getAppointmentDate() == null) {
                throw new IllegalArgumentException("缺少必要欄位：appointmentDate");
            }

            //重複預約判斷
            if (appointmentDAO.existsDuplicateAppointment(a.getPatientId(), a.getAppointmentDate())) {
                throw new IllegalArgumentException("重複預約");
            }

            //超出預約人數判斷
            Long existingCount = appointmentDAO.countAppointmentsByGroup(
                    clinicId, a.getDoctorId(), a.getAppointmentDate(), a.getTimePeriod()
            );

            Clinic clinic = clinicDAO.selectById(clinicId);
            int quota = clinic.getQuota();

            if (existingCount >= quota) {
                throw new IllegalArgumentException("該時段已達預約上限，無法新增預約");
            }

            a.setClinicId(clinicId);
            a.setAppointmentId(UUID.randomUUID().toString());

            a.setReserveNo(commonUtil.getNextReserveNo(
                    clinicId,
                    a.getDoctorId(),
                    a.getAppointmentDate(),
                    a.getTimePeriod()
            ));

            a.setFirstVisit(appointmentDAO.existsByPatientIdAndClinicId(a.getPatientId(), clinicId) ? 0 : 1);

            a.setStatus(0);

            appointmentDAO.insert(a);

            //預約成功後寫入一筆通知
            Patient patient = patientService.findById(a.getPatientId());
            Appointment appointment = appointmentDAO.selectById(a.getAppointmentId());
            Doctor doctor = doctorDao.selectById(a.getDoctorId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Notification notification = new Notification();
            notification.setNotificationId(UUID.randomUUID().toString());
            notification.setAppointment(appointment);
            notification.setPatient(patient);
            notification.setMessage("您已成功預約，看診日期：" + a.getAppointmentDate()
                    + "、時段：" + getTimePeriod(a.getTimePeriod())
                    + " 醫師：" + doctor.getDoctorName());
            notification.setMessage("您已成功預約，看診日期：" + sdf.format(a.getAppointmentDate())
                    + "、時段：" + getTimePeriod(a.getTimePeriod())
                    + "、醫師：" + doctor.getDoctorName());
            notification.setMessage("您已成功預約，看診日期：" + sdf.format(a.getAppointmentDate()) +
                    "、時段：" + getTimePeriod(a.getTimePeriod()) +
                    "、醫師：" + doctor.getDoctorName());
            notification.setSentDatetime(new Timestamp(System.currentTimeMillis()));
            notification.setReadStatus(false);
            notification.setNotificationType("預約成功通知");

            notificationService.createNotification(notification);
        }
    }

    private String getTimePeriod(Integer code) {
        switch (code) {
            case 1:
                return "早上";
            case 2:
                return "下午";
            case 3:
                return "晚上";
            default:
                return "未知";
        }
    }

    public int resolveTimePeriod(Clinic clinic, LocalTime now) {
        try {
            CommonUtil.TimeRange morning = CommonUtil.parseTimeRange(clinic.getMorning());
            CommonUtil.TimeRange afternoon = CommonUtil.parseTimeRange(clinic.getAfternoon());

            if (morning != null && now.isBefore(morning.end.plusSeconds(1))) {
                return 1;
            }

            if (afternoon != null && now.isBefore(afternoon.end.plusSeconds(1))) {
                return 2;
            }

            return 3;
        } catch (Exception e) {
            System.err.println("時段判斷失敗，回傳預設值（3）：" + e.getMessage());
            return 3;
        }
    }

    @Override
    public List<Appointment> findByPatientId(Integer patientId) {
        return appointmentDAO.findByPatientId(patientId);
    }
}
