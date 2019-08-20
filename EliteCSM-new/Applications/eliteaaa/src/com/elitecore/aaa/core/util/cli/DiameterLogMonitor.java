package com.elitecore.aaa.core.util.cli;

import java.util.Date;

import javax.annotation.Nonnull;

import com.elitecore.core.logmonitor.LogMonitorInfo;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.cli.cmd.logmonitorext.Expression;
import com.elitecore.core.util.cli.cmd.logmonitorext.Monitor;
import com.elitecore.core.util.cli.cmd.logmonitorext.MonitorExpression;
import com.elitecore.core.util.cli.cmd.logmonitorext.impl.BaseMonitor;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

/**
 * Provides log monitoring ability for diameter packet by evaluating expression provided
 * from user interface
 * 
 * @author narendra.pathai
 *
 */
public class DiameterLogMonitor extends BaseMonitor<DiameterPacket, Void> {

	private final Compiler compiler;
	
	public DiameterLogMonitor(@Nonnull TaskScheduler taskScheduler) {
		super(taskScheduler);
		compiler = Compiler.getDefaultCompiler();
	}

	@Override
	public String getType() {
		return "diameter";
	}

	public final boolean evaluate(DiameterPacket packet, Void dontCare){
		for (MonitorExpression<DiameterPacket, Void> expression : getExpressions()) {
			if (expression.evaluate(packet, dontCare)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected MonitorExpression<DiameterPacket, Void> createMonitorExpression(
			String condition, long time) throws Exception {
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
		return new MonitorExpression<DiameterPacket, Void> (expr, logMonitorInfo);
	}

	private class DiameterMonitorExpression implements Expression<DiameterPacket, Void> {
		
		private LogicalExpression expression;
		public DiameterMonitorExpression(LogicalExpression expression) {
			this.expression = expression;
		}

		@Override
		public boolean evaluate(DiameterPacket packet, Void response) {
			if (packet != null) {
				return expression.evaluate(new DiameterAVPValueProvider(packet));
			}
			
			return false;
		}
	}
}
