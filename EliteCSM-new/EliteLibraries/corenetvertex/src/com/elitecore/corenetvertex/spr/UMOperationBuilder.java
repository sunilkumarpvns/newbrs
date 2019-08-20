package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pm.PolicyRepository;

public class UMOperationBuilder {

    private AlertListener alertListener;
    private PolicyRepository policyRepository;
    private String sprName;
    private UMconfiguration umConf;
    private TransactionFactory transactionFactory;
    private RecordProcessor<UMBatchOperation.BatchOperationData> recordProcessor
            ;

    public UMOperationBuilder(AlertListener alertListener, PolicyRepository policyRepository, String sprName) {
        this.alertListener = alertListener;
        this.policyRepository = policyRepository;
        this.sprName = sprName;
    }

    public UMOperationBuilder withUMConf(UMconfiguration umConf) {
        this.umConf = umConf;
        return this;
    }

    public UMOperationBuilder withTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
        return this;
    }


    public UMOperation build() {
        UMOperation umOperation;
        if (umConf.getBatchSize() > 1) {
            umOperation = new UMBatchOperation(transactionFactory, alertListener, policyRepository, recordProcessor, umConf.getBatchSize(), umConf.getBatchQueryTimeout(),
                    sprName);
        } else {
            umOperation = new UMOperation(alertListener, policyRepository);
        }

        umOperation.init();
        return umOperation;
    }

    public UMOperationBuilder withRecordProcessor(RecordProcessor<UMBatchOperation.BatchOperationData> recordProcessor) {
        this.recordProcessor = recordProcessor;
        return this;
    }
}
