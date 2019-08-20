package com.elitecore.aaa.statistics.radius.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.rrd4j.DsType;
import org.rrd4j.core.ArcDef;
import org.rrd4j.core.DsDef;

import com.elitecore.aaa.statistics.BaseRRDUpdater;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.rrd.RRDManager;

public class RadAuthRejectReasonsRRDUpdater  extends BaseRRDUpdater {
	private static final String MODULE = "RadAuthRespTimeRRDUpdater";
	private boolean isInitialized;
	private static final String rrdFileName = "auth-reject-reasons.rrd";
	private String rrdFileLocation;

	public RadAuthRejectReasonsRRDUpdater(String strServerHome) {
		isInitialized = false;
		rrdFileLocation = strServerHome + File.separator + "system" + File.separator + "rrd" + File.separator + "radius"+ File.separator+"radauth"+ File.separator +rrdFileName;
	}

	public void init(){
		isInitialized = false;
		List<DsDef> dsList = new ArrayList<DsDef>();
		
		DsDef userNotFound = new DsDef("UserNotFound", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef invalidPassword = new DsDef("InvalidPassword", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef invalidCHAPPassword = new DsDef("InvalidCHAP", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef invalidMSCHAPv1Password = new DsDef("InvalidMSCHAPv1", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef invalidMSCHAPv2Password = new DsDef("InvalidMSCHAPv2", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef invalidDigestPassword = new DsDef("InvalidDigest", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef eapFailure = new DsDef("EAPFailure", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef authenticationFailed = new DsDef("AuthenticationFailed", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef accountIsNotActive = new DsDef("AccountIsNotActive", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef accountExpired = new DsDef("AccountExpired", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef creditLimitExceeded = new DsDef("CreditLimitExceeded", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef digestFailure = new DsDef("DigestFailure", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef rmCommTimeOut = new DsDef("RMCommTimeOut", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		
		
		dsList.add(userNotFound);
		dsList.add(invalidPassword);
		dsList.add(invalidCHAPPassword);
		dsList.add(invalidMSCHAPv1Password);
		dsList.add(invalidMSCHAPv2Password);
		dsList.add(invalidDigestPassword);
		dsList.add(eapFailure);
		dsList.add(authenticationFailed);
		dsList.add(accountIsNotActive);
		dsList.add(accountExpired);
		dsList.add(creditLimitExceeded);
		dsList.add(digestFailure);
		dsList.add(rmCommTimeOut);
		
		List<ArcDef> arcList = getArchiveList();
		try{
			RRDManager.getInstance().createOrOpenRRDFile(rrdFileLocation, dsList, arcList);
			isInitialized = true;
		}catch(IOException ioExp){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Error in initializing Auth Service RRD Updater");
			LogManager.getLogger().trace(MODULE, ioExp);
		}
	}

	public void update(Observable o, Object arg) {
		if(isInitialized == false)
			return;
		try{
			RadAuthRejectReasonsData newServiceStatistics = (RadAuthRejectReasonsData) o;

			RRDManager.getInstance().insertIntoRRDFile(rrdFileLocation,
					newServiceStatistics.getUserNotFound(),
					newServiceStatistics.getInvalidPassword(),
					newServiceStatistics.getInvalidCHAPPassword(),
					newServiceStatistics.getInvalidMSCHAPv1Password(),
					newServiceStatistics.getInvalidMSCHAPv2Password(),
					newServiceStatistics.getInvalidDigestPassword(),
					newServiceStatistics.getEapFailure(),
					newServiceStatistics.getAuthenticationFailed(),
					newServiceStatistics.getAccountIsNotActive(),
					newServiceStatistics.getAccountExpired(),
					newServiceStatistics.getCreditLimitExceeded(),
					newServiceStatistics.getDigestFailure(),
					newServiceStatistics.getRmCommTimeOut()
					);

			
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while updating RRD file. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	public String getRRDFileLocation(){
		return rrdFileLocation;
	}

}
