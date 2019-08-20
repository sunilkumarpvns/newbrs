package org.npathai.interactivedbcsetup.domain.schema;

import java.sql.Connection;
import java.sql.SQLException;

public interface SchemaElement {

	void addSchemaEventListener(SchemaEventListener listener);
	void create(Connection connection) throws SQLException;
	void drop(Connection connection) throws SQLException;
}
