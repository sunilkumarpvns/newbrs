package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import com.elitecore.nvsmx.ws.subscription.data.ValidationData;

import javax.xml.bind.annotation.XmlTransient;

public class ChangeBillDayWSRequest extends BaseWebServiceRequest {

    private String subscriberId;
    private String alternateId;
    private Integer nextBillDate;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;


    public ChangeBillDayWSRequest(String subscriberId, String alternateId, Integer nextBillDate, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.subscriberId = subscriberId;
        this.alternateId = alternateId;
        this.nextBillDate = nextBillDate;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public Integer getNextBillDate() {
        return nextBillDate;
    }

    public void setNextBillDate(Integer nextBillDate) {
        this.nextBillDate = nextBillDate;
    }

    @Override
    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    @Override
    public String getParameter2() {
        return parameter2;
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

        builder.append("Subscriber Id", subscriberId);
        builder.append("Alternate Id", alternateId);
        builder.append("Usage Limit", nextBillDate);
        builder.append("Parameter 1", getParameter1());
        builder.append("Parameter 2", getParameter2());

        return builder.toString();
    }

    @XmlTransient
    @Override
    public String getWebServiceName() {
        return webServiceName;
    }

    @XmlTransient
    @Override
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }


    public ValidationData validateParameters() {
        String validationMsg = validateMissingParameters();
        if (Strings.isNullOrBlank(validationMsg) == false) {
            return new ValidationData(ResultCode.INPUT_PARAMETER_MISSING, "Mandatory parameters(" + validationMsg + ") missing.");
        }

        if (nextBillDate <= 0 || nextBillDate > 28) {
            return new ValidationData(ResultCode.INVALID_INPUT_PARAMETER,
                    "Invalid Next Bill Date. Next Bill Date must be between 1 to 28");
        }

        return null;
    }

    private String validateMissingParameters() {
        StringBuilder message = new StringBuilder("");

        if (Strings.isNullOrBlank(subscriberId) && Strings.isNullOrBlank(alternateId)) {
            message.append("subscriberId or alternateId, ");
        }

        if (nextBillDate == null) {
            message.append("nextBillDate, ");
        }

        if (message.toString().trim().length() == 0) {
            return message.toString().trim();
        } else {
            return message.toString().substring(0, message.length() - 2);
        }
    }
}