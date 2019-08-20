package com.elitecore.elitesm.web.diameter.diameterepeergroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.exceptions.server.ParameterNotFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerRelationWithPeerGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterepeergroup.form.DiameterPeerGroupForm;

/**
 * @author nayana.rathod
 *
 */

public class CreateDiameterPeerGroupAction extends BaseWebAction{

	private static final String ERROR_DETAILS = "errorDetails";
	private static final String INFORMATION = "information";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_PEER_GROUP;
	private static final String DIAMETER_PEER_GROUP = CreateDiameterPeerGroupAction.class.getSimpleName();
	private static final String CREATE_FORWARD = "createDiameterPeerGroup";

	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(DIAMETER_PEER_GROUP,"Enter execute method of :- "+getClass().getName());
		
		try{
			
			/* Check Permission of Diameter Peer Group Module */
			checkActionPermission(request, ACTION_ALIAS);
			
			DiameterPeerGroupForm diameterPeerGroupForm = (DiameterPeerGroupForm)form;
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			
			DiameterPeerGroupBLManager blManager = new DiameterPeerGroupBLManager();
			DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
			DiameterPeerGroup diameterPeerGroupData = new DiameterPeerGroup();
			
			if("create".equals(diameterPeerGroupForm.getAction())){
				
				return createDiameterPeerGroup(blManager, diameterPeerGroupData, diameterPeerGroupForm, mapping, form, request);

			}else{
				Logger.getLogger().info(DIAMETER_PEER_GROUP, "Enter in Else method of Create Diameter Peer Group method : ");
				
				if(diameterPeerGroupForm.getPeerGroupName() == null){
					diameterPeerGroupForm.setPeerGroupName((String)request.getAttribute("peerGroupName"));
				}
				
				diameterPeerGroupForm.setDescription(getDefaultDescription(userName));
				
				List<DiameterPeerData> diameterPeerDataList = diameterPeerBLManager.getDiameterPeerList();
				diameterPeerGroupForm.setDiameterPeerDataList(diameterPeerDataList);
				
				List<DiameterPeerGroup> diameterPeerGroupList = blManager.getDiameterPeerGroupList();
				diameterPeerGroupForm.setDiameterPeerGroupList(diameterPeerGroupList);
				
				request.setAttribute("diameterPeerGroupForm", diameterPeerGroupForm);
				return mapping.findForward(CREATE_FORWARD);
				
			}

		}catch(ActionNotPermitedException e){
            Logger.logError(DIAMETER_PEER_GROUP,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add(INFORMATION, new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(DIAMETER_PEER_GROUP, "Error during Data Manager operation , reason : " + e.getMessage());
			Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("diameterpeergroup.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

	private void convertFromFormToData( DiameterPeerGroupForm diameterPeerGroupForm, DiameterPeerGroup diameterPeerGroupData){
		
		diameterPeerGroupData.setPeerGroupName(diameterPeerGroupForm.getPeerGroupName());
		diameterPeerGroupData.setDescription(diameterPeerGroupForm.getDescription());
		diameterPeerGroupData.setStateful(diameterPeerGroupForm.getStateful());
		diameterPeerGroupData.setTransactionTimeout(diameterPeerGroupForm.getTransactionTimeout());
		diameterPeerGroupData.setPeerList(diameterPeerGroupForm.getPeerList());
		diameterPeerGroupData.setGeoRedunduntGroup(diameterPeerGroupForm.getGeoRedunduntGroup());
		
	}
	
	public static void setPeersDataInList(List<String> peerNames, DiameterPeerGroupForm diameterPeerGroupForm){
		List<DiameterPeerRelationWithPeerGroup> peerList = new ArrayList<DiameterPeerRelationWithPeerGroup>();
		if(Collectionz.isNullOrEmpty(peerNames) == false){
			for(String peerName : peerNames){
				String[] peerNameAndWeightage = peerName.split("-W-");
				if(peerNameAndWeightage != null && peerNameAndWeightage.length >= 1){
					DiameterPeerRelationWithPeerGroup peerRelationWithPeerGroup = new DiameterPeerRelationWithPeerGroup();
					peerRelationWithPeerGroup.setPeerName(peerNameAndWeightage[0].trim());
					peerRelationWithPeerGroup.setWeightage(Integer.parseInt(peerNameAndWeightage[1].trim()));
					peerList.add(peerRelationWithPeerGroup);
				}
			}
			diameterPeerGroupForm.setPeerList(peerList);
		}
	}
	
	private ActionForward createDiameterPeerGroup(DiameterPeerGroupBLManager blManager, DiameterPeerGroup diameterPeerGroupData, DiameterPeerGroupForm diameterPeerGroupForm, ActionMapping mapping,ActionForm form,HttpServletRequest request){
		
		try{
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String[] peers = request.getParameterValues("selectedPeers");
			
			if(peers == null){
				throw new ParameterNotFoundException("Peer-entries not found in Peer Group");
			}
			
			setPeersDataInList(Arrays.asList(peers),diameterPeerGroupForm); 
								
			convertFromFormToData(diameterPeerGroupForm,diameterPeerGroupData);

			/*Create plugin Code*/
			blManager.createDiameterPeerGroup(diameterPeerGroupData, staffData);
			
			Logger.getLogger().info(DIAMETER_PEER_GROUP, "Plugin [" + diameterPeerGroupData.getPeerGroupName() + "] Created Successfully");
			
			request.setAttribute("responseUrl", "/searchDiameterPeerGroup.do");
			ActionMessage message = new ActionMessage("diameterpeergroup.create.success");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveMessages(request, messages);
			
			return mapping.findForward(SUCCESS);
		}catch(DuplicateParameterFoundExcpetion dpf){
			Logger.logError(DIAMETER_PEER_GROUP, "Returning error forward from " + getClass().getName());
			Logger.logTrace(DIAMETER_PEER_GROUP,dpf);
			Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("diameterpeergroup.create.duplicate.failure",diameterPeerGroupData.getPeerGroupName());
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION,message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE);
		}catch(Exception e) {					
			Logger.logError(DIAMETER_PEER_GROUP, "Returning error forward from " + getClass().getName());
			Logger.logTrace(DIAMETER_PEER_GROUP,e);
			Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("diameterpeergroup.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION,message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE);
		}
	}
}