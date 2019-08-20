package com.elitecore.netvertexsm.hibernate.core.system.systemparameter;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.SystemParameterDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.SystemParameterData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.SystemParameterValuePoolData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HSystemParameterDataManager extends HBaseDataManager implements SystemParameterDataManager{
	
	public void updateBasicDetail(List lstValueData) throws DataManagerException{
		Session session = getSession();
		
		try {
			  for(int i=0 ; i < lstValueData.size(); i++){
				  ISystemParameterData systemParameterData = (ISystemParameterData)lstValueData.get(i);
				  session.update(systemParameterData);
			  }
			  		  
		  }	  
		catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	public List getList() throws DataManagerException{
		List parameterList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SystemParameterData.class).add(Restrictions.eq("systemGenerated","N"));

			parameterList = criteria.list();
			
			
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return parameterList;
	}
			
	public ISystemParameterData getSystemParameter(String profileAlias) throws DataManagerException{
		ISystemParameterData systemParameterData = null;
		try {
			Session  session = getSession();
			Criteria criteria = session.createCriteria(SystemParameterData.class)
									   .add(Restrictions.eq("alias",profileAlias));
			systemParameterData = (ISystemParameterData)criteria.uniqueResult();
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return systemParameterData;
	}
	
	@Override
	public List getSystemParameterValuePoolList(long parameterId)
			throws DataManagerException {
		List systemParameterValuePoolList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SystemParameterValuePoolData.class).add(Restrictions.eq("parameterId",String.valueOf(parameterId)));

			systemParameterValuePoolList = criteria.list();
			
			
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return systemParameterValuePoolList;
	}
	/*public List getList(ISystemParameterData systemParameterData) throws DataManagerException{
	List systemParameterList = null;
	  try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(SystemParameterData.class);
            
            if(systemParameterData.getParameterId() != null)         
            	criteria.add(Restrictions.eq("parameterId",systemParameterData.getParameterId()));

            if(systemParameterData.getName() != null)
            	criteria.add(Restrictions.eq("name",systemParameterData.getName()));
           
            systemParameterList = criteria.list();

         }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }catch(Exception exp){
        	throw new DataManagerException(exp.getMessage(), exp);
        }
	return systemParameterList;
	}*/


	
	/*public void create(ISystemParameterData systemParameterData) throws DataManagerException{
		
		try{
			Session session = getSession();
			session.save(systemParameterData);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}*/
	
	/*public PageList search(ISystemParameterData systemParameterData, int pageNo, int pageSize) throws DataManagerException{
		PageList pageList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(SystemParameterData.class);
            
           
            
			if((systemParameterData.getName() != null && systemParameterData.getName().length()>0 )){
            	criteria.add(Restrictions.like("name",systemParameterData.getName()));
            }
			
                       
            int totalItems = criteria.list().size();

        	criteria.setFirstResult(((pageNo-1) * pageSize));

        	if (pageSize > 0 ){
        		criteria.setMaxResults(pageSize);
        	}
        	
        	List systemParameter = criteria.list();
           
            long totalPages = (long)Math.ceil(totalItems/10);
            pageList = new PageList(systemParameter, pageNo, totalPages ,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}*/
	
	/*public void updateBasicDetail(ISystemParameterData isystemParameterData) throws DataManagerException{
		Session session = getSession();
//		Transaction tx = session.beginTransaction();
		SystemParameterData systemParameterData = null;
		if(isystemParameterData != null){
			try{
				Criteria criteria = session.createCriteria(SystemParameterData.class);
				systemParameterData = (SystemParameterData)criteria.add(Restrictions.eq("parameterId",isystemParameterData.getParameterId()))
				.uniqueResult();
				
				//systemParameterData.setName(isystemParameterData.getName());
				//systemParameterData.setAlias(isystemParameterData.getAlias());
				systemParameterData.setValue(isystemParameterData.getValue());
				//systemParameterData.setSystemGenerated(isystemParameterData.getSystemGenerated());
				
				session.update(systemParameterData);
				session.flush();
				
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(),hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException();
		}
	}*/
	
/*	public void update(ISystemParameterData isystemParameterData) throws DataManagerException{
		Session session = getSession();
//		Transaction tx = session.beginTransaction();
		SystemParameterData systemParameterData = null;
		if(isystemParameterData != null){
			try{
				Criteria criteria = session.createCriteria(SystemParameterData.class);
				systemParameterData = (SystemParameterData)criteria.add(Restrictions.eq("parameterId",isystemParameterData.getParameterId()))
				.uniqueResult();
				
				//systemParameterData.setName(isystemParameterData.getName());
				//systemParameterData.setAlias(isystemParameterData.getAlias());
				systemParameterData.setValue(isystemParameterData.getValue());
				//systemParameterData.setSystemGenerated(isystemParameterData.getSystemGenerated());
				
				session.update(systemParameterData);
				session.flush();
				
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(),hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException();
		}
	}
*/	
	
	/*public void delete(String parameterId)  throws DataManagerException{
		 
		SystemParameterData systemParameterData = null;
		 
		try{
			 Session session = getSession();
//			 Transaction tx = session.beginTransaction();
			 Criteria criteria = session.createCriteria(SystemParameterData.class);

			 systemParameterData = (SystemParameterData)criteria.add(Restrictions.like("parameterId",parameterId))
			 							    .uniqueResult();

			 session.delete(systemParameterData);
//			 tx.commit();				 
//			 session.flush();

		 }catch(HibernateException hExp){
			 hExp.printStackTrace();
			 throw new DataManagerException(hExp.getMessage(), hExp);
		 }catch(Exception exp){
			 throw new DataManagerException(exp.getMessage(),exp);
		 }
	}*/
}
