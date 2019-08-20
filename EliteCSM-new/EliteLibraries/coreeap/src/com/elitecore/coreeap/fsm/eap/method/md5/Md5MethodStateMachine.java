package com.elitecore.coreeap.fsm.eap.method.md5;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.actions.MD5Actions;
import com.elitecore.coreeap.util.constants.fsm.events.MD5Events;

public class Md5MethodStateMachine extends BaseMd5MethodStateMachine{

	public Md5MethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
	}

	public byte[] getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean check(AAAEapRespData aaaEapRespData) {
		// TODO Auto-generated method stub
		parseMD5Resp(aaaEapRespData);
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
		parseMD5Resp(aaaEapRespData);
		MD5Events event = (MD5Events)getEvent(aaaEapRespData);		
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
				switch((MD5Actions)actionList[iCounter]){
				case Generate_challenge:
					actionOnGenerate_challenge(aaaEapRespData,provider);
					break;
				case Initialize:
					actionOnInitialize(aaaEapRespData,provider);
					break;
				case ValidateMD5Response:
					actionOnValidateMD5Response(aaaEapRespData,provider);
					break;
				default:
					actionOnDiscardRequest(aaaEapRespData,provider);
				}
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "No MD5 Actions for Event :"+ event);
		}
		return null;
	}

	public int getMethodCode() {
		return EapTypeConstants.MD5_CHALLENGE.typeId;
	}

}
