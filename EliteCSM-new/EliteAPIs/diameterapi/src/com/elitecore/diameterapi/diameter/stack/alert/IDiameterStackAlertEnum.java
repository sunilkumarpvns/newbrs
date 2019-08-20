/**
 * 
 */
package com.elitecore.diameterapi.diameter.stack.alert;

import com.elitecore.diameterapi.core.stack.alert.IStackAlertEnum;

/**
 * @author pulin
 *
 */
public interface IDiameterStackAlertEnum extends IStackAlertEnum{

	public IStackAlertEnum getDiameterStackUPAlert();
	public IStackAlertEnum getDiameterStackDownAlert();
}
