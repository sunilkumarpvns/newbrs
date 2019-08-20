package com.elitecore.nvsmx.pd.controller.revenuedetail;

import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "pd")
@Namespace("/pd/revenuedetail")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "revenue-detail" }),

})
public class RevenueDetailCTRL extends RestGenericCTRL<RevenueDetailData> {

    private static final long serialVersionUID = -1514438069544331696L;

    @Override
    public ACLModules getModule() {
        return ACLModules.REVENUE_DETAIL;
    }

    @Override
    public RevenueDetailData createModel() {
        return new RevenueDetailData();
    }
}