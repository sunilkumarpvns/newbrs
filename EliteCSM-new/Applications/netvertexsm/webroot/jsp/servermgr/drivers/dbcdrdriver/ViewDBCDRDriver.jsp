<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRDriverData"%>

<%

	DBCDRDriverData dbCDRDriverData = null;
	Set<DBCDRDriverData> dbcdrDriverDataSet = driverInstanceData.getDbcdrDriverDataSet();
	if(dbcdrDriverDataSet != null && !dbcdrDriverDataSet.isEmpty()){
		for(Iterator<DBCDRDriverData> iterator= dbcdrDriverDataSet.iterator();iterator.hasNext();){
			dbCDRDriverData = iterator.next();
		}
	}
	if(dbCDRDriverData!=null){
		pageContext.setAttribute("dbCDRDriverData",dbCDRDriverData);
	}
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >

	<%if(dbCDRDriverData != null){%>
    
    <tr> 
      <td valign="top" align="right"> 
      
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
        <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="4"  >
				<bean:message bundle="driverResources" key="driver.dbcdrdriver.dbcdrdriverdetails" />
			</td>
		</tr>
         <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.datasource" /></td>
            <td class="tblcol" width="70%" height="20%">
             <a href="<%=basePath%>/viewDatabaseDS.do?databaseId=<bean:write name="dbCDRDriverData" property="dataSourceData.databaseId"/>">
            	<bean:write name="dbCDRDriverData" property="dataSourceData.name"/>&nbsp;
            </a>
            </td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.dbquerytimeout" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="dbQueryTimeout" />&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.querytimeoutcount" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="maxQueryTimeoutCount" />&nbsp;</td>
          </tr>
           <tr>
			<td align="left" class="tblheader-bold" valign="top"  colspan="2"  >
				<bean:message bundle="driverResources" key="driver.dbcdrdriver.cdrdetails"/>
			</td>
		</tr>
		    <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.tablename" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="tableName" />&nbsp;</td>
          </tr>
         <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.identityfield" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="identityField" />&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.sequencename" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="sequenceName" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.storeallcdr" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="storeAllCDR" />&nbsp;</td>
          </tr>          
			<tr>
				<td align="left" class="tblheader-bold" valign="top" colspan="2"  >
				<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdate"/></td>
			</tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdate" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="isBatchUpdate" />&nbsp;</td>
          </tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.batchsize" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="batchSize" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdateinterval" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="batchUpdateInterval" />&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdatequerytimeout" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="queryTimeout" />&nbsp;</td>
          </tr>
          <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2"  >
			<bean:message bundle="driverResources" key="driver.dbcdrdriver.mandatoryfielddetails"/></td>
		  </tr>
      	  <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.sessionidfieldname" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="sessionIDFieldName" />&nbsp;</td>
          </tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.createdatefieldname" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="createDateFieldName" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.lastmodifiedfieldname" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="lastModifiedFieldName" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.timestampfieldname" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="timeStampformat" />&nbsp;</td>
          </tr>
          <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2"  >
			<bean:message bundle="driverResources" key="driver.csvdriver.usagefields"/></td>
		  </tr>
      	  <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.reportingtype" /></td>
            <td class="tblcol"  height="20%">
            	<%if(dbCDRDriverData.getReportingType().equalsIgnoreCase("QR")){%>
            		Quota Reporting&nbsp;
            	<%}else if(dbCDRDriverData.getReportingType().equalsIgnoreCase("UM")){%>
            		Usage Monitoring&nbsp;
            	<%}else{%>
            		&nbsp;
            	<%}%>
            </td>
          </tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.inputoctetsheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="inputOctetsFieldName" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.outputoctetsheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="outputOctetsFieldName" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.totalOctetsheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="totalOctetsFieldName" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.usageTimeheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="usageTimeFieldName" />&nbsp;</td>
          </tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.usagekeyheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="dbCDRDriverData" property="usageKeyFieldName" />&nbsp;</td>
          </tr>         
		  <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2" >
			 <bean:message bundle="driverResources" key="driver.dbcdrdriver.mappingdetails"/></td>
		  </tr>
		  <tr> 
			<td colspan="2">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"   align="right" >
					<tr>
					    <td class="tblheaderfirstcol "  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.pcrfkey" /></td>
            			<td class="tblheader-bold" height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.dbfield" /></td>
            			<td class="tblheader-bold" height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.datatype" /></td>
            			<td class="tblheader-bold" height="20%"><bean:message bundle="driverResources" key="driver.dbcdrdriver.defvalue" /></td>
            		</tr>
					  <%if(dbCDRDriverData.getDbcdrDriverFieldMappingDataSet()!=null && !dbCDRDriverData.getDbcdrDriverFieldMappingDataSet().isEmpty()){ %>
						<logic:iterate id="dbCDRFieldBean" name="dbCDRDriverData" property="dbcdrDriverFieldMappingDataSet" scope="page" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRFieldMappingData">					  
				 		  <tr> 
				            <td class="tblfirstcol" width="25%" height="20%"><bean:write name="dbCDRFieldBean" property="pcrfKey" />&nbsp;</td>
				            <td class="tblrows" width="25%" height="20%"><bean:write name="dbCDRFieldBean" property="dbField" />&nbsp;</td>
				            <td class="tblrows" width="25%" height="20%">
				            	<%if(dbCDRFieldBean.getDataType() != null && dbCDRFieldBean.getDataType() == 4){%>
				            		<bean:message bundle="driverResources" key="driver.dbcdrdriver.datatype.date" />
				            	<%}else if(dbCDRFieldBean.getDataType() != null && dbCDRFieldBean.getDataType() == 5){%>
				            		<bean:message bundle="driverResources" key="driver.dbcdrdriver.datatype.string" />
				            	<%}else{%>
				            		&nbsp;
				            	<%}%>
				            </td>
				            <td class="tblrows" width="25%" height="20%"><bean:write name="dbCDRFieldBean" property="defaultValue" />&nbsp;</td>
				          </tr>
						</logic:iterate>
					  <%}else{%>		
						<tr> 
			            <td class="tbllabelcol"  height="20%" colspan="2">No Record Found</td>
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