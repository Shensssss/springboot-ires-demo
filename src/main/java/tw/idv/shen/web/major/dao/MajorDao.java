package tw.idv.shen.web.major.dao;

import java.util.List;

import tw.idv.shen.web.major.entity.Major;

public interface MajorDao {
	List<Major> selectAll();
	
	Major selectMajorById(Integer majorId);
}
