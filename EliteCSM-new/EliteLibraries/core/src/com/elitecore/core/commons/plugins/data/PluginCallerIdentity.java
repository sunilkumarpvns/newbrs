package com.elitecore.core.commons.plugins.data;



public final class PluginCallerIdentity {

	private static final char SEPARATOR = '.';

	private ServiceTypeConstants serviceName;					// Mandatory Field
	private String servicePolicyName;
	private ServicePolicyFlow servicePolicyFlow;
	private String pluginHandlerName;
	private PluginType pluginType;
	private PluginMode pluginMode;								// Mandatory Field
	private String pluginName;									// Mandatory Field
	private int pluginIndex;									// Mandatory Field
	private String key;

	private PluginCallerIdentity() {
		
	}

	public static PluginKeyBuilder createAndGetIdentity(ServiceTypeConstants serviceName,
			PluginMode pluginMode, int pluginIndex, String pluginName) {
		return new PluginKeyBuilder(serviceName, pluginMode, pluginIndex, pluginName);
	}
	
	public ServiceTypeConstants getServiceName() {
		return serviceName;
	}
	
	public String getPluginHandlerName() {
		return pluginHandlerName;
	}
	
	public PluginMode getPluginMode() {
		return pluginMode;
	}
	
	public PluginType getPluginType() {
		return pluginType;
	}
	
	public String getServicePolicyName() {
		return servicePolicyName;
	}
	
	public ServicePolicyFlow getServicePolicyFlow() {
		return servicePolicyFlow;
	}
	
	public int getPluginIndex() {
		return pluginIndex;
	}
	
	public String getPluginName() {
		return pluginName;
	}
	
	public String getKey() {
		return key;
	}
	
	public static class PluginKeyBuilder {
		
		private PluginCallerIdentity pluginKey;
		private byte flag = 0;
		
		private PluginKeyBuilder(ServiceTypeConstants serviceName,
				PluginMode pluginMode, int pluginIndex, String pluginName) {
			pluginKey = new PluginCallerIdentity();
			pluginKey.serviceName = serviceName;
			pluginKey.pluginMode = pluginMode;
			pluginKey.pluginIndex = pluginIndex;
			pluginKey.pluginName = pluginName;
		}

		public PluginKeyBuilder setPluginHandlerName(String name) {
			pluginKey.pluginHandlerName = name;
			flag |= 0x08;
			return this;
		}
		
		public PluginKeyBuilder setPluginType(PluginType pluginType) {
			pluginKey.pluginType = pluginType;
			flag |= 0x04;
			return this;
		}
		
		public PluginKeyBuilder setServicePolicyName(String servicePolicyName) {
			pluginKey.servicePolicyName = servicePolicyName;
			flag |= 0x02;
			return this;
		}
		
		public PluginKeyBuilder setServicePolicyFlow(ServicePolicyFlow servicePolicyFlow) {
			pluginKey.servicePolicyFlow = servicePolicyFlow;
			flag |= 0x01;
			return this;
		}
		
		public PluginCallerIdentity getId() {
			
			StringBuilder builder = new StringBuilder();
			builder.append(pluginKey.serviceName).append(SEPARATOR);
			if ((flag & 0x02) == 0x02) {
				builder.append(pluginKey.servicePolicyName).append(SEPARATOR);
			}
			if ((flag & 0x01) == 0x01) {
				builder.append(pluginKey.servicePolicyFlow).append(SEPARATOR);
			}
			if ((flag & 0x08) == 0x08) {
				builder.append(pluginKey.pluginHandlerName).append(SEPARATOR);
			}
			if ((flag & 0x04) == 0x04) {
				builder.append(pluginKey.pluginType).append(SEPARATOR);
			}
			builder.append(pluginKey.pluginMode).append(SEPARATOR);
			builder.append(pluginKey.pluginName).append(SEPARATOR);
			builder.append(pluginKey.pluginIndex);
			
			pluginKey.key = builder.toString();
			return pluginKey;
		}
	}
}
