package com.elitecore.elitesm.web.livemonitoring.client.acct;

import java.util.Arrays;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.livemonitoring.client.AbstractChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.GraphConstants;

public class AcctErrorsChartPanel extends AbstractChartPanel{
	
	private static final String  title = "<b> Accounting Errors</b>";
	private static final String MODULE = "AcctErrorsChartPanel";
	private Long startTimestamp = null;
	private Long lastTimestamp = null;
	
	private Curve badAuthenticatorsCurve;
	private Curve duplicateRequestsCurve;
	private Curve malformedRequestsCurve;
	private Curve invalidRequestsCurve;
	private Curve unknownRequestsCurve;
	private Curve droppedCurve;
	
	public AcctErrorsChartPanel(){
		super();
		try{
			this.setBorderColor("#000000");
			setChartTitle(title);
			
			badAuthenticatorsCurve = addCurve("Bad Authenticators");
			duplicateRequestsCurve   = addCurve("Duplicate");
			malformedRequestsCurve = addCurve("Malformed");
			invalidRequestsCurve = addCurve("Invalid");
			unknownRequestsCurve = addCurve("Unknown");
			droppedCurve = addCurve("Dropped");
			
			getXAxis().setHasGridlines(true);
			getYAxis().setAxisLabel("E<br>r<br>r<br>o<br>r<br>s");
			getYAxis().setTickLabelFormat("# ");
			setChartTitle(title);

			this.update();
			this.setVisible(true);
			Log.info("AcctErrorsChartPanel created.");
		}catch(Exception e){
			Log.error("Error in creating AcctErrorsChartPanel", e);
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
						Long badAuthenticators = data[GraphConstants.IDX_ACCT_ERRORS_BAD_AUTHENTICATORS];
						Long duplicateRequests = data[GraphConstants.IDX_ACCT_ERRORS_DUPLICATE];
						Long malformedRequests = data[GraphConstants.IDX_ACCT_ERRORS_MALFORMED];
						Long invalidRequests = data[GraphConstants.IDX_ACCT_ERRORS_INVALID];
						Long unknownRequests = data[GraphConstants.IDX_ACCT_ERRORS_UNKNOWN];
						Long dropped = data[GraphConstants.IDX_ACCT_ERRORS_DROPPED];
						
						badAuthenticatorsCurve.addPoint(timestamp,badAuthenticators);
						duplicateRequestsCurve.addPoint(timestamp,duplicateRequests);
						malformedRequestsCurve.addPoint(timestamp,malformedRequests);
						invalidRequestsCurve.addPoint(timestamp,invalidRequests);
						unknownRequestsCurve.addPoint(timestamp,unknownRequests);
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
			Log.error("Error in setdata for AcctErrorsChartPanel", e);
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
