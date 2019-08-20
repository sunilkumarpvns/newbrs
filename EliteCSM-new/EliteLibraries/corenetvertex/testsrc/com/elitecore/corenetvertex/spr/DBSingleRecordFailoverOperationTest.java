package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class DBSingleRecordFailoverOperationTest {

	private static final String BATCH_ITEM = "BATCH_ITEM";
	private DBSingleRecordFailoverOperation<String> dbSingleRecordFailoverOperation;
	private Executor executor;
	private TransactionFactory transactionFactory = mock(TransactionFactory.class);
	private SingleRecordOperation<String> singleRecordOperation = mock(SingleRecordOperation.class);
	private FailOverOperation<String> secondaryFailoverOperation = mock(FailOverOperation.class);
	
	@Before
	public void before() {

        executor = command -> command.run();
        dbSingleRecordFailoverOperation = new DBSingleRecordFailoverOperation<>(singleRecordOperation, secondaryFailoverOperation, transactionFactory, executor);
	}

	public class DoFailOverWithCollection {
	
		@Test
        public void processEachBatchRecord() throws OperationFailedException {
			
			Collection<String> records = new ArrayList<String>();
			records.add(BATCH_ITEM + 1);
			records.add(BATCH_ITEM + 2);
			records.add(BATCH_ITEM + 3);
			
			dbSingleRecordFailoverOperation.doFailover(records);

			verify(singleRecordOperation, times(1)).process(BATCH_ITEM + 1, transactionFactory);
			verify(singleRecordOperation, times(1)).process(BATCH_ITEM + 2, transactionFactory);
			verify(singleRecordOperation, times(1)).process(BATCH_ITEM + 3, transactionFactory);
		}
		
		@Test
        public void callsSecondaryFailOver() throws OperationFailedException {
			
			Collection<String> records = new ArrayList<String>();
			records.add(BATCH_ITEM + 1);
			records.add(BATCH_ITEM + 2);
			records.add(BATCH_ITEM + 3);
			
			doThrow(OperationFailedException.class).when(singleRecordOperation).process(Mockito.anyString(), Mockito.anyObject());
			
			dbSingleRecordFailoverOperation.doFailover(records);
			
			verify(secondaryFailoverOperation, times(1)).doFailover(BATCH_ITEM + 1);
			verify(secondaryFailoverOperation, times(1)).doFailover(BATCH_ITEM + 2);
			verify(secondaryFailoverOperation, times(1)).doFailover(BATCH_ITEM + 3);
		}
		
	}
	
	
	public class DoFailOverWithSingleRecord {
		
		@Test
		public void processProvidedRecord() throws OperationFailedException, InterruptedException {
			
			dbSingleRecordFailoverOperation.doFailover(BATCH_ITEM + 1);

            verify(singleRecordOperation, times(1)).process(BATCH_ITEM + 1, transactionFactory);
		}
		
		@Test
		public void callsSecondaryFailOver() throws InterruptedException, OperationFailedException {
			
			doThrow(OperationFailedException.class).when(singleRecordOperation).process(Mockito.anyString(), Mockito.anyObject());
			
			dbSingleRecordFailoverOperation.doFailover(BATCH_ITEM + 1);

            verify(secondaryFailoverOperation, times(1)).doFailover(BATCH_ITEM + 1);
		}
	}
	

}
