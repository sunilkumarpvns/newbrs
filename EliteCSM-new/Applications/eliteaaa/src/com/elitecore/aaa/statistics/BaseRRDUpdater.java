package com.elitecore.aaa.statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.ArcDef;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.Util;

import com.elitecore.core.util.rrd.RRDManager;

public abstract class BaseRRDUpdater  implements StatisticsObserver{
	 public abstract String getRRDFileLocation();
	
	 protected List<ArcDef> getArchiveList(){
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
			
			return arcList;
	 }
	 
	 public List<Long[]> getData (Date startDate, Date endDate) throws IOException {
		 Calendar calendor = new GregorianCalendar();
		 calendor.setTime(startDate);
		 long starTimestamp = Util.getTimestamp(calendor);

		 calendor.setTime(endDate);
		 long endTimestamp = Util.getTimestamp(calendor);

		 FetchData fetchdata =RRDManager.getInstance().fetchData(getRRDFileLocation(), ConsolFun.AVERAGE, starTimestamp, endTimestamp);
		 
		 double data[][] = fetchdata.getValues();
		 List<Long[]> dataList = new ArrayList<Long[]>();
		 
		 if(data!=null){
			 for(int i=0;i<data.length;i++){

				 Long[] sampleData = new Long[data[i].length];
				 System.out.println("sample data length :"+sampleData.length);
				 for(int j=0;j<sampleData.length;j++){
					 sampleData[j] = (long)data[i][j];
				 }
				 dataList.add(sampleData);

			 }
		 }
		 return dataList;
	 }
	 
	 @Override
	public boolean isExpired() {
		return false;
	}
}
