package com.elitecore.aaa.radius.drivers; 

import java.util.List;

import com.elitecore.aaa.core.conf.DBAuthDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.DBAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;


public class RadDBAuthDirver extends DBAuthDriver {
	public static final String MODULE = "RAD-DB-AUTH-DRIVER"; 
	private DBAuthDriverConfiguration radDbAuthDriverConfiguration = null;

	public RadDBAuthDirver(AAAServerContext serverContext, DBAuthDriverConfiguration configuration, DBDataSource datasource, DBConnectionManager dbConnectionManager) {
		super(serverContext, datasource, dbConnectionManager);
		this.radDbAuthDriverConfiguration = configuration;
	}

	@Override 
	protected void initInternal()throws DriverInitializationFailedException{	
		if(!(radDbAuthDriverConfiguration.getAccountDataFieldMapping().getFieldNameList().size()>0)){
			throw new DriverInitializationFailedException("No DB Field Mapping Configured");
		}			
		super.initInternal();
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
		
	}	
	protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity){
		IRadiusAttribute identityAttribute =  ((RadServiceRequest)serviceRequest).getRadiusAttribute(userIdentity, true);
		if(identityAttribute != null)
			return identityAttribute.getStringValue();
		else
			return null;
	}
	
	@Override
	public String getTablename() {
		return this.radDbAuthDriverConfiguration.getTablename();
	}
	@Override
	public int getDbQueryTimeout() {
		return this.radDbAuthDriverConfiguration.getDbQueryTimeout();
	}
	@Override
	public long getMaxQueryTimeoutCount() {
		return this.radDbAuthDriverConfiguration.getMaxQueryTimeoutCount();
	}
	
	@Override
	public String getDriverInstanceId() {
		return this.radDbAuthDriverConfiguration.getDriverInstanceId();
	}
	
	@Override
	public AccountDataFieldMapping getAccountDataFieldMapping() {	 
		return radDbAuthDriverConfiguration.getAccountDataFieldMapping();
	}
	@Override
	public String getDSName() {		
		return radDbAuthDriverConfiguration.getDSName();
	}
	
	@Override
	public String getName() {
		return radDbAuthDriverConfiguration.getDriverName();
	}
	
	@Override
	public String toString() {
		return radDbAuthDriverConfiguration.toString();
	}

	@Override
	public int getType() {
		return DriverTypes.RAD_OPENDB_AUTH_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.RAD_OPENDB_AUTH_DRIVER.name();
	}

	@Override
	public String getProfileLookUpColumnName() {
		return radDbAuthDriverConfiguration.getProfileLookUpColumnName();
	}

	@Override
	public String getPrimaryKeyColumn() {
		return radDbAuthDriverConfiguration.getPrimaryKeyColumn();
	}

	@Override
	public String getSequenceName() {
		return radDbAuthDriverConfiguration.getSequenceName();
	}

	@Override
	public List<String> getUserIdentityAttributes() {
		return radDbAuthDriverConfiguration.getUserIdentityAttributes();
	}
	
	@Override
	public void reInit() {

	}
}
