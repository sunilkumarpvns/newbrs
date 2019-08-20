package com.elitecore.elitesm.blmanager.radius.correlatedradius;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.correlatedradius.CorrelatedRadiusDataManager;
import com.elitecore.elitesm.datamanager.radius.correlatedradius.data.CorrelatedRadiusData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CorrelatedRadiusBLManager extends BaseBLManager {

    public PageList search(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData, int pageNo, int pageSize) throws DataManagerException {

        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        CorrelatedRadiusDataManager correlatedRadiusDataManager = getCorrelatedRadiusDataManager(session);

        PageList correlatedRadiusEsiList;

        if (correlatedRadiusDataManager == null ){
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }


        try{
            session.beginTransaction();
            correlatedRadiusEsiList = correlatedRadiusDataManager.search(correlatedRadiusData, pageNo, pageSize);

            AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_CORRELATED_RADIUS);
            commit(session);
        } catch(DataManagerException dme) {
            dme.printStackTrace();
            rollbackSession(session);
            throw dme;
        } catch(Exception exp) {
            exp.printStackTrace();
            rollbackSession(session);
            throw new DataManagerException(exp.getMessage(), exp);
        } finally {
            closeSession(session);
        }
        return correlatedRadiusEsiList;
    }

    public CorrelatedRadiusDataManager getCorrelatedRadiusDataManager(IDataManagerSession session) {
        CorrelatedRadiusDataManager correlatedRadiusDataManager = (CorrelatedRadiusDataManager) DataManagerFactory.getInstance().getDataManager(CorrelatedRadiusDataManager.class,session);
        return correlatedRadiusDataManager;
    }

    public void deleteCorrelatedRadiusById(List<String> corrRadiusIds, IStaffData staffData) throws DataManagerException {

        deleteRadiusCorrelatedEsi(corrRadiusIds, staffData, BY_ID);

    }

    public void deleteCorrelatedRadiusByName(List<String> corrRadiusNames, IStaffData staffData) throws DataManagerException {

        deleteRadiusCorrelatedEsi(corrRadiusNames, staffData, BY_NAME);

    }
    private void deleteRadiusCorrelatedEsi(List<String> corRadIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        CorrelatedRadiusDataManager correlatedRadiusDataManager = getCorrelatedRadiusDataManager(session);

        if(correlatedRadiusDataManager == null) {
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        try {
            session.beginTransaction();
            if (Collectionz.isNullOrEmpty(corRadIdOrNames) == false) {

                int size = corRadIdOrNames.size();
                for (int i = 0; i < size; i++) {

                    if (Strings.isNullOrBlank(corRadIdOrNames.get(i)) == false) {

                        String correlatedRadiusIdOrName = corRadIdOrNames.get(i).trim();

                        String correlatedRadiusName;

                        if (isIdOrName) {
                            correlatedRadiusName = correlatedRadiusDataManager.deleteCorelatedrRadiusById(correlatedRadiusIdOrName);
                        } else {
                            correlatedRadiusName = correlatedRadiusDataManager.deleteCorelatedrRadiusByName(correlatedRadiusIdOrName);
                        }

                        staffData.setAuditName(correlatedRadiusName);
                        AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_CORRELATED_RADIUS);
                    }
                }
                commit(session);
            }
        } catch (DataManagerException dme) {
            rollbackSession(session);
            throw dme;
        } catch (Exception e) {
            rollbackSession(session);
            e.printStackTrace();
            throw new DataManagerException(e.getMessage(), e);
        } finally {
            closeSession(session);
        }
    }

    public void create(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData) throws DataManagerException {

        List<CorrelatedRadiusData> correlatedESI = new ArrayList<CorrelatedRadiusData>();
        correlatedESI.add(correlatedRadiusData);
        createCorrelatedRadius(correlatedESI, staffData, "");
    }

    public Map<String, List<Status>> createCorrelatedRadius(List<CorrelatedRadiusData> radiusESIGroups, IStaffData staffData, String partialSuccess) throws DataManagerException {
        return insertRecords(CorrelatedRadiusDataManager.class, radiusESIGroups, staffData, ConfigConstant.CREATE_CORRELATED_RADIUS, partialSuccess);
    }

    public CorrelatedRadiusData getCorrelatedRadiusDataById(String radCorId) throws DataManagerException{
        return getCorrelatedRadiusData(radCorId, BY_ID);
    }

    private CorrelatedRadiusData getCorrelatedRadiusData(Object correlatedRadNameOrId, boolean isIdOrName) throws DataManagerException {

        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        CorrelatedRadiusDataManager correlatedRadiusDataManager = getCorrelatedRadiusDatamanager(session);

        if(correlatedRadiusDataManager == null) {
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }

        try{

            CorrelatedRadiusData correlatedRadData;

            if (isIdOrName) {
                correlatedRadData = correlatedRadiusDataManager.getCorrelatedRadiusDataById((String)correlatedRadNameOrId);
            } else {
                correlatedRadData = correlatedRadiusDataManager.getCorrelatedRadiusDataByName((String) correlatedRadNameOrId);
            }

            return correlatedRadData;

        } catch (DataManagerException dme) {
            throw dme;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataManagerException(e.getMessage(), e);
        } finally {
            closeSession(session);
        }
    }

    public CorrelatedRadiusDataManager getCorrelatedRadiusDatamanager(IDataManagerSession session) {
        CorrelatedRadiusDataManager correlatedRadiusDataManager = (CorrelatedRadiusDataManager) DataManagerFactory.getInstance().getDataManager(CorrelatedRadiusDataManager.class, session);
        return correlatedRadiusDataManager;
    }

    public void updateCorrelatedRadiusById(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData) throws DataManagerException {
        updateCorrelatedRadiusData(correlatedRadiusData,staffData,null);
    }

    public void updateCorrelatedRadiusByName(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData, String corrRadiusName) throws DataManagerException {
        updateCorrelatedRadiusData(correlatedRadiusData, staffData, corrRadiusName);
    }

    private void updateCorrelatedRadiusData(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData, String correlatedRadiusName) throws DataManagerException {

        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        CorrelatedRadiusDataManager correlatedRadiusDataManager = getCorrelatedRadiusDataManager(session);

        if(correlatedRadiusDataManager == null) {
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }
        try {
            session.beginTransaction();

            if (correlatedRadiusName == null) {
                correlatedRadiusDataManager.updateCorrelatedRadiusDataById(correlatedRadiusData, staffData, correlatedRadiusData.getId());
            } else {
                correlatedRadiusDataManager.updateCorrelatedRadiusDataByName(correlatedRadiusData, staffData, correlatedRadiusData.getName());
            }
            commit(session);
        } catch (DataManagerException dme) {
            rollbackSession(session);
            throw dme;
        } catch (Exception e) {
            rollbackSession(session);
            e.printStackTrace();
            throw new DataManagerException(e.getMessage(), e);
        } finally {
            closeSession(session);
        }
    }

    public CorrelatedRadiusData getCorrelatedRadiusDataByName(String correlatedRadiusName) throws DataManagerException {
        return getCorrelatedRadiusData(correlatedRadiusName,BY_NAME);
    }

    public List<CorrelatedRadiusData> getCorrelatedRadiusDataList() throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        CorrelatedRadiusDataManager correlatedRadiusDataManager = getCorrelatedRadiusDatamanager(session);
        List<CorrelatedRadiusData> correlatedRadiusDataList = null;

        if (correlatedRadiusDataManager == null) {
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        }

        try {
            correlatedRadiusDataList = correlatedRadiusDataManager.getCorrelatedRadiusDataList();
        } catch (DataManagerException dme) {
            throw dme;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataManagerException(e.getMessage(), e);
        } finally {
            closeSession(session);
        }
        return correlatedRadiusDataList;
    }
}
