package com.sterlite.netvertex.nvsampler.cleanup.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.sterlite.netvertex.nvsampler.cleanup.Result;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.jmeter.protocol.jdbc.config.DataSourceElement;


import static com.sterlite.netvertex.nvsampler.cleanup.util.SamplerLogger.getLogger;

public class DBQueryExecutor {

	public Result executeUpdate(String query, String param) {
		Result result = new Result("Execute Query: " + query + " with param: " + param);
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			getLogger().info("Executing: " + query);
			conn = DataSourceElement.getConnection("DB");
			ps = conn.prepareStatement(query);
			ps.setString(1, param);
			ps.setQueryTimeout(1);
			ps.executeUpdate();
			conn.commit();
			result.success();
		} catch (SQLException e) {
			getLogger().error(e.getMessage(), e);
			result.notSuccess();
			result.setFailMessage(ExceptionUtils.getStackTrace(e));
		} finally {
			closePreparedStatement(ps);
			closeConnection(conn);
		}

		return result;
	}

	private void closeConnection(Connection conn) {
		if (conn == null) {
			return;
		}
		try {
			conn.close();
		} catch (SQLException e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	private void closePreparedStatement(PreparedStatement ps) {
		if (ps == null) {
			return;
		}
		try {
			ps.close();
		} catch (SQLException e) {
			getLogger().error(e.getMessage(), e);
		}
	}
}
