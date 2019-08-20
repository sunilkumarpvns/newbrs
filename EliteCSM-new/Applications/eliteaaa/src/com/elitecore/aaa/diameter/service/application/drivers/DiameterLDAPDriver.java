package com.elitecore.aaa.diameter.service.application.drivers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.conf.LDAPAuthDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.LDAPAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

public class DiameterLDAPDriver extends LDAPAuthDriver {
	public static final String MODULE = "DIAMETER-LDAP-DRIVER";		
	private LDAPAuthDriverConfiguration nasLDAPAuthDriverConfiguration;
	private LDAPDataSource ldapDataSource;
	public DiameterLDAPDriver(AAAServerContext serverContext, String driverInstanceId){
		super(serverContext);
		nasLDAPAuthDriverConfiguration =  (LDAPAuthDriverConfiguration)((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverInstanceId);
		this.ldapDataSource = ((AAAServerContext)getServerContext()).getServerConfiguration().getLDAPDSConfiguration().getDatasourceByName(nasLDAPAuthDriverConfiguration.getDSName());
	}
	
	@Override 
	protected void initInternal()throws DriverInitializationFailedException {		
		super.initInternal();
		if(!(nasLDAPAuthDriverConfiguration.getAccountDataFieldMapping().getFieldNameList().size()>0)){
			throw new DriverInitializationFailedException("No DB Field Mapping Configured");
		}
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully: ");
	}
	
	
	protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity){
		IDiameterAVP identityAttribute =  ((ApplicationRequest)serviceRequest).getDiameterRequest().getAVP(userIdentity, true);
		if(identityAttribute!= null)
			return identityAttribute.getStringValue();
		else
			return null;
	}
	
	@Override
	protected String getDSName() {	
		return ldapDataSource.getDataSourceName();
	}

	@Override
	protected AccountDataFieldMapping getAccountDataFieldMapping() {
		return nasLDAPAuthDriverConfiguration.getAccountDataFieldMapping();
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
		return nasLDAPAuthDriverConfiguration.getExpiryDatePatterns();
	}
	
	@Override
	protected ArrayList<String> getLdapEntryAttributes() {
		return nasLDAPAuthDriverConfiguration.getAccountDataFieldMapping().getFieldNameList();
	}

	@Override
	public String getName() {
		return nasLDAPAuthDriverConfiguration.getDriverName();
	}

	@Override
	public String toString() {
		return nasLDAPAuthDriverConfiguration.toString();
	}

	@Override
	protected int getStatusChkDuration() {
		return nasLDAPAuthDriverConfiguration.getStatusCheckDuration();
	}

	

	@Override
	public String getDriverInstanceId() {		
		return this.nasLDAPAuthDriverConfiguration.getDriverInstanceId();
	}

	@Override
	protected String getLdapDsId() {
		return this.ldapDataSource.getDataSourceId();
	}

	@Override
	public int getType() {
		return DriverTypes.DIAMETER_LDAP_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.DIAMETER_LDAP_DRIVER.name();
	}

	@Override
	protected long getMaxQueryTimeoutCount() {
		return this.nasLDAPAuthDriverConfiguration.getMaxQueryTimeoutCount();
	}

	@Override
	protected List<String> getUserIdentityAttributes() {
		return nasLDAPAuthDriverConfiguration.getUserIdentityAttributes();
	}
	
	@Override
	protected int getSearchScope() {
		return nasLDAPAuthDriverConfiguration.getSearchScope().value;
}

	@Override
	protected String getSearchFilter() {
		return nasLDAPAuthDriverConfiguration.getSearchFilter();
	}

	@Override
	protected LDAPDataSource getLDAPDataSource() {
		return ldapDataSource;
	}
	
	@Override
	public void reInit() {
		this.nasLDAPAuthDriverConfiguration = (LDAPAuthDriverConfiguration)((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(getDriverInstanceId());
	}
	
}
