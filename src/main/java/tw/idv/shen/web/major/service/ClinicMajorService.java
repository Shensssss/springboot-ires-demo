package tw.idv.shen.web.major.service;

import java.util.List;

import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.major.entity.ClinicMajor;
import tw.idv.shen.web.major.entity.Major;

public interface ClinicMajorService {
	List<Clinic> getClinicsByMajorId(Integer majorId);
	List<Major> getMajorByClinicId(Integer clinicId);
	int addClinicMajor(ClinicMajor clinicMajor);
	int editClinicMajor(Integer loggedinClinicId, List<Integer> selectedMajorIds);
}
