package com.elitecore.netvertex.pm;


public class QoSSelectionStrategyFactory {
	
	public static QoSSelectionWithinGroupStrategy instance(PackageType packageType) {
		
		switch (packageType) {
			case BASE:
			case EMERGENCY:
				return QoSSelectionWithinGroupStrategy.REPLACE_QOS;
			case EXCLUSIVEADDON:
				return ExclusivePackageQoSStrategy.instance();
			case NONEXCLUSIVEADDON:
				return BestQoSSelectionStrategy.instance();
			case PROMOTION:
			case PREFERQOSPROMOTION:
				return ExclusivePackageQoSStrategy.instance();
		}
		
		return null;
	}

	private static QoSSelectionAcrossGroupStrategy fromBaseTo(PackageType toData) {

		switch (toData) {
			case EXCLUSIVEADDON:
				return AlwaysPreferQosStrategy.getAlwaysSelectAddOn();
			case NONEXCLUSIVEADDON:
				return BestQoSSelectionStrategy.instance();
			case BASE:				
			case EMERGENCY:
				return null;
				
		}
		
		return null;
	}
	
	private static QoSSelectionAcrossGroupStrategy fromExclusiveAddOnTo(PackageType toData) {

		switch (toData) {
			case BASE:
				return AlwaysPreferQosStrategy.getAlwaysSelectAddOn();
			case NONEXCLUSIVEADDON:
				return BestQoSSelectionStrategy.instance();
			case EXCLUSIVEADDON:
			case EMERGENCY:
				return null;
		}
		
		return null;
	}
	
	private static QoSSelectionAcrossGroupStrategy fromNonExclusiveAddOnTo(PackageType toData) {

		switch (toData) {
			case BASE:
				return BestQoSSelectionStrategy.instance();
			case EXCLUSIVEADDON:
				return BestQoSSelectionStrategy.instance();
			case NONEXCLUSIVEADDON:
			case EMERGENCY:
				return null;
		}
		
		return null;
	}

	public static QoSSelectionAcrossGroupStrategy instance(PackageType fromPackageType, PackageType toPackageType) {
		
		switch (fromPackageType) {
			case BASE:
				return fromBaseTo(toPackageType);
			case EXCLUSIVEADDON:
					return fromExclusiveAddOnTo(toPackageType);
			case NONEXCLUSIVEADDON:
					return fromNonExclusiveAddOnTo(toPackageType);
			case EMERGENCY:
				return null;
			case PROMOTION:
				return BestQoSSelectionStrategy.instance();
			case PREFERQOSPROMOTION:
				return AlwaysPreferQosStrategy.getAlwaysSelectPromotional();
		}
		
		return null;
	}





}
