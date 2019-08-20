package com.elitecore.core.systemx.esix.http;

import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;

import javax.annotation.Nullable;

/**
 * ESCommunication group implementation for the HTTP end point group
 *
 * @author Kartik Prajapati
 */

public class HTTPConnectorGroupImpl extends ESCommunicatorGroupImpl<HTTPConnector> {

    /**
     * <PRE>
     * 		calls submit method of the alive EndPoint HTTPConnector
     * </PRE>
     *
     * @throws CommunicationException
     *             in case of a problem, the server connection was aborted or encoding problem
     * @param method Server information and arguments
     */
    public @Nullable Object submit(RemoteMethod method) throws CommunicationException {
        HTTPConnector connector = getCommunicator();

        if(connector==null){
            throw new CommunicationException("No action was taken on submit call Reason: No alive HTTPConnector was " +
                    "found in group or group is empty.");
        }

        return connector.submit(method);
    }
}
