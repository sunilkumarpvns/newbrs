package com.elitecore.elitesm.web.livemonitoring.client.acct;

import java.util.Arrays;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.livemonitoring.client.AbstractChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.GraphConstants;

public class AcctSummaryChartPanel extends AbstractChartPanel{
	
	private static final String  title = "<b> Accounting Summary</b>";
	private static final String MODULE = "AcctSummaryChartPanel";
	private Long startTimestamp = null;
	private Long lastTimestamp = null;
	
	private Curve startCurve;
	private Curve stopCurve;
	private Curve intrimCurve;
	private Curve requestCurve;
	private Curve droppedCurve;
	
	
	public AcctSummaryChartPanel(){
		super();
		try{
			this.setBorderColor("#000000");
			setChartTitle(title);
			
			
			startCurve = addCurve("Start");
			stopCurve   = addCurve("Stop");
			intrimCurve = addCurve("Intrim");
			requestCurve = addCurve("Request");
			droppedCurve = addCurve("Dropped");
			
			getXAxis().setHasGridlines(true);
			getYAxis().setAxisLabel("N<br>u<br>m<br>b<br>e<br>r<br>s");
			getYAxis().setTickLabelFormat("# ");
			setChartTitle(title);

			this.update();
			this.setVisible(true);
			Log.info("AcctSummaryChartPanel created.");
		}catch(Exception e){
			Log.error("Error in creating AcctSummaryChartPanel", e);
		}
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
						Long start = data[GraphConstants.IDX_ACCT_SUMMARY_START];
						Long stop = data[GraphConstants.IDX_ACCT_SUMMARY_STOP];
						Long intrim = data[GraphConstants.IDX_ACCT_SUMMARY_INTRIM];
						Long request = data[GraphConstants.IDX_ACCT_SUMMARY_REQUEST];
						Long dropped = data[GraphConstants.IDX_ACCT_SUMMARY_DROPPED];
						
						startCurve.addPoint(timestamp,start);
						stopCurve.addPoint(timestamp,stop);
						intrimCurve.addPoint(timestamp,intrim);
						requestCurve.addPoint(timestamp,request);
						droppedCurve.addPoint(timestamp,dropped);
						lastTimestamp=timestamp;
					}
				}
			}
			if(startTimestamp==null && lastTimestamp!=null){
				startTimestamp = lastTimestamp-GRAPH_TIMESPAN;
			}else if(startTimestamp!=null && (lastTimestamp-startTimestamp)>=GRAPH_TIMESPAN){
				startTimestamp = startTimestamp + ((lastTimestamp - startTimestamp)-GRAPH_TIMESPAN);
			}
			if(startTimestamp!=null && lastTimestamp!=null){
				getXAxis().setAxisMin(startTimestamp);
				clearPointsForAllCurve(startTimestamp);
				getXAxis().setAxisMax(lastTimestamp);
			}
			this.update();
		}catch(Exception e){
			Log.error("Error in setdata for AcctSummaryChartPanel", e);
		}

	}	
	
	public void updateData(List<Long[]> result) throws Exception{
		setData(result);
	}
	
	protected void finalize() throws Throwable {
		super.finalize();
		
	}
	public String getGroupKey(){
		return this.ACCT_STATISTICS_GROUP;
	}
}
