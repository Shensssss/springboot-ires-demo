package tw.idv.shen.web.doctor.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.doctor.dao.DoctorDao;
import tw.idv.shen.web.doctor.entity.Doctor;

@Repository
public class DoctorDaoImpl implements DoctorDao {
	@PersistenceContext
	private Session session;

	@Override
	public int insert(Doctor doctor) {
		session.persist(doctor);
		return 1;
	}

	@Override
	public int deleteById(Integer doctorId) {
		Doctor doctor = session.getReference(Doctor.class, doctorId);
		session.remove(doctor);
		return 1;
	}

	@Override
	public int update(Doctor newDoctor) {
		Doctor doctor = session.getReference(Doctor.class, newDoctor.getDoctorId());
		doctor.setDoctorName(newDoctor.getDoctorName());
		doctor.setEducation(newDoctor.getEducation());
		doctor.setExperience(newDoctor.getExperience());
		doctor.setMemo(newDoctor.getMemo());
		doctor.setProfilePicture(newDoctor.getProfilePicture());
		return 1;
	}

	@Override
	public Doctor selectById(Integer doctorId) {
		return session.get(Doctor.class, doctorId);
	}

	// 各診所會依clinicId去搜尋資料，似乎用不到此DAO方法
	@Override
	public List<Doctor> selectAll() {
		return session.createQuery("FROM Doctor", Doctor.class).list();
	}

	@Override
	public List<Doctor> selectAllByClinicId(Integer clinicId) {
		return session.createQuery("FROM Doctor d WHERE d.clinic.clinicId = :clinicId", Doctor.class)
				.setParameter("clinicId", clinicId).getResultList();
	}

	@Override
	public List<Doctor> selectByClinicIdAndDoctorName(Integer clinicId, String doctorName) {
		return session
				.createQuery("FROM Doctor d WHERE d.clinic.clinicId = :clinicId AND d.doctorName LIKE :name",
						Doctor.class)
				.setParameter("clinicId", clinicId).setParameter("name", "%" + doctorName + "%").getResultList();
	}

	@Override
	public List<Doctor> findDoctorsByKeyword(String keyword, int offset, int pageSize, int clinicId) {
		String hql = "FROM Doctor d WHERE d.clinic.clinicId = :clinicId "
				+ "AND d.doctorName LIKE :keyword ORDER BY d.doctorId ASC";
		return session.createQuery(hql, Doctor.class).setParameter("clinicId", clinicId)
				.setParameter("keyword", "%" + keyword + "%").setFirstResult(offset).setMaxResults(pageSize)
				.getResultList();
	}

	@Override
	public long countDoctorsByKeyword(String keyword, int clinicId) {
		String hql = "SELECT COUNT(d) FROM Doctor d WHERE d.clinic.clinicId = :clinicId "
				+ "AND d.doctorName LIKE :keyword";
		return session.createQuery(hql, Long.class).setParameter("clinicId", clinicId)
				.setParameter("keyword", "%" + keyword + "%").uniqueResult();
	}
}
