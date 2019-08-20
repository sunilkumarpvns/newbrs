<%@page import="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData"%>
<%
  	RoutingTableData routingTableData=(RoutingTableData)request.getAttribute("routingTableData"); 
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="mccmncGroupBean" name="mccmncGroupData" scope="request" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData" />
	 <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%">
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="3"  ><bean:message  bundle="routingMgmtResources" key="mccmncgroup.view.title"/> </td>
			</tr>			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="routingMgmtResources" key="mccmncgroup.groupname" /></td> 
					<td align="left" class="tblrows" valign="top" colspan="2"> <bean:write name="mccmncGroupBean" property="name"/>&nbsp;</td>					
				  </tr>
			   	 			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="routingMgmtResources" key="mccmncgroup.description" /></td> 
					<td align="left" class="tblrows" valign="top"  colspan="2" > <bean:write name="mccmncGroupBean" property="description"/>&nbsp;</td> 
				  </tr>
	
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="routingMgmtResources" key="network.brand" /></td> 
					<td align="left" class="tblrows" valign="top"  colspan="2"> <bean:write name="mccmncGroupBean" property="brandData.name"/>&nbsp;</td>
			<%-- 	  </tr>
				    <tr> 
					<td align="left" class="tblfirstcol" valign="top" ><bean:message bundle="routingMgmtResources" key="network.brand" /></td> 
					<td align="left" class="tblrows" valign="top"><%=request.getAttribute("brandName")%>&nbsp;</td>
				  </tr> --%>
				    <tr><td class="small-gap">&nbsp;</td></tr><tr><td class="small-gap">&nbsp;</td></tr>
				     <tr>
						<td class="tblheader-bold"  valign="top" colspan="3" ><bean:message bundle="routingMgmtResources" key="routingtable.associations" /></td>
			 		</tr>
			 	<tr> 
					<td align="left" class="tblheaderfirstcol" valign="top" ><bean:message bundle="routingMgmtResources" key="routingtable.name" /></td> 
					<td align="left" class="tblheader" valign="top"  ><bean:message bundle="routingMgmtResources" key="routingtable.type" /></td>
					<td align="left" class="tblheaderlastcol" valign="top" ><bean:message bundle="routingMgmtResources" key="routingtable.realmcondtion"/></td> 
		       </tr>
			   <%if(routingTableData!=null){ %> 
			   <tr>
					<td align="left" class="tblfirstcol" valign="top" >
					<a href="<%=basePath%>/routingTableManagement.do?method=view&routingTableId=<%=routingTableData.getRoutingTableId()%>">
									<%=routingTableData.getName()%>&nbsp;</a>											
					</td>
				    <td align="left" class="tblrows" valign="top" ><%=routingTableData.getType()%></td>
				    <td align="left" class="tblrows" valign="top" ><%=routingTableData.getRoutingAction()%></td>
			   </tr>
			  <%}else{ %>
			  <tr>
	    		<td  align="center" class="tblfirstcol" colspan="3">
	    				<bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/>
	    		</td>
			</tr>
			<%}%>		
			
				    <tr><td class="small-gap">&nbsp;</td></tr><tr><td class="small-gap">&nbsp;</td></tr>
				    <td colspan="3">
						<% if(request.getAttribute("hideTable")!=null){	%>
						<table align="left"     border="0" 	cellpadding="0" cellspacing="0" width ="100%" style="display: none">
						<% }else{%>
						<table align="left"     border="0" 	cellpadding="0" cellspacing="0" width ="100%" >
						<% }%>
						<tr>
						<td class="tblheader-bold" colspan="4"  ><bean:message bundle="routingMgmtResources" key="mccmncgroup.mccmnclist" /></td>
						</tr>
							<tr>
								<td align="left" class="tblheader" valign="top" ><bean:message
										bundle="routingMgmtResources" key="network.networkname" /></td>
								<td align="left" class="tblheader" valign="top" width="35%"><bean:message
										bundle="routingMgmtResources" key="network.operator" /></td>
								<td align="left" class="tblheader" valign="top" ><bean:message
										bundle="routingMgmtResources" key="network.mcc" />-<bean:message
										bundle="routingMgmtResources" key="network.mnc" /></td>
									</tr>
								<logic:iterate id="mccmncCodes" name="mccmncGroupData"  property="mccmncCodeGroupRelDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCCodeGroupRelData">
									<tr >
										<td align="left" class="tblfirstcol">
										<a href="<%=basePath%>/networkManagement.do?method=view&networkID=<bean:write name="mccmncCodes" property="mccMNCID"/>">
										<bean:write name="mccmncCodes" property="mccmncCodeData.networkName" />&nbsp;		
										</a>
										</td>
										<td align="left" class="tblrows">
										<bean:write name="mccmncCodes" property="mccmncCodeData.operatorData.name" /> &nbsp;</td>
										<td align="left" class="tblrows"><bean:write name="mccmncCodes" property="mccmncCodeData.mcc" />-<bean:write name="mccmncCodes" property="mccmncCodeData.mnc" />&nbsp;</td>		
										</tr>
								</logic:iterate>
						</table>
					</td>
			</table>
		</td>
	</tr>
	</tr>
</table>
