package com.elitecore.coreeap.fsm.eap;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.types.IdentityEAPType;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.events.EapEvents;
public abstract class DefaultEapStateMachine extends BaseEapStateMachine {
	 
	private static final boolean RECEIVED = true;
	public DefaultEapStateMachine(IEapConfigurationContext eapConfigurationContext) {
		// TODO Auto-generated constructor stub
		super(eapConfigurationContext);
	}
	@Override
	public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		
		setDuplicateRequest(false);
		setCustomerAccountInfoProvider(provider);
		// TODO Auto-generated method stub
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(getModuleName(), "Eap Packet being processed in state :" + getCurrentState());
		if(getLastAAAEapRespData() != null && getLastAAAEapRespData().getEapRespPacketBytes() != null && Arrays.equals(getLastAAAEapRespData().getEapRespPacketBytes(),aaaEapRespData.getEapRespPacketBytes())){
			EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
			if(eapPacket != null){
				if(eapPacket.getEAPType()!= null && eapPacket.getEAPType().getType() == EapTypeConstants.IDENTITY.typeId){
					String identity;
					try {
						identity = new String(((IdentityEAPType)eapPacket.getEAPType()).getIdentity(),CommonConstants.UTF8);
					} catch (UnsupportedEncodingException e) {
						identity = new String(((IdentityEAPType)eapPacket.getEAPType()).getIdentity());							
					}
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(getModuleName(), "Authentication reinitiated,Identity : " + identity + " packet id : "+ eapPacket.getIdentifier());
				}else{
					if(getLastAAAEapRespData().getEapReqPacketBytes() != null){
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(getModuleName(), "Retransmitting eap packet, packet type : " + eapPacket.getEAPType() + " packet id : "+ eapPacket.getIdentifier());
						setDuplicateRequest(true);
						aaaEapRespData.setEapReqPacket(getLastAAAEapRespData().getEapReqPacketBytes());
						aaaEapRespData.setAAAEapKeyData(getLastAAAEapRespData().getAAAEapKeyData());
						return getCurrentEvent();
					}
				}
			}				
		}				
		if(aaaEapRespData != null){
			setAaaEapResp(RECEIVED);
		}
		parseEapResp(aaaEapRespData);
		EapEvents event = (EapEvents)getEvent(aaaEapRespData);		
		setCurrentEvent(event);		
		handleEvent(event, aaaEapRespData,provider);
		sendEapRequest(aaaEapRespData, provider);
		if(ignore){
			aaaEapRespData.setEapReqPacket(getLastAAAEapRespData().getEapReqPacket());
		}
		setLastAAAEapRespData(aaaEapRespData);
		return getCurrentEvent();
	}
	public AAAEapRespData handleEvent(IEnum event,AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		// TODO Auto-generated method stub
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), "Handle Eap Event :"+ event);
			applyActions(event, aaaEapRespData,provider);
			IEnum state = getNextState(getCurrentState());
			changeCurrentState(state);
		return null;
	}
}
