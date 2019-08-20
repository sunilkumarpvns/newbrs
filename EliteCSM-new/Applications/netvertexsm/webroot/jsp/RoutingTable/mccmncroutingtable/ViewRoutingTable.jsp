<%@page import="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData"%>
<bean:define id="routingTableBean" name="routingTableData" scope="request" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData" />

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	 <tr  >
		<td valign="top" align="right"> 
			<table cellpadding="0"valign="top" cellspacing="0" border="0" width="97%" >
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="3"  ><bean:message  bundle="routingMgmtResources" key="routingtable.view.title"/>
				</td>
			</tr>			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="routingMgmtResources" key="routingtable.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="routingTableBean" property="name"/>&nbsp;</td>					
				  </tr>
			   	  <logic:equal name="routingTableBean" property="type" value="MCCMNC">
 					<tr>
 						<td align="left" class="tbllabelcol" valign="top"><bean:message	bundle="routingMgmtResources" key="routingtable.type" /></td>
 						<td align="left" class="tblrows">MCCMNC-BASED</td>
 					</tr>
 					<% RoutingTableData routingTableData =(RoutingTableData)request.getAttribute("routingTableData");
						if(routingTableData.getMccmncGroupData() != null ){ 					
 					%>
 					<tr>
 						<td align="left" class="tbllabelcol" valign="top" width="30%"><bean:message	bundle="routingMgmtResources" key="routingtable.mccmncgroup" /></td>
 						<td align="left" class="tblrows" valign="top" width="70%">
 						<a href="<%=basePath%>/mccmncGroupManagement.do?method=view&mccmncGroupId=<bean:write name="routingTableBean" property="mccmncGroupData.mccmncGroupId"/>">
 						<bean:write name="routingTableBean" property="mccmncGroupData.name" />
 						</a>
 						</td>
 					</tr>
 					<% } %>
 				</logic:equal>
 				<logic:equal name="routingTableBean" property="type" value="REALM">
 					<tr>
 						<td align="left" class="tbllabelcol" valign="top"><bean:message	bundle="routingMgmtResources" key="routingtable.type" /></td>
 						<td align="left" class="tblrows">CUSTOM-REALM-BASED</td>
 					</tr>
 					<tr>
 						<td align="left" class="tbllabelcol" valign="top" width="30%"><bean:message	bundle="routingMgmtResources" key="routingtable.realmcondtion" /></td>
 						<td align="left" class="tblrows" valign="top" width="70%"><bean:write  name="routingTableBean" property="realmCondition" />&nbsp;</td>
 					</tr>
 				</logic:equal>
				    <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="routingMgmtResources" key="routingtable.action" /></td> 
					 <logic:equal name="routingTableBean" property="routingAction" value="0">
							    <td align="left" class="tblrows">LOCAL</td>
					 </logic:equal>
					<logic:equal name="routingTableBean" property="routingAction" value="2">
								 <td align="left" class="tblrows">PROXY</td>
					</logic:equal>
				  </tr>
				    <tr><td>&nbsp;</td></tr>
				    <tr>
					<td colspan="3">
						<% if(request.getAttribute("hideTable")!=null){	%>
						<table align="left"     border="0" 	cellpadding="0" cellspacing="0" width ="100%" style="display: none">
						<% }else{%>
						<table align="left"     border="0" 	cellpadding="0" cellspacing="0" width ="100%" >
						<% }%>
						<tr>
						<td class="tblheader-bold" colspan="4"  ><bean:message bundle="routingMgmtResources" key="routingtable.gateway" /></td>
						</tr>
						<logic:notEmpty    name="routingTableData"  property="routingTableGatewayRelData" >
							<tr>
								<td align="left" class="tblheader" valign="top" width="30%"><bean:message
										bundle="routingMgmtResources" key="routingtable.gateway.name" /></td>
								<td align="left" class="tblheader" valign="top" ><bean:message
										bundle="routingMgmtResources" key="routingtable.gateway.weightage" /></td>
								<td align="left" class="tblheader" valign="top" ><bean:message
										bundle="routingMgmtResources" key="routingtable.gateway.protocol" /></td>
								<td align="left" class="tblheader" valign="top" width="30%"><bean:message
										bundle="routingMgmtResources" key="routingtable.gateway.area" /></td>
							</tr>
								<logic:iterate id="gateway" name="routingTableData"  property="routingTableGatewayRelData" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableGatewayRelData">
									<tr>
										<td align="left" class="tblfirstcol">
										<a href="<%=basePath%>/viewGateway.do?gatewayId=<bean:write name="gateway" property="gatewayId"/>&commProtocolId=<bean:write name='gateway' property='gatewayData.commProtocol'/>">
										<bean:write name="gateway" property="gatewayData.gatewayName" /> &nbsp;
										</a>
										</td>
										<td align="left" class="tblrows"><bean:write name="gateway" property="weightage" />&nbsp;
										</td>
										<td align="left" class="tblrows"><bean:write name="gateway" property="gatewayData.commProtocol" /> &nbsp;</td>
										<td align="left" class="tblrows"><bean:write name="gateway" property="gatewayData.areaName" />&nbsp;</td>		
										</tr>
								</logic:iterate>
						</logic:notEmpty>
						<logic:empty   name="routingTableData"  property="routingTableGatewayRelData" >
						<td colspan="4" class="tblfirstcol" align="center" > <bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/> </td>
						</tr>
						</logic:empty>
				</table>
			</td></tr>
</table></td></tr></table>