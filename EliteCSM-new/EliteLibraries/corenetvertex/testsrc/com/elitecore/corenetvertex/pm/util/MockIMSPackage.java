package com.elitecore.corenetvertex.pm.util;

import java.util.List;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;


import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MockIMSPackage extends IMSPackage {

    public MockIMSPackage(String id, String name, PkgMode packageMode, PkgStatus availabilityStatus,
                          PolicyStatus policyStatus, String failReason, String partialFailReason, Double price, List<String> groupIds) {
        super(id, name, packageMode, availabilityStatus, policyStatus, failReason, partialFailReason, price, groupIds);
    }

    public MockIMSPackage() {
        super(null,
                null,
                null,
                null,
                null,
                null,
                null
        );

    }

    public static MockIMSPackage create(String id, String name) {
        MockIMSPackage mockPackage = spy(new MockIMSPackage());
        when(mockPackage.getId()).thenReturn(id);
        when(mockPackage.getName()).thenReturn(name);
        return mockPackage;
    }

    public MockIMSPackage statusInActive() {
        when(this.getAvailabilityStatus()).thenReturn(PkgStatus.INACTIVE);
        return this;
    }

    public MockIMSPackage statusActive() {
        when(this.getAvailabilityStatus()).thenReturn(PkgStatus.ACTIVE);
        return this;
    }

    public MockIMSPackage policyStatusSuccess() {
        when(this.getStatus()).thenReturn(PolicyStatus.SUCCESS);
        return this;
    }

    public MockIMSPackage policyStatusFailure() {
        when(this.getStatus()).thenReturn(PolicyStatus.FAILURE);
        return this;
    }
}
