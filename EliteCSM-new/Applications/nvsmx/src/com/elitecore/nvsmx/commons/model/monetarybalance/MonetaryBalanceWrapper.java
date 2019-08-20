package com.elitecore.nvsmx.commons.model.monetarybalance;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.service.Service;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.nvsmx.system.util.NVSMXUtil;

public class MonetaryBalanceWrapper implements Comparable{

    private String id;
    private String subscriberId;
    private String serviceId;
    private double availBalance;
    private double initialBalance;
    private double totalReservation;
    private long validFromDate;
    private long validToDate;
    private String currency;
    private long lastUpdateTime;
    private String parameter1;
    private String parameter2;
    private String validFromDateStr;
    private String validToDateStr;
    private String totalReservationStr;
    private String serviceName;
    private String availBalanceStr;
    private String initialBalanceStr;
    private long creditLimit;
    private String creditLimitStr;
    private String creditUsageStr;

    public String getAvailBalanceStr() {
        return availBalanceStr;
    }

    public void setAvailBalanceStr(String availBalanceStr) {
        this.availBalanceStr = availBalanceStr;
    }

    public String getInitialBalanceStr() {
        return initialBalanceStr;
    }

    public void setInitialBalanceStr(String initialBalanceStr) {
        this.initialBalanceStr = initialBalanceStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public double getAvailBalance() {
        return availBalance;
    }

    public void setAvailBalance(double availBalance) {
        this.availBalance = availBalance;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public double getTotalReservation() {
        return totalReservation;
    }

    public void setTotalReservation(double totalReservation) {
        this.totalReservation = totalReservation;
    }

    public long getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(long validFromDate) {
        this.validFromDate = validFromDate;
    }

    public long getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(long validToDate) {
        this.validToDate = validToDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getParameter1() {
        return parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(long creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getCreditLimitStr() {
        return creditLimitStr;
    }

    public void setCreditLimitStr(String creditLimitStr) {
        this.creditLimitStr = creditLimitStr;
    }

    public String getCreditUsageStr() {
        return creditUsageStr;
    }

    public void setCreditUsageStr(String creditUsageStr) {
        this.creditUsageStr = creditUsageStr;
    }

    public static class MonetaryBalanceWrapperBuilder {
        private MonetaryBalanceWrapper monetaryBalanceWrapper;

        public MonetaryBalanceWrapperBuilder() {
            monetaryBalanceWrapper = new MonetaryBalanceWrapper();
        }

        public MonetaryBalanceWrapper build() {
            return monetaryBalanceWrapper;
        }

        public MonetaryBalanceWrapperBuilder withMonetaryBalanceData(MonetaryBalance data, PolicyRepository policyRepository) {
            monetaryBalanceWrapper.id = data.getId();
            monetaryBalanceWrapper.subscriberId = data.getSubscriberId();
            monetaryBalanceWrapper.serviceId = data.getServiceId();
            monetaryBalanceWrapper.availBalance = data.getAvailBalance();
            monetaryBalanceWrapper.initialBalance = data.getInitialBalance();
            monetaryBalanceWrapper.totalReservation = data.getTotalReservation();
            monetaryBalanceWrapper.validFromDate = data.getValidFromDate();
            monetaryBalanceWrapper.validToDate = data.getValidToDate();
            monetaryBalanceWrapper.validFromDateStr = NVSMXUtil.simpleDateFormatPool.get().format(data.getValidFromDate());
            monetaryBalanceWrapper.validToDateStr = NVSMXUtil.simpleDateFormatPool.get().format(data.getValidToDate());
            monetaryBalanceWrapper.currency = data.getCurrency();
            monetaryBalanceWrapper.lastUpdateTime = data.getLastUpdateTime();
            monetaryBalanceWrapper.parameter1 = data.getParameter1();
            monetaryBalanceWrapper.parameter2 = data.getParameter2();
            monetaryBalanceWrapper.availBalanceStr = String.format("%.6f",data.getAvailBalance());
            monetaryBalanceWrapper.initialBalanceStr = String.format("%.6f",data.getInitialBalance());
            monetaryBalanceWrapper.totalReservationStr = String.format("%.6f",data.getTotalReservation());
            monetaryBalanceWrapper.creditLimit = data.getCreditLimit();
            monetaryBalanceWrapper.creditLimitStr = String.valueOf(data.getCreditLimit());
            monetaryBalanceWrapper.creditUsageStr = String.format("%.6f",data.getCreditLimit()==0?0:(data.getAvailBalance()>0?0:Math.abs(data.getAvailBalance())));

            if(Strings.isNullOrBlank(data.getServiceId())){
                monetaryBalanceWrapper.setServiceName(CommonConstants.ALL_SERVICE_DISPLAY_VALUE);
            } else {
                Service serviceData = policyRepository.getService().byId(data.getServiceId());
                if(serviceData!=null){
                    monetaryBalanceWrapper.setServiceName(serviceData.getName());
                }
            }
            return this;
        }

    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof MonetaryBalanceWrapper == false) {
            return 0;
        }
        if(this.getValidToDate() > ((MonetaryBalanceWrapper) o).getValidToDate()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}