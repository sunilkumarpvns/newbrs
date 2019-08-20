package com.elitecore.corenetvertex.pm.util;

import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MockQuotaProfile extends QuotaProfile {

    public MockQuotaProfile(String name, String pkgName, String id, BalanceLevel balanceLevel, int renewalInterval, RenewalIntervalUnit renewalIntervalUnit, QuotaProfileType quotaProfileType,
                            List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais) {
        super(name, pkgName, id, balanceLevel, renewalInterval, renewalIntervalUnit, quotaProfileType, fupLevelserviceWiseQuotaProfileDetais,CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
    }

    public MockQuotaProfile(String id, String name, Map<String, QuotaProfileDetail> serviceToDetailMap) {
        super(name,
                null,
                id,
                null,
                0,
                RenewalIntervalUnit.MONTH,
                null,
                Arrays.asList(serviceToDetailMap), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
    }

    public static MockQuotaProfile create(String id, String name, Map<String, QuotaProfileDetail> serviceToDetailMap) {
        MockQuotaProfile mockQuotaProfile = spy(new MockQuotaProfile(id, name, serviceToDetailMap));
        return mockQuotaProfile;
    }

    public MockQuotaProfile withHsqQuotaProfileDetail(String serviceId, RncProfileDetail rncProfileDetail) {
        Map<String, QuotaProfileDetail> serviceToDetailMap = new HashMap<>();
        serviceToDetailMap.put(serviceId, rncProfileDetail);
        when(this.getHsqLevelServiceWiseQuotaProfileDetails()).thenReturn(serviceToDetailMap);
        when(this.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(serviceToDetailMap));
        return this;
    }
}
