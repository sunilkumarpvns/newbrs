/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitSearchDiameterpolicyAction.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.peer;  

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peer.forms.SearchDiameterPeerForm;



public class InitSearchDiameterPeerAction extends BaseWebAction { 

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-PEER";
	private static final String INITSEARCHDIAMETERPEER = "searchDiameterPeer"; 
	private static final String SEARCHALLDIAMETERPEER = "searchAllDiameterPeer";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_DIAMETER_PEER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			SearchDiameterPeerForm searchDiameterPeerForm = (SearchDiameterPeerForm)form; 
			DiameterPeerBLManager blManager = new DiameterPeerBLManager();
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo=1;
			DiameterPeerData diameterPeerData = new DiameterPeerData();
			String strHostIdentity = searchDiameterPeerForm.getHostIdentity();
			if(strHostIdentity!=null)
				diameterPeerData.setHostIdentity(strHostIdentity);
			else
				diameterPeerData.setHostIdentity("");
			
			String peerProfileId = searchDiameterPeerForm.getPeerProfileId();
			if(Strings.isNullOrBlank(peerProfileId) == false)
				diameterPeerData.setPeerProfileId(peerProfileId);
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			Map infoMap= new HashedMap();
		    infoMap.put("pageNo", requiredPageNo);
		    infoMap.put("pageSize", pageSize);
		    
			PageList pageList = blManager.search(diameterPeerData, infoMap, staffData);
		    
			searchDiameterPeerForm.setHostIdentity(strHostIdentity);
			searchDiameterPeerForm.setPageNumber(pageList.getCurrentPage());
			searchDiameterPeerForm.setTotalPages(pageList.getTotalPages());
			searchDiameterPeerForm.setTotalRecords(pageList.getTotalItems());
			searchDiameterPeerForm.setListDiameterPeer(pageList.getListData());
			searchDiameterPeerForm.setDiameterPeerList(pageList.getCollectionData());
			searchDiameterPeerForm.setActionType(BaseConstant.LISTACTION);
			searchDiameterPeerForm.setPeerProfileList(blManager.getPeerProfileList());
			request.setAttribute("searchDiameterPeerForm", searchDiameterPeerForm);
			return mapping.findForward(INITSEARCHDIAMETERPEER);             
		}catch (Exception exp) {                                                                                                             
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("diameter.peer.search.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		
		return mapping.findForward(FAILURE_FORWARD); 
	}
}
