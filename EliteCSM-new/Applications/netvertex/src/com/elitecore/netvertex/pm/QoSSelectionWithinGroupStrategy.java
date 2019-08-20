package com.elitecore.netvertex.pm;

import static com.elitecore.commons.logging.LogManager.getLogger;

public interface QoSSelectionWithinGroupStrategy {
	
	public void select(PackageQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData);
	public final static QoSSelectionWithinGroupStrategy REPLACE_QOS = new QoSSelectionWithinGroupStrategy() {
		
		public static final String MODULE = "REPLACE-QOS";
		@Override
		public void select(PackageQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData) {
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "QoS selection started for type " + currentQoSSelection.getPackageType());
			}
			
			if(getLogger().isDebugLogLevel()) {
				QoSProfileDetail currentQosProfileDetail = currentQoSSelection.getQosProfileDetail();
				String fupLevel = currentQosProfileDetail.getFUPLevel() == 0 ? "HSQ" : ("FUP" + currentQosProfileDetail.getFUPLevel());
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Applying QoS from QoS Profile: " + currentQosProfileDetail.getName() + ", Level: " + fupLevel);
				}				
			}
			
			finalQoSSelectionData.replaceQoS(currentQoSSelection);
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "QoS selection completed for type " + currentQoSSelection.getPackageType());
			}
		}
	};
}
