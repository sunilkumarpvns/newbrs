package com.elitecore.elitesm.web.dashboard.widget.dataprovider;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetAlerts;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;

public abstract class BaseWidgetDataProvider implements WidgetDataProvider {
	private boolean isInErrorState = false;
	
	@Override
	public List<WidgetJSON> provideInitialData(String serverKey) {
		try {
			return getInitialData(serverKey);
		} catch(Exception e) {
			isInErrorState =true;
			return sendError(e);
		}
	}
	
	@Override
	public List<WidgetJSON> provideData() {
		if( isInErrorState ) {
			return provideInitialData(null); 
		}
		try {
			return getProvideData();
		} catch (Exception e) {
			isInErrorState =true;
			return sendError(e);
		}
	}
	
	
	protected List<WidgetJSON> sendError(Exception e) {
		e.printStackTrace();
		List<WidgetJSON> widgetJSONList = new ArrayList<WidgetJSON>(1);
		widgetJSONList.add( WidgetAlerts.ERROR.createWidgetErrorJSON(e)) ;
		return widgetJSONList;
	}

	protected abstract List<WidgetJSON> getInitialData(String serverKey) throws Exception; 
	protected abstract List<WidgetJSON> getProvideData()  throws Exception;
}
