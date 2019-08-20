package com.elitecore.elitesm.hibernate.servicepolicy.acct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.AcctServicePoilcyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyBroadcastESIRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyExternalSystemRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyMainDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicyRMParamsData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.data.AcctPolicySMRelData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.logger.Logger;

public class HAcctServicePoilcyDataManager extends HBaseDataManager implements AcctServicePoilcyDataManager{
	private static final String MODULE="HAcctServicePoilcyDataManager";

	public void create(AcctPolicyInstData acctPolicyInstData) throws DataManagerException,DuplicateEntityFoundException{
		EliteAssert.notNull(acctPolicyInstData,"acctPolicyInstData must not be null.");
		try{
			verifyAcctPolicyName(acctPolicyInstData);

			Session session = getSession();
			Criteria criteria = session.createCriteria(AcctPolicyInstData.class).addOrder(Order.desc("orderNumber"));
			criteria.setFetchSize(1);
			List<AcctPolicyInstData> list = criteria.list();
			if(list!=null && !list.isEmpty()){
				AcctPolicyInstData data = list.get(0);
				acctPolicyInstData.setOrderNumber(data.getOrderNumber()+1);
			}
			
			String auditId= UUIDGenerator.generate();
			
			acctPolicyInstData.setAuditUId(auditId);
			
			session.save(acctPolicyInstData);
			session.flush();

			/* save main drivers relation*/
			if(acctPolicyInstData.getMainDriverList()!=null){
				for(Iterator<AcctPolicyMainDriverRelData> iterator = acctPolicyInstData.getMainDriverList().iterator();iterator.hasNext();){
					AcctPolicyMainDriverRelData acctPolicyMainDriverRelData = iterator.next();
					acctPolicyMainDriverRelData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
					session.save(acctPolicyMainDriverRelData);
					Logger.logInfo(MODULE,"saving main driver relation:"+ acctPolicyMainDriverRelData.getDriverInstanceId());
				}
			}
			
			saveExternalSystem(acctPolicyInstData,acctPolicyInstData.getPrepaidServerRelList());
			saveExternalSystem(acctPolicyInstData,acctPolicyInstData.getIpPoolServerRelList());
			saveExternalSystem(acctPolicyInstData,acctPolicyInstData.getProxyServerRelList());
			saveExternalSystem(acctPolicyInstData,acctPolicyInstData.getChargingGatewayServerRelList());
			saveBroadcastExternalSystem(acctPolicyInstData,acctPolicyInstData.getBroadcastingServerRelList());
			
			/* save additional drivers relation*/
			if(acctPolicyInstData.getAdditionalDriverList()!=null){
				for(Iterator<AcctPolicyAdditionalDriverRelData> iterator = acctPolicyInstData.getAdditionalDriverList().iterator();iterator.hasNext();){
					AcctPolicyAdditionalDriverRelData acctPolicyAdditionalDriverRelData = iterator.next();
					acctPolicyAdditionalDriverRelData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
					session.save(acctPolicyAdditionalDriverRelData);
					Logger.logInfo(MODULE,"saving additional driver relation:"+ acctPolicyAdditionalDriverRelData.getDriverInstanceId());
				}
			}
			
			if(acctPolicyInstData.getIpPoolRMParamsData()!=null){
				AcctPolicyRMParamsData acctPolicyRMParamsData = acctPolicyInstData.getIpPoolRMParamsData();
				acctPolicyRMParamsData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				Logger.logDebug(MODULE, "IP Pool RM Params :" + acctPolicyRMParamsData);
				session.save(acctPolicyRMParamsData);
			}
			
			if(acctPolicyInstData.getPrepaidRMParamsData()!=null){
				AcctPolicyRMParamsData acctPolicyRMParamsData = acctPolicyInstData.getPrepaidRMParamsData();
				acctPolicyRMParamsData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				Logger.logDebug(MODULE, "Prepaid RM Params :" + acctPolicyRMParamsData);
				session.save(acctPolicyRMParamsData);
			}
			if(acctPolicyInstData.getChargingGatewayRMParamsData()!=null){
				AcctPolicyRMParamsData acctPolicyRMParamsData = acctPolicyInstData.getChargingGatewayRMParamsData();
				acctPolicyRMParamsData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				Logger.logDebug(MODULE, "Charging Gateway RM Params :" + acctPolicyRMParamsData);
				session.save(acctPolicyRMParamsData);
			}
			
			AcctPolicySMRelData acctPolicySMRelData =  acctPolicyInstData.getAcctPolicySMRelData();
			if(acctPolicySMRelData!=null){
				acctPolicySMRelData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				session.save(acctPolicySMRelData);
				Logger.logInfo(MODULE,"saving sessin manager relation:"+ acctPolicySMRelData.getSessionManagerInstanceId());
			}

			session.flush();
		}catch(DuplicateEntityFoundException exp){
			throw new DuplicateEntityFoundException(exp.getMessage(), exp);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}


	}

	private void saveExternalSystem(AcctPolicyInstData acctPolicyInstData,List<AcctPolicyExternalSystemRelData> externalSystemRelList){
		Session session = getSession();
		if(externalSystemRelList!=null && !externalSystemRelList.isEmpty()){
			for(Iterator<AcctPolicyExternalSystemRelData> iterator = externalSystemRelList.iterator();iterator.hasNext();){
				AcctPolicyExternalSystemRelData acctPolicyExternalSystemRelData = iterator.next();
				acctPolicyExternalSystemRelData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				session.save(acctPolicyExternalSystemRelData);
				Logger.logInfo(MODULE,"saving external system relation:"+ acctPolicyExternalSystemRelData.getEsiInstanceId());
			}
		}
		
	}
	private void saveBroadcastExternalSystem(AcctPolicyInstData acctPolicyInstData,List<AcctPolicyBroadcastESIRelData> externalSystemRelList){
		Session session = getSession();
		if(externalSystemRelList!=null && !externalSystemRelList.isEmpty()){
			for(Iterator<AcctPolicyBroadcastESIRelData> iterator = externalSystemRelList.iterator();iterator.hasNext();){
				AcctPolicyBroadcastESIRelData acctPolicyBroadcastESIRelData = iterator.next();
				acctPolicyBroadcastESIRelData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				session.save(acctPolicyBroadcastESIRelData);
				Logger.logInfo(MODULE,"saving external system relation:"+ acctPolicyBroadcastESIRelData.getEsiInstanceId());
			}
		}
		
	}
	public PageList search(AcctPolicyInstData acctPolicyInstData ,int pageNo, int pageSize) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(AcctPolicyInstData.class);
		PageList pageList = null;

		try{

			if((acctPolicyInstData.getName() != null && acctPolicyInstData.getName().length()>0 )){
            	criteria.add(Restrictions.ilike("name","%"+acctPolicyInstData.getName()+"%"));
            }

            if(!(acctPolicyInstData.getStatus().equalsIgnoreCase("All")) ){
            	
            	criteria.add(Restrictions.ilike("status",acctPolicyInstData.getStatus()));
            }

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}
			criteria.addOrder(Order.asc("orderNumber"));
			List policyList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(policyList, pageNo, totalPages ,totalItems);
			Logger.logDebug(MODULE,"LIST SIZE IS:"+policyList.size());
			return  pageList;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

	}
	private void updateProxyServers(AcctPolicyInstData acctPolicyInstData){
		updateExternalSystem(acctPolicyInstData,ExternalSystemConstants.ACCT_PROXY,acctPolicyInstData.getProxyServerRelList());
	}
	private void updateIPPoolServers(AcctPolicyInstData acctPolicyInstData){
		updateExternalSystem(acctPolicyInstData,ExternalSystemConstants.IPPOOL_COMMUNICATION,acctPolicyInstData.getIpPoolServerRelList());
	}
	private void updatePrepaidServers(AcctPolicyInstData acctPolicyInstData){
		updateExternalSystem(acctPolicyInstData,ExternalSystemConstants.PREPAID_COMMUNICATION,acctPolicyInstData.getPrepaidServerRelList());
	}
	private void updateChargingGatewayServers(AcctPolicyInstData acctPolicyInstData){
		updateExternalSystem(acctPolicyInstData,ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION,acctPolicyInstData.getChargingGatewayServerRelList());
	}
	
	private void updateExternalSystem(AcctPolicyInstData acctPolicyInstData, long externalSystemTypeId, List<AcctPolicyExternalSystemRelData> externalSystemRelList){
		Session session = getSession();
		
		Criteria  criteria = session.createCriteria(AcctPolicyExternalSystemRelData.class).add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
		criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",(long)externalSystemTypeId));
		List<AcctPolicyExternalSystemRelData> oldRelList =criteria.list();
		if(oldRelList!=null && !oldRelList.isEmpty()){
			deleteObjectList(oldRelList, session);
		}
		if(externalSystemRelList!=null && !externalSystemRelList.isEmpty()){
			for (Iterator<AcctPolicyExternalSystemRelData> iterator = externalSystemRelList.iterator(); iterator.hasNext();) {
				AcctPolicyExternalSystemRelData relData =  iterator.next();
				Logger.logInfo(MODULE,"update external system relation:"+ relData.getEsiInstanceId());
				relData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				session.save(relData);
			}
		}
	}
	
	public void delete(List<String> acctPolicyIds) throws DataManagerException {

		try{

			Session session = getSession();

			long acctPolicyId = 0;

			for(int i=0;i<acctPolicyIds.size();i++){
				acctPolicyId = Long.parseLong(acctPolicyIds.get(i));

				/*//  from fallback driver
				Criteria criteria = session.createCriteria(AcctPolicyFallbackDriverRelData.class);				
				List<AcctPolicyFallbackDriverRelData> acctPolicyFallbackDriverRelDataList=criteria.add(Restrictions.eq("acctPolicyId",acctPolicyId)).list();
				deleteObjectList(acctPolicyFallbackDriverRelDataList,session);*/

				// from main driver
				Criteria criteria = session.createCriteria(AcctPolicyMainDriverRelData.class);
				List<AcctPolicyMainDriverRelData> acctPolicyMainDriverRelDataList = criteria.add(Restrictions.eq("acctPolicyId", acctPolicyId)).list();	  
				deleteObjectList(acctPolicyMainDriverRelDataList,session);
								
				// from additional driver
				criteria = session.createCriteria(AcctPolicyAdditionalDriverRelData.class);
				List<AcctPolicyMainDriverRelData> acctPolicyAdditionalDriverRelData = criteria.add(Restrictions.eq("acctPolicyId", acctPolicyId)).list();	  
				deleteObjectList(acctPolicyAdditionalDriverRelData,session);


				// from esi
				criteria = session.createCriteria(AcctPolicyExternalSystemRelData.class);
				List<AcctPolicyExternalSystemRelData> acctPolicyExternalSystemRelDataList =criteria.add(Restrictions.eq("acctPolicyId", acctPolicyId)).list();
				deleteObjectList(acctPolicyExternalSystemRelDataList,session);


				// from sm 
				criteria = session.createCriteria(AcctPolicySMRelData.class);
				List<AcctPolicySMRelData> acctPolicySMRelDataList=criteria.add(Restrictions.eq("acctPolicyId", acctPolicyId)).list();
				deleteObjectList(acctPolicySMRelDataList,session);



				// from sm 
				criteria = session.createCriteria(AcctPolicyRMParamsData.class);
				List<AcctPolicyRMParamsData> acctPolicyRMParamsList=criteria.add(Restrictions.eq("acctPolicyId", acctPolicyId)).list();
				deleteObjectList(acctPolicyRMParamsList,session);
			
				// from broadcastesirel
				criteria = session.createCriteria(AcctPolicyBroadcastESIRelData.class);
				List<AcctPolicyBroadcastESIRelData> acctPolicyBroadcastESIRelList=criteria.add(Restrictions.eq("acctPolicyId", acctPolicyId)).list();
				deleteObjectList(acctPolicyBroadcastESIRelList,session);
				
				// from additionaldriverrel
				criteria = session.createCriteria(AcctPolicyAdditionalDriverRelData.class);
				List<AcctPolicyAdditionalDriverRelData> acctPolicyAdditionalDriverRelList=criteria.add(Restrictions.eq("acctPolicyId", acctPolicyId)).list();
				deleteObjectList(acctPolicyAdditionalDriverRelList,session);

				
				// from policy
				criteria = session.createCriteria(AcctPolicyInstData.class);
				List<AcctPolicyInstData> acctInstDataList=criteria.add(Restrictions.eq("acctPolicyId", acctPolicyId)).list();
				deleteObjectList(acctInstDataList,session);
				
				
				
				

			}


		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

	}

	
	
	public void updateStatus(List<String> acctPolicyIds,String status) throws DataManagerException {

		long acctPolicyId = 0;
		Session session = getSession();
		Criteria criteria = null;


		for(int i=0;i<acctPolicyIds.size();i++){
			acctPolicyId = Long.parseLong(acctPolicyIds.get(i));
			criteria = session.createCriteria(AcctPolicyInstData.class);
			AcctPolicyInstData acctInstData = (AcctPolicyInstData)criteria.add(Restrictions.eq("acctPolicyId",acctPolicyId)).uniqueResult();
			if(status.equals(BaseConstant.SHOW_STATUS_ID)){
				if(acctInstData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
					long orderNumber = acctInstData.getOrderNumber();
					Criteria newCriteria = session.createCriteria(AcctPolicyInstData.class); 
					newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",new Long(orderNumber))); 					
					List sameOrderNoList = newCriteria.list();
					if(sameOrderNoList != null && sameOrderNoList.size() >0){
						// set the order number to the last number
						criteria = session.createCriteria(AcctPolicyInstData.class);
						criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
						List<AcctPolicyInstData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
						if(tempList != null){
							acctInstData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
						}
					}
				}				
			}
			acctInstData.setStatus(status);			
			session.update(acctInstData);
			session.flush();

		}


	}
	private void verifyAcctPolicyName(AcctPolicyInstData acctPolicyInstData) throws DuplicateEntityFoundException{
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(AcctPolicyInstData.class);
		List list = criteria.add(Restrictions.eq("name",acctPolicyInstData.getName())).list();
		if(list==null || list.isEmpty()){
			return;
		}else{
			throw new DuplicateEntityFoundException("Accounting Poilcy Name Is Duplicated.");
		}

	}

	public List<AcctPolicyInstData> searchActiveAcctServicePolicy() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AcctPolicyInstData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID)).list();

		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}



	public AcctPolicyInstData getAcctPolicyData(AcctPolicyInstData acctPolicyInstData) throws DataManagerException {

		AcctPolicyInstData policyInstData = new AcctPolicyInstData();
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AcctPolicyInstData.class);
			policyInstData = (AcctPolicyInstData)criteria.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId())).uniqueResult();

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

		return policyInstData;	
	}



	public void updateAcctServicePolicy(AcctPolicyInstData acctPolicyInstData,IStaffData staffData,String actionAlias)throws DataManagerException {

		AcctPolicyInstData policyInstData = new AcctPolicyInstData();
		
		try{

			Session session = getSession();
			Criteria criteria=session.createCriteria(AcctPolicyInstData.class);
			policyInstData = (AcctPolicyInstData)criteria.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId())).uniqueResult();
			
			setAcctPolicyInstData(policyInstData, session);
			JSONObject oldObject = policyInstData.toJson();
			
			policyInstData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
//			policyInstData.setAcctMethod(acctPolicyInstData.getAcctMethod());
			policyInstData.setDescription(acctPolicyInstData.getDescription());
			policyInstData.setResponseAttributes(acctPolicyInstData.getResponseAttributes());
			policyInstData.setName(acctPolicyInstData.getName());
			policyInstData.setRuleSet(acctPolicyInstData.getRuleSet());
			policyInstData.setValidateAcctPacket(acctPolicyInstData.getValidateAcctPacket());
			policyInstData.setMultipleUserIdentity(acctPolicyInstData.getMultipleUserIdentity());
			policyInstData.setCuiAttribute(acctPolicyInstData.getCuiAttribute());
			policyInstData.setStatus(acctPolicyInstData.getStatus());
			
			if(policyInstData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				policyInstData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(policyInstData);
			session.flush();

			Criteria smRelCriteria = session.createCriteria(AcctPolicySMRelData.class);
			AcctPolicySMRelData relData =(AcctPolicySMRelData)smRelCriteria.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId())).uniqueResult();
			if(relData != null){
				relData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				session.delete(relData);
				session.flush();
			}
			
			if(acctPolicyInstData.getAcctPolicySMRelData()!=null && Strings.isNullOrBlank(acctPolicyInstData.getAcctPolicySMRelData().getSessionManagerInstanceId()) == false){
				AcctPolicySMRelData smRelData = acctPolicyInstData.getAcctPolicySMRelData();
				smRelData.setAcctPolicyId(policyInstData.getAcctPolicyId());
				session.save(smRelData);
				session.flush();
			}
			session.clear();
			setAcctPolicyInstData(policyInstData, session);
			JSONArray jsonArray = ObjectDiffer.diff(oldObject, policyInstData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException he){
			he.printStackTrace();
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

	}

	public List<DriverInstanceData> getMainDriverList(AcctPolicyInstData acctPolicyInstData) throws DataManagerException {

		List<DriverInstanceData> mainDriverList = new ArrayList<DriverInstanceData>();
		try{
			Session session = getSession();

			Criteria mainDriverCriteria = session.createCriteria(AcctPolicyMainDriverRelData.class);
			mainDriverCriteria.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
			List<AcctPolicyMainDriverRelData> mainDriverRelList =mainDriverCriteria.list(); 

			for (Iterator iterator = mainDriverRelList.iterator(); iterator.hasNext();) {
				AcctPolicyMainDriverRelData acctPolicyMainDriverRelData = (AcctPolicyMainDriverRelData) iterator.next();

				Criteria driverCriteria = session.createCriteria(DriverInstanceData.class);
				DriverInstanceData data=(DriverInstanceData)driverCriteria.add(Restrictions.eq("driverInstanceId",acctPolicyMainDriverRelData.getDriverInstanceId())).uniqueResult();
				mainDriverList.add(data);

			}

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

		return mainDriverList;
	}
	
	public List<AcctPolicyMainDriverRelData> getDriverListByPolicyId(AcctPolicyInstData acctPolicyInstData) throws DataManagerException {
		try{
			Session session = getSession();

			Criteria mainDriverCriteria = session.createCriteria(AcctPolicyMainDriverRelData.class);
			mainDriverCriteria.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
			List<AcctPolicyMainDriverRelData> mainDriverRelList = mainDriverCriteria.list();
			return mainDriverRelList;

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}


	}

	public void updateAcctServicePolicyExternalSystemList(AcctPolicyInstData acctPolicyInstData,IStaffData staffData,String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();
			/*
			 * update for ippoolcommunication and prepaidcommunication
			 */
			Criteria acctInstanceDataCriteria = session.createCriteria(AcctPolicyInstData.class);
			acctInstanceDataCriteria.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
			AcctPolicyInstData instData = (AcctPolicyInstData)acctInstanceDataCriteria.uniqueResult();

			setAcctPolicyInstData(instData, session);
			session.flush();
			JSONObject oldObject = instData.toJson();
	        
			instData.setIpPoolRMParamsData(acctPolicyInstData.getIpPoolRMParamsData());
			instData.setPrepaidRMParamsData(acctPolicyInstData.getPrepaidRMParamsData());
			instData.setChargingGatewayRMParamsData(acctPolicyInstData.getChargingGatewayRMParamsData());
			instData.setIpPoolServerRelList(acctPolicyInstData.getIpPoolServerRelList());
			instData.setPrepaidServerRelList(acctPolicyInstData.getPrepaidServerRelList());
			instData.setChargingGatewayServerRelList(acctPolicyInstData.getChargingGatewayServerRelList());
			
			if(instData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				instData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(instData);

			updateIpPoolRMParams(instData);
			updatePrepaidRMParams(instData);
			updateChargingGatewayRMParams(instData);
			updateIPPoolServers(instData);
			updatePrepaidServers(instData);
			updateChargingGatewayServers(instData);
			session.flush();

			session.clear();
			setAcctPolicyInstData(instData, session);
			JSONArray jsonArray = ObjectDiffer.diff(oldObject, instData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
		}catch(HibernateException he){
			he.printStackTrace();
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

	}

	private void setAcctPolicyInstData(AcctPolicyInstData acctPolicyInstData, Session session){
		Long acctPolicyId = acctPolicyInstData.getAcctPolicyId();

		//For prepaidServerRelList
        acctPolicyInstData.setPrepaidRMParamsData(
        		getAcctPolicyRMParamsDataFromDB(
        				acctPolicyId, 
        				ExternalSystemConstants.PREPAID_COMMUNICATION, 
        				session)
        		);
        
        //For ipPoolRMParamsData
        acctPolicyInstData.setIpPoolRMParamsData(
        		getAcctPolicyRMParamsDataFromDB(
        				acctPolicyId, 
        				ExternalSystemConstants.IPPOOL_COMMUNICATION, 
        				session)
        		);
        
        //For chargingGatewayRMParamsData
        acctPolicyInstData.setChargingGatewayRMParamsData(
        		getAcctPolicyRMParamsDataFromDB(
        				acctPolicyId, 
        				ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION, 
        				session)
        		);
        
        //For proxyServerRelList
        acctPolicyInstData.setProxyServerRelList(
        		getAcctPolicyExternalSystemRelDataList(
        				acctPolicyId, 
        				ExternalSystemConstants.ACCT_PROXY, 
        				session)
        		);

        //For ipPoolServerRelList
        acctPolicyInstData.setIpPoolServerRelList(
        		getAcctPolicyExternalSystemRelDataList(
        				acctPolicyId, 
        				ExternalSystemConstants.IPPOOL_COMMUNICATION, 
        				session)
        		);

        //For prepaidServerRelList
        acctPolicyInstData.setPrepaidServerRelList(
        		getAcctPolicyExternalSystemRelDataList(
        				acctPolicyId, 
        				ExternalSystemConstants.PREPAID_COMMUNICATION, 
        				session)
        		);

        //For chargingGatewayServerRelList
        acctPolicyInstData.setChargingGatewayServerRelList(
        		getAcctPolicyExternalSystemRelDataList(acctPolicyId, 
        				ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION, 
        				session)
        		);
        
		//For mainDriverList
        acctPolicyInstData.setMainDriverList(
        		session.createCriteria(AcctPolicyMainDriverRelData.class)
        		.add(Restrictions.eq("acctPolicyId",acctPolicyId))
        		.list()
        		);
        session.flush();
        
        //For additionalDrivers
        List driverInstIdList = session.createCriteria(AcctPolicyAdditionalDriverRelData.class)
        		.add(Restrictions.eq("acctPolicyId", acctPolicyId))
        		.setProjection(Projections.property("driverInstanceId"))
        		.list();
        session.flush();
        
        if(!driverInstIdList.isEmpty()){
        	acctPolicyInstData.setAdditionalDrivers(
	        			new HashSet<DriverInstanceData>(
	        					session.createCriteria(DriverInstanceData.class)
	        						.add(Restrictions.in("driverInstanceId", driverInstIdList))
	        						.list()
	        			)
	        		);
        }
        
        //For broadcastingServerRelList
        acctPolicyInstData.setBroadcastingServerRelList(
        		session.createCriteria(AcctPolicyBroadcastESIRelData.class)
        		.add(Restrictions.eq("acctPolicyId",acctPolicyId))
        		.list()
        		);
        session.flush();
	}
	
	private AcctPolicyRMParamsData getAcctPolicyRMParamsDataFromDB(Long policyId, Long esiTypeId, Session session){
		Criteria  criteria = session.createCriteria(AcctPolicyRMParamsData.class)
				.add(Restrictions.eq("acctPolicyId",policyId))
				.add(Restrictions.eq("esiTypeId",esiTypeId));
		AcctPolicyRMParamsData acctPolicyRMParamsData = (AcctPolicyRMParamsData) criteria.uniqueResult();
		session.flush();
		return acctPolicyRMParamsData;
	}
	
	private List<AcctPolicyExternalSystemRelData> getAcctPolicyExternalSystemRelDataList(Long policyId, Long esiTypeId, Session session){
		Criteria inValuesCriteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class)
				.add(Restrictions.eq("esiTypeId",esiTypeId))
				.setProjection(Projections.property("esiInstanceId"));
		List instanceId = inValuesCriteria.list();
		session.flush();
		
		List<AcctPolicyExternalSystemRelData> list = null;
		if(!instanceId.isEmpty()){
			Criteria  criteria = session.createCriteria(AcctPolicyExternalSystemRelData.class)
					.add(Restrictions.eq("acctPolicyId",policyId))
					.add(Restrictions.in("esiInstanceId", instanceId));
			list = criteria.list();
			session.flush();
		}
		return list;
	}
	
	private void updateIpPoolRMParams(AcctPolicyInstData acctPolicyInstData){
		updateRMParams(acctPolicyInstData,acctPolicyInstData.getIpPoolRMParamsData(),ExternalSystemConstants.IPPOOL_COMMUNICATION);
	}
	private void updatePrepaidRMParams(AcctPolicyInstData acctPolicyInstData){
		updateRMParams(acctPolicyInstData,acctPolicyInstData.getPrepaidRMParamsData(),ExternalSystemConstants.PREPAID_COMMUNICATION);
	}
	private void updateChargingGatewayRMParams(AcctPolicyInstData acctPolicyInstData){
		updateRMParams(acctPolicyInstData,acctPolicyInstData.getChargingGatewayRMParamsData(),ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION);
	}
	
	private void updateRMParams(AcctPolicyInstData acctPolicyInstData, AcctPolicyRMParamsData rmParamsData, long externalSystemTypeId ){
		Session session = getSession();

		Criteria  criteria = session.createCriteria(AcctPolicyRMParamsData.class)
		.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()))
		.add(Restrictions.eq("esiTypeId",externalSystemTypeId));
		List<AcctPolicyRMParamsData> oldRMParamsList =criteria.list();
		if(oldRMParamsList!=null && !oldRMParamsList.isEmpty()){
			for (Iterator<AcctPolicyRMParamsData> iterator = oldRMParamsList.iterator(); iterator.hasNext();) {
				AcctPolicyRMParamsData relData =  iterator.next();
				session.delete(relData);
			}
		}
		if(rmParamsData!=null){
			rmParamsData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
			session.save(rmParamsData);
		}
	}



	public AcctPolicySMRelData getAcctPolicySMRelData(Long acctPolicyId) throws DataManagerException {
		AcctPolicySMRelData acctPolicySMRelData = new AcctPolicySMRelData();
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AcctPolicySMRelData.class);
			criteria.add(Restrictions.eq("acctPolicyId",acctPolicyId));
			acctPolicySMRelData = (AcctPolicySMRelData)criteria.uniqueResult();

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);	
		}
		return acctPolicySMRelData;}



	public void updateAcctServicePolicyDriverGroup(AcctPolicyInstData acctPolicyInstData) throws DataManagerException {
		EliteAssert.notNull(acctPolicyInstData,"acctPolicyInstData must not be null.");
		try{

			Session session = getSession();



			/* remove old values for main drivers relation*/
			Criteria criteria = session.createCriteria(AcctPolicyMainDriverRelData.class);
			List<AcctPolicyMainDriverRelData> oldMainDriverRelDataList = criteria.add(Restrictions.eq("acctPolicyId", acctPolicyInstData.getAcctPolicyId())).list();
			if(oldMainDriverRelDataList!=null && !oldMainDriverRelDataList.isEmpty()){
				for (Iterator iterator = oldMainDriverRelDataList.iterator(); iterator.hasNext();) {
					AcctPolicyMainDriverRelData acctPolicyMainDriverRelData = (AcctPolicyMainDriverRelData) iterator.next();

					if(acctPolicyMainDriverRelData != null){
						session.delete(acctPolicyMainDriverRelData);
						session.flush();

					}

				}
			}

			/* save main drivers relation*/
			List<AcctPolicyMainDriverRelData> newMainDriverRelDataList = acctPolicyInstData.getMainDriverList();
			if(newMainDriverRelDataList!=null && !newMainDriverRelDataList.isEmpty()){
				for(Iterator<AcctPolicyMainDriverRelData> iterator = newMainDriverRelDataList.iterator();iterator.hasNext();){
					AcctPolicyMainDriverRelData acctPolicyMainDriverRelData = iterator.next();
					acctPolicyMainDriverRelData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
					acctPolicyMainDriverRelData.setDriverData(null);
					session.save(acctPolicyMainDriverRelData);
					Logger.logInfo(MODULE,"saving main driver relation:"+ acctPolicyMainDriverRelData.getDriverInstanceId());
				}
			}
			session.flush();
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}



	}



	public void deleteAcctServicePolicyProxyServerList(AcctPolicyInstData acctPolicyInstData) throws DataManagerException {

		try{
			Session session = getSession();
			List<AcctPolicyExternalSystemRelData> proxyServerList = getExternalSystemRelList(acctPolicyInstData,ExternalSystemConstants.ACCT_PROXY);

			for (Iterator iterator = proxyServerList.iterator(); iterator.hasNext();) {
				AcctPolicyExternalSystemRelData acctPolicyExternalSystemRelData = (AcctPolicyExternalSystemRelData) iterator.next();
				session.delete(acctPolicyExternalSystemRelData);
			}


		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}



	}



	public void updateAcctServicePolicyProxyServerList(AcctPolicyInstData policyInstData) throws DataManagerException {

		List<AcctPolicyExternalSystemRelData> oldProxyServerList = new ArrayList<AcctPolicyExternalSystemRelData>();
		long esiTypeId=2;
		try{

			Session session = getSession();
			updateProxyServers(policyInstData);
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

	}



	public void deleteMainDriverList(AcctPolicyInstData policyInstData) throws DataManagerException {
		try{
			Session session = getSession();

			Criteria mainDriverCriteria = session.createCriteria(AcctPolicyMainDriverRelData.class);

			mainDriverCriteria.add(Restrictions.eq("acctPolicyId",policyInstData.getAcctPolicyId()));
			List<AcctPolicyMainDriverRelData> mainDriverRelList =mainDriverCriteria.list(); 
			deleteObjectList(mainDriverRelList, session);
		

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	public void updateAcctAccountingMethod(AcctPolicyInstData acctPolicyInstData,IStaffData staffData,String actionAlias) throws DataManagerException {

		AcctPolicyInstData policyInstData = new AcctPolicyInstData();
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(AcctPolicyInstData.class);
			policyInstData = (AcctPolicyInstData)criteria.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId())).uniqueResult();
		
			setAcctPolicyInstData(policyInstData, session);
			JSONObject oldObject = policyInstData.toJson();

			policyInstData.setAcctMethod(acctPolicyInstData.getAcctMethod());
			policyInstData.setMainDriverList(acctPolicyInstData.getMainDriverList());
			policyInstData.setDriverScript(acctPolicyInstData.getDriverScript());
			policyInstData.setProxyServerRelList(acctPolicyInstData.getProxyServerRelList());
			policyInstData.setProxyTranslationMapConfigId(acctPolicyInstData.getProxyTranslationMapConfigId());
			policyInstData.setProxyScript(acctPolicyInstData.getProxyScript());
			  
            if(policyInstData.getAuditUId() == null){
            	String auditId= UUIDGenerator.generate();
				policyInstData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			JSONArray jsonArray = ObjectDiffer.diff(oldObject, policyInstData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException he){
			he.printStackTrace();
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	public List<AcctPolicyExternalSystemRelData> getExternalSystemRelList(AcctPolicyInstData acctPolicyInstData, long externalSystemTypeId)
	throws DataManagerException {
		List<AcctPolicyExternalSystemRelData> externalSystemRelList=null; 
		Logger.logDebug(MODULE, "externalSystemTypeId :"+externalSystemTypeId);
		try{

			Session session = getSession();
			Criteria  criteria = session.createCriteria(AcctPolicyExternalSystemRelData.class).add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
			criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",(long)externalSystemTypeId));
			externalSystemRelList=criteria.list();         
			session.clear(); 
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return externalSystemRelList;
	}



	public AcctPolicyRMParamsData getRMParamsData(AcctPolicyInstData acctPolicyInstData, long externalSystemTypeId) throws DataManagerException {
		AcctPolicyRMParamsData acctPolicyRMParamsData =null;
		
		try{

			Session session = getSession();
			Criteria  criteria = session.createCriteria(AcctPolicyRMParamsData.class)
				.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()))
				.add(Restrictions.eq("esiTypeId", externalSystemTypeId));
			List<AcctPolicyRMParamsData>  rmParamsList = criteria.list();
			
			if(rmParamsList!=null && !rmParamsList.isEmpty()){
				acctPolicyRMParamsData =rmParamsList.get(0);
			}
			 
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return acctPolicyRMParamsData;	
	}

	public void updateAdditionalProc(AcctPolicyInstData policyInstData,IStaffData staffData,String actionAlias)throws DataManagerException {
		EliteAssert.notNull(policyInstData,"acctPolicyInstData must not be null.");
		try{
			
			Session session = getSession();
			Criteria criteria = session.createCriteria(AcctPolicyInstData.class).add(Restrictions.eq("acctPolicyId", policyInstData.getAcctPolicyId()));
			AcctPolicyInstData acctPolicyInstData =(AcctPolicyInstData) criteria.uniqueResult();
			
			setAcctPolicyInstData(acctPolicyInstData, session);
			JSONObject oldObject = acctPolicyInstData.toJson();
			
			acctPolicyInstData.setBroadcastingServerRelList(policyInstData.getBroadcastingServerRelList());
			acctPolicyInstData.setAdditionalDriverList(policyInstData.getAdditionalDriverList());
			updateBroadcastExternalSystem(acctPolicyInstData,ExternalSystemConstants.ACCT_PROXY,acctPolicyInstData.getBroadcastingServerRelList());
			updateAdditionalDriver(acctPolicyInstData,acctPolicyInstData.getAdditionalDriverList());
			session.flush();
			
			session.clear();
			setAcctPolicyInstData(acctPolicyInstData, session);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject, acctPolicyInstData.toJson());
			System.out.println(jsonArray);
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
		
		
	}
	private void updateBroadcastExternalSystem(AcctPolicyInstData acctPolicyInstData, long externalSystemTypeId, List<AcctPolicyBroadcastESIRelData> externalSystemRelList){
		Session session = getSession();
		
		Criteria  criteria = session.createCriteria(AcctPolicyBroadcastESIRelData.class).add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
		criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",(long)externalSystemTypeId));
		List<AcctPolicyBroadcastESIRelData> oldRelList =criteria.list();
		if(oldRelList!=null && !oldRelList.isEmpty()){
			deleteObjectList(oldRelList, session);
		}
		
		if(externalSystemRelList!=null && !externalSystemRelList.isEmpty()){
			for (Iterator<AcctPolicyBroadcastESIRelData> iterator = externalSystemRelList.iterator(); iterator.hasNext();) {
				AcctPolicyBroadcastESIRelData relData =  iterator.next();
				Logger.logInfo(MODULE,"update broadcast external system relation:"+ relData.getEsiInstanceId());
				relData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				session.save(relData);
			}
		}
	}
	private void updateAdditionalDriver(AcctPolicyInstData acctPolicyInstData, List<AcctPolicyAdditionalDriverRelData> driverRelList){
		Session session = getSession();
		
		Criteria  criteria = session.createCriteria(AcctPolicyAdditionalDriverRelData.class).add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
		List<AcctPolicyBroadcastESIRelData> oldRelList =criteria.list();
		if(oldRelList!=null && !oldRelList.isEmpty()){
			deleteObjectList(oldRelList, session);
		}
		if(driverRelList!=null && !driverRelList.isEmpty()){
			for (Iterator<AcctPolicyAdditionalDriverRelData> iterator = driverRelList.iterator(); iterator.hasNext();) {
				AcctPolicyAdditionalDriverRelData relData =  iterator.next();
				Logger.logInfo(MODULE,"update additional relation:"+ relData.getDriverInstanceId());
				relData.setAcctPolicyId(acctPolicyInstData.getAcctPolicyId());
				session.save(relData);
			}
		}
	}
	public List<AcctPolicyAdditionalDriverRelData> getAdditionalDriverRelList(AcctPolicyInstData acctPolicyInstData) throws DataManagerException {
		List<AcctPolicyAdditionalDriverRelData> additionalDriverList = null;
		try{
			Session session = getSession();

			Criteria driverCriteria = session.createCriteria(AcctPolicyAdditionalDriverRelData.class);
			driverCriteria.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
			additionalDriverList =driverCriteria.list(); 

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return additionalDriverList;
	}

	public List<DriverInstanceData> getAdditionalDriverList(AcctPolicyInstData acctPolicyInstData) throws DataManagerException {

		List<DriverInstanceData> additionalDriverList = new ArrayList<DriverInstanceData>();
		try{
			Session session = getSession();

			Criteria driverCriteria = session.createCriteria(AcctPolicyAdditionalDriverRelData.class);
			driverCriteria.add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
			List<AcctPolicyAdditionalDriverRelData> additionalDriverRelList =driverCriteria.list(); 

			for (Iterator iterator = additionalDriverRelList.iterator(); iterator.hasNext();) {
				AcctPolicyAdditionalDriverRelData acctPolicyAdditionalDriverRelData = (AcctPolicyAdditionalDriverRelData) iterator.next();

				Criteria additionalDriverCriteria = session.createCriteria(DriverInstanceData.class);
				DriverInstanceData data=(DriverInstanceData)additionalDriverCriteria.add(Restrictions.eq("driverInstanceId",acctPolicyAdditionalDriverRelData.getDriverInstanceId())).uniqueResult();
				additionalDriverList.add(data);

			}

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

		return additionalDriverList;
	}



	public List<AcctPolicyBroadcastESIRelData> getBroadcastingESIRelList(AcctPolicyInstData acctPolicyInstData, long externalSystemTypeId) throws DataManagerException {
		List<AcctPolicyBroadcastESIRelData> externalSystemRelList=null; 
		Logger.logDebug(MODULE, "externalSystemTypeId :"+externalSystemTypeId);
		try{

			Session session = getSession();
			Criteria  criteria = session.createCriteria(AcctPolicyBroadcastESIRelData.class).add(Restrictions.eq("acctPolicyId",acctPolicyInstData.getAcctPolicyId()));
			criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",(long)externalSystemTypeId));
			criteria.setFetchMode("translationMappingConfData", FetchMode.JOIN);
			externalSystemRelList=criteria.list();         
			session.clear(); 
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return externalSystemRelList;
	}

	public AcctPolicyInstData getPluginListByAcctPolicyIdForAcct(AcctPolicyInstData acctInstPolicyInstData) throws DataManagerException {
			
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AcctPolicyInstData.class);			
			AcctPolicyInstData policyData = (AcctPolicyInstData)criteria.add(Restrictions.eq("acctPolicyId",acctInstPolicyInstData.getAcctPolicyId())).uniqueResult();
			return policyData;
		
		}catch (HibernateException e) {
			throw new DataManagerException(e.getMessage(),e);
		}		
	
	}

	public void updateAcctPolicyPlugins(AcctPolicyInstData acctPolicyInstData,IStaffData staffData,String actionAlias)throws DataManagerException {

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AcctPolicyInstData.class);
			AcctPolicyInstData policyData = (AcctPolicyInstData)criteria.add(Restrictions.eq("acctPolicyId", acctPolicyInstData.getAcctPolicyId())).uniqueResult();
	
			setAcctPolicyInstData(policyData, session);
			JSONObject oldObject = policyData.toJson();
			
			policyData.setPrePlugins(acctPolicyInstData.getPrePlugins());
			policyData.setPostPlugins(acctPolicyInstData.getPostPlugins());
			session.update(policyData);
			
			session.clear();
			setAcctPolicyInstData(policyData, session);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject, policyData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
		
	}

	public List<AcctPolicyBroadcastESIRelData> getAcctPolicyBroadcastData(Long acctPolicyId) throws DataManagerException {
		EliteAssert.notNull(acctPolicyId,"acctPolicyId must not be null.");
		List<AcctPolicyBroadcastESIRelData> acctPolicyBroadcastData = null;
		try{
			Session session = getSession();
            Criteria criteria = session.createCriteria(AcctPolicyBroadcastESIRelData.class).add(Restrictions.eq("acctPolicyId", acctPolicyId));
            acctPolicyBroadcastData = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return acctPolicyBroadcastData;
	}
	
}
