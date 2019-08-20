<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
</script>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>"
	border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box" colspan="2">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td valign="top" align="right" width="*">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
    									<tr> 
		            						<td class="tblheader-bold" height="20%" colspan="4"><bean:message bundle="driverResources" key="driver.view"/></td>
		          						</tr>  
		          						 <tr>
											<td align="left" class="tblheader-bold" valign="top" colspan="4">
											<bean:message bundle="driverResources" key="driver.driverinstancedetails" /></td>
										 </tr>  					
		          						 <tr>
											<td class="tblfirstcol" width="20%" height="20%" >
											<bean:message bundle="driverResources" key="driver.instname" /></td>
											<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="driverInstanceName"/></td>
										 </tr>   
							    
							   			 <tr>
											<td class="tblfirstcol" width="20%" height="20%">
											<bean:message bundle="driverResources" key="driver.instdesc" /></td>
											<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateDiameterDetailLocalAcctDriverForm" property="driverInstanceDesp"/>&nbsp;</td>
																
										</tr>
    									<tr>
											<td colspan="4">
												<%@ include file="/jsp/core/system/history/ViewHistoryJSON.jsp"%>
											</td>
										</tr>
									</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top">
									<%@ include file="/jsp/servermgr/drivers/diameter/DiameterDetailLocalNavigation.jsp"%>
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


