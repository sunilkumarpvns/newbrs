package com.elitecore.core.systemx.esix;

import com.elitecore.core.systemx.esix.http.RemoteMethod;

/**
 * Created by Kartik on 27/10/17.
 */
public interface EndPoint extends ESCommunicator{

    Object submit(RemoteMethod method) throws CommunicationException;
}
