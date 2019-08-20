package com.elitecore.license.nfv;

import java.util.List;

public interface LicenseDAO {
	
	void insert(LicenseDaoData data) throws DaoException;
	void updateStatus(String encryptedInstanceName, String status, String decryptedInstanceName) throws DaoException;
	List<LicenseDaoData> fetchAll(String status) throws DaoException;
	List<LicenseDaoData> fetchBy(String instanceName, String status) throws DaoException;
	int count(String status) throws DaoException;
	LicenseDaoData fetchBy(String id) throws DaoException;

}