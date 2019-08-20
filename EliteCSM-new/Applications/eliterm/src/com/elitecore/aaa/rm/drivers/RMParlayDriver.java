package com.elitecore.aaa.rm.drivers;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.csapi.www.schema.parlayx.common.v3_1.ChargingInformation;
import org.csapi.www.schema.parlayx.common.v3_1.PolicyException;
import org.csapi.www.wsdl.parlayx.payment.amount_charging.v3_1._interface.AmountCharging;
import org.csapi.www.wsdl.parlayx.payment.amount_charging.v3_1.service.AmountChargingService;
import org.csapi.www.wsdl.parlayx.payment.amount_charging.v3_1.service.AmountChargingServiceLocator;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.rm.drivers.conf.RMParlayDriverConfiguration;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.BaseDriver;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorPolicyData;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;

public class RMParlayDriver extends BaseDriver implements RMChargingDriver{
	private static final String MODULE = "RM-PRLY-DRVR";
	private String strParlayChargingUrl;
	
	private RMParlayDriverConfiguration driverConfiguration;
	
	private AmountChargingService parlayAmountChargingService ;
	private AmountCharging amountCharging;
	private Map<String,String> dummyMapRequest;
	private boolean isDummy = false;
	/* PARLAY Constants */
	private static final String URI = "USER-IDENTIFIER";
	private static final String AMOUNT = "AMOUNT";
	private static final String CODE = "CODE";
	private static final String CURRENCY = "CURRENCY";
	private static final String DESCRIPTION = "DESCRIPTION";
	
	public RMParlayDriver(AAAServerContext serverContext, RMParlayDriverConfiguration driverConfiguration) {
		super(serverContext);
		this.driverConfiguration = driverConfiguration;
		this.strParlayChargingUrl = "http://" + driverConfiguration.getWebServiceAddress() + "/parlayx30/payment/AmountCharging";
		this.parlayAmountChargingService = new AmountChargingServiceLocator(); 
		((AmountChargingServiceLocator) parlayAmountChargingService).setAmountChargingEndpointAddress(this.strParlayChargingUrl );
		((AmountChargingServiceLocator) parlayAmountChargingService).setAmountChargingWSDDServiceName(driverConfiguration.getParlayServiceName());
		dummyMapRequest = new HashMap<String, String>();
	}
	
	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		TranslatorPolicyData policyData = ((AAAServerContext)getServerContext()).getServerConfiguration().getTranslationMappingConfiguration().getTranslatorPolicyData(driverConfiguration.getTranslationMappingName());
		if(policyData.getIsDummyResponse()){
			isDummy = true;
			Map<String,String> dummyMap =  policyData.getDummyResponseMap();
			for(Entry<String, String> entry : dummyMap.entrySet()){
				dummyMapRequest.put(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public int getType() {
		return driverConfiguration.getDriverType().value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RM_PARLAY_DRIVER.name();
	}

	@Override
	public String getName() {		
		return driverConfiguration.getDriverName();
	}

	@Override
	public void scan() {
	}
	
	@Override
	protected int getStatusCheckDuration() {
		return 0;
	}
	private String getValue(Map<String,String>map,String key){
		String val = map.get(key);
		if(val != null)
			return val;
		return "";
	}
	
	@Override
	public void handleRequest(ServiceRequest request, ServiceResponse response) throws DriverProcessFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Driver process started.");
		
		Map<String,String> mapRequest = new HashMap<String, String>();
		if(!isDummy){
			TranslatorParams params = new TranslatorParamsImpl(request, mapRequest);
			try {
				TranslationAgent.getInstance().translate(driverConfiguration.getTranslationMappingName(), params, TranslatorConstants.REQUEST_TRANSLATION);
				try {
					amountCharging = parlayAmountChargingService.getAmountCharging();
					URI endUserIdentifier= new URI(getValue(mapRequest, URI)) ;
					ChargingInformation chargeObj = new ChargingInformation();				
					BigDecimal bd = new BigDecimal(getValue(mapRequest, AMOUNT));
					chargeObj.setAmount(bd);
					chargeObj.setCode(getValue(mapRequest, CODE));
					chargeObj.setCurrency(getValue(mapRequest, CURRENCY));
					chargeObj.setDescription(getValue(mapRequest, DESCRIPTION));
					amountCharging.chargeAmount(endUserIdentifier, chargeObj, "referenceCode");
				} catch (MalformedURIException e) {
					throw new DriverProcessFailedException(e.getMessage());
				} catch (ServiceException e) {
					throw new DriverProcessFailedException(e.getMessage());
				} catch (PolicyException e) {
					throw new DriverProcessFailedException(e.getMessage());
				} catch (org.csapi.www.schema.parlayx.common.v3_1.ServiceException e) {
					throw new DriverProcessFailedException(e.getMessage());
				} catch (RemoteException e) {
					throw new DriverProcessFailedException(e.getMessage());
				}
			} catch (TranslationFailedException e) {
				throw new DriverProcessFailedException(e.getMessage());
			}			
		}else{ //Dummy Response							
			((RadServiceResponse)response).setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
		}		
		
	}
}
