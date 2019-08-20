package com.elitecore.aaa.radius.drivers;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.LDAPAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.drivers.conf.impl.RadLDAPAuthDriverConfigurationImpl;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

public class RadLDAPAuthDrvier extends LDAPAuthDriver {
	public static final String MODULE = "RAD-LDAP-AUTH-DRIVER";		
	private RadLDAPAuthDriverConfigurationImpl radLDAPAuthDriverConfiguration;
	private LDAPDataSource ldapDataSource;
	private final AAAServerContext serverContext;
	
	public RadLDAPAuthDrvier(AAAServerContext serverContext, String driverInstanceId){
		super(serverContext);
		this.serverContext = serverContext;				
		radLDAPAuthDriverConfiguration = (RadLDAPAuthDriverConfigurationImpl)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId);
		this.ldapDataSource = ((AAAServerContext)getServerContext()).getServerConfiguration().getLDAPDSConfiguration().getDatasourceByName(radLDAPAuthDriverConfiguration.getDSName());
	}
	
	@Override 
	protected void initInternal()throws DriverInitializationFailedException{		
		super.initInternal();
		if(!(radLDAPAuthDriverConfiguration.getAccountDataFieldMapping().getFieldNameList().size()>0)){
			throw new DriverInitializationFailedException("No DB Field Mapping Configured");
		}
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}
	
	
	protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity){
		IRadiusAttribute identityAttribute = ((RadServiceRequest)serviceRequest).getRadiusAttribute(userIdentity, true);
		if(identityAttribute!= null)
			return identityAttribute.getStringValue();
		else
			return null;
	}
	
	@Override
	protected String getDSName() {		
		return radLDAPAuthDriverConfiguration.getDSName();
	}

	@Override
	protected AccountDataFieldMapping getAccountDataFieldMapping() {
		return radLDAPAuthDriverConfiguration.getAccountDataFieldMapping();
	}

	@Override
	protected String getUserDN() {	
		return ldapDataSource.getUserPrefix();
	}

	@Override
	protected ArrayList<String> getDnDetailList() {	
		return ldapDataSource.getSearchBaseDnList();
	}
	@Override
	protected SimpleDateFormat[] getExpiryDatePatterns(){
		return radLDAPAuthDriverConfiguration.getExpiryDatePatterns();
	}
	
	@Override
	protected ArrayList<String> getLdapEntryAttributes() {
		return radLDAPAuthDriverConfiguration.getAccountDataFieldMapping().getFieldNameList();
	}

	@Override
	public String getName() {
		return radLDAPAuthDriverConfiguration.getDriverName();
	}

	@Override
	public String toString() {
		return radLDAPAuthDriverConfiguration.toString();
	}

	@Override
	protected int getStatusChkDuration() {
		return radLDAPAuthDriverConfiguration.getStatusCheckDuration();
	}

	@Override
	public String getDriverInstanceId() {		
		return this.radLDAPAuthDriverConfiguration.getDriverInstanceId();
	}

	@Override
	protected String getLdapDsId() {
		return this.ldapDataSource.getDataSourceId();
	}

	@Override
	public int getType() {
		return DriverTypes.RAD_LDAP_AUTH_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RAD_LDAP_AUTH_DRIVER.name();
	}

	@Override
	protected long getMaxQueryTimeoutCount() {
		return this.radLDAPAuthDriverConfiguration.getMaxQueryTimeoutCount();
	}

	@Override
	protected List<String> getUserIdentityAttributes() {
		return radLDAPAuthDriverConfiguration.getUserIdentityAttributes();
	}

	@Override
	protected int getSearchScope() {
		return radLDAPAuthDriverConfiguration.getSearchScope().value;
}

	@Override
	protected String getSearchFilter() {
		return radLDAPAuthDriverConfiguration.getSearchFilter();
	}

	@Override
	protected LDAPDataSource getLDAPDataSource() {
		return ldapDataSource;
	}
	
	@Override
	public void reInit() {
		this.radLDAPAuthDriverConfiguration = (RadLDAPAuthDriverConfigurationImpl)serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(getDriverInstanceId());
	}
}
