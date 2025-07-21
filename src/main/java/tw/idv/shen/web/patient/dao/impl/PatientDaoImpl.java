package tw.idv.shen.web.patient.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import tw.idv.shen.web.patient.dao.PatientDao;
import tw.idv.shen.web.patient.entity.Patient;

@Repository
public class PatientDaoImpl implements PatientDao {
	@PersistenceContext
	private Session session;

	@Override
	public int insert(Patient patient) {
		saveOrUpdate(patient);
		return 1;
	}

	@Override
	public Patient selectById(Integer id) {
		return session.get(Patient.class, id);
	}

	@Override
	public int deleteById(Integer id) {
		Patient patient = session.getReference(Patient.class, id);
		session.remove(patient);
		return 1;
	}

	@Override
	public int update(Patient patient) {
		final StringBuilder hql = new StringBuilder().append("UPDATE Patient SET ");
		final String password = patient.getPassword();
		if (password != null && !password.isEmpty()) {
			hql.append("password = :password,");
		}
		hql.append("name = :name,")
			.append("gender = :gender,")
			.append("birthday = :birthday,")
			.append("phone = :phone,")
			.append("address = :address,")
			.append("emergencyContent = :emergencyContent,")
			.append("emergencyName = :emergencyName,")
			.append("relation = :relation,")
			.append("bloodType = :bloodType,")
			.append("notes = :notes,")
			.append("profilePicture = :profilePicture, ")
			.append("updateTime = :updateTime ")
			.append("WHERE email = :email");

		MutationQuery query = session.createMutationQuery(hql.toString());
		if (password != null && !password.isEmpty()) {
			query.setParameter("password", password);
		}
		return query.setParameter("name", patient.getName())
					.setParameter("gender", patient.getGender())
					.setParameter("birthday", patient.getBirthday())
					.setParameter("phone", patient.getPhone())
					.setParameter("address", patient.getAddress())
					.setParameter("emergencyContent", patient.getEmergencyContent())
					.setParameter("emergencyName", patient.getEmergencyName())
					.setParameter("relation", patient.getRelation())
					.setParameter("bloodType", patient.getBloodType())
					.setParameter("notes", patient.getNotes())
					.setParameter("profilePicture", patient.getProfilePicture())
					.setParameter("updateTime", patient.getUpdateTime())
					.setParameter("email", patient.getEmail())
					.executeUpdate();
	}

	@Override
	public List<Patient> selectAll() {
		final String hql = "from Patient order by patientId";
		return session.createQuery(hql, Patient.class)
				.getResultList();
	}

	public void saveOrUpdate(Patient patient) {
	    session.merge(patient);
	}

	@Override
	public Patient selectByEmail(String email) {
		CriteriaBuilder cBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Patient> cQuery = cBuilder.createQuery(Patient.class);
		Root<Patient> root = cQuery.from(Patient.class);
		cQuery.where(cBuilder.equal(root.get("email"), email));

		return session.createQuery(cQuery).uniqueResult();
	}

	@Override
	public Patient selectForLogin(String email, String password) {
		String sql = "select * from patient where email = :email and password = :password";
		System.out.println(email);
		System.out.println(password);
		return session
				.createNativeQuery(sql, Patient.class)
				.setParameter("email", email)
				.setParameter("password", password)
				.uniqueResult();
	}

	@Override
	public Patient findById(int patientId) {
	    return selectById(patientId);
	}

	@Override
	public List<Patient> searchedByNameAndBirthday(String name, String birthday) {
	    String hql = "FROM Patient WHERE name = :name AND birthday = :birthday";
	    System.out.println("Searching for birthday: " + birthday);

	    return session.createQuery(hql, Patient.class)
	                  .setParameter("name", name)
	                  .setParameter("birthday", birthday)
	                  .getResultList();
	}
	
	@Override
	public List<Patient> searchedByNameAndPhone(String name, String phone) {
		String hql = "FROM Patient WHERE name = :name AND phone = :phone";

	    return session.createQuery(hql, Patient.class)
	                  .setParameter("name", name)
	                  .setParameter("phone", phone)
	                  .getResultList();
	}

	@Override
	public List<Patient> searchedByNameAndBirthdayAndPhone(String name, String birthday, String phone) {
		String hql = "FROM Patient WHERE name = :name AND birthday = :birthday AND phone = :phone";

	    return session.createQuery(hql, Patient.class)
	                  .setParameter("name", name)
	                  .setParameter("birthday", birthday)
	                  .setParameter("phone", phone)
	                  .getResultList();
	}

	public List<Patient> findReservedPatientsByKeyword(String keyword, int offset, int pageSize, int clinicId) {
		String hql = "SELECT DISTINCT a.patient FROM Appointment a " +
				"WHERE a.clinic.id = :clinicId " +
				"AND (a.patient.name LIKE :keyword OR a.patient.phone LIKE :keyword) " +
				"ORDER BY a.patient.name ASC";

		return session.createQuery(hql, Patient.class)
				.setParameter("clinicId", clinicId)
				.setParameter("keyword", "%" + keyword + "%")
				.setFirstResult(offset)
				.setMaxResults(pageSize)
				.getResultList();
	}

	@Override
	public long countReservedPatientsByKeyword(String keyword, int clinicId) {
		String hql = "SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a " +
				"WHERE a.clinic.id = :clinicId " +
				"AND (a.patient.name LIKE :keyword OR a.patient.phone LIKE :keyword) ";

		return session.createQuery(hql, Long.class)
				.setParameter("clinicId", clinicId)
				.setParameter("keyword", "%" + keyword + "%")
				.uniqueResult();
	}

	//以行動電話取得病患
	public Patient findByPhone(String phone) {
		String hql = "FROM Patient p WHERE p.phone = :phone";
		return session.createQuery(hql, Patient.class)
				.setParameter("phone", phone)
				.uniqueResult();
	}
	
	@Override
	public int updateNotes(Integer patientId, String newNotes) {
		String hql = "UPDATE Patient p SET p.notes = :notes WHERE p.patientId = :patientId";
	    session.createMutationQuery(hql)
	           .setParameter("notes", newNotes)
	           .setParameter("patientId", patientId)
	           .executeUpdate();
	    return 1;
	}
}
