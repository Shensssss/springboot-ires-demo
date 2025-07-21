package tw.idv.shen.web.appointment.service;

import java.util.List;

import tw.idv.shen.web.appointment.entity.Notification;

public interface NotificationService {
    String createNotification(Notification notification);
    Notification findById(String id);
    List<Notification> findByPatientId(int patientId);
    void markAsRead(String notificationId);
	boolean remove(String notificationId);
}
