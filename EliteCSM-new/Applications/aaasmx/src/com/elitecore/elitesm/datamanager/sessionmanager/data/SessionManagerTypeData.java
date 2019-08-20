package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.sql.Timestamp;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;


public class SessionManagerTypeData extends BaseData implements ISessionManagerTypeData{
	  
	    
	    private String smtypeid;
	    private String name;
	    private long serialno;
	    private String alias;
	    private String description;
	    private Timestamp statusChangedDate;
	    private String status;
	    private Set sessionManagerInstanceSet;
	    
	    
		public String getSmtypeid() {
			return smtypeid;
		}
		public void setSmtypeid(String smtypeid) {
			this.smtypeid = smtypeid;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public long getSerialno() {
			return serialno;
		}
		public void setSerialno(long serialno) {
			this.serialno = serialno;
		}
		public String getAlias() {
			return alias;
		}
		public void setAlias(String alias) {
			this.alias = alias;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Timestamp getStatusChangedDate() {
			return statusChangedDate;
		}
		public void setStatusChangedDate(Timestamp statusChangedDate) {
			this.statusChangedDate = statusChangedDate;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public Set getSessionManagerInstanceSet() {
			return sessionManagerInstanceSet;
		}
		public void setSessionManagerInstanceSet(Set sessionManagerInstanceSet) {
			this.sessionManagerInstanceSet = sessionManagerInstanceSet;
		}
	   
	

}
