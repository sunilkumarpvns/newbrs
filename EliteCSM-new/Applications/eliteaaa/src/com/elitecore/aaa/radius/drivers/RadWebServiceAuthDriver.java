package com.elitecore.aaa.radius.drivers;

import java.text.SimpleDateFormat;
import java.util.List;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.WebServiceAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.drivers.conf.RadWebServiceAuthDriverConfiguration;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

public class RadWebServiceAuthDriver extends WebServiceAuthDriver{
	private static final String MODULE = "RAD-WEB-SERVICE-AUTH-DRVR";
	private final AAAServerContext serverContext;
	private RadWebServiceAuthDriverConfiguration webServiceAuthDriverConfiguration;

	public RadWebServiceAuthDriver(AAAServerContext serverContext, String driverInstanceId) {
		super(serverContext);
		this.serverContext = serverContext;
		webServiceAuthDriverConfiguration = (RadWebServiceAuthDriverConfiguration)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId);
	}

	@Override
	protected void initInternal() throws DriverInitializationFailedException{
		super.initInternal();
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}
	
	@Override
	public String getDriverInstanceId() {
		return webServiceAuthDriverConfiguration.getDriverInstanceId();
	}

	@Override
	public String getServiceAddress() {
		return webServiceAuthDriverConfiguration.getServiceAddress();
	}


	@Override
	public String getName() {
		return webServiceAuthDriverConfiguration.getDriverName();
	}

	@Override
	protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity){
		IRadiusAttribute identityAttribute = ((RadAuthRequest) serviceRequest).getRadiusAttribute(userIdentity, true);
		if(identityAttribute != null)
			return identityAttribute.getStringValue();
		else
			return null;
	}

	@Override
	public int getType() {
		return webServiceAuthDriverConfiguration.getDriverType().value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RAD_WEBSERVICE_AUTH_DRIVER.name();
	}

	
	@Override
	public String getUserIdentityValue(ServiceRequest request) {
		return getValueForIdentityAttribute(request, webServiceAuthDriverConfiguration.getIMSIAttribute());
	}
	
	

	@Override
	public AccountDataFieldMapping getAccountDataFieldMapping() {
		return webServiceAuthDriverConfiguration.getAccountDataFieldMapping();
	}

	@Override
	public int getMaxQueryTimeoutCount() {
		return webServiceAuthDriverConfiguration.getMaxQueryTimeoutCount();
	}

	@Override
	protected SimpleDateFormat[] getExpiryDatePatterns() {
		return webServiceAuthDriverConfiguration.getExpiryDatePatterns();
	}

	@Override
	protected int getStatusChkDuration() {
		return webServiceAuthDriverConfiguration.getStatusCheckDuration();
	}

	@Override
	protected List<String> getUserIdentityAttributes() {
		return webServiceAuthDriverConfiguration.getUserIdentityAttributes();
	}
	
	@Override
	public void reInit() {
		this.webServiceAuthDriverConfiguration = (RadWebServiceAuthDriverConfiguration)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(getDriverInstanceId());
	}

	
}
