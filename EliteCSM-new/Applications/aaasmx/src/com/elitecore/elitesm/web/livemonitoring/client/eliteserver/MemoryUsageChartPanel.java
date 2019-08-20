package com.elitecore.elitesm.web.livemonitoring.client.eliteserver;

import java.util.Arrays;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.livemonitoring.client.AbstractChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.GraphConstants;

public class MemoryUsageChartPanel extends AbstractChartPanel{
	
	
	
	private static final String  title = "<b> Memory Summary</b>";
	private static final String MODULE = "MemorySummaryChartPanel";
	
	
	private Curve memoryCurve;
	
	
	public MemoryUsageChartPanel() {
		super();
		try{
			this.setBorderColor("#000000");
			setChartTitle(title);

			memoryCurve = addCurve("Memory");
			getXAxis().setHasGridlines(true);
			getYAxis().setAxisLabel("<center>M<br>e<br>m<br><br>U<br>s<br>a<br>g<br>e</center>");
			getYAxis().setTickLabelFormat("# KB");
			setChartTitle(title);

			this.update();
			this.setVisible(true);
			Log.info("MemorySummaryChartPanel created.");
		}catch(Exception e){
			Log.error("Error in creating MemorySummaryChartPanel", e);
		}
		
	}
	
	

	@Override
	public String getGroupKey() {
		return this.SERVER_STATISTICS_GROUP;
	}

	@Override
	public void updateData(List<Long[]> result) throws Exception{
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
						Long memorySummary = data[GraphConstants.MEMORY_USAGE_DATA_INDEX];
						memoryCurve.addPoint(timestamp,memorySummary);
						setLastTimestamp(timestamp);
					}
				}
			}
			
			  
			setGraphTimeSpan();
			
			this.update();
		}catch(Exception e){
			Log.error("Error in setdata for MemorySummaryChartPanel", e);
		}

	}

	
	
	
	

}
