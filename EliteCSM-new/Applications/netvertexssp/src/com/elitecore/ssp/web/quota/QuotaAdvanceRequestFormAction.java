package com.elitecore.ssp.web.quota;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;




import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.util.ChildAccountUtility;
import com.elitecore.ssp.util.DataManagerUtils;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.web.core.base.BaseWebAction;
import com.elitecore.ssp.web.quota.forms.QuotaAdvanceRequestForm;
import com.elitecore.ssp.web.subscriber.SubscriberAdditionalInfo;

public class QuotaAdvanceRequestFormAction extends BaseWebAction{

	
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response){
		
		System.out.println("Getting form");
		
		QuotaAdvanceRequestForm quotaAdvanceRequestForm = (QuotaAdvanceRequestForm) form;
		
		System.out.println("Got form : "+quotaAdvanceRequestForm);
		
		request.setAttribute("successCode", null);
		
		request.setAttribute("errorCode", null);
		
		if(null!=quotaAdvanceRequestForm){
			
			String actionPerformed = quotaAdvanceRequestForm.getActionPerformed();
			
			System.out.println("actionPerformed : "+actionPerformed);
			
			if(actionPerformed!=null && !actionPerformed.trim().isEmpty() && actionPerformed.trim().equalsIgnoreCase("requestQuotaAdvance")){
				
				DataManagerUtils dataManagerUtils = new DataManagerUtils();
				
				SubscriberProfile currentUser = (SubscriberProfile) request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
				
				ResultSet resultSet = null;
				
				Connection connection = null;
				
				if(null!=currentUser){
					String subscriberID = currentUser.getSubscriberID();			
					
					try{
						
						String query1 = "select PARAM5,PARAM6,PARAM9 from tblnetvertexcustomer where subscriberidentity = '"+subscriberID+"'";
						
						String query2 = "select PARAM1 from tbltelenorparam";
						
						String query3 = "select policyGroup.HSQVALUE,customer.subscriberpackage from TBLNETVERTEXCUSTOMER customer, tblmpolicygroup policyGroup"
											+" where customer.subscriberpackage = policygroup.name and customer.subscriberidentity = '"+subscriberID+"'";
						
						
						String query4 = "SELECT SUM (TOTALOCTETS) FROM tblquota_advance where SUBSCRIBERIDNETITY = '"+subscriberID+"'";

						
						
						
						String maxQuotaAllowedToTransfer = null;
						
						String noOfTimesUserRequested = null;
						
						String maxMonthAdvanceAllowed = null;
						
						String maxTimesQuotaRequestAllowed = null;
						
						long totalQuotaRequestedTillDate = 0;
						
						long maxMonthlyQuotaHSQ = 0;
						
						String policyName = null;
						
						connection = dataManagerUtils.getDatabaseConnection();
						
						resultSet = dataManagerUtils.executeQuery(query1,connection);
						
						if(null!=resultSet){
							
							while(resultSet.next()){
								maxQuotaAllowedToTransfer = resultSet.getString(1);
								noOfTimesUserRequested = resultSet.getString(2);
								maxMonthAdvanceAllowed = resultSet.getString(3);
								break;
							}
						}
						
						resultSet = dataManagerUtils.executeQuery(query2,connection);
						
						if(null!=resultSet){
							
							while(resultSet.next()){
								maxTimesQuotaRequestAllowed = resultSet.getString(1);
								break;
							}
						}
						
						resultSet = dataManagerUtils.executeQuery(query3,connection);
						if(null!=resultSet){
							while(resultSet.next()){
								maxMonthlyQuotaHSQ = resultSet.getLong(1);
								policyName = resultSet.getString(2);
							}
						}
						
						
						resultSet = dataManagerUtils.executeQuery(query4, connection);
						
						if(null!=resultSet){
							while(resultSet.next()){
								totalQuotaRequestedTillDate = resultSet.getLong(1);
							}
						}
						
						if(maxQuotaAllowedToTransfer==null || maxQuotaAllowedToTransfer.trim().isEmpty()){
							maxQuotaAllowedToTransfer = "0";
						}
						
						if(noOfTimesUserRequested == null || noOfTimesUserRequested.trim().isEmpty()){
							noOfTimesUserRequested = "0";
						}
						
						if(maxMonthAdvanceAllowed == null || maxMonthAdvanceAllowed.trim().isEmpty()){
							maxMonthAdvanceAllowed = "0";
						}
						
						if(maxTimesQuotaRequestAllowed == null || maxTimesQuotaRequestAllowed.trim().isEmpty()){
							maxTimesQuotaRequestAllowed = "0";
						}
						
						long maxBytesTransferAllowed = Integer.valueOf(maxQuotaAllowedToTransfer);						
						int advanceQuotaRequestCount = Integer.valueOf(noOfTimesUserRequested);	;						
						int maxMonthAdvanceQuotaAllowed = Integer.valueOf(maxMonthAdvanceAllowed);	;
						int maxTimesQuotaTransferAllowed = Integer.valueOf(maxTimesQuotaRequestAllowed);
						
						
						long maxMBTransferAllowed = ChildAccountUtility.convertBytesToMB(maxBytesTransferAllowed);
						
						System.out.println("maxMBTransferAllowed "+maxMBTransferAllowed);
						System.out.println("advanceQuotaRequestCount "+advanceQuotaRequestCount);
						System.out.println("maxMonthAdvanceQuotaAllowed "+maxMonthAdvanceQuotaAllowed);
						System.out.println("maxTimesQuotaTransferAllowed "+maxTimesQuotaTransferAllowed);
						
						SubscriberAdditionalInfo subscriberAdditionalInfo = new SubscriberAdditionalInfo(subscriberID, maxMBTransferAllowed, 
																								advanceQuotaRequestCount, maxMonthAdvanceQuotaAllowed, 
																								maxTimesQuotaTransferAllowed, maxMonthlyQuotaHSQ);
						
						
						int requestedForMonths = quotaAdvanceRequestForm.getNoOfMonth();
						int requestedQuotaInMB = quotaAdvanceRequestForm.getQuotaAmount();
						
						System.out.println("requestedForMonths : "+requestedForMonths);
						System.out.println("requestedQuotaInMB : "+requestedQuotaInMB);
						
						//Check if the Maximum Allowed limit is breached
						if(requestedQuotaInMB > maxMBTransferAllowed){							
							request.setAttribute("errorCode", "Request cannot be completed. This much amount of quota cannot be requested in Advance.");
						}else if(advanceQuotaRequestCount >=  maxTimesQuotaTransferAllowed){
							//Check if the Max Times Quota Request Limit is breached
							request.setAttribute("errorCode", "You have exceeded the Max limit of Requests.");
						}else if(requestedForMonths > maxMonthAdvanceQuotaAllowed){
							//Check if the Max months allowed Request Limit is breached
							request.setAttribute("errorCode", "You cannot request advance quota for this many months.");
						}else if(totalQuotaRequestedTillDate >= maxMonthlyQuotaHSQ){
							//Check if the total monthly usage allowed in the package is breached
							request.setAttribute("errorCode", "You have exceeded you max quota limit that you can request in Advance.");
						}else{
							creditTheRequestedAdvanceQuotaToUser(quotaAdvanceRequestForm.getTotalQuotaToBeMoved(), policyName, subscriberID, dataManagerUtils, connection);
							
							updateQuotaAdvanceDetails(requestedForMonths, requestedQuotaInMB, subscriberID, dataManagerUtils);
							
							updateRequestCountOfUser(subscriberID, dataManagerUtils);
							
							updateEDRRecords(requestedForMonths, requestedQuotaInMB, subscriberID, dataManagerUtils);
							
							updateNotificationForUser(dataManagerUtils, currentUser, quotaAdvanceRequestForm.getTotalQuotaToBeMoved());
							
							request.setAttribute("successCode", "Data advance credited successfully to your account as per the request");
							
						}
						
					}catch(SQLException sqlException){
						request.setAttribute("errorCode", "Cannot fulfill your request at this time. An error has occured. Please contact your system Administrator.");
					}catch(Exception exception){
						exception.printStackTrace();
					}finally{
						if(null!=connection){
							DataManagerUtils.closeQuietly(connection);
						}
						
						if(null!=resultSet){
							DataManagerUtils.closeQuietly(resultSet);
						}
					}
					
					
				}
			}
		}

		return mapping.findForward("quotaAdvancePage");
		
	}
	
	
	private void creditTheRequestedAdvanceQuotaToUser(long requestedAmount, String policyName, String subscriberID,DataManagerUtils dataManagerUtils,Connection connection) throws Exception{
		
//		String query1 = "select TOTALOCTETS from tblmsessionusagesummary where userid = '"+subscriberID+"' and aggregatekey = '"+policyName+"-MONTHLY'"
//							+"and MONITORINGKEY ='"+policyName+"'";
//		
//		ResultSet resultSet = dataManagerUtils.executeQuery(query1, connection);
		
		System.out.println("requestedAmount in MB : "+requestedAmount);
		System.out.println("Policy Name : "+policyName);
		System.out.println("SubscriberID : "+subscriberID);
		
		long bytesToBeSubtracted = ChildAccountUtility.convertMBToBytes((int) requestedAmount);
		
		String updateQueryForTotalOctets = "update tblmsessionusagesummary set TOTALOCTETS = TOTALOCTETS - "+ bytesToBeSubtracted +" where userid = '"+subscriberID+"'";
		
		dataManagerUtils.executeUpdateOrInsert(updateQueryForTotalOctets);
		
//		long totalUsedOctets = 0L;
//		
//		if(null!= resultSet){
//			while(resultSet.next()){
//				totalUsedOctets = resultSet.getLong(1);
//				
//				System.out.println("totalUsedOctets : "+totalUsedOctets);
//				System.out.println("Requested Bytes : "+ChildAccountUtility.convertMBToBytes((int) requestedAmount));
//				
//				long newAvailabelOctets = totalUsedOctets - ChildAccountUtility.convertMBToBytes((int) requestedAmount);
//				
//				System.out.println("newAvailabelOctets : "+newAvailabelOctets);
//				
//				String updateQuery = "update tblmsessionusagesummary set TOTALOCTETS = "+newAvailabelOctets+" where userid = '"+subscriberID+"'";
//				
//				System.out.println("Update Query : "+updateQuery);
//				
//				dataManagerUtils.executeUpdateOrInsert(updateQuery);
//				
//				break;
//			}
//		}
		
	}
	
	private void updateQuotaAdvanceDetails(int noOfMonths, int amount,String subscriberID, DataManagerUtils datamanagerUtils) throws Exception{
		
		long amountInBytes = ChildAccountUtility.convertMBToBytes(amount);
		
		String insertQuery = "insert into tblquota_advance(SUBSCRIBERIDNETITY,TOTALOCTETS) values ('"+subscriberID+"', "+amountInBytes+" )" ;
		
		for(int i=0;i<noOfMonths;i++){
			datamanagerUtils.executeUpdateOrInsert(insertQuery);
		}
	}
	
	private void updateRequestCountOfUser(String subscriberID,DataManagerUtils datamanagerUtils) throws Exception{
		String updateQuery = "UPDATE TBLNETVERTEXCUSTOMER SET PARAM6 = PARAM6 + 1 where subscriberidentity = '"+subscriberID+"'";
		
		datamanagerUtils.executeUpdateOrInsert(updateQuery);
	}
	
	
	private void updateEDRRecords(int noOfMonths, int advanceAsked, String subscriberID,DataManagerUtils datamanagerUtils) throws Exception{
		String insertQuery = "insert into tblEDR(subscriberidnetity,quotaRequested,noOfMonths) values ('"+subscriberID+"', "+ChildAccountUtility.convertMBToBytes(advanceAsked)+" , "+noOfMonths+" )" ;
		
		datamanagerUtils.executeUpdateOrInsert(insertQuery);
	}
	
	
	private void updateNotificationForUser(DataManagerUtils datamanagerUtils,SubscriberProfile currentUser, long totalQuotaAdvance) throws Exception{
		String userEmail = currentUser.getEmail();
		String phoneNo = currentUser.getPhone();
		String subscriberID = currentUser.getSubscriberID();
		String notificationQuery  ="insert into tblmnotificationqueue_v (NOTIFICATIONID,TOEMAILADDRESS,TOSMSADDRESS,EMAILSUBJECT,EMAILDATA,SMSDATA,EMAILSTATUS,SMSSTATUS,NOTIFICATIONRECIPIENT,SUBSCRIBERIDENTITY) values (SEQ_MNOTIFICATIONQUEUE.nextval,'"+userEmail+"','"+phoneNo+"','Telenor : Quota Advance Notification','<div>"
                  +"<p>Dear {Sub.UserName},</p>"                  
                  +"<p>You have applied for Quota Advance. Total " +totalQuotaAdvance+ " mb is reserved against your advance billing cycle. </p>"                  
                  +"<p>With Warm Regards,</p>"                 
                 +" <p>Team Telenor.</p>"
                 +"</div>','<div>"
                  +"<p>Dear {Sub.UserName},</p>"                  
                  +"<p>You have applied for Quota Advance. Total "+totalQuotaAdvance+ " mb is reserved against your advance billing cycle. </p>"                  
                  
                 +" <p>With Warm Regards,</p>"                  
                  +"<p>Team Telenor.</p>"
                  +"</div>','PENDING','PENDING',1,'"+subscriberID+"')";
		
		datamanagerUtils.executeUpdateOrInsert(notificationQuery);
	}

}
