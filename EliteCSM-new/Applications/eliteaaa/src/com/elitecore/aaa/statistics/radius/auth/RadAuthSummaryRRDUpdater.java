package com.elitecore.aaa.statistics.radius.auth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observable;

import org.rrd4j.DsType;
import org.rrd4j.core.ArcDef;
import org.rrd4j.core.DsDef;

import com.elitecore.aaa.statistics.BaseRRDUpdater;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.rrd.RRDManager;

public class RadAuthSummaryRRDUpdater  extends BaseRRDUpdater {
	private static final String MODULE = "RadAuthSummaryRRDUpdater";
	private boolean isInitialized;
	private static final String rrdFileName = "auth-summary.rrd";
	private String rrdFileLocation;

	public RadAuthSummaryRRDUpdater(String strServerHome) {
		isInitialized = false;
		rrdFileLocation = strServerHome + File.separator + "system" + File.separator + "rrd" + File.separator + "radius"+ File.separator+"radauth"+ File.separator +rrdFileName;
	}

	public void init(){
		isInitialized = false;
		List<DsDef> dsList = new ArrayList<DsDef>();
		
		DsDef accessRequest = new DsDef("AccessRequest", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef accessAccept = new DsDef("AccessAccept", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef accessReject = new DsDef("AccessReject", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef accessChallenge = new DsDef("AccessChallenge", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef dropped = new DsDef("Dropped", DsType.GAUGE, 2, 0, Double.MAX_VALUE);

		dsList.add(accessRequest);
		dsList.add(accessAccept);
		dsList.add(accessReject);
		dsList.add(accessChallenge);
		dsList.add(dropped);
		

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
			RadAuthSummaryData newServiceStatistics = (RadAuthSummaryData) o;

			RRDManager.getInstance().insertIntoRRDFile(rrdFileLocation,
					newServiceStatistics.getAccessRequest(),
					newServiceStatistics.getAccessAccept(),
					newServiceStatistics.getAccessReject(),
					newServiceStatistics.getAccessChallenge(),
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
	
	public static void main(String args[]) throws Exception{
		RadAuthSummaryRRDUpdater radAuthRespTimeRRDUpdater = new RadAuthSummaryRRDUpdater("/media/2CF441B3F4417FD6/EliteAAA/Trunk/Applications/eliteaaa");
		Date endDate = new Date();
		
		Calendar calendor = new GregorianCalendar();
		
		calendor.set(2010, 10, 15,2,10);
		
		Date startDate = calendor.getTime();
		System.out.println("startDate : "+startDate);
		System.out.println("endDate   : "+endDate);
		List<Long[]> longList =  radAuthRespTimeRRDUpdater.getData(startDate, endDate);
		
		System.out.println(" Long List "+longList.size());
		
		
	}

}
