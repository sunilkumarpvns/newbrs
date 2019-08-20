package com.elitecore.elitesm.hibernate.core.base;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.ActivePassiveCommunicatorData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.CommunicatorData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import com.elitecore.elitesm.web.radius.radiusesigroup.RedundancyMode;
import org.apache.log4j.MDC;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.systemaudit.Data.ISystemAuditData;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditData;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditDetails;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditRelationData;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateDataSession;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;

import javax.xml.bind.JAXBException;

public class HBaseDataManager implements DataManager {
    
    private static final String MODULE = "HBaseDataManager";
	private IDataManagerSession dataManagerSession;
	public static final String REASON = ", Reason: ";
	public static final String FAILED_TO_CREATE = "Failed to create ";
    
    public final void setDataManagerSession(IDataManagerSession session) {
        this.dataManagerSession = session;
    }

    protected final IDataManagerSession getDataManagerSession(){
        return dataManagerSession;
    }
    
    protected final HibernateDataSession getHibernateDataSession(){
        
        if (dataManagerSession != null && dataManagerSession instanceof HibernateDataSession) {
            return (HibernateDataSession) dataManagerSession;
        }
        
        return null;
    }
    
    protected final Session getSession(){
        HibernateDataSession dataSession = getHibernateDataSession();
        if (dataSession != null){
            return dataSession.getSession();
        }
        return null;
    }
    protected Timestamp getCurrentTimeStamp(Connection connection) throws Exception {
    	String dbProductName;
    	Timestamp timestamp = null;

    	Statement stmt = connection.createStatement(); 
    	dbProductName = connection.getMetaData().getDatabaseProductName();
    	String queryStringDt = "SELECT SYSDATE FROM DUAL";	

    	if(dbProductName.equalsIgnoreCase("postgresql")){
    		queryStringDt = "SELECT CURRENT_TIMESTAMP";
    	}

    	ResultSet rs1 = stmt.executeQuery(queryStringDt);
    	if(rs1.next()){
    		timestamp = rs1.getTimestamp(1);
    	}
    	rs1.close();
    	return timestamp;

    }
    protected Object getValueObject(String javaClassName,String fieldValue) throws DataManagerException{
    	if(fieldValue == null)
    		return null;
    	Object object = new Object();
    	Logger.logDebug(MODULE, "method called ... javaclass is: "+javaClassName+ "fieldValue is: "+fieldValue);
    	try{
    		if(("java.lang.String").equalsIgnoreCase(javaClassName)){

    			object = new String(fieldValue);
    		}
    		else if(("java.lang.Integer").equalsIgnoreCase(javaClassName))
    		{

    			object = new Integer(fieldValue);
    		}
    		else if(("java.lang.Long").equalsIgnoreCase(javaClassName))
    		{

    			object = new Long(fieldValue);
    		}
    		else if(javaClassName != null && javaClassName.toUpperCase().contains("TIMESTAMP"))
    		{
    			object = getTimestampValue(fieldValue);
    		}
    		else if(("java.math.BigDecimal").equalsIgnoreCase(javaClassName))
    		{
    			object = new BigDecimal(fieldValue);
    		}
    		return object;
    	}catch (NumberFormatException e) {
			return null;
		} catch(Exception e)
    	{
    		throw new DataManagerException(e.getMessage(),e);
    	}
    }
    private Timestamp getTimestampValue(String value) throws ParseException, DataValidationException{
    	Timestamp dateToTimestamp = null;
    	if(value!=null && value.trim().length()>0){	
    		String longDateFormat = ConfigManager.get(ConfigConstant.DATE_FORMAT);
    		String shortDateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
    		try{
    			String[] parsePatterns = {longDateFormat,shortDateFormat};
				Date date = EliteUtility.parseDate(value, parsePatterns );
				dateToTimestamp = new Timestamp(date.getTime());
    		}catch(ParseException psEx){
    			throw new DataValidationException("Date should be in format:" + longDateFormat + " or " + shortDateFormat ,"Date Parsing Exception");
    		}			
    	}
    	return dateToTimestamp;	
    }
    
    
    
	protected void deleteObjectList(List<?> list,Session session){
		if(Collectionz.isNullOrEmpty(list) == false){
			for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
				Object obj = iterator.next();
				if(obj != null){
					session.delete(obj);
					session.flush();
				}	
			}
		}
	}
    
	protected void deleteObjectSet(Set<?> set,Session session){
		if(Collectionz.isNullOrEmpty(set) == false){
			for (Iterator<?> iterator = set.iterator(); iterator.hasNext();) {
				Object obj = iterator.next();
				if(obj != null){
					session.delete(obj);
					session.flush();
				}	
			}
		}
	}
	
	public void doAuditingJson(String JsonArray,IStaffData staffData,String actionAlias) throws DataManagerException {
		try{

					Session session = getSession();
					
					Criteria criteria = session.createCriteria(ActionData.class);
					criteria.add(Restrictions.eq("alias",actionAlias));
					List<ActionData> lstActionId = criteria.list();
					IActionData actionData = (ActionData)lstActionId.get(0);
					String actionId = actionData.getActionId();
		            String staffId = staffData.getStaffId();
					ISystemAuditData systemAuditData = new SystemAuditData();

					systemAuditData.setActionId(actionId);
					systemAuditData.setSystemUserId(staffId);
					systemAuditData.setSystemUserName(staffData.getUsername());
					systemAuditData.setRemarks("");
					systemAuditData.setTransactionId(null);
					systemAuditData.setClientIP(getClientIP());
					Date auditDate = new Date();
					systemAuditData.setAuditDate(auditDate);
					systemAuditData.setAuditId(staffData.getAuditId());
					systemAuditData.setAuditName(staffData.getAuditName());
					Logger.logDebug("MODULE", systemAuditData);
					
					session.save(systemAuditData);
					session.flush();
					criteria = session.createCriteria(SystemAuditDetails.class);
					
					SystemAuditDetails systemAuditDetails=new  SystemAuditDetails();
					systemAuditDetails.setSystemAuditId(systemAuditData.getSystemAuditId());
					systemAuditDetails.setHistory(JsonArray.getBytes());
					session.save(systemAuditDetails);
					session.flush();
					
					Logger.logDebug("MODULE", "Difference Save Succesfully");
					
				}catch(HibernateException hExp){
					hExp.printStackTrace();
					throw new DataManagerException(hExp.getMessage(), hExp);
				}catch(Exception exp){
					exp.printStackTrace();
					throw new DataManagerException(exp.getMessage(), exp);
				}
			
		}
	private String getClientIP(){
		String clientAddress = (String)MDC.get("remoteaddress");
		
		if(clientAddress == null){
			clientAddress = (String)MDC.get("restremoteaddress");
			
			if(clientAddress == null){
				clientAddress  = "Unknown";
			}
		}
		return clientAddress;
	}
	
	public Object verifyInstance(Class<?> className, String propertyValue, Session session) throws DataManagerException {

		Criteria criteria = null;
		try {
			criteria = session.createCriteria(className).add(Restrictions.eq("name", propertyValue));
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive , Reason: "+ hbe.getMessage(), hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive , Reason: "+ exp.getMessage(), exp);
		}
		return criteria.uniqueResult();
	}
	
	public void deleteExistInstance(Class<?> className,String propertyValue) throws DataManagerException{
		
		Session session =  HibernateSessionFactory.createSession(); 
		Criteria criteria = null;
		try {
			Transaction transaction= session.beginTransaction();
			criteria = session.createCriteria(className).add(Restrictions.eq("name", propertyValue));
			session.delete(criteria.uniqueResult());
			transaction.commit();
		}catch (ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete , Reason: " + exp.getMessage(), exp);
		}
	}
	
	public String getAuditId(String moduleName) throws DataManagerException {
		Session session = getSession();
        Criteria criteria = session.createCriteria(SystemAuditRelationData.class);
		criteria.add(Restrictions.eq("moduleName",moduleName));
		
		SystemAuditRelationData systemAuditRelationData = (SystemAuditRelationData) criteria.uniqueResult();
		
		if(systemAuditRelationData == null) {
			systemAuditRelationData = new SystemAuditRelationData();
			systemAuditRelationData.setModuleName(moduleName);
			
			String uuId = UUIDGenerator.generate();
			systemAuditRelationData.setAuditId(uuId);
			try{
				session.save(systemAuditRelationData);
				session.flush();
			} catch (ConstraintViolationException cve) {
				cve.printStackTrace();
				throw new DataManagerException("Failed to create " + systemAuditRelationData.getModuleName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
			} catch(HibernateException hbe){
				hbe.printStackTrace();
				throw new DataManagerException("Failed to create " + systemAuditRelationData.getModuleName() +", Reason: " +  hbe.getMessage(),hbe);
			} catch(Exception e){
				e.printStackTrace();
				throw new DataManagerException("Failed to create " + systemAuditRelationData.getModuleName() + ", Reason: "+e.getMessage(),e);
			}
			return systemAuditRelationData.getAuditId();
		} else {
			return systemAuditRelationData.getAuditId();
		}
	}
	
	public Integer getMaxOrderNumber(Session session, Object obj) throws DataManagerException {
		try {
			
			Criteria criteria = session.createCriteria(obj.getClass()).setProjection(Projections.max("orderNumber"));
			
			List<?>  maxOrderNumber = criteria.list();
			
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
				Integer orderNumber = (Integer) maxOrderNumber.get(0);
				return ++orderNumber;
			} else {
				return 1;
			}
			
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, "Failed to generate max order for : " + obj.getClass().getSimpleName() + ", Reason : " + hExp.getMessage());
			throw new DataManagerException( "Failed to generate max order for : " + obj.getClass().getSimpleName() + ", Reason : " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, "Failed to generate max order for : " + obj.getClass().getSimpleName() + ", Reason : " + exp.getMessage());
			throw new DataManagerException( "Failed to generate max order for : " + obj.getClass().getSimpleName() + ", Reason : "+ exp.getMessage(), exp);
		}
	}

	@Override
	public String create(Object object) throws DataManagerException {
		// "Intentionaly Left Blank"
		return "";
	}
	
	public Object verifyNameWithIngnoreCase(Class<?> className, String propertyValue, Session session, boolean getOperation) throws DataManagerException {

		Query query = null;
		Object data = null;
		try {
			query = session.createQuery("from "+ className.getName() +" where lower(name) = '" + propertyValue.toLowerCase() +"'");
			data = query.uniqueResult();

			if (getOperation && data == null) {
				throw new InvalidValueException("Policy not found");
			} else if (!getOperation && data != null) {
				throw new ConstraintViolationException("Duplicate name found", new SQLIntegrityConstraintViolationException("ORA-00001: unique constraint"), "ConstraintViolationException");
			}
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to create "+ propertyValue +", Reason: "+  EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive "+ propertyValue +", Reason: "+ hbe.getMessage(), hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive "+ propertyValue +", Reason: "+ exp.getMessage(), exp);
		}
		return data;
	}

	//Validate If Esi is Binded in Radius Esi Group
	public void verifyRadiusEsiIsBinded(List<RadiusESIGroupData> radEsiDataList, String esiInstanceName) throws JAXBException, DataManagerException {
		for (RadiusESIGroupData radEsiDatas:radEsiDataList) {
			String xmlDatas = new String(radEsiDatas.getEsiGroupDataXml());
			StringReader stringReader =new StringReader(xmlDatas.trim());

			RadiusEsiGroupConfigurationData esiConfigurationData = ConfigUtil.deserialize(stringReader, RadiusEsiGroupConfigurationData.class);

			if (RedundancyMode.NM.redundancyModeName.equalsIgnoreCase(esiConfigurationData.getRedundancyMode())){
				if(Collectionz.isNullOrEmpty(esiConfigurationData.getPrimaryEsiList()) == false){
					for (CommunicatorData primaryEsiNames:esiConfigurationData.getPrimaryEsiList()) {
						if(esiInstanceName.equalsIgnoreCase(primaryEsiNames.getName())){
							throw new DataManagerException("External System Interface "+ esiInstanceName +" is binded in Primary Esi of Radius Esi Group with name "+esiConfigurationData.getName());
						}
					}
				}
				if(Collectionz.isNullOrEmpty(esiConfigurationData.getFailOverEsiList()) == false){
					for (CommunicatorData failOverEsiNames:esiConfigurationData.getFailOverEsiList()) {
						if(esiInstanceName.equalsIgnoreCase(failOverEsiNames.getName())){
							throw new DataManagerException("External System Interface "+ esiInstanceName +" is binded in Secondary Esi of Radius Esi Group with name "+esiConfigurationData.getName());
						}
					}
				}
			}else if(RedundancyMode.ACTIVE_PASSIVE.redundancyModeName.equalsIgnoreCase(esiConfigurationData.getRedundancyMode())){
				if(Collectionz.isNullOrEmpty(esiConfigurationData.getActivePassiveEsiList()) == false){
					for (ActivePassiveCommunicatorData activePassiveEsiData:esiConfigurationData.getActivePassiveEsiList()) {
						if(esiInstanceName.equalsIgnoreCase(activePassiveEsiData.getActiveEsiName())){
							throw new DataManagerException("External System Interface "+ esiInstanceName +" is binded in Active Esi of Radius Esi Group with name "+esiConfigurationData.getName());
						}else if(esiInstanceName.equalsIgnoreCase(activePassiveEsiData.getPassiveEsiName())){
							throw new DataManagerException("External System Interface "+ esiInstanceName +" is binded in Passive Esi of Radius Esi Group with name "+esiConfigurationData.getName());
						}
					}
				}
			}
		}
	}
}