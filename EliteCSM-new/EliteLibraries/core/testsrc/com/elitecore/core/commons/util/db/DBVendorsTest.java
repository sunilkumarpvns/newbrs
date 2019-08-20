package com.elitecore.core.commons.util.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class DBVendorsTest {

	private static final String SEQUENCE_NAME = "testSequence";
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void fiveVendorsAreSupported() {
		assertThat(DBVendors.values().length, is(equalTo(5)));
	}
	
	@Test
	public void oracleVendorIsSupported() throws DatabaseTypeNotSupportedException {
		assertThat(DBVendors.fromUrl("jdbc:oracle:thin:@//host:port/database"), is(equalTo(DBVendors.ORACLE)));
	}
	
	@Test
	public void timestenVendorIsSupported() throws DatabaseTypeNotSupportedException {
		assertThat(DBVendors.fromUrl("jdbc:timesten:direct:dsn=name"), is(equalTo(DBVendors.TIMESTEN)));
	}
	
	@Test
	public void postgresVendorIsSupported() throws DatabaseTypeNotSupportedException {
		assertThat(DBVendors.fromUrl("jdbc:postgresql://host:port/database"), is(equalTo(DBVendors.POSTGRESQL)));
	}

	@Test
	public void derbyVendorIsSupported() throws DatabaseTypeNotSupportedException {
		assertThat(DBVendors.fromUrl("jdbc:derby:memory:database;create=true"), is(equalTo(DBVendors.DERBY)));
	}
	
	@Test
	public void mySqlVendorIsSupported() throws DatabaseTypeNotSupportedException {
		assertThat(DBVendors.fromUrl("jdbc:mysql//host:port/database"), is(equalTo(DBVendors.MYSQL)));
	}
	
	@Test
	public void throwsTypeNotSupportedExceptionIfUrlVendorIsUnknown() throws DatabaseTypeNotSupportedException {
		exception.expect(DatabaseTypeNotSupportedException.class);
		
		DBVendors.fromUrl("jdbc:unknown//host:port/database");
	}
	
	public Object[][] dataFor_Test_VendorSpecificSequenceSyntax() {
		return new Object[][] {
				{DBVendors.ORACLE, SEQUENCE_NAME, SEQUENCE_NAME + ".NEXTVAL"},
				{DBVendors.POSTGRESQL, SEQUENCE_NAME, "nextval('"+ SEQUENCE_NAME + "')"},
				{DBVendors.MYSQL, SEQUENCE_NAME, "nextval('"+ SEQUENCE_NAME + "')"},
				{DBVendors.TIMESTEN, SEQUENCE_NAME, SEQUENCE_NAME+".NEXTVAL"},
				{DBVendors.DERBY, SEQUENCE_NAME, "'next value for " + SEQUENCE_NAME + "'"},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_Test_VendorSpecificSequenceSyntax")
	public void verifyVendorSpecificSequenceSyntax(DBVendors dbVendor, String sequenceName, String outputSequenceSyntax){
		assertThat(dbVendor.getVendorSpecificSequenceSyntax(sequenceName), is(equalTo(outputSequenceSyntax)));
	}
}
