package com.elitecore.nvsmx.remotecommunications;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by aditya on 4/18/17.
 */
public interface RMIGroup  {
	public <V> Future<RMIResponse<V>> call(RemoteMethod method) ;

    public	<V> RMIResponse<V> call(RemoteMethod method, long time, TimeUnit timeUnit);

	public <V> RMIResponse<V> call(RemoteMethod method, long time, TimeUnit timeUnit, String serverInstanceId);

	public String getName();

	public String id();
}
