package com.elitecore.elitesm.web.dashboard.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.json.JSONArray;

import com.elitecore.elitesm.web.dashboard.widget.conf.WidgetConfiguration;
import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetAlerts;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;

/**
 * 
 * A <strong>Widget Handler</strong>  handle Widget Data based on interval
 *  
 * <p> More specifically, the WidgetHandler class is used for 
 * two purposes:
 * 
 * <ul>
 * <li> to push Widget Data in Widget Data Queue by WidgetListener Class,<p> 
 * <li> to send Widget Data by WebSocketListener Class</li> 
 * </ul>  
 * 
 * @see WidgetListener
 * @see WebSocketListener
 * 
 * @author punit.j.patel
 *
 */
public class WidgetHandler{
	private Queue<WidgetJSON> widgetDataQueue = new ConcurrentLinkedQueue<WidgetJSON>();
	private String widgetID;
	private int interval = 5;
	private WidgetDataSender widgetDataSender;
	private AtomicInteger counter = new AtomicInteger(0);
	private int emptyDataCounter = 0;
	private boolean isFreeze=false;
	
	public WidgetHandler(String widgetID, int interval, WidgetDataSender widgetDataSender,String serverKey) {
		this.widgetID = widgetID;
		if( interval > 0 ) {
			this.interval = interval;
		}
		this.widgetDataSender = widgetDataSender;
	}
	
	/**
	 * Based on <code>interval</code>, poll <code>WidgetJSON</code> from Widget Data Queue and 
	 * send it to Widget Client.
	 * 
	 * @see WidgetDataSender
	 */
	public void sendWidgetData() {
		int count = counter.incrementAndGet();
		if( count >= interval ) {
			List<WidgetJSON> widgetJSONList = new ArrayList<WidgetJSON>();
			while(true) {
				WidgetJSON widgetJSON = widgetDataQueue.poll();
				if(widgetJSON == null) {
					break;
				}
				widgetJSON.getHeader().setId(widgetID);
				widgetJSONList.add(widgetJSON);
			}
			if(!widgetJSONList.isEmpty()){
				String data = JSONArray.fromObject(widgetJSONList).toString();
				widgetDataSender.send( data );
			} else {
				emptyDataCounter++;
				if( emptyDataCounter >= WidgetConfiguration.getAlertCount()){
					List<WidgetJSON> widgetJSONs = new ArrayList<WidgetJSON>(1);
					WidgetJSON widgetJSON = WidgetAlerts.WARN.createWidgetAlertJSON();
					widgetJSON.getHeader().setId(widgetID);
					widgetJSONs.add(widgetJSON);
					String data = JSONArray.fromObject(widgetJSONs).toString();
					widgetDataSender.send( data );
					emptyDataCounter = 0;
				}
			}
			counter.set(0);
		}
	}
	

	/**
	 * push Widget JSON Data in Widget Data Queue
	 * 
	 * @param widgetJSONList a list of WidgetJSON 
	 */
	public void pushWidgetData(List<WidgetJSON> widgetJSONList) {
		/*WidgetJSON widgetJSON1 = WidgetAlerts.WARN.createWidgetAlertJSON();
		if(widgetJSON1 != null) {
			widgetJSON1.getHeader().setId(widgetID);
			widgetJSON1.setBody("Freeze Test");
			widgetDataQueue.offer(widgetJSON1);
			return;
		}*/
		for(WidgetJSON widgetJSON : widgetJSONList) {
			widgetDataQueue.offer(widgetJSON);
		}
	}
	
	public boolean isFreeze() {
		return isFreeze;
	}

	public void setFreeze(boolean isFreeze) {
		this.isFreeze = isFreeze;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((widgetID == null) ? 0 : widgetID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WidgetHandler other = (WidgetHandler) obj;
		if (widgetID == null) {
			if (other.widgetID != null)
				return false;
		} else if (!widgetID.equals(other.widgetID))
			return false;
		return true;
	}

	public String getWidgetID() {
		return widgetID;
	}

	
	
	
}
