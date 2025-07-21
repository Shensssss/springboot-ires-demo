package tw.idv.shen.web.major.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.major.dao.ClinicMajorDao;
import tw.idv.shen.web.major.entity.ClinicMajor;
import tw.idv.shen.web.major.entity.Major;

@Repository
public class ClinicMajorDaoImpl implements ClinicMajorDao{
	@PersistenceContext
	private Session session;

	@Override
	public List<Clinic> findClinicsByMajorIdOrAll(Integer majorId) {
		if (majorId != null) {
			String hql = "select cm.clinic from ClinicMajor cm join cm.clinic join cm.major where cm.major.majorId = :majorId";
			return session.createQuery(hql, Clinic.class).setParameter("majorId", majorId).getResultList();
		} else {
			String hql = "from Clinic";
			return session.createQuery(hql, Clinic.class).getResultList();
		}
	}

	@Override
	public List<Major> findMajorByClinicsId(Integer clinicId) {
		String hql = "select cm.major from ClinicMajor cm join cm.clinic c join cm.major m where c.clinicId = :clinicId";
		return session.createQuery(hql, Major.class).setParameter("clinicId", clinicId).getResultList();
	}

	@Override
	public int insert(ClinicMajor clinicMajor) {
		session.persist(clinicMajor);
		return 1;
	}

	@Override
	public int update(ClinicMajor clinicMajor) {
		session.update(clinicMajor);
		return 1;
	}

	@Override
	public int deleteByClinicId(Integer clinicId) {
		String hql = "DELETE FROM ClinicMajor WHERE clinic.clinicId = :clinicId";
	    session.createQuery(hql)
	           .setParameter("clinicId", clinicId)
	           .executeUpdate();
	    return 1;
	} 
	  
}
