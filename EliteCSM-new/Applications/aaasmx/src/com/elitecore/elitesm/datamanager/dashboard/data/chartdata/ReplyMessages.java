package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;

import java.util.List;

public class ReplyMessages {
	
	private List<ReplyMessageStatData> replyMessageDataList;
	private String[] replyMessages;
	
	public List<ReplyMessageStatData> getReplyMessageDataList() {
		return replyMessageDataList;
	}
	public String[] getReplyMessages() {
		return replyMessages;
	}
	public void setReplyMessageDataList(
			List<ReplyMessageStatData> replyMessageDataList) {
		this.replyMessageDataList = replyMessageDataList;
	}
	public void setReplyMessages(String[] replyMessages) {
		this.replyMessages = replyMessages;
	}
	
	

}
