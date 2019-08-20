package com.elitecore.commons.threads;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class SettableFutureTest {
	
	private SettableFuture<Object> result;
	
	@Before
	public void init(){
		result = SettableFuture.create();
	}
	
	public Object[][] dataProvider_For_Test_Set_And_Get_Object_From_SetTableFuture(){
		
	TestClass testObj = new TestClass(12345, (long)122432.23, "string of Test Class");
	return new Object[][]{
			{
				"This is Test."
			},
			{
				12345
			},
			{
				testObj
			},
	};
		
	}
	
	/**
	 * SetTableFuture will store different type of objects with set() method.
	 * With method get() this test method checks whether the Object which was set, only that object is retrived 
	 * or not.*/
	@Test @Parameters(method="dataProvider_For_Test_Set_And_Get_Object_From_SetTableFuture")
	public void test_Set_And_Get_Object_From_SetTableFuture(Object expectedObject) throws InterruptedException, TimeoutException, ExecutionException{
		
		result.set(expectedObject);
		Object actualObject = result.get();
		Assert.assertSame(expectedObject, actualObject);
	}
	public @Rule ExpectedException expectedException = ExpectedException.none();
	
	 /**
	  * 
	  *  <PRE>
	  * No value us set in SettableFuture, trying to get with 1 second time.
	  * it waits for 1 second and then throw TimoutException 
	  * 
	  * </PRE>
	 **/

	@Test
	public void test_Get_Method_When_No_Value_Set() throws InterruptedException, ExecutionException, TimeoutException {
		
		expectedException.expect(TimeoutException.class);
		
		result.get(1, TimeUnit.MILLISECONDS);
	}	
	
	 /**
	 * <PRE>
	 * 	settable task is cancelled before computation (get() call),
	 * 	get call should throw CancellationException
	 * </PRE>
	 **/
	
	@Test
	public void test_Get_Method_Should_Throw_CancellationException_When_Task_Cancelled() throws InterruptedException, TimeoutException, ExecutionException {

		expectedException.expect(CancellationException.class);
		
		result.cancel(false);
		result.get();
		
	}
	private class TestClass{
		
		int integerObj;
		long longObj;
		String stringObj;
		
		public TestClass(int integerObj, long longObj, String stringObj) {
			this.integerObj = integerObj;
			this.longObj = longObj;
			this.stringObj = stringObj;
		}

		@Override
		public String toString() {
			return "TestClass [integerObj=" + integerObj + ", longObj="
					+ longObj + ", stringObj=" + stringObj + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + integerObj;
			result = prime * result + (int) (longObj ^ (longObj >>> 32));
			result = prime * result
					+ ((stringObj == null) ? 0 : stringObj.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestClass other = (TestClass) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (integerObj != other.integerObj)
				return false;
			if (longObj != other.longObj)
				return false;
			if (stringObj == null) {
				if (other.stringObj != null)
					return false;
			} else if (!stringObj.equals(other.stringObj))
				return false;
			return true;
		}

		private SettableFutureTest getOuterType() {
			return SettableFutureTest.this;
		}
		
	}
	
}
