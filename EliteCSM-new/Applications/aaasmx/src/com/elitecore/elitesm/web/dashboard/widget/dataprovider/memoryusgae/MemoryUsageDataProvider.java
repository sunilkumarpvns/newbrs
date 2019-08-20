package com.elitecore.elitesm.web.dashboard.widget.dataprovider.memoryusgae;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetTypeConstants;
import com.elitecore.elitesm.web.dashboard.widget.dao.memoryusage.DefaultMemoryUsageDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.memoryusage.MemoryUsageDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.memoryusage.MemoryUsageData;
import com.elitecore.elitesm.web.dashboard.widget.model.memoryusage.ServerWiseMemoryUsageData;
import com.elitecore.elitesm.web.dashboard.widget.model.memoryusage.TotalMemoryUsage;
public class MemoryUsageDataProvider extends BaseWidgetDataProvider {

	private MemoryUsageDAO memoryUsageDAO;
	private static final String ID = WidgetTypeConstants.MEMUSAGE.name();
	private static final String MODULE=MemoryUsageDataProvider.class.getSimpleName();

	public MemoryUsageDataProvider() {
		memoryUsageDAO=new DefaultMemoryUsageDAO();
	}

   

	@Override
	public List<WidgetJSON> getInitialData(String serverKey) throws Exception {
		Logger.logDebug(MODULE, "calling provideInitialData");
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>();
			List<MemoryUsageData> memoryUsageDatas = memoryUsageDAO.getMemoryUsageData(serverKey);
			if(memoryUsageDatas!=null && !memoryUsageDatas.isEmpty()){
			List<ServerWiseMemoryUsageData> serverWiseMemoryUsageDataList=convertToChartData(memoryUsageDatas,true);
			if(serverWiseMemoryUsageDataList!=null && !serverWiseMemoryUsageDataList.isEmpty()){
				TotalMemoryUsage totalMemoryUsage=new TotalMemoryUsage();
				ServerWiseMemoryUsageData[] serverWiseMemoryUsageDataArray=new ServerWiseMemoryUsageData[serverWiseMemoryUsageDataList.size()];
				totalMemoryUsage.setMemoryUsageData(serverWiseMemoryUsageDataList.toArray(serverWiseMemoryUsageDataArray));
				JSONObject jsonObject = JSONObject.fromObject(totalMemoryUsage);
				WidgetJSON widgetJSON = new WidgetJSON(getHeaderForInitial(), jsonObject.toString());
				widgetList.add(widgetJSON);
			}
			}
		return widgetList;


	}
	

	@Override
	public List<WidgetJSON> getProvideData() throws Exception{
		Logger.logDebug(MODULE, "calling provideData");
		
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>();
			List<MemoryUsageData> memoryUsageDatas = memoryUsageDAO.getMemoryUsageData(2);
			if(memoryUsageDatas!=null && !memoryUsageDatas.isEmpty()){
				List<ServerWiseMemoryUsageData> serverWiseMemoryUsageDataList=convertToChartData(memoryUsageDatas,true);
				if(serverWiseMemoryUsageDataList!=null && !serverWiseMemoryUsageDataList.isEmpty()){
					TotalMemoryUsage totalMemoryUsage=new TotalMemoryUsage();
					ServerWiseMemoryUsageData[] serverWiseMemoryUsageDataArray=new ServerWiseMemoryUsageData[serverWiseMemoryUsageDataList.size()];
					totalMemoryUsage.setMemoryUsageData(serverWiseMemoryUsageDataList.toArray(serverWiseMemoryUsageDataArray));
					JSONObject jsonObject = JSONObject.fromObject(totalMemoryUsage);
					WidgetJSON widgetJSON = new WidgetJSON(getHeaderForUpdate(), jsonObject.toString());
					widgetList.add(widgetJSON);
				}
			}
		return widgetList;
		
	}



	private List<ServerWiseMemoryUsageData> convertToChartData(List<MemoryUsageData> totalMemoryList,boolean isParentList) throws DataManagerException{
		HashMap<String,List<MemoryUsageData>> tempMemoryMap=null;
		if(totalMemoryList != null && !totalMemoryList.isEmpty()){
			int size = totalMemoryList.size();
			tempMemoryMap=new HashMap<String,List<MemoryUsageData>>();
			for(int i=0;i<size;i++){
				MemoryUsageData memoryUsage = totalMemoryList.get(i);
				String serverName=memoryUsage.getInstanceId();
				if(tempMemoryMap.get(serverName)==null){
					List<MemoryUsageData> templist=new ArrayList<MemoryUsageData>();
					templist.add(memoryUsage);
					tempMemoryMap.put(serverName,templist); 	
				}else{
					tempMemoryMap.get(serverName).add(memoryUsage);
				}

			}
		}
		return convertMemoryDataToServerWiseMemoryData(tempMemoryMap);
	}

	private List<ServerWiseMemoryUsageData> convertMemoryDataToServerWiseMemoryData(HashMap<String,List<MemoryUsageData>> memoryMap){
		List<ServerWiseMemoryUsageData> serverWiseMemoryUsageDataList=null;
		if(memoryMap==null||memoryMap.isEmpty()){
			return  null;
		}
		serverWiseMemoryUsageDataList=new ArrayList<ServerWiseMemoryUsageData>();
		for(List<MemoryUsageData> tempList:memoryMap.values()){
			int size=tempList.size();
			ServerWiseMemoryUsageData serverWiseMemoryUsageData=new ServerWiseMemoryUsageData();
			Long[] epochTime=new Long[size];
			Long[] memoryUsage=new Long[size];
			for(int i=0;i<size;i++){
				serverWiseMemoryUsageData.setServerId(tempList.get(i).getInstanceId());
				epochTime[i]=tempList.get(i).getEpochTime();
				memoryUsage[i]=tempList.get(i).getMemoryUsage();
			}
			serverWiseMemoryUsageData.setEpochTime(epochTime);
			serverWiseMemoryUsageData.setMemoryUsed(memoryUsage);
			serverWiseMemoryUsageDataList.add(serverWiseMemoryUsageData);
		}
		return serverWiseMemoryUsageDataList;
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
