<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<% 
	String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
setTitle('<bean:message bundle="diameterResources" key="imsibasedroutingtable.title"/>');
</script>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>"
	border="0" cellspacing="0" cellpadding="0">
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
												 <bean:define id="imsiBasedRoutingTableDataBean" name="imsiBasedRoutingTableData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData" />
												<table width="100%" border="0" cellspacing="0" cellpadding="0" >
											          <tr> 
											            <td class="tblheader-bold" colspan="2" height="20%">
											              <bean:message bundle="diameterResources" key="imsibasedroutingtable.viewroutingtable"/>
											            </td>
											          </tr>
											          <tr> 
											            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="imsibasedroutingtable.routingtablename" /> </td>
											            <td class="tblcol" width="70%" ><bean:write name="imsiBasedRoutingTableDataBean" property="routingTableName"/>&nbsp;</td>
											          </tr>
											          <tr> 
											            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="imsibasedroutingtable.imsiidentityattribute" /> </td>
											            <td class="tblcol" width="70%" ><bean:write name="imsiBasedRoutingTableDataBean" property="imsiIdentityAttributes"/>&nbsp;</td>
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
								<td width="168" class="grey-bkgd" valign="top"><%@ include
										file="IMSIBasedRoutingTableNavigation.jsp"%>
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


