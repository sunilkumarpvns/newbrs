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

public class RadAuthRespTimeRRDUpdater  extends BaseRRDUpdater {
	private static final String MODULE = "RadAuthRespTimeRRDUpdater";
	private boolean isInitialized;
	private static final String rrdFileName = "auth-resp-time.rrd";
	private String rrdFileLocation;

	public RadAuthRespTimeRRDUpdater(String strServerHome) {
		isInitialized = false;
		rrdFileLocation = strServerHome + File.separator + "system" + File.separator + "rrd" + File.separator + "radius"+ File.separator+"radauth"+ File.separator +rrdFileName;
	}

	public void init(){
		isInitialized = false;
		List<DsDef> dsList = new ArrayList<DsDef>();
		DsDef totalAvgResponseTime = new DsDef("TotalAvgRespTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef databaseAvgResponseTime = new DsDef("DBAvgRespTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef ldapAvgResponseTime = new DsDef("LdapAvgResponseTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef usersFileAvgTime = new DsDef("UsersFileAvgTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef resourceServerAvgResponseTime = new DsDef("RMAvgRespTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef prePluginAvgTime = new DsDef("PrePluginAvgTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef postPluginAvgTime = new DsDef("PostPluginAvgTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef queueAvgTime = new DsDef("QueueAvgTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);

		dsList.add(totalAvgResponseTime);
		dsList.add(databaseAvgResponseTime);
		dsList.add(ldapAvgResponseTime);
		dsList.add(usersFileAvgTime);
		dsList.add(resourceServerAvgResponseTime);
		dsList.add(prePluginAvgTime);
		dsList.add(postPluginAvgTime);
		dsList.add(queueAvgTime);

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
			RadAuthRespTimeData newServiceStatistics = (RadAuthRespTimeData) o;

			RRDManager.getInstance().insertIntoRRDFile(rrdFileLocation,
					newServiceStatistics.getTotalAvgResponseTime(),
					newServiceStatistics.getDatabaseAvgResponseTime(),
					newServiceStatistics.getLdapAvgResponseTime(),
					newServiceStatistics.getUsersFileAvgResponseTime(),
					newServiceStatistics.getResourceServerAvgResponseTime(),
					newServiceStatistics.getPrePluginAvgTime(),
					newServiceStatistics.getPostPluginAvgTime(),
					newServiceStatistics.getQueueAvgTime()
					);

			
		}catch(Exception e){ 
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while updating RRD file. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	public static void main(String args[]) throws Exception{
		RadAuthRespTimeRRDUpdater radAuthRespTimeRRDUpdater = new RadAuthRespTimeRRDUpdater("/media/2CF441B3F4417FD6/EliteAAA/Trunk/Applications/eliteaaa");
		Date endDate = new Date();
		
		Calendar calendor = new GregorianCalendar();
		
		calendor.set(2010, 10, 15,2,10);
		
		Date startDate = calendor.getTime();
		System.out.println("startDate : "+startDate);
		System.out.println("endDate   : "+endDate);
		List<Long[]> longList =  radAuthRespTimeRRDUpdater.getData(startDate, endDate);
		
		System.out.println(" Long List "+longList.size());
		
		
	}

	@Override
	public String getRRDFileLocation() {
		return rrdFileLocation;
	}
	
}
