package tw.idv.shen.web.appointment.service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import tw.idv.shen.web.appointment.entity.Appointment;
import tw.idv.shen.web.clinic.entity.Clinic;

public interface AppointmentService {

    void saveOrUpdate(Appointment appointment);

    Appointment findById(String appointmentId);

    List<Appointment> findAll();

    void deleteById(String appointmentId);

    List<Appointment> getAppointmentsByDate(Date date);

    List<Appointment> getAppointmentsByDateAndPeriod(Date date, int timePeriod);

    List<Appointment> getAppointmentsByClinicDateAndPeriod(Integer clinicId, Date date, int timePeriod);

    List<Appointment> getHistoryByPatientId(int patientId, Integer clinicId);

    void save(Appointment appointment);

    Appointment updateAppointment(Appointment a);

    boolean deleteAppointment(String id);

    void reserveAppointments(Integer clinicId, List<Appointment> appointments);
    
    List<Appointment> findByPatientId(Integer patientId);

    int resolveTimePeriod(Clinic clinic, LocalTime now);
}