package com.elitecore.elitesm.web.sessionmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteDynAuthWS;
import com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteDynAuthWSService;
import com.elitecore.aaa.radius.service.ws.eliteRadiusDynAuthWS.EliteDynAuthWSServiceLocator;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.IASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerDBConfiguration;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.util.url.UrlUtils;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.SearchASMForm;
import com.elitecore.elitesm.ws.WebServiceUtils;



public class PurgeClosedSessionAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";

	private static final String LIST_FORWARD = "searchASM";
	private static final String ACTION_ALIAS = ConfigConstant.PURGE_CLOSED_SESSION;
	private static String dmURL = null;
	private static boolean isPurgePostProcessRequired = false;
	private static String dynAuthWebServiceURL = null;
	private static final String MODULE="PURGE CLOSED SESSION ACTION";
	
	private static boolean isWebServiceCallEnabled = false;
	private static String dynAuthWebServiceParams=null;
	private static Map<String,String> attrIDColumnMap = null;
	
	
	static{
		init();
	}
	public static void init(){
		try{
			setMiscConfig();
		}catch(MalformedURLException e){
			Logger.logError(MODULE, "Error in getting URL query components :"+e.getMessage());
		}catch(IOException e){
			Logger.logError(MODULE, "Error in getting URL from property file :"+e.getMessage());
		}
	}
	public static void setMiscConfig() throws IOException{
        
		FileInputStream fileInputStream = null;
		
		String miscPropsFileLocation = ConfigConstant.MISC_CONFIG_FILE_LOCATION;
		
		Properties properties = new Properties();
		File misConfigPropsFile = new File(EliteUtility.getSMHome()+File.separator+miscPropsFileLocation);
        fileInputStream = new FileInputStream(misConfigPropsFile);
        properties.load(fileInputStream);
		
		dmURL = (String)properties.get("dm.url");
		
			
		Logger.logDebug(MODULE, "DM url : "+dmURL);
		String strPurgePostProcessRequired= (String)properties.get("dm.enabled");
		if(strPurgePostProcessRequired!=null && strPurgePostProcessRequired.trim().equalsIgnoreCase("true")){
			 
			isPurgePostProcessRequired = true;
		}
		Logger.logDebug(MODULE, "Purge post process requied : "+isPurgePostProcessRequired);
		
		dynAuthWebServiceURL = (String)properties.get("dynauth.webservice.url");
		String strWebServiceCallEnabled= (String)properties.get("dynauth.webservice.disconnect.call.enabled");
		if(strWebServiceCallEnabled!=null && strWebServiceCallEnabled.trim().equalsIgnoreCase("true")){
			 
			isWebServiceCallEnabled = true;
		}
		
		dynAuthWebServiceParams = (String)properties.get("dynauth.webservice.disconnect.dbfieldmapping");
		
		attrIDColumnMap = WebServiceUtils.getAttrIDColumnMap(dynAuthWebServiceParams);
		Logger.logDebug(MODULE, "Dynauth WebService Call Enabled = "+isWebServiceCallEnabled);
	}

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		String sessionManagerId=null;
		if(checkAccess(request, ACTION_ALIAS)){		
			try{

				SearchASMForm searchASMForm = (SearchASMForm)form;
				SessionManagerBLManager asmBLManager = new SessionManagerBLManager();
				
				String strSmInstancId = request.getParameter("sminstanceid");
				Logger.logDebug(MODULE,"strSmInstancId: "+strSmInstancId);
				
				if(strSmInstancId!=null){
					sessionManagerId = strSmInstancId;
				}else{
					sessionManagerId = searchASMForm.getSessionManagerId();
				}
				searchASMForm.setSessionManagerId(sessionManagerId);
				
				SessionManagerDBConfiguration sessionManagementDBConfiguration = asmBLManager.getSessionManagerDBConfiguration(sessionManagerId);

				String strUserName = request.getParameter("strUserName");
				searchASMForm.setUserName(strUserName);
				String nasIp = request.getParameter("nasIp");
				searchASMForm.setNasIpAddress(nasIp);
				String framedIp = request.getParameter("framedIp");
				searchASMForm.setFramedIpAddress(framedIp);
				String groupName  = request.getParameter("groupName");
				searchASMForm.setGroupName(groupName);
				String idletime = request.getParameter("idleTime");
				searchASMForm.setIdleTime(idletime);
				String action = request.getParameter("action");
				searchASMForm.setAction(action);
				String groupByCriteria = request.getParameter("groupByCriteria");
				searchASMForm.setGroupbyCriteria(groupByCriteria);

				int requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNumber")));
				if(requiredPageNo == 0)
					requiredPageNo = 1;

				IASMData asmSearchData = new ASMData();

				if(strUserName !=null && !(strUserName.equalsIgnoreCase("")))
					asmSearchData.setUserName(strUserName);
				else
					asmSearchData.setUserName("");

				if(nasIp !=null && !(nasIp.equalsIgnoreCase("")))
					asmSearchData.setNasIpAddress(nasIp);
				else
					asmSearchData.setNasIpAddress("");
				
				if(framedIp !=null && !(framedIp.equalsIgnoreCase("")))
					asmSearchData.setFramedIpAddress(framedIp);
				else
					asmSearchData.setFramedIpAddress("");
				
				if(groupName !=null && !(groupName.equalsIgnoreCase("")))
					asmSearchData.setGroupName(groupName);
				else
					asmSearchData.setGroupName("");
				if(idletime !=null && !(idletime.equalsIgnoreCase("")))
					asmSearchData.setIdleTime(idletime);
				else
					asmSearchData.setIdleTime("");

				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;

				List<Map<String,Object>> purgedSessionsList = asmBLManager.purgeClosedSession(sessionManagementDBConfiguration,staffData);
				if(isPurgePostProcessRequired){
					if(dmURL!=null){
						int size = purgedSessionsList.size();
						String query =UrlUtils.getUrlQuery(dmURL);
						List<String> columnList = UrlUtils.parseFindColumns(query);
						for(int index=0;index<size;index++){
							Map<String,Object> columnValueMap =purgedSessionsList.get(index);


							Iterator<String> iterator = columnList.iterator();
							String url = dmURL; 

							while(iterator.hasNext()){
								String column = iterator.next();
								Object valueObject = columnValueMap.get(column.toLowerCase());
								String value = "";
								if(valueObject!=null){
									value = valueObject.toString();
								}
								url=UrlUtils.replaceQueryColumnsWithValue(url, "$"+column, value);
							}
							System.out.println("URL ["+index+"] :"+url);
							UrlUtils.connectUrl(url,true);
						}
					}else{
						Logger.logError(MODULE, "DM URL is not configured in misc-config.properties "+dmURL);
					}
				}
				if(isWebServiceCallEnabled){
					if(dynAuthWebServiceURL!=null){
						int size = purgedSessionsList.size();
						EliteDynAuthWSService service = new EliteDynAuthWSServiceLocator(dynAuthWebServiceURL);
						EliteDynAuthWS  presence = service.geteliteRadiusDynAuthWS();
						for(int index=0;index<size;index++){
							Map<String,Object> columnValueMap =purgedSessionsList.get(index);
							Iterator<String> iterator = attrIDColumnMap.keySet().iterator();
							HashMap<String, String> attributeMap = new HashMap<String,String>();
							while(iterator.hasNext()){
								String attributeId = iterator.next();
								String column = attrIDColumnMap.get(attributeId);
								Object valueObject = columnValueMap.get(column);
								String value = "";
								if(valueObject!=null){
									value = valueObject.toString();
								}
								attributeMap.put(attributeId,value);
							}
							String userName = ""; 
							if(attributeMap.get("0:1")!=null){
								userName =attributeMap.get("0:1").trim();
								attributeMap.remove("0:1");
							}else if(attributeMap.get("1")!=null){
								userName =attributeMap.get("1").trim();
								attributeMap.remove("1");
							}else if(attributeMap.get("user-name")!=null){
								userName =attributeMap.get("user-name").trim();
								attributeMap.remove("user-name");
							}
							Logger.logDebug(MODULE, " Attribute map : "+attributeMap);
							if(!userName.trim().equals("")){
								int responsePacketType = presence.requestDisconnect(userName,attributeMap);
								String msg = "Response Back - Response Packet Type("+responsePacketType+")";
								if(responsePacketType==41){
									msg= msg+" Disconnect-ACK ";
								}else if(responsePacketType==42){
									msg= msg+" Disconnect-NAK ";
								}else{
									msg= msg+" Other";
								}
								Logger.logInfo(MODULE,msg);
							}else{
								Logger.logError(MODULE, "User name is not found.");
							}
						}
					}else{
						Logger.logError(MODULE, "dynauth.webservice.url is not configured in misc-config.properties.");
					}
				}
				PageList pageList = asmBLManager.search(asmSearchData,requiredPageNo,10,sessionManagementDBConfiguration);
				searchASMForm.setUserName(strUserName);

				if(pageList!=null){
					searchASMForm.setPageNumber(pageList.getCurrentPage());
					searchASMForm.setTotalPages(pageList.getTotalPages());
					searchASMForm.setTotalRecords(pageList.getTotalItems());								
					// this is List without Group BY
					searchASMForm.setAsmList(pageList.getListData());
					searchASMForm.setGroupbyCriteria("");

				}
				request.setAttribute("responseUrl","/initSearchASM.do?sminstanceid="+sessionManagerId);
				ActionMessage message = new ActionMessage("servermgr.asm.purge.closed.session.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);

			}
			catch(DataManagerException dnisExp){
				Logger.logError(MODULE, "Error during data Manager operation,reason : " + dnisExp.getMessage());
				Logger.logTrace(MODULE,dnisExp);
				request.setAttribute("responseUrl", "/searchASM.do?sminstanceid="+sessionManagerId);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dnisExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("asm.purge.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
//			catch(ServiceException serviceException){
//				Logger.logError(MODULE, "Error during calling a dynauth webservice ,reason : " + serviceException.getMessage());
//				Logger.logTrace(MODULE,serviceException);
//				request.setAttribute("responseUrl", "/searchASM.do");
//				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(serviceException);
//				request.setAttribute("errorDetails", errorElements);
//				ActionMessage message = new ActionMessage("asm.purge.webservice.failure");
//				ActionMessages messages = new ActionMessages();
//				messages.add("information", message);
//				saveErrors(request, messages);
//			}
			catch(RemoteException remoteException){
				Logger.logError(MODULE, "Error during calling a dynauth webservice ,reason : " + remoteException.getMessage());
				Logger.logTrace(MODULE,remoteException);
				request.setAttribute("responseUrl", "/searchASM.do?sminstanceid="+sessionManagerId);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(remoteException);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("asm.purge.webservice.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}catch (Exception managerExp) {
				Logger.logError(MODULE, "Error during purging closed session,reason : " + managerExp.getMessage());
				Logger.logTrace(MODULE,managerExp);
				request.setAttribute("responseUrl", "/searchASM.do?sminstanceid="+sessionManagerId);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("asm.purge.failure");
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
