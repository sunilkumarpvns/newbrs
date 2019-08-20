package com.elitecore.netvertexsm.datamanager.servermgr.certificate;

import java.util.List;
import java.util.Map;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData;

public interface ServerCertificateDataManager extends DataManager {

	public void create(ServerCertificateData serverCertificateData) throws DataManagerException;
	
	public PageList search(ServerCertificateData serverCertificateData,Map infoMap) throws DataManagerException;
	
	public void update(ServerCertificateData serverCertificateData) throws DataManagerException;
	
	public void delete(List<Long> serverCertificateId) throws DataManagerException;
	
	public ServerCertificateData getServerCertificateDataByServerCertificateId(Long serverCertificateId) throws DataManagerException;
	
}
