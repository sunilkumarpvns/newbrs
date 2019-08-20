package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.spr.data.FnFGroup;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;

import java.sql.Timestamp;
import java.util.Objects;

/**
 *
 * @author Jay Trivedi
 *
 */
public class SubscriptionData {

	private static final Long BIG_DATE_IN_MILLIS = Timestamp.valueOf("2037-12-31 00:00:00").getTime();
	private String addonSubscriptionId;
	private String subscriberIdentity;
	private String addOnId;
	private String addOnName;
	private Long startTime;
	private Long endTime;
	private SubscriptionState addOnStatus;
	private String parameter1;
	private String parameter2;
	private String productOfferId;
	private String productOfferName;
	private Integer priority;
	private FnFGroup fnFGroup;

	public SubscriptionData(){}
	public SubscriptionData(String addonSubscriptionId, String subscriberIdentity, String addOnId, String addOnName,
							Long startTime, Long endTime, SubscriptionState addOnStatus, String parameter1, String parameter2,
							String productOfferId, String productOfferName, int priority, FnFGroup fnFGroup) {

		this.addonSubscriptionId = addonSubscriptionId;
		this.subscriberIdentity = subscriberIdentity;
		this.addOnId = addOnId;
		this.addOnName = addOnName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.addOnStatus = addOnStatus;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.productOfferId = productOfferId;
		this.productOfferName = productOfferName;
		this.priority = priority;
		this.fnFGroup = fnFGroup;
	}

	public String getAddonSubscriptionId() {
		return addonSubscriptionId;
	}
	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}
	public String getAddOnId() {
		return addOnId;
	}
	public String getAddOnName() {
		return addOnName;
	}
	public Long getStartTime() {
		return startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public SubscriptionState getAddOnStatus() {
		return addOnStatus;
	}
	public String getParameter1() {
		return parameter1;
	}
	public String getParameter2() {
		return parameter2;
	}
	public void setAddonSubscriptionId(String addonSubscriptionId) {
		this.addonSubscriptionId = addonSubscriptionId;
	}
	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}
	public void setAddOnId(String addOnId) {
		this.addOnId = addOnId;
	}
	public void setAddOnName(String addOnName) {
		this.addOnName = addOnName;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public void setAddOnStatus(SubscriptionState addOnStatus) {
		this.addOnStatus = addOnStatus;
	}
	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}
	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}

	public String getProductOfferId() {
		return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
		this.productOfferId = productOfferId;
	}

	public String getProductOfferName() {
		return productOfferName;
	}

	public void setProductOfferName(String productOfferName) {
		this.productOfferName = productOfferName;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public FnFGroup getFnFGroup() {
		return fnFGroup;
	}

	public void setFnFGroup(FnFGroup fnFGroup) {
		this.fnFGroup = fnFGroup;
	}

	public static SubscriptionData fromSubscription(Subscription subscription) {
		ProductOffer productOffer = null;
		String addOnProductOfferId = subscription.getProductOfferId();
		if (Strings.isNullOrBlank(addOnProductOfferId) == false) {
			productOffer = getNVSMXPolicyRepository().getProductOffer().byId(addOnProductOfferId);
		}

		String addonProductOfferName = Objects.nonNull(productOffer) ? productOffer.getName() : null;
		String packageName = getPackageNameById(subscription.getPackageId(),subscription.getType());
		FnFGroup fnFGroup = subscription.getMetadata()==null?null:subscription.getMetadata().getFnFGroup();

		return new SubscriptionData(subscription.getId(),
				subscription.getSubscriberIdentity(), subscription.getPackageId(), packageName
				, subscription.getStartTime() == null ? null : subscription.getStartTime().getTime()
				, subscription.getEndTime() == null ? BIG_DATE_IN_MILLIS : subscription.getEndTime().getTime()
				, subscription.getStatus() , subscription.getParameter1(), subscription.getParameter2()
				, addOnProductOfferId, addonProductOfferName, subscription.getPriority(), fnFGroup);
	}

	private static String getPackageNameById(String id,SubscriptionType subscriptionType){
		if(SubscriptionType.ADDON == subscriptionType) {
			SubscriptionPackage subscriptionPackage = getNVSMXPolicyRepository().getAddOnById(id);
			return  subscriptionPackage.getName();
		}else if(SubscriptionType.RO_ADDON == subscriptionType) {
			RnCPackage rncPackage = getNVSMXPolicyRepository().getRnCPackage().byId(id);
			return rncPackage.getName();
		}
		return null;
	}

	private static PolicyRepository getNVSMXPolicyRepository() {
		return DefaultNVSMXContext.getContext().getPolicyRepository();
	}
}