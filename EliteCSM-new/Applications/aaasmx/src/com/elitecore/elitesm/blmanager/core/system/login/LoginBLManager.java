package com.elitecore.elitesm.blmanager.core.system.login;

import java.util.Date;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;

public class LoginBLManager extends BaseBLManager{

	/**
	 * 
	 * @param userName, password
	 * @return String
	 * @throws DataManagerException
	 */
	public String validateLogin(String userName, String password) throws DataManagerException {
		StaffBLManager staffBLManager = new StaffBLManager();
		return staffBLManager.validateLogin(userName,password);
    }
	
	public boolean validateUserName(String userName)throws DataManagerException{
		StaffBLManager staffBLManager = new StaffBLManager();
		return staffBLManager.validateUserName(userName);
	}
	public Date getLastChangePwdDate(String userName) throws DataManagerException {
		StaffBLManager staffBLManager = new StaffBLManager();
		return staffBLManager.getLastChangedPwdDate(userName);
    }

	public boolean isValidUser(String systemUserName, String password) throws DataManagerException {
		StaffBLManager staffBLManager = new StaffBLManager();
		return staffBLManager.isValidUser(systemUserName,password);
	}
}
