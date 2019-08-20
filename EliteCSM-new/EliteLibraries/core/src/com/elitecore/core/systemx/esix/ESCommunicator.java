package com.elitecore.core.systemx.esix;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;


public interface ESCommunicator extends ReInitializable{
	
	int NO_SCANNER_THREAD = 0;
	
	int ALWAYS_ALIVE = -1;
	
	void init() throws InitializationFailedException;
	
	public boolean isAlive();
	
	public void scan();
	
	public void addESIEventListener(ESIEventListener<ESCommunicator> eventListener);
	
	public void removeESIEventListener(ESIEventListener<ESCommunicator> eventListener);
	
	public void stop();
	
	public String getName();
	
	public String getTypeName();
	
	public ESIStatistics getStatistics();
	
	public void registerAlertListener(AlertListener alertListener);
}
