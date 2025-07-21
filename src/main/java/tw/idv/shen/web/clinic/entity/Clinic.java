package tw.idv.shen.web.clinic.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.idv.shen.core.pojo.Core;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "clinic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Clinic extends Core{
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_id")
    private Integer clinicId;

    @Column(name = "clinic_name")
    private String clinicName;

    @Column(name = "agency_id")
    private String agencyId;

    @Column(name = "account")
    private String account;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address_city")
    private String addressCity;

    @Column(name = "address_town")
    private String addressTown;

    @Column(name = "address_road")
    private String addressRoad;

    @Column(name = "web")
    private String web;

    @Column(name = "morning")
    private String morning;

    @Column(name = "afternoon")
    private String afternoon;

    @Column(name = "night")
    private String night;

    @Column(name = "week_morning")
    private String weekMorning;

    @Column(name = "week_afternoon")
    private String weekAfternoon;

    @Column(name = "week_night")
    private String weekNight;

    @Column(name = "registration_fee")
    private Integer registrationFee;

    @Column(name = "memo")
    private String memo;

    @Column(name = "create_id")
    private String createId;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private Timestamp createTime;

    @Column(name = "update_id")
    private String updateId;

    @UpdateTimestamp
    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "rating")
    private Double rating;

    @Lob
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    //該診所的評論數量
    @Column(name = "comments")
    private Integer comments;

    //各時段的預約額度
    @Column(name = "quota")
    private Integer quota = 20;
}
