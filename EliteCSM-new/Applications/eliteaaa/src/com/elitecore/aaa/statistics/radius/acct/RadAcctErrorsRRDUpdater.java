package com.elitecore.aaa.statistics.radius.acct;

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

public class RadAcctErrorsRRDUpdater  extends BaseRRDUpdater {
	private static final String MODULE = "RadAcctRespTimeRRDUpdater";
	private boolean isInitialized;
	private static final String rrdFileName = "acct-errors.rrd";
	private String rrdFileLocation;

	public RadAcctErrorsRRDUpdater(String strServerHome) {
		isInitialized = false;
		rrdFileLocation = strServerHome + File.separator + "system" + File.separator + "rrd" + File.separator + "radius"+ File.separator+"radacct"+ File.separator +rrdFileName;
	}

	public void init(){
		isInitialized = false;
		List<DsDef> dsList = new ArrayList<DsDef>();
		DsDef badAuthenticators = new DsDef("BadAcctenticators", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
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
				LogManager.getLogger().warn(MODULE, "Error in initializing Acct Service RRD Updater");
			LogManager.getLogger().trace(MODULE, ioExp);
		}
	}

	public void update(Observable o, Object arg) {
		if(isInitialized == false)
			return;
		try{
			RadAcctErrorsData newServiceStatistics = (RadAcctErrorsData) o;

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
