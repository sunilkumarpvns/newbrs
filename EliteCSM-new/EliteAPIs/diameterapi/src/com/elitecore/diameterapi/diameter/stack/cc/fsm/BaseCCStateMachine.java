package com.elitecore.diameterapi.diameter.stack.cc.fsm;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.State;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.StateMachine;
import com.elitecore.diameterapi.core.common.peer.IPeerListener;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.enums.CCEvents;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.enums.CCState;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.states.IdleState;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.states.OpenState;
import com.elitecore.diameterapi.diameter.stack.cc.session.BaseCCAppSession;

public abstract class BaseCCStateMachine extends StateMachine implements CCActionExecutor{
	private static final String MODULE = "BS-CC-ST-MCHN";
	protected IPeerListener peerListener;
	protected BaseCCAppSession session;
	public BaseCCStateMachine(BaseCCAppSession session){		
		this(session,CCState.IDLE);
	}
	
	public BaseCCStateMachine(BaseCCAppSession session,CCState state){		
		super(state);
		this.session = session;
	}
	@Override
	protected final StateEvent createStateEvent(IStateTransitionData transitionData) {
		if(currentState == CCState.IDLE) {
			return getIdleStateEvent(transitionData);
		}else if(currentState == CCState.OPEN){			
			return getOpenStateEvent(transitionData);
		}
		return null;
	}

	@Override
	protected final List<State> createStates() {
		List<State> states = new ArrayList<State>();		
		
		/* order must be same as in CCState */
		states.add(CCState.IDLE.stateOrdinal(),new IdleState(this));
		states.add(CCState.OPEN.stateOrdinal(),new OpenState(this));
		
		return states;

	}

	
	@Override
	public void act() {
		// TODO Auto-generated method stub

	}	
	
	/**
	 * 
	 * Actions that is performed by CC State Machine:
	 * ----------------------------------------------
	 *  StartTcc
	 *	RestartTcc
	 *	StopTcc
	 *	SendCcAnswer
	 *	SendErrorMessage
	 * 
	 */
	@Override
	public final void startTcc(StateEvent event) {
		// TODO getAvp from Answer packet and the set the timer
		// For sending Abort session request and release the resources.
		
	}

	@Override
	public final void restartTcc(StateEvent event) {
		// TODO Reset above timerTask and reset		
	}

	@Override
	public final void stopTcc(StateEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
	private  StateEvent getIdleStateEvent(IStateTransitionData transitionData){
		DiameterRequest request = (DiameterRequest)transitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		Session session = (Session) transitionData.getData(PeerDataCode.USER_SESSION);
		int iCommandCode = request.getCommandCode();
		IDiameterAVP avp = request.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);		
		int iRequestType =(int) avp.getInteger();
		StateEvent stateEvent = null;
		DiameterAnswer answer  = new DiameterAnswer(request);
		if(iCommandCode == CommandCode.CREDIT_CONTROL.code ){			
		
			switch(iRequestType){
			
			case DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST:
				answer =  this.handleInitialRequest(session, request);				
				if(isSuccess(answer)){//Is successfully processed.					
					stateEvent = new StateEvent(currentState, CCEvents.CcInitialRequest,CCState.OPEN, transitionData);
				}else{//Is not successfully processed.
					stateEvent = new StateEvent(currentState, CCEvents.CcInitialRequest,CCState.IDLE, transitionData);
				}
				break;
				
			case DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST:
				answer = this.handleEventRequest(session, request);//Is successfully processed. OR not it remains in same state.
				stateEvent = new StateEvent(currentState, CCEvents.CcEventRequest,CCState.IDLE, transitionData);
				break;
			default:
				stateEvent = new StateEvent(currentState, CCEvents.UnknownEvent,CCState.IDLE,transitionData);
			}
		}else{
			stateEvent = new StateEvent(currentState, CCEvents.UnknownEvent,CCState.IDLE,transitionData);
		}
		transitionData.addObject(PeerDataCode.DIAMETER_PACKET_TO_SEND,answer);
		return stateEvent; 
		
	}
	private boolean isSuccess(DiameterAnswer answer){		
		IDiameterAVP resultCodeAvp = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
		return (resultCodeAvp != null && resultCodeAvp.getInteger() == ResultCode.DIAMETER_SUCCESS.code);
	}
	private StateEvent getOpenStateEvent(IStateTransitionData transitionData){
		DiameterRequest request = (DiameterRequest)transitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		Session session = (Session)transitionData.getData(PeerDataCode.USER_SESSION);
		DiameterAnswer answer = (DiameterAnswer)transitionData.getData(PeerDataCode.DIAMETER_PACKET_TO_SEND);
		int iCommandCode = request.getCommandCode();
		IDiameterAVP avp = request.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);		
		int iRequestType =(int) avp.getInteger();
		StateEvent stateEvent = null;
		if(iCommandCode == CommandCode.CREDIT_CONTROL.code ){			
			switch(iRequestType){
			case DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST:
				answer = this.handleUpdateRequest(session, request);				
				if(isSuccess(answer)){//Is successfully processed.
					stateEvent = new StateEvent(currentState, CCEvents.CcUpdateRequest,CCState.OPEN,transitionData);					
				}else{//Is not successfully processed.
					stateEvent = new StateEvent(currentState, CCEvents.CcUpdateRequest,CCState.IDLE, transitionData);
				}
				break;
				
			case DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST:
				answer = this.handleTerminationRequest(session, request );
				
				IDiameterAVP resultCodeAvp = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
				if(resultCodeAvp != null && resultCodeAvp.getInteger() == ResultCode.DIAMETER_SUCCESS.code ){
					if(session != null){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
							LogManager.getLogger().debug(MODULE, "Releasing Session for Session id: " + session.getSessionId());
						}
						session.release();						
					}
				}				
				
				stateEvent = new StateEvent(currentState, CCEvents.CcTerminationRequest,CCState.IDLE, transitionData);
				break;
				
			default:
				stateEvent = new StateEvent(currentState, CCEvents.UnknownEvent,CCState.OPEN,transitionData);
			}
		}else{
			stateEvent = new StateEvent(currentState, CCEvents.UnknownEvent,CCState.OPEN,transitionData);
		}
		transitionData.addObject(PeerDataCode.DIAMETER_PACKET_TO_SEND, answer);
		/*
		 * TODO CCEvents.SessionSupervisionTimerExpired;
		 * Need to check for  Session supervision timer Tcc expired
		 *  
		 */
		
		return stateEvent;
		
	}
	
	
	
	/**
	 * This method is called when CC INITIAL_REQUEST is received.
	 * 
	 * @param request The Request Packet that is received.
	 * @param answer  The Answer packet formed using request received.
	 * @return it returns TRUE if request is successfully processed 
	 * 			 And FALSE if request is not successfully processed
	 */
	public abstract DiameterAnswer handleInitialRequest(Session session, DiameterRequest request);
	
	
	/**
	 * This method is called when CC UPDATE_REQUEST is received.
	 * 
	 * @param request The Request Packet that is received.
	 * @param answer  The Answer packet formed using request received.
	 * @return it returns TRUE if request is successfully processed 
	 * 			 And FALSE if request is not successfully processed
	 */
	public abstract DiameterAnswer handleUpdateRequest(Session session, DiameterRequest request);
	

	/**
	 * This method is called when CC TERMINATION_REQUEST is received.
	 * 
	 * @param request The Request Packet that is received.
	 * @param answer  The Answer packet formed using request received.
	 */
	public abstract DiameterAnswer handleTerminationRequest(Session session, DiameterRequest request);
	
	/**
	 * This method is called when CC EVENT_REQUEST is received.
	 * 
	 * @param request The Request Packet that is received.
	 * @param answer  The Answer packet formed using request received.
	 * @return TRUE if request is successfully processed 
	 * 	    And FALSE if request is not successfully processed
	 */
	public abstract DiameterAnswer handleEventRequest(Session session, DiameterRequest request);
	/*	
	 * Events that will be executed on Event Request:
	 * ----------------------------------------------	 
	 *	-handleDirectDebitingRequest
	 *	
	 *	-handleRefundAccountRequest
	 *	
	 *	-handleCheckBalanceRequest
	 *	
	 *	-handlePriceEnquiryRequest
	 *
	 *  This Events will be executed by State-less State Machine 
	 *	
	 */

	
	
	
}
