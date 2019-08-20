package com.elitecore.elitesm.ws.sessionmgmt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.wsconfig.WebServiceConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSAttrFieldMapData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSDBFieldMapData;
import com.elitecore.elitesm.ws.BaseWebServiceBLManager;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.exception.EmptyAttributeFieldMappingException;
import com.elitecore.elitesm.ws.exception.SessionMgmtWebServiceException;
import com.elitecore.elitesm.ws.logger.Logger;



public class SessionMgmtWebServiceBLManager extends BaseWebServiceBLManager {
	private static final String DATABASE_CONFIGURATION_IS_NOT_FOUND = "Database configuration is not found";
	private static final String QUERY_STRING = "queryString :";
	private static final String WHERE = " where ";
	private static SessionMgmtDBConfiguration dbConfiguration;
	private static final String  MODULE="SessionManagementWebServiceBLManager";
	private static String query;

	private static final String DEFAULT_USERNAME_COLUMN = "USER_NAME";
	private static final String DEFAULT_FRAMED_IP_ADDRESS_COLUMN = "FRAMED_IP_ADDRESS";
	private static final String DEFAULT_SERVICE_TYPE_COLUMN = "SERVICE_TYPE";

	private static String userNameColumn = "USER_NAME";
	private static String framedIPAddressColumn = "FRAMED_IP_ADDRESS";
	private static String serviceTypeColumn = "SERVICE_TYPE";
	private static boolean isDBFieldMapExist = false;

	static{
		 setConfiguration();
	}
   
	public static void setConfiguration(){
		try{
			dbConfiguration = getDBConfiguration();
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error in initializing database configuration");
			Logger.logTrace(MODULE, e);
		}catch(Exception e){
			Logger.logError(MODULE, "Error in initializing database configuration");
			Logger.logTrace(MODULE, e);
		}
		try{
			query = getQuery(dbConfiguration);
			userNameColumn = getUserNameDBField(dbConfiguration);
			framedIPAddressColumn = getFramedIPAddressDBField(dbConfiguration);
			serviceTypeColumn = getServiceTypeDBField(dbConfiguration);
		}catch(SessionMgmtWebServiceException e){
			Logger.logError(MODULE, "Error in initializing query");
			Logger.logTrace(MODULE, e);
		}catch(Exception e){
			Logger.logError(MODULE, "Error in initializing query");
			Logger.logTrace(MODULE, e);
		}	
	}

	private static SessionMgmtDBConfiguration getDBConfiguration() throws DataManagerException, DatabaseConnectionException{



		WebServiceConfigBLManager configBLManager = new WebServiceConfigBLManager();

		IWSConfigData sessionMgmtDBConfigData = configBLManager.getSessionMgmtConfiguration();
		DatabaseDSData datasource = sessionMgmtDBConfigData.getDatasourceConfigInstance();
		String dbUrl = datasource.getConnectionUrl().trim();
		String dbUser = datasource.getUserName().trim();
		String dbPassword = datasource.getPassword().trim();
		String dbTable = sessionMgmtDBConfigData.getTableName().trim();
		Integer dbRecordFetchLimit = sessionMgmtDBConfigData.getRecordFetchLimit();

		Set<IWSDBFieldMapData> dbFieldMapSet = sessionMgmtDBConfigData.getWsDBFieldMapSet();
		List<IWSDBFieldMapData> dbFieldMapList=new ArrayList<IWSDBFieldMapData>(dbFieldMapSet);

		Set<IWSAttrFieldMapData> attrFieldMapSet =sessionMgmtDBConfigData.getWsAttrFieldMapSet();
		List<IWSAttrFieldMapData> attrFieldMapList=new ArrayList<IWSAttrFieldMapData>(attrFieldMapSet);
		String driverClass = getDriverClass(dbUrl);



		Logger.logInfo(MODULE, "Database Url       :"+dbUrl
				+ "\n\tDatabase Class     :"+driverClass
				+ "\n\tDatabase Username  :"+dbUser
				+ "\n\tDatabase Password  :"+dbPassword
				+ "\n\tDatabase Tablename :"+dbTable
				+ "\n\tRecord Fetch Limit :"+dbRecordFetchLimit);

		SessionMgmtDBConfiguration configuration = new SessionMgmtDBConfiguration(driverClass,dbUrl,dbUser,dbPassword);
		if(dbTable.trim().length()>0){
			configuration.setTableName(dbTable);
		}
		if(dbRecordFetchLimit!=null){
			configuration.setRecordFetchLimit(dbRecordFetchLimit);	
		}

		if(datasource.getMaximumPool() > 0 && datasource.getMinimumPool()>0 && datasource.getMaximumPool()> datasource.getMinimumPool()){
			configuration.setMaximumPool(datasource.getMaximumPool());
			configuration.setMaxIdle(datasource.getMinimumPool());
		}
		
		if(Collectionz.isNullOrEmpty(dbFieldMapList) == false){
			for (Iterator<IWSDBFieldMapData> iterator = dbFieldMapList.iterator(); iterator.hasNext();) {
				IWSDBFieldMapData iSubscriberDBFieldMapData = iterator.next();
				configuration.getDbFieldMap().put(iSubscriberDBFieldMapData.getKey(), iSubscriberDBFieldMapData.getFieldName());	
			}

		}

		Logger.logDebug(MODULE, "DB Field Map :"+configuration.getDbFieldMap());

		if(Collectionz.isNullOrEmpty(attrFieldMapList) == false){

			for (Iterator<IWSAttrFieldMapData> iterator = attrFieldMapList.iterator(); iterator.hasNext();) {
				IWSAttrFieldMapData attrFieldMapData = iterator.next();
				configuration.getAttributeFieldMap().put(attrFieldMapData.getAttribute(),attrFieldMapData.getFieldName());
			}

		}
		Logger.logDebug(MODULE, "Attribute Field Map :"+configuration.getAttributeFieldMap());

		return configuration;

	}




	private static String getQuery(SessionMgmtDBConfiguration dbConfiguration) throws SessionMgmtWebServiceException{
		String query=null;
		if(dbConfiguration!=null){
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT ");
			Map<String,String> map = dbConfiguration.getDbFieldMap();
			if(map!=null && !map.isEmpty()){
				isDBFieldMapExist=true;
				Set<String> set = map.keySet();
				Iterator<String> iterator =set.iterator();
				int size = map.size();
				int i=0;
				while(iterator.hasNext()){
					String key = iterator.next();
					String field  = map.get(key);

					if(i<=size-2){
						buffer.append("tcu.");
						buffer.append(field+" as \""+key+"\", ");

					}else{
						buffer.append("tcu.");
						buffer.append(field+" as \""+key+"\" ");
					}
					i++;
				}
				buffer.append(" FROM "+ dbConfiguration.getTableName()+" tcu");
				query =  buffer.toString();

			}else{
				buffer.append(" * FROM "+ dbConfiguration.getTableName()+" tcu");
				query =  buffer.toString();
				Logger.logWarn(MODULE, "DB Field Mapping is not found.");
				return query;
			}


		}else{
			throw new SessionMgmtWebServiceException("Database Configuration is not found.");
		}
		return query;
	}
	
	public Map<String,Map<String,String>> findByServiceType(String serviceType)  throws SQLException, DatabaseConnectionException{

		String whereClause = WHERE+serviceTypeColumn+" = ?";
		String sqlQuery = query +whereClause;
		Logger.logDebug(MODULE,QUERY_STRING+sqlQuery);
		String inParameter = getCriteria(serviceType);
		return getResult(sqlQuery, dbConfiguration,inParameter);

	}
	public Map<String,Map<String,String>> findByFramedIPAddress(String ipAddress) throws SQLException,DatabaseConnectionException{
		String whereClause =  WHERE+framedIPAddressColumn+" = ?";
		String sqlQuery = query +whereClause;
		Logger.logDebug(MODULE,QUERY_STRING+sqlQuery);
		String inParameter = getCriteria(ipAddress);
		return getResult(sqlQuery, dbConfiguration,inParameter);
	}


	public Map<String,Map<String,String>> findByUserName(String userName) throws SQLException, DatabaseConnectionException{

		String whereClause = WHERE+userNameColumn+" = ?";
		String sqlQuery = query +whereClause;
		Logger.logDebug(MODULE,QUERY_STRING+sqlQuery);
		String inParameter = getCriteria(userName);
		return getResult(sqlQuery, dbConfiguration,inParameter);

	}

	public Map<String,Map<String,String>> findByAttribute(String attribute,String value) 
	throws SQLException,DatabaseConnectionException, EmptyAttributeFieldMappingException,SessionMgmtWebServiceException{

		String attributeColumn = getAttributeDBField(dbConfiguration,attribute);
		String whereClause = WHERE+attributeColumn+" = ?";
		String sqlQuery = query +whereClause;
		Logger.logDebug(MODULE,QUERY_STRING+sqlQuery);
		String inParameter = getCriteria(value);
		return getResult(sqlQuery, dbConfiguration,inParameter);
	}

	/**
	 * DO NOT REMOVE THE COMMENTED TWO WEBSERVICES 
	 */
	
//	public Map<String,Map<String,String>> findByElapsedTime(String operator , int seconds) throws SQLException,Exception{
//		if(operator==null){
//			operator = " >= ";
//		}
//		String sqlQuery = "SELECT  a.*, " +
//		"SUBSTR((SYSTIMESTAMP-START_TIME) , INSTR((SYSTIMESTAMP-START_TIME),' ')+4 ,2) + "+
//		"SUBSTR((SYSTIMESTAMP-START_TIME) , INSTR((SYSTIMESTAMP-START_TIME),' ')+1 ,2)*60 + "+
//		"TRUNC(TO_NUMBER(SUBSTR((SYSTIMESTAMP-START_TIME),1, INSTR(SYSTIMESTAMP-START_TIME,' '))))*1440*60 "+ 
//		"as ELAPSED_TIME_IN_SECONDS " + 
//		"from "+dbConfiguration.getTableName()+" a " +
//		"where START_TIME IS NOT NULL "+ 
//		" and "+ 
//		"SUBSTR((SYSTIMESTAMP-START_TIME) , INSTR((SYSTIMESTAMP-START_TIME),' ')+4 ,2) + " + 
//		"SUBSTR((SYSTIMESTAMP-START_TIME) , INSTR((SYSTIMESTAMP-START_TIME),' ')+1 ,2)*60 + "+
//		"TRUNC(TO_NUMBER(SUBSTR((SYSTIMESTAMP-START_TIME),1, INSTR(SYSTIMESTAMP-START_TIME,' '))))*1440*60 "+
//		operator +" "+ seconds;
//
//
//		return getResult(sqlQuery, dbConfiguration);
//
//
//	}

//	public Map<String,Map<String,String>> getDatabyWhereClause(String whereClause) throws SQLException,Exception{
//		String queryString = query+" "+whereClause;
//		Logger.logDebug(MODULE,"queryString :"+queryString);
//		return getResult(queryString, dbConfiguration);
//	}

	private String getAttributeDBField(SessionMgmtDBConfiguration configuration,String attribute) throws EmptyAttributeFieldMappingException, SessionMgmtWebServiceException{
		if(configuration!=null){
			Map<String,String> attributeFieldMap = configuration.getAttributeFieldMap();
			if(!attributeFieldMap.isEmpty()){
				String field;
				field = attributeFieldMap.get(attribute);
				if(field==null){
					field = attributeFieldMap.get(attribute.toLowerCase());
				}
				if(field==null){
					field = attributeFieldMap.get(attribute.toUpperCase());
				}

				if(field!=null){
					return field;
				}else{
					throw new EmptyAttributeFieldMappingException("Attribute["+attribute+"] is not found in AttributeFieldMap.");
				}

			}else{
				throw new EmptyAttributeFieldMappingException("Attribute Field Map is not found");
			}
		}else{
			throw new SessionMgmtWebServiceException("Database Configuration is not found");
		}

	}

	private static String getUserNameDBField(SessionMgmtDBConfiguration configuration) throws SessionMgmtWebServiceException{
		if(configuration!=null){
			Map<String,String> attributeFieldMap = configuration.getAttributeFieldMap();
			if(!attributeFieldMap.isEmpty()){
				String field;
				field = attributeFieldMap.get("0:1");
				if(field==null){
					field = attributeFieldMap.get("1");
				}
				if(field==null){
					field = attributeFieldMap.get("User-Name");
				}

				if(field!=null){
					return field;
				}else{
					return DEFAULT_USERNAME_COLUMN;
					//throw new SessionMgmtWebServiceException("Attribute User-Name Field Map is not found");
				}

			}else{
				return DEFAULT_USERNAME_COLUMN;
				//throw new SessionMgmtWebServiceException("Attribute Field Map is not found");
			}

		}else{
			throw new SessionMgmtWebServiceException(DATABASE_CONFIGURATION_IS_NOT_FOUND);
		}

	}

	private static String getFramedIPAddressDBField(SessionMgmtDBConfiguration configuration) throws SessionMgmtWebServiceException{
		if(configuration!=null){
			Map<String,String> attributeFieldMap = configuration.getAttributeFieldMap();
			if(!attributeFieldMap.isEmpty()){
				String field;
				field = attributeFieldMap.get("0:8");
				if(field==null){
					field = attributeFieldMap.get("8");
				}
				if(field==null){
					field = attributeFieldMap.get("Framed-IP-Address");
				}

				if(field!=null){
					return field;
				}else{
					return DEFAULT_FRAMED_IP_ADDRESS_COLUMN;
					//throw new SessionMgmtWebServiceException("Attribute Framed-IP-Address Field Map is not found");
				}

			}else{
				return DEFAULT_FRAMED_IP_ADDRESS_COLUMN;
				//throw new SessionMgmtWebServiceException("Attribute Field Map is not found");
			}

		}else{
			throw new SessionMgmtWebServiceException(DATABASE_CONFIGURATION_IS_NOT_FOUND);
		}
	}

	private static String getServiceTypeDBField(SessionMgmtDBConfiguration configuration) throws SessionMgmtWebServiceException{
		if(configuration!=null){
			Map<String,String> attributeFieldMap = configuration.getAttributeFieldMap();
			if(!attributeFieldMap.isEmpty()){
				String field;
				field = attributeFieldMap.get("0:6");
				if(field==null){
					field = attributeFieldMap.get("6");
				}
				if(field==null){
					field = attributeFieldMap.get("Service-Type");
				}

				if(field!=null){
					return field;
				}else{
					return DEFAULT_SERVICE_TYPE_COLUMN;
					//throw new SessionMgmtWebServiceException("Attribute Service-Type Field Map is not found");
				}

			}else{
				return DEFAULT_SERVICE_TYPE_COLUMN;
				//throw new SessionMgmtWebServiceException("Attribute Field Map is not found");
			}

		}else{
			throw new SessionMgmtWebServiceException(DATABASE_CONFIGURATION_IS_NOT_FOUND);
		}
	}


	@Override
	protected boolean isDBFieldMappingExist() {
		return isDBFieldMapExist;
	}

	public static void main(String args[]){
		System.out.println("Before Created");
		/*% java org.apache.axis.wsdl.Java2WSDL -o SessionManagerWS.wsdl
	    -l"http://localhost:8080/axis/services/SessionManagerWS"
	    -n  "urn:SessionManagerWS" -p"com.elitecore.elitesm.ws" "urn:SessionManagerWS"
	    com.elitecore.elitesm.ws.SessionManagerWS*/
		org.apache.axis.wsdl.WSDL2Java.main(args);
		System.out.println("Successfully Created");

	}

	public final SessionMgmtDBConfiguration getDbConfiguration() {
		return dbConfiguration;
	}
	
	public final String getSessionQuery() {
		return query;
	}
}
