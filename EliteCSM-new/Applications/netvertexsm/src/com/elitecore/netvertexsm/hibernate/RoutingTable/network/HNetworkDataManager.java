package com.elitecore.netvertexsm.hibernate.RoutingTable.network;

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
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.NetworkDataManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandOperatorRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.hibernate.devicemgmt.HDeviceMgmtDataManager;

public class HNetworkDataManager extends HBaseDataManager implements NetworkDataManager{
	
	private static final String MODULE=HDeviceMgmtDataManager.class.getSimpleName();
	

	@Override
	public void create(NetworkData networkData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(networkData);
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
	public void update(NetworkData networkData) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NetworkData.class);
			criteria.add(Restrictions.eq("networkID", networkData.getNetworkID()));
			
			NetworkData data = (NetworkData)criteria.uniqueResult();
			data.setNetworkID(networkData.getNetworkID());
			data.setNetworkName(networkData.getNetworkName());
			data.setOperatorID(networkData.getOperatorID());
			data.setCountryID(networkData.getCountryID());
			data.setMcc(networkData.getMcc());
			data.setMnc(networkData.getMnc());
			data.setBrandID(networkData.getBrandID());
			data.setTechnology(networkData.getTechnology());				
			
			session.update(data);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	@Override
	public void delete(Long[] networkIDs) throws DataManagerException {
		try{
			Session session = getSession();			
			List list = session.createCriteria(NetworkData.class).add(Restrictions.in("networkID", networkIDs)).list();
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
	public NetworkData getNetworkDetailData(Long networkID) throws DataManagerException {
		NetworkData networkData=null;
		try{
			Session session = getSession();			
			List list = session.createCriteria(NetworkData.class).add(Restrictions.eq("networkID", networkID)).list();
			if(list!=null && !list.isEmpty()){
				networkData = (NetworkData) list.get(0);
			}
			return networkData;
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
	@Override
	public PageList search(NetworkData networkData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Example example = Example.create(networkData).enableLike(MatchMode.ANYWHERE).ignoreCase();
			
			Criteria criteria = session.createCriteria(NetworkData.class).add(example).addOrder(Order.asc("networkName"));			
			
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			
			if(pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			List bodList = criteria.list();
	        long totalPages = (long)Math.ceil(totalItems/pageSize);
	        if(totalItems%pageSize == 0)
				totalPages-=1;
	        
	        pageList = new PageList(bodList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}
	
	@Override
	public List<CountryData> getCountryDataList() throws DataManagerException{
		List<CountryData> countryDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CountryData.class).addOrder(Order.asc("name"));		
			countryDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return countryDataList;	
	}
	
	@Override
	public List<OperatorData> getOperatorDataList() throws DataManagerException{
		List<OperatorData> operatorDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(OperatorData.class).addOrder(Order.asc("name"));		
			operatorDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return operatorDataList;	
	}
	
	@Override
	public List<BrandData> getBrandDataList() throws DataManagerException{
		List<BrandData> brandDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(BrandData.class).addOrder(Order.asc("name"));		
			brandDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return brandDataList;	
	}
	
	@Override
	public List<BrandOperatorRelData> getBrandOperatorDataRelList() throws DataManagerException{
		List<BrandOperatorRelData> brandOperatorDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(BrandOperatorRelData.class);		
			brandOperatorDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return brandOperatorDataList;
	}

	@Override
	public List<NetworkData> getNetworkDataList(String restrictionSqlQuery)
			throws DataManagerException {
		List<NetworkData> networkDataList=null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NetworkData.class);
			if(restrictionSqlQuery!=null){
				criteria.add(Restrictions.sqlRestriction(restrictionSqlQuery));
			}
			networkDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return networkDataList;
	}

	@Override
	public List<NetworkData> getNetworkDataList()
			throws DataManagerException {
		List<NetworkData> networkDataList=null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NetworkData.class);
			networkDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return networkDataList;
	}
	

	
}