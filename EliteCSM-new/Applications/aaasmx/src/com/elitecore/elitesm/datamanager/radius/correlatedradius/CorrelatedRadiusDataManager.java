package com.elitecore.elitesm.datamanager.radius.correlatedradius;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.correlatedradius.data.CorrelatedRadiusData;

import java.util.List;

public interface CorrelatedRadiusDataManager extends DataManager{
    PageList search(CorrelatedRadiusData correlatedRadiusData, int pageNo, int pageSize) throws DataManagerException;
    @Override
    public String create(Object object) throws DataManagerException;

    CorrelatedRadiusData getCorrelatedRadiusDataById(String correlatedRadNameOrId) throws DataManagerException;

    CorrelatedRadiusData getCorrelatedRadiusDataByName(String correlatedRadNameOrId) throws DataManagerException;

    void updateCorrelatedRadiusDataById(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData, String id) throws DataManagerException;

    void updateCorrelatedRadiusDataByName(CorrelatedRadiusData correlatedRadiusData, IStaffData staffData, String name) throws DataManagerException;

    String deleteCorelatedrRadiusById(String correlatedRadiusIdOrName) throws DataManagerException;

    String deleteCorelatedrRadiusByName(String correlatedRadiusIdOrName) throws DataManagerException;

    List<CorrelatedRadiusData> getCorrelatedRadiusDataList() throws DataManagerException;
}
