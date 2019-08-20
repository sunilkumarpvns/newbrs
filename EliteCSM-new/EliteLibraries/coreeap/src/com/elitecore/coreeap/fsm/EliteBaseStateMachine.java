package com.elitecore.coreeap.fsm;

import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.util.constants.fsm.IEnum;

/**
 * @author devangadeshara
 * @version 1.0
 * @created 05-Nov-2008 3:15:59 PM
 */
public abstract class EliteBaseStateMachine extends BaseStateMachine implements IEliteStateMachine {
	

	public EliteBaseStateMachine(){

	}

	/**
	 * 
	 * @exception Throwable
	 */
	public void finalize()
	  throws Throwable{
		super.finalize();
	}

	/**
	 * 
	 * @param requestPacket    requestPacket
	 * @throws EAPException 
	 */
	public abstract IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException;

	public abstract AAAEapRespData handleEvent(IEnum event, AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException;
	public abstract IEnum getEvent(AAAEapRespData aaaEapRespData);
	public abstract IEnum[] getActionList(IEnum event);
	public abstract void applyActions(IEnum event,AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException;
	
}
