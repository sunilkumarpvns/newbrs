package com.elitecore.netvertex.gateway.diameter.logmonitor;

import com.elitecore.core.logmonitor.LogMonitorInfo;
import com.elitecore.core.util.cli.cmd.logmonitorext.Expression;
import com.elitecore.core.util.cli.cmd.logmonitorext.Monitor;
import com.elitecore.core.util.cli.cmd.logmonitorext.MonitorExpression;
import com.elitecore.core.util.cli.cmd.logmonitorext.impl.BaseMonitor;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.diameter.utility.DiameterValueProvider;

import java.util.Date;

public class DiameterMonitor extends BaseMonitor<DiameterRequest,DiameterAnswer> {
	private final Compiler compiler;

	public DiameterMonitor(final NetVertexServerContext serverContext) {
		super(serverContext.getTaskScheduler());
		this.compiler = Compiler.getDefaultCompiler();
	}

	@Override
	public String getType() {
		return "diameter";
	}
	
	public final boolean evaluate(DiameterPacket diameterPacket){
		
		DiameterRequest diameterRequest = null;
		DiameterAnswer diameterAnswer = null;
		
		if(diameterPacket.isRequest()){
			diameterRequest = (DiameterRequest) diameterPacket;
		} else {
			diameterAnswer = (DiameterAnswer) diameterPacket;
		}
		
		for(MonitorExpression<DiameterRequest,DiameterAnswer> expression : getExpressions()){
			
			if(expression.evaluate(diameterRequest, diameterAnswer)){
				return true;
			}
		}
		
		return false;
		
	}


	@Override
	protected MonitorExpression<DiameterRequest,DiameterAnswer> createMonitorExpression(String condition, long time) throws Exception {
		LogicalExpression expression = compiler.parseLogicalExpression(condition); 
		
		DiameterMonitorExpression expr = new DiameterMonitorExpression(expression);
		
		Date startDate = new Date();
		long expiryTime = 0;
		if(time <= 0){
			expiryTime = Monitor.NO_TIME_LIMIT;
		} else {
			expiryTime = startDate.getTime() + (time * 60 *  1000);
		}
		LogMonitorInfo logMonitorInfo = new LogMonitorInfo(condition, startDate.getTime(), time, expiryTime);
		return new MonitorExpression<DiameterRequest, DiameterAnswer>(expr, logMonitorInfo);
	}
	
	
	private class DiameterMonitorExpression implements Expression<DiameterRequest, DiameterAnswer>{
		
		private LogicalExpression expression;
		public DiameterMonitorExpression(LogicalExpression expression) {
			this.expression = expression;
			
		}

		@Override
		public boolean evaluate(DiameterRequest requst, DiameterAnswer response) {
			return expression.evaluate(new DiameterValueProvider(requst, response));
		}
		
	}
}
