package tw.idv.shen.web.appointment.dao;

import java.util.Date;
import java.util.List;

import tw.idv.shen.core.dao.CoreDao;
import tw.idv.shen.web.appointment.entity.Appointment;

//此檔案可以合在 AppointmentDAO

public interface reservequeryDAO extends CoreDao<Appointment, String> {


	List<Object[]> findByclinicid_doctorid_DateAndPeriod(int clinic_id, int doctor_id, Date dateS, Date dateE, int timePeriod);
}
