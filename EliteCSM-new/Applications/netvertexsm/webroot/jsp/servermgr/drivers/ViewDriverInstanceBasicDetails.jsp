
<%@page import="com.elitecore.netvertexsm.blmanager.servermgr.driver.DriverBLManager"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.driver.ldapdriver.data.LDAPSPRDriverData" %>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility" %> 
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant" %>

<%
	DriverInstanceData driverInstanceData= (DriverInstanceData)request.getAttribute("driverInstanceData");
	if(driverInstanceData == null) {
		DriverBLManager driverBLManager = new DriverBLManager();
		driverInstanceData = new DriverInstanceData();
		driverInstanceData.setDriverInstanceId(Long.parseLong(request.getParameter("id")));
		driverInstanceData = driverBLManager.getDriverInstanceData(driverInstanceData);
	}
%>
<script type="text/javascript">

function show() {
	document.getElementById("head").style.display = "block";
	document.getElementById("<%=driverInstanceData.getDriverTypeId()%>").style.display = "block";			
}
	
</script>

<div class="tblheader-bold">Driver Instance Details</div>
<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
	<!--  bean:define id="driverInstanceBean" name="driverInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" /-->
    
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="driverResources" key="driver.driverinstance.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><%=driverInstanceData.getName() %></td>
          </tr>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.driverinstance.decription" /></td>
            <td class="tblcol" width="70%" height="20%"><%=driverInstanceData.getDescription() %>&nbsp;</td>
          </tr>
      
</table>








