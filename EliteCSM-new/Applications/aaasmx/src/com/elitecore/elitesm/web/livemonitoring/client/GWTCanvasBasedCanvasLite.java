package com.elitecore.elitesm.web.livemonitoring.client;

import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.googlecode.gchart.client.GChartCanvasLite;

public class GWTCanvasBasedCanvasLite extends GWTCanvas implements GChartCanvasLite{

	public void setFillStyle(String canvasFillStyle) {
		setFillStyle(new Color(canvasFillStyle));
	}

	public void setStrokeStyle(String canvasStrokeStyle) {
		 setLineJoin(GWTCanvas.BEVEL);
	     setStrokeStyle(new Color(canvasStrokeStyle));
	}

}
