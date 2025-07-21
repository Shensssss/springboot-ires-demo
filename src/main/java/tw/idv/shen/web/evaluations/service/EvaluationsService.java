package tw.idv.shen.web.evaluations.service;

import java.util.List;

import tw.idv.shen.web.evaluations.entity.Evaluations;

public interface EvaluationsService {
	List<Evaluations> getEvaluationsByClinicId(Integer clinicId);
	Integer addComment(Evaluations evaluations);
}
