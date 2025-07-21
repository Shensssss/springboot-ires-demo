package tw.idv.shen.web.major.dao;

import java.util.List;

import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.major.entity.ClinicMajor;
import tw.idv.shen.web.major.entity.Major;

public interface ClinicMajorDao {
	List<Clinic> findClinicsByMajorIdOrAll(Integer majorId);
	List<Major> findMajorByClinicsId(Integer clinicId);
	int insert(ClinicMajor clinicMajor);
	int update(ClinicMajor clinicMajor);
	int deleteByClinicId(Integer clinicId);
}

