package tw.idv.shen.web.patient.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.patient.dao.FavoriteDao;
import tw.idv.shen.web.patient.entity.Favorite;
import tw.idv.shen.web.patient.service.FavoriteService;

@Service
public class FavoriteServiceImpl implements FavoriteService {
	@PersistenceContext
	private Session session;

	@Autowired
	private FavoriteDao favoriteDao;

	@Override
	public boolean hasFavoriteClinic(Integer patientId, Integer clinicId) {
		return favoriteDao.existsByPatientIdClinicId(patientId, clinicId);
	}

	@Transactional
	@Override
	public boolean addFavorite(Integer patientId, Integer clinicId) {
		if (favoriteDao.existsByPatientIdClinicId(patientId, clinicId)) {
			return false;
		}

		Favorite favorite = new Favorite();
		favorite.setFavoritesId(UUID.randomUUID().toString()); // ← 就放這！
		favorite.setPatientId(patientId);
		favorite.setClinicId(clinicId);
		favorite.setCreateTime(new Timestamp(System.currentTimeMillis()));

		favoriteDao.save(favorite); // DAO 不需處理 ID 生成
		return true;
	}

	@Override
	public List<Map<String, Object>> getFavoritesByPatientId(Integer patientId) {
		return favoriteDao.findFavoritesByPatientId(patientId);
	}

	@Transactional
	@Override
	public boolean removeFavorite(Integer patientId, Integer clinicId) {
		return favoriteDao.removeFavorite(patientId, clinicId);
	}

	@Override
    public List<Favorite> findByPatientId(Long patientId) {
        return favoriteDao.findByPatientId(patientId.intValue());
    }

}