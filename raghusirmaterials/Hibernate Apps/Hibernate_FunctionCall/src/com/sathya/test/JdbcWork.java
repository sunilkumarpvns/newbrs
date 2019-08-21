package com.sathya.test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.jdbc.Work;

public class JdbcWork implements Work {

	@Override
	public void execute(Connection con) throws SQLException {
		CallableStatement  cstmt = con.prepareCall("{?=call  emp_bonus(?)}");
		cstmt.registerOutParameter(1, Types.DOUBLE);
		cstmt.setInt(2, 7989);
		cstmt.execute();
		System.out.println("Bonus = "+cstmt.getDouble(1));
		cstmt.close();
	}

}
