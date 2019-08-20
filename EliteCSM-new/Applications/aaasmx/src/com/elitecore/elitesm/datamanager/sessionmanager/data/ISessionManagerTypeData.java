package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.sql.Timestamp;
import java.util.Set;

public interface ISessionManagerTypeData {
	
	
	public String getSmtypeid();
	public void setSmtypeid(String smtypeid) ;
	public String getName() ;
	public void setName(String name) ;
	public long getSerialno();
	public void setSerialno(long serialno);
	public String getAlias();
	public void setAlias(String alias);
	public String getDescription() ;
	public void setDescription(String description);
	public Timestamp getStatusChangedDate();
	public void setStatusChangedDate(Timestamp statusChangedDate);
	public String getStatus() ;
	public void setStatus(String status);
	public Set getSessionManagerInstanceSet();
	public void setSessionManagerInstanceSet(Set sessionManagerInstanceSet) ;
	

}
