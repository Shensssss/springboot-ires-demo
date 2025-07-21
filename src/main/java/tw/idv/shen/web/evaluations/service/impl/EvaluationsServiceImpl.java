package tw.idv.shen.web.evaluations.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.idv.shen.web.evaluations.dao.EvaluationsDao;
import tw.idv.shen.web.evaluations.entity.Evaluations;
import tw.idv.shen.web.evaluations.service.EvaluationsService;

@Service
@Transactional
public class EvaluationsServiceImpl implements EvaluationsService {

	@Autowired 
	private EvaluationsDao dao;
	
	@Override
	public List<Evaluations> getEvaluationsByClinicId(Integer clinicId) {
		return dao.findEvaluationsByClinicId(clinicId);
	}

	@Override
	public Integer addComment(Evaluations evaluations) {
		return dao.insert(evaluations);
	}

}
