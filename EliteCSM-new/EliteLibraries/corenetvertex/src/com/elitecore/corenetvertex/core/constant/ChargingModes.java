package com.elitecore.corenetvertex.core.constant;

import java.util.HashMap;
import java.util.Map;


public enum ChargingModes {

	ONLINE("Online", 0 ) {
		@Override
		public boolean isOnlineEnabled() {
			return true;
		}

		@Override
		public boolean isOfflineEnabled() {
			return false;
		}
	},
	OFFLINE("Offline", 1) {
		@Override
		public boolean isOnlineEnabled() {
			return false;
		}

		@Override
		public boolean isOfflineEnabled() {
			return true;
		}
	},
	BOTH("Both", 2) {
		@Override
		public boolean isOnlineEnabled() {
			return true;
		}

		@Override
		public boolean isOfflineEnabled() {
			return true;
		}
	},
	NONE("None", 3) {
		@Override
		public boolean isOnlineEnabled() {
			return false;
		}

		@Override
		public boolean isOfflineEnabled() {
			return false;
		}
	};

	public final String displayName;
	public final int val;

	private static final Map<Integer, ChargingModes> map = new HashMap<Integer, ChargingModes>();
	private static final Map<String, ChargingModes> displayValToChargingMod = new HashMap<String, ChargingModes>();

	static {
		for (ChargingModes chargingMode : ChargingModes.values()) {
			map.put(chargingMode.val, chargingMode);
			displayValToChargingMod.put(chargingMode.displayName, chargingMode);
		}
	}

	private ChargingModes(String displayName, int val) {
		this.displayName = displayName;
		this.val = val;
	}
	
	public int getVal() {
		return val;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static ChargingModes fromValue(int val){
		return map.get(val);
	}
	
	public static ChargingModes fromValue(String val){
		return displayValToChargingMod.get(val);
	}
	
	public abstract boolean isOnlineEnabled();
	
	public abstract boolean isOfflineEnabled();

}
