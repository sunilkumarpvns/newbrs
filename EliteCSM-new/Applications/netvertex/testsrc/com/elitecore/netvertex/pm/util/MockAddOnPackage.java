package com.elitecore.netvertex.pm.util;

import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.netvertex.pm.AddOn;

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
public class MockAddOnPackage extends AddOn {


    public MockAddOnPackage(String id, String name, QuotaProfileType quotaProfileType, PkgStatus availabilityStatus, List<QoSProfile> qosProfiles, boolean multipleSubscription, boolean isExclusive, int validityPeriod, ValidityPeriodUnit validityPeriodUnit, UsageNotificationScheme usageNotificationScheme, PkgMode packageMode, String description, Double price, Timestamp availabilityStartDate, Timestamp availabilityEndDate, String eligibleGroupIds, PolicyStatus status, @Nullable String failReason, @Nullable String partialFailReason, @Nullable String param1, @Nullable String param2,String currency) {
        super(id, name, quotaProfileType, availabilityStatus, qosProfiles, multipleSubscription, isExclusive, validityPeriod, validityPeriodUnit, usageNotificationScheme, packageMode, description, price, availabilityStartDate, availabilityEndDate, CommonConstants.COMMA_SPLITTER.split(eligibleGroupIds), status, failReason, partialFailReason, param1, param2, null,currency);
    }

    public MockAddOnPackage(String id, String name) {
        super(id, name,
                null,
                null,
                Collections.emptyList(),
                false,
                false,
                1,
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
                null,
                null,
                null,null);
    }


    public MockAddOnPackage quotaProfileTypeIsUM() {
        when(this.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);
        return this;
    }

    public static MockAddOnPackage create(String id, String name) {
        MockAddOnPackage mockBasePackage = spy(new MockAddOnPackage(id, name));
        when(mockBasePackage.getId()).thenReturn(id);
        when(mockBasePackage.getName()).thenReturn(name);
        when(mockBasePackage.getPackageType()).thenReturn(PkgType.ADDON);
        when(mockBasePackage.getQoSProfiles()).thenReturn(Collections.emptyList());
        return mockBasePackage;
    }

    public MockAddOnPackage mockQuotaProfie() {
        when(this.getQuotaProfiles()).thenReturn(Arrays.asList(mock(QuotaProfile.class)));
        return this;
    }

    public MockAddOnPackage mockUMQuotaProfie() {
      return mockQuotaProfie().quotaProfileTypeIsUM();
    }

    public com.elitecore.netvertex.pm.QoSProfile mockQoSProfile() {
        com.elitecore.netvertex.pm.QoSProfile qoSProfile = mock(com.elitecore.netvertex.pm.QoSProfile.class);
        String id = UUID.randomUUID().toString();
        when(qoSProfile.getId()).thenReturn(id);
        when(qoSProfile.getName()).thenReturn("Name:" + UUID.randomUUID().toString());
        when(this.getQoSProfiles()).thenReturn(Arrays.asList(qoSProfile));
        when(this.getQoSProfile(id)).thenReturn(qoSProfile);

        return qoSProfile;
    }

    public MockAddOnPackage quotaProfileTypeIsRnC() {
        when(this.getQuotaProfileType()).thenReturn(QuotaProfileType.RnC_BASED);
        return this;
    }
}
