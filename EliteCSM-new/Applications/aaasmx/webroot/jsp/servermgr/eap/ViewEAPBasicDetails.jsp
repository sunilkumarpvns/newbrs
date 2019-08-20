<%@page import="com.elitecore.elitesm.util.eapconfig.EAPConfigUtils"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData"%>
<%@ page import="java.util.*" %>

<%
EAPConfigData eapconf = (EAPConfigData)request.getAttribute("eapConfigData");
String strEnabledAuthMethods="";
if(eapconf!=null){
	strEnabledAuthMethods = EAPConfigUtils.convertEnableAuthMethodToLabelString(eapconf.getEnabledAuthMethods());
}
%>



 
<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
	<bean:define id="eapConfigBean" name="eapConfigData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData" /> 
				<tr> 
		            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.viewinformation"/></td>
	            </tr>
	            <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message  bundle="servermgrResources" key="servermgr.eapconfig.name" /></td>
	            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="eapConfigBean" property="name"/>&nbsp;</td>
	            
	           </tr>
	           <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message  bundle="servermgrResources" key="servermgr.eapconfig.description" /></td>
	            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="eapConfigBean" property="description"/>&nbsp;</td>
	           </tr>
         
               <tr> 
	            <td class="tblfirstcol" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessioncleanupinterval" />(secs)</td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="eapConfigBean" property="sessionCleanupInterval"/>&nbsp;</td>
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessiondurationforcleanup" />(secs)</td>
	            <td class="tblcol" width="30%" height="20%"><bean:write name="eapConfigBean" property="sessionDurationForCleanup"/>&nbsp;</td>
	          </tr>
	          
	          <tr>
	           <td class="tblfirstcol" valign="top" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessiontimeout" />&nbsp;(secs)</td>
	           <td class="tblcol" width="20%" height="20%"><bean:write name="eapConfigBean" property="sessionTimeout"/>&nbsp;</td>
	           <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.treatinvalidpacketasfatal" />&nbsp;</td>
	           <td class="tblcol" width="30%" height="20%"><bean:write name="eapConfigBean" property="treatInvalidPacketAsFatal"/>&nbsp;</td>
              </tr>
              
              <tr>
	           <td class="tblfirstcol" valign="top" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.mskrevalidationtime" />&nbsp;(secs)</td>
	           <td class="tblcol" width="20%" height="20%"><bean:write name="eapConfigBean" property="mskRevalidationTime"/>&nbsp;</td>
	           <td class="tblfirstcol" valign="top" width="30%" height="20%">&nbsp;</td>
	           <td class="tblcol" width="30%" height="20%">&nbsp;</td>
              </tr>
              
              <tr>
	           <td class="tblfirstcol" valign="top" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.defaultnegiotationmethod" />&nbsp;</td>
	           <td class="tblcol" width="20%" height="20%"><bean:write name="labelDefaultNegotiationMethod"/>&nbsp;</td>
	           <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.maxeappacketsize" />&nbsp;</td>
	           <td class="tblcol" width="30%" height="20%"><bean:write name="eapConfigBean" property="maxEapPacketSize"/>&nbsp;</td>
              </tr>   	
              <tr>
	           <td class="tblfirstcol" valign="top" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.notificationsuccess" />&nbsp;</td>
	           <td class="tblcol" width="20%" height="20%"><bean:write name="eapConfigBean" property="notificationSuccess"/>&nbsp;</td>
	           <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.notificationfailure" />&nbsp;</td>
	           <td class="tblcol" width="30%" height="30%"><bean:write name="eapConfigBean" property="notificationFailure"/>&nbsp;</td>
              </tr> 
              
              <tr>
	           <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.enabledauthmethod" />&nbsp;</td>
	           <td class="tblcol" width="70%" height="20%" colspan="3"><%=strEnabledAuthMethods%>&nbsp;</td>
              </tr>   				
	</table>