/**
 * 
 */
package com.elitecore.core.imdg.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author malav
 *
 */
public interface IMDGConfiguration {

	String getGroupName();

	String getPassword();

	Properties getProperties();

	boolean isMancenterEnabled();

	String getUrl();

	List<String> getMemberIps();

	int getPortCount();

	int getStartPort();

	Collection<String> getOutBoundPorts();

	String getInMemoryFormat();

	Map<String, String> getMemberData();

}
