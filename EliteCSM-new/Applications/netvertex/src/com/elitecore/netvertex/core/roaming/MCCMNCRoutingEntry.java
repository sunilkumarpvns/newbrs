package com.elitecore.netvertex.core.roaming;

import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingType;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

import java.util.Map;
import java.util.Set;

public class MCCMNCRoutingEntry extends RoutingEntry{
	private MCCMNCGroup mccmncGroup;
	private String expression;

	public MCCMNCRoutingEntry(String name, MCCMNCGroup mccmncGroup, RoutingActions routingAction, Map<String, Integer> gatewayNames, boolean isRoaming) {
		super(name, routingAction, gatewayNames,isRoaming);
		this.mccmncGroup = mccmncGroup;
		this.expression = "matchmccmnc(\""+ name +"\")=\"true\"";
	}
	
	
	public MCCMNCGroup getMCCMNCGroup() {
		return mccmncGroup;
	}

	@Override
	public RoutingType getType() {
		return RoutingType.MCC_MNC_BASED;
	}
	
	@Override
	public boolean equals(Object routingEntry){
		
		if(this == routingEntry){
			return true;
		}
			
		
		if(routingEntry == null){
			return false;
		}
		
		if(!(routingEntry instanceof MCCMNCRoutingEntry)){
			return false;
		}
		
		MCCMNCRoutingEntry mccmncRoutingEntry = (MCCMNCRoutingEntry) routingEntry;
		
		return mccmncGroup.equals(mccmncRoutingEntry.mccmncGroup);
			
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mccmncGroup == null) ? 0 : mccmncGroup.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading("  --- MCC-MNC Routing Entry --- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public String getExpression() {
		return expression;
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.incrementIndentation()
				.append("Name", name)
				.append("Type", getType())
				.appendChildObject("MCC-MNC Group", mccmncGroup)
				.append("Expression", expression)
				.append("Routing Action", routingAction)
				.append("Is Roaming", isRoaming());
		Set<String> gatewayNames = getGatewayNames();
		builder.appendHeading("Gateway Entries =");
		builder.incrementIndentation();
		for (String gatewayName : gatewayNames) {
			builder.append(gatewayName, getGatewayWeightage(gatewayName));
		}
		builder.decrementIndentation();
	}
}
