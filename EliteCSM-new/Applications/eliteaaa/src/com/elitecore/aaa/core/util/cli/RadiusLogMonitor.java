package com.elitecore.aaa.core.util.cli;

import java.util.Date;

import javax.annotation.Nonnull;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.util.exprlib.RequestOnlyValueProvider;
import com.elitecore.core.logmonitor.LogMonitorInfo;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.cli.cmd.logmonitorext.Expression;
import com.elitecore.core.util.cli.cmd.logmonitorext.Monitor;
import com.elitecore.core.util.cli.cmd.logmonitorext.MonitorExpression;
import com.elitecore.core.util.cli.cmd.logmonitorext.impl.BaseMonitor;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

/**
 * Provides log monitoring ability for radius by evaluating expression provided
 * from user interface
 * 
 * @author narendra.pathai
 *
 */
public class RadiusLogMonitor extends BaseMonitor<RadServiceRequest, RadServiceResponse> {
	
	private final Compiler compiler;

	public RadiusLogMonitor(@Nonnull TaskScheduler scheduler) {
		super(scheduler);
		this.compiler = Compiler.getDefaultCompiler();
	}


	@Override
	public String getType() {
		return "radius";
	}
	
	public final boolean evaluate(RadServiceRequest request, RadServiceResponse response){
		for (MonitorExpression<RadServiceRequest, RadServiceResponse> expression : getExpressions()){
			if (expression.evaluate(request, response)) {
				return true;
			}
		}
		
		return false;
	}
	

	@Override
	protected MonitorExpression<RadServiceRequest, RadServiceResponse> createMonitorExpression(String condition, long time) throws Exception {
		LogicalExpression expression = compiler.parseLogicalExpression(condition); 
		
		RadiusMonitorExpression expr = new RadiusMonitorExpression(expression);
		
		Date startDate = new Date();
		long expiryTime = 0;
		if(time <= 0){
			expiryTime = Monitor.NO_TIME_LIMIT;
		} else {
			expiryTime = startDate.getTime() + (time * 60 *  1000);
		}
		
		LogMonitorInfo logMonitorInfo = new LogMonitorInfo(condition, startDate.getTime(), time, expiryTime);
		return new MonitorExpression<RadServiceRequest, RadServiceResponse>(expr, logMonitorInfo);
	}
	
	
	private class RadiusMonitorExpression implements Expression<RadServiceRequest, RadServiceResponse>{
		
		private LogicalExpression expression;
		public RadiusMonitorExpression(LogicalExpression expression) {
			this.expression = expression;
			
		}

		@Override
		public boolean evaluate(RadServiceRequest request, RadServiceResponse response) {
			if (request != null) {
				return expression.evaluate(new RequestOnlyValueProvider((RadServiceRequest)request));
			}
			
			return false;
		}
	}
}

