package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.util.FakeExecutor;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class CSVFailOverOperationTest {

	private static final String BATCH_ITEM = "BATCH_ITEM";
	private CSVFailOverOperation<String> csvFailoverOperation;
	private FakeExecutor executor;
	@SuppressWarnings("unchecked")
	private RecordProcessor<String> recordProcessor = mock(RecordProcessor.class);
	
	@Before
	public void before() {
		executor = new FakeExecutor();
		csvFailoverOperation = new CSVFailOverOperation<String>(recordProcessor, executor);
	}

	public class DoFailOverWithCollection {
	
		@Test
		public void processEachBatchRecord() throws InterruptedException, ProcessFailException {
			
			Collection<String> records = new ArrayList<String>();
			records.add(BATCH_ITEM + 1);
			records.add(BATCH_ITEM + 2);
			records.add(BATCH_ITEM + 3);
			
			csvFailoverOperation.doFailover(records);
			executor.tick();

			verify(recordProcessor, times(1)).process(BATCH_ITEM + 1);
			verify(recordProcessor, times(1)).process(BATCH_ITEM + 2);
			verify(recordProcessor, times(1)).process(BATCH_ITEM + 3);
		}
		
	}
	
	
	public class DoFailOverWithSingleRecord {
		
		@Test
		public void processProvidedRecord() throws InterruptedException, ProcessFailException {
			
			csvFailoverOperation.doFailover(BATCH_ITEM + 1);

			verify(recordProcessor, times(1)).process(BATCH_ITEM + 1);
		}
		
	}
	

}
