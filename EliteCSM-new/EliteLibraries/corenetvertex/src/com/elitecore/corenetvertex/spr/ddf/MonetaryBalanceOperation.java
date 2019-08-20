package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import java.util.List;

public class MonetaryBalanceOperation {

    private RepositorySelector repositorySelector;

    public MonetaryBalanceOperation(RepositorySelector repositorySelector) {
        this.repositorySelector = repositorySelector;
    }

    public void directDebitBalance(List<MonetaryBalance> directDebits) throws OperationFailedException {
        String subscriberId = directDebits.get(0).getSubscriberId();
        SubscriberRepository repository = repositorySelector.select(subscriberId);
        repository.directDebitMonetaryBalance(subscriberId, directDebits);
    }

    public void reserve(List<MonetaryBalance> reserves) throws OperationFailedException {
        String subscriberId = reserves.get(0).getSubscriberId();
        SubscriberRepository repository = repositorySelector.select(subscriberId);
        repository.reserveMonetaryBalance(subscriberId, reserves);
    }

    public void reserveAndReport(List<MonetaryBalance> reserveAndReports) throws OperationFailedException {
        String subscriberId = reserveAndReports.get(0).getSubscriberId();
        SubscriberRepository repository = repositorySelector.select(subscriberId);
        repository.reportAndReserveMonetaryBalance(subscriberId, reserveAndReports);
    }
}
