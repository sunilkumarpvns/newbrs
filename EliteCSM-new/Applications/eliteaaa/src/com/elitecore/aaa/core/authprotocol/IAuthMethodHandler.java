package com.elitecore.aaa.core.authprotocol;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;


public interface IAuthMethodHandler extends ReInitializable{
	public void init() throws InitializationFailedException;
	
}
