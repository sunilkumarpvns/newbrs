/**
 * 
 */
package com.elitecore.diameterapi.diameter.stack;

import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.diameterapi.core.common.peer.exception.StatusListenerRegistrationFailException;
import com.elitecore.diameterapi.core.common.transport.VirtualConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.PacketProcess;
import com.elitecore.diameterapi.mibs.statistics.DiameterStatisticsProvider;

/**
 * @author pulindani
 *
 */
public interface IDiameterStackContext extends IStackContext{
	/**
	 * 
	 * @return Set<ApplicationEnum> the set of all supported applications' identifiers
	 */
	public Set<ApplicationEnum> getApplicationsIdentifiersList();

	public boolean validate();
	public boolean isNAIEnabled();
	public boolean isValidNAIRealm(String realm);
	
	

	public DiameterPeerState registerPeerStatusListener(String hostIdentity, DiameterPeerStatusListener listener) throws StatusListenerRegistrationFailException;
	/**
	 * 
	 * @return true if Explicit Routing (i.e. RFC 6159) is Enabled.
	 */
	public boolean isEREnabled();

	int getTotalActiveSessionCount();

	public boolean isOverLoad(DiameterRequest diameterRequest);
	

	public CDRDriver<DiameterPacket> getDiameterCDRDriver(String name) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException;
	
	DiameterStatisticsProvider getDiameterStatisticsProvider();

	/**
	 * Removes all the sessions established 
	 * with the peer identity in the origin host 
	 * 
	 * @param request this is CER or DPR
	 * @return no of sessions removed
	 */
	@Nonnegative 
	long releasePeerSessions(@Nonnull DiameterRequest request);

	public boolean isServerInitiatedMessage(int commandCode);
	
	public void submitToWorker(PacketProcess packetProcess);

	public int getMaxWorkerThreads();

	VirtualConnectionHandler registerVirtualPeer(PeerData peerData, VirtualOutputStream outpurStream)
			throws ElementRegistrationFailedException;

}
