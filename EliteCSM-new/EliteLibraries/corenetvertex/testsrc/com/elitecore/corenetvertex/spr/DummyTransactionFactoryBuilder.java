package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;

public class DummyTransactionFactoryBuilder {
    
    private DummyDBDataSource dataSource;
    private int wieghtage;
    private String dsName;

    public DummyTransactionFactoryBuilder() {

    }
    
    public DummyTransactionFactoryBuilder withDBDataSource(DummyDBDataSource dataSource, int wieghtage) {
	this.dataSource = dataSource;
	this.wieghtage = wieghtage;
	return this;
    }

    public DummyTransactionFactoryBuilder withDBDataSource(String dsName, int wieghtage) {
	this.dsName = dsName;
	return this;
    }

    public TransactionFactory build() throws Exception {
	return new DummyTransactionFactory(dataSource);
    }

}
