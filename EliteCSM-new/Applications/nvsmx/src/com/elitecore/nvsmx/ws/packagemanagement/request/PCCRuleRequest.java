package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;

/**
 * Created by aditya on 12/5/16.
 */
public class PCCRuleRequest {
    private final PCCRuleData globalPCCRule;
    private final String action;
    private final String parameter1;
    private final String parameter2;

    public PCCRuleRequest(PCCRuleData globalPCCRule, String action, String parameter1, String parameter2) {
        this.globalPCCRule = globalPCCRule;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public PCCRuleData getGlobalPCCRule() {
        return globalPCCRule;
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
