package com.elitecore.elitesm.web.dashboard.widget.constants;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;

public enum WidgetAlerts {
	WARN,
	ERROR,
	;
	
	public WidgetJSON createWidgetAlertJSON() {
		Header header = new Header();
		header.setType(getClass().getSimpleName());
		WidgetJSON widgetJSON = new WidgetJSON(header, name());
		return widgetJSON;
	}
	
	public WidgetJSON createWidgetErrorJSON(Exception e) {
		Header header = new Header();
		header.setType(getClass().getSimpleName());
		
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stacktrace = sw.toString();

		WidgetJSON widgetJSON = new WidgetJSON(header,  e.getClass().getSimpleName(),stacktrace);
		return widgetJSON;
	}
}
