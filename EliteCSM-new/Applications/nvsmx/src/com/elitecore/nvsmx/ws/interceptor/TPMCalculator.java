package com.elitecore.nvsmx.ws.interceptor;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.Calendar;

import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
 
public class TPMCalculator extends BaseIntervalBasedTask{
	private String MODULE = "TPM-CALCULATOR";
	private long lastResetTimeMillis;
	private final TPMAware tpmAware;
	private String name;
	
	public TPMCalculator(TPMAware tpmAware,String name){
		this.tpmAware = tpmAware;
		lastResetTimeMillis = System.currentTimeMillis();
		this.name = name;
	}
	
	@Override
	public long getInitialDelay() {
		return 60 - Calendar.getInstance().get(Calendar.SECOND);		
	}

	@Override
	public long getInterval() {
		return 60;
	}

	@Override
	public void execute(AsyncTaskContext context) {
		
		try{
			long tempReqCount = tpmAware.getLastMinutesTotalRequest();
			tpmAware.resetLastMinutesTotalRequest();
			
			long currentTimeMillis = System.currentTimeMillis();
			long timeDIffSec = (long) (Math.ceil(currentTimeMillis - lastResetTimeMillis) / 1000D);		
			lastResetTimeMillis = currentTimeMillis;
			
			long tps = (long) (Math.ceil(tempReqCount / timeDIffSec));
			tpmAware.setTPS(tps);
			
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Last 1 minute Average TPS = "+ tps+" for '"+name+"'");
			}
		}catch(Exception ex){
			if (getLogger().isErrorLogLevel()) {
				getLogger().warn(MODULE, "Error while calculating TPS for '"+name+"'");
			}
		}
	}
 
}

