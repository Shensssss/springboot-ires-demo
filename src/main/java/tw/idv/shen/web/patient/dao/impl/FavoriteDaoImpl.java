package tw.idv.shen.web.patient.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import tw.idv.shen.web.patient.dao.FavoriteDao;
import tw.idv.shen.web.patient.entity.Favorite;

@Repository
public class FavoriteDaoImpl implements FavoriteDao {

	@PersistenceContext
	private Session session;

	@Override
	public boolean existsByPatientIdClinicId(Integer patientId, Integer clinicId) {
		String hql = "SELECT COUNT(f.favoritesId) FROM Favorite f WHERE f.patientId = :patientId AND f.clinicId = :clinicId";
		Long count = (Long) session.createQuery(hql).setParameter("patientId", patientId)
				.setParameter("clinicId", clinicId).uniqueResult();

		return count != null && count > 0;
	}

	@Override
	public void save(Favorite favorite) {
		// 這邊不用再產 ID，因為 Service 已經幫你設定好了 UUID
		session.save(favorite);
		session.flush(); // 可留著讓錯誤早點拋出
	}

	@Override
	public List<Map<String, Object>> findFavoritesByPatientId(Integer patientId) {
		String sql = "SELECT c.clinic_id, c.clinic_name AS name, CONCAT(c.address_city, c.address_town, c.address_road) AS address, c.phone " + "FROM favorites f "
				+ "JOIN clinic c ON f.clinic_id = c.clinic_id " + "WHERE f.patient_id = :patientId";
		@SuppressWarnings("unchecked")
		List<Object[]> results = session.createNativeQuery(sql).setParameter("patientId", patientId).getResultList();

		// 將 Object[] 映射成 Map<String, Object>
		return results.stream().map(row -> {
			Map<String, Object> map = new HashMap<>();
			map.put("clinicId", row[0]);
			map.put("name", row[1]);
			map.put("address", row[2]);
			map.put("phone", row[3]);
			return map;
		}).collect(Collectors.toList());
	}

	@Override
	public boolean removeFavorite(Integer patientId, Integer clinicId) {
	    String hql = "DELETE FROM Favorite f WHERE f.patientId = :patientId AND f.clinicId = :clinicId";
	    int deleted = session.createQuery(hql)
	        .setParameter("patientId", patientId)
	        .setParameter("clinicId", clinicId)
	        .executeUpdate();
	    return deleted > 0;
	}

	@Override
    public List<Favorite> findByPatientId(Integer patientId) {
        String jpql = "SELECT f FROM Favorite f WHERE f.patientId = :patientId";
        TypedQuery<Favorite> query = session.createQuery(jpql, Favorite.class);
        query.setParameter("patientId", patientId);
        return query.getResultList();
    }

}
