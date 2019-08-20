package com.elitecore.netvertex.pm.util;

import javax.annotation.Nonnull;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.netvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.netvertex.service.notification.ThresholdNotificationScheme;


import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MockRnCPackage extends RnCPackage {

	public MockRnCPackage() {
		super(null, null, null, null, null, null, null, null, null, null, null, null, null, null,null);
	}

	public static MockRnCPackage createBase(String id, String name) {
		MockRnCPackage mockRnCPackage = create(id, name);
		when(mockRnCPackage.getPkgType()).thenReturn(RnCPkgType.BASE);
		return mockRnCPackage;
	}

	@Nonnull
	private static MockRnCPackage create(String id, String name) {
		MockRnCPackage mockRnCPackage = spy(new MockRnCPackage());
		when(mockRnCPackage.getId()).thenReturn(id);
		when(mockRnCPackage.getName()).thenReturn(name);
		return mockRnCPackage;
	}

	public void setNotificationScheme(ThresholdNotificationScheme notificationScheme) {
		when(this.getThresholdNotificationScheme()).thenReturn(notificationScheme);
	}

	public static MockRnCPackage createNonMonetaryAddOn(String id, String name) {
		MockRnCPackage mockRnCPackage = create(id, name);
		when(mockRnCPackage.getPkgType()).thenReturn(RnCPkgType.NON_MONETARY_ADDON);
		return mockRnCPackage;
	}

	public void eventchargingType() {
		when(this.getChargingType()).thenReturn(ChargingType.EVENT);
	}
}
