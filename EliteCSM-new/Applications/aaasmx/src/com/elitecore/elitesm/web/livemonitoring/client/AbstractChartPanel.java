package com.elitecore.elitesm.web.livemonitoring.client;

import java.util.List;

import com.googlecode.gchart.client.GChart;
import com.googlecode.gchart.client.GChart.Curve.Point;

public abstract class AbstractChartPanel extends GChart {
	

	
	protected static final String TIMESTAMP_FORMAT = "=(Date)HH:mm:ss";
	
	protected static final int GRAPH_TIMESPAN = 300000; // 5 minute
	private Long startTimestamp = null;
	private Long lastTimestamp = null;
	
//	public static final int  MEMORY_USAGE_CHART = 100;
//	
//	public static final int  AUTH_SERVICE_REQUEST_CHART = 101;
//	public static final int  AUTH_SERVICE_RESPONSE_TIME_CHART = 102;
//	public static final int  AUTH_SERVICE_THREAD_USAGE_CHART = 103;
//	
//	
//	public static final int  ACCT_SERVICE_REQUEST_CHART = 201;
//	public static final int  ACCT_SERVICE_RESPONSE_TIME_CHART = 202;
//	public static final int  ACCT_SERVICE_THREAD_USAGE_CHART = 203;
	
	public static final String AUTH_STATISTICS_GROUP = "AuthServiceStatistics";
	public static final String ACCT_STATISTICS_GROUP = "AcctServiceStatistics";
	public static final String SERVER_STATISTICS_GROUP = "ServerStatistics";
	
	
	public static final String AUTH_RESPONSE_TIME = "AUTHRESPONSETIME";
	public static final String AUTH_ERRORS = "AUTHERRORS";
	public static final String AUTH_SUMMARY = "AUTHSUMMARY";
	public static final String AUTH_REJECT_REASONS = "AUTHREJECTREASONS";
	
	public static final String ACCT_RESPONSE_TIME = "ACCTRESPONSETIME";
	public static final String ACCT_ERRORS = "ACCTERRORS";
	public static final String ACCT_SUMMARY = "ACCTSUMMARY";
	
	public static final String MEMORY_USAGE="MEMORYUSAGE";
	public static final String THREAD_STATISTICS="THREADSTATISTICS";	
	private int chartIndex=0;
	
	protected AbstractChartPanel(){
		 setChartSize(400, 130);
	     setSize("500px","150px");
	     setOptimizeForMemory(true);
	     
	     //general X-Axis config
	     getXAxis().setAxisLabelThickness(20);
	     getXAxis().setTickCount(11);
	     getXAxis().setTicksPerLabel(2);
		 getXAxis().setAxisLabel("Time");
		 getXAxis().setTickLabelFormat(TIMESTAMP_FORMAT);
		 
	     //general Y-Axis config

    	 getYAxis().setAxisLabelThickness(30);
    	 getYAxis().setAxisMin(0);

	}
	abstract public void updateData(List<Long[]> result) throws Exception;
	abstract public String getGroupKey();
	
	protected void clearPointsTillStartTime(Long startTimestamp, Curve curve){
		
		if(startTimestamp!=null && curve!=null){
			boolean flag = true;
			while(flag){
				Point p = curve.getPoint(0);
				if(p.getX()<startTimestamp){
					curve.removePoint(p);
				}else{
					flag=false;
				}
			}
		}
	}
	protected void clearPointsForAllCurve(Long startTimestamp){
		if(startTimestamp!=null){
			int n = getNCurves();
			for (int i = 0; i < n; i++) {
				Curve curve = getCurve(i);
				if(curve!=null){
					boolean flag = true;
					while(flag){
						Point p = curve.getPoint(0);
						if(p.getX()<startTimestamp){
							curve.removePoint(p);
						}else{
							flag=false;
						}
					}
				}
			}
		}
	}
	
	public Curve addCurve(String legendLabel ){
		return addCurve(legendLabel,1);
	}
	
	public Curve addCurve(String legendLabel, int thinkness){
		addCurve(chartIndex);
		 Curve curve = getCurve(chartIndex);
		// solid, 2px thick, 1px resolution, connecting lines:
		Symbol symbol = curve.getSymbol();
		symbol.setSymbolType(SymbolType.LINE);
		symbol.setFillThickness(thinkness);
		symbol.setFillSpacing(0);
		symbol.setHeight(1); 
		symbol.setWidth(1);
		// Make center-fill of square point markers same color as line:
		symbol.setBackgroundColor(curve.getSymbol().getBorderColor());

		curve.setLegendLabel(legendLabel);
		chartIndex++;
		return curve;
	}
	
	public Curve addCurve(String legendLabel, String color, int thinkness){
		addCurve(chartIndex);
		 Curve curve = getCurve(chartIndex);
		// solid, 2px thick, 1px resolution, connecting lines:
		Symbol symbol = curve.getSymbol();
		symbol.setSymbolType(SymbolType.LINE);
		symbol.setFillThickness(thinkness);
		symbol.setFillSpacing(0);
		symbol.setHeight(1); 
		symbol.setWidth(1);
		// Make center-fill of square point markers same color as line:
		symbol.setBackgroundColor(color);

		curve.setLegendLabel(legendLabel);
		chartIndex++;
		return curve;
	}
	
	protected void setGraphTimeSpan() {
		Long startTimestamp=getStartTimestamp();
		Long lastTimestamp=getLastTimestamp();
		
		if(startTimestamp==null && lastTimestamp!=null){
			//startTimestamp = lastTimestamp-GRAPH_TIMESPAN;
			setStartTimestamp(lastTimestamp-GRAPH_TIMESPAN);
		}else if(startTimestamp!=null && (lastTimestamp-startTimestamp)>=GRAPH_TIMESPAN){
			//startTimestamp = startTimestamp + ((lastTimestamp - startTimestamp)-GRAPH_TIMESPAN);
			setStartTimestamp(startTimestamp + ((lastTimestamp - startTimestamp)-GRAPH_TIMESPAN));
		}
		 
		startTimestamp=getStartTimestamp();
		lastTimestamp=getLastTimestamp();
		
		if(startTimestamp!=null && lastTimestamp!=null){
			getXAxis().setAxisMin(startTimestamp);
			clearPointsForAllCurve(startTimestamp);
			getXAxis().setAxisMax(lastTimestamp);
		}
		
	}
	protected Long getStartTimestamp() {
		return startTimestamp;
	}
	protected void setStartTimestamp(Long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}
	protected Long getLastTimestamp() {
		return lastTimestamp;
	}
	protected void setLastTimestamp(Long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}
	
	
}
