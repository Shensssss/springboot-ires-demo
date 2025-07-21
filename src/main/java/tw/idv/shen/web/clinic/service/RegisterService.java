package tw.idv.shen.web.clinic.service;

import tw.idv.shen.web.clinic.entity.Clinic;

public interface RegisterService {
	
	String register(Clinic clinic);
	
	Clinic login(Clinic clinic);
	
	Clinic findPassword(Clinic clinic);
	
	String resetPassword(Clinic clinic);

}