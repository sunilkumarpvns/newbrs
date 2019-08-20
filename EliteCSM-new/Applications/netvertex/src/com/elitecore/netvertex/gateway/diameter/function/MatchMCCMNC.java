package com.elitecore.netvertex.gateway.diameter.function;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingType;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.AbstractStringFunctionExpression;
import com.elitecore.netvertex.core.roaming.MCCMNCRoutingEntry;
import com.elitecore.netvertex.core.roaming.RoutingEntry;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;

public class MatchMCCMNC extends AbstractStringFunctionExpression {
	
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "MATCH-MCC-MNC";
	private transient MCCMNCRoutingConfiguration routingConfiguration;
	
	public MatchMCCMNC(MCCMNCRoutingConfiguration routingConfiguration){
		this.routingConfiguration = routingConfiguration;
	}

	@Override
	public int getFunctionType() {
		return StringFunction;
	}

	@Override
	public String getName() {
		return "matchmccmnc";
	}

	@Override
	public String getStringValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, getName() + " function called");
		
		if(routingConfiguration == null){
            throw new IllegalArgumentException("routing configuration not found");
		}
		if(argumentList.size() != 1){
			throw new IllegalArgumentException("Number of argumet mismatch. No of argument:"+ argumentList.size() +" while " + getName() +" Function has 1 arguments 1)RoutingEntryName");
		}
		
		
		List<String> subscriberMCCMNCs = valueProvider.getStringValues(DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC);
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "EC_SUBSCRIBER_MCC_MNC("+ DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC +") info AVP values: " + subscriberMCCMNCs.toString());
		
		
		String routingEntryName = argumentList.get(0).getStringValue(valueProvider);
		
		RoutingEntry routingEntry = routingConfiguration.getRoutingEntryByName(routingEntryName);
		
		if(routingEntry == null){
			throw new IllegalArgumentException("Routing Entry with name:" + routingEntryName + " not found in routing configuration");
		}
		
		if(RoutingType.MCC_MNC_BASED != routingEntry.getType()){
			throw new IllegalArgumentException("Routing Entry with name:" + routingEntryName + " is "
							+ routingEntry.getType() +" base, required MCC-MNC base routing Entry");
		}
		
		MCCMNCRoutingEntry mccmncRoutingEntry = (MCCMNCRoutingEntry) routingEntry;
		
	
		for(String mccMnc : subscriberMCCMNCs){
			if(mccmncRoutingEntry.getMCCMNCGroup().getMCCMNCEntities(mccMnc) != null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "EC_SUBSCRIBER_MCC_MNC("+ DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC +") info AVP value " + mccMnc + " found in routing entry with name: " + routingEntryName);
				return "true";
			}
			
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "EC_SUBSCRIBER_MCC_MNC("+ DiameterAVPConstants.EC_SUBSCRIBER_MCC_MNC 
					+") info AVP values not found in routing entry with name: " + routingEntryName);
		
		return "false";
	
	}

	@Override
	public long getLongValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {
		return -1;
	}

}
