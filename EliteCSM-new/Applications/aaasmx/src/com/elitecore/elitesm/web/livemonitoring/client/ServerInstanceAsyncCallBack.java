package com.elitecore.elitesm.web.livemonitoring.client;

import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.Widget;

class ServerInstanceAsyncCallBack implements AsyncCallback<ServerInstanceBean>{
	private static String MODULE ="ServerInstanceAsyncCallBack";
	ServerInstanceBean serverInstanceBean;
	String graphType;
	private static final GraphServiceAsync graphService = GWT.create(GraphService.class);
	private boolean logEnabled = false;
	private final int REFRESH_INTERVAL = 5000; 
	
	public ServerInstanceAsyncCallBack(String graphType, boolean logEnabled ){
		this.graphType=graphType;
		this.logEnabled = logEnabled;
	}
	
	public void onSuccess(ServerInstanceBean serverInstanceBean) {
		if(serverInstanceBean!=null){
			Log.debug(MODULE+":"+"serverInstanceBean- "+serverInstanceBean);
			this.serverInstanceBean=serverInstanceBean;


			AbstractChartPanel chartPanel=null;
			try{	
				chartPanel = ChartPanelFactory.getInstance().getChartPanel(graphType);
				final GraphServiceCallBack graphServiceCallback = new GraphServiceCallBack(chartPanel);
				graphService.getGraphData(serverInstanceBean, graphType,  graphServiceCallback);
				Timer  refreshTimer = new DataRetriverTimer(graphService,serverInstanceBean,graphType,graphServiceCallback) ;
				if( "MEMORYUSAGE".equals(graphType) || "THREADSTATISTICS".equals(graphType) ){
					refreshTimer.scheduleRepeating(1000);
				}else{
					refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
				}
			}catch(InvalidGraphException e){
				Log.debug(MODULE+":"+"Invalid Graph Type - "+graphType);
				return;
			}
			if(logEnabled){
				VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel();
				verticalSplitPanel.setTopWidget(chartPanel);
				verticalSplitPanel.setSplitPosition("300px");
				Widget logWidget = Log.getLogger(DivLogger.class).getWidget();
				logWidget.setVisible(true);
				verticalSplitPanel.setBottomWidget(logWidget);
				logWidget.setSize("816px", "180px");
				verticalSplitPanel.setSize("816px", "500px");
				RootPanel.get("demo").add(verticalSplitPanel);
			}else{
				Widget logWidget = Log.getLogger(DivLogger.class).getWidget();
				logWidget.setVisible(false);
				RootPanel.get("demo").add(chartPanel);
			}
		}else{
			if(logEnabled){
				Widget logWidget = Log.getLogger(DivLogger.class).getWidget();
				logWidget.setVisible(true);
				VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel();
				verticalSplitPanel.setTopWidget(new FormPanel());
				verticalSplitPanel.setSplitPosition("300px");
				verticalSplitPanel.setBottomWidget(logWidget);
				logWidget.setSize("816px", "180px");
				verticalSplitPanel.setSize("816px", "500px");
				RootPanel.get("demo").add(verticalSplitPanel);
			}
		}
	}
	public void onFailure(Throwable caught) {
		if(logEnabled){
			Widget logWidget = Log.getLogger(DivLogger.class).getWidget();
			logWidget.setVisible(true);
			VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel();
			verticalSplitPanel.setTopWidget(new FormPanel());
			verticalSplitPanel.setSplitPosition("300px");
			verticalSplitPanel.setBottomWidget(logWidget);
			logWidget.setSize("816px", "180px");
			verticalSplitPanel.setSize("816px", "500px");
			RootPanel.get("demo").add(verticalSplitPanel);
		}else{
			RootPanel.get("demo").add(new FormPanel());
		}
		Log.debug(" "+caught.getMessage());
		caught.printStackTrace();
	}
	public ServerInstanceBean getServerInstanceBean(){
		return serverInstanceBean;
	}
	

	
}
