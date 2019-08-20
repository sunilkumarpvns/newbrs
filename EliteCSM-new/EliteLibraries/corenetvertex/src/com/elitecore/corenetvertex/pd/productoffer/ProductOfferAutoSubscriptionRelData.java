package com.elitecore.corenetvertex.pd.productoffer;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "TBLM_PRDCT_OFR_AUTO_SUBS_REL")
@Entity(name="com.elitecore.corenetvertex.pd.productoffer.ProductOfferAutoSubscriptionRelData")
public class ProductOfferAutoSubscriptionRelData implements Serializable,Comparable<ProductOfferAutoSubscriptionRelData> {

    private static final long serialVersionUID = -5594238485050373317L;
    private String id;
    private transient ProductOfferData parentProductOfferData;
    private String advanceCondition;
    private Integer orderNo;
    private ProductOfferData addOnProductOfferData;


    @Id
    @Column(name="ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_PRODUCT_OFFER_ID")
    @JsonIgnore
    public ProductOfferData getParentProductOfferData() {
        return parentProductOfferData;
    }

    public void setParentProductOfferData(ProductOfferData parentProductOfferData) {
        this.parentProductOfferData = parentProductOfferData;
    }


    @Column(name = "ADVANCED_CONDITION")
    public String getAdvanceCondition() {
        return advanceCondition;
    }

    public void setAdvanceCondition(String advanceCondition) {
        this.advanceCondition = advanceCondition;
    }

    @Transient
    public String getAddOnProductOfferId() {
        if(Objects.isNull(getAddOnProductOfferData())){
            return null;
        }
        return getAddOnProductOfferData().getId();
    }

    public void setAddOnProductOfferId(String addOnProductOfferId) {
        if(Strings.isNullOrBlank(addOnProductOfferId) == false){
            addOnProductOfferData = new ProductOfferData();
            addOnProductOfferData.setId(addOnProductOfferId);
        }
    }

    @ManyToOne
    @JoinColumn(name="PRODUCT_OFFER_ID")
    @JsonIgnore
    public ProductOfferData getAddOnProductOfferData() {
        return addOnProductOfferData;
    }

    public void setAddOnProductOfferData(ProductOfferData addOnProductOfferData) {
        this.addOnProductOfferData = addOnProductOfferData;
    }

    public ProductOfferAutoSubscriptionRelData copyModel() {
        ProductOfferAutoSubscriptionRelData newData = new ProductOfferAutoSubscriptionRelData();
        newData.addOnProductOfferData = this.addOnProductOfferData;
        newData.advanceCondition = this.advanceCondition;
        newData.orderNo = this.orderNo;
        return newData;
    }

    @Column(name = "ORDER_NO")
    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Add On Product Offer",addOnProductOfferData.getName());
        jsonObject.addProperty(FieldValueConstants.ADVANCE_CONDITION, advanceCondition);
        jsonObject.addProperty(FieldValueConstants.ORDER_NO, orderNo );
        return jsonObject;
    }

    @Override
    public int compareTo(ProductOfferAutoSubscriptionRelData other) {
        return this.orderNo.compareTo(other.getOrderNo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof ProductOfferAutoSubscriptionRelData)) {
            return false;
        }

        ProductOfferAutoSubscriptionRelData that = (ProductOfferAutoSubscriptionRelData) o;

        return getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
