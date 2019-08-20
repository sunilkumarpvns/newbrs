package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.db.DBDataSource;
import com.elitecore.corenetvertex.spr.data.ProfileFieldMapping;

public interface DBSPInterfaceConfiguration extends SPInterfaceConfiguration {
	
	  
	  /**
	   * This method returns the identity column field 
	   * @return String
	   */
	  public String getIdentityField();

	DBDataSource getDbDataSource();

	/**
	   * This method returns the time limit for query execution 
	   * @return int
	   */
	  public int getDbQueryTimeout();
	  
	  /**
	   * This method returns the <code>AccountDataFieldMapping</code>
	   * @return AccountDataFieldMapping 
	   */
	  
	  public ProfileFieldMapping getProfileFieldMapping();
	  
	  /**
	   * This method returns the status check duration
	   * @return int
	   */
	  
	  public int getStatusCheckDuration();
	  
	  /**
	   * This method returns the time out
	   * @return int
	   */
	  
	  public int getTimeOut();
	  

	  public String getTableName();
}
