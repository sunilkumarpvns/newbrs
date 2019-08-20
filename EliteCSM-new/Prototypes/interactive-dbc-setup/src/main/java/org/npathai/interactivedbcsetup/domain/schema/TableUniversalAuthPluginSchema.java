package org.npathai.interactivedbcsetup.domain.schema;

@Table
public class TableUniversalAuthPluginSchema extends TableElementSupport {
	
	private static final String TABLE = "TBLUNIVERSALAUTHPLUGIN";

	public TableUniversalAuthPluginSchema() {
		super(TABLE);
	}

	@Override
	protected String createStatement() {
		return "CREATE TABLE " + TABLE
				+ " (PersonID int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255))";
	}

	@Override
	protected String dropStatement() {
		return "DROP TABLE " + TABLE;
	}
}
