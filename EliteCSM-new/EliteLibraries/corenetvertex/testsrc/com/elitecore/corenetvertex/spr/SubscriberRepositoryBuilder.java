package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pm.PolicyRepository;

/**
 * 
 * @author Chetan.Sankhala
 */
public class SubscriberRepositoryBuilder {

    public static final String MODULE="SR-BLDR";
    private String id;
    private String name;
    private TransactionFactory transactionFactory;
    private AlertListener alertListener;
    private UMconfiguration uMconfiguration;
    private ABMFconfiguration abmfConfiguration;
    private PolicyRepository policyRepository;
    private TestSubscriberCache testSubscriberCache;
    private RecordProcessor<UMBatchOperation.BatchOperationData> umRecordProcessor;
    private RecordProcessor<ABMFBatchOperation.BatchOperationData> abmfRecordProcessor;
    private RecordProcessor<MonetaryABMFOperationImpl.MonetaryOperationData> monetaryAbmfRecordProcessor;
    private RecordProcessor<RnCABMFBatchOperation.BatchOperationData> rncAbmfRecordProcessor;
    private EDRListener balanceEDRListener;
    private EDRListener subscriberEDRListener;
    private EDRListener subscriptionEDRListener;
    private String systemCurrency;

    public SubscriberRepositoryBuilder(TransactionFactory transactionFactory, AlertListener alertListener, PolicyRepository policyRepository) {
        this.transactionFactory = transactionFactory;
        this.alertListener = alertListener;
        this.policyRepository = policyRepository;
    }

    public SubscriberRepositoryBuilder withUMRecordProcessor(RecordProcessor<UMBatchOperation.BatchOperationData> umRecordProcessor) {
        this.umRecordProcessor = umRecordProcessor;
        return this;
    }


    public SubscriberRepositoryBuilder withABMFRecordProcessor(RecordProcessor<ABMFBatchOperation.BatchOperationData> abmfRecordProcessor) {
        this.abmfRecordProcessor = abmfRecordProcessor;
        return this;
    }

    public SubscriberRepositoryBuilder withRnCABMFRecordProcessor(RecordProcessor<RnCABMFBatchOperation.BatchOperationData> rncAbmfRecordProcessor) {
        this.rncAbmfRecordProcessor = rncAbmfRecordProcessor;
        return this;
    }

    public SubscriberRepositoryBuilder withMonetaryABMFRecordProcessor(RecordProcessor<MonetaryABMFOperationImpl.MonetaryOperationData> monetaryAbmfRecordProcessor) {
        this.monetaryAbmfRecordProcessor = monetaryAbmfRecordProcessor;
        return this;
    }

    public SubscriberRepositoryBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public SubscriberRepositoryBuilder withTestSubscriberCache(TestSubscriberCache testSubscriberCache) {
        this.testSubscriberCache = testSubscriberCache;
        return this;
    }

    public SubscriberRepositoryBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SubscriberRepositoryBuilder withUMConfiguration(UMconfiguration uMconfiguration) {
        this.uMconfiguration = uMconfiguration;
        return this;
    }

    public SubscriberRepositoryBuilder withABMFConfiguration(ABMFconfiguration abmfConfiguration) {
        this.abmfConfiguration = abmfConfiguration;
        return this;
    }

    public SubscriberRepositoryBuilder withBalanceEDRListener(EDRListener balanceEDRListener) {
        this.balanceEDRListener = balanceEDRListener;
        return this;
    }
    public SubscriberRepositoryBuilder withSubscriberEDRListener(EDRListener subscriberEDRListener) {
        this.subscriberEDRListener = subscriberEDRListener;
        return this;
    }
    public SubscriberRepositoryBuilder withSubscriptionEDRListener(EDRListener subscriptionEDRListener) {
        this.subscriptionEDRListener = subscriptionEDRListener;
        return this;
    }

    public SubscriberRepositoryBuilder withSystemCurrency(String systemCurrency) {
        this.systemCurrency = systemCurrency;
        return this;
    }

    //TODO build local SR or build external SR
    public SubscriberRepositoryImpl build() {

        if (uMconfiguration == null) {
            //this will make batch enable for usage metering
            uMconfiguration = new UMconfigurationImpl(CommonConstants.BATCH_SIZE_MIN, CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT);
        }

        if (abmfConfiguration == null) {
            abmfConfiguration = new ABMFconfigurationImpl(CommonConstants.BATCH_SIZE_MIN, CommonConstants.DEFAULT_BATCH_QUERY_TIMEOUT, CommonConstants.QUERY_TIMEOUT_DEFAULT);
        }

        UMOperation umOperation = new UMOperationBuilder(alertListener, policyRepository, null)
                .withUMConf(uMconfiguration)
                .withRecordProcessor(umRecordProcessor)
                .build();

        return new SubscriberRepositoryImpl(id, name, transactionFactory, alertListener, policyRepository, umOperation, abmfConfiguration,
                testSubscriberCache, Collectionz.<String>newArrayList(), null, abmfRecordProcessor, monetaryAbmfRecordProcessor, rncAbmfRecordProcessor,
                balanceEDRListener, subscriberEDRListener, subscriptionEDRListener, systemCurrency);
    }
}
