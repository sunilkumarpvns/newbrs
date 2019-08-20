package com.elitecore.corenetvertex.pm.util;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by harsh on 5/25/17.
 */
public class MockPromotionalPackage extends PromotionalPackage {

    public MockPromotionalPackage(String id,
                                  String name,
                                  QuotaProfileType quotaProfileType,
                                  PkgStatus availabilityStatus,
                                  List<QoSProfile> qosProfiles,
                                  UsageNotificationScheme usageNotificationScheme,
                                  PkgMode packageMode,
                                  String description,
                                  Double price,
                                  Timestamp availabilityStartDate,
                                  Timestamp availabilityEndDate,
                                  String eligibleGroupIds, boolean preferPromotionalQoS, PolicyStatus status, @Nullable String failReason, @Nullable String partialFailReason, @Nullable String param1, @Nullable String param2, List<GroupManageOrder> groupOrderConfs) {
        super(id, name, quotaProfileType, availabilityStatus, qosProfiles, usageNotificationScheme, packageMode, description, price, availabilityStartDate, availabilityEndDate, CommonConstants.COMMA_SPLITTER.split(eligibleGroupIds), preferPromotionalQoS, status, failReason, partialFailReason, param1, param2, groupOrderConfs);
    }

    public MockPromotionalPackage() {
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
                true,
                null,
                null,
                null,
                null,
                null
                , null);
    }

    public MockPromotionalPackage quotaProfileTypeIsUM() {
        when(this.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);
        return this;
    }

    public MockPromotionalPackage quotaProfileTypeIsRnC() {
        when(this.getQuotaProfileType()).thenReturn(QuotaProfileType.RnC_BASED);
        return this;
    }

    public static MockPromotionalPackage create(String id, String name) {
        MockPromotionalPackage mockBasePackage = spy(new MockPromotionalPackage());
        when(mockBasePackage.getId()).thenReturn(id);
        when(mockBasePackage.getName()).thenReturn(name);
        when(mockBasePackage.getPackageType()).thenReturn(PkgType.BASE);
        when(mockBasePackage.getQoSProfiles()).thenReturn(Collections.emptyList());
        return mockBasePackage;
    }

    public MockPromotionalPackage mockQuotaProfie() {
        when(this.getQuotaProfiles()).thenReturn(Arrays.asList(mock(QuotaProfile.class)));
        return this;
    }

    public MockPromotionalPackage statusActive() {
        when(this.getAvailabilityStatus()).thenReturn(PkgStatus.ACTIVE);
        return this;
    }

    public MockPromotionalPackage statusInActive() {
        when(this.getAvailabilityStatus()).thenReturn(PkgStatus.INACTIVE);
        return this;
    }

    public MockPromotionalPackage policyStatusSuccess() {
        when(this.getStatus()).thenReturn(PolicyStatus.SUCCESS);
        return this;
    }

    public MockPromotionalPackage policyStatusFailure() {
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

    public QoSProfile mockQoSProfile() {
        QoSProfile qoSProfile = mock(QoSProfile.class);
        when(qoSProfile.getId()).thenReturn(UUID.randomUUID().toString());
        when(qoSProfile.getName()).thenReturn("Name:" + UUID.randomUUID().toString());
        when(this.getQoSProfiles()).thenReturn(Arrays.asList(qoSProfile));
        when(this.getQoSProfile(qoSProfile.getId())).thenReturn(qoSProfile);
        return qoSProfile;
    }
}
