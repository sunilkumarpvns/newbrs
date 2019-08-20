package com.elitecore.netvertex.pm;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ExclusivePackageQoSStrategy implements QoSSelectionWithinGroupStrategy {

	private static final ExclusivePackageQoSStrategy EXCLUSIVE_PACKAGE_STRATEGY = new ExclusivePackageQoSStrategy();
	private static final String MODULE = "EXCLUSIVE-PACKAGE-STRATEGY";

	@Override
	public void select(PackageQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData) {
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection started for type " + currentQoSSelection.getPackageType());
		}
		
		if(BestQoSSelectionStrategy.electSessionQoS(currentQoSSelection.getQosProfileDetail(), finalQoSSelectionData.getQosProfileDetail())) {
			finalQoSSelectionData.replaceQoS(currentQoSSelection);
		}
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection completed for type " + currentQoSSelection.getPackageType());
		}
	}

	public static QoSSelectionWithinGroupStrategy instance() {
		return EXCLUSIVE_PACKAGE_STRATEGY;
	}

}
