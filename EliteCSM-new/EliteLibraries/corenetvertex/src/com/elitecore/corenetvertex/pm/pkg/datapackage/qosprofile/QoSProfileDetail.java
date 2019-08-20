
package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;

import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.util.ToStringStyle;
import org.apache.commons.lang.SystemUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface QoSProfileDetail extends Serializable{

	public static final ToStringStyle QOS_PROFILE_DETAIL_DATA_TO_STRING_STYLE = new QoSProfileDetailDataToString();
	
	public String FUP = "FUP";
	public String HSQ = "HSQ";

	IPCANQoS getSessionQoS();

	QoSProfileAction getAction();

	String getName();

	int getFUPLevel();

	//FIXME SHUOLD NOT BE HERE, AS RnC does not have slice information
	SliceInformation getSliceInformation();

	String getRejectCause();

	String getQuotaProfileName();

	String getQuotaProfileId();

	String getPackageName();

	//FIXME SHUOLD NOT BE HERE, AS RnC should not have this field
	boolean getUsageMonitoring();

	List<PCCRule> getPCCRules();

	List<ChargingRuleBaseName> getChargingRuleBaseNames();

	String getUniqueName();

	int getOrderNo();

	QuotaProfileDetail getAllServiceQuotaProfileDetail();

	Map<String, QuotaProfileDetail> getServiceToQuotaProfileDetail();

    static class QoSProfileDetailDataToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		QoSProfileDetailDataToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(6)
					+ getTabs(2));
		}
	}

	String getLevel();

	String getRedirectURL();

}
