package com.elitecore.elitesm.web.dashboard.widget.dataprovider.nasesistatistics;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.NASESIReqChartData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetTypeConstants;
import com.elitecore.elitesm.web.dashboard.widget.dao.nasesistatistics.DefaultNASESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.nasesistatistics.NASESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.BaseWidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;
import com.elitecore.elitesm.web.dashboard.widget.model.nasesistatistics.NasESIStatData;

public class NasESIStatDataProvider  extends BaseWidgetDataProvider {
	
	private NASESIStatisticsDAO nasESIStatDAO;
	private static final String ID = WidgetTypeConstants.NAS_ESI_STATISTICS.name();
	private static final String MODULE=NasESIStatDataProvider.class.getSimpleName();
	
	public NasESIStatDataProvider() {
		nasESIStatDAO=new DefaultNASESIStatisticsDAO();
	}
    
	private Header getHeader(){
		Header header = new Header();
		header.setType("tableschema");
		return header;
	}
	
	@Override
	public List<WidgetJSON> getInitialData(String serverKey) throws Exception {
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>(1);
			
		/*get data from  DefaultNASESIStatisticsDAO */
		List<NasESIStatData> nasESIDataList = nasESIStatDAO.getNasESIDataList(serverKey);
		NASESIReqChartData reqChartData=convertToChartData(nasESIDataList,true);
		JSONObject jsonObject = JSONObject.fromObject(reqChartData);
		String data=jsonObject.toString();
		Logger.logDebug(MODULE,"--"+data);
		WidgetJSON widgetJSON = new WidgetJSON(getHeader(), data);
		widgetList.add(widgetJSON);
		
		return widgetList;
	}

	@Override
	public List<WidgetJSON> getProvideData() throws Exception {
		List<WidgetJSON> widgetList=new ArrayList<WidgetJSON>(1);
		
		/*get data from  DefaultNASESIStatisticsDAO */
		List<NasESIStatData> nasESIDataList = nasESIStatDAO.getNasESIDataList(2);
		if(nasESIDataList!=null && !nasESIDataList.isEmpty()){
			NASESIReqChartData reqChartData=convertToChartData(nasESIDataList,true);
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
	
	private Header getHeaderForUpdate(){
		Header header = new Header();
		//header.setId(ID);
		header.setType("tabledata");
		return header;
	}
	
	private NASESIReqChartData convertToChartData(List<NasESIStatData> totalReqStatistics,boolean isParentList) throws DataManagerException{
		
		NASESIReqChartData reqChartData =null;
		if(totalReqStatistics != null && !totalReqStatistics.isEmpty())
		{
		  reqChartData =new NASESIReqChartData();
		  int size = totalReqStatistics.size();
		  String[] esiArray= new String[size];
		  Integer[] disconnectReq=new Integer[size];
		  Integer[] disconnectNack=new Integer[size];
		  Integer[] disconnectAck=new Integer[size];
		  Integer[] disconnectTimeout=new Integer[size];
		  Integer[] coaReq=new Integer[size];
		  Integer[] coaNack=new Integer[size];
		  Integer[] coaAck=new Integer[size];
		  Integer[] coaTimeout=new Integer[size];
		  String[] serverAddress = new  String[size];
		  Integer[] serverPort= new Integer[size];
		  
		  Long[] epochTimeArray=new Long[size];
		  
		  NASESIReqChartData[] nasESIReqChartData=null;
		
		  
		  for(int i=0;i<size;i++)
		  {
			  NasESIStatData totalReqStatisticsData = totalReqStatistics.get(i);
			  
			  esiArray[i]=totalReqStatisticsData.getDynaAuthServerName();
			  disconnectReq[i]=totalReqStatisticsData.getClientDisconRequests();
			  disconnectNack[i]=totalReqStatisticsData.getRadiusDynAuthClientDisconNaks();
			  disconnectAck[i]=totalReqStatisticsData.getRadiusDynAuthClientDisconAcks();
			  disconnectTimeout[i]=totalReqStatisticsData.getClientDisconTimeouts();
			  coaReq[i]=totalReqStatisticsData.getRadiusDynAuthClientCoARequests();
			  coaAck[i]=totalReqStatisticsData.getRadiusDynAuthClientCoAAcks();
			  coaNack[i]=totalReqStatisticsData.getRadiusDynAuthClientCoANaks();
			  coaTimeout[i]=totalReqStatisticsData.getRadiusDynAuthClientCoATimeouts();
			  epochTimeArray[i]=totalReqStatisticsData.getCreateTime().getTime();
			  serverAddress[i]=totalReqStatisticsData.getRadiusDynAuthServerAddress();
			  serverPort[i]=totalReqStatisticsData.getDynaAuthServerPort();
		  }
		  
		  reqChartData.setEsi(esiArray);
		  reqChartData.setCoaAck(coaAck);
		  reqChartData.setCoaNack(coaNack);
		  reqChartData.setCoaReq(coaReq);
		  reqChartData.setCoaTimeout(coaTimeout);
		  reqChartData.setDisconnectAck(disconnectAck);
		  reqChartData.setDisconnectNack(disconnectNack);
		  reqChartData.setDisconnectReq(disconnectReq);
		  reqChartData.setDisconnectTimeout(disconnectTimeout);
		  reqChartData.setEpochTime(epochTimeArray);
		  reqChartData.setNasReqDataArray(nasESIReqChartData);
		  reqChartData.setServerAddress(serverAddress);
		  reqChartData.setServerPort(serverPort);
		}
		return reqChartData;
	}
}


