<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.alert.forms.CreateAlertListenerForm"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
   String basePath = request.getContextPath();
   String typeIdVal=(String)request.getParameter("typeId");
   CreateAlertListenerForm createAlertListenerForm=(CreateAlertListenerForm)request.getAttribute("createAlertListenerForm");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script><!--
var isValidName;
$(document).ready(function() {
    $('#typeId').val('<%=typeIdVal%>');
});
function validateNext()
{
	if(isNull(document.forms[0].name.value)){
		document.forms[0].name.focus();
		alert('Alert Listener Name must be specified');
	}else if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();
		return;
	}else if(document.forms[0].typeId.value =='0'){
		document.forms[0].typeId.focus();
		alert('Listener Type must be selected');
	}else{
	
	     document.forms[0].submit();
    }
    
}	


function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.ALERT_LISTENER%>',searchName,'create','','verifyNameDiv');
}

setTitle('Alert Listener');

</script>

<html:form action="/createAlertListener">
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header">Create Alert Listener</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="servermgrResources" 
														key="servermgr.alert.listenerName" />
															<ec:elitehelp headerBundle="servermgrResources" 
																text="servermgr.alert.listenerName" 
																	header="servermgr.alert.listenerName"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
													<html:text styleId="name" tabindex="1" property="name" onkeyup="verifyName();" size="25" maxlength="60" style="width:200px" name="createAlertListenerForm" /> 
													<font color="#FF0000">*</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="servermgrResources" 
														key="servermgr.alert.listenerType" /> 
															<ec:elitehelp headerBundle="servermgrResources" 
																text="servermgr.alert.listenerType" 
																	header="servermgr.alert.listenerType"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:select property="typeId" style="width:200px" styleId="typeId" tabindex="2">
														<html:option value="0">Select</html:option>
														<html:optionsCollection name="createAlertListenerForm" property="availableListenerTypes" label="typeName" value="typeId" />
													</html:select>
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2">
													<input type="button" name="c_btnNext" tabindex="3" onclick="validateNext()" id="c_btnNext2" value="Next" class="light-btn"> 
													<input type="reset" name="c_btnCancel" tabindex="4" onclick="javascript:location.href='<%=basePath%>/initSearchAlertListener.do?/>'" value="Cancel" class="light-btn">
												</td>
											</tr>
										</table>
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
</html:form>

