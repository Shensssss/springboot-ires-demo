package tw.idv.shen.web.clinic.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import tw.idv.shen.web.clinic.entity.CallNumber;
import tw.idv.shen.web.clinic.entity.Clinic;

public interface ClinicService {
	Clinic selectById(int clinic_id);

	List<Clinic> getClinicByAccount(String clinic_account);

	String editPsd(Clinic clinic);

	List<Clinic> filterClinics(Integer majorId, String towns, Double minRating, Double maxDistanceKm, Double userLat,
			Double userLng, LocalDate date, LocalTime startTime, LocalTime endTime);

	Clinic findById(Integer clinicId);

	CallNumber findOrCreateCallNumber(Integer clinicId, Integer doctorId, Integer timePeriod, LocalDate date);

	CallNumber saveCallNumber(CallNumber callNumber);

	List<CallNumber> findCallNumbersByClinicId(Integer clinicId, LocalDate date);
}
