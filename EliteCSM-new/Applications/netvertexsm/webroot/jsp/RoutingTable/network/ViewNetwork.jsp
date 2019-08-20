<%@page import="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData"%>
<%
	request.getContextPath();
    MCCMNCGroupData mccmncGroupData=(MCCMNCGroupData)request.getAttribute("mccmncGroupData");
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="networkBean" name="networkData" scope="request" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData" />
 	
    <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="97%">
			<tr>
				<td class="tblheader-bold"  valign="top" colspan="3" ><bean:message  bundle="routingMgmtResources" key="network.view.title"/> </td>
			</tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%"><bean:message bundle="routingMgmtResources" key="network.brand" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%">
						<logic:notEmpty name="networkBean" property="brandData"> 
							<bean:write name="networkBean" property="brandData.name"/>
						</logic:notEmpty>
						<logic:empty name="networkBean" property="brandData">-</logic:empty>						
						&nbsp;
					</td> 
				  </tr>
				  
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top"  ><bean:message bundle="routingMgmtResources" key="network.operator" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> 
						<logic:notEmpty name="networkBean" property="operatorData">
							<bean:write name="networkBean" property="operatorData.name"/>
						</logic:notEmpty>
						<logic:empty name="networkBean" property="operatorData">-</logic:empty>
						&nbsp;
					</td> 
					
				  </tr>
			   	 			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="routingMgmtResources" key="network.networkname" /></td> 
					<td align="left" class="tblrows" valign="top" > 
						<bean:write name="networkBean" property="networkName"/>&nbsp;						
					</td> 
				  </tr>
				  
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="routingMgmtResources" key="network.technology" /></td> 
					<td align="left" class="tblrows" valign="top" >						
						<bean:write name="networkBean" property="technology"/>
						<logic:empty name="networkBean" property="technology">-</logic:empty>
					</td> 
				  </tr>
	
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="routingMgmtResources" key="network.country" /></td> 
					<td align="left" class="tblrows" valign="top"> 
						<logic:notEmpty name="networkBean" property="countryData">
							<bean:write name="networkBean" property="countryData.name"/>
						</logic:notEmpty>
						<logic:empty name="networkBean" property="countryData">-</logic:empty>
						&nbsp;
					</td>
				  </tr>
				  
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="routingMgmtResources" key="network.mcc" /></td> 
					<td align="left" class="tblrows" valign="top"> 				
						<logic:notEqual name="networkBean" property="mcc" value="0" >
							<bean:write name="networkBean" property="mcc" />
						</logic:notEqual>
						<logic:equal name="networkBean" property="mcc" value="0" >-</logic:equal>
					</td> 
				  </tr>
				  	
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" ><bean:message bundle="routingMgmtResources" key="network.mnc" /></td> 
					<td align="left" class="tblrows" valign="top"> 
						<logic:notEqual name="networkBean" property="mnc" value="0" >
							<bean:write name="networkBean" property="mnc" />
						</logic:notEqual>
						<logic:equal name="networkBean" property="mnc" value="0" >-</logic:equal>
					</td>  
				  </tr>
		      <tr><td class="small-gap">&nbsp;</td></tr><tr><td class="small-gap">&nbsp;</td></tr>
		      <tr><td class="small-gap">&nbsp;</td></tr><tr><td class="small-gap">&nbsp;</td></tr>
		     <tr>
				<td class="tblheader-bold"  valign="top" colspan="3" ><bean:message bundle="routingMgmtResources" key="mccmncgroup.associations" /></td>
			 </tr>
			 	<tr> 
					<td align="left" class="tblheaderfirstcol" valign="top" ><bean:message bundle="routingMgmtResources" key="mccmncgroup.groupname" /></td> 
					<td align="left" class="tblheaderlastcol" valign="top" ><bean:message bundle="routingMgmtResources" key="mccmncgroup.description" /></td> 
		       </tr>
			   <%if(mccmncGroupData!=null){ %> 
			   <tr>
					<td align="left" class="tblfirstcol" valign="top" >
					<a href="<%=basePath%>/mccmncGroupManagement.do?method=view&mccmncGroupId=<%=mccmncGroupData.getMccmncGroupId()%>">
									<%=mccmncGroupData.getName()%>&nbsp;</a>											
					</td>
						 	<td align="left" class="tblrows" valign="top" ><%=mccmncGroupData.getDescription()%> </td>
			   </tr>
			  <%}else{ %>
			  <tr>
	    		<td  align="center" class="tblfirstcol" colspan="3">
	    				<bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/>
	    		</td>
			</tr>
			<%}%>		
			</table>
		</td>
	</tr>
</table>