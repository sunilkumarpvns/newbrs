package com.elitecore.ssp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicy;
import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsListChildProfiles.ChildProfileCriteria.Entry;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsUpdateSubscriberProfile;
import com.elitecore.ssp.util.logger.Logger;

public class ChildAccountUtility {
	private static final String MODULE = ChildAccountUtility.class.getSimpleName();
		
	public static final String[] accessControlPoliciesColor={"silver","gold","Tan","violet","purple","olive","teal","YellowGreen","Lime","brown","peru","gray","green","red","black","yellow","indigo","blue","cyan","magenta",};	
	
	public static List<ParentalPolicy> prepareDayAndTimeSlots( HttpServletRequest request ){
		Logger.logInfo(MODULE, " Called Method : prepareDayAndTimeSlots(..)");
		String srNoChain=request.getParameter("srNoChain");
		List<ParentalPolicy> parentalPolicyList=new ArrayList<ParentalPolicy>();
		if(srNoChain!=null && srNoChain.trim().length()!=0){
			
			String[] srNoChainArr=srNoChain.split(",");
			
			for(int i=0; i<srNoChainArr.length; i++){
				
				String addOnPackageId=request.getParameter(("prData"+srNoChainArr[i]));										
				String weekDaysAr[] = request.getParameterValues(("weekDay"+srNoChainArr[i])); 
				String timePeriod=request.getParameter(("time"+srNoChainArr[i]));				
				String weekDays="";
				
				if (weekDaysAr != null && weekDaysAr.length != 0) {						
					for (String day:weekDaysAr) {
						weekDays+=day+",";						 
					}
					weekDays=weekDays.substring(0,weekDays.trim().length()-1);
					weekDays=weekDays.trim();
				}			
				
				if(weekDays!=null && addOnPackageId!=null && timePeriod!=null){
					ParentalPolicy obj=new ParentalPolicy();
					obj.setAddOnPackageID(Long.parseLong(addOnPackageId));
					obj.setDaysOfTheWeek(weekDays);							
					obj.setTimePeriod(timePeriod);
					parentalPolicyList.add(obj);
				}

			}
			
			return parentalPolicyList;// ChildAccountUtility.convertAddOnPackageListToArray(parentalPolicyList);
		}else{
			ParentalPolicy obj=new ParentalPolicy();
			//obj.setAddOnPackageId(0L);
			parentalPolicyList.add(obj);
			return parentalPolicyList;//ChildAccountUtility.convertAddOnPackageListToArray(parentalPolicyList);			
		}				
	}
	
	public static List<ParentalPolicy> parseToDayAndTimeSlots(String data,AddOnPackage[] promotionalData){
		Logger.logInfo(MODULE, " Called Method : parseToDayAndTimeSlots(..)");
		
		String[][] dataArray=new String[7][24];
				
		String[] tempDataArr=data.split(",", 168);
		int dayRow=0,hrColumn=0;				
		for(String name:tempDataArr){
			
				dataArray[dayRow][hrColumn]=name;
				if(hrColumn==23){
					hrColumn=0;
					dayRow++;
				}else{
					hrColumn++;
				}																						
		}
				
		/* GETTING WEEKWISE DATA BEGIN*/	 	
		String[] days={"1","2","3","4","5","6","7"};
	 	
	 	AddOnPackage[] accessControlPromotionalData = getAccessControlPolicies(promotionalData);
	 	int totAccessControlAddOns=0;
	 	if(accessControlPromotionalData != null){
	 		totAccessControlAddOns = accessControlPromotionalData.length;
	 	}	 	
	
		int flag=0;
		String weekWise="";				
		int toFlag=0;
		String[][] tempArr=new String[accessControlPoliciesColor.length][7];
		
		for(int c=0; c<accessControlPoliciesColor.length; c++){
							
			for(int i=0; i<7; i++){
									
					weekWise="";					 
					flag=0;					
					
				for(int j=0; j<24; j++){
									
					if(j!=0 && flag==1){
							if((dataArray[i][j].equalsIgnoreCase(accessControlPoliciesColor[c]) && dataArray[i][j-1].equalsIgnoreCase(accessControlPoliciesColor[c]))){								
								toFlag=1;	
								if(j==23){

									weekWise+=" to "+(j)+"-"+accessControlPoliciesColor[c];						
								}
							}else{								
								if(toFlag==1){									

									weekWise+=" to "+(j-1)+"-"+accessControlPoliciesColor[c];									
									toFlag=0;
								}else if(dataArray[i][j].equalsIgnoreCase(accessControlPoliciesColor[c])){

									weekWise+=","+j;									
								}
							}							
					}else{
						
						if(dataArray[i][j].equalsIgnoreCase(accessControlPoliciesColor[c])){														

							weekWise+=""+j;
							flag=1;							
						}												
					}
				}		
				tempArr[c][i]=weekWise;
						
			}

		}
		/* GETTING WEEKWISE DATA END */
				
		/* FILTERING TIME-SLOTS BEGIN */
 		
		String[] timeSlots1;
		String[] timeSlots2;
		String dayColorAndSlots="";					
		String weekDays="";
		String timePeriod="";		
		List<ParentalPolicy> parentalPolicyList=new ArrayList<ParentalPolicy>();
		
		long addOnPackageId;
		for(int c=0; c<totAccessControlAddOns; c++){	
			dayColorAndSlots+="\n"+accessControlPoliciesColor[c];				
			addOnPackageId=accessControlPromotionalData[c].getAddOnPackageID(); 
			for(int d1=0; d1<7;d1++){											
				timeSlots1=tempArr[c][d1].split(",",12);				
				if(timeSlots1!=null && timeSlots1.length>0){
					
					for(int t1=0; t1<timeSlots1.length;	t1++){
	
						if(timeSlots1[t1]!=null && timeSlots1[t1].length()>0){						
							
							if(timeSlots1[t1].length()<=2){
								
								//timeSlots1[t1]+=" to "+timeSlots1[t1];
								timeSlots1[t1]+=" to "+timeSlots1[t1]+"-"+accessControlPoliciesColor[c];
							}
							boolean isDuplicate=(dayColorAndSlots.contains(timeSlots1[t1]));							
							if(isDuplicate==false){							
								dayColorAndSlots+="\n"+timeSlots1[t1]+"-"+days[d1];
								weekDays=days[d1];
								if(timeSlots1[t1].contains("-")){
									timePeriod=timeSlots1[t1].substring(0,timeSlots1[t1].indexOf("-"));
								}else{
									timePeriod=timeSlots1[t1];
								}
							}
													 							
							for(int d2=d1+1; d2<7;d2++){						
								timeSlots2=tempArr[c][d2].split(",",12);								
								
								if(timeSlots2 !=null && timeSlots2.length>0){
									for(int t2=0; t2<timeSlots2.length;t2++){										
										if(timeSlots2[t2]!=null && timeSlots2[t2].length()<=2){											
											timeSlots2[t2]+=" to "+timeSlots2[t2]+"-"+accessControlPoliciesColor[c];
										}
										if(timeSlots1[t1].equals(timeSlots2[t2]) && timeSlots2[t2].length()>0){
											if(isDuplicate==false){
												dayColorAndSlots+="-"+days[d2];												
												//weekDays+="-"+days[d2];
												weekDays+=","+days[d2];
											}											
											timeSlots2[t2]="";																						
											break;
										}
									}						
								}
							}	
							if(isDuplicate==false){																
								ParentalPolicy obj=new ParentalPolicy();
								obj.setAddOnPackageID(addOnPackageId);
								obj.setDaysOfTheWeek(weekDays);						
								timePeriod=timePeriod.replaceAll(" to ", "-");
								timePeriod+=":59:59";
								obj.setTimePeriod(timePeriod);								
								parentalPolicyList.add(obj);							
							}
						}						
					}					

				}
			}
			dayColorAndSlots+="\n.........................................\n";
		}	
		/*if(AddOnPackageList != null && AddOnPackageList.size()<=0){
			
			AddOnPackage obj=new AddOnPackage();
			obj.setAddOnPackageId(0L);
			obj.setDaysOfTheWeek("0");							
			obj.setTimePeriod("0-0");			
			AddOnPackageList.add(obj);
		}*/
		
		//System.out.println(dayColorAndSlots+"\n--------------------------------------------\n\n");
		
		/* FILTERING TIME-SLOTS END */

		return parentalPolicyList;//ChildAccountUtility.convertAddOnPackageListToArray(parentalPolicyList);
	}		
	
	/*public static List<ParentalPolicy> convertAddOnPackageListToArray(List<ParentalPolicy> parentalPolicyList){
		Logger.logInfo(MODULE, " Called Method : convertAddOnPackageListToArray(..)");
		List<ParentalPolicy> parentalPolicyList = new ArrayList<ParentalPolicy>();
		if(parentalPolicyList != null){
		Logger.logDebug(MODULE, " Total Configured Policies :"+parentalPolicyList.size());
			int i=0;
			while(i < parentalPolicyList.size()){
				//parentalPolicyArr[i] = parentalPolicyList.get(i);
				parentalPolicyList.add(parentalPolicyList.get(i));
				i++;
			}
		}
		return parentalPolicyList;
	}*/

	public static AddOnPackage[] getAccessControlPolicies(AddOnPackage[] promotionalData){
		Logger.logInfo(MODULE, " Called Method : getAccessControlPolicies(..)");
		AddOnPackage[] accessControlPolociesArr=null; 
		if(promotionalData!=null){
			List<AddOnPackage> accessControlPolociesList=new ArrayList<AddOnPackage>();
	  		for(AddOnPackage accessControlBean:promotionalData){	  			
	  			if(accessControlBean.getAddOnPackageType().equalsIgnoreCase("AccessControl")){
	  				accessControlPolociesList.add(accessControlBean);
	  			}
	  		}
	  		accessControlPolociesArr=ChildAccountUtility.convertPromotionalDataListToArray(accessControlPolociesList);
		}
		return accessControlPolociesArr;
	}
	
	public static AddOnPackage[] getQuotaControlPolicies(AddOnPackage[] promotionalData){
		Logger.logInfo(MODULE, " Called Method : getQuotaControlPolicies(..) "+promotionalData);
		
		AddOnPackage[] quotaControlPoliciesArr=null; 
		if(promotionalData!=null){
			List<AddOnPackage> quotaControlPolociesList=new ArrayList<AddOnPackage>();
	  		for(AddOnPackage quotaControlBean:promotionalData){	  			
	  			
	  			if(quotaControlBean.getAddOnPackageType().equalsIgnoreCase("QuotaControl")){
	  				quotaControlPolociesList.add(quotaControlBean);
	  			}
	  		}	  		
	  		quotaControlPoliciesArr=ChildAccountUtility.convertPromotionalDataListToArray(quotaControlPolociesList);
		}
		return quotaControlPoliciesArr;
	}
	
	public static AddOnPackage[] convertPromotionalDataListToArray(List<AddOnPackage> promotionalDataList){
		Logger.logInfo(MODULE, " Called Method : convertPromotionalDataListToArray(..)");
		AddOnPackage[] promotionalDataArr = null;
		if(promotionalDataList != null){
			Object[] beanArr = promotionalDataList.toArray();  		  
			promotionalDataArr = Arrays.copyOf(beanArr, beanArr.length, AddOnPackage[].class);					
		}
		return promotionalDataArr;
	}

	public static List<Entry> convertMapToList(Map<String, String> searchCriteriaMap) {
		List<Entry> entryList=null;
		if (searchCriteriaMap != null && !searchCriteriaMap.isEmpty()) {
			entryList=new ArrayList<Entry>();
			for(Map.Entry<String ,String> temp:searchCriteriaMap.entrySet()){
                  Entry entry=new Entry();
                  entry.setKey(temp.getKey());
                  entry.setValue(temp.getValue());
                  entryList.add(entry);
			}
		}
		return entryList;
	}
	
	
	public static List<com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsUpdateSubscriberProfile.SubscriberProfile.Entry> convertMapToEntryList(HashMap<String, String> map){
		List<com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsUpdateSubscriberProfile.SubscriberProfile.Entry> entryList = new ArrayList<WsUpdateSubscriberProfile.SubscriberProfile.Entry>();
		
		for(String mapKey : map.keySet()){
			WsUpdateSubscriberProfile.SubscriberProfile.Entry entry = new WsUpdateSubscriberProfile.SubscriberProfile.Entry();
			entry.setKey(mapKey);
			entry.setValue(map.get(mapKey));
			
			entryList.add(entry);
		}
		
		return entryList;
	}
	
	
	public static long convertMBToBytes(int mbToConvert){
		long convertedBytes = 0;
		convertedBytes = mbToConvert * 1024 * 1024;
		
		return convertedBytes;
	}
	
	public static long convertBytesToMB(long bytesToBeConverted){
		long convertedMB = 0;
		
		long kiloBytes = bytesToBeConverted/1024;
		
		convertedMB = kiloBytes/1024;
		
		return convertedMB;
	}
	
}
