package com.elitecore.elitesm.datamanager.diameter.routingentry;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;

public interface DiameterRoutingEntryDataManager extends DataManager{
	
	@Override
	public String create(Object object) throws DataManagerException;
}
