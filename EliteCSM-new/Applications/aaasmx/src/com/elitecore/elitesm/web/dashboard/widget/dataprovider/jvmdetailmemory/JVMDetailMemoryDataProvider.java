package com.elitecore.elitesm.web.dashboard.widget.dataprovider.jvmdetailmemory;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetTypeConstants;
import com.elitecore.elitesm.web.dashboard.widget.dao.jvmdetailmemoryusage.DefaultJVMDetailMemoryUsageDao;
import com.elitecore.elitesm.web.dashboard.widget.dao.jvmdetailmemoryusage.DetailMemoryUsageDao;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory.JVMDetailMemoryTotalData;

public class JVMDetailMemoryDataProvider extends BaseWidgetDataProvider {

	private DetailMemoryUsageDao memoryUsageDAO;
	private static final String ID = WidgetTypeConstants.JVM_DETAIL_MEMUSAGE.name();
	private static final String MODULE=JVMDetailMemoryDataProvider.class.getSimpleName();

	public JVMDetailMemoryDataProvider() {
		memoryUsageDAO=new DefaultJVMDetailMemoryUsageDao();
	}

   

	@Override
	public List<WidgetJSON> getInitialData(String server) throws Exception {
		Logger.logDebug(MODULE,"calling provideInitialData");
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>();
		List<JVMDetailMemoryTotalData> memoryUsageList=memoryUsageDAO.getDetailMemoryUsageData();
		
		if(memoryUsageList!=null && !memoryUsageList.isEmpty()){
			JSONArray jsonObject = JSONArray.fromObject(memoryUsageList);
			WidgetJSON widgetJSON = new WidgetJSON(getHeaderForInitial(), jsonObject.toString());
			widgetList.add(widgetJSON);
		}
		return widgetList;
	}
	

	@Override
	public List<WidgetJSON> getProvideData() throws Exception {
		Logger.logDebug(MODULE,"calling provideData");
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>();
		
		List<JVMDetailMemoryTotalData> memoryUsageList=memoryUsageDAO.getDetailMemoryUsageData(2);
		if(memoryUsageList!=null && !memoryUsageList.isEmpty()){
			JSONArray jsonObject = JSONArray.fromObject(memoryUsageList);
			String data=jsonObject.toString();
			WidgetJSON widgetJSON = new WidgetJSON(getHeaderForUpdate(), data);
			widgetList.add(widgetJSON);
		}
		return widgetList;
	}



	
	@Override
	public String getName(){
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
