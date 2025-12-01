package model.dao;

import model.entities.Department;
import model.entities.Seller;

import java.util.List;

public interface SellerDao {
    void inser(Seller sl);
    void update(Seller sl);
    void delete(Integer id);
    Seller findById(Integer id);
    List<Seller> findAll();
    List<Seller> findDepartment(Department dp);
}
