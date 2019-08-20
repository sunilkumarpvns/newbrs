package com.elitecore.elitesm.web.dashboard.widget.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.sf.json.JSONObject;

import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.ReplyMessageStatData;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.ReplyMessages;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.SIDDetailData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetTypeConstants;
import com.elitecore.elitesm.web.dashboard.widget.json.Header;
import com.elitecore.elitesm.web.dashboard.widget.json.WidgetJSON;

public class ReplyMessageStatSummaryDataProvider  {
	
	
	private static final String ID = WidgetTypeConstants.REPLYMESSAGESTATSUMMARY.name();
	private static final String MODULE=ReplyMessageStatSummaryDataProvider.class.getSimpleName();
    
	private Header getHeader(){
		Header header = new Header();
		header.setId(ID);
		header.setType("tableschema");
		return header;
	}
	
	
	public String provideInitialData(String widgetID) {
		String data=null;
		try{
			ReplyMessages createDummyReplyMessageStat = createDummyReplyMessageStat();
			JSONObject jsonObject = JSONObject.fromObject(createDummyReplyMessageStat);
			data=jsonObject.toString();
			Logger.logDebug(MODULE,"data ::::"+data);
			WidgetJSON widgetJSON = new WidgetJSON(getHeader(), data);
			return JSONObject.fromObject(widgetJSON).toString();
		}catch(Exception exp){
			Logger.logError(MODULE, exp.getMessage());
		}
		return data;
	}

	public WidgetJSON provideData() {
		try{
			ReplyMessages createDummyReplyMessageStat = createDummyReplyMessageStat();
			JSONObject jsonObject = JSONObject.fromObject(createDummyReplyMessageStat);
			String data=jsonObject.toString();
			Logger.logDebug(MODULE,"data ::::"+data);
			WidgetJSON widgetJSON = new WidgetJSON(getHeader(), data);
			return widgetJSON;
		}catch(Exception exp){
			Logger.logError(MODULE, exp.getMessage());
		}
		return null;
	}

	public String getName() {
		return ID;
	}
	


  public ReplyMessages createDummyReplyMessageStat(){
	  ReplyMessages replyMessagesData=new ReplyMessages();
	 
	  List<ReplyMessageStatData> replyMessageDataList=new ArrayList<ReplyMessageStatData>();
	  String[] replyMessages=new String[2];
	  for(int i=0;i<2;i++){
		  
		  String replyMessage="";
		     if(i%2==0){
		    	 replyMessage=ReplyMessage.AUTHENTICATIONSUCCESS.getReplyMessage();
		     }else if(i%3 == 0){
		    	 replyMessage=ReplyMessage.USERNOTFOUND.getReplyMessage();
		     }else if(i%5 == 0){
		    	 replyMessage=ReplyMessage.EAPFAILURE.getReplyMessage();
		     }else {
		    	 replyMessage=ReplyMessage.MACVALIDATIONFAILED.getReplyMessage();
		     }
		     replyMessages[i]=replyMessage;
		     List<SIDDetailData> sidList=buildDummySidList();
		     ReplyMessageStatData replyMessageStatData = new ReplyMessageStatData("sid-"+i,replyMessage,new Date().getTime(),300l);
		     replyMessageStatData.setSidDetail(sidList);
		     
		     replyMessageDataList.add(replyMessageStatData);
	  }
	  
	  replyMessagesData.setReplyMessageDataList(replyMessageDataList);
	  replyMessagesData.setReplyMessages(replyMessages);
	  return replyMessagesData;
  }
 
  
  private List<SIDDetailData> buildDummySidList(){
	 List<SIDDetailData> sidList=new ArrayList<SIDDetailData>();
	 Random random=new Random();
	 SID[] values = SID.values();
	 for(SID sid:values){
		 int integerValue = random.nextInt(500);
		 SIDDetailData detailData=new SIDDetailData(sid.getSidName(),integerValue);
		 sidList.add(detailData);
	 }
	 return sidList;
  }


  
  enum ReplyMessage{
	  
	  AUTHENTICATIONSUCCESS("Authentication Success"),
	  USERNOTFOUND("User not found"),
	  EAPFAILURE("EAP-Failure"),
	  MACVALIDATIONFAILED("MAC Validation Failed");
	  
	  private ReplyMessage(String replyMessage){
		  this.replyMessage=replyMessage;
	  }
	  
	  public String getReplyMessage(){
		  return replyMessage;
	  }
	  final private String replyMessage;
  }
  
  enum SID{
	  
	  SIDONE("sid-one"),
	  SIDTWO("sid-two"),
	  SIDTHREE("sid-three"),
	  SIDFOUR("sid-four");
	  
	  private final String sidName;
	 
	  private SID(String sidName){
		  this.sidName=sidName;
	  }
	  
	  public String getSidName() {
			return sidName;
	  }
  }
  
 
	

}






/*
Authentication Success', 
'User not found', 
'Authentication failed due to Invalid password', 
'EAP-Failure',
'Authentication Failed',
'Max Login Limit Reached',
'Concurrency Failed',
'No Policy Satisfied',
'Packet validation failed',
'Account is blacklisted',
'Authorization Failed',
'Account is not active',
'Account Expired',
'Account Credit Limit Exceeded',
'MAC Validation Failed',
'Invalid Calling Station Id',
'Calling Station Id not found',
'Invalid Called Station Id',
'Called Station Id not found',
'Invalid NAS Port Type',
'NAS Port Type not found',
'Unsupported Authentication method'
*/