package com.elitecore.elitesm.web.diameter.diameterepeergroup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.diameterepeergroup.form.DiameterPeerGroupForm;

public class ViewDiameterPeerGroupAction extends BaseWebAction{
	protected static final String DIAMETER_PEER_GROUP = "ViewDiameterPeerGroupAction";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DIAMETER_PEER_GROUP;
	private static final String VIEW_FORWARD = "viewDiameterPeerGroup";

	@Override
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			
			try {
				Logger.logInfo(DIAMETER_PEER_GROUP, "Enter execute method of" + getClass().getName());
	
				DiameterPeerGroupBLManager diameterPeerGroupBLManager = new DiameterPeerGroupBLManager();
				DiameterPeerGroupForm diameterPeerGroupForm = (DiameterPeerGroupForm)form;
				
				String strDiaPeerGroupId = request.getParameter("peerGroupId");
				Logger.getLogger().info(DIAMETER_PEER_GROUP, "Peer Group Id is : " + strDiaPeerGroupId);
				
				String diaPeerGroupId = "";
				if(Strings.isNullOrBlank(strDiaPeerGroupId) == false){
					diaPeerGroupId = strDiaPeerGroupId;
				}
				
				if(Strings.isNullOrBlank(diaPeerGroupId) == false){
					
					DiameterPeerGroup diameterPeerGroup;
					
					diameterPeerGroup = diameterPeerGroupBLManager.getDiameterPeerGroupById(diaPeerGroupId);
					
					if(Strings.isNullOrBlank(diameterPeerGroup.getGeoRedunduntGroup())){
						diameterPeerGroupForm.setGeoRedunduntGroupName("NONE");
					}else{
						DiameterPeerGroup diameterPeerGroupData = diameterPeerGroupBLManager.getDiameterPeerGroupById(diameterPeerGroup.getGeoRedunduntGroup());
						diameterPeerGroupForm.setGeoRedunduntGroupName(diameterPeerGroupData.getPeerGroupName());
					}
					
					convertBeanToFrom(diameterPeerGroup, diameterPeerGroupForm);
					
					request.setAttribute("diameterPeerGroupForm",diameterPeerGroupForm);
					request.setAttribute("diameterPeerGroup",diameterPeerGroup);
				}
			}catch (DataManagerException managerExp) {
				Logger.logError(DIAMETER_PEER_GROUP, "Error during Data Manager operation, reason : " + managerExp.getMessage());
				Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("diameterpeergroup.view.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);

			}
			return mapping.findForward(VIEW_FORWARD);
		}else{
			Logger.logError(DIAMETER_PEER_GROUP, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	private void convertBeanToFrom(DiameterPeerGroup diameterPeerGroup, DiameterPeerGroupForm diameterPeerGroupForm) {
		diameterPeerGroupForm.setPeerGroupId(diameterPeerGroup.getPeerGroupId());
		diameterPeerGroupForm.setPeerGroupName(diameterPeerGroup.getPeerGroupName());
		diameterPeerGroupForm.setDescription(diameterPeerGroup.getDescription());
		diameterPeerGroupForm.setStateful(diameterPeerGroup.getStateful());
		diameterPeerGroupForm.setTransactionTimeout(diameterPeerGroup.getTransactionTimeout());
		diameterPeerGroupForm.setPeerList(diameterPeerGroup.getPeerList());
		diameterPeerGroupForm.setAuditUId(diameterPeerGroup.getAuditUId());
	}
}
