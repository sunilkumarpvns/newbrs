package com.elitecore.elitesm.web.livemonitoring.client.auth;

import java.util.Arrays;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.livemonitoring.client.AbstractChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.GraphConstants;

public class AuthResponseTimeChartPanel extends AbstractChartPanel{
	


	private static final String  title = "<b> Authentication Service Response Time</b>";
	private static final String MODULE = "AuthResponseTimeChartPanel";
	private Long startTimestamp = null;
	private Long lastTimestamp = null;
	private int chartIndex=0;
	private Curve avgReponseTimeCurve;
	private Curve avgQueueTimeCurve;
	private Curve avgRMCommTimeCurve;
	
	public AuthResponseTimeChartPanel(){
		super();
		try{
			this.setBorderColor("#000000");
			setChartTitle(title);
			avgReponseTimeCurve = addCurve("Total Avg Reponse Time");
			avgQueueTimeCurve   = addCurve("Queue Time");
			avgRMCommTimeCurve = addCurve("RM Communication Time");
			getXAxis().setHasGridlines(true);
			getYAxis().setAxisLabel("<center>R<br>e<br>s<br><br>T<br>i<br>m<br>e</center>");
			getYAxis().setTickLabelFormat("# ms");
			setChartTitle(title);

			this.update();
			this.setVisible(true);
			Log.info("AuthResponseTimeChartPanel created.");
		}catch(Exception e){
			Log.error("Error in creating AuthResponseTimeChartPanel", e);
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
						Long aveResponseTime = data[GraphConstants.IDX_AUTH_RESPONSETIME_TOTAL_REPSONSE_TIME];
						Long avgQueueTime = data[GraphConstants.IDX_AUTH_RESPONSETIME_QUEUE_TIME];
						Long avgRMCommTime = data[GraphConstants.IDX_AUTH_RESPONSETIME_RMCOMMUNICATION_TIME];
						
						avgReponseTimeCurve.addPoint(timestamp,aveResponseTime);
						avgQueueTimeCurve.addPoint(timestamp,avgQueueTime);
						avgRMCommTimeCurve.addPoint(timestamp,avgRMCommTime);
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
			Log.error("Error in setdata for AuthResponseTimeChartPanel", e);
		}

	}	
	
	public void updateData(List<Long[]> result) throws Exception{
		setData(result);
	}
	
	protected void finalize() throws Throwable {
		super.finalize();
		
	}
	public String getGroupKey(){
		return this.AUTH_STATISTICS_GROUP;
	}
}
