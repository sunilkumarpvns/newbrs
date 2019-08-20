

<%
	int viewIndex = 0;
	
%>

<bean:define id="concurrentLoginPolicyBean"
	name="concurrentLoginPolicyData" scope="request"
	type="com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData" />
<tr>
	<td class="table-header"><logic:equal name="action"
			value="updateBasicDetails" scope="request">
			<%viewIndex = 1;%>
			<bean:message bundle="radiusResources"
				key="concurrentloginpolicy.update.basicdetails" />
		</logic:equal> <logic:equal name="action" value="updateNasPortTypeDetails"
			scope="request">
			<%viewIndex = 2;%>
			<bean:message bundle="radiusResources"
				key="concurrentloginpolicy.update.nasporttypedetail" />
		</logic:equal> <logic:equal name="action" value="viewNasPortTypeDetails"
			scope="request">
			<%viewIndex = 3;%>
			<bean:message bundle="radiusResources"
				key="concurrentloginpolicy.view.detail.attribute" />
		</logic:equal> <logic:equal name="action" value="updateStatus" scope="request">
			<%viewIndex = 4;%>
			<bean:message bundle="radiusResources"
				key="concurrentloginpolicy.update.activitystatus" />
		</logic:equal> <%if(viewIndex == 0){%> <bean:message bundle="radiusResources"
			key="concurrentloginpolicy.view.details" /> <%}%></td>
</tr>

<tr>
	<td valign="top" align="right">
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			height="15%">
			<tr>
				<td class="tblheader-bold" colspan="2" height="20%"><bean:message
						bundle="radiusResources"
						key="concurrentloginpolicy.view.information" /></td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="30%" height="20%"><bean:message
						bundle="radiusResources" key="concurrentloginpolicy.name" /></td>
				<td class="tblcol" width="70%" height="20%"><bean:write
						name="concurrentLoginPolicyBean" property="name" />&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="30%" height="20%"><bean:message
						bundle="radiusResources" key="concurrentloginpolicy.description" /></td>
				<td class="tblcol" width="70%" height="20%"><bean:write
						name="concurrentLoginPolicyBean" property="description" />&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="30%" height="20%"><bean:message
						bundle="radiusResources"
						key="concurrentloginpolicy.concurrentloginpolicytype" /></td>
				<logic:equal name="concurrentLoginPolicyBean"
					property="concurrentLoginPolicyTypeId" value="SMS0139">
					<td class="tblcol" width="70%" height="20%"><bean:message
							bundle="radiusResources"
							key="concurrentloginpolicy.policytypegroup" />&nbsp;</td>
				</logic:equal>
				<logic:equal name="concurrentLoginPolicyBean"
					property="concurrentLoginPolicyTypeId" value="SMS0138">
					<td class="tblcol" width="70%" height="20%"><bean:message
							bundle="radiusResources"
							key="concurrentloginpolicy.policytypeindividual" />&nbsp;</td>
				</logic:equal>
			</tr>
			<tr>
				<td class="tblfirstcol" width="30%" height="20%"><bean:message
						bundle="radiusResources" key="concurrentloginpolicy.policymode" /></td>
				<logic:equal name="concurrentLoginPolicyBean"
					property="concurrentLoginPolicyModeId" value="SMS0149">
					<td class="tblcol" width="70%" height="20%"><bean:message
							bundle="radiusResources"
							key="concurrentloginpolicy.policymodegeneral" />&nbsp;</td>
				</logic:equal>
				<logic:equal name="concurrentLoginPolicyBean"
					property="concurrentLoginPolicyModeId" value="SMS0150">
					<td class="tblcol" width="70%" height="20%"><bean:message
							bundle="radiusResources"
							key="concurrentloginpolicy.polivymodeservicewise" />&nbsp;</td>
				</logic:equal>
			</tr>

			<tr>
				<td class="tblfirstcol" width="30%" height="20%"><bean:message
						bundle="radiusResources"
						key="concurrentloginpolicy.maximumconcurrentlogin" /></td>
				<logic:equal name="concurrentLoginPolicyBean" property="login"
					value="-1">
					<td class="tblcol" width="70%" height="20%"><bean:message
							bundle="radiusResources" key="concurrentloginpolicy.unlimited" />&nbsp;</td>
				</logic:equal>
				<logic:notEqual name="concurrentLoginPolicyBean" property="login"
					value="-1">
					<td class="tblcol" width="70%" height="20%"><bean:write
							name="concurrentLoginPolicyBean" property="login" />&nbsp;</td>
				</logic:notEqual>
			</tr>

			<tr>
				<td class="tblfirstcol" width="30%" height="20%"><bean:message
						bundle="radiusResources" key="concurrentloginpolicy.attribute" /></td>

				<td class="tblcol" width="70%" height="20%"><bean:write
						name="concurrentLoginPolicyBean" property="attribute" />&nbsp;</td>
			</tr>

			<tr>
				<td class="tblfirstcol" width="30%" height="20%"><bean:message
						bundle="radiusResources" key="concurrentloginpolicy.status" /></td>
				<logic:equal name="concurrentLoginPolicyBean"
					property="commonStatusId" value="CST01">
					<td class="tblcol" width="70%" height="20%"><img
						src="<%=basePath%>/images/active.jpg" />&nbsp;</td>
				</logic:equal>
				<logic:equal name="concurrentLoginPolicyBean"
					property="commonStatusId" value="CST02">
					<td class="tblcol" width="70%" height="20%"><img
						src="<%=basePath%>/images/deactive.jpg" />&nbsp;</td>
				</logic:equal>
			</tr>


		</table>
	</td>
</tr>



