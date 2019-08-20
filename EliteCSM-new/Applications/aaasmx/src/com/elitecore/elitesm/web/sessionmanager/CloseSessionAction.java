package com.elitecore.elitesm.web.sessionmanager;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.IASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerDBConfiguration;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.SearchASMForm;

public class CloseSessionAction extends BaseWebAction{


	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String LIST_FORWARD = "searchASM";
	private static final String ACTION_ALIAS_CLOSE = ConfigConstant.CLOSE_SESSION;
	private static final String ACTION_ALIAS_CLOSEALL = ConfigConstant.CLOSE_ALL_SESSION;
	private static final String ACTION_ALIAS_DOWNLOAD = ConfigConstant.DOWNLOAD_ACTIVE_SESSION;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());

		if(checkAccess(request, ACTION_ALIAS_CLOSE) || checkAccess(request, ACTION_ALIAS_CLOSEALL) || checkAccess(request, ACTION_ALIAS_DOWNLOAD)){
			try{

				SearchASMForm searchASMForm = (SearchASMForm)form;
				String strSmInstancId = request.getParameter("sminstanceid");
				Logger.logDebug(MODULE,"strSmInstancId: "+strSmInstancId);
				String sessionManagerId=null;
				if(strSmInstancId!=null){
					sessionManagerId = strSmInstancId;
				}else{
					sessionManagerId = searchASMForm.getSessionManagerId();
				}
				searchASMForm.setSessionManagerId(sessionManagerId);
				request.setAttribute("searchASMForm",searchASMForm);
				Logger.logDebug(MODULE,"sessionManagerId: "+sessionManagerId);
				SessionManagerBLManager asmBLManager = new SessionManagerBLManager();

				String strUserName = searchASMForm.getUserName();
				String strNasIPAddress = searchASMForm.getNasIpAddress();
				String strFramedIPAddress = searchASMForm.getFramedIpAddress();
				String strGroupName  = searchASMForm.getGroupName();
				String strIdleTime = searchASMForm.getIdleTime();
				String action  = searchASMForm.getAction(); 
				String strGroupByCriteria = searchASMForm.getGroupbyCriteria();

				SessionManagerDBConfiguration sessionManagementDBConfiguration = asmBLManager.getSessionManagerDBConfiguration(sessionManagerId);
				ISessionManagerInstanceData sessionManagerInstanceData = asmBLManager.getSessionManagerDataById(sessionManagerId);
				Logger.logDebug(MODULE,"sessionManagerInstanceData: "+sessionManagerInstanceData);
				request.setAttribute("sessionManagerInstanceData",sessionManagerInstanceData);

				if(action != null && action.equalsIgnoreCase("closeSelected")){
					checkActionPermission(request, ACTION_ALIAS_CLOSE);
					String[] selectedSessions = request.getParameterValues("selected");

					if(selectedSessions!=null){
						IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						String actionAlias = ACTION_ALIAS_CLOSE;

						if(selectedSessions.length==1){

							asmBLManager.closeSession(Long.parseLong(selectedSessions[0]),sessionManagementDBConfiguration,staffData);
						}else{
							asmBLManager.closeSession(selectedSessions,sessionManagementDBConfiguration,staffData);
						}
					}
					int requiredPageNo = Integer.parseInt(String.valueOf(searchASMForm.getPageNumber()));
					if(requiredPageNo == 0)
						requiredPageNo = 1;
					IASMData asmSearchData = new ASMData();
					String userName="";
					String nasIpAddress = "";
					String framedIpAddress = "";
					String groupName = "";
					String idleTime = "";


					if(strUserName !=null && !(strUserName.equalsIgnoreCase(""))){
						asmSearchData.setUserName(strUserName);
						userName = strUserName;
					}
					else{
						asmSearchData.setUserName("");

					}
					if(strNasIPAddress !=null && !(strNasIPAddress.equalsIgnoreCase(""))){
						asmSearchData.setNasIpAddress(strNasIPAddress);
						nasIpAddress = strNasIPAddress;
					}
					else{
						asmSearchData.setNasIpAddress("");

					}
					if(strFramedIPAddress !=null && !(strFramedIPAddress.equalsIgnoreCase(""))){
						asmSearchData.setFramedIpAddress(strFramedIPAddress);
						framedIpAddress = strFramedIPAddress;
					}
					else{
						asmSearchData.setFramedIpAddress("");

					}

					if(strGroupName !=null && !(strGroupName.equalsIgnoreCase(""))){
						asmSearchData.setGroupName(strGroupName);
						groupName = strGroupName;
					}
					else{
						asmSearchData.setGroupName("");

					}
					if(strIdleTime !=null && !(strIdleTime.equalsIgnoreCase(""))){
						asmSearchData.setIdleTime(strIdleTime);
						idleTime = strIdleTime;
					}
					else{
						asmSearchData.setIdleTime("");

					}
					if(strGroupByCriteria == null){
						strGroupByCriteria = "";
					}
					Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
					PageList pageList = asmBLManager.search(asmSearchData,requiredPageNo,pageSize,sessionManagementDBConfiguration);


					searchASMForm.setUserName(userName);

					if(pageList!=null){

						int strselectedSessionsLen = selectedSessions.length;
						long currentPageNumber = getCurrentPageNumberAfterDel(strselectedSessionsLen, searchASMForm.getPageNumber(), searchASMForm.getTotalPages(), searchASMForm.getTotalRecords());

						searchASMForm.setPageNumber(currentPageNumber);
						searchASMForm.setTotalPages(pageList.getTotalPages());
						searchASMForm.setTotalRecords(pageList.getTotalItems());								
						// this is List without Group BY
						searchASMForm.setAsmList(pageList.getListData());

					}


					request.setAttribute("responseUrl","/searchASM.do?sminstanceid="+sessionManagerId+"&userName="+userName+"&nasIpAddress="+nasIpAddress+"&groupName="+groupName+"&idleTime="+idleTime+"&groupbyCriteria="+strGroupByCriteria+"&action=search&pageNumber="+searchASMForm.getPageNumber());
					ActionMessage message = new ActionMessage("servermgr.server.session.close.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);

					searchASMForm.setGroupbyCriteria(strGroupByCriteria);
					System.out.println("In Close Selected Action");
					//return mapping.findForward(LIST_FORWARD);
					return mapping.findForward(SUCCESS_FORWARD);

				}

				else if(action != null && action.equalsIgnoreCase("download")){
					checkActionPermission(request, ACTION_ALIAS_DOWNLOAD);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
					ServletOutputStream out = null;
					PrintWriter writer = null;

					String username1 = searchASMForm.getUserName();
					String nasip1 = searchASMForm.getNasIpAddress();
					String framedIp = searchASMForm.getFramedIpAddress();
					String groupname1  = searchASMForm.getGroupName();
					String idleTime1 = searchASMForm.getIdleTime(); 
					//String groupbyCriteria1 = searchASMForm.getGroupbyCriteria();


					IASMData asmSearchData = new ASMData();



					if(username1 !=null && !(username1.equalsIgnoreCase(""))){
						asmSearchData.setUserName(username1);

					}
					else{
						asmSearchData.setUserName("");

					}
					if(nasip1 !=null && !(nasip1.equalsIgnoreCase(""))){
						asmSearchData.setNasIpAddress(nasip1);

					}
					else{
						asmSearchData.setNasIpAddress("");

					}
					if(framedIp !=null && !(framedIp.equalsIgnoreCase(""))){
						asmSearchData.setFramedIpAddress(framedIp);

					}
					else{
						asmSearchData.setFramedIpAddress("");

					}
					if(groupname1 !=null && !(groupname1.equalsIgnoreCase(""))){
						asmSearchData.setGroupName(groupname1);

					}
					else{
						asmSearchData.setGroupName("");

					}
					if(idleTime1 !=null && !(idleTime1.equalsIgnoreCase(""))){
						asmSearchData.setIdleTime(idleTime1);

					}
					else{
						asmSearchData.setIdleTime("");

					}

					PageList pageList = asmBLManager.search(asmSearchData,sessionManagementDBConfiguration);

					List asmRecords = (ArrayList)pageList.getListData();

					String csvStringHeader = "Concurrent-User-Id,User-Name,Group-Name,Last-Updated-Time,Acct-Session-Id,Session-Time,Framed-Ip-Address," +
					"Nas-Ip-Address,Nas-Port-Type,Param-Str0,Param-Str1,Param-Str2,Param-Str3," +
					"Param-Str4,Param-Str5,User-Identity";
					String csvString = "";

					for(Iterator iter = asmRecords.iterator(); iter.hasNext();){

						ASMData asmdata = (ASMData) iter.next();

						String lastUpdatedTimeString =EliteUtility.dateToString(asmdata.getLastUpdatedTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT));

						csvString += 		(String.valueOf(asmdata.getConcUserId())) + "," + (asmdata.getUserName()==null ? "" : asmdata.getUserName() ) + "," + (asmdata.getGroupName()==null ? "": asmdata.getGroupName())+","
						+ (asmdata.getLastUpdatedTime()==null ? "" : lastUpdatedTimeString) + "," +(asmdata.getAcctSessionId()==null ? "" : asmdata.getAcctSessionId()) +"," + (asmdata.getStartTime()==null ? "" : asmdata.getStartTime())+","
						+ (asmdata.getFramedIpAddress()==null ? "" : asmdata.getFramedIpAddress()) + "," + (asmdata.getNasIpAddress() == null ? "": asmdata.getNasIpAddress())+","
						+ (asmdata.getNasPortType()==null ? "" : asmdata.getNasPortType()) + "," + (asmdata.getUserIdentity()==null ? "" : asmdata.getUserIdentity()) + "\n";


					}
					response.setHeader("Content-Disposition", "attachment;filename=\"activeSessions.csv");
					response.setContentType("application/octet-stream");
					out = response.getOutputStream();
					writer = new PrintWriter(out);
					writer.println(csvStringHeader);
					writer.println(csvString);
					/* Closeing all the streams */
					writer.close();
					out.close();
					doAuditing(staffData, ACTION_ALIAS_DOWNLOAD);
					return null;
				}
				else{
					checkActionPermission(request, ACTION_ALIAS_CLOSEALL);
					String reqUserName = request.getParameter("strUserName");
					searchASMForm.setUserName(reqUserName);
					String reqNasIpAddress = request.getParameter("nasIp");
					searchASMForm.setNasIpAddress(reqNasIpAddress);
					String reqFramedIpAddress = request.getParameter("framedIp");
					searchASMForm.setFramedIpAddress(reqFramedIpAddress);
					String reqGroupName  = request.getParameter("groupName");
					searchASMForm.setGroupName(reqGroupName);
					String reqIdleTime = request.getParameter("idleTime");
					searchASMForm.setIdleTime(reqIdleTime);
					String groupByCriteria = request.getParameter("groupByCriteria");
					searchASMForm.setGroupbyCriteria(groupByCriteria);

					IASMData asmSearchData = new ASMData();


					String userName="";
					String nasIpAddress = "";
					String framedIpAddress = "";
					String groupName = "";
					String idleTime = "";

					if(reqUserName !=null && !(reqUserName.equalsIgnoreCase(""))){
						asmSearchData.setUserName(reqUserName);
						userName = reqUserName;
					}
					else{
						asmSearchData.setUserName("");
						userName = "";
					}
					if(reqNasIpAddress !=null && !(reqNasIpAddress.equalsIgnoreCase(""))){
						asmSearchData.setNasIpAddress(reqNasIpAddress);
						nasIpAddress = reqNasIpAddress;
					}
					else{
						asmSearchData.setNasIpAddress("");
						nasIpAddress = "";
					}
					if(reqFramedIpAddress !=null && !(reqFramedIpAddress.equalsIgnoreCase(""))){
						asmSearchData.setFramedIpAddress(reqFramedIpAddress);
						framedIpAddress = reqFramedIpAddress;
					}
					else{
						asmSearchData.setFramedIpAddress("");
						framedIpAddress = "";
					}
					if(reqGroupName !=null && !(reqGroupName.equalsIgnoreCase(""))){
						asmSearchData.setGroupName(reqGroupName);
						groupName = reqGroupName;
					}
					else{
						asmSearchData.setGroupName("");
						groupName = "";
					}
					if(reqIdleTime !=null && !(reqIdleTime.equalsIgnoreCase(""))){
						asmSearchData.setIdleTime(reqIdleTime);
						idleTime = reqIdleTime;
					}
					else{
						asmSearchData.setIdleTime("");
						idleTime = "";
					}
					if(groupByCriteria==null){
						groupByCriteria = "";
					}


					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

					asmBLManager.closeAllSession(asmSearchData,sessionManagementDBConfiguration,staffData);

					request.setAttribute("responseUrl","/searchASM.do?sminstanceid="+sessionManagerId+"&userName="+userName+"&nasIpAddress="+nasIpAddress+"&groupName="+groupName+"&idleTime="+idleTime+"&groupbyCriteria="+groupByCriteria+"&action=search");
					ActionMessage message = new ActionMessage("servermgr.server.session.delete.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);

				}
			}catch(ActionNotPermitedException exception){
				Logger.logError(MODULE,"Error :-" + exception.getMessage());
				printPermitedActionAlias(request);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}catch(DataManagerException dnisExp){
				Logger.logError(MODULE, "Error during data Manager operation,reason : " + dnisExp.getMessage());
				Logger.logTrace(MODULE,dnisExp);
				request.setAttribute("responseUrl", "/searchASM.do");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dnisExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("asm.closeSession.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			catch (Exception managerExp) {
				Logger.logError(MODULE, "Error during data Manager operation,reason : " + managerExp.getMessage());
				Logger.logTrace(MODULE,managerExp);
				request.setAttribute("responseUrl", "/searchASM.do");
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);	
				ActionMessage message = new ActionMessage("asm.closeSession.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE_FORWARD);

		}
		else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}	

	}


}
