package com.elitecore.corenetvertex.sm.routing.mccmncroutingtable;

/**
 * @author jaidiptrivedi
 */
public enum RoutingAction {

    LOCAL("Local"),
    PROXY("Proxy");

    private String displayValue;

    RoutingAction(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

}
