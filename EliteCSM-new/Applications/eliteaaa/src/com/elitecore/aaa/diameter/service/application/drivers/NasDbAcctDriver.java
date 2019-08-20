package com.elitecore.aaa.diameter.service.application.drivers;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elitecore.aaa.core.conf.DBAcctDriverConfiguration;
import com.elitecore.aaa.core.conf.impl.DbFiledMapping;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.DBAcctDriver;
import com.elitecore.aaa.core.drivers.DBTypeAndValueTuple;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;

public class NasDbAcctDriver extends DBAcctDriver {

	private static String MODULE = "NAS-DB-ACCT-Drvr";
	private DBAcctDriverConfiguration dbAcctDriverConfiguration = null;
	
	private NasAcctDriver acctDriver = null;
	
	public NasDbAcctDriver(ServerContext serverContext,String driverInstanceId) {
		this(serverContext, (DBAcctDriverConfiguration)((AAAServerContext)serverContext).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverInstanceId),driverInstanceId);
	}

	public NasDbAcctDriver(ServerContext serverContext, DBAcctDriverConfiguration driverConfiguration, String driverInstanceId) {
		super(serverContext);
		this.acctDriver = new NasAcctDriver();		
		this.dbAcctDriverConfiguration = driverConfiguration;
	}

	@Override
	public String toString() {
		return dbAcctDriverConfiguration.toString();
	}
			
	private List<DBTypeAndValueTuple> getAcctFieldList(ApplicationRequest nasAcctRequest){
		List<DBTypeAndValueTuple> acctFiledList = new ArrayList<DBTypeAndValueTuple>();
		List<DbFiledMapping> fieldMappings =  dbAcctDriverConfiguration.getDbFiledMappingList();
		
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
					List<IDiameterAVP> attributeList = nasAcctRequest.getAVPList(attributeID);

			String attributeValue = null;
			if(attributeList!=null){
				for(IDiameterAVP iDiameterAvp :attributeList){
					if(iDiameterAvp!=null){
						attributeValue = iDiameterAvp.getStringValue(dbFiledMapping.getUseDictionaryValue());

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
	
	@Override
	protected void handleServiceRequest(ServiceRequest serviceRequest) throws DriverProcessFailedException {				
		try {
			handleNasAccountingRequest((ApplicationRequest)serviceRequest);
		} catch (DriverProcessFailedException e) {
			throw e;
		}	
	}
	
	private void handleNasAccountingRequest(ApplicationRequest serviceRequest) throws DriverProcessFailedException {
		LogManager.getLogger().info(MODULE, "Processing Request through Open Db Acct Driver: " + getName());
		List<DBTypeAndValueTuple> acctFiledList = getAcctFieldList(serviceRequest);
		int iStatusType = this.acctDriver.getAcctReqStatusType(serviceRequest);
		int result = -1;
		try{		
			switch(iStatusType){
			case DiameterAttributeValueConstants.START_RECORD:							
				if(isStoreStartRecordEnabled()) {
					result =  executeQuery(INSERT_QUERY_TYPE,getCDRInsertQuery(),acctFiledList,serviceRequest);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Store Interim Records : " + getStoreInterimRec() + ". Start entry not stored.");
				}
				if(result > 0)
					LogManager.getLogger().debug(MODULE, "Attributes Dumped in table:" + getInterimTableName());
				break;
				
			case DiameterAttributeValueConstants.EVENT_RECORD:
			case DiameterAttributeValueConstants.STOP_RECORD:
				// don't change the sequence of applying Delete and Insert query.
				if(getStoreStopRec()) {
					result =  executeQuery(INSERT_QUERY_TYPE,getCDRInsertQuery(),acctFiledList,serviceRequest);
					if(result > 0)
						LogManager.getLogger().debug(MODULE, "Attributes Dumped in table:" + getCdrTableName());
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Store Interim Record configuration is disabled. Interims will not be stored.");
				}
				if(isStoreUpdateRecordEnabled() && getRemoveInterimOnStop()) {
					result = executeQuery(DELETE_QUERY_TYPE, getCDRInterimDeleteQuery(), acctFiledList, serviceRequest);
					if(result > 0)
						LogManager.getLogger().debug(MODULE, "Attributes Remove From table:" + getInterimTableName());
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Remove Interim Update Record configuration is diabled. Interim update entries will not be removed.");
				}
				break;	    
	
		    case DiameterAttributeValueConstants.INTERIM_RECORD:	    
		    	if(isStoreUpdateRecordEnabled()) {
		    		if(!getStoreInterimRec()){ 
		    			result = executeQuery(UPDATE_QUERY_TYPE,getCDRInterimUpdateQuery() ,acctFiledList, serviceRequest);
		    			if(result > 0)
		    				LogManager.getLogger().debug(MODULE, "Attributes Updated in table:" + getInterimTableName());
		    			else {
		    			result = executeQuery(INSERT_QUERY_TYPE,getCDRInterimInsertQuery(), acctFiledList, serviceRequest);
		    			if(result > 0)
		    				LogManager.getLogger().debug(MODULE, "Attributes dumped in table:" + getInterimTableName());
						}
		    	}else {
		    			result = executeQuery(INSERT_QUERY_TYPE,getCDRInterimInsertQuery(), acctFiledList, serviceRequest);
		    			if(result > 0)
		    				LogManager.getLogger().debug(MODULE, "Attributes dumped in table:" + getInterimTableName());
		    		}	
		    	}else {
		    		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
		    			LogManager.getLogger().debug(MODULE, "Store Interim Record configuration is disabled. Interims will not be stored.");
		    	}
		    	break;
		    		    	
		    default:
		    	LogManager.getLogger().trace(MODULE ,"No support of OpenDB Driver For Request Status Type:" + iStatusType);
		    	throw new DriverProcessFailedException("No support of OpenDb Driver For Request Status Type:" + iStatusType);
			}			
		} catch (DriverProcessFailedException e) {
			throw e;				
		}
	}
	@Override
	public String getDsName(){
		return dbAcctDriverConfiguration.getDsName();
	}
	
	private String getCDRInsertQuery(){		
		return dbAcctDriverConfiguration.getCDRInsertQuery();		
	}
	
	private String getCDRInterimInsertQuery() {
		return dbAcctDriverConfiguration.getCDRInterimInsertQuery();
	}
	
	private String getCDRInterimUpdateQuery() {
		return dbAcctDriverConfiguration.getCDRInterimUpdateQuery();
	}
	
	private String getCDRInterimDeleteQuery() {
		return dbAcctDriverConfiguration.getCDRInterimDeleteQuery();
	}
	
	private String getInterimCdrIdDbField() {
		return dbAcctDriverConfiguration.getInterimCdrIdDbField();
	}
	
	private String getInterimTableName() {
		return dbAcctDriverConfiguration.getInterimTableName();
	}
	
	private String getCdrTableName() {
		return dbAcctDriverConfiguration.getCdrTableName();
	}
	
	private boolean getStoreInterimRec() {
		return dbAcctDriverConfiguration.getStoreInterimRec();
	}
	
	private boolean getStoreStopRec() {
		return dbAcctDriverConfiguration.getStoreStopRec();
	}
	
	private boolean getRemoveInterimOnStop() {
		return dbAcctDriverConfiguration.getRemoveInterimOnStop();
	}
	
	@Override
	public int getDbQueryTimeout() {
		return dbAcctDriverConfiguration.getDbQueryTimeout();
	}
	
	@Override
	public String getCallEndFieldName() {
		return dbAcctDriverConfiguration.getCallEndFieldName();
	}
	
	@Override
	public int getMaxQueryTimeoutCount() {
		return dbAcctDriverConfiguration.getMaxQueryTimeoutCount();
	}
	
	@Override
	public String getCallStartFieldName() {
		return dbAcctDriverConfiguration.getCallStartFieldName();
	}

	@Override
	public String getCreateDateFieldName() {
		return dbAcctDriverConfiguration.getCreateDateFieldName();
	}

	private boolean isStoreStartRecordEnabled() {
		return dbAcctDriverConfiguration.isStoreStartRecordEnabled();
	}
	private boolean isStoreUpdateRecordEnabled() {
		return dbAcctDriverConfiguration.isStoreUpdateRecordEnabled();
	}

	@Override
	public boolean getEnebled() {
		return dbAcctDriverConfiguration.getEnebled();
	}

	@Override
	public String getDbDateField() {
		return dbAcctDriverConfiguration.getDbDateField();
	}

	@Override
	public String getLastModifiedDateFieldName() {
		return dbAcctDriverConfiguration.getLastModifiedDateFieldName();
	}

	@Override
	protected void initInternal() throws DriverInitializationFailedException {
		if(!isStoreStartRecordEnabled() && !isStoreUpdateRecordEnabled()){
			throw new DriverInitializationFailedException("Both StoteCDRRecord and StoteInterinRecord disabled for driver: "+getName());
		}
		super.initInternal();
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}

	@Override
	public String getName() {
		return dbAcctDriverConfiguration.getDriverName();
	}

	@Override
	protected String getAcctSessionId(ServiceRequest request){
		
		IDiameterAVP  avp = ((ApplicationRequest)request).getAVP(DiameterAVPConstants.ACCOUNTING_SESSION_ID);
		if(avp != null) {
			avp.getStringValue(); 
		}
		return null;
	}

	@Override
	protected long getAcctSessionTimeValue(ServiceRequest request) {
		long lAcctSessionTime = 0;
		IDiameterAVP acctSessionTime = ((ApplicationRequest)request).getAVP(DiameterAVPConstants.ACCT_SESSION_TIME);
		if(acctSessionTime != null){
			lAcctSessionTime = acctSessionTime.getTime() * 1000;
		}
		return lAcctSessionTime;
	}

	@Override
	protected long getEventTimeValue(ServiceRequest request) {
		long lEventTimeStamp = 0;
		IDiameterAVP eventTimeStamp = ((ApplicationRequest)request).getAVP(DiameterAVPConstants.EVENT_TIMESTAMP);
		if(eventTimeStamp != null){
			lEventTimeStamp = eventTimeStamp.getTime();
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
		return DriverTypes.NAS_OPENDB_ACCT_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.NAS_OPENDB_ACCT_DRIVER.name();
	}

	@Override
	public String getMultivalDelimeter() {
		return this.dbAcctDriverConfiguration.getMultivalDelimeter();
	}
	@Override
	public void reInit() {
		this.dbAcctDriverConfiguration = (DBAcctDriverConfiguration)((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(dbAcctDriverConfiguration.getDriverInstanceId());
	}
}
