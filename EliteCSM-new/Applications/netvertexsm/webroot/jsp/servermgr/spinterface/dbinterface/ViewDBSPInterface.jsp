<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@ page import="com.elitecore.corenetvertex.spr.data.SPRFields" %>
<%

	DatabaseSPInterfaceData databaseSPInterfaceData = null;

	Set<DatabaseSPInterfaceData> dbDriverDataSet = driverInstanceData.getDatabaseSPInterfaceDriverSet();
	if(dbDriverDataSet!=null && !dbDriverDataSet.isEmpty()){
		for(Iterator<DatabaseSPInterfaceData> iterator= dbDriverDataSet.iterator();iterator.hasNext();){
			databaseSPInterfaceData = iterator.next();
		}
	}
	if(databaseSPInterfaceData!=null){
		pageContext.setAttribute("databaseSPInterfaceData",databaseSPInterfaceData);
	}
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >

	<%if(databaseSPInterfaceData!=null){%>
    
    <tr> 
      <td valign="top" align="right"> 
      
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right" >
         <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="spinterface.db.dbdatasource" /></td>
            <td class="tblcol" width="70%" height="20%">
            <a href="<%=basePath%>/viewDatabaseDS.do?databaseId=<bean:write name="databaseSPInterfaceData" property="databaseDs.databaseId"/>">
            <bean:write name="databaseSPInterfaceData" property="databaseDs.name"/>&nbsp;
            </a>
            </td>
          </tr>
         
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="spinterface.db.tablename" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="databaseSPInterfaceData" property="tableName"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="spinterface.db.identityfield" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="databaseSPInterfaceData" property="identityField"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="spinterface.db.dbquerytimeout" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="databaseSPInterfaceData" property="dbQueryTimeout"/>&nbsp;</td>
          </tr> 
         
          <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="spinterface.db.maxquerytimeoutcnt" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="databaseSPInterfaceData" property="maxQueryTimeoutCnt"/>&nbsp;</td>
          </tr>
          <tr>
			<td class="small-gap">&nbsp;</td>
		  </tr>
		  <tr>
			<td class="small-gap">&nbsp;</td>
		  </tr>
          <tr>
          <tr>
          	<td class="tblheader-bold" colspan="2"><bean:message bundle="driverResources" key="spinterface.db.fieldmapping" /></td>
          </tr>
          <tr> 
			<td colspan="2">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<tr>
					    <td class="tblheader-bold"  height="20%"><bean:message bundle="driverResources" key="spinterface.db.logicalname" /></td>
            			<td class="tblheader-bold" height="20%"><bean:message bundle="driverResources" key="spinterface.db.fieldname" /></td>
            		</tr>
					  <%if(databaseSPInterfaceData.getDbFieldMapSet()!=null && !databaseSPInterfaceData.getDbFieldMapSet().isEmpty()){ %>
						<logic:iterate id="dbFieldBean" name="databaseSPInterfaceData" property="dbFieldMapSet" scope="page" type="com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData">					  
				 			<tr> 
				            <td class="tblrows"  height="20%"><%=SPRFields.fromSPRFields(dbFieldBean.getLogicalName()).displayName%>&nbsp;</td>
				            <td class="tblcol"  height="20%"><bean:write name="dbFieldBean" property="dbField" />&nbsp;</td>
				          </tr>
						</logic:iterate>
					  <%}else{%>		
						<tr> 
			            <td class="tblfirstcol"  height="20%" colspan="2">No Record Found</td>
			           </tr>
			          <%}%>
				</table>
			</td>	  
          </tr>
          </table>
	</td>
   </tr>
   <%}%>
</table>
	







