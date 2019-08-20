<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData" %>
<%@ page import="java.util.List" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<% 
	String localBasePath = request.getContextPath();
%>

<script>
	function validateSynchronize(){
		document.forms[0].submit();
	}
</script>


<html:form action="/updateNetServiceSynchronizeBackConfigDetail">
<html:hidden name="updateNetServiceSynchronizeBackConfigDetailForm" styleId="action" property="action" />
<html:hidden name="updateNetServiceSynchronizeBackConfigDetailForm" styleId="netServerId" property="netServerId"/>
<html:hidden name="updateNetServiceSynchronizeBackConfigDetailForm" styleId="netServiceId" property="netServiceId"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="netServerInstanceBean" name="netServerInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData" />
   <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
			<tr> 
			  <td class="tblheader-bold" colspan="2"><bean:message bundle="servermgrResources" key="servermgr.synchronizebackservicedetails"/></td>
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
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
		  <tr>         
      		<td class="small-text-grey">Note : All Drivers and its Details within this Service Instance along with it's configuration would be Updated.</td>
      	  </tr>
      	</table>
      </td>
    </tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
   	<tr> 
      <td class="btns-td" valign="middle"  >
      	 <input type="button" name="c_btnSynchronize"  onclick="javascript:location.href='<%=localBasePath%>/synchronizeBackNetServiceConfig.do?netServiceId=<bean:write name="netServiceInstanceBean" property="netServiceId"/>'"  id="c_btnSynchronize"  value=" Synchronize "  class="light-btn">                   
         <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/viewNetServiceInstance.do?netserviceid=<bean:write name="netServiceInstanceBean" property="netServiceId"/>'" value="Cancel" class="light-btn"> 
      </td>
	</tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>
</html:form>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
