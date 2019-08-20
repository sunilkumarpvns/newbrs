package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import org.apache.commons.lang.SystemUtils;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

public class SyCounterBaseQuotaProfileDetail implements QuotaProfileDetail {

	private static final long serialVersionUID = 1L;

	protected static final ToStringStyle QUOTA_PROFILE_DETAIL_TO_STRING_STYLE = new QuotaProfileDetailDataToString();
	
	private final String quotaProfileId;
	private final String name;
	private final String pkgName;
	private final String serviceId;
	private final int fupLevel;
	private String serviceName;

	public SyCounterBaseQuotaProfileDetail(String quotaProfileId, String name, String pkgName, String serviceId, String serviceName, int fupLevel) {
		this.quotaProfileId = quotaProfileId;
		this.name = name;
		this.pkgName = pkgName;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.fupLevel = fupLevel;
	}

	public String getUsageKey() {
		return quotaProfileId + CommonConstants.USAGE_KEY_SEPARATOR + serviceId;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.ISyCounterBaseQuotaProfileDetail#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.ISyCounterBaseQuotaProfileDetail#getQuotaProfileIdOrRateCardId()
	 */
	@Override
	public String getQuotaProfileId() {
		return quotaProfileId;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.ISyCounterBaseQuotaProfileDetail#getServiceId()
	 */
	@Override
	public String getServiceId() {
		return serviceId;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.ISyCounterBaseQuotaProfileDetail#getFupLevel()
	 */
	@Override
	public int getFupLevel() {
		return fupLevel;
	}

	@Override
	public boolean isHsqLevel() {
		return true;
	}

	@Override
	public QuotaProfileType getQuotaProfileType() {
		return QuotaProfileType.SY_COUNTER_BASED;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public String toString() {
		
		return new ToStringBuilder(this, QUOTA_PROFILE_DETAIL_TO_STRING_STYLE)
			.append("FUP Level", fupLevel)
			.append("Service Name",serviceName)
			.append("Type", QuotaProfileType.SY_COUNTER_BASED)
			.toString();
	}
	
	private static final class QuotaProfileDetailDataToString extends ToStringStyle.CustomToStringStyle {
		
		private static final long serialVersionUID = 1L;

		QuotaProfileDetailDataToString() {
			super();
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(6) + getTabs(2));
		}
	}
}
