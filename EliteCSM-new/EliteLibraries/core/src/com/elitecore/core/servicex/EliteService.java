package com.elitecore.core.servicex;

import java.util.Date;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.commons.util.constants.ServiceRemarks;

public interface EliteService extends ReInitializable {
	
	/**
	 * 
	 * @return
	 */
	public boolean start();
	
    public EliteService init() throws ServiceInitializationException;
	
	
	/**
	 * 
	 * @return
	 */
	public boolean stop();

	public void doFinalShutdown();

	/**
	 * All the resources, such as {@link ILogger}, that are used by the system 
	 * should be closed in this hook.
	 * <p>
	 * This hook is called by system at last when the server shutdown process has either 
	 * completed gracefully or is being aborted. In both cases the resources that are
	 * occupied must be closed.
	 * <p>
	 * NOTE: It is possible that server shutdown might be in progress while cleanup hook
	 * is called.
	 */
	public void cleanupResources();
	
	public String getServiceIdentifier();
	public String getServiceName();

	ServiceDescription getDescription();

	String getStatus();

	String getRemarks();
	public void setRemark(ServiceRemarks remark);

	Date getStartDate();



}
