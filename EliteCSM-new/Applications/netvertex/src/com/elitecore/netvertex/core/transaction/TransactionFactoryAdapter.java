package com.elitecore.netvertex.core.transaction;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.core.systemx.esix.LoadBalancerType;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Netvertex's implementation of TransactionFactory interface
 * 
 */
public class TransactionFactoryAdapter implements TransactionFactory {

    private TransactionFactoryGroupImpl transactionFactoryGroup;

    public TransactionFactoryAdapter(TransactionFactoryGroupImpl transactionFactoryGroup) {
	this.transactionFactoryGroup = transactionFactoryGroup;
    }

    @Override
    public @Nullable Transaction createTransaction() {

	com.elitecore.core.commons.utilx.db.TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();

	if (transactionFactory == null) {
	    return null;
	}

	return new TransactionAdapter(transactionFactory.createTransaction(), transactionFactory.getDataSource().getDataSourceName());
    }

	@Nullable
	@Override
	public DBTransaction createReadOnlyTransaction() {
		com.elitecore.core.commons.utilx.db.TransactionFactory transactionFactory = transactionFactoryGroup.getTransactionFactory();

		if (transactionFactory == null) {
			return null;
		}

		return new ReadOnlyTransactionAdapter(transactionFactory.createTransaction(), transactionFactory.getDataSource().getDataSourceName());
	}

	@Override
    public boolean isAlive() {
	return transactionFactoryGroup.isAlive();
    }

    public static class TransactionFactoryBuilder {

	private static final String MODULE = "TRANS-FACT-BLDR";

	private final TaskScheduler taskScheduler;
	private final Map<DBDataSource, Integer> datasourcesToWeight;

	private TransactionFactoryGroupImpl transactionFactoryGroup;

	public TransactionFactoryBuilder(TaskScheduler taskScheduler, DBDataSource dbDataSource) {
	    this(taskScheduler, dbDataSource, 1);
	}
	
	
	public TransactionFactoryBuilder(TaskScheduler taskScheduler, DBDataSource dbDataSource, int weight) {
	    this.taskScheduler = taskScheduler;
	    								//TAKEN DEFAULT
	    this.transactionFactoryGroup = new TransactionFactoryGroupImpl(LoadBalancerType.SWITCH_OVER);
	    this.datasourcesToWeight = new IdentityHashMap<DBDataSource, Integer>(8);
	    
	    // default 1 for making primary
	    this.datasourcesToWeight.put(dbDataSource, weight);
	}
	
	public TransactionFactoryBuilder withDBDataSource(DBDataSource dataSource, int wieghtage) {
	    datasourcesToWeight.put(dataSource, wieghtage);
	    return this;
	}

	public TransactionFactory build() throws Exception {

	    for (Entry<DBDataSource, Integer> entry : datasourcesToWeight.entrySet()) {

		DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(entry.getKey().getDataSourceName());
		com.elitecore.core.commons.utilx.db.TransactionFactory transactionFactory = dbConnectionManager.getTransactionFactory();
		
		if (transactionFactory == null) {
		    try {
			dbConnectionManager.init(entry.getKey(), taskScheduler);
		    } catch (DatabaseInitializationException e) {
			
			getLogger().warn(MODULE, "Error While initializing DB connection. Reason: Datasource: "
				+ entry.getKey().getDataSourceName() + " is DOWN");
			getLogger().trace(MODULE, e);
			
		    } catch (DatabaseTypeNotSupportedException e) {
			throw new InitializationFailedException(e);
		    }
		    
		    transactionFactory = dbConnectionManager.getTransactionFactory();
		}
		transactionFactoryGroup.addCommunicator(transactionFactory, entry.getValue());
	    }

	    return new TransactionFactoryAdapter(transactionFactoryGroup);
	}
    }
}
