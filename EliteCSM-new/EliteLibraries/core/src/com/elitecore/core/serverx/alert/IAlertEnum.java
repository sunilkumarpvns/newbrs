/**
 * 
 */
package com.elitecore.core.serverx.alert;

import java.util.List;

import com.elitecore.core.serverx.alert.event.SystemAlert;

/**
 * @author pulin
 *
 */
public interface IAlertEnum {

	public String id();
	public String name();
	public String oid();
	public String getName();
	public String aggregateAlertMessages(List<SystemAlert> alerts);
	
}
