package com.elitecore.netvertex.core.session.conf.impl;

import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.session.conf.SessionManagerConfiguration;

import java.util.List;

public class SessionManagerConfigurationImpl implements SessionManagerConfiguration {

    private boolean isBatchUpdateEnable;
    private long batchSize;
    private long batchUpdateInterval;
    private int batchQueryTimeout;
    private boolean isSaveInBatch;
    private boolean isUpdateInBatch;
    private boolean isDeleteInBatch;
    private List<FieldMapping> coreSessFieldMappings;
    private List<FieldMapping> sessionRuleFieldMappings;

    public SessionManagerConfigurationImpl(boolean isBatchUpdateEnable,
                                       long batchSize,
                                       long batchUpdateInterval,
                                       int batchQueryTimeout,
                                       boolean isSaveInBatch,
                                       boolean isUpdateInBatch,
                                       boolean isDeleteInBatch,
                                       List<FieldMapping> coreSessFieldMappings,
                                       List<FieldMapping> sessionRuleFieldMappings) {

        this.isBatchUpdateEnable = isBatchUpdateEnable;
        this.batchSize = batchSize;
        this.batchUpdateInterval = batchUpdateInterval;
        this.batchQueryTimeout = batchQueryTimeout;
        this.isSaveInBatch = isSaveInBatch;
        this.isUpdateInBatch = isUpdateInBatch;
        this.isDeleteInBatch = isDeleteInBatch;
        this.coreSessFieldMappings = coreSessFieldMappings;
        this.sessionRuleFieldMappings = sessionRuleFieldMappings;
    }

    @Override
    public List<FieldMapping> getCoreSessionFieldMappings(){
        return coreSessFieldMappings;
    }
    @Override
    public List<FieldMapping> getSessionRuleFieldMappings() {
        return sessionRuleFieldMappings;
    }
    @Override
    public long getBatchUpdateIntervalInSec() {
        return this.batchUpdateInterval;
    }
    @Override
    public boolean isBatchUpdateEnable() {
        return this.isBatchUpdateEnable;
    }
    @Override
    public long getBatchSize() {
        return this.batchSize;
    }
    @Override
    public int getBatchQueryTimeout() {
        return this.batchQueryTimeout;
    }
    @Override
    public boolean isSaveInBatch() {
        return isSaveInBatch;
    }
    @Override
    public boolean isUpdateInBatch() {
        return isUpdateInBatch;
    }
    @Override
    public boolean isDeleteInBatch() {
        return isDeleteInBatch;
    }

    @Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- Session Manager Configuration -- ");
        toString(builder);
        return builder.toString();
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.incrementIndentation();
        if (isBatchUpdateEnable) {
            if (isSaveInBatch) {
                builder.append("Is Batch Update Enabled", isBatchUpdateEnable);
            } else {
                builder.append("Is Batch Update Enabled", "Hybrid");
            }
            builder.append("Batch Size", batchSize);
            builder.append("Update Interval", batchUpdateInterval);
            builder.append("DB Query Timeout", batchQueryTimeout);
        } else {
            builder.append("Is Batch Update Enabled", isBatchUpdateEnable);
        }

        builder.newline();
        builder.appendHeading(" -- Core Session Field Mappings (Property Name, DB Field, Type) -- ");
        if (coreSessFieldMappings == null || coreSessFieldMappings.isEmpty()) {
            builder.appendValue("No core session field mapping found");
        } else {
            builder.newline();
            builder.incrementIndentation();
            for (FieldMapping fieldMapping : coreSessFieldMappings) {
                builder.append(fieldMapping.getPropertyName(), fieldMapping.getColumnName() + " (" + (fieldMapping.getType() == 0 ? "String)" : "Timestamp)"));
            }
            builder.decrementIndentation();
        }

        builder.newline();
        builder.appendHeading(" -- Sub Session Field Mappings (Property Name, DB Field, Type) -- ");
        if (sessionRuleFieldMappings == null || sessionRuleFieldMappings.isEmpty()) {
            builder.appendValue("No session rule field mapping found");
        } else {
            builder.newline();
            builder.incrementIndentation();

            for (FieldMapping fieldMapping : sessionRuleFieldMappings) {
                builder.append(fieldMapping.getPropertyName(), fieldMapping.getColumnName() + " (" + (fieldMapping.getType() == 0 ? "String)" : "Timestamp)"));
            }
            builder.decrementIndentation();
        }

        builder.decrementIndentation();
    }
}
