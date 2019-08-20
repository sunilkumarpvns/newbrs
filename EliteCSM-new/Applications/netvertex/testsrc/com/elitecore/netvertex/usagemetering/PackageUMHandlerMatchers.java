package com.elitecore.netvertex.usagemetering;

import java.util.Collection;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.elitecore.corenetvertex.spr.SubscriberUsage;

public class PackageUMHandlerMatchers {

	
	public static Matcher<PackageUMHandler> hasCurrentUsage(final SubscriberUsage... expectedSubscriberUsages) {
		
		return new TypeSafeDiagnosingMatcher<PackageUMHandler>() {

			@Override
			public void describeTo(Description desc) {
					
			}

			@Override
			protected boolean matchesSafely(PackageUMHandler packageUMHandler, Description desc) {
				
				Collection<SubscriberUsage> updateList = packageUMHandler.getUpdateList();
				ReflectionAssert.assertReflectionEquals(expectedSubscriberUsages, updateList, ReflectionComparatorMode.LENIENT_ORDER);
				return true;
			}
		};
		
	}
	
	public static Matcher<PackageUMHandler> hasUsageToUpdateInDB(final SubscriberUsage... expectedSubscriberUsages) {
		
		return new TypeSafeDiagnosingMatcher<PackageUMHandler>() {

			@Override
			public void describeTo(Description desc) {
					
			}

			@Override
			protected boolean matchesSafely(PackageUMHandler packageUMHandler, Description desc) {
				
				Map<String, SubscriberUsage> actualCurrentServiceUsage = packageUMHandler.getCurrentServiceUsage();
				ReflectionAssert.assertReflectionEquals(expectedSubscriberUsages, actualCurrentServiceUsage.values(), ReflectionComparatorMode.LENIENT_ORDER);
				
				return true;
			}
		};
		
	}
}
