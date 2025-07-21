package tw.idv.shen.web.patient.dao;

import java.util.List;
import java.util.Map;

import tw.idv.shen.web.patient.entity.Favorite;

public interface FavoriteDao {

	boolean existsByPatientIdClinicId(Integer patientId, Integer clinicId);

	void save(Favorite favorite);

	List<Map<String, Object>> findFavoritesByPatientId(Integer patientId);

	boolean removeFavorite(Integer patientId, Integer clinicId);

	List<Favorite> findByPatientId(Integer patientId);
}
