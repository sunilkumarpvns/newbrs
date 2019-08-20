package com.elitecore.test.dependecy.diameter;

import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import com.elitecore.test.dependecy.diameter.packet.DiameterAnswer;
import com.elitecore.test.dependecy.diameter.packet.DiameterRequest;
import com.elitecore.test.exception.ChennelClosedException;

public interface VirtualGateway {
	public @Nonnull Future<DiameterAnswer> send(@Nonnull DiameterRequest diameterRequest) throws ChennelClosedException;
	public @Nonnull void send(@Nonnull DiameterAnswer diameterAnswer) throws ChennelClosedException;
	public @Nonnull Future<DiameterRequest> receive(@Nonnull String commandCode) throws ChennelClosedException;
}
