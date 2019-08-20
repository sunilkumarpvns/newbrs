<%@page import="com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

<tr> 
<td colspan="2" valign="top"> 

<%
	String navigationBasePath = request.getContextPath();
	LDAPDatasourceData ldapDatasourceData1 = (LDAPDatasourceData)session.getAttribute("ldapDatasourceData");
	
	
	String updateLDAPDS              = navigationBasePath+"/initupdateLDAPDS.do?ldapDsId="+ldapDatasourceData1.getLdapDsId();	
	String viewLDAPDS                 = navigationBasePath+"/viewLDAPDS.do?ldapDsId="+ldapDatasourceData1.getLdapDsId();	

%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('UpdateRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateLDAPDS%>" tabindex="15"><bean:message bundle="datasourceResources" key="ldap.update.ldap" /></a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.view" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewLDAPDS%>" tabindex="16"><bean:message  bundle="datasourceResources" key="ldap.view.ldap" /></a>
									</td>
								</tr>								
							</table>
						</div>
					</td>
				</tr>
			</table>
		</td>
  </tr>

</table>

