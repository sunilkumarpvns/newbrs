package com.elitecore.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.commons.base.Optional;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.test.diameter.factory.PacketFactory;
import com.elitecore.test.util.TaskScheduler;

public class ExecutionContextImpl implements ExecutionContext {
	
	private ConcurrentHashMap<String, Object> valueMap = new ConcurrentHashMap<String, Object>();
	private TaskScheduler taskScheduler;
	private Process process;
	private ValueProvider valueProvider;
	private PacketFactory packetFactory;
	

	public ExecutionContextImpl(TaskScheduler taskScheduler,Process process,PacketFactory packetFactory) {
		super();
		this.taskScheduler = taskScheduler;
		this.process = process;
		this.packetFactory = packetFactory;
		this.valueProvider = new ValueproviderImpl();
	}

	@Override
	public Optional<Object> get(String key) {
		Object val = valueMap.get(key);
		if(val == null){
			return Optional.absent();
		} else {
			return Optional.of(val);
		}
	}

	@Override
	public Object set(String key, Object obj) {
		return valueMap.put(key, obj);
	}

	@Override
	public TaskScheduler getTaskScheduler() {
		return taskScheduler;
	}

	@Override
	public ExecutionContext copy() {
		ExecutionContextImpl executionContext = new ExecutionContextImpl(taskScheduler,process,packetFactory);
		executionContext.valueMap.putAll(valueMap);
		return executionContext;
	}

	@Override
	public Process getProcess() {
		return process;
	}

	@Override
	public String toString() {
		
		StringWriter write = new StringWriter();
		
		IndentingPrintWriter	 tabbedPrintWriter = new IndentingPrintWriter(new PrintWriter(write));
		tabbedPrintWriter.write("Cached Values:");
		tabbedPrintWriter.println();
		tabbedPrintWriter.incrementIndentation();
		int count = 0;
		for(Entry<String, Object> entry : valueMap.entrySet()){
			tabbedPrintWriter.write("Entry:" + (count++) + ":");
			tabbedPrintWriter.println();
			tabbedPrintWriter.incrementIndentation();
			tabbedPrintWriter.write(entry.getKey() + ":" + entry.getValue().toString());
		}
		
		Closeables.closeQuietly(tabbedPrintWriter);
		
		return write.toString();
	}

	@Override
	public PacketFactory getPacketFactory() {
		
		return this.packetFactory;
	}

	@Override
	public ValueProvider getValueProvider() {
		return valueProvider;
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
