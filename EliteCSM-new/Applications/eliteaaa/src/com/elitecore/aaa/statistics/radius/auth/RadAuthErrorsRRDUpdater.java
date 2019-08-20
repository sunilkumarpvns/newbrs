package com.elitecore.aaa.statistics.radius.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.ArcDef;
import org.rrd4j.core.DsDef;

import com.elitecore.aaa.statistics.BaseRRDUpdater;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.rrd.RRDManager;

public class RadAuthErrorsRRDUpdater  extends BaseRRDUpdater {
	private static final String MODULE = "RadAuthRespTimeRRDUpdater";
	private boolean isInitialized;
	private static final String rrdFileName = "auth-errors.rrd";
	private String rrdFileLocation;

	public RadAuthErrorsRRDUpdater(String strServerHome) {
		isInitialized = false;
		rrdFileLocation = strServerHome + File.separator + "system" + File.separator + "rrd" + File.separator + "radius"+ File.separator+"radauth"+ File.separator +rrdFileName;
	}

	public void init(){
		isInitialized = false;
		List<DsDef> dsList = new ArrayList<DsDef>();
		DsDef badAuthenticators = new DsDef("BadAuthenticators", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef duplicateRequests = new DsDef("Duplicate", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef malformedRequests = new DsDef("Malformed", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef invalidRequests = new DsDef("Invalid", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef unknownRequests = new DsDef("Unknown", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef dropped = new DsDef("Dropped", DsType.GAUGE, 2, 0, Double.MAX_VALUE);

		dsList.add(badAuthenticators);
		dsList.add(duplicateRequests);
		dsList.add(malformedRequests);
		dsList.add(invalidRequests);
		dsList.add(unknownRequests);
		dsList.add(dropped);
		

		List<ArcDef> arcList = new ArrayList<ArcDef>();

		ArcDef perMinArchive = new ArcDef(ConsolFun.AVERAGE, 0.1, 1, 60);
		arcList.add(perMinArchive);

		ArcDef perHourArchive = new ArcDef(ConsolFun.AVERAGE, 0.1, 3, 1200);
		arcList.add(perHourArchive);

		ArcDef perDayArchive = new ArcDef(ConsolFun.AVERAGE, 0.1, 20, 4320);
		arcList.add(perDayArchive);

		ArcDef perWeekArchive = new ArcDef(ConsolFun.AVERAGE, 0.1, 50, 12096);
		arcList.add(perWeekArchive);

		ArcDef perMonthArchive = new ArcDef(ConsolFun.AVERAGE, 0.1, 100, 25920);
		arcList.add(perMonthArchive);

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
			RadAuthErrorsData newServiceStatistics = (RadAuthErrorsData) o;

			RRDManager.getInstance().insertIntoRRDFile(rrdFileLocation,
					newServiceStatistics.getBadAuthenticators(),
					newServiceStatistics.getDuplicateRequests(),
					newServiceStatistics.getMalformedRequests(),
					newServiceStatistics.getInvalidRequests(),
					newServiceStatistics.getUnknownRequests(),
					newServiceStatistics.getDropped()
					);

			
		}catch(Exception e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while updating RRD file. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	@Override
	public String getRRDFileLocation() {
		return rrdFileLocation;
	}
}
