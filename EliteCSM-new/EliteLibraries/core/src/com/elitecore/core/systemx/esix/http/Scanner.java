package com.elitecore.core.systemx.esix.http;

/**
 * Implement this interface when it is necessary to provide custom implementation to check
 * if external system is accessible or not. i.e. in HTTPConnector
 *
 * @author Kartik Prajapati
*/
public interface Scanner {
    boolean isAccessible(String ip, int port);
}
