package com.elitecore.core.serverx.policies.data;

import java.util.List;



/**
 * @author Subhash Punani
 * @version 1.0
 * @created 20-Jan-2009 5:29:01 PM
 */
public interface IPolicyData {

	/**
	 * 
	 * @param strPolicyName
	 */
	public void setPolicyName(String strPolicyName);

	public String getPolicyName();

	/**
	 * 
	 * @param strDescription
	 */
	public void setDescription(String strDescription);

	/**
	 * 
	 * @param checkItemParseTree
	 */
	public void setCheckItem(String strCheckItem);

	public String getCheckItem();

	/**
	 * 
	 * @param rejectItem
	 */
	public void setRejectItem(String strRejectItem);

	/**
	 * 
	 * @param replyItemList
	 */
	public String getRejectItem();

	public void setReplyItem(String strReplyItem);

	public String getReplyItem();

	public void setAddItem(String strAddItem);

	public String getAddItem();
	
	public void setParseble(boolean isParseble);
	
	public boolean isParseble();

	public Object clone() throws CloneNotSupportedException;
	
	public void addTimeSlots(List<TimeSlotData> timeSlots);
	
	public List<TimeSlotData> getTimeSlots();
}
