package tw.idv.shen.web.patient.dao;

import java.util.List;

import tw.idv.shen.core.dao.CoreDao;
import tw.idv.shen.web.patient.entity.Patient;

public interface PatientDao extends CoreDao<Patient, Integer> {
	Patient selectByEmail(String email);

	Patient selectForLogin(String email, String password);

	Patient findById(int patientId);


	List<Patient> searchedByNameAndBirthday(String name, String birthday);

	List<Patient> searchedByNameAndPhone(String name, String phone);

	List<Patient> searchedByNameAndBirthdayAndPhone(String name, String birthday, String phone);

	List<Patient> findReservedPatientsByKeyword(String keyword, int offset, int pageSize, int clinicId);
	long countReservedPatientsByKeyword(String keyword, int clinicId);
	Patient findByPhone(String phone);

	int updateNotes(Integer patientId, String newNotes);
}
