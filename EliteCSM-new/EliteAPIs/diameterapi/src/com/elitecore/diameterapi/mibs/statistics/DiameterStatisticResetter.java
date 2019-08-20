package com.elitecore.diameterapi.mibs.statistics;


public interface DiameterStatisticResetter {

	public boolean reset();

	public boolean resetStackStatistics();

	public boolean resetAllPeerStatistics();

	public boolean resetAllRealmStatistics();

	public boolean resetApplicationStatistics(String applicationStr);

	public boolean resetApplicationAllPeerStatistics(String applicationStr);

	public boolean resetApplicationPeerStatistics(String applicationStr,
			String hostIdentity);

	public boolean resetPeerStatistics(String hostIdentity);

	public boolean resetRealmStatistics(String realmName);

	public boolean resetAllApplicationStatistics();

}
