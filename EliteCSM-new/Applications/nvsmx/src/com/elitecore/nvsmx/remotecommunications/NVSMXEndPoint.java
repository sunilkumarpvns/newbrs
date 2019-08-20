package com.elitecore.nvsmx.remotecommunications;

/**
 * Created by aditya on 6/24/17.
 */
public interface NVSMXEndPoint extends EndPoint {

    public void markShutDown();
    public void markStarted();

    public String getStatus();
}
