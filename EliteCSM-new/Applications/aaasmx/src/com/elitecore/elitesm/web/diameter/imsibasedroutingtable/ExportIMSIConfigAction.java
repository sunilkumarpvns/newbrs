package com.elitecore.elitesm.web.diameter.imsibasedroutingtable;

import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData;
import com.elitecore.elitesm.util.constants.IPPoolConstant;
import com.elitecore.elitesm.util.constants.RoutingTableConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm;

public class ExportIMSIConfigAction  extends BaseWebAction{
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE, "Enter the execute method of "+getClass().getName());
			try{
				String routingParamId = request.getParameter("routingTableId");
				String peerId = request.getParameter("peerId");
				
				IMSIBasedRoutingTableBLManager imsiBasedRoutingTableBLManager = new IMSIBasedRoutingTableBLManager();
				List<IMSIFieldMappingData> imsiFieldMappingList = null;
				if(peerId !=null){
					 imsiFieldMappingList = imsiBasedRoutingTableBLManager.getIMSIConfigDataList(routingParamId,peerId);
				}else{
					 imsiFieldMappingList = imsiBasedRoutingTableBLManager.getIMSIConfigDataList(routingParamId);
				}
				
				DiameterPeerBLManager diameterPeerBLManager = new  DiameterPeerBLManager();
				
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition","attachment;filename=IMSIBasedRoutingConfigEntries.csv");
				ServletOutputStream out = response.getOutputStream();
				StringBuilder csvFormatFileData = new StringBuilder(IPPoolConstant.CSV_FILE_HEADERROW.length()+80);
				csvFormatFileData.append(RoutingTableConstants.CSV_FILE_HEADERROW).append(System.getProperty("line.separator"));
				    
			
				int i=0;
				if(imsiFieldMappingList != null && imsiFieldMappingList.size() > 0){
					for(IMSIFieldMappingData imsiFieldMappingData :imsiFieldMappingList ){
						String primaryPeerName = "";
						String secondaryPeerName ="";
						
						if(imsiFieldMappingData.getPrimaryPeerId() != null){
							DiameterPeerData diameterPeerData = diameterPeerBLManager.getDiameterPeerById(imsiFieldMappingData.getPrimaryPeerId());
							primaryPeerName=diameterPeerData.getName();
						}
						
						if(imsiFieldMappingData.getSecondaryPeerId() != null){
							DiameterPeerData diameterPeerData = diameterPeerBLManager.getDiameterPeerById(imsiFieldMappingData.getSecondaryPeerId());
							secondaryPeerName=diameterPeerData.getName();
						}
						
						csvFormatFileData.append(i+1).append(",").append((imsiFieldMappingData.getImsiRange() == null ? "" :imsiFieldMappingData.getImsiRange())).append(",").append(primaryPeerName).append(",").append(secondaryPeerName).append(",").append((imsiFieldMappingData.getTag() == null ? "" :imsiFieldMappingData.getTag()));
						i=i+1;
						csvFormatFileData.append(System.getProperty("line.separator"));
					}
				}
				
				    out.write(csvFormatFileData.toString().getBytes());
				    out.flush();
				    out.close();
					return null;
				
			}catch (Exception e) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}
	}
}
