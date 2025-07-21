package tw.idv.shen.web.clinic.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.idv.shen.web.clinic.dao.RegisterDao;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.clinic.service.RegisterService;

@Service
@Transactional
public class RegisterServiceImpl implements RegisterService {
	@Autowired
	private RegisterDao dao;

	@Override
	public String register(Clinic clinic) {
		String clinicName = clinic.getClinicName();
		if (clinicName == null || clinicName.length() < 1 || clinicName.length() > 20) {
			return "診所名字長度需介1~20";
		}

		String agencyId = clinic.getAgencyId();
		if (agencyId == null || agencyId.length() != 10) {
			return "機構代碼長度需為10";
		}

		String phone = clinic.getPhone();
		if (phone == null || phone.length() < 10 || phone.length() > 10) {
			return "電話長度需為10";
		}

		String addressCity = clinic.getAddressCity();
		if (addressCity == null || addressCity.length() < 3 || addressCity.length() > 3) {
			return "城市長度為3";
		}

		String addressTown = clinic.getAddressTown();
		if (addressTown == null || addressTown.length() < 2 || addressTown.length() > 4) {
			return "鄉鎮長度介於2~4";
		}

		String addressRoad = clinic.getAddressRoad();
		if (addressRoad == null || addressRoad.length() < 1 || addressRoad.length() > 50) {
			return "道路長度介於1~50";
		}

		String account = clinic.getAccount();
		if (account == null || !account.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") || account.length() > 50) {
		    return "email格式錯誤或長度不符 (1~50)";
		}

		String password = clinic.getPassword();
		if (password == null || password.length() < 1 || password.length() > 50) {
			return "password長度介於1~50";
		}
		
		Double longitude = clinic.getLongitude();
		if (longitude == null || longitude < -180 || longitude > 180) {
			return "經度格式錯誤";
		}
		
		Double latitude = clinic.getLatitude();
		if (latitude == null || latitude < -90 || latitude > 90) {
			return "緯度格式錯誤";
		}
		

		if (dao.selectbyAccount(account) != null) {
			return "此email已被註冊";
		}
		if (clinic.getMorning() == null) {
			clinic.setMorning("");
		}
		if (clinic.getAfternoon() == null) {
			clinic.setAfternoon("");
		}
		if (clinic.getNight() == null) {
			clinic.setNight("");
		}
		if (clinic.getWeekMorning() == null) {
			clinic.setWeekMorning("");
		}
		if (clinic.getWeekAfternoon() == null) {
			clinic.setWeekAfternoon("");
		}
		if (clinic.getWeekNight() == null) {
			clinic.setWeekNight("");
		}
		if (clinic.getRegistrationFee() == null) {
			clinic.setRegistrationFee(0);
		}
		if (clinic.getWeb() == null) {
			clinic.setWeb("");
		}
		if (clinic.getMemo() == null) {
			clinic.setMemo("");
		}		
		if (clinic.getRating() == null) {
			clinic.setRating(0.0);
		}
		
		if (clinic.getQuota() == null) {
			clinic.setQuota(0);
		}
		
		if (clinic.getQuota() == null) {
			clinic.setQuota(0);
		}
		
		if (clinic.getQuota() == null) {
			clinic.setQuota(0);
		}

		int count = dao.insert(clinic);
		if (count < 1) {
			return "系統錯誤，請聯絡管理員";
		}
		return null;

	}

	@Override
	public Clinic login(Clinic clinic) {

		final String account = clinic.getAccount();
		final String password = clinic.getPassword();
		
		if (account == null) {
			clinic.setMessage("使用者名稱未輸入");
			clinic.setSuccessful(false);
			return clinic;
		}
		
		if (password == null) {
			clinic.setMessage("密碼未輸入");
			clinic.setSuccessful(false);
			return clinic;
		}
		
		clinic = dao.selectForLogin(account, password);
		if (clinic == null) {
			clinic = new Clinic();
			clinic.setMessage("使用者名稱或密碼錯誤");
			clinic.setSuccessful(false);
			return clinic;
		}
		
		clinic.setMessage("登入成功");
		clinic.setSuccessful(true);
		return clinic;
	}

	@Override
	public Clinic findPassword(Clinic clinic) {
		
		final String agencyId = clinic.getAgencyId();
		final String account = clinic.getAccount();
		
		if (agencyId == null) {
			clinic.setMessage("機構代碼未輸入");
			clinic.setSuccessful(false);
			return clinic;
		}
		
		if (account == null) {
			clinic.setMessage("使用者名稱未輸入");
			clinic.setSuccessful(false);
			return clinic;
		}
							
		clinic = dao.selectForPassword(agencyId, account);
		if (clinic == null) {
			clinic = new Clinic();
			clinic.setMessage("使用者名稱或機構代碼錯誤");
			clinic.setSuccessful(false);
			return clinic;
		}
		
		clinic.setMessage("成功獲得密碼連結");
		clinic.setSuccessful(true);
		return clinic;
	}


	@Override
	public String resetPassword(Clinic clinic) {
		String account = clinic.getAccount();
		if (account == null || account.isBlank()) {
			return "信箱地址未輸入";
		}

		String password = clinic.getPassword();
		if (password == null || password.isBlank()) {
			return "密碼未輸入";
		}
		int result = dao.updatePassword(account, password);
		if (result == 0) {
			return "電子郵件錯誤，重設密碼失敗";
		}
		return null;
	}
}
