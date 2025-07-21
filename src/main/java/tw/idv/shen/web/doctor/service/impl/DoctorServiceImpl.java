package tw.idv.shen.web.doctor.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.idv.shen.web.doctor.dao.DoctorDao;
import tw.idv.shen.web.doctor.entity.Doctor;
import tw.idv.shen.web.doctor.service.DoctorService;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

	@Autowired
	private DoctorDao doctorDao;

	@Override
	public int addDoctor(Doctor doctor) {
		// 1. name不可以為空
		if (doctor.getDoctorName() == null || doctor.getDoctorName().isEmpty()) {
			throw new IllegalArgumentException("醫師姓名為空");
		}

		// 2. name不可以重複
		Integer clinicId = doctor.getClinic().getClinicId();
		List<Doctor> existed = doctorDao.selectByClinicIdAndDoctorName(clinicId, doctor.getDoctorName());
		if (!existed.isEmpty()) {
			throw new IllegalArgumentException("醫師姓名重複");
		}

		// clinicId若是前端傳來的doctor物件參數之一需要驗證避免被攻擊?或是可以直接從servlet加上這個屬性?

		// 一定要設定時間(不可為null)避免出錯
		doctor.setCreateTime(new Timestamp(System.currentTimeMillis()));
		doctor.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		// 3. 執行insert
		return doctorDao.insert(doctor);
	}

	@Override
	public int deleteDoctor(Integer doctorId, Integer clinicId) {
		if (doctorId == null) {
			return 0;
		}
		Doctor doctor = doctorDao.selectById(doctorId);
		if (doctor == null || !doctor.getClinic().getClinicId().equals(clinicId)) {
			return 0;
		}
		return doctorDao.deleteById(doctorId);
	}

	@Override
	public int editDoctor(Doctor doctor) {
		// 1. name不可以為空
		if (doctor.getDoctorName() == null || doctor.getDoctorName().isEmpty()) {
			throw new IllegalArgumentException("醫師姓名為空");
		}

		// 2. name不可以重複，但要排除自己(當同名的id和自己id不同就是跟別人姓名重複)
		Integer clinicId = doctor.getClinic().getClinicId();
		List<Doctor> existed = doctorDao.selectAllByClinicId(clinicId);

		boolean isDuplicate = false;
		for (int i = 0; i < existed.size(); i++) {
			Doctor d = existed.get(i);
			if (!d.getDoctorId().equals(doctor.getDoctorId()) && d.getDoctorName().equals(doctor.getDoctorName())) {
				isDuplicate = true;
				break;
			}
		}

		if (isDuplicate) {
			throw new IllegalArgumentException("醫師姓名重複");
		} else {
			// 3. 執行update
			doctor.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			return doctorDao.update(doctor);
		}
	}

	@Override
	public List<Doctor> showAllDoctors(Integer clinicId) {
		return doctorDao.selectAllByClinicId(clinicId);
	}

	@Override
	public List<Doctor> showSearchedByName(Integer clinicId, String doctorName) {
		return doctorDao.selectByClinicIdAndDoctorName(clinicId, doctorName);
	}

	@Override
	public Map<String, Object> getDoctorsByKeyword(Integer clinicId, String keyword, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		List<Doctor> doctors = doctorDao.findDoctorsByKeyword(keyword, offset, pageSize, clinicId);
		long total = doctorDao.countDoctorsByKeyword(keyword, clinicId);

		List<Map<String, Object>> result = new ArrayList<>();
		for (Doctor d : doctors) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", d.getDoctorId());
			map.put("name", d.getDoctorName());
			map.put("education", d.getEducation());
			map.put("experience", d.getExperience());
			result.add(map);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("doctors", result);
		response.put("totalPages", (int) Math.ceil((double) total / pageSize));
		return response;
	}
}
