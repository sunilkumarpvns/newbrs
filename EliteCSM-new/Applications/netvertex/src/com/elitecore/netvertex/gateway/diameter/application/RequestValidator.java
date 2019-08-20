package com.elitecore.netvertex.gateway.diameter.application;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public interface RequestValidator {
	
	@Nonnull ValidationResult validate(@Nonnull DiameterRequest request);
}
