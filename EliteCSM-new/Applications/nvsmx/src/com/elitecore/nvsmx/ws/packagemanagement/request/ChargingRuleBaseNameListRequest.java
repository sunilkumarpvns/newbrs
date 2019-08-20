package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;

import java.util.List;

/**
 * Created by RAJ KIRPALSINH
 */
public class ChargingRuleBaseNameListRequest {

    private final List<ChargingRuleBaseNameData> chargingRuleBaseNames;
    private final String action;
    private final String parameter1;
    private final String parameter2;

    public ChargingRuleBaseNameListRequest(List<ChargingRuleBaseNameData> chargingRuleBaseNames, String action, String parameter1, String parameter2) {
        this.chargingRuleBaseNames = chargingRuleBaseNames;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public List<ChargingRuleBaseNameData> getChargingRuleBaseNames() {
        return chargingRuleBaseNames;
    }

    public String getAction() {
        return action;
    }

    public String getParameter1() {
        return parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }
}
