package com.elitecore.corenetvertex.spr.params;

public class ChangeBaseProductOfferParams {
    private String subscriberId;
    private String alternateId;
    private String currentBasePackageId;
    private String currentProductOfferId;
    private String newProductOfferName;
    private String param1;
    private String param2;
    private String param3;

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public String getCurrentBasePackageId() {
        return currentBasePackageId;
    }

    public void setCurrentBasePackageId(String currentBasePackageId) {
        this.currentBasePackageId = currentBasePackageId;
    }

    public String getCurrentProductOfferId() {
        return currentProductOfferId;
    }

    public void setCurrentProductOfferId(String currentProductOfferId) {
        this.currentProductOfferId = currentProductOfferId;
    }

    public String getNewProductOfferName() {
        return newProductOfferName;
    }

    public void setNewProductOfferName(String newProductOfferName) {
        this.newProductOfferName = newProductOfferName;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public static class Builder {
        private ChangeBaseProductOfferParams params;

        public Builder() {
            this.params = new ChangeBaseProductOfferParams();
        }

        public Builder withSubscriberId(String subscriberId) {
            params.subscriberId = subscriberId;
            return this;
        }

        public ChangeBaseProductOfferParams build() {
            return params;
        }

        public Builder withCurrentPackageId(String currentBasePackageId) {
            params.currentBasePackageId = currentBasePackageId;
            return this;
        }

        public Builder withNewProductOfferName(String newProductOfferName) {
            params.newProductOfferName = newProductOfferName;
            return this;
        }

        public Builder withAlternateId(String alternateId) {
            params.alternateId = alternateId;
            return this;
        }

        public Builder withCurrentProductOfferId(String currentProductOfferId) {
            params.currentProductOfferId = currentProductOfferId;
            return this;
        }

        public Builder withParam1(String param1) {
            params.param1 = param1;
            return this;
        }

        public Builder withParam2(String param2) {
            params.param2 = param2;
            return this;
        }

        public Builder withParam3(String param3) {
            params.param3 = param3;
            return this;
        }
    }
}
