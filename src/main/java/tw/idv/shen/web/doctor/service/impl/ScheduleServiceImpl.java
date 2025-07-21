package tw.idv.shen.web.doctor.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.idv.shen.web.doctor.dao.ScheduleDao;
import tw.idv.shen.web.doctor.entity.Schedule;
import tw.idv.shen.web.doctor.service.ScheduleService;


@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService{
	
	@Autowired
	private ScheduleDao scheduleDao;

	@Override
	public List<Schedule> showSchedule(Integer doctorId) {
		return scheduleDao.selectByDoctorId(doctorId);
	}

	@Override
	public int addDateOff(Schedule schedule) {
		return scheduleDao.insert(schedule);
	}

	@Override
	public int editDateOff(Schedule schedule) {
		 return scheduleDao.update(schedule);
	}

	@Override
	public int addWeeklyOff(Schedule schedule) {
		return scheduleDao.insert(schedule);
	}

	@Override
	public int editWeeklyOff(Schedule schedule) {
		return scheduleDao.update(schedule);
	}


}
