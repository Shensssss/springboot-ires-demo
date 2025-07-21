package tw.idv.shen.web.appointment.dao;

import java.util.List;

import javax.management.Notification;

public interface clinicNotificationDAO {

	List<Notification> findByClinicId(int clinic_id);

	List<Object[]> selectNotificationMsgByClinicId(int clinic_id);

	int updateReadStatus(String appointment_id);

}
