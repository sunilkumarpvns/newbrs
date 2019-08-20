package com.elitecore.netvertex.service.pcrf.logmonitor;

import java.util.Date;

import com.elitecore.core.logmonitor.LogMonitorInfo;
import com.elitecore.core.util.cli.cmd.logmonitorext.Expression;
import com.elitecore.core.util.cli.cmd.logmonitorext.Monitor;
import com.elitecore.core.util.cli.cmd.logmonitorext.MonitorExpression;
import com.elitecore.core.util.cli.cmd.logmonitorext.impl.BaseMonitor;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.util.PCRFValueProvider;

public class PCRFServiceMonitor extends BaseMonitor<PCRFRequest, PCRFResponse> {
	private final Compiler compiler;

	public PCRFServiceMonitor(final PCRFServiceContext context) {
		super(context.getServerContext().getTaskScheduler());
		this.compiler = Compiler.getDefaultCompiler();
	}

	@Override
	public String getType() {
		return "pcrfservice";
	}
	
	public final boolean evaluate(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse){
		for(MonitorExpression<PCRFRequest, PCRFResponse> expression : getExpressions()){
			if(expression.evaluate(pcrfRequest, pcrfResponse)){
				return true;
			}
		}
		
		return false;
	}


	@Override
	protected MonitorExpression<PCRFRequest, PCRFResponse> createMonitorExpression(String condition, long time) throws Exception {
		LogicalExpression expression = compiler.parseLogicalExpression(condition); 
		
		PCRFMonitorExpression expr = new PCRFMonitorExpression(expression);
		
		Date startDate = new Date();
		long expiryTime = 0;
		if(time <= 0){
			expiryTime = Monitor.NO_TIME_LIMIT;
		} else {
			expiryTime = startDate.getTime() + (time * 60 *  1000);
		}
		
		LogMonitorInfo logMonitorInfo = new LogMonitorInfo(condition, startDate.getTime(), time, expiryTime);
		return new MonitorExpression<PCRFRequest, PCRFResponse>(expr, logMonitorInfo);
	}
	
	
	private class PCRFMonitorExpression implements Expression<PCRFRequest,PCRFResponse>{
		
		private LogicalExpression expression;
		public PCRFMonitorExpression(LogicalExpression expression) {
			this.expression = expression;
			
		}

		@Override
		public boolean evaluate(PCRFRequest request, PCRFResponse response) {
			return expression.evaluate(new PCRFValueProvider(request, response));
		}
		
	}


}
