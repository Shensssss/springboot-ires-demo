package tw.idv.shen.web.appointment.dao;

import java.util.List;

import tw.idv.shen.web.appointment.entity.Notification;

public interface NotificationDAO {
    void save(Notification notification);
    Notification findById(String id);
    List<Notification> findByPatientId(int patientId);
    void update(Notification notification);
    boolean existsByTypeAndAppointment(String type, String appointmentId);
	List<Notification> findByPatientIdCheckedIn(Integer patientId);
	int deleteById(String notificationId);
}

