/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   MiscDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peer; 

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
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peer.forms.SearchDiameterPeerForm;

public class MiscDiameterPeerAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER";
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_DIAMETER_PEER; 
	private static final String SEARCHALLDIAMETERPEER = "searchAllDiameterPeer";
	private static final String ACTION_ALIAS_SHOWALL=ConfigConstant.SHOW_ALL_DIAMETER_PEER;
	private static final String LIST_FORWARD = "searchDiameterPeerList";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		try{
			SearchDiameterPeerForm searchDiameterPeerForm = (SearchDiameterPeerForm)form;
			DiameterPeerBLManager DiameterPeerBLManager = new DiameterPeerBLManager();
			GenericBLManager genericBLManager=null;
			PageList pageList=null;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(searchDiameterPeerForm.getActionType() != null) {
				 if(searchDiameterPeerForm.getActionType().equalsIgnoreCase("delete")){
					  String[] strSelectedIds = request.getParameterValues("select");
					checkActionPermission(request, ACTION_ALIAS_DELETE);
					DiameterPeerBLManager.deleteDiameterPeerById(Arrays.asList(strSelectedIds), staffData);
					int strSelectedIdsLen = strSelectedIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchDiameterPeerForm.getPageNumber(),searchDiameterPeerForm.getTotalPages(),searchDiameterPeerForm.getTotalRecords());
					request.setAttribute("responseUrl","/searchDiameterPeer.do?hostIdentity="+searchDiameterPeerForm.getHostIdentity()+"&peerProfileId="+searchDiameterPeerForm.getPeerProfileId()+"&pageNumber="+currentPageNumber+"&totalPages="+searchDiameterPeerForm.getTotalPages()+"&totalRecords="+searchDiameterPeerForm.getTotalRecords());
					ActionMessage message = new ActionMessage("diameter.peer.delete");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}
				else if(searchDiameterPeerForm.getActionType().equalsIgnoreCase("showall"))
				{
						genericBLManager = new GenericBLManager();
						pageList = genericBLManager.getAllRecords(DiameterPeerData.class,"hostIdentity",true);
						searchDiameterPeerForm.setPageNumber(pageList.getCurrentPage());
						searchDiameterPeerForm.setTotalPages(pageList.getTotalPages());
						searchDiameterPeerForm.setTotalRecords(pageList.getTotalItems());
						searchDiameterPeerForm.setListDiameterPeer(pageList.getListData());
						request.setAttribute("searchDiameterPeerForm", searchDiameterPeerForm);
						doAuditing(staffData, ACTION_ALIAS_SHOWALL);
						return mapping.findForward(SEARCHALLDIAMETERPEER); 
				}	
			}
			
			request.setAttribute("searchDiameterPeerForm",searchDiameterPeerForm);
			return mapping.findForward(LIST_FORWARD);
		}catch (ConstraintViolationException e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("diameter.peer.alreadyinuse");
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);	
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD); 
		}catch(Exception managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
		}

		return mapping.findForward(FAILURE_FORWARD);
	}
}
