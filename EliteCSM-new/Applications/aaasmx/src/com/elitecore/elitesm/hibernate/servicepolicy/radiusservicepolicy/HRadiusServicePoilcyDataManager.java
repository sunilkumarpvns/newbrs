package com.elitecore.elitesm.hibernate.servicepolicy.radiusservicepolicy;

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
import com.elitecore.elitesm.blmanager.digestconf.DigestConfBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.RadiusServicePolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadiusServicePolicyData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;


public class HRadiusServicePoilcyDataManager extends HBaseDataManager implements RadiusServicePolicyDataManager{
	private static final String RADIUS_SERVICE_POLICY = " Radius Service Policy: ";
	private static final String RADIUS_SERVICE_POLICY_NAME = "name";
	private static final String RADIUS_SERVICE_POLICY_ID = "radiusPolicyId";
	private static final String MODULE="HAuthServicePoilcyDataManager";

	public PageList search(RadServicePolicyData radiusServicePolicyData ,int pageNo, int pageSize) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(RadServicePolicyData.class);
		PageList pageList = null;

		try{

			if((radiusServicePolicyData.getName() != null && radiusServicePolicyData.getName().length()>0 )){
				criteria.add(Restrictions.ilike(RADIUS_SERVICE_POLICY_NAME,"%"+radiusServicePolicyData.getName()+"%"));
			}

			if(!(radiusServicePolicyData.getStatus().equalsIgnoreCase("All")) ){

				criteria.add(Restrictions.ilike("status",radiusServicePolicyData.getStatus()));
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
	public String create(Object obj) throws DataManagerException {
		RadServicePolicyData radiusServicePolicyData = (RadServicePolicyData) obj;
		try{	
			Session session=getSession();
			session.clear();
			
			Criteria criteria = session.createCriteria(RadServicePolicyData.class).setProjection(Projections.max("orderNumber")); 
			
			List  maxOrderNumber = criteria.list();
	
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
			long orderNumber = (Long) maxOrderNumber.get(0);
				radiusServicePolicyData.setOrderNumber(++orderNumber);
			} else {
				radiusServicePolicyData.setOrderNumber(1L);
			}
			
			
			radiusServicePolicyData.setAuditUid(UUIDGenerator.generate());
			
			session.save(radiusServicePolicyData);
			session.flush();
			session.clear();
		}catch(ConstraintViolationException e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + RADIUS_SERVICE_POLICY + radiusServicePolicyData.getName() +
					REASON + EliteExceptionUtils.extractConstraintName(e.getSQLException()), e);
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE + RADIUS_SERVICE_POLICY +radiusServicePolicyData.getName() +
					REASON + hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE + RADIUS_SERVICE_POLICY +radiusServicePolicyData.getName() +
					REASON + exp.getMessage(),exp);
		}
		return radiusServicePolicyData.getName();
	}

	@Override
	public String deleteById(String policyToDelete) throws DataManagerException {
		return delete(policyToDelete, RADIUS_SERVICE_POLICY_ID);
	}
	
	@Override
	public String deleteByName(String name) throws DataManagerException {
		return delete(name, RADIUS_SERVICE_POLICY_NAME);
	}

	private String delete(Object policyToDelete, String columnName) throws DataManagerException {
		String policyName = (RADIUS_SERVICE_POLICY_NAME.equals(columnName)) ? (String)policyToDelete : "Radius Service Policy ";
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadServicePolicyData.class);

			RadServicePolicyData authPolicyData = (RadServicePolicyData) criteria.add(Restrictions.eq(columnName, policyToDelete)).uniqueResult()	;
			
			if (authPolicyData == null) {
				throw new InvalidValueException("Radius Service Policy does not exist");
			}
			session.delete(authPolicyData);
			session.flush();
			return authPolicyData.getName();
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to delete " + policyName + ", Reason: " + hbe.getMessage(), hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + policyName + ", Reason: " + e.getMessage(), e);
		}
	}
	
	@Override
	public RadServicePolicyData getRadiusServPolicyInstDataById(String policyId)throws DataManagerException {
		return getRadiusServicePolicy(RADIUS_SERVICE_POLICY_ID, policyId);
	}

	@Override
	public void updateRadiusServicePolicyById(RadServicePolicyData radServicePolicyData, IStaffData staffData,String actionAlias) throws DataManagerException {
		updateRadiusServicePolicy(radServicePolicyData, RADIUS_SERVICE_POLICY_ID, radServicePolicyData.getRadiusPolicyId(), staffData, actionAlias);
	}
	
	@Override
	public void updateRadiusServicePolicyName(RadServicePolicyData policyData, String policyToUpdate, IStaffData staffData, String actionAlias) throws DataManagerException {
		updateRadiusServicePolicy(policyData, RADIUS_SERVICE_POLICY_NAME, policyToUpdate, staffData, actionAlias);
	}
	
	private void updateRadiusServicePolicy(RadServicePolicyData policyData, String columnName, Object policyToUpdate, IStaffData staffData, String actionAlias) throws DataManagerException {
		EliteAssert.notNull(policyData,"radius service policy must not be null.");
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadServicePolicyData.class).add(Restrictions.eq(columnName, policyToUpdate));
			RadServicePolicyData radServicePolicyDataInst = (RadServicePolicyData)criteria.uniqueResult();
			
			if (radServicePolicyDataInst == null) {
				throw new InvalidValueException("Policy does not exist");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(radServicePolicyDataInst,policyData);
			
			JAXBContext context = JAXBContext.newInstance(RadiusServicePolicyData.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			String xmlDatas = new String(policyData.getRadiusPolicyXml());
			StringReader stringReader =new StringReader(xmlDatas.trim());
			RadiusServicePolicyData radServerPolicyData = (RadiusServicePolicyData) unmarshaller.unmarshal(stringReader);
			
			String xmlDatasOld = new String(radServicePolicyDataInst.getRadiusPolicyXml());
			StringReader stringReaderOld =new StringReader(xmlDatasOld.trim());
			RadiusServicePolicyData radServerPolicyDataOld = (RadiusServicePolicyData) unmarshaller.unmarshal(stringReaderOld);
			
			JSONArray radFlowArray=ObjectDiffer.diff(radServerPolicyDataOld,radServerPolicyData);
			
			radServicePolicyDataInst.setName(policyData.getName());
			radServicePolicyDataInst.setDescription(policyData.getDescription());
			radServicePolicyDataInst.setStatus(policyData.getStatus());
			radServicePolicyDataInst.setAuthMsg(policyData.getAuthMsg());
			radServicePolicyDataInst.setAcctMsg(policyData.getAcctMsg());
			radServicePolicyDataInst.setAuthRuleset(policyData.getAuthRuleset());
			radServicePolicyDataInst.setAcctRuleset(policyData.getAcctRuleset());
			radServicePolicyDataInst.setValidatepacket(policyData.getValidatepacket());
			radServicePolicyDataInst.setDefaultAuthResBehavior(policyData.getDefaultAuthResBehavior());
			radServicePolicyDataInst.setHotlinePolicy(policyData.getHotlinePolicy());
			radServicePolicyDataInst.setDefaultAcctResBehavior(policyData.getDefaultAcctResBehavior());
			radServicePolicyDataInst.setSessionManagerId(policyData.getSessionManagerId());
			radServicePolicyDataInst.setAuthResponseAttributes(policyData.getAuthResponseAttributes());
			radServicePolicyDataInst.setAcctResponseAttributes(policyData.getAcctResponseAttributes());
			radServicePolicyDataInst.setAuthAttribute(policyData.getAuthAttribute());
			radServicePolicyDataInst.setAcctAttribute(policyData.getAcctAttribute());
			radServicePolicyDataInst.setCui(policyData.getCui());
			radServicePolicyDataInst.setRadiusPolicyXml(policyData.getRadiusPolicyXml());
			radServicePolicyDataInst.setUserIdentity(policyData.getUserIdentity());
         
			session.update(radServicePolicyDataInst);
			session.flush();
			session.clear();
			
			int jsonArraySize = jsonArray.size();
			for (int jsonArrayIndex = 0; jsonArrayIndex < jsonArraySize; jsonArrayIndex++) {
				JSONObject jsonArrayObj = jsonArray.getJSONObject(jsonArrayIndex);
				String fieldVal = jsonArrayObj.getString("Field");
				
				if("Session Management".equals(fieldVal)){
					JSONArray sessionMgtArray = jsonArrayObj.getJSONArray("values");
					JSONObject sessionManger = sessionMgtArray.getJSONObject(0);
					
					JSONObject newObj= createJSONObjectWithName(new SessionManagerBLManager(),sessionManger);
					sessionMgtArray.set(0, newObj);
					
					jsonArrayObj.remove("values");
					jsonArrayObj.put("values", sessionMgtArray);
					
					jsonArray.set(jsonArrayIndex, jsonArrayObj);
				}
			}
			
			int radFlowArraySize = radFlowArray.size();
			for (int i = 0; i < radFlowArraySize; i++) {
				JSONObject flowObject = radFlowArray.getJSONObject(i);
				JSONArray flowArray = flowObject.getJSONArray("values");
				
				int arrayLen = flowArray.size();
				for (int j = 0; j < arrayLen; j++) {
					JSONObject handlerObject = flowArray.getJSONObject(j);
					String handlerObjectField = handlerObject.getString("Field");
					boolean isPostHandler = "Post response Service Flow".equals(handlerObjectField);
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
										} else if (fieldVal.contains("COA/DM Entry")){
											updateCoadmJsonObject(postObj,postHandlerEntryArray,l);
										}
									}

									rec.remove("values");
									rec.put("values", postHandlerEntryArray);

									handlerArray.set(k, rec);
								}
							} else {
								String fieldVal = rec.getString("Field");

								if("EAP Config".equals(fieldVal)){
									JSONObject newObj= createJSONObjectWithName(new EAPConfigBLManager(),rec);
									handlerArray.set(k, newObj);
								} else if ("Digest Config".equals(fieldVal)){
									JSONObject newObj= createJSONObjectWithName(new DigestConfBLManager(),rec);
									handlerArray.set(k, newObj);
								} else if(fieldVal.equals("Primary Group") || fieldVal.equals("Secondary Group") || fieldVal.equals("Additional Group")){
									JSONObject newObj=getJSONObjWithDriverNameFromIds(rec);
									handlerArray.set(k, newObj);
								} else if(fieldVal.contains("CDR")){
									JSONObject newRec = updateCDRDriverIdtoName(rec);
									handlerArray.set(k, newRec);
								} else if("Session Manager".equals(fieldVal)){
									JSONObject newObj= createJSONObjectWithName(new SessionManagerBLManager(),rec);
									handlerArray.set(k, newObj);
								} else if(fieldVal.contains("Proxy(Sequential) Entries") || fieldVal.contains("Broadcast(Parallel) Communication Entries")){
									JSONArray proxySeqArray = rec.getJSONArray("values");

									int proxySeqArraySize = proxySeqArray.size();
									for (int l = 0; l < proxySeqArraySize; l++) {
										JSONObject serverGroupObj = proxySeqArray.getJSONObject(l);

										Object sgArray = serverGroupObj.get("values");
										if(sgArray != null){
											JSONArray serverGroupArray = (JSONArray) sgArray;
											int serverGroupArraySize = serverGroupArray.size();

											for (int m = 0; m < serverGroupArraySize; m++) {
												JSONObject serverGroupEntryObj = serverGroupArray.getJSONObject(m);
												JSONArray serverGroupEntryArray = serverGroupEntryObj.getJSONArray("values");
												int serverGroupEntryArraySize = serverGroupEntryArray.size();

												for (int n = 0; n < serverGroupEntryArraySize; n++) {
													JSONObject serverGroupEntryArrayObj = serverGroupEntryArray.getJSONObject(n);
													if("Server Name".equals(serverGroupEntryArrayObj.getString("Field"))){
														JSONObject newObj= createJSONObjectWithName(new ExternalSystemInterfaceBLManager(),
																serverGroupEntryArray.getJSONObject(n));
														serverGroupEntryArray.set(n, newObj);
													}

												}
												serverGroupEntryObj.remove("values");
												serverGroupEntryObj.put("values", serverGroupEntryArray);

												serverGroupArray.set(m, serverGroupEntryObj);
											}
											serverGroupObj.remove("values");
											serverGroupObj.put("values", serverGroupArray);

											proxySeqArray.set(l, serverGroupObj);
										}

									}
									rec.remove("values");
									rec.put("values", proxySeqArray);

									handlerArray.set(k, rec);
								} else if (fieldVal.contains("COA/DM Entry")){
									updateCoadmJsonObject(rec,handlerArray,k);
								}
							}
						}
						handlerObject.remove("values");
						handlerObject.put("values", handlerArray);
						flowArray.set(j, handlerObject);
					}
				}
				flowObject.remove("values");
				flowObject.put("values", flowArray);
				radFlowArray.set(i, flowObject);
			}
			
			for (int i = 0; i < radFlowArraySize; i++) {
				jsonArray.add(radFlowArray.getJSONObject(i));
			}
			
			staffData.setAuditId(radServicePolicyDataInst.getAuditUid());
			staffData.setAuditName(radServicePolicyDataInst.getName());
			doAuditingJson(jsonArray.toString(), staffData, actionAlias);
		}catch(ConstraintViolationException e){
			throw new DataManagerException("Failed to update Radius Service Policy, Reason: " + EliteExceptionUtils.extractConstraintName(e.getSQLException()), e);
		}catch(HibernateException hExp){
			throw new DataManagerException("Failed to update Radius Service Policy, Reason:" + hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException("Failed to update Radius Service Policy, Reason: " + exp.getMessage(),exp);
		}
	}
	
	/**
	 * This method updates the COA/DM Entry JSONObject from the handler array
	 * @param coadmEntryObj COA/DM Entry JSONObject
	 * @param handlerArray JSONArray that contains the handlers
	 * @param index At what index should the values to be updated in handlerArray
	 */
	private void updateCoadmJsonObject(JSONObject coadmEntryObj,JSONArray handlerArray,int index){

		JSONArray coadmArray = coadmEntryObj.getJSONArray("values");
		int coadmArraySize = coadmArray.size();

		for (int m = 0; m < coadmArraySize; m++) {
			JSONObject coadmValObj = coadmArray.getJSONObject(m);
			String coadmFieldVal = coadmValObj.getString("Field");

			if("Packet Type".equals(coadmFieldVal)){
				String oldName = getCoadmPacketTypeNameFromId(coadmValObj.getString("OldValue"));
				String newName = getCoadmPacketTypeNameFromId(coadmValObj.getString("NewValue"));

				JSONObject newObj=new JSONObject();
				newObj.put("Field", coadmFieldVal);
				newObj.put("OldValue", oldName);
				newObj.put("NewValue", newName);
				coadmArray.set(m, newObj);
			}
		}

		coadmEntryObj.remove("values");
		coadmEntryObj.put("values", coadmArray);
		handlerArray.set(index, coadmEntryObj);
	
	}
	
	/**
	 * Get Packet Type from its id
	 * @param id Id of the packet type
	 * @return name of Packet Type
	 */
	private String getCoadmPacketTypeNameFromId(String id){
		String name = null;
		if(id != null && id.equals("-") == false){
			int packetTypeId = Integer.parseInt(id);
			
			if(packetTypeId == 43){
				name = "COA Request";
			} else if(packetTypeId == 40){
				name = "Disconnect Request";
			} else {
				name = "-";
			}
		}else{
			name = "-";
		}
		return name;
	}
	
	/**
	 * Replaces Driver id with name from CDR JSONObject
	 * @param object JSONObject that contains CDR handler
	 * @return updated JSONObject
	 * @throws NumberFormatException
	 * @throws DataManagerException
	 */
	private JSONObject updateCDRDriverIdtoName(JSONObject object) throws NumberFormatException, DataManagerException{

		JSONArray cdrArray = object.getJSONArray("values");
		int cdrArraySize = cdrArray.size();
		
		for (int l = 0; l < cdrArraySize; l++) {
			JSONObject cdrObj = cdrArray.getJSONObject(l);
			String cdrField = cdrObj.getString("Field");
			
			if(cdrField.equals("Primary Driver") || cdrField.equals("Secondary Driver")){
				JSONObject newObj=getJSONObjWithDriverNameFromIds(cdrObj);
				cdrArray.set(l, newObj);
			}
			
		}
		object.remove("values");
		object.put("values", cdrArray);
		
		return object;
	}
	
	/**
	 * Replaces Driver Id with its name
	 * @param object JSONObject that contains Driver Id
	 * @return JSONObject
	 * @throws NumberFormatException
	 * @throws DataManagerException
	 */
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
	 * This function get Driver Name from database using Driver Id
	 * @param object JSONObject containing Driver Id
	 * @param jsonIdField object field containing Driver Id 
	 * @return name of driver
	 * @throws NumberFormatException
	 * @throws DataManagerException
	 */
	private String getDriverNamesFromId(JSONObject object,String jsonIdField) throws NumberFormatException, DataManagerException{
		DriverBLManager driverBLManager = new DriverBLManager();
		
		String ids = object.getString(jsonIdField);
		String fieldVal = object.getString("Field");
		
		boolean isPrimaryGroup = fieldVal.equals("Primary Group");
		boolean isSecondaryGroup = fieldVal.equals("Secondary Group");
		
		StringTokenizer idTokens = new StringTokenizer(ids,",");
		List<String> namesList = new ArrayList<String>(); 
		
		String strId = null;
		String id = null;
		String appendedStr = null;
		while (idTokens.hasMoreTokens()) {
			strId = idTokens.nextElement().toString();
			
			if(strId.equals("-") == false){
				if(isPrimaryGroup || isSecondaryGroup){
					id = strId.substring(0,strId.indexOf("|"));
					appendedStr = strId.substring(strId.indexOf("|")+1);
					
					if(isPrimaryGroup){
						namesList.add(driverBLManager.getDriverNameById(id) + "-W-" + appendedStr);
					} else {
						String cachedDriverName = driverBLManager.getDriverNameById(appendedStr);
						if(cachedDriverName == null || cachedDriverName.isEmpty() == true){
							cachedDriverName = "--None--";
						}
						namesList.add(driverBLManager.getDriverNameById(id) + "(" + cachedDriverName + ")");
					}
				} else {
					namesList.add(driverBLManager.getDriverNameById(strId));
				}
			}else{
				namesList.add("-");
			}
		}
		
		return namesList.toString().replaceAll("[\\s\\[\\]]", "");
	}
	
	/**
	 * This function get name of session manager, EAP, Digest and ESI from id
	 * @param id of session manager, EAP, Digest and ESI
	 * @param blManager BaseBLManager of session manager, EAP, Digest and ESI respectively
	 * @return name 
	 * @throws NumberFormatException
	 * @throws DataManagerException
	 */
	private String getNameFromId(String id,BaseBLManager blManager) throws NumberFormatException, DataManagerException{
		String name = null;
		if(id.equals("-")){
			name = "-";
		} else {
			if(blManager instanceof SessionManagerBLManager){
				name = ((SessionManagerBLManager) blManager).getSessionManagerInstanceDataNameFromId(id);
			} else if(blManager instanceof EAPConfigBLManager){
				name = ((EAPConfigBLManager) blManager).getEapNameFromId(id);
			} else if(blManager instanceof DigestConfBLManager){
				name = ((DigestConfBLManager) blManager).getDigestConfigInstDataNameFormId(id);
			} else if(blManager instanceof ExternalSystemInterfaceBLManager){
				name = ((ExternalSystemInterfaceBLManager) blManager).getExternalSystemnameFromId(id);
			}
			
			if(name == null || name.isEmpty()){
				name = "-";
			}
		}
		return name;
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

	@Override
	public List<RadServicePolicyData> searchActiveRadiusServicePolicy() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadServicePolicyData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("status","CST01")).list();

		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<RadServicePolicyData> getRadiusServicePolicyList() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadServicePolicyData.class).addOrder(Order.asc("orderNumber"));
			return criteria.list();

		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	@Override
	public RadServicePolicyData getRadiusServicePolicyDataByName(String policyName) throws DataManagerException {
		return getRadiusServicePolicy(RADIUS_SERVICE_POLICY_NAME, policyName);
	}
	
	private RadServicePolicyData getRadiusServicePolicy(String columnName, Object value) throws DataManagerException {
		String policyName = (RADIUS_SERVICE_POLICY_NAME.equals(columnName)) ? (String)value : "Radius Service Policy";

		Session session = getSession();
		try {
		
			Criteria criteria = session.createCriteria(RadServicePolicyData.class).add(Restrictions.eq(columnName, value));
			
			RadServicePolicyData policyData = (RadServicePolicyData) criteria.uniqueResult();
			
			if (policyData == null) {
				throw new InvalidValueException("Radius Service Policy doest not exist");
			}
			
			return policyData;
		} catch (HibernateException e) {
			throw new DataManagerException("Failed to retrieve " + policyName + ", Reason: " + e.getMessage(), e);
		} catch (Exception e) {
			throw new DataManagerException("Failed to retrieve " + policyName + ", Reason: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void updateStatus(List<String> radiusPolicyIds, String status) throws DataManagerException {

		String radiusPolicyId = null;
		Session session = getSession();
		Criteria criteria = null;

		for(int i=0;i<radiusPolicyIds.size();i++){
			radiusPolicyId = radiusPolicyIds.get(i);
			criteria = session.createCriteria(RadServicePolicyData.class);
			RadServicePolicyData radServicePolicyData = (RadServicePolicyData)criteria.add(Restrictions.eq(RADIUS_SERVICE_POLICY_ID,radiusPolicyId)).uniqueResult();
			if(status.equals(BaseConstant.SHOW_STATUS_ID)){
				if(radServicePolicyData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
					long orderNumber = radServicePolicyData.getOrderNumber();
					Criteria newCriteria = session.createCriteria(RadServicePolicyData.class); 
					newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",new Long(orderNumber))); 					
					List sameOrderNoList = newCriteria.list();
					if(sameOrderNoList != null && sameOrderNoList.size() >0){
						// set the order number to the last number
						criteria = session.createCriteria(RadServicePolicyData.class);
						criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
						List<RadServicePolicyData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
						if(tempList != null){
							radServicePolicyData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
						}
					}
				}				
			}
			radServicePolicyData.setStatus(status);			
			session.update(radServicePolicyData);			
			session.flush();
		}
	}
	
}
