

package com.elitecore.aaa.rm.conf;

import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.radius.conf.BaseRadiusServiceConfiguration;

public interface RMIPPoolConfiguration  extends BaseRadiusServiceConfiguration, DriverConfigurationProvider {
	
	public  String getDataSourceName();
	public boolean isAutoSessionClosureEnabled();
	public int getExecutionInterval();
	public int getReservationTimeoutInterval();
	public int getSessionTimeoutInterval();
	public int getMaxBatchSize() ;
	public boolean isOperationFailureEnabled();
	public int getStatusCheckDuration();
	public String getDefaultIPPoolName();
	public int getDbQueryTimeOut();
	
}
