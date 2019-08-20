/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.service;

import com.elitecore.aaa.rm.conf.GTPPrimeConfiguration;
import com.elitecore.core.servicex.ServiceContext;

/**
 * @author dhaval.jobanputra
 *
 */
public interface GTPPrimeServiceContext extends ServiceContext {
	public GTPPrimeConfiguration getGTPPrimeConfiguration();
}
