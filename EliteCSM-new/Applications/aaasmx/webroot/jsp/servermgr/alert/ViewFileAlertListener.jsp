<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@page import="com.elitecore.elitesm.web.servermgr.alert.forms.ViewAlertListenerForm"%>






<%
	ViewAlertListenerForm viewAlertListenerForm = (ViewAlertListenerForm) request.getAttribute("viewAlertListenerForm");
%>


<table width="100%" border="0" cellspacing="0" cellpadding="0"  >
   
    <tr> 
      <td valign="top" align="right" height="15%" > 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.viewalertlistener" /></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="alertListenerData" property="name"/></td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.listenerType" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="alertListenertypeData" property="typeName"/></td>
          </tr>
           
          <tr> 
            <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.filename" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="alertFileListenerData" property="fileName"/>&nbsp;</td>
          </tr>
     
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.rollingtype" /></td>
            <td class="tblcol" width="70%" height="20%">
          
            <logic:equal name="alertFileListenerData" property="rollingType" value="1">
            <bean:message bundle="servermgrResources" key="servermgr.alert.timebased" />
            </logic:equal>
          
            <logic:equal name="alertFileListenerData" property="rollingType" value="2">
            <bean:message bundle="servermgrResources" key="servermgr.alert.sizebased" />
            </logic:equal>
          
            &nbsp;</td>
          </tr>
          
          <tr> 
            
            <td class="tblfirstcol" width="30%" height="20%">
             
            <logic:equal name="alertFileListenerData" property="rollingType" value="1">
            <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit" />
            </logic:equal>
            <logic:equal name="alertFileListenerData" property="rollingType" value="2">
            <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunitkbs" />
            </logic:equal>
            
            </td>
            
            <td class="tblcol" width="70%" height="20%">
            
            <logic:equal name="alertFileListenerData" property="rollingType" value="1">
              
                <logic:equal name="alertFileListenerData" property="rollingUnit" value="3">
                  <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.minute" />
                </logic:equal>
                
                <logic:equal name="alertFileListenerData" property="rollingUnit" value="4">
                  <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.hour" />
                </logic:equal>
                
                <logic:equal name="alertFileListenerData" property="rollingUnit" value="5">
                  <bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.daily" />
                </logic:equal>
                
            </logic:equal>
            
            
            <logic:equal name="alertFileListenerData" property="rollingType" value="2">
            <bean:write name="alertFileListenerData" property="rollingUnit" />
            </logic:equal>
            
            &nbsp;</td>
          </tr>
          
          <logic:equal name="alertFileListenerData" property="rollingType" value="2">
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.maxrollingunit" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="alertFileListenerData" property="maxRollingUnit" />&nbsp;</td>
          </tr>
          
          
          </logic:equal>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.comprolledunit" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="alertFileListenerData" property="compRollingUnit" />&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.alert.repeatedmessagereduction" /></td>
            <td class="tblcol" width="70%" height="20%">
            	<logic:equal name="alertFileListenerData" property="repeatedMessageReduction" value="true">
            		<bean:message bundle="servermgrResources" key="servermgr.alert.repeatedmessagereduction.enabled" />
            	</logic:equal>
            	<logic:equal name="alertFileListenerData" property="repeatedMessageReduction" value="false">
            		<bean:message bundle="servermgrResources" key="servermgr.alert.repeatedmessagereduction.disabled" />
            	</logic:equal>
            	&nbsp;
            </td>
          </tr>
        </table>
		</td>
    </tr>
</table>
