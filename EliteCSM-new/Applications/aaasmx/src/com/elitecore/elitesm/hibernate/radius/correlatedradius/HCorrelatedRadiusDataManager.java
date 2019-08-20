package com.elitecore.elitesm.hibernate.radius.correlatedradius;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import net.sf.json.JSONArray;
import org.hibernate.exception.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.correlatedradius.CorrelatedRadiusDataManager;
import com.elitecore.elitesm.datamanager.radius.correlatedradius.data.CorrelatedRadiusData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HCorrelatedRadiusDataManager extends HBaseDataManager implements CorrelatedRadiusDataManager {

    private static final String CORRELATED_RADIUS_NAME = "name";
    private static final String CORRELATED_RADIUS_ID = "id";
    private static final String MODULE = "Correlated Radius";

    public PageList search(CorrelatedRadiusData correlatedRadiusData, int pageNo, int pageSize) throws DataManagerException {
        PageList pageList = null;

        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(CorrelatedRadiusData.class);

            if((correlatedRadiusData.getName() != null && correlatedRadiusData.getName().length()>0 )){
                criteria.add(Restrictions.ilike(CORRELATED_RADIUS_NAME,correlatedRadiusData.getName()));
            }

            int totalItems = criteria.list().size();
            criteria.setFirstResult(((pageNo-1) * pageSize));

            if (pageSize > 0 ){
                criteria.setMaxResults(pageSize);
            }

            List correlatedRadiusList = criteria.list();


            long totalPages = (long)Math.ceil(totalItems/pageSize);
            if(totalItems%pageSize == 0)
                totalPages-=1;

            pageList = new PageList(correlatedRadiusList, pageNo, totalPages ,totalItems);

        } catch(HibernateException hExp) {
            hExp.printStackTrace();
            throw new DataManagerException("Failed to search Correlated Radius, Reason: " + hExp.getMessage(), hExp);
        } catch(Exception exp) {
            exp.printStackTrace();
            throw new DataManagerException("Failed to search Correlated Radius, Reason: " + exp.getMessage(), exp);
        }
        Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
        return pageList;
    }

    @Override
    public String create(Object obj) throws DataManagerException {
        CorrelatedRadiusData correlatedRadiusData = (CorrelatedRadiusData) obj;
        String correlatedRadiusName = correlatedRadiusData.getName();

        try{

            Session session = getSession();
            session.clear();

            String auditId= UUIDGenerator.generate();

            correlatedRadiusData.setAuditUId(auditId);

            session.save(correlatedRadiusData);
            session.flush();
            session.clear();
        } catch (ConstraintViolationException cve) {
            Logger.logTrace(MODULE, cve);
            throw new DataManagerException(FAILED_TO_CREATE + correlatedRadiusName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
        } catch (HibernateException he) {
            Logger.logTrace(MODULE, he);
            throw new DataManagerException(FAILED_TO_CREATE + correlatedRadiusName + REASON + he.getMessage(), he);
        } catch (Exception e) {
            Logger.logTrace(MODULE, e);
            throw new DataManagerException(FAILED_TO_CREATE + correlatedRadiusName + REASON + e.getMessage(), e);
        }
        return correlatedRadiusName;

    }

    @Override
    public CorrelatedRadiusData getCorrelatedRadiusDataById(String correlatedRadNameOrId) throws DataManagerException {
        return getCorrelatedRadiusData(CORRELATED_RADIUS_ID,correlatedRadNameOrId);
    }

    @Override
    public CorrelatedRadiusData getCorrelatedRadiusDataByName(String correlatedRadNameOrId) throws DataManagerException {
        return getCorrelatedRadiusData(CORRELATED_RADIUS_NAME,correlatedRadNameOrId);
    }

    private CorrelatedRadiusData getCorrelatedRadiusData(String propertyName, Object value) throws DataManagerException {

        String radiusESIGroupName = (CORRELATED_RADIUS_NAME.equals(propertyName)) ? (String) value : "Correlated Radius";

        try {

            Session session = getSession();

            Criteria criteria = session.createCriteria(CorrelatedRadiusData.class).add(Restrictions.eq(propertyName, value));
            CorrelatedRadiusData correlatedRadiusData = (CorrelatedRadiusData) criteria.uniqueResult();

            if (correlatedRadiusData == null) {
                throw new InvalidValueException("Correlated Radius Data not found");
            }

            radiusESIGroupName = correlatedRadiusData.getName();

            return correlatedRadiusData;

        } catch (HibernateException he) {
            he.printStackTrace();
            throw new DataManagerException("Failed to retrive " + radiusESIGroupName + ", Reason: " + he.getMessage(), he);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataManagerException("Failed to retrive " + radiusESIGroupName + ", Reason: " + e.getMessage(), e);
        }

    }

    @Override
    public void updateCorrelatedRadiusDataById(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData, String id) throws DataManagerException {
        updateRadiusESIGroup(correlatedRadiusData,staffData,CORRELATED_RADIUS_ID, id);
    }

    @Override
    public void updateCorrelatedRadiusDataByName(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData, String name) throws DataManagerException {
        updateRadiusESIGroup(correlatedRadiusData, staffData, CORRELATED_RADIUS_NAME, name);
    }

    @Override
    public String deleteCorelatedrRadiusById(String correlatedRadiusIdOrName) throws DataManagerException {
        return deleteCorrelatedRadius(CORRELATED_RADIUS_ID, correlatedRadiusIdOrName);
    }

    @Override
    public String deleteCorelatedrRadiusByName(String correlatedRadiusIdOrName) throws DataManagerException {
        return deleteCorrelatedRadius(CORRELATED_RADIUS_NAME, correlatedRadiusIdOrName);
    }

    @Override
    public List<CorrelatedRadiusData> getCorrelatedRadiusDataList() throws DataManagerException {

        try {

            Session session = getSession();

            Criteria criteria = session.createCriteria(CorrelatedRadiusData.class);
            List<CorrelatedRadiusData> correlatedRadiusDataList = criteria.list();

            return correlatedRadiusDataList;

        } catch (HibernateException he) {
            he.printStackTrace();
            throw new DataManagerException("Failed to retrive Correlated Radius Data, Reason: " + he.getMessage(), he);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataManagerException("Failed to retrive Correlated Radius Data, Reason: " + e.getMessage(), e);
        }
    }

    private String deleteCorrelatedRadius(String propertyName, Object value) throws DataManagerException {

        String correlatedRadName = (CORRELATED_RADIUS_NAME.equals(propertyName)) ? (String) value : "Correlated Radius";

        try {

            Session session = getSession();

            Criteria criteria = session.createCriteria(CorrelatedRadiusData.class).add(Restrictions.eq(propertyName, value));
            CorrelatedRadiusData correlatedRadiusData = (CorrelatedRadiusData) criteria.uniqueResult();

            if (correlatedRadiusData == null) {
                throw new InvalidValueException("Correlated Radius Data not found");
            }

            correlatedRadName = correlatedRadiusData.getName();

            Criteria radEsiData = session.createCriteria(RadiusESIGroupData.class);
            List<RadiusESIGroupData> radEsiDataList = radEsiData.list();

            //Validate If Esi is Binded in Radius Esi Group
            verifyRadiusEsiIsBinded(radEsiDataList,correlatedRadName);

            session.delete(correlatedRadiusData);
            session.flush();

            return correlatedRadName;

        } catch (ConstraintViolationException cve) {
            cve.printStackTrace();
            throw new DataManagerException("Failed to delete " + correlatedRadName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
        } catch (HibernateException he) {
            he.printStackTrace();
            throw new DataManagerException("Failed to delete " + correlatedRadName + ", Reason: " + he.getMessage(), he);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataManagerException("Failed to delete " + correlatedRadName + ", Reason: " + e.getMessage(), e);
        }
    }

    private void updateRadiusESIGroup(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData, String propertyName, Object value) throws DataManagerException {

        String corrRadiusName = (CORRELATED_RADIUS_NAME.equals(propertyName)) ? (String) value : "Correlated Radius";

        try {

            Session session = getSession();

            Criteria criteria = session.createCriteria(CorrelatedRadiusData.class).add(Restrictions.eq(propertyName, value));
            CorrelatedRadiusData corrRadData = (CorrelatedRadiusData) criteria.uniqueResult();

            if (corrRadData == null) {
                throw new InvalidValueException("Correlated Radius Data not found");
            }

            corrRadiusName = corrRadData.getName();

            JSONArray jsonArray = ObjectDiffer.diff(corrRadData, correlatedRadiusData);

            corrRadData.setName(correlatedRadiusData.getName());
            corrRadData.setDescription(correlatedRadiusData.getDescription());
            corrRadData.setAuthEsiId(correlatedRadiusData.getAuthEsiId());
            corrRadData.setAcctEsiId(correlatedRadiusData.getAcctEsiId());
            corrRadData.setAuthEsiName(correlatedRadiusData.getAuthEsiName());
            corrRadData.setAcctEsiName(correlatedRadiusData.getAcctEsiName());

            session.update(corrRadData);
            session.flush();

            staffData.setAuditId(corrRadData.getAuditUId());
            staffData.setAuditName(corrRadData.getName());

            doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_RADIUS_ESI_GROUP);

        } catch (ConstraintViolationException cve) {
            cve.printStackTrace();
            throw new DataManagerException("Failed to update " + corrRadiusName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
        } catch (HibernateException he) {
            he.printStackTrace();
            throw new DataManagerException("Failed to update " + corrRadiusName + ", Reason: " + he.getMessage(), he);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataManagerException("Failed to update " + corrRadiusName + ", Reason: " + e.getMessage(), e);
        }
    }
}
