package com.elitecore.netvertexsm.hibernate.locationconfig.area;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.AreaDataManager;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.LacData;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaNetworkRelationData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HAreaDataManager extends HBaseDataManager implements AreaDataManager {

	@Override
	public void create(AreaData areaData) throws DataManagerException {
		try{
			
			Session session = getSession();
			session.save(areaData);
			session.flush();

		}catch(ConstraintViolationException hExp){
	        throw hExp;
	    }catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public void update(AreaData areaData) throws DataManagerException {
		try{
			
			List<LacData> deleteLacDataList = areaData.getDeleteLacIds();
			delete(deleteLacDataList);
			Session session = getSession();		
			session.update(areaData);
			session.flush();
			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@SuppressWarnings("unchecked")
	public void delete(AreaNetworkRelationData areaNetworkRelationData) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AreaNetworkRelationData.class);
			criteria.add(Restrictions.eq("areaId", areaNetworkRelationData.getAreaId()));
			List<AreaNetworkRelationData> tempList = criteria.list();
			deleteObjectList(tempList,session);			
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void delete(Long[] areaIDs) throws DataManagerException {
		try{
			Session session = getSession();
			List<AreaData> areaDataList = session.createCriteria(AreaData.class).add(Restrictions.in("areaId", areaIDs)).list();
			deleteObjectList(areaDataList,session);			
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public AreaData getAreaData(Long areaId) throws DataManagerException {
		AreaData areaData = null;
		try{
			
			Session session = getSession();			
			areaData = (AreaData)session.createCriteria(AreaData.class).add(Restrictions.eq("areaId", areaId)).uniqueResult();

			return areaData;
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	@SuppressWarnings("unchecked")
	public PageList search(AreaData areaData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Example example = Example.create(areaData).enableLike(MatchMode.ANYWHERE).ignoreCase();
			
			Criteria criteria = session.createCriteria(AreaData.class).add(example).addOrder(Order.asc("area"));			
			
			if(areaData.getArea()!=null && areaData.getArea().length()>0) {
				criteria.add(Restrictions.ilike("area", areaData.getArea(), MatchMode.ANYWHERE));
			}
			
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			
			if(pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			
			List<AreaData> areaDataList = criteria.list();
	        long totalPages = (long)Math.ceil(totalItems/pageSize);
	        if(totalItems%pageSize == 0)
				totalPages-=1;
	        
	        pageList = new PageList(areaDataList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}
		
	@SuppressWarnings("unchecked")
	public void delete(List<LacData> deleteDataList) throws DataManagerException {
		try{
			Session session = getSession();
			deleteObjectList(deleteDataList,session);			
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public List<AreaData> getAreasByCity(Long cityID)
			throws DataManagerException {
		List<AreaData> areaList = null;
		try{
			Session session = getSession();			
			areaList = session.createCriteria(AreaData.class).add(Restrictions.eq("cityId", cityID)).list();
			return areaList;
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	}
	}
}