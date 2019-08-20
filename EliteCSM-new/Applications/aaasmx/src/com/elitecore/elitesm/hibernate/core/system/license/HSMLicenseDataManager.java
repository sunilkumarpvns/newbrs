package com.elitecore.elitesm.hibernate.core.system.license;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.license.SMLicenseDataManager;
import com.elitecore.elitesm.datamanager.core.system.license.data.SMLicenseData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.license.util.AES;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HSMLicenseDataManager  extends HBaseDataManager implements SMLicenseDataManager{
	private static final String REGISTERED = "Registered";
	private static final String DEREGISTERED = "Deregistered";
	@Override
	public void updateLicenseStatus(String instanceName, String status, IStaffData staffData) throws DataManagerException {

		try{	
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(SMLicenseData.class);
			List<SMLicenseData> smLicenseDataList = (List<SMLicenseData>)criteria.add(Restrictions.eq("instanceName",instanceName)).add(Restrictions.ne("status", status)).list();
			
			if(Collectionz.isNullOrEmpty(smLicenseDataList) == false) {
				for(SMLicenseData data : smLicenseDataList) {
					data.setStatus(status);
					session.update(data);
				}
				
				JSONObject oldJsonObject = deRegisteredHistory(smLicenseDataList, true);
				JSONObject newJsonObject = deRegisteredHistory(smLicenseDataList, false);
				
				staffData.setAuditId(getAuditId(ConfigConstant.LICENSE));
				
				JSONArray jsonArray = ObjectDiffer.diff(oldJsonObject, newJsonObject);
				doAudit(jsonArray.toString(), staffData);
				session.flush();
			} 
			
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update License status, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update License status, Reason: " + hbe.getMessage(), hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update License status, Reason: " + e.getMessage(), e);
		}
	}

	@Override
	public void insertRecord(SMLicenseData licenseData, IStaffData staffData) throws DataManagerException {

		try{
			Session session = getSession();
			session.save(licenseData);
			
			JSONObject oldJsonObject = new JSONObject();
			JSONObject newJsonObject = new JSONObject();
			
			
			staffData.setAuditId(getAuditId(ConfigConstant.LICENSE));
			oldJsonObject.put(AES.decrypt(licenseData.getInstanceName()), DEREGISTERED);
			newJsonObject.put(AES.decrypt(licenseData.getInstanceName()), REGISTERED);

			JSONArray jsonArray = ObjectDiffer.diff(oldJsonObject, newJsonObject);
			doAudit(jsonArray.toString(), staffData);
			session.flush();
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to insert License data, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to insert License data, Reason: " + hbe.getMessage(), hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to insert License data, Reason: " + e.getMessage(), e);
		}
	}

	@Override
	public List<SMLicenseData> fetchRecords(Map<String, Object> propertyNameValues) throws DataManagerException {
		List<SMLicenseData> smLicenseData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(SMLicenseData.class)
					.add(Restrictions.allEq(propertyNameValues));
			smLicenseData = criteria.list();
		} catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrieve fetch records, Reason: " + hbe.getMessage(), hbe);
		} catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrieve fetch records, Reason: " + e.getMessage(), e);
		}

		return smLicenseData;
	}

	@Override
	public void doAudit(String jsonString, IStaffData staffData) throws DataManagerException {
		doAuditingJson(jsonString, staffData, ConfigConstant.UPGRADE_LICENSE);
	}

	@Override
	public String getSystemAuditId(String moduleName) throws DataManagerException {
		return getAuditId(moduleName);
	}
	
	private JSONObject deRegisteredHistory(List<SMLicenseData> priorityTableDatas, boolean isDeregistered) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		JSONObject jsonObject = new JSONObject();
		if(isDeregistered) {
			for(SMLicenseData data : priorityTableDatas) {
				
				jsonObject.put(AES.decrypt(data.getInstanceName()), REGISTERED);
			}	
		} else {
			for(SMLicenseData data : priorityTableDatas) {
				jsonObject.put(AES.decrypt(data.getInstanceName()), DEREGISTERED);
			}	
		}
		
		return jsonObject;
	}
}
