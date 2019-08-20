package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;

import javax.annotation.Nullable;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class FailedUMBaseQoSProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.FailedUMBaseQoSProfileDetail  implements QoSProfileDetail{

	
	public FailedUMBaseQoSProfileDetail(String name, String packageName, QoSProfileAction action, String reason, int fupLevel, int orderNo, @Nullable String redirectURL) {
		super(name, packageName, action, reason, fupLevel, orderNo, redirectURL);
	}

	public FailedUMBaseQoSProfileDetail(String name, String pkgName, QoSProfileAction action, String reason, int fupLevel, IPCANQoS sessionQoS,
			List<PCCRule> pccRules, boolean usageMonitoring, SliceInformation sliceInformation, @Nullable String redirectURL,List<ChargingRuleBaseName> chargingRuleBaseNames) {
		super(name, pkgName, action, reason, fupLevel, sessionQoS, pccRules, usageMonitoring, sliceInformation, redirectURL, chargingRuleBaseNames);
	}

	public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, SelectionResult previousQoSResult) {
		if(getLogger().isInfoLogLevel()) {
			policyContext.getTraceWriter().println("FUP level:" + getFUPLevel());
			policyContext.getTraceWriter().incrementIndentation();
			policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue+"(Fup level:" + getFUPLevel() +" quota not defined)");
			policyContext.getTraceWriter().decrementIndentation();
		}
		
		return SelectionResult.NOT_APPLIED;
	}


}
