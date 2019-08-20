package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.RemoteCommunicationManagerFactory;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.servermgr.server.form.ListUserfileAccountInformationForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ListUserfileAccountInformationAction extends BaseWebAction{
    private final String VIEW_FORWARD = "listUserfileDatasourceAccountInformation";
    private final String MODULE = "USERFILE DATASOURCE";
    private final String FAILURE_FORWARD = "failure";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.LIST_USERFILE_DATASOURCE_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
        Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());

        List netServerTypeList=null;
        INetServerInstanceData netServerInstanceData=null;
        IRemoteCommunicationManager remoteConnectionManager = null;

        try{
            checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
            ListUserfileAccountInformationForm listUserfileAccountInformationForm=(ListUserfileAccountInformationForm)form;

            String strNetServerId = request.getParameter("netserverid");
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}
            if(netServerId==null)
                netServerId=listUserfileAccountInformationForm.getNetserverid();

            String fileName = request.getParameter("selectedFileName");

            if(fileName==null){
                fileName=listUserfileAccountInformationForm.getSelectedFileName();
            }
            String action=request.getParameter("action");

            if(action==null)
                action=listUserfileAccountInformationForm.getAction();

            int requiredPageNo;
            if(request.getParameter("pageNo") != null ){
            	requiredPageNo = Integer.parseInt(request.getParameter("pageNo"));
            }else{
                requiredPageNo = listUserfileAccountInformationForm.getPageNo();
            }   
            if (requiredPageNo == 0)
                requiredPageNo = 1;
            	
            //System.out.println("requiredPageNo is :"+listUserfileAccountInformationForm.getPageNo());
            NetServerBLManager netServerBLManager = new NetServerBLManager();
            netServerTypeList = netServerBLManager.getNetServerTypeList();
            netServerInstanceData=netServerBLManager.getNetServerInstance(netServerId);
            
            listUserfileAccountInformationForm.setTotalField(Integer.parseInt(ConfigManager.get(BaseConstant.TOTAL_FIELD)));
            listUserfileAccountInformationForm.setNumerOfRecordsPerPage(Integer.parseInt(ConfigManager.get("TOTAL_ROW")));
            listUserfileAccountInformationForm.setSelectedFileName(fileName);
            
            String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);			
            remoteConnectionManager = RemoteCommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
            remoteConnectionManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(),netServerCode,true);


            Object[] argValue={fileName};
            String[] argName={"java.lang.String"};

            Map unsortedUserMap=null;
            

            if(action==null || action.trim().equalsIgnoreCase("")){
                unsortedUserMap = (Map) remoteConnectionManager.execute(MBeanConstants.RADIUS_USERFILE,"readEntities",argValue,argName);
              
                listUserfileAccountInformationForm.setTotalNumberOfRecord(unsortedUserMap.size());
                listUserfileAccountInformationForm.setPageNo(1);
                listUserfileAccountInformationForm=updateStartPageAndEndPage(listUserfileAccountInformationForm,unsortedUserMap);

            }else if(action!=null && action.trim().equalsIgnoreCase("paging")){
                unsortedUserMap = (Map) remoteConnectionManager.execute(MBeanConstants.RADIUS_USERFILE,"readEntities",argValue,argName);
                
                listUserfileAccountInformationForm.setTotalNumberOfRecord(unsortedUserMap.size());
                
                listUserfileAccountInformationForm=updateStartPageAndEndPage(listUserfileAccountInformationForm,unsortedUserMap,requiredPageNo);
            }

            listUserfileAccountInformationForm.setSelectedFileName(fileName);

            request.setAttribute("userAccoiuntMap", unsortedUserMap);
            request.setAttribute("netServerTypeList", netServerTypeList);
            request.setAttribute("netServerInstanceData", netServerInstanceData); 
            request.setAttribute("netserverid", netServerId);

            return mapping.findForward(VIEW_FORWARD);

        }catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (UnidentifiedServerInstanceException commExp) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + commExp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
            ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
        }catch(CommunicationException e){
            Logger.logError(MODULE, "Returning Error Forward From :" + getClass().getName());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
        }catch(Exception e){
            Logger.logError(MODULE, "Returning Error Forward From :" + getClass().getName());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
        }finally{
            try{
                if(remoteConnectionManager != null)
                    remoteConnectionManager.close();  
            }
            catch (Throwable e) {
                remoteConnectionManager = null;
            }
        }
        ActionMessage message = new ActionMessage("userfiledatasource.useracountlist.failed");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
    }

    public ListUserfileAccountInformationForm updateStartPageAndEndPage(ListUserfileAccountInformationForm listUserfileAccountInformationForm,Map unsortedUserMap){

        LinkedHashMap newMap=new LinkedHashMap();
        int start=0;
        int end=0;
        int totalPage=0;

        int recordPerPage=listUserfileAccountInformationForm.getNumerOfRecordsPerPage();
        int totalRecord=listUserfileAccountInformationForm.getTotalNumberOfRecord();

        totalPage=(int)Math.ceil((float)totalRecord/recordPerPage);
        
        
        if(totalPage>=1){
            listUserfileAccountInformationForm.setNextPage(2);
        }

        if(totalRecord <= recordPerPage)
            end=totalRecord;
        else
            end=recordPerPage;	

        newMap=getMapKeys(start,end,unsortedUserMap);

        listUserfileAccountInformationForm.setUserAccountMap(newMap);
        listUserfileAccountInformationForm.setStart(start);
        listUserfileAccountInformationForm.setEnd(end);
        listUserfileAccountInformationForm.setLastPage(totalPage);
        listUserfileAccountInformationForm.setTotalNoOfPage(totalPage);
        
        return 	listUserfileAccountInformationForm;
    }
    public ListUserfileAccountInformationForm updateStartPageAndEndPage(ListUserfileAccountInformationForm listUserfileAccountInformationForm,Map unsortedUserMap,int pageNo){

        LinkedHashMap newMap=new LinkedHashMap();
        int start=0;
        int end=0;
        int nextPage=0;
        int selectedPageNo = pageNo;
        int recordPerPage = listUserfileAccountInformationForm.getNumerOfRecordsPerPage();
        int totalRecord = listUserfileAccountInformationForm.getTotalNumberOfRecord();  
        
        listUserfileAccountInformationForm.setPageNo(selectedPageNo);
        listUserfileAccountInformationForm.setPreviousPage(selectedPageNo-1);
        
        
        int totalNoPage =(int)Math.ceil((float)totalRecord/recordPerPage);
        
        
        if(totalNoPage == 1){
            selectedPageNo=1;	
        }else {	
            if((selectedPageNo+1)>totalNoPage)
                nextPage=totalNoPage;
            else
                nextPage=selectedPageNo+1;
        }	

        start=(selectedPageNo*recordPerPage)-recordPerPage;
        end= selectedPageNo*recordPerPage;

        if(end > totalRecord)
        end=totalRecord;

        newMap=getMapKeys(start,end,unsortedUserMap);
        listUserfileAccountInformationForm.setUserAccountMap(newMap);

        listUserfileAccountInformationForm.setNextPage(nextPage);
        listUserfileAccountInformationForm.setStart(start);
        listUserfileAccountInformationForm.setEnd(end);
        listUserfileAccountInformationForm.setLastPage(totalNoPage);
        listUserfileAccountInformationForm.setTotalNoOfPage(totalNoPage);
        
        return 	listUserfileAccountInformationForm;
    }

    private LinkedHashMap getMapKeys(int start,int end,Map unsortedUserMap){

        Set set=unsortedUserMap.keySet();
        LinkedHashMap newMap=new LinkedHashMap();

        Iterator iter=set.iterator();
        String[] keyArray=new String[set.size()];

        int i=0;
        while(iter.hasNext()){
            keyArray[i]=(String)iter.next();
            i++;
        }

        for(int j=start;j<end;j++){
            newMap.put(keyArray[j],unsortedUserMap.get(keyArray[j]));
        }
        return newMap;
    }
}
