package com.elitecore.netvertexsm.hibernate.locationconfig.region;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.RegionDataManager;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HRegionDataManager extends HBaseDataManager implements RegionDataManager {
   
	@Override
	public void createRegionByList(List<RegionData> regionDataList)
			throws DataManagerException {
	for (RegionData regionData : regionDataList) {
		create(regionData);
	}
	}
	
	
	
	
	
	@Override
	public void create(RegionData regionData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(regionData);
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
	public void update(RegionData regionData) throws DataManagerException {
		try{
			Session session = getSession();
/*			Criteria criteria = session.createCriteria(RegionData.class);
			criteria.add(Restrictions.eq("regionId", regionData.getRegionId()));
			
			RegionData data = (RegionData)criteria.uniqueResult();
			data.setRegionName(regionData.getRegionName());*/
			session.update(regionData);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}

	@Override
	public void delete(Long[] regionIds) throws DataManagerException {
		try{
			Session session = getSession();			
			List list = session.createCriteria(RegionData.class).add(Restrictions.in("regionId", regionIds)).list();
			deleteObjectList(list,session);
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
		
	}

	@Override
	public RegionData getRegionData(Long regionId) throws DataManagerException {
		RegionData regionData = null;
		try{
			Session session = getSession();
			List list = session.createCriteria(RegionData.class).add(Restrictions.eq("regionId", regionId)).list();
			if(list != null && !list.isEmpty()){
				regionData = (RegionData) list.get(0);
			}
			return regionData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public PageList search(RegionData regionData, int pageNo, int pageSize)
			throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RegionData.class).addOrder(Order.asc("regionName"));
			if(regionData.getRegionName() != null && regionData.getRegionName().length() > 0){
				criteria.add(Restrictions.ilike("regionName", regionData.getRegionName(),MatchMode.ANYWHERE));
			}
			if(regionData.getCountryId()>0){
				criteria.add(Restrictions.eq("countryId", regionData.getCountryId()));
			}int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo - 1) * pageSize));
			if(pageSize > 0){
				criteria.setMaxResults(pageSize);
			}
			List regionDataList = criteria.list();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems % pageSize == 0){
				totalPages -= 1;
			}
			pageList = new PageList(regionDataList, pageNo, totalPages, totalItems);
			}catch (HibernateException hExe) {
				throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}





	@SuppressWarnings("unchecked")
	@Override
	public List<RegionData> getRegionDataList() throws DataManagerException {
		List<RegionData> regionDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RegionData.class);
			regionDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return regionDataList;
	}

	

}
