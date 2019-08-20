package com.elitecore.corenetvertex;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class GatewayStatusInfo implements ToStringable {

    private String communicationProtocol;
    private String gatewayIdentity;
    private String status;

    public GatewayStatusInfo(String communicationProtocol, String gatewayIdentity, String status) {
        this.communicationProtocol = communicationProtocol;
        this.gatewayIdentity = gatewayIdentity;
        this.status = status;
    }

	public String getCommunicationProtocol() {
		return communicationProtocol;
	}

	public String getName() {
        return gatewayIdentity;
    }

    public void setName(String name) {
        this.gatewayIdentity = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Gateway Status Information -- ");
        toString(builder);
        return builder.toString();
    }


    @Override
    public void toString(IndentingToStringBuilder out) {
        out.incrementIndentation();
        out.append("Communication Protocol", communicationProtocol);
        out.append("Gateway Identity", gatewayIdentity);
        out.append("Status", status);
        out.decrementIndentation();
    }
}
