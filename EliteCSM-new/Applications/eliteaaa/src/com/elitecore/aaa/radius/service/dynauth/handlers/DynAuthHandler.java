package com.elitecore.aaa.radius.service.dynauth.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.DBFieldDetail;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.DynAuthServicePolicyConfiguration;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.impl.DynAuthServicePolicyConfigurationData.DBFailureAction;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthRequest;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthResponse;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthService;
import com.elitecore.aaa.radius.service.dynauth.RadDynAuthServiceContext;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.DynAuthErrorCode;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class DynAuthHandler implements RadDynAuthServiceHandler{

	private static final String MODULE = "DYN-AUTH-HANDLER";
	public static final int NONE = 1;
	public static final int RECENT = 2;
	public static final int OLDEST = 3;
	public static final int ALL = 4;
	
	private RadDynAuthServiceContext serviceContext;
	private String dbSourceName;
	private String concurrentDBQuery;
	private String policyId;
	private String policyName;
	private DynAuthServicePolicyConfiguration servicePolicyConfiguration;
	private final DynAuthCommunicationHandler dynAuthCommunicationHandler;
	
	public DynAuthHandler(RadDynAuthServiceContext serviceContext,String policyId) {
		this.serviceContext = serviceContext;
		this.policyId = policyId;
		this.servicePolicyConfiguration =  ((AAAServerContext)serviceContext.getServerContext()).getServerConfiguration().getDynAuthConfiguration().getDynAuthServicePolicyConfiguraion(policyId) ;
		this.policyName = servicePolicyConfiguration.getPolicyName();
		this.dynAuthCommunicationHandler = new DynAuthCommunicationHandler(serviceContext, servicePolicyConfiguration.getCommunicatorData());
	}

	@Override
	public void init() throws InitializationFailedException {
		AAAServerContext aaaServerContext = (AAAServerContext)serviceContext.getServerContext();
		DBDataSource dataSource = aaaServerContext.getServerConfiguration().getDatabaseDSConfiguration().getDataSourceByName(servicePolicyConfiguration.getDataSourceName());
		DBConnectionManager dbConnectionManager	= DBConnectionManager.getInstance(dataSource.getDataSourceName());
		try {
			dbConnectionManager.init(dataSource, aaaServerContext.getTaskScheduler());
		} catch (DatabaseInitializationException e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().info(MODULE, "Datasource initialization failure, continue initializing policy with datasource marked as dead");
		} catch (DatabaseTypeNotSupportedException e) {
			throw new InitializationFailedException("Problem in initializing Dyna Auth handler for policy : " + policyName 
					+ " , Reason :" + e.getMessage(), e);
		}
		dbSourceName = dataSource.getDataSourceName();
		try {
			this.concurrentDBQuery = servicePolicyConfiguration.getConcurrentDBQuery();
			this.dynAuthCommunicationHandler.init();
		} catch (Exception e) {
			throw new InitializationFailedException(e);
		}
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		this.servicePolicyConfiguration = ((AAAServerContext)serviceContext.getServerContext()).getServerConfiguration().getDynAuthConfiguration().getDynAuthServicePolicyConfiguraion(policyId) ;
	}

	@Override
	public boolean isEligible(RadDynAuthRequest request,
			RadDynAuthResponse response) {
		return true;
	}

	/**
	 * Request for DynAuth will handle by configured communicators,
	 * if none of the communicator is satisfy then request will drop.
	 * otherwise post processing is applied after first satisfy communicator. 
	 * <pre>
	 *        SYNCHRONOUS COMMUNICATION HANDLER          
	 * +-------------------+		+-------------------+
	 * | FILTER            |		| FILTER            |
	 * |      +---------+  |		|      +---------+  |
	 * | REQ  |         |  |	NO	|      |         |  | NO 
	 * +------> HANDLER |  |------>	|      | HANDLER |  |----> DROP REQUEST
	 * |      |         |  |		|      |         |  |
	 * |      +----+----+  |		|      +---------+  |
	 * |           |       |		|           	    |
	 * +-------------------+		+-------------------+
	 *         YES |                                    
	 *             v                                    
	 *       SKIP OTHER COMMUNICATOR ---> POST PROCESSING                    
	 *  
	 * </pre>
	 */
	@Override
	public void handleRequest(RadDynAuthRequest request,
			RadDynAuthResponse response, ISession session) {
		
		addMissingAttributes(request,response);
		if(response.isFurtherProcessingRequired() && validatePacketAfterAddingMissingAttribute(request,response)){
			addAttributesOfSatisfiedPolicies(request,response);
			dynAuthCommunicationHandler.handleRequest(request, response, session);
			dropOnNoNasCommunication(request, response);
		}
	}

	private void dropOnNoNasCommunication(RadDynAuthRequest request, RadDynAuthResponse response) {
		
		Boolean isNasCommunicationFilterSelected = (Boolean)request.getParameter(RadiusConstants.NAS_COMMUNICATOR_SELECTED);
		
		if(response.isFurtherProcessingRequired() && isNasCommunicationFilterSelected == null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "No nas communication occured, so dropping the request.");
			}
			RadiusProcessHelper.dropResponse(response);
		}
	}

	private void addAttributesOfSatisfiedPolicies(RadDynAuthRequest request, RadDynAuthResponse response){
		IRadiusAttribute satisfiedServicePoliciesAttr = request.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_SATISFIED_POLICIES);
		
		if(satisfiedServicePoliciesAttr != null && satisfiedServicePoliciesAttr.getStringValue().trim().length() > 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "EC-Satisfied-Policies (21067:129) found in request with value: " + satisfiedServicePoliciesAttr.getStringValue());
			}
			
			try {
				List<String> satisfiedPolicies = RadiusPolicyManager.getInstance(serviceContext.getServiceIdentifier()).applyPolicies(request, response, satisfiedServicePoliciesAttr.getStringValue());
				if(satisfiedPolicies != null && satisfiedPolicies.size() > 0){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "Following policies were applied successfully: " + StringUtility.getDelimitirSeparatedString(satisfiedPolicies, ","));
					}
				}
			} catch (ParserException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE,"Failed adding reply items using EC-Satisfied-Policies attribute (21067:129), Reason : "+e.getMessage());
			} catch (PolicyFailedException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE,"Failed adding reply items using EC-Satisfied-Policies attribute (21067:129), Reason : "+e.getMessage());
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "EC-Satisfied-Policies (21067:129) not found in request or found with blank value.");
			}
		}
	}
	
	private void actionOnDbFailure(RadDynAuthRequest request,RadDynAuthResponse response) {
		if (servicePolicyConfiguration.getDbFailureAction() == DBFailureAction.NAK) {
			LogManager.getLogger().warn(MODULE, "Sending NAK response, Reason: database datsource: " + dbSourceName 
					+ " has been marked dead and can not allow further processing");
			setPacketTypeToNak(request, DynAuthErrorCode.ResourcesUnavailable, response);
		}else if (servicePolicyConfiguration.getDbFailureAction() == DBFailureAction.DROP) {
			LogManager.getLogger().warn(MODULE, "Dropping request, Reason: database datsource: " + dbSourceName 
					+ " has been marked dead and can not allow further processing");
			RadiusProcessHelper.dropResponse(response);
		}
	}
	
	private void addMissingAttributes(RadDynAuthRequest request,RadDynAuthResponse response){
		
		LogManager.getLogger().debug(MODULE, "Starting process to add missing attributes");
		
		List<DBFieldDetail> dbFieldDetailList = servicePolicyConfiguration.getDbFieldDetailList();

		List<DBFieldDetail> missingAttributeList = null;
		
		if(dbFieldDetailList!=null && dbFieldDetailList.size()>0){
			
			missingAttributeList = new ArrayList<DBFieldDetail>();
			
			int size = dbFieldDetailList.size();
			String attributeId;
			for(int i=0;i<size;i++){
				attributeId = dbFieldDetailList.get(i).getAttributeId();

				if(request.getRadiusAttribute(attributeId)==null){
					//Nas IP Address or Nas IPv6 Address one of them should be in packet, not both. 
					if(attributeId.equals(RadDynAuthService.NAS_IP_ADDRESS)){
						if(request.getRadiusAttribute(RadDynAuthService.NAS_IPV6_ADDRESS)==null)
							missingAttributeList.add(dbFieldDetailList.get(i));
					}else if(attributeId.equals(RadDynAuthService.NAS_IPV6_ADDRESS)){
						if(request.getRadiusAttribute(RadDynAuthService.NAS_IP_ADDRESS)==null)
							missingAttributeList.add(dbFieldDetailList.get(i));
					}else{
						missingAttributeList.add(dbFieldDetailList.get(i));
					}
				}
			}
		}
		
    	if(missingAttributeList!=null && missingAttributeList.size()>0){
    		
    		IRadiusAttribute userNameAttribute = request.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
    		String strUserName = null;
    		if(userNameAttribute!=null)
    			strUserName = userNameAttribute.getStringValue();
    		
    		Connection connection = null;
    		PreparedStatement preparedStatement = null;
    		ResultSet resultSet = null;
    		
    		try {
    			connection = DBConnectionManager.getInstance(dbSourceName).getConnection();
    			int iEligibleSession = servicePolicyConfiguration.getEligibleSession();
    			IRadiusAttribute acctSessionId = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID);
    			preparedStatement = connection.prepareStatement(concurrentDBQuery,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
    			preparedStatement.setString(1, strUserName);
    			if(acctSessionId!=null){
    				preparedStatement.setString(2, acctSessionId.getStringValue());
    			}else{
    				preparedStatement.setString(2, "%");
    			}
    			resultSet = preparedStatement.executeQuery();

    			boolean bProperRecordPosition = false;
    			if(iEligibleSession == RECENT){
    				bProperRecordPosition = resultSet.last();
    			}else if(iEligibleSession == OLDEST){
    				bProperRecordPosition = resultSet.first();
    			}else if(iEligibleSession == NONE){
    				resultSet.last();
    				if(resultSet.getRow()==1)
    					bProperRecordPosition=true;
    				else{
    					setPacketTypeToNak(request, DynAuthErrorCode.AdministrativelyProhibited,response);
    					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
    						LogManager.getLogger().debug(MODULE,"Multiple record found for given user.  Sending Nak");

    					return;
    				}
    			}

    			String dbFieldName ;
    			String strAttributeId ;
    			String strDefaultValue ;
    			String strAttributeValue ;

    			for(int i=0;i<missingAttributeList.size();i++){
    				DBFieldDetail tempFieldDetail = missingAttributeList.get(i);
    				dbFieldName = tempFieldDetail.getDbField();
    				strAttributeId = tempFieldDetail.getAttributeId();

    				strAttributeValue = null;
    				if (bProperRecordPosition) {
    					strAttributeValue = resultSet.getString(dbFieldName);
    				} else {
    					
    					if (iEligibleSession == ALL) {
    						if (resultSet.first()) {
    							strAttributeValue = resultSet.getString(dbFieldName);
    							if (strAttributeValue != null) {
    								String strNextValue;
    								while (resultSet.next()) {
    									strNextValue = resultSet.getString(dbFieldName);
    									if (strAttributeValue.equals(strNextValue) == false) {
    										strAttributeValue = null;
    										if (LogManager.getLogger().isDebugLogLevel()) {
    											LogManager.getLogger().debug(MODULE, "Attribute: " + strAttributeId + " is not unique for all records. So, default value will be used");
    										}
    										break;
    									}
    								}
    							} else {
    								if (LogManager.getLogger().isDebugLogLevel()) {
    									LogManager.getLogger().debug(MODULE, "Value not found for DB field: " + dbFieldName + "(" + strAttributeId + "). So, default value will be used");
    								}
    							}
    						}
    					}else{
    						if(LogManager.getLogger().isDebugLogLevel()) {
    							LogManager.getLogger().debug(MODULE,"Record not found for user " + strUserName + " from concurrent login session.");
    						}
    					}
    				}
    				strDefaultValue = tempFieldDetail.getDefaultValue();

    				if (strAttributeValue == null) {
    					if (Strings.isNullOrBlank(strDefaultValue) == false) {
    						strAttributeValue=strDefaultValue;
    					} else if (tempFieldDetail.getIsMandatory()) {
    						if (LogManager.getLogger().isDebugLogLevel()) {
    							LogManager.getLogger().debug(MODULE, "Default value not found for mandatory attribute: " + strAttributeId + " in request packet. Sending NAK");
    						}
    						setPacketTypeToNak(request, DynAuthErrorCode.MissingAttribute,response);
    						return;
    					} else {
    						if (LogManager.getLogger().isDebugLogLevel()) {
    							LogManager.getLogger().debug(MODULE,"Default value not found for non-mandatory attribute: " +strAttributeId + " in request packet. So, will not be added");
    						}
    						continue;
    					}
    				}

    				IRadiusAttribute  radiusAttribute = Dictionary.getInstance().getKnownAttribute(strAttributeId) ;

    				if(radiusAttribute!=null && radiusAttribute.getVendorID() != RadiusConstants.WIMAX_VENDOR_ID){
    					radiusAttribute.setStringValue(strAttributeValue);								
    					request.addInfoAttribute(radiusAttribute);

    				}

    			}
    			if(LogManager.getLogger().isInfoLogLevel()) {
    				LogManager.getLogger().debug(MODULE,"Request Packet after adding missing attribute: " + request);
    			}
    		} catch (DataSourceException e) {
    			if(LogManager.getLogger().isWarnLogLevel()){
    				LogManager.getLogger().warn(MODULE,"Connection to datasource: " + dbSourceName 
    						+ " is unavailable, Reason: " + e.getMessage());
    			}
    			LogManager.getLogger().trace(MODULE, e);
    			actionOnDbFailure(request, response);
    		} catch (SQLException e) {
    			if(LogManager.getLogger().isErrorLogLevel()){
    				LogManager.getLogger().error(MODULE, "Error while fetching data from database, Reason: " + e.getMessage());
    			}
    			LogManager.getLogger().trace(MODULE, e);
    			actionOnDbFailure(request, response);
    		}finally{
    			DBUtility.closeQuietly(resultSet);
    			DBUtility.closeQuietly(preparedStatement);
    			DBUtility.closeQuietly(connection);
    		}

    	}else{
    		if (LogManager.getLogger().isDebugLogLevel()) {
    			LogManager.getLogger().debug(MODULE,"No missing attribute mapping configured");
    		}
    	}

	}
	
    private void setPacketTypeToNak(RadDynAuthRequest request,DynAuthErrorCode errorCode,RadDynAuthResponse response){
    	
        if(request.getPacketType() == RadiusConstants.COA_REQUEST_MESSAGE){
        	response.setPacketType(RadiusConstants.COA_NAK_MESSAGE);
		} else if (request.getPacketType() == RadiusConstants.DISCONNECTION_REQUEST_MESSAGE) {
			response.setPacketType(RadiusConstants.DISCONNECTION_NAK_MESSAGE);
		}
		IRadiusAttribute attr = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.ERROR_CAUSE);
		if(attr!=null){
			attr.setIntValue(errorCode.value);
			response.addAttribute(attr);
		}
		
		response.setFurtherProcessingRequired(false);
		
	}
    
    private boolean validatePacketAfterAddingMissingAttribute(RadDynAuthRequest request,RadDynAuthResponse response) {
        boolean isValidate = true;
       
        long eventTimestampValue = servicePolicyConfiguration.getEventTimestampValue();

        if (eventTimestampValue > 0 ) {

            IRadiusAttribute eventTimestampAttr = request.getRadiusAttribute(RadiusAttributeConstants.EVENT_TIMESTAMP);

            if (eventTimestampAttr != null) {
            	if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            		LogManager.getLogger().debug(MODULE, "Event-Timestamp attribute found in request packet");
                long packetCreationTime =eventTimestampAttr.getLongValue();

                if(((System.currentTimeMillis()/1000) - packetCreationTime) >  eventTimestampValue ){
                	if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
                		LogManager.getLogger().warn(MODULE, "Event-Timestamp value exceeds  " + eventTimestampValue + ". Dropping request");
                	response.markForDropRequest();
                	response.setFurtherProcessingRequired(false);
                	isValidate = false;
                }

            }else{
            	if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
            		LogManager.getLogger().info(MODULE, "Attribute Event-Timestamp not found in the request packet. Ignoring Event-Timestamp check");
            }
        }
                
        return isValidate;
    }
    
	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
 }
