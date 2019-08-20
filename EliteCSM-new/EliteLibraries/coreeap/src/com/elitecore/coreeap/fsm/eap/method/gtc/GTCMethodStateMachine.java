package com.elitecore.coreeap.fsm.eap.method.gtc;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.actions.GtcActions;
import com.elitecore.coreeap.util.constants.fsm.events.GtcEvents;

public class GTCMethodStateMachine extends BaseGTCMethodStateMachine{

	public GTCMethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
	}


	public boolean check(AAAEapRespData aaaEapRespData) {
		parseGtcResp(aaaEapRespData);
		return isValidRequest();
	}

	public void process(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		processRequest(aaaEapRespData,provider);
	}

	@Override
	public IEnum processRequest(AAAEapRespData aaaEapRespData, ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(getModuleName(), "GTC Packet being processed in state :" + getCurrentState());
		parseGtcResp(aaaEapRespData);
		GtcEvents event = (GtcEvents)getEvent(aaaEapRespData);		
		setCurrentEvent(event);
		IEnum state = getNextState(event);
		changeCurrentState(state);
		
		handleEvent(event, aaaEapRespData,provider);
		return getCurrentEvent();
	}

	@Override
	public AAAEapRespData handleEvent(IEnum event, AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		IEnum[] actionList = getActionList(event);
		if(actionList != null){
			for(int iCounter=0; iCounter < actionList.length ; iCounter ++){
				switch((GtcActions)actionList[iCounter]){
				case Generate_challenge:
					actionOnGenerate_challenge(aaaEapRespData,provider);
					break;
				case Initialize:
					actionOnInitialize(aaaEapRespData,provider);
					break;
				case ValidateGtcResponse:
					actionOnValidateGtcResponse(aaaEapRespData,provider);
					break;
				default:
					actionOnDiscardRequest(aaaEapRespData,provider);
				}
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "No GTC Actions for Event :"+ event);
		}
		return null;
	}

	public int getMethodCode() {
		return EapTypeConstants.GTC.typeId;
	}

	public byte[] getKey() {
		return null;
	}


}
