package tw.idv.shen.web.major.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.idv.shen.web.major.dao.MajorDao;
import tw.idv.shen.web.major.entity.Major;
import tw.idv.shen.web.major.service.MajorService;

@Service
public class MajorServiceImpl implements MajorService{
	@Autowired
	private MajorDao dao; 

	@Override
	public List<Major> findAllMajor() {
		return dao.selectAll();
	}
	
	@Override
	public Major findById(Integer majorId) {
		return dao.selectMajorById(majorId);	
	}

}
