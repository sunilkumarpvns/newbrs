package com.elitecore.elitesm.hibernate.servicepolicy.diameter.tgppaaapolicy;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.TGPPAAAPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.TGPPServerPolicyData;

public class HTGPPAAAPolicyDataManager extends HBaseDataManager implements TGPPAAAPolicyDataManager {
	
	private static final String TGPP_AAA_POLICY_ID = "tgppAAAPolicyId";
	private static final String TGPP_POLICY_NAME = "name";
	private static final String MODULE = "HTGPPAAAPolicyDataManager";

	@Override
	public PageList searchTGPPAAAPolicy(TGPPAAAPolicyData tgppAAAPolicyData, int requiredPageNo, Integer pageSize) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(TGPPAAAPolicyData.class);
		PageList pageList = null;

		try{

			if((tgppAAAPolicyData.getName() != null && tgppAAAPolicyData.getName().length()>0 )){
				criteria.add(Restrictions.ilike("name","%"+tgppAAAPolicyData.getName()+"%"));
			}

			if(!(tgppAAAPolicyData.getStatus().equalsIgnoreCase("All")) ){

				criteria.add(Restrictions.ilike("status",tgppAAAPolicyData.getStatus()));
			}


			int totalItems = criteria.list().size();
			criteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}
			criteria.addOrder(Order.asc("orderNumber"));
			List policyList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(policyList, requiredPageNo, totalPages ,totalItems);
			Logger.logDebug(MODULE,"LIST SIZE IS:"+policyList.size());
		
			session.flush();
			session.clear();
			
			return  pageList;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	@Override
	public String deleteTGPPAAAPolicyById(String id) throws DataManagerException {
		return deleteTgppAAAPolicy(TGPP_AAA_POLICY_ID, id);
	}
	
	@Override
	public String deleteTGPPAAAPolicyByName(String name) throws DataManagerException {
		return deleteTgppAAAPolicy(TGPP_POLICY_NAME, name.trim());
	}
	
	private String deleteTgppAAAPolicy(String columnName, Object policyToDelete) throws DataManagerException {
		String policyName = (TGPP_POLICY_NAME.equals(columnName)) ? (String)policyToDelete : "TGPP AAA Policy";
		Session session = getSession();
		try{
			Criteria criteria = session.createCriteria(TGPPAAAPolicyData.class);
			TGPPAAAPolicyData data = (TGPPAAAPolicyData)criteria.add(Restrictions.eq(columnName, policyToDelete)).uniqueResult();
			
			if (data == null) {
				throw new InvalidValueException("TGPP AAA Policy does not exist");
			}
			
			session.delete(data);
			return data.getName();
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + policyName + ", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(e.getSQLException()), e);
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + policyName + ", Reason: "
							+ e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + policyName + ", Reason: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public String create(Object obj) throws DataManagerException, DuplicateInstanceNameFoundException {
		TGPPAAAPolicyData tgppAAAPolicyData = (TGPPAAAPolicyData) obj;
		try{	
			Session session=getSession();
			session.clear();
			
			Criteria criteria = session.createCriteria(TGPPAAAPolicyData.class).setProjection(Projections.max("orderNumber")); 
			
			List  maxOrderNumber = criteria.list();
	
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
			long orderNumber = (Long) maxOrderNumber.get(0);
				tgppAAAPolicyData.setOrderNumber(++orderNumber);
			} else {
				tgppAAAPolicyData.setOrderNumber(1L);
			}
			
			
			tgppAAAPolicyData.setAuditUid(UUIDGenerator.generate());
			
			session.save(tgppAAAPolicyData);
			session.flush();
			session.clear();
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException("Failed to create TGPP Service Policy " + tgppAAAPolicyData.getName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE,exp);
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return tgppAAAPolicyData.getName();
	}

	@Override
	public TGPPAAAPolicyData getTGPPAAAPolicyData(String tgppAAAPolicyID) throws DataManagerException {
		return getTgppAAAPolicy(TGPP_AAA_POLICY_ID, tgppAAAPolicyID);
	}

	@Override
	public TGPPAAAPolicyData getTGPPAAAPolicyDataByName(String policyName) throws DataManagerException {
		return getTgppAAAPolicy(TGPP_POLICY_NAME, policyName.trim());
	}
	
	private TGPPAAAPolicyData getTgppAAAPolicy(String columnName, Object policyToGet) throws DataManagerException {
		String policyName = (TGPP_POLICY_NAME.equals(columnName)) ? (String)policyToGet : "TGPP AAA Policy";
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(TGPPAAAPolicyData.class);
			
			TGPPAAAPolicyData tgppAAAPolicyData = (TGPPAAAPolicyData)criteria.add(Restrictions.eq(columnName, policyToGet)).uniqueResult();
			if (tgppAAAPolicyData == null) {
				throw new InvalidValueException("TGPP AAA Policy does not exist");
			}
			
			return tgppAAAPolicyData;
		} catch(DataManagerException exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + policyName +", Reason: " + exp.getMessage(), exp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + policyName +", Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public String updateTgppAAAPolicyById(TGPPAAAPolicyData tgppAAAPolicyData, IStaffData staffData, 
			boolean auditEnable) throws DataManagerException {
		return updateTGPPAAABasicDetail(tgppAAAPolicyData, tgppAAAPolicyData.getTgppAAAPolicyId(),
				TGPP_AAA_POLICY_ID, staffData, auditEnable);
	}
	
	@Override
	public String updateTgppAAAPolicyByName(TGPPAAAPolicyData tgppAAAPolicyData, String policyToUpdate, IStaffData staffData, 
			boolean auditEnable) throws DataManagerException {
		return updateTGPPAAABasicDetail(tgppAAAPolicyData, policyToUpdate,
				TGPP_POLICY_NAME, staffData, auditEnable);
	}
	
	private String updateTGPPAAABasicDetail(TGPPAAAPolicyData tgppAAAPolicyData, Object policyToUpdate, String columnName, IStaffData staffData, 
			boolean auditEnable) throws DataManagerException {
		
		EliteAssert.notNull(tgppAAAPolicyData,"TGPPAAAPolicyData must not be null.");
		
		try{
		
			Session session = getSession();
			Criteria criteria = session.createCriteria(TGPPAAAPolicyData.class).add(Restrictions.eq(columnName, policyToUpdate));
			TGPPAAAPolicyData tgppAAAPolicyDataInst = (TGPPAAAPolicyData)criteria.uniqueResult();
			
			if (tgppAAAPolicyDataInst == null) {
				throw new InvalidValueException("TGPP AAA Policy not found");
			}
			
			JSONArray jsonArray = null;
			JSONArray jsonArrayCCF = null;
			if(auditEnable){
				jsonArray=ObjectDiffer.diff(tgppAAAPolicyDataInst,tgppAAAPolicyData);
				//History of command code flow
				JAXBContext context = JAXBContext.newInstance(TGPPServerPolicyData.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				
				String xmlDatas = new String(tgppAAAPolicyData.getTgppAAAPolicyXml());
				StringReader stringReader =new StringReader(xmlDatas.trim());
				TGPPServerPolicyData tgppServerPolicyData = (TGPPServerPolicyData) unmarshaller.unmarshal(stringReader);
				
				String xmlDatasOld = new String(tgppAAAPolicyDataInst.getTgppAAAPolicyXml());
				StringReader stringReaderOld =new StringReader(xmlDatasOld.trim());
				TGPPServerPolicyData tgppServerPolicyDataOld = (TGPPServerPolicyData) unmarshaller.unmarshal(stringReaderOld);
				
				jsonArrayCCF=ObjectDiffer.diff(tgppServerPolicyDataOld,tgppServerPolicyData);
			}
			
			tgppAAAPolicyDataInst.setName(tgppAAAPolicyData.getName());
			tgppAAAPolicyDataInst.setDescription(tgppAAAPolicyData.getDescription());
			tgppAAAPolicyDataInst.setStatus(tgppAAAPolicyData.getStatus());
			tgppAAAPolicyDataInst.setRuleset(tgppAAAPolicyData.getRuleset());
			tgppAAAPolicyDataInst.setUserIdentity(tgppAAAPolicyData.getUserIdentity());
			tgppAAAPolicyDataInst.setSessionManagement(tgppAAAPolicyData.getSessionManagement());
			tgppAAAPolicyDataInst.setCui(tgppAAAPolicyData.getCui());
			tgppAAAPolicyDataInst.setTgppAAAPolicyXml(tgppAAAPolicyData.getTgppAAAPolicyXml());
			tgppAAAPolicyDataInst.setDefaultResponseBehaviorArgument(tgppAAAPolicyData.getDefaultResponseBehaviorArgument());
			tgppAAAPolicyDataInst.setDefaultResponseBehaviour(tgppAAAPolicyData.getDefaultResponseBehaviour());
			  
			session.update(tgppAAAPolicyDataInst);
			session.flush();
			session.clear();
			
			if(auditEnable){
				int ccfArraySize = jsonArrayCCF.size();
				for (int i = 0; i < ccfArraySize; i++) {
					JSONObject ccfObject = jsonArrayCCF.getJSONObject(i);
					JSONArray ccfArray = ccfObject.getJSONArray("values");
					
					int arrayLen = ccfArray.size();
					
					for (int j = 0; j < arrayLen; j++) {
						JSONObject handlerObject = ccfArray.getJSONObject(j);
						String handlerObjectField = handlerObject.getString("Field");
						boolean isPostHandler = "Post Response Command Code Flow".equals(handlerObjectField);
						Object valuesArray = handlerObject.get("values");
						
						if(valuesArray != null){
							JSONArray handlerArray = (JSONArray) valuesArray;
	
							int handlerArrayLen = handlerArray.size();
							for (int k = 0; k < handlerArrayLen; k++) {
								JSONObject rec = handlerArray.getJSONObject(k);

								if(isPostHandler){
									Object postArray = rec.get("values");
									if(postArray != null){
										JSONArray postHandlerEntryArray = (JSONArray) postArray;
										int postHandlerEntryArraySize = postHandlerEntryArray.size();

										for (int l = 0; l < postHandlerEntryArraySize; l++) {
											JSONObject postObj = postHandlerEntryArray.getJSONObject(l);
											String fieldVal = postObj.getString("Field");

											if(fieldVal.contains("CDR")){
												JSONObject newRec = updateCDRDriverIdtoName(postObj);
												postHandlerEntryArray.set(l, newRec);
											}
										}

										rec.remove("values");
										rec.put("values", postHandlerEntryArray);

										handlerArray.set(k, rec);
									}
								} else {
									
									String fieldVal = rec.getString("Field");

									if(fieldVal.equals("EAP Config")){
									
										JSONObject newObj=createJSONObjectWithName(new EAPConfigBLManager(),rec);
										handlerArray.set(k, newObj);
									}
										/*Get DiameterConcurrency name  for  History */
									
									if(fieldVal.equals("Diameter Concurrency Policy")){
										
										JSONObject newObj=createJSONObjectWithName(new DiameterConcurrencyBLManager(),rec);
										handlerArray.set(k, newObj);
									}

									if(fieldVal.equals("Primary Group") || fieldVal.equals("Secondary Group") || fieldVal.equals("Additional Group")){
										JSONObject newObj=getJSONObjWithDriverNameFromIds(rec);
										handlerArray.set(k, newObj);
									}

									if(fieldVal.contains("Proxy Communication")){
										JSONArray proxyComArray = rec.getJSONArray("values");
										int proxyCommArraySize = proxyComArray.size();

										for (int l = 0; l < proxyCommArraySize; l++) {
											JSONObject proxyCommObj = proxyComArray.getJSONObject(l);
											String proxyCommField = proxyCommObj.getString("Field");

											if(proxyCommField.equals("Peer Group")){
												String oldId = proxyCommObj.getString("OldValue");
												String newId = proxyCommObj.getString("NewValue");

												String oldName = null;
												String newName = null;

												BaseBLManager baseBLManager = null;
												boolean isRadius = handlerObject.getString("Field").contains("Radius"); 
												if(isRadius){
													baseBLManager = new RadiusESIGroupBLManager();
												} else {
													baseBLManager = new DiameterPeerGroupBLManager();
												}

												if(oldId.equals("-")){
													oldName = "-";
												} else {
													if(isRadius){
														oldName = ((RadiusESIGroupBLManager) baseBLManager).getRadiusESIGroupById(oldId).getName();
													} else {
														oldName = ((DiameterPeerGroupBLManager) baseBLManager).getDiameterPeerGroupNameFromId(oldId);
													}
												}

												if(newId.equals("-")){
													newName = "-";
												} else {
													if(isRadius){
														newName = ((RadiusESIGroupBLManager) baseBLManager).getRadiusESIGroupById(newId).getName();
													} else {
														newName = ((DiameterPeerGroupBLManager) baseBLManager).getDiameterPeerGroupNameFromId(newId);
													}
												}

												JSONObject newObj=new JSONObject();
												newObj.put("Field", proxyCommField);
												newObj.put("OldValue", oldName);
												newObj.put("NewValue", newName);

												proxyComArray.set(l, newObj);
											}
										}
										rec.remove("values");
										rec.put("values", proxyComArray);

										handlerArray.set(k, rec);
									}

									if(fieldVal.contains("CDR")){
										JSONObject newRec = updateCDRDriverIdtoName(rec);
										handlerArray.set(k, newRec);
									}
								}

								handlerObject.remove("values");
								handlerObject.put("values", handlerArray);

								ccfArray.set(j, handlerObject);
							}
						}
					}
					ccfObject.remove("values");
					ccfObject.put("values", ccfArray);
					
					jsonArrayCCF.set(i, ccfObject);
				}
				
				for (int i = 0; i < ccfArraySize; i++) {
					jsonArray.add(jsonArrayCCF.getJSONObject(i));
				}
				
				staffData.setAuditId(tgppAAAPolicyDataInst.getAuditUid());
				staffData.setAuditName(tgppAAAPolicyDataInst.getName());
				doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_TGPP_AAA_SERVICE_POLICY);
			}
			return tgppAAAPolicyDataInst.getName();
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update TGPP Service Policy " + tgppAAAPolicyData.getName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	private JSONObject updateCDRDriverIdtoName(JSONObject rec) throws NumberFormatException, DataManagerException{

		JSONArray cdrArray = rec.getJSONArray("values");
		int cdrArraySize = cdrArray.size();
		
		for (int l = 0; l < cdrArraySize; l++) {
			JSONObject cdrObj = cdrArray.getJSONObject(l);
			String cdrField = cdrObj.getString("Field");
			
			if(cdrField.equals("Primary Driver") || cdrField.equals("Secondary Driver")){
				JSONObject newObj=getJSONObjWithDriverNameFromIds(cdrObj);
				cdrArray.set(l, newObj);
			}
			
		}
		rec.remove("values");
		rec.put("values", cdrArray);
		
		return rec;
	}
	private String getDriverNamesFromId(JSONObject object,String jsonIdField) throws NumberFormatException, DataManagerException{
		DriverBLManager driverBLManager = new DriverBLManager();
		
		String ids = object.getString(jsonIdField);
		String fieldVal = object.getString("Field");
		
		boolean isPrimaryGroup = fieldVal.equals("Primary Group");
		
		StringTokenizer idTokens = new StringTokenizer(ids,",");
		List<String> namesList = new ArrayList<String>(); 
		
		String strId = null;
		String id = null;
		String appendedStr = null;
		while (idTokens.hasMoreTokens()) {
			strId = idTokens.nextElement().toString();
			
			if(strId.equals("-") == false){
				if(isPrimaryGroup){
					id = strId.substring(0,strId.indexOf("|"));
					appendedStr = strId.substring(strId.indexOf("|")+1);
					namesList.add(driverBLManager.getDriverNameById(id) + "-W-" + appendedStr);
				} else {
					namesList.add(driverBLManager.getDriverNameById(strId));
				}
			}else{
				namesList.add("-");
			}
		}
		
		return namesList.toString().replaceAll("[\\s\\[\\]]", "");
	}
	
	private JSONObject getJSONObjWithDriverNameFromIds(JSONObject object) throws NumberFormatException, DataManagerException{
		String oldNames = getDriverNamesFromId(object,"OldValue");
		String newNames = getDriverNamesFromId(object,"NewValue");;
		
		JSONObject newObj=new JSONObject();
		newObj.put("Field", object.getString("Field"));
		newObj.put("OldValue", oldNames);
		newObj.put("NewValue", newNames);
		
		return newObj;
	}

	
	/**
	 * This method return new JSONObject from old that contains the Id
	 * @param baseBLManager BaseBLManager object for making database call for name
	 * @param oldJsonObject JSONObject containing Id
	 * @return JSONObject with updated name
	 * @throws NumberFormatException
	 * @throws DataManagerException
	 */
	private JSONObject createJSONObjectWithName(BaseBLManager baseBLManager, JSONObject oldJsonObject) throws NumberFormatException, DataManagerException{
		String oldId = oldJsonObject.getString("OldValue");
		String newId = oldJsonObject.getString("NewValue");
		
		String oldName = getNameFromId(oldId,baseBLManager);
		String newName = getNameFromId(newId,baseBLManager);
		
		JSONObject newJsonObj=new JSONObject();
		newJsonObj.put("Field", oldJsonObject.getString("Field"));
		newJsonObj.put("OldValue", oldName);
		newJsonObj.put("NewValue", newName);
		
		return newJsonObj;
	}
	/**
	 * This function get name of  EAP, Diameter concurrency policy from id
	 * @param id of EAP, Diameter concurrency policy
	 * @param blManager BaseBLManager of EAP, Diameter concurrency policy
	 * @return name 
	 * @throws NumberFormatException
	 * @throws DataManagerException
	 */
	private String getNameFromId(String id,BaseBLManager blManager) throws NumberFormatException, DataManagerException{
		String name = null;
		DiameterConcurrencyData concurrencyData=new DiameterConcurrencyData();
		if(id.equals("-")){
			name = "-";
		} else {
			if(blManager instanceof DiameterConcurrencyBLManager){
				concurrencyData =((DiameterConcurrencyBLManager) blManager).getDiameterConcurrencyDataById(id);
				if(concurrencyData != null){
					name = concurrencyData.getName().toString();
					}
			} else if(blManager instanceof EAPConfigBLManager){
				name = ((EAPConfigBLManager) blManager).getEapNameFromId(id);
			} 
			
			if(name == null || name.isEmpty()){
				name = "-";
			}
		}
		return name;
	}
	

	@Override
	public void updateStatus(List<String> asList, String status) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = null;
		String tgppAAAPolicyId = null;
		for(int i=0;i<asList.size();i++){
			tgppAAAPolicyId = asList.get(i);
			criteria = session.createCriteria(TGPPAAAPolicyData.class);
			TGPPAAAPolicyData tgppaaaPolicyData = (TGPPAAAPolicyData)criteria.add(Restrictions.eq(TGPP_AAA_POLICY_ID,tgppAAAPolicyId)).uniqueResult();
			if(status.equals(BaseConstant.SHOW_STATUS_ID)){
				if(tgppaaaPolicyData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
					long orderNumber = tgppaaaPolicyData.getOrderNumber();
					Criteria newCriteria = session.createCriteria(TGPPAAAPolicyData.class); 
					newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",new Long(orderNumber))); 	
					List sameOrderNoList = newCriteria.list();
					if(sameOrderNoList != null && sameOrderNoList.size() >0){
						// set the order number to the last number
						criteria = session.createCriteria(TGPPAAAPolicyData.class);
						criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
						List<TGPPAAAPolicyData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
						if(tempList != null){
							tgppaaaPolicyData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
						}
				}
			}
		}
			tgppaaaPolicyData.setStatus(status);			
			session.update(tgppaaaPolicyData);			
			session.flush();
	}

	}
	@Override
		public List<TGPPAAAPolicyData> searchActiveTGPPAAAServicePolicy() throws DataManagerException {
			try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(TGPPAAAPolicyData.class).addOrder(Order.asc("orderNumber"));
				return criteria.add(Restrictions.eq("status", "CST01")).list();
	
			} catch (HibernateException e) {
				throw new DataManagerException(e.getMessage(), e);
			}
	 	}
	@Override
	public List<TGPPAAAPolicyData> getTGPPAAAServicePolicyList() throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(TGPPAAAPolicyData.class).addOrder(Order.asc("orderNumber"));
			return criteria.list();

		} catch (HibernateException e) {
			throw new DataManagerException(e.getMessage(), e);
		}
 	}
}
