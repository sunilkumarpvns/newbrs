package com.elitecore.core.system.comm;

public interface ILocalResponseListener {

	public void responseReceived( byte[] response);
    public void requestDropped(byte[] request);
    public void requestTimedout(byte[] request);


}
