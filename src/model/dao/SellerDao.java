package model.dao;

import model.entities.Department;
import model.entities.Seller;

import java.util.List;

public interface SellerDao {
    void insert(Seller sl);
    void update(Seller sl);
    void deleteById(Long id);
    Seller findById(Long id);
    List<Seller> findAll();
    List<Seller> findDepartment(Department dp);
}
