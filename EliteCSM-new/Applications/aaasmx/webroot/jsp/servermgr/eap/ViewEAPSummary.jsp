<%@ page import="java.util.*" %>





<bean:define id="eapConfigBean" name="eapConfigData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData" />
<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
	 
	<tr>
		<td align="right" valign="top"> 
			<table cellpadding="0" cellspacing="0" border="0" width="100%" >

				<tr> 
		            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.viewbasicdetails"/></td>
	            </tr>
         
               <tr> 
	            <td class="tblfirstcol" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessioncleanupinterval" /></td>
	            <td class="tblcol" width="20%" height="20%"><bean:write name="eapConfigBean" property="sessionCleanupInterval"/>&nbsp;</td>
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessiondurationforcleanup" /></td>
	            <td class="tblcol" width="30%" height="20%"><bean:write name="eapConfigBean" property="sessionDurationForCleanup"/>&nbsp;</td>
	          </tr>
	          
	          <tr>
	           <td class="tblfirstcol" valign="top" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.sessiontimeout" />&nbsp;</td>
	           <td class="tblcol" width="20%" height="20%"><bean:write name="eapConfigBean" property="sessionTimeout"/>&nbsp;</td>
	           <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.treatinvalidpacketasfatal" />&nbsp;</td>
	           <td class="tblcol" width="30%" height="20%"><bean:write name="eapConfigBean" property="treatInvalidPacketAsFatal"/>&nbsp;</td>
              </tr>
              
               <tr>
	           <td class="tblfirstcol" valign="top" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.mskrevalidationtime" />&nbsp;(secs)</td>
	           <td class="tblcol" width="20%" height="20%"><bean:write name="eapConfigBean" property="mskRevalidationTime"/>&nbsp;</td>
	           <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.maxeappacketsize" />&nbsp;</td>
	           <td class="tblcol" width="30%" height="20%"><bean:write name="eapConfigBean" property="maxEapPacketSize"/>&nbsp;</td>
              </tr>
              
              <tr>
	           <td class="tblfirstcol" valign="top" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.notificationsuccess" />&nbsp;</td>
	           <td class="tblcol" width="20%" height="20%"><bean:write name="eapConfigBean" property="notificationSuccess"/>&nbsp;</td>
	           <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.notificationfailure" />&nbsp;</td>
	           <td class="tblcol" width="30%" height="30%"><bean:write name="eapConfigBean" property="notificationFailure"/>&nbsp;</td>
              </tr> 
                				
			</table>
		</td>
	</tr>
</table>