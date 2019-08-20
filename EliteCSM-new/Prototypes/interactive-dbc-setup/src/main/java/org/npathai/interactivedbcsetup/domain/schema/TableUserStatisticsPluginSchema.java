package org.npathai.interactivedbcsetup.domain.schema;

@Table
public class TableUserStatisticsPluginSchema extends TableElementSupport {

	private static final String TBLUSERSTATISTICS = "TBLUSERSTATISTICS";

	public TableUserStatisticsPluginSchema() {
		super(TBLUSERSTATISTICS);
	}

	@Override
	protected String createStatement() {
		return "CREATE TABLE " + TBLUSERSTATISTICS
				+ " (PersonID int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255))";
	}

	@Override
	protected String dropStatement() {
		return "DROP TABLE " + TBLUSERSTATISTICS;
	}
}
