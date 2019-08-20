package com.elitecore.aaa.radius.drivers.conf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.commons.base.DBUtility;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class DriverSelectQueryBuilderTest {

	private static final DriverTypes ANY_DRIVER_TYPE = DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER;
	private static final Set<String> ANY_SET = Collections.emptySet();
	
	@Rule public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws ClassNotFoundException {
		loadDerbyDriver();
	}
	
	private static void loadDerbyDriver() throws ClassNotFoundException {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}
	
	public class DBConnectionNotRequiredContext {
		
		@Test
		public void testConstructor_ShouldThrowNPE_IfDriverTypeIsNull() {
			exception.expect(NullPointerException.class);
			exception.expectMessage("driverType is null");
			
			new DriverSelectQueryBuilder(null, ANY_SET);
		}
		
		@Test
		public void testConstructor_ShouldThrowNPE_IfDriverTypesToReadSetIsNull() {
			exception.expect(NullPointerException.class);
			exception.expectMessage("driverIdsToRead is null");
			
			new DriverSelectQueryBuilder(ANY_DRIVER_TYPE, null);
		}
	}

	public class DBConnectionRequiredContext {
		
		private static final String TBLMDRIVERINSTANCE = "TBLMDRIVERINSTANCE";
		private Connection derbyConnection;
		
		@Before
		public void setUp() throws SQLException {
			connectToDerby();
			createTable(TBLMDRIVERINSTANCE);
		}
		
		private void connectToDerby() throws SQLException {
			derbyConnection = DriverManager.getConnection("jdbc:derby:memory:TestingDB;create=true");
		}
		
		private void createTable(String string) throws SQLException {
			derbyConnection.prepareCall("CREATE TABLE " + TBLMDRIVERINSTANCE 
					+ " (DRIVERINSTANCEID VARCHAR(36),DRIVERTYPEID VARCHAR(25))").execute();
		}
		
		@Test
		public void testBuild_ShouldGenerateAQueryThatReturnsNoDriverIds_WhenEmptyDriverIdsAreProvided() throws SQLException {
			insertSomeData();
			
			String query = new DriverSelectQueryBuilder(ANY_DRIVER_TYPE, Collections.<String>emptySet()).build();
			
			assertTrue(valuesOf("DRIVERINSTANCEID", query).isEmpty());
		}

		private void insertSomeData() throws SQLException {
			List<String> expectedDriverIds = Arrays.asList("driver_1", "driver_2", "driver_3");
			List<String> otherDriverIds = Arrays.asList("driver_4", "driver_5", "driver_6");
			DriverTypes anyDriverType = DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER;
			
			insertInto(TBLMDRIVERINSTANCE).driverIds(expectedDriverIds).withType(anyDriverType);
			insertInto(TBLMDRIVERINSTANCE).driverIds(otherDriverIds).withType(anyDriverType);
		}
		
		@Test
		public void testBuild_ShouldGenerateAQueryThatReadsOnlyTheSelectedDriverIds() throws SQLException {
			List<String> expectedDriverIds = Arrays.asList("driver_1", "driver_2", "driver_3");
			List<String> otherDriverIds = Arrays.asList("driver_4", "driver_5", "driver_6");
			DriverTypes anyDriverType = DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER;
			
			insertInto(TBLMDRIVERINSTANCE).driverIds(expectedDriverIds).withType(anyDriverType);
			insertInto(TBLMDRIVERINSTANCE).driverIds(otherDriverIds).withType(anyDriverType);
			
			String query = new DriverSelectQueryBuilder(anyDriverType, 
					expectedDriverIds).build();
			
			assertEquals(expectedDriverIds, valuesOfDriverInstanceId("DRIVERINSTANCEID", query));
		}

		private List<Integer> valuesOf(String columnName, String query) throws SQLException {
			List<Integer> actualDriverIds = new ArrayList<Integer>();
			ResultSet resultSet = null;
			PreparedStatement prepareStatement = null;

			try {
				prepareStatement = derbyConnection.prepareStatement(query);
				resultSet = prepareStatement.executeQuery();

				while (resultSet.next()) {
					actualDriverIds.add(resultSet.getInt(columnName));
				}
				return actualDriverIds;
			} finally {
				DBUtility.closeQuietly(resultSet);
				DBUtility.closeQuietly(prepareStatement);
			}
		}
		
		
		private List<String> valuesOfDriverInstanceId(String columnName, String query) throws SQLException {
			List<String> actualDriverIds = new ArrayList<String>();
			ResultSet resultSet = null;
			PreparedStatement prepareStatement = null;

			try {
				prepareStatement = derbyConnection.prepareStatement(query);
				resultSet = prepareStatement.executeQuery();

				while (resultSet.next()) {
					actualDriverIds.add(resultSet.getString(columnName));
				}
				return actualDriverIds;
			} finally {
				DBUtility.closeQuietly(resultSet);
				DBUtility.closeQuietly(prepareStatement);
			}
		}
		
		@Test
		public void testBuild_ShouldGenerateAQueryThatReadsOnlyTheDriversWithSpecifiedType() throws SQLException {
			List<String> driverIds = Arrays.asList("driver_1", "driver_2", "driver_3");
			List<String> otherDriverIds = Arrays.asList("driver_4", "driver_5", "driver_6");
			
			List<String> allDriverIds = new ArrayList<String>();
			allDriverIds.addAll(driverIds);
			allDriverIds.addAll(otherDriverIds);
			
			DriverTypes expectedDriverType = DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER;
			DriverTypes otherDriverType = DriverTypes.RAD_DETAIL_LOCAL_ACCT_DRIVER;
			
			insertInto(TBLMDRIVERINSTANCE).driverIds(driverIds).withType(expectedDriverType);
			insertInto(TBLMDRIVERINSTANCE).driverIds(otherDriverIds).withType(otherDriverType);
			
			String query = new DriverSelectQueryBuilder(expectedDriverType, 
					allDriverIds).build();
			
			List<Integer> actualDriverTypes = valuesOf("DRIVERTYPEID", query);
			for (Integer actualDriverType : actualDriverTypes) {
				assertEquals(new Integer(expectedDriverType.value), actualDriverType);
			}
		}
		
		private InsertQueryBuilder insertInto(String tableName) {
			return new InsertQueryBuilder(tableName);
		}
		
		class InsertQueryBuilder {
			private Collection<String> driverIds;
			private final String tableName;

			public InsertQueryBuilder(String tableName) {
				this.tableName = tableName;
			}
			
			InsertQueryBuilder driverIds(Collection<String> driverIds) {
				this.driverIds = driverIds;
				return this;
			}
			
			void withType(DriverTypes driverType) throws SQLException {
				for (String driverInstanceId : driverIds) {
					CallableStatement insertQuery = derbyConnection.prepareCall("INSERT INTO " + tableName + 
							" VALUES ('" 
							+ driverInstanceId
							+ "',"
							+ "'" + driverType.value + "'"
							+ ")");
					insertQuery.execute();
				}
			}
		}

		@After
		public void tearDown() throws SQLException {
			dropTable(TBLMDRIVERINSTANCE);
			DBUtility.closeQuietly(derbyConnection);
		}

		private void dropTable(String tableName) throws SQLException {
			derbyConnection.prepareCall("DROP TABLE " + tableName).execute();
		}
	}
	
}
