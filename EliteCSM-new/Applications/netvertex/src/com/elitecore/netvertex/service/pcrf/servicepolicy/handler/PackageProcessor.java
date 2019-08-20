package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;

public interface PackageProcessor {

	void process(PolicyContext policyContext, QoSInformation qosInformation);

}