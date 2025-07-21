package tw.idv.shen.web.appointment.dao;

import java.util.Date;
import java.util.List;

import tw.idv.shen.core.dao.CoreDao;
import tw.idv.shen.web.appointment.entity.Appointment;

public interface AppointmentDAO extends CoreDao<Appointment, String> {
    List<Appointment> findByDate(Date date);

    List<Appointment> findByDateAndPeriod(Date date, int timePeriod);

    List<Appointment> findByClinicDateAndPeriod(Integer clinicId, Date date, int timePeriod);

    boolean existsByPatientIdAndClinicId(Integer patientId, Integer clinicId);

    boolean existsDuplicateAppointment(int patientId, Date date);

    List<Appointment> findByPatientId(int patientId);

    List<Appointment> findByPatientIdAndClinicId(int patientId, Integer clinicId);

    Long countAppointmentsByGroup(int clinicId, int doctorId, Date date, int timePeriod);

    Appointment findByClinicIdPatientIdDateTimePeriod(Integer clinicId, Integer patientId, Date appointmentDate, Integer timePeriod);
}