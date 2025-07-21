package tw.idv.shen.web.major.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "major")
public class Major{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "major_id")
    private Integer majorId;
	
	@Column(name = "symptom")
	private String symptom;

	@Column(name = "description")
	private String description;
	
	@Column(name = "profile_picture")
	private byte[] image;

    @Column(name = "major_name", nullable = false, unique = true)
    private String majorName;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;
}
