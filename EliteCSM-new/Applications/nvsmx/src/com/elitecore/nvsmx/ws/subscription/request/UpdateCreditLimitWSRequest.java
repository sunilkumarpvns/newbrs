package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import com.elitecore.nvsmx.ws.subscription.data.ValidationData;
import com.elitecore.nvsmx.ws.util.ConvertStringToDigit;

import javax.xml.bind.annotation.XmlTransient;

public class UpdateCreditLimitWSRequest extends BaseWebServiceRequest {

    private static final String MODULE = "UPDATE_CL";

    private String subscriberId;
    private String alternateId;
    private String creditLimit;
    private String applicableBillingCycle;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;


    public UpdateCreditLimitWSRequest(String subscriberId, String alternateId, String creditLimit, String applicableBillingCycle, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.subscriberId = subscriberId;
        this.alternateId = alternateId;
        this.creditLimit = creditLimit;
        this.applicableBillingCycle = applicableBillingCycle;
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

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getApplicableBillingCycle() {
        return applicableBillingCycle;
    }

    public void setApplicableBillingCycle(String nextBillingCycleCreditLimit) {
        this.applicableBillingCycle = nextBillingCycleCreditLimit;
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
        builder.append("Usage Limit", creditLimit);
        builder.append("Applicable Billing Cycle", applicableBillingCycle);
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
        if(Strings.isNullOrBlank(validationMsg)==false){
            return new ValidationData(ResultCode.INPUT_PARAMETER_MISSING, "Mandatory parameters("+validationMsg+") missing.");
        }

        Long creditLimit = null;
        Integer applicableBillingCycle = null;
        try {
            if(Strings.isNullOrBlank(this.creditLimit) == false){
                creditLimit = ConvertStringToDigit.convertStringToLong(this.creditLimit);

                if(creditLimit> CommonConstants.MONETARY_VALUE_LIMIT){
                    return new ValidationData(ResultCode.INVALID_INPUT_PARAMETER,"Invalid Credit Limit value: "+creditLimit+ ". Value must not be greater than 999999999");
                }

                if(creditLimit<=0){
                    return new ValidationData(ResultCode.INVALID_INPUT_PARAMETER,"Credit Limit should be between 1 and 999999999");
                }
            }
            if(Strings.isNullOrBlank(this.applicableBillingCycle) == false){

                if(Strings.isNullOrBlank(this.applicableBillingCycle) == false){

                }
                applicableBillingCycle = ConvertStringToDigit.convertStringToInt(this.applicableBillingCycle);

                if(applicableBillingCycle < 0 || applicableBillingCycle > 1){
                    return new ValidationData(ResultCode.INVALID_INPUT_PARAMETER,
                            "Possible values for Applicable Billing Cycle are : 0 for Current Billing Cycle, 1 for Upcoming Billing Cycle.");
                }
            }
        } catch (OperationFailedException e) {
            LogManager.getLogger().error(MODULE, "Error in validating input parameters : "+e.getMessage());
            return new ValidationData(ResultCode.INVALID_INPUT_PARAMETER, e.getMessage());
        }

        return null;
    }

    private String validateMissingParameters() {
        StringBuilder message = new StringBuilder("");

        if(Strings.isNullOrBlank(subscriberId) && Strings.isNullOrBlank(alternateId)){
            message.append("subscriberId or alternateId, ");
        }

        if(Strings.isNullOrBlank(creditLimit)){
            message.append("creditLimit, ");
        }

        if(message.toString().trim().length()==0){
            return message.toString().trim();
        } else {
            return message.toString().substring(0,message.length()-2);
        }
    }
}
