package com.elitecore.nvsmx.ws.util;

import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.nvsmx.ws.subscription.data.TopUpPackageData;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.function.Function;

public class WebServiceTopUpPkgResponseMapper {
    public static final Function<QuotaTopUp,TopUpPackageData> QUOTA_TOP_UP_TO_WS_TOP_UP_MAPPER = new QuotaTopUpToWSTopUpMapper();

    private WebServiceTopUpPkgResponseMapper(){
    }

    private static class QuotaTopUpToWSTopUpMapper implements Function<QuotaTopUp, TopUpPackageData> {

        @Override
        public TopUpPackageData apply(QuotaTopUp topUp) {
            String applicablePCCProfiles = CollectionUtils.isEmpty(topUp.getApplicablePCCProfiles()) ? null : StringUtils.join(topUp.getApplicablePCCProfiles(), ',');
            return new TopUpPackageData(topUp.getId(), topUp.getName(), topUp.getDescription(), topUp.getPrice(), topUp.getPackageType().name(), topUp.getValidity(), topUp
                    .getValidityPeriodUnit(), topUp.getParam1(), topUp.getParam2()
                    , topUp.getQuotaType(), topUp.getUnitType(), topUp.getVolumeBalance(), topUp.getVolumeBalance() == null ? null : topUp.getVolumeBalanceUnit(), topUp.getTimeBalance(), topUp.getTimeBalance() == null ? null : topUp.getTimeBalanceUnit(), applicablePCCProfiles);
        }
    }



}


