package tw.idv.shen.web.patient.service;

import java.util.List;
import java.util.Map;

import tw.idv.shen.web.patient.entity.Favorite;

public interface FavoriteService {

	boolean hasFavoriteClinic(Integer patientId, Integer clinicId);

	boolean addFavorite(Integer patientId, Integer clinicId);

	List<Map<String, Object>> getFavoritesByPatientId(Integer patientId);

	boolean removeFavorite(Integer patientId, Integer clinicId);

	List<Favorite> findByPatientId(Long patientId);
}
