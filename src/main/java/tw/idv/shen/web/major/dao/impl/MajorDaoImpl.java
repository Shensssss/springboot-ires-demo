package tw.idv.shen.web.major.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.major.dao.MajorDao;
import tw.idv.shen.web.major.entity.Major;

@Repository
public class MajorDaoImpl implements MajorDao{
	@PersistenceContext
	private Session session;

	@Override
	public List<Major> selectAll() {
		String hql = "from Major";
		return session
		        .createQuery(hql, Major.class)
		        .getResultList();
	}
	
	@Override
	public Major selectMajorById(Integer majorId) {
	    String hql = "FROM Major m WHERE m.majorId = :majorId";
	    return session.createQuery(hql, Major.class)
	                  .setParameter("majorId", majorId)
	                  .uniqueResult();
	}
	
}
