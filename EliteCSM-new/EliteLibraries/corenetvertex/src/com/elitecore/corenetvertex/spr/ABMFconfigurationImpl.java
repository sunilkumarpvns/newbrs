package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

public class ABMFconfigurationImpl implements ABMFconfiguration {
    private final int batchSize;
    private final int batchQueryTimeout;
    private final int queryTimeout;

    public ABMFconfigurationImpl(int batchSize, int batchQueryTimeout, int queryTimeout){
        this.batchSize = batchSize;
        this.batchQueryTimeout = batchQueryTimeout;
        this.queryTimeout = queryTimeout;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public int getBatchQueryTimeout() {
        return batchQueryTimeout;
    }

    @Override
    public int getQueryTimeout() {
        return queryTimeout;
    }


    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- ABMF Configuration -- ");
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.append("Batch Size", batchSize)
                .append("Batch Query Timeout", batchQueryTimeout)
                .append("Query Timeout", queryTimeout);
    }
}
