package tw.idv.shen.web.doctor.dao;

import java.util.List;

import tw.idv.shen.core.dao.CoreDao;
import tw.idv.shen.web.doctor.entity.Schedule;

public interface ScheduleDao extends CoreDao<Schedule, Integer>{
	
	public List<Schedule> selectByDoctorId(Integer doctorId);

}
