package com.elitecore.corenetvertex.spr;

import java.util.List;

public interface MonetaryABMFOperation {
    class MonetaryOperationData {
        private List<MonetaryBalance> monetaryBalances;
        private ABMFOperationType operation;
        private String subscriberIdentiry;


        public MonetaryOperationData(List<MonetaryBalance> monetaryBalances, String subscriberIdentity, ABMFOperationType operation) {
            this.monetaryBalances = monetaryBalances;
            this.subscriberIdentiry = subscriberIdentity;
            this.operation = operation;
        }

        public List<MonetaryBalance> getMonetaryBalances() {
            return monetaryBalances;
        }

        public String getSubscriberIdentity() {
            return subscriberIdentiry;
        }

        public ABMFOperationType getOperation() {
            return operation;
        }
    }

}
