package com.elitecore.elitesm.datamanager.externalsystem.data;

import java.util.Set;

public interface IExternalSystemInterfaceTypeData {
	
	
	public Set getEsiInstanceDetail(); 
	public void setEsiInstanceDetail(Set esiInstanceDetail);

	public long getEsiTypeId() ;
	public void setEsiTypeId(long esiTypeId);

	public String getName();
	public void setName(String name);

	public String getDisplayName();
	public void setDisplayName(String displayName);

	public long getSerialNo() ;
	public void setSerialNo(long serialNo);

	public String getAlias();
	public void setAlias(String alias);

	public String getDescription() ;
	public void setDescription(String description);

	public String getStatus() ;
	public void setStatus(String status);
	

}
