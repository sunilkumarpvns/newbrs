
<%@ page import="com.elitecore.core.util.mbean.data.live.EliteNetServerDetails" %>
<%@ page import="com.elitecore.core.util.mbean.data.live.EliteNetServiceDetails" %>




<%@ page import="org.apache.struts.util.MessageResources" %>
<% 
	String localBasePath = request.getContextPath();
	EliteNetServerDetails liveDetailsBean = (EliteNetServerDetails)request.getAttribute("eliteLiveServerDetails");
 	Long netServerId = (Long) request.getAttribute("netServerId");
 	String errorCode = (String) request.getAttribute("errorCode");
	
%>


<script>
    function startServer(){    
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.managenetserverlivedetails.startservre"/>';        
        var agree = confirm(msg);
        if(agree){
            javascript:location.href='<%=localBasePath%>/startNetServerInstance.do?netServerId=<%=netServerId%>';  
        }
    }
    
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

   <bean:define id="netServerInstanceBean" name="netServerInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
	        <tr>
			  	<td>&nbsp;</td>
			</tr>
			<tr> 
			  <td class="tblheader-bold" colspan="2"><bean:message bundle="servermgrResources" key="servermgr.reloadPolicy"/></td>
			</tr>
			<tr> 
			  <td align="left" class="labeltext" valign="top"  colspan="2">&nbsp;</td>
			</tr>	
			<tr>
			  <td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceip"/></td>
			  <td align="left" class="labeltext" valign="top" width="32%" ><bean:write name="netServerInstanceBean" property="adminHost"/>
  			  </td>
			</tr>								
			<tr>
			  <td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources" key="servermgr.admininterfaceport"/></td>
			  <td align="left" class="labeltext" valign="top" width="32%" ><bean:write name="netServerInstanceBean" property="adminPort"/>
			  </td>
			</tr>								
        </table>
	  </td>
    </tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
	
	<tr> 
      <td>&nbsp;</td>
    </tr>
   	<tr> 
      <td class="btns-td" valign="middle" >
      	<input type="button" name="c_btnReloadCache"  onclick="javascript:location.href='<%=localBasePath%>/signalServerReloadPolicy.do?netServerId=<%=netServerId %>'"  id="c_btnReloadCache"  value="Reload Policy"  class="light-btn" />                                           
      	 <input type="reset" name="c_btnDeletePolicy" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=localBasePath%>/viewNetServerInstance.do?netserverid=<%=netServerId %>'">  
      </td>
	</tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	