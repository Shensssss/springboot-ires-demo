package tw.idv.shen.web.appointment.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.idv.shen.web.appointment.dao.NotificationDAO;
import tw.idv.shen.web.appointment.entity.Notification;
import tw.idv.shen.web.appointment.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationDAO notificationDAO;

    @Override
    @Transactional
    public String createNotification(Notification notification) {
        String type = notification.getNotificationType();
        String appointmentId = notification.getAppointment().getAppointmentId();

        boolean exists = notificationDAO.existsByTypeAndAppointment(type, appointmentId);

        if (exists) {
            return "已發送過通知";
        } else {
            notificationDAO.save(notification);
            return "通知已發送";
        }
    }

    @Override
    public Notification findById(String id) {
        return notificationDAO.findById(id);
    }

    @Override
    public List<Notification> findByPatientId(int patientId) {
        return notificationDAO.findByPatientId(patientId);
    }

    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        Notification n = notificationDAO.findById(notificationId);
        if (n != null && !Boolean.TRUE.equals(n.getReadStatus())) {
            n.setReadStatus(true);
            n.setReadDatetime(new Timestamp(System.currentTimeMillis()));
            notificationDAO.update(n);
        }
    }

	@Override
	@Transactional
	public boolean remove(String notificationId) {
		return notificationDAO.deleteById(notificationId) > 0;
	}
}


