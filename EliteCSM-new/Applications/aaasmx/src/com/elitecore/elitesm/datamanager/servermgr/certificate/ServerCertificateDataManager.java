package com.elitecore.elitesm.datamanager.servermgr.certificate;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;

public interface ServerCertificateDataManager extends DataManager {

	public PageList search(ServerCertificateData serverCertificateData) throws DataManagerException;

	public ServerCertificateData getServerCertificateById(String serverCertificateId) throws DataManagerException;
	public ServerCertificateData getServerCertificateByName(String serverCertificateName) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;
	
	public void updateServerCertificateByName(ServerCertificateData serverCertificate, String serverCertificateName, String certificateType) throws DataManagerException;

	public String deleteServerCertificateById(String serverCertificateId) throws DataManagerException;
	public String deleteServerCertificateByName(String serverCertificateName) throws DataManagerException;
	
}
