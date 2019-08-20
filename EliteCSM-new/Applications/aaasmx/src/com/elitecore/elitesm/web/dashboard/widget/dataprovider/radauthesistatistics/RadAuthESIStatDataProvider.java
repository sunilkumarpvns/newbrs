package com.elitecore.elitesm.web.dashboard.widget.dataprovider.radauthesistatistics;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.TotalReqChartData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetTypeConstants;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthesistatistics.DefaultRadAuthESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthesistatistics.RadiusAuthESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthesistatistics.RadiusAuthESIStatData;

public class RadAuthESIStatDataProvider  extends BaseWidgetDataProvider {
	
	private RadiusAuthESIStatisticsDAO radAuthESIStatDAO;
	private static final String ID = WidgetTypeConstants.RAD_AUTH_ESI_STATISTICS.name();
	private static final String MODULE=RadAuthESIStatDataProvider.class.getSimpleName();
	
	public RadAuthESIStatDataProvider() {
		radAuthESIStatDAO=new DefaultRadAuthESIStatisticsDAO();
	}
    
	private Header getHeader(){
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
	
	@Override
	public List<WidgetJSON> getInitialData(String serverKey) throws Exception {
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>(1);
			
		/*get data from  DefaultRadAuthESIStatisticsDAO */
		List<RadiusAuthESIStatData> radAuthESIDataList = radAuthESIStatDAO.getRadAuthESIDataList(serverKey);
		TotalReqChartData reqChartData=convertToChartData(radAuthESIDataList,true);
		JSONObject jsonObject = JSONObject.fromObject(reqChartData);
		String data=jsonObject.toString();
		Logger.logDebug(MODULE,"--"+data);
		WidgetJSON widgetJSON = new WidgetJSON(getHeader(), data);
		widgetList.add(widgetJSON);
		
		return widgetList;
	}

	@Override
	public List<WidgetJSON> getProvideData() throws Exception {
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>();
		
		/*get data from  DefaultRadAuthESIStatisticsDAO */
		List<RadiusAuthESIStatData> radAuthESIDataList = radAuthESIStatDAO.getRadAuthESIDataList(2);
		if(radAuthESIDataList!=null && !radAuthESIDataList.isEmpty()){
			TotalReqChartData reqChartData=convertToChartData(radAuthESIDataList,true);
			JSONObject jsonObject = JSONObject.fromObject(reqChartData);
			String data=jsonObject.toString();
			Logger.logDebug(MODULE,"--"+data);
			WidgetJSON widgetJSON = new WidgetJSON(getHeaderForUpdate(), data);
			widgetList.add(widgetJSON);
		}
		
		return widgetList;
	}

	public String getName() {
		return ID;
	}
	
private TotalReqChartData convertToChartData(List<RadiusAuthESIStatData> totalReqStatistics,boolean isParentList) throws DataManagerException{
		
		TotalReqChartData reqChartData =null;
		if(totalReqStatistics != null && !totalReqStatistics.isEmpty())
		{
		  reqChartData =new TotalReqChartData();
		  int size = totalReqStatistics.size();
		  String[] esiArray= new String[size];
		  Integer[] accessChallengeArray=new Integer[size];
		  Integer[] accessAcceptArray=new Integer[size];
		  Integer[] accessRejectArray=new Integer[size];
		  Integer[] requestDropArray=new Integer[size];
		  Long[] epochTimeArray=new Long[size];
		  String[] serverAddress = new String[size];
		  Integer[] serverPort =new Integer[size];
		  
		  TotalReqChartData[] totalReqChartDataArray=null;
		  /* if(isParentList)
			   totalReqChartDataArray=new TotalReqChartData[size]; */
		  
		  for(int i=0;i<size;i++)
		  {
			  RadiusAuthESIStatData totalReqStatisticsData = totalReqStatistics.get(i);
			  
			  esiArray[i]=totalReqStatisticsData.getAuthServerName();
			  accessChallengeArray[i]=totalReqStatisticsData.getClientAccessChallenges();
			  accessAcceptArray[i]=totalReqStatisticsData.getRadAuthClientAccessAccepts();
			  accessRejectArray[i]=totalReqStatisticsData.getRadAuthClientAccessRejects();
			  requestDropArray[i]=totalReqStatisticsData.getRadAuthClientTimeouts();
			  epochTimeArray[i]=totalReqStatisticsData.getCreateTime().getTime();
			  serverAddress[i]=totalReqStatisticsData.getRadAuthServerAddress();
			  serverPort[i]=totalReqStatisticsData.getClientServerPortNumber();
			  
			  /* iterate over list of specific esi
			  if(isParentList){
			    //List<TotalRequestStatistics> childList= blManager.getTotalReqStatList(totalReqStatisticsData);
			    List<RadiusAuthESIStatData> radAuthESIStatisticsDetails = radAuthESIStatDAO.getRadAuthESIStatisticsDetails(totalReqStatisticsData.getRadAuthServerAddress());
			    TotalReqChartData convertToChartData = convertToChartData(radAuthESIStatisticsDetails,false);
			    totalReqChartDataArray[i]=convertToChartData;
			  }*/  
			  
			  
		  }
		  reqChartData.setEsi(esiArray);
		  reqChartData.setAccessAccept(accessAcceptArray);
		  reqChartData.setAccessChallenge(accessChallengeArray);
		  reqChartData.setAccessReject(accessRejectArray);
		  reqChartData.setRequestDrop(requestDropArray);
		  reqChartData.setEpochTime(epochTimeArray);
		  reqChartData.setTotalReqDataArray(totalReqChartDataArray);
		  reqChartData.setServerAddress(serverAddress);
		  reqChartData.setServerPort(serverPort);
		  
		}
		return reqChartData;
	}


     

	
	

}


