package com.elitecore.netvertex.pm;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.EnumSet;

public class AlwaysPreferQosStrategy implements QoSSelectionAcrossGroupStrategy {

	private static final String MODULE = "ALWAYS-SELECT-QoS";
	private static final AlwaysPreferQosStrategy ALWAYS_SELECT_ADDON_STRATEGY = new AlwaysPreferQosStrategy(EnumSet.of(PackageType.EXCLUSIVEADDON, PackageType.NONEXCLUSIVEADDON));
	private static final AlwaysPreferQosStrategy ALWAYS_SELECT_PROMOTIONAL_STRATEGY = new AlwaysPreferQosStrategy(EnumSet.of(PackageType.PREFERQOSPROMOTION));
	private EnumSet<PackageType> packageTypes;
	
	private AlwaysPreferQosStrategy(EnumSet<PackageType> packageTypes) {
		this.packageTypes = packageTypes; 
	}

	@Override
	public void select(FinalQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData) {
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection started between " + currentQoSSelection.getPackageType() + " and " + finalQoSSelectionData.getPackageType());
		}
		
		if (packageTypes.contains(currentQoSSelection.getPackageType())) {
			finalQoSSelectionData.replaceQoS(currentQoSSelection);
		}
		
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection completed between " + currentQoSSelection.getPackageType() + " and " + finalQoSSelectionData.getPackageType());
		}
	}

	public static QoSSelectionAcrossGroupStrategy getAlwaysSelectAddOn() {
		return ALWAYS_SELECT_ADDON_STRATEGY;
	}
	
	public static QoSSelectionAcrossGroupStrategy getAlwaysSelectPromotional() {
		return ALWAYS_SELECT_PROMOTIONAL_STRATEGY;
	}

}
