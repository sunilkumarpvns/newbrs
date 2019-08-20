package com.elitecore.aaa.statistics.radius;

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

public class MemoryUsageRRDUpdater  extends BaseRRDUpdater {
	private static final String MODULE = "MemoryUsageRRDUpdater";
	private boolean isInitialized;
	private static final String rrdFileName = "memory-usage.rrd";
	private String rrdFileLocation;

	public MemoryUsageRRDUpdater(String strServerHome) {
		isInitialized = false;
		rrdFileLocation = strServerHome + File.separator + "system" + File.separator + "rrd" + File.separator + "radius"+ File.separator +rrdFileName;
	}

	public void init(){
		isInitialized = false;
		List<DsDef> dsList = new ArrayList<DsDef>();
		DsDef totalMemoryUsed = new DsDef("TotalMemoryUsed", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		dsList.add(totalMemoryUsed);
		

		List<ArcDef> arcList = getArchiveList();

		try{
			RRDManager.getInstance().createOrOpenRRDFile(rrdFileLocation, dsList, arcList);
			isInitialized = true;
		}catch(IOException ioExp){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Error in initializing Memory Usage RRD Updater");
			LogManager.getLogger().trace(MODULE, ioExp);
		}
	}

	public void update(Observable o, Object arg) {
		if(isInitialized == false)
			return;
		try{
			MemoryUsageData newServiceStatistics = (MemoryUsageData) o;

			RRDManager.getInstance().insertIntoRRDFile(rrdFileLocation,
					newServiceStatistics.getTotalMemoryUsed()
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
