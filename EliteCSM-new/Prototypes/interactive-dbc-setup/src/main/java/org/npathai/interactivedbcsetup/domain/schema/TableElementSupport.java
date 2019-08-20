package org.npathai.interactivedbcsetup.domain.schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class TableElementSupport implements SchemaElement {

	private SchemaEventListener listener;
	private String tableName;
	
	public TableElementSupport(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public void addSchemaEventListener(SchemaEventListener listener) {
		this.listener = listener;
	}

	@Override
	public void create(Connection connection) throws SQLException {
		listener.creating(tableName);
		try (PreparedStatement statement = 
				connection.prepareStatement(createStatement())) {
			statement.executeUpdate();
			listener.created(tableName);
		} catch (SQLException ex) {
			ex.printStackTrace();
			listener.error(tableName, String.valueOf(ex));
			throw ex;
		}
	}

	protected abstract String createStatement();
	
	@Override
	public void drop(Connection connection) throws SQLException {
		listener.dropping(tableName);
		try (PreparedStatement statement = 
				connection.prepareStatement(dropStatement())) {
			statement.executeUpdate();
			listener.dropped(tableName);
		} catch (SQLException ex) {
			ex.printStackTrace();
			listener.error(tableName, String.valueOf(ex));
			throw ex;
		}
	}

	protected abstract String dropStatement();
}
