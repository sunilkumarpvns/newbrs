package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.util.ToStringStyle;
import org.apache.commons.lang.SystemUtils;

import java.io.Serializable;


public interface QuotaProfileDetail extends Serializable{

	public static final ToStringStyle QUOTA_PROFILE_DETAIL_TO_STRING_STYLE = new QuotaProfileDetailDataToString();
	
	public abstract String getName();

	public abstract String getQuotaProfileId();

	public abstract String getServiceId();
	
	public abstract String getServiceName();

	public abstract int getFupLevel();

	public abstract boolean isHsqLevel();

	QuotaProfileType getQuotaProfileType();


	static class QuotaProfileDetailDataToString extends ToStringStyle.CustomToStringStyle {
		
		private static final long serialVersionUID = 1L;

		QuotaProfileDetailDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(6) + getTabs(2));
		}
	}
	
}