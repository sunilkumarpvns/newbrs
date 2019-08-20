package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.EnumMap;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ExternalDBSPInterfaceTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void test_updateProfile_should_throw_OperationFailedException_with_resultCode_OPERATION__NOT_SUPPORTED() throws OperationFailedException {
		ExternalDBSPInterface spInterface = getSPInterface();
		
		expectedException.expect(OperationFailedException.class);
		
		try {
			spInterface.updateProfile("111", new EnumMap<SPRFields, String>(SPRFields.class));
		} catch (OperationFailedException e) {
			assertSame(ResultCode.OPERATION_NOT_SUPPORTED, e.getErrorCode());
			throw e;
		}
		
		fail("Should throw OperationFailedException");
	}

	private ExternalDBSPInterface getSPInterface() {
		return new ExternalDBSPInterface(mock(DBSPInterfaceConfiguration.class), mock(AlertListener.class)
								, mock(TransactionFactory.class));
	}

	@Test
	public void test_purgeProfile_should_throw_OperationFailedException_with_resultCode_OPERATION__NOT_SUPPORTED() throws OperationFailedException {
		ExternalDBSPInterface spInterface = getSPInterface();

		expectedException.expect(OperationFailedException.class);

		try {
			spInterface.purgeProfile("101");
		} catch (OperationFailedException e) {
			assertSame(ResultCode.OPERATION_NOT_SUPPORTED, e.getErrorCode());
			throw e;
		}

		fail("Should throw OperationFailedException");
	}

	@Test
	public void test_purgeProfile_with_transaction_should_throw_OperationFailedException_with_resultCode_OPERATION__NOT_SUPPORTED() throws OperationFailedException, TransactionException {
		ExternalDBSPInterface spInterface = getSPInterface();

		expectedException.expect(OperationFailedException.class);

		try {
			spInterface.purgeProfile("101", mock(Transaction.class));
		} catch (OperationFailedException e) {
			assertSame(ResultCode.OPERATION_NOT_SUPPORTED, e.getErrorCode());
			throw e;
		}

		fail("Should throw OperationFailedException");
	}

	@Test
	public void test_markForDeleteProfile_should_throw_OperationFailedException_with_resultCode_OPERATION__NOT_SUPPORTED() throws OperationFailedException {
		ExternalDBSPInterface spInterface = getSPInterface();

		expectedException.expect(OperationFailedException.class);

		try {
			spInterface.markForDeleteProfile("101");
		} catch (OperationFailedException e) {
			assertSame(ResultCode.OPERATION_NOT_SUPPORTED, e.getErrorCode());
			throw e;
		}

		fail("Should throw OperationFailedException");
	}

	@Test
	public void test_getDeleteMarkedProfiles_should_throw_OperationFailedException_with_resultCode_OPERATION__NOT_SUPPORTED() throws OperationFailedException {
		ExternalDBSPInterface spInterface = getSPInterface();

		expectedException.expect(OperationFailedException.class);

		try {
			spInterface.getDeleteMarkedProfiles();
		} catch (OperationFailedException e) {
			assertSame(ResultCode.OPERATION_NOT_SUPPORTED, e.getErrorCode());
			throw e;
		}

		fail("Should throw OperationFailedException");
	}

	@Test
	public void test_restoreProfile_should_throw_OperationFailedException_with_resultCode_OPERATION__NOT_SUPPORTED() throws OperationFailedException {
		ExternalDBSPInterface spInterface = getSPInterface();

		expectedException.expect(OperationFailedException.class);

		try {
			spInterface.restoreProfile("101");
		} catch (OperationFailedException e) {
			assertSame(ResultCode.OPERATION_NOT_SUPPORTED, e.getErrorCode());
			throw e;
		}

		fail("Should throw OperationFailedException");
	}

	@Test
	public void test_restoreProfile_multiple_should_throw_OperationFailedException_with_resultCode_OPERATION__NOT_SUPPORTED() throws OperationFailedException {
		ExternalDBSPInterface spInterface = getSPInterface();

		expectedException.expect(OperationFailedException.class);

		try {
			spInterface.restoreProfile(Arrays.asList("101"));
		} catch (OperationFailedException e) {
			assertSame(ResultCode.OPERATION_NOT_SUPPORTED, e.getErrorCode());
			throw e;
		}

		fail("Should throw OperationFailedException");
	}

}
