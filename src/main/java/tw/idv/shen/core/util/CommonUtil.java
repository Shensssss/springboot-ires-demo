package tw.idv.shen.core.util;

import static tw.idv.shen.core.util.Constants.GSON;
import static tw.idv.shen.core.util.Constants.JSON_MIME_TYPE;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CommonUtil {
	@PersistenceContext
	private Session session;

	private static final Logger logger = LogManager.getLogger(CommonUtil.class);

	public static TimeRange parseTimeRange(String range) {
		if (range == null || !range.contains("-")) {
			throw new IllegalArgumentException("時間範圍格式錯誤，應為 HH:mm-HH:mm");
		}

		String[] parts = range.split("-");
		if (parts.length != 2) {
			throw new IllegalArgumentException("時間範圍格式錯誤，應為 HH:mm-HH:mm");
		}

		try {
			LocalTime start = LocalTime.parse(parts[0].trim());
			LocalTime end = LocalTime.parse(parts[1].trim());
			return new TimeRange(start, end);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("無法解析時間：" + range);
		}
	}

	public static class TimeRange {
		public final LocalTime start;
		public final LocalTime end;

		public TimeRange(LocalTime start, LocalTime end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {
			return start + " ~ " + end;
		}
	}

	public static <T> T getBean(ServletContext sc, Class<T> clazz) {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
		return context.getBean(clazz);
	}
	
	public static <P> P json2Pojo(HttpServletRequest request, Class<P> classOfPojo) {
		try (BufferedReader br = request.getReader()) {
			return GSON.fromJson(br, classOfPojo);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	public static <P> void writePojo2Json(HttpServletResponse response, P pojo) {
		response.setContentType(JSON_MIME_TYPE);
		try (PrintWriter pw = response.getWriter()) {
			pw.print(GSON.toJson(pojo));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	//取得看診號碼
	public int getNextReserveNo(int clinicId, int doctorId, Date date, int timePeriod) {
		String hql = "SELECT MAX(a.reserveNo) FROM Appointment a " +
				"WHERE a.clinicId = :clinicId AND a.doctorId = :doctorId " +
				"AND DATE(a.appointmentDate) = :date AND a.timePeriod = :period";

		Integer max = session.createQuery(hql, Integer.class)
				.setParameter("clinicId", clinicId)
				.setParameter("doctorId", doctorId)
				.setParameter("date", date)
				.setParameter("period", timePeriod)
				.uniqueResult();

		int next = (max != null ? max : 0) + 1;

		// 奇數號 = 網路預約，偶數號保留給現場掛號
		if (next % 2 == 0) {
			next += 1;
		}

		return next;
	}

	//判斷是否有預約過firstVisit
	public int getFirstVisit(int patientId, int clinicId) {
		String hql = "SELECT COUNT(*) FROM Appointment a WHERE a.patientId = :pid AND a.clinicId = :cid";
		Long count = session.createQuery(hql, Long.class)
				.setParameter("pid", patientId)
				.setParameter("cid", clinicId)
				.uniqueResult();
		return (count != null && count > 0) ? 1 : 0;
	}
}
