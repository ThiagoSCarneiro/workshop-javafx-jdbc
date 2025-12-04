package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {
    private Connection conn;

    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller sl) {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(
                    "INSERT INTO seller "
                            + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                            + "VALUES ( ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            pstm.setString(1, sl.getName());
            pstm.setString(2, sl.getEmail());
            pstm.setDate(3, new java.sql.Date(sl.getBirthDate().getTime()));
            pstm.setDouble(4, sl.getBaseSalary());
            pstm.setLong(5, sl.getDepartment().getId());

            int rows = pstm.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pstm.getGeneratedKeys();
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    sl.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected error, no rows affected");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closePrepareStatement(pstm);

        }
    }

    @Override
    public void update(Seller sl) {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(
                    "UPDATE seller "
                            + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ?  "
                            + "WHERE Id = ?");

            pstm.setString(1, sl.getName());
            pstm.setString(2, sl.getEmail());
            pstm.setDate(3, new java.sql.Date(sl.getBirthDate().getTime()));
            pstm.setDouble(4, sl.getBaseSalary());
            pstm.setLong(5, sl.getDepartment().getId());
            pstm.setLong(6, sl.getId());

            pstm.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closePrepareStatement(pstm);
        }
    }

    @Override
    public void deleteById(Long id) {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("DELETE FROM seller WHERE id = ?");

            pstm.setLong(1, id);
            pstm.executeUpdate();
        }catch (SQLException e){
            throw  new DbException(e.getMessage());
        }finally {
            DB.closePrepareStatement(pstm);
        }
    }

    @Override
    public Seller findById(Long id) {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "WHERE seller.Id = ?");
            pstm.setLong(1, id);
            rs = pstm.executeQuery();
            if (rs.next()) {
                Department dep = instantiateDepartment(rs);
                Seller sl = instantiateSeller(rs, dep);
                return sl;
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
    public List<Seller> findAll() {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "ORDER BY Name");

            rs = pstm.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }
                Seller sl = instantiateSeller(rs, dep);
                list.add(sl);
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closePrepareStatement(pstm);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Seller> findDepartment(Department dp) {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "WHERE DepartmentId = ? "
                            + "ORDER BY Name");

            pstm.setLong(1, dp.getId());
            rs = pstm.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();
            while (rs.next()) {

                Department dep = map.get(rs.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }

                Seller sl = instantiateSeller(rs, dep);
                list.add(sl);
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closePrepareStatement(pstm);
            DB.closeResultSet(rs);
        }
    }

    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller sl = new Seller();
        sl.setId(rs.getLong("Id"));
        sl.setName(rs.getString("Name"));
        sl.setEmail(rs.getString("Email"));
        sl.setBaseSalary(rs.getDouble("BaseSalary"));
        sl.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
        sl.setDepartment(dep);
        return sl;
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getLong("DepartmentId"));
        dep.setName(rs.getString("Name"));
        return dep;
    }
}
