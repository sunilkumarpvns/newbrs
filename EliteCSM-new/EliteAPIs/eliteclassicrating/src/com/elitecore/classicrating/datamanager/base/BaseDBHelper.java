/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on Oct 1, 2008
 *	@author baiju
 *  Last Modified Oct 1, 2008
 */

/*
 * BaseDBHelper.java
 * This class provides base class for DB Helper classes.
 * 
 */

package com.elitecore.classicrating.datamanager.base;

import com.elitecore.classicrating.datasource.ITransactionContext;

public class BaseDBHelper {

	private ITransactionContext transactionContext;

	public BaseDBHelper(ITransactionContext transactionContext) {
		this.transactionContext = transactionContext;
	}

	protected final ITransactionContext getTransactionContext() {
		return this.transactionContext;
	}

}
