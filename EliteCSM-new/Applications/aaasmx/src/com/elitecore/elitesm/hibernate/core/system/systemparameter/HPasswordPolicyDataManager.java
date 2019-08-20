package com.elitecore.elitesm.hibernate.core.system.systemparameter;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.PasswordPolicyDataManager;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;

import net.sf.json.JSONArray;

public class HPasswordPolicyDataManager extends HBaseDataManager implements PasswordPolicyDataManager{
	
	public void updatePolicyDetail(PasswordPolicyConfigData oldPasswordSelectionPolicy,PasswordPolicyConfigData passwordPolicyConfigData, IStaffData staffData) throws DataManagerException{
		Session session = getSession();
		try {
			JSONArray jsonArray = ObjectDiffer.diff(oldPasswordSelectionPolicy, passwordPolicyConfigData);
			
			if(passwordPolicyConfigData !=null){
				session.update(passwordPolicyConfigData);
			}else{
				throw new DataManagerException();
			}
			if(jsonArray.isEmpty() ==  false) {
				
				String auditId = getAuditId(ConfigConstant.SYSTEM_PARAMETER);
	    		staffData.setAuditId(auditId);
	    		
				doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_SYSTEM_PARAMETER_ACTION);
			}
		
		 }catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	public PasswordPolicyConfigData getPasswordSelectionPolicy() throws DataManagerException{
		List parameterList = null;
		PasswordPolicyConfigData passwordPolicyConfigData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(PasswordPolicyConfigData.class);
			parameterList = criteria.list();
			passwordPolicyConfigData=(PasswordPolicyConfigData) parameterList.get(0);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return passwordPolicyConfigData;
	}
			
	
}
