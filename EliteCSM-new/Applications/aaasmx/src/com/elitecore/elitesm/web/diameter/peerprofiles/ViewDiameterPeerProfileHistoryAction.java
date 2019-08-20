package com.elitecore.elitesm.web.diameter.peerprofiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.blmanager.diameter.diameterpeerprofile.DiameterPeerProfileBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.peerprofiles.forms.DiameterPeerProfileForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewDiameterPeerProfileHistoryAction extends BaseWebAction {

	private static final String VIEW_HISTORY_FORWARD = "viewDiameterPeerProfileHistory";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DIAMETER_PEER_PROFILE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			try {
					checkActionPermission(request, ACTION_ALIAS);
					DiameterPeerProfileForm diameterPeerProfileForm = (DiameterPeerProfileForm)form;
					Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
					DiameterPeerProfileBLManager blManager = new DiameterPeerProfileBLManager();
					String peerProfileId="";
					String strPeerProileId = request.getParameter("peerProfileId");
					if(Strings.isNullOrBlank(strPeerProileId) == false)
					{
						peerProfileId = strPeerProileId;
					}	
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

					DiameterPeerProfileData diameterPeerProfileData = blManager.getDiameterPeerProfileById(peerProfileId);

					List<ServerCertificateData> serverCertificateList = blManager.getListOfServerCertificate();
		 			
		 			if(serverCertificateList != null){
						for(int i=0;i<serverCertificateList.size();i++){
							ServerCertificateData tempServerCertificateData = (ServerCertificateData) serverCertificateList.get(i);
							if(tempServerCertificateData.getServerCertificateId().equals(diameterPeerProfileData.getServerCertificateId())){
									diameterPeerProfileData.setServerCertificateName(tempServerCertificateData.getServerCertificateName());
							}
						}
					}
					if(diameterPeerProfileData.getCiphersuiteList() != null){
						List<String> cipherSuiteList = convertcipherSuiteListToLabel(diameterPeerProfileData.getCiphersuiteList());
						request.setAttribute("cipherSuiteList",cipherSuiteList);
					}
					
					HistoryBLManager historyBlManager= new HistoryBLManager();
					
					String strAuditUid = request.getParameter("auditUid");
					String strSytemAuditId=request.getParameter("systemAuditId");
					String name=request.getParameter("name");
					
					if(strSytemAuditId != null){
						request.setAttribute("systemAuditId", strSytemAuditId);
					}
					
					if(peerProfileId != null && Strings.isNullOrBlank(strAuditUid) == false){
						String actionAlias = ACTION_ALIAS;
						
						staffData.setAuditName(diameterPeerProfileData.getProfileName());
						staffData.setAuditId(diameterPeerProfileData.getAuditUId());
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
						
						request.setAttribute("name", name);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
					}
					
					request.setAttribute("diameterPeerProfileData",diameterPeerProfileData);
					
			}catch(ActionNotPermitedException e){
				Logger.logError(MODULE, "Restricted to do action.");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);	
			}catch (DataManagerException managerExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");                                                         
				ActionMessages messages = new ActionMessages();                                                                                 
				messages.add("information", message);                                                                                           
				saveErrors(request, messages);                                         
				return mapping.findForward(FAILURE_FORWARD);
			}
			return mapping.findForward(VIEW_HISTORY_FORWARD);
	}
	protected List<String> convertcipherSuiteListToLabel(String ciphersuiteList) {
		String[] cipherSuiteValueArray = ciphersuiteList.split(",");
		List<String> cipherSuiteLabelArray = new ArrayList<String>();
		Collection<CipherSuites> cipherSuites = Arrays.asList(CipherSuites.values());
		
		for(int i=0;i<cipherSuiteValueArray.length;i++){
				for(CipherSuites cipherSuites2:cipherSuites){
					CipherSuites strCipherSuitesName=CipherSuites.fromCipherCode(cipherSuites2.code);
					String strCode=String.valueOf(cipherSuites2.code);
					if(strCode.equals(cipherSuiteValueArray[i])){
						cipherSuiteLabelArray.add(strCipherSuitesName.name());
					}
				}
			}
		return cipherSuiteLabelArray;
	}
}
