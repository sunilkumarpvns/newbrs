/**
 * 
 */
package com.elitecore.diameterapi.core.stack.alert;

/**
 * @author pulin
 *
 */
public interface IStackAlertManager {

	public void scheduleAlert(StackAlertSeverity severity,IStackAlertEnum alert, String alertGeneratorIdentity, String alertMessage);

	public void scheduleAlert(StackAlertSeverity severity, IStackAlertEnum alert, String alertGeneratorIdentity, String alertMessage, int alertIntValue, String alertStringValue);
}
