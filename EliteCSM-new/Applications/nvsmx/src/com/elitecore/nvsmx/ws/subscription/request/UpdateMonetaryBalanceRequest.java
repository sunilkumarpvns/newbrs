package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import com.elitecore.nvsmx.ws.subscription.data.ValidationData;

public class UpdateMonetaryBalanceRequest extends BaseWebServiceRequest {
    private final String subscriberId;
    private final String alternateId;
    private final Integer updateAction;
    private final String balanceId;
    private final String serviceId;
    private final String serviceName;
    private final Double amount;
    private final String operation;
    private final String reason;
    private final String transactionId;
    private final String parameter1;
    private final String parameter2;
    private final String webserviceName;
    private final String webServiceMethodName;

    public UpdateMonetaryBalanceRequest(String subscriberId, String alternateId, Integer updateAction, String balanceId,
                                        String serviceId, String serviceName, Double amount, String operation,
                                        Long validToDate, String reason,String transactionId, String parameter1,
                                        String parameter2, String webserviceName, String webServiceMethodName) {
        this.subscriberId = subscriberId;
        this.alternateId = alternateId;
        this.updateAction = updateAction;
        this.balanceId = balanceId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.amount = amount;
        this.operation = operation;
        this.reason = reason;
        this.transactionId = transactionId;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webserviceName = webserviceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    @Override
    public String toString(){
        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE);

        toStringBuilder.append(super.toString());
        toStringBuilder.append("Primary Id", subscriberId);
        toStringBuilder.append("Secondary Id", alternateId);
        toStringBuilder.append("Update Action", updateAction);
        toStringBuilder.append("Balance Id", balanceId);
        toStringBuilder.append("Service Id", serviceId);
        toStringBuilder.append("Service Name", serviceName);
        toStringBuilder.append("Amount", amount);
        toStringBuilder.append("Operation", operation);
        toStringBuilder.append("Reason", reason);
        toStringBuilder.append("Transaction Id", transactionId);
        toStringBuilder.append("Parameter1", parameter1);
        toStringBuilder.append("Parameter2", parameter2);
        return toStringBuilder.toString();
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public Integer getUpdateAction() {
        return updateAction;
    }

    public String getBalanceId() {
        return balanceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Double getAmount() {
        return amount;
    }

    public String getOperation() {
        return operation;
    }

    public String getReason() {
        return reason;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public String getWebServiceName() {
        return webserviceName;

    }

    @Override
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    @Override
    public String getParameter1() {
        return parameter1;
    }

    @Override
    public String getParameter2() {
        return parameter2;
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }

    private String validateMissingParameters(){
        StringBuilder message = new StringBuilder("");

        if(Strings.isNullOrBlank(subscriberId) && Strings.isNullOrBlank(alternateId)){
            message.append("subscriberId or alternateId, ");
        }

        if(updateAction==null){
            message.append("updateAction, ");
        }

        if(amount==null){
            message.append("amount, ");
        }

        if(amount!=null && Strings.isNullOrBlank(operation)){
            message.append("operation, ");
        }

        if(message.toString().trim().length()==0){
            return message.toString().trim();
        } else {
            return message.toString().substring(0,message.length()-2);
        }
    }

    public ValidationData validateParameters(){

        String validationMsg = validateMissingParameters();
        if (Strings.isNullOrBlank(validationMsg) == false) {
            return new ValidationData(ResultCode.INPUT_PARAMETER_MISSING, "Mandatory parameters(" + validationMsg + ") missing.");
        }

        if (amount != null && amount <= 0) {
            return new ValidationData(ResultCode.INVALID_INPUT_PARAMETER,
                    "Amount can not be negative or zero.");
        }

        return null;
    }
}
