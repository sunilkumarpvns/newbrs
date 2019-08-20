package com.elitecore.elitesm.util;

import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.CrestelRatingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData;

/**
 * Class to store result data
 * @author Tejas.P.Shah
 *
 */
public class ResultObject {
	private boolean isValid;
	private String errorMsg;
	private DriverInstanceData driverInstance;
	private MappingGatewayAuthDriverData mappingGarewayAuthData;
	private DBAcctDriverData dbAcctDriverData;
	private ClassicCSVAcctDriverData classicCSVAcctDriverData;
	private WebServiceAuthDriverData webServiceAuthDriverData;
	private CrestelChargingDriverData crestelChargingDriverData;
	private DiameterChargingDriverData diameterChargingDriverData;
	private CrestelRatingDriverData crestelRatingDriverData;
	
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	public void setErrorMsg(String msg) {
		errorMsg = msg;
		
	}
	public boolean isError() {
		return isValid;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public MappingGatewayAuthDriverData getMappingGarewayAuthData() {
		return mappingGarewayAuthData;
	}

	public void setMappingGarewayAuthData(
			MappingGatewayAuthDriverData mappingGarewayAuthData) {
		this.mappingGarewayAuthData = mappingGarewayAuthData;
	}

	public DriverInstanceData getDriverInstance() {
		return driverInstance;
	}

	public void setDriverInstance(DriverInstanceData driverInstance) {
		this.driverInstance = driverInstance;
	}

	public DBAcctDriverData getDbAcctDriverData() {
		return dbAcctDriverData;
	}

	public void setDbAcctDriverData(DBAcctDriverData dbAcctDriverData) {
		this.dbAcctDriverData = dbAcctDriverData;
	}

	public ClassicCSVAcctDriverData getClassicCSVAcctDriverData() {
		return classicCSVAcctDriverData;
	}

	public void setClassicCSVAcctDriverData(ClassicCSVAcctDriverData classicCSVAcctDriverData) {
		this.classicCSVAcctDriverData = classicCSVAcctDriverData;
	}

	public WebServiceAuthDriverData getWebServiceAuthDriverData() {
		return webServiceAuthDriverData;
	}

	public void setWebServiceAuthDriverData(WebServiceAuthDriverData webServiceAuthDriverData) {
		this.webServiceAuthDriverData = webServiceAuthDriverData;
	}

	public CrestelChargingDriverData getCrestelChargingDriverData() {
		return crestelChargingDriverData;
	}

	public void setCrestelChargingDriverData(CrestelChargingDriverData crestelChargingDriverData) {
		this.crestelChargingDriverData = crestelChargingDriverData;
	}

	public DiameterChargingDriverData getDiameterChargingDriverData() {
		return diameterChargingDriverData;
	}

	public void setDiameterChargingDriverData(DiameterChargingDriverData diameterChargingDriverData) {
		this.diameterChargingDriverData = diameterChargingDriverData;
	}

	public CrestelRatingDriverData getCrestelRatingDriverData() {
		return crestelRatingDriverData;
	}

	public void setCrestelRatingDriverData(CrestelRatingDriverData crestelRatingDriverData) {
		this.crestelRatingDriverData = crestelRatingDriverData;
	}
}
