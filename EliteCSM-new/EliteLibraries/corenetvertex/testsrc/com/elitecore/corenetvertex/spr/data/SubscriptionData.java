package com.elitecore.corenetvertex.spr.data;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.Table;
import com.elitecore.corenetvertex.util.QueryBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Objects;

/**
 * Used for DB query generation and expected AddonSubscription generation
 * 
 */
@Entity
@javax.persistence.Table(name = "TBLT_SUBSCRIPTION")
@Table(name = "TBLT_SUBSCRIPTION")
public class SubscriptionData {

	private String subscriberId;
	private String addonId;
	private String startTime;
	private String endTime;
	private String status;
	private String priority;
	private String subscriptionId;
	private String usageresetDate;
	private UserPackage addOn;
	private QuotaTopUp quotaTopUp;
	private String parentID;
	private String subscriptionTime;
	private String serverInstanceId;
	private String lastUpdateTime;
	private String rejectReason;
	private String param1;
	private String param2;
	private String productOfferId;
	private String type;
	private BoDPackage bod;
	private String metadata;

	public SubscriptionData(String subscriberId, String addonId, String startTime, String endTime, String status,
							String priority, String subscriptionId, String usageresetDate, String productOfferId) {
		this.subscriberId = subscriberId;
		this.addonId = addonId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.priority = priority;
		this.subscriptionId = subscriptionId;
		this.usageresetDate = usageresetDate;
		this.productOfferId = productOfferId;
	}

	public SubscriptionData() {

	}

	@javax.persistence.Column(name = "PRIORITY", nullable = true, precision = 0)
	@Column(name = "PRIORITY", type = Types.NUMERIC)
	public String getPriority() {
		return priority;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public void setAddonId(String addonId) {
		this.addonId = addonId;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setStartTimeStamp(Timestamp startTime) {
		this.startTime = startTime.getTime()+"";
	}

	public void setEndTimeStamp(Timestamp endTime) {
		this.endTime = endTime.getTime()+"";
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public void setUsageresetDate(String usageresetDate) {
		this.usageresetDate = usageresetDate;
	}

	@Id
	@javax.persistence.Column(name = "SUBSCRIPTION_ID", nullable = true, length = 36)
	@Column(name = "SUBSCRIPTION_ID", type = Types.VARCHAR)
	public String getSubscriptionId() {
		return subscriptionId;
	}

	@Column(name = "USAGE_RESET_DATE", type = Types.TIMESTAMP)
	public String getUsageresetDate() {
		return usageresetDate;
	}

	@javax.persistence.Column(name = "STATUS", nullable = true, precision = 0)
	@Column(name = "STATUS", type = Types.CHAR)
	public String getStatus() {
		return status;
	}

	@javax.persistence.Column(name = "SUBSCRIBER_ID", nullable = true, precision = 0)
	@Column(name = "SUBSCRIBER_ID", type = Types.VARCHAR)
	public String getSubscriberId() {
		return subscriberId;
	}

	@javax.persistence.Column(name = "PACKAGE_ID", nullable = true, length = 36)
	@Column(name = "PACKAGE_ID", type = Types.VARCHAR)
	public String getAddonId() {
		return addonId;
	}

	@Column(name = "START_TIME", type = Types.TIMESTAMP)
	public String getStartTime() {
		return startTime;
	}

	@javax.persistence.Column(name = "START_TIME", nullable = true, precision = 0)
	public Timestamp getStartTimeStamp() {
		if(Objects.nonNull(startTime)) {
			return Timestamp.valueOf(startTime);
		}

		return null;
	}

	@Transient
	@Column(name = "END_TIME", type = Types.TIMESTAMP)
	public String getEndTime() {
		return endTime;
	}

	@javax.persistence.Column(name = "END_TIME", nullable = true, precision = 0)
	public Timestamp getEndTimeStamp() {
		if(Objects.nonNull(endTime)) {
			return Timestamp.valueOf(endTime);
		}

		return null;
	}

	@javax.persistence.Column(name = "SUBSCRIPTION_TIME", nullable = true, precision = 0)
	public Timestamp getSubscriptionTimeStamp() {
		if(Objects.nonNull(subscriptionTime)) {
			return Timestamp.valueOf(subscriptionTime);
		}

		return null;
	}

	public void setSubscriptionTimeStamp(Timestamp subscriptionTime) {
		this.subscriptionTime = subscriptionTime.getTime() + "";
	}

	@javax.persistence.Column(name = "LAST_UPDATE_TIME", nullable = true, precision = 0)
	public Timestamp getLastUpdateTimeStamp() {
		if(Objects.nonNull(lastUpdateTime)) {
			return Timestamp.valueOf(lastUpdateTime);
		}

		return null;
	}

	public void setLastUpdateTimeStamp(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime.getTime() + "";
	}


	@javax.persistence.Column(name = "USAGE_RESET_DATE", nullable = true, precision = 0)
	public Timestamp getUsageRestDate() {
		if(Objects.nonNull(usageresetDate)) {
			return Timestamp.valueOf(usageresetDate);
		}

		return null;
	}

	public void setUsageRestDate(Timestamp usageresetDate) {
		this.usageresetDate = usageresetDate.getTime() + "";
	}


	@javax.persistence.Column(name = "PARENT_IDENTITY", nullable = true, precision = 0)
	@Column(name = "PARENT_IDENTITY", type = Types.VARCHAR)
	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	@Column(name = "SUBSCRIPTION_TIME", type = Types.TIMESTAMP)
	public String getSubscriptionTime() {
		return subscriptionTime;
	}

	public void setSubscriptionTime(String subscriptionTime) {
		this.subscriptionTime = subscriptionTime;
	}

	@javax.persistence.Column(name = "SERVER_INSTANCE_ID", nullable = true, precision = 0)
	@Column(name = "SERVER_INSTANCE_ID", type = Types.VARCHAR)
	public String getServerInstanceId() {
		return serverInstanceId;
	}

	public void setServerInstanceId(String serverInstanceId) {
		this.serverInstanceId = serverInstanceId;
	}

	@Column(name = "LAST_UPDATE_TIME", type = Types.TIMESTAMP)
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@javax.persistence.Column(name = "REJECT_REASON", nullable = true, precision = 0)
	@Column(name = "REJECT_REASON", type = Types.VARCHAR)
	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	@javax.persistence.Column(name = "PARAM1", nullable = true, precision = 0)
	@Column(name = "PARAM1", type = Types.VARCHAR)
	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	@javax.persistence.Column(name = "PARAM2", nullable = true, precision = 0)
	@Column(name = "PARAM2", type = Types.VARCHAR)
	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	@javax.persistence.Column(name = "METADATA", nullable = true, precision = 0)
	@Column(name = "METADATA", type = Types.VARCHAR)
	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	@Transient
	public Subscription getAddonSubscription() {

		Timestamp startTime = null, endTime = null, resetTime = null;
		if (Strings.isNullOrBlank(getStartTime()) == false) {
			startTime = Timestamp.valueOf(getStartTime());
		}
		if (Strings.isNullOrBlank(getEndTime()) == false) {
			endTime = Timestamp.valueOf(getEndTime());
		}
		/*
		 * when usageReset time is not configured, assigned expiry time in
		 * expected subscriptions
		 */
		if (Strings.isNullOrBlank(getUsageresetDate()) == false) {
			if (Strings.isNullOrBlank(getEndTime()) == false) {
				resetTime = Timestamp.valueOf(getEndTime());
			}
		}

		Subscription addOnSubscription = new Subscription.SubscriptionBuilder()
				.withStatus(SubscriptionState.fromValue(Integer.parseInt(getStatus())))
				.withId(subscriptionId)
				.withPackageId(addOn.getId())
				.withEndTime(endTime)
				.withStartTime(startTime)
				.withType(SubscriptionType.ADDON)
				.withSubscriberIdentity(getSubscriberId()).withProductOfferId(productOfferId)
				.build();

		return addOnSubscription;
	}

	@Transient
	public Subscription getQuotaTopUpSubscription() {

		Timestamp startTime = null, endTime = null, resetTime = null;
		if (Strings.isNullOrBlank(getStartTime()) == false) {
			startTime = Timestamp.valueOf(getStartTime());
		}
		if (Strings.isNullOrBlank(getEndTime()) == false) {
			endTime = Timestamp.valueOf(getEndTime());
		}
		/*
		 * when usageReset time is not configured, assigned expiry time in
		 * expected subscriptions
		 */
		if (Strings.isNullOrBlank(getUsageresetDate()) == false) {
			if (Strings.isNullOrBlank(getEndTime()) == false) {
				resetTime = Timestamp.valueOf(getEndTime());
			}
		}

		Subscription addOnSubscription = new Subscription.SubscriptionBuilder()
				.withStatus(SubscriptionState.fromValue(Integer.parseInt(getStatus())))
				.withId(subscriptionId)
				.withPackageId(quotaTopUp.getId())
				.withEndTime(endTime)
				.withStartTime(startTime)
				.withSubscriberIdentity(getSubscriberId())
				.withType(SubscriptionType.TOP_UP)
				.build();

		return addOnSubscription;
	}

	@Transient
	public Subscription getBoDSubscription() {

		Timestamp startTime = null, endTime = null, resetTime = null;
		if (Strings.isNullOrBlank(getStartTime()) == false) {
			startTime = Timestamp.valueOf(getStartTime());
		}
		if (Strings.isNullOrBlank(getEndTime()) == false) {
			endTime = Timestamp.valueOf(getEndTime());
		}
		/*
		 * when usageReset time is not configured, assigned expiry time in
		 * expected subscriptions
		 */
		if (Strings.isNullOrBlank(getUsageresetDate()) == false) {
			if (Strings.isNullOrBlank(getEndTime()) == false) {
				resetTime = Timestamp.valueOf(getEndTime());
			}
		}

		Subscription bodSubscription = new Subscription.SubscriptionBuilder()
				.withStatus(SubscriptionState.fromValue(Integer.parseInt(getStatus())))
				.withId(subscriptionId)
				.withPackageId(bod.getId())
				.withEndTime(endTime)
				.withStartTime(startTime)
				.withType(SubscriptionType.BOD)
				.withSubscriberIdentity(getSubscriberId()).withProductOfferId(null)
				.build();

		return bodSubscription;
	}

	public static String createTableQuery() {
		return QueryBuilder.buildCreateQuery(SubscriptionData.class);
	}

	public String insertQuery() throws IllegalArgumentException, NullPointerException, IllegalAccessException, InvocationTargetException {
		return QueryBuilder.buildInsertQuery(this);
	}

	public static String dropTableQuery() {
		return QueryBuilder.buildDropQuery(SubscriptionData.class);
	}

	public void setAddOn(UserPackage addOn) {
		this.type = SubscriptionType.ADDON.name();
		this.addOn = addOn;
	}

	public void setQuotaTopUp(QuotaTopUp quotaTopUp) {
		this.quotaTopUp = quotaTopUp;
	}

	@Transient
	public UserPackage getAddon() {
		return addOn;
	}

	@Transient
	public QuotaTopUp getQuotaTopUp() {
		return quotaTopUp;
	}

	@javax.persistence.Column(name = "PRODUCT_OFFER_ID", nullable = true, length = 36)
	@Column(name = "PRODUCT_OFFER_ID", type = Types.VARCHAR)
	public String getProductOfferId() {
		return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
		this.productOfferId = productOfferId;
	}

	@javax.persistence.Column(name = "TYPE", nullable = true, length = 10)
	@Column(name = "TYPE", type = Types.VARCHAR)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BoDPackage getBod() {
		return bod;
	}

	public void setBod(BoDPackage bod) {
		this.type = SubscriptionType.BOD.name();
		this.bod = bod;
	}
}
