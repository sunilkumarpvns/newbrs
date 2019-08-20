package com.elitecore.aaa.core.plugins.conf;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import com.elitecore.aaa.diameter.plugins.conf.UniversalDiameterPluginConfigurable;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.impl.QuotaManagementPluginConfigurable;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurable;
import com.elitecore.aaa.radius.service.acct.plugins.conf.impl.UniversalAcctPluginConfigurable;
import com.elitecore.aaa.radius.service.auth.plugins.conf.impl.UniversalAuthPluginCofigurable;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import javax.annotation.Nullable;

@ReadOrder(order = { "pluginTypeWiseConfigurable","universalAuthPluginCofigurable",
					"universalAcctPluginCofigurable", "universalDiameterPluginConfigurable",
					"radGroovyPluginConfigurable",
					"diaGroovyPluginConfigurable",
					"diameterTransactionLoggerConfigurable","radiusTransactionLoggerConfigurable",
					"quotaManagementPluginConfigurable","userStatisticAuthPluginConfigurable"
					}
			)

public class PluginConfigurable extends CompositeConfigurable {
	
	@Configuration private PluginTypeWiseConfigurable pluginTypeWiseConfigurable;
	@Configuration private @Nullable UniversalAuthPluginCofigurable universalAuthPluginCofigurable;
	@Configuration private @Nullable UniversalAcctPluginConfigurable universalAcctPluginCofigurable;
	@Configuration private @Nullable UniversalDiameterPluginConfigurable universalDiameterPluginConfigurable;
	@Configuration private @Nullable RadiusGroovyPluginConfigurable radGroovyPluginConfigurable;
	@Configuration private @Nullable DiameterGroovyPluginConfigurable diaGroovyPluginConfigurable;
	@Configuration private @Nullable DiameterTransactionLoggerConfigurable diameterTransactionLoggerConfigurable;
	@Configuration private @Nullable RadiusTransactionLoggerConfigurable radiusTransactionLoggerConfigurable;

	@Configuration private @Nullable QuotaManagementPluginConfigurable quotaManagementPluginConfigurable;
	@Configuration private @Nullable UserStatisticPostAuthPluginConfigurable userStatisticAuthPluginConfigurable;
	
	
	public PluginConfigurable(){
	}
	
	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		return false;
	}

	public UniversalAuthPluginCofigurable getUniversalAuthPluginCofigurable() {
		return universalAuthPluginCofigurable;
	}

	public UniversalAcctPluginConfigurable getUniversalAcctPluginCofigurable() {
		return universalAcctPluginCofigurable;
	}

	public UniversalDiameterPluginConfigurable getUniversalDiameterPluginConfigurable() {
		return universalDiameterPluginConfigurable;
	}

	public RadiusGroovyPluginConfigurable getRadGroovyPluginConfigurable() {
		return radGroovyPluginConfigurable;
	}

	public DiameterGroovyPluginConfigurable getDiaGroovyPluginConfigurable() {
		return diaGroovyPluginConfigurable;
	}

	public DiameterTransactionLoggerConfigurable getDiameterTransactionLoggerConfigurable() {
		return diameterTransactionLoggerConfigurable;
	}

	public RadiusTransactionLoggerConfigurable getRadiusTransactionLoggerConfigurable() {
		return radiusTransactionLoggerConfigurable;
	}

	public QuotaManagementPluginConfigurable getQuotaManagementPluginConfigurable() {
		return quotaManagementPluginConfigurable;
	}

	public UserStatisticPostAuthPluginConfigurable getUserStatisticAuthPluginConfigurable() {
		return userStatisticAuthPluginConfigurable;
	}

	@PostRead
	@PostReload
	public void postReadProcessing(){
	
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@XmlEnum
	public enum PluginType {
		
		@XmlEnumValue(value = "UNIVERSAL_AUTH_PLUGIN")
		UNIVERSAL_AUTH(1, "UNIVERSAL_AUTH_PLUGIN"),

		@XmlEnumValue(value = "UNIVERSAL_ACCT_PLUGIN")
		UNIVERSAL_ACCT(2, "UNIVERSAL_ACCT_PLUGIN"),
		
		@XmlEnumValue(value = "RADIUS_GROOVY_PLUGIN")
		RADIUS_GROOVY_PLUGIN(3, "RADIUS_GROOVY_PLUGIN"),
		
		@XmlEnumValue(value = "UNIVERSAL_DIAMETER_PLUGIN")
		UNIVERSAL_DIAMETER_PLUGIN(4, "UNIVERSAL_DIAMETER_PLUGIN"),
		
		@XmlEnumValue(value = "DIAMETER_GROOVY_PLUGIN")
		DIAMETER_GROOVY_PLUGIN(5, "DIAMETER_GROOVY_PLUGIN"),
		
		@XmlEnumValue(value = "RADIUS_TRANSACTION_LOGGER")
		RADIUS_TRANSACTION_LOGGER(6, "RADIUS_TRANSACTION_LOGGER"),
		
		@XmlEnumValue(value = "DIAMETER_TRANSACTION_LOGGER")
		DIAMETER_TRANSACTION_LOGGER(7, "DIAMETER_TRANSACTION_LOGGER"),
		
		@XmlEnumValue(value = "QUOTA_MANAGEMENT")
		QUOTA_MANAGEMENT(8, "QUOTA_MANAGEMENT"),
		
		@XmlEnumValue(value = "USER_STATISTICS_POST_AUTH_PLUGIN")
		USER_STATISTICS_POST_AUTH_PLUGIN(9, "USER_STATISTICS_POST_AUTH_PLUGIN")
		;
		
		private int typeId;
		private String typeName;
		
		PluginType(int typeId, String typeName) {
			this.typeId = typeId;
			this.typeName = typeName;
		}
		
		public String getTypeName() {
			return typeName;
		}
		
		private static final Map<Integer, PluginType> map;
		
		public static final PluginType[] VALUES = values();
		
		static {
			map = new HashMap<Integer, PluginType>();
			for (PluginType type : VALUES) {
				map.put(type.typeId, type);
			}
		}
		
		public static PluginType from(int name) {
			return map.get(name);
		}
	}
	
	public enum PluginMode {
		PRE("PRE"),
		POST("POST")
		;
		
		String pluginModeValue;
		
		private PluginMode(String pluginModeValue) {
			this.pluginModeValue = pluginModeValue;
		}
		
		public String getPluginModeValue() {
			return pluginModeValue;
		}
	}
}