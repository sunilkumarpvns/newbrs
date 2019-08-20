<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.PolicyPluginConstants"%>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="ccPolicyInstDataBean" name="ccPolicyData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData" />
	<tr>

		<td valign="top" align="right">
			<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
				<tr>
					<td class="tblheader-bold" colspan="2" height="20%">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary" />
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="25.5%" height="20%">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.name" />
					</td>
					<td class="tblcol" width="74.5%" height="20%">
						<bean:write name="ccPolicyInstDataBean" property="name" />
					</td>
				</tr>
			 	<tr>
					<td class="tblfirstcol" width="25.5%" height="20%">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.desp" />
					</td>
					<td class="tblcol" width="74.5%" height="20%">
						<bean:write name="ccPolicyInstDataBean" property="description" />&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="25.5%" height="20%">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.ruleset" />
					</td>
					<td class="tblcol" width="74.5%" height="20%">
						<bean:write name="ccPolicyInstDataBean" property="ruleSet" />
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="25.5%" height="20%">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.ccpolicy.sessionmanagement" />
					</td>
					<td class="tblcol" width="70%" height="20%">
						<logic:equal value="true" name="ccPolicyInstDataBean" property="sessionManagement" >
							True
						</logic:equal>
						<logic:equal value="false" name="ccPolicyInstDataBean" property="sessionManagement" >
							False
						</logic:equal>
						&nbsp;
					</td>
				</tr>
				<%if(("view").equals(request.getAttribute("pageAction"))){ %>
				<tr>
					<td class="tblfirstcol" width="30%" height="20%">
						Response Attributes
					</td>
					<td class="tblcol" width="74.5%" height="20%">
						<table width="100%" cellpadding="0" cellspacing="0">
							<tr>
								<td class="tblheader" width="15%">Commanad Code</td>
								<td class="tblheader" width="40%">Response Attribute</td> 
							</tr>
							
							<logic:notEmpty name="ccPolicyInstDataBean" property="ccResponseAttributesSet">
								<logic:iterate id="obj" name="ccPolicyInstDataBean" property="ccResponseAttributesSet">
								<tr>
									<td class="tblrows"><bean:write name="obj" property="commandCodes"/>&nbsp;</td>
									<td class="tblrows"><bean:write name="obj" property="responseAttributes"/>&nbsp;</td>
								</tr>	
								</logic:iterate>
							</logic:notEmpty>
							
							<logic:empty name="ccPolicyInstDataBean" property="ccResponseAttributesSet">
								<tr>
									<td class="tblrows" colspan="3">No Response Attributes Configured</td>
								</tr>
							</logic:empty>
							
						</table>
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="25.5%" height="20%">
						Driver Group
					</td>
					<td class="tblcol" width="74.5%" height="20%">
						<table width="100%" cellpadding="0" cellspacing="0">
							<tr>
								<td class="tblheader" width="15%">DriverName</td>
								<td class="tblheader" width="40%">Driver Description</td> 
								<td class="tblheader" width="35%">Driver Type</td>
								<td class="tblheader" width="10%">Weightage</td>
							</tr>
							
							<logic:notEmpty name="ccPolicyInstDataBean" property="driverList">
								<logic:iterate id="obj" name="ccPolicyInstDataBean" property="driverList">
								<tr>
									<td class="tblrows">
										<logic:notEmpty name="obj" property="driverData.name">
										    <span class="view-details-css" onclick="openViewDetails(this,'<bean:write name="obj" property="driverData.driverInstanceId"/>','<bean:write name="obj" property="driverData.name"/>','DRIVERS');">
										    	<bean:write name="obj" property="driverData.name"/>
										    </span>
										</logic:notEmpty>&nbsp;
									</td>
									<td class="tblrows"><bean:write name="obj" property="driverData.description"/>&nbsp;</td>
									<td class="tblrows">
										<logic:notEmpty name="obj" property="driverData.driverTypeData">
											<bean:write name="obj" property="driverData.driverTypeData.displayName"/>
										</logic:notEmpty>	
										&nbsp;
									</td> 
									<td class="tblrows"><bean:write name="obj" property="weightage"/>&nbsp;</td>
								</tr>	
								</logic:iterate>
							</logic:notEmpty>
							
							<logic:empty name="ccPolicyInstDataBean" property="driverList">
								<tr>
									<td class="tblrows" colspan="3">No driver Configured</td>
								</tr>
							</logic:empty>
							
						</table>
					</td>
				</tr>
				<tr>
					<td class="tblfirstcol" width="25.5%" height="20%">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.script" />
					</td>
					<td class="tblcol" width="74.5%" height="20%">
						<bean:write name="ccPolicyInstDataBean" property="script" />&nbsp;
					</td>
				</tr>
				
				<tr>
					<td class="tblfirstcol" width="25.5%" height="20%">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.creditcontrolpolicy.defaultresponsebehaviour" />
					</td>
					<td class="tblcol" width="74.5%" height="20%">
						<bean:write name="ccPolicyInstDataBean" property="defaultResponseBehaviour" />&nbsp;
					</td>
				</tr>
				
					<tr>
					<td class="tblfirstcol" width="25.5%" height="20%">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.creditcontrolpolicy.defaultresponsebehaviourargument" />
					</td>
					<td class="tblcol" width="74.5%" height="20%">
						<bean:write name="ccPolicyInstDataBean" property="defaultResponseBehaviorArgument" />&nbsp;
					</td>
				</tr>
				
				<tr> 
		            <td class="tblheader-bold" colspan="2"><bean:message bundle="servicePolicyProperties" key="servicepolicy.preplugins" /></td>
		        </tr>
		        <tr>
		          	<td align="center" colspan="2" style="padding-top: 10px;padding-bottom: 10px;">
		          			<table width="80%" cellspacing="0" cellpadding="0" border="0">
		          				<tr>
		          					<td class="tblheader-bold" width="50%">
		          						Plugin Name
		          					</td>
		          					<td class="tblheader-bold table-border-right" width="50%">
		          						Plugin Argument
		          					</td>
		          				</tr>
		          				<logic:iterate id="obj" name="ccPolicyInstDataBean" property="ccPolicyPluginConfigList">
			          				<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.IN_PLUGIN%>">
				          				<tr>
				          					<td class="tblfirstcol" width="50%">
				          						<bean:write name="obj" property="pluginName"/>
				          					</td>
				          					<td class="tblrows" width="50%">
				          						<bean:write name="obj" property="pluginArgument"/>
				          					</td>
				          				</tr>
			          				</logic:equal>
		          				</logic:iterate>
		          			</table>
		          	</td>
		        </tr>
		          
		        <tr> 
		            <td class="tblheader-bold" colspan="2"><bean:message bundle="servicePolicyProperties" key="servicepolicy.postplugins" /></td>
		        </tr>
		        <tr>
		          	<td align="center" colspan="2" style="padding-top: 10px;padding-bottom: 10px;">
	          			<table width="80%" cellspacing="0" cellpadding="0" border="0">
	          				<tr>
	          					<td class="tblheader-bold" width="50%">
	          						Plugin Name
	          					</td>
	          					<td class="tblheader-bold table-border-right" width="50%">
	          						Plugin Argument
	          					</td>
	          				</tr>
	          				<logic:iterate id="obj" name="ccPolicyInstDataBean" property="ccPolicyPluginConfigList">
		          				<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.OUT_PLUGIN%>">
			          				<tr>
			          					<td class="tblfirstcol" width="50%">
			          						<bean:write name="obj" property="pluginName"/>
			          					</td>
			          					<td class="tblrows" width="50%">
			          						<bean:write name="obj" property="pluginArgument"/>
			          					</td>
			          				</tr>
		          				</logic:equal>
	          				</logic:iterate>
	          			</table>
		          	</td>
		        </tr>
		        <%}%>
			</table>
		</td>
	</tr>
	<tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
	</tr>
</table>


