package com.elitecore.netvertex.core.session;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.core.NetVertexServerContext;

public class NetvertexSessionManagerFactory {

    public NetvertexSessionManager create(NetVertexServerContext serverContext, SessionDao sessionDao) throws InitializationFailedException {
        NetvertexSessionManager netvertexSessionManager = new NetvertexSessionManager(serverContext, sessionDao);
        netvertexSessionManager.init();
        return netvertexSessionManager;
    }
}
