package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
/**
 * 
 * @author Jay Trivedi
 *
 * <br><br>
 * <i> This class provides an interface which contracts to execute/update a single row with help of given transaction factory </i>
 * 
 * @param <T> Here type T will be the data to be passed for processing single row commit. e.g. BatchOperationData
 * 
 */

public interface SingleRecordOperation<T> {

	void process(T dataToProcess, TransactionFactory transactionFactory) throws OperationFailedException;
}
