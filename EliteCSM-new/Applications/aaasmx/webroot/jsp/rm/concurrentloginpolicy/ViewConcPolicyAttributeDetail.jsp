<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<html>
<tr>
	<td valign="top" align="right">
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			height="15%">
			<tr>
				<td align="left" class="tblheader-bold" valign="top" colspan="3">
					<bean:message bundle="radiusResources"
						key="concurrentloginpolicy.view.detail.attribute" />
				</td>
			</tr>

			<tr>
				<td>
					<table width="100%" align="right" border="0" cellpadding="0"
						cellspacing="0">
						<%
							int index1 = 0;
						%>
						<tr>
							<td align="left" class="tblheader" valign="top" width="5%"
								heigth="15%"><bean:message bundle="radiusResources"
									key="concurrentloginpolicy.serialnumber" /></td>
							<td align="left" class="tblheader" valign="top" width="10%">
								<bean:message bundle="radiusResources"
									key="concurrentloginpolicy.attributevalue" />
							</td>
							<td align="left" class="tblheader" valign="top" width="50%">
								<bean:message bundle="radiusResources"
									key="concurrentloginpolicy.maxlogin" />
							</td>
						</tr>

						<logic:iterate id="loginPolicyDetailBean" name="loginPolicyDetail"
							type="com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyDetailData"
							scope="session">
							<tr>
								<td align="left" class="tblfirstcol"><%=(index1+1)%></td>
								<td align="left" class="tblrows"><bean:write
										name="loginPolicyDetailBean" property="attributeValue" /></td>
								<td align="left" class="tblrows"><bean:write
										name="loginPolicyDetailBean" property="login" /></td>
							</tr>
							<% index1++; %>
						</logic:iterate>

					</table>
				</td>
			</tr>
		</table>
	</td>
</tr>
</html>



