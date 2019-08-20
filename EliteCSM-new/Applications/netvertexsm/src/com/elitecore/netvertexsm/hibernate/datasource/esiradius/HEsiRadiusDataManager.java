package com.elitecore.netvertexsm.hibernate.datasource.esiradius;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.EsiRadiusDataManager;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.EsiRadiusData;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.IEsiRadiusData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HEsiRadiusDataManager extends HBaseDataManager implements EsiRadiusDataManager{
	
	/**
	 * This Method is generated to create ESI Radius.
	 * @author Manjil Purohit
	 * @param esiRadiusData
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion 
	 */
	public void create(IEsiRadiusData esiRadiusData) throws DataManagerException,DuplicateParameterFoundExcpetion {				
		try{
			Session session = getSession();
			session.save(esiRadiusData);
			session.flush();			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }		
	}

	/**
	 * This method will return the search result.
	 * All the not null fields will be compared for equality.
	 * @author Manjil Purohit
	 * @param esiRadiusData
	 * @return pageList
	 * @throws DataManagerException
	 */
	public PageList search(IEsiRadiusData esiRadiusData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(EsiRadiusData.class);			
			
			if(esiRadiusData.getName()!= null || esiRadiusData.getName().length()>0){
				criteria.add(Restrictions.ilike("name", esiRadiusData.getName(), MatchMode.ANYWHERE));			
			}		
			
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			
			if(pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			List gatewayList = criteria.list();
	        
	        long totalPages = (long)Math.ceil(totalItems/10);
	        pageList = new PageList(gatewayList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
		return pageList;
	}
	
	public List getEsiRadiusList(){		
		List<IEsiRadiusData> listOfLDAP = new ArrayList<IEsiRadiusData>();
		
		Session session = getSession();
		Criteria criteria = session.createCriteria(IEsiRadiusData.class);
		listOfLDAP = criteria.list();	
		
		return listOfLDAP;
		
	}
}
