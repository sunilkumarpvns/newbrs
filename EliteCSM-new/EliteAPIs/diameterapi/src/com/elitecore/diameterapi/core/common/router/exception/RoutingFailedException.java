package com.elitecore.diameterapi.core.common.router.exception;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

public class RoutingFailedException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private ResultCode resultCode = ResultCode.DIAMETER_UNABLE_TO_COMPLY;
	private RoutingActions routeAction;
	transient private DiameterPacket diameterPacket;
	
	public RoutingFailedException(ResultCode resultCode, RoutingActions routingActions, DiameterPacket diameterPacket, String message) {
		this(resultCode,routingActions,message);
		this.diameterPacket = diameterPacket;
	}
	
	
	public RoutingFailedException(RoutingActions routingAction, String message) {
		super(message);
		this.routeAction = routingAction;
	}
	
	public RoutingFailedException(ResultCode resultCode, RoutingActions routingAction, String message) {
		super(message);
		this.resultCode = resultCode;
		this.routeAction = routingAction;
	}
	
	
	public RoutingFailedException(ResultCode resultCode, RoutingActions routingAction) {
		super();
		this.resultCode = resultCode;
		this.routeAction = routingAction;
	}

	public ResultCode getResultCode(){
		return resultCode;
	}
	
	public RoutingActions getRoutingAction(){
		return routeAction;
	}

	public DiameterPacket getDiameterPacket() {
		return diameterPacket;
	}
}
