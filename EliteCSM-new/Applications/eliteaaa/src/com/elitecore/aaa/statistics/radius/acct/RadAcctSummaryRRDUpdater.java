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

public class RadAcctSummaryRRDUpdater  extends BaseRRDUpdater {
	private static final String MODULE = "RadAcctSummaryRRDUpdater";
	private boolean isInitialized;
	private static final String rrdFileName = "acct-summary.rrd";
	private String rrdFileLocation;

	public RadAcctSummaryRRDUpdater(String strServerHome) {
		isInitialized = false;
		rrdFileLocation = strServerHome + File.separator + "system" + File.separator + "rrd" + File.separator + "radius"+ File.separator+"radacct"+ File.separator +rrdFileName;
	}

	public void init(){
		isInitialized = false;
		List<DsDef> dsList = new ArrayList<DsDef>();
		
		DsDef accountingStart = new DsDef("Start", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef accountingStop = new DsDef("Stop", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef accountingIntrim = new DsDef("Intrim", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef accountingRequest = new DsDef("Request", DsType.GAUGE, 2, 0, Double.MAX_VALUE);
		DsDef dropped = new DsDef("Dropped", DsType.GAUGE, 2, 0, Double.MAX_VALUE);

		dsList.add(accountingStart);
		dsList.add(accountingStop);
		dsList.add(accountingIntrim);
		dsList.add(accountingRequest);
		dsList.add(dropped);
		

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
			RadAcctSummaryData newServiceStatistics = (RadAcctSummaryData) o;

			RRDManager.getInstance().insertIntoRRDFile(rrdFileLocation,
					newServiceStatistics.getAccountingStart(),
					newServiceStatistics.getAccountingStop(),
					newServiceStatistics.getAccountingIntrim(),
					newServiceStatistics.getAccountingRequest(),
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
