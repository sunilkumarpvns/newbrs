<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.netvertexsm.web.sendpacket.form.SendRadiusPacketForm"%>
<%@ page
	import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page
	import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="java.io.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/jquery/libs/jsoneditor/jsoneditor.css">
<script type="text/javascript"
	src="<%=request.getContextPath()%>/jquery/libs/jsoneditor/jsoneditor.js"></script>

<script type="text/javascript"
	src="<%=request.getContextPath()%>/jquery/libs/jsoneditor/ace.js"></script>

<script type="text/javascript"
	src="<%=request.getContextPath()%>/jquery/libs/jsoneditor/jsonlint.js"></script>

<style type="text/css">
code {
	background-color: #f5f5f5;
}

#jsoneditorRequest {
	height: 400px;
}

#jsoneditorResponse {
	height: 400px;
}
</style>

<%
	SendRadiusPacketForm sendRadiusPacketForm = (SendRadiusPacketForm) request.getAttribute("sendRadiusPacketForm");
%>
<script language="javascript">
function validate(id){
	if(isNull(document.forms[0].name.value)){
		alert('Packet Name must be specified.');
		document.forms[0].name.focus();
	}else if(isNaN(document.forms[0].timeOut.value)){
		alert('Time Out must be in Numeric.');
		document.forms[0].timeOut.focus();
	}else if(isNull(document.forms[0].secretKey.value)){
		alert('Secret Key must be specified');
		document.forms[0].secretKey.focus;
	}else if(getJSON() == "{}" || getJSON() == null || getJSON == ''){
		alert("Can not Send Empty Request");
	}else if(isValidJson(getJSON()) == false){
		alert('Json Request Configuration is Invalid');
	}  
	else{
		document.getElementById("packetData").value = getJSON();
		if(id == 'send'){
		document.getElementById("action").value = "send";
		}else{
			document.getElementById("action").value = "create";
		}
		document.forms[0].submit();
	}
};
function isValidJson(json) {
	try{
		jsonlint.parse(json);
		return true;
	}catch(e){
		alert("Can not Parse JSON");
		return false;
	}
}
$(document).ready(function(){
	setTitle('<bean:message bundle="servermgrResources" key="sendpacket.header"/>');
});

</script>
</head>
<html:form action="/createRadiusSendPacket">
	<html:hidden property="action" styleId="action" style="action"
		name="sendRadiusPacketForm" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="10">&nbsp;</td>
			<td width="<%=ConfigConstant.PAGE_WIDTH %>" colspan="2" valign="top"
				class="box">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="table-header" colspan="5"><bean:message
								bundle="servermgrResources"
								key="servermgr.sendpacket.radius.create" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="3">
							<table width="97%" align="right" border="0">
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%"><bean:message
											bundle="servermgrResources"
											key="servermgr.sendpacket.packetname" /></td>
									<td align="left" class="labeltext" valign="top" width="32%"><html:text
											property="name" styleId="name"
											tabindex="1" /></td>

								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%"><bean:message
											bundle="servermgrResources"
											key="servermgr.sendpacket.ipaddress" /> <img
										src="<%=basePath%>/images/help-hover.jpg" height="12"
										width="12" style="cursor: pointer"
										onclick="parameterDescription('<bean:message bundle="descriptionResources" key="sendpacket.ipaddress"/>','<bean:message bundle="servermgrResources" key="servermgr.sendpacket.ipaddress"/>')" />
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:text property="ipAddress" styleId="ipAddress"
											tabindex="2" readonly="true"/>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%"><bean:message
											bundle="servermgrResources" key="servermgr.sendpacket.port" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12"
										width="12" style="cursor: pointer"
										onclick="parameterDescription('<bean:message bundle="descriptionResources" key="sendpacket.port"/>','<bean:message bundle="servermgrResources" key="servermgr.sendpacket.port"/>')" />
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:text property="port" styleId="port" tabindex="3" readonly="true"/>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%"><bean:message
											bundle="servermgrResources"
											key="servermgr.sendpacket.timeout" /> <img
										src="<%=basePath%>/images/help-hover.jpg" height="12"
										width="12" style="cursor: pointer"
										onclick="parameterDescription('<bean:message bundle="descriptionResources" key="sendpacket.timeout"/>','<bean:message bundle="servermgrResources" key="servermgr.sendpacket.timeout"/>')" />
									</td>
									<td align="left" class="labeltext" valign="top" width="32%"><html:text
											property="timeOut" styleId="timeOut" tabindex="4" /></td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" width="10%"><bean:message
											bundle="servermgrResources"
											key="servermgr.sendpacket.secretkey" /> <img
										src="<%=basePath%>/images/help-hover.jpg" height="12"
										width="12" style="cursor: pointer"
										onclick="parameterDescription('<bean:message bundle="descriptionResources" key="sendpacket.secretkey"/>','<bean:message bundle="servermgrResources" key="servermgr.sendpacket.secretkey"/>')" />
									</td>
									<td align="left" class="labeltext" valign="top" width="32%"><html:text
											property="secretKey" styleId="secretKey" tabindex="6" /><font
										color="#FF0000"> *</font></td>
								</tr>
								<tr>
									<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
								</tr>
								<tr>
									<td valign="middle" colspan="3">
										<table width="100%" cellpadding="0" cellspacing="0" border="0">
											<tr>
												<td align="center" class="tblheader-bold-font"
													style='cursor: default' valign="top" width="100%"
													colspan="2">Packet Detail Configuration</td>
											</tr>
											<tr width="100%">
												<td width="50%" class="tbllabelcol">Request</td>
												<td width="50%" colspan="2" class="tbllabelcol">
													Response</td>
											</tr>
											<tr width="100%">
												<td width="50%">
													<div id="jsoneditorRequest" width="100%">
														<html:hidden property="packetData" styleId="packetData" />
													</div>
												</td>
												<td width="50%" colspan="2"><div id="jsoneditorResponse" width="100%">
														<html:hidden property="responseData"
															styleId="responseData" />
													</div></td>
											</tr>
										</table>
									</td>
								</tr>

								<tr width="100%">
									<td valign="middle" align="center" colspan="2" width="100%"><input
										type="button" id="create" value="  Create  " class="light-btn"
										tabindex="" onclick="validate(this.id);"> <input
										type="button" id="send" value="  Send  " class="light-btn"
										tabindex="" onclick="validate(this.id);"> <input
										type="button" tabindex="" align="left" value="  Cancel  "
										class="light-btn"
										onclick="javascript:location.href='<%=basePath%>/searchRadiusSendPacket.do?netserverid=<%=request.getSession().getAttribute("netserverid") %>'" /></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<%@include file="/jsp/core/includes/common/Footerbar.jsp"%>
	<script type="text/javascript">
		var container = document.getElementById('jsoneditorRequest');
		var options = {
			mode : 'text',
			modes : [ 'code', 'form', 'text', 'tree', 'view' ], // allowed modes
			error : function(err) {
				alert(err.toString());
			}
		};
		var json = <%=sendRadiusPacketForm.getPacketData() %>
		var editor = new JSONEditor(container, options, json);
		function setJSON() {
			editor.set(json);
		}

		function getJSON() {
			var json = editor.get();
			return  JSON.stringify(json, null, 2);
		}
		
		var containerResponse = document.getElementById('jsoneditorResponse');
		var optionsResponse = {
			mode : 'tree',
			modes : [ 'code', 'form', 'text', 'tree', 'view' ], 
			error : function(err) {
				alert(err.toString());
			}
	
		};
		var jsonResponse =<%=sendRadiusPacketForm.getResponseData()%>;
		var responseEditor = new JSONEditor(containerResponse, optionsResponse, jsonResponse);
		function setJSONResponse() {
			responseEditor.set(jsonResponse);
		}
		
		function getJSONResponse() {
			var jsonResponse = responseEditor.get();
			return JSON.stringify(jsonResponse, null, 2);
		};
	</script>

</html:form>


</html>