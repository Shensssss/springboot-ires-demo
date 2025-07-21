package tw.idv.shen.web.doctor.service;

import java.util.List;

import tw.idv.shen.web.doctor.entity.Schedule;

public interface ScheduleService {

	int addDateOff(Schedule schedule);
    int editDateOff(Schedule schedule);
    int addWeeklyOff(Schedule schedule);
    int editWeeklyOff(Schedule schedule);

	List<Schedule> showSchedule(Integer doctorId);

}
