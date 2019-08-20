package com.elitecore.test.command;

import com.elitecore.commons.base.Optional;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.Process;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.diameter.factory.PacketFactory;
import com.elitecore.test.util.TaskScheduler;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SendCommandContextImpl implements SendCommandContext {

	private final @Nonnull ExecutionContext executionContext;
	private final @Nonnull ConcurrentHashMap<String, Object> valueMap;
	private final @Nonnull ValueProvider valueProvider;

	


	public SendCommandContextImpl(ExecutionContext executionContext) {
		this.executionContext = executionContext;		
		valueProvider = new ValueproviderImpl();
		valueMap = new ConcurrentHashMap<String, Object>();
	}
	
	@Override
	public Optional<Object> get(String key) {
		Object val = valueMap.get(key);
		if(val == null){
			return executionContext.get(key);
		} else {
			return Optional.of(val);
		}
	}

	@Override
	public Object set(String val, Object obj) {
		return executionContext.set(val, obj);
	}

	@Override
	public Optional<DiameterPacket> getSendPacketDetail() {
		Optional<Object> optional =  get(SendCommandContext.SEND_DATA);
		
		return optional.isPresent() ? Optional.of((DiameterPacket)optional.get()) : Optional.<DiameterPacket>absent();
	}
	
	@Override
	public TaskScheduler getTaskScheduler() {
		return executionContext.getTaskScheduler();
	}
	
	@Override
	public ExecutionContext copy() {
		SendCommandContextImpl executionContext = new SendCommandContextImpl(this.executionContext.copy());
		executionContext.valueMap.putAll(valueMap);
		return executionContext;
	}


	@Override
	public Process getProcess() {
		return executionContext.getProcess();
	}
	

	@Override
	public PacketFactory getPacketFactory() {
		return executionContext.getPacketFactory();
	}


	@Override
	public ValueProvider getValueProvider() {
		return this.valueProvider;
	}
	
	private class ValueproviderImpl implements ValueProvider{
		
		@Override
		public String getStringValue(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
			Optional<Object> val = get(identifier);
			if(val.isPresent() == false){
				throw new MissingIdentifierException(identifier + " not found");
			}
			
			return val.get().toString();
		}
		
		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return Long.parseLong(getStringValue(identifier));
		}
		
		@Override
		public List<String> getStringValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			
			return Arrays.asList(getStringValue(identifier));
		}
		
		@Override
		public List<Long> getLongValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return Arrays.asList(getLongValue(identifier));
		}
		
	}

}
