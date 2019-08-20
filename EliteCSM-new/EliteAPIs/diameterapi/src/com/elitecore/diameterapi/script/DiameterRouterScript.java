package com.elitecore.diameterapi.script;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.script.manager.DiameterScriptContext;

/**
 * This is a Routing Groovy Base Class.
 * Any Pre/Post Routing Operation can be performed.
 * 
 * @author monica.lulla
 *
 */
public abstract class DiameterRouterScript {

	
	protected DiameterScriptContext scriptContext;

	public DiameterRouterScript(DiameterScriptContext scriptContext){
		this.scriptContext = scriptContext; 
	}
	
	public final void init() throws InitializationFailedException {
		
		initGroovy();
	}
	
	public final void preRequest(String routingTableName, DiameterRequest originRequest, String reqOriginatorPeerId){
		try{
			preRequestRouting(routingTableName, originRequest, reqOriginatorPeerId);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
			LogManager.getLogger().error(getName(), "Error in executing \"preRequest\" method of routing script: " + getName() + 
					". Reason: " + ex.getMessage());
		}
	}
	
	public final void postRequest(String routingTableName, String routingEntryName, 
			DiameterRequest originRequest, String reqOriginatorPeerId, DiameterRequest destinationRequest, String destPeerId){
		try{
			postRequestRouting(routingTableName, routingEntryName, originRequest, reqOriginatorPeerId, destinationRequest, destPeerId);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
			LogManager.getLogger().error(getName(), "Error in executing \"postRequest\" method of routing script: " + getName() + 
					". Reason: " + ex.getMessage());
		}
	}
	
	public final void preAnswer (String routingTableName, String routingEntryName, 
			DiameterRequest originRequest, String ansOriginitorPeerId, DiameterRequest destinationRequest, DiameterAnswer originAnswer){
		try{
			preAnswerRouting(routingTableName, routingEntryName, originRequest, ansOriginitorPeerId, destinationRequest, originAnswer);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
			LogManager.getLogger().error(getName(), "Error in executing \"preAnswer\" method of routing script: " + getName() + 
					". Reason: " + ex.getMessage());
		}
	}
	
	public final void postAnswer (String routingTableName, String routingEntryName, 
			DiameterRequest originRequest, String ansOriginatorPeerId, 
			DiameterRequest destinationRequest, DiameterAnswer originAnswer, DiameterAnswer destinationAnswer, String destAnsPeerId){
		try{
			postAnswerRouting(routingTableName, routingEntryName, originRequest, ansOriginatorPeerId, 
					destinationRequest, originAnswer, destinationAnswer, destAnsPeerId);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
			LogManager.getLogger().error(getName(), "Error in executing \"postAnswer\" method of routing script: " + getName() + 
					". Reason: " + ex.getMessage());
		}
	}
	
	/**
	 * This is the most important method. This method identifies the script in the EliteDSC Framework.
	 * The same name should be used anywhere to configure the script.
	 * <br/>
	 * <b>NOTE: Do not keep the name null or blank. Also name is case-sensitive </b>
	 * @return the name of the script for identifying the script in EliteDSC Framework. 
	 */
	protected abstract String getName();

	/**
	 * This hook is available for Initialization tasks are to be performed.
	 * 
	 * @throws InitializationFailedException when any error occurs during Initialization.
	 */
	public abstract void initGroovy() throws InitializationFailedException;
	
	/**
	 * This hook is available for processing the Request arrived from Originator
	 * @param originRequest is the request from Originator
	 * @param requestOriginatorPeerId is Originator Peer Host Identity
	 */
	protected abstract void preRequestRouting(String routingTableName, DiameterRequest originRequest, String requestOriginatorPeerId);
	
	/**
	 * This hook is available for processing request just before routing to Destination Peer
	 * @param routingEntryName is the Routing Entry which is selected.
	 * @param originRequest is the request from Originator
	 * @param reqOriginatorPeerId is the Originator Peer Host Identity
	 * @param destinationRequest is the request sent to Destined Peer
	 * @param destPeerId is Host Identity of Peer to which the Request is to be Sent. 
	 * 
	 */
	protected abstract void postRequestRouting(String routingTableName, String routingEntryName, 
			DiameterRequest originRequest, String reqOriginatorPeerId, DiameterRequest destinationRequest, String destPeerId);
	
	/**
	 * This hook is available for processing the Answer arrived form Destined Peer
	 * @param routingEntryName is the Routing Entry which is selected.
	 * @param originRequest is the Request that arrived from Originator Peer
	 * @param ansOriginatorPeerId is the Peer which sent originAnswer
	 * @param destinationRequest is the Request that was sent to Destined Peer 
	 * @param originAnswer is the Answer that Arrived from Destined Peer
	 */
	protected abstract void preAnswerRouting(String routingTableName, String routingEntryName, 
			DiameterRequest originRequest, String ansOriginatorPeerId, DiameterRequest destinationRequest, DiameterAnswer originAnswer);
	
	/**
	 * This hook is available for processing before routing the Answer to Originator.
	 * @param routingEntryName is the Routing Entry which is selected.
	 * @param originRequest is the Request that arrived from Originator Peer
	 * @param ansOriginitorPeerId is the Peer which sent originAnswer
	 * @param destinationRequest is the Request that was sent to Destined Peer 
	 * @param originAnswer is the Answer that Arrived from Destined Peer
	 * @param destinationAnswer is the Answer to be Sent to the Originator
	 * @param destAnsPeerId is the Peer to which the Answer is to be Sent, i.e. the Peer from which Origin Request arrived.
	 */
	protected abstract void postAnswerRouting(String routingTableName, String routingEntryName, 
			DiameterRequest originRequest, String ansOriginitorPeerId, 
			DiameterRequest destinationRequest, DiameterAnswer originAnswer, DiameterAnswer destinationAnswer, String destAnsPeerId);
	
}
