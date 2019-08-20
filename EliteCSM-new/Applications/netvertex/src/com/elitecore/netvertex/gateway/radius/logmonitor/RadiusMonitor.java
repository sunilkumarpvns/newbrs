package com.elitecore.netvertex.gateway.radius.logmonitor;

import java.util.Date;

import com.elitecore.core.logmonitor.LogMonitorInfo;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.util.cli.cmd.logmonitorext.Expression;
import com.elitecore.core.util.cli.cmd.logmonitorext.Monitor;
import com.elitecore.core.util.cli.cmd.logmonitorext.MonitorExpression;
import com.elitecore.core.util.cli.cmd.logmonitorext.impl.BaseMonitor;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.radius.RadiusGatewayControllerContext;
import com.elitecore.netvertex.gateway.radius.ValueProviderImpl;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;

public class RadiusMonitor extends BaseMonitor<ServiceRequest, ServiceResponse> {
	
	private final Compiler compiler;

	public RadiusMonitor(final RadiusGatewayControllerContext context) {
		super(context.getServerContext().getTaskScheduler());
		this.compiler = Compiler.getDefaultCompiler();
	}


	@Override
	public String getType() {
		return "radius";
	}
	
	public final boolean evaluate(ServiceRequest request, ServiceResponse response){
		for(MonitorExpression<ServiceRequest,ServiceResponse> expression : getExpressions()){
			if(expression.evaluate(request, response)){
				return true;
			}
		}
		
		return false;
	}
	

	@Override
	protected MonitorExpression<ServiceRequest, ServiceResponse> createMonitorExpression(String condition, long time) throws Exception {
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
		return new MonitorExpression<ServiceRequest, ServiceResponse>(expr, logMonitorInfo);
	}
	
	
	private class RadiusMonitorExpression implements Expression<ServiceRequest, ServiceResponse>{
		
		private LogicalExpression expression;
		public RadiusMonitorExpression(LogicalExpression expression) {
			this.expression = expression;
			
		}

		@Override
		public boolean evaluate(ServiceRequest request, ServiceResponse response) {
			if(request != null){
				return expression.evaluate(new ValueProviderImpl((RadServiceRequest)request));
			}
			
			return false;
		}
		
	}

}
