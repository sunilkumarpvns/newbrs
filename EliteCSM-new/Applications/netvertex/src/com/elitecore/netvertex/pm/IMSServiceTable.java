package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.IMSServiceAction;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.imspackage.MediaType;
import com.elitecore.corenetvertex.pm.pkg.imspackage.PCCAttributeTableEntry;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class IMSServiceTable extends com.elitecore.corenetvertex.pm.pkg.imspackage.IMSServiceTable{
	
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "SERV-TABLE";
	
	public IMSServiceTable(String name, MediaType serviceType, String afAppId, LogicalExpression logicalExpression, String expressionStr,
			IMSServiceAction action, Map<PCCAttribute, List<PCCAttributeTableEntry>> pccAttributeToTableEntries) {
		super(name, serviceType, afAppId, logicalExpression, expressionStr, action, pccAttributeToTableEntries);
	}

	public boolean apply(MediaComponent mediaComponent,
						 PCRFResponse pcrfResponse,
						 ValueProvider valueProvider) {
		
		
		if(getAfAppId() != null) {
			if(mediaComponent.getAfIdentifier() == null) {
				if(getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Skipping service entry:" + getName() + ". Reason: AF app id expected:" + getAfAppId() + " while not reqceved in media conpoent");
				return false;				
			}

			if(mediaComponent.getAfIdentifier().equalsIgnoreCase(getAfAppId()) == false) {
				if(getLogger().isDebugLogLevel())
					getLogger().debug(MODULE, "Skipping service entry:" + getName() + ". Reason: AF app id does not match, expected:" + getAfAppId() + " actual:" + mediaComponent.getAfIdentifier());
				return false;
			}
		}
		
		if(getLogicalExpression() != null && getLogicalExpression().evaluate(valueProvider) == false) {
			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Skipping service entry:" + getName() + ". Reason: Addition condition("+getExpressionStr()+") fail");
			return false;
		}
		
		
		if(getAction() == IMSServiceAction.REJECT) {
			mediaComponent.setResultCode(PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
			return true;
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Service entry: " + getName() + " statisfied for service: " + mediaComponent.getMediaType());
		}
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Applying service entry:" + getName());
		
	
		PCCRule pccRule = mediaComponent.newPCC(pcrfResponse,getServiceType());
		if(getPccAttributeEntries() != null && getPccAttributeEntries().isEmpty() == false) {
			for(Entry<PCCAttribute, List<PCCAttributeTableEntry>> serviceTableEntries : getPccAttributeEntries().entrySet()) {
				boolean isStatisfied = false;
				List<PCCAttributeTableEntry> pccAttributeTableEntriess = serviceTableEntries.getValue();
				for(int pccAttributeTableEntryIndex=0; pccAttributeTableEntryIndex < pccAttributeTableEntriess.size(); pccAttributeTableEntryIndex++) {
					if(((com.elitecore.netvertex.pm.PCCAttributeTableEntry) pccAttributeTableEntriess.get(pccAttributeTableEntryIndex)).apply(pccRule, valueProvider)) {
						isStatisfied = true;
						break;
					}
				}
				
				if(isStatisfied == false) {
					if(getLogger().isDebugLogLevel())
						getLogger().debug(MODULE, "Applying action:" + PCCRuleAttributeAction.STANDARD.val 
									+ " for attribute: " + serviceTableEntries.getKey() 
									+". Reason: no entry satisfied configured in service table:" + getName());
				}
				
			}
		} else {
			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Applying action:" + PCCRuleAttributeAction.STANDARD.val 
						+ " for all attributes. Reason: no entry configured in service table:" + getName());
		}
		
		mediaComponent.setPCCRule(pccRule);
		
		return true;
	}
	
}
