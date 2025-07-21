package tw.idv.shen.web.clinic.dao.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.PersistenceContext;
import tw.idv.shen.web.clinic.dao.ClinicDAO;
import tw.idv.shen.web.clinic.entity.CallNumber;
import tw.idv.shen.web.clinic.entity.Clinic;

@Repository
public class ClinicDaoImpl implements ClinicDAO {

    @PersistenceContext
    private Session session;

    @Override
    public int insert(Clinic clinic) {
        session.persist(clinic);
        return 1;
    }

    @Override
    public int update(Clinic clinic) {
        Clinic newclinic = session.load(Clinic.class, clinic.getClinicId());
        newclinic.setClinicName(clinic.getClinicName());
        newclinic.setAccount(clinic.getAccount());
        newclinic.setPassword(clinic.getPassword());
        newclinic.setPhone(clinic.getPhone());
        newclinic.setAddressCity(clinic.getAddressCity());
        newclinic.setAddressTown(clinic.getAddressTown());
        newclinic.setAddressRoad(clinic.getAddressRoad());
        newclinic.setWeb(clinic.getWeb());
        newclinic.setRegistrationFee(clinic.getRegistrationFee());
        newclinic.setMemo(clinic.getMemo());

        session.update(newclinic);
        return 1;
    }

    @Override
    public int deleteById(Integer id) {
        Clinic clinic = session.load(Clinic.class, id);
        session.remove(clinic);
        return 1;
    }

    @Override
    public Clinic selectById(Integer id) {
        return session.get(Clinic.class, id);
    }

    @Override
    public int updatePsd(Clinic newclinic) {

        Clinic clinic = session.load(Clinic.class, newclinic.getClinicId());
        final String newclinicPsd = newclinic.getPassword();
        if (newclinicPsd != null && !newclinicPsd.isEmpty()) {
            clinic.setPassword(newclinic.getPassword());
        }
        session.update(clinic);
        return 1;
    }

    @Override
    public List<Clinic> selectAll() {
        return session.createQuery("FROM Clinic", Clinic.class).getResultList();
    }

    @Override
    public Integer findClinicIdByAgencyId(String agencyId) {
        return session.createQuery("SELECT c.clinicId FROM Clinic c WHERE c.agencyId = :id", Integer.class)
                .setParameter("id", agencyId).getSingleResult();

    }

    @Override
    public List<Clinic> getClinicByAccount(String clinic_account) {

        return session.createQuery("FROM Clinic where account  = :clinic_account ", Clinic.class)
                .setParameter("clinic_account", clinic_account).getResultList();

    }

    @Override
    public Clinic selectById(int clinic_id) {
        return session.get(Clinic.class, clinic_id);

    }

    @Override
    public Optional<CallNumber> findCallNumber(Integer clinicId, Integer doctorId, Integer timePeriod, LocalDate date) {
        String hql = "FROM CallNumber c WHERE c.clinicId = :clinicId AND c.doctorId = :doctorId AND c.timePeriod = :timePeriod AND c.appointmentDate = :date";
        return session.createQuery(hql, CallNumber.class)
                .setParameter("clinicId", clinicId)
                .setParameter("doctorId", doctorId)
                .setParameter("timePeriod", timePeriod)
                .setParameter("date", date)
                .uniqueResultOptional();
    }

    @Override
    public CallNumber save(CallNumber callNumber) {
        session.persist(callNumber);
        return callNumber;
    }

    @Override
    public List<CallNumber> findCallNumbersByClinicIdAndDate(Integer clinicId, LocalDate date) {
        String hql = "FROM CallNumber c WHERE c.clinicId = :clinicId AND c.appointmentDate = :date ORDER BY c.timePeriod";
        return session.createQuery(hql, CallNumber.class)
                .setParameter("clinicId", clinicId)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    public CallNumber findByClinicDoctorDate(Integer clinicId, Integer doctorId, LocalDate date) {
        String hql = "FROM CallNumber cn WHERE cn.clinicId = :clinicId AND cn.doctorId = :doctorId AND cn.appointmentDate = :date";
        return session.createQuery(hql, CallNumber.class)
                .setParameter("clinicId", clinicId)
                .setParameter("doctorId", doctorId)
                .setParameter("date", date)
                .uniqueResult();
    }
}
