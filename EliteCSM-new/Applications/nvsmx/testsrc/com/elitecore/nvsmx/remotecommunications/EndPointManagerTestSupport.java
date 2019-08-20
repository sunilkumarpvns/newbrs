package com.elitecore.nvsmx.remotecommunications;

import org.mockito.Mockito;

/**
 * Created by aditya on 6/27/17.
 */
public class EndPointManagerTestSupport extends EndPointManager {

    public static EndPointManagerTestSupport spy() {
        return Mockito.spy(new EndPointManagerTestSupport());
    }

   public EndPointManagerTestSupport add(TestableNVSMXEndPoint endPoint) {
        return this;
    }

    public TestableNVSMXEndPoint spyNvsxmEndPont(String id) {
        TestableNVSMXEndPoint testableNVSMXEndPoint = TestableNVSMXEndPoint.spy();
        add(testableNVSMXEndPoint);
        return testableNVSMXEndPoint;
    }

}

