package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class StatefulProHandlerData {

    private String ruleset;
    private String serverGroupName;
    private String translationMappingName;
    private String script;
    private String acceptOnTimeout;
    private String waitForResponse;

    public String getRuleset() {
        return ruleset;
    }

    public void setRuleset(String ruleset) {
        this.ruleset = ruleset;
    }

    public String getServerGroupName() {
        return serverGroupName;
    }

    public void setServerGroupName(String serverGroupName) {
        this.serverGroupName = serverGroupName;
    }

    public String getTranslationMappingName() {
        return translationMappingName;
    }

    public void setTranslationMappingName(String translationMappingName) {
        this.translationMappingName = translationMappingName;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getAcceptOnTimeout() {
        return acceptOnTimeout;
    }

    public void setAcceptOnTimeout(String acceptOnTimeout) {
        this.acceptOnTimeout = acceptOnTimeout;
    }

    public String getWaitForResponse() {
        return waitForResponse;
    }

    public void setWaitForResponse(String waitForResponse) {
        this.waitForResponse = waitForResponse;
    }
}
