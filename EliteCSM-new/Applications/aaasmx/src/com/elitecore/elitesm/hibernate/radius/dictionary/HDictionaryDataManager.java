package com.elitecore.elitesm.hibernate.radius.dictionary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.dictionary.DictionaryDataManager;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DataTypeData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.logger.Logger;


public class HDictionaryDataManager extends HBaseDataManager implements DictionaryDataManager {
	private static final String MODULE="HDictionaryDataManager";

	public List getAllList() throws DataManagerException {

		List dictionaryList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);
			dictionaryList = criteria.list();

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return dictionaryList;
	} 

	public List getList() throws DataManagerException {

		List dictionaryList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);
			criteria.add(Restrictions.eq("commonStatusId","CST01"));            
			dictionaryList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return dictionaryList;
	} 

	public List<DictionaryData> getOnlyDictionaryDataList() throws DataManagerException {
		List dictionaryParamList = null;
		List<DictionaryData> tmpDictionaryParamList;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property("dictionaryId"));
			proList.add(Projections.property("name"));
			proList.add(Projections.property("commonStatusId"));
			proList.add(Projections.property("vendorId"));

			criteria.setProjection(proList);
			dictionaryParamList = criteria.list();
			DictionaryData dictionaryData;
			tmpDictionaryParamList = new ArrayList<DictionaryData>();

			if(dictionaryParamList != null && dictionaryParamList.size() > 0) {
				Iterator dictionaryParamListItr = dictionaryParamList.iterator();
				while(dictionaryParamListItr.hasNext()) { 
					Object tmpDictionaryData[] = (Object[]) dictionaryParamListItr.next();
					dictionaryData = new DictionaryData();
					dictionaryData.setDictionaryId((String)tmpDictionaryData[0]);
					dictionaryData.setName((String)tmpDictionaryData[1]);
					dictionaryData.setCommonStatusId((String)tmpDictionaryData[2]);
					dictionaryData.setVendorId((Long)tmpDictionaryData[3]);
					tmpDictionaryParamList.add(dictionaryData);
				}
			}

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}
		return tmpDictionaryParamList;	
	}

	public List<DictionaryData> getDictionaryDataList() throws DataManagerException {
		List dictionaryDataList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}
		return dictionaryDataList;	
	}


	public List getList(IDictionaryData dictionaryData) throws DataManagerException {
		List dictionaryList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);

			if(dictionaryData != null){
				criteria.add(Restrictions.eq("commonStatusId","CST01"));
				if( Strings.isNullOrEmpty(dictionaryData.getDictionaryId()) == false){
					criteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()));
				}

				if(dictionaryData.getName() != null){
					criteria.add(Restrictions.eq("name",dictionaryData.getName()));
				}

				if(dictionaryData.getDictionaryNumber() != 0){
					criteria.add(Restrictions.eq("dictionaryNumber",new Long(dictionaryData.getDictionaryNumber()) ));
				}

				if(dictionaryData.getVendorId() != 0){
					criteria.add(Restrictions.eq("vendorId",new Long(dictionaryData.getVendorId()) ));
				}

				if(dictionaryData.getCommonStatusId() != null){
					criteria.add(Restrictions.eq("commonStatusId",dictionaryData.getCommonStatusId()));
				}

				if(dictionaryData.getCreatedByStaffId() != null){
					criteria.add(Restrictions.eq("createdByStaffId",dictionaryData.getCreatedByStaffId()));
				}

			}

			dictionaryList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return dictionaryList;
	}

	public List getAllList(IDictionaryData dictionaryData) throws DataManagerException{
		List dictionaryList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);

			if(dictionaryData != null){

				if(Strings.isNullOrEmpty(dictionaryData.getDictionaryId()) == false){
					criteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()));
				}

				if(dictionaryData.getName() != null){
					criteria.add(Restrictions.eq("name",dictionaryData.getName()));
				}

				if(dictionaryData.getDictionaryNumber() != -1){
					criteria.add(Restrictions.eq("dictionaryNumber",new Long(dictionaryData.getDictionaryNumber()) ));
				}

				if(dictionaryData.getVendorId() != -1){
					criteria.add(Restrictions.eq("vendorId",new Long(dictionaryData.getVendorId()) ));
				}

				if(dictionaryData.getCommonStatusId() != null){
					criteria.add(Restrictions.eq("commonStatusId",dictionaryData.getCommonStatusId()));
				}

				if(dictionaryData.getCreatedByStaffId() != null){
					criteria.add(Restrictions.eq("createdByStaffId",dictionaryData.getCreatedByStaffId()));
				}

			}

			dictionaryList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return dictionaryList;

	}

	public IDictionaryData getAllListByVendor(IDictionaryData dictionaryData) throws DataManagerException{
		//List dictionaryList = null;
		IDictionaryData retDictionaryData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);

			if(dictionaryData != null){
				if(dictionaryData.getVendorId() != -1){
					criteria.add(Restrictions.eq("vendorId",new Long(dictionaryData.getVendorId()) ));
				}

			}

			retDictionaryData = (IDictionaryData)criteria.uniqueResult();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return retDictionaryData;

	}

	public List search(IDictionaryData dictionaryData) throws DataManagerException {
		// TODO Auto-generated method stub
		return null;
	}

	public List updateStatus(String dictionaryId, String commonStatus,Timestamp statusChangeDate) throws DataManagerException{
		List dictionaryList = null;

		Session session = getSession();
		DictionaryData dictionaryData = null;

		try{
			Criteria criteria = session.createCriteria(DictionaryData.class);
			dictionaryData = (DictionaryData)criteria.add(Restrictions.eq("dictionaryId",dictionaryId))
			.uniqueResult();
			dictionaryData.setCommonStatusId(commonStatus);
			dictionaryData.setStatusChangedDate(statusChangeDate);
			dictionaryData.setLastModifiedDate(statusChangeDate);
			session.update(dictionaryData);
			session.flush();

			dictionaryData = null;
			criteria = null;


		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return dictionaryList;
	}



	public List getDictionaryParameterDetailList(IDictionaryParameterDetailData dictionaryParameterDetailData) throws DataManagerException{
		List dictionaryParameterDetailList ;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryParameterDetailData.class);
			if(dictionaryParameterDetailData != null ){
				if(dictionaryParameterDetailData.getName() !=null ){
					criteria.add(Restrictions.eq("name",dictionaryParameterDetailData.getName()));
				}
				
			} 
			dictionaryParameterDetailList = criteria.list();
		} catch (HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return dictionaryParameterDetailList;
	}


	public void create(IDictionaryData dictionaryData) throws DataManagerException{

		try {
			Session session = getSession();

			Set lstDictionaryDetail = dictionaryData.getDictionaryParameterDetail();
			dictionaryData.setDictionaryParameterDetail(null);

			session.save(dictionaryData);


			Iterator itrDictionaryDetail = lstDictionaryDetail.iterator();

			while(itrDictionaryDetail.hasNext()){
				IDictionaryParameterDetailData dictionaryDetailData = (IDictionaryParameterDetailData)itrDictionaryDetail.next();
				dictionaryDetailData.setDictionaryId(dictionaryData.getDictionaryId());
				String attributeId=dictionaryDetailData.getVendorId()+":"+dictionaryDetailData.getVendorParameterId();
				dictionaryDetailData.setAttributeId(attributeId);

				session.save(dictionaryDetailData); 
				session.flush();
				recursiveSave(dictionaryDetailData,dictionaryData,session);

			}

		} catch (HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}


	}
	private void recursiveSave(IDictionaryParameterDetailData dictionaryDetailData, IDictionaryData dictionaryData, Session session) throws HibernateException{
		List<DictionaryParameterDetailData> nestedParameterDetailList = dictionaryDetailData.getNestedParameterDetailList();

		if(nestedParameterDetailList!=null && !nestedParameterDetailList.isEmpty()){

			Iterator iterator = nestedParameterDetailList.iterator();
			while(iterator.hasNext()){
				IDictionaryParameterDetailData tempDictionaryDetailData = (IDictionaryParameterDetailData)iterator.next();
				tempDictionaryDetailData.setDictionaryId(dictionaryData.getDictionaryId());
				tempDictionaryDetailData.setParentDetailId(dictionaryDetailData.getDictionaryParameterDetailId());
				String attributeId=getAttributeId(dictionaryDetailData,tempDictionaryDetailData,session);
				tempDictionaryDetailData.setAttributeId(attributeId);
				session.save(tempDictionaryDetailData);

				recursiveSave(tempDictionaryDetailData,dictionaryData,session);
			}
		}
	}


	private String getAttributeId(IDictionaryParameterDetailData parentData,IDictionaryParameterDetailData childData, Session session) {
		String atttributeId=null;
		atttributeId=parentData.getAttributeId()+":"+childData.getVendorParameterId();
		return atttributeId;
	}

	public void updateBasicDetails(IDictionaryData dictionaryData,Timestamp statusChageDate) throws DataManagerException{
		Session session = getSession();

		try {
			if(dictionaryData != null){
				Criteria criteria = session.createCriteria(DictionaryParameterDetailData.class);
				criteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()));
				List lstDictionaries = criteria.list();

				if(lstDictionaries.size()>0){
					for(int i=0;i<lstDictionaries.size();i++){
						IDictionaryParameterDetailData dictinaryParamDetailData = (IDictionaryParameterDetailData)lstDictionaries.get(i);
						session.delete(dictinaryParamDetailData);
					}
				}
				session.flush();

				Set stDictionaryDetail = dictionaryData.getDictionaryParameterDetail();
				Iterator itrDictionaryDetail = stDictionaryDetail.iterator();
				while(itrDictionaryDetail.hasNext()){
					IDictionaryParameterDetailData dictionaryParamDetailData = (IDictionaryParameterDetailData)itrDictionaryDetail.next();
					dictionaryParamDetailData.setDictionaryId(dictionaryData.getDictionaryId());
					
					String attributeId=dictionaryParamDetailData.getVendorId()+":"+dictionaryParamDetailData.getVendorParameterId();
					dictionaryParamDetailData.setAttributeId(attributeId);
					session.save(dictionaryParamDetailData);
					session.flush();
					recursiveSave(dictionaryParamDetailData, dictionaryData, session);
				}

				criteria = session.createCriteria(DictionaryData.class);
				criteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()));
				IDictionaryData newDictionaryData = (IDictionaryData)criteria.uniqueResult();

				newDictionaryData.setName(dictionaryData.getName());
				newDictionaryData.setDescription(dictionaryData.getDescription());
				newDictionaryData.setCommonStatusId(dictionaryData.getCommonStatusId());
				newDictionaryData.setVendorId(dictionaryData.getVendorId());
				newDictionaryData.setDictionaryNumber(dictionaryData.getDictionaryNumber());
				newDictionaryData.setLastModifiedDate(statusChageDate);
				session.update(newDictionaryData);
			}

		} catch (HibernateException hExp){    		
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}


	}


	public void delete(String dictionaryId) throws DataManagerException{
		DictionaryData dictionaryData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);
			if( Strings.isNullOrEmpty(dictionaryId) == false){
				dictionaryData = (DictionaryData)criteria.add(Restrictions.eq("dictionaryId",dictionaryId)).uniqueResult();
				if(dictionaryData != null){

					session.delete(dictionaryData);
				}else{
					throw new DataManagerException("Dictionary Instance not Found");
				}
			}else{
				throw new DataManagerException("Invalid Dictionary Id");
			} 
		} catch (HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}

	}



	private void recursiveDelete(IDictionaryParameterDetailData dictionaryDetailData,Session session) throws HibernateException{
		if(dictionaryDetailData!=null){
			Criteria criteria = session.createCriteria(DictionaryParameterDetailData.class).add(Restrictions.eq("parentDetailId", dictionaryDetailData.getDictionaryParameterDetailId()));
			List<DictionaryParameterDetailData> nestedParameterDetailList = criteria.list();
			if(nestedParameterDetailList!=null && !nestedParameterDetailList.isEmpty()){

				for (int i = 0; i < nestedParameterDetailList.size(); i++) {
					DictionaryParameterDetailData temp= nestedParameterDetailList.get(i);
					recursiveDelete(temp,session);
				}

			}
			session.delete(dictionaryDetailData);


		}
	}

	/**
	 * This method returns a List of DataType
	 *  @return LIst 
	 */

	public List getDatatype() throws DataManagerException
	{
		List dataTypeList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DataTypeData.class);
			criteria.add(Restrictions.eq("dictionaryType","R"));
			dataTypeList = criteria.list();
		}
		catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return dataTypeList;
	}
	public String getDictionaryParameterName(String vendorId, String dictionaryParameterId) throws DataManagerException{

		DictionaryData dictionaryData = null;
		DictionaryParameterDetailData dictionaryParameterDetailData=null;
		String dictionaryParameterName=null;
		try {

			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);
			dictionaryData = (DictionaryData)criteria.add(Restrictions.eq("vendorId",Long.parseLong(vendorId))).uniqueResult();

			Criteria DictionaryParameterCriteria = session.createCriteria(DictionaryParameterDetailData.class);
			dictionaryParameterDetailData  = (DictionaryParameterDetailData)DictionaryParameterCriteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()))
			.add(Restrictions.eq("vendorParameterId",dictionaryParameterId)).uniqueResult();

			dictionaryParameterName=dictionaryParameterDetailData.getName();

		}catch (HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch (Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return dictionaryParameterName;
	}

	public String getDictionaryParamDetail(String vendorId,String dictionaryParameterId) throws DataManagerException{

		DictionaryData dictionaryData = null;
		DictionaryParameterDetailData dictionaryParameterDetailData=null;
		String dictionaryParameterName=null;
		try {

			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryData.class);

			dictionaryData = (DictionaryData)criteria.add(Restrictions.eq("vendorId",Long.parseLong(vendorId))).uniqueResult();

			Criteria DictionaryParameterCriteria = session.createCriteria(DictionaryParameterDetailData.class);
			dictionaryParameterDetailData  = (DictionaryParameterDetailData)DictionaryParameterCriteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()))
			.add(Restrictions.eq("vendorParameterId", Integer.parseInt(dictionaryParameterId))).uniqueResult();

			dictionaryParameterName=dictionaryData.getName()+" : "+dictionaryParameterDetailData.getName();

		}catch (HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch (Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return dictionaryParameterName;
	}

	public List getDictionaryParameterDetailList(IDictionaryParameterDetailData dictionaryParameterDetailData, String criteriaName) throws DataManagerException{
		List dictionaryParameterDetailList ;
		List dictionaryParameterDetailListByCriteria = new ArrayList();
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryParameterDetailData.class);
			criteria.addOrder(Order.asc("name"));
			if(dictionaryParameterDetailData != null ){
				if(Strings.isNullOrEmpty(dictionaryParameterDetailData.getDictionaryId()) == false ){
					criteria.add(Restrictions.eq("dictionaryId",dictionaryParameterDetailData.getDictionaryId()));
				}
			} 


			dictionaryParameterDetailList = criteria.list();

			Iterator iter = dictionaryParameterDetailList.iterator();

			if(criteriaName.equalsIgnoreCase("")){
				return dictionaryParameterDetailList;
			}
			else{
				while(iter.hasNext()){
					IDictionaryParameterDetailData dictionaryParamDetailData = (IDictionaryParameterDetailData)iter.next();

					if(dictionaryParamDetailData.getName().toLowerCase().contains(criteriaName.toLowerCase())){

						dictionaryParameterDetailListByCriteria.add(dictionaryParamDetailData);
					}
				}
				return dictionaryParameterDetailListByCriteria;
			}
		} catch (HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}


	}

	public List getDictionaryParameterDetailAllList() throws DataManagerException {

		List dictionaryParameterDetailList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryParameterDetailData.class);
			criteria.addOrder(Order.asc("name"));
			dictionaryParameterDetailList = criteria.list();

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return dictionaryParameterDetailList;
	}

	public List getDictionaryParameterDetailAllList(String searchByName) throws DataManagerException{

		List dictionaryParameterDetailList = null;
		List dictionaryParameterDetailListByName = new ArrayList();
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryParameterDetailData.class);
			criteria.addOrder(Order.asc("name"));
			dictionaryParameterDetailList = criteria.list();

			Iterator iter = dictionaryParameterDetailList.iterator();

			while(iter.hasNext()){
				IDictionaryParameterDetailData dictionaryParamDetailData = (IDictionaryParameterDetailData)iter.next();


				if(dictionaryParamDetailData.getName().toLowerCase().contains(searchByName.toLowerCase())){

					dictionaryParameterDetailListByName.add(dictionaryParamDetailData);
				}
			}

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return dictionaryParameterDetailListByName;
	}


	public IDictionaryParameterDetailData getDictionaryIdByName(String name) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryParameterDetailData.class);
			criteria.add(Restrictions.eq("name", name));

			return (IDictionaryParameterDetailData)criteria.uniqueResult();
		}
		catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
	}


	public DictionaryData getDictionaryByName(String dictionaryId,String searchByName) throws DataManagerException{
		try
		{
			Session session = getSession();

			Criteria criteria = session.createCriteria(DictionaryData.class).add(Restrictions.eq("dictionaryId",dictionaryId));
			DictionaryData dictionaryData =(DictionaryData) criteria.uniqueResult();
			if(searchByName.equalsIgnoreCase("")){
				return dictionaryData;
			}else{
				Criteria tempCriteria = session.createCriteria(DictionaryParameterDetailData.class).add(Restrictions.eq("dictionaryId", dictionaryData.getDictionaryId()));
				List lstDictionaryParameterDetail = tempCriteria.list();
				List lstResultDictionrParameterDetail = new ArrayList();
				Iterator iterator = lstDictionaryParameterDetail.iterator();
				while(iterator.hasNext()){
					IDictionaryParameterDetailData dictionaryParameterDetailData = (IDictionaryParameterDetailData)iterator.next();
					if(dictionaryParameterDetailData.getName().toLowerCase().contains(searchByName.toLowerCase())){
						lstResultDictionrParameterDetail.add(dictionaryParameterDetailData);
					}	
				}
				Collections.sort(lstResultDictionrParameterDetail);
				Set  setDictionaryParameterDetail = new HashSet(lstResultDictionrParameterDetail); 
				dictionaryData.setDictionaryParameterDetail(setDictionaryParameterDetail);

				return  dictionaryData;
			}
		}
		catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}

	}

	public List<DictionaryParameterDetailData> getDictionaryParameterDetailList(Long dictionaryId) throws DataManagerException {
		List<DictionaryParameterDetailData> list=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DictionaryParameterDetailData.class);
			if(dictionaryId != 0){
			criteria.add(Restrictions.eq("dictionaryId", dictionaryId)).add(Restrictions.isNull("parentDetailId")).addOrder(Order.asc("vendorParameterId"));
			}
			list=criteria.list();

		}
		catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}

		return list;

	}

	public DictionaryData getDictionaryData(String dictionaryId)throws DataManagerException {
		DictionaryData data = null;
		try{

			Session session = getSession();
			Criteria criteria=session.createCriteria(DictionaryData.class);
			criteria.add(Restrictions.eq("dictionaryId",dictionaryId));
			data=(DictionaryData) criteria.uniqueResult();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
		return data;
	}

	public DictionaryData updateDictionary(IDictionaryData dictionaryData)
	throws DataManagerException {
		DictionaryData dictionaryData2 = (DictionaryData)dictionaryData;
		try {

			Session session = getSession();
			Set lstDictionaryDetail = dictionaryData.getDictionaryParameterDetail();
			dictionaryData.setDictionaryParameterDetail(null);
			List list = session.createCriteria(DictionaryData.class).add(Restrictions.eq("dictionaryId", dictionaryData.getDictionaryId())).list();
			if(list!=null && !list.isEmpty()){
				DictionaryData existingDictionaryData = (DictionaryData)list.get(0);

				existingDictionaryData.setDictionaryNumber(dictionaryData.getDictionaryNumber());
				existingDictionaryData.setDictionaryParameterDetail(dictionaryData.getDictionaryParameterDetail());
				existingDictionaryData.setEditable(dictionaryData.getEditable());
				existingDictionaryData.setLastModifiedByStaff(dictionaryData.getLastModifiedByStaff());
				existingDictionaryData.setLastModifiedByStaffId(dictionaryData.getLastModifiedByStaffId());
				existingDictionaryData.setLastModifiedDate(dictionaryData.getLastModifiedDate());
				existingDictionaryData.setModalNumber(dictionaryData.getModalNumber());
				existingDictionaryData.setName(dictionaryData.getName());
				existingDictionaryData.setDescription(dictionaryData.getDescription());

				session.update(existingDictionaryData);
				session.flush();

				Criteria criteria = session.createCriteria(DictionaryParameterDetailData.class).add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()));
				List oldParameterDetailList = criteria.list();

				for (Iterator<DictionaryParameterDetailData> iterator = oldParameterDetailList.iterator(); iterator.hasNext();) {
					DictionaryParameterDetailData object = iterator.next();
					Logger.logDebug(MODULE,"Name is:"+object.getName());
					session.delete(object);
				}
				session.flush();

				Iterator itrDictionaryDetail = lstDictionaryDetail.iterator();
				//save

				while(itrDictionaryDetail.hasNext()){

					DictionaryParameterDetailData dictionaryDetailData = (DictionaryParameterDetailData)itrDictionaryDetail.next();
					dictionaryDetailData.setDictionaryId(dictionaryData.getDictionaryId());
					String attributeId=dictionaryDetailData.getVendorId()+":"+dictionaryDetailData.getVendorParameterId();	  
					dictionaryDetailData.setAttributeId(attributeId);
					session.save(dictionaryDetailData); 
					session.flush();
					Logger.logDebug(MODULE, "Save Data Attribute :"+dictionaryDetailData.getName());
					recursiveSave(dictionaryDetailData,dictionaryData,session);


				}

				dictionaryData2.setDictionaryParameterDetail(lstDictionaryDetail);

			}    		
		} catch (HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return dictionaryData2;

	}

	public DictionaryData getDictionaryDataByVendor(long vendorId) throws DataManagerException {
		DictionaryData data = null;
		try{

			Session session = getSession();
			Criteria criteria=session.createCriteria(DictionaryData.class);
			criteria.add(Restrictions.eq("vendorId",vendorId));
			data=(DictionaryData) criteria.uniqueResult();
			if(data != null){
				criteria=session.createCriteria(DictionaryParameterDetailData.class).add(Restrictions.eq("dictionaryId", data.getDictionaryId())).add(Restrictions.isNull("parentDetailId"));
				List<DictionaryParameterDetailData> dictionaryParameterDetailList = criteria.list();
				data.setDictionaryParameterDetailList(dictionaryParameterDetailList);
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
		return data;
	}

	public List<DictionaryParameterDetailData> getOnlyDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException {
		List<DictionaryParameterDetailData> dictionaryParamDetailList = null;
		List tmpDictionaryParamObjectList;
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(DictionaryParameterDetailData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property("dictionaryParameterDetailId"));
			proList.add(Projections.property("name"));
			proList.add(Projections.property("vendorId"));
			proList.add(Projections.property("attributeId"));
			proList.add(Projections.property("dataTypeId"));

			criteria.setProjection(proList);
			criteria.add( Restrictions.disjunction()
					.add( Restrictions.ilike("name","%"+searchNameOrAttributeId+"%" ) )
					.add( Restrictions.like("attributeId",searchNameOrAttributeId+"%"))
			);

			tmpDictionaryParamObjectList = criteria.list();	
			DictionaryParameterDetailData dictionaryParameterDetailData;
			dictionaryParamDetailList = new ArrayList<DictionaryParameterDetailData>();

			if(tmpDictionaryParamObjectList != null && tmpDictionaryParamObjectList.size() > 0) {
				Iterator dictionaryParamListItr = tmpDictionaryParamObjectList.iterator();
				while(dictionaryParamListItr.hasNext()) { 
					Object tmpDictionaryParameterDetailData[] = (Object[]) dictionaryParamListItr.next();
					dictionaryParameterDetailData = new DictionaryParameterDetailData();
					dictionaryParameterDetailData.setDictionaryParameterDetailId((String)tmpDictionaryParameterDetailData[0]);
					dictionaryParameterDetailData.setName((String)tmpDictionaryParameterDetailData[1]);
					dictionaryParameterDetailData.setVendorId((Long)tmpDictionaryParameterDetailData[2]);
					dictionaryParameterDetailData.setAttributeId((String)tmpDictionaryParameterDetailData[3]);
					dictionaryParameterDetailData.setDataTypeId((String)tmpDictionaryParameterDetailData[4]);
					dictionaryParamDetailList.add(dictionaryParameterDetailData);
				}
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		} 
		return dictionaryParamDetailList;
	}
	public DictionaryParameterDetailData getDictionaryParameterDetailData(String dictionaryParameterId) throws DataManagerException {
		DictionaryParameterDetailData dictionaryParameterDetailData=null;
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(DictionaryParameterDetailData.class).add(Restrictions.eq("dictionaryParameterDetailId", dictionaryParameterId));

			dictionaryParameterDetailData = (DictionaryParameterDetailData)criteria.uniqueResult();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		} 
		return  dictionaryParameterDetailData;
	}

	public DictionaryParameterDetailData getOnlyDictionaryParametersByAttributeId(
			String attributeId) throws DataManagerException {
		
		DictionaryParameterDetailData dictionaryParameterDetailData=null;
		List tmpDictionaryParamObjectList;
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(DictionaryParameterDetailData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property("dictionaryParameterDetailId"));
			proList.add(Projections.property("name"));
			proList.add(Projections.property("vendorId"));
			proList.add(Projections.property("attributeId"));
			proList.add(Projections.property("dataTypeId"));
			proList.add(Projections.property("predefinedValues"));

			criteria.setProjection(proList);
			criteria.add( Restrictions.disjunction()
					.add( Restrictions.like("attributeId",attributeId))
			);

			tmpDictionaryParamObjectList = criteria.list();	
			
			

			if(tmpDictionaryParamObjectList != null && tmpDictionaryParamObjectList.size() > 0) {


				Object tmpDictionaryParameterDetailData[] = (Object[]) tmpDictionaryParamObjectList.get(0);
				dictionaryParameterDetailData = new DictionaryParameterDetailData();
				dictionaryParameterDetailData.setDictionaryParameterDetailId((String)tmpDictionaryParameterDetailData[0]);
				dictionaryParameterDetailData.setName((String)tmpDictionaryParameterDetailData[1]);
				dictionaryParameterDetailData.setVendorId((Long)tmpDictionaryParameterDetailData[2]);
				dictionaryParameterDetailData.setAttributeId((String)tmpDictionaryParameterDetailData[3]);
				dictionaryParameterDetailData.setDataTypeId((String)tmpDictionaryParameterDetailData[4]);
				dictionaryParameterDetailData.setPredefinedValues((String)tmpDictionaryParameterDetailData[5]);
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		} 
		return dictionaryParameterDetailData;
	}
}
