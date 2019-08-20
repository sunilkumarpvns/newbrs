package com.elitecore.elitesm.web.livemonitoring.client;

import com.elitecore.elitesm.web.livemonitoring.client.acct.AcctErrorsChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.acct.AcctResponseTimeChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.acct.AcctSummaryChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.auth.AuthErrorsChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.auth.AuthRejectReasonsChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.auth.AuthResponseTimeChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.auth.AuthSummaryChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.eliteserver.MemoryUsageChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.eliteserver.ThreadStatisticChartPanel;

public class ChartPanelFactory {

	private static ChartPanelFactory chartFactory;
	private ChartPanelFactory(){
		
	}
	public static ChartPanelFactory getInstance(){
		if(chartFactory==null){
			chartFactory = new ChartPanelFactory();
		}
		return chartFactory;
	}
	
	public AbstractChartPanel getChartPanel(String graphType) throws InvalidGraphException{
		AbstractChartPanel panel = null;
		if(graphType.equalsIgnoreCase(AbstractChartPanel.AUTH_RESPONSE_TIME)){
			panel = new AuthResponseTimeChartPanel();
		}else if(graphType.equalsIgnoreCase(AbstractChartPanel.AUTH_ERRORS)){
			panel = new AuthErrorsChartPanel();
		}else if(graphType.equalsIgnoreCase(AbstractChartPanel.AUTH_SUMMARY)){
			panel = new AuthSummaryChartPanel();
		}else if(graphType.equalsIgnoreCase(AbstractChartPanel.AUTH_REJECT_REASONS)){
			panel = new AuthRejectReasonsChartPanel();
		}if(graphType.equalsIgnoreCase(AbstractChartPanel.ACCT_RESPONSE_TIME)){
			panel = new AcctResponseTimeChartPanel();
		}else if(graphType.equalsIgnoreCase(AbstractChartPanel.ACCT_ERRORS)){
			panel = new AcctErrorsChartPanel();
		}else if(graphType.equalsIgnoreCase(AbstractChartPanel.ACCT_SUMMARY)){
			panel = new AcctSummaryChartPanel();
		}else if(graphType.equalsIgnoreCase(AbstractChartPanel.MEMORY_USAGE)){
			panel = new MemoryUsageChartPanel();
		}else if(graphType.equalsIgnoreCase(AbstractChartPanel.THREAD_STATISTICS)){
			panel = new ThreadStatisticChartPanel();
		}
		else {
			new InvalidGraphException("Invalid Graph Type :"+graphType);
		}
		return panel;
		
	}
}
