package tw.idv.shen.web.clinic.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.clinic.dao.RegisterDao;
import tw.idv.shen.web.clinic.entity.Clinic;

@Repository
public class RegisterDaoImpl implements RegisterDao {
	@PersistenceContext
	private Session session;

	@Override
	public int deleteById(String id) {
		throw new RuntimeException("NO-OP");
	}

	@Override
	public int update(Clinic pojo) {
		throw new RuntimeException("NO-OP");
	}

	@Override
	public Clinic selectById(String id) {
		throw new RuntimeException("NO-OP");
	}

	@Override
	public List<Clinic> selectAll() {
		throw new RuntimeException("NO-OP");
	}

	@Override
	public int insert(Clinic clinic) {
		session.persist(clinic);
		return 1;
	}

	@Override
	public Clinic selectbyAccount(String account) {
		String hql = "FROM Clinic WHERE account = :account";
		return session.createQuery(hql, Clinic.class)
				.setParameter("account", account)
				.uniqueResult();
	}

	@Override
	public Clinic selectForLogin(String account, String password) {
		String hql = "FROM Clinic where account = :account and password = :password";
		return session.createQuery(hql, Clinic.class)
				.setParameter("account", account)
				.setParameter("password", password)
				.uniqueResult();
	}

	@Override
	public Clinic selectForPassword(String agencyId, String account) {
		String hql = "FROM Clinic where agencyId = :agencyId and account = :account";
		return session.createQuery(hql, Clinic.class)
				.setParameter("agencyId", agencyId)
				.setParameter("account", account)
				.uniqueResult();
	}

	@Override
	public int updatePassword(String account, String password) {
		String hql = "UPDATE Clinic SET password = :password WHERE account = :account";
		return session.createMutationQuery(hql)
				.setParameter("account", account)
				.setParameter("password", password)
				.executeUpdate();
	}
}
