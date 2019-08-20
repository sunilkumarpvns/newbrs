package com.elitecore.netvertexsm.blmanager.servermgr.dictionary.radius;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
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

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.NullValueException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.data.DataTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.exception.DuplicateDictionaryConstraintExcpetion;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.RadiusDictionaryDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryParamDetailData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.IRadiusDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.IRadiusDictionaryParamDetailData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.util.constants.DictionaryConstant;
import com.elitecore.netvertexsm.util.logger.Logger;

public class RadiusDictionaryBLManager extends BaseBLManager {

	private static final String MODULE = "DICTIONARY";


	/**
	 * @return
	 * @throws DataManagerException
	 */

	public List<RadiusDictionaryData> getAllDictionaryList(IStaffData staffData, String actionAlias ) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		List<RadiusDictionaryData> lstDictionaryList;

		if (dictionaryDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{

			session.beginTransaction();	

			lstDictionaryList = dictionaryDataManager.getAllList();
			String transactionId = "";
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);

			session.commit();

		}
		catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :",e);
		}finally{
			session.close();
		}

		return lstDictionaryList;
	}

	public List<RadiusDictionaryData> getDictionaryList( ) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		List<RadiusDictionaryData> lstDictionaryList;
        try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		lstDictionaryList = dictionaryDataManager.getList();
        }finally{
        	session.close();
        }
		return lstDictionaryList;
	}

	public List<RadiusDictionaryData> getOnlyDictionaryDataList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		List<RadiusDictionaryData> lstDictionaryDataList;
		try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		lstDictionaryDataList = dictionaryDataManager.getOnlyDictionaryDataList();
		}finally{
			session.close();
		}
		return lstDictionaryDataList;
	}

	public List<RadiusDictionaryData> getDictionaryDataList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		List<RadiusDictionaryData> lstDictionaryDataList;
		try{
			if (dictionaryDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			lstDictionaryDataList = dictionaryDataManager.getAllList();
		}finally{
			session.close();
		}
		return lstDictionaryDataList;
	}

	public List<RadiusDictionaryData> getDictionaryById( Collection<Long> colDictionaryId ) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		List<RadiusDictionaryData> lstDictionaryData;
        try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		lstDictionaryData = new ArrayList<RadiusDictionaryData>();
		Iterator<Long> itr = colDictionaryId.iterator();
		RadiusDictionaryData dictionaryData;

		while (itr.hasNext()) {
			dictionaryData = new RadiusDictionaryData();
			long dictionaryId = (Long) itr.next();
			if (dictionaryId >0) {
				dictionaryData.setDictionaryId(dictionaryId);
				dictionaryData = (RadiusDictionaryData) dictionaryDataManager.getList(dictionaryData).get(0);
				lstDictionaryData.add(dictionaryData);
			}
		}
        }finally{
        	session.close();
        }
		return lstDictionaryData;
	}

	public List<RadiusDictionaryData> getAllDictionaryById( Collection<Long> colDictionaryId ) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		List<RadiusDictionaryData> lstDictionaryData;
		try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		 lstDictionaryData = new ArrayList<RadiusDictionaryData>();
		Iterator<Long> itr = colDictionaryId.iterator();
		RadiusDictionaryData dictionaryData;

		while (itr.hasNext()) {
			dictionaryData = new RadiusDictionaryData();
			long dictionaryId =  itr.next();
			if (dictionaryId>0) {
				dictionaryData.setDictionaryId(dictionaryId);
				dictionaryData = (RadiusDictionaryData) dictionaryDataManager.getAllList(dictionaryData).get(0);
				lstDictionaryData.add(dictionaryData);
			}
		}
		} finally{
			session.close();
		}
		return lstDictionaryData;
	}
	public List<RadiusDictionaryData> getAllDictionaryById( Collection<Long> colDictionaryId,IStaffData staffData, String actionAlias ) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		
		List<RadiusDictionaryData> lstDictionaryData = new ArrayList<RadiusDictionaryData> ();

		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{

			session.beginTransaction();


			Iterator<Long> itr = colDictionaryId.iterator();
			RadiusDictionaryData dictionaryData;

			while (itr.hasNext()) {
				dictionaryData = new RadiusDictionaryData();
				long dictionaryId =  itr.next();
				if (dictionaryId >0) {
					dictionaryData.setDictionaryId(dictionaryId);
					dictionaryData = (RadiusDictionaryData) dictionaryDataManager.getAllList(dictionaryData).get(0);
					lstDictionaryData.add(dictionaryData);
				}
			}
			String transactionId = "";
			session.commit();
		}
		catch(Exception e){
			session.rollback();

			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return lstDictionaryData;
	}




	public List<RadiusDictionaryParamDetailData> getDictionaryParametersById( Collection colDictionaryId ) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		try{
			if (dictionaryDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			List<RadiusDictionaryParamDetailData> lstDictionaryDetailData = new ArrayList<RadiusDictionaryParamDetailData>();
			List<RadiusDictionaryData> lstDictionaryData = getAllDictionaryById(colDictionaryId);
			Iterator<RadiusDictionaryData> itrDictionaryData = lstDictionaryData.iterator();
			RadiusDictionaryData dictionaryData;
			while (itrDictionaryData.hasNext()) {
				dictionaryData = (RadiusDictionaryData) itrDictionaryData.next();
				Iterator<RadiusDictionaryParamDetailData> itrDictionaryDetail = dictionaryData.getDictionaryParameterDetail().iterator();
				while (itrDictionaryDetail.hasNext()) {
					lstDictionaryDetailData.add((RadiusDictionaryParamDetailData) itrDictionaryDetail.next());
				}
			}
			return lstDictionaryDetailData;
		}finally{
			session.close();
		}
	}

	public List<RadiusDictionaryData> getDictionariesWithParametersById( Collection<Long> colDictionaryId ) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		List<RadiusDictionaryData> lstDictionaryData = getAllDictionaryById(colDictionaryId);
		return lstDictionaryData;
		}finally{
			session.close();
		}
	}

	// / Private methods to create data managers for this BL Manager. ///

	public void updateStatus( List<Long> lstdictionaryId ,
			String commonStatusId,IStaffData staffData, String actionAlias ) throws DataManagerException,
			DataValidationException,
			ConstraintViolationException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		Date currentDate = new Date();

		try {
			if (dictionaryDataManager == null || systemAuditDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			if (lstdictionaryId != null) {
				updateStatusValidate(lstdictionaryId, commonStatusId);
				session.beginTransaction();
				for ( int i = 0; i < lstdictionaryId.size(); i++ ) {
					if (lstdictionaryId.get(i)  != null){
						long dictionaryId = lstdictionaryId.get(i);
						String transactionId = lstdictionaryId.get(i).toString();
						dictionaryDataManager.updateStatus(dictionaryId, commonStatusId, new Timestamp(currentDate.getTime()));

						systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
					}
				}
				session.commit();
			} else {
				session.rollback();
				throw new DataManagerException("Data Manager implementation not found for ");
			}
		}
		catch (DataManagerException e) {
			session.rollback();
			throw new DataManagerException("Action failed : " + e.getMessage());
		}finally{
			session.close();
		}
	}

	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailList( IRadiusDictionaryParamDetailData dictionaryParameterDetailData ) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		List<RadiusDictionaryParamDetailData> lstDictionaryDetailList;
        try{
		if (dictionaryDataManager == null) { throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); }
		lstDictionaryDetailList = dictionaryDataManager.getDictionaryParameterDetailList(dictionaryParameterDetailData);
        }finally{
        	session.close();
        }
		return lstDictionaryDetailList;
	}

	public void create( IRadiusDictionaryData dictionaryData,IStaffData staffData, String actionAlias ) throws DataManagerException,
	DataValidationException,
	ConstraintViolationException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (dictionaryDataManager == null || systemAuditDataManager==null) { throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); }
		try {
			createValidate(dictionaryData);
			session.beginTransaction();
			RadiusDictionaryData dicData = new RadiusDictionaryData();
			dicData.setVendorId(dictionaryData.getVendorId());
			List<RadiusDictionaryData> lstDictionaryData = dictionaryDataManager.getAllList(dicData);
			if (lstDictionaryData == null || lstDictionaryData.size() <= 0) {

				dictionaryDataManager.create(dictionaryData);

				String transactionId = Long.toString(dictionaryData.getDictionaryId());
				systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);

				session.commit();
				session.close();
			} else {
				throw new DuplicateDictionaryConstraintExcpetion("Dictionary " + dictionaryData.getName() + " with Vendor Number " + dictionaryData.getVendorId() + " already exists");
			}
		}
		catch (ConstraintViolationException e) {
			session.rollback();
			throw e;

		}
		catch (DataValidationException e) {
			session.close();
			throw e;
		}
		catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Create Operation failed : " + exp.getMessage());
		}finally{
			session.close();
		}
	}

	public void updateBasicDetails( IRadiusDictionaryData dictionaryData ,IStaffData staffData, String actionAlias) throws DataManagerException,
	ConstraintViolationException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		Date currentDate = new Date();

		if (dictionaryDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try {
			updateBasicDetailsValidate(dictionaryData);
			session.beginTransaction();

			dictionaryDataManager.updateBasicDetails(dictionaryData, new Timestamp(currentDate.getTime()));
			String transactionId = Long.toString(dictionaryData.getDictionaryId());
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);

			session.commit();
		}
		catch (ConstraintViolationException e) {
			session.rollback();
			throw e;
		}
		catch (DataValidationException e) {
			session.rollback();
			throw e;
		}catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed : " + exp.getMessage());
		}finally{
			session.close();
		}



	}

	public void delete( List<Long> lstdictionaryId , IStaffData staffData , String actionAlias ) throws DataManagerException, ConstraintViolationException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (dictionaryDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try {
			session.beginTransaction();
			if (lstdictionaryId != null) {
				for ( int i = 0; i < lstdictionaryId.size(); i++ ) {
					if (lstdictionaryId.get(i) != null) {
						long dictionaryId = lstdictionaryId.get(i);
						String transactionId = lstdictionaryId.get(i).toString();


						dictionaryDataManager.delete(dictionaryId);
						systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias, transactionId);
					}
				}
			} else {
				throw new DataManagerException("Empty Dictionary to be deleted");
			}
			session.commit();
		} catch (DataManagerException exp) {
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
			RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

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
			return htab_strName_strDataTypeId;
		}
		catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}finally{
			session.close();
		}
	}

	/**
	 * @return Returns Data Manager instance for dictionary data.
	 */
	public RadiusDictionaryDataManager getDictionaryDataManager( IDataManagerSession session ) {
		RadiusDictionaryDataManager dictionaryDataManager = (RadiusDictionaryDataManager) DataManagerFactory.getInstance().getDataManager(RadiusDictionaryDataManager.class, session);
		return dictionaryDataManager;
	}

	public void updateStatusValidate( List lstdictionaryId ,
			String commonStatusId ) throws DataValidationException {

		if (EliteGenericValidator.isBlankOrNull(commonStatusId)) { throw (new NullValueException("Invalid Dictionary Status", (MODULE + "." + "status").toLowerCase())); }

	}

	public void createValidate( IRadiusDictionaryData dictionaryData ) throws DataValidationException {

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

	public void updateBasicDetailsValidate( IRadiusDictionaryData dictionaryData ) throws DataValidationException {

		// Name
		if (EliteGenericValidator.isBlankOrNull(dictionaryData.getName())) { throw (new NullValueException("Invalid Dictionary Name", (MODULE + "." + "name").toLowerCase())); }

		// CommonStatusId
		if (EliteGenericValidator.isBlankOrNull(dictionaryData.getCommonStatusId())) { throw (new NullValueException("Invalid Dictionary Status", (MODULE + "." + "status").toLowerCase())); }

		// DictionaryId
		if (dictionaryData.getDictionaryId()>0) { throw (new NullValueException("Invalid Dictionary Dictionary ID", (MODULE + "." + "dictionaryid").toLowerCase())); }

	}

	public String getDictionaryParameterName( String vendorId ,
			long dictionaryParameterId ) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		String dictionaryParameterName = null;
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try {
			dictionaryParameterName = dictionaryDataManager.getDictionaryParameterName(vendorId, dictionaryParameterId);
		}
		catch (DataManagerException exp) {
			exp.printStackTrace();
			throw new DataManagerException("Basic Details Update Operation failed : " + exp.getMessage());
		}finally{
			session.close();
		}return dictionaryParameterName;
	}

	public String getDictionaryParameterDetail( String vendorId , long dictionaryParameterId ) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		String dictionaryWithParameterName = null;
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try {
			dictionaryWithParameterName = dictionaryDataManager.getDictionaryParamDetail(vendorId, dictionaryParameterId);
		}
		catch (DataManagerException exp) {
			exp.printStackTrace();
		}finally{
			session.close();
		}
		return dictionaryWithParameterName;
	}

	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailList(IRadiusDictionaryParamDetailData dictionaryParameterDetailData, String criteriaName) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		List<RadiusDictionaryParamDetailData> lstDictionaryDetailList;
        try{
		if (dictionaryDataManager == null) { throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); }
		lstDictionaryDetailList = dictionaryDataManager.getDictionaryParameterDetailList(dictionaryParameterDetailData, criteriaName);
        }finally{
			session.close();
		}
		return lstDictionaryDetailList;

	}

	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailAllList() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		List<RadiusDictionaryParamDetailData> lstDictionaryParameterList;
        try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		lstDictionaryParameterList = dictionaryDataManager.getDictionaryParameterDetailAllList();
        }finally{
			session.close();
		}
		return lstDictionaryParameterList;

	}

	public List<RadiusDictionaryParamDetailData> getOnlyDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		List<RadiusDictionaryParamDetailData> dictionaryParameterList;
		try{
			if (dictionaryDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			dictionaryParameterList = dictionaryDataManager.getOnlyDictionaryParameterList(searchNameOrAttributeId);
		}finally{
			session.close();
		}

		return dictionaryParameterList;
	}
	public RadiusDictionaryParamDetailData getOnlyDictionaryParametersByAttributeId(String attributeId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		RadiusDictionaryParamDetailData dictionaryParameterDetailData;
	    try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		dictionaryParameterDetailData = dictionaryDataManager.getOnlyDictionaryParametersByAttributeId(attributeId);
	    }finally{
			session.close();
		}
		return dictionaryParameterDetailData;
	}

	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailAllList(String searchByName) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		List<RadiusDictionaryParamDetailData> lstDictionaryParameterList;
        try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		lstDictionaryParameterList = dictionaryDataManager.getDictionaryParameterDetailAllList(searchByName);
        }finally{
        	session.close();
        }
		return lstDictionaryParameterList;

	}

	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailByIdList(List<Long> dictIdList,String searchByName) throws DataManagerException {
		List<RadiusDictionaryParamDetailData> lstDictionaryParameterList = new ArrayList<RadiusDictionaryParamDetailData>();
		IRadiusDictionaryParamDetailData dictionaryParameterSearchData = new RadiusDictionaryParamDetailData();

		if(dictIdList != null && dictIdList.size() > 0) {
			for(Iterator<Long> iter = dictIdList.iterator();iter.hasNext();) {
				long dictionaryId = iter.next();

				if (dictionaryId>0) {
					dictionaryParameterSearchData.setDictionaryId(dictionaryId);
					List<RadiusDictionaryParamDetailData> tmpList = getDictionaryParameterDetailList(dictionaryParameterSearchData,searchByName);
					lstDictionaryParameterList.addAll(tmpList);
				}
			}    
		}
		return lstDictionaryParameterList;
	}
	public List<RadiusDictionaryData> getDictionaryListByParameter(Collection<Long> dictIdList, String searchByName) throws DataManagerException{
		List<RadiusDictionaryData> lstDictionary = new ArrayList<RadiusDictionaryData>();
		if(dictIdList!=null && dictIdList.size()>0){

			for(Iterator<Long> iter = dictIdList.iterator();iter.hasNext();){
				long dictionaryId= iter.next();
				if(dictionaryId>0){
					RadiusDictionaryData dictionaryData = getDictionariesByParams( dictionaryId, searchByName);
					lstDictionary.add(dictionaryData);
				}
			}

		}
		return lstDictionary;
	}
	public List<RadiusDictionaryData> getDictionaryListByParameter(IRadiusDictionaryParamDetailData dictionaryParameterSearchData, String searchByName) throws DataManagerException{
		List<RadiusDictionaryData> lstDictionary = new ArrayList<RadiusDictionaryData>();

		long dictionaryId= dictionaryParameterSearchData.getDictionaryId();
		if(dictionaryId >0){
			RadiusDictionaryData dictionaryData = getDictionariesByParams( dictionaryId, searchByName);
			lstDictionary.add(dictionaryData);
		}

		return lstDictionary;
	}
	public RadiusDictionaryData getDictionariesByParams(long dictionaryId,String searchByName) throws DataManagerException{
		RadiusDictionaryData dictionaryData = null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		dictionaryData	 = dictionaryDataManager.getDictionaryByName(dictionaryId,searchByName);
		return dictionaryData;
	}

	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}

	public Integer getDictionaryIdByName(String name) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		try{
			if (dictionaryDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			IRadiusDictionaryParamDetailData paramData = dictionaryDataManager.getDictionaryIdByName(name);
			return paramData.getVendorParameterId();
		}finally{
			session.close();
		}

	}

	public IRadiusDictionaryParamDetailData getDictionaryParamDetailByName(String name)throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        try{ 
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		IRadiusDictionaryParamDetailData paramData = dictionaryDataManager.getDictionaryIdByName(name);
		return paramData;
        }finally{
			session.close();
		}
	}

	public ArrayList<RadiusDictionaryParamDetailData> getDictionaryParameterDetailListById(Long dictionaryId) throws DataManagerException {
		ArrayList<RadiusDictionaryParamDetailData> arrList=new ArrayList<RadiusDictionaryParamDetailData>();
		List<RadiusDictionaryParamDetailData> list=null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);

		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		if(dictionaryId != null) {
			list = dictionaryDataManager.getDictionaryParameterDetailList(dictionaryId);
		}      
		//     printParameters(list,null);
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			RadiusDictionaryParamDetailData  parameterDetailData = (RadiusDictionaryParamDetailData) iterator.next();
			arrList.add(parameterDetailData);	

		}
		return arrList;
	}
	public RadiusDictionaryParamDetailData getDictionaryParameterDetailData(Long dictionaryParameterId) throws DataManagerException {
		RadiusDictionaryParamDetailData dictionaryParameterDetailData=null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
         try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		dictionaryParameterDetailData = dictionaryDataManager.getDictionaryParameterDetailData(dictionaryParameterId);
         }finally{
 			session.close();
 		}      
		return dictionaryParameterDetailData;
	}

	public RadiusDictionaryData getDictionaryDataByVendor(long vendorId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		RadiusDictionaryData dictionaryData= null;
		try{
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		dictionaryData = dictionaryDataManager.getDictionaryDataByVendor(vendorId);
		}finally{
			session.close();
		}
		return dictionaryData;
	}

	public RadiusDictionaryData getDictionaryById(Long dictionaryId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
        try{  
		if (dictionaryDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		RadiusDictionaryData dictionaryData = new RadiusDictionaryData();
		if (dictionaryId!=null) {

			dictionaryData = dictionaryDataManager.getDictionaryData(dictionaryId);

		}
		return dictionaryData;
        }finally{
        	session.close();
        }

	}

	public void createDictonaryFromXML(String xmlString){

		//parse function


	}

	public void create(IRadiusDictionaryData dictionaryData) throws DataManagerException {


		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (dictionaryDataManager == null || systemAuditDataManager==null) { throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); }
		try {
			createValidate(dictionaryData);
			session.beginTransaction();
			RadiusDictionaryData dicData = new RadiusDictionaryData();
			dicData.setVendorId(dictionaryData.getVendorId());
			List<RadiusDictionaryData> lstDictionaryData = dictionaryDataManager.getAllList(dicData);
			Logger.logDebug(MODULE,"List :"+lstDictionaryData.size());
			if (lstDictionaryData == null || lstDictionaryData.size() <= 0) {

				dictionaryDataManager.create(dictionaryData);

				session.commit();
			} else {
				throw new DuplicateDictionaryConstraintExcpetion("Dictionary " + dictionaryData.getName() + " with Vendor Number " + dictionaryData.getVendorId() + " already exists");
			}
		}
		catch (ConstraintViolationException e) {
			session.rollback();
			throw e;
		}
		catch (DataValidationException e) {
			session.close();
			throw e;
		}
		catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Create Operation failed : " + exp.getMessage());
		}finally{
		 session.close();
		}
	}
	

	public RadiusDictionaryData updateDictionary(IRadiusDictionaryData dictionaryData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusDictionaryDataManager dictionaryDataManager = getDictionaryDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		RadiusDictionaryData dicData = new RadiusDictionaryData();
		if (dictionaryDataManager == null || systemAuditDataManager==null) { throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); }
		try {
			session.beginTransaction();

			dicData.setVendorId(dictionaryData.getVendorId());
			List<RadiusDictionaryData> lstDictionaryData = dictionaryDataManager.getAllList(dicData);

			if (lstDictionaryData != null) {

				dicData=dictionaryDataManager.updateDictionary(dictionaryData);

				session.commit();
			}
		}
		catch (ConstraintViolationException e) {
			session.rollback();
			throw e;
		}
		catch (DataValidationException e) {
			session.rollback();
			throw e;
		}
		catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Create Operation failed : " + exp.getMessage());
		}finally{
			session.close();
		}
		return dicData;
	}



	/*
	 * Convert to XML String
	 */

	public String convertAsXml(IRadiusDictionaryData dictionaryData){
		String xmlString = "";
			try{
				xmlString = getXMLString(dictionaryData);
			}catch(Exception e){
				Logger.logDebug(MODULE, "XML Parsing error, Reason:"+e.getMessage());
				Logger.logTrace(MODULE, e);
			}
			return xmlString;
	}
	public String getXMLString(IRadiusDictionaryData dictionaryData) throws ParserConfigurationException, IOException, TransformerFactoryConfigurationError, TransformerException{
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
					RadiusDictionaryParamDetailData dictionaryParameterDetailData= (RadiusDictionaryParamDetailData) parameterIterator.next();
					if(dictionaryParameterDetailData.getParentDetailId() == null){
								createAttribute(builder,dictionaryParameterDetailData,1);
					}
				}
			}
			builder.append("\n</" +DictionaryConstant.TAG_ATTRIBUTE_LIST+">");
	 		
			xmlString = builder.toString();
			return xmlString;
	}	
	
	private void createAttribute(StringBuilder builder,RadiusDictionaryParamDetailData dictionaryParameterDetailData,int depth) {
		String indentation = getIndent(depth);
		builder.append(indentation+"<"+DictionaryConstant.TAG_ATTRIBUTE+" ");

		builder.append(DictionaryConstant.ATTR_ID+"=\""+Long.toString(dictionaryParameterDetailData.getVendorParameterId())+"\" ");
		builder.append(DictionaryConstant.ATTR_NAME+"=\""+dictionaryParameterDetailData.getName()+"\" ");
		builder.append(DictionaryConstant.ATTR_TYPE+"=\""+dictionaryParameterDetailData.getDataType().getName()+"\" ");


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
				RadiusDictionaryParamDetailData childData =  (RadiusDictionaryParamDetailData)iterator.next();
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

	public String getAttributeFullName(RadiusDictionaryParamDetailData dictionaryParameterDetailData,Map<Long,RadiusDictionaryData> dictionaryMap) throws DataManagerException{
		List<RadiusDictionaryParamDetailData> attributeList = new ArrayList<RadiusDictionaryParamDetailData>();

		String attributeFullName = null;
		RadiusDictionaryData dictionaryData = dictionaryMap.get(dictionaryParameterDetailData.getVendorId());
		if(dictionaryData!=null){
			attributeFullName = dictionaryData.getName();
		}
		if(dictionaryParameterDetailData.getParentDetailId()!=null){
			recursiveAttribute(attributeList,dictionaryParameterDetailData);
			for (int i = attributeList.size()-1; i >0; i--) {
				RadiusDictionaryParamDetailData temp = attributeList.get(i);
				attributeFullName  = attributeFullName+":"+temp.getName();
			}
		}else{
			attributeFullName  = attributeFullName+":"+dictionaryParameterDetailData.getName();
		}

		return attributeFullName;
	}
	public String getAttributeId(RadiusDictionaryParamDetailData dictionaryParameterDetailData,Map<Long,RadiusDictionaryData> dictionaryMap) throws DataManagerException{
		List<RadiusDictionaryParamDetailData> attributeList = new ArrayList<RadiusDictionaryParamDetailData>();

		String attributeFullId = null;
		RadiusDictionaryData dictionaryData = dictionaryMap.get(dictionaryParameterDetailData.getVendorId());
		if(dictionaryData!=null){
			attributeFullId = Long.toString(dictionaryData.getVendorId());
		}
		if(dictionaryParameterDetailData.getParentDetailId()!=null){
			recursiveAttribute(attributeList,dictionaryParameterDetailData);
			for (int i = attributeList.size()-1; i >0; i--) {
				RadiusDictionaryParamDetailData temp = attributeList.get(i);
				attributeFullId  = attributeFullId+":"+temp.getVendorParameterId();
			}
		}else{
			attributeFullId  = attributeFullId+":"+dictionaryParameterDetailData.getName();
		}

		return attributeFullId;
	}
	private void recursiveAttribute(List<RadiusDictionaryParamDetailData> attributeList, RadiusDictionaryParamDetailData dictionaryParameterDetailData)  throws DataManagerException{
		if(dictionaryParameterDetailData!=null){
			attributeList.add(dictionaryParameterDetailData);
		}
		if(dictionaryParameterDetailData.getParentDetailId()==null){
			return;
		}
		RadiusDictionaryParamDetailData data =getDictionaryParameterDetailData(dictionaryParameterDetailData.getParentDetailId());
		recursiveAttribute(attributeList,data);
	}

}