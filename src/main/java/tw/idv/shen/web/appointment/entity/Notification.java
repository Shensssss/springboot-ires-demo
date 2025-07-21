package tw.idv.shen.web.appointment.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tw.idv.shen.web.patient.entity.Patient;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @Column(name = "notification_id")
    private String notificationId;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "sent_datetime")
    private Timestamp sentDatetime;

    @Column(name = "read_status")
    private Boolean readStatus;

    @Column(name = "read_datetime")
    private Timestamp readDatetime;

    @Column(name = "notification_type")
    private String notificationType;

    // === Getters and Setters ===

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Timestamp sentDatetime) {
        this.sentDatetime = sentDatetime;
    }

    public Boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }

    public Timestamp getReadDatetime() {
        return readDatetime;
    }

    public void setReadDatetime(Timestamp readDatetime) {
        this.readDatetime = readDatetime;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
