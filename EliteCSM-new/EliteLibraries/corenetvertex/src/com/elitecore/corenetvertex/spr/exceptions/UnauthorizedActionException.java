package com.elitecore.corenetvertex.spr.exceptions;

import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;

public class UnauthorizedActionException extends Exception {

	public UnauthorizedActionException(String message){
		super(message);
	}
	
	public UnauthorizedActionException(Exception e){
		super(e);
	}
	
	public UnauthorizedActionException(String message, Exception e){
		super(message,e);
	}

	public UnauthorizedActionException(ACLModules aclModule, ACLAction aclAction) {
		super(aclModule.getDisplayLabel() + " '" + aclAction.name() + "' is not allowed");
	}
}
