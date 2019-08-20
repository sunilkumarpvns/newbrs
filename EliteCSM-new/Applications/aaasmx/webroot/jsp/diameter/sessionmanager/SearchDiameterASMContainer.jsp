<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<% String basePath = request.getContextPath();%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
	setTitle('<bean:message bundle="sessionmanagerResources" key="sessionmanager.header"/>');
</script>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<bean:define id="diameterSessionManagerDataObj" scope="request" name="diameterSessionManagerData" type="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData"></bean:define>
				<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr> 
						          			<td class="tblheader-bold" height="10%" colspan="2">
						          				<bean:message bundle="driverResources" key="driver.view"/>
						          			</td>
						        		</tr>
						        		<tr>
											<td class="tblfirstcol" width="24%" height="10%" >
												<bean:message bundle="sessionmanagerResources" key="sessionmanager.name" />
											</td>
											<td class="tblcol" width="76%" height="80%" >
												<bean:write name="diameterSessionManagerDataObj" property="name"/>&nbsp;
											</td>
										</tr>  
										<tr>
											<td class="tblfirstcol" width="24%" height="10%" >
												<bean:message bundle="sessionmanagerResources" key="sessionmanager.description" />
											</td>
											<td class="tblcol" width="76%" height="80%" >
												<bean:write name="diameterSessionManagerDataObj" property="description"/>&nbsp;
											</td>
										</tr>  
										<tr>
											<td valign="top" align="right" colspan="2">
												 <%@ include file="SearchDiameterASM.jsp"%> 
											</td>
										</tr>
									</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top">
									<%@ include file="DiameterSessionManagerNavigation.jsp"%>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>