package com.elitecore.elitesm.hibernate.core.system.profilemanagement;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.ProfileDataManager;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModelModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModuleSubBISModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModuleTypeData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ConfigurationProfileData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ConfigurationProfileScriptData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleTypeData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IConfigurationProfileData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ProfileBISModelRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.SubBISModuleActionRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.SubBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.SubBISModuleTypeData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;


public class HProfileDataManager extends HBaseDataManager implements ProfileDataManager{

	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of all the records of ConfigurationProfileData
	 * 
	 */
	public List getConfigurationProfileList() throws DataManagerException{
		List lstConfigurationProfileList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ConfigurationProfileData.class)
									   .add(Restrictions.eq("systemGenerated","N"));
			lstConfigurationProfileList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch(Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstConfigurationProfileList;
	}
	
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of all the records of BIS-ModelData
	 */
	public List getBusinessModelList() throws DataManagerException{
		List lstBusinessModelList= null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModelData.class);
			criteria.add(Restrictions.eq("systemGenerated","N"));
			lstBusinessModelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return lstBusinessModelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of all the records of BIS-ModuleData
	 */
	public List getBusinessModuleList() throws DataManagerException{
		List lstBusinessModuleList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModuleData.class);
			lstBusinessModuleList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return lstBusinessModuleList;
	}
	
	/** 
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of all the records of SUB-BIS-ModuleData
	 */
	public List getSubBusinessModuleList() throws DataManagerException{
		List lstSubBusinessModuleList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SubBISModuleData.class);
			lstSubBusinessModuleList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch(Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return lstSubBusinessModuleList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of BIS-ModuleId list
	 */         
	public List getBISModelModuleRelList(String BISModelId) throws DataManagerException{
		List lstBISModelModuleRelList = null;
		try {
			Session session = getSession(); 
			Criteria criteria = session.createCriteria(BISModelModuleRelData.class);
			criteria.add(Restrictions.eq("businessModelId",BISModelId));
			criteria.createAlias("bisModule","bisModuleData");
			criteria.add(Restrictions.eq("bisModuleData.systemGenerated","N"));
			lstBISModelModuleRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstBISModelModuleRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of BISModuleSubBisModuleRel list  
	 */
	public List getBISModuleSubBISModuleRelList(String BISModuleId) throws DataManagerException{
		List lstBISModuleSubBISModuleRelList = null;
		try {
			Session  session = getSession();
			Criteria criteria = session.createCriteria(BISModuleSubBISModuleRelData.class);
			criteria.add(Restrictions.eq("businessModuleId",BISModuleId));
			criteria.createAlias("subBisModule","subBisModuleData");
			criteria.add(Restrictions.eq("subBisModuleData.systemGenerated","N"));
			lstBISModuleSubBISModuleRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return lstBISModuleSubBISModuleRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of BISModuleType 
	 */
	public List getBusinessModuleTypeList() throws DataManagerException{
		List lstBusinessModuleTypeList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModuleTypeData.class);
			lstBusinessModuleTypeList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return lstBusinessModuleTypeList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of SubBISModuleType 
	 */
	public List getSubBusinessModuleTypeList() throws DataManagerException{
		List lstSubBusinessModuleTypeList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SubBISModuleTypeData.class);
			lstSubBusinessModuleTypeList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
		return lstSubBusinessModuleTypeList;
	}
	
	/**
	 *  @author  dhavalraval
	 *  @return  List
	 *  @throws  DataManagerException
	 *  @purpose This method returns the list of SubBISModuleActionRel list
	 */
	public List getSubBISModuleActionRelList(String subBusinessModuleId) throws DataManagerException {
		List lstSubBISModuleActionRelList = null;

		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SubBISModuleActionRelData.class);
			criteria.add(Restrictions.eq("subBusinessModuleId",subBusinessModuleId));
			criteria.createAlias("actionData","moduleActionData");
			criteria.add(Restrictions.eq("moduleActionData.systemGenerated","N"));
			lstSubBISModuleActionRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
		return lstSubBISModuleActionRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of SubBISModuleActionRelData.
	 */
	public List getActionSubBISModuleRelList(String actionId) throws DataManagerException{
		List lstActionSubBISModuleRelList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SubBISModuleActionRelData.class);
			criteria.add(Restrictions.eq("actionId",actionId));
			lstActionSubBISModuleRelList = criteria.list();
		} catch (HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstActionSubBISModuleRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of BISModuleSubBISModuleRelData.
	 */
	public List getSubBISModuleBISModuleRelList(String subBusinessModuleId) throws DataManagerException{
		List lstSubBISModuleBISModuleRelList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModuleSubBISModuleRelData.class);
			criteria.add(Restrictions.eq("subBusinessModuleId",subBusinessModuleId));
			lstSubBISModuleBISModuleRelList = criteria.list();
		} catch (HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
			return lstSubBISModuleBISModuleRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of BISModelModuleRelData.
	 */
	public List getBISModuleModelRelList(String businessModuleId) throws DataManagerException{
		List lstBISModuleModelRelList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModelModuleRelData.class);
			criteria.add(Restrictions.eq("businessModuleId",businessModuleId));
			lstBISModuleModelRelList = criteria.list();
		} catch (HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		  return lstBISModuleModelRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @param   BISModelData object
	 * @purpose This method returns the list of BISModelData
	 */
	public List getBISModelList(IBISModelData bisModelData) throws DataManagerException{
		List lstBISModelList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModelData.class)
									   .add(Restrictions.eq("businessModelId",bisModelData.getBusinessModelId()));
			lstBISModelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
		return lstBISModelList;
	}
	
	/**
	 * @author  dhavalraval 
	 * @return  List
	 * @throws  DataManagerException
	 * @param   BISModuleData object
	 * @purpose This method returns thelist of BISModuleData
	 */
	public List getBISModuleList(IBISModuleData bisModuleData) throws DataManagerException{
		List lstBISModuleList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModuleData.class)
									   .add(Restrictions.eq("businessModuleId",bisModuleData.getBusinessModuleId()));
			lstBISModuleList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
		return lstBISModuleList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @param   SubBISModuleData object
	 * @purpose This method returns the list of SubBISModuleData
	 */
	public List getSubBISModuleList(ISubBISModuleData subBISModuleData) throws DataManagerException{
		List lstSubBISModuleList = null;

		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SubBISModuleData.class)
									   .add(Restrictions.eq("subBusinessModuleId",subBISModuleData.getSubBusinessModuleId()));
			lstSubBISModuleList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
		return lstSubBISModuleList;
	}
	
	public List getSubBISModuleAlias(ISubBISModuleData subBISModuledata) throws DataManagerException{
		List lstSubBISModuleList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SubBISModuleData.class)
									   .add(Restrictions.eq("alias",subBISModuledata.getAlias()));
			lstSubBISModuleList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstSubBISModuleList;
	}
	
	 
	public List getBISModuleAlias(IBISModuleData bisModuleData) throws DataManagerException{
		List lstBISModuleList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModuleData.class)
									   .add(Restrictions.eq("alias",bisModuleData.getAlias()));
			lstBISModuleList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
			return lstBISModuleList;
	}
	
	public List getBISModelAlias(IBISModelData bisModelData) throws DataManagerException{
		List lstBISModelList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModelData.class)
									   .add(Restrictions.eq("alias",bisModelData.getAlias()));
			lstBISModelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstBISModelList;
	}
	
	public List getActionAlias(IActionData actionData) throws DataManagerException{
		List lstActionList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ActionData.class)
									   .add(Restrictions.eq("alias",actionData.getAlias()));
			lstActionList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstActionList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @param   ActionData object
	 * @purpose This method returns the list of ActionData
	 */
	public ActionData getActionList(IActionData actionData) throws DataManagerException{
		ActionData data = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ActionData.class)
									   .add(Restrictions.eq("actionId",actionData.getActionId()));
			data = (ActionData)criteria.uniqueResult();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
		return data;
	}
	
	public List getActionList(String[] actionIds) throws DataManagerException{
		List lstActionList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ActionData.class)
									   .add(Restrictions.in("actionId",actionIds));
			lstActionList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
		return lstActionList;
	}
	
	
	public List getActionList() throws DataManagerException{
		List lstActionList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ActionData.class);
			lstActionList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstActionList;
	}
	/**
	 * @author  dhavalraval 
	 * @throws  DataManagerException
	 * @param   BisModuleTypeData object
	 * @return  List
	 * @purpose This method returns the list of BISModuleTypeData
	 */
	public List getBISModuleTypeList(IBISModuleTypeData bisModuleTypeData) throws DataManagerException{
		List lstBisModuleTypeList = null;
		
		try {
			Session session  = getSession();
			Criteria criteria = session.createCriteria(BISModuleTypeData.class)
									   .add(Restrictions.eq("bisModuleTypeId",bisModuleTypeData.getBisModuleTypeId()));
			lstBisModuleTypeList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
		return lstBisModuleTypeList;
	}
	
	/**
	 * @author  dhavalraval
	 * @param   businessModelId,businessModuleTypeId,businessModuleName
	 * @throws  DataManagerException
	 * @return  List
	 * @purpose This method returns the list from bisModuleData with the specified arguments.
	 */
	public List search(String businessModelId,String businessModuleTypeId,String businessModuleName) throws DataManagerException{
		List lstBusinessModuleList = new ArrayList();

		try {
			Session session = getSession();
			List lstBISModelModuleRelList = null;
			
			Criteria criteria = session.createCriteria(BISModelModuleRelData.class);
			criteria.createAlias("bisModule","bisModuleData");
			criteria.add(Restrictions.eq("bisModuleData.systemGenerated","N"));
			
			if(businessModelId != null){
				criteria.add(Restrictions.eq("businessModelId",businessModelId));
			}
			lstBISModelModuleRelList = criteria.list();
				
			for(int i=0;i<lstBISModelModuleRelList.size();i++){
				BISModelModuleRelData bisModelModuleRelData = (BISModelModuleRelData)lstBISModelModuleRelList.get(i);
				criteria = session.createCriteria(BISModuleData.class);
					
				criteria.add(Restrictions.eq("businessModuleId",bisModelModuleRelData.getBusinessModuleId()));
				if(businessModuleTypeId != null) {
					criteria.createAlias("bisModuleType","bisModuleTypeData");
					criteria.add(Restrictions.eq("bisModuleTypeData.bisModuleTypeId",businessModuleTypeId));
				}
				if(businessModuleName != null && !businessModuleName.trim().equalsIgnoreCase("")){
					criteria.add(Restrictions.like("name",businessModuleName));
				}
				lstBusinessModuleList.addAll(criteria.list());
			}
			
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
		return lstBusinessModuleList;
	}
	
	/**
	 * @author  dhavalraval
	 * @param   businessModelId,businessmoduleTypeId,subBusinessModuleTypeId,subBusinessModuleName
	 * @throws  DataManagerException
	 * @return  List
	 * @purpose This method returns the list from subBusinessModuleData class with specified parameters.
	 */
	public List search(String businessModelId,String businessModuleId,String subBusinessModuleTypeId,String subBusinessModuleName) throws DataManagerException{
		List lstSubBusinessModuleList = new ArrayList();

		try{
			Session session = getSession();
			List lstBISModelModuleRelList = null;
			List lstBISModuleSubBISModuleRelList = null;
			
			if(businessModelId != null || businessModuleId != null || subBusinessModuleTypeId != null || subBusinessModuleName != null){
				Criteria criteria = session.createCriteria(BISModelModuleRelData.class);
				
				if(businessModelId != null){
					criteria.add(Restrictions.eq("businessModelId",businessModelId));
				}
				
				if(businessModuleId != null){
					criteria.add(Restrictions.eq("businessModuleId",businessModuleId));
				}
				
				lstBISModelModuleRelList = criteria.list();
				
				for(int j=0;j<lstBISModelModuleRelList.size();j++){
					BISModelModuleRelData bisModelModuleRelList = (BISModelModuleRelData)lstBISModelModuleRelList.get(j);
					criteria = session.createCriteria(BISModuleSubBISModuleRelData.class);
						
					criteria.add(Restrictions.eq("businessModuleId",bisModelModuleRelList.getBusinessModuleId()));
						
					lstBISModuleSubBISModuleRelList = criteria.list();
											
					for(int k=0;k<lstBISModuleSubBISModuleRelList.size();k++){
						BISModuleSubBISModuleRelData bisModuleSubBISModuleRelData = (BISModuleSubBISModuleRelData)lstBISModuleSubBISModuleRelList.get(k);
						criteria = session.createCriteria(SubBISModuleData.class);
						criteria.add(Restrictions.eq("subBusinessModuleId",bisModuleSubBISModuleRelData.getSubBusinessModuleId()));

						if(subBusinessModuleTypeId != null && !subBusinessModuleTypeId.equalsIgnoreCase(""))
							criteria.add(Restrictions.eq("subBisModuleTypeId",subBusinessModuleTypeId));
							
						if(subBusinessModuleName != null && !subBusinessModuleName.trim().equalsIgnoreCase("")){
							criteria.add(Restrictions.like("name",subBusinessModuleName));
						}
							lstSubBusinessModuleList.addAll(criteria.list());
						}
					}
			}else{
				Criteria criteria = session.createCriteria(SubBISModuleData.class);
				lstSubBusinessModuleList =criteria.list();
			}
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
		return lstSubBusinessModuleList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of BISModelModuleRelData 
	 */
	public List getBusinessModelModuleRelList(String businessModuleId) throws DataManagerException{
		List lstBusinessModelModuleRelList = null;
	
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModelModuleRelData.class)
									   .add(Restrictions.eq("businessModuleId",businessModuleId));
			lstBusinessModelModuleRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
		return lstBusinessModelModuleRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of BISModuleSubBISModuleRelData Lists
	 */
	public List getBISModuleSubBISModuleRel(String subBusinessModuleId) throws DataManagerException{
		List lstBISModuleSibBISModuleRelList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModuleSubBISModuleRelData.class)
									   .add(Restrictions.eq("subBusinessModuleId",subBusinessModuleId));
			lstBISModuleSibBISModuleRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage());
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage());
		}
		
		return lstBISModuleSibBISModuleRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @return  List 
	 * @throws  DataManagerException
	 * @purpose This method retunrs the list of ConfigurationProfileData
	 */
	public List getConfigurationProfileData(String profileName) throws DataManagerException{
		List lstConfigurationProfileList = null;

		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ConfigurationProfileData.class)
									   .add(Restrictions.eq("alias",profileName));
			lstConfigurationProfileList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return lstConfigurationProfileList;
	} 
	
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of ProfileBISModelRelData
	 */
	public List getProfileBISModelRelList(String configurationProfileId) throws DataManagerException{
		List lstPorilfeBISModelRelList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ProfileBISModelRelData.class)
									   .add(Restrictions.eq("configurationProfileId",configurationProfileId));
			lstPorilfeBISModelRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstPorilfeBISModelRelList;
		
	}
	
	/**
	 * @author  dhavalraval
	 * @param   subBusinessModuleId
	 * @param   subBusinessModuleStatus
	 * @purpose This method is generated to update freezeProfile of SubBISModuleData
	 */
	public void updateSubBusinessModuleProfileStatus(String subBusinessModuleId,String subBusinessModuleStatus) throws DataManagerException{
		Session session = getSession();
		SubBISModuleData subBISModuleData = null;

		try {
			Criteria criteria = session.createCriteria(SubBISModuleData.class);
			subBISModuleData = (SubBISModuleData)criteria.add(Restrictions.eq("subBusinessModuleId",subBusinessModuleId)).uniqueResult();
			subBISModuleData.setFreezeProfile(subBusinessModuleStatus);
			session.update(subBISModuleData);					
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   businessModuleId
	 * @param   businessModuleProfileStatus
	 * @purpose This method is generated to update freezeProfile of BISModuleData
	 */
	public void updateBusinessModuleProfileStatus(String businessModuleId,String businessModuleProfileStatus) throws DataManagerException{
		Session session = getSession();
		BISModuleData bisModuleData = null;
		
		try {
			Criteria criteria = session.createCriteria(BISModuleData.class);
			bisModuleData = (BISModuleData)criteria.add(Restrictions.eq("businessModuleId",businessModuleId))
												   .uniqueResult();
			bisModuleData.setFreezeProfile(businessModuleProfileStatus);
			session.update(bisModuleData);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
	}
	
	/**
	 * @author  dhavalraval
	 * @param   actionId
	 * @param   actionProfileStatus
	 * @purpose This method is generated to update freezeProfile of ActionData
	 * 
	 */
	public void updateActionProfileStatus(String actionId,String actionProfileStatus) throws DataManagerException{
		Session session = getSession();
		ActionData actionData = null;

		try {
			Criteria criteria = session.createCriteria(ActionData.class);
			actionData = (ActionData)criteria.add(Restrictions.eq("actionId",actionId))
											 .uniqueResult();
			actionData.setFreezeProfile(actionProfileStatus);
			session.update(actionData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   businessModelId
	 * @param   businessModelProfileStatus
	 * @purpose This method is generated to update freezeProfile of BISModelData
	 */
	public void updateModelProfileStatus(String businessModelId,String businessModelProfileStatus) throws DataManagerException{
		Session session = getSession();
		BISModelData bisModelData = null;
		
		try {
			Criteria criteria = session.createCriteria(BISModelData.class);
			bisModelData = (BISModelData)criteria.add(Restrictions.eq("businessModelId",businessModelId))
		    									   .uniqueResult();
			bisModelData.setFreezeProfile(businessModelProfileStatus);
		    session.update(bisModelData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   configurationProfileId
	 * @param   byte[]
	 * @purpose This method is generated to update xmlData of ConfigurationProfileData.
	 */
	public void updateFreezeConfigurationProfile(String configurationProfileId,byte[] profileData) throws DataManagerException{
		Session session = getSession();
		ConfigurationProfileData configurationProfileData = null;
		
		try {
			Criteria criteria = session.createCriteria(ConfigurationProfileData.class)
									   .add(Restrictions.eq("configurationProfileId",configurationProfileId));
			configurationProfileData = (ConfigurationProfileData)criteria.uniqueResult();
			configurationProfileData.setXmlData(profileData);
			session.update(configurationProfileData);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @throws  DataManagerException
	 * @purpose This method is generated to update status of BISModelData when freeze profile.
	 */
	public void updateFreezeModelProfile() throws DataManagerException{
		Session session = getSession();
		List lstbisModelList = null;
		
		try {
			Criteria criteria = session.createCriteria(BISModelData.class);
			lstbisModelList = criteria.list();
			for(int i=0;i<lstbisModelList.size();i++){
				BISModelData bisModelData = (BISModelData)lstbisModelList.get(i);
				bisModelData.setStatus(bisModelData.getFreezeProfile());
				session.update(bisModelData);
			}
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @throws  DataManagerException
	 * @purpose This method is generated to update status of BISModuleData when freeze Profile 
	 */
	public void updateFreezeModuleProfile() throws DataManagerException{
		Session session = getSession();
		BISModuleData bisModuledata = null;
		List lstbisModuleList = null;
		
		try {
			Criteria criteria = session.createCriteria(BISModuleData.class);
			lstbisModuleList = criteria.list();
			for(int i=0;i<lstbisModuleList.size();i++){
				bisModuledata = (BISModuleData)lstbisModuleList.get(i);
				bisModuledata.setStatus(bisModuledata.getFreezeProfile());
				session.update(bisModuledata);
			}
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @throws  DataManagerException
	 * @purpose This method is generated to update status of SubBISModuleData when freeze Profile
	 */
	public void updateFreezeSubModuleProfile() throws DataManagerException{
		Session session = getSession();
		SubBISModuleData subBISModuleData = null;
		List lstSubBISModulelist = null;
		
		try {
			Criteria criteria = session.createCriteria(SubBISModuleData.class);
			lstSubBISModulelist = criteria.list();
			for(int i=0;i<lstSubBISModulelist.size();i++){
				subBISModuleData = (SubBISModuleData)lstSubBISModulelist.get(i);
				subBISModuleData.setStatus(subBISModuleData.getFreezeProfile());
				session.update(subBISModuleData);
			}
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author dhavalraval
	 * @throws DataManagerException
	 * @pupose This method is generated to update status of ActionData when freeze Profile
	 */
	public void updateFreezeActionProfile() throws DataManagerException{
		Session session = getSession();
		ActionData actionData = null;
		List lstActionList = null;
		
		try {
			Criteria criteria = session.createCriteria(ActionData.class);
			lstActionList = criteria.list();
			for(int i=0;i<lstActionList.size();i++){
				actionData = (ActionData)lstActionList.get(i);
				actionData.setStatus(actionData.getFreezeProfile());
				session.update(actionData);
			}
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	
	}
	
	/**
	 * @author  dhavalraval
	 * @param   profileAlias
	 * @purpose This method is generated to return object of IConfigurationProfileData
	 */
	public IConfigurationProfileData getConfigurationProfile(String profileAlias) throws DataManagerException{
		 IConfigurationProfileData configurationProfileData = null;
		 try {
			Session  sesion = getSession();
			Criteria criteria = sesion.createCriteria(ConfigurationProfileData.class)
									  .add(Restrictions.eq("alias",profileAlias));
			configurationProfileData = (IConfigurationProfileData)criteria.uniqueResult();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return configurationProfileData;
	}
	
	/**
	 * @author  dhavalraval
	 * @param   modelAlias
	 * @param   modelStatus
	 * @throws  DataManagerException
	 * @purpose This method is generated to update status,freezeProfileStatus of BISModelData
	 */
	public void updateModelStatus(String modelAlias,String modelStatus) throws DataManagerException{
		Session session = getSession();
		BISModelData bisModelData = null;
		
		try {
			Criteria criteria = session.createCriteria(BISModelData.class);
			bisModelData = (BISModelData)criteria.add(Restrictions.eq("alias",modelAlias))
										         .uniqueResult();
			bisModelData.setStatus(modelStatus);
			bisModelData.setFreezeProfile(modelStatus);
			session.update(bisModelData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   moduleAlias
	 * @param   moduleStatus
	 * @throws  DataManagerException
	 * @purpose This method is generated to update status,freezeProfileStatus of BISModuleData
	 */
	public void updateModuleStatus(String moduleAlias,String moduleStatus) throws DataManagerException{
		Session session = getSession();
		BISModuleData bisModuleData = null;
		
		try {
			Criteria criteria = session.createCriteria(BISModuleData.class);
			bisModuleData = (BISModuleData)criteria.add(Restrictions.eq("alias",moduleAlias))
												   .uniqueResult();
			bisModuleData.setStatus(moduleStatus);
			bisModuleData.setFreezeProfile(moduleStatus);
			session.update(bisModuleData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   subModuleAlias
	 * @param   subModuleStauts
	 * @throws  DataManagerException
	 * @purpose This method is generated to update status,freezeProfileStatus of SubBISModuleData.
	 */
	public void updateSubModuleStatus(String subModuleAlias,String subModuleStatus) throws DataManagerException{
		Session session = getSession();
		SubBISModuleData subBISModuleData = null;
		
		try {
			Criteria criteria = session.createCriteria(SubBISModuleData.class);
			subBISModuleData = (SubBISModuleData)criteria.add(Restrictions.eq("alias",subModuleAlias))
														 .uniqueResult();
			subBISModuleData.setStatus(subModuleStatus);
			subBISModuleData.setFreezeProfile(subModuleStatus);
			session.update(subBISModuleData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   actionAlias
	 * @param   actionStatus
	 * @throws  DataManagerException
	 * @purpose This method is generated to update status,freezeProfileStatus of ActionData
	 */
	public void updateActionStatus(String actionAlias,String actionStatus) throws DataManagerException{
		Session session = getSession();
		ActionData actionData = null;
		
		try {
			Criteria criteria = session.createCriteria(ActionData.class);
			actionData = (ActionData)criteria.add(Restrictions.eq("alias",actionAlias))
											 .uniqueResult();
			actionData.setStatus(actionStatus);
			actionData.setFreezeProfile(actionStatus);
			session.update(actionData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   IConfigurationProfileData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to update the IConfigurationProfileData contents.
	 */
	public void updateConfigurationProfile(IConfigurationProfileData configurationProfileData) throws DataManagerException{
		Session session = getSession();

		try {
			session.update(configurationProfileData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   currentProfile
	 * @param   lastProfileAlias
	 * @throws  DataManagerException
	 * @purpose This method is generated to set value of lastprofile with currentProfile in SystemParameterData
	 */
	public void updateLastProfile(String currentProfile,String lastProfileAlias) throws DataManagerException{
		Session session = getSession();
		SystemParameterData systemParameterData = null;
		
		try {
			Criteria criteria = session.createCriteria(SystemParameterData.class);
			systemParameterData	= (SystemParameterData)criteria.add(Restrictions.eq("alias",lastProfileAlias))
															   .uniqueResult();
			systemParameterData.setValue(currentProfile);
			session.update(systemParameterData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author  dhavalraval
	 * @param   configurationProfileId
	 * @throws  DataManagerException
	 * @return  List
	 * @purpose This method is generated to returns the list of specfied configurationProfileId.
	 * 
	 */
	public List getConfigurationProfileName(String configurationProfileId) throws DataManagerException{
		List lstConfigurationProfileList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ConfigurationProfileData.class)
									   .add(Restrictions.eq("configurationProfileId",configurationProfileId));
			lstConfigurationProfileList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstConfigurationProfileList;
	}
	
	
	public List getBISModelModuleRelList() throws DataManagerException{
		List lstBISModelModuleRelList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModelModuleRelData.class);
			lstBISModelModuleRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstBISModelModuleRelList;
	}
	
	/**
	 * @author  dhavalraval
	 * @param   paramAlias
	 * @param   paramValue
	 * @purpose This method is generated to update the Parameter with alias name and with value.
	 */
	public void updateParameter(String paramAlias,String paramValue) throws DataManagerException{
		Session session = getSession();
		SystemParameterData systemParameterData = null;
		
		try {
			Criteria criteria = session.createCriteria(SystemParameterData.class);
			systemParameterData = (SystemParameterData)criteria.add(Restrictions.eq("alias",paramValue))
															   .uniqueResult();
			systemParameterData.setValue(paramAlias);
			session.update(systemParameterData);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	/**
	 * @author dhavalraval
	 * @param  configurationProfileId
	 * @return List
	 * @throws DataManagerException
	 */
	public List getProfileSpecificList(String configurationProfileId, String mode) throws DataManagerException{
		List lstProfileSpecificList = null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ConfigurationProfileScriptData.class)
									   .add(Restrictions.eq("configurationProfileId",configurationProfileId))
			   						   .add(Restrictions.eq("executionMode",mode));
			lstProfileSpecificList = criteria.list();

			/*for(int i=0;i<lstProfileSpecificList.size();i++){
				ConfigurationProfileScriptData scriptData = (ConfigurationProfileScriptData)lstProfileSpecificList.get(i);
				System.out.println("Query "+i+" : "+scriptData.getNetScript());
			}
			System.out.println("Profile Script Size : "+lstProfileSpecificList.size());*/
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstProfileSpecificList;
	}
	
	/**
	 * @author dhavalraval
	 * @param  configurationProfileId
	 * @return List
	 * @throws DataManagerException
	 */
	public void executeProfileScripts(String configurationProfileId, String mode) throws DataManagerException{
		Session session = getSession();
//		System.out.println("@@@@@@@ value of the configuration Profileid is @@@@@"+configurationProfileId);
		try {
			List profileScriptsList = getProfileSpecificList(configurationProfileId, mode);
//			System.out.println("Size : "+profileScriptsList.size());
			for(int i=0;i<profileScriptsList.size();i++){
				ConfigurationProfileScriptData scriptData = (ConfigurationProfileScriptData)profileScriptsList.get(i);
				
				Query query = session.createQuery( scriptData.getNetScript() );
//				System.out.println("Here the query is :"+query);
				int updatedEntities = query.executeUpdate();
				
			}
/*			Criteria criteria = session.createCriteria(ConfigurationProfileScriptData.class)
									   .add(Restrictions.eq("configurationProfileId",configurationProfileId));
			lstProfileSpecificList = criteria.list();
*/		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}


	@Override
	public BISModelData getBISModel(String buisenessModelId) throws DataManagerException {
		BISModelData modelData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(BISModelData.class)
									   .add(Restrictions.eq("businessModelId",buisenessModelId));
			modelData = (BISModelData) criteria.uniqueResult();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return modelData;
	}

}
 
