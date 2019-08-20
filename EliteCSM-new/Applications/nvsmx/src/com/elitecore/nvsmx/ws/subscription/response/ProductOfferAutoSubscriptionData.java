package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class ProductOfferAutoSubscriptionData {
    private String id;
    private LogicalExpression advancedCondition;
    private String advancedConditionStr;
    private String addOnProductOfferId;
    private String addOnProductOfferName;

    public ProductOfferAutoSubscriptionData() {
    }

    public ProductOfferAutoSubscriptionData(String id, LogicalExpression advancedCondition,
                                            String advancedConditionStr, String addOnProductOfferId,
                                            String addOnProductOfferName) {
        this.id = id;
        this.advancedCondition = advancedCondition;
        this.advancedConditionStr = advancedConditionStr;
        this.addOnProductOfferId = addOnProductOfferId;
        this.addOnProductOfferName = addOnProductOfferName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LogicalExpression getAdvancedCondition() {
        return advancedCondition;
    }

    public void setAdvancedCondition(LogicalExpression advancedCondition) {
        this.advancedCondition = advancedCondition;
    }

    public String getAdvancedConditionStr() {
        return advancedConditionStr;
    }

    public void setAdvancedConditionStr(String advancedConditionStr) {
        this.advancedConditionStr = advancedConditionStr;
    }

    public String getAddOnProductOfferId() {
        return addOnProductOfferId;
    }

    public void setAddOnProductOfferId(String addOnProductOfferId) {
        this.addOnProductOfferId = addOnProductOfferId;
    }

    public String getAddOnProductOfferName() {
        return addOnProductOfferName;
    }

    public void setAddOnProductOfferName(String addOnProductOfferName) {
        this.addOnProductOfferName = addOnProductOfferName;
    }
}
