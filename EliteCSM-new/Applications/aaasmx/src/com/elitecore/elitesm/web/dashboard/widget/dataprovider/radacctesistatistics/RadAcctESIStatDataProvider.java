package com.elitecore.elitesm.web.dashboard.widget.dataprovider.radacctesistatistics;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.RadAcctReqChartData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetTypeConstants;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctesistatistics.DefaultRadAcctESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctesistatistics.RadiusAcctESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctesistatistics.RadiusAcctESIStatData;

public class RadAcctESIStatDataProvider  extends BaseWidgetDataProvider {
	
	private RadiusAcctESIStatisticsDAO radAcctESIStatDAO;
	private static final String ID = WidgetTypeConstants.RAD_ACCT_ESI_STATISTICS.name();
	private static final String MODULE=RadAcctESIStatDataProvider.class.getSimpleName();
	
	public RadAcctESIStatDataProvider() {
		radAcctESIStatDAO=new DefaultRadAcctESIStatisticsDAO();
	}
    
	private Header getHeader(){
		Header header = new Header();
		header.setType("tableschema");
		return header;
	}
	
	@Override
	public List<WidgetJSON> getInitialData(String serverKey) throws Exception {
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>(1);
			
		/*get data from  DefaultRadAcctESIStatisticsDAO */
		List<RadiusAcctESIStatData> radAcctESIDataList = radAcctESIStatDAO.getRadAuthESIDataList(serverKey);
		RadAcctReqChartData radAcctReqChartData=convertToChartData(radAcctESIDataList,true);
		JSONObject jsonObject = JSONObject.fromObject(radAcctReqChartData);
		String data=jsonObject.toString();
		Logger.logDebug(MODULE,"--"+data);
		WidgetJSON widgetJSON = new WidgetJSON(getHeader(), data);
		widgetList.add(widgetJSON);
		
		return widgetList;
	}

	@Override
	public List<WidgetJSON> getProvideData() throws Exception {
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>(1);
		
		/*get data from  DefaultRadAcctESIStatisticsDAO */
		List<RadiusAcctESIStatData> radAcctESIDataList = radAcctESIStatDAO.getRadAuthESIDataList(2);
		if(radAcctESIDataList!=null && !radAcctESIDataList.isEmpty()){
			RadAcctReqChartData radAcctReqChartData=convertToChartData(radAcctESIDataList,true);
			JSONObject jsonObject = JSONObject.fromObject(radAcctReqChartData);
			String data=jsonObject.toString();
			Logger.logDebug(MODULE,"--"+data);
			WidgetJSON widgetJSON = new WidgetJSON(getHeaderForUpdate(), data);
			widgetList.add(widgetJSON);
		}
		
		return widgetList;
	}

	private Header getHeaderForUpdate(){
		Header header = new Header();
		//header.setId(ID);
		header.setType("tabledata");
		return header;
	}
	
	public String getName() {
		return ID;
	}
	
	private RadAcctReqChartData convertToChartData(List<RadiusAcctESIStatData> totalReqStatistics,boolean isParentList) throws DataManagerException{
		
		RadAcctReqChartData reqChartData =null;
		
		if(totalReqStatistics != null && !totalReqStatistics.isEmpty())
		{
		  reqChartData =new RadAcctReqChartData();
		  int size = totalReqStatistics.size();
		  String[] esiArray= new String[size];
		  Integer[] clientRequestArray=new Integer[size];
		  Integer[] clientResponseArray=new Integer[size];
		  Integer[] retransmissionArray=new Integer[size];
		  Integer[] requestDropArray=new Integer[size];
		  Long[] epochTimeArray=new Long[size];
		  String[] serverAddressArray = new String[size];
		  Integer[] serverPortArray = new Integer[size];
		  
		  RadAcctReqChartData[] totalReqChartDataArray=null;
		 
		  for(int i=0;i<size;i++)
		  {
			  RadiusAcctESIStatData totalReqStatisticsData = totalReqStatistics.get(i);
			  
			  serverAddressArray[i]=totalReqStatisticsData.getRadiusAccServerAddress();
			  serverPortArray[i]=totalReqStatisticsData.getClientServerPortNumber();
			  esiArray[i]=totalReqStatisticsData.getAcctServerName();
			  clientRequestArray[i]=totalReqStatisticsData.getRadiusAccClientRequests();
			  clientResponseArray[i]=totalReqStatisticsData.getRadiusAccClientResponses();
			  retransmissionArray[i]=totalReqStatisticsData.getRadiusAccClientRetransmissions();
			  requestDropArray[i]=totalReqStatisticsData.getRadiusAccClientTimeouts();
			  epochTimeArray[i]=totalReqStatisticsData.getCreateTime().getTime();
			  
		  }
		  reqChartData.setEsi(esiArray);
		  reqChartData.setAccountingRes(clientResponseArray);
		  reqChartData.setAccountingReq(clientRequestArray);
		  reqChartData.setRetransmission(retransmissionArray);
		  reqChartData.setRequestDrop(requestDropArray);
		  reqChartData.setEpochTime(epochTimeArray);
		  reqChartData.setTotalReqDataArray(totalReqChartDataArray);
		  reqChartData.setServerAddress(serverAddressArray);
		  reqChartData.setServerPort(serverPortArray);
		  
		}
		return reqChartData;
	}


     

	
	

}


