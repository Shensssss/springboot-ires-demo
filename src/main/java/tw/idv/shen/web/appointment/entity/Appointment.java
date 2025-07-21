package tw.idv.shen.web.appointment.entity;

import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.doctor.entity.Doctor;
import tw.idv.shen.web.major.entity.Major;
import tw.idv.shen.web.patient.entity.Patient;

@Entity
@Table(name = "appointment")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @Column(name = "appointment_id", length = 36)
    private String appointmentId;

    @Column(name = "clinic_id")
    private Integer clinicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", insertable = false, updatable = false)
    private Clinic clinic;

    @Column(name = "doctor_id")
    private Integer doctorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "major_id")
    private Integer majorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "major_id", insertable = false, updatable = false)
	private Major major;

	@Column(name = "reserve_no")
	private Integer reserveNo;

    @Column(name = "appointment_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Taipei")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;

    @Column(name = "time_period")
    private Integer timePeriod;

    @Column(name = "first_visit")
    private Integer firstVisit;

    @Column(name = "status")
    private Integer status;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private Timestamp createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
	@Column(name = "appointment_type")
	private Integer appointmentType;  // 0: 醫師診間, 1: 線上看診

	@Column(name = "self_condition")
	private Integer selfCondition;  // 0: 近期發病, 1: 長期病症, 2: 報告查詢

	@Transient
	public String getDoctorName() {
		return (doctor != null) ? doctor.getDoctorName() : null;
	}

    @Transient
    public String getAgencyId() {
        return (clinic != null) ? clinic.getAgencyId() : null;
    }
}
