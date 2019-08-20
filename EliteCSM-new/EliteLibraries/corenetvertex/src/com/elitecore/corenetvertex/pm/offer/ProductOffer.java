package com.elitecore.corenetvertex.pm.offer;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ProductOffer implements Serializable, ToStringable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String description;
    private PkgType type;
    private PkgMode packageMode;
    private Integer validityPeriod;
    private ValidityPeriodUnit validityPeriodUnit;
    private double subscriptionPrice;
    private double creditBalance;
    private Timestamp availabilityStartDate;
    private Timestamp availabilityEndDate;
    private boolean isFnFOffer;
    private String param1;
    private String param2;
    private PkgStatus status;
    private List<ProductOfferServicePkgRel> offerServicePkgRel;
    private List<ProductOfferAutoSubscription> productOfferAutoSubscriptions;
    private String dataServicePkgId;
    private List<String> groupIds;
    private PolicyStatus policyStatus;
    private FailReason failReason;
    private FailReason partialFailReason;
    private transient PolicyRepository policyRepository;
    private Template subscribeEmailNotification;
    private Template subscribeSmsNotification;
    private Map<String, Set<String>> packageIdToServiceIds;
    private String currency;


    public ProductOffer(String id, String name, String description, PkgType type, PkgMode packageMode,
                        Integer validityPeriod, ValidityPeriodUnit validityPeriodUnit, double subscriptionPrice, double creditBalance,
                        PkgStatus status, List<ProductOfferServicePkgRel> offerServicePkgRel, List<ProductOfferAutoSubscription> productOfferAutoSubscriptions,
                        String dataServicePkgId, List<String> groupIds, Timestamp availabilityStartDate,
                        Timestamp availabilityEndDate, PolicyStatus policyStatus, FailReason failReason, FailReason partialFailReason, boolean isFnFOffer, String param1, String param2, PolicyRepository policyRepository,Template subscribeEmailNotification,Template subscribeSmsNotification,Map<String, Set<String>> packageIdToServiceIds,String currency) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.packageMode = packageMode;
        this.validityPeriod = validityPeriod;
        this.validityPeriodUnit = validityPeriodUnit;
        this.subscriptionPrice = subscriptionPrice;
        this.creditBalance = creditBalance;
        this.status = status;
        this.offerServicePkgRel = offerServicePkgRel;
        this.productOfferAutoSubscriptions = productOfferAutoSubscriptions;
        this.dataServicePkgId = dataServicePkgId;
        this.groupIds = groupIds;
        this.availabilityStartDate = availabilityStartDate;
        this.availabilityEndDate = availabilityEndDate;
        this.failReason = failReason;
        this.partialFailReason = partialFailReason;
        this.policyStatus = policyStatus;
        this.isFnFOffer = isFnFOffer;
        this.param1 = param1;
        this.param2 = param2;
        this.policyRepository = policyRepository;
        this.subscribeEmailNotification = subscribeEmailNotification;
        this.subscribeSmsNotification = subscribeSmsNotification;
        this.packageIdToServiceIds = packageIdToServiceIds;
        this.currency=currency;
    }

    public String getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PkgType getType() {
        return type;
    }

    public PkgMode getPackageMode() {
        return packageMode;
    }

    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public ValidityPeriodUnit getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public double getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public double getCreditBalance() {
        return creditBalance;
    }

    public Timestamp getAvailabilityStartDate() {
        return availabilityStartDate;
    }

    public Timestamp getAvailabilityEndDate() {
        return availabilityEndDate;
    }

    public boolean isFnFOffer() { return isFnFOffer; }

    public String getParam1() {
        return param1;
    }

    public String getParam2() {
        return param2;
    }

    public List<String> getGroups() {
        return groupIds;
    }

    public PkgStatus getStatus() {
        return status;
    }

    public String getResourceName() {
        return getName();
    }

    public List<ProductOfferServicePkgRel> getProductOfferServicePkgRelDataList() {
        return offerServicePkgRel;
    }

    public List<ProductOfferAutoSubscription> getProductOfferAutoSubscriptions() {
        return productOfferAutoSubscriptions;
    }

    public FailReason getFailReason(){
        return failReason;
    }

    public FailReason getPartialFailReason(){
        return partialFailReason;
    }

    public PolicyStatus getPolicyStatus(){
        return policyStatus;
    }

    public UserPackage getDataServicePkgData() {
        if(Strings.isNullOrBlank(dataServicePkgId)){
            return null;
        }

        return policyRepository.getPkgDataById(dataServicePkgId);
    }

    public String getDataServicePkgId(){

        if (getDataServicePkgData() == null) {
            return null;
        }
        return getDataServicePkgData().getId();
    }

    public String getCurrency() {
        return currency;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }

    public void setFailReason(FailReason failReason) {
        this.failReason = failReason;
    }

    public void setPartialFailReason(FailReason partialFailReason) {
        this.partialFailReason = partialFailReason;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!ProductOffer.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final ProductOffer other = (ProductOffer) obj;
        return (this.id == null) ? (other.id == null) : this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.append("Name", getName())
                .append("Type",getType())
                .append("Offer Mode", getPackageMode());

        builder.append("Price", getSubscriptionPrice());
        builder.append("Credit Balance", getCreditBalance());
        builder.append("Currency", getCurrency());
        builder.append("Availability Start Date", getAvailabilityStartDate()==null?CommonConstants.NOT_APPLICABLE:getAvailabilityStartDate());
        builder.append("Availability End Date", getAvailabilityEndDate()==null?CommonConstants.NOT_APPLICABLE:getAvailabilityEndDate());
        builder.append("Availability Status", getStatus());

        if(PkgType.BASE.name().equals(type.name()) == false){
            builder.append("Validity Period", getValidityPeriod());
            builder.append("Validity Period Unit", getValidityPeriodUnit().displayValue);
            builder.append("Subscribe Email Notification", subscribeEmailNotification != null ? subscribeEmailNotification.getName() : null);
            builder.append("Subscribe SMS Notification", subscribeSmsNotification != null ? subscribeSmsNotification.getName() : null);
        }

        builder.append("Data Package", getDataServicePkgData()==null?CommonConstants.NOT_APPLICABLE:getDataServicePkgData().getName());
        builder.append("Is FnF Offer", isFnFOffer());
        builder.append("Param 1", getParam1());
        builder.append("Param 2", getParam2());

        if(this.getProductOfferServicePkgRelDataList().isEmpty()==false){
            builder.newline();
            builder.appendHeading("RnC Package");
            builder.incrementIndentation();
            for(ProductOfferServicePkgRel productOfferServicePkgRel : this.getProductOfferServicePkgRelDataList()){
                builder.append("Service", productOfferServicePkgRel.getServiceData()!=null?productOfferServicePkgRel.getServiceData().getName():null);
                builder.append("RnC Package", productOfferServicePkgRel.getRncPackageData()!=null?productOfferServicePkgRel.getRncPackageData().getName():null);
                builder.newline();
            }
            builder.decrementIndentation();
        }


        if(CollectionUtils.isNotEmpty(this.productOfferAutoSubscriptions)) {
            builder.newline();
            builder.appendHeading("Auto Subscribe Packages");
            builder.incrementIndentation();
            for (ProductOfferAutoSubscription productOfferAutoSubscription : this.getProductOfferAutoSubscriptions()) {
                builder.append("Advance Condition", productOfferAutoSubscription.getAdvancedCondition() != null ? productOfferAutoSubscription.getAdvancedCondition() : null);
                builder.append("AddOn Product Offer", productOfferAutoSubscription.getAddOnProductOfferData() != null ? productOfferAutoSubscription.getAddOnProductOfferData().getName() : null);
                builder.newline();
            }

            builder.decrementIndentation();
        }

        builder.append("Offer Status", getPolicyStatus());
        if (Objects.nonNull(getFailReason()) && getFailReason().isEmpty() == false) {
            builder.append("Fail Reasons", getFailReason().toString());
        }
        if (Objects.nonNull(getPartialFailReason()) && getPartialFailReason().isEmpty() == false) {
            builder.append("Partial Fail Reasons", getPartialFailReason().toString());
        }
    }

    public boolean isRnCPackageAttachedWithService(String rncPackageId, String serviceId){
        Set services = packageIdToServiceIds.get(rncPackageId);
        return services==null?false:services.contains(serviceId);
    }

    public Template getSubscribeEmailNotification() {
        return subscribeEmailNotification;
    }

    public Template getSubscribeSmsNotification() {
        return subscribeSmsNotification;
    }
}
