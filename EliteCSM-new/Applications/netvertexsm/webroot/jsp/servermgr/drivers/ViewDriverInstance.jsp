<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%
    DriverInstanceData driverInstanceData= (DriverInstanceData)request.getAttribute("driverInstanceData");
	long driverTypeId=driverInstanceData.getDriverTypeId();
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
	<bean:define id="driverInstanceBean" name="driverInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" />
   
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right" >
           <tr>
			<td class="tblheader-bold" valign="top" colspan="2">
			<bean:message bundle="driverResources" key="driver.driverinstance.information" /></td>
		 </tr>
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="driverResources" key="driver.driverinstance.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="driverInstanceBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.driverinstance.decription" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="description"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.driverinstance.drivertype" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="driverTypeData.name"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message key="general.createddate" /></td>
            <td class="tblcol" width="70%" height="20%"><%= EliteUtility.dateToString(driverInstanceBean.getCreateDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%> &nbsp;</td>
          </tr>  
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message key="general.lastmodifieddate" /></td>
            <td class="tblcol" width="70%" height="20%"><%= EliteUtility.dateToString(driverInstanceBean.getLastModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%> &nbsp;</td>
          </tr>
	   	 </table>
		</td>
    </tr>
</table>
