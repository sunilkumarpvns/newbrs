package com.elitecore.netvertexsm.hibernate.servermgr.dictionary.radius;

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

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.data.DataTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.RadiusDictionaryDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryParamDetailData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.IRadiusDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.IRadiusDictionaryParamDetailData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.logger.Logger;


public class HRadiusDictionaryDataManager extends HBaseDataManager implements RadiusDictionaryDataManager {
	private static final String MODULE="HRadiusDictionaryDataManager";

	@SuppressWarnings("unchecked")
	public List<RadiusDictionaryData> getAllList() throws DataManagerException {

		List<RadiusDictionaryData> dictionaryList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);
			dictionaryList = criteria.list();

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return dictionaryList;
	} 

	@SuppressWarnings("unchecked")
	public List<RadiusDictionaryData> getList() throws DataManagerException {

		List<RadiusDictionaryData> dictionaryList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);
			criteria.add(Restrictions.eq("commonStatusId","CST01"));            
			dictionaryList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return dictionaryList;
	} 

	@SuppressWarnings("unchecked")
	public List<RadiusDictionaryData> getOnlyDictionaryDataList() throws DataManagerException {
		List<RadiusDictionaryData> dictionaryParamList = null;
		List<RadiusDictionaryData> tmpDictionaryParamList;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property("dictionaryId"));
			proList.add(Projections.property("name"));
			proList.add(Projections.property("commonStatusId"));
			proList.add(Projections.property("vendorId"));

			criteria.setProjection(proList);
			dictionaryParamList = criteria.list();
			RadiusDictionaryData dictionaryData;
			tmpDictionaryParamList = new ArrayList<RadiusDictionaryData>();

			if(dictionaryParamList != null && dictionaryParamList.size() > 0) {
				Iterator dictionaryParamListItr = dictionaryParamList.iterator();
				while(dictionaryParamListItr.hasNext()) { 
					Object tmpDictionaryData[] = (Object[]) dictionaryParamListItr.next();
					dictionaryData = new RadiusDictionaryData();
					dictionaryData.setDictionaryId((Long)tmpDictionaryData[0]);
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

	@SuppressWarnings("unchecked")
	public List<RadiusDictionaryData> getList(IRadiusDictionaryData dictionaryData) throws DataManagerException {
		List<RadiusDictionaryData> dictionaryList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);

			if(dictionaryData != null){
				criteria.add(Restrictions.eq("commonStatusId","CST01"));
				if(dictionaryData.getDictionaryId() >0){
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

	@SuppressWarnings("unchecked")
	public List<RadiusDictionaryData> getAllList(IRadiusDictionaryData dictionaryData) throws DataManagerException{
		List<RadiusDictionaryData> dictionaryList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);

			if(dictionaryData != null){

				if(dictionaryData.getDictionaryId() >0){
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

	public IRadiusDictionaryData getAllListByVendor(IRadiusDictionaryData dictionaryData) throws DataManagerException{

		IRadiusDictionaryData retDictionaryData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);

			if(dictionaryData != null){
				if(dictionaryData.getVendorId() != -1){
					criteria.add(Restrictions.eq("vendorId",new Long(dictionaryData.getVendorId()) ));
				}

			}

			retDictionaryData = (IRadiusDictionaryData)criteria.uniqueResult();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return retDictionaryData;

	}

	public RadiusDictionaryData updateStatus(long dictionaryId, String commonStatus,Timestamp statusChangeDate) throws DataManagerException{


		Session session = getSession();
		RadiusDictionaryData dictionaryData = null;

		try{
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);
			dictionaryData = (RadiusDictionaryData)criteria.add(Restrictions.eq("dictionaryId",dictionaryId))
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
		return dictionaryData;
	}



	@SuppressWarnings("unchecked")
	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailList(IRadiusDictionaryParamDetailData dictionaryParameterDetailData) throws DataManagerException{
		List<RadiusDictionaryParamDetailData> dictionaryParameterDetailList ;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryParamDetailData.class);
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


	public void create(IRadiusDictionaryData dictionaryData) throws DataManagerException{

		try {
			Session session = getSession();

			Set<IRadiusDictionaryParamDetailData> lstDictionaryDetail = dictionaryData.getDictionaryParameterDetail();
			dictionaryData.setDictionaryParameterDetail(null);

			session.save(dictionaryData);


			Iterator<IRadiusDictionaryParamDetailData> itrDictionaryDetail = lstDictionaryDetail.iterator();

			while(itrDictionaryDetail.hasNext()){
				IRadiusDictionaryParamDetailData dictionaryDetailData = (IRadiusDictionaryParamDetailData)itrDictionaryDetail.next();
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
	private void recursiveSave(IRadiusDictionaryParamDetailData dictionaryDetailData, IRadiusDictionaryData dictionaryData, Session session) throws HibernateException{
		List<RadiusDictionaryParamDetailData> nestedParameterDetailList = dictionaryDetailData.getNestedParameterDetailList();

		if(nestedParameterDetailList!=null && !nestedParameterDetailList.isEmpty()){

			Iterator<RadiusDictionaryParamDetailData> iterator = nestedParameterDetailList.iterator();
			while(iterator.hasNext()){
				IRadiusDictionaryParamDetailData tempDictionaryDetailData = (IRadiusDictionaryParamDetailData)iterator.next();
				tempDictionaryDetailData.setDictionaryId(dictionaryData.getDictionaryId());
				tempDictionaryDetailData.setParentDetailId(dictionaryDetailData.getDictionaryParameterDetailId());
				String attributeId=getAttributeId(dictionaryDetailData,tempDictionaryDetailData,session);
				tempDictionaryDetailData.setAttributeId(attributeId);
				session.save(tempDictionaryDetailData);

				recursiveSave(tempDictionaryDetailData,dictionaryData,session);
			}
		}
	}


	private String getAttributeId(IRadiusDictionaryParamDetailData parentData,IRadiusDictionaryParamDetailData childData, Session session) {
		String atttributeId=null;
		atttributeId=parentData.getAttributeId()+":"+childData.getVendorParameterId();
		return atttributeId;
	}

	public void updateBasicDetails(IRadiusDictionaryData dictionaryData,Timestamp statusChageDate) throws DataManagerException{
		Session session = getSession();

		try {
			if(dictionaryData != null){
				Criteria criteria = session.createCriteria(RadiusDictionaryParamDetailData.class);
				criteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()));
				List<IRadiusDictionaryParamDetailData> lstDictionaries = criteria.list();

				if(lstDictionaries.size()>0){
					for(int i=0;i<lstDictionaries.size();i++){
						IRadiusDictionaryParamDetailData dictinaryParamDetailData = (IRadiusDictionaryParamDetailData)lstDictionaries.get(i);
						session.delete(dictinaryParamDetailData);
					}
				}
				session.flush();

				Set<IRadiusDictionaryParamDetailData> stDictionaryDetail = dictionaryData.getDictionaryParameterDetail();
				Iterator<IRadiusDictionaryParamDetailData> itrDictionaryDetail = stDictionaryDetail.iterator();
				while(itrDictionaryDetail.hasNext()){
					IRadiusDictionaryParamDetailData dictionaryParamDetailData = (IRadiusDictionaryParamDetailData)itrDictionaryDetail.next();
					dictionaryParamDetailData.setDictionaryId(dictionaryData.getDictionaryId());
					session.save(dictionaryParamDetailData);
				}

				criteria = session.createCriteria(RadiusDictionaryData.class);
				criteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()));
				IRadiusDictionaryData newDictionaryData = (IRadiusDictionaryData)criteria.uniqueResult();

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


	public void delete(long dictionaryId) throws DataManagerException{
		RadiusDictionaryData dictionaryData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);
			if(dictionaryId>0){
				dictionaryData = (RadiusDictionaryData)criteria.add(Restrictions.eq("dictionaryId",dictionaryId)).uniqueResult();
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



	@SuppressWarnings("unchecked")
	private void recursiveDelete(IRadiusDictionaryParamDetailData dictionaryDetailData,Session session) throws HibernateException{
		if(dictionaryDetailData!=null){
			Criteria criteria = session.createCriteria(RadiusDictionaryParamDetailData.class).add(Restrictions.eq("parentDetailId", dictionaryDetailData.getDictionaryParameterDetailId()));
			List<RadiusDictionaryParamDetailData> nestedParameterDetailList = criteria.list();
			if(nestedParameterDetailList!=null && !nestedParameterDetailList.isEmpty()){

				for (int i = 0; i < nestedParameterDetailList.size(); i++) {
					RadiusDictionaryParamDetailData temp= nestedParameterDetailList.get(i);
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

	@SuppressWarnings("unchecked")
	public List<DataTypeData> getDatatype() throws DataManagerException
	{
		List<DataTypeData> dataTypeList = null;
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
	public String getDictionaryParameterName(String vendorId,long dictionaryParameterId) throws DataManagerException{

		RadiusDictionaryData dictionaryData = null;
		RadiusDictionaryParamDetailData dictionaryParameterDetailData=null;
		String dictionaryParameterName=null;
		try {

			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);
			dictionaryData = (RadiusDictionaryData)criteria.add(Restrictions.eq("vendorId",Long.parseLong(vendorId))).uniqueResult();

			Criteria DictionaryParameterCriteria = session.createCriteria(RadiusDictionaryParamDetailData.class);
			dictionaryParameterDetailData  = (RadiusDictionaryParamDetailData)DictionaryParameterCriteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()))
			.add(Restrictions.eq("vendorParameterId",dictionaryParameterId)).uniqueResult();

			dictionaryParameterName=dictionaryParameterDetailData.getName();

		}catch (HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch (Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return dictionaryParameterName;
	}

	public String getDictionaryParamDetail(String vendorId,long dictionaryParameterId) throws DataManagerException{

		RadiusDictionaryData dictionaryData = null;
		RadiusDictionaryParamDetailData dictionaryParameterDetailData=null;
		String dictionaryParameterName=null;
		try {

			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryData.class);

			dictionaryData = (RadiusDictionaryData)criteria.add(Restrictions.eq("vendorId",Long.parseLong(vendorId))).uniqueResult();

			Criteria DictionaryParameterCriteria = session.createCriteria(RadiusDictionaryParamDetailData.class);
			dictionaryParameterDetailData  = (RadiusDictionaryParamDetailData)DictionaryParameterCriteria.add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()))
			.add(Restrictions.eq("vendorParameterId",(int)dictionaryParameterId)).uniqueResult();

			dictionaryParameterName=dictionaryData.getName()+" : "+dictionaryParameterDetailData.getName();

		}catch (HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch (Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return dictionaryParameterName;
	}

	@SuppressWarnings("unchecked")
	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailList(IRadiusDictionaryParamDetailData dictionaryParameterDetailData, String criteriaName) throws DataManagerException{
		List<RadiusDictionaryParamDetailData> dictionaryParameterDetailList ;
		List<RadiusDictionaryParamDetailData> dictionaryParameterDetailListByCriteria = new ArrayList<RadiusDictionaryParamDetailData>();
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryParamDetailData.class);
			criteria.addOrder(Order.asc("name"));
			if(dictionaryParameterDetailData != null ){
				if(dictionaryParameterDetailData.getDictionaryId() > 0){
					criteria.add(Restrictions.eq("dictionaryId",dictionaryParameterDetailData.getDictionaryId()));
				}
			} 


			dictionaryParameterDetailList = criteria.list();

			Iterator<RadiusDictionaryParamDetailData> iter = dictionaryParameterDetailList.iterator();

			if(criteriaName.equalsIgnoreCase("")){
				return dictionaryParameterDetailList;
			}
			else{
				while(iter.hasNext()){
					RadiusDictionaryParamDetailData dictionaryParamDetailData = (RadiusDictionaryParamDetailData)iter.next();

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

	@SuppressWarnings("unchecked")
	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailAllList() throws DataManagerException {

		List<RadiusDictionaryParamDetailData> dictionaryParameterDetailList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryParamDetailData.class);
			criteria.addOrder(Order.asc("name"));
			dictionaryParameterDetailList = criteria.list();

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		return dictionaryParameterDetailList;
	}

	@SuppressWarnings("unchecked")
	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailAllList(String searchByName) throws DataManagerException{

		List<RadiusDictionaryParamDetailData> dictionaryParameterDetailList = null;
		List<RadiusDictionaryParamDetailData> dictionaryParameterDetailListByName = new ArrayList<RadiusDictionaryParamDetailData>();
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryParamDetailData.class);
			criteria.addOrder(Order.asc("name"));
			dictionaryParameterDetailList = criteria.list();

			Iterator<RadiusDictionaryParamDetailData> iter = dictionaryParameterDetailList.iterator();

			while(iter.hasNext()){
				RadiusDictionaryParamDetailData dictionaryParamDetailData = (RadiusDictionaryParamDetailData)iter.next();


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


	public IRadiusDictionaryParamDetailData getDictionaryIdByName(String name) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryParamDetailData.class);
			criteria.add(Restrictions.eq("name", name));

			return (IRadiusDictionaryParamDetailData)criteria.uniqueResult();
		}
		catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
	}


	public RadiusDictionaryData getDictionaryByName(long dictionaryId,String searchByName) throws DataManagerException{
		try
		{
			Session session = getSession();

			Criteria criteria = session.createCriteria(RadiusDictionaryData.class).add(Restrictions.eq("dictionaryId",dictionaryId));
			RadiusDictionaryData dictionaryData =(RadiusDictionaryData) criteria.uniqueResult();
			if(searchByName.equalsIgnoreCase("")){
				return dictionaryData;
			}else{
				Criteria tempCriteria = session.createCriteria(RadiusDictionaryParamDetailData.class).add(Restrictions.eq("dictionaryId", dictionaryData.getDictionaryId()));
				List<RadiusDictionaryParamDetailData> lstDictionaryParameterDetail = tempCriteria.list();
				List<RadiusDictionaryParamDetailData> lstResultDictionrParameterDetail = new ArrayList<RadiusDictionaryParamDetailData>();
				Iterator<RadiusDictionaryParamDetailData> iterator = lstDictionaryParameterDetail.iterator();
				while(iterator.hasNext()){
					RadiusDictionaryParamDetailData dictionaryParameterDetailData = (RadiusDictionaryParamDetailData)iterator.next();
					if(dictionaryParameterDetailData.getName().toLowerCase().contains(searchByName.toLowerCase())){
						lstResultDictionrParameterDetail.add(dictionaryParameterDetailData);
					}	
				}
				Collections.sort(lstResultDictionrParameterDetail);
				Set<RadiusDictionaryParamDetailData>  setDictionaryParameterDetail = new HashSet(lstResultDictionrParameterDetail); 
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

	public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailList(Long dictionaryId) throws DataManagerException {
		List<RadiusDictionaryParamDetailData> list=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusDictionaryParamDetailData.class);
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

	public RadiusDictionaryData getDictionaryData(Long dictionaryId)throws DataManagerException {
		RadiusDictionaryData data = null;
		try{

			Session session = getSession();
			Criteria criteria=session.createCriteria(RadiusDictionaryData.class);
			criteria.add(Restrictions.eq("dictionaryId",dictionaryId));
			data=(RadiusDictionaryData) criteria.uniqueResult();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		}
		return data;
	}

	public RadiusDictionaryData updateDictionary(IRadiusDictionaryData dictionaryData) throws DataManagerException {
		RadiusDictionaryData dictionaryData2 = (RadiusDictionaryData)dictionaryData;
		try {

			Logger.logDebug("hdictionarydatamanager","hdictionary called...:"+dictionaryData);
			Session session = getSession();
			Set<RadiusDictionaryParamDetailData> lstDictionaryDetail = dictionaryData.getDictionaryParameterDetail();
			dictionaryData.setDictionaryParameterDetail(null);
			List<RadiusDictionaryData> list = session.createCriteria(RadiusDictionaryData.class).add(Restrictions.eq("dictionaryId", dictionaryData.getDictionaryId())).list();
			if(list!=null && !list.isEmpty()){
				RadiusDictionaryData existingDictionaryData = (RadiusDictionaryData)list.get(0);

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

				Criteria criteria = session.createCriteria(RadiusDictionaryParamDetailData.class).add(Restrictions.eq("dictionaryId",dictionaryData.getDictionaryId()));
				List<RadiusDictionaryParamDetailData> oldParameterDetailList = criteria.list();

				for (Iterator<RadiusDictionaryParamDetailData> iterator = oldParameterDetailList.iterator(); iterator.hasNext();) {
					RadiusDictionaryParamDetailData object = iterator.next();
					Logger.logDebug("test","Name is:"+object.getName());
					session.delete(object);
				}
				session.flush();

				Iterator<RadiusDictionaryParamDetailData> itrDictionaryDetail = lstDictionaryDetail.iterator();
				//save

				while(itrDictionaryDetail.hasNext()){

					RadiusDictionaryParamDetailData dictionaryDetailData = (RadiusDictionaryParamDetailData)itrDictionaryDetail.next();
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

	public RadiusDictionaryData getDictionaryDataByVendor(long vendorId) throws DataManagerException {
		RadiusDictionaryData data = null;
		try{

			Session session = getSession();
			Criteria criteria=session.createCriteria(RadiusDictionaryData.class);
			criteria.add(Restrictions.eq("vendorId",vendorId));
			data=(RadiusDictionaryData) criteria.uniqueResult();
			if(data != null){
				criteria=session.createCriteria(RadiusDictionaryParamDetailData.class).add(Restrictions.eq("dictionaryId", data.getDictionaryId())).add(Restrictions.isNull("parentDetailId"));
				List<RadiusDictionaryParamDetailData> dictionaryParameterDetailList = criteria.list();
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

	public List<RadiusDictionaryParamDetailData> getOnlyDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException {
		List<RadiusDictionaryParamDetailData> dictionaryParamDetailList = null;
		List<RadiusDictionaryParamDetailData> tmpDictionaryParamObjectList;
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(RadiusDictionaryParamDetailData.class);
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
			RadiusDictionaryParamDetailData dictionaryParameterDetailData;
			dictionaryParamDetailList = new ArrayList<RadiusDictionaryParamDetailData>();

			if(tmpDictionaryParamObjectList != null && tmpDictionaryParamObjectList.size() > 0) {
				Iterator dictionaryParamListItr = tmpDictionaryParamObjectList.iterator();
				while(dictionaryParamListItr.hasNext()) { 
					Object tmpDictionaryParameterDetailData[] = (Object[]) dictionaryParamListItr.next();
					dictionaryParameterDetailData = new RadiusDictionaryParamDetailData();
					dictionaryParameterDetailData.setDictionaryParameterDetailId((Long)tmpDictionaryParameterDetailData[0]);
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
	public RadiusDictionaryParamDetailData getDictionaryParameterDetailData(Long dictionaryParameterId) throws DataManagerException {
		RadiusDictionaryParamDetailData dictionaryParameterDetailData=null;
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(RadiusDictionaryParamDetailData.class).add(Restrictions.eq("dictionaryParameterDetailId", dictionaryParameterId));

			dictionaryParameterDetailData = (RadiusDictionaryParamDetailData)criteria.uniqueResult();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}
		catch (Exception e) {
			throw new DataManagerException(e.getMessage(), e);
		} 
		return  dictionaryParameterDetailData;
	}

	public RadiusDictionaryParamDetailData getOnlyDictionaryParametersByAttributeId(
			String attributeId) throws DataManagerException {

		RadiusDictionaryParamDetailData dictionaryParameterDetailData=null;
		List tmpDictionaryParamObjectList;
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(RadiusDictionaryParamDetailData.class);
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
				dictionaryParameterDetailData = new RadiusDictionaryParamDetailData();
				dictionaryParameterDetailData.setDictionaryParameterDetailId((Long)tmpDictionaryParameterDetailData[0]);
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
