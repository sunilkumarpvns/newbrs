package com.elitecore.corenetvertex.spr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.UMBatchOperation.BatchOperationData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.ConcurrentLinkedQueue;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(HierarchicalContextRunner.class)
public class UMBatchOperationTest {

	private UMBatchOperation umBatchOperation;
	private TransactionFactory transactionFactory;
	private RecordProcessor<BatchOperationData> recordProcessor;
	private DBSingleRecordFailoverOperation<BatchOperationData> failOverOperation;
	private ScheduledExecutorService batchExecutor;
	private CSVFailOverOperation<BatchOperationData> csvFailOverOperation;
	ConcurrentLinkedQueue<BatchOperationData> batchOperationQueue;

	@Before
	public void init() throws OperationFailedException, InterruptedException {

		batchExecutor = mock(ScheduledExecutorService.class);
		recordProcessor = mock(RecordProcessor.class);
		transactionFactory = mock(TransactionFactory.class);
		Executor executor = mock(Executor.class);
		csvFailOverOperation = spy(new CSVFailOverOperation<UMBatchOperation.BatchOperationData>(null, executor));
		batchOperationQueue = spy(new ConcurrentLinkedQueue<BatchOperationData>(3));
		failOverOperation = spy(new DBSingleRecordFailoverOperation<BatchOperationData>(new SingleRecordOperation<BatchOperationData>() {

			@Override
			public void process(BatchOperationData dataToProcess, TransactionFactory transactionFactory) throws OperationFailedException {

			}
		}, null, transactionFactory, executor));

		doReturn(true).when(batchExecutor).awaitTermination(5, TimeUnit.SECONDS);

		setUpUMBatch();
	}

	public class OnTransactionException {

		private static final String COMMIT = "COMMITT";
		private static final String EXECUTE_BATCH = "EXECUTE_BATCH";
		private static final String CONNECTION_NOT_FOUND = "CONNECTION_NOT_FOUND";
		private static final String BEGIN = "BEGIN";
		private static final String PREPARED_STATEMENT = "PREPARED_STATEMENT";

		@Test
		public void failOverWhilePreparedStatementCall() throws Exception {

			setUpTransactionFailedException(PREPARED_STATEMENT);

			runBatch();
			verify(failOverOperation, times(1)).doFailover(Mockito.<Collection> any());
		}

		@Test
		public void csvFailOverWhileConnectionNotFound() throws Exception  {

			setUpTransactionFailedException(CONNECTION_NOT_FOUND);

			runBatch();
			verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection> any());
		}

		@Test
		public void failOverWhileBeginCall() throws Exception  {

			setUpTransactionFailedException(BEGIN);

			runBatch();
			verify(failOverOperation, times(1)).doFailover(Mockito.<Collection> any());
		}

		@Test
		public void failOverWhileExecuteBatchCall() throws Exception {

			setUpTransactionFailedException(EXECUTE_BATCH);
			
			runBatch();
			verify(failOverOperation, times(1)).doFailover(Mockito.<Collection>any());
		}
		
		@Test
		public void failOverWhileCommit() throws Exception {

			setUpTransactionFailedException(COMMIT);
			
			runBatch();
			verify(failOverOperation, times(1)).doFailover(Mockito.<Collection>any());
		}
		
		private void setUpTransactionFailedException(String on) throws Exception {
			
			Transaction transaction = spy(new DummyTransaction(mock(Connection.class), null)); 
			doReturn(transaction).when(transactionFactory).createTransaction();
			doReturn(true).when(transactionFactory).isAlive();
			when(transactionFactory.createTransaction()).thenReturn(transaction);

			if (on.equals(PREPARED_STATEMENT)) {
				doThrow(new TransactionException("Test Exception")).when(transaction).prepareStatement(Mockito.anyString());
			} else if (on.equals(BEGIN)) {
				doThrow(new TransactionException("Test Exception")).when(transaction).begin();
			} else if (on.equals(CONNECTION_NOT_FOUND)) {
				doThrow(new TransactionException("Test Exception", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
			} else if (on.equals(EXECUTE_BATCH)) {
				PreparedStatement preparedStatement = mock(PreparedStatement.class);
				doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
				doThrow(new SQLException("Test Exception")).when(preparedStatement).executeBatch();
			} else if (on.equals(COMMIT)) {
				doThrow(new TransactionException("Test Exception")).when(transaction).commit();
			}
		}

	}

	public class OnSQLException {

		private static final String DB_DOWN = "DB_DOWN";
		private static final String GENERAL_SQL_EXCEPTION = "GENERAL";

		@Test
		public void failOver() throws InterruptedException, TransactionException, SQLException {

			setUpSQLException(GENERAL_SQL_EXCEPTION);

			runBatch();
			verify(failOverOperation, times(1)).doFailover(Mockito.<Collection> any());
		}

		@Test
		public void csvFailOverWhileDBDownException() throws InterruptedException, TransactionException, SQLException {

			setUpSQLException(DB_DOWN);

			runBatch();
			verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection> any());
		}

		private void setUpSQLException(String on) throws TransactionException, SQLException {

			Transaction transaction = spy(new DummyTransaction(mock(Connection.class), null));
			doReturn(transaction).when(transactionFactory).createTransaction();
			doReturn(true).when(transactionFactory).isAlive();
			when(transactionFactory.createTransaction()).thenReturn(transaction);

			PreparedStatement preparedStatement = mock(PreparedStatement.class);
			doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

			if (on.equals(DB_DOWN)) {
				doReturn(true).when(transaction).isDBDownSQLException(Mockito.any());
			}

			doThrow(new SQLException("Test Exception")).when(preparedStatement).setQueryTimeout(Mockito.anyInt());
		}

	}

	public class OnRunTimeException {

		@Test
		public void failOver() throws InterruptedException, TransactionException, SQLException {

			setUpRunTimeException();

			runBatch();
			verify(failOverOperation, times(1)).doFailover(Mockito.<Collection> any());
		}

		private void setUpRunTimeException() throws TransactionException, SQLException {

			Transaction transaction = spy(new DummyTransaction(mock(Connection.class), null));
			doReturn(transaction).when(transactionFactory).createTransaction();
			doReturn(true).when(transactionFactory).isAlive();
			when(transactionFactory.createTransaction()).thenReturn(transaction);

			doThrow(new NumberFormatException("Test Exception")).when(transaction).begin();
		}

	}

	public class OnDataSourceUnavailable {

		@Test
		public void csvFailOverWhileTransactionFactoryIsNotAlive() throws InterruptedException, TransactionException, SQLException {

			doReturn(false).when(transactionFactory).isAlive();
			runBatch();
			verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<BatchOperationData>> any());
		}

	}

	public class OnQueueFull {

		private static final String SUBSCRIBER_ID = "SUB_ID";
		
		@Before
		public void setUp() {
			doReturn(false).when(batchOperationQueue).add(Mockito.any());
		}
		
		public class WhenTransactionFactoryAlive {

			@Before
			public void setUp() {
				when(transactionFactory.isAlive()).thenReturn(true);
			}
			
			@Test
			public void failoverOnInsert() throws Exception {

				List<SubscriberUsage> usages = createUsages();
				umBatchOperation.insert(SUBSCRIBER_ID, usages, transactionFactory);
				verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<BatchOperationData>> any());
			}
			
			@Test
			public void failoverOnAddToExisting() throws Exception {

				List<SubscriberUsage> usages = createUsages();
				umBatchOperation.addToExisting(SUBSCRIBER_ID, usages, transactionFactory);
				verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<BatchOperationData>> any());
			}
			
			@Test
			public void failoverOnReplace() throws Exception {

				List<SubscriberUsage> usages = createUsages();
				umBatchOperation.replace(SUBSCRIBER_ID, usages, transactionFactory);
				verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<BatchOperationData>> any());
			}

		}

		public class WhenTransactionFactoryNotAlive {

			@Before
			public void setUp() {
				when(transactionFactory.isAlive()).thenReturn(false);
			}
			
			@Test
			public void failoverOnInsert() throws Exception {

				List<SubscriberUsage> usages = createUsages();
				umBatchOperation.addToExisting(SUBSCRIBER_ID, usages, transactionFactory);
				verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<BatchOperationData>> any());
			}
			
			@Test
			public void failoverOnAddToExisting() throws Exception {

				List<SubscriberUsage> usages = createUsages();
				umBatchOperation.insert(SUBSCRIBER_ID, usages, transactionFactory);
				verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<BatchOperationData>> any());
			}
			
			@Test
			public void failoverOnReplace() throws Exception {

				List<SubscriberUsage> usages = createUsages();
				umBatchOperation.replace(SUBSCRIBER_ID, usages, transactionFactory);
				verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<BatchOperationData>> any());
			}
		}

		private List<SubscriberUsage> createUsages() {
			List<SubscriberUsage> usages = new ArrayList<SubscriberUsage>();
			usages.add(new SubscriberUsage.SubscriberUsageBuilder(SUBSCRIBER_ID, "", "", "", "","").build());
			usages.add(new SubscriberUsage.SubscriberUsageBuilder(SUBSCRIBER_ID, "", "", "", "","").build());
			usages.add(new SubscriberUsage.SubscriberUsageBuilder(SUBSCRIBER_ID, "", "", "", "","").build());
			return usages;
		}

	}

	private void runBatch() {

		ArgumentCaptor<Runnable> batchExecutorTask = ArgumentCaptor.forClass(Runnable.class);
		verify(batchExecutor).scheduleWithFixedDelay(batchExecutorTask.capture(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyObject());
		batchExecutorTask.getValue().run();
	}

	private void setUpUMBatch() throws OperationFailedException {

		umBatchOperation = new UMBatchOperation(transactionFactory, mock(AlertListener.class), null, recordProcessor, failOverOperation
				, csvFailOverOperation, 4, 1000, batchExecutor, batchOperationQueue);
		umBatchOperation.init();
		umBatchOperation.insert("1", createDummyUsage(), transactionFactory);
	}

	private Collection<SubscriberUsage> createDummyUsage() {

		List<SubscriberUsage> subscriberUsages = new ArrayList<SubscriberUsage>();
		subscriberUsages.add(new SubscriberUsage.SubscriberUsageBuilder("1", "1", "1", "1", "1","1").build());
		return subscriberUsages;
	}
}
