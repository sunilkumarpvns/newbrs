package com.elitecore.diameterapi.mibs.custom.utility;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.mibs.statistics.ApplicationStatsIdentifier;

/**
 * <p>
 * This class is used by DIAMETER_STACK_MIB only.
 * It is used to decide the type of the entry based
 * on the events generated while updation of the 
 * diameter statistics.
 *  
 * @author sanjay.dhamelia
 */
public class DiameterStatisticsEvents {

	public static final int APPLICATION_TO_PEER = 1;
	public static final int APPLICATION_TO_PEER_CC_WISE = 2;
	public static final int APPLICATION_TO_PEER_RC_WISE = 3;
	
	@Nonnull private ApplicationStatsIdentifier applicationIdentifier;
	@Nonnull private String peerIdentity;
	private int commandCode;
	private final int type;
	private long resultCode;

	/**
	 * This constructor is used to create the entry in applicationWiseStatisticsTable
	 * 
	 * @param applicationIdentifier - identity of the application
	 * @param peerIdentity - peer identity
	 */
	public DiameterStatisticsEvents(@Nonnull ApplicationStatsIdentifier applicationIdentifier, 
			@Nonnull String peerIdentity) {
		
		this.applicationIdentifier = checkNotNull(applicationIdentifier, "Application Idenetifier is null.");
		this.peerIdentity = checkNotNull(peerIdentity, "peerIdentity is null.");
		this.type = APPLICATION_TO_PEER;
	}
	
	/**
	 * This constructor is used to create the entry in commandCodeWiseStatisticsTable
	 * 
	 * @param applicationIdentifier - identity of the application
	 * @param peerIdentity - peer identity
	 * @param commandCode - command code
	 */
	public DiameterStatisticsEvents(@Nonnull ApplicationStatsIdentifier applicationIdentifier, 
			@Nonnull String peerIdentity, int commandCode) {
		
		this.applicationIdentifier = checkNotNull(applicationIdentifier, "Application Idenetifier is null.");
		this.peerIdentity = checkNotNull(peerIdentity, "peerIdentity is null.");
		this.commandCode = commandCode;
		this.type = APPLICATION_TO_PEER_CC_WISE;
	}
	
	/**
	 * This constructor is used to create the entry in resultCodeWiseStatisticsTable
	 * 
	 * @param applicationIdentifier - identity of the application
	 * @param peerIdentity - peer identity
	 * @param resultCode - result code
	 */
	public DiameterStatisticsEvents(@Nonnull ApplicationStatsIdentifier applicationIdentifier, 
			@Nonnull String peerIdentity, long resultCode) {
		
		this.applicationIdentifier = checkNotNull(applicationIdentifier, "Application Idenetifier is null.");
		this.peerIdentity = checkNotNull(peerIdentity, "peerIdentity is null.");
		this.resultCode = resultCode;
		this.type = APPLICATION_TO_PEER_RC_WISE;
	}
	
	/**
	 * Specifies the type of the entry
	 */
	public int getType() {
		return type;
	}
	
	public ApplicationStatsIdentifier getApplicationIdentifier() {
		return applicationIdentifier;
	}
	
	public int getCommandCode() {
		return commandCode;
	}
	
	public String getPeerIdentity() {
		return peerIdentity;
	}
	
	public long getResultCode() {
		return resultCode;
	}
}