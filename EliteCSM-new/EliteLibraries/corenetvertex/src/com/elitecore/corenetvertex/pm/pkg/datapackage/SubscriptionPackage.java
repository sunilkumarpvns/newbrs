package com.elitecore.corenetvertex.pm.pkg.datapackage;

import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;

public interface SubscriptionPackage extends UserPackage{


	public abstract ValidityPeriodUnit getValidityPeriodUnit();

	public abstract int getValidity();

	public abstract boolean isMultipleSubscription();

}