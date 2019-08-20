<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<% String basePath = request.getContextPath(); %>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
	setTitle('<bean:message bundle="diameterResources" key="diameter.diameterconcurrency"/>');
</script>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td cellpadding="0" cellspacing="0" border="0" width="100%"
						class="box">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td valign="top" align="right">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td valign="top" align="right">
										    <bean:define id="diameterConcurrencyDataBean" name="diameterConcurrencyData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData" />
 												 <table width="100%" border="0" cellspacing="0" cellpadding="0" >
											          <tr> 
											            <td class="tblheader-bold" colspan="2" height="20%">
											            	<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.viewsummary"/>
											            </td>
											          </tr>
											           <tr> 
											            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources"  key="diameterpolicy.diameterpolicygroup.name" /> </td>
											            <td class="tblcol" width="70%" ><bean:write name="diameterConcurrencyDataBean" property="name"/>&nbsp;</td>
											          </tr>
											          <tr> 
											            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameter.diameterconcurrency.description" /> </td>
											            <td class="tblcol" width="70%" ><bean:write name="diameterConcurrencyDataBean" property="description"/>&nbsp;</td>
											          </tr>
											       </table>
											</td>
										</tr>
										<tr>
											<td>
												<%@ include file="/jsp/core/system/history/ViewHistoryJSON.jsp"%>
											</td>
										</tr>
									</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top">
									<%@ include file="DiameterConcurrencyNavigation.jsp"%>
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


