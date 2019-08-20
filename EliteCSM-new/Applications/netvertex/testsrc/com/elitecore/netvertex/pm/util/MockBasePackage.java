package com.elitecore.netvertex.pm.util;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.netvertex.pm.BasePackage;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by harsh on 5/25/17.
 */
public class MockBasePackage extends BasePackage {

    public MockBasePackage(String id, String name, QuotaProfileType quotaProfileType, PkgStatus availabilityStatus, List<QoSProfile> qosProfiles, UsageNotificationScheme usageNotificationScheme, PkgMode packageMode, String description, Double price, Timestamp availabilityStartDate, Timestamp availabilityEndDate, String eligibleGroupIds, PolicyStatus status, @Nullable String failReason, @Nullable String partialFailReason, @Nullable String param1, @Nullable String param2,String currency) {
        super(id, name, quotaProfileType, availabilityStatus, qosProfiles, usageNotificationScheme, packageMode, description, price, availabilityStartDate, availabilityEndDate, CommonConstants.COMMA_SPLITTER.split(eligibleGroupIds), status, failReason, partialFailReason, param1, param2, null,currency);
    }

    public MockBasePackage() {
        super(null,
                null,
                null,
                null,
                Collections.emptyList(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, null,null);
    }

    public MockBasePackage quotaProfileTypeIsUM() {
        when(this.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);
        return this;
    }

    public MockBasePackage quotaProfileTypeIsRnC() {
        when(this.getQuotaProfileType()).thenReturn(QuotaProfileType.RnC_BASED);
        return this;
    }

    public static MockBasePackage create(String id, String name) {
        MockBasePackage mockBasePackage = spy(new MockBasePackage());
        when(mockBasePackage.getId()).thenReturn(id);
        when(mockBasePackage.getName()).thenReturn(name);
        when(mockBasePackage.getPackageType()).thenReturn(PkgType.BASE);
        when(mockBasePackage.getQoSProfiles()).thenReturn(Collections.emptyList());
        when(mockBasePackage.getQuotaProfileType()).thenReturn(QuotaProfileType.RnC_BASED);
        when(mockBasePackage.getCurrency()).thenReturn("INR");
        return mockBasePackage;
    }

    public MockBasePackage mockQuotaProfie() {
        QuotaProfile quotaProfile = mock(QuotaProfile.class);
        when(quotaProfile.getId()).thenReturn(UUID.randomUUID().toString());
        when(quotaProfile.getName()).thenReturn("Name:" + UUID.randomUUID().toString());
        when(this.getQuotaProfile(quotaProfile.getId())).thenReturn(quotaProfile);
        when(this.getQuotaProfiles()).thenReturn(Arrays.asList(quotaProfile));
        return this;
    }

    public MockBasePackage statusActive() {
        when(this.getAvailabilityStatus()).thenReturn(PkgStatus.ACTIVE);
        return this;
    }

    public MockBasePackage statusInActive() {
        when(this.getAvailabilityStatus()).thenReturn(PkgStatus.INACTIVE);
        return this;
    }

    public MockBasePackage policyStatusSuccess() {
        when(this.getStatus()).thenReturn(PolicyStatus.SUCCESS);
        return this;
    }

    public MockBasePackage policyStatusFailure() {
        when(this.getStatus()).thenReturn(PolicyStatus.FAILURE);
        return this;
    }

    public void mockPCCRule(List<PCCRule> pccRules) {
        QoSProfile qoSProfile = mock(QoSProfile.class);
        when(qoSProfile.getPCCRules()).thenReturn(pccRules);

        when(this.getQoSProfiles()).thenReturn(Arrays.asList(qoSProfile));
    }

    public void mockChargingRuleBaseName(List<ChargingRuleBaseName> chargingRuleBaseNames) {
        QoSProfile qoSProfile = mock(QoSProfile.class);
        when(qoSProfile.getChargingRuleBaseNames()).thenReturn(chargingRuleBaseNames);
        when(this.getQoSProfiles()).thenReturn(Arrays.asList(qoSProfile));
    }

    public com.elitecore.netvertex.pm.QoSProfile mockQoSProfile() {
        com.elitecore.netvertex.pm.QoSProfile qoSProfile = mock(com.elitecore.netvertex.pm.QoSProfile.class);
        when(qoSProfile.getId()).thenReturn(UUID.randomUUID().toString());
        when(qoSProfile.getName()).thenReturn("Name:" + UUID.randomUUID().toString());
        when(this.getQoSProfiles()).thenReturn(Arrays.asList(qoSProfile));
        when(this.getQoSProfile(qoSProfile.getId())).thenReturn(qoSProfile);
        return qoSProfile;
    }

    public MockBasePackage quotaProfileIsSy() {
        when(this.getQuotaProfileType()).thenReturn(QuotaProfileType.SY_COUNTER_BASED);
        return this;

    }

    public void mockQuotaProfie(QuotaProfile quotaProfile) {
        when(getQuotaProfiles()).thenReturn(Arrays.asList(quotaProfile));
        when(getQuotaProfile(quotaProfile.getId())).thenReturn(quotaProfile);
    }

    public void mockRateCard(DataRateCard dataRateCard) {
        when(getDataRateCards()).thenReturn(Arrays.asList(dataRateCard));
        when(getDataRateCard(dataRateCard.getId())).thenReturn(dataRateCard);
    }
}
