package tw.idv.shen.web.major.service;

import java.util.List;

import tw.idv.shen.web.major.entity.Major;

public interface MajorService {
	List<Major> findAllMajor();

	Major findById(Integer majorId);

}
