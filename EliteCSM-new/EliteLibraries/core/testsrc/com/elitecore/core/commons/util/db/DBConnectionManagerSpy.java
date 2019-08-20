package com.elitecore.core.commons.util.db;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;

import org.mockito.Mockito;

import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.systemx.esix.TaskScheduler;

/**
 * A test helper spy of {@link DBConnectionManager} that helps to replicate various invariants for testing purposes.
 * 
 * <p>
 * <b>NOTE:</b> This class is only for testing purposes.
 * @author narendra.pathai
 *
 */
public class DBConnectionManagerSpy {
	private static final int QUERY_TIMEOUT_CODE = 1013;
	private DBConnectionManager spiedManager;
	private DBConnectionManager nonSpiedManager;

	public DBConnectionManagerSpy(DBConnectionManager nonSpiedManager, DBConnectionManager spiedManager) {
		this.nonSpiedManager = nonSpiedManager;
		this.spiedManager = spiedManager;
		TransactionFactory spiedTransactionFactory = spy(spiedManager.getTransactionFactory());
		when(spiedManager.getTransactionFactory()).thenReturn(spiedTransactionFactory);
	}

	public static DBConnectionManagerSpy create(DBConnectionManager manager) {
		DBConnectionManagerSpy managerSpy = new DBConnectionManagerSpy(manager, spy(manager));
		return managerSpy;
	}

	public DBConnectionManager getNonSpiedManager() {
		return nonSpiedManager;
	}

	public DBConnectionManager getSpiedInstance() {
		return spiedManager;
	}

	public void simulateDatasourceExceptionFromGetConnection() throws DataSourceException {
		doThrow(DataSourceException.class).when(spiedManager).getConnection();
	}

	public void simulateSQLRecoverableExceptionFromPreparedStatement() throws SQLException {
		PreparedStatement mockStatement = mock(PreparedStatement.class);
		Connection mockConnection = mock(Connection.class);
		when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
		when(spiedManager.getConnection()).thenReturn(mockConnection);
		doThrow(SQLRecoverableException.class).when(mockStatement).executeQuery();
		doThrow(SQLRecoverableException.class).when(mockStatement).executeBatch();
	}
	
	public void simulateQueryTimeout() throws SQLException {
		PreparedStatement mockStatement = mock(PreparedStatement.class);
		Connection mockConnection = mock(Connection.class);
		when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
		when(spiedManager.getConnection()).thenReturn(mockConnection);
		SQLException sqlTimeoutException = new SQLException("FakeQueryTimeout", "FakeState", QUERY_TIMEOUT_CODE);

		doThrow(sqlTimeoutException).when(mockStatement).executeQuery();
		doThrow(sqlTimeoutException).when(mockStatement).executeBatch();
	}
	
	public void simulateDatabaseTypeUnsupported() throws DatabaseInitializationException, DatabaseTypeNotSupportedException {
		doThrow(DatabaseTypeNotSupportedException.class).when(spiedManager).init(Mockito.any(DBDataSource.class), Mockito.any(TaskScheduler.class));
	}

	public void simulateDatabaseInitializationFailure() throws Exception {
		doThrow(DatabaseInitializationException.class).when(spiedManager).init(Mockito.any(DBDataSource.class), Mockito.any(TaskScheduler.class));
	}

	public void simulateDBDownDatasourceExceptionFromGetConnection() throws DataSourceException {
		doThrow(DataSourceException.class).when(spiedManager).getConnection();
		when(spiedManager.isDBDownSQLException(Mockito.any(DataSourceException.class))).thenReturn(true);
	}
}
