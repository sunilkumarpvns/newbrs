package com.elitecore.aaa.radius.plugins.quotamgmt;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.naming.ServiceUnavailableException;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.radius.plugins.core.BaseRadPlugin;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementData;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConf;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.acct.plugins.DS;
import com.elitecore.aaa.radius.util.exprlib.DefaultValueProvider;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class QuotaManagementPlugin extends BaseRadPlugin<RadServiceRequest, RadServiceResponse> {

	private static final String MODULE = "QUOTA-MANAGEMENT-PLUGIN";
	private static final String VOLUME_TYPE_QUOTA = "VOLUME";
	private static final String DURATION_TYPE_QUOTA = "TIME";
	private static final String BOTH_TYPE_QUOTA = "BOTH";
	private QuotaManagementPluginConfiguration data;
	private EliteAAAServiceExposerManager exposerManager;
	
	public QuotaManagementPlugin(PluginContext pluginContext, QuotaManagementPluginConfiguration data) {
		super(pluginContext, data.getPluginInfo());
		this.data = data;
		this.exposerManager = EliteAAAServiceExposerManager.getInstance();
	}
	
	QuotaManagementPlugin(PluginContext pluginContext, QuotaManagementPluginConfiguration data, EliteAAAServiceExposerManager exposerManager ) {
		super(pluginContext, data.getPluginInfo());
		this.data = data;
		this.exposerManager = exposerManager;
	}

	@Override
	public void handlePostRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		// not required as this is a pre plugin

	}

	@Override
	public void handlePreRequest(RadServiceRequest radServiceRequest,
			RadServiceResponse radServiceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		
		
		if(isEligible(radServiceRequest)) {
			return;
		}
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Process request with plugin: " + this.data.getName());
		}
		
		for (QuotaManagementData pluginData : data.getPluginsData()) {
			
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Applying plugin policy: " + pluginData.getName());	
			}

			LogicalExpression expression = pluginData.getExpression();

			if (expression == null) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Invalid ruleset: " + pluginData.getRuleset() + 
							" configured in policy: " + pluginData.getName() + ", of plugin: " + this.data.getName() +
							" so skip this policy.");
				}
				
				continue;
			}

			if (expression.evaluate(new DefaultValueProvider(radServiceRequest, radServiceResponse)) == false) {
				LogManager.getLogger().info(MODULE, "Ruleset: " + pluginData.getRuleset() + " of policy: " 
					+ pluginData.getName() + " does not satisfy, so skip this policy.");
				
				continue;
			}

			boolean result = false;

		//check the type of quota 
		String quotaType = (String)pluginData.getValue(QuotaManagementPluginConf.QUOTA_TYPE);	

		if(quotaType.equalsIgnoreCase(VOLUME_TYPE_QUOTA)){
			result = isVolumeExceeded(radServiceRequest, pluginData);
		}else if(quotaType.equalsIgnoreCase(DURATION_TYPE_QUOTA)){
			result = isTimeExceeded(radServiceRequest, pluginData);
		}else if(quotaType.equalsIgnoreCase(BOTH_TYPE_QUOTA)){
			result = isBothVolumeAndDurationExceeded(radServiceRequest, pluginData);
		}

		if(result){

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"Generating CoA / DM as per configuration .");

			// check the presence of class attribute
			Collection<IRadiusAttribute> col = null;
			if((col = radServiceRequest.getRadiusAttributes(RadiusAttributeConstants.CLASS)) == null){
				return ;
			}		
			
			ArrayList<IRadiusAttribute> list = (ArrayList<IRadiusAttribute>)col;

			// Permitted Volume / time Limit broken  

			String cfp = (String)pluginData.getValue(QuotaManagementPluginConf.CONTINUE_FURTHER_PROCESSING);
			if(cfp.equals("false")){
				int action = pluginData.getAction(QuotaManagementPluginConf.ACTION);
				if(action == QuotaManagementPluginConf.ACCEPT) {
					radServiceResponse.addAttribute(radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID));
					radServiceResponse.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
				}else if (action == QuotaManagementPluginConf.DROP) {
					radServiceResponse.markForDropRequest();
				}
				radServiceResponse.setFurtherProcessingRequired(false);
			}
			// code for generating DM/CoA.
			RadiusPacket dynaAuthRequestPacket = new RadiusPacket();
			Integer top = (Integer)pluginData.getValue(QuotaManagementPluginConf.TYPE_OF_PACKET);
			if(top == RadiusConstants.COA_REQUEST_MESSAGE)
				dynaAuthRequestPacket.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE);			
			else if(top == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE)
				dynaAuthRequestPacket.setPacketType(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE);
			else{
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE,"Type of Packet is neither DM nor CoA in interim-volume-meter-configurations.xml.Exiting Plugin.");
				return;			
			}

			ArrayList<DS> listOfAttributes = pluginData.getListOfAttributes();
			if(Collectionz.isNullOrEmpty(listOfAttributes)){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE,"List of Attributes is empty or is not properly configured in Quota Management Plugin Configuration ");
			}else{			
				long vendorID = 0;
				int attributeID = 0;
				String value = null;
				IRadiusAttribute radiusAttribute = null;
				for(int i = 0 ; i < listOfAttributes.size() ; i++){
					DS data = listOfAttributes.get(i);
					vendorID = data.getCurr().getVendorID();
					attributeID = data.getCurr().getAttributeID();
					if(data.getFixedValue() == null && data.getDyn() == null){
						if(vendorID == 0){
							radiusAttribute = radServiceRequest.getRadiusAttribute(attributeID);
							if(radiusAttribute == null) continue;
							value = radiusAttribute.getStringValue();
							radiusAttribute = Dictionary.getInstance().getKnownAttribute(attributeID);
							if(radiusAttribute == null) continue;
							radiusAttribute.setStringValue(value);
						}
						else{
							col = radServiceRequest.getRadiusAttributes(vendorID, attributeID);
							if(col == null) continue;
							list = (ArrayList<IRadiusAttribute>)col;
							value = ((IRadiusAttribute)list.get(0)).getStringValue();
	
							radiusAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
	
							radiusAttribute.setVendorID(vendorID);
							IVendorSpecificAttribute vsaType = Dictionary.getInstance().getVendorAttributeType(vendorID);
							IRadiusAttribute vsaRadiusAttribute = Dictionary.getInstance().getKnownAttribute(vendorID, attributeID);
	
							//if( vsaRadiusAttribute == null || vsaRadiusAttribute instanceof UnknownAttribute || vsaRadiusAttribute instanceof WimaxUnknownAttribute ) {
							if( vsaRadiusAttribute == null){
								continue;
							}
	
							vsaRadiusAttribute.setStringValue(value);
							vsaType.addAttribute(vsaRadiusAttribute);
							vsaType.refreshAttributeHeader();
							((VendorSpecificAttribute)radiusAttribute).setVendorTypeAttribute(vsaType);
							((VendorSpecificAttribute)radiusAttribute).refreshAttributeHeader();											
						}
						dynaAuthRequestPacket.addAttribute(radiusAttribute);
					}else if((value=data.getFixedValue()) != null && data.getDyn() == null){
						if(vendorID == 0){
							radiusAttribute = Dictionary.getInstance().getKnownAttribute(attributeID);
							if(radiusAttribute == null) continue;
							radiusAttribute.setStringValue(value);
						}
						else{
							radiusAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
							radiusAttribute.setVendorID(vendorID);
							IVendorSpecificAttribute vsaType = Dictionary.getInstance().getVendorAttributeType(vendorID);
							IRadiusAttribute vsaRadiusAttribute = Dictionary.getInstance().getKnownAttribute(vendorID, attributeID);
	
							if( vsaRadiusAttribute == null){
								continue;
							}
	
							vsaRadiusAttribute.setStringValue(value);
							vsaType.addAttribute(vsaRadiusAttribute);
							vsaType.refreshAttributeHeader();
							((VendorSpecificAttribute)radiusAttribute).setVendorTypeAttribute(vsaType);
							((VendorSpecificAttribute)radiusAttribute).refreshAttributeHeader();	
						}
						dynaAuthRequestPacket.addAttribute(radiusAttribute);
					}else{
						long tempv =  data.getDyn().getVendorID();
						int  tempa = data.getDyn().getAttributeID();
	
						if(tempv == 0){
							radiusAttribute = radServiceRequest.getRadiusAttribute(tempa);
							if(radiusAttribute == null) continue;
							value = radiusAttribute.getStringValue();
							//if(value == null) continue;
							if(vendorID == 0){
								radiusAttribute = Dictionary.getInstance().getKnownAttribute(attributeID);
								if(radiusAttribute == null) continue;
								radiusAttribute.setStringValue(value);
							}
							else{
								radiusAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
								radiusAttribute.setVendorID(vendorID);
								IVendorSpecificAttribute vsaType = Dictionary.getInstance().getVendorAttributeType(vendorID);
								IRadiusAttribute vsaRadiusAttribute = Dictionary.getInstance().getAttribute(vendorID, attributeID);
	
								if( vsaRadiusAttribute == null){
									continue;
								}
	
								vsaRadiusAttribute.setStringValue(value);
								vsaType.addAttribute(vsaRadiusAttribute);
								vsaType.refreshAttributeHeader();
								((VendorSpecificAttribute)radiusAttribute).setVendorTypeAttribute(vsaType);
								((VendorSpecificAttribute)radiusAttribute).refreshAttributeHeader();											
	
							}				
						}
						else{					
							col = radServiceRequest.getRadiusAttributes(tempv, tempa);
							if(col == null) continue;
							list = (ArrayList<IRadiusAttribute>)col;					
							value = ((IRadiusAttribute)list.get(0)).getStringValue();
							if(value == null) continue;
							if(vendorID == 0){							
								radiusAttribute = Dictionary.getInstance().getKnownAttribute(attributeID);
								if(radiusAttribute == null) continue;
								radiusAttribute.setStringValue(value);
	
							}else{
								radiusAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
	
								radiusAttribute.setVendorID(vendorID);
								IVendorSpecificAttribute vsaType = Dictionary.getInstance().getVendorAttributeType(vendorID);
								IRadiusAttribute vsaRadiusAttribute = Dictionary.getInstance().getKnownAttribute(vendorID, attributeID);
	
								if( vsaRadiusAttribute == null){
									continue;
								}
	
								vsaRadiusAttribute.setStringValue(value);
								vsaType.addAttribute(vsaRadiusAttribute);
								vsaType.refreshAttributeHeader();
								((VendorSpecificAttribute)radiusAttribute).setVendorTypeAttribute(vsaType);
								((VendorSpecificAttribute)radiusAttribute).refreshAttributeHeader();
							}					
						}
						dynaAuthRequestPacket.addAttribute(radiusAttribute);
					}
				}
			}

			dynaAuthRequestPacket.refreshPacketHeader();

			dynaAuthRequestPacket.refreshInfoPacketHeader();
			
			String sharedSecret = radServiceResponse.getClientData().getSharedSecret(radServiceRequest.getPacketType());
			byte[] authenticator = getRequestAuthenticator(dynaAuthRequestPacket.getPacketType(), dynaAuthRequestPacket, sharedSecret);
			dynaAuthRequestPacket.setAuthenticator(authenticator);
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, dynaAuthRequestPacket.toString());
			}

			RadiusPacket dynaAuthresponsePacket = null;
			
			//sending the request to the DynaAuthService using the ExposureManager
			try {
				dynaAuthresponsePacket = exposerManager
						.sendBlockingLocalRequest(dynaAuthRequestPacket);

				if (LogManager.getLogger().isDebugLogLevel()) {
					if (dynaAuthresponsePacket != null) {
						LogManager.getLogger().debug(MODULE, "Response received from DynaAuth service:");
						LogManager.getLogger().debug(MODULE, dynaAuthresponsePacket.toString());
					} else {
						LogManager.getLogger().debug(MODULE, "No response received from DynaAuth service," +
								" Reason: Request dropped/timed-out");
					}
				}
			} catch (ServiceUnavailableException e) {
				LogManager.getLogger().warn(MODULE, e.getMessage());
			} catch (InterruptedException e) {
				LogManager.getLogger().debug(MODULE, e.getMessage());
			} catch (TimeoutException e) {
				LogManager.getLogger().info(MODULE, e.getMessage());
			} catch (ExecutionException e) {
				LogManager.getLogger().warn(MODULE, e.getMessage());
			} catch(UnknownHostException ex){
				LogManager.getLogger().warn(MODULE, ex.getMessage());
			}
		}
		}
	}

	private byte[] getRequestAuthenticator(int packetType, RadiusPacket dynaAuthRequestPacket, String sharedSecret) {
		if(packetType==RadiusConstants.ACCOUNTING_REQUEST_MESSAGE || packetType == RadiusConstants.COA_REQUEST_MESSAGE || packetType == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE){
			return RadiusUtility.generateRFC2866RequestAuthenticator(dynaAuthRequestPacket, sharedSecret);
		}
		return RadiusUtility.generateRFC2865RequestAuthenticator();
	}

	private boolean isEligible(RadServiceRequest radServiceRequest) {
		IRadiusAttribute radiusAttribute=radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
		if(radiusAttribute!=null && radiusAttribute.getIntValue()!=RadiusAttributeValuesConstants.INTERIM_UPDATE){
			return true;
		}else {
			return false;
		}
	}

	@Override
	public void init() throws InitializationFailedException {

		Collectionz.filter(this.data.getPluginsData(), QuotaManagementData.ENABLED);

		for (QuotaManagementData data : this.data.getPluginsData()) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Initailize plugin policy: " + data.getName() + 
						", for plugin: " + this.data.getName());
			}
			parseRuleSet(data);
		}

		this.exposerManager.init();

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Quota Management Plugin: " + data.getName() + " Initialized Successfully");
		}
	}

	private void parseRuleSet(QuotaManagementData data) {
		try {
			LogicalExpression expression = Compiler.getDefaultCompiler().parseLogicalExpression(data.getRuleset());
			data.setExpression(expression);
		} catch (InvalidExpressionException e) {
			LogManager.getLogger().error(MODULE, "Invalid ruleset: " + data.getRuleset() + 
					" configured for policy: " + data.getName() + " of plugin: " + getName());
			LogManager.getLogger().trace(e);
		}
	}


	@Override
	public void reInit() throws InitializationFailedException {
		init();
	}

	@VisibleForTesting
	boolean isVolumeExceeded(RadServiceRequest radiusRequestPacket, QuotaManagementData pluginData){

		int  acctInputOctets = 0;
		int acctOutputOctets = 0;
		int acctInputGw = 0;
		int acctOutputGw = 0;
		String classAttributeKey = "";

		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_OCTETS) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_INPUT_OCTETS attribute not found in request packet.");			
		}
		else{
			acctInputOctets = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_OCTETS).getIntValue() ;
		}
		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_OCTETS) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_OUTPUT_OCTETS attribute not found in request packet.");			
		}
		else{
			acctOutputOctets = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_OCTETS).getIntValue() ;
		}
		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_GIGAWORDS) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_INPUT_GIGAWORDS attribute not found in request packet.");			
		}
		else{
			acctInputGw = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_GIGAWORDS).getIntValue() ;
		}
		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_GIGAWORDS) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_OUTPUT_GIGAWORDS attribute not found in request packet.");			
		}
		else{
			acctOutputGw = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_GIGAWORDS).getIntValue() ;
		}

		classAttributeKey = (String)pluginData.getValue(QuotaManagementPluginConf.CLASS_ATTRIBUTE_KEY_FOR_VOLUME);

		if(acctInputOctets==0 && acctOutputOctets==0 && acctInputGw==0 && acctOutputGw==0){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"ACCT_INPUT_OCTETS , ACCT_OUTPUT_OCTETS ,ACCT_INPUT_GIGAWORDS ,ACCT_OUTPUT_GIGAWORDS attributes not found in request packet.");
			return false;			
		}

		if(classAttributeKey == null || classAttributeKey.length()<=0){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"There is no key configured for class attribute to take values.So no further processing will be done.");
			return false;
		}				

		// check the presence of class attribute
		Collection<IRadiusAttribute> col = null;
		if((col = radiusRequestPacket.getRadiusAttributes(RadiusAttributeConstants.CLASS)) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"There are no class attributes present in the request.");

			return false;
		}

		long permittedValueForVolume = (-1);	
		long valueOfIpGW = (4294967296L) * acctInputGw;
		long valueOfOpGw = (4294967296L) * acctOutputGw;


		permittedValueForVolume = getPremittedValueFromClassAttribute(col, classAttributeKey);	

		if(permittedValueForVolume == (-1)){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"The configured key is not present in any of the class attributes.");

			return false;
		}
		if(permittedValueForVolume != (-1)){
			long usedVolume = (((valueOfIpGW + valueOfOpGw)/1024) + ((acctInputOctets+acctOutputOctets)/1024)); 
			if(usedVolume < permittedValueForVolume){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Used Volume: "+usedVolume+" is less than Permitted Volume: "+permittedValueForVolume);
				return false;
			}
		}

		return true;
	}

	@VisibleForTesting
	boolean isTimeExceeded(RadServiceRequest radiusRequestPacket, QuotaManagementData pluginData){

		int acctSessionTime = 0;
		String classAttributeKey = "";
		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_TIME) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_SESSION_TIME attribute not found in request packet.");		
		}else{
			acctSessionTime = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_TIME).getIntValue() ;
		}			
		classAttributeKey = (String)pluginData.getValue(QuotaManagementPluginConf.CLASS_ATTRIBUTE_KEY_FOR_TIME);

		if(acctSessionTime == 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"ACCT_SESSION_TIME attributes not found in request packet.");
			return false;	
		}	

		if(classAttributeKey == null || classAttributeKey.length()<=0){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"No class attribute key configured in plugin configuration .So no further processing will be done.");
			return false;
		}	

		// check the presence of class attribute
		Collection<IRadiusAttribute> col = null;
		if((col = radiusRequestPacket.getRadiusAttributes(RadiusAttributeConstants.CLASS)) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"There are no class attributes present in the request.");

			return false;
		}			
		long permittedValueForTime = (-1);

		permittedValueForTime = getPremittedValueFromClassAttribute(col, classAttributeKey);

		if(permittedValueForTime == (-1)){

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"The configured key is not found in any of the class attributes.");

			return false;
		}					
		if(acctSessionTime < permittedValueForTime){
			return false;
		}		
		return true;
	}

	@VisibleForTesting
	boolean isBothVolumeAndDurationExceeded(RadServiceRequest radiusRequestPacket, QuotaManagementData pluginData){

		int acctInputOctets = 0;
		int acctOutputOctets = 0;
		int acctSessionTime = 0;
		int acctInputGw = 0;
		int acctOutputGw = 0;

		String classAttributeKeyForVolume = "";
		String classAttributeKeyForTime = "";

		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_OCTETS) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_INPUT_OCTETS attribute not found in request packet.");			
		}
		else{
			acctInputOctets = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_OCTETS).getIntValue() ;
		}
		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_OCTETS) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_OUTPUT_OCTETS attribute not found in request packet.");			
		}
		else{
			acctOutputOctets = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_OCTETS).getIntValue() ;
		}	
		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_GIGAWORDS) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_INPUT_GIGAWORDS attribute not found in request packet.");			
		}
		else{
			acctInputGw = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_GIGAWORDS).getIntValue() ;
		}
		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_GIGAWORDS) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_OUTPUT_GIGAWORDS attribute not found in request packet.");
		}else{
			acctOutputGw = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_GIGAWORDS).getIntValue();
		}

		classAttributeKeyForVolume = (String)pluginData.getValue(QuotaManagementPluginConf.CLASS_ATTRIBUTE_KEY_FOR_VOLUME);

		if(radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_TIME) == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,"ACCT_SESSION_TIME attribute not found in request packet.");		
		}else{
			acctSessionTime = radiusRequestPacket.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_TIME).getIntValue() ;
		}			
		classAttributeKeyForTime = (String)pluginData.getValue(QuotaManagementPluginConf.CLASS_ATTRIBUTE_KEY_FOR_TIME);

		if((acctSessionTime == 0) && (acctInputOctets == 0 && acctOutputOctets == 0 && acctInputGw == 0 && acctOutputGw == 0)){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"Either of ACCT_SESSION_TIME  or ACCT_INPUT_OCTETS or ACCT_OUTPUT_OCTETS or ACCT_INPUT_GIGAWORDS or ACCT_OUTPUT_GIGAWORDS attributes not found in request packet.");
			return false;	
		}	

		if((classAttributeKeyForTime == null || classAttributeKeyForTime.length()<=0)  && (classAttributeKeyForVolume == null || classAttributeKeyForVolume.length() <=0)){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"There is no key configured for class attribute to take values.So no further processing will be done.");
			return false;
		}	

		// check the presence of class attribute
		Collection<IRadiusAttribute> col = null;
		if((col = radiusRequestPacket.getRadiusAttributes(RadiusAttributeConstants.CLASS)) == null){			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"There are no class attributes present in the request.");

			return false;
		}

		long permittedValueForVolume = (-1);
		long permittedValueForTime = (-1);
		long valueOfInputGw = (4294967296L) * acctInputGw;
		long valueOfOutputGw = (4294967296L) * acctOutputGw;

		if(classAttributeKeyForTime != null && classAttributeKeyForTime.length() > 0){
			permittedValueForTime = getPremittedValueFromClassAttribute(col, classAttributeKeyForTime);
		}if(classAttributeKeyForVolume != null && classAttributeKeyForVolume.length() > 0){
			permittedValueForVolume = getPremittedValueFromClassAttribute(col, classAttributeKeyForVolume);
		}

		if(permittedValueForTime == (-1) && permittedValueForVolume == (-1)){

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE,"The configured key is not found in any of the class attributes.");

			return false;
		}

		long totalOctets = (((valueOfInputGw + valueOfOutputGw)/1024) + ((acctInputOctets + acctOutputOctets) / 1024));


		if(permittedValueForTime != (-1)){			
			if(acctSessionTime < permittedValueForTime){				
				if(permittedValueForVolume == (-1) || (totalOctets < permittedValueForVolume)){
					return false;
				}				
			}
		}if(permittedValueForVolume != (-1)){
			if(totalOctets < permittedValueForVolume){
				if(permittedValueForTime == (-1) || acctSessionTime < permittedValueForTime){
					return false;
				}				
			}
		}

		return true;
	}

	private long getPremittedValueFromClassAttribute(Collection<IRadiusAttribute> col,String classAttributeKey){		

		long permittedValue = 0;
		ArrayList<IRadiusAttribute> list = (ArrayList<IRadiusAttribute>)col;
		boolean found = false;
		for(int i = 0; i < list.size(); i++)	
		{
			String content = ((IRadiusAttribute)list.get(i)).getStringValue().toLowerCase();
			int pos = content.indexOf(classAttributeKey);
			if (pos == -1) continue;
			found = true;
			pos += classAttributeKey.length();
			char[] volumeValue = content.toCharArray();
			if(pos > content.length()){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, classAttributeKey + " is incomplete in request packet.");
				return -1;
			}
			StringBuilder strBuilder = new StringBuilder();
			char ch;
			for(;Character.isDigit(volumeValue[pos]);pos++){
				strBuilder.append(volumeValue[pos]);
				try{
					ch = volumeValue[pos+1];
				}catch(ArrayIndexOutOfBoundsException e){
					permittedValue = Long.parseLong(strBuilder.toString());
					break;
				}
			}
			permittedValue = Long.parseLong(strBuilder.toString());
			break;
		}	

		if(!found){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, classAttributeKey + " not found in class attribute of request packet.");
			return -1;
		}

		return permittedValue;

	}

}
