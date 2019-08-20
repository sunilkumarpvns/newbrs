package com.elitecore.elitesm.web.servermgr.eap;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.eap.EAPConfigBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.eap.forms.SearchEAPConfigForm;

public class SearchEAPConfigAction extends BaseWebAction{

	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_EAP_CONFIGURATION;
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SEARCH EAP CONFIG ACTION";
	private static final String LIST_FORWARD = "searchEapConfigList";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			try{


				EAPConfigBLManager eapConfigBLManager = new EAPConfigBLManager();
				SearchEAPConfigForm searchEAPConfigForm = (SearchEAPConfigForm)form;

				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(searchEAPConfigForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo = 1;


				EAPConfigData eapConfigData = new EAPConfigData();

				String strEAPName =request.getParameter("name"); 
				if(strEAPName != null){
					eapConfigData.setName(strEAPName);
				}else if(searchEAPConfigForm.getName() != null){
					eapConfigData.setName(searchEAPConfigForm.getName());
				}else{
					eapConfigData.setName("");
				}
				strEAPName = eapConfigData.getName();


				/*
				 * Enabled Auth Methods
				 */

				String[] enableAuthMethodarry=new String[9];

				String strTls = request.getParameter("tls");

				if(strTls != null && strTls.length()>0){
					//int tls=Integer.parseInt(strTls);
					enableAuthMethodarry[0]=strTls;
				}else{
					enableAuthMethodarry[0]=searchEAPConfigForm.getTls();
				}


				//ttls

				String strTtls = request.getParameter("ttls");

				if(strTtls != null && strTtls.length()>0){
					//int ttls=Integer.parseInt(strTtls);
					enableAuthMethodarry[1]=strTtls;
				}else{
					enableAuthMethodarry[1]=searchEAPConfigForm.getTtls();
				}



				//aka

				String strAka = request.getParameter("aka");

				if(strAka != null && strAka.length()>0){
					//int aka=Integer.parseInt(strAka);
					enableAuthMethodarry[2]=strAka;
				}else{
					enableAuthMethodarry[2]=searchEAPConfigForm.getAka();
				}
				
				
				
				

				//sim
				String strSim = request.getParameter("sim");

				if(strSim != null && strSim.length()>0){
					//int sim=Integer.parseInt(strSim);
					enableAuthMethodarry[3]=strSim;
				}else{
					enableAuthMethodarry[3]=searchEAPConfigForm.getSim();
				}
				//mschapv2
				String strMschapv2 = request.getParameter("mschapv2");

				if(strMschapv2 != null && strMschapv2.length()>0){
					//	int mschapv2=Integer.parseInt(strMschapv2);
					enableAuthMethodarry[4]=strMschapv2;
				}else{
					enableAuthMethodarry[4]=searchEAPConfigForm.getMschapv2();
				}
				//md5
				String strMd5 = request.getParameter("md5");

				if(strMd5 != null && strMd5.length()>0){
					//int md5=Integer.parseInt(strMd5);
					enableAuthMethodarry[5]=strMd5;
				}else{
					enableAuthMethodarry[5]=searchEAPConfigForm.getMd5();
				}
				//gtc
				String strGtc = request.getParameter("gtc");

				if(strGtc != null && strGtc.length()>0){
					//int gtc=Integer.parseInt(strGtc);
					enableAuthMethodarry[6]=strGtc;
				}else{
					enableAuthMethodarry[6]=searchEAPConfigForm.getGtc();
				}
				//peap
				String strPeap = request.getParameter("peap");

				if(strPeap != null && strPeap.length()>0){
				
					enableAuthMethodarry[7]=strPeap;
				}else{
					enableAuthMethodarry[7]=searchEAPConfigForm.getPeap();
				}
				
				//aka'
				String strAkaPrime = request.getParameter("akaPrime");

				if(strAkaPrime != null && strAkaPrime.length()>0){
					enableAuthMethodarry[8]=strAkaPrime;
				}else{
					enableAuthMethodarry[8]=searchEAPConfigForm.getAkaPrime();
				}



				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;


				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				Logger.logDebug(MODULE, "PAGENO IS:"+requiredPageNo+"");



				PageList pageList = eapConfigBLManager.search(eapConfigData,enableAuthMethodarry,requiredPageNo,pageSize);
				doAuditing(staffData, actionAlias);
				List<EAPConfigData> eapConfigList=pageList.getListData();


				searchEAPConfigForm.setName(eapConfigData.getName());
				searchEAPConfigForm.setTls(enableAuthMethodarry[0]);
				searchEAPConfigForm.setTtls(enableAuthMethodarry[1]);
				searchEAPConfigForm.setAka(enableAuthMethodarry[2]);
				searchEAPConfigForm.setSim(enableAuthMethodarry[3]);
				searchEAPConfigForm.setMschapv2(enableAuthMethodarry[4]);
				searchEAPConfigForm.setMd5(enableAuthMethodarry[5]);
				searchEAPConfigForm.setGtc(enableAuthMethodarry[6]);
				searchEAPConfigForm.setPeap(enableAuthMethodarry[7]);
				searchEAPConfigForm.setAkaPrime(enableAuthMethodarry[8]);
				searchEAPConfigForm.setPageNumber(pageList.getCurrentPage());
				searchEAPConfigForm.setTotalPages(pageList.getTotalPages());
				searchEAPConfigForm.setTotalRecords(pageList.getTotalItems());
				searchEAPConfigForm.setEapConfigList(eapConfigList);
				searchEAPConfigForm.setAction(BaseConstant.LISTACTION);

				return mapping.findForward(LIST_FORWARD);



			}catch(Exception e){
				Logger.logError(MODULE, "Error List Display operation , reason : " + e.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);

			}

			Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
			ActionMessage message = new ActionMessage("servermgr.eap.search.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD); 
		}else{

			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}



}