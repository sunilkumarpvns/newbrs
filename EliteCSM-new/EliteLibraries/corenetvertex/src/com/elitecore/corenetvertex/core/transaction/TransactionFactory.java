package com.elitecore.corenetvertex.core.transaction;

import javax.annotation.Nullable;


public interface TransactionFactory {

    public @Nullable Transaction createTransaction();
    public @Nullable DBTransaction createReadOnlyTransaction();

    public boolean isAlive();
}
