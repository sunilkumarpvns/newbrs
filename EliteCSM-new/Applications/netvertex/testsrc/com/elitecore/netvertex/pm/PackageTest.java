package com.elitecore.netvertex.pm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimePeriod;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.acesstime.exception.InvalidTimeSlotException;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.data.PackageType;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class PackageTest {

	public Object[][] data_provider_for_Apply_should_set_timeperiod_if_applicable() {
		
		return new Object[][] {
			$(6, 59, 59, 	   	TimeUnit.SECONDS.toSeconds(1)),
			$(7, 0, 0, 	   		TimeUnit.HOURS.toSeconds(1)),
			$(7, 0, 01, 	 	TimeUnit.HOURS.toSeconds(1) - 1),
			
			$(7, 59, 59, 		TimeUnit.SECONDS.toSeconds(1)),
			$(8, 0, 0, 	   		TimeUnit.HOURS.toSeconds(1)),
			$(8, 0, 01, 	   	TimeUnit.HOURS.toSeconds(1) - 1),
			
			$(8, 59, 59, 	   	TimeUnit.SECONDS.toSeconds(1)),
			$(9, 0, 0, 	   		TimeUnit.HOURS.toSeconds(15) - 1),
			$(9, 0, 01, 	   	TimeUnit.HOURS.toSeconds(15) - 2),

			$(23, 59, 59,    	TimeUnit.SECONDS.toSeconds(1)),
			$(0, 0, 0,    		TimeUnit.HOURS.toSeconds(1)),
			$(0, 0, 1,    		TimeUnit.HOURS.toSeconds(1) - 1),
			
			$(00, 59, 59,    	TimeUnit.SECONDS.toSeconds(1)),
			$(1, 0, 0,    		TimeUnit.HOURS.toSeconds(6)),
			$(1, 0, 1,    		TimeUnit.HOURS.toSeconds(6) - 1)
		};
	}

	@Test
	public void timeSlotNotProvided() {
		PolicyContext policyContext = mock(PolicyContext.class);
		Calendar calendar = Calendar.getInstance();
		TimePeriod timePeriod = new TimePeriod(calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND)
		);

		QoSProfile qoSProfile = mock(QoSProfile.class);
		TestPackage pkg = new TestPackage(Arrays.asList(qoSProfile));
		Assert.assertEquals(AccessTimePolicy.NO_TIME_OUT, pkg.getNextSessionTimeOut(null));
	}

	@Test
	@Parameters(method = "data_provider_for_Apply_should_set_timeperiod_if_applicable")
	public void test_Apply_should_set_timeperiod_if_applicable(int currentHour, int currentMinute, int currentSecond, long result) throws InvalidTimeSlotException {
		
		
		TimeSlot timeSlot = TimeSlot.getTimeSlot("", "", "", "8-1");
		AccessTimePolicy accessTimePolicy = new AccessTimePolicy();
		accessTimePolicy.setListTimeSlot(Arrays.asList(timeSlot));
		
		QoSProfile qoSProfile = mock(QoSProfile.class);
		when(qoSProfile.getAccessTimePolicy()).thenReturn(accessTimePolicy);
		
		timeSlot = TimeSlot.getTimeSlot("", "", "", "7-9");
		accessTimePolicy = new AccessTimePolicy();
		accessTimePolicy.setListTimeSlot(Arrays.asList(timeSlot));
		QoSProfile qoSProfile2 = mock(QoSProfile.class);
		when(qoSProfile2.getAccessTimePolicy()).thenReturn(accessTimePolicy);
		
		TestPackage pkg = new TestPackage(Arrays.asList(qoSProfile, qoSProfile2));
		pkg.init();

		TimePeriod timePeriod = new TimePeriod(currentHour, currentMinute, currentSecond, currentHour, currentMinute, currentSecond);
		
		assertEquals(result, pkg.getNextSessionTimeOut(timePeriod));
	}
	
	

	private static class TestPackage extends Package {

		public TestPackage(List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile> qosProfiles) {
			super("test", "1", QuotaProfileType.USAGE_METERING_BASED, null, qosProfiles, null,null,null,null,
					null,null, new ArrayList<>(), PolicyStatus.SUCCESS, "FAILED", null, null, null,
					null);
		}
		
		public void apply(PolicyContext policyContext, QoSInformation qosInformation) {
			apply(policyContext, qosInformation);
			
		}

		@Override
		public String getType() {
			return PackageType.BASE_DATA_PACKAGE.getType();
		}

		@Override
		public PkgType getPackageType() {
			return PkgType.ADDON;
		}
		
	}

}
