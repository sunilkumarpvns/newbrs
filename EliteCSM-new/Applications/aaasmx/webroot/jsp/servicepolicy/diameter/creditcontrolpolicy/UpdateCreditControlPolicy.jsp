<%@ page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCResponseAttributes"%>
<%@ page import="java.util.*"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@ page import="com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ServiceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.CommandCode" %>
<%@ page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCResponseAttributes" %>
<%@ page import="com.elitecore.elitesm.util.constants.PolicyPluginConstants"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData"%>
<%@page import="com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%
	UpdateCreditControlPolicyForm form = (UpdateCreditControlPolicyForm)request.getAttribute("policyForm");

	DriverBLManager driverManager = new DriverBLManager();
	List<DriverInstanceData> listOfDriver = driverManager.getDriverInstanceList(ServiceTypeConstants.NAS_CREDIT_CONTROL_APPLICATION);
	
	
	String[] driverInstanceIds = new String [listOfDriver.size()];
	String[][] driverInstanceNames = new String[listOfDriver.size()][3]; 
	
	for(int i=0;i<listOfDriver.size();i++){
		DriverInstanceData data = listOfDriver.get(i);				
			driverInstanceNames[i][0] = String.valueOf(data.getName());
			driverInstanceNames[i][1] = String.valueOf(data.getDescription());
			driverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
		driverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
	}
	
	CreditControlPolicyData creditControlPolicyData = (CreditControlPolicyData)request.getAttribute("ccPolicyData");
	
	Set<CCResponseAttributes> ccResponseAttributes = form.getCcResponseAttributesSet();
	
	List<PluginInstData> pluginInstDataList = (List<PluginInstData>) request.getAttribute("pluginInstDataList");
%>


<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms.UpdateCreditControlPolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlDriverRelationData"%>

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-policy.js"></script>
<script>
setExpressionData("Diameter");
var supportedDriverList;
var isValidName;
var driverScriptList = [];

<% 
if( Collectionz.isNullOrEmpty(form.getDriverScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : form.getDriverScriptList()){ %>
		driverScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

function validate()
{	
	if(!validateName(document.forms[0].name.value)){
	alert('Policy Name should have following characters. A-Z, a-z, 0-9, _ and - ');
	document.forms[0].name.focus();
	return;
	}
	
	if(isNull(document.forms[0].name.value)){
		alert('Policy Name must be specified');
		document.forms[0].name.focus();
		return;
	}

	if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return;
	}
	
	if(isNull(document.forms[0].ruleSet.value)){
		alert('Policy Selection Rule must be specified');
		document.forms[0].ruleSet.focus();
		return;
	}

	
	var behaviour = $("#defaultResponseBehaviour").val();
	if(behaviour == "REJECT" || behaviour == "HOTLINE"){
		if($("#defaultResponseBehaviorArgument").val().trim() == ""){
			alert("Default Response Behaviour Argument must be specified when Default Response Behaviour is "+behaviour);
			$("#defaultResponseBehaviorArgument").focus();
			return false;
		}
	}
	
	if(supportedDriverList.options.length == 0){
		alert('Driver must be specified');
		return;
	}	
	
	if(!isValidMappings()){
		return;
	}
	
	if(!validatePlugins('pre-plugin-mapping-table','post-plugin-mapping-table','prePluginsList','postPluginList')){
		return;
	}
	
	
	selectAll(supportedDriverList);	
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
function selectAll(selObj){
	if(selObj!=null){
		for(var i=0;i<selObj.options.length;i++){
			selObj.options[i].selected = true;
		}
	}
}

var jdriverNames = new Array();
var jdriverInstanceIds = new Array();
var count=0;

$(document).ready(function() {		
	
	 /* Script Autocomplete */
	setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
	
	jdriverNames.length = <%=listOfDriver.size()%>;				
	jdriverInstanceIds.length= <%=listOfDriver.size()%>;
		
	
	<%int j,k=0;
	for(j =0;j<listOfDriver.size();j++){%>		
		jdriverNames[<%=j%>] = new Array(3);		
			<%for(k=0;k<3;k++){%>												
				jdriverNames[<%=j%>][<%=k%>] = '<%=driverInstanceNames[j][k]%>'				
			<%}%>
		jdriverInstanceIds[<%=j%>] = '<%=driverInstanceIds[j]%>'	
		count ++;							
	<%}%>	 	 

	var headersArr = new Array(5);
	headersArr[0] = '';
	headersArr[1] = 'Driver Name';
	headersArr[2] = 'Driver Description';
	headersArr[3] = 'Driver Type';
	headersArr[4] = 'Weightage';
	
	initializeData(jdriverInstanceIds,jdriverNames,'addDriver','driverDataCheckBoxId',headersArr,'true',count);
	hideSelectedData('selecteddriverIds','driverDataCheckBoxId');
	
	$('.responseAttributeTable td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
	});
	
	setCommandCodeAutocompleteData();
   }
);

function driverpopup(){
	openpopup('driverPopup','driverDataCheckBoxId',jdriverInstanceIds,jdriverNames,'selecteddriverIds');
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.CREDIT_CONTROL_POLICY%>',searchName,'update','<%=creditControlPolicyData.getPolicyId()%>','verifyNameDiv');
}


var commandCodeList = [];
	
<%for(CommandCode commandCode:CommandCode.VALUES){%>
	commandCodeList.push({'value':'<%=commandCode.code%>','label':'[<%=commandCode.code%>] <%=commandCode.name()%>','id':'<%=commandCode.code%>'});
<%}%>
function splitData( val ) {
	return val.split( /[,;]\s*/ );
}

function extractLastItems( term ) {
	return splitData( term ).pop();
}
function setCommandCodeAutocompleteData(){
		$( '.commandCode' ).bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB && $( this ).autocomplete( "instance" ).menu.active ) {
				event.preventDefault();
			}
		}).autocomplete({
		minLength: 0,
		source: function( request, response ) {
			response( $.ui.autocomplete.filter(
				commandCodeList, extractLastItems( request.term ) ) );
			},
		focus: function() {
			return false;
		},
		select: function( event, ui ) {
			var val = this.value;
 			var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
 			var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
 			 if(commaIndex == semiColonIndex) {
 					val = "";
 			}  else if(commaIndex > semiColonIndex) {
 					val = val.substring(0,commaIndex+1); 
 			} else if(semiColonIndex !=0 && semiColonIndex > commaIndex){
 				val = val.substring(0,semiColonIndex+1); 
 			}	 
 			this.value = val + ui.item.value ;
 			return false;
		}
		});
}

function setCommandCodeData(commandCodeObj){
	var commandCodevalue=$(commandCodeObj).val();
	commandCodevalue=commandCodevalue.trim();
	var lastChar = commandCodevalue.charAt(commandCodevalue.length - 1);
	if(lastChar == ","){
		var result = commandCodevalue.substring(0, commandCodevalue.length-1);
		$(commandCodeObj).val(result);
	}
}
function addResponseAttributesTable(tableId,templateId){
		var tableRowStr = $("#"+templateId).find("tr");
		$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
		$("#"+tableId+" tr:last").find("input:first").focus();
		setCommandCodeAutocompleteData();
}

function uncheckAllCheckBoxes(pluginNames){
		$("input:checkbox[name='"+pluginNames+"']").each(function(){
			if($(this).attr('checked')){
				$(this).attr('checked',false);
			}
		});
}
	
function isValidMappings(){
	var isValidMapping = true;
	$('.responseAttributeTable').find('.commandCode').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == 0) {
			alert("Command Code must be Specified");
			isValidMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidMapping;
}

var pluginList = [];
<%if(pluginInstDataList != null){%>
	<logic:iterate id="pluginBean" name="pluginInstDataList">
		pluginList.push({
			'id' : '<bean:write property="pluginInstanceId" name="pluginBean" />',
			'value' : '<bean:write property="pluginTypeId" name="pluginBean" />',
			'label' : '<bean:write property="name" name="pluginBean" />'
		});
	</logic:iterate>
<%}%>
</script>

<html:form action="/updateCcpolicy">
<html:hidden name="updateCcpolicyForm" styleId="policyId" property="policyId" />
<html:hidden name="updateCcpolicyForm" styleId="action" property="action" />
<html:hidden name="updateCcpolicyForm" styleId="auditUId" property="auditUId" />
<html:hidden name="updateCcpolicyForm" property="prePluginsList" styleId="prePluginsList"/>
<html:hidden name="updateCcpolicyForm" property="postPluginList" styleId="postPluginList"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" 	height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.ccpolicy" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.name" />
							<ec:elitehelp  header="Name" headerBundle="servicePolicyProperties" text="creditControlpolicy.name" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap"
							colspan="2"><html:text property="name" size="35"
								onkeyup="verifyName();" styleId="name" style="width: 250px"
								tabindex="1" /> <font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="servicePolicyProperties"
								key="servicepolicy.naspolicy.desp" />
						</td>
						<td align="left" class="labeltext" valign="top" width="25%"
							colspan="2"><html:textarea property="description"
								styleId="description" rows="2" cols="50" style="width: 250px"
								tabindex="2" /></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.ruleset" /> 
							<ec:elitehelp  header="Ruleset" headerBundle="servicePolicyProperties" text="authpolicy.ruleset" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" width="25%"
							colspan="2"><html:textarea property="ruleSet"
								styleId="ruleSet" rows="2" cols="50" style="width: 250px"
								tabindex="3" /><font color="#FF0000"> *</font> <img
							alt="Expression" src="<%=basePath%>/images/lookup.jpg"
							onclick="popupExpression('ruleSet');" tabindex="4" /></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.ccpolicy.sessionmanagement" /> 
							<ec:elitehelp  header="servicepolicy.ccpolicy.sessionmanagement" headerBundle="servicePolicyProperties" text="servicepolicy.ccpolicy.sessionmanagement" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" width="25%" colspan="2">
							<html:select property="sessionManagement" styleId="sessionManagement">
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>
							</html:select>
						</td>
					</tr>
					<tr>	
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.creditcontrolpolicy.defaultresponsebehaviour" /> 
								<ec:elitehelp  header="servicepolicy.creditcontrolpolicy.defaultresponsebehaviour"  headerBundle="servicePolicyProperties" text="servicepolicy.ccpolicy.defaultauthresponsebehavior"></ec:elitehelp>
							</td>
							<td align="left" class="labeltext" valign="top">
								<html:select name="updateCcpolicyForm" styleId="defaultResponseBehaviour" property="defaultResponseBehaviour" style="width:90px" tabindex="9" >
									<logic:iterate id="behaviour"  collection="<%=DefaultResponseBehavior.DefaultResponseBehaviorType.values() %>">
										<html:option value="<%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%>"><%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%></html:option>
									</logic:iterate>
								</html:select>
							</td>
						</tr>
					<tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.creditcontrolpolicy.defaultresponsebehaviourargument" /> 
								<ec:elitehelp  header="servicepolicy.creditcontrolpolicy.defaultresponsebehaviourargument" headerBundle="servicePolicyProperties" text="servicepolicy.ccpolicy.defaultresponsebehaviourargument" ></ec:elitehelp>
							</td>
							<td align="left" class="labeltext" valign="top">
								<html:text property="defaultResponseBehaviorArgument" name="updateCcpolicyForm" styleId="defaultResponseBehaviorArgument"  maxlength="1000" styleClass="textbox_width" tabindex="10" />
							</td> 
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.driverdetails" />
						</td>
					</tr>
					<tr>
						<td width="20%" align="left" class="captiontext" valign="top">
							Driver Details 
							<ec:elitehelp  header="Driver Group" headerBundle="servicePolicyProperties" text="authpolicy.drivergroup" ></ec:elitehelp>
						</td>
						<td width="30%" align="left" class="labeltext" valign="top">
							<html:select property="selecteddriverIds" multiple="true"
								styleId="selecteddriverIds" size="5" style="width: 250px"
								tabindex="5">

								<%
													
												List<CreditControlDriverRelationData> driverList = form.getDriversList();												
												if(driverList != null){
													for(int i = 0;i<driverList.size();i++){
																										
														CreditControlDriverRelationData data = driverList.get(i);
														String nm = " ";
														if(data.getWeightage() != null)
															nm = data.getWeightage().toString();
														nm = data.getDriverData().getName() + "-W-" + nm;		
														String value = data.getDriverInstanceId() + "-" + data.getWeightage();
													%>
								<html:option value="<%=value%>"><%=nm%></html:option>
								<%}
												}%>
							</html:select> <font color="#FF0000">*</font>
						</td>

						<td align="left" class="labeltext" valign="top"><input
							type="button" value="Add " onClick="driverpopup()"
							class="light-btn" style="width: 75px" tabindex="6" /><br /> <br />
							<input type="button" value="Remove "
							onclick="removeData('selecteddriverIds','driverDataCheckBoxId');"
							class="light-btn" style="width: 75px" tabindex="7" /></td>
					</tr>
					
					<tr>
						<td class="captiontext" valign="top" width="25%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.script" />
							<ec:elitehelp  header="servicepolicy.naspolicy.script" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.script" ></ec:elitehelp>
						</td>
						<td class="labeltext" valign="top" nowrap="nowrap" colspan="2">
							<html:text property="script" size="30" style="width:250px" maxlength="255" tabindex="8" styleClass="scriptInstAutocomplete"></html:text>
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.preplugins" /> 
						</td>
					</tr>
					<tr>
						<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
							<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
								<tr>
									<td class="captiontext" valign="top" colspan="2">
										<input type="button" value="Add Plugin" onClick="addPluginMapping('pre-plugin-mapping-table','pre-plugin-mapping-template');" class="light-btn" tabindex="2" /><br />
									</td>
								</tr>
								<tr>
									<td  class="captiontext" valign="top">
										<table cellspacing="0" cellpadding="0" border="0" width="100%" id="pre-plugin-mapping-table" class="pre-plugin-mapping-table">
											<tr>
												<td class="tbl-header-bold tbl-header-bg" width="47.5%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginname" />
													<ec:elitehelp   header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname" ></ec:elitehelp>
												</td>	
												<td class="tbl-header-bold tbl-header-bg" width="47.5%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginagr" />
													<ec:elitehelp header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg" ></ec:elitehelp>
												</td>	
												<td class="tbl-header-bold tbl-header-bg" width="5%">
													Remove
												</td>	
											</tr>
											
											<logic:iterate id="obj" name="ccPolicyData" property="ccPolicyPluginConfigList">
						          				<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.IN_PLUGIN%>">
							          				<tr>
														<td class="tblfirstcol" width="47.5%">
															<input type="text" name="prePluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);" value='<bean:write name="obj" property="pluginName"/>'/>
														</td>	
														<td class="tblrows" width="47.5%">
															<textarea name="prePluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"><bean:write name="obj" property="pluginArgument"/></textarea>
														</td>	
														<td class="tblrows" width="5%" align="center">
															<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
														</td>	
													</tr>
						          				</logic:equal>
						          			</logic:iterate>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.postplugins" /> 
						</td>
					</tr>
					<tr>
						<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
							<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
								<tr>
									<td class="captiontext" valign="top" colspan="2">
										<input type="button" value="Add Plugin" onClick="addPluginMapping('post-plugin-mapping-table','post-plugin-mapping-template');" class="light-btn" tabindex="2" /><br />
									</td>
								</tr>
								<tr>
									<td  class="captiontext" valign="top">
										<table cellspacing="0" cellpadding="0" border="0" width="100%" id="post-plugin-mapping-table" class="post-plugin-mapping-table">
											<tr>
												<td class="tbl-header-bold tbl-header-bg" width="47.5%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginname" />
													<ec:elitehelp   header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname" ></ec:elitehelp>
												</td>	
												<td class="tbl-header-bold tbl-header-bg" width="47.5%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginagr" />
													<ec:elitehelp header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg" ></ec:elitehelp>
												</td>	
												<td class="tbl-header-bold tbl-header-bg" width="5%">
													Remove
												</td>	
											</tr>
											<logic:iterate id="obj" name="ccPolicyData" property="ccPolicyPluginConfigList">
						          				<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.OUT_PLUGIN%>">
							          				<tr>
														<td class="tblfirstcol" width="47.5%">
															<input type="text" name="postPluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);" value='<bean:write name="obj" property="pluginName"/>'/>
														</td>	
														<td class="tblrows" width="47.5%">
															<textarea name="postPluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"><bean:write name="obj" property="pluginArgument"/></textarea>
														</td>	
														<td class="tblrows" width="5%" align="center">
															<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
														</td>	
													</tr>
						          				</logic:equal>
					          				</logic:iterate>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattributes" />
						</td>
					</tr>
					<tr>
						<td colspan="4" style="padding-bottom: 10px;">
							<table border="0" width="100%" cellspacing="0" cellpadding="0">
								<tr>
									<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
										<input type="button" value=" Add Mapping " class="light-btn" onclick="addResponseAttributesTable('responseAttributeTable','responseAttributesTemplate');" tabindex="25" />
									</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
										<!-- Attributes Table -->
										<table width="60%" cellspacing="0" cellpadding="0" border="0" id="responseAttributeTable" class="responseAttributeTable">
											<tr>
												<td align="left" class="tblheader" valign="top" width="35%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.commandcode" />
													<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.commandcode" header="servicepolicy.naspolicy.commandcode"/>
												</td>
												<td align="left" class="tblheader" valign="top" width="60%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattribute" />
													<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.responseattribute" header="servicepolicy.naspolicy.responseattribute"/>
												</td>
												<td align="left" class="tblheader" valign="top"width="5%">Remove</td>
											</tr>
											<% if(ccResponseAttributes != null){ %>
												<%for(CCResponseAttributes attributes : ccResponseAttributes) {%>
													<tr>
														<td class="allborder" width="35%">
															<input  autocomplete="off" class="commandCode noborder" value="<%=attributes.getCommandCodes() %>" type="text" name="commandCode" maxlength="1000" size="28"  onkeyup="setCommandCode(this);" style="width:200px;" onblur="setCommandCodeData(this);"/></td>
														<td class="tblrows" width="60%">
															<textarea rows="1" class="responseAttributes noborder" name="responseAttributes"  id="responseAttributes"  style="min-width:100%;min-height:20px;height:20px;"><%=(attributes.getResponseAttributes() == null)?"":attributes.getResponseAttributes() %></textarea>
														</td>
														<td class="tblrows" align="center" width="5%"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  /></td>
													</tr>
												<%} %>
											<%} %>
										</table>
									</td>
								</tr>
							 </table>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2"><input
							type="button" value="Update " class="light-btn"
							onclick="validate()" tabindex="9" /> <input type="reset"
							name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/initSearchCcpolicy.do?policyId=<%=form.getPolicyId()%>'"
							value="Cancel" class="light-btn" tabindex="10" /></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>
				<div id="driverPopup" style="display: none;" title="Add Drivers">
					<table id="addDriver" name="addDriver" cellpadding="0"
						cellspacing="0" width="100%" class="box">
					</table>
				</div>
			</td>
		</tr>
</table>

</html:form>

<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>
<script>  
	supportedDriverList=document.getElementById("selecteddriverIds");
</script>
<table style="display:none;" id="responseAttributesTemplate">
	<tr>
		<td class="allborder" width="35%">
			<input  autocomplete="off" class="commandCode noborder" type="text" name="commandCode" maxlength="1000" size="28"  style="width:200px;" onblur="setCommandCodeData(this);"/></td>
		<td class="tblrows" width="60%">
			<textarea rows="1" class="responseAttributes noborder" name="responseAttributes"  id="responseAttributes"  style="min-width:100%;min-height:20px;height:20px;"></textarea>
		</td>
		<td class="tblrows" align="center" width="5%"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  /></td>
	</tr>
</table>

<!-- Mapping Table of Pre Plugin -->
<table class="pre-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="prePluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="prePluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"></textarea>
		</td>	
		<td class="tblrows" width="5%" align="center">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
		</td>	
	</tr>
</table>

<!-- Mapping Table of Post Plugin -->
<table class="post-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="postPluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="postPluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"></textarea>
		</td>	
		<td class="tblrows" width="5%" align="center">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
		</td>	
	</tr>
</table>