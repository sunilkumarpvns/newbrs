package com.elitecore.diameterapi.diameter.common.fsm.peer.enums;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

public enum DiameterPeerState implements IStateEnum{
	
	// Don't change the order of following statement for Creating Enum instances. 
	
	Closed(true),
	Wait_Conn_Ack(true),
	Wait_I_CEA(true),
	Elect(true),
	Wait_Returns(true),
	R_Open(false),
	I_Open(false),
	Closing(true),
	Wait_Conn_Ack_Elect(true),
	;
	
	
	
	
	public final boolean isSync;
	
	private static Map<Integer, DiameterPeerState> diameterPeerStates = new HashMap<Integer, DiameterPeerState>();
	
	static{
		for(DiameterPeerState state : values()){
			diameterPeerStates.put(state.ordinal(), state);
		}
	}

	DiameterPeerState(boolean isSync) {
		this.isSync = isSync;
	}
	
	public IStateEnum getNextState(IEventEnum event) {
		return getNextState(this, event);
	}

	public static DiameterPeerState getNextState(IStateEnum state, IEventEnum event) {
		if(state == Closed) {
			if(event == DiameterPeerEvent.Start) {
				return Wait_Conn_Ack;
			}else if(event == DiameterPeerEvent.RConnCER) {
				return R_Open;
			}else {
				return null;
			}
		}else if(state == R_Open) {
			if(event == DiameterPeerEvent.SendMessage) {
				return R_Open;
			}else if(event == DiameterPeerEvent.RrcvMessage) {
				return R_Open;
			}else if(event == DiameterPeerEvent.RRcvDWR) {
				return R_Open;
			}else if(event == DiameterPeerEvent.RRcvDWA) {
				return R_Open;
			}else if(event == DiameterPeerEvent.RConnCER) {
				return R_Open;
			}else if(event == DiameterPeerEvent.Stop) {
				return Closing;
			}else if(event == DiameterPeerEvent.RRcvDPR) {
				return Closed;
			}else if(event == DiameterPeerEvent.RPeerDisc) {
				return Closed;
			}else if(event == DiameterPeerEvent.RRcvCER) {
				return R_Open;
			}else if(event == DiameterPeerEvent.RRcvCEA) {
				return R_Open;
			}else if(event == DiameterPeerEvent.IPeerDisc) {
				return Closed;
			}else {
				return null;
			}
		}else if(state == Closing) {
			if(event == DiameterPeerEvent.IRcvDPA) {
				return Closed;
			}else if(event == DiameterPeerEvent.RRcvDPA) {
				return Closed;
			}else if(event == DiameterPeerEvent.Timeout) {
				return Closed;
			}else if(event == DiameterPeerEvent.IPeerDisc) {
				return Closed;
			}else if(event == DiameterPeerEvent.RPeerDisc) {
				return Closed;
			}else if(event == DiameterPeerEvent.Start) {
				return Closed;
			}else if(event == DiameterPeerEvent.Stop) {
				return Closed;
			}else {
				return null;
			}
		}else if(state == Wait_Conn_Ack_Elect) {
			if(event == DiameterPeerEvent.IRcvConnAck) {
				return Wait_I_CEA;
			}else if(event == DiameterPeerEvent.IRcvConnNack) {
				return Closed;
			}else if(event == DiameterPeerEvent.RConnCER) {
				return Wait_Conn_Ack_Elect;
			}else if(event == DiameterPeerEvent.Timeout) {
				return Closed;
			}else if(event == DiameterPeerEvent.Start) {
				return Closed;
			}else if(event == DiameterPeerEvent.IPeerDisc) {
				return Closed;
			}else if(event == DiameterPeerEvent.RPeerDisc) {
				return Closed;
			}else {
				return null;
			}
			
		}else if(state == Wait_I_CEA) {
			if(event == DiameterPeerEvent.IRcvCEA) {
				return I_Open;
			}else if(event == DiameterPeerEvent.RConnCER) {
				return Wait_Returns;
			}else if(event == DiameterPeerEvent.IPeerDisc) {
				return Closed;
			}else if(event == DiameterPeerEvent.IRcvNonCEA) {
				return Closed;
			}else if(event == DiameterPeerEvent.Timeout) {
				return Closed;
			}else if(event == DiameterPeerEvent.Start) {
				return Closed;
			}else if(event == DiameterPeerEvent.RPeerDisc) {
				return Closed;
			}else {
				return null;
			}

		}else if(state == Elect) {
			if(event == DiameterPeerEvent.IRcvConnAck) {
				return Wait_Returns;
			}else if(event == DiameterPeerEvent.IRcvConnNack) {
				return R_Open;
			}else if(event == DiameterPeerEvent.RPeerDisc) {
				return Wait_Conn_Ack;
			}else if(event == DiameterPeerEvent.RConnCER) {
				return Wait_Conn_Ack_Elect;
			}else if(event == DiameterPeerEvent.Timeout) {
				return Closed;
			}else if(event == DiameterPeerEvent.Start) {
				return Closed;
			}else if(event == DiameterPeerEvent.IPeerDisc) {
				return Closed;
			}else {
				return null;
			}
		}else if(state == Wait_Returns) {
			if(event == DiameterPeerEvent.WinElection) {
				return R_Open;
			}else if(event == DiameterPeerEvent.IPeerDisc) {
				return R_Open;
			}else if(event == DiameterPeerEvent.IRcvCEA) {
				return I_Open;
			}else if(event == DiameterPeerEvent.RPeerDisc) {
				return Wait_I_CEA;
			}else if(event == DiameterPeerEvent.RConnCER) {
				return Wait_Returns;
			}else if(event == DiameterPeerEvent.Timeout) {
				return Closed;
			}else if(event == DiameterPeerEvent.Start) {
				return Closed;
			}else {
				return null;
			}
		}else if(state == I_Open) {
			if(event == DiameterPeerEvent.SendMessage) {
				return I_Open;
			}else if(event == DiameterPeerEvent.IrcvMessage) {
				return I_Open;
			}else if(event == DiameterPeerEvent.IRcvDWR) {
				return I_Open;
			}else if(event == DiameterPeerEvent.IRcvDWA) {
				return I_Open;
			}else if(event == DiameterPeerEvent.RConnCER) {
				return I_Open;
			}else if(event == DiameterPeerEvent.Stop) {
				return Closing;
			}else if(event == DiameterPeerEvent.IRcvDPR) {
				return Closed;
			}else if(event == DiameterPeerEvent.IPeerDisc) {
				return Closed;
			}else if(event == DiameterPeerEvent.IRcvCER) {
				return I_Open;
			}else if(event == DiameterPeerEvent.IRcvCEA) {
				return I_Open;
			}else if(event == DiameterPeerEvent.RPeerDisc) {
				return Closed;
			}else {
				return null;
			}
		}else if(state == Wait_Conn_Ack) {
			if(event == DiameterPeerEvent.IRcvConnAck) {
				return Wait_I_CEA;
			}else if(event == DiameterPeerEvent.IRcvConnNack) {
				return Closed;
			}else if(event == DiameterPeerEvent.RConnCER) {
				return Wait_Conn_Ack_Elect;
			}else if(event == DiameterPeerEvent.IPeerDisc) {
				return Closed;
			}else if(event == DiameterPeerEvent.IRcvNonCEA) {
				return Closed;
			}else if(event == DiameterPeerEvent.Timeout) {
				return Closed;
			}else if(event == DiameterPeerEvent.Start) {
				return Closed;
			}else if(event == DiameterPeerEvent.RPeerDisc) {
				return Closed;
			}else {
				return null;
			}
		}else {
			return null;
		}
	}
	
	public boolean isSync() {
		return isSync;
	}
	
	public int stateOrdinal() {
		return this.ordinal();
	}
	
	public static DiameterPeerState fromStateOrdinal(int ordinalVal){
		return diameterPeerStates.get(ordinalVal);
	}
}
