package com.elitecore.nvsmx.system.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;

import javax.annotation.Nullable;

public class TransactionFactoryImpl extends ESCommunicatorImpl implements TransactionFactory {

	
	private static final String MODULE = "TRAN-FACTORY"; 
	private String datasourceName;
	private int statusCheckDuration;

	public TransactionFactoryImpl(TaskScheduler scheduler, String datasourceName) {
		this(scheduler, datasourceName, ESCommunicator.NO_SCANNER_THREAD);
	}
	
	public TransactionFactoryImpl(TaskScheduler scheduler, String datasourceName, int statusCheckDuration) {
		super(scheduler);
		this.datasourceName = datasourceName;
		this.statusCheckDuration = statusCheckDuration;
	}

	@Override
	public Transaction createTransaction() {
		
		if (isAlive() == false) {
			return null;
		}
		
		
		return new TransactionAdapter(datasourceName) {


			@Override
			public void markDead() {
				TransactionFactoryImpl.this.markDead();
			}
			
			@Override
			public boolean isDBDownSQLException(SQLException ex) {
				return DBConnectionManager.getInstance(datasourceName).isDBDownSQLException(ex);
			}
			
			@Override
			public String getDataSourceName() {
				return datasourceName;
			}
		};
			
	}

	@Nullable
	@Override
	public DBTransaction createReadOnlyTransaction() {
		if (isAlive() == false) {
			return null;
		}


		return new ReadOnlyTransactionAdapter(datasourceName) {

			@Override
			public void markDead() {
				TransactionFactoryImpl.this.markDead();
			}

			@Override
			public boolean isDBDownSQLException(SQLException ex) {
				return DBConnectionManager.getInstance(datasourceName).isDBDownSQLException(ex);
			}

			@Override
			public String getDataSourceName() {
				return datasourceName;
			}
		};
	}


	@Override
	public void scan() {
		Connection connection = null;
		try {
			connection = DBConnectionManager.getInstance(datasourceName).getConnection();
			markAlive();
		} catch (SQLException e) {
			markDead();
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error while checking for aliveness of datasource: " + 
						datasourceName + ", Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		} finally{
			DBUtility.closeQuietly(connection);
		}
	
	}

	@Override
	public String getName() {
		return datasourceName;
	}

	@Override
	public String getTypeName() {
		return MODULE;
	}

	@Override
	protected int getStatusCheckDuration() {
		return statusCheckDuration;
	}


}
