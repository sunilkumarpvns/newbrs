<%@page import="com.elitecore.commons.base.Strings"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData"%>

<%
	SPRForm formView = (SPRForm)request.getAttribute("sprForm");
	StringBuilder stringBuilder = new StringBuilder();
	for(String string : formView.getGroupNameList()){
		if((stringBuilder.length()==0) == false){
			stringBuilder.append(", ");
		}
		stringBuilder.append(string);
	}
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="sprBean" name="sprData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData" />
	 <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%">
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="3"  ><bean:message  bundle="sprResources" key="spr.view"/> </td>
			</tr>			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="sprResources" key="spr.name" /></td> 
					<td align="left" class="tblrows" valign="top" colspan="2"> <bean:write name="sprBean" property="sprName"/>&nbsp;</td>					
				  </tr>
			   	 			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="sprResources" key="spr.description" /></td> 
					<td align="left" class="tblrows" valign="top"  colspan="2" > <bean:write name="sprBean" property="description"/>&nbsp;</td> 
				  </tr>
	
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="sprResources" key="spr.datasource" /></td> 
					<td align="left" class="tblrows" valign="top"  colspan="2">
					<a href="<%=basePath%>/viewDatabaseDS.do?databaseId=<bean:write name="sprBean" property="databaseDSData.databaseId"/>">
					 <bean:write name="sprBean" property="databaseDSData.name"/>&nbsp;</td>
				  </tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="sprResources" key="spr.alternate.id.field" /></td> 
					<td align="left" class="tblrows" valign="top"  colspan="2">
						<bean:write name="sprBean" property="alternateIdField"/>&nbsp;</td>
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="sprResources" key="spr.groups" /></td> 
					<td align="left" class="tblrows" valign="top"  colspan="2">
					 <bean:write name="sprBean" property="groupNames"/>&nbsp;
					</td>
				  </tr>
				  
				    <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="sprResources" key="spr.spinterface" /></td> 
					<logic:notEmpty name="sprBean" property="driverInstanceData">
						<td align="left" class="tblrows" valign="top"  colspan="2"> 
							<a href="<%=basePath%>/viewSPInterface.do?driverInstanceId=<bean:write name="sprBean" property="driverInstanceData.driverInstanceId"/>">
							<bean:write name="sprBean" property="driverInstanceData.name"/>&nbsp;</td>
					</logic:notEmpty >
					<logic:empty name="sprBean" property="driverInstanceData">
						<td align="left" class="tblrows" valign="top"  colspan="2"> --&nbsp;</td>
					</logic:empty>
				  </tr>
				  
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="sprResources" key="spr.batchsize" /></td> 
					<td align="left" class="tblrows" valign="top"  colspan="2" > <bean:write name="sprBean" property="batchSize"/>&nbsp;</td> 
				  </tr>
				  
				    <tr><td class="small-gap">&nbsp;</td></tr><tr><td class="small-gap">&nbsp;</td></tr>
				  
			 	
			</table></td>
	</tr>
	</tr>
</table>
