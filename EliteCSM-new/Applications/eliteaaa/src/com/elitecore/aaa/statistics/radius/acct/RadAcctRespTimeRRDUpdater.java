package com.elitecore.aaa.statistics.radius.acct;

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

public class RadAcctRespTimeRRDUpdater  extends BaseRRDUpdater {
	private static final String MODULE = "RadAuthRespTimeRRDUpdater";
	private boolean isInitialized;
	private static final String rrdFileName = "acct-resp-time.rrd";
	private String rrdFileLocation;

	public RadAcctRespTimeRRDUpdater(String strServerHome) {
		isInitialized = false;
		rrdFileLocation = strServerHome + File.separator + "system" + File.separator + "rrd" + File.separator + "radius"+ File.separator+"radacct"+ File.separator +rrdFileName;
	}

	public void init(){
		isInitialized = false;
		List<DsDef> dsList = new ArrayList<DsDef>();
		DsDef totalAvgResponseTime = new DsDef("TotalAvgRespTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef databaseAvgResponseTime = new DsDef("DBAvgRespTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef rmCommAvgResponseTime = new DsDef("RMAvgRespTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef prePluginAvgTime = new DsDef("PrePluginAvgTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef postPluginAvgTime = new DsDef("PostPluginAvgTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef queueTime = new DsDef("QueueTime", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		

		dsList.add(totalAvgResponseTime);
		dsList.add(databaseAvgResponseTime);
		dsList.add(rmCommAvgResponseTime);
		dsList.add(prePluginAvgTime);
		dsList.add(postPluginAvgTime);
		dsList.add(queueTime);
		

		List<ArcDef> arcList = getArchiveList();
		
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
			RadAcctRespTimeData newServiceStatistics = (RadAcctRespTimeData) o;

			RRDManager.getInstance().insertIntoRRDFile(rrdFileLocation,
					newServiceStatistics.getTotalAvgResponseTime(),
					newServiceStatistics.getDatabaseAvgResponseTime(),
					newServiceStatistics.getRmCommAvgResponseTime(),
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

	@Override
	public String getRRDFileLocation() {
		return rrdFileLocation;
	}
}

