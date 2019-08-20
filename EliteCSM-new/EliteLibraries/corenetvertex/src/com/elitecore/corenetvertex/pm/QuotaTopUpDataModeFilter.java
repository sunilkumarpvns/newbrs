package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pkg.PkgMode;

public class QuotaTopUpDataModeFilter implements Predicate<DataTopUpData> {

    private static QuotaTopUpDataModeFilter modeFilter;

    private QuotaTopUpDataModeFilter() {
    }

    static {
        modeFilter = new QuotaTopUpDataModeFilter();
    }

    public static QuotaTopUpDataModeFilter getInstance() {
        return modeFilter;
    }

    @Override
    public boolean apply(DataTopUpData dataTopUpData) {
        return (PkgMode.DESIGN.name().equalsIgnoreCase(dataTopUpData.getPackageMode())) == false;
    }
}
