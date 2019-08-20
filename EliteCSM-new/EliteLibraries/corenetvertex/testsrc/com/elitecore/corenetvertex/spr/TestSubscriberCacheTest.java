package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.spr.data.TestSubscriberData;
import com.elitecore.corenetvertex.util.DerbyUtil;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(JUnitParamsRunner.class)
public class TestSubscriberCacheTest {

	private static final String DS_NAME = "test-DB";
	private DummyTransactionFactory transactionFactory;
	private TestHelper helper;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:derby:memory:TestingDB;create=true", "", "", 1, 5000, 3000);
		DummyTransactionFactoryBuilder builder = new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1);

		transactionFactory = (DummyTransactionFactory) builder.build();
		
		helper = new TestHelper(transactionFactory);
		helper.createTable();
	}
	
	@Test
	public void test_getTestSubscribers_should_return_list_of_test_subscriber_ids() throws Exception {
		
		helper.insert(getTestSubscriberIdList());
		
		TestSubscriberCache testSubscriberCache = new TestSubscriberCache(transactionFactory, mock(AlertListener.class));
		testSubscriberCache.refresh();
		Iterator<String> iterator = testSubscriberCache.getTestSubscriberIterator();
		
		while(iterator.hasNext()) {
			String element = iterator.next();
			getLogger().debug("TEST", element);
			if (helper.getTestSubscribers().contains(element) == false) {
				fail("should contain all element");
			}
		}
	}
	
	
	@Test
	public void test_add_should_insert_subscriberid_in_db_and_cache() throws Exception {
		TestSubscriberCache testSubscriberCache = new TestSubscriberCache(transactionFactory, mock(AlertListener.class));
		testSubscriberCache.init();
		String expectedSubscriberId = "1000";
		
		testSubscriberCache.add(expectedSubscriberId);

		assertTrue(testSubscriberCache.exists(expectedSubscriberId));
		
		assertTrue(testSubscriberCache.refreshAndExist(expectedSubscriberId));
	}
	
	@Test
	public void test_remove_should_remove_subscriber_from_cache_and_db() throws Exception {
		
		TestSubscriberCache testSubscriberCache = new TestSubscriberCache(transactionFactory, mock(AlertListener.class));
		testSubscriberCache.init();
		
		String subscriberId1 = "1000";
		
		testSubscriberCache.add(subscriberId1);
		
		testSubscriberCache.remove(subscriberId1);
		
		assertFalse(testSubscriberCache.exists(subscriberId1));
		
		assertFalse(testSubscriberCache.refreshAndExist(subscriberId1));
	}
	
	@Test
	public void test_remove_should_remove_all_subscriber_from_cache_and_db() throws Exception {
		
		TestSubscriberCache testSubscriberCache = new TestSubscriberCache(transactionFactory, mock(AlertListener.class));
		testSubscriberCache.init();
		List<String> testSubscriberIdList = Arrays.asList("101","102","103","104");
		
		for (String id : testSubscriberIdList) {
			testSubscriberCache.add(id);
		}
		
		assertEquals(testSubscriberIdList.size(), testSubscriberCache.remove(testSubscriberIdList));
		
		assertTrue(testSubscriberCache.getTestSubscriberIterator().hasNext() == false);
	}
	
	
	private List<TestSubscriberData> getTestSubscriberIdList() {
		TestSubscriberData data1 =  new TestSubscriberData("101");
		TestSubscriberData data2 =  new TestSubscriberData("102");
		TestSubscriberData data3 =  new TestSubscriberData("103");
		TestSubscriberData data4 =  new TestSubscriberData("104");
		
		return Arrays.asList(data1, data2, data3, data4);
	}
	
	@After
	public void AfterSetUp() throws Exception {
		helper.dropTable();
		DBUtility.closeQuietly(transactionFactory.getConnection());
		DerbyUtil.closeDerby("TestingDB");
	}
	
	private class TestHelper {

		private TransactionFactory transactionFactory;
		private List<String> testSubscriberList;

		public TestHelper(TransactionFactory transactionFactory) {
			this.transactionFactory = transactionFactory;
			this.testSubscriberList = new ArrayList<String>();
		}
		
		public void createTable() throws Exception {
			execute(TestSubscriberData.createTableQuery());
		}
		
		public void execute(String qry) throws Exception {
			
			Transaction transaction = transactionFactory.createTransaction();
			
			try {
				transaction.begin();
				getLogger().debug(getClass().getSimpleName(), "Query: " + qry);
				transaction.statement().execute(qry);
				
			} finally {
				transaction.end();
			}
		}
		
		public void insert(TestSubscriberData data) throws Exception {
			testSubscriberList.add(data.getSubscriberId());
			execute(data.insertQuery());
		}
		
		public void insert(List<TestSubscriberData> datas) throws Exception {
			for (TestSubscriberData data : datas) {
				insert(data);
			}
		}
		
		public void dropTable() throws Exception {
			execute(TestSubscriberData.dropTableQuery());
			testSubscriberList.clear();
		}
		
		public List<String> getTestSubscribers() {
			return testSubscriberList;
		}
	}
	
	
}
