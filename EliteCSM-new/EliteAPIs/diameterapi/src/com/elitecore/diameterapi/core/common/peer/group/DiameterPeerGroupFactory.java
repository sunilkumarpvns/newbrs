package com.elitecore.diameterapi.core.common.peer.group;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.exception.PeerNotFoundException;
import com.elitecore.diameterapi.core.stack.IStackContext;

/**
 * Created by harsh on 6/17/15.
 */
public class DiameterPeerGroupFactory {
    private final GroupStore groupStore;
	private final IStackContext stackContext;

    public DiameterPeerGroupFactory(IStackContext stackContext) {
        this.stackContext = stackContext;
		this.groupStore = new GroupStore();
    }

    public DiameterPeerCommGroup getInstance(DiameterPeerGroupParameter diameterPeerGroupParameter) throws PeerNotFoundException{
        DiameterPeerCommGroup group = groupStore.get(diameterPeerGroupParameter);
        if(group == null) {
        	synchronized (this) {
        		group = groupStore.get(diameterPeerGroupParameter);
        		if(group == null) {
        			group = createInstance(diameterPeerGroupParameter);
        			groupStore.store(diameterPeerGroupParameter, group);
        		}
        	}
        }
        return group;
    }

    protected synchronized  DiameterPeerCommGroup createInstance(DiameterPeerGroupParameter diameterPeerGroupParameter) throws PeerNotFoundException {
    	Preconditions.checkArgument(Maps.isNullOrEmpty(diameterPeerGroupParameter.getPeers()) == false, 
    			"Peer list not found from group configuration");
        
    	DefaultDiameterPeerGroup diameterPeerCommGroup = create(diameterPeerGroupParameter);

        for(Map.Entry<String,Integer> peerNameEntry : diameterPeerGroupParameter.getPeers().entrySet()) {
            DiameterPeerCommunicator diameterPeerCommunicator = stackContext.getPeerCommunicator(peerNameEntry.getKey());

            if(diameterPeerCommunicator == null) {
                throw new PeerNotFoundException("Peer:" + peerNameEntry.getKey() +" not found");
            }

            diameterPeerCommGroup.addCommunicator(diameterPeerCommunicator, peerNameEntry.getValue());
        }

        return diameterPeerCommGroup;
    }

	protected DefaultDiameterPeerGroup create(DiameterPeerGroupParameter diameterPeerGroupParameter) {
		DefaultDiameterPeerGroup diameterPeerCommGroup = new DefaultDiameterPeerGroup(stackContext, diameterPeerGroupParameter.getName(),
                diameterPeerGroupParameter.getMaxRetryCount(),
                diameterPeerGroupParameter.getTransactionTimeout(),
                diameterPeerGroupParameter.isStateFull());

		return diameterPeerCommGroup;
	}



    private static class GroupStore {

        private final Map<String, DiameterPeerCommGroup> diameterPeerCommunicators = new IdentityHashMap<String, DiameterPeerCommGroup>();
        private final Lock readLock;
        private final Lock writeLock;

        public GroupStore() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            readLock = lock.readLock();
            writeLock = lock.writeLock();
        }

        private DiameterPeerCommGroup get(DiameterPeerGroupParameter parameter){
            readLock.lock();
            try {
                return diameterPeerCommunicators.get(parameter.getName());
            }finally {
                readLock.unlock();
            }
        }

        private void store(DiameterPeerGroupParameter diameterPeerGroupParameter, DiameterPeerCommGroup diameterPeerCommGroup) {
            writeLock.lock();
            try{
                diameterPeerCommunicators.put(diameterPeerGroupParameter.getName(), diameterPeerCommGroup);
            }finally {
                writeLock.unlock();
            }

        }
    }


}
