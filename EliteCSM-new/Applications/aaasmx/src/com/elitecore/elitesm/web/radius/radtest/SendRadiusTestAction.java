package com.elitecore.elitesm.web.radius.radtest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.util.chap.CHAPUtil;
import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.blmanager.radius.radtest.RadiusTestBLManager;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestParamData;
import com.elitecore.elitesm.util.constants.RadiusConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.radtest.forms.ViewRadiusTestForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class SendRadiusTestAction extends BaseWebAction{
    private static final String SUCCESS_FORWARD = "success";               
    private static final String FAILURE_FORWARD = "failure";               
    private static final String MODULE ="SendRadiusTestAction";
    private static final String VIEW_FORWARD = "responseRadiusPacket";
    private static final String[] serviceType = {"Authentication Request","Accounting Start","Accounting Stop","Accounting Update","Accounting On","Accounting Off","Status Server","User Define"};
        
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
            Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
            ViewRadiusTestForm viewRadiusTestForm = (ViewRadiusTestForm)form;
            RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
            RadiusTestBLManager radiusBLManager = new RadiusTestBLManager();
            ActionMessages messages = new ActionMessages();
            String hostAddress = request.getParameter("hostAddress");
            if(hostAddress==null){
            	
            	hostAddress = viewRadiusTestForm.getHostAddress();
            }
            
           
            try {
                String strRadiusTestId = (String)request.getSession().getAttribute("fieldId");
                String radiusTestId =  strRadiusTestId;
                
                if(Strings.isNullOrBlank(radiusTestId) == false) {
                	IRadiusTestData radiusTestData = radiusBLManager.getRadiusObjById(radiusTestId);
                    if(radiusTestData != null) {
                        this.beanToForm(radiusTestData, viewRadiusTestForm);
                        request.setAttribute("radParamList", viewRadiusTestForm.getRadParamRel());
                        
                        RadiusDictionaryBLManager radiusDictionaryBLManager = new RadiusDictionaryBLManager();
                        
                        List<DictionaryData> dictionaryList = radiusDictionaryBLManager.getDictionaryList();
                        if(dictionaryList!=null && !dictionaryList.isEmpty()){
                        	for (Iterator<DictionaryData> iterator = dictionaryList.iterator(); iterator.hasNext();) {
								DictionaryData dictionaryData =  iterator.next();
								
								String xmlDictionary = null;
								try{
									xmlDictionary = radiusDictionaryBLManager.convertAsXml(dictionaryData);
								}catch(Exception e){
									 Logger.logError(MODULE,"Error during build dictionary xml String, Dictonary ["+dictionaryData.getName()+"]reason : " + e.getMessage());
								}
								
								if(xmlDictionary!=null){
								 InputStream is = new ByteArrayInputStream(xmlDictionary.getBytes());
								 try {
			                          Dictionary.getInstance().load(new InputStreamReader(is));
			                     } catch(Exception e) {
			                    	 Logger.logError(MODULE,"Error during load dictionary, reason : " + e.getMessage());
			                     }
								}
							}
                        	
                        }
                        /* Send & Receive Data Through Radius API */
		                  String responseString;
		                  
		                 /*decrypt User Password*/
		      			 String decryptedPassword = PasswordEncryption.getInstance().decrypt(radiusTestData.getUserPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
		      			 radiusTestData.setUserPassword(decryptedPassword);
		                
                         responseString = sendDataToRadiusAPI(radiusTestData,request);
                         request.setAttribute("responseString", responseString);
		                
                }
                return mapping.findForward(VIEW_FORWARD);
             
              }
            }catch(SocketException sexp){
                   
        	  		Logger.logError(MODULE, "Error during Data Manager operation , reason : " + sexp.getMessage());
        			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(sexp);
        			request.setAttribute("errorDetails", errorElements);
                    ActionMessage message = new ActionMessage("radius.radtest.socket.bind.failure");
                    ActionMessage messageReason=new ActionMessage("radius.radtest.socket.bind.information");
                    messages.add("information", message);
                    messages.add("information", messageReason);
                    saveErrors(request, messages);
                          	
                         	
           }catch(Exception e) {
            	
            	Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
    		    ActionMessage message = new ActionMessage("general.error");
                messages.add("information", message);
    			saveErrors(request, messages);
            	
            }
           return mapping.findForward(FAILURE_FORWARD); 
           
           
    }
    
    private void beanToForm(IRadiusTestData radiusTestData ,ViewRadiusTestForm viewRadiusTestForm) {
        viewRadiusTestForm.setNtradId(radiusTestData.getNtradId());
        viewRadiusTestForm.setName(radiusTestData.getName());
        viewRadiusTestForm.setAdminHost(radiusTestData.getAdminHost());
        viewRadiusTestForm.setAdminPort(radiusTestData.getAdminPort());
        viewRadiusTestForm.setReTimeOut(radiusTestData.getReTimeOut());
        viewRadiusTestForm.setRetries(radiusTestData.getRetries());
        viewRadiusTestForm.setScecretKey(radiusTestData.getScecretKey());
        viewRadiusTestForm.setUserName(radiusTestData.getUserName());
        viewRadiusTestForm.setUserPassword(radiusTestData.getUserPassword());
        viewRadiusTestForm.setIsChap(radiusTestData.getIsChap());
        
        for(int i = 0;i<serviceType.length;i++) {
            if((radiusTestData.getRequestType()-1) == i) {
                viewRadiusTestForm.setServiceType(serviceType[i]);
                break;
            } else {
                viewRadiusTestForm.setServiceType(serviceType[serviceType.length -1]);
            }
        }
        viewRadiusTestForm.setRequestType(radiusTestData.getRequestType());
        viewRadiusTestForm.setRadParamRel(radiusTestData.getRadParamRel());
    }
    
    private String sendDataToRadiusAPI(IRadiusTestData radiusData,HttpServletRequest request) throws SocketException{
        
    	RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
        DatagramSocket dSocket = null;
        DatagramPacket dPacket = null;
        String responseString = "";
        
        try {
            /* SENDING A REQUEST */ 
            RadiusPacket radiusPacket = new RadiusPacket();
            
            byte[] requestAuthenticatorForAuthentication = new byte[16];
            byte[] requestAuthenticatorForAccounting = new byte[16];
            
            requestAuthenticatorForAuthentication = RadiusUtility.generateRFC2865RequestAuthenticator();
            
            //Add user name as an attribute
            IRadiusAttribute radiusAttributeUser = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.USER_NAME);
            radiusAttributeUser.setStringValue(radiusData.getUserName());
            radiusPacket.addAttribute(radiusAttributeUser);
            
            //Add user pass as an attribute
            if(radiusData.getIsChap().equalsIgnoreCase("N")){
           
              IRadiusAttribute radiusAttributePass = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.USER_PASSWORD);
              radiusAttributePass.setValueBytes(RadiusUtility.encryptPasswordRFC2865(radiusData.getUserPassword(), requestAuthenticatorForAuthentication, radiusData.getScecretKey()));
              if(radiusData.getRequestType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){	
                 radiusPacket.addAttribute(radiusAttributePass);
              }
            }else if(radiusData.getIsChap().equalsIgnoreCase("Y")){
            
            	IRadiusAttribute radiusAttributeChapPass = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
                byte[] calculatedCHAPPassword = CHAPUtil.generateCHAPPassword((byte)1, radiusData.getUserPassword(), requestAuthenticatorForAuthentication);
                byte[] chapPasswordValue = new byte[calculatedCHAPPassword.length + 1];
                chapPasswordValue[0] = (byte)1;
                System.arraycopy(calculatedCHAPPassword, 0, chapPasswordValue, 1, calculatedCHAPPassword.length);
                radiusAttributeChapPass.setValueBytes(chapPasswordValue);
                if(radiusData.getRequestType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){      
                   radiusPacket.addAttribute(radiusAttributeChapPass);
                 }
          	
            }
           
            radiusPacket.refreshPacketHeader();
            for(Iterator iter = radiusData.getRadParamRel().iterator();iter.hasNext();) {
                RadiusTestParamData radParamData = (RadiusTestParamData)iter.next();
                IDictionaryParameterDetailData dictParamDetail = dictionaryBLManager.getDictionaryParamDetailByName(radParamData.getName().trim());
                long lVendorId = dictParamDetail.getVendorId();
                int attrId = dictionaryBLManager.getDictionaryIdByName(radParamData.getName().trim());
                
                if(lVendorId == 0){
                	IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(attrId);
                	if(radiusAttribute != null){
                        radiusAttribute.setStringValue(radParamData.getValue());
                        radiusPacket.addAttribute(radiusAttribute);
                	}
                }else{
                	VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute)Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
                	if(vsaAttribute != null){
	                	vsaAttribute.setVendorID(lVendorId);
	                	IVendorSpecificAttribute vsaAttributeType = Dictionary.getInstance().getVendorAttributeType(lVendorId);
	                	IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(lVendorId, attrId);
	                	if(radiusAttribute != null){
	                		radiusAttribute.setStringValue(radParamData.getValue());
	                		vsaAttributeType.addAttribute(radiusAttribute);
	                		vsaAttributeType.refreshAttributeHeader();
	                		vsaAttribute.setVendorTypeAttribute(vsaAttributeType);
	                		vsaAttribute.refreshAttributeHeader();
	                		radiusPacket.addAttribute(vsaAttribute);
	                	}
                	}
                }
                
                radiusPacket.refreshPacketHeader();
            }
            
            //Check which request is going to be send
            if(radiusData.getRequestType() == RadiusConstants.ACCESS_REQUEST_MESSAGE) {
                radiusPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
                radiusPacket.setAuthenticator(requestAuthenticatorForAuthentication);
                
            }else if (radiusData.getRequestType() == RadiusConstant.ACCOUNTING_REQUEST_START || radiusData.getRequestType() == RadiusConstant.ACCOUNTING_REQUEST_STOP || radiusData.getRequestType() == RadiusConstant.ACCOUNTING_REQUEST_UPDTAE) {
                radiusPacket.setPacketType(RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
                
                //if account session id is not specified than set it .
                RadiusTestParamData radparam = new RadiusTestParamData();
                radparam.setName(RadiusAttributeConstants.ACCT_SESSION_ID_STR);
               
                if(!isAttributeExist(radiusData,radparam)){
                	//generate random number for acct session id
                	int acct_session_id = (int)(Math.random()*1000);
                	Logger.logDebug(MODULE,RadiusAttributeConstants.ACCT_SESSION_ID_STR+":"+acct_session_id );
                	String value = String.valueOf(acct_session_id);
                	radparam.setValue(value);
                	int attrId = dictionaryBLManager.getDictionaryIdByName(radparam.getName().trim());
                	IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(attrId);
                	if(radiusAttribute != null){
                		radiusAttribute.setStringValue(radparam.getValue());
                		radiusPacket.addAttribute(radiusAttribute);
                	}
                }
                
                radiusPacket.refreshPacketHeader();
                IRadiusAttribute radiusAttributeStatus = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE);
                radiusAttributeStatus.setIntValue(radiusData.getRequestType() - 1);
                radiusPacket.addAttribute(radiusAttributeStatus);
                radiusPacket.refreshPacketHeader();

                requestAuthenticatorForAccounting = RadiusUtility.generateRequestAuthenticatorForAccounting(radiusPacket, radiusData.getScecretKey());
                radiusPacket.setAuthenticator(requestAuthenticatorForAccounting);
                
            } else {
                radiusPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
                radiusPacket.setAuthenticator(requestAuthenticatorForAuthentication);
            }
            
            String hostAddress="";
            int hostPort=-1;
            
            if(radiusData.getHostAddress()!= null){
            
             String[] clientInterface = radiusData.getHostAddress().split(":");
             
             
             if(clientInterface.length == 2){
            	 if(clientInterface[0]!=null && clientInterface[0].trim().length()>0){    
            		 hostAddress=clientInterface[0];
            	 }
            	 hostPort=Integer.parseInt(clientInterface[1]);
             }else if(clientInterface.length == 1){
            	 hostAddress=clientInterface[0];
             }
            
            }
            if(hostPort <=0 || hostPort > 65535){
            	hostPort = 0;
            	Logger.logDebug(MODULE, "Host Port is not specified. Using default: " + hostPort );
            }
             
            
            
            Logger.logDebug(MODULE, "Client Address: "+hostAddress);
            Logger.logDebug(MODULE, "Client Port: "+hostPort); 
            
             SocketAddress socketAddress=null;
             if(hostAddress == null || hostAddress.trim().length()==0){
            	 socketAddress = new InetSocketAddress(hostPort);	            	 
                 dSocket = new DatagramSocket(socketAddress);
             }else{
            	 socketAddress = new InetSocketAddress(hostAddress,hostPort);
            	 dSocket = new DatagramSocket(socketAddress);
             }
            
             
        
            Logger.logDebug(MODULE,"Reply Timeout: "+radiusData.getReTimeOut());
            Logger.logDebug(MODULE,"Sending Radius Packet: " + radiusPacket);
            if(dSocket != null){
            	
            dSocket.setSoTimeout(radiusData.getReTimeOut()*1000);
            dSocket.connect(InetAddress.getByName(radiusData.getAdminHost()), radiusData.getAdminPort());
            
            dPacket = new DatagramPacket(radiusPacket.getBytes(), radiusPacket.getBytes().length);
            
            dSocket.send(dPacket);
            
            dPacket  = new DatagramPacket(new byte[4096], 4096);
            dPacket.setData(new byte[4096]);
            dSocket.receive(dPacket);
            
            /* RECEIVED A RESPONSE */
            RadiusPacket radiusResponsePacket = new RadiusPacket();
            radiusResponsePacket.setBytes(dPacket.getData());
            
            byte[] receivedResponseAuthenticator = radiusResponsePacket.getAuthenticator();
            byte[] calculatedResponseAuthenticator = null;
            
            if(radiusPacket.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
                calculatedResponseAuthenticator = RadiusUtility.generateRFC2865ResponseAuthenticator(radiusResponsePacket, requestAuthenticatorForAuthentication, radiusData.getScecretKey());
                
            } else if(radiusPacket.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE) {
                calculatedResponseAuthenticator = RadiusUtility.generateRFC2865ResponseAuthenticator(radiusResponsePacket, requestAuthenticatorForAccounting, radiusData.getScecretKey()); 
            }
            
            if(RadiusUtility.isByteArraySame(receivedResponseAuthenticator, calculatedResponseAuthenticator)){
                Logger.logInfo(MODULE,"VALID Response-Authenticator received in the radius packet.");
            }else{
            	Logger.logInfo(MODULE,"INVALID Response-Authenticator received in the radius packet.");
            }       
        
            Logger.logInfo(MODULE,"Response Packet : "+radiusResponsePacket);
            responseString = radiusResponsePacket.toString();
            
            }
                    	
        }catch(SocketTimeoutException e){
            Logger.logError(MODULE,"Timeout error, reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
            
        }catch(PortUnreachableException e){
            Logger.logError(MODULE,"Port Unreachable error, reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
           
        }catch(SocketException e){
        	Logger.logError(MODULE,"Socket Binding error, reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
        	throw new SocketException();   
        }catch(Exception e){
            Logger.logError(MODULE,"Sending Request Failed, reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
            
        }finally{
        	    if(dSocket != null)
                dSocket.close();
        }
        return responseString;
    }
    
    private boolean isAttributeExist(IRadiusTestData radiusData,RadiusTestParamData radparam) {
		
    	boolean attrExist = false;
    	if(radiusData.getRadParamRel()!=null){
    		for (Iterator<RadiusTestParamData> iter = radiusData.getRadParamRel().iterator();iter.hasNext();) {
    			RadiusTestParamData radParamData = iter.next();
    			if(radParamData.getName().equals(radparam.getName())){
    				attrExist =true;
    			}
    		}
    	}
    	
       return attrExist;
		
	}

	public StringBuffer convertDictionaryDataToByteArray(IDictionaryData dictionaryData) {
        Collection colDictionaryParameterDetailData = null;
        Iterator itLstDicParamList = null;
        DictionaryParameterDetailData dictionaryParameterDetailData = null;
        String strPredefinedValues = null;
        String strFileData = null;
        StringTokenizer stComma = null;
        StringTokenizer stColon = null;
        StringBuffer stringBuffer = new StringBuffer(); 
        
        try {

            stringBuffer.append("<attribute-list vendorid=\"" + dictionaryData.getVendorId() + "\" vendor-name=\"" + dictionaryData.getName() + "\">" + "\n");
            
            colDictionaryParameterDetailData = dictionaryData.getDictionaryParameterDetail();
            if (colDictionaryParameterDetailData != null && colDictionaryParameterDetailData.isEmpty() == false) {
                    List lstDicParamList = Arrays.asList(colDictionaryParameterDetailData.toArray());
                    Collections.sort(lstDicParamList);
                    
                    itLstDicParamList = lstDicParamList.iterator();
                
                    while (itLstDicParamList.hasNext()) {
                        dictionaryParameterDetailData = (DictionaryParameterDetailData) itLstDicParamList.next();
                        
                        if (dictionaryParameterDetailData.getPredefinedValues() != null && dictionaryParameterDetailData.getPredefinedValues().trim().equalsIgnoreCase("") == false) {
                        strFileData ="<attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) + "\" name=\"" + (dictionaryParameterDetailData.getName()) + "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()) + "\">";
                        stringBuffer.append(strFileData + "\n");
                        
                        strPredefinedValues = (String) dictionaryParameterDetailData.getPredefinedValues();
                        stComma = new StringTokenizer(strPredefinedValues, ",");
                        stringBuffer.append("<supported-values>"+ "\n");
                        
                        String valueId = null;
                        String valueName = null;
                        stComma = new StringTokenizer(strPredefinedValues, ",");
                        while (stComma.hasMoreTokens()) {
                            stColon = new StringTokenizer(stComma.nextToken(), ":");
                            valueName = stColon.nextToken();
                            valueId = stColon.nextToken();
                            strFileData = "<value id=\"" + valueId + "\" name=\"" + valueName + "\"/>";
                            stringBuffer.append(strFileData + "\n");
                        }
                        stringBuffer.append("</supported-values>" + "\n");
                        stringBuffer.append("</attribute>" + "\n");
                    } else {
                        strFileData ="<attribute id=\"" + (dictionaryParameterDetailData.getVendorParameterId()) + "\" name=\"" + (dictionaryParameterDetailData.getName()) + "\" type=\""+ (dictionaryParameterDetailData.getDataType().getName()) + "\"/>";
                        stringBuffer.append(strFileData + "\n");
                    }
                }
             }
            stringBuffer.append("</attribute-list>" + "\n");
        }
        catch (Exception e) {
            Logger.logError(MODULE,"Convert Dictionary Data To Byte Array Failed, reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
        }
        return stringBuffer;
    }
}
