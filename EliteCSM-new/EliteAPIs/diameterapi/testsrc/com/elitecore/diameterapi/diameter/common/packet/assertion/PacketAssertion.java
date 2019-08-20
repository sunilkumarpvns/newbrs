package com.elitecore.diameterapi.diameter.common.packet.assertion;

import org.hamcrest.Matcher;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public interface PacketAssertion {

	public Matcher<? super DiameterRequest> createRequestMatcher();
	public Matcher<? super DiameterAnswer> createResponseMatcher();

}
