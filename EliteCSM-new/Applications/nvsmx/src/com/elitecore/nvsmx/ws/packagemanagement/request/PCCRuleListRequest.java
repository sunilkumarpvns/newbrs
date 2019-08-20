package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;

import java.util.List;

/**
 * Created by aditya on 12/5/16.
 */
public class PCCRuleListRequest {


    private final List<PCCRuleData> globalPCCRules;
    private final String action;
    private final String parameter1;
    private final String parameter2;

    public PCCRuleListRequest(List<PCCRuleData> globalPCCRules, String action, String parameter1, String parameter2) {
        this.globalPCCRules = globalPCCRules;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public List<PCCRuleData> getGlobalPCCRules() {
        return globalPCCRules;
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
