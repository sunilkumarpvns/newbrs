
package com.elitecore.aaa.rm.service.ippool.handlers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.rm.conf.RMIPPoolConfiguration;
import com.elitecore.aaa.rm.drivers.DatabaseIPPoolServiceDriver;
import com.elitecore.aaa.rm.drivers.DatabaseIPPoolServiceDriver.PoolDetail;
import com.elitecore.aaa.rm.drivers.DatabaseIPPoolServiceDriver.TBLMIPPOOL_class;
import com.elitecore.aaa.rm.service.ippool.RMIPPoolService;
import com.elitecore.aaa.rm.service.ippool.RMIPPoolServiceContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;


public class RMIPPoolServiceHandlerImpl implements RMIPPoolServiceHandler{

	private static final String MODULE = "RM-IPPool-Handler";
	private RMIPPoolConfiguration ippoolServiceConfiguration;
	private RMIPPoolServiceContext serviceContext;
	private DatabaseIPPoolServiceDriver iPPoolDriver;
	private boolean isDefaultIPPoolEnabled;

	public RMIPPoolServiceHandlerImpl(RMIPPoolServiceContext serviceContext){
		this.serviceContext=serviceContext;
		ippoolServiceConfiguration=serviceContext.getIPPoolConfiguration();
		iPPoolDriver=new DatabaseIPPoolServiceDriver(serviceContext);
	}

	@Override
	public void init() throws InitializationFailedException {

		iPPoolDriver.init();
		

		if(ippoolServiceConfiguration.getDefaultIPPoolName() != null && ippoolServiceConfiguration.getDefaultIPPoolName().trim().length() > 0){
			isDefaultIPPoolEnabled = true;
		}
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		ippoolServiceConfiguration=serviceContext.getIPPoolConfiguration();
		iPPoolDriver.reInit();
	}

	public void handleIPAllocateRequest(RadServiceRequest request,RadServiceResponse response)     {
		LogManager.getLogger().debug(MODULE,"ip address allocate process started");
		List<IRadiusAttribute> framedPoolAttribute =(List<IRadiusAttribute>) request.getRadiusAttributes(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_FRAMED_POOL_NAME);
		ArrayList<String> poolNameList = new ArrayList<String>();
		if(framedPoolAttribute!=null && !framedPoolAttribute.isEmpty()){
			
			for (int i=0 ; i<framedPoolAttribute.size() ; i++) {

				String strPoolName = framedPoolAttribute.get(i).getStringValue();
				
				if (strPoolName.contains("[") && strPoolName.contains("]")){
					List<String> poolList = getPoolListFrom(strPoolName);
					poolNameList.addAll(poolList);
					} else {
					poolNameList.add(strPoolName);
					}
				}
			
			if(poolNameList.isEmpty()){
				LogManager.getLogger().info(MODULE,"framed-poolname(21067:145) is empty in request, could not allocate ip address");
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
				response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				response.setFurtherProcessingRequired(false);
				return;				
			}	
			
		}else{
			if(isDefaultIPPoolEnabled){				
				String defaultPoolName = ippoolServiceConfiguration.getDefaultIPPoolName();
				
				if (defaultPoolName.contains("[") && defaultPoolName.contains("]")){
					List<String> poolList = getPoolListFrom(defaultPoolName);
					poolNameList.addAll(poolList);
				} else {
					poolNameList.add(defaultPoolName);
				}
				LogManager.getLogger().debug(MODULE,"framed-poolname(21067:145) does not contain received in request, considering default IP Pool : " + poolNameList);
			}else{
				LogManager.getLogger().info(MODULE,"framed-poolname(21067:145) does not contain received in request, could not allocate ip address");
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
				response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				response.setFurtherProcessingRequired(false);
				return;
			}
		}
		allocateIpAddress(poolNameList,request, response);
	}

	private List<String> getPoolListFrom(String poolName) {

		List<String> poolsList = new ArrayList<String>();
		int startIndex = poolName.indexOf('[', 0);
		int endIndex = poolName.indexOf(']', startIndex);

		while (startIndex != -1 && startIndex != poolName.length() - 1) {
			poolsList.add(poolName.substring(startIndex + 1, endIndex));
			startIndex = poolName.indexOf('[', endIndex);
			endIndex = poolName.indexOf(']', startIndex);
	}

		if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
			LogManager.getLogger().trace(MODULE, "Pool list: " + poolsList + " from configured pool: " + poolName);
		}
		return poolsList;
	}

	private void allocateIpAddress(ArrayList<String> strPoolName, RadServiceRequest request,RadServiceResponse response)  {

		PoolDetail poolDetail=null;
		try{ 

			if(!iPPoolDriver.isAlive()){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,"IPPool Service Driver is not alive .");
				}
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DATABASE_GENERIC, 
						MODULE, "IPPool Service Driver is not alive .", 0, 
						"IPPool Service Driver is not alive.");
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
				throw new SQLException("IPPool Service Driver is not alive");
			}


			poolDetail=iPPoolDriver.getPoolDetail(strPoolName,request,response);

			if(!response.isFurtherProcessingRequired()){
				return;
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info (MODULE,"Pool selected name = " + poolDetail.getPoolName() + ",pool id = "+ poolDetail.getPoolId() +", ipaddress = " +poolDetail.getIPAddress());
			}

			IRadiusAttribute classAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CLASS);
			classAttribute.setStringValue("poolname ="+poolDetail.getPoolName()+",poolid="+poolDetail.getPoolId()+",poolserialnumber="+poolDetail.getPoolSerialNumber());
			response.addAttribute(classAttribute);

			IRadiusAttribute framedIPAddress = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS);
			framedIPAddress.setStringValue(poolDetail.getIPAddress());
			response.addAttribute(framedIPAddress);
			
			TBLMIPPOOL_class tBLMIPPOOL_class= iPPoolDriver.getIPPoolDetail(poolDetail.getPoolName());
			if(tBLMIPPOOL_class!=null){
				AdditionalResponseAttributes additionalResponseAttributes = tBLMIPPOOL_class.getAdditionalResponseAttributes();
				if(additionalResponseAttributes!=null){
					addAdditionalAttributes(response, additionalResponseAttributes.getHardcodedAttributeMap(), new HardCodedValueProvider());
					addAdditionalAttributes(response, additionalResponseAttributes.getAttributeFromRequest(), new RequestValueProvider(request));
					addAdditionalAttributes(response, additionalResponseAttributes.getAttributeFromResponse(), new ResponseValueProvider(response));
				}
				
			}
			

		}catch(DriverProcessFailedException ex) { 

			if(ippoolServiceConfiguration.isOperationFailureEnabled()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE,"Failed to allocate IP Address. Reason: " + ex.getMessage() + ", Success On Operation Failure is enabled.");
				}
				response.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
				response.setResponseMessage(AuthReplyMessageConstant.IP_SUCCESS);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,"Failed to allocate IP Address. Reason: " + ex.getMessage() + ", Success On Operation Failure is disabled.");
				}
				response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				if(response.getResponseMessege()==null){
					response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
				}				
			}

			response.setFurtherProcessingRequired(false);

		}catch (Exception e) {
			LogManager.getLogger().warn(MODULE, "Failed to allocate IP Address. Reason: " + e.getMessage());
			response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
		}

	}// End of Method 
	private void addAdditionalAttributes(RadServiceResponse response,Map<String, List<String>> responseAttributeMap,ValueProvider valueProvider) {
		if(responseAttributeMap!=null){
			String attributeId;
			String attributeValue;
			for(Map.Entry<String, List<String>> entry:responseAttributeMap.entrySet()){
				attributeId  = entry.getKey();

				for (String attributeDefaultValue : entry.getValue()) {

				IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(attributeId);

				if(radiusAttribute!=null){
						attributeValue = valueProvider.getStringValue(attributeDefaultValue);
					if(attributeValue!=null){
						radiusAttribute.setStringValue(attributeValue);
						response.addAttribute(radiusAttribute);
					}
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Attribute not found from dictionary for Attribute-ID: "+attributeId+" while adding additional attributes");
					}
				}
			}
		}
	}
	}


	public void handleIPUpdateOrReleaseRequest(RadServiceRequest request,RadServiceResponse response,String iRequestType){
		LogManager.getLogger().debug(MODULE,"ip address update or release process started");
		boolean isSuccess = false;
		String iPPoolName=null;
		String iPPoolId = null;
		String poolSerialNumber = null;

		//here if the same key occurs in multiple attributes then the value will
		//be updated
		Collection<IRadiusAttribute> classAttributes =request.getRadiusAttributes(RadiusAttributeConstants.CLASS);
		if (classAttributes != null && classAttributes.size() > 0) {
			for(IRadiusAttribute classAttribute : classAttributes){
				StringTokenizer stk = new StringTokenizer(classAttribute.getStringValue().trim(),",");
				while (stk.hasMoreElements()) {
					StringTokenizer stkInner=new StringTokenizer(String.valueOf(stk.nextElement()),"=");
					if(stkInner.hasMoreElements()){
						String nextElement = String.valueOf(stkInner.nextElement());
						if(nextElement.trim().equals("poolname")){
							if (stkInner.hasMoreElements()){
								iPPoolName = stkInner.nextToken().trim();
							}
						}else if (nextElement.trim().equals("poolid")) {
							if (stkInner.hasMoreElements()){
								iPPoolId = stkInner.nextToken().trim();
							}
						}else if (nextElement.trim().equals("poolserialnumber")) {
							if (stkInner.hasMoreElements()){
								poolSerialNumber = stkInner.nextToken().trim();
							}
						}
					}
				}
			}
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE," class Attribute found : poolname = "+iPPoolName+",poolid ="+iPPoolId+",poolSerialNumber ="+poolSerialNumber);
			}
		}

		try{
			if(!iPPoolDriver.isAlive()){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,"ippool Driver is not alive .");
				}
				serviceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN,Alerts.DATABASE_GENERIC, 
						MODULE, "IPPool Service Driver is not alive .", 0,
						"IPPool Service Driver is not alive .");
				response.setResponseMessage(AuthReplyMessageConstant.IP_POOL_OPERATION_FAILED);
				throw new DriverProcessFailedException("IPPool Service Driver is not alive");
			}

			if (iPPoolId != null && poolSerialNumber != null) {
				int serialNumber=-1;
				try {
					serialNumber = Integer.parseInt(poolSerialNumber);
				}catch(NumberFormatException e) {
					LogManager.getLogger().warn(MODULE,"Invalid serial Number  :"+poolSerialNumber+",operation can't apply for this pool Serial Number");
				}

				isSuccess=iPPoolDriver.updateOrReleaseIP(iPPoolId, serialNumber,iRequestType,request,response);

			}else {

				IRadiusAttribute framedIPAddressAttribute = (IRadiusAttribute) request.getRadiusAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS);
				if (framedIPAddressAttribute != null) {
					String framedIpAddress=framedIPAddressAttribute.getStringValue().trim();

					isSuccess=iPPoolDriver.updateOrReleaseIP(framedIpAddress,iRequestType,request,response);

				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "IP-Pool-AVPair with (poolid, poolserialnumber) or Framed-IP-Address is must, none of the attribute found in the request.");
					}
					response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
					response.setFurtherProcessingRequired(false);
					
				}
			}

			if(!response.isFurtherProcessingRequired()){
				return;
			}


			String strStatus ="status=IP ";
			if(iRequestType==RMIPPoolService.IP_ADDRESS_RELEASE_MESSAGE)
				strStatus=strStatus+"Release ";
			else if (iRequestType==RMIPPoolService.IP_UPDATE_MESSAGE)
				strStatus=strStatus+"Update ";
			else if (iRequestType==RMIPPoolService.ACCT_UPDATE_MESSAGE)
				strStatus=strStatus+"Intrim ";
			
			if (isSuccess) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, strStatus +  "Successful ");
				}
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE,strStatus + "UnSuccessful");
				}
			}	

			

		} catch (DriverProcessFailedException ex) {

			if(ippoolServiceConfiguration.isOperationFailureEnabled()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE," Success On Operation Failure is = True ,"+ex.getMessage());
				}
				response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);

			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE," Success On Operation Failure is = False , Request is mark for Drop .");
				}
				response.markForDropRequest();
			}

			response.setFurtherProcessingRequired(false);
		}catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE," Request is mark for Drop .");
			}

			response.markForDropRequest();
		}

	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	class RequestValueProvider implements ValueProvider{
		
		private RadServiceRequest radServiceRequest;
		
		public RequestValueProvider(RadServiceRequest request) {
			this.radServiceRequest = request;
		}

		@Override
		public String getStringValue(String identifier) {
			if(identifier==null)
				return null;
			IRadiusAttribute radiusAttribute = radServiceRequest.getRadiusAttribute(identifier,true);
			if(radiusAttribute!=null){
				return radiusAttribute.getStringValue();
			}
			return null;
		}
		
	}
	
	class ResponseValueProvider implements ValueProvider{
		
		private RadServiceResponse radServiceResponse;
		
		public ResponseValueProvider(RadServiceResponse response) {
			this.radServiceResponse = response;
		}

		@Override
		public String getStringValue(String identifier) {
			if(identifier==null)
				return null;
			IRadiusAttribute radiusAttribute = radServiceResponse.getRadiusAttribute(identifier);
			if(radiusAttribute!=null){
				return radiusAttribute.getStringValue();
			}
			return null;
		}
		
	}
	
	class HardCodedValueProvider implements ValueProvider{

		@Override
		public String getStringValue(String identifier) {
			return identifier;
		}
		
	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response,
			ISession session) {
		/// not used
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return iPPoolDriver.isAlive() == false;
	}

}