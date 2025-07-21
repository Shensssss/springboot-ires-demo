package tw.idv.shen.web.clinic.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.clinic.dao.ClinicInfoDao;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.ClinicInfoService;

@Service
@Transactional
public class ClinicInfoServiceImpl implements ClinicInfoService {
	@Autowired
	private ClinicInfoDao clinicInfoDao;
	
	@PersistenceContext
	private Session session;

	@Override
	public int editClinic(Clinic editedClinic) {
		Clinic existing = clinicInfoDao.selectById(editedClinic.getClinicId());
        if(existing == null) {
            return 0;
        }
        if(editedClinic.getClinicName() != null) {
        	existing.setClinicName(editedClinic.getClinicName());
        }
        if(editedClinic.getPhone() != null) {
        	existing.setPhone(editedClinic.getPhone());
        }
        if(editedClinic.getAddressCity() != null) {
        	existing.setAddressCity(editedClinic.getAddressCity());
        } 
        if(editedClinic.getAddressTown() != null) {
        	existing.setAddressTown(editedClinic.getAddressTown());
        }
        if(editedClinic.getAddressRoad() != null) {
        	existing.setAddressRoad(editedClinic.getAddressRoad());
        } 
        if(editedClinic.getLatitude() != null) {
        	existing.setLatitude(editedClinic.getLatitude());
        }
        if(editedClinic.getLongitude() != null) {
        	existing.setLongitude(editedClinic.getLongitude());
        }
        if(editedClinic.getWeb() != null) {
        	existing.setWeb(editedClinic.getWeb());
        } 
        if(editedClinic.getRegistrationFee() != null) {
        	existing.setRegistrationFee(editedClinic.getRegistrationFee());
        } 
        if(editedClinic.getMemo() != null) {
        	existing.setMemo(editedClinic.getMemo());
        }
        if(editedClinic.getProfilePicture() != null) {
        	existing.setProfilePicture(editedClinic.getProfilePicture());
        }

        return clinicInfoDao.update(existing);
	}

	@Override
	public int editBusinessHours(Clinic editedClinic) {
		Clinic existing = clinicInfoDao.selectById(editedClinic.getClinicId());
        if (existing == null) {
            return 0;
        }
        if(editedClinic.getMorning() != null) {
        	existing.setMorning(editedClinic.getMorning());
        }
        if(editedClinic.getAfternoon() != null) {
        	existing.setAfternoon(editedClinic.getAfternoon());
        }
        if(editedClinic.getNight() != null) {
        	existing.setNight(editedClinic.getNight());
        }
        if(editedClinic.getWeekMorning() != null) {
        	existing.setWeekMorning(editedClinic.getWeekMorning());
        }
        if(editedClinic.getWeekAfternoon() != null) {
        	existing.setWeekAfternoon(editedClinic.getWeekAfternoon());
        }
        if(editedClinic.getWeekNight() != null) {
        	existing.setWeekNight(editedClinic.getWeekNight());
        }
        
        return clinicInfoDao.update(existing);
	}

	@Override
	public Clinic getClinicById(Integer clinicId) {
		return clinicInfoDao.selectById(clinicId);
	}

	@Override
	public Map<String, String> getOpenPeriod(Integer clinicId) {
		Clinic clinic = clinicInfoDao.selectById(clinicId);
        if (clinic == null) {
            return null;
        }

        Map<String, String> periods = new HashMap<>();
        periods.put("weekMorning", clinic.getWeekMorning());
        periods.put("weekAfternoon", clinic.getWeekAfternoon());
        periods.put("weekNight", clinic.getWeekNight());
        return periods;
	}

}
