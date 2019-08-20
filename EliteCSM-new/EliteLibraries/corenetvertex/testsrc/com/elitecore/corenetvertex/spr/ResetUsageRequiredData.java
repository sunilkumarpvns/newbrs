package com.elitecore.corenetvertex.spr;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.corenetvertex.util.QueryBuilder;

@Table(name="TBLM_RESET_USAGE_REQ")
public class ResetUsageRequiredData {

	private String billingCycleId;
	private String subscriberIdentity;
	private String alternateIdentity;
	private long billingCycleDate;
	private long createDate;
	private String packageId;
	private String resetReason;
	private String status;
	private String serverInstanceId;
	private String parameter1;
	private String parameter2;
	private String parameter3;
	private String productOfferId;
	
	@Column(name = "BILLING_CYCLE_ID", type = Types.VARCHAR)
	public String getBillingCycleId() {
		return billingCycleId;
	}
	public void setBillingCycleId(String billingCycleId) {
		this.billingCycleId = billingCycleId;
	}
	
	@Column(name = "SUBSCRIBER_IDENTITY", type = Types.VARCHAR)
	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}
	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}
	
	@Column(name = "ALTERNATE_IDENTITY", type = Types.VARCHAR)
	public String getAlternateIdentity() {
		return alternateIdentity;
	}
	public void setAlternateIdentity(String alternateIdentity) {
		this.alternateIdentity = alternateIdentity;
	}
	
	@Column(name = "BILLING_CYCLE_DATE", type = Types.TIMESTAMP)
	public String getBillingCycleDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(billingCycleDate));
	}
	public void setBillingCycleDate(long billingCycleDate) {
		this.billingCycleDate = billingCycleDate;
	}
	
	@Column(name = "PACKAGE_ID", type = Types.VARCHAR)
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	
	@Column(name = "RESET_REASON", type = Types.VARCHAR)
	public String getResetReason() {
		return resetReason;
	}
	public void setResetReason(String resetReason) {
		this.resetReason = resetReason;
	}
	
	@Column(name = "PARAM1", type = Types.VARCHAR)
	public String getParameter1() {
		return parameter1;
	}
	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}
	
	@Column(name = "PARAM2", type = Types.VARCHAR)
	public String getParameter2() {
		return parameter2;
	}
	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}
	
	@Column(name = "PARAM3", type = Types.VARCHAR)
	public String getParameter3() {
		return parameter3;
	}
	public void setParameter3(String parameter3) {
		this.parameter3 = parameter3;
	}
	
	@Column(name = "CREATED_DATE", type = Types.TIMESTAMP)
	public String getCreateDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(createDate));
	}
	
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	
	@Column(name = "STATUS", type = Types.VARCHAR)
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name = "SERVER_INSTANCE_ID", type = Types.VARCHAR)
	public String getServerInstanceId() {
		return serverInstanceId;
	}
	public void setServerInstanceId(String serverInstanceId) {
		this.serverInstanceId = serverInstanceId;
	}

	@Column(name = "PRODUCT_OFFER_ID", type = Types.VARCHAR)
	public String getProductOfferId() {
		return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
		this.productOfferId = productOfferId;
	}

	public static String createTableQuery() {
		return QueryBuilder.buildCreateQuery(ResetUsageRequiredData.class);
	}
	
	public static String dropTableQuery() {
		return QueryBuilder.buildDropQuery(ResetUsageRequiredData.class);
	}

	@Override
	public String toString() {
		return  packageId + "," + subscriberIdentity;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResetUsageRequiredData other = (ResetUsageRequiredData) obj;
		if (subscriberIdentity == null) {
			if (other.subscriberIdentity != null)
				return false;
		} else if (!subscriberIdentity.equals(other.subscriberIdentity))
			return false;
		if (packageId == null) {
			if (other.packageId != null)
				return false;
		} else if (!packageId.equals(other.packageId))
			return false;
		return true;
	}
}

