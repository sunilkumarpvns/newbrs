<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<jsp:directive.page
	import="com.elitecore.elitesm.util.constants.BaseConstant" />
<%@page import="java.util.List"%>
<%@page
	import="com.elitecore.elitesm.web.servermgr.server.forms.CreateServerForm"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>






<%
    String basePath = request.getContextPath();
	List staffDataList = (List)request.getAttribute("staffDataList");
    CreateServerForm  createServerForm = (CreateServerForm) request.getAttribute("createServerForm");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>

<script>
function validateAdminInterfaceIP()
{
	   //Check for valid IPAddress
		var ipre = /((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
		if(document.forms[0].adminInterfaceIP.value != null && document.forms[0].adminInterfaceIP.value!="" && !ipre.test(document.forms[0].adminInterfaceIP.value)){
			//	alert('Admin Interface IP  is not valid. Please Enter valid data.');
				document.forms[0].adminInterfaceIP.focus();
				return false;
		}
		else {
			return true;
		}
}
function validateCreate(act){
	var check;
	var checkPort;
	if(document.forms[0].netServerType.value == '0'){
	    alert('ServerType is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].name.value == ''){
		alert('Name is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].adminInterfaceIP.value == ''){
		alert('AdminInterface is a compulsory field Please enter required data in this field.');
	}else if(document.forms[0].adminInterfacePort.value == ''){
		alert('AdminInterfacePort is a compulsory field Please enter required data in this field.');
	} else if (document.forms[0].staff.value == '0') {
		alert("Please select the staff")
	} else{	
		check = validateAdminInterfaceIP();
		checkPort = validatePort(document.forms[0].adminInterfacePort.value)
		if(check == true ){
			if(checkPort == true){
				if(act=='f'){
				  document.forms[0].action.value='create';
				}
				document.forms[0].submit();
			}else
				alert('Admin Interface Port is not valid. Please Enter valid data.');
		}else{
			alert('Admin Interface IP  is not valid. Please Enter valid data.');
		}
	}
}
function validatePort(txt){
	// check for valid numeric port	 
	if(IsNumeric(txt) == true){
		if(txt >= 0 && txt<=65535)
			return(true);
	}else
		return(false);
}

setTitle('<bean:message bundle="servermgrResources" key="servermgr.server"/>');	
</script>

<html:form action="/createServer">
	<html:hidden styleId="isInSync" property="isInSync"
		value="<%=BaseConstant.HIDE_STATUS_ID%>" />
	<html:hidden styleId="action" property="action" value="next" />
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header" colspan="4"><bean:message
											bundle="servermgrResources" key="servermgr.createserver" />
										<%--<img src="<%=basePath%>/images/open.jpg" border="0" name="closeopen"></td> --%>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="100%" name="c_tblCrossProductList"
											id="c_tblCrossProductList" align="right" border="0">
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="servermgrResources"
														key="servermgr.servertype" /> 
															<ec:elitehelp headerBundle="servermgrResources" 
																text="servermgr.servertype" 
																	header="servermgr.servertype"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:select name="createServerForm"
														styleId="netServerType" property="netServerType" size="1"
														tabindex="1">
														<html:option value="0">
															<bean:message bundle="servermgrResources"
																key="servermgr.servertype" />
														</html:option>
														<html:options collection="lstNetServerType"
															property="netServerTypeId" labelProperty="name" />
													</html:select><font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="servermgrResources"
														key="servermgr.servername" /> 
															<ec:elitehelp headerBundle="servermgrResources" 
																text="servermgr.servername" 
																	header="servermgr.servername"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="name" property="name" size="30"
														maxlength="60" tabindex="2" style="width:250px" /><font
													color="#FF0000"> *</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:checkbox
														name="createServerForm" styleId="status" property="status"
														value="1" /> Active
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="servermgrResources"
														key="servermgr.description" />
															<ec:elitehelp headerBundle="servermgrResources" 
																text="servermgr.desc" 
																	header="servermgr.description"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:textarea styleId="description" property="description"
														cols="50" rows="4" tabindex="3" style="width:250px"></html:textarea>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="servermgrResources"
														key="servermgr.admininterfaceip" /> 
															<ec:elitehelp headerBundle="servermgrResources" 
																text="servermgr.admininterfaceip" 
																	header="servermgr.admininterfaceip"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="adminInterfaceIP"
														property="adminInterfaceIP" size="30" maxlength="60"
														tabindex="4" style="width:250px" /><font color="#FF0000">
														*</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="servermgrResources" key="servermgr.admininterfaceport" /> 
															<ec:elitehelp headerBundle="servermgrResources" text="servermgr.admininterfaceport" header="servermgr.admininterfaceport"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="adminInterfacePort" property="adminInterfacePort" size="10" maxlength="60" tabindex="5" style="width:250px" />
														<font color="#FF0000">*</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
														<bean:message bundle="servermgrResources" key="servermgr.user" /> 
														<ec:elitehelp headerBundle="servermgrResources" text="servermgr.staffUser" header="servermgr.user"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:select  property="staff"  styleClass="dropdown-width" tabindex="6">
														<html:option value="0">--None--</html:option>
														<html:optionsCollection property="staffDataList" name="createServerForm" label="username" value="username"/>
													</html:select>	
													<font color="#FF0000">*</font>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle">&nbsp;</td>
									<td class="btns-td" valign="middle"><input type="button"
										name="c_btnCreate" onclick="validateCreate('n')"
										id="c_btnCreate2" value="   Next   " class="light-btn"
										tabindex="7"> <input type="button" name="c_btnCreate"
										onclick="validateCreate('f')" id="c_btnCreate2"
										value="   Finish   " class="light-btn" tabindex="8"> <input
										type="reset" name="c_btnDeletePolicy"
										onclick="javascript:location.href='<%=basePath%>/listNetServerInstance.do?/>'"
										value="Cancel" class="light-btn" tabindex="9"></td>
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

