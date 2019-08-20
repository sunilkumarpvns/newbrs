<%@page import="com.elitecore.elitesm.web.diameter.subscriberroutingtable.form.SubscriberRoutingTableForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.UpdateDiameterPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData"%>
<%@ page import="com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData" %>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
	IMSIBasedRoutingTableForm imsiBasedRoutingTableForm = (IMSIBasedRoutingTableForm)request.getAttribute("imsiBasedRoutingTableForm");
%>
<style type="text/css">
	.ui-dialog .ui-dialog-titlebar-close {
	    background: none !important;
	    border: none;
	    cursor:pointer;
	}
</style>

<script type="text/javascript">

var isValidName;
function validate(){
	
	var imsiIdentityAttributes = $('#imsiIdentityAttributes').val();
	if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return;
	}else if(imsiIdentityAttributes.length == 0){
		alert('Enter IMSI Identity Attribute');
		document.forms[0].imsiIdentityAttributes.focus();
		return;
	}
	document.forms[0].submit();
}

function verifyName() {
	var searchName = document.getElementById("imsiBasedRoutingTableName").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.IMSI_BASED_ROUTING_TABLE%>',searchName,'create','','verifyNameDiv');
	if(isValidName){
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.MSISDN_BASED_ROUTING_TABLE%>',searchName,'create','','verifyNameDiv');
	}
}

</script>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td colspan="3">
									<html:form action="/createIMSIBasedRoutingTable.do">
										<html:hidden property="action" styleId="action" />
										<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="left" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%" border="0">
														<tr>
															<td class="table-header" colspan="100%">
																<bean:message bundle="diameterResources" key="imsibasedroutingtable.title" />
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														<tr>
															<td align="left" class="btns-td" valign="top" width="20%">
																<bean:message bundle="diameterResources" key="imsibasedroutingtable.routingtablename" /> 
																<ec:elitehelp headerBundle="diameterResources"  text="imsibasedroutingtable.routingtablename" header="imsibasedroutingtable.routingtablename"/>
															</td>
															<td align="left" class="labeltext" valign="top" width="80%">
																<html:text styleId="imsiBasedRoutingTableName" tabindex="1" property="imsiBasedRoutingTableName" size="30" onkeyup="verifyName();" maxlength="60" style="width:250px" />
																<font color="#FF0000"> *</font>
																<div id="verifyNameDiv" class="labeltext"></div>
															</td>
														</tr>
														<tr>
															<td align="left" class="btns-td" valign="top" width="20%">
																<bean:message bundle="diameterResources" key="imsibasedroutingtable.imsiidentityattribute" /> 
																<ec:elitehelp headerBundle="diameterResources"  text="imsibasedroutingtable.imsiidentityattribute" header="imsibasedroutingtable.imsiidentityattribute"/>
															</td>
															<td align="left" class="labeltext" valign="top" width="80%">
																<html:text styleId="imsiIdentityAttributes" onfocus="setAVPsData();" tabindex="1" property="imsiIdentityAttributes" size="30"  maxlength="60" style="width:250px" />
																<font color="#FF0000"> *</font>
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
															<td>&nbsp;</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
															<td>
																<input type="button" value=" Create " class="light-btn" onclick="validate()" tabindex="20" />
																<input type="button" name="cancel" id="cancelbtn" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchSubscriberRoutingTable.do?method=initSearch'"/>
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														</table>
														</td>
													</tr>
										</table>
									</html:form>
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

<script>
	setTitle('<bean:message bundle="diameterResources" key="imsibasedroutingtable.title"/>');
</script>
