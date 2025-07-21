package tw.idv.shen.web.major.entity;

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
import tw.idv.shen.web.clinic.entity.Clinic;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clinicMajor")
public class ClinicMajor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_major_id")
    private Integer clinicMajorId;
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;
    @ManyToOne
    @JoinColumn(name = "major_id")
    private Major major;

    @Column(name="create_id")
    private Integer createId;
    
    @Column(name="create_time")
    private Timestamp createTime;
    
    @Column(name="update_id")
    private Integer updateId;
    
    @Column(name="update_time")
    private Timestamp updateTime;

}
