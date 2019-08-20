package com.elitecore.elitesm.hibernate.radius.radiusesigroup;

import java.io.StringReader;
import java.util.List;

import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.RadiusESIGroupDatamanager;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
/**
 * 
 * @author Tejas Shah
 *
 */
public class HRadiusESIGroupDataManager  extends HBaseDataManager implements RadiusESIGroupDatamanager{

	private static final String ESI_GROUP_NAME = "name";
	private static final String ESI_GROUP_ID = "id";
	private static final String MODULE = "HRadiusESIGroupDataManager";

	@Override
	public PageList searchRadiusESIGroupData(RadiusESIGroupData radiusESIGroupData, int requiredPageNo,Integer pageSize) throws DataManagerException {
		List<RadiusESIGroupData> pluginList = null;
		PageList pageList = null;
		
		try{		
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusESIGroupData.class);
			String esiGroupName=radiusESIGroupData.getName();

			if((esiGroupName != null && !"".equals(esiGroupName))){
				esiGroupName = "%"+esiGroupName+"%";
				criteria.add(Restrictions.ilike(ESI_GROUP_NAME,esiGroupName));
			}

			int totalItems =  criteria.list().size();
			criteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			pluginList = criteria.list();

			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(pluginList, requiredPageNo, totalPages ,totalItems);

		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Radius ESI Group List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Radius ESI Group List, Reason: " + e.getMessage(), e);
		}
		return pageList;
	}

	@Override
	public List<RadiusESIGroupData> getRadiusESIGroupDataList() throws DataManagerException {
		List<RadiusESIGroupData> radiusESIGroupDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusESIGroupData.class);
			radiusESIGroupDataList = criteria.list();
			return radiusESIGroupDataList;
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Radius ESI Group List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Radius ESI Group List, Reason: " + e.getMessage(), e);
		}
	}

	
	@Override
	public RadiusESIGroupData getRadiusESIGroupById(String radiusESIGroupId) throws DataManagerException {
		
		return getRadiusESIGroup(ESI_GROUP_ID, radiusESIGroupId);
		
	}

	@Override
	public RadiusESIGroupData getRadiusESIGroupByName(String radiusESIGroupName) throws DataManagerException {
		
		return getRadiusESIGroup(ESI_GROUP_NAME, radiusESIGroupName);
		
	}

	private RadiusESIGroupData getRadiusESIGroup(String propertyName, Object value) throws DataManagerException {
		
		String radiusESIGroupName = (ESI_GROUP_NAME.equals(propertyName)) ? (String) value : "Radius ESI Group";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(RadiusESIGroupData.class).add(Restrictions.eq(propertyName, value));
			RadiusESIGroupData radiusESIGroup = (RadiusESIGroupData) criteria.uniqueResult();
			
			if (radiusESIGroup == null) {
				throw new InvalidValueException("Radius ESI Group not found");
			}
			
			radiusESIGroupName = radiusESIGroup.getName();
			
			return radiusESIGroup;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + radiusESIGroupName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + radiusESIGroupName + ", Reason: " + e.getMessage(), e);
		}
		
	}
	

	@Override
	public String create(Object obj) throws DataManagerException {
		
		RadiusESIGroupData radiusESIGroup = (RadiusESIGroupData) obj;
		String radiusESIGroupName = radiusESIGroup.getName();

		try {

			Session session = getSession();
			session.clear();
			
			String auditId= UUIDGenerator.generate();

			radiusESIGroup.setAuditUId(auditId);

			session.save(radiusESIGroup);

			session.flush();
			session.clear();
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + radiusESIGroupName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + radiusESIGroupName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + radiusESIGroupName + REASON + e.getMessage(), e);
		}
		return radiusESIGroupName;
	}
	

	@Override
	public void updateRadiusESIGroupById(RadiusESIGroupData radiusESIGroup, IStaffData staffData, String radiusESIGroupId) throws DataManagerException {
		
		updateRadiusESIGroup(radiusESIGroup, staffData, ESI_GROUP_ID, radiusESIGroupId);

	}

	@Override
	public void updateRadiusESIGroupByName(RadiusESIGroupData radiusESIGroup, IStaffData staffData, String radiusESIGroupName) throws DataManagerException {

		updateRadiusESIGroup(radiusESIGroup, staffData, ESI_GROUP_NAME, radiusESIGroupName);
		
	}

	private void updateRadiusESIGroup(RadiusESIGroupData radiusESIGroup, IStaffData staffData, String propertyName, Object value) throws DataManagerException {

		String radiusESIGroupName = (ESI_GROUP_NAME.equals(propertyName)) ? (String) value : "Radius ESI Group";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(RadiusESIGroupData.class).add(Restrictions.eq(propertyName, value));
			RadiusESIGroupData radiusESIGroupData = (RadiusESIGroupData) criteria.uniqueResult();

			if (radiusESIGroupData == null) {
				throw new InvalidValueException("Radius ESI Group not found");
			}

			radiusESIGroupName = radiusESIGroupData.getName();

			String oldXmlDatas = new String(radiusESIGroupData.getEsiGroupDataXml());
			StringReader oldStringReader =new StringReader(oldXmlDatas.trim());
			RadiusEsiGroupConfigurationData oldEsiConfigurationData = ConfigUtil.deserialize(oldStringReader, RadiusEsiGroupConfigurationData.class);

			String newXmlDatas = new String(radiusESIGroup.getEsiGroupDataXml());
			StringReader newStringReader =new StringReader(newXmlDatas.trim());
			RadiusEsiGroupConfigurationData newEsiConfigurationData = ConfigUtil.deserialize(newStringReader, RadiusEsiGroupConfigurationData.class);

			JSONArray jsonArray = ObjectDiffer.diff(oldEsiConfigurationData, newEsiConfigurationData);

			radiusESIGroupData.setName(radiusESIGroup.getName());
			radiusESIGroupData.setDescription(radiusESIGroup.getDescription());
			radiusESIGroupData.setEsiGroupDataXml(radiusESIGroup.getEsiGroupDataXml());

			session.update(radiusESIGroupData);
			session.flush();
			
			staffData.setAuditId(radiusESIGroupData.getAuditUId());
			staffData.setAuditName(radiusESIGroupData.getName());

			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_RADIUS_ESI_GROUP);

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + radiusESIGroupName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + radiusESIGroupName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + radiusESIGroupName + ", Reason: " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteRadiusESIGroupById(String radiusESIGroupId) throws DataManagerException {
		
		return deleteRadiusESIGroup(ESI_GROUP_ID, radiusESIGroupId);
		
	}

	@Override
	public String deleteRadiusESIGroupByName(String radiusESIGroupName) throws DataManagerException {
		
		return deleteRadiusESIGroup(ESI_GROUP_NAME, radiusESIGroupName);
		
	}

	private String deleteRadiusESIGroup(String propertyName, Object value) throws DataManagerException {
		
		String radiusESIGroupName = (ESI_GROUP_NAME.equals(propertyName)) ? (String) value : "Radius ESI Group";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(RadiusESIGroupData.class).add(Restrictions.eq(propertyName, value));
			RadiusESIGroupData radiusESIGroup = (RadiusESIGroupData) criteria.uniqueResult();

			if (radiusESIGroup == null) {
				throw new InvalidValueException("Radius ESI Group not found");
			}

			radiusESIGroupName = radiusESIGroup.getName();
			
			session.delete(radiusESIGroup);
			session.flush();

			return radiusESIGroupName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + radiusESIGroupName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + radiusESIGroupName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + radiusESIGroupName + ", Reason: " + e.getMessage(), e);
		} 

	}

	
}
