package com.elitecore.aaa.diameter.service.application.drivers;

import java.util.List;

import com.elitecore.aaa.core.conf.DBAuthDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.DBAuthDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

public class DiameterDbDriver extends DBAuthDriver{
	private static final String MODULE = "DIAMETER-DB-DRIVER";
	private DBAuthDriverConfiguration dbAuthDriverConfiguration;
	
	public DiameterDbDriver(AAAServerContext serverContext, DBAuthDriverConfiguration driverConfiguration, DBDataSource datasource, DBConnectionManager dbConnectionManager) {
		super(serverContext, datasource, dbConnectionManager);
		dbAuthDriverConfiguration = driverConfiguration;
	}
	
	@Override 
	protected void initInternal()throws DriverInitializationFailedException{	
		if(!(dbAuthDriverConfiguration.getAccountDataFieldMapping().getFieldNameList().size()>0)){
			throw new DriverInitializationFailedException("No DB Field Mapping Configured");
		}
		super.initInternal();
		LogManager.getLogger().info(MODULE, "Driver: " + getName() + " initialized successfully");
	}
	
	protected String getValueForIdentityAttribute(ServiceRequest serviceRequest, String userIdentity){
		IDiameterAVP identityAttribute =  ((ApplicationRequest)serviceRequest).getDiameterRequest().getAVP(userIdentity, true);
		if(identityAttribute != null)
			return identityAttribute.getStringValue();
		else
			return null;
	}
	
	@Override
	public String getTablename() {
		return this.dbAuthDriverConfiguration.getTablename();
	}
	@Override
	public int getDbQueryTimeout() {
		return this.dbAuthDriverConfiguration.getDbQueryTimeout();
	}
	@Override
	public long getMaxQueryTimeoutCount() {
		return this.dbAuthDriverConfiguration.getMaxQueryTimeoutCount();
	}
	
	@Override
	public String getDriverInstanceId() {
		return this.dbAuthDriverConfiguration.getDriverInstanceId();
	}
	
	@Override
	public AccountDataFieldMapping getAccountDataFieldMapping() {	 
		return dbAuthDriverConfiguration.getAccountDataFieldMapping();
	}
	@Override
	public String getDSName() {		
		return dbAuthDriverConfiguration.getDSName();
	}

	@Override
	public String getName() {
		return dbAuthDriverConfiguration.getDriverName();
	}
	
	@Override
	public String toString() {
		return dbAuthDriverConfiguration.toString();
	}

	@Override
	public int getType() {
		return DriverTypes.DIAMETER_DB_DRIVER.value;
	}
	
	@Override
	public String getTypeName() {
		return DriverTypes.DIAMETER_DB_DRIVER.name();
	}
	@Override
	public String getProfileLookUpColumnName() {
		return dbAuthDriverConfiguration.getProfileLookUpColumnName();
	}
	@Override
	public String getPrimaryKeyColumn() {
		return dbAuthDriverConfiguration.getPrimaryKeyColumn();
	}
	@Override
	public String getSequenceName() {
		return dbAuthDriverConfiguration.getSequenceName();
	}
	@Override
	public List<String> getUserIdentityAttributes() {
		return dbAuthDriverConfiguration.getUserIdentityAttributes();
	}
	@Override
	public void reInit() {
		this.dbAuthDriverConfiguration =  (DBAuthDriverConfiguration)((AAAServerContext)getServerContext()).getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(getDriverInstanceId());	
	}
}
