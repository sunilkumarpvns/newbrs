<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.ManageTGPPAAAPolicyOrderForm" %>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.tableorder.TableOrderData"%>
<%
	String basePath = request.getContextPath();		
%>

<script src="<%=request.getContextPath()%>/jquery/OrderTable/jquery.tablednd.js"></script>
<link rel="stylesheet" TYPE="text/css" href="<%=request.getContextPath()%>/css/tablednd.css">
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<script language="javascript">

$(document).ready(function(){
	/* Set Drag and Drop functionality on table */
	setTableDragAndDrop("listTable");
 	
	/* Save order of Table */
	$(".saveorder").click(function(){
 		saveOrder("tgppAAAPolicyId", "<%=TableOrderData.TGPPAAAPOLICYINST%>", "searchTGPPAAAPolicy");
 	});
	setTitle('<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.policy"/>');
	 
});
</script>
<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box">
				<tr height="5">
					<td></td>
				</tr>
				<tr>
					<td class="btns-td" valign="middle" colspan="2">

						<table width="100%" border="0" cellpadding="0" cellspacing="0"
							id="listTable">
							<tr>
								<td align="center" class="tblheader" valign="top" width="40px">Sr.No.</td>
								<td align="left" class="tblheader" valign="top" width="25%">Name</td>
								<td align="left" class="tblheader" valign="top" width="30%">
									<bean:message bundle="servicePolicyProperties"
										key="tgppaaapolicy.ruleset" />
								</td>
								<td align="left" class="tblheader" valign="top" width="30%">Description</td>
							</tr>
							<logic:iterate id="obj"	name="manageTGPPAAAServicePolicyOrderForm" property="policyList" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData" indexId="policyCount">
								<tr>
									<td align="left" valign="top" class="tblfirstcol" width="5%">
										<%=policyCount + 1%> <input type="hidden" name="tgppAAAPolicyId" value='<bean:write name="obj" property="tgppAAAPolicyId"/>'>
									</td>
									<td align="left" valign="top" class="tblrows" width="23.75%">
										<input type="hidden" name="order" value="<bean:write name="obj" property="name"/>" /> <bean:write name="obj" property="name" />
									</td>
									<td align="left" class="tblrows"><bean:write name="obj"	property="ruleset" />&nbsp;</td>
									<td align="left" class="tblrows"><%=EliteUtility.formatDescription(obj.getDescription())%>&nbsp;&nbsp;</td>
								</tr>
							</logic:iterate>
							<logic:empty name="manageTGPPAAAServicePolicyOrderForm"		property="policyList">
								<tr>
									<td class="tblfirstcol" align="center" colspan="5">No Records Found.</td>
								</tr>
							</logic:empty>
						</table>
					</td>
				</tr>
				<tr height="5">
					<td></td>
				</tr>
				<tr>
					<td align="left" valign="top" colspan="100%" class="btns-td">
						<input type="button" value=" Save " class="light-btn saveorder" />
						<input type="button" value=" Cancel " class="light-btn"	onclick="javascript:location.href='<%=basePath%>/searchTGPPAAAPolicy.do?/>'" />
						<input type="button" value=" Reset " class="light-btn"
						onclick="javascript:location.href='<%=basePath %>/manageTGPPAAAServicePolicyOrder.do?/>'" />
					</td>
				</tr>
				<tr height="5">
					<td></td>
				</tr>
				</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>
