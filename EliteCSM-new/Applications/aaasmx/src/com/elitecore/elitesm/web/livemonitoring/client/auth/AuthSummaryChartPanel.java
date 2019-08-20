package com.elitecore.elitesm.web.livemonitoring.client.auth;

import java.util.Arrays;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.livemonitoring.client.AbstractChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.GraphConstants;

public class AuthSummaryChartPanel extends AbstractChartPanel{
	
	private static final String  title = "<b> Authentication Summary</b>";
	private static final String MODULE = "AuthSummaryChartPanel";
	private Long startTimestamp = null;
	private Long lastTimestamp = null;

	private Curve accessRequestCurve;
	private Curve accessAcceptCurve;
	private Curve accessRejectCurve;
	private Curve accessChallengeCurve;
	private Curve droppedCurve;
	
	
	public AuthSummaryChartPanel(){
		super();
		try{
			this.setBorderColor("#000000");
			setChartTitle(title);
			
			
			accessRequestCurve = addCurve("Access Request","#4169E1",1);
			accessAcceptCurve   = addCurve("Accept");
			accessRejectCurve = addCurve("Reject");
			accessChallengeCurve = addCurve("Challenge");
			droppedCurve = addCurve("Dropped");

			
			
			getXAxis().setHasGridlines(true);
			getYAxis().setAxisLabel("N<br>u<br>m<br>b<br>e<br>r");
			getYAxis().setTickLabelFormat("# ");
			

			setChartTitle(title);

			this.update();
			this.setVisible(true);
			Log.info("AuthSummaryChartPanel created.");
		}catch(Exception e){
			Log.error("Error in creating AuthSummaryChartPanel", e);
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
						Long accessRequest = data[GraphConstants.IDX_AUTH_SUMMARY_ACCESS_REQUEST];
						Long accessAccept = data[GraphConstants.IDX_AUTH_SUMMARY_ACCESS_ACCEPT];
						Long accessReject = data[GraphConstants.IDX_AUTH_SUMMARY_ACCESS_REJECT];
						Long accessChallenge = data[GraphConstants.IDX_AUTH_SUMMARY_ACCESS_CHALLENGE];
						Long dropped = data[GraphConstants.IDX_AUTH_SUMMARY_DROPPED];
						
						accessRequestCurve.addPoint(timestamp,accessRequest);
						accessAcceptCurve.addPoint(timestamp,accessAccept);
						accessRejectCurve.addPoint(timestamp,accessReject);
						accessChallengeCurve.addPoint(timestamp,accessChallenge);
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
			Log.error("Error in setdata for AuthSummaryChartPanel", e);
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
