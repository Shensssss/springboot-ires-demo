package tw.idv.shen.web.appointment.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import tw.idv.shen.web.clinic.entity.Clinic;

public class AppointmentUtils {
    public boolean isPastAppointment(LocalDate appointmentDate, int timePeriod, Clinic clinic) {
        LocalDateTime now = LocalDateTime.now();
        String periodStr;

        switch (timePeriod) {
            case 1: periodStr = clinic.getMorning(); break;
            case 2: periodStr = clinic.getAfternoon(); break;
            case 3: periodStr = clinic.getNight(); break;
            default: throw new IllegalArgumentException("無效的時段編號：" + timePeriod);
        }

        if (periodStr == null || !periodStr.contains("-")) {
            throw new IllegalArgumentException("診所該時段尚未設定時間範圍");
        }

        String startTimeStr = periodStr.split("-")[0];
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalDateTime appointmentStart = LocalDateTime.of(appointmentDate, startTime);

        return appointmentStart.isBefore(now);
    }
}
