package com.elitecore.netvertex.pm;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class TopUpQoSSelectionStrategy implements QoSSelectionWithinGroupStrategy {
	
	private static final TopUpQoSSelectionStrategy TOP_UP_PACKAGE_STRATEGY = new TopUpQoSSelectionStrategy();
	private static final String MODULE = "TOP-UP-PACKAGE-STRATEGY";

	@Override
	public void select(PackageQoSSelectionData currentQoSSelection, FinalQoSSelectionData finalQoSSelectionData) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection started for type " + currentQoSSelection.getPackageType());
		}

		QoSProfileDetail currentQosProfileDetail = currentQoSSelection.getQosProfileDetail();
		QoSProfileDetail finalQosProfileDetail = finalQoSSelectionData.getQosProfileDetail();
		
		if (finalQosProfileDetail == null) {
			finalQoSSelectionData.replaceQoS(currentQoSSelection);
		} else {

			/*
			 * When order no is same, compare fup level
			 */
			if (currentQosProfileDetail.getOrderNo() == finalQosProfileDetail.getOrderNo()) {

				/*
				 * When same fup level, elect session qos
				 */
				if (currentQosProfileDetail.getFUPLevel() == finalQosProfileDetail.getFUPLevel()) {
					ExclusivePackageQoSStrategy.instance().select(currentQoSSelection, finalQoSSelectionData);

					/*
					 * When current qos profile is higher level then set
					 */
				} else if (currentQosProfileDetail.getFUPLevel() < finalQosProfileDetail.getFUPLevel()) {
					finalQoSSelectionData.replaceQoS(currentQoSSelection);
				}

			} else if (currentQosProfileDetail.getOrderNo() < finalQosProfileDetail.getOrderNo()) {
				finalQoSSelectionData.replaceQoS(currentQoSSelection);
			}
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "QoS selection completed for type " + currentQoSSelection.getPackageType());
		}
	}
	
	public static QoSSelectionWithinGroupStrategy instance() {
		return TOP_UP_PACKAGE_STRATEGY;
	}


}
