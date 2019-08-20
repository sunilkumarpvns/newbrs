<%@page import="com.elitecore.elitesm.web.servermgr.copypacket.forms.UpdateCopyPacketMappingConfigForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@ page import="com.elitecore.commons.base.Collectionz"%>

<%
	CopyPacketTranslationConfData copyPacketMappingConfData = (CopyPacketTranslationConfData) request.getAttribute("copyPacketMappingConfData");
	UpdateCopyPacketMappingConfigForm updateCopyPacketMappingConfigForm = (UpdateCopyPacketMappingConfigForm) request.getAttribute("updateCopyPacketMappingConfigForm");
%>

<script>
var isValidName;
var externalScriptList = [];

<%
if( Collectionz.isNullOrEmpty(updateCopyPacketMappingConfigForm.getExternalScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : updateCopyPacketMappingConfigForm.getExternalScriptList()){ %>
		externalScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

function validation()
{	
	if(isNull(document.forms[0].name.value)){
		alert('Name must be specified');
		document.forms[0].name.focus();
		return;
	}
	
	if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();
		return;
	}
	
	if(!validateName(document.forms[0].name.value)){
		alert('Name should have following characters. A-Z, a-z, 0-9, _ and - ');
		document.forms[0].name.focus();
		return;
	}

	document.forms[0].action.value="update";
	document.forms[0].submit();
}


function validateName(val){

	var test1 = /(^[A-Za-z0-9-_]*$)/;
	var regexp =new RegExp(test1);
	if(regexp.test(val)){
		return true;
	}
	return false;
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.COPY_PACKET_TRANSLATION_MAPPING_CONFIG%>',searchName,'update','<%=copyPacketMappingConfData.getCopyPacketTransConfId()%>','verifyNameDiv');
	if(isValidName == true){
	isValidTranslationName = verifyInstanceName('<%=InstanceTypeConstants.TRANSLATION_MAPPING_CONFIG%>',searchName,'update','<%=copyPacketMappingConfData.getCopyPacketTransConfId()%>','verifyNameDiv');
	}
}
 $(function(){
	document.forms[0].name.focus();
	
	/* Script Autocomplete */
	setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
}) 

</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<html:form action="/updateCopyPacketMappingConfig">
	
		<html:hidden name="updateCopyPacketMappingConfigForm"
			styleId="copyPacketTransConfId" property="copyPacketTransConfId"
			value="<%=copyPacketMappingConfData.getCopyPacketTransConfId()%>" />
		
		<html:hidden name="updateCopyPacketMappingConfigForm"
			styleId="action" property="action" />
		
		<html:hidden name="updateCopyPacketMappingConfigForm" styleId="auditId" property="auditId"/>
		
		<bean:define id="copyPacketMappingConfBean"
			name="copyPacketMappingConfData" scope="request"
			type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData" />
		
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="servermgrResources" key="copypacket.updatebasicdetail" />
						</td>
					</tr>
					<tr>

						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="servermgrResources" 
								key="copypacket.name" />
									<ec:elitehelp headerBundle="servermgrResources" 
										text="copypacket.name" 
											header="copypacket.name"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="25%" nowrap="nowrap" colspan="3">
							<html:text property="name" tabindex="1" size="47" styleId="name" onkeyup="verifyName();"
								style="width:250px" /> <font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servermgrResources" 
								key="copypacket.description" />
									<ec:elitehelp headerBundle="servermgrResources" 
										text="copypacket.description" 
											header="copypacket.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:textarea property="description" tabindex="2"
								styleId="description" rows="2" cols="50" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td class="captiontext" valign="top" >
							<bean:message bundle="servermgrResources" 
								key="copypacket.script" />
									<ec:elitehelp headerBundle="servermgrResources" 
										text="copypacket.script" 
											header="copypacket.script"/>
						</td>
						<td class="labeltext" valign="top" nowrap="nowrap" >
							<html:text property="script" styleId="script" size="30" style="width:250px" maxlength="255" tabindex="3" styleClass="esiScriptAutocomplete"></html:text>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="3"><input
							type="button" value="Update " tabindex="4" class="light-btn"
							onclick="validation();" /> <input type="reset"
							name="c_btnDeletePolicy" tabindex="5"
							onclick="javascript:location.href='<%=basePath%>/viewCopyPacketConfigBasicDetail.do?copyPacketTransConfId=<%=copyPacketMappingConfBean.getCopyPacketTransConfId()%>'"
							value="Cancel" class="light-btn"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		
</table>
</html:form>


