package com.elitecore.netvertexsm.web.sendpacket.radius;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.util.remotecommunications.RemoteMethodInvocator;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.sendpacket.form.SendRadiusPacketForm;

public class SearchRadiusSendPacketAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String LIST_FORWARD = "searchRadiusSendPacket";
	private static final String MODULE = "SEARCH_SEND_PACKET_ACTION";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if(LogManager.getLogger().isInfoLogLevel()){
			Logger.logInfo(MODULE,"Enter execute method of " + getClass().getName());
		}
		String netServerId = null;
		try{	
			netServerId = request.getParameter("netserverid");
			NetServerBLManager blManager = new NetServerBLManager();
			final INetServerInstanceData netServerInstance = blManager.getNetServerInstance(Long.parseLong(netServerId));
			List<NetServerInstanceData> netServerTypeList = blManager.getNetServerInstanceList();
			request.setAttribute("netServerInstanceData",netServerInstance);
			request.setAttribute("netServerTypeList",netServerTypeList);
			request.getSession().setAttribute("netserverid", netServerId);

			SendRadiusPacketForm sendRadiusPacketForm = (SendRadiusPacketForm) form;
			final String[] strSelectedIds = request.getParameterValues("select");
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(sendRadiusPacketForm.getPageNumber()).intValue();
			}
			if (requiredPageNo == 0)
				requiredPageNo = 1;		

			final String strName = request.getParameter("name");

			if(sendRadiusPacketForm.getAction() != null){
				if(sendRadiusPacketForm.getAction().equals("delete")){
					ExecutorService service = Executors.newFixedThreadPool(1);
					String param = "";
					String concate = "";
					for(String str : strSelectedIds){
						concate = concate + str +",";
						param = concate;
					}
					final String strFileNames = param;
					Future<List<String>> futureResult = service.submit(new Callable<List<String>>() {
						@Override
						public List<String> call() throws Exception {
							Object[] objectParams = {ConfigConstant.RADIUS,strFileNames};
							String[] objectType = {"java.lang.String","java.lang.String"};
							List<String> deletedFiles = (List<String>) RemoteMethodInvocator.invokeRemoteMethod(netServerInstance, MBeanConstants.PACKET_UTILITY, "delete", objectParams, objectType);
							return deletedFiles;
						}

					});

					List<String> deletedFiles = futureResult.get(10, TimeUnit.SECONDS);
					int strSelectedIdsLen = strSelectedIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,sendRadiusPacketForm.getPageNumber(),sendRadiusPacketForm.getTotalPages(),sendRadiusPacketForm.getTotalRecords());
					if(Collectionz.isNullOrEmpty(deletedFiles) == false){
						ActionMessage message = new ActionMessage("sendpacket.radius.delete.success",deletedFiles.size());
						ActionMessages messages1 = new ActionMessages();
						messages1.add("information",message);
						saveMessages(request,messages1);
					}
					sendRadiusPacketForm.setAction(BaseConstant.LISTACTION);
					request.setAttribute("responseUrl","/searchRadiusSendPacket.do?name="+sendRadiusPacketForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+sendRadiusPacketForm.getTotalPages()+"&totalRecords="+sendRadiusPacketForm.getTotalRecords()+"&netserverid="+request.getSession().getAttribute("netserverid"));
					return mapping.findForward(SUCCESS_FORWARD);
				}
			}


			ExecutorService searchService = Executors.newFixedThreadPool(1);


			Future<List<File>> futureResult = searchService.submit(new Callable<List<File>>() {
				List<File> lstFiles;
				@Override
				public List<File> call() throws Exception {
					String[] objectParams = {ConfigConstant.RADIUS,strName};
					String[] objectType = {"java.lang.String","java.lang.String"};
					lstFiles =  (List<File>) RemoteMethodInvocator.invokeRemoteMethod(netServerInstance, MBeanConstants.PACKET_UTILITY, "search", objectParams, objectType);
					return lstFiles;
				}

			});

			List<File> listFiles = futureResult.get(10,TimeUnit.SECONDS);
			int totalItems = 0;
			long totalPages = 0;
			if(Collectionz.isNullOrEmpty(listFiles) == false){
				totalItems = listFiles.size();		
				totalPages = (long)Math.ceil(totalItems/pageSize);
				if(totalItems%pageSize == 0)
					totalPages-=1;
				PageList pageList = new PageList(listFiles, requiredPageNo, totalPages, totalItems);
				if(Strings.isNullOrEmpty(strName) == false) {
					sendRadiusPacketForm.setName(strName);
				}else{
					sendRadiusPacketForm.setName("");
				}
				sendRadiusPacketForm.setLstSendPacketData(((List<File>)pageList.getListData()));
				sendRadiusPacketForm.setPageNumber(pageList.getCurrentPage());
				sendRadiusPacketForm.setTotalPages(pageList.getTotalPages());
				sendRadiusPacketForm.setTotalRecords(pageList.getTotalItems());
			}
			sendRadiusPacketForm.setAction(BaseConstant.LISTACTION);
			request.setAttribute("sendRadiusPacketForm", sendRadiusPacketForm);
			return mapping.findForward(LIST_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);		
			ActionMessage message;
			if(e instanceof CommunicationException){
				message = new ActionMessage("sendpacket.communication.fail");
			}else if (e  instanceof DataManagerException){
				message = new ActionMessage("sendpacket.datamanager.fail");
			}else if (e instanceof InterruptedException){
				message =  new ActionMessage("sendpacket.interruped");
			}else if(e instanceof TimeoutException){
				message = new ActionMessage("sendpacket.timout");
			}else if(e instanceof ExecutionException){
				message = new ActionMessage("sendpacket.execution.fail");
			}else{
				message = new ActionMessage("servermgr.sendpacket.radius.search.failure");
			}
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}

}
