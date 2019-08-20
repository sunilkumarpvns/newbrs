package com.elitecore.aaa.diameter.service.drivers;

import com.elitecore.aaa.core.conf.DBAuthDriverConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.DriverFactory;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.drivers.DiameterCrestelOCSv2Driver;
import com.elitecore.aaa.diameter.service.application.drivers.DiameterCrestelRatingDriver;
import com.elitecore.aaa.diameter.service.application.drivers.DiameterDbDriver;
import com.elitecore.aaa.diameter.service.application.drivers.DiameterHSSAuthDriver;
import com.elitecore.aaa.diameter.service.application.drivers.DiameterLDAPDriver;
import com.elitecore.aaa.diameter.service.application.drivers.DiameterMAPGWAuthDriver;
import com.elitecore.aaa.diameter.service.application.drivers.DiameterUserFileDriver;
import com.elitecore.aaa.diameter.service.application.drivers.NasClassicCSVAcctDriver;
import com.elitecore.aaa.diameter.service.application.drivers.NasDbAcctDriver;
import com.elitecore.aaa.diameter.service.application.drivers.NasDetailLocalAcctDriver;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.driverx.IEliteDriver;

public class DiameterDriverFactory implements DriverFactory {

	private final AAAServerContext serverContext;
	
	public DiameterDriverFactory(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	@Override
	public IEliteDriver createDriver(DriverTypes driverType, String driverInstanceId) {
		
		switch (driverType) {
		case DIAMETER_DB_DRIVER:
			return createDBAuthDriver(driverInstanceId);
		case DIAMETER_LDAP_DRIVER:
			return new DiameterLDAPDriver(serverContext, driverInstanceId);
		case DIAMETER_MAPGW_AUTH_DRIVER:
			return new DiameterMAPGWAuthDriver(serverContext, driverInstanceId);
		case DIAMETER_USERFILE_DRIVER:
			return new DiameterUserFileDriver(serverContext, driverInstanceId); 
		case NAS_OPENDB_ACCT_DRIVER:
			return new NasDbAcctDriver(serverContext, driverInstanceId);
		case NAS_DETAIL_LOCAL_ACCT_DRIVER:
			return new NasDetailLocalAcctDriver(serverContext, driverInstanceId);
		case NAS_CLASSIC_CSV_ACCT_DRIVER:
			return new NasClassicCSVAcctDriver(serverContext, driverInstanceId);
		case CRESTEL_RATING_DRIVER:
			return new DiameterCrestelRatingDriver(serverContext, driverInstanceId);
		case DIAMETER_CRESTEL_OCSv2_DRIVER:
			return new DiameterCrestelOCSv2Driver(serverContext, driverInstanceId);
		case DIA_HSS_AUTH_DRIVER:
			return new DiameterHSSAuthDriver((AAAServerContext) serverContext, driverInstanceId);
		default:
			return null;
		}
	}

	private DiameterDbDriver createDBAuthDriver(String driverInstanceId) {
		DBAuthDriverConfiguration driverConfiguration = (DBAuthDriverConfiguration)serverContext.getServerConfiguration().getDiameterDriverConfiguration().getDriverConfiguration(driverInstanceId);
		DBDataSource datasource = serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDataSourceByName(driverConfiguration.getDSName());
		return new DiameterDbDriver(serverContext, driverConfiguration, datasource, DBConnectionManager.getInstance(datasource.getDataSourceName()));
	}
}
