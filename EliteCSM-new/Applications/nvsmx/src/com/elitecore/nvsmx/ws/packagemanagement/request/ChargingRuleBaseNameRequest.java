package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;

/**
 * Created by RAJ KIRPALSINH
 */
public class ChargingRuleBaseNameRequest {
    private final ChargingRuleBaseNameData chargingRuleBaseNameData;
    private final String action;
    private final String parameter1;
    private final String parameter2;

    public ChargingRuleBaseNameRequest(ChargingRuleBaseNameData chargingRuleBaseNameData, String action, String parameter1, String parameter2) {
        this.chargingRuleBaseNameData = chargingRuleBaseNameData;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public ChargingRuleBaseNameData getChargingRuleBaseNameData() {
        return chargingRuleBaseNameData;
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
