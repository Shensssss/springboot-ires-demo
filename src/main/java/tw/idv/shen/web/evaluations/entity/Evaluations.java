package tw.idv.shen.web.evaluations.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.idv.shen.core.pojo.Core;
import tw.idv.shen.web.clinic.entity.Clinic;
import tw.idv.shen.web.patient.entity.Patient;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "evaluations")
public class Evaluations extends Core {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "evaluate_id")
	private String evaluateId;
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;
	@ManyToOne
	@JoinColumn(name = "clinic_id")
	private Clinic clinic;
	@Column(name = "rating")
	private Integer rating;
	@Column(name="comment")
	private String comment;
	@Column(name = "create_time", updatable = false, insertable = false)
	private Timestamp createTime;
}
