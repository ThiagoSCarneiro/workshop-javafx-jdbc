package model.dao;

import model.entities.Department;

import java.util.List;

public interface DepartmentDao {

    void insert(Department dp);
    void update(Department dp);
    void deleteById(Long id);
    Department findById(Long id);
    List<Department> findAll();

}
