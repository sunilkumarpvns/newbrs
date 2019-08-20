package com.elitecore.core.serverx.policies.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author subhashpunani
 * @version 1.0
 * @created 20-Jan-2009 5:29:01 PM
 */
public class PolicyData implements IPolicyData,Serializable,Cloneable {

	private static final long serialVersionUID = 2L;
	private String strPolicyName;
	private String strDescription;
	private String strCheckItem;
	private String strRejectItem;
	private String strReplyItem;
	private String strAddItem;
	private boolean isParseble;
	private List<TimeSlotData> timeSlotData;
	
	public PolicyData(){
		isParseble = true;
		timeSlotData = new ArrayList<TimeSlotData>();
	}

//	public void finalize() throws Throwable {
//
//	}

	/**
	 * 
	 * @param strPolicyName
	 */
	public void setPolicyName(String strPolicyName){
		this.strPolicyName = strPolicyName;
	}

	public String getPolicyName(){
		return strPolicyName;
	}

	public String getDescription() {
		return strDescription;
	}

	/**
	 * 
	 * @param strDescription
	 */
	public void setDescription(String strDescription){
		this.strDescription = strDescription;

	}

	public String getCheckItem(){
		return strCheckItem;
	}

	/**
	 * 
	 * @param strCheckItems
	 */
	public void setCheckItem(String strCheckItem){
		this.strCheckItem = strCheckItem;
	}

	public String getRejectItem(){
		return strRejectItem;
	}

	/**
	 * 
	 * @param rejectItem
	 */
	public void setRejectItem(String strRejectItem){
		this.strRejectItem = strRejectItem;
	}

	public String getReplyItem(){
		return strReplyItem;
	}

	/**
	 * 
	 * @param replyItemList
	 */
	public void setReplyItem(String strReplyItem){
		this.strReplyItem = strReplyItem;
	}
	
	public String getAddItem(){
		return strAddItem;
	}
	
	public void setAddItem(String strAddItem){
		this.strAddItem = strAddItem;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Policy Name  : " + strPolicyName);
		
		if(strDescription != null)
			out.println("Description  : " + strDescription);
		else
			out.println("Description  : ");
		
		if(strCheckItem != null)
			out.println("Check Items  : " + strCheckItem);
		else
			out.println("Check Items  : No Check-Item Configured");
		
		if(strRejectItem != null)
			out.println("Reject Items : " + strRejectItem);
		else
			out.println("Reject Items : No Reject-Item Configured");
		
		if(strReplyItem != null)
			out.println("Reply Items  : " + strReplyItem);
		else
			out.println("Reply Items  : No Reply-Item Configured");
		
		if(isParseble == false)
			out.println("Warning: Configured policy is not in parsable format.  This policy will not be applied.");
		return stringBuffer.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public void setParseble(boolean isParseble){
		this.isParseble = isParseble;
	}
	
	@Override
	public boolean isParseble(){
		return isParseble;
	}

	@Override
	public void addTimeSlots(List<TimeSlotData> timeSlots) {
		this.timeSlotData = timeSlots;
	}

	@Override
	public List<TimeSlotData> getTimeSlots() {
		return this.timeSlotData;
	}
}