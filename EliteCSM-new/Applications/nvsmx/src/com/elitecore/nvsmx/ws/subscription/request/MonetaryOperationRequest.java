package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import com.elitecore.nvsmx.ws.subscription.data.ValidationData;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;

public class MonetaryOperationRequest extends BaseWebServiceRequest {

    private String subscriberId;
    private String alternateId;
    private String serviceId;
    private String serviceName;
    private Double totalBalance;
    private Long validFromDate;
    private Long validToDate;
    private String transactionId;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    public MonetaryOperationRequest(String subscriberId, String alternateId, String serviceId, String serviceName, Double totalBalance, Long validFromDate, Long validToDate,String transactionId, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.subscriberId = subscriberId;
        this.alternateId = alternateId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.totalBalance = totalBalance;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
        this.transactionId=transactionId;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    public String getParameter1() {
        return parameter1;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Double getTotalBalance() {
        return totalBalance;
    }

    public Long getValidFromDate() {
        return validFromDate;
    }

    public Long getValidToDate() {
        return validToDate;
    }

    public String getTransactionId() { return transactionId; }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public void setValidFromDate(Long validFromDate) {
        this.validFromDate = validFromDate;
    }

    public void setValidToDate(Long validToDate) {
        this.validToDate = validToDate;
    }

    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

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

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE);

        toStringBuilder.append(super.toString());
        toStringBuilder.append("Primary Id", subscriberId);
        toStringBuilder.append("Secondary Id", alternateId);
        toStringBuilder.append("Service Id", serviceId);
        toStringBuilder.append("Service Name", serviceName);
        toStringBuilder.append("Total Balance", totalBalance);
        toStringBuilder.append("Valid From Date", validFromDate);
        toStringBuilder.append("Valid To Date", validToDate);
        toStringBuilder.append("transactionId",transactionId);
        toStringBuilder.append("Parameter1", parameter1);
        toStringBuilder.append("Parameter2", parameter2);
        return toStringBuilder.toString();
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }

    private String validateMissingParameters() {
        StringBuilder message = new StringBuilder("");

        if (Strings.isNullOrBlank(subscriberId) && Strings.isNullOrBlank(alternateId)) {
            message.append("subscriberId or alternateId, ");
        }
        if (totalBalance == null) {
            message.append("totalBalance, ");
        }
        if (message.toString().trim().length() == 0) {
            return message.toString().trim();
        } else {
            return message.toString().substring(0, message.length() - 2);
        }
    }

    public ValidationData validateAddBalanceOperationParameters() {

        String validationMsg = validateMissingParameters();
        if (Strings.isNullOrBlank(validationMsg) == false) {
            return new ValidationData(ResultCode.INPUT_PARAMETER_MISSING, "Mandatory missing parameters(" + validationMsg + ").");
        }

        if (totalBalance < 0) {
            return new ValidationData(ResultCode.INVALID_INPUT_PARAMETER,
                    "totalBalance can not be negative.");
        }

        if (validToDate != null) {
            if (validFromDate != null && validFromDate >= validToDate) {
                return new ValidationData(ResultCode.INVALID_INPUT_PARAMETER,
                        "validToDate is invalid, it must always be later than validFromDate.");
            }

            if (validToDate <= System.currentTimeMillis()) {
                return new ValidationData(ResultCode.INVALID_INPUT_PARAMETER, "validToDate is invalid, it must always be later than current date.");
            }
        }
        if (isRequestForMainBalance()) {
            if (isValidToAndValidFromDateExist() || isValidToAndValidFromDateNotExist()) {
                return null;
            } else {
                return new ValidationData(ResultCode.INPUT_PARAMETER_MISSING, "For All-Service Both validToDate and validFromDate are required if any date is present");
            }
        } else {
            if (Objects.isNull(validToDate) || Objects.isNull(validFromDate)) {
                return new ValidationData(ResultCode.INPUT_PARAMETER_MISSING, "validToDate or validFromDate is missing.");
            }
        }



        return null;
    }

    private boolean isRequestForMainBalance() {
        return Strings.isNullOrEmpty(serviceId) && Strings.isNullOrEmpty(serviceName);
    }

    private boolean isValidToAndValidFromDateExist() {
        return Objects.isNull(validToDate) == false && Objects.isNull(validFromDate) == false;
    }

    private boolean isValidToAndValidFromDateNotExist() {
        return Objects.isNull(validToDate) && Objects.isNull(validFromDate);
    }
}


