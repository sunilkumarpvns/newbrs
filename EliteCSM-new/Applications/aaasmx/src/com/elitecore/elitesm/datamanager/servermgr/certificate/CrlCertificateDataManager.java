package com.elitecore.elitesm.datamanager.servermgr.certificate;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.CrlCertificateData;

public interface CrlCertificateDataManager extends DataManager {
	
	public PageList search(CrlCertificateData crlCertificateData)throws DataManagerException;
	
	public CrlCertificateData getCertificateRevocationById(String certificateRevocationId) throws DataManagerException;
	public CrlCertificateData getCertificateRevocationByName(String certificateRevocationName) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;
	
	public void updateCertificateRevocationByName(CrlCertificateData certificateRevocation, String certificateRevocationName) throws DataManagerException;
	
	public String deleteCertificateRevocationById(String certificateRevocationId) throws DataManagerException;
	public String deleteCertificateRevocationByName(String certificateRevocationName) throws DataManagerException;
	
}
