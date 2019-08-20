package com.elitecore.elitesm.web.diameter.imsibasedroutingtable;

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
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RoutingTableConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm;

public class UpdateIMSIBasedRoutingTableAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="IMSI-BASED-ROUTING-CONF";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_IMSI_BASED_ROUTING_TABLE;
	private static final String INIT_UPDATE_FORWARD = "initUpdateIMSIRoutingTable";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			
			IMSIBasedRoutingTableForm imsiBasedRoutingTableForm = (IMSIBasedRoutingTableForm)form;
			IMSIBasedRoutingTableBLManager blManager = new IMSIBasedRoutingTableBLManager();
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String requestAction = request.getParameter("action");
			
			if(imsiBasedRoutingTableForm.getAction() == null){
				String strRoutingTableId = request.getParameter("routingTableId");
				if(Strings.isNullOrBlank(strRoutingTableId) == false){
					IMSIBasedRoutingTableData imsiBasedRoutingTableData = blManager.getImsiBasedRoutingTableData(strRoutingTableId);
					convertBeanToForm(imsiBasedRoutingTableForm,imsiBasedRoutingTableData);
					
					/* Fetch Diameter Peer List*/
					DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
					List<DiameterPeerData> diameterPeerDataList = diameterPeerBLManager.getDiameterPeerList();
					imsiBasedRoutingTableForm.setDiameterPeerDataList(diameterPeerDataList);
					
					request.setAttribute("diameterPeerDataList", diameterPeerDataList);
					request.setAttribute("imsiBasedRoutingTableData", imsiBasedRoutingTableData);
					request.setAttribute("imsiBasedRoutingTableForm", imsiBasedRoutingTableForm);
					return mapping.findForward(INIT_UPDATE_FORWARD);
				}
			}else if(requestAction != null && requestAction.equals("addEntries")){
				String imsiRangeStr = request.getParameter("imsiRange");
				String primaryPeerStr = request.getParameter("primaryPeer");
				String secondaryPeerStr = request.getParameter("secondaryPeer");
				String strRoutingTableId = request.getParameter("routingTableId");
				String strTag=request.getParameter("tag");
				String routingTableName = request.getParameter("routingTableName");
				String auditUId=request.getParameter("auditUId");
				
				IMSIFieldMappingData imsiFieldMappingData = new IMSIFieldMappingData();
				imsiFieldMappingData.setImsiRange(imsiRangeStr);
				
				DiameterPeerBLManager diameterBLManager = new DiameterPeerBLManager();
				
				if(primaryPeerStr != null && primaryPeerStr.equals("0")){
					imsiFieldMappingData.setPrimaryPeerName(null);
					imsiFieldMappingData.setPrimaryPeerId(null);
				}else{
					primaryPeerStr=primaryPeerStr.trim();
					imsiFieldMappingData.setPrimaryPeerName(primaryPeerStr);
					String peerId = diameterBLManager.getDiameterPeerIdByName(primaryPeerStr);
					imsiFieldMappingData.setPrimaryPeerId(peerId);
				}
				
				if(secondaryPeerStr != null && secondaryPeerStr.equals("0")){
					imsiFieldMappingData.setSecondaryPeerName(null);
					imsiFieldMappingData.setSecondaryPeerId(null);
				}else{
					secondaryPeerStr=secondaryPeerStr.trim();
					imsiFieldMappingData.setSecondaryPeerName(secondaryPeerStr);
					String peerId = diameterBLManager.getDiameterPeerIdByName(secondaryPeerStr);
					imsiFieldMappingData.setSecondaryPeerId(peerId);
				}
				imsiFieldMappingData.setRoutingTableId(strRoutingTableId);
				imsiFieldMappingData.setTag(strTag);
				
				staffData.setAuditName(routingTableName);
				if( auditUId != null && auditUId.length() > 0){
					staffData.setAuditId(auditUId);
				}
				
				String fieldMapId = blManager.addEntries(imsiFieldMappingData,staffData,ACTION_ALIAS);
				
				response.setContentType("text/plain");
				PrintWriter out = response.getWriter();
				
				out.print(fieldMapId);
				
				return null;
			}else if(imsiBasedRoutingTableForm.getAction().equals("update")){
				IMSIBasedRoutingTableData imsiBasedRoutingTableData = blManager.getImsiBasedRoutingTableData(imsiBasedRoutingTableForm.getRoutingTableId());
				imsiBasedRoutingTableData.setRoutingTableId(imsiBasedRoutingTableForm.getRoutingTableId());
				imsiBasedRoutingTableData.setRoutingTableName(imsiBasedRoutingTableForm.getImsiBasedRoutingTableName());
				imsiBasedRoutingTableData.setImsiIdentityAttributes(imsiBasedRoutingTableForm.getImsiIdentityAttributes());
				imsiBasedRoutingTableData.setAuditUId(imsiBasedRoutingTableForm.getAuditUId());
				
				staffData.setAuditName(imsiBasedRoutingTableData.getRoutingTableName());
				staffData.setAuditId(imsiBasedRoutingTableData.getAuditUId());
				
				blManager.updateRoutingTable(imsiBasedRoutingTableData,staffData,ACTION_ALIAS);
				
				request.setAttribute("responseUrl", "/initUpdateIMSIBasedRouting?routingTableId="+imsiBasedRoutingTableForm.getRoutingTableId());
				ActionMessage message = new ActionMessage("imsibased.routingtable.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS);
			}else if(requestAction != null && requestAction.equals("updateEntries")){
				 
				 String[] strSelectedIds = request.getParameterValues("hidden_fieldmap");
				 String[] strImsiRange = request.getParameterValues("imsiRange");
				 String[] strPrimaryPeer = request.getParameterValues("primaryPeer");
				 String[] strSecondaryPeer = request.getParameterValues("secondaryPeer");
				 String[] strTag=request.getParameterValues("tag");
				 String routingTableName = imsiBasedRoutingTableForm.getImsiBasedRoutingTableName();
				 String auditUid = imsiBasedRoutingTableForm.getAuditUId();
				 
				 staffData.setAuditName(routingTableName);
				 staffData.setAuditId(auditUid);
				 
				 DiameterPeerBLManager diameterBlManager = new DiameterPeerBLManager();
				
				 if(strSelectedIds != null){
					 for(int i=0;i<strSelectedIds.length;i++){
						 IMSIFieldMappingData imsiFieldMappingData = new IMSIFieldMappingData();
						 imsiFieldMappingData.setImsiFieldMapId(strSelectedIds[i]);
						 imsiFieldMappingData.setImsiRange(strImsiRange[i]);
						
						 if(strPrimaryPeer[i].equals("0")){
								imsiFieldMappingData.setPrimaryPeerName(null);
								imsiFieldMappingData.setPrimaryPeerId(null);
						 }else{
								strPrimaryPeer[i]=strPrimaryPeer[i].trim();
								imsiFieldMappingData.setPrimaryPeerName(strPrimaryPeer[i]);
								String primaryPeerid = diameterBlManager.getDiameterPeerIdByName(strPrimaryPeer[i]);
								imsiFieldMappingData.setPrimaryPeerId(primaryPeerid);
						 }
						 
						 if(strSecondaryPeer[i].equals("0")){
								imsiFieldMappingData.setSecondaryPeerName(null);
								imsiFieldMappingData.setSecondaryPeerId(null);
						}else{
								strSecondaryPeer[i]=strSecondaryPeer[i].trim();
								imsiFieldMappingData.setSecondaryPeerName(strSecondaryPeer[i]);
								String secondaryPeerid = diameterBlManager.getDiameterPeerIdByName(strSecondaryPeer[i]);
								imsiFieldMappingData.setSecondaryPeerId(secondaryPeerid);
						}
						
						imsiFieldMappingData.setTag(strTag[i]); 
						 
						if(imsiFieldMappingData.getTag() != null && imsiFieldMappingData.getTag().length() > 0){
						  imsiFieldMappingData.setTag(strTag[i]); 
						}else{
						 imsiFieldMappingData.setTag(null);
						}
						
						 blManager.updateEntries(imsiFieldMappingData,staffData,ACTION_ALIAS);
					 }
				 }
				 
				 request.setAttribute("responseUrl", "/initUpdateIMSIBasedRouting?routingTableId="+imsiBasedRoutingTableForm.getRoutingTableId());
				 ActionMessage message = new ActionMessage("imsibased.routingtable.entries.update.success");
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
					  
					String routingTableName = imsiBasedRoutingTableForm.getImsiBasedRoutingTableName();
					String auditUid = imsiBasedRoutingTableForm.getAuditUId();
						 
					staffData.setAuditName(routingTableName);
					staffData.setAuditId(auditUid);
					  
					blManager.deleteEntries(listSelectedIDs,staffData,ConfigConstant.DELETE_IMSI_BASED_ROUTING_TABLE);
					request.setAttribute("responseUrl", "/initUpdateIMSIBasedRouting?routingTableId="+imsiBasedRoutingTableForm.getRoutingTableId());
					ActionMessage message = new ActionMessage("imsibased.routingtable.entriesdelete.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				}catch(Exception exp){
					Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
					ActionMessage message = new ActionMessage("imsibased.routingtable.entriesdelete.failure");                                                         
					ActionMessages messages = new ActionMessages();                                                                                 
					messages.add("information", message);                                                                                           
					saveErrors(request, messages); 
					return mapping.findForward(FAILURE_FORWARD); 
				}
			}else if(imsiBasedRoutingTableForm.getAction().equals("importCSV")){
				try{
					String strRoutingTableId = request.getParameter("routingTableId");
					
					IMSIBasedRoutingTableData imsiBasedRoutingTableData = blManager.getImsiBasedRoutingTableData(strRoutingTableId);
					imsiBasedRoutingTableData.setImsiFieldMappingDataSet(getIMSIFieldMappingDataByCSVFile(imsiBasedRoutingTableForm.getFileUpload()));
					
					blManager.importImsiBasedConfigurtaion(imsiBasedRoutingTableData);
					request.setAttribute("responseUrl", "/initUpdateIMSIBasedRouting?routingTableId="+imsiBasedRoutingTableForm.getRoutingTableId());
					ActionMessage message = new ActionMessage("imsibased.routingtable.import.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				}catch(Exception exp){
					Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
					ActionMessage message = new ActionMessage("imsibased.routingtable.import.failure");                                                         
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
				
				MSISDNBasedRoutingTableBLManager msisdnBLManager = new MSISDNBasedRoutingTableBLManager();
				MSISDNBasedRoutingTableData msiBasedRoutingTableData =new MSISDNBasedRoutingTableData();
				
				response.setContentType("text/plain");
				PrintWriter out = response.getWriter();
				
				if(routingTableName != null){
					msiBasedRoutingTableData = msisdnBLManager.getMSISDNDataByName(routingTableName);
					if(msiBasedRoutingTableData == null){
						out.print(0);
					}else{
						out.print(msiBasedRoutingTableData.getRoutingTableId());
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
			ActionMessage message = new ActionMessage("imsibased.routingtable.update.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}

	private void convertBeanToForm(IMSIBasedRoutingTableForm searchIMSIBasedRoutingTableForm,IMSIBasedRoutingTableData imsiBasedRoutingTableData) {
		searchIMSIBasedRoutingTableForm.setRoutingTableId(imsiBasedRoutingTableData.getRoutingTableId());
		searchIMSIBasedRoutingTableForm.setImsiBasedRoutingTableName(imsiBasedRoutingTableData.getRoutingTableName());
		searchIMSIBasedRoutingTableForm.setAuditUId(imsiBasedRoutingTableData.getAuditUId());
		searchIMSIBasedRoutingTableForm.setImsiIdentityAttributes(imsiBasedRoutingTableData.getImsiIdentityAttributes());
		searchIMSIBasedRoutingTableForm.setImsiFieldMappingSet(imsiBasedRoutingTableData.getImsiFieldMappingDataSet());
	}
	
	protected Set<IMSIFieldMappingData> getIMSIFieldMappingDataByCSVFile(FormFile fileUpload) throws IOException, DataManagerException {
		Set<IMSIFieldMappingData> ipPoolDetailDataSet = new LinkedHashSet<IMSIFieldMappingData>();
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
					IMSIFieldMappingData imsiFieldMappingData = new IMSIFieldMappingData();
					imsiFieldMappingData.setImsiRange(fileDataArray[1]);
					
					String primaryPeerName = fileDataArray[2] ;
					if(primaryPeerName != null && primaryPeerName.length() > 0){
						DiameterPeerBLManager diameterPeerBLManager =  new DiameterPeerBLManager();
						String peerId = diameterPeerBLManager.getDiameterPeerIdByName(primaryPeerName.trim());
						imsiFieldMappingData.setPrimaryPeerId(peerId);
						imsiFieldMappingData.setPrimaryPeerName(primaryPeerName);
					}else{
						imsiFieldMappingData.setPrimaryPeerId(null);
						imsiFieldMappingData.setPrimaryPeerName(null);
					}
					
					String secondaryPeerName = fileDataArray[3] ;
					if(secondaryPeerName != null && secondaryPeerName.length() > 0){
						DiameterPeerBLManager diameterPeerBLManager =  new DiameterPeerBLManager();
						String peerId = diameterPeerBLManager.getDiameterPeerIdByName(secondaryPeerName.trim());
						imsiFieldMappingData.setSecondaryPeerId(peerId);
						imsiFieldMappingData.setSecondaryPeerName(secondaryPeerName);
					}else{
						imsiFieldMappingData.setSecondaryPeerId(null);
						imsiFieldMappingData.setSecondaryPeerName(null);
					}
					
					imsiFieldMappingData.setTag(fileDataArray[4]);
					
					ipPoolDetailDataSet.add(imsiFieldMappingData);
				}
			}
		}
		return ipPoolDetailDataSet; 
	}
}