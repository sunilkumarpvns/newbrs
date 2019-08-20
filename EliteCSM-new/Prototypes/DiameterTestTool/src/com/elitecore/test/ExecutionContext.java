package com.elitecore.test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.Optional;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.test.diameter.factory.PacketFactory;
import com.elitecore.test.util.TaskScheduler;

public interface ExecutionContext {
	
	public @Nonnull Optional<Object> get(@Nonnull String val);
	public @Nullable Object set(@Nonnull String val,@Nonnull Object obj);
	public @Nullable TaskScheduler getTaskScheduler();
	public @Nullable ExecutionContext copy();
	public @Nonnull Process getProcess();
	public @Nonnull PacketFactory getPacketFactory();
	public @Nonnull ValueProvider getValueProvider();

}
