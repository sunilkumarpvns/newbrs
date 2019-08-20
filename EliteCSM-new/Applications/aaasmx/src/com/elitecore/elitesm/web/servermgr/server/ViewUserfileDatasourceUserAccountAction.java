package com.elitecore.elitesm.web.servermgr.server;

import java.util.HashMap;
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

import com.elitecore.coreradius.util.mbean.data.MBeanConstants;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.server.forms.ViewUserfileDatasourceUserAccountForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class ViewUserfileDatasourceUserAccountAction extends BaseWebAction{
	private final String VIEW_FORWARD = "viewUserfileDatasourceUserAccount";
	private final String MODULE = "USERFILE DATASOURCE";
	private final String FAILURE_FORWARD = "failure";
	private final String SUCCESS_FORWARD = "success";
	private final String SUB_MODULE_ACTIONALIAS = ConfigConstant.UPDATE_USERFILE_DATASOURCE_ACTION;
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());

		int port=0;
		Integer status=0; 
		String host=null;
		List netServerTypeList=null;
		/*
		 * Comment By Kaushik - Old JMX Code Replaced By new
		 * JMXServiceURL url=null;
		JMXConnector jmxConnector=null;*/
		INetServerInstanceData netServerInstanceData=null;
		IRemoteCommunicationManager remoteCommunicationManager = null;
		try{

			ViewUserfileDatasourceUserAccountForm viewUserfileDatasourceUserAccountForm=(ViewUserfileDatasourceUserAccountForm)form;

			String strNetServerId = request.getParameter("netserverid");
			String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}else{
				netServerId=viewUserfileDatasourceUserAccountForm.getNetServerId();
			}
			String id = request.getParameter("id");
			if(id==null)
				id=viewUserfileDatasourceUserAccountForm.getId();

			String fileName=request.getParameter("filename");
			if(fileName==null)
				fileName=viewUserfileDatasourceUserAccountForm.getFileName();


			NetServerBLManager netServerBLManager = new NetServerBLManager();
			netServerTypeList = netServerBLManager.getNetServerTypeList();
			netServerInstanceData=netServerBLManager.getNetServerInstance(netServerId);

			viewUserfileDatasourceUserAccountForm.setId(id);
			viewUserfileDatasourceUserAccountForm.setNetServerId(netServerId);
			viewUserfileDatasourceUserAccountForm.setFileName(fileName);

			netServerInstanceData=netServerBLManager.getNetServerInstance(netServerId);
			port=netServerInstanceData.getAdminPort();
			host=netServerInstanceData.getAdminHost();

			/*
			 * Comment By Kaushik - Old JMX Code Replaced By new
			 * url =  new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
			jmxConnector = JMXConnectorFactory.connect(url, null);
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			ObjectName objName = new ObjectName("Elitecore:type=EliteAdmin");
			 */

			String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);			
			remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);                
			remoteCommunicationManager.init(host,port,netServerCode,true);


			Map userAccountMap=null;

			if(viewUserfileDatasourceUserAccountForm.getAction()==null){
				Object[] argValue={fileName,id};
				String[] argName={"java.lang.String","java.lang.String"};
				/*
				 * Comment By Kaushik - Old JMX Code Replaced By new
				 * 
				 * userAccountMap = (Map)mbeanServerConnection.invoke(objName,"getAccount",argValue,argName);*/
				userAccountMap = (Map) remoteCommunicationManager.execute(com.elitecore.core.commons.utilx.mbean.MBeanConstants.RADIUS_USERFILE,"getAccount",argValue,argName);


				try{
					viewUserfileDatasourceUserAccountForm=addMapDataInForm(viewUserfileDatasourceUserAccountForm,userAccountMap);
				}catch(Exception e){
					Logger.logTrace(MODULE, "Returning Error Forward From :" + getClass().getName());
					ActionMessage message = new ActionMessage("userfiledatasource.view.dictionaryfailed");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE_FORWARD);
				}

			}else if(viewUserfileDatasourceUserAccountForm.getAction()!=null && viewUserfileDatasourceUserAccountForm.getAction().trim().equalsIgnoreCase("update")){
				 checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
				Map updatesUserAccountMap=addFormDataInMap(viewUserfileDatasourceUserAccountForm);
				Object[] argValue={fileName,id,updatesUserAccountMap};
				String[] argName={"java.lang.String","java.lang.String","java.util.Map"};
				/*Comment By Kaushik - Old JMX Code Replaced By new
				 * status=(Integer)mbeanServerConnection.invoke(objName,"updateUserAccount",argValue,argName);
				 */
				status = (Integer) remoteCommunicationManager.execute(com.elitecore.core.commons.utilx.mbean.MBeanConstants.RADIUS_USERFILE,"updateUserAccount",argValue,argName);
			}

			if(viewUserfileDatasourceUserAccountForm.getAction()!=null && viewUserfileDatasourceUserAccountForm.getAction().trim().equalsIgnoreCase("addAttribute")){

				List listDictionaryId=viewUserfileDatasourceUserAccountForm.getListAttributeId();
				List listAttributValue=viewUserfileDatasourceUserAccountForm.getListAttrbuteValue();
				List listAttributeName=viewUserfileDatasourceUserAccountForm.getListAttributeName();
				listDictionaryId.add(viewUserfileDatasourceUserAccountForm.getAttributeId());
				listAttributValue.add(new String());
				listAttributeName.add(viewUserfileDatasourceUserAccountForm.getAttributeIdName());
			}

			if(viewUserfileDatasourceUserAccountForm.getAction()!=null && viewUserfileDatasourceUserAccountForm.getAction().trim().equalsIgnoreCase("removeAttribute")){
				int index =  Integer.parseInt(viewUserfileDatasourceUserAccountForm.getAttributeId());

				List listDictionaryId=viewUserfileDatasourceUserAccountForm.getListAttributeId();
				List listAttributValue=viewUserfileDatasourceUserAccountForm.getListAttrbuteValue();
				List listAttributeName=viewUserfileDatasourceUserAccountForm.getListAttributeName();

				listDictionaryId.remove(index);
				listAttributValue.remove(index);
				listAttributeName.remove(index);
			}

			request.setAttribute("userAccountMap", userAccountMap);
			request.setAttribute("netServerTypeList", netServerTypeList);
			request.setAttribute("netServerInstanceData", netServerInstanceData); 
			request.setAttribute("netserverid", netServerId);

			if(viewUserfileDatasourceUserAccountForm.getAction()!=null && viewUserfileDatasourceUserAccountForm.getAction().trim().equalsIgnoreCase("update") && status >=0 ){

				String strUrl="/listUserfileAccountInformation.do?netserverid="+netServerId+"&id="+id+"&selectedFileName="+fileName;
				request.setAttribute("responseUrl",strUrl);
				ActionMessage message = new ActionMessage("userfiledatasource.updateuseraccount.success","Account"+id);

				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);

			}else if(viewUserfileDatasourceUserAccountForm.getAction()!=null && viewUserfileDatasourceUserAccountForm.getAction().trim().equalsIgnoreCase("update") && status < 0){

				Logger.logError(MODULE, "Returning Error Forward From :" + getClass().getName());
				ActionMessage message = new ActionMessage("userfiledatasource.updateuseraccount.failed");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				if(status == -2){
					ActionMessage message1 = new ActionMessage("userfiledatasource.adduseraccount.failed.reason");					
					messages.add("information",message1);
				}

				//ActionMessage message1 = new ActionMessage("userfiledatasource.updateuseraccount.readonlyfile");


				//messages.add("information",message1);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}

			return mapping.findForward(VIEW_FORWARD);

		} catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (UnidentifiedServerInstanceException commExp) {
			commExp.printStackTrace();
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
		}
		catch(CommunicationException e)
		{
			Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("userfiledatasource.view.viewfailed");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
		catch(Exception e){
			Logger.logError(MODULE,"Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE,e);	
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("userfiledatasource.view.viewfailed");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
		finally{
			try{
				if(remoteCommunicationManager != null)
					remoteCommunicationManager.close();  
			}
			catch (Throwable e) {
				remoteCommunicationManager = null;
			}
		}

	}
	private Map addFormDataInMap(ViewUserfileDatasourceUserAccountForm viewUserfileDatasourceUserAccountForm){
		Map userAccountMap=new HashMap();
		userAccountMap=getUserAccountMap(viewUserfileDatasourceUserAccountForm);
		return userAccountMap;
	}
	private Map getUserAccountMap(ViewUserfileDatasourceUserAccountForm viewUserfileDatasourceUserAccountForm) {

		Map attrAcctDataMap = new HashMap();

		Map accountInfoMap = new HashMap();
		Map authAttributesMap = new LinkedHashMap();


		List listAttributeId=viewUserfileDatasourceUserAccountForm.getListAttributeId();
		List listAttributeValue=viewUserfileDatasourceUserAccountForm.getListAttrbuteValue();

		for(int i=0;i<listAttributeId.size() && i<listAttributeValue.size();i++){
			authAttributesMap.put(listAttributeId.get(i), listAttributeValue.get(i));
		}	

		accountInfoMap.put(MBeanConstants.USER_PASSWORD_CHECK_ENABLED, viewUserfileDatasourceUserAccountForm.getPasswordCheckEnable());
		accountInfoMap.put(MBeanConstants.USER_PASSWORD,viewUserfileDatasourceUserAccountForm.getPassword());
		accountInfoMap.put(MBeanConstants.USER_PASSWORD_ENCRYPTION_TYPE,viewUserfileDatasourceUserAccountForm.getEncryptionType());
		accountInfoMap.put(MBeanConstants.ACCOUNT_STATUS, viewUserfileDatasourceUserAccountForm.getAccountStatus());
		accountInfoMap.put(MBeanConstants.CREDIT_LIMIT,Integer.toString(viewUserfileDatasourceUserAccountForm.getCreditLimit()));
		accountInfoMap.put(MBeanConstants.EXPIRY_DATE,viewUserfileDatasourceUserAccountForm.getExpiryDate());
		accountInfoMap.put(MBeanConstants.CUSTOMER_SERVICE, viewUserfileDatasourceUserAccountForm.getCustomerService());
		accountInfoMap.put(MBeanConstants.CUSTOMER_TYPE, viewUserfileDatasourceUserAccountForm.getCustomerType());
		accountInfoMap.put(MBeanConstants.SERVICE_TYPE,viewUserfileDatasourceUserAccountForm.getServiceType());
		accountInfoMap.put(MBeanConstants.CALLING_STATION_ID, viewUserfileDatasourceUserAccountForm.getCallingStationId());
		accountInfoMap.put(MBeanConstants.CALLED_STATION_ID, viewUserfileDatasourceUserAccountForm.getCalledStationId());
		accountInfoMap.put(MBeanConstants.EMAIL_ID,viewUserfileDatasourceUserAccountForm.getEmailId());
		accountInfoMap.put(MBeanConstants.USER_GROUP, viewUserfileDatasourceUserAccountForm.getUserGroup());
		accountInfoMap.put(MBeanConstants.USER_CHECK_ITEM, viewUserfileDatasourceUserAccountForm.getUserCheckItem());
		accountInfoMap.put(MBeanConstants.USER_REJECT_ITEM, viewUserfileDatasourceUserAccountForm.getUserRejectItem());
		accountInfoMap.put(MBeanConstants.USER_REPLY_ITEM, viewUserfileDatasourceUserAccountForm.getUserReplyItem());
		accountInfoMap.put(MBeanConstants.USER_ACCESS_POLICY, viewUserfileDatasourceUserAccountForm.getAccessPolicy());
		accountInfoMap.put(MBeanConstants.USER_RADIUS_POLICY, viewUserfileDatasourceUserAccountForm.getRadiusPolicy());
		accountInfoMap.put(MBeanConstants.USER_CONCURRENT_LOGIN_POLICY, viewUserfileDatasourceUserAccountForm.getConcurrentLoginPolicy());
		accountInfoMap.put(MBeanConstants.IP_POOL_NAME, viewUserfileDatasourceUserAccountForm.getIpPoolName());
		accountInfoMap.put(MBeanConstants.CUI, viewUserfileDatasourceUserAccountForm.getCui());
		accountInfoMap.put(MBeanConstants.USER_HOTLINE_POLICY, viewUserfileDatasourceUserAccountForm.getHotlinePolicy());
		accountInfoMap.put(MBeanConstants.PARAM1, viewUserfileDatasourceUserAccountForm.getParam1());
		accountInfoMap.put(MBeanConstants.PARAM2, viewUserfileDatasourceUserAccountForm.getParam2());
		accountInfoMap.put(MBeanConstants.PARAM3, viewUserfileDatasourceUserAccountForm.getParam3());
		accountInfoMap.put(MBeanConstants.PARAM4, viewUserfileDatasourceUserAccountForm.getParam4());
		accountInfoMap.put(MBeanConstants.PARAM5, viewUserfileDatasourceUserAccountForm.getParam5());
		accountInfoMap.put(MBeanConstants.GRACEPOLICY, viewUserfileDatasourceUserAccountForm.getGracePolicy());
		accountInfoMap.put(MBeanConstants.CALLBACK_ID, viewUserfileDatasourceUserAccountForm.getCallbackId());
		accountInfoMap.put(MBeanConstants.USERNAME, viewUserfileDatasourceUserAccountForm.getProfileUserName());
		accountInfoMap.put(MBeanConstants.MACVALIDATION, viewUserfileDatasourceUserAccountForm.getMacValidation());
		accountInfoMap.put(MBeanConstants.GROUPBANDWIDTH, viewUserfileDatasourceUserAccountForm.getGroupBandwidth());
		accountInfoMap.put(MBeanConstants.IMSI, viewUserfileDatasourceUserAccountForm.getImsi());
        accountInfoMap.put(MBeanConstants.MEID, viewUserfileDatasourceUserAccountForm.getMeid());
        accountInfoMap.put(MBeanConstants.MSISDN, viewUserfileDatasourceUserAccountForm.getMsIsdn());
        accountInfoMap.put(MBeanConstants.MDN, viewUserfileDatasourceUserAccountForm.getMdn());
        accountInfoMap.put(MBeanConstants.IMEI, viewUserfileDatasourceUserAccountForm.getImei());
        accountInfoMap.put(MBeanConstants.DEVICEVENDOR, viewUserfileDatasourceUserAccountForm.getDeviceVendor());
        accountInfoMap.put(MBeanConstants.DEVICENAME, viewUserfileDatasourceUserAccountForm.getDeviceName());
        accountInfoMap.put(MBeanConstants.DEVICEPORT, viewUserfileDatasourceUserAccountForm.getDevicePort());
        accountInfoMap.put(MBeanConstants.GEOLOCATION, viewUserfileDatasourceUserAccountForm.getGeoLocation());
        accountInfoMap.put(MBeanConstants.DEVICEVLAN, viewUserfileDatasourceUserAccountForm.getDeviceVLAN());
        accountInfoMap.put(MBeanConstants.FRAMEDIPV4ADDRESS, viewUserfileDatasourceUserAccountForm.getFramedIPV4Address());
        accountInfoMap.put(MBeanConstants.FRAMEDIPV6PREFIX, viewUserfileDatasourceUserAccountForm.getFramedIPV6Prefix());
        accountInfoMap.put(MBeanConstants.FRAMEDPOOL, viewUserfileDatasourceUserAccountForm.getFramedPool());
        accountInfoMap.put(MBeanConstants.AUTHORIZATIONPOLICYGROUP, viewUserfileDatasourceUserAccountForm.getAuthorizationPolicyGroup());
        attrAcctDataMap.put(MBeanConstants.AUTH_ATTRIBUTES_MAP, authAttributesMap);
		attrAcctDataMap.put(MBeanConstants.ACCOUNT_INFO_MAP, accountInfoMap);
		
		
		return attrAcctDataMap;
	}

	private ViewUserfileDatasourceUserAccountForm addMapDataInForm(ViewUserfileDatasourceUserAccountForm viewUserfileDatasourceUserAccountForm,Map userAccountMap) throws DataManagerException{

		Map accountInfoMap=(Map)userAccountMap.get(MBeanConstants.ACCOUNT_INFO_MAP);
		Map authAttributeMap=(Map)userAccountMap.get(MBeanConstants.AUTH_ATTRIBUTES_MAP);

		Set set = authAttributeMap.keySet();
		Iterator iter = set.iterator();
		String[] keyArray = new String[set.size()];
		int i = 0;

		while (iter.hasNext()) {
			keyArray[i] = (String) iter.next();
			i++;
		}

		List listDictionaryId=viewUserfileDatasourceUserAccountForm.getListAttributeId();
		List listAttributValue=viewUserfileDatasourceUserAccountForm.getListAttrbuteValue();
		List listAttributeName=viewUserfileDatasourceUserAccountForm.getListAttributeName();

		for(int j=0;j<keyArray.length;j++){
			String dictAttributeName=getDictionaryAttributeName(keyArray[j]);
			listDictionaryId.add(keyArray[j]);
			listAttributeName.add(dictAttributeName);
			listAttributValue.add(authAttributeMap.get(keyArray[j]));
		}

		viewUserfileDatasourceUserAccountForm.setPasswordCheckEnable((String)accountInfoMap.get(MBeanConstants.USER_PASSWORD_CHECK_ENABLED));
		viewUserfileDatasourceUserAccountForm.setPassword((String)accountInfoMap.get(MBeanConstants.USER_PASSWORD));
		viewUserfileDatasourceUserAccountForm.setEncryptionType((String)accountInfoMap.get(MBeanConstants.USER_PASSWORD_ENCRYPTION_TYPE));
		viewUserfileDatasourceUserAccountForm.setAccountStatus((String)accountInfoMap.get(MBeanConstants.ACCOUNT_STATUS));
		viewUserfileDatasourceUserAccountForm.setCreditLimit(Integer.parseInt((String)accountInfoMap.get(MBeanConstants.CREDIT_LIMIT)));
		viewUserfileDatasourceUserAccountForm.setExpiryDate((String)accountInfoMap.get(MBeanConstants.EXPIRY_DATE));
		viewUserfileDatasourceUserAccountForm.setCustomerService((String)accountInfoMap.get(MBeanConstants.CUSTOMER_SERVICE));
		viewUserfileDatasourceUserAccountForm.setCustomerType((String)accountInfoMap.get(MBeanConstants.CUSTOMER_TYPE));
		viewUserfileDatasourceUserAccountForm.setServiceType((String)accountInfoMap.get(MBeanConstants.SERVICE_TYPE));
		viewUserfileDatasourceUserAccountForm.setCallingStationId((String)accountInfoMap.get(MBeanConstants.CALLING_STATION_ID));
		viewUserfileDatasourceUserAccountForm.setCalledStationId((String)accountInfoMap.get(MBeanConstants.CALLED_STATION_ID));
		viewUserfileDatasourceUserAccountForm.setEmailId((String)accountInfoMap.get(MBeanConstants.EMAIL_ID));
		viewUserfileDatasourceUserAccountForm.setUserGroup((String)accountInfoMap.get(MBeanConstants.USER_GROUP));
		viewUserfileDatasourceUserAccountForm.setUserCheckItem((String)accountInfoMap.get(MBeanConstants.USER_CHECK_ITEM));
		viewUserfileDatasourceUserAccountForm.setUserRejectItem((String)accountInfoMap.get(MBeanConstants.USER_REJECT_ITEM));
		viewUserfileDatasourceUserAccountForm.setUserReplyItem((String)accountInfoMap.get(MBeanConstants.USER_REPLY_ITEM));
		viewUserfileDatasourceUserAccountForm.setAccessPolicy((String)accountInfoMap.get(MBeanConstants.USER_ACCESS_POLICY));
		viewUserfileDatasourceUserAccountForm.setRadiusPolicy((String)accountInfoMap.get(MBeanConstants.USER_RADIUS_POLICY));
		viewUserfileDatasourceUserAccountForm.setConcurrentLoginPolicy((String)accountInfoMap.get(MBeanConstants.USER_CONCURRENT_LOGIN_POLICY));
		viewUserfileDatasourceUserAccountForm.setIpPoolName((String)accountInfoMap.get(MBeanConstants.IP_POOL_NAME));
		viewUserfileDatasourceUserAccountForm.setCui((String)accountInfoMap.get(MBeanConstants.CUI));
		viewUserfileDatasourceUserAccountForm.setHotlinePolicy((String)accountInfoMap.get(MBeanConstants.USER_HOTLINE_POLICY));
		viewUserfileDatasourceUserAccountForm.setParam1((String)accountInfoMap.get(MBeanConstants.PARAM1));
		viewUserfileDatasourceUserAccountForm.setParam2((String)accountInfoMap.get(MBeanConstants.PARAM2));
		viewUserfileDatasourceUserAccountForm.setParam3((String)accountInfoMap.get(MBeanConstants.PARAM3));
		viewUserfileDatasourceUserAccountForm.setParam4((String)accountInfoMap.get(MBeanConstants.PARAM4));
		viewUserfileDatasourceUserAccountForm.setParam5((String)accountInfoMap.get(MBeanConstants.PARAM5));
		viewUserfileDatasourceUserAccountForm.setGracePolicy((String)accountInfoMap.get(MBeanConstants.GRACEPOLICY));
		viewUserfileDatasourceUserAccountForm.setCallbackId((String)accountInfoMap.get(MBeanConstants.CALLBACK_ID));
		viewUserfileDatasourceUserAccountForm.setProfileUserName((String)accountInfoMap.get(MBeanConstants.USERNAME));
		viewUserfileDatasourceUserAccountForm.setMacValidation((String)accountInfoMap.get(MBeanConstants.MACVALIDATION));
		viewUserfileDatasourceUserAccountForm.setGroupBandwidth((String)accountInfoMap.get(MBeanConstants.GROUPBANDWIDTH));
		viewUserfileDatasourceUserAccountForm.setImsi((String)accountInfoMap.get(MBeanConstants.IMSI));
		viewUserfileDatasourceUserAccountForm.setMeid((String)accountInfoMap.get(MBeanConstants.MEID));
		viewUserfileDatasourceUserAccountForm.setMsIsdn((String)accountInfoMap.get(MBeanConstants.MSISDN));
		viewUserfileDatasourceUserAccountForm.setMdn((String)accountInfoMap.get(MBeanConstants.MDN));
		viewUserfileDatasourceUserAccountForm.setImei((String)accountInfoMap.get(MBeanConstants.IMEI));
		viewUserfileDatasourceUserAccountForm.setDeviceVendor((String)accountInfoMap.get(MBeanConstants.DEVICEVENDOR));
		viewUserfileDatasourceUserAccountForm.setDeviceName((String)accountInfoMap.get(MBeanConstants.DEVICENAME));
		viewUserfileDatasourceUserAccountForm.setDevicePort((String)accountInfoMap.get(MBeanConstants.DEVICEPORT));
		viewUserfileDatasourceUserAccountForm.setGeoLocation((String)accountInfoMap.get(MBeanConstants.GEOLOCATION));
		viewUserfileDatasourceUserAccountForm.setDeviceVLAN((String)accountInfoMap.get(MBeanConstants.DEVICEVLAN));
		viewUserfileDatasourceUserAccountForm.setFramedIPV4Address((String)accountInfoMap.get(MBeanConstants.FRAMEDIPV4ADDRESS));
		viewUserfileDatasourceUserAccountForm.setFramedIPV6Prefix((String)accountInfoMap.get(MBeanConstants.FRAMEDIPV6PREFIX));
		viewUserfileDatasourceUserAccountForm.setFramedPool((String)accountInfoMap.get(MBeanConstants.FRAMEDPOOL));
		viewUserfileDatasourceUserAccountForm.setAuthorizationPolicyGroup((String)accountInfoMap.get(MBeanConstants.AUTHORIZATIONPOLICYGROUP));
		return viewUserfileDatasourceUserAccountForm;
	}
	String getDictionaryAttributeName(String dictAttributeName) throws DataManagerException{

		int index=dictAttributeName.indexOf(':');
		String vendorId=dictAttributeName.substring(0, index);
		String strDictionaryParameterId=dictAttributeName.substring(index+1, dictAttributeName.length());
		RadiusDictionaryBLManager dictionaryDataManager=new RadiusDictionaryBLManager();
		String dictionaryParameterId = strDictionaryParameterId;
		String DictionaryParameterName=dictionaryDataManager.getDictionaryParameterDetail(vendorId,dictionaryParameterId);

		return DictionaryParameterName;
	}
}
