package com.elitecore.aaa.diameter.sessionmanager;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.exprlib.parser.expression.ValueProvider;

public enum DBOperationAction{
	NONE(0, "No Operation") {
		@Override
		public int applyAction(SessionScenario scenario, ValueProvider requestValueProvider, ValueProvider responseValueProvider) {
			return 0;
		}
	},
	INSERT(1, "Save Operation") {
		@Override
		public int applyAction(SessionScenario scenario, ValueProvider requestValueProvider, ValueProvider responseValueProvider) {
			return scenario.save(requestValueProvider, responseValueProvider);
		}
	},
	DELETE(2, "Delete Operation") {
		@Override
		public int applyAction(SessionScenario scenario, ValueProvider requestValueProvider, ValueProvider responseValueProvider) {
			return scenario.delete(requestValueProvider, responseValueProvider);
		}
	},
	UPDATE(3, "Update Operation") {
		@Override
		public int applyAction(SessionScenario scenario, ValueProvider requestValueProvider, ValueProvider responseValueProvider) {
			return scenario.update(requestValueProvider, responseValueProvider);
		}
	};
	
	private long operationValue;
	private String operationString;
	private static Map<Long, DBOperationAction> valueToOperationMap;
	private static Map<String, DBOperationAction> nameToOperationMap;
	
	static{
		valueToOperationMap = new HashMap<Long, DBOperationAction>(4, 1);
		nameToOperationMap = new HashMap<String, DBOperationAction>(4, 1);
		for (DBOperationAction operation : values()) {
			valueToOperationMap.put(operation.getValue(), operation);
			nameToOperationMap.put(operation.name(), operation);
		}
	}
	
	private DBOperationAction( long operationValue, String operationString) {
		this.operationValue = operationValue;
		this.operationString = operationString;
	}
	
	public long getValue() {
		return operationValue;
	}
	
	public String getName() {
		return operationString;
	}
	
	public static DBOperationAction getDBOperationForOverrideActionFromOpearationValue(long value) {
		return valueToOperationMap.get(value);
	}
	
	public static DBOperationAction fromName(String name) {
		return nameToOperationMap.get(name);
	}
	
	public abstract int applyAction(SessionScenario scenario, ValueProvider requestValueProvider, ValueProvider responseValueProvider);
}