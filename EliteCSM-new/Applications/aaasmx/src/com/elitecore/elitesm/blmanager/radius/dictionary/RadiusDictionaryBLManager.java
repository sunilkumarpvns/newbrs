package com.elitecore.elitesm.blmanager.radius.dictionary;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.hibernate.HibernateException;

import com.elitecore.commons.base.Strings;
import com.elitecore.coreradius.commons.util.constants.LengthFormat;
import com.elitecore.coreradius.commons.util.constants.PaddingType;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.NullValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.radius.clientprofile.ClientProfileDataManager;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.datamanager.radius.dictionary.DictionaryDataManager;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.exception.DuplicateDictionaryConstraintExcpetion;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.constants.DictionaryConstant;
import com.elitecore.elitesm.util.logger.Logger;

public class RadiusDictionaryBLManager extends BaseBLManager {
    
    private static final String MODULE = "DICTIONARY";
    
    
    /**
     * @return
     * @throws DataManagerException
     */
    
    public List getAllDictionaryList() throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        List lstDictionaryList;
        
        if (dictionaryDataManager == null )
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        
        session.beginTransaction();	
        
        lstDictionaryList = dictionaryDataManager.getAllList();
        
        session.commit();
        session.close();
        
        }
        catch(Exception e){
        	e.printStackTrace();
        	session.rollback();
        	session.close();
        	
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			closeSession(session);
		}
        
        return lstDictionaryList;
    }
    
    public List getDictionaryList( ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try{
        	 List lstDictionaryList;
             
             lstDictionaryList = dictionaryDataManager.getList();
             return lstDictionaryList;
        }catch(DataManagerException e){
        	e.printStackTrace();
        	throw new DataManagerException("Failed to retrive dictionary list. reason :"+e.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    public List<DictionaryData> getOnlyDictionaryDataList() throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	List<DictionaryData> lstDictionaryDataList;
            
            lstDictionaryDataList = dictionaryDataManager.getOnlyDictionaryDataList();
            return lstDictionaryDataList;
        }catch(DataManagerException e){
        	e.printStackTrace();
        	throw new DataManagerException("Failed to retrive dictionary list. reason :"+e.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    public List<DictionaryData> getDictionaryDataList() throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	 List<DictionaryData> lstDictionaryDataList;
             
             lstDictionaryDataList = dictionaryDataManager.getAllList();
             return lstDictionaryDataList;
        }catch(DataManagerException e){
        	e.printStackTrace();
        	throw new DataManagerException("Failed to retrive dictionary list. reason :"+e.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    public List getDictionaryById( Collection<String> colDictionaryId ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
             
             List<DictionaryData> lstDictionaryData = new ArrayList<DictionaryData>();
             Iterator<String> itr = colDictionaryId.iterator();
             DictionaryData dictionaryData;
             
             while (itr.hasNext()) {
                 dictionaryData = new DictionaryData();
                 String dictionaryId = (String) itr.next();
                 if (Strings.isNullOrEmpty(dictionaryId) == false ) {
                     dictionaryData.setDictionaryId(dictionaryId);
                     dictionaryData = (DictionaryData) dictionaryDataManager.getList(dictionaryData).get(0);
                     lstDictionaryData.add(dictionaryData);
                 }
             }
             return lstDictionaryData;
        }catch(DataManagerException e){
        	e.printStackTrace();
        	throw new DataManagerException("Failed to retrive dictionary list. reason :"+e.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    public List getAllDictionaryById( Collection<String> colDictionaryId ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
    	if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	}
    	
        try {
        	
            List<DictionaryData> lstDictionaryData = new ArrayList<DictionaryData>();
            Iterator<String> itr = colDictionaryId.iterator();
            DictionaryData dictionaryData;
            
            while (itr.hasNext()) {
                dictionaryData = new DictionaryData();
                String dictionaryId =  itr.next();
                if ( Strings.isNullOrEmpty(dictionaryId) == false ) {
                    dictionaryData.setDictionaryId(dictionaryId);
                    dictionaryData = (DictionaryData) dictionaryDataManager.getAllList(dictionaryData).get(0);
                    lstDictionaryData.add(dictionaryData);
                }
            }
            
            return lstDictionaryData;
        }catch(DataManagerException e){
        	e.printStackTrace();
        	throw new DataManagerException("Failed to retrive dictionary list. reason :"+e.getMessage());
        }finally{
        	closeSession(session);
        }
    }
public List<DictionaryData> getAllDictionaryById( Collection<String> colDictionaryId,IStaffData staffData, String actionAlias ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        List<DictionaryData> lstDictionaryData = new ArrayList<DictionaryData> ();
        
        if (dictionaryDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        
        session.beginTransaction();
        
        
        Iterator<String> itr = colDictionaryId.iterator();
        DictionaryData dictionaryData;
        
        while (itr.hasNext()) {
            dictionaryData = new DictionaryData();
            String dictionaryId =  itr.next();
            if ( Strings.isNullOrEmpty(dictionaryId) == false ) {
                dictionaryData.setDictionaryId(dictionaryId);
                dictionaryData = (DictionaryData) dictionaryDataManager.getAllList(dictionaryData).get(0);
                lstDictionaryData.add(dictionaryData);
            }
        }
        String transactionId = "";
        systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
        
        session.commit();
        session.close();
        
        }
        catch(Exception e){
        	e.printStackTrace();
        	session.rollback();
        	session.close();
        	
        	throw new DataManagerException("Action failed :"+e.getMessage());
        } finally {
        	closeSession(session);
        }
        return lstDictionaryData;
    }
    
    
    
    
    public List getDictionaryParametersById( Collection colDictionaryId ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
       
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try{
        	
              List lstDictionaryDetailData = new ArrayList();
              List lstDictionaryData = getAllDictionaryById(colDictionaryId);
              Iterator itrDictionaryData = lstDictionaryData.iterator();
              DictionaryData dictionaryData;
              while (itrDictionaryData.hasNext()) {
                  dictionaryData = (DictionaryData) itrDictionaryData.next();
                  Iterator itrDictionaryDetail = dictionaryData.getDictionaryParameterDetail().iterator();
                  while (itrDictionaryDetail.hasNext()) {
                      lstDictionaryDetailData.add((DictionaryParameterDetailData) itrDictionaryDetail.next());
                  }
              }
              return lstDictionaryDetailData;
        } catch(DataManagerException e){
        	e.printStackTrace();
        	throw new DataManagerException("Failed to retrive dictionary list. reason :"+e.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    public List<DictionaryData> getDictionariesWithParametersById( Collection<String> colDictionaryId ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
       
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
            List<DictionaryData> lstDictionaryData = getAllDictionaryById(colDictionaryId);
            return lstDictionaryData;
        } catch(DataManagerException e){
        	e.printStackTrace();
        	throw new DataManagerException("Failed to retrive dictionary list. reason :"+e.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    // / Private methods to create data managers for this BL Manager. ///
    
    public void updateStatus( List<String> lstdictionaryId ,String commonStatusId) throws DataManagerException,DataValidationException,ConstraintViolationException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        Date currentDate = new Date();
        
        if (dictionaryDataManager == null ){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
            
            if (lstdictionaryId != null) {
                updateStatusValidate(lstdictionaryId, commonStatusId);
                session.beginTransaction();
                for ( int i = 0; i < lstdictionaryId.size(); i++ ) {
                    if (lstdictionaryId.get(i)  != null){
                    	String dictionaryId = lstdictionaryId.get(i);
                    	String transactionId = lstdictionaryId.get(i).toString();
                    	dictionaryDataManager.updateStatus(dictionaryId, commonStatusId, new Timestamp(currentDate.getTime()));
                    }
                }
                session.commit();
                session.close();
            } else {
                session.rollback();
                session.close();
                throw new DataManagerException("Data Manager implementation not found for ");
            }
        }
        catch (DataManagerException e) {
            session.rollback();
            session.close();
            throw new DataManagerException("Action failed : " + e.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    public List getDictionaryParameterDetailList( IDictionaryParameterDetailData dictionaryParameterDetailData ) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        List lstDictionaryDetailList;
        
        if (dictionaryDataManager == null) {
        	throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); 
        }
        
        try{
        	  lstDictionaryDetailList = dictionaryDataManager.getDictionaryParameterDetailList(dictionaryParameterDetailData);
              return lstDictionaryDetailList;
        }catch(DataManagerException e){
        	throw new DataManagerException("Failed to retrive dictionary list: " + e.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    public void createDictionary( IDictionaryData dictionaryData) throws DataManagerException,
                                                        DataValidationException,
                                                        ConstraintViolationException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        ClientProfileDataManager clientProfileDataManager = getClientProfileDataManager(session);
        
        if (dictionaryDataManager == null || clientProfileDataManager==null){ 
        	throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); 
        }
        
        try {
            createValidate(dictionaryData);
            session.beginTransaction();
            DictionaryData dicData = new DictionaryData();
            dicData.setVendorId(dictionaryData.getVendorId());
            List lstDictionaryData = dictionaryDataManager.getAllList(dicData);
            if (lstDictionaryData == null || lstDictionaryData.size() <= 0) {
                
            	dictionaryDataManager.create(dictionaryData);
            	VendorData vendorData =clientProfileDataManager.getVendorData(dictionaryData.getVendorId());
            	if(vendorData==null){
            		vendorData = new VendorData();
            		vendorData.setVendorId(dictionaryData.getVendorId());
            		vendorData.setVendorName(dictionaryData.getName());
            		clientProfileDataManager.createVendor(vendorData);
            	}
            	 
                session.commit();
                session.close();
            } else {
                throw new DuplicateDictionaryConstraintExcpetion("Dictionary " + dictionaryData.getName() + " with Vendor Number " + dictionaryData.getVendorId() + " already exists");
            }
        }
        catch (ConstraintViolationException e) {
            session.rollback();
            session.close();
            throw e;
            
        }
        catch (DataValidationException e) {
            session.close();
            throw e;
        }
        catch (DataManagerException exp) {
            session.rollback();
            session.close();
            throw new DataManagerException("Create Operation failed : " + exp.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    public void updateBasicDetails( IDictionaryData dictionaryData ,IStaffData staffData, String actionAlias) throws DataManagerException,
                                                                    ConstraintViolationException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        Date currentDate = new Date();
        
        if (dictionaryDataManager == null || systemAuditDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
            updateBasicDetailsValidate(dictionaryData);
            session.beginTransaction();
            
            dictionaryDataManager.updateBasicDetails(dictionaryData, new Timestamp(currentDate.getTime()));
            String transactionId = dictionaryData.getDictionaryId();
            systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
            
            session.commit();
            session.close();
        }
        catch (ConstraintViolationException e) {
            session.rollback();
            session.close();
            throw e;
        }
        catch (DataValidationException e) {
            session.rollback();
            session.close();
            throw e;
        }
        
        
        catch (DataManagerException exp) {
            session.rollback();
            session.close();
            throw new DataManagerException("Action failed : " + exp.getMessage());
        }finally{
        	closeSession(session);
        }
    }
    
    public void delete( List<String> lstdictionaryId) throws DataManagerException, ConstraintViolationException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
            session.beginTransaction();
                if (lstdictionaryId != null) {
                    for ( int i = 0; i < lstdictionaryId.size(); i++ ) {
                        if (lstdictionaryId.get(i) != null) {
                        	String dictionaryId = lstdictionaryId.get(i);
                            String transactionId = lstdictionaryId.get(i).toString();
                            dictionaryDataManager.delete(dictionaryId);
                        }
                    }
                } else {
                    throw new DataManagerException("Empty Dictionary to be deleted");
                }
                session.commit();
            } catch (DataManagerException exp) {
            	exp.printStackTrace();
            	Logger.logTrace(MODULE, "Error While deleting Dictionary"+ exp.getMessage());
                session.rollback();
                //Logger.logTrace(MODULE, "Error While deleting Dictionary" + exp.getMessage());
                throw new DataManagerException("Delete Dictionary Action failed : " + exp.getMessage());
            } catch (Exception e) {
                session.rollback();
                Logger.logTrace(MODULE, "Error While deleting Dictionary" + e.getMessage());
                throw new DataManagerException("Delete Dictionary Action failed : " + e.getMessage());
            } finally {
                closeSession(session);
            }
    }
    
    public Hashtable getDatatype( ) throws DataManagerException {
        IDataManagerSession session = null;
        List lstDataType;
        Hashtable htab_strName_strDataTypeId = new Hashtable();
        try {
            
            session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
            
            if (dictionaryDataManager == null)
                throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
            
            DataTypeData iDatatypeData = null;
            lstDataType = dictionaryDataManager.getDatatype();
            if (lstDataType != null) {
                for ( int i = 0; i < lstDataType.size(); i++ ) {
                    iDatatypeData = (DataTypeData) lstDataType.get(i);
                    htab_strName_strDataTypeId.put(iDatatypeData.getName(), iDatatypeData.getDataTypeId());
                }
            }
            session.close();
            return htab_strName_strDataTypeId;
        }
        catch (HibernateException hExp) {
            session.close();
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        catch (Exception exp) {
            exp.printStackTrace();
            session.close();
            throw new DataManagerException(exp.getMessage(), exp);
        }finally{
        	closeSession(session);
        }
    }
    
    /**
     * @return Returns Data Manager instance for dictionary data.
     */
    public DictionaryDataManager getDictionaryDataManager( IDataManagerSession session ) {
        DictionaryDataManager dictionaryDataManager = (DictionaryDataManager) DataManagerFactory.getInstance().getDataManager(DictionaryDataManager.class, session);
        return dictionaryDataManager;
    }
    
    public void updateStatusValidate( List lstdictionaryId ,
                                      String commonStatusId ) throws DataValidationException {
        
        if (EliteGenericValidator.isBlankOrNull(commonStatusId)) { throw (new NullValueException("Invalid Dictionary Status", (MODULE + "." + "status").toLowerCase())); }
        
    }
    
    public void createValidate( IDictionaryData dictionaryData ) throws DataValidationException {
        
        // Name
        if (EliteGenericValidator.isBlankOrNull(dictionaryData.getName())) { throw (new NullValueException("Invalid Dictionary Name", (MODULE + "." + "name").toLowerCase())); }
        
        // Editable
        if (EliteGenericValidator.isBlankOrNull(dictionaryData.getEditable())) { throw (new NullValueException("Invalid Dictionary Editable", (MODULE + "." + "editable").toLowerCase())); }
        
        // CommonStatusId
        if (EliteGenericValidator.isBlankOrNull(dictionaryData.getCommonStatusId())) { throw (new NullValueException("Invalid Dictionary Status", (MODULE + "." + "status").toLowerCase())); }
        
        // ModalNumber
        if (EliteGenericValidator.isBlankOrNull(dictionaryData.getModalNumber())) { throw (new NullValueException("Invalid Dictionary Modal Number", (MODULE + "." + "ModalNumber").toLowerCase())); }
        
        // CreatedByStaffId
        if (EliteGenericValidator.isBlankOrNull(dictionaryData.getCreatedByStaffId())) { throw (new NullValueException("Invalid Dictionary Created By Staff ID", (MODULE + "." + "createdbystaffid")
                .toLowerCase())); }
        
        // CreateDate
        if (EliteGenericValidator.isBlankOrNull(dictionaryData.getCreateDate())) { throw (new NullValueException("Invalid Dictionary Create Date", (MODULE + "." + "createdate").toLowerCase())); }
        
        // SystemGenerated
        if (EliteGenericValidator.isBlankOrNull(dictionaryData.getSystemGenerated())) { throw (new NullValueException("Invalid Dictionary System Generated", (MODULE + "." + "systemgenerated")
                .toLowerCase())); }
        
    }
    
    public void updateBasicDetailsValidate( IDictionaryData dictionaryData ) throws DataValidationException {
        
        // Name
        if (EliteGenericValidator.isBlankOrNull(dictionaryData.getName())) { throw (new NullValueException("Invalid Dictionary Name", (MODULE + "." + "name").toLowerCase())); }
        
        // CommonStatusId
        if (EliteGenericValidator.isBlankOrNull(dictionaryData.getCommonStatusId())) { throw (new NullValueException("Invalid Dictionary Status", (MODULE + "." + "status").toLowerCase())); }
        
        // DictionaryId
        if (Strings.isNullOrBlank(dictionaryData.getDictionaryId())) { throw (new NullValueException("Invalid Dictionary Dictionary ID", (MODULE + "." + "dictionaryid").toLowerCase())); }
        
    }
    
    public String getDictionaryParameterName( String vendorId ,
                                              String dictionaryParameterId ) throws DataManagerException {
        
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        String dictionaryParameterName = null;
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
            dictionaryParameterName = dictionaryDataManager.getDictionaryParameterName(vendorId, dictionaryParameterId);
        }
        catch (DataManagerException exp) {
            exp.printStackTrace();
            throw new DataManagerException("Basic Details Update Operation failed : " + exp.getMessage());
        }finally{
        	closeSession(session);
        }
        return dictionaryParameterName;
    }
  
    public String getDictionaryParameterDetail( String vendorId , String dictionaryParameterId ) throws DataManagerException {

    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
    	String dictionaryWithParameterName = null;
    	
    	if (dictionaryDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	}
    	
    	try {
    		dictionaryWithParameterName = dictionaryDataManager.getDictionaryParamDetail(vendorId, dictionaryParameterId);
    	}
    	catch (DataManagerException exp) {
    		exp.printStackTrace();
    		throw new DataManagerException("Basic Details Update Operation failed : " + exp.getMessage());
    	}finally{
    		closeSession(session);
    	}
    	return dictionaryWithParameterName;
    }
    
    public List getDictionaryParameterDetailList(IDictionaryParameterDetailData dictionaryParameterDetailData, String criteriaName) throws DataManagerException{
    		
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        List lstDictionaryDetailList;
        
        if (dictionaryDataManager == null) { throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); }
        
        try {
        	  lstDictionaryDetailList = dictionaryDataManager.getDictionaryParameterDetailList(dictionaryParameterDetailData, criteriaName);
              return lstDictionaryDetailList;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive parameter details list : " + e.getMessage());
		}finally{
			closeSession(session);
		}
    }
    
    public List getDictionaryParameterDetailAllList() throws DataManagerException{
    	
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        List lstDictionaryParameterList;
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	lstDictionaryParameterList = dictionaryDataManager.getDictionaryParameterDetailAllList();
            return lstDictionaryParameterList;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive parameter details list : " + e.getMessage());
		}finally{
			closeSession(session);
		}
    }
    
    public List<DictionaryParameterDetailData> getOnlyDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        List<DictionaryParameterDetailData> dictionaryParameterList;
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	dictionaryParameterList = dictionaryDataManager.getOnlyDictionaryParameterList(searchNameOrAttributeId);
            return dictionaryParameterList;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive parameter details list : " + e.getMessage());
		}finally{
			closeSession(session);
		}
    }
    
    public DictionaryParameterDetailData getOnlyDictionaryParametersByAttributeId(String attributeId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        DictionaryParameterDetailData dictionaryParameterDetailData;
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	dictionaryParameterDetailData = dictionaryDataManager.getOnlyDictionaryParametersByAttributeId(attributeId);
            return dictionaryParameterDetailData;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive parameter details list : " + e.getMessage());
		}finally{
			closeSession(session);
		}
    }
    
    public List getDictionaryParameterDetailAllList(String searchByName) throws DataManagerException{
    	
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        List lstDictionaryParameterList;
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	 lstDictionaryParameterList = dictionaryDataManager.getDictionaryParameterDetailAllList(searchByName);
             return lstDictionaryParameterList;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive parameter details list : " + e.getMessage());
		}finally{
			closeSession(session);
		}
    }
    
    public List getDictionaryParameterDetailByIdList(List<String> dictIdList,String searchByName) throws DataManagerException {
        List lstDictionaryParameterList = new ArrayList();
        IDictionaryParameterDetailData dictionaryParameterSearchData = new DictionaryParameterDetailData();

        if(dictIdList != null && dictIdList.size() > 0) {
                for(Iterator<String> iter = dictIdList.iterator();iter.hasNext();) {
                String dictionaryId = iter.next();
                
                    if (Strings.isNullOrEmpty(dictionaryId) == false ) {
                        dictionaryParameterSearchData.setDictionaryId(dictionaryId);
                        List tmpList = getDictionaryParameterDetailList(dictionaryParameterSearchData,searchByName);
                        lstDictionaryParameterList.addAll(tmpList);
                    }
                }    
        }
        return lstDictionaryParameterList;
    }
    public List getDictionaryListByParameter(Collection<String> dictIdList, String searchByName) throws DataManagerException{
    	List lstDictionary = new ArrayList();
    	if(dictIdList!=null && dictIdList.size()>0){
    		
    		for(Iterator<String> iter = dictIdList.iterator();iter.hasNext();){
    			String dictionaryId= iter.next();
    				if(Strings.isNullOrEmpty(dictionaryId) == false){
    					DictionaryData dictionaryData = getDictionariesByParams( dictionaryId, searchByName);
    					lstDictionary.add(dictionaryData);
    				}
    		}
    		
    	}
    	return lstDictionary;
    }
    public List getDictionaryListByParameter(IDictionaryParameterDetailData dictionaryParameterSearchData, String searchByName) throws DataManagerException{
    	List lstDictionary = new ArrayList();

    	String dictionaryId= dictionaryParameterSearchData.getDictionaryId();
    	if( Strings.isNullOrEmpty(dictionaryId) == false) {
    		DictionaryData dictionaryData = getDictionariesByParams( dictionaryId, searchByName);
    		lstDictionary.add(dictionaryData);
    	}

    	return lstDictionary;
    }
	public DictionaryData getDictionariesByParams(String dictionaryId,String searchByName) throws DataManagerException{
		DictionaryData dictionaryData = null;
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	  dictionaryData	 = dictionaryDataManager.getDictionaryByName(dictionaryId,searchByName);
              return dictionaryData;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive dictionary data : " + e.getMessage());
		}finally{
			closeSession(session);
		}
	}
	
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }
    public ClientProfileDataManager getClientProfileDataManager(IDataManagerSession session){
    	ClientProfileDataManager clientProfileDataManager = (ClientProfileDataManager)DataManagerFactory.getInstance().getDataManager(ClientProfileDataManager.class, session);
        return clientProfileDataManager;
    }
    public Integer getDictionaryIdByName(String name) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	  IDictionaryParameterDetailData paramData = dictionaryDataManager.getDictionaryIdByName(name);
              return paramData.getVendorParameterId();
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive dictionary id : " + e.getMessage());
		}finally{
			closeSession(session);
		}
    }
    
    public IDictionaryParameterDetailData getDictionaryParamDetailByName(String name)throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	 IDictionaryParameterDetailData paramData = dictionaryDataManager.getDictionaryIdByName(name);
             return paramData;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive dictionary id : " + e.getMessage());
		}finally{
			closeSession(session);
		}
    }

	public ArrayList<DictionaryParameterDetailData> getDictionaryParameterDetailListById(Long dictionaryId) throws DataManagerException {
		ArrayList<DictionaryParameterDetailData> arrList=new ArrayList<DictionaryParameterDetailData>();
		List<DictionaryParameterDetailData> list=null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	
             if(dictionaryId != null) {
                  list = dictionaryDataManager.getDictionaryParameterDetailList(dictionaryId);
             }      
             //     printParameters(list,null);
             for (Iterator iterator = list.iterator(); iterator.hasNext();) {
             	DictionaryParameterDetailData  parameterDetailData = (DictionaryParameterDetailData) iterator.next();
            		arrList.add(parameterDetailData);	
     			
     		}
             return arrList;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive dictionary details : " + e.getMessage());
		}finally{
			closeSession(session);
		}
	}
	public DictionaryParameterDetailData getDictionaryParameterDetailData(String dictionaryParameterId) throws DataManagerException {
		ArrayList<DictionaryParameterDetailData> arrList=new ArrayList<DictionaryParameterDetailData>();
		List<DictionaryParameterDetailData> list=null;
		DictionaryParameterDetailData dictionaryParameterDetailData=null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	
             if(dictionaryParameterDetailData != null) {
             	dictionaryParameterDetailData = dictionaryDataManager.getDictionaryParameterDetailData(dictionaryParameterId);
             }      
       
             return dictionaryParameterDetailData;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive dictionary details : " + e.getMessage());
		}finally{
			closeSession(session);
		}
	}
	
	public DictionaryData getDictionaryDataByVendor(long vendorId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        DictionaryData dictionaryData= null;
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	  dictionaryData = dictionaryDataManager.getDictionaryDataByVendor(vendorId);
              return dictionaryData;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive dictionary details : " + e.getMessage());
		}finally{
			closeSession(session);
		}
	}

	public DictionaryData getDictionaryById(String dictionaryId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        
        if (dictionaryDataManager == null){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        
        try {
        	DictionaryData dictionaryData = new DictionaryData();
    		if (dictionaryId != null) {
    			dictionaryData = dictionaryDataManager.getDictionaryData(dictionaryId);
    		}

    		return dictionaryData;
        } catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive dictionary details : " + e.getMessage());
		}finally{
			closeSession(session);
		}
	}
  
	public void createDictonaryFromXML(String xmlString){
		
		//parse function

		
	}

	public void create(IDictionaryData dictionaryData) throws DataManagerException {
		

        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (dictionaryDataManager == null || systemAuditDataManager==null) { 
        		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); 
        }
        
        try {
            createValidate(dictionaryData);
            session.beginTransaction();
            DictionaryData dicData = new DictionaryData();
            dicData.setVendorId(dictionaryData.getVendorId());
            List lstDictionaryData = dictionaryDataManager.getAllList(dicData);
            Logger.logDebug(MODULE,"List :"+lstDictionaryData.size());
            if (lstDictionaryData == null || lstDictionaryData.size() <= 0) {
                
            	dictionaryDataManager.create(dictionaryData);
                
                session.commit();
                session.close();
            } else {
                throw new DuplicateDictionaryConstraintExcpetion("Dictionary " + dictionaryData.getName() + " with Vendor Number " + dictionaryData.getVendorId() + " already exists");
            }
        }
        catch (ConstraintViolationException e) {
            session.rollback();
            session.close();
            throw e;
            
        }
        catch (DataValidationException e) {
            session.close();
            throw e;
        }
        catch (DataManagerException exp) {
            session.rollback();
            session.close();
            throw new DataManagerException("Create Operation failed : " + exp.getMessage());
        }finally{
        	closeSession(session);
        }
	}

	public DictionaryData updateDictionary(IDictionaryData dictionaryData) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        DictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        DictionaryData dicData = new DictionaryData();
        
        if (dictionaryDataManager == null || systemAuditDataManager==null) { 
        	throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); 
        }
       
        try {
            session.beginTransaction();
            
            dicData.setVendorId(dictionaryData.getVendorId());
            List lstDictionaryData = dictionaryDataManager.getAllList(dicData);
            
            if (lstDictionaryData != null) {
                
            	dicData=dictionaryDataManager.updateDictionary(dictionaryData);
                
                session.commit();
                session.close();
            }
        }
        catch (ConstraintViolationException e) {
            e.printStackTrace();
        	session.rollback();
            session.close();
            throw e;
            
            
        }
        catch (DataValidationException e) {
            session.close();
            throw e;
        }
        catch (DataManagerException exp) {
            session.rollback();
            session.close();
            throw new DataManagerException("Create Operation failed : " + exp.getMessage());
        }finally{
        	closeSession(session);
        }
		return dicData;
	}
	
	
	
	/*
	 * Convert to XML String
	 */

//	public String getXMLString(IDictionaryData dictionaryData) throws ParserConfigurationException, IOException, TransformerFactoryConfigurationError, TransformerException{
//		String xmlString = "";
//		
//		Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
//
//		//set dictionary data
//		Element attributeList = xmlDocument.createElement(DictionaryConstant.TAG_ATTRIBUTE_LIST);
//		attributeList.setAttribute(DictionaryConstant.ATTR_VENDOR_ID, Long.toString(dictionaryData.getVendorId()));
//		attributeList.setAttribute(DictionaryConstant.ATTR_VENDOR_ID, dictionaryData.getName());
//		Set dictionaryParameterSet = dictionaryData.getDictionaryParameterDetail();
//		
//		if(dictionaryParameterSet!=null && !dictionaryParameterSet.isEmpty()){
//			Iterator parameterIterator  = dictionaryParameterSet.iterator();
//			while(parameterIterator.hasNext()){
//				DictionaryParameterDetailData dictionaryParameterDetailData= (DictionaryParameterDetailData) parameterIterator.next();
//				
//				Element attribute = createAttribute(xmlDocument,dictionaryParameterDetailData);
//				attributeList.appendChild(attribute);
//			}
//		}
//		xmlDocument.appendChild(attributeList);
//		xmlString = format(xmlDocument);
//		return xmlString;
//	}
//	private Element createAttribute(Document document,DictionaryParameterDetailData dictionaryParameterDetailData) {
//		Element attribute = document.createElement(DictionaryConstant.TAG_ATTRIBUTE);
//		attribute.setAttribute(DictionaryConstant.ATTR_ENCRYPT_STANDARD, Integer.toString(dictionaryParameterDetailData.getEncryptStandard()));
//		attribute.setAttribute(DictionaryConstant.ATTR_AVPAIR, dictionaryParameterDetailData.getAvPair());
//
//		attribute.setAttribute(DictionaryConstant.ATTR_TYPE, dictionaryParameterDetailData.getDataType().getAlias());
//		attribute.setAttribute(DictionaryConstant.ATTR_HAS_TAG, dictionaryParameterDetailData.getHasTag());
//
//		attribute.setAttribute(DictionaryConstant.ATTR_NAME, dictionaryParameterDetailData.getName());
//		
//		if(dictionaryParameterDetailData.getIgnoreCase()!=null){
//			attribute.setAttribute(DictionaryConstant.ATTR_IGNORE_CASE, dictionaryParameterDetailData.getIgnoreCase());
//		}
//		attribute.setAttribute(DictionaryConstant.ATTR_ID, Long.toString(dictionaryParameterDetailData.getVendorParameterId()));
//			Set nestedParameterDetailSet = dictionaryParameterDetailData.getNestedParameterDetailSet();
//		if(nestedParameterDetailSet!=null && !nestedParameterDetailSet.isEmpty()){
//			Iterator iterator = nestedParameterDetailSet.iterator();
//			while(iterator.hasNext()){
//				DictionaryParameterDetailData childData =  (DictionaryParameterDetailData)iterator.next();
//				Element nestatedAttribute = createAttribute(document,childData);
//				attribute.appendChild(nestatedAttribute);
//			}
//		}
//		
//		Element supportedValues = createSupportedValues(document,dictionaryParameterDetailData.getPredefinedValues());
//		if(supportedValues!=null){
//			attribute.appendChild(supportedValues);
//		}
//		return attribute;
//	}
//	
//	private Element createSupportedValues(Document document, String predefinedValues){
//		Element supportedValues = null;
//		if(predefinedValues!=null && predefinedValues.trim().equalsIgnoreCase("") ==false){
//			supportedValues = document.createElement(DictionaryConstant.TAG_SUPPORTED_VALUES);
//			Element value = document.createElement(DictionaryConstant.TAG_VALUE);
//			StringTokenizer commaTokenizer = new StringTokenizer(predefinedValues, ",");
//			while (commaTokenizer.hasMoreTokens()) {
//				StringTokenizer colonTokenizer = new StringTokenizer(commaTokenizer.nextToken(), ":");
//				String name = colonTokenizer.nextToken();
//				String id = colonTokenizer.nextToken();
//				value.setAttribute(DictionaryConstant.ATTR_ID, id);
//				value.setAttribute(DictionaryConstant.ATTR_NAME, name);
//			}
//			supportedValues.appendChild(value);
//		}
//		return supportedValues;
//	}
	
	public String convertAsXml(IDictionaryData dictionaryData){
		String xmlString = "";
		try{
			xmlString = getXMLString(dictionaryData);
		}catch(Exception e){
			Logger.logDebug(MODULE, "XML Parsing error, Reason:"+e.getMessage());
			Logger.logTrace(MODULE, e);
		}
		return xmlString;
	}
	
	public String getXMLString(IDictionaryData dictionaryData) throws ParserConfigurationException, IOException, TransformerFactoryConfigurationError, TransformerException{
		String xmlString = "";
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		builder.append("\n<"+DictionaryConstant.TAG_ATTRIBUTE_LIST+" ");
		builder.append(DictionaryConstant.ATTR_VENDOR_ID + "=\""+Long.toString(dictionaryData.getVendorId())+"\" ");
		builder.append(DictionaryConstant.ATTR_VENDOR_NAME + "=\""+dictionaryData.getName()+"\" >");
		
		Set dictionaryParameterSet = dictionaryData.getDictionaryParameterDetail();

		if(dictionaryParameterSet!=null && !dictionaryParameterSet.isEmpty()){
			Iterator parameterIterator  = dictionaryParameterSet.iterator();
			while(parameterIterator.hasNext()){
				DictionaryParameterDetailData dictionaryParameterDetailData= (DictionaryParameterDetailData) parameterIterator.next();
				if(dictionaryParameterDetailData.getParentDetailId() == null){
					createAttribute(builder,dictionaryParameterDetailData,1);
				}
			}
		}
		builder.append("\n</" +DictionaryConstant.TAG_ATTRIBUTE_LIST+">");
		
		xmlString = builder.toString();
		return xmlString;
	}
	
	private void createAttribute(StringBuilder builder,DictionaryParameterDetailData dictionaryParameterDetailData,int depth) {
		String indentation = getIndent(depth);
		builder.append(indentation+"<"+DictionaryConstant.TAG_ATTRIBUTE+" ");
		
		builder.append(DictionaryConstant.ATTR_ID+"=\""+Long.toString(dictionaryParameterDetailData.getVendorParameterId())+"\" ");
		builder.append(DictionaryConstant.ATTR_NAME+"=\""+dictionaryParameterDetailData.getName()+"\" ");
		builder.append(DictionaryConstant.ATTR_TYPE+"=\""+dictionaryParameterDetailData.getDataType().getName()+"\" ");
		
		if(dictionaryParameterDetailData.getPaddingType() != null && !PaddingType.NONE.getType().equalsIgnoreCase(dictionaryParameterDetailData.getPaddingType() )){
			builder.append(DictionaryConstant.ATTR_PADDING_TYPE+"=\""+dictionaryParameterDetailData.getPaddingType()+"\" ");
		}
		if(dictionaryParameterDetailData.getLengthFormat() != null && !LengthFormat.TLV.getName().equalsIgnoreCase(dictionaryParameterDetailData.getLengthFormat())){
			builder.append(DictionaryConstant.ATTR_LENGTH_FORMAT+"=\""+dictionaryParameterDetailData.getLengthFormat()+"\" ");
		}
		
		if(dictionaryParameterDetailData.getAvPair()!=null && !dictionaryParameterDetailData.getAvPair().equalsIgnoreCase("no")){
			builder.append(DictionaryConstant.ATTR_AVPAIR+"=\""+dictionaryParameterDetailData.getAvPair()+"\" ");
		}
		if(dictionaryParameterDetailData.getHasTag()!=null && !dictionaryParameterDetailData.getHasTag().equalsIgnoreCase("no")){
			builder.append(DictionaryConstant.ATTR_HAS_TAG+"=\""+ dictionaryParameterDetailData.getHasTag()+"\" ");
		}
		if(dictionaryParameterDetailData.getIgnoreCase()!=null && !dictionaryParameterDetailData.getIgnoreCase().equalsIgnoreCase("no")){
			builder.append(DictionaryConstant.ATTR_IGNORE_CASE+"=\""+ dictionaryParameterDetailData.getIgnoreCase()+"\" ");
		}
		if(dictionaryParameterDetailData.getEncryptStandard()!=null && dictionaryParameterDetailData.getEncryptStandard().intValue()!=0){
			builder.append(DictionaryConstant.ATTR_ENCRYPT_STANDARD+"=\""+ dictionaryParameterDetailData.getEncryptStandard()+"\" ");
		}
	
		
		
		Set nestedParameterDetailSet = dictionaryParameterDetailData.getNestedParameterDetailSet();
		boolean nesteadParameterAdded = false;
		if(nestedParameterDetailSet!=null && !nestedParameterDetailSet.isEmpty()){
			builder.append(">");
			nesteadParameterAdded = true;
			Iterator iterator = nestedParameterDetailSet.iterator();
			while(iterator.hasNext()){
				DictionaryParameterDetailData childData =  (DictionaryParameterDetailData)iterator.next();
				createAttribute(builder,childData,depth+1);
			}
		}

		boolean supportedValueAdded = createSupportedValues(builder,dictionaryParameterDetailData.getPredefinedValues(),depth+1);
		if(!supportedValueAdded && !nesteadParameterAdded){
			builder.append("/>");
		}else{
			builder.append(indentation+"</"+DictionaryConstant.TAG_ATTRIBUTE+">");
		}
		
	
	}
	private boolean createSupportedValues(StringBuilder builder, String predefinedValues, int depth){
		
		if(predefinedValues!=null && predefinedValues.trim().equalsIgnoreCase("") ==false){
			builder.append(">");
			String indentation = getIndent(depth);
			
			builder.append(indentation+"<"+DictionaryConstant.TAG_SUPPORTED_VALUES+">");
			StringTokenizer commaTokenizer = new StringTokenizer(predefinedValues, ",");
			while (commaTokenizer.hasMoreTokens()) {
				builder.append(indentation+"\t<"+DictionaryConstant.TAG_VALUE+" ");
				StringTokenizer colonTokenizer = new StringTokenizer(commaTokenizer.nextToken(), ":");
				String name = colonTokenizer.nextToken();
				String id = colonTokenizer.nextToken();
				builder.append(DictionaryConstant.ATTR_ID+"=\""+ id+"\" ");
				builder.append(DictionaryConstant.ATTR_NAME+"=\""+ name+"\" ");
				builder.append("/>");
			}
			builder.append(indentation+"</"+DictionaryConstant.TAG_SUPPORTED_VALUES+">");
			return true;
		}
		return false;
	}
	private String getIndent(int depth){
		String indentation = "\n";
		for(int i=0;i<depth;i++){
			indentation = indentation+"\t";
		}
		return indentation;
	}
	
	public String getAttributeFullName(DictionaryParameterDetailData dictionaryParameterDetailData,Map<Long,DictionaryData> dictionaryMap) throws DataManagerException{
		List<DictionaryParameterDetailData> attributeList = new ArrayList<DictionaryParameterDetailData>();
		
		String attributeFullName = null;
		DictionaryData dictionaryData = dictionaryMap.get(dictionaryParameterDetailData.getVendorId());
		if(dictionaryData!=null){
			attributeFullName = dictionaryData.getName();
		}
		if(dictionaryParameterDetailData.getParentDetailId()!=null){
			recursiveAttribute(attributeList,dictionaryParameterDetailData);
			for (int i = attributeList.size()-1; i >0; i--) {
				DictionaryParameterDetailData temp = attributeList.get(i);
				attributeFullName  = attributeFullName+":"+temp.getName();
			}
		}else{
			attributeFullName  = attributeFullName+":"+dictionaryParameterDetailData.getName();
		}
		
		return attributeFullName;
	}
	public String getAttributeId(DictionaryParameterDetailData dictionaryParameterDetailData,Map<Long,DictionaryData> dictionaryMap) throws DataManagerException{
		List<DictionaryParameterDetailData> attributeList = new ArrayList<DictionaryParameterDetailData>();
		
		String attributeFullId = null;
		DictionaryData dictionaryData = dictionaryMap.get(dictionaryParameterDetailData.getVendorId());
		if(dictionaryData!=null){
			attributeFullId = Long.toString(dictionaryData.getVendorId());
		}
		if(dictionaryParameterDetailData.getParentDetailId()!=null){
			recursiveAttribute(attributeList,dictionaryParameterDetailData);
			for (int i = attributeList.size()-1; i >0; i--) {
				DictionaryParameterDetailData temp = attributeList.get(i);
				attributeFullId  = attributeFullId+":"+temp.getVendorParameterId();
			}
		}else{
			attributeFullId  = attributeFullId+":"+dictionaryParameterDetailData.getName();
		}
		
		return attributeFullId;
	}
	private void recursiveAttribute(List<DictionaryParameterDetailData> attributeList, DictionaryParameterDetailData dictionaryParameterDetailData)  throws DataManagerException{
		if(dictionaryParameterDetailData!=null){
			attributeList.add(dictionaryParameterDetailData);
		}
		if(dictionaryParameterDetailData.getParentDetailId()==null){
			return;
		}
		DictionaryParameterDetailData data =getDictionaryParameterDetailData(dictionaryParameterDetailData.getParentDetailId());
		recursiveAttribute(attributeList,data);
	}
	
}
