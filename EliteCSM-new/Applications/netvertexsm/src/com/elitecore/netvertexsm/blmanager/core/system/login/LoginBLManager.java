package com.elitecore.netvertexsm.blmanager.core.system.login;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;

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
}
