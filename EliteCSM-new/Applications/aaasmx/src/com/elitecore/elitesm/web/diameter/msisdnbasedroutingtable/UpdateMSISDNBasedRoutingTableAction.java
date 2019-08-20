package com.elitecore.elitesm.web.diameter.msisdnbasedroutingtable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNFieldMappingData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RoutingTableConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.msisdnbasedroutingtable.form.MSISDNBasedRoutingTableForm;

public class UpdateMSISDNBasedRoutingTableAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="MSISDN-BASED-ROUTING-CONF";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_MSISDN_BASED_ROUTING_TABLE;
	private static final String INIT_UPDATE_FORWARD = "initUpdateMSISDNRoutingTable";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			
			MSISDNBasedRoutingTableForm msisdnBasedRoutingTableForm = (MSISDNBasedRoutingTableForm)form;
			MSISDNBasedRoutingTableBLManager blManager = new MSISDNBasedRoutingTableBLManager();
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String requestAction = request.getParameter("action");
			
			if(msisdnBasedRoutingTableForm.getAction() == null){
				String strRoutingTableId = request.getParameter("routingTableId");
				if(Strings.isNullOrBlank(strRoutingTableId) == false){
					MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = blManager.getMSISDNBasedRoutingTableData(strRoutingTableId);
					convertBeanToForm(msisdnBasedRoutingTableForm,msisdnBasedRoutingTableData);
					
					/* Fetch Diameter Peer List*/
					DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
					List<DiameterPeerData> diameterPeerDataList = diameterPeerBLManager.getDiameterPeerList();
					msisdnBasedRoutingTableForm.setDiameterPeerDataList(diameterPeerDataList);
					
					request.setAttribute("diameterPeerDataList", diameterPeerDataList);
					request.setAttribute("msisdnBasedRoutingTableData", msisdnBasedRoutingTableData);
					request.setAttribute("msisdnBasedRoutingTableForm", msisdnBasedRoutingTableForm);
					return mapping.findForward(INIT_UPDATE_FORWARD);
				}
			}else if(requestAction != null && requestAction.equals("addEntries")){
				String msisdnRangeStr = request.getParameter("msisdnRange");
				String primaryPeerStr = request.getParameter("primaryPeer");
				String secondaryPeerStr = request.getParameter("secondaryPeer");
				String strRoutingTableId = request.getParameter("routingTableId");
				String strTag=request.getParameter("tag");
				String routingTableName = request.getParameter("routingTableName");
				String auditUId=request.getParameter("auditUId");
				
				MSISDNFieldMappingData msisdnFieldMappingData =new MSISDNFieldMappingData();
				msisdnFieldMappingData.setMsisdnRange(msisdnRangeStr);
				
				DiameterPeerBLManager diameterBLManager = new DiameterPeerBLManager();
				
				if(primaryPeerStr != null && primaryPeerStr.equals("0")){
					msisdnFieldMappingData.setPrimaryPeerName(null);
					msisdnFieldMappingData.setPrimaryPeerId(null);
				}else{
					primaryPeerStr=primaryPeerStr.trim();
					msisdnFieldMappingData.setPrimaryPeerName(primaryPeerStr);
					String peerId = diameterBLManager.getDiameterPeerIdByName(primaryPeerStr);
					msisdnFieldMappingData.setPrimaryPeerId(peerId);
				}
				
				if(secondaryPeerStr != null && secondaryPeerStr.equals("0")){
					msisdnFieldMappingData.setSecondaryPeerName(null);
					msisdnFieldMappingData.setSecondaryPeerId(null);
				}else{
					secondaryPeerStr=secondaryPeerStr.trim();
					msisdnFieldMappingData.setSecondaryPeerName(secondaryPeerStr);
					String peerId = diameterBLManager.getDiameterPeerIdByName(secondaryPeerStr);
					msisdnFieldMappingData.setSecondaryPeerId(peerId);
				}
				msisdnFieldMappingData.setRoutingTableId(strRoutingTableId);
				msisdnFieldMappingData.setTag(strTag);
				
				staffData.setAuditName(routingTableName);
				if( auditUId != null && auditUId.length() > 0){
					staffData.setAuditId(auditUId);
				}
				
				String fieldMapId = blManager.addEntries(msisdnFieldMappingData,staffData,ACTION_ALIAS);
				
				response.setContentType("text/plain");
				PrintWriter out = response.getWriter();
				
				out.print(fieldMapId);
				
				return null;
			}else if(msisdnBasedRoutingTableForm.getAction().equals("update")){
				MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = blManager.getMSISDNBasedRoutingTableData(msisdnBasedRoutingTableForm.getRoutingTableId());			
				
				msisdnBasedRoutingTableData.setRoutingTableId(msisdnBasedRoutingTableForm.getRoutingTableId());
				msisdnBasedRoutingTableData.setRoutingTableName(msisdnBasedRoutingTableForm.getMsisdnBasedRoutingTableName());
				msisdnBasedRoutingTableData.setMsisdnIdentityAttributes(msisdnBasedRoutingTableForm.getMsisdnIdentityAttributes());
				msisdnBasedRoutingTableData.setMcc(msisdnBasedRoutingTableForm.getMcc());
				msisdnBasedRoutingTableData.setMsisdnLength(msisdnBasedRoutingTableForm.getMsisdnLength());
				msisdnBasedRoutingTableData.setAuditUId(msisdnBasedRoutingTableForm.getAuditUId());
				
				staffData.setAuditName(msisdnBasedRoutingTableData.getRoutingTableName());
				staffData.setAuditId(msisdnBasedRoutingTableData.getAuditUId());
				
				blManager.updateRoutingTable(msisdnBasedRoutingTableData,staffData,ACTION_ALIAS);
				
				request.setAttribute("responseUrl", "/updateMSISDNBasedRouting?routingTableId="+msisdnBasedRoutingTableForm.getRoutingTableId());
				ActionMessage message = new ActionMessage("msisdnbased.routingtable.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS);
			}else if(requestAction != null && requestAction.equals("updateEntries")){
				 
				 String[] strSelectedIds = request.getParameterValues("hidden_fieldmap");
				 String[] strMsisdnRange = request.getParameterValues("msisdnRange");
				 String[] strPrimaryPeer = request.getParameterValues("primaryPeer");
				 String[] strSecondaryPeer = request.getParameterValues("secondaryPeer");
				 String[] strTag=request.getParameterValues("tag");
				 
				 String routingTableName = msisdnBasedRoutingTableForm.getMsisdnBasedRoutingTableName();
				 String auditUid = msisdnBasedRoutingTableForm.getAuditUId();
				 
				 staffData.setAuditName(routingTableName);
				 staffData.setAuditId(auditUid);
				 
				 DiameterPeerBLManager diameterBlManager = new DiameterPeerBLManager();
				
				 if(strSelectedIds != null){
					 for(int i=0;i<strSelectedIds.length;i++){
						 MSISDNFieldMappingData msisdnFieldMappingData = new MSISDNFieldMappingData();
						 msisdnFieldMappingData.setMsisdnFieldMapId(strSelectedIds[i]);
						 msisdnFieldMappingData.setMsisdnRange(strMsisdnRange[i]);
						
						 if(strPrimaryPeer[i].equals("0")){
							 msisdnFieldMappingData.setPrimaryPeerName(null);
							 msisdnFieldMappingData.setPrimaryPeerId(null);
						 }else{
								strPrimaryPeer[i]=strPrimaryPeer[i].trim();
								msisdnFieldMappingData.setPrimaryPeerName(strPrimaryPeer[i]);
								String primaryPeerid = diameterBlManager.getDiameterPeerIdByName(strPrimaryPeer[i]);
								msisdnFieldMappingData.setPrimaryPeerId(primaryPeerid);
						 }
						 
						 if(strSecondaryPeer[i].equals("0")){
							 msisdnFieldMappingData.setSecondaryPeerName(null);
							 msisdnFieldMappingData.setSecondaryPeerId(null);
						}else{
								strSecondaryPeer[i]=strSecondaryPeer[i].trim();
								msisdnFieldMappingData.setSecondaryPeerName(strSecondaryPeer[i]);
								String secondaryPeerid = diameterBlManager.getDiameterPeerIdByName(strSecondaryPeer[i]);
								msisdnFieldMappingData.setSecondaryPeerId(secondaryPeerid);
						}
						 msisdnFieldMappingData.setTag(strTag[i]);
						
						 if(msisdnFieldMappingData.getTag() != null && msisdnFieldMappingData.getTag().length() > 0){
							 msisdnFieldMappingData.setTag(strTag[i]); 
							}else{
								msisdnFieldMappingData.setTag(null);
							}
							
						 
						 blManager.updateEntries(msisdnFieldMappingData,staffData,ACTION_ALIAS);
					 }
				 }
				 
				 request.setAttribute("responseUrl", "/updateMSISDNBasedRouting?routingTableId="+msisdnBasedRoutingTableForm.getRoutingTableId());
				 ActionMessage message = new ActionMessage("msisdnbased.routingtable.entries.update.success");
				 ActionMessages messages = new ActionMessages();
				 messages.add("information",message);
				 saveMessages(request, messages);
				 return mapping.findForward(SUCCESS);
				
			}else if(requestAction != null && requestAction.equals("deleteEntries")){
				try{
					String[] strSelectedIds = request.getParameterValues("select");
					 List<String> listSelectedIDs = new ArrayList<String>();
					  if(strSelectedIds != null){
						 for(int i=0;i<strSelectedIds.length;i++){
							listSelectedIDs.add(strSelectedIds[i]);
						 }
					 } 
					  
					String routingTableName = msisdnBasedRoutingTableForm.getMsisdnBasedRoutingTableName();
					String auditUid = msisdnBasedRoutingTableForm.getAuditUId();
							 
					staffData.setAuditName(routingTableName);
					staffData.setAuditId(auditUid);
					  
					blManager.deleteEntries(listSelectedIDs,staffData,ConfigConstant.DELETE_MSISDN_BASED_ROUTING_TABLE);
					request.setAttribute("responseUrl", "/updateMSISDNBasedRouting?routingTableId="+msisdnBasedRoutingTableForm.getRoutingTableId());
					ActionMessage message = new ActionMessage("msisdnbased.routingtable.entriesdelete.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				}catch(Exception exp){
					Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
					ActionMessage message = new ActionMessage("msisdnbased.routingtable.entriesdelete.failure");                                                         
					ActionMessages messages = new ActionMessages();                                                                                 
					messages.add("information", message);                                                                                           
					saveErrors(request, messages); 
					return mapping.findForward(FAILURE_FORWARD); 
				}
			}else if(msisdnBasedRoutingTableForm.getAction().equals("importCSV")){
				try{
					String strRoutingTableId = request.getParameter("routingTableId");
					
					MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = blManager.getMSISDNBasedRoutingTableData(strRoutingTableId);
					msisdnBasedRoutingTableData.setMsisdnFieldMappingDataSet(getMSISDNFieldMappingDataByCSVFile(msisdnBasedRoutingTableForm.getFileUpload()));
					
					blManager.importMSISDNBasedConfigurtaion(msisdnBasedRoutingTableData);
					request.setAttribute("responseUrl", "/updateMSISDNBasedRouting?routingTableId="+msisdnBasedRoutingTableForm.getRoutingTableId());
					ActionMessage message = new ActionMessage("msisdnbased.routingtable.import.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				}catch(Exception exp){
					Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
					ActionMessage message = new ActionMessage("msisdnbased.routingtable.import.failure");                                                         
					ActionMessages messages = new ActionMessages();                                                                                 
					messages.add("information", message);                                                                                           
					saveErrors(request, messages); 
				}
			}else if(requestAction != null && requestAction.equals("retriveMSISDNData")){
				String routingTableName = request.getParameter("routingTableName");
				
				if(routingTableName != null && routingTableName.length() > 0){
					routingTableName = routingTableName.trim();
				}else{
					routingTableName = null;
				}
				
				IMSIBasedRoutingTableBLManager imsiBLManager = new IMSIBasedRoutingTableBLManager();
				IMSIBasedRoutingTableData imsiBasedRoutingTableData = new IMSIBasedRoutingTableData();
				
				response.setContentType("text/plain");
				PrintWriter out = response.getWriter();
				
				if(routingTableName != null){
					imsiBasedRoutingTableData = imsiBLManager.getIMSIDataByName(routingTableName);
					if(imsiBasedRoutingTableData == null){
						out.print(0);
					}else{
						out.print(imsiBasedRoutingTableData.getRoutingTableId());
					}
				}else{
					out.print(0);
				}
				return null;
			}
		}catch(ActionNotPermitedException e){
				Logger.logError(MODULE,"Error :-" + e.getMessage());
				printPermitedActionAlias(request);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("msisdnbased.routingtable.update.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}

	private void convertBeanToForm(MSISDNBasedRoutingTableForm msisdnBasedRoutingTableForm,MSISDNBasedRoutingTableData msisdnBasedRoutingTableData) {
		msisdnBasedRoutingTableForm.setRoutingTableId(msisdnBasedRoutingTableData.getRoutingTableId());
		msisdnBasedRoutingTableForm.setMsisdnBasedRoutingTableName(msisdnBasedRoutingTableData.getRoutingTableName());
		msisdnBasedRoutingTableForm.setAuditUId(msisdnBasedRoutingTableData.getAuditUId());
		msisdnBasedRoutingTableForm.setMsisdnIdentityAttributes(msisdnBasedRoutingTableData.getMsisdnIdentityAttributes());
		msisdnBasedRoutingTableForm.setMsisdnLength(msisdnBasedRoutingTableData.getMsisdnLength());
		msisdnBasedRoutingTableForm.setMcc(msisdnBasedRoutingTableData.getMcc());
		msisdnBasedRoutingTableForm.setMsisdnFieldMappingSet(msisdnBasedRoutingTableData.getMsisdnFieldMappingDataSet());
	}
	
	protected Set<MSISDNFieldMappingData> getMSISDNFieldMappingDataByCSVFile(FormFile fileUpload) throws IOException, DataManagerException {
		Set<MSISDNFieldMappingData> msisdnDetailDataSet = new LinkedHashSet<MSISDNFieldMappingData>();
		if(fileUpload.getFileName().endsWith(".csv")){
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileUpload.getInputStream()));
			String lineData = bufferedReader.readLine();
			Logger.logDebug(MODULE, "Header Row:"+lineData);
			if(!RoutingTableConstants.CSV_FILE_HEADERROW.equalsIgnoreCase(lineData)){
				Logger.logWarn(MODULE, "Invalid CSV File Header.");
			}
			// Read and validate Header Column
			while( (lineData = bufferedReader.readLine()) != null ) {
				String[] fileDataArray = lineData.split(",",-1);
				if(fileDataArray != null ) {
					MSISDNFieldMappingData msisdnFieldMappingData = new MSISDNFieldMappingData();
					msisdnFieldMappingData.setMsisdnRange(fileDataArray[1]);
					
					String primaryPeerName = fileDataArray[2] ;
					if(primaryPeerName != null && primaryPeerName.length() > 0){
						DiameterPeerBLManager diameterPeerBLManager =  new DiameterPeerBLManager();
						String peerId = diameterPeerBLManager.getDiameterPeerIdByName(primaryPeerName.trim());
						msisdnFieldMappingData.setPrimaryPeerId(peerId);
						msisdnFieldMappingData.setPrimaryPeerName(primaryPeerName);
					}else{
						msisdnFieldMappingData.setPrimaryPeerId(null);
						msisdnFieldMappingData.setPrimaryPeerName(null);
					}
					
					String secondaryPeerName = fileDataArray[3] ;
					if(secondaryPeerName != null && secondaryPeerName.length() > 0){
						DiameterPeerBLManager diameterPeerBLManager =  new DiameterPeerBLManager();
						String peerId = diameterPeerBLManager.getDiameterPeerIdByName(secondaryPeerName.trim());
						msisdnFieldMappingData.setSecondaryPeerId(peerId);
						msisdnFieldMappingData.setSecondaryPeerName(secondaryPeerName);
					}else{
						msisdnFieldMappingData.setSecondaryPeerId(null);
						msisdnFieldMappingData.setSecondaryPeerName(null);
					}
					
					msisdnFieldMappingData.setTag(fileDataArray[4]);
					
					msisdnDetailDataSet.add(msisdnFieldMappingData);
				}
			}
		}
		return msisdnDetailDataSet; 
	}
}