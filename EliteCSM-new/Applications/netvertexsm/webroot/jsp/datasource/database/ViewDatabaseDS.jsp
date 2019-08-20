
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData" %>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility" %> 
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant" %>


<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
    <bean:define id="databaseDSBean" name="databaseDSData" scope="request" type="com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="datasourceResources" key="database.datasource.viewinformation"/></td>
          </tr>
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="datasourceResources" key="database.datasource.datasourcename" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="databaseDSBean" property="name"/></td>
          </tr>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="database.datasource.connectionurl" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="databaseDSBean" property="connectionUrl"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="database.datasource.username" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="databaseDSBean" property="userName"/>&nbsp;</td>
          </tr>  
           
          <!--  <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="database.datasource.timeout" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="databaseDSBean" property="timeout"/>&nbsp;</td>
          </tr> -->
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="database.datasource.statuscheckduration" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="databaseDSBean" property="statusCheckDuration"/>&nbsp;</td>
          </tr> 
            
           
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="database.datasource.minimumPool" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="databaseDSBean" property="minimumPool"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="datasourceResources" key="database.datasource.maximumPool" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="databaseDSBean" property="maximumPool"/>&nbsp;</td>
          </tr> 
          	<tr>
				<td class="tbllabelcol" width="30%" height="20%" ><bean:message key="general.createddate" /></td>
				<td class="tblcol" width="70%" height="20%" ><%=EliteUtility.dateToString(databaseDSBean.getCreatedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>					
			</tr>
			<tr>
				<td class="tbllabelcol" width="30%" height="20%" ><bean:message key="general.lastmodifieddate" /></td>
				<td class="tblcol" width="70%" height="20%" ><%=EliteUtility.dateToString(databaseDSBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;</td>					
			</tr>	   
        </table>
		</td>
    </tr>
</table>

