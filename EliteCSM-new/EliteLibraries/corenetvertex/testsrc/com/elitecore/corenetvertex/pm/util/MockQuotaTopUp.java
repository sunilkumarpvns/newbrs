package com.elitecore.corenetvertex.pm.util;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MockQuotaTopUp extends QuotaTopUp {

    public MockQuotaTopUp(String id, String name, boolean multipleSubscription,
						  int validityPeriod,
						  ValidityPeriodUnit validityPeriodUnit,
						  PkgMode packageMode,
						  TopUpType topUpType,
						  String description,
						  Double price,
						  Timestamp availabilityStartDate,
						  Timestamp availabilityEndDate,
						  PkgStatus availabilityStatus,
						  List<String> groupIds,
						  PolicyStatus status,
						  QuotaProfile quotaProfile,
						  String failReason,
						  String partialFailReason,
						  @Nullable String param1,
						  @Nullable String param2,
						  QuotaNotificationScheme quotaNotificationScheme,
						  String quotaType,
						  String unitType,
						  Long volumeBalance,
						  String volumeBalanceUnit,
						  Long timeBalance,
						  String timeBalanceUnit, List<String> applicablePCCPRofiles) {

        super(id, name,
                multipleSubscription, validityPeriod, validityPeriodUnit, packageMode, topUpType,
                description, price,
                availabilityStartDate,
                availabilityEndDate, availabilityStatus,
                groupIds, quotaProfile,
                failReason, partialFailReason, status,param1, param2, quotaNotificationScheme,quotaType,unitType,volumeBalance,volumeBalanceUnit,timeBalance,timeBalanceUnit, applicablePCCPRofiles);
    }

    public MockQuotaTopUp(String id, String name) {
        this(id,name,Collections.emptyList());
    }

    public MockQuotaTopUp(String id, String name,List<String> applicablePCCProfiles) {

        this(id,
                name,
                false,
                1,
                null,
                null,
                TopUpType.TOP_UP,
                null,
                null,
                null,
                null,
                PkgStatus.ACTIVE,
                Collections.emptyList(),
                null,
                null,
                null,
                null,
                null,
                null, null,null,
                null,0L,null,0L,null, applicablePCCProfiles);
    }


    public MockQuotaTopUp quotaProfileTypeIsRnC() {
        when(this.getQuotaProfileType()).thenReturn(QuotaProfileType.RnC_BASED);
        return this;
    }

    public MockQuotaTopUp withPackageType(TopUpType topUpType) {
        when(this.getPackageType()).thenReturn(topUpType);
        return this;
    }

    public static MockQuotaTopUp create(String id, String name) {
        MockQuotaTopUp mockQuotaTopUp = spy(new MockQuotaTopUp(id, name));
        when(mockQuotaTopUp.getId()).thenReturn(id);
        when(mockQuotaTopUp.getName()).thenReturn(name);
        when(mockQuotaTopUp.getMode()).thenReturn(PkgMode.LIVE);
        return mockQuotaTopUp;
    }

    public static MockQuotaTopUp create(String id, String name,List<String> applicablePCCProfiles) {
        MockQuotaTopUp mockQuotaTopUp = spy(new MockQuotaTopUp(id, name,applicablePCCProfiles));
        when(mockQuotaTopUp.getId()).thenReturn(id);
        when(mockQuotaTopUp.getName()).thenReturn(name);
        when(mockQuotaTopUp.getMode()).thenReturn(PkgMode.LIVE);
        return mockQuotaTopUp;
    }

    public MockQuotaTopUp statusActive() {
        when(this.getAvailabilityStatus()).thenReturn(PkgStatus.ACTIVE);
        return this;
    }

    public MockQuotaTopUp statusInActive() {
        when(this.getAvailabilityStatus()).thenReturn(PkgStatus.INACTIVE);
        return this;
    }

    public MockQuotaTopUp modeIsLive() {
        when(this.getMode()).thenReturn(PkgMode.LIVE);
        return this;
    }

    public MockQuotaTopUp modeIsTest() {
        when(this.getMode()).thenReturn(PkgMode.TEST);
        return this;
    }


    public MockQuotaTopUp policyStatusSuccess() {
        when(this.getStatus()).thenReturn(PolicyStatus.SUCCESS);
        return this;
    }

    public MockQuotaTopUp policyStatusFailure() {
        when(this.getStatus()).thenReturn(PolicyStatus.FAILURE);
        return this;
    }


    public MockQuotaTopUp withApplicablePCCProfiles(List<String> applicablePCCProfiles){
        when(this.getApplicablePCCProfiles()).thenReturn(applicablePCCProfiles);
        return this;
    }

    private Map<String, QuotaProfileDetail> createQuotaProfile(){
        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE,
                new BillingCycleAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY,
                new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY,
                new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);

        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage, 0, 0,0,0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null, VolumeUnitType.TOTAL, "test", true, null, 0,0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();

        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        return fupLevelServiceWiseQuotaProfileDetails;
    }

    public MockQuotaTopUp mockQuotaProfie() {
        when(this.getQuotaProfile()).thenReturn(new QuotaProfile("QuotaProfile", "PkgName", "QuotaProfileId", BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED,
                Arrays.asList(createQuotaProfile()), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue()));
        return this;
    }

    public MockQuotaTopUp mockRnCQuotaProfie(){
        return mockQuotaProfie().quotaProfileTypeIsRnC();
    }


    public MockQuotaTopUp setQuotaProfiles(QuotaProfile quotaProfile) {
        when(this.getQuotaProfile()).thenReturn(quotaProfile);
        return this;
    }

    public void multipleSubscriptionNotAllowed() {
        when(this.isMultipleSubscription()).thenReturn(false);
    }

    public MockQuotaTopUp topUpIsSpare() {
        when(this.getPackageType()).thenReturn(TopUpType.SPARE_TOP_UP);
        return this;
    }
}
