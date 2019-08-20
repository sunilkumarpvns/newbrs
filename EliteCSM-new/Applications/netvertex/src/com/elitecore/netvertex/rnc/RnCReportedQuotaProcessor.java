package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

public abstract class RnCReportedQuotaProcessor {

    private PolicyRepository policyRepository;

    public RnCReportedQuotaProcessor(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public PolicyRepository getPolicyRepository() {
        return policyRepository;
    }

    public abstract void handle();

    public MSCC getUnAccountedUsage() {
        return null;
    }

    public abstract ReportedUsageSummary getReportedUsageSummary();
}
