<%@page
	import="com.elitecore.corenetvertex.test.radius.jaxb.RadiusPacketConfigurationData"%>
<%@page
	import="com.elitecore.netvertexsm.web.sendpacket.form.SendRadiusPacketForm"%>
<%@ page
	import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page
	import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
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
var editor;
var responseEditor;
$(document).ready(function(){
	var container = document.getElementById('jsoneditorRequest');
	var options = {
		mode : 'tree',
		modes : [ 'code', 'form', 'text', 'tree', 'view' ], // allowed modes
		error : function(err) {
			alert(err.toString());
		}
	};
	var json = <%=sendRadiusPacketForm.getPacketData()%> ;
	editor = new JSONEditor(container, options, json);
	var responseContainer = document.getElementById('jsoneditorResponse');
	var optionsForResponse = {
		mode : 'tree',
		modes : [ 'code', 'form', 'text', 'tree', 'view' ], // allowed modes
		error : function(err) {
			alert(err.toString());
		}
	};

var jsonResponse = <%=sendRadiusPacketForm.getResponseData() %> ;
responseEditor = new JSONEditor(responseContainer, optionsForResponse, jsonResponse);
setTitle('<bean:message bundle="servermgrResources" key="sendpacket.header"/>');	
});
function setJSONResponse() {
	responseEditor.set(jsonResponse);
} 
function getJSONResponse() {
	var jsonResponse = responseEditor.get();
	return JSON.stringify(jsonResponse, null, 2);
};

function setJSON() {
	editor.set(json);
} 
function getJSON() {
	var json = editor.get();
	return JSON.stringify(json, null, 2);
};
function validate(id){
	 if(isNaN(document.forms[0].timeOut.value)){
		 alert('Time Out must be in Numeric.');
		document.forms[0].timeOut.focus();
	}else if(isValidJson(getJSON()) == false){
		alert('Packet Detail Configuration is Invalid');
		document.forms[0].jsoneditorRequest.focus();
	}else if(isNull(document.forms[0].secretKey.value)){
		alert('Secret Key must be specified');
		document.forms[0].secretKey.focus;
	} else if(getJSON() == "{}" || getJSON() == null || getJSON == ''){
		alert("Can not send empty JSON Request");
	}else{
		document.getElementById("packetData").value = getJSON();
		if(id == 'send'){
			document.getElementById("action").value = "send";
		}else{
			document.getElementById("action").value = "update";
		}
		document.forms[0].submit();
	}
};

 function isValidJson(json) {
	try {
        JSON.parse(json);
        return true;
    } catch (e) {
       alert("Invalid JSON Format For Packet Detail");
       document.forms[0].jsoneditorRequest.focus();
    	return false;
    }
} 
</script>
</head>

<html:form action="/editRadiusSendPacket">
	<html:hidden name="sendRadiusPacketForm" styleId="action"
		style="action" property="action" />
	<html:hidden name="sendRadiusPacketForm" styleId="packetName"
		style="packetName" property="name"
		value="<%=sendRadiusPacketForm.getName()%>" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<%@include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">&nbsp;</td>
			<td width="<%=ConfigConstant.PAGE_WIDTH %>" colspan="2" valign="top"
				class="box">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="table-header" colspan="5"><bean:message bundle="servermgrResources"
								key="servermgr.sendpacket.radius.update" /></td>
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
											property="name" styleId="name" style="name" disabled="true"
											tabindex="2" />
										</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%"><bean:message bundle="servermgrResources"
											key="servermgr.sendpacket.ipaddress" /> <img
										src="<%=basePath%>/images/help-hover.jpg" height="12"
										width="12" style="cursor: pointer"
										onclick="parameterDescription('<bean:message bundle="descriptionResources" key="sendpacket.ipaddress"/>','<bean:message bundle="servermgrResources" key="servermgr.sendpacket.ipaddress"/>')" />
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:text property="ipAddress" styleId="ipAddress"
											tabindex="3" readonly="true"/>
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
										<html:text property="port" styleId="port" tabindex="4" readonly="true"/>
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
											property="timeOut" styleId="timeOut" tabindex="5" /></td>
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
											<tr>
												<td width="50%">
													<div id="jsoneditorRequest" width="100%">
														<html:hidden property="packetData" styleId="packetData" />
													</div>
												</td>
												<td width="50%">
													<div id="jsoneditorResponse" width="100%">
														<html:hidden property="responseData"
															styleId="responseData" />
													</div>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td valign="middle" align="center" colspan="2"><input
										type="button" id="update" value="  Update  " class="light-btn"
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
</html:form>
</html>