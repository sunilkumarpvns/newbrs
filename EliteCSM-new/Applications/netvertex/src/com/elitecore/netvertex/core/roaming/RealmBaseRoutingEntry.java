package com.elitecore.netvertex.core.roaming;

import com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingType;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

import java.util.Map;
import java.util.Set;

public class RealmBaseRoutingEntry extends RoutingEntry{
	private String expression;

	public RealmBaseRoutingEntry(String name, String expression,RoutingActions routingAction, Map<String, Integer> gatewayWeightageMap, boolean isRoaming) {
		super(name, routingAction, gatewayWeightageMap,isRoaming);
		this.expression = expression;
		
	}
	
	@Override
	public String getExpression(){
		return expression;
	}

	@Override
	public RoutingType getType() {
		return RoutingType.CUSTOM_REALM_BASED;
	}
	
	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" 	--- Realm-Base Routing Entry ---");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("Expression", expression)
				.append("Is Roaming", isRoaming())
				.append("Routing Action", routingAction);
		Set<String> gatewayNames = getGatewayNames();
		builder.appendHeading("Gateway Entries");
		builder.incrementIndentation();
		for (String gatewayName : gatewayNames) {
			builder.append(gatewayName, getGatewayWeightage(gatewayName));
		}
		builder.decrementIndentation();
	}
}
