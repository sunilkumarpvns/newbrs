<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData"%>
<%@page import="com.elitecore.elitesm.web.diameter.sessionmanager.form.UpdateDiameterSessionManagerForm"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData"%>
<%@page import="com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions"%>
<%@page import="com.elitecore.diameterapi.core.translator.TranslatorConstants" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.ScenarioMappingData" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionOverideActionData" %>
<%@page import="com.elitecore.aaa.diameter.sessionmanager.DBOperationAction" %>

<%
	UpdateDiameterSessionManagerForm updateDiameterSessionManagerForm = (UpdateDiameterSessionManagerForm)request.getAttribute("updateDiameterSessionManagerForm");
	DiameterSessionManagerData diameterSessionManagerDataObj = (DiameterSessionManagerData)request.getAttribute("diameterSessionManagerData");	
	Set<ScenarioMappingData> scenarioMappingDataSet = diameterSessionManagerDataObj.getScenarioMappingDataSet();
	Set<SessionOverideActionData> sessionOverideActionDataSet = diameterSessionManagerDataObj.getSessionOverideActionDataSet();
	String databaseId=updateDiameterSessionManagerForm.getDatabaseId();
	String tableName= updateDiameterSessionManagerForm.getTablename(); 
%>
<script>
var dbId = '<%=databaseId%>';
var autoCompleteArrayJson = {};
</script>
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-session-manager.js"></script>
<script>
	$(document).ready(function(){
		
		dbId = '<%=databaseId%>';
		
		$('.dbScenarioTable td img.delete').live('click',function() {
			 $(this).parent().parent().remove(); 
		});
		
		$('.sessionActionOverideTable td img.delete').live('click',function() {
			 $(this).parent().parent().remove(); 
		});
		
		<%for(ScenarioMappingData scenarioMappingData:scenarioMappingDataSet) {%>
			addUpdatedScenarioMapping('<%=(scenarioMappingData.getName() == null)?"":scenarioMappingData.getName()%>','<%=(scenarioMappingData.getDescription() == null)?"":scenarioMappingData.getDescription()%>','<%=(scenarioMappingData.getRuleset() == null)?"":scenarioMappingData.getRuleset()%>','<%=(scenarioMappingData.getCriteria() == null)?"":scenarioMappingData.getCriteria()%>','<%=scenarioMappingData.getMappingName()%>');
		<%}%>
		<%for(SessionOverideActionData sessionOverideActionData:sessionOverideActionDataSet){%>
			addUpdatedSessionOverideActionData('<%=(sessionOverideActionData.getName() == null)?"":sessionOverideActionData.getName()%>','<%=(sessionOverideActionData.getDescription() == null)?"":sessionOverideActionData.getDescription()%>','<%=(sessionOverideActionData.getRuleset() == null)?"":sessionOverideActionData.getRuleset()%>','<%=sessionOverideActionData.getActions()%>');
		<%}%>
	});
	function addUpdatedScenarioMapping(name,description,ruleSet,criteria,mappingName){
		var htmlSource ="<tr><td class='allborder'>"+
		"<input type='text' name='scenarioName' id='scenarioName' class='scenarioName noborder' value='"+name+"' style='width: 100%'/></td>"+
		"<td class='tblrows'><textarea rows='1' cols='1' class='scenarioDescription noborder' name='scenarioDescription' id='scenarioDescription' style='width: 100%;height:20px;'>"+description+"</textarea></td>"+
		"<td class='tblrows'><textarea rows='1' cols='1' class='scenarioRuleset noborder' name='scenarioRuleset' id='scenarioRuleset' style='width: 100%;height:20px;' maxlength='2000'>"+ruleSet+"</textarea></td>"+
		"<td class='tblrows'><input class='scenarioCriteria noborder' type='text' name='scenarioCriteria' value='"+criteria+"' maxlength='1000' size='28' style='width:100%' tabindex='10'  onblur='setCriteriaData(this);'/></td>"+
		"<td class='tblrows'><select class='scenarioMapping' name='scenarioMapping' id='scenarioMapping' size='1' style='width: 100%;' onchange='setAutoCompleteForSelectedMapping(this)'><option value='0' selected='selected'>--Select--</option>"+
		"<logic:iterate id='sessionManagerMappingObj' property='diameterSessionManagerMappingDataSet' name='updateDiameterSessionManagerForm' type='com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData'>"+
		"<option value='<bean:write name='sessionManagerMappingObj' property='mappingName'/>' id-value='<bean:write name='sessionManagerMappingObj' property='mappingId'/>'><bean:write name='sessionManagerMappingObj' property='mappingName'/></option>"+
		"</logic:iterate></select></td><td class='tblrows' align='center' colspan='3'><img value='top' src='<%=request.getContextPath()%>/images/minus.jpg'  class='delete' style='padding-right: 5px; padding-top: 5px;' tabindex='10'/></td></tr>";						
	
		$('#dbScenarioTable').append(htmlSource);
		var tableObj= $('#dbScenarioTable').find("tr:last");
		var esiObj=$(tableObj).find("select[name='scenarioMapping']");
		$(esiObj).val(mappingName);
		setAutoCompleteForSelectedMapping(esiObj);
		
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
	function addUpdatedSessionOverideActionData(name,description,ruleSet,criteria){
		var htmlSource="<tr><td class='allborder'>"+
						"<input type='text' name='sessionName' id='sessionName' class='sessionName noborder' value='"+name+"' style='width: 100%'/></td>"+
						"<td class='tblrows'><textarea rows='1' cols='1' class='sessionDescription noborder' name='sessionDescription' id='sessionDescription' style='width: 100%;height:20px;'>"+description+"</textarea></td>"+
						"<td class='tblrows'><textarea rows='1' cols='1' class='sessionRuleset noborder' name='sessionRuleset' id='sessionRuleset' style='width: 100%;height:20px;'>"+ruleSet+"</textarea>"+
						"</td><td class='tblrows'><select name='sessionCriteria' id='sessionCriteria' class='sessionCriteria' size='1' style='width: 100%;' >"+
						"<option value='0'>--Select--</option>"+
						"<logic:iterate id='dbOperationAction'  collection='<%=DBOperationAction.values() %>'>"+
						"<option value='<%=((DBOperationAction)dbOperationAction).name()%>'><%=((DBOperationAction)dbOperationAction).name()%></option>"+
						"</logic:iterate>"+
						"</select></td><td class='tblrows' align='center' colspan='3'>"+
						"<img value='top' src='<%=request.getContextPath()%>/images/minus.jpg'  class='delete' style='padding-right: 5px; padding-top: 5px;' height='14'  tabindex='10'/></td></tr>";

		$('#sessionActionOverideTable').append(htmlSource);
		var tableObj= $('#sessionActionOverideTable').find("tr:last");
		var esiObj=$(tableObj).find("select[name='sessionCriteria']");
		$(esiObj).val(criteria);
	}
	
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
				alert("Please Select at least one criteria from drop down");
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
	 
	function validateUpdate(){
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
	     	document.forms[0].action.value='Update';
	        document.forms[0].submit();
		}
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
	
	//Removes table mappings
	function removeComponent(compId){
		$(compId).remove();
	}
</script>
<html:form action="/updateDiameterSessionManagerScenario" styleId="sessionManagerForm">
	<html:hidden name="updateDiameterSessionManagerForm" styleId="sessionManagerId" property="sessionManagerId" />
	<html:hidden name="updateDiameterSessionManagerForm" styleId="auditUId" property="auditUId" />	
	<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
		<tr>
			<td class="box" valign="middle" colspan="5" class="table-header">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
					<tr>
						<td align="left" class="tblheader table-header" valign="top" colspan="4">
							<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario" /> 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="4" style="padding-top: 10px;">
							<input type="button" name="c_btnAddScenario" onclick="addScenarioMappings('dbScenarioTable','dbScenarioTemplateTable');" id="btnAddMapping" value="Add Scenario" class="light-btn" tabindex="52"/>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%" colspan="4">
							<table width="97%" cellspacing="0" cellpadding="0" border="0" id="dbScenarioTable" class="dbScenarioTable" style="margin-bottom: 10px;">
								<tr>
									<td align="left" class="tblheader" valign="top" width="18%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.session.name" />
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.session.name" header="diameter.sessionmanager.session.name"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="18%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.session.description" />
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.session.description" header="diameter.sessionmanager.session.description"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="18%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario.ruleset" />
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.scenario.ruleset" header="diameter.sessionmanager.scenario.ruleset"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="22%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario.criteria" />
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.scenario.criteria" header="diameter.sessionmanager.scenario.criteria"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="18%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario.mappings" /> 
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.scenario.mappings" header="diameter.sessionmanager.scenario.mappings"/>
									</td>
									<td align="left" class="tblheader" valign="top"width="6%">Remove</td>
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
						<td align="left" class="captiontext" valign="top" colspan="4" style="padding-top: 10px;">
							<input type="button" name="c_btnAddScenario" onclick="addSessionOverrideMappings('sessionActionOverideTable','sessionActionOverideTemplateTable');" id="btnAddMapping" value="Add Action" class="light-btn" tabindex="52"/>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%" colspan="4">
							<table width="97%" cellspacing="0" cellpadding="0" border="0" id="sessionActionOverideTable" class="sessionActionOverideTable">
								<tr>
									<td align="left" class="tblheader" valign="top" width="18%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.session.name" />
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.session.name" header="diameter.sessionmanager.session.name"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="28%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.session.description" />
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.session.description" header="diameter.sessionmanager.session.description"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="28%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.scenario.ruleset" />
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.scenario.ruleset" header="diameter.sessionmanager.scenario.ruleset"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="18%">
										<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.sessionoverideaction.action" />
										<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.sessionoverideaction.action" header="diameter.sessionmanager.sessionoverideaction.action"/>
									</td>
									<td align="left" class="tblheader" valign="top"width="6%">Remove</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle">&nbsp;</td>
			<td class="btns-td" valign="middle" colspan="2">
				<input type="button" name="c_btnCreate" id="c_btnCreate2" value="Update" class="light-btn" tabindex="17" onclick="validateUpdate();"/> 
				<input type="reset" name="c_btnDeletePolicy" tabindex="18" onclick="javascript:location.href='<%=basePath%>/viewDiameterSessionManager.do?sessionManagerId=<%=updateDiameterSessionManagerForm.getSessionManagerId()%>'" value="Cancel" class="light-btn">
			</td>
		</tr>
	</table>
			
</html:form>
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
			<textarea rows="1" cols="1" class="scenarioRuleset noborder" name="scenarioRuleset" id="scenarioRuleset" style="width: 100%;height:20px;"></textarea>
		</td>
		<td class="tblrows"><input  class="scenarioCriteria noborder" type="text" name="scenarioCriteria"  maxlength="1000" size="28" style="width:100%" tabindex="10"  onblur='setCriteriaData(this);'/></td>
		<td class="tblrows">
			<select name="scenarioMapping" id="scenarioMapping" size="1" class="scenarioMapping" style="width: 100%;" onchange="setAutoCompleteForSelectedMapping(this)">
				<option value="0">--Select--</option>
				<logic:iterate id="sessionManagerMappingObj" property="diameterSessionManagerMappingDataSet" name="updateDiameterSessionManagerForm" type="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData">
					<option value='<bean:write name="sessionManagerMappingObj" property="mappingName"/>' id-value='<bean:write name="sessionManagerMappingObj" property="mappingId"/>'><bean:write name="sessionManagerMappingObj" property="mappingName"/></option>
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
			<textarea rows="1" cols="1" class="sessionRuleset noborder" name="sessionRuleset" id="sessionRuleset" style="width: 100%;height:20px;"></textarea>
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