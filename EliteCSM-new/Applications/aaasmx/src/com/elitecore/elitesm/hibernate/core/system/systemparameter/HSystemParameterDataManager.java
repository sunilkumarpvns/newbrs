package com.elitecore.elitesm.hibernate.core.system.systemparameter;

import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.CaseSensitivity;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.SystemParameterDataManager;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HSystemParameterDataManager extends HBaseDataManager implements SystemParameterDataManager{
	
			
	public void updateBasicDetail(List<ISystemParameterData> dbSystemParameterList, List lstValueData, IStaffData staffData) throws DataManagerException{
		try {
			
			Session session = getSession();

			// converted id to value for json diff
			IdAndNameParameterConversion(dbSystemParameterList,true);
			IdAndNameParameterConversion(lstValueData,true);
			JSONObject newJSONObject = listToJSONObject(lstValueData);
			JSONObject oldJSONObject = listToJSONObject(dbSystemParameterList);
			JSONArray jsonArray = ObjectDiffer.diff(oldJSONObject, newJSONObject);

			IdAndNameParameterConversion(lstValueData,false);
			
			  for(int i=0 ; i < lstValueData.size(); i++){
				  ISystemParameterData systemParameterData = (ISystemParameterData)lstValueData.get(i);
				  session.update(systemParameterData);
			  }
			  
			  String auditId = getAuditId(ConfigConstant.SYSTEM_PARAMETER);
			  staffData.setAuditId(auditId);
			  
			  doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_SYSTEM_PARAMETER_ACTION);
		  } catch(HibernateException hExp){
			  hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		  } catch(Exception exp){
			  throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	private void IdAndNameParameterConversion(List<ISystemParameterData> parameterList,boolean idOrName) {
		for (ISystemParameterData parameter : parameterList){
			if(CaseSensitivity.POLICY_CASESENSITIVITY.equalsIgnoreCase(parameter.getAlias())){
				if(idOrName){
					parameter.setValue(convertIdToName(parameter.getValue()));
				}else {
					parameter.setValue(convertNameToId(parameter.getValue()));
				}
			}
			if(CaseSensitivity.SUBSCRIBER_CASESENSITIVITY.equalsIgnoreCase(parameter.getAlias())){
				if(idOrName){
					parameter.setValue(convertIdToName(parameter.getValue()));
				} else {
					parameter.setValue(convertNameToId(parameter.getValue()));
				}
			}
		}
	}

	private String convertIdToName(String paramId) {
		String paramVal = null;
		if(Strings.isNullOrBlank(CaseSensitivity.getCaseValueFromId(paramId)) == false){
			paramVal = CaseSensitivity.getCaseValueFromId(paramId);
		}
		return paramVal;
	}

	private String convertNameToId(String paramName){
		String paramId = null;
		for (CaseSensitivity paramVal: CaseSensitivity.values()){
			if(paramName.equalsIgnoreCase(paramVal.name)){
				paramId = paramVal.id;
			}
		}
		return paramId;
	}

	public List<ISystemParameterData> getList() throws DataManagerException{
		List<ISystemParameterData> parameterList = null;
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
	
	private JSONObject listToJSONObject(List<ISystemParameterData> systemParameterDataList) {
		
		JSONObject jsonObject = new JSONObject();
		JSONArray authenticationHandlerArray = JSONArray.fromObject(systemParameterDataList);
		
		jsonObject.put("System Parameter List", authenticationHandlerArray);

		return jsonObject;
	}
	
	@Override
	public void updateCaseSesitivity(String policyCaseSensitivity, String subscriberCaseSensitivity) throws DataManagerException {
		ISystemParameterData systemParameterData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SystemParameterData.class)
									.add(Restrictions.eq("alias", "CASE_SENSITIVITY_FOR_POLICY"));
			systemParameterData = (ISystemParameterData) criteria.uniqueResult();
			systemParameterData.setValue(policyCaseSensitivity);
			
			criteria = session.createCriteria(SystemParameterData.class)
										.add(Restrictions.eq("alias", "CASE_SENSITIVITY_FOR_SUBSCRIBER"));
			systemParameterData = (ISystemParameterData) criteria.uniqueResult(); 
			systemParameterData.setValue(subscriberCaseSensitivity);
			
			session.update(systemParameterData);
			session.flush();
		} catch (HibernateException e) {
			throw new DataManagerException(e.getMessage(),e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}
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

	public static void main(String[] args){
		System.out.println(CaseSensitivity.getCaseValueFromId("Case Sensitive"));
	}
}
