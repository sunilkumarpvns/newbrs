<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.ManageNASPolicyOrderForm"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager,com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.*,com.elitecore.elitesm.util.constants.*"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.tableorder.TableOrderData"%>


<%
	String basePath = request.getContextPath();		
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script src="<%=request.getContextPath()%>/jquery/OrderTable/jquery.tablednd.js"></script>
<link rel="stylesheet" TYPE="text/css" href="<%=request.getContextPath()%>/css/tablednd.css">

<script language="javascript">

 $(document).ready(function(){
	 /* Set Drag and Drop functionality on table */
	  setTableDragAndDrop("listTable");
	 	
	 /* Save order of Table */
	 $(".saveorder").click(function(){
	 	saveOrder("nasPolicyId", "<%=TableOrderData.NASPOLICYINST%>", "initSearchNASServicePolicy");
	 });
});

setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy"/>');

</script>


<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="box" cellpadding="0" cellspacing="0" border="0" width="100%">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header" colspan="5">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy" />
								</td>
							</tr>
							<tr>
								<td colspan="4">&nbsp;</td>
							</tr>
							<tr>
								<td class="btns-td" colspan="4"></td>
							</tr>
							<tr>
								<td width="100%" align="center" valign="middle">
									<table id="listTable" width="97%" cellpadding="0" cellspacing="0" border="0">
										<tr>
											<td align="left" class="tblheader" valign="top" width="5%">Sr. No.</td>
											<td align="left" class="tblheader" valign="top" width="35%">Name</td>
											<td align="left" class="tblheader" valign="top"width="30%">RuleSet</td>
											<td align="left" class="tblheader" valign="top" width="25%">Description</td>
										</tr>
											
											<logic:iterate id="obj" name="naspolicyorder" property="policyList" type="NASPolicyInstData" indexId="policyCount">
												<tr>
													<td align="left" class="tblfirstcol" width="5%">
														<%=policyCount+1%>
														<input type="hidden" name="nasPolicyId" value="<bean:write name="obj" property="nasPolicyId"/>" />
													</td>
													<td align="left" class="tblrows" width="35%">
														<bean:write name="obj" property="name" />
													</td>
													<td align="left" class="tblrows" width="30%">
														<bean:write name="obj" property="ruleSet" />
													</td>
													<td align="left" class="tblrows" width="25%"><%=EliteUtility.formatDescription(obj.getDescription()) %>&nbsp;&nbsp;</td>
												</tr>
											 </logic:iterate>
												
											<logic:empty name="naspolicyorder" property="policyList">
											<tr>
												<td align="center" class="tblfirstcol" colspan="4">No Records Found.</td>
											</tr>
											</logic:empty>
										</table>
									</td>

								</tr>
								<tr>
									<td colspan="4">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" align="left" valign="top">
										<input type="button" value=" Save " class="light-btn saveorder" /> 
										<input type="button" value=" Cancel " class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchNASServicePolicy.do?/>'" />
										<input type="button" value=" Reset " class="light-btn" onclick="javascript:location.href='<%=basePath %>/manageOrderNAS.do?/>'" />
									</td>
								</tr>
								<tr>
									<td colspan="5">&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
</table>
