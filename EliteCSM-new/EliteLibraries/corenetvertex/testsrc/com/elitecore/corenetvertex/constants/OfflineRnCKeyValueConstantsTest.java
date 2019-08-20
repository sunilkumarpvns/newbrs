package com.elitecore.corenetvertex.constants;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class OfflineRnCKeyValueConstantsTest {

	@Test
	public void returnsValuesForSpecificKey() {
		List<OfflineRnCKeyValueConstants> trafficTypeValues = OfflineRnCKeyValueConstants.valuesFor(OfflineRnCKeyConstants.TRAFFIC_TYPE);
		
		assertThat(trafficTypeValues, hasItem(equalTo(OfflineRnCKeyValueConstants.TRAFFIC_TYPE_ANSI)));
		assertThat(trafficTypeValues, hasItem(equalTo(OfflineRnCKeyValueConstants.TRAFFIC_TYPE_AUDIO)));
	}
	
	@Test
	public void filtersValuesOfOtherKeys() {
		List<OfflineRnCKeyValueConstants> trafficTypeValues = OfflineRnCKeyValueConstants.valuesFor(OfflineRnCKeyConstants.TRAFFIC_TYPE);
		
		assertThat(trafficTypeValues, not(hasItem(equalTo(OfflineRnCKeyValueConstants.SERVICE_TYPE_FIXED))));
		assertThat(trafficTypeValues, not(hasItem(equalTo(OfflineRnCKeyValueConstants.SUB_SERVICE_TYPE_MOBILE_POSTPAID))));
	}
}
