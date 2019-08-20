package com.elitecore.elitesm.blmanager.core.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.Session;
import org.w3c.dom.Document;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;


public abstract class BaseBLManager {
    
	private static final String MODULE = "BASE_BL_MANAGER";
    public static final boolean BY_ID = true;
	public static final boolean BY_NAME = false;
	private static final String FAILURE = "FAILURE";
	private static final String SUCCESS = "SUCCESS";
    
    private Timestamp systemTimeStemp;
    
    
    /*
     * @author kaushikVira
     * @param IDataManagerSession
     * @purpose close Session Safe way.
     */
    protected void closeSession( IDataManagerSession session ) throws DataManagerException {
        if(session != null)
            session.close();
    }
    
    /*
     * @author kaushikVira
     * @param IDataManagerSession
     * @purpose rollBack Session Safe way.
     */
    protected void rollbackSession( IDataManagerSession session ) throws DataManagerException {
        if(session != null)
            session.rollback();
    }
    
    /*
     * @author kaushikVira
     * @param IDataManagerSession
     * @purpose commit Session Safe way.
     */
    protected void commit( IDataManagerSession session ) throws DataManagerException {
        if(session != null)
            session.commit();
    }
    
    /*
     * @author kaushikVira
     * @Return System TimeStemp,First time i will return current system TimeStemp but 
     * after first call it will return same timeStemp which is return in first Call,Basically 
     * use this method to get timeStemp value in one transaction. 
     */
    protected Timestamp getSystemTimeStemp(){
        if(systemTimeStemp == null)
            systemTimeStemp = getCurrentTimeStemp();
        return systemTimeStemp;
    }
    
    /*
     * @author kaushikVira
     * @Return it give System CurrentTime TimeStemp
     */
    protected Timestamp getCurrentTimeStemp(){
        return new Timestamp(new Date().getTime());
    }
    
	protected  SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession  session){
		SystemAuditDataManager gracePolicyDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return gracePolicyDataManager;
	}
	public void format(Document document,ByteArrayOutputStream outputStream) throws IOException, TransformerFactoryConfigurationError, TransformerException {
		if(EliteUtility.isSunJava()){
			com.sun.org.apache.xml.internal.serialize.OutputFormat format = new com.sun.org.apache.xml.internal.serialize.OutputFormat(document);
			format.setLineWidth(0);
			format.setIndenting(false);
			format.setIndent(7);
			com.sun.org.apache.xml.internal.serialize.XMLSerializer serializer = new com.sun.org.apache.xml.internal.serialize.XMLSerializer(outputStream, format);
			serializer.serialize(document);
		}else{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(outputStream);
			DOMSource source = new DOMSource(document);
			transformer.transform(source, result);
		}
	}
	public String format(Document document) throws IOException, TransformerFactoryConfigurationError, TransformerException {
		StringWriter writer = new StringWriter();
		if(EliteUtility.isSunJava()){
			com.sun.org.apache.xml.internal.serialize.OutputFormat format = new com.sun.org.apache.xml.internal.serialize.OutputFormat(document);
			format.setLineWidth(0);
			format.setIndenting(false);
			format.setIndent(7);
			com.sun.org.apache.xml.internal.serialize.XMLSerializer serializer = new com.sun.org.apache.xml.internal.serialize.XMLSerializer(writer, format);
			serializer.serialize(document);
		}else{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(writer);
			DOMSource source = new DOMSource(document);
			transformer.transform(source, result);
		}
		return writer.toString();
	}
	
	/**
	 * Function used to insert records from GUI as well as REST API both.
	 * Function support single creation while inserting record through GUI as
	 * well as REST API and also support partially creation of records while
	 * performing bulk operation from REST API.
	 * 
	 * @param dataManagerClass
	 *            - provide datamanager class object of particular class.
	 * @param records
	 *            - record which are going to insert in database
	 * @param staffData
	 *            - maintain audit for user.
	 * @param configConstant
	 *            - particular module operation name.
	 * @param isPartialSuccess
	 *            - if true then partial bulk operation will be performed.
	 * @return - map of failure and success records list.
	 */
	public Map<String, List<Status>> insertRecords(Class<?> dataManagerClass,List<?> records, IStaffData staffData,  String configConstant, String isPartialSuccess) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DataManager dataManager = DataManagerFactory.getInstance().getDataManager(dataManagerClass, session);

		ArrayList<Status> successRecordList = new ArrayList<Status>();
		ArrayList<Status> failureRecordList = new ArrayList<Status>();

		Map<String, List<Status>> responseMap  = new HashMap<String,List<Status>>();

		try{
			if(Collectionz.isNullOrEmpty(records) == false){

				for (Iterator<?> iterator = records.iterator(); iterator.hasNext();) {
					session.beginTransaction();
					Object object =  iterator.next();
					try {
						String name = dataManager.create(object);
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session, staffData, configConstant);

						if(isPartialSuccess.equalsIgnoreCase("true")){
							commit(session);
							successRecordList.add(new Status(ResultCode.SUCCESS.responseCode, name + " created successfully"));
						}
					} catch (DataManagerException e) {
						if(isPartialSuccess.equalsIgnoreCase("true")){

							failureRecordList.add(new Status(RestUtitlity.getResultCode(e), e.getMessage()));
							rollbackSession(session);
						} else {
							rollbackSession(session);
							throw e;
						}
					}
				}
				responseMap.put(SUCCESS, successRecordList);
				responseMap.put(FAILURE, failureRecordList);
				if(isPartialSuccess.equalsIgnoreCase("false") || isPartialSuccess.isEmpty()){
					commit(session);
				}
			}
		}catch(DataManagerException exp){
			Logger.logTrace(MODULE, exp);
			throw exp;
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException("failed to perform bulk operation, Reason: "+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return responseMap;
	}
		
	/**
	 * Verifies the name of configuration which is going to be create against the existing configuration,
	 * and if it does not exist in the server then it will create/update the configuration else throws 
	 * <code>{@link DataManagerException}<code>
	 *     
	 * @param dataManagerClass
	 * @param name
	 * @param getOperation
	 * @param createOperation
	 * @return
	 * @throws DataManagerException when configuration exist with the name.
	 */
	public Object verifyNameWithIgnoreCase(Class<?> dataManagerClass,String name, boolean getOperation) throws DataManagerException{
		
		HBaseDataManager dataManager = new HBaseDataManager();
		Object verifyNameWithIngnoreCase = null;
		Session session = null; 
		try {
			session =  HibernateSessionFactory.createSession();
			 verifyNameWithIngnoreCase = dataManager.verifyNameWithIngnoreCase(dataManagerClass, name, session, getOperation);
		}catch (DataManagerException dmExp) {
			throw dmExp;
		}catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			if( session != null ){
				session.close();
			}
		}
		return verifyNameWithIngnoreCase;
	}
}
