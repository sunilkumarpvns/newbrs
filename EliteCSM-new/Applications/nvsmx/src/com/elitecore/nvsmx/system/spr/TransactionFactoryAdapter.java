package com.elitecore.nvsmx.system.spr;

import com.elitecore.core.commons.utilx.db.TransactionFactoryGroupImpl;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.nvsmx.system.db.ReadOnlyTransactionAdapter;
import com.elitecore.nvsmx.system.db.TransactionAdapter;

import javax.annotation.Nullable;

public class TransactionFactoryAdapter implements com.elitecore.corenetvertex.core.transaction.TransactionFactory {

    private TransactionFactoryGroupImpl transactionFactoryGroup;

    public TransactionFactoryAdapter(TransactionFactoryGroupImpl transactionFactoryGroup) {
        this.transactionFactoryGroup = transactionFactoryGroup;
    }

    @Override
    public @Nullable
    Transaction createTransaction() {

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

        return new ReadOnlyTransactionAdapter(transactionFactory.createReadOnlyTransaction(), transactionFactory.getDataSource().getDataSourceName());
    }

    @Override
    public boolean isAlive() {
        return transactionFactoryGroup.isAlive();
    }
}
