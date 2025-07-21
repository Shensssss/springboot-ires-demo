package tw.idv.shen.web.appointment.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.appointment.dao.reservequeryDAO;
import tw.idv.shen.web.appointment.entity.Appointment;

// 此檔案可以合在 AppointmentDAOImpl
@Repository
public class reservequeryDAOImpl implements reservequeryDAO {
	@PersistenceContext
	private Session session;

	@Override
	public int insert(Appointment appointment) {
		session.persist(appointment);
		return 1;
	}

	@Override
	public int deleteById(String id) {
		Appointment appointment = session.load(Appointment.class, id);
		session.remove(appointment);
		return 1;
	}

	@Override
	public int update(Appointment pojo) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Appointment selectById(String id) {
		return session.get(Appointment.class, id);
	}

	@Override
	public List<Appointment> selectAll() {

		final String hql = "FROM Appointment ORDER BY reserveNo";
		return session.createQuery(hql, Appointment.class).getResultList();
	}

	@Override
	public List<Object[]> findByclinicid_doctorid_DateAndPeriod(int clinic_id, int doctor_id, Date dateS, Date dateE,
			int timePeriod) {

		String hql = "SELECT  DATE_FORMAT(a.appointmentDate, '%Y/%m/%d')"
				+ ", case a.timePeriod when 1 then '上午' when 2 then '下午' when 3 then '晚上' else '無該時段' end "
				+ ", a.reserveNo, p.name, d.doctorName" + ", case a.status when 1 then '已報到' else '未報到' end "
				+ "FROM Appointment a " + "LEFT JOIN a.patient p " + "LEFT JOIN a.doctor d "
				+ "WHERE a.clinic.clinicId = :clinic_id " + "AND a.appointmentDate between :dateS and :dateE ";

		hql += (doctor_id != 0) ? "AND a.doctor.doctorId = :doctor_id " : "";
		hql += (timePeriod != 0) ? "AND a.timePeriod = :period " : "";
		hql += "ORDER BY a.appointmentDate,a.timePeriod,d.doctorName,a.reserveNo";

		System.out.println("查看hql結果:" + hql);

		Query<Object[]> objResult = session.createQuery(hql, Object[].class).setParameter("clinic_id", clinic_id)
				.setParameter("dateS", dateS).setParameter("dateE", dateE);

		// 增加參數
		if (doctor_id != 0) {
			objResult.setParameter("doctor_id", doctor_id);
		}
		if (timePeriod != 0) {
			objResult.setParameter("period", timePeriod);
		}

		return objResult.getResultList();
	}

}
