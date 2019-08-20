package com.elitecore.coreeap.fsm.eap.method.mschapv2;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.actions.MSCHAPv2Actions;
import com.elitecore.coreeap.util.constants.fsm.events.MSCHAPv2Events;

public class MSCHAPv2MethodStateMachine extends BaseMSCHAPv2MethodStateMachine{

	public MSCHAPv2MethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
	}

	public byte[] getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean check(AAAEapRespData aaaEapRespData) {
		// TODO Auto-generated method stub
		parseMSCHAPv2Resp(aaaEapRespData);
		return isValidRequest();
	}

	public void process(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		// TODO Auto-generated method stub
		processRequest(aaaEapRespData,provider);
	}

	@Override
	public IEnum processRequest(AAAEapRespData aaaEapRespData, ICustomerAccountInfoProvider provider) throws EAPException {
		// TODO Auto-generated method stub
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(getModuleName(), "MD5 Packet being processed in state :" + getCurrentState());
		MSCHAPv2Events event = (MSCHAPv2Events)getEvent(aaaEapRespData);		
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
				switch((MSCHAPv2Actions)actionList[iCounter]){
				case BuildFailure:
					actionOnBuildFailure(aaaEapRespData,provider);
					break;
				case Generate_challenge:
					actionOnGenerate_challenge(aaaEapRespData,provider);
					break;
				case Initialize:
					actionOnInitialize(aaaEapRespData,provider);
					break;
				case ProcessRequest:
					actionOnProcessRequest(aaaEapRespData,provider);
					break;
				case SendResponse:
					actionOnSendResponse(aaaEapRespData,provider);
					break;
				case BuildSuccess:
					actionOnBuildSuccess(aaaEapRespData,provider);
					break;
				case ValidateMSCHAPv2Response:
					actionOnValidateMSCHAPv2Response(aaaEapRespData,provider);
					break;
				default:
					actionOnDiscardRequest(aaaEapRespData,provider);
				}
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "No MSCHAPv2 Actions for Event :"+ event);
		}
		return null;
	}

	public int getMethodCode() {
		return EapTypeConstants.MSCHAPv2.typeId;
	}

}
