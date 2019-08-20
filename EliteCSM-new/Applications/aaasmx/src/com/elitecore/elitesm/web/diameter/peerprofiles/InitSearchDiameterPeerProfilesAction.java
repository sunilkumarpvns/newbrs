/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitSearchDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peerprofiles;  

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peerprofiles.forms.SearchDiameterPeerProfileForm;


public class InitSearchDiameterPeerProfilesAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETERPEERPROFILE";
	private static final String INITSEARCHDIAMETERPEERPROFILE = "searchDiameterPeerProfile"; 
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIAMETER_PEER_PROFILE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			SearchDiameterPeerProfileForm searchDiameterPeerProfileForm = (SearchDiameterPeerProfileForm)form; 	
			DiameterPeerProfileBLManager blManager = new DiameterPeerProfileBLManager();
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo=1;
			DiameterPeerProfileData diameterPeerProfileData = new DiameterPeerProfileData();
			String strProfileName = searchDiameterPeerProfileForm.getProfileName();
			
			if(strProfileName!=null)
				diameterPeerProfileData.setProfileName(strProfileName);
			else
				diameterPeerProfileData.setProfileName("");
		
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			Map infoMap= new HashedMap();
		    infoMap.put("pageNo", requiredPageNo);
		    infoMap.put("pageSize", pageSize);
		    
		    PageList pageList = blManager.searchDiameterPeerProfile(diameterPeerProfileData,infoMap,staffData);
		    
		    searchDiameterPeerProfileForm.setProfileName(strProfileName);
			searchDiameterPeerProfileForm.setPageNumber(pageList.getCurrentPage());
			searchDiameterPeerProfileForm.setTotalPages(pageList.getTotalPages());
			searchDiameterPeerProfileForm.setTotalRecords(pageList.getTotalItems());
			searchDiameterPeerProfileForm.setListDiameterPeerProfile(pageList.getListData());
			searchDiameterPeerProfileForm.setDiameterPeerProfileList(pageList.getCollectionData());
			searchDiameterPeerProfileForm.setActionType(BaseConstant.LISTACTION);
			request.setAttribute("searchDiameterPeerProfileForm", searchDiameterPeerProfileForm);
			return mapping.findForward(INITSEARCHDIAMETERPEERPROFILE);             
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("diameter.peerprofile.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
