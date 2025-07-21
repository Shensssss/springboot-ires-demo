package tw.idv.shen.web.doctor.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.idv.shen.core.util.ListToJsonConverter;
import tw.idv.shen.web.clinic.entity.Clinic;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "doctor")
public class Doctor implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "doctor_id", updatable = false)
	private Integer doctorId;
	
	@Column(name = "doctor_name")
	private String doctorName;
	
	//FK多位醫師對一間診所
	@ManyToOne
	@JoinColumn(name = "clinic_id", nullable = false, updatable = false) //insertable不可以false因為我要手動填入?
	private Clinic clinic;
	
	@Convert(converter = ListToJsonConverter.class)
	@Column(name = "education", columnDefinition = "json")
	private List<String> education = new ArrayList<>();

	@Convert(converter = ListToJsonConverter.class)
	@Column(name = "experience", columnDefinition = "json")
	private List<String> experience = new ArrayList<>();

	@Convert(converter = ListToJsonConverter.class)
	@Column(name = "memo", columnDefinition = "json")
	private List<String> memo = new ArrayList<>();
	
	@Lob
	@Column(name = "profile_picture")
	private byte[] profilePicture;
	
	@Column(name = "create_id")
	private String createId;
	
	@Column(name = "create_time")
	private Timestamp createTime;
	
	@Column(name = "update_id")
	private String updateId;

	@Column(name = "update_time")
	private Timestamp updateTime;

}
