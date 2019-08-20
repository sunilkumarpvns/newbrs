package com.elitecore.nvsmx.ws.subscription;

import com.elitecore.corenetvertex.util.commons.jaxb.adapter.DoubleToStringAdapter;
import com.elitecore.nvsmx.ws.subscription.response.BalanceEnquiryResponse;
import com.elitecore.nvsmx.ws.subscription.response.MonetaryBalanceInquiryResponse;
import com.elitecore.nvsmx.ws.subscription.response.MonetoryOperationResponse;
import com.elitecore.nvsmx.ws.subscription.response.NonMonitoryBalanceInquiryResponse;
import com.elitecore.nvsmx.ws.subscription.response.RnCBalanceEnquiryResponse;
import com.elitecore.nvsmx.ws.subscription.response.UMQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.UpdateCreditLimitResponse;
import com.elitecore.nvsmx.ws.subscription.response.UpdateMonetaryBalanceResponse;
import com.elitecore.nvsmx.ws.util.LongToStringAdapter;
import com.elitecore.nvsmx.ws.util.StringToIntegerAdapterForReauth;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public interface IUsageMonitoringWS {

	public static final String WS_GET_BALANCE = "wsGetBalance";
	public static final String WS_GET_NONMONITORY_BALANCE = "wsGetNonMonitoryBalance";
	public static final String WS_LIST_USAGE_MONITORING_INFORMATION = "wsListUsageMonitoringInformation";
	public static final String WS_GET_MONETARY_BALANCE = "wsGetMonetaryBalance";
	public static final String WS_ADD_MONETARY_BALANCE = "wsAddMonetaryBalance";
	public static final String WS_UPDATE_MONETARY_BALANCE = "wsUpdateMonetaryBalance";
	public static final String WS_GET_RNC_BALANCE = "wsGetRnCBalance";
	public static final String WS_UPDATE_CREDIT_LIMIT = "wsUpdateCreditLimit";

	@WebMethod(operationName= WS_LIST_USAGE_MONITORING_INFORMATION)
	public UMQueryResponse wsListUsageMonitoringInformation(
			@WebParam(name="subscriberId")String subscriberId ,
			@WebParam(name="alternateId")String alternateId, 
			@WebParam(name="packageId")String packageId,
			@WebParam(name="packageName")String packageName,
			@WebParam(name="quotaProfileId")String quotaProfileId,
			@WebParam(name="quotaProfileName")String quotaProfileName,
			@WebParam(name="serviceId")String serviceId,
			@WebParam(name="serviceName")String serviceName,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod(operationName=WS_GET_BALANCE)
	public BalanceEnquiryResponse wsGetBalance(
			@WebParam(name="subscriberId")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="packageId")String packageId,
			@WebParam(name="packageName")String packageName,
			@WebParam(name="subscriptionId")String subscriptionId,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod(operationName=WS_GET_NONMONITORY_BALANCE)
	public NonMonitoryBalanceInquiryResponse wsGetNonMonitoryBalance(
			@WebParam(name="subscriberId")String subscriberId,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name="packageId")String packageId,
			@WebParam(name="packageName")String packageName,
			@WebParam(name="subscriptionId")String subscriptionId,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod(operationName = WS_GET_MONETARY_BALANCE)
	public MonetaryBalanceInquiryResponse wsGetMonetaryBalance(
			@WebParam(name = "subscriberId")String subscriberId,
			@WebParam(name = "alternateId")String alternateId,
			@WebParam(name = "serviceId")String serviceId,
			@WebParam(name = "serviceName")String serviceName,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod(operationName = WS_ADD_MONETARY_BALANCE)
	public MonetoryOperationResponse wsAddMonetaryBalance(
			@WebParam(name = "subscriberId")String subscriberId,
			@WebParam(name = "alternateId")String alternateId,
			@WebParam(name = "serviceId")String serviceId,
			@WebParam(name = "serviceName")String serviceName,
			@WebParam(name = "totalBalance")@XmlJavaTypeAdapter(DoubleToStringAdapter.class) Double totalBalance,
			@WebParam(name = "validFromDate")@XmlJavaTypeAdapter(LongToStringAdapter.class) Long validFromDate,
			@WebParam(name = "validToDate")@XmlJavaTypeAdapter(LongToStringAdapter.class) Long validToDate,
			@WebParam(name = "transactionId")String transactionId,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod(operationName = WS_UPDATE_MONETARY_BALANCE)
	public UpdateMonetaryBalanceResponse wsUpdateMonetaryBalance(
			@WebParam(name = "subscriberId")String subscriberId,
			@WebParam(name = "alternateId")String alternateId,
			@WebParam(name="updateAction")@XmlJavaTypeAdapter(StringToIntegerAdapterForReauth.class) Integer updateAction,
			@WebParam(name="balanceId")String balanceId,
			@WebParam(name = "serviceId")String serviceId,
			@WebParam(name = "serviceName")String serviceName,
			@WebParam(name = "amount")@XmlJavaTypeAdapter(DoubleToStringAdapter.class) Double amount,
			@WebParam(name = "operation")String operation,
			@WebParam(name = "validToDate")@XmlJavaTypeAdapter(LongToStringAdapter.class) Long validToDate,
			@WebParam(name = "reason")String reason,
			@WebParam(name = "transactionId")String transactionId,
			@WebParam(name="parameter1")String parameter1,
			@WebParam(name="parameter2")String parameter2);

	@WebMethod(operationName = WS_GET_RNC_BALANCE)
	public RnCBalanceEnquiryResponse wsGetRnCBalance(
			@WebParam(name="subscriberId")String subscriberId ,
			@WebParam(name="alternateId")String alternateId,
			@WebParam(name = "packageId") String packageId,
			@WebParam(name = "packageName") String packageName,
			@WebParam(name="subscriptionId")String subscriptionId,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_UPDATE_CREDIT_LIMIT)
	public UpdateCreditLimitResponse wsUpdateCreditLimit(
			@WebParam(name = "subscriberId")String subscriberId,
			@WebParam(name = "alternateId")String alternateId,
			@WebParam(name = "creditLimit")String creditLimit,
			@WebParam(name = "applicableBillingCycle")String applicableBillingCycle,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	}
