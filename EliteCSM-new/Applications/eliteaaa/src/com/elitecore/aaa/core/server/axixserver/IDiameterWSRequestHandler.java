package com.elitecore.aaa.core.server.axixserver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public interface IDiameterWSRequestHandler {
	public @Nonnull DiameterAnswer submitToStack(DiameterRequest diameterRequest);
	public @Nullable String getMappingConfigId(String methodId);
	@Nonnull SettableFuture<DiameterAnswer> submitNonBlockingToStack(
			DiameterRequest diameterRequest);
}
