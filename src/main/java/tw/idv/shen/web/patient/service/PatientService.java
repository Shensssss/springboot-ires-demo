package tw.idv.shen.web.patient.service;

import java.util.List;
import java.util.Map;

import tw.idv.shen.web.patient.entity.Patient;

public interface PatientService {
	
	Patient register(Patient patient);
	
	Patient login(Patient patient);
	
	Patient findById(int patientId); 

	void updatePatient(Patient patient);
	
	Patient edit(Patient reqPatient);
	
	List<Patient> clinicSearch(String name, String birthday, String phone);

	Map<String, Object> getReservedPatientsWithKeyword(Integer clinicId, String keyword, int page, int pageSize);

	Patient findByPhone(String phone);
	
	int clinicEditPatientNotes(int patientId, String newNotes);

	boolean checkIn(Patient patient, String code);
}
