<%@page import="com.elitecore.elitesm.util.constants.ExternalSystemConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants" %>
<%@page import="com.elitecore.elitesm.web.diameter.sessionmanager.form.CreateDiameterSessionManagerForm" %>
<%@ page import="java.util.*"%>
<%@page import="com.elitecore.coreradius.commons.util.constants.RadiusConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData"%>
<%@page import="com.elitecore.aaa.diameter.sessionmanager.DBOperationAction" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%
	String basePath = request.getContextPath();
	CreateDiameterSessionManagerForm createDiameterSessionManagerForm = (CreateDiameterSessionManagerForm)request.getAttribute("createDiameterSessionManagerForm");
	String databaseId=createDiameterSessionManagerForm.getDatabaseId();
	String tableName= createDiameterSessionManagerForm.getTablename(); 
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
	var autoCompleteArrayJson = JSON.parse('<%=request.getAttribute("tableFieldMappingsJson")%>');
</script>
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-session-manager.js"></script>
<script type="text/javascript"> 
var isValidName;
var mappingIndex=0;
var dbId = '<%=databaseId%>';
function validateCreate(){
	if(!isValidNameforScenarioMapping()){
		return false;
	}else if(!isValidRuleSetforScenarioMapping()){
		return false;
	}else if(!isValidCriteriaforScenarioMapping()){
		return false;
	}else if(!isValidScenarioMappings()){
		return false;
	}else if(!isValidNameForSessionOverideCriteria()){
		return false;
	}else if(!isValidRulesetForSessionOverideCriteria()){
		return false;
	}else if(!isValidSessionOverideCriteria()){
		return false;
	}else{
     	document.forms[0].action.value='Create';
        document.forms[0].submit();
	}
}
function isValidCriteriaforScenarioMapping(){
	var isValidFieldMapping = true;
	$('.dbScenarioTable').find('.scenarioCriteria').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == "0") {
			alert("Scenario Mapping Criteria must be specified");
			isValidFieldMapping = false;
			$(this).focus();
			return false;
		} else {
			isValidFieldMapping = isCriteriaFromMap(this);
			if(isValidFieldMapping == false){
				return false;	
			}
		}
	});
	return isValidFieldMapping;
}

function isValidRuleSetforScenarioMapping(){
	var isValidFieldMapping = true;
	$('.dbScenarioTable').find('.scenarioRuleset').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == "0") {
			alert("Scenario Mapping Ruleset must be specified");
			isValidFieldMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidFieldMapping;
}

function isValidNameforScenarioMapping(){
	var isValidFieldMapping = true;
	$('.dbScenarioTable').find('.scenarioName').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == "0") {
			alert("Scenario Mapping Name must be specified");
			isValidFieldMapping = false;
			$(this).focus();
			return false;
		} 
	});
	isValidFieldMapping = validateScenarioMappingNameIsDuplicateOrNot();
	return isValidFieldMapping;
}

function isValidScenarioMappings(){
	var isValidFieldMapping = true;
	$('.dbScenarioTable').find('.scenarioMapping').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue == "0") {
			alert("Please Select at least one mapping from drop down");
			isValidFieldMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidFieldMapping;
}

function isValidSessionOverideCriteria(){
	var isValidFieldMapping = true;
	$('.sessionActionOverideTable').find('.sessionCriteria').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue == "0") {
			alert("Please Select at least one Action from drop down");
			isValidFieldMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidFieldMapping;
}

function isValidRulesetForSessionOverideCriteria(){
	var isValidFieldMapping = true;
	$('.sessionActionOverideTable').find('.sessionRuleset').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == "0") {
			alert("In Session Overide Action ruleset must be specified");
			isValidFieldMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidFieldMapping;
}

function isValidNameForSessionOverideCriteria(){
	var isValidFieldMapping = true;
	$('.sessionActionOverideTable').find('.sessionName').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == "0") {
			alert("In Session Overide Action Name must be specified");
			isValidFieldMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidFieldMapping;
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_SESSION_MANAGER%>',searchName,'create','','verifyNameDiv');
}

$(document).ready(function(){
	dbId = '<%=databaseId%>';
	
	$('.dbScenarioTable td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
	});
	
	$('.sessionActionOverideTable td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
	});
});

function addScenarioMappings(mappingTable,templateTableId){
	var tableRowStr = $("#"+templateTableId).find("tr");
	$("#"+mappingTable+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#"+mappingTable+" tr:last").find("input:first").focus();
}
function addSessionOverrideMappings(mappingTable,templateTableId){
	var tableRowStr = $("#"+templateTableId).find("tr");
	$("#"+mappingTable+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#"+mappingTable+" tr:last").find("input:first").focus();
}

function setCriteriaData(criteriaObj){
	var criteriaValue=$(criteriaObj).val();
	criteriaValue=criteriaValue.trim();
	var lastChar = criteriaValue.charAt(criteriaValue.length - 1);
	if(lastChar == ","){
		var result = criteriaValue.substring(0, criteriaValue.length-1);
		$(criteriaObj).val(result);
	}
}
setTitle('<bean:message bundle="sessionmanagerResources" key="sessionmanager.header"/>');
</script>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="3">
										<bean:message bundle="sessionmanagerResources" key="sessionmanager.sessionmanagerconfig" />
									</td>
								</tr>
								<tr>
									<td colspan="3">
									<html:form action="/createDiameterSessionManager">
										<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" cellpadding="0" cellspacing="0">
											 <tr>
												<td align="left" class="tblheader table-header" valign="top" colspan="4">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario" /> 
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="20%">
													<input type="button" name="c_btnAddScenario" onclick="addScenarioMappings('dbScenarioTable','dbScenarioTemplateTable');" id="btnAddMapping" value="Add Scenario" class="light-btn" tabindex="52"/>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="20%" colspan="2" >
													<table width="95%" cellspacing="0" cellpadding="0" border="0" id="dbScenarioTable" class="dbScenarioTable" style="margin-bottom: 10px;">
														<tr>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.session.name" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.session.name" header="diameter.sessionmanager.session.name"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="19.25%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.session.description" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.session.description" header="diameter.sessionmanager.session.description"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="19.25%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario.ruleset" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.scenario.ruleset" header="diameter.sessionmanager.scenario.ruleset"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="19.25%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario.criteria" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.scenario.criteria" header="diameter.sessionmanager.scenario.criteria"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="19.25%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario.mappings" /> 
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.scenario.mappings" header="diameter.sessionmanager.scenario.mappings"/>
															</td>
															<td align="left" class="tblheader" valign="top"width="3%">Remove</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align="left" class="tblheader table-header" valign="top" colspan="4">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.sessionoverideaction" /> 
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="20%">
													<input type="button" name="c_btnAddScenario" onclick="addSessionOverrideMappings('sessionActionOverideTable','sessionActionOverideTemplateTable');" id="btnAddMapping" value="Add Action" class="light-btn" tabindex="52"/>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="20%" colspan="2">
													<table width="95%" cellspacing="0" cellpadding="0" border="0" id="sessionActionOverideTable" class="sessionActionOverideTable">
														<tr>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.session.name" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.session.name" header="diameter.sessionmanager.session.name"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="33.5%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.session.description" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.session.description" header="diameter.sessionmanager.session.description"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="33.5%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario.ruleset" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.scenario.ruleset" header="diameter.sessionmanager.scenario.ruleset"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="15%">
																<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.sessionoverideaction.action" />
																<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.sessionoverideaction.action" header="diameter.sessionmanager.sessionoverideaction.action"/>
															</td>
															<td align="left" class="tblheader" valign="top"width="3%">Remove</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</html:form>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" colspan="3" valign="top">
										&nbsp;</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle"></td>
									<td class="btns-td" valign="middle">
									<input type="reset"
										name="c_btnDeletePolicy"
										onclick="javascript:location.href='<%=basePath%>/getDiameterSessionManager.do?/>'"
										value=" Previous " class="light-btn" tabindex="53"/>
									<input type="button" name="c_btnCreate"
										onclick="validateCreate();"
										id="c_btnCreate2" value=" Create " class="light-btn"
										tabindex="52"/> <input type="reset"
										name="c_btnDeletePolicy"
										onclick="javascript:location.href='<%=basePath%>/searchDiameterSessionManager.do?/>'"
										value="Cancel" class="light-btn" tabindex="53"/></td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
	<!-- Mapping Table Row template -->
	<table style="display:none;" id="dbScenarioTemplateTable">
	<tr>
		<td class="allborder">
			<input type="text" name="scenarioName" id="scenarioName" class="scenarioName noborder" style="width: 100%"/>
		</td>
		<td class="tblrows">
			<textarea rows="1" cols="1" class="scenarioDescription noborder" name="scenarioDescription" id="scenarioDescription" style="width: 100%;height:20px;"></textarea>
		</td>
		<td class="tblrows">
			<textarea rows="1" cols="1" class="scenarioRuleset noborder"  name="scenarioRuleset" id="scenarioRuleset" style="width: 100%;height:20px;"></textarea>
		</td>
		<td class="tblrows"><input  class="scenarioCriteria noborder" type="text" name="scenarioCriteria"  id="scenarioCriteria" maxlength="1000" size="28" style="width:100%" tabindex="10"  onblur='setCriteriaData(this);' /></td>
		<td class="tblrows">
			<select name="scenarioMapping" id="scenarioMapping" class="scenarioMapping" size="1" style="width: 100%;" onchange="setAutoCompleteForSelectedMapping(this)">
				<option value="0">--Select--</option>
				<logic:iterate id="sessionManagerMappingObj" property="diameterSessionManagerMappingDataSet" name="createDiameterSessionManagerForm" type="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData">
					<option value='<bean:write name="sessionManagerMappingObj" property="mappingName"/>'><bean:write name="sessionManagerMappingObj" property="mappingName"/></option>
				</logic:iterate>
			</select>
		</td>
		<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
	</tr>
	</table> 
	
	<!-- Session Action Overide Table template -->
	<table style="display:none;" id="sessionActionOverideTemplateTable">
	<tr>
		<td class="allborder">
			<input type="text" name="sessionName" id="sessionName" class="sessionName noborder" style="width: 100%"/>
		</td>
		<td class="tblrows">
			<textarea rows="1" cols="1" class="sessionDescription noborder" name="sessionDescription" id="sessionDescription" style="width: 100%;height:20px;"></textarea>
		</td>
		<td class="tblrows">
			<textarea rows="1" cols="1" class="sessionRuleset noborder" name="sessionRuleset" id="sessionRuleset" style="width:100%;height: 20px;"></textarea>
		</td>
		<td class="tblrows">
			<select name="sessionCriteria" id="sessionCriteria" class="sessionCriteria" size="1" style="width: 100%;" >
				<option value="0">--Select--</option>
				<logic:iterate id="dbOperationAction"  collection="<%=DBOperationAction.values() %>">
					<option value="<%=((DBOperationAction)dbOperationAction).name()%>"><%=((DBOperationAction)dbOperationAction).name()%></option>
				</logic:iterate>
			</select>
		</td>
		<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
	</tr>
	</table> 
	
	