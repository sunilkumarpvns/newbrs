package com.app;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EmpRowMapper implements RowMapper<Employee> {

	public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {

		Employee e=new Employee();
		e.setEmpId(rs.getInt("eid"));
		e.setEmpName(rs.getString("ename"));
		e.setEmpSal(rs.getInt("esal"));
		return e;
	}
}