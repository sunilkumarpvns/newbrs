package com.elitecore.netvertex.service.offlinernc.servicepolicy;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.core.RncRequestValueProvider;
import com.elitecore.netvertex.service.offlinernc.servicepolicy.handler.OfflineRnCHandler;

public class OfflineRnCServicePolicy implements ServicePolicy<RnCRequest> {

	private static final String MODULE = "OFFLINE-RNC-SRV-PLCY";
	private LogicalExpression expression;
	private String ruleset;
	private List<OfflineRnCHandler> handlers = new ArrayList<>();
	
	public OfflineRnCServicePolicy(String ruleset) {
		this.ruleset = ruleset;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initializing Offline RnC service policy: " + getPolicyName());

		initRuleset();
	}

	private void initRuleset() throws InitializationFailedException {
		try {
			Compiler compiler = Compiler.getDefaultCompiler();
			if (Strings.isNullOrBlank(ruleset) == false) {
				expression = compiler.parseLogicalExpression(ruleset);
			} else {
				expression = compiler.parseLogicalExpression("\"1\" = \"1\"");
			}
		} catch(InvalidExpressionException e) {
			throw new InitializationFailedException(ruleset+" is "+e.getMessage() , e);
		}
	}

	@Override
	public boolean assignRequest(RnCRequest request) {
	
		boolean servicePolicyApplied;

		servicePolicyApplied = expression.evaluate(new RncRequestValueProvider(request));

		if (LogManager.getLogger().isDebugLogLevel()) {
			request.getTraceWriter().println();
			request.getTraceWriter().incrementIndentation();
			request.getTraceWriter().print("Applying Service Policy : " + getPolicyName() + ". Result: " + servicePolicyApplied);
			request.getTraceWriter().decrementIndentation();
		}
		return servicePolicyApplied;
	}
	
	public void addHandler(OfflineRnCHandler handler) {
		handlers.add(handler);
	}
	
	public void handle(RnCRequest request, RnCResponse response, PacketOutputStream out) throws Exception {
		for (OfflineRnCHandler handler : handlers) {
			if (handler.isEligible(request) == false) {
				continue;
			}
			
			try {
				handler.handleRequest(request, response, out);
			} catch (OfflineRnCException ex) {
				request.getTraceWriter().println();
				request.getTraceWriter().incrementIndentation();
				request.getTraceWriter().print("Error in handling EDR, SerialNumber: " 
						+ request.getSerialNumber() + ", Reason: " + ex.getMessage());
				request.getTraceWriter().decrementIndentation();
				LogManager.getLogger().trace(MODULE, ex);
				response.setError(ex);
				out.writeError(request, response);
				break;
			}
		}
	}

	@Override
	public String getPolicyName() {
		return "DefaultPolicy";
	}
	
}
