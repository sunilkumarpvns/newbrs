package com.elitecore.aaa.radius.drivers;

import java.text.SimpleDateFormat;
import java.util.List;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.HttpAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.drivers.conf.impl.RadHttpAuthDriverConfigurationData;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

public class RadHttpAuthDriver extends HttpAuthDriver{
	
	public static final String MODULE = "RAD-HTTP-AUTH-DRIVER"; 
	private RadHttpAuthDriverConfigurationData radHttpAuthDriverConfiguration = null;
	private final AAAServerContext serverContext;
	
	public RadHttpAuthDriver(AAAServerContext serverContext, String driverInstanceId) {
		super(serverContext);
		this.serverContext = serverContext;
		this.radHttpAuthDriverConfiguration = (RadHttpAuthDriverConfigurationData)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId);
	}

	@Override
	protected String getValueForIdentityAttribute(
			ServiceRequest serviceRequest, String userIdentity) {
		IRadiusAttribute identityAttribute =  ((RadServiceRequest)serviceRequest).getRadiusAttribute(userIdentity, true);
		if(identityAttribute != null)
			return identityAttribute.getStringValue();
		else
			return null;
	}
	@Override 
	protected void initInternal()throws DriverInitializationFailedException{	
		super.initInternal();
		if(!(radHttpAuthDriverConfiguration.getAccountDataFieldMapping().getFieldMapping().size() > 0)){
			throw new DriverInitializationFailedException("No Parameter Index Mapping with Logical Name configured.");
		}
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully: ");
	}
	@Override
	public int getType() {
		return DriverTypes.RAD_HTTP_AUTH_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RAD_HTTP_AUTH_DRIVER.name();
	}
	
	@Override
	protected int getStatusCheckDuration() {
		return radHttpAuthDriverConfiguration.getStatusCheckDuration();
	}
	
	@Override
	public String getDriverInstanceId() {
		return this.radHttpAuthDriverConfiguration.getDriverInstanceId();
	}
	
	@Override
	public String getName() {
		return this.radHttpAuthDriverConfiguration.getDriverName();
	}

	@Override
	public AccountDataFieldMapping getAccountDataFieldMapping() {
		return this.radHttpAuthDriverConfiguration.getAccountDataFieldMapping();
	}

	@Override
	public String getHttpUrl() {
		return this.radHttpAuthDriverConfiguration.getHttpURL();
	}

	@Override
	public SimpleDateFormat[] getExpiryPatterns() {
		return radHttpAuthDriverConfiguration.getExpiryPatterns();
	}

	@Override
	public int getMaxQueryTimeoutCount() {
		return radHttpAuthDriverConfiguration.getMaxQueryTimeoutCount();
	}

	@Override
	public List<String> getUserIdentityAttributes() {
		return radHttpAuthDriverConfiguration.getUserIdentityAttributes();
	}
	@Override
	public void reInit() {
		this.radHttpAuthDriverConfiguration = (RadHttpAuthDriverConfigurationData)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(getDriverInstanceId());
	}

}
