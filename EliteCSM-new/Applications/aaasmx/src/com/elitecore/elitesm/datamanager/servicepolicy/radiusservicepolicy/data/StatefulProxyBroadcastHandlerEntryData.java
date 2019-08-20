package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import com.elitecore.commons.base.Strings;
import net.sf.json.JSONObject;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"ruleset", "serverGroupName", "translationMappingName", "script", "acceptOnTimeout", "waitForResponse"})
public class StatefulProxyBroadcastHandlerEntryData {

    private String ruleset;
    private String serverGroupName;
    private String translationMappingName;
    private String script;
    private String acceptOnTimeout;
    private String waitForResponse;

    @XmlElement(name = "ruleset")
    public String getRuleset() {
        return ruleset;
    }

    public void setRuleset(String ruleset) {
        this.ruleset = ruleset;
    }

    @XmlElement(name = "esi-group-name")
    public String getServerGroupName() {
        return serverGroupName;
    }

    public void setServerGroupName(String serverGroupName) {
        this.serverGroupName = serverGroupName;
    }

    @XmlElement(name = "translation-mapping")
    public String getTranslationMappingName() {
        return translationMappingName;
    }

    public void setTranslationMappingName(String translationMappingName) {
        this.translationMappingName = translationMappingName;
    }

    @XmlElement(name = "script")
    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @XmlAttribute(name = "accept-on-timeout")
    public String getAcceptOnTimeout() {
        return acceptOnTimeout;
    }

    public void setAcceptOnTimeout(String acceptOnTimeout) {
        this.acceptOnTimeout = acceptOnTimeout;
    }

    @XmlAttribute(name = "wait-for-response")
    public String getWaitForResponse() {
        return waitForResponse;
    }

    public void setWaitForResponse(String waitForResponse) {
        this.waitForResponse = waitForResponse;
    }

    public JSONObject toJson() {

        JSONObject jsonObject = new JSONObject();

        if ( Strings.isNullOrEmpty(ruleset) == false ){
            jsonObject.put("Ruleset", ruleset);
        }
        jsonObject.put("Server Group Name",serverGroupName);
        jsonObject.put("Translation Mapping Name",translationMappingName);
        jsonObject.put("Script", script);
        jsonObject.put("Accept On Timeout",acceptOnTimeout);
        jsonObject.put("Wait For Response",waitForResponse);
        return jsonObject;
    }
}
