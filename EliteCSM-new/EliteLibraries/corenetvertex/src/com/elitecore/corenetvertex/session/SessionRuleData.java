package com.elitecore.corenetvertex.session;

import com.elitecore.corenetvertex.pm.pkg.imspackage.MediaType;

import java.sql.Timestamp;

/**
 * This class is used to fetch session information of ims pccrule
 * Created by dhyani on 19/4/17.
 */
public class SessionRuleData {

    private String id;
    private String sessionId;
    private String afSessionId;
    private String gatewayAddress;
    private String mediaType;
    private String pccRule;
    private String mediaComponentNumber;
    private String flowNumber;
    private String upLinkFlow;
    private String downLinkFlow;
    private Timestamp startTime;
    private Timestamp modifiedTime;
    private String additionalParameter;
    private String qciValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAfSessionId() {
        return afSessionId;
    }

    public void setAfSessionId(String afSessionId) {
        this.afSessionId = afSessionId;
    }

    public String getGatewayAddress() {
        return gatewayAddress;
    }

    public void setGatewayAddress(String gatewayAddress) {
        this.gatewayAddress = gatewayAddress;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getPccRule() {
        return pccRule;
    }

    public void setPccRule(String pccRule) {
        this.pccRule = pccRule;
    }

    public String getMediaComponentNumber() {
        return mediaComponentNumber;
    }

    public void setMediaComponentNumber(String mediaComponentNumber) {
        this.mediaComponentNumber = mediaComponentNumber;
    }

    public String getFlowNumber() {
        return flowNumber;
    }

    public void setFlowNumber(String flowNumber) {
        this.flowNumber = flowNumber;
    }

    public String getUpLinkFlow() {
        return upLinkFlow;
    }

    public void setUpLinkFlow(String upLinkFlow) {
        this.upLinkFlow = upLinkFlow;
    }

    public String getDownLinkFlow() {
        return downLinkFlow;
    }

    public void setDownLinkFlow(String downLinkFlow) {
        this.downLinkFlow = downLinkFlow;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getAdditionalParameter() {
        return additionalParameter;
    }

    public void setAdditionalParameter(String additionalParameter) {
        this.additionalParameter = additionalParameter;
    }

    public String getQciValue() {
        return qciValue;
    }

    public void setQciValue(String qciValue) {
        this.qciValue = qciValue;
    }
}
