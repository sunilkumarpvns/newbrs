package org.npathai.interactivedbcsetup.domain.schema;

@Table
public class TableDriverSchema extends TableElementSupport {

	private static final String TBLDRIVERS = "TBLDRIVERS";

	public TableDriverSchema() {
		super(TBLDRIVERS);
	}

	@Override
	protected String createStatement() {
		return "CREATE TABLE TBLDRIVERS (PersonID int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255))";
	}

	@Override
	protected String dropStatement() {
		return "DROP TABLE TBLDRIVERS";
	}
}
