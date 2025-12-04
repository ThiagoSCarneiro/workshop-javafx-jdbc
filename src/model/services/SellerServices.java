package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;


public class SellerServices  {
	private SellerDao dao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll(){

		return dao.findAll();
	}	
	
	public void saveOrUpdate(Seller dpj) {
		if(dpj.getId() == null) {
			dao.insert(dpj);			
		}else {
			dao.update(dpj);
		}
	}
	
	public void remove(Seller dpj) {
		dao.deleteById(dpj.getId());
	}
	

}
