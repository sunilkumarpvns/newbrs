package org.npathai.interactivedbcsetup.domain.schema;

@Table
public class TableRadiusCustomerSchema extends TableElementSupport {

	private static final String TBLRADIUSCUSTOMER = "TBLRADIUSCUSTOMER";

	public TableRadiusCustomerSchema() {
		super(TBLRADIUSCUSTOMER);
	}

	@Override
	protected String createStatement() {
		return "CREATE TABLE " + TBLRADIUSCUSTOMER
				+ " (PersonID int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255))";
	}

	@Override
	protected String dropStatement() {
		return "DROP TABLE " + TBLRADIUSCUSTOMER;
	}
}
