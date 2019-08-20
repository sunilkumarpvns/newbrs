package com.elitecore.aaa.radius.drivers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.DBAuthDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.DriverFactory;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.driverx.IEliteDriver;

/**
 * 
 * @author narendra.pathai
 *
 */
public class RadiusDriverFactory implements DriverFactory {

	private final AAAServerContext serverContext;

	public RadiusDriverFactory(@Nonnull AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}

	@Override
	public @Nullable IEliteDriver createDriver(DriverTypes driverType, String driverInstanceId) {
		switch (driverType) {
		case RAD_OPENDB_AUTH_DRIVER:
			return createRadDBAuthDriver(driverInstanceId);
		case RAD_LDAP_AUTH_DRIVER:
			return new RadLDAPAuthDrvier(serverContext, driverInstanceId);
		case RAD_MAPGW_AUTH_DRIVER: 
			return new RadMAPGWAuthDriver(serverContext, driverInstanceId);					 
		case RAD_HTTP_AUTH_DRIVER: 
			return new RadHttpAuthDriver(serverContext, driverInstanceId);					 
		case RAD_WEBSERVICE_AUTH_DRIVER: 
			return new RadWebServiceAuthDriver(serverContext, driverInstanceId);					 
		case RAD_USERFILE_AUTH_DRIVER:
			return new RadUserFileAuthDriver(serverContext,driverInstanceId);
		case RAD_HSS_AUTH_DRIVER:
			return new RadHSSAuthDriver(serverContext, driverInstanceId);
		case RAD_OPENDB_ACCT_DRIVER:
			return new RadDBAcctDriver(serverContext, driverInstanceId);
		case RAD_DETAIL_LOCAL_ACCT_DRIVER:
			return new RadDetailLocalAcctDriver(serverContext, driverInstanceId);
		case RAD_CLASSIC_CSV_ACCT_DRIVER:
			return new RadClassicCSVAcctDriver(serverContext, driverInstanceId);
		default:
			return null;
		}
	}

	private RadDBAuthDirver createRadDBAuthDriver(String driverInstanceId) {
		DBAuthDriverConfiguration driverConfiguration = (DBAuthDriverConfiguration) serverContext.getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration(driverInstanceId);
		DBDataSource datasource = serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDataSourceByName(driverConfiguration.getDSName());
		return new RadDBAuthDirver(serverContext, driverConfiguration, datasource, DBConnectionManager.getInstance(datasource.getDataSourceName()));
	}
}
