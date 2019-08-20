package com.elitecore.elitesm.web.dashboard.widget.dataprovider.cpuusage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetTypeConstants;
import com.elitecore.elitesm.web.dashboard.widget.dao.cpuusage.CpuUsageDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.cpuusage.CpuUsageDAOImpl;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.cpuusage.CpuUsageData;

public class CpuUsageDataProvider implements WidgetDataProvider {
	
	private CpuUsageDAO cpuUsageDAO;
	private static final String ID = WidgetTypeConstants.CPU_USAGE.name();
	private static final String MODULE=CpuUsageDataProvider.class.getSimpleName();

    public CpuUsageDataProvider(){
    	cpuUsageDAO=new CpuUsageDAOImpl();
    }
			
			
  @Override
	public List<WidgetJSON> provideInitialData(String serverKey) {
		Logger.logDebug(MODULE,"calling provideInitialData");
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>();
		try{
			Map<String,List<CpuUsageData>> cpuUsageMap=cpuUsageDAO.getCpuUsageData(serverKey);
			if(cpuUsageMap!=null && !cpuUsageMap.isEmpty()){
				JSONArray jsonObject = JSONArray.fromObject(cpuUsageMap);
				WidgetJSON widgetJSON = new WidgetJSON(getHeaderForInitial(), jsonObject.toString());
				widgetList.add(widgetJSON);
			}
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			Logger.logError(MODULE, exp.getMessage());
			
		}
		return widgetList;

	}

	@Override
	public List<WidgetJSON> provideData() {
		
		Logger.logDebug(MODULE,"calling provideData");
		List<WidgetJSON> widgetList=null;
		try{
			Map<String,List<CpuUsageData>> cpuUsageMap=cpuUsageDAO.getCpuUsageData(2);
			if(cpuUsageMap!=null && !cpuUsageMap.isEmpty()){
				widgetList=new ArrayList<WidgetJSON>();
				JSONArray jsonObject = JSONArray.fromObject(cpuUsageMap);
				WidgetJSON widgetJSON = new WidgetJSON(getHeaderForUpdate(), jsonObject.toString());
				widgetList.add(widgetJSON);
			}
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			Logger.logError(MODULE, exp.getMessage());
			
		}
		return widgetList;
	}

	@Override
	public String getName() {
		
		return ID;
	}
	
	private Header getHeaderForInitial(){
		Header header = new Header();
		//header.setId(ID);
		header.setType("tableschema");
		return header;
	}
	private Header getHeaderForUpdate(){
		Header header = new Header();
		//header.setId(ID);
		header.setType("tabledata");
		return header;
		
	}		
	
}
