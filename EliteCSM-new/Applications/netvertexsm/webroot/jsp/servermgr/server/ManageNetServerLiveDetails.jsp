<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ViewNetServerLiveDetailsForm" %>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.core.util.mbean.data.live.EliteNetServerDetails" %>
<%@ page import="com.elitecore.core.util.mbean.data.live.EliteNetServiceDetails" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant" %>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility" %>
<%@ page import="com.elitecore.core.services.data.LiveServiceSummary" %>
<%@ page import="com.elitecore.core.commons.util.Logger" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.elitecore.core.commons.scheduler.EliteTaskData" %>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility" %>







<%
	String basePath = request.getContextPath();
 EliteNetServerDetails liveDetailsBean = (EliteNetServerDetails)request.getAttribute("eliteLiveServerDetails");
 String netServerId = (String) request.getAttribute("netServerId");
 String errorCode = (String) request.getAttribute("errorCode");

%>

<script>
    function validateSynchronize(){
        document.forms[0].submit();
    }
    function stopServer(){    
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.viewnetserverlivedetailsjsp.stopserver"/>';        
        //msg = "Are your sure you want to send Signal for stopping the Server ? ";
        var agree = confirm(msg);
        if(agree){
            javascript:location.href='<%=basePath%>/signalServerShutDown.do?netServerId=<%=netServerId%>';  
        }
    }
    
     function startServer(){    
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.managenetserverlivedetails.startservre"/>';        
        //msg = "Are your sure you want to send Signal for stopping the Server ? ";
        var agree = confirm(msg);
        if(agree){
            javascript:location.href='<%=basePath%>/startNetServerInstance.do?netServerId=<%=netServerId%>';  
        }
    }
    
</script>


<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">

    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
                <tr>
                    <td>&nbsp;</td>
                </tr>
               <tr> 
					<td class="tblheader-bold" colspan="4"><bean:message bundle="servermgrResources" key="servermgr.liveserverdetails"/></td>
				</tr>
             
             <% if(errorCode.equalsIgnoreCase("0")){  %>
				<tr>
					<td colspan="1" align="left" class="tblfirstcol" valign="top"><bean:message bundle="servermgrResources" key="servermgr.servername"/></td>
					<td colspan="3" align="left" class="tblcol" valign="top"><bean:write name="eliteLiveServerDetails" property="name"/>&nbsp;</td>
				</tr>								
				<tr>
					<td align="left" class="tblfirstcol" valign="top" width="20%" ><bean:message bundle="servermgrResources" key="servermgr.serveridentification"/></td>
					<td align="left" class="tblcol" valign="top" width="30%"><bean:write name="eliteLiveServerDetails" property="identification"/>&nbsp;</td>
					<td align="left" class="tblfirstcol" valign="top" width="20%"><bean:message bundle="servermgrResources" key="servermgr.serverversion"/></td>
					<td align="left" class="tblcol" valign="top" width="30%"><bean:write name="eliteLiveServerDetails" property="version"/>&nbsp;</td>
				</tr>								
                <tr>
                    <td align="left" class="tblfirstcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.server.serverstartupdatetime"/></td>
                    <td align="left" class="tblcol" valign="top" ><%=EliteUtility.dateToString(liveDetailsBean.getServerStartUpTime(), ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT))%>&nbsp;</td>
                    <td align="left" class="tblfirstcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.server.softstartdatetime"/></td>
                    <td align="left" class="tblcol" valign="top" ><%=EliteUtility.dateToString(liveDetailsBean.getSoftRestartTime(), ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT))%>&nbsp;</td>
                </tr>                               
                <tr>
                    <td align="left" class="tblfirstcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.server.cachereloaddatetime"/></td>
                    <td align="left" class="tblcol" valign="top" ><%=EliteUtility.dateToString(liveDetailsBean.getCacheReloadTime(), ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT))%>&nbsp;</td>
                    <td align="left" class="tblfirstcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.server.serverreloaddatetime"/></td>
                    <td align="left" class="tblcol" valign="top" ><%=EliteUtility.dateToString(liveDetailsBean.getServerReloadTime(), ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT))%>&nbsp;</td>
                </tr>                               
              <%} else{ %>  
                <tr>
                    <td class="blue-text-bold">
                            <bean:message bundle="servermgrResources" key="servermgr.connectionfailure"/><br>
                            <bean:message bundle="servermgrResources" key="servermgr.admininterfaceip"/> : <bean:write name="netServerInstanceData" property="adminHost"/><br>
                            <bean:message bundle="servermgrResources" key="servermgr.admininterfaceport"/> : <bean:write name="netServerInstanceData" property="adminPort"/> &nbsp;                          
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                </tr>
           
	<%} %>
				<tr>
       				<td >&nbsp;</td>
       				
       			</tr>
 		<tr> 
 		<td valign="middle" colspan="5">
	        	<%if(!errorCode.equalsIgnoreCase("0")){%>
	        	<input type="button" name="c_btnStartup"  onclick="startServer()" id="c_btnStart"  value="Start Server"  class="light-btn" />                       
            	<%} %>
            	 <% if(errorCode.equalsIgnoreCase("0")){ %>
            	<input type="button" name="c_btnShutdown"  onclick="stopServer()" id="c_btnShutdown" value="Stop Server"  class="light-btn" />                       
				<input type="button" name="c_btnSoftRestart" onclick="javascript:location.href='<%=basePath%>/signalSoftRestart.do?netServerId=<%=netServerId %>'" id="c_softRestart" value="Soft Restart" class="light-btn"/>
	            <input type="button" name="c_btnReloadConfig"  onclick="javascript:location.href='<%=basePath%>/signalServerReloadConfiguration.do?netServerId=<%=netServerId %>'"  id="c_btnReloadConfig"  value="Reload Configuration"  class="light-btn" />                               
	            <input type="button" name="c_btnReloadCache"  onclick="javascript:location.href='<%=basePath%>/signalServerReloadCache.do?netServerId=<%=netServerId %>'"  id="c_btnReloadCache"  value="Reload Cache"  class="light-btn" />
	            <input type="button" name="c_btnReloadPolicy"  onclick="javascript:location.href='<%=basePath%>/signalServerReloadPolicy.do?netServerId=<%=netServerId %>'"  id="c_btnReloadCache"  value="Reload Policy"  class="light-btn" />                                           
	            <%} %>
          	<input type="reset" name="c_btnDeletePolicy" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/viewNetServerInstance.do?netserverid=<%=netServerId %>'" /> 
        </td>
	</tr>
	
   	</table>    
	<tr> 
      <td  >&nbsp;</td>
    </tr>

</table>

