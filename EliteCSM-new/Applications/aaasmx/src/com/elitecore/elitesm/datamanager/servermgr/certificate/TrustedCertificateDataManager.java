package com.elitecore.elitesm.datamanager.servermgr.certificate;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.TrustedCertificateData;

public interface TrustedCertificateDataManager extends DataManager {
	
	public PageList search(TrustedCertificateData trustedCertificateData)throws DataManagerException;
	
	public TrustedCertificateData getTrustedCertificateById(String trustedCertificateId) throws DataManagerException;
	public TrustedCertificateData getTrustedCertificateByName(String trustedCertificateName) throws DataManagerException;

	@Override
	String create(Object object) throws DataManagerException;
	
	public void updateTrustedCertificateByName(TrustedCertificateData trustedCertificate, String trustedCertificateName) throws DataManagerException;
	
	public String deleteTrustedCertificateById(String trustedCertificateId) throws DataManagerException;
	public String deleteTrustedCertificateByName(String trustedCertificateName) throws DataManagerException;
	
}
