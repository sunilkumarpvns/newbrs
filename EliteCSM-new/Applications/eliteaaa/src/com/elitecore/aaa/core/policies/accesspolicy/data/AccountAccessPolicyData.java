package com.elitecore.aaa.core.policies.accesspolicy.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;

public class AccountAccessPolicyData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String accessPolicyID;
	private String accessPolicyName;
	private String accessPolicyDesc;
    private char policyAccessFlag;
    ArrayList<AccessPolicyTime> accessPolicyTimeSlots;
    
	private static final int LAST_DAY_OF_WEEK=6;
	private static final int LAST_HOUR_OF_DAY=23;
	private static final int LAST_MINUTE_OF_HOUR=59;
	private static final int LAST_SECOND_OF_MINUTE=59;
	private static final int FIRST_DAY_OF_WEEK=0;
	private static final int FIRST_HOUR_OF_DAY=0;
	private static final int FIRST_MINUTE_OF_HOUR=0;
	private static final int FIRST_SECOND_OF_MINUTE=0;

	public AccountAccessPolicyData(String accessPolicyID,String accessPolicyName,
			String accessPolicyDesc, char policyAccessFlag) {
		this.accessPolicyID = accessPolicyID;
		this.accessPolicyName = accessPolicyName;
		this.accessPolicyDesc = accessPolicyDesc;
		this.policyAccessFlag = policyAccessFlag;
		accessPolicyTimeSlots = new ArrayList<AccessPolicyTime>();
	}

	
    public String getAccessPolicyID() {
		return accessPolicyID;
	}

	public String getAccessPolicyName() {
		return accessPolicyName;
	}

	public String getAccessPolicyDesc() {
		return accessPolicyDesc;
	}

	public ArrayList<AccessPolicyTime> getAccessPolicyTimeSlots() {
		return accessPolicyTimeSlots;
	}

	public char getPolicyAccessFlag() {
		return policyAccessFlag;
	}

	public void setPolicyAccessFlag(char policyAccessFlag) {
		this.policyAccessFlag = policyAccessFlag;
	}
	
	public void addTimeSlot(AccessPolicyTime accessPolicyTimeSlot){
		int i=0;
		int startTime = (accessPolicyTimeSlot.getStartDay()*24*60*60) + (accessPolicyTimeSlot.getStartHour()*60*60) + 
			(accessPolicyTimeSlot.getStartMinute()*60)+accessPolicyTimeSlot.getStartSecond();
		int endTime = (accessPolicyTimeSlot.getEndDay()*24*60*60) + (accessPolicyTimeSlot.getStopHour()*60*60) +
			(accessPolicyTimeSlot.getStopMinute()*60) + accessPolicyTimeSlot.getStopSecond();
		
		if(endTime<startTime){
			AccessPolicyTime firstTimeSlot = new AccessPolicyTime(accessPolicyTimeSlot.getStartDay(),accessPolicyTimeSlot.getStartHour(),
					accessPolicyTimeSlot.getStartMinute(),accessPolicyTimeSlot.getStartSecond(),LAST_DAY_OF_WEEK,LAST_HOUR_OF_DAY,
					LAST_MINUTE_OF_HOUR,LAST_SECOND_OF_MINUTE);
			AccessPolicyTime secondTimeSlot = new AccessPolicyTime(FIRST_DAY_OF_WEEK,FIRST_HOUR_OF_DAY,FIRST_MINUTE_OF_HOUR,
					FIRST_SECOND_OF_MINUTE,accessPolicyTimeSlot.getEndDay(),accessPolicyTimeSlot.getStopHour(),
					accessPolicyTimeSlot.getStopMinute(),accessPolicyTimeSlot.getStopSecond());
			
			addTimeSlot(firstTimeSlot);
			addTimeSlot(secondTimeSlot);
			return;
		}
		//Please don't use for loop instead of below while
		//We really don't want to move on next index when we remove element from Collection
		//If you use for loop then index(i) will skip element after removed one.
		while(i<accessPolicyTimeSlots.size()){
			AccessPolicyTime currentTimeSlot = accessPolicyTimeSlots.get(i);
			int result = currentTimeSlot.compareTo(accessPolicyTimeSlot);
			if(result==-1){
				break;
			}else if(result==0){
				accessPolicyTimeSlots.remove(i);
				accessPolicyTimeSlot.mergTime(currentTimeSlot);
				continue;
			}
			i++;
		}
		accessPolicyTimeSlots.add(i, accessPolicyTimeSlot);
	}
	
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.print("Policy Name : " + accessPolicyName);
		out.println("Description : " + accessPolicyDesc);
		out.println("Default Access : " + policyAccessFlag);
		for(int i=0;i<accessPolicyTimeSlots.size();i++){
			out.println("Time slot " + (i+1));
			out.println(accessPolicyTimeSlots.get(i));
		}
		return stringBuffer.toString();
	}
    
    public static void main(String args[]){
    	AccountAccessPolicyData accessPolicyData = new AccountAccessPolicyData("test","test","test",'A');
    	AccessPolicyTime policyTime = new AccessPolicyTime(0,0,0,0,2,0);
    	accessPolicyData.addTimeSlot(policyTime);
    	AccessPolicyTime policyTime1 = new AccessPolicyTime(0,6,0,0,8,0);
    	accessPolicyData.addTimeSlot(policyTime1);
    	AccessPolicyTime policyTime2 = new AccessPolicyTime(0,4,0,0,8,0);
    	accessPolicyData.addTimeSlot(policyTime2);
    	AccessPolicyTime policyTime3 = new AccessPolicyTime(0,3,0,0,4,30);
    	accessPolicyData.addTimeSlot(policyTime3);
    	System.out.println(accessPolicyData.toString());
    }
}
