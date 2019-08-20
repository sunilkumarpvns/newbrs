package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.coreradius.util.mbean.data.MBeanConstants;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.RemoteCommunicationManagerFactory;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.CreateUserfileDatasourceForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class CreateUserfileDatasourceAction extends BaseWebAction {
    private final String VIEW_FORWARD = "createUserfileDatasource";
    private final String FAILURE_FORWARD = "failure";
    private final String MODULE = "USERFILE DATASOURCE";
    private final String SUCCESS_FORWARD = "success";
    private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.CREATE_USERFILE_DATASOURCE_ACTION;


    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
        Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
        Integer status=0; 
        IRemoteCommunicationManager remoteConnectionManager = null;


        try{
            checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
            CreateUserfileDatasourceForm createUserfileDatasourceForm=(CreateUserfileDatasourceForm)form;
            String strNetServerId = request.getParameter("netserverid");
            long netServerId;
            if(strNetServerId==null){
            	netServerId =createUserfileDatasourceForm.getNetServerId();
            }else{
              netServerId = Long.parseLong(strNetServerId.trim());
            }
            String fileName=request.getParameter("filename");

            if(fileName==null)
                fileName=createUserfileDatasourceForm.getFileName();

            NetServerBLManager netServerBLManager = new NetServerBLManager();
            List netServerTypeList = netServerBLManager.getNetServerTypeList();
            INetServerInstanceData netServerInstanceData=netServerBLManager.getNetServerInstance(netServerId);

            createUserfileDatasourceForm.setNetServerId(netServerId);
            createUserfileDatasourceForm.setFileName(fileName);

            netServerInstanceData=netServerBLManager.getNetServerInstance(netServerId);


            if(createUserfileDatasourceForm.getAction()!=null && createUserfileDatasourceForm.getAction().trim().equalsIgnoreCase("add")){
                Map updatesUserAccountMap=addFormDataInMap(createUserfileDatasourceForm);
                Object[] argValue={fileName,updatesUserAccountMap};
                String[] argName={"java.lang.String","java.util.Map"};
                String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);
                remoteConnectionManager = RemoteCommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
                remoteConnectionManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(),netServerCode,true);
                status=(Integer)  remoteConnectionManager.execute(com.elitecore.core.commons.utilx.mbean.MBeanConstants.RADIUS_USERFILE,"addUserAccount",argValue,argName);
            }else if(createUserfileDatasourceForm.getAction() !=null && createUserfileDatasourceForm.getAction().trim().equalsIgnoreCase("addAttribute")){
                List listDictionaryId=createUserfileDatasourceForm.getListAttributeId();
                List listAttributValue=createUserfileDatasourceForm.getListAttrbuteValue();
                List listAttributeName=createUserfileDatasourceForm.getListAttributeName();
                listDictionaryId.add(createUserfileDatasourceForm.getAttributeId());
                listAttributValue.add(new String());
                listAttributeName.add(createUserfileDatasourceForm.getAttributeIdName());
            }else if(createUserfileDatasourceForm.getAction()!=null && createUserfileDatasourceForm.getAction().trim().equalsIgnoreCase("removeAttribute")){
				int index =  Integer.parseInt(createUserfileDatasourceForm.getAttributeId());

				List listDictionaryId=createUserfileDatasourceForm.getListAttributeId();
				List listAttributValue=createUserfileDatasourceForm.getListAttrbuteValue();
				List listAttributeName=createUserfileDatasourceForm.getListAttributeName();

				listDictionaryId.remove(index);
				listAttributValue.remove(index);
				listAttributeName.remove(index);
			}else{
				Calendar calender = Calendar.getInstance();
				calender.add(Calendar.DAY_OF_YEAR,30);
			    String defaultExpiryDate = calender.get(Calendar.DAY_OF_MONTH)+"/"+(calender.get( Calendar.MONTH)+1)+"/"+calender.get(Calendar.YEAR);
			    createUserfileDatasourceForm.setExpiryDate(defaultExpiryDate);
				
			}
            

            request.setAttribute("netServerTypeList", netServerTypeList);
            request.setAttribute("netServerInstanceData", netServerInstanceData); 
            request.setAttribute("netserverid", netServerId);
            System.out.println("Status : "+status);
            System.out.println("Action : "+createUserfileDatasourceForm.getAction());
            if(createUserfileDatasourceForm.getAction()!=null && createUserfileDatasourceForm.getAction().trim().equalsIgnoreCase("add") && status > 0 ){
                String strUrl="/listUserfileAccountInformation.do?netserverid="+netServerId+"&selectedFileName="+fileName;
                request.setAttribute("responseUrl",strUrl);
                ActionMessage message = new ActionMessage("userfiledatasource.adduseraccount.success","Account"+status);
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);
                saveMessages(request,messages);
                return mapping.findForward(SUCCESS_FORWARD);

            }else if(createUserfileDatasourceForm.getAction()!=null && createUserfileDatasourceForm.getAction().trim().equalsIgnoreCase("add") && status <=0 ){
                Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
                ActionMessage message = new ActionMessage("userfiledatasource.adduseraccount.failed");
                ActionMessages messages = new ActionMessages();
                messages.add("information",message);

                if(status == -2){
                    ActionMessage message1 = new ActionMessage("userfiledatasource.adduseraccount.failed.reason");					
                    messages.add("information",message1);
                }
                saveErrors(request,messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
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
            ActionMessage message = new ActionMessage("servermgr.server.operation.failure");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(commExp);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage messageReason = new ActionMessage("servermgr.server.invalididentifier");			
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            messages.add("information",messageReason);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
        }catch(CommunicationException e){
            Logger.logError(MODULE,"Error during  Operation, reason : "+e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
        }catch(Exception e){
            Logger.logError(MODULE,"Error during  Operation, reason : "+e.getMessage());
            Logger.logTrace(MODULE,e);
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
        Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
        ActionMessage message = new ActionMessage("userfiledatasource.add.addfailed");
        ActionMessages messages = new ActionMessages();
        messages.add("information",message);
        saveErrors(request,messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
    private Map addFormDataInMap(CreateUserfileDatasourceForm createUserfileDatasourceForm){
        Map userAccountMap=new HashMap();
        userAccountMap=getAuthAttributeAccountDataMap(createUserfileDatasourceForm);
        return userAccountMap;
    }
    private Map getAuthAttributeAccountDataMap(CreateUserfileDatasourceForm createUserfileDatasourceForm) {

        Map attrAcctDataMap = new HashMap();
        Map accountInfoMap = new HashMap();
        Map authAttributesMap = new LinkedHashMap();

        List listAttributeId=createUserfileDatasourceForm.getListAttributeId();
        List listAttributeValue=createUserfileDatasourceForm.getListAttrbuteValue();

        for(int i=0;i<listAttributeId.size() && i<listAttributeValue.size();i++){
            authAttributesMap.put(listAttributeId.get(i), listAttributeValue.get(i));
        }	

        accountInfoMap.put(MBeanConstants.USER_PASSWORD_CHECK_ENABLED, createUserfileDatasourceForm.getPasswordCheckEnable());
        accountInfoMap.put(MBeanConstants.USER_PASSWORD,createUserfileDatasourceForm.getPassword());
        accountInfoMap.put(MBeanConstants.USER_PASSWORD_ENCRYPTION_TYPE,createUserfileDatasourceForm.getEncryptionType());
        accountInfoMap.put(MBeanConstants.ACCOUNT_STATUS, createUserfileDatasourceForm.getAccountStatus());
        accountInfoMap.put(MBeanConstants.CREDIT_LIMIT,Integer.toString(createUserfileDatasourceForm.getCreditLimit()));
        accountInfoMap.put(MBeanConstants.EXPIRY_DATE,createUserfileDatasourceForm.getExpiryDate());
        accountInfoMap.put(MBeanConstants.CUSTOMER_SERVICE, createUserfileDatasourceForm.getCustomerService());
        accountInfoMap.put(MBeanConstants.CUSTOMER_TYPE, createUserfileDatasourceForm.getCustomerType());
        accountInfoMap.put(MBeanConstants.SERVICE_TYPE,createUserfileDatasourceForm.getServiceType());
        accountInfoMap.put(MBeanConstants.CALLING_STATION_ID, createUserfileDatasourceForm.getCallingStationId());
        accountInfoMap.put(MBeanConstants.CALLED_STATION_ID, createUserfileDatasourceForm.getCalledStationId());
        accountInfoMap.put(MBeanConstants.EMAIL_ID,createUserfileDatasourceForm.getEmailId());
        accountInfoMap.put(MBeanConstants.USER_GROUP, createUserfileDatasourceForm.getUserGroup());
        accountInfoMap.put(MBeanConstants.USER_CHECK_ITEM, createUserfileDatasourceForm.getUserCheckItem());
        accountInfoMap.put(MBeanConstants.USER_REJECT_ITEM, createUserfileDatasourceForm.getUserRejectItem());
        accountInfoMap.put(MBeanConstants.USER_REPLY_ITEM, createUserfileDatasourceForm.getUserReplyItem());
        accountInfoMap.put(MBeanConstants.USER_ACCESS_POLICY, createUserfileDatasourceForm.getAccessPolicy());
        accountInfoMap.put(MBeanConstants.USER_RADIUS_POLICY, createUserfileDatasourceForm.getRadiusPolicy());
        accountInfoMap.put(MBeanConstants.USER_CONCURRENT_LOGIN_POLICY, createUserfileDatasourceForm.getConcurrentLoginPolicy());
        accountInfoMap.put(MBeanConstants.IP_POOL_NAME, createUserfileDatasourceForm.getIpPoolName());
        accountInfoMap.put(MBeanConstants.CUI, createUserfileDatasourceForm.getCui());
        accountInfoMap.put(MBeanConstants.USER_HOTLINE_POLICY,createUserfileDatasourceForm.getHotlinePolicy());
        accountInfoMap.put(MBeanConstants.PARAM1, createUserfileDatasourceForm.getParam1());
        accountInfoMap.put(MBeanConstants.PARAM2, createUserfileDatasourceForm.getParam2());
        accountInfoMap.put(MBeanConstants.PARAM3, createUserfileDatasourceForm.getParam3());
        accountInfoMap.put(MBeanConstants.GRACEPOLICY, createUserfileDatasourceForm.getGracePeriod());
        accountInfoMap.put(MBeanConstants.CALLBACK_ID, createUserfileDatasourceForm.getCallbackId());
        accountInfoMap.put(MBeanConstants.USERNAME, createUserfileDatasourceForm.getProfileUserName());
        attrAcctDataMap.put(MBeanConstants.AUTH_ATTRIBUTES_MAP, authAttributesMap);
        attrAcctDataMap.put(MBeanConstants.ACCOUNT_INFO_MAP, accountInfoMap);
        accountInfoMap.put(MBeanConstants.MACVALIDATION, createUserfileDatasourceForm.getMacValidation());
        accountInfoMap.put(MBeanConstants.GROUPBANDWIDTH, createUserfileDatasourceForm.getGroupBandwidth());
        return attrAcctDataMap;

    }
}
