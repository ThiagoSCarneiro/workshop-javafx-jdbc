package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;


public class DepartmentServices  {
	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll(){

		return dao.findAll();
	}	
	
	public void saveOrUpdate(Department dpj) {
		if(dpj.getId() == null) {
			dao.insert(dpj);			
		}else {
			dao.update(dpj);
		}
	}
	
	public void remove(Department dpj) {
		dao.deleteById(dpj.getId());
	}
	

}
