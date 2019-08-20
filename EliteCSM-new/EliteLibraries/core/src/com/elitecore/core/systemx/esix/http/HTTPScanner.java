package com.elitecore.core.systemx.esix.http;

import java.net.Socket;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Checks whether machine at given ip is listening to desired port or not.
 *
 * @author Kartik Prajapati
 */
public class HTTPScanner implements Scanner {
    private static final String MODULE = "HTTP-SCANNER";
    public boolean isAccessible(String ip, int port){
        try(Socket s = new Socket(ip, port))
        {
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE,"http://"+ip+":"+port+" is accessible");
            }
            return true;
        } catch (Exception e) {
            getLogger().error(MODULE, "Server is not listening. Reason: http://"+ip+":"+port+" is not accessible");
            getLogger().trace(e);
            return false;
        }
    }
}
