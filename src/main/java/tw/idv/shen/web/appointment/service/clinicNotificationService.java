package tw.idv.shen.web.appointment.service;

import java.util.List;

import javax.management.Notification;

public interface clinicNotificationService {
	List<Notification> findByClinicId(int clinic_id);

	List<Object[]> selectNotificationMsgByClinicId(int clinic_id);
	
	int updateReadStatus(String appointment_id);

}
