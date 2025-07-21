package tw.idv.shen.web.appointment.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.appointment.dao.AppointmentDAO;
import tw.idv.shen.web.appointment.entity.Appointment;

@Repository
public class AppointmentDAOImpl implements AppointmentDAO {

	@PersistenceContext
	private Session session;

	@Override
	public int insert(Appointment appointment) {
		session.save(appointment);
		return 1;
	}

	@Override
	public int update(Appointment appointment) {
		Appointment original = session.get(Appointment.class, appointment.getAppointmentId());
		if (original == null)
			return 0;

		if (appointment.getAppointmentDate() != null)
			original.setAppointmentDate(appointment.getAppointmentDate());

		if (appointment.getTimePeriod() != null)
			original.setTimePeriod(appointment.getTimePeriod());

		if (appointment.getDoctorId() != null)
			original.setDoctorId(appointment.getDoctorId());
		
		if (appointment.getStatus() != null)
	        original.setStatus(appointment.getStatus());

		original.setUpdateTime(new Timestamp(new Date().getTime()));

		session.update(original);
		return 1;
	}

	@Override
	public Appointment selectById(String id) {
		return session.get(Appointment.class, id);
	}

	@Override
	public List<Appointment> selectAll() {
		return session.createQuery("FROM Appointment", Appointment.class).list();
	}

	@Override
	public int deleteById(String id) {
		Appointment appointment = session.get(Appointment.class, id);
		if (appointment != null) {
			session.delete(appointment);
			return 1;
		}
		return 0;
	}

	@Override
	public List<Appointment> findByDate(Date date) {
		return session.createQuery("FROM Appointment WHERE appointmentDate = :today", Appointment.class)
				.setParameter("today", date).getResultList();
	}

	@Override
	public List<Appointment> findByDateAndPeriod(Date date, int timePeriod) {
		String hql = "SELECT a FROM Appointment a " +
				"JOIN FETCH a.doctor d " +
				"JOIN FETCH a.clinic c " +
				"JOIN FETCH a.patient p " +
				"WHERE a.appointmentDate = :date " +
				"AND a.timePeriod = :period " +
				"ORDER BY a.reserveNo ASC";

		return session.createQuery(hql, Appointment.class)
				.setParameter("date", date)
				.setParameter("period", timePeriod)
				.getResultList();
	}

	@Override
	public List<Appointment> findByClinicDateAndPeriod(Integer clinicId, Date date, int timePeriod) {
		String hql = "SELECT a FROM Appointment a " +
				"JOIN FETCH a.doctor d " +
				"JOIN FETCH a.clinic c " +
				"JOIN FETCH a.patient p " +
				"WHERE a.appointmentDate = :date " +
				"AND a.timePeriod = :period " +
				"AND c.clinicId = :clinicId " +
				"ORDER BY a.reserveNo ASC";

		return session.createQuery(hql, Appointment.class)
				.setParameter("date", date)
				.setParameter("period", timePeriod)
				.setParameter("clinicId", clinicId)
				.getResultList();
	}

	// 判斷病患是否有在該診所預約過
	public boolean existsByPatientIdAndClinicId(Integer patientId, Integer clinicId) {
		String hql = "SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :pid AND a.clinic.id = :cid";
		Long count = session.createQuery(hql, Long.class).setParameter("pid", patientId).setParameter("cid", clinicId)
				.getSingleResult();
		return count > 0;
	}

	// 判斷同一天是否有重複預約
	@Override
	public boolean existsDuplicateAppointment(int patientId, Date date) {
		String hql = "SELECT COUNT(*) FROM Appointment a " + "WHERE a.patient.patientId = :pid "
				+ "AND a.appointmentDate = :date ";

		Long count = session.createQuery(hql, Long.class).setParameter("pid", patientId).setParameter("date", date)
				.uniqueResult();

		return count != null && count > 0;
	}

	// 取得病患歷史預約紀錄
	@Override
	public List<Appointment> findByPatientId(int patientId) {
		String hql = "FROM Appointment a " + "JOIN FETCH a.doctor " + "JOIN FETCH a.clinic " + "JOIN FETCH a.patient "
				+ "WHERE a.patientId = :pid " + "ORDER BY a.appointmentDate DESC";
		return session.createQuery(hql, Appointment.class).setParameter("pid", patientId).getResultList();
	}

	// 取得診所病患歷史預約紀錄
	@Override
	public List<Appointment> findByPatientIdAndClinicId(int patientId, Integer clinicId) {
		String hql = "FROM Appointment a " +
				"JOIN FETCH a.doctor " +
				"JOIN FETCH a.clinic " +
				"JOIN FETCH a.patient " +
				"WHERE a.patientId = :pid " +
				"AND a.clinic.clinicId = :cid " +
				"ORDER BY a.appointmentDate DESC";

		return session.createQuery(hql, Appointment.class)
				.setParameter("pid", patientId)
				.setParameter("cid", clinicId)
				.getResultList();
	}

	// 判斷是否超出預約人數
	@Override
	public Long countAppointmentsByGroup(int clinicId, int doctorId, Date date, int timePeriod) {
		String hql = "SELECT COUNT(*) FROM Appointment a " + "WHERE a.clinic.clinicId = :cid "
				+ "AND a.doctor.doctorId = :did " + "AND DATE(a.appointmentDate) = :date "
				+ "AND a.timePeriod = :period";

		return session.createQuery(hql, Long.class).setParameter("cid", clinicId).setParameter("did", doctorId)
				.setParameter("date", date).setParameter("period", timePeriod).uniqueResult();
	}

	@Override
	public Appointment findByClinicIdPatientIdDateTimePeriod(Integer clinicId, Integer patientId, Date appointmentDate,
			Integer timePeriod) {

		String hql = "FROM Appointment a WHERE a.clinicId = :clinicId AND a.patientId = :patientId "
				+ "AND a.appointmentDate = :appointmentDate AND a.timePeriod = :timePeriod";

		List<Appointment> results = session.createQuery(hql, Appointment.class).setParameter("clinicId", clinicId)
				.setParameter("patientId", patientId).setParameter("appointmentDate", appointmentDate)
				.setParameter("timePeriod", timePeriod).getResultList();

		return results.isEmpty() ? null : results.get(0);
	}
}