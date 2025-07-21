package tw.idv.shen.web.clinic.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.idv.shen.web.clinic.dao.ClinicDAO;
import tw.idv.shen.web.clinic.entity.CallNumber;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicService;
import tw.idv.shen.web.major.dao.ClinicMajorDao;

@Service
@Transactional
public class ClinicServiceImpl implements ClinicService {

    @Autowired
    private ClinicDAO clinicDAO;

    @Autowired
    private ClinicMajorDao clinicMajorDao;
	@Override
	public String editPsd(Clinic clinic) {
		final Clinic oclinic = clinicDAO.selectById(clinic.getClinicId());

		clinic.setClinicName(oclinic.getClinicName());
		clinic.setAgencyId(oclinic.getAgencyId());
		clinic.setAccount(oclinic.getAccount());
		clinic.setPhone(oclinic.getPhone());
		final int resultCount = clinicDAO.updatePsd(clinic);
		String strReturn = (resultCount > 0 ? "密碼修改成功" : "密碼修改失敗");

		return strReturn;
	}

    @Override
    public List<Clinic> filterClinics(
            Integer majorId,
            String towns,
            Double minRating,
            Double maxDistanceKm,
            Double userLat,
            Double userLng,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        int weekday = date.getDayOfWeek().getValue();
        List<Clinic> clinics = clinicMajorDao.findClinicsByMajorIdOrAll(majorId);
        List<Clinic> result = new ArrayList<>();

        for (Clinic clinic : clinics) {
            if (towns != null && !towns.isBlank()) {
                List<String> selectedTowns = Arrays.asList(towns.split(","));
                if (clinic.getAddressTown() == null || !selectedTowns.contains(clinic.getAddressTown())) {
                    continue;
                }
            }

            if (minRating != null && clinic.getRating() != null && clinic.getRating() < minRating) {
                continue;
            }

            if (userLat != null && userLng != null && maxDistanceKm != null
                    && clinic.getLatitude() != null && clinic.getLongitude() != null) {
                double distance = haversine(userLat, userLng, clinic.getLatitude(), clinic.getLongitude());
                if (distance > maxDistanceKm) {
                    continue;
                }
            }

            boolean matched = false;

            if (clinic.getWeekMorning() != null && clinic.getMorning() != null && clinic.getMorning().contains("-")) {
                List<String> days = Arrays.asList(clinic.getWeekMorning().split(","));
                if (days.contains(String.valueOf(weekday))) {
                    String[] times = clinic.getMorning().split("-");
                    LocalTime clinicStart = LocalTime.parse(times[0].trim());
                    LocalTime clinicEnd = LocalTime.parse(times[1].trim());
                    if (!(clinicEnd.isBefore(startTime) || clinicStart.isAfter(endTime))) {
                        matched = true;
                    }
                }
            }

            if (!matched && clinic.getWeekAfternoon() != null && clinic.getAfternoon() != null && clinic.getAfternoon().contains("-")) {
                List<String> days = Arrays.asList(clinic.getWeekAfternoon().split(","));
                if (days.contains(String.valueOf(weekday))) {
                    String[] times = clinic.getAfternoon().split("-");
                    LocalTime clinicStart = LocalTime.parse(times[0].trim());
                    LocalTime clinicEnd = LocalTime.parse(times[1].trim());
                    if (!(clinicEnd.isBefore(startTime) || clinicStart.isAfter(endTime))) {
                        matched = true;
                    }
                }
            }

            if (!matched && clinic.getWeekNight() != null && clinic.getNight() != null && clinic.getNight().contains("-")) {
                List<String> days = Arrays.asList(clinic.getWeekNight().split(","));
                if (days.contains(String.valueOf(weekday))) {
                    String[] times = clinic.getNight().split("-");
                    LocalTime clinicStart = LocalTime.parse(times[0].trim());
                    LocalTime clinicEnd = LocalTime.parse(times[1].trim());
                    if (!(clinicEnd.isBefore(startTime) || clinicStart.isAfter(endTime))) {
                        matched = true;
                    }
                }
            }

            if (matched) {
                result.add(clinic);
            }
        }

        return result;
    }

    // 距離公式
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
	
	@Override
	public Clinic selectById(int clinic_id) {
		return clinicDAO.selectById(clinic_id);
	}
	
	
	@Override
	public List<Clinic> getClinicByAccount(String clinic_account) {
		return clinicDAO.getClinicByAccount(clinic_account);
	}
	
	@Override
	public Clinic findById(Integer clinicId) {
		return clinicDAO.selectById(clinicId);
	}

    @Override
    public CallNumber findOrCreateCallNumber(Integer clinicId, Integer doctorId, Integer timePeriod, LocalDate date) {
        CallNumber existing = clinicDAO.findByClinicDoctorDate(clinicId, doctorId, date);

        // 情況 1：查到，且時段相同 → 不做任何更新
        if (existing != null && Objects.equals(existing.getTimePeriod(), timePeriod)) {
            return existing;
        }

        // 情況 2：查到，但時段不同 → 更新
        if (existing != null) {
            existing.setNumber(0);
            existing.setConsultationStatus(0);
            existing.setTimePeriod(timePeriod);
            existing.setUpdateTime(LocalDateTime.now());
            existing.setUpdateId("system");
            return clinicDAO.save(existing);
        }

        // 情況 3：查不到 → 新增
        CallNumber newCall = new CallNumber();
        newCall.setClinicId(clinicId);
        newCall.setDoctorId(doctorId);
        newCall.setAppointmentDate(date);
        newCall.setTimePeriod(timePeriod);
        newCall.setNumber(0);
        newCall.setConsultationStatus(0);
        newCall.setCreateTime(LocalDateTime.now());
        newCall.setCreateId("system");

        return clinicDAO.save(newCall);
    }


    @Override
	public CallNumber saveCallNumber(CallNumber callNumber) {
		return clinicDAO.save(callNumber);
	}

	@Override
	public List<CallNumber> findCallNumbersByClinicId(Integer clinicId, LocalDate date) {
	    return clinicDAO.findCallNumbersByClinicIdAndDate(clinicId, date);
	}
}
