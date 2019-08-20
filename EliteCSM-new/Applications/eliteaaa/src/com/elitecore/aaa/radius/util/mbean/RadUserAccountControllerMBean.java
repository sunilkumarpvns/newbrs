package com.elitecore.aaa.radius.util.mbean;

import java.util.Map;

public interface RadUserAccountControllerMBean {
	public String[] readUserFiles();
	public Map  getAccount(String filename,String id);
	public int  updateUserAccount(String filename,String id,Map userAccount);
	public int addUserAccount(String filename,Map userAccount);
	public int deleteUserAccount(String filename,String id);
	public Map readEntities(String filename);
}
