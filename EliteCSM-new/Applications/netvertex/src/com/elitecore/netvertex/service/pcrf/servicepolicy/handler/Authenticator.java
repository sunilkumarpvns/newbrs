package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception.AuthenticationFailedException;

/**
 * Created by kirpalsinh on 19/10/16.
 */
public interface Authenticator {
    public void authenticate(PCRFRequest request) throws AuthenticationFailedException ;
}
