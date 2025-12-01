package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmenDaoJDBC implements DepartmentDao {

    private Connection conn;

    public DepartmenDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department dp) {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("INSERT INTO department (Name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, dp.getName());

            int rows = pstm.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pstm.getGeneratedKeys();
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    dp.setId(id);
                }
                DB.closeResultSet(rs);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closePrepareStatement(pstm);
        }
    }

    @Override
    public void update(Department dp) {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("UPDATE department set Name = ? Where Id = ?");

            pstm.setString(1, dp.getName());
            pstm.setLong(2, dp.getId());

            pstm.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("DELETE FROM department WHERE Id = ?");

            pstm.setInt(1, id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }finally {
            DB.closePrepareStatement(pstm);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement("SELECT * FROM department WHERE Id = ?");
            pstm.setInt(1, id);
            rs = pstm.executeQuery();
            Department dep = new Department();
            if (rs.next()) {
                dep = instantiateDepartment(rs);
                return dep;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closePrepareStatement(pstm);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement("SELECT * FROM department");
            rs = pstm.executeQuery();
            Department dep;
            List<Department> list = new ArrayList<>();
            while (rs.next()) {
                dep = instantiateDepartment(rs);
                list.add(dep);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closePrepareStatement(pstm);
            DB.closeResultSet(rs);
        }
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getLong("Id"));
        dep.setName(rs.getString("Name"));
        return dep;
    }
}
