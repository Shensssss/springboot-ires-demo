package tw.idv.shen.web.evaluations.dao;

import java.util.List;

import tw.idv.shen.core.dao.CoreDao;
import tw.idv.shen.web.evaluations.entity.Evaluations;

public interface EvaluationsDao extends CoreDao<Evaluations, Integer>{
	List<Evaluations> findEvaluationsByClinicId(Integer clinicId);
}
