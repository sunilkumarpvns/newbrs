package com.elitecore.netvertexsm.hibernate.locationconfig.city;

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
import com.elitecore.netvertexsm.datamanager.locationconfig.city.CityDataManager;
import com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HCityDataManager extends HBaseDataManager implements CityDataManager {

	@Override
	public void createCityByList(List<CityData> cityDataList)
			throws DataManagerException {
	for (CityData cityData: cityDataList) {
		create(cityData);
	}
	}
	
	
	
	@Override
	public void create(CityData cityData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(cityData);
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
	public void update(CityData cityData) throws DataManagerException {
		try{
			Session session = getSession();
			session.update(cityData);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}

	@Override
	public void delete(Long[] cityIds) throws DataManagerException {
		try{
			Session session = getSession();			
			List list = session.createCriteria(CityData.class).add(Restrictions.in("cityId", cityIds)).list();
			deleteObjectList(list,session);
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
		
	}

	@Override
	public CityData getCityData(Long cityId) throws DataManagerException {
		CityData cityData=null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CityData.class).add(Restrictions.eq("cityId",cityId));
			List cityList = criteria.list();
			if(cityList!=null&&!cityList.isEmpty()){
				cityData=(CityData) cityList.get(0);
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return cityData;
	}

	@Override
	public PageList search(CityData cityData, int pageNo, int pageSize)
			throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(CityData.class).addOrder(Order.asc("cityName"));
			if(cityData.getCityName() != null && cityData.getCityName().length() > 0){
				criteria.add(Restrictions.ilike("cityName", cityData.getCityName(),MatchMode.ANYWHERE));
			}
			
			if(cityData.getRegionId()>0){
				criteria.add(Restrictions.eq("regionId", cityData.getRegionId()));
			}
			
			int totalItems = criteria.list().size();
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

	@Override
	public List<CityData> getCityDataList() throws DataManagerException {
		List<CityData> cityDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CityData.class);
			cityDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return cityDataList;
	}
	
	@Override
	public List<CityData> getCityDataList(long regionId) throws DataManagerException {
		List<CityData> cityDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CityData.class).add(Restrictions.eq("regionId",regionId));
			cityDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return cityDataList;
	}
	
	
	

}
