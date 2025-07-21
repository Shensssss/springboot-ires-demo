package tw.idv.shen.web.major.service.impl;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicService;
import tw.idv.shen.web.major.dao.ClinicMajorDao;
import tw.idv.shen.web.major.entity.ClinicMajor;
import tw.idv.shen.web.major.entity.Major;
import tw.idv.shen.web.major.service.ClinicMajorService;
import tw.idv.shen.web.major.service.MajorService;

@Service
public class ClinicMajorServiceImpl implements ClinicMajorService {

	@Autowired
	private ClinicMajorDao dao;
	
	@Autowired
    private MajorService majorService;
	
	@Autowired
	private ClinicService clinicService;

	@Override
	public List<Clinic> getClinicsByMajorId(Integer majorId) {
		return dao.findClinicsByMajorIdOrAll(majorId);
	}

	@Override
	public List<Major> getMajorByClinicId(Integer clinicId) {
		return dao.findMajorByClinicsId(clinicId);
	}

	@Override
	public int addClinicMajor(ClinicMajor clinicMajor) {
		
		return dao.insert(clinicMajor);
	}

	@Override
	@Transactional
	public int editClinicMajor(Integer loggedinClinicId, List<Integer> selectedMajorIds) {
		// 選擇的專科不可為空
	    if (selectedMajorIds == null || selectedMajorIds.isEmpty()) {
	        return 0;
	    }

	    // 取得所有合法majorId
	    List<Major> majors = majorService.findAllMajor();
	    Set<Integer> validIds = new HashSet<>();
	    for (Major m : majors) {
	        validIds.add(m.getMajorId());
	    }

	    // 檢查selectedMajorIds是否都在validIds裡
	    for (Integer id : selectedMajorIds) {
	        if (!validIds.contains(id)) {
	            return 0;
	        }
	    }

	    // 刪除舊資料、新增新資料
	    dao.deleteByClinicId(loggedinClinicId);
	    for (Integer majorId : selectedMajorIds) {
	        ClinicMajor editedClinicMajor = new ClinicMajor();
	        
	        editedClinicMajor.setClinic(clinicService.findById(loggedinClinicId));
	        editedClinicMajor.setMajor(majorService.findById(majorId));
	        editedClinicMajor.setCreateId(loggedinClinicId);
	        editedClinicMajor.setCreateTime(new Timestamp(System.currentTimeMillis()));
	        editedClinicMajor.setUpdateTime(new Timestamp(System.currentTimeMillis()));
	        dao.insert(editedClinicMajor);
	    }
	    return 1;
	}

}
