package com.elitecore.aaa.radius.drivers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elitecore.aaa.core.conf.DBAcctDriverConfiguration;
import com.elitecore.aaa.core.conf.impl.DbFiledMapping;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.DBAcctDriver;
import com.elitecore.aaa.core.drivers.DBTypeAndValueTuple;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;

public class RadDBAcctDriver extends DBAcctDriver {
	private static String MODULE = "RAD-DB-ACCT-Drvr";
	private DBAcctDriverConfiguration radDBAcctDriverConfiguration = null;
	
	private RadAcctDriver acctDriver = null;
	private final AAAServerContext serverContext;
	
	public RadDBAcctDriver(AAAServerContext serverContext ,String driverInstanceId) {
		this(serverContext, (DBAcctDriverConfiguration)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId));
	}

	public RadDBAcctDriver(AAAServerContext serverContext, DBAcctDriverConfiguration driverConfiguration) {
		super(serverContext);		
		this.serverContext = serverContext;
		this.acctDriver = new RadAcctDriver();		
		this.radDBAcctDriverConfiguration = driverConfiguration;
	}

	@Override
	public String toString() {
		return radDBAcctDriverConfiguration.toString();
	}
	
	@Override
	protected void handleServiceRequest(ServiceRequest serviceRequest) throws DriverProcessFailedException {				
		try {
			handleRadAccountingRequest((RadServiceRequest)serviceRequest);
		} catch (DriverProcessFailedException e) {
			throw e;
		}	
	}	
	
	private List<DBTypeAndValueTuple> getAcctFieldList(RadServiceRequest request){
		List<DBTypeAndValueTuple> acctFiledList = new ArrayList<DBTypeAndValueTuple>();

		List<DbFiledMapping> fieldMappings =  radDBAcctDriverConfiguration.getDbFiledMappingList();

		int size = fieldMappings.size();
		DbFiledMapping dbFiledMapping = null;

		String multiValueDelimeter = getMultivalDelimeter();
		
		for(int index = 0; index < size; index++ ){
			dbFiledMapping = fieldMappings.get(index);		
			String value = "";
			String defaultValue = "";
			int iFoundOccurence =0;
			if(dbFiledMapping.getDefaultValue() != null){
				defaultValue = dbFiledMapping.getDefaultValue();
			}

			List<String> attributeIDList = dbFiledMapping.getAttributeIDList();
			
			if(attributeIDList != null && !(attributeIDList.isEmpty())){
				for(int i = 0; i < attributeIDList.size(); i++){
					String attributeID = attributeIDList.get(i);
					Collection<IRadiusAttribute> attributeList = request.getRadiusAttributes(attributeID, true);
			String attributeValue = null;
			if(attributeList!=null){
				for(IRadiusAttribute radAttribute :attributeList){
					if(radAttribute!=null){
						attributeValue = radAttribute.getStringValue(dbFiledMapping.getUseDictionaryValue());

						if(attributeValue!=null){
							if(multiValueDelimeter!=null){
								if(value.length()>0)
									value = value+multiValueDelimeter;
								if(attributeValue.contains(multiValueDelimeter)){
									attributeValue = attributeValue.replaceAll(multiValueDelimeter, "\\\\"+multiValueDelimeter);
								}
							}
									value += attributeValue;
									iFoundOccurence++;
						}
					}	
				}
						if(iFoundOccurence > 0)
							break;
					}
				}
			}
			if(!(value.length() > 0) && defaultValue.trim().length() > 0){
 				value = defaultValue;
 			}
			acctFiledList.add(new DBTypeAndValueTuple(dbFiledMapping.getDataType(),value));
		}
		return acctFiledList;
	}
	
	private void handleRadAccountingRequest(RadServiceRequest request) throws DriverProcessFailedException {
		LogManager.getLogger().info(MODULE, "Processing Request through Open Db Acct Driver: " + getName());
		List<DBTypeAndValueTuple> acctFiledList = getAcctFieldList(request);
		int iStatusType = this.acctDriver.getAcctReqStatusType(request);
		int result = -1;
		try{		
			switch(iStatusType){
			
			case RadiusAttributeValuesConstants.START:							
				if(isStoreStartRecordEnabled()) {
					result =  executeQuery(INSERT_QUERY_TYPE,getCDRInsertQuery(),acctFiledList, request);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Store Interim Records: " + getStoreInterimRec() + ". Start entry not stored.");
				}
				if(result > 0)
					LogManager.getLogger().debug(MODULE, "Attributes Dumped in table: " + getCdrTableName());
				break;
				
			case RadiusAttributeValuesConstants.STOP:
				
				// don't change the sequence of applying Delete and Insert query. 
				if(isStoreUpdateRecordEnabled() && getRemoveInterimOnStop()) {
					result = executeQuery(DELETE_QUERY_TYPE, getCDRInterimDeleteQuery(), acctFiledList, request);
					if(result > 0)
						LogManager.getLogger().debug(MODULE, "Attributes Remove From table: " + getInterimTableName());
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Remove Interim Update Record configuration is diabled. Interim update entries will not be removed.");
				}				
				if(getStoreStopRec()) {
					result =  executeQuery(INSERT_QUERY_TYPE,getCDRInsertQuery(),acctFiledList, request);
					if(result > 0)
						LogManager.getLogger().debug(MODULE, "Attributes Dumped in table: " + getCdrTableName());
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Store Interim Record configuration is disabled. Interims will not be stored.");
				}
				break;	    
	
		    case RadiusAttributeValuesConstants.INTERIM_UPDATE:	    
		    	if(isStoreUpdateRecordEnabled()) {
		    		if(!getStoreInterimRec()){ 
		    			result = executeQuery(UPDATE_QUERY_TYPE,getCDRInterimUpdateQuery() ,acctFiledList, request);
		    			if(result > 0){
		    				LogManager.getLogger().debug(MODULE, "Attributes Updated in table: " + getInterimTableName());
		    			}else {
		    				result = executeQuery(INSERT_QUERY_TYPE,getCDRInterimInsertQuery(), acctFiledList, request);
		    				if(result > 0)
		    					LogManager.getLogger().debug(MODULE, "Attributes dumped in table: " + getInterimTableName());
		    			}	
		    		}else {
		    			result = executeQuery(INSERT_QUERY_TYPE,getCDRInterimInsertQuery(), acctFiledList, request);
		    			if(result > 0)
		    				LogManager.getLogger().debug(MODULE, "Attributes dumped in table: " + getInterimTableName());
		    		}	
		    	}else {
		    		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
		    			LogManager.getLogger().debug(MODULE, "Store Interim Record configuration is disabled. Interims will not be stored.");
		    	}
		    	break;
		    case DBAcctDriver.ACCT_STATUS_TYPE_NOT_FOUND:	    	
		    	if(getStoreAllCDR()){
		    		result =  executeQuery(INSERT_QUERY_TYPE,getCDRInterimInsertQuery(),acctFiledList, request);
		    	}else {
			    	throw new DriverProcessFailedException("Acct-Status-Type Attribute not present in request packet and Store All CDR configured to false");
				}
		    	if(result > 0)
					LogManager.getLogger().debug(MODULE, "Attributes Dumped in table: " + getInterimTableName());
		    	break;
		    default:
		    	LogManager.getLogger().trace(MODULE ,"No support of OpenDB Driver For Request Status Type: " + iStatusType);
		    	throw new DriverProcessFailedException("No support of OpenDb Driver For Request Status Type: " + iStatusType);
			}			
		} catch (DriverProcessFailedException e) {
			throw e;				
		}
	}
	
	@Override
	public String getDsName(){
		return radDBAcctDriverConfiguration.getDsName();
	}
	
	private String getCDRInsertQuery(){		
		return radDBAcctDriverConfiguration.getCDRInsertQuery();		
	}
	
	private String getCDRInterimInsertQuery() {
		return radDBAcctDriverConfiguration.getCDRInterimInsertQuery();
	}
	
	private String getCDRInterimUpdateQuery() {
		return radDBAcctDriverConfiguration.getCDRInterimUpdateQuery();
	}
	
	private String getCDRInterimDeleteQuery() {
		return radDBAcctDriverConfiguration.getCDRInterimDeleteQuery();
	}
	
	private String getInterimCdrIdDbField() {
		return radDBAcctDriverConfiguration.getInterimCdrIdDbField();
	}
	
	private String getInterimTableName() {
		return radDBAcctDriverConfiguration.getInterimTableName();
	}
	
	private String getCdrTableName() {
		return radDBAcctDriverConfiguration.getCdrTableName();
	}
	
	private boolean getStoreInterimRec() {
		return radDBAcctDriverConfiguration.getStoreInterimRec();
	}
	
	private boolean isStoreStartRecordEnabled() {
		return radDBAcctDriverConfiguration.isStoreStartRecordEnabled();
	}
	private boolean isStoreUpdateRecordEnabled() {
		return radDBAcctDriverConfiguration.isStoreUpdateRecordEnabled();
	}
	
	private boolean getStoreStopRec() {
		return radDBAcctDriverConfiguration.getStoreStopRec();
	}
	
	private boolean getRemoveInterimOnStop() {
		return radDBAcctDriverConfiguration.getRemoveInterimOnStop();
	}
	
	private boolean getStoreAllCDR(){
		return radDBAcctDriverConfiguration.getStoreAllCDR();
	}
	
	@Override
	public int getDbQueryTimeout() {
		return radDBAcctDriverConfiguration.getDbQueryTimeout();
	}
	
	@Override
	public String getCallEndFieldName() {
		return radDBAcctDriverConfiguration.getCallEndFieldName();
	}
	
	@Override
	public int getMaxQueryTimeoutCount() {
		return radDBAcctDriverConfiguration.getMaxQueryTimeoutCount();
	}
	
	@Override
	public String getCallStartFieldName() {
		return radDBAcctDriverConfiguration.getCallStartFieldName();
	}

	@Override
	public String getCreateDateFieldName() {
		return radDBAcctDriverConfiguration.getCreateDateFieldName();
	}

	@Override
	public boolean getEnebled() {
		return radDBAcctDriverConfiguration.getEnebled();
	}

	@Override
	public String getDbDateField() {
		return radDBAcctDriverConfiguration.getDbDateField();
	}

	@Override
	public String getLastModifiedDateFieldName() {
		return radDBAcctDriverConfiguration.getLastModifiedDateFieldName();
	}

	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		if(!isStoreStartRecordEnabled() && !isStoreUpdateRecordEnabled()){
			throw new DriverInitializationFailedException("Both StoteCDRRecord and StoteInterinRecord disabled for driver: "+getName());
		}
		List<DbFiledMapping> dbFieldMapping = this.radDBAcctDriverConfiguration.getDbFiledMappingList(); 
		if(dbFieldMapping == null || dbFieldMapping.size() == 0){
			throw new DriverInitializationFailedException("No valid DbFieldMapping found in driver : "+getName());
		}
		super.initInternal();
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}

	@Override
	public String getName() {
		return radDBAcctDriverConfiguration.getDriverName();
	}

	@Override
	protected String getAcctSessionId(ServiceRequest request) {
		IRadiusAttribute acctSessionIDAttr = ((RadServiceRequest)request).getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID);
		if(acctSessionIDAttr!=null){
			return acctSessionIDAttr.getStringValue();
		}
		return null;
	}

	@Override
	protected long getAcctSessionTimeValue(ServiceRequest request) {
		long lAcctSessionTime = 0;
		IRadiusAttribute acctSessionTime = ((RadServiceRequest)request).getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_TIME);
		if(acctSessionTime != null){
			lAcctSessionTime = acctSessionTime.getLongValue() * 1000;
		}
		return lAcctSessionTime;
	}

	@Override
	protected long getEventTimeValue(ServiceRequest request) {
		long lEventTimeStamp = 0;
		IRadiusAttribute eventTimeStamp = ((RadServiceRequest)request).getRadiusAttribute(RadiusAttributeConstants.EVENT_TIMESTAMP);
		if(eventTimeStamp != null){
			lEventTimeStamp = eventTimeStamp.getLongValue() * 1000;
			if(lEventTimeStamp <= 0){
				lEventTimeStamp = new Date().getTime();
			}
		}else{
			lEventTimeStamp = new Date().getTime();
		}
		return lEventTimeStamp;
	}

	@Override
	public int getType() {
		return DriverTypes.RAD_OPENDB_ACCT_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RAD_OPENDB_ACCT_DRIVER.name();
	}

	@Override
	public String getMultivalDelimeter() {
		return this.radDBAcctDriverConfiguration.getMultivalDelimeter();
	}
	
	@Override
	public void reInit() {
		this.radDBAcctDriverConfiguration = (DBAcctDriverConfiguration)(serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(this.radDBAcctDriverConfiguration.getDriverInstanceId()));
	}

}
