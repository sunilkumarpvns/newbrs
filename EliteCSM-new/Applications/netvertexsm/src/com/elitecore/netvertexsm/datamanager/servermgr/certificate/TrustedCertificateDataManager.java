package com.elitecore.netvertexsm.datamanager.servermgr.certificate;

import java.util.List;
import java.util.Map;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData;

public interface TrustedCertificateDataManager extends DataManager {
	
	public void create(TrustedCertificateData trustedCertificateData)throws DataManagerException;
	
	public PageList search(TrustedCertificateData trustedCertificateData, Map infoMap)throws DataManagerException;

	public void update(TrustedCertificateData trustedCertificateData) throws DataManagerException;
	
	public void delete(Long trustedCertificateId) throws DataManagerException;
	
	public void deleteAll(List<Long> lstTrustedCertificateId) throws DataManagerException;
	
	public TrustedCertificateData getTrustedCertificateDataByTrustedCertificateId(Long trustedCertificateId) throws DataManagerException;
}
