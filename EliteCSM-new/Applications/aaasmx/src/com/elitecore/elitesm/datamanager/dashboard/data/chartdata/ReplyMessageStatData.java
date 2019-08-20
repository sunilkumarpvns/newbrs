package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;

import java.util.List;

public class ReplyMessageStatData  {
	 
	    
	private String sid;
    private Long epochTime;
	private String replyMessageName;
	private Long replyMessageCount;
	private List<SIDDetailData> sidDetail;
	
	 
	 public ReplyMessageStatData(String sid,String replyMessageName,Long epochTime,Long replyMessageCount) {
		this.sid=sid;
		this.replyMessageName=replyMessageName;
		this.epochTime=epochTime;
		this.replyMessageCount=replyMessageCount;
	}
  
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	

	public String getReplyMessageName() {
		return replyMessageName;
	}

	public void setReplyMessageName(String replyMessageName) {
		this.replyMessageName = replyMessageName;
	}

	public Long getReplyMessageCount() {
		return replyMessageCount;
	}

	public void setReplyMessageCount(Long replyMessageCount) {
		this.replyMessageCount = replyMessageCount;
	}

	public List<SIDDetailData> getSidDetail() {
		return sidDetail;
	}

	public void setSidDetail(List<SIDDetailData> sidDetail) {
		this.sidDetail = sidDetail;
	}

	public Long getEpochTime() {
		return epochTime;
	}

	public void setEpochTime(Long epochTime) {
		this.epochTime = epochTime;
	}
	
	
}
