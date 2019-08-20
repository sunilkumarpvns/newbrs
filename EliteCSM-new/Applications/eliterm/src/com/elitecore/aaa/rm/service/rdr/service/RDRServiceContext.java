/**
 * 
 */
package com.elitecore.aaa.rm.service.rdr.service;

import com.elitecore.aaa.rm.conf.RDRConfiguration;
import com.elitecore.aaa.rm.conf.RdrDetailLocalConfiguration;
import com.elitecore.core.servicex.ServiceContext;

/**
 * @author nitul.kukadia
 *
 */
public interface RDRServiceContext extends ServiceContext {
	public RDRConfiguration getRDRConfiguration();
	public RdrDetailLocalConfiguration getRdrDetailLocalConfiguration();
}
