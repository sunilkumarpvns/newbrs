package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;

import java.util.Map;

import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;

public interface UMBaseQoSProfileDetail extends QoSProfileDetail {

	public abstract QuotaProfileDetail getAllServiceQuotaProfileDetail();

	public abstract Map<String, QuotaProfileDetail> getServiceToQuotaProfileDetail();

	boolean isUsageRequired();

	boolean isApplyOnUsageUnavailability();

}