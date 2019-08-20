
package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

public class UMBaseQoSProfileDetailImpl implements UMBaseQoSProfileDetail {

	private static final long serialVersionUID = 1L;
	@Nullable
    private final QuotaProfileDetail allServiceQuotaProfileDetail;
    @Nullable
    private final Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail;
    @Nullable
    private final List<PCCRule> pccRules;
    @Nullable
    private final List<ChargingRuleBaseName> chargingRuleBaseNames;
    private final String name;
    private final String packageName;
    private final QoSProfileAction action;
    private final String rejectCause;
    private final IPCANQoS sessionQoS;
    private final int fupLevel;
    private final boolean usageMonitoring;
    private final SliceInformation sliceInformation;
    private final Integer orderNo;
    private final boolean isUsageRequired;
    private final boolean applyOnUsageUnavailability;
    @Nullable
    private final String redirectURL;

    public UMBaseQoSProfileDetailImpl(String name, String packageName, QoSProfileAction action, String reason,
                                      @Nullable QuotaProfileDetail allServiceQuotaProfileDetail,
                                      @Nullable Map<String, QuotaProfileDetail> serviceToUMQuotaProfileDetail, boolean isUsageRequired,
                                      int fupLevel, Integer orderNo, boolean applyOnUsageUnavailability, @Nullable String redirectURL) {
        this(packageName, packageName, action, reason, fupLevel, allServiceQuotaProfileDetail, serviceToUMQuotaProfileDetail, isUsageRequired, null, null, false, null, orderNo, applyOnUsageUnavailability, redirectURL, null);

    }

    public UMBaseQoSProfileDetailImpl(String name, String pkgName,
                                      QoSProfileAction action, String reason,
                                      int fupLevel,
                                      @Nullable QuotaProfileDetail allServiceQuotaProfileDetail,
                                      @Nullable Map<String, QuotaProfileDetail> serviceToUMQuotaProfileDetail,
                                      boolean isUsageRequired,  IPCANQoS sessionQoS,
                                      List<PCCRule> pccRules,   boolean usageMonitoring,
                                      SliceInformation sliceInformation, Integer orderNo,
                                      boolean applyOnUsageUnavailability, @Nullable String redirectURL,
                                      List<ChargingRuleBaseName> chargingRuleBaseNames
                                      ) {
        this.name = name;
        this.packageName = pkgName;
        this.action = action;
        this.rejectCause = reason;
        this.fupLevel = fupLevel;
        this.allServiceQuotaProfileDetail = allServiceQuotaProfileDetail;
        this.serviceToQuotaProfileDetail = serviceToUMQuotaProfileDetail;
        this.sessionQoS = sessionQoS;
        this.pccRules = pccRules;
        this.usageMonitoring = usageMonitoring;
        this.sliceInformation = sliceInformation;
        this.orderNo = orderNo;
        this.applyOnUsageUnavailability = applyOnUsageUnavailability;
        this.isUsageRequired = isUsageRequired;
        this.redirectURL = redirectURL;
        this.chargingRuleBaseNames = chargingRuleBaseNames;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.UMBaseQuotaProfileDetail#getAllServiceQuotaProfileDetail()
     */
    @Override
    public QuotaProfileDetail getAllServiceQuotaProfileDetail() {
        return allServiceQuotaProfileDetail;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.UMBaseQuotaProfileDetail#getServiceToQuotaProfileDetail()
     */
    @Override
    public Map<String, QuotaProfileDetail> getServiceToQuotaProfileDetail() {
        return serviceToQuotaProfileDetail;
    }

    @Override
    public String getLevel() {
        return fupLevel == 0 ? HSQ : FUP + CommonConstants.DASH + fupLevel;
    }

    @Override
    @Nullable
    public String getRedirectURL() {
        return redirectURL;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getOrderNo()
     */
    @Override
    public int getOrderNo() {
        return orderNo;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getQuotaProfileName()
     */
    @Override
    public
    @Nullable
    String getQuotaProfileName() {
        if (allServiceQuotaProfileDetail != null) {
            return allServiceQuotaProfileDetail.getName();
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getQuotaProfileId()
     */
    @Override
    public
    @Nullable
    String getQuotaProfileId() {
        if (allServiceQuotaProfileDetail != null) {
            return allServiceQuotaProfileDetail.getQuotaProfileId();
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getSessionQoS()
     */
    @Override
    public IPCANQoS getSessionQoS() {
        return sessionQoS;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getAction()
     */
    @Override
    public QoSProfileAction getAction() {
        return action;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getPCCRules()
     */
    @Override
    public List<PCCRule> getPCCRules() {
        return this.pccRules;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getFUPLevel()
     */
    @Override
    public int getFUPLevel() {
        return fupLevel;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getSliceInformation()
     */
    @Override
    public SliceInformation getSliceInformation() {
        return sliceInformation;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getUsageMonitoring()
     */
    @Override
    public boolean getUsageMonitoring() {
        return usageMonitoring;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getRejectCause()
     */
    @Override
    public String getRejectCause() {
        return rejectCause;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getPackageName()
     */
    @Override
    public String getPackageName() {
        return packageName;
    }

    /* (non-Javadoc)
     * @see com.elitecore.corenetvertex.pm.newpkg.IUMBasedQoSProfileDetail#getUniqueName()
     */
    @Override
    public String getUniqueName() {
        return packageName + CommonConstants.HASH + name;
    }

    public List<PCCRule> getPccRules() {
        return pccRules;
    }


    @Override
    public boolean isUsageRequired() {
        return isUsageRequired;
    }

    @Override
    public boolean isApplyOnUsageUnavailability() {
        return applyOnUsageUnavailability;
    }

    @Override
    public List<ChargingRuleBaseName> getChargingRuleBaseNames(){
        return chargingRuleBaseNames;
    }

    @Override
    public String toString() {

        return toString(QOS_PROFILE_DETAIL_DATA_TO_STRING_STYLE);
    }

    public String toString(ToStringStyle toStringStyle) {

        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                toStringStyle).append("Level", (fupLevel == 0 ? "HSQ" : "fup level " + fupLevel))
                .append("Action", getAction());

        if (sessionQoS != null) {
            sessionQoS.toString(toStringBuilder);
        }
        toStringBuilder.append("Redirect URL", redirectURL);

        if (getAction() == QoSProfileAction.ACCEPT) {
            toStringBuilder.append("");
            if (Collectionz.isNullOrEmpty(pccRules) == false) {
                for (PCCRule pccRule : pccRules) {
                    toStringBuilder.append("PCC Rule", pccRule);
                }
            }
        }

        toStringBuilder.append("");
        if (Collectionz.isNullOrEmpty(chargingRuleBaseNames) == false) {
            for (ChargingRuleBaseName chargingRuleBaseName : chargingRuleBaseNames) {
                toStringBuilder.append("Charging Rule Base Name", chargingRuleBaseName);
            }
        }

        return toStringBuilder.toString();
    }

}
