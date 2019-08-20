package com.elitecore.elitesm.web.livemonitoring.client;

import com.googlecode.gchart.client.GChartCanvasFactory;
import com.googlecode.gchart.client.GChartCanvasLite;

public class GWTCanvasBasedCanvasFactory implements GChartCanvasFactory{

	public GChartCanvasLite create() {
	  
		return new GWTCanvasBasedCanvasLite();
	}

}
