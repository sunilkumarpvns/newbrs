package com.elitecore.netvertex.pm.quota;

import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class GrantedMsccMatches {

    public static Matcher<MSCC> hasVolumeThreshold(long expectedThreshold) {
        return new TypeSafeDiagnosingMatcher<MSCC>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("an answer with volume threshold ").appendValue(expectedThreshold);
            }

            @Override
            protected boolean matchesSafely(MSCC mscc,
                                            Description description) {


                if (mscc.getVolumeQuotaThreshold() != expectedThreshold) {
                    description.appendText(" was answer with volume threshold ").appendValue(mscc.getVolumeQuotaThreshold());
                    return false;
                }
                return true;
            }
        };
    }

    public static Matcher<MSCC> hasTimeThreshold(long expectedThreshold) {
        return new TypeSafeDiagnosingMatcher<MSCC>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("an answer with time threshold  ").appendValue(expectedThreshold);
            }

            @Override
            protected boolean matchesSafely(MSCC mscc,
                                            Description description) {


                if (mscc.getTimeQuotaThreshold() != expectedThreshold) {
                    description.appendText(" was answer with time threshold ").appendValue(mscc.getTimeQuotaThreshold());
                    return false;
                }
                return true;
            }
        };
    }
}
