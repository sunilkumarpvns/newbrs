package com.elitecore.netvertexsm.datamanager.servermgr.certificate;

import java.util.List;
import java.util.Map;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.CrlCertificateData;

public interface CrlCertificateDataManager extends DataManager {
	
	public PageList search(CrlCertificateData crlCertificateData, Map infoMap)throws DataManagerException;
	
	public void create(CrlCertificateData crlCertificateDatas)throws DataManagerException;
	
	public void update(CrlCertificateData crlCertificateDatas) throws DataManagerException;
	
	public void deleteAll(List<Long> lstCrlCertificateId) throws DataManagerException;
	
	public CrlCertificateData getCrlCertificateDataByCrlCertificateId(Long crlCertificateId) throws DataManagerException;
}
