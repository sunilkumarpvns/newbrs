package com.elitecore.elitesm.hibernate.servermgr.drivers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.DiameterDriverDataManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.CrestelRatingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.RatingDriverPropsData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HDiameterDriverDataManager extends HBaseDataManager implements DiameterDriverDataManager {

	private static final String DRIVER_INSTANCE_ID = "driverInstanceId";
	private static final String DRIVER_INSTANCE_NAME = "name";
	private static final String MODULE = null;
	
	
	public String createCrestelRatingDriver(DriverInstanceData driverInstanceData,CrestelRatingDriverData crestelRatingDriverData) throws DataManagerException {
		try{
			Session session = getSession();
			session.clear();
			Criteria criteria = session.createCriteria(DriverInstanceData.class);

			String auditId= UUIDGenerator.generate();
			
			driverInstanceData.setAuditUId(auditId);
			Set<CrestelRatingDriverData> crestelRatingDriverDataSet = new HashSet<CrestelRatingDriverData>();
			crestelRatingDriverDataSet.add(crestelRatingDriverData);
			driverInstanceData.setCrestelRatingSet(crestelRatingDriverDataSet);
			session.save(driverInstanceData);

			String driverInstanceId = driverInstanceData.getDriverInstanceId();
			crestelRatingDriverData.setDriverInstanceId(driverInstanceId);		
			List<RatingDriverPropsData> tempfeilMapSet = crestelRatingDriverData.getJndiPropValMapList();
			crestelRatingDriverData.setJndiPropValMapList(null);

			session.save(crestelRatingDriverData);
			if(Collectionz.isNullOrEmpty(tempfeilMapSet) == false) {
				Iterator<RatingDriverPropsData> itr = tempfeilMapSet.iterator();
				int orderNumber = 1;
				while(itr.hasNext()){
					RatingDriverPropsData feildMapData = itr.next();
					feildMapData.setCrestelRatingDriverId(crestelRatingDriverData.getCrestelRatingDriverId());
					feildMapData.setOrderNumber(orderNumber++);
					session.save(feildMapData);
					session.flush();
					session.clear();
				}
			}
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON +  hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON +e.getMessage(),e);
		}
		return driverInstanceData.getName();
	}

	public boolean getDriverInstancesByName(String name) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(DriverInstanceData.class);
		List<DriverInstanceData> driverInstanceList = criteria.add(Restrictions.eq("name",name)).list();
		if(driverInstanceList != null && driverInstanceList.size() > 0){
			return true;
		}
		return false;

	}

	public CrestelRatingDriverData getCrestelRatingDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException {
		
		try{
			Session session = getSession();
			CrestelRatingDriverData crestelRatingDriverData = null;
			Criteria criteria = session.createCriteria(CrestelRatingDriverData.class);
			crestelRatingDriverData = (CrestelRatingDriverData)criteria.add(Restrictions.eq("driverInstanceId",driverInstanceId)).uniqueResult();
			return crestelRatingDriverData;
			
		}catch(HibernateException hbe){
			
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
		
	}
	
	public HssAuthDriverData getHSSDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException {
		
		try{
			Session session = getSession();
			HssAuthDriverData hssAuthDriverData = null;
			Criteria criteria = session.createCriteria(HssAuthDriverData.class);
			hssAuthDriverData = (HssAuthDriverData)criteria.add(Restrictions.eq("driverInstanceId",driverInstanceId)).uniqueResult();
			return hssAuthDriverData;
			
		}catch(HibernateException hbe){
			
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
		
	}

	@Override
	public void updateDiameterRatingTranslationDriverByName(DriverInstanceData driverInstdata,CrestelRatingDriverData crestelRatingDriverData, IStaffData staffData,
			String name, String moduleName) throws DataManagerException {
		updateDiameterRatingTranslationDriver(driverInstdata, crestelRatingDriverData, staffData, DRIVER_INSTANCE_NAME, name, moduleName);
	}

	@Override
	public void updateDiameterRatingTranslationDriverById(DriverInstanceData driverInstdata, CrestelRatingDriverData crestelRatingDriverData, IStaffData staffData, String modulName)
			throws DataManagerException {
		updateDiameterRatingTranslationDriver(driverInstdata, crestelRatingDriverData, staffData, DRIVER_INSTANCE_ID, driverInstdata.getDriverInstanceId(), modulName);
	}
	
	public void updateDiameterRatingTranslationDriver(DriverInstanceData driverInstdata,CrestelRatingDriverData driverData, IStaffData staffData, String property, Object value, String moduleName) throws DataManagerException {
		try{

			Session session = getSession();
			
			Set<CrestelRatingDriverData> diameterRatingSet= new LinkedHashSet<CrestelRatingDriverData>();
			
			/*
			 * update Driverinstance Data
			 */
			Criteria criteria = session.createCriteria(DriverInstanceData.class);
			
			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(property, value)).uniqueResult();
			
			if(tempDriverInstanceData == null) {
				throw new InvalidValueException("Driver Instance not found.");
			}
			
			diameterRatingSet.add(driverData);
			driverInstdata.setCrestelRatingSet(diameterRatingSet);
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstdata);
		
			tempDriverInstanceData.setName(driverInstdata.getName());
			tempDriverInstanceData.setDescription(driverInstdata.getDescription());
			tempDriverInstanceData.setLastModifiedDate(driverInstdata.getLastModifiedDate());
			session.update(tempDriverInstanceData);
			session.flush();
			
			/*
			 * update CrestelRatingDriverData 
			 */

			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();
			criteria = session.createCriteria(CrestelRatingDriverData.class);

			CrestelRatingDriverData diameterRatingDriverData = (CrestelRatingDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();
			
			if(diameterRatingDriverData == null ) {
				throw new InvalidValueException("Crestel Rating Driver not found.");
			}
            
			diameterRatingDriverData.setTransMapConfId(driverData.getTransMapConfId());
            diameterRatingDriverData.setInstanceNumber(driverData.getInstanceNumber());
            session.update(diameterRatingDriverData);
			session.flush();
            
            /*
             * update RatingDriverPropsData List
             */
            
            Criteria ratingDriverPropsCriteria = session.createCriteria(RatingDriverPropsData.class);
            ratingDriverPropsCriteria.add(Restrictions.eq("crestelRatingDriverId",diameterRatingDriverData.getCrestelRatingDriverId()));
       
			List<RatingDriverPropsData> ratingDriverPropsList = ratingDriverPropsCriteria.list();
            deleteObjectList(ratingDriverPropsList, session);
            
            List<RatingDriverPropsData> tempfeilMapList = driverData.getJndiPropValMapList();
            Set<RatingDriverPropsData> ratingDriverSet = new LinkedHashSet<RatingDriverPropsData>();
            
            if(Collectionz.isNullOrEmpty(tempfeilMapList) == false) {
            	Iterator<RatingDriverPropsData> itr = tempfeilMapList.iterator();
            	int orderNumber = 1;
            	while(itr.hasNext()) {
            		RatingDriverPropsData feildMapData = itr.next();
            		feildMapData.setCrestelRatingDriverId(diameterRatingDriverData.getCrestelRatingDriverId());
            		feildMapData.setOrderNumber(orderNumber++);
            		ratingDriverSet.add(feildMapData);
            		session.save(feildMapData);
            		session.flush();
            	}
            }

            staffData.setAuditId(tempDriverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstdata.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update "+moduleName+", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update "+moduleName+", Reason: "  +  hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update "+moduleName+", Reason: " +e.getMessage(),e);
		}
	}
	
	@Override
	public List<HssAuthDriverFieldMapData> getHssFieldMappingData(String hssauthdriverid) throws DataManagerException {
		PageList pageList=null;
		try{			
			Session session=getSession();
			Criteria criteria=session.createCriteria(HssAuthDriverFieldMapData.class).add(Restrictions.eq("hssauthdriverid", hssauthdriverid)).addOrder(Order.asc("dbFieldMapId"));

			List<HssAuthDriverFieldMapData> hssAuthDriverFieldmapList=criteria.list();
			
			return hssAuthDriverFieldmapList;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public List<DiameterPeerRelData> getHssPeerRelDataList(String hssauthdriverid) throws DataManagerException {
		PageList pageList=null;
		try{			
			Session session=getSession();
			Criteria criteria=session.createCriteria(DiameterPeerRelData.class).add(Restrictions.eq("hssauthdriverid", hssauthdriverid));
			criteria.addOrder(Order.asc("hhsPeerGroupRelId"));
			
			List<DiameterPeerRelData> diameterPeerRealDataList=criteria.list();
			
			return diameterPeerRealDataList;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
}
