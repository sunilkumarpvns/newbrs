/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   MiscDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peerprofiles; 

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
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peerprofiles.forms.SearchDiameterPeerProfileForm;

public class MiscDiameterPeerProfileAction extends BaseWebAction { 

	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER-PROFILE";
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_DIAMETER_PEER_PROFILE; 
	private static final String SHOW_ALL_ACTION=ConfigConstant.SHOW_ALL_DIAMETER_PEER_PROFILE;
	private static final String SEARCHALLDIAMETERPEERPROFILE = "searchAllDiameterPeerProfile";
	private static final String LIST_FORWARD = "searchDiameterPeerProfileList";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		try{
			SearchDiameterPeerProfileForm searchDiameterPeerProfileForm = (SearchDiameterPeerProfileForm)form;
			DiameterPeerProfileBLManager diameterPeerProfileBLManager = new DiameterPeerProfileBLManager();
			GenericBLManager genericBLManager=null;
			PageList pageList=null;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			Logger.logTrace(MODULE,"ActionType===== "+searchDiameterPeerProfileForm.getActionType());
			if(searchDiameterPeerProfileForm.getActionType() != null) {
				 if(searchDiameterPeerProfileForm.getActionType().equalsIgnoreCase("delete")){
					  String[] strSelectedIds = request.getParameterValues("select");
					checkActionPermission(request, ACTION_ALIAS_DELETE);
					
					diameterPeerProfileBLManager.deleteDiameterPeerProfileById(Arrays.asList(strSelectedIds),staffData);
					
					int strSelectedIdsLen = strSelectedIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchDiameterPeerProfileForm.getPageNumber(),searchDiameterPeerProfileForm.getTotalPages(),searchDiameterPeerProfileForm.getTotalRecords());
					request.setAttribute("responseUrl","/searchDiameterPeerProfiles.do?profileName="+searchDiameterPeerProfileForm.getProfileName()+"&pageNumber="+currentPageNumber+"&totalPages="+searchDiameterPeerProfileForm.getTotalPages()+"&totalRecords="+searchDiameterPeerProfileForm.getTotalRecords());
					ActionMessage message = new ActionMessage("diameter.peerprofile.delete");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
				}
				else if(searchDiameterPeerProfileForm.getActionType().equalsIgnoreCase("showall"))
				{
					genericBLManager = new GenericBLManager();
					pageList = genericBLManager.getAllRecords(DiameterPeerProfileData.class,"profileName",true);
					searchDiameterPeerProfileForm.setPageNumber(pageList.getCurrentPage());
					searchDiameterPeerProfileForm.setTotalPages(pageList.getTotalPages());
					searchDiameterPeerProfileForm.setTotalRecords(pageList.getTotalItems());
					searchDiameterPeerProfileForm.setListDiameterPeerProfile(pageList.getListData());
					request.setAttribute("searchDiameterPeerProfileForm", searchDiameterPeerProfileForm);
					doAuditing(staffData, SHOW_ALL_ACTION);
					return mapping.findForward(SEARCHALLDIAMETERPEERPROFILE); 
				}	
			}
			
			request.setAttribute("searchDiameterPeerProfileForm",searchDiameterPeerProfileForm);
			return mapping.findForward(LIST_FORWARD);
		}catch (ConstraintViolationException e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("diameter.peerprofile.alreadyinuse");
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
