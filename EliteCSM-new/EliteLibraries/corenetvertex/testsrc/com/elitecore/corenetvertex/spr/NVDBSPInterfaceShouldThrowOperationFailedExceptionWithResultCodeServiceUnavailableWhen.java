package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.DerbyUtil;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.UUID;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class NVDBSPInterfaceShouldThrowOperationFailedExceptionWithResultCodeServiceUnavailableWhen { //NOSONAR

	private static final String DS_NAME = "test-DB";
	private static final int QUERY_TIMEOUT_CODE = 1013;
	private static final int ORACLE_DBDOWN_ERRORCODE = 17002;
	private String subscriberID = "101";
	private DummyTransactionFactory transactionFactory;
	private AlertListener alertListener;
	private Transaction transaction;
	private PreparedStatement preparedStatement;
	private SPRInfoImpl sprInfo;
	private NVDBSPInterface nvdbspInterface;
	private DBTransaction dbTransaction;
	private String testingDB = UUID.randomUUID().toString();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {
		sprInfo = new SPRInfoImpl();
		sprInfo.setSubscriberIdentity(subscriberID);
		alertListener = mock(AlertListener.class);

		DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:derby:memory:" + testingDB + ";create=true", "", "", 1, 5000, 3000);
		transactionFactory = spy((DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build());
		transactionFactory.createTransaction();
		this.transaction = mock(Transaction.class);
		this.preparedStatement = mock(PreparedStatement.class);
		this.dbTransaction = mock(DBTransaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(dbTransaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
		doReturn(preparedStatement).when(dbTransaction).prepareStatement(anyString());
		doNothing().when(preparedStatement).setString(anyShort(), anyString());
	}

	@After
	public void tearDown() throws SQLException {

		DBUtility.closeQuietly(transactionFactory.getConnection());

		DerbyUtil.closeDerby(testingDB);

	}

	public class addProfile {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecute(QUERY_TIMEOUT_CODE, false);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecute(ORACLE_DBDOWN_ERRORCODE,  true);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				nvdbspInterface.addProfile(sprInfo);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("addProfile should throw Exception");
		}
	}


	public class PurgeProfile {

		@Test(expected = OperationFailedException.class)
 		public void querytimeOut() throws Exception {
		    setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
	    }

		private void executeAndAssert() throws OperationFailedException {
			try {
				nvdbspInterface.purgeProfile(subscriberID);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("purgeProfile should throw Exception");
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}
	}

	public class markForDeleteProfile {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				nvdbspInterface.markForDeleteProfile(subscriberID);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("purgeProfile should throw Exception");
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}
	}

	public class getDeleteMarkedProfiles {



		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(QUERY_TIMEOUT_CODE, false);
			doReturn(false).when(dbTransaction).isDBDownSQLException(any(SQLException.class));

			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				nvdbspInterface.getDeleteMarkedProfiles();
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("purgeProfile should throw Exception");
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(ORACLE_DBDOWN_ERRORCODE, true);
			doReturn(true).when(dbTransaction).isDBDownSQLException(any(SQLException.class));
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

	}

	public class restoreProfile {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				nvdbspInterface.restoreProfile(subscriberID);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("purgeProfile should throw Exception");
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}
	}

	public class updateProfile {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				nvdbspInterface.updateProfile(subscriberID, new EnumMap<>(SPRFields.class));
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("purgeProfile should throw Exception");
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			nvdbspInterface = new NVDBSPInterface(alertListener, transactionFactory);
			executeAndAssert();
		}
	}

	private void setUpMockToGenerateConnectionNotFound() throws TransactionException {
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(dbTransaction).begin();
	}

	private void setUpMockToGenerateSQLExceptionOnExecuteUpdate(int errorCode, boolean isDbDownError) throws Exception {
		doReturn(isDbDownError).when(transaction).isDBDownSQLException(any(SQLException.class));
		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("sql error", "sql error", errorCode));
	}


	private void setUpMockToGenerateSQLExceptionOnExecute(int errorCode, boolean isDbDownError) throws Exception {
		doReturn(isDbDownError).when(transaction).isDBDownSQLException(any(SQLException.class));
		when(preparedStatement.execute()).thenThrow(new SQLException("sql error", "sql error", errorCode));
	}

	private void setUpMockToGenerateSQLExceptionOnExecuteQuery(int errorCode, boolean isDbDownError) throws Exception {
		doReturn(isDbDownError).when(transaction).isDBDownSQLException(any(SQLException.class));
		when(preparedStatement.executeQuery()).thenThrow(new SQLException("sql error", "sql error", errorCode));
	}

}

