package com.elitecore.elitesm.web.livemonitoring.client.eliteserver;

import java.util.Arrays;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.livemonitoring.client.AbstractChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.GraphConstants;

public class ThreadStatisticChartPanel extends AbstractChartPanel{
	
	
	
	private static final String  title = "<b> Thread Statistics</b>";
	private static final String MODULE = "ThreadStatisticChartPanel";
	private Curve activeThreadCurve;
	private Curve peakThreadCurve;
	public ThreadStatisticChartPanel() {
		
		super();
		try{
			this.setBorderColor("#000000");
			setChartTitle(title);

			activeThreadCurve = addCurve("Active Threads");
			peakThreadCurve=addCurve("Peak Threads");
			
			getXAxis().setHasGridlines(true);
			getYAxis().setAxisLabel("<center>T<br>h<br>r<br>e<br>a<br>d<br>s<br></center>");
			getYAxis().setTickLabelFormat("# ");
			setChartTitle(title);

			this.update();
			this.setVisible(true);
			Log.info("ThreadStatisticChartPanel created.");
		}catch(Exception e){
			Log.error("Error in creating ThreadStatisticChartPanel", e);
		}
		
	}
	
	@Override
	public String getGroupKey() {
		return this.SERVER_STATISTICS_GROUP;
	}
	@Override
	public void updateData(List<Long[]> result) throws Exception {
		setData(result);
	}
	protected void finalize() throws Throwable {
		super.finalize();
		
	}
	
	public void setData(List<Long[]>result){
		try{
			if(result!=null){
				int size = result.size();
				for(int i=0;i<size;i++){
					Long[] data= result.get(i);
					
					Log.debug(MODULE+" :"+Arrays.toString(data));
					if(data!=null){
						Long timestamp = data[GraphConstants.TIMESTAMP_DATA_INDEX];
						Long activeThreadSummary = data[GraphConstants.THREAD_STATISTIC_INDEX];
						Long peakThreadSummary = data[GraphConstants.PEAK_THREAD_STATISTIC_INDEX];
						activeThreadCurve.addPoint(timestamp,activeThreadSummary);
						peakThreadCurve.addPoint(timestamp,peakThreadSummary);
						setLastTimestamp(timestamp);
					}
				}
			}
			
			  
			setGraphTimeSpan();
			
			this.update();
		}catch(Exception e){
			Log.error("Error in setdata for ThreadStatisticChartPanel", e);
		}

	}
	

}
