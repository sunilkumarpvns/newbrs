package com.elitecore.coreeap.fsm;

import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.util.constants.fsm.IEnum;


/**
 * @author devangadeshara
 * @version 1.0
 * @created 5-Nov-2008 3:16:15 PM
 */
public interface IEliteStateMachine {

	/**
	 * 
	 * @param state    state
	 */
	public void changeCurrentState(IEnum state);

	/**
	 * 
	 * @param requestPacket    requestPacket
	 * @throws EAPException 
	 */
	public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException;

	/**
	 * 
	 * @param event    event
	 */
	public void setCurrentEvent(IEnum event);
	
	public IEnum getCurrentState();
	public IEnum getCurrentEvent();
	public AAAEapRespData handleEvent(IEnum event,AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException;	
}
