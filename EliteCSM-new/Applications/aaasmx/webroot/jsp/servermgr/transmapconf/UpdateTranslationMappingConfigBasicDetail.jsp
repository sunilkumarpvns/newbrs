<%@ page import="com.elitecore.elitesm.web.servermgr.transmapconf.forms.UpdateTranslationMappingConfigForm"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.commons.base.Collectionz"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>

<%
	TranslationMappingConfData translationMappingConfData = (TranslationMappingConfData) request.getAttribute("translationMappingConfData");
	UpdateTranslationMappingConfigForm updateTranslationMappingConfigForm = (UpdateTranslationMappingConfigForm) request.getAttribute("updateTranslationMappingConfigForm");
%>

<script>

var isValidName;
var scriptInstanceList = [];

function validate()
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
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.TRANSLATION_MAPPING_CONFIG%>',searchName,'update','<%=translationMappingConfData.getTranslationMapConfigId()%>','verifyNameDiv');
	if(isValidName == true){
		isValidTranslationName = verifyInstanceName('<%=InstanceTypeConstants.COPY_PACKET_TRANSLATION_MAPPING_CONFIG%>',searchName,'update','<%=translationMappingConfData.getTranslationMapConfigId()%>','verifyNameDiv');
	}
}

<% 
if( Collectionz.isNullOrEmpty(updateTranslationMappingConfigForm.getScriptDataList()) == false ){
	for( ScriptInstanceData scriptInstData : updateTranslationMappingConfigForm.getScriptDataList()){ %>
		scriptInstanceList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

$(document).ready(function(){
	
	/* Autocomplete list of Translation Mapping Script Instances */
	setSuggestionForScript(scriptInstanceList, "scriptInstAutocomplete");
});

</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<html:form action="/updateTranslationMappingConfigBasicDetail">
		<html:hidden name="updateTranslationMappingConfigForm" styleId="translationMapConfigId" property="translationMapConfigId" value="<%=translationMappingConfData.getTranslationMapConfigId()%>" />
		<html:hidden name="updateTranslationMappingConfigForm" styleId="action" property="action" />
		<html:hidden name="updateTranslationMappingConfigForm" styleId="auditUid" property="auditUid"/>
		<bean:define id="translationMappingConfBean" name="translationMappingConfData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData" />
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" 	height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="servermgrResources" key="translationmapconf.basicdetail" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="servermgrResources" key="translationmapconf.name" />
							<ec:elitehelp headerBundle="servermgrResources" text="transmapping.name" header="translationmapconf.name"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="25%" nowrap="nowrap" colspan="3">
							<html:text property="name" tabindex="1" size="47" styleId="name" onkeyup="verifyName();" style="width:250px" /> 
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servermgrResources" key="translationmapconf.desc" />
							<ec:elitehelp headerBundle="servermgrResources" text="transmapping.desc" header="translationmapconf.desc"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:textarea property="description" tabindex="2" styleId="description" rows="2" cols="50" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td class="captiontext" valign="top" >
						 	<bean:message bundle="servermgrResources" key="translationmapconf.script" />
							<ec:elitehelp headerBundle="servermgrResources" text="transmapping.script" header="translationmapconf.script"/>
						</td>
						<td class="labeltext" valign="top" nowrap="nowrap" >
							<html:text property="script" size="30" style="width:250px" maxlength="255" tabindex="3" styleClass="scriptInstAutocomplete"></html:text>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="3">
							<input type="button" value="Update " tabindex="4" class="light-btn" onclick="validate()" /> 
							<input type="reset" name="c_btnDeletePolicy" tabindex="5" onclick="javascript:location.href='<%=basePath%>/viewTranslationMappingConfigBasicDetail.do?translationMapConfigId=<%=translationMappingConfBean.getTranslationMapConfigId()%>'" value="Cancel" class="light-btn">
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
</table>
</html:form>