package com.elitecore.corenetvertex.pm.offer;

import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

import java.io.Serializable;
import java.util.Objects;

public class ProductOfferAutoSubscription implements Serializable {
    private static final long serialVersionUID = -5594238485050373317L;
    private String id;
    private LogicalExpression advancedCondition;
    private String advancedConditionStr;
    private String addOnProductOfferId;
    private transient PolicyRepository policyRepository;
    private String addOnProductOfferName;


    public ProductOfferAutoSubscription(String id,
                                        LogicalExpression advancedCondition,
                                        String advancedConditionStr,
                                        String addOnProductOfferId,
                                        PolicyRepository policyRepository,
                                        String addOnProductOfferName) {
        this.id = id;
        this.advancedCondition = advancedCondition;
        this.advancedConditionStr = advancedConditionStr;
        this.addOnProductOfferId = addOnProductOfferId;
        this.policyRepository = policyRepository;
        this.addOnProductOfferName = addOnProductOfferName;
    }

    public String getId() {
        return id;
    }

    public LogicalExpression getAdvancedCondition() {
        return advancedCondition;
    }

    public ProductOffer getAddOnProductOfferData() {
        return policyRepository.getProductOffer().byId(addOnProductOfferId);
    }

    public String getAddOnProductOfferId() {
        if(getAddOnProductOfferData() == null){
            return null;
        }
        return getAddOnProductOfferData().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductOfferServicePkgRel)){
            return false;
        }
        ProductOfferAutoSubscription that = (ProductOfferAutoSubscription) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getAddOnProductOfferId(), that.getAddOnProductOfferId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAddOnProductOfferId());
    }


    public String getAddOnProductOfferName() {
        return addOnProductOfferName;
    }

    public String getAdvanceConditonStr() {
        return advancedConditionStr;
    }
}
