package tw.idv.shen.web.doctor.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.doctor.dao.ScheduleDao;
import tw.idv.shen.web.doctor.entity.Schedule;

@Repository
public class ScheduleDaoImpl implements ScheduleDao{
	@PersistenceContext
    private Session session;

	@Override
	public int insert(Schedule schedule) {
		session.persist(schedule);
		return 1;
	}

	@Override
	public int deleteById(Integer scheduleId) {
		Schedule schedule = session.load(Schedule.class, scheduleId);
		session.remove(schedule);
		return 1;
	}

	@Override
	public int update(Schedule newSchedule) {
		Schedule schedule = session.load(Schedule.class, newSchedule.getScheduleDoctorId());
		schedule.setOffDate(newSchedule.getOffDate());
		schedule.setDayOfWeek(newSchedule.getDayOfWeek());
		schedule.setTimePeriod(newSchedule.getTimePeriod());
		schedule.setOff(newSchedule.getOff());
		return 1;
	}

	@Override
	public Schedule selectById(Integer scheduleDoctorId) {
		return session.get(Schedule.class, scheduleDoctorId);
	}
	
	//一定要override但應該用不到
	@Override
	public List<Schedule> selectAll() {
		return session.createQuery("FROM Schedule", Schedule.class).list();
	}

	@Override
	public List<Schedule> selectByDoctorId(Integer doctorId) {
		return session.createQuery("FROM Schedule s WHERE s.doctor.doctorId = :doctorId", Schedule.class)
				.setParameter("doctorId", doctorId)
                .getResultList();
	}

}
