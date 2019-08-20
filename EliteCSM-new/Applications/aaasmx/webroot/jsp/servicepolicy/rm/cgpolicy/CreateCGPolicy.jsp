<%@page import="com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.CreateCGPolicyForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ServiceTypeConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>

<%
		String basePath = request.getContextPath();
		//code for driver jquery popup
		DriverBLManager driverManager = new DriverBLManager();
		List<DriverInstanceData> listOfDriver = driverManager.getDriverInstanceList(ServiceTypeConstants.CHARGING_GATEWAY_SERVICE);
		
		String[] driverInstanceIds = new String [listOfDriver.size()];
		String[][] driverInstanceNames = new String[listOfDriver.size()][3]; 
		String statusVal=(String)request.getParameter("status");
		for(int i=0;i<listOfDriver.size();i++){
			DriverInstanceData data = listOfDriver.get(i);				
				driverInstanceNames[i][0] = String.valueOf(data.getName());
				driverInstanceNames[i][1] = String.valueOf(data.getDescription());
				driverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
			driverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
		}
		
		CreateCGPolicyForm createCGPolicyForm = (CreateCGPolicyForm)request.getAttribute("createCGPolicyForm"); 
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
setExpressionData("Diameter");
var driverScriptList = [];

<% 
if( Collectionz.isNullOrEmpty(createCGPolicyForm.getDriverScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : createCGPolicyForm.getDriverScriptList()){ %>
		driverScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

 var isValidName;
 $(document).ready(function() {
		var chkBoxVal='<%=statusVal%>';
		if(chkBoxVal=='Inactive'){
			document.getElementById("status").checked=false;
		}else{
			document.getElementById("status").checked=true;
		}
		
		 /* Script Autocomplete */
		setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
	});
 	function validateForm(){
 		if(isNull(document.forms[0].name.value)){
 			alert('Name must be specified');
 		}else if(!isValidName) {
 			alert('Enter Valid Policy Name');
 			document.forms[0].name.focus();
 			return;
 		}else if(isNull(document.forms[0].ruleSet.value)){
 			alert('RuleSet must be specified');
 		}else{
 			var supportedDriverList=document.getElementById("selecteddriverIds");	
 			if(supportedDriverList.options.length > 0){
 				selectAll(supportedDriverList);
 				document.forms[0].action.value = 'create';
 	 	 		document.forms[0].submit();	 				
 			}else{
				alert('Driver must be specified');	
 	 		}
 		}		
	}

 	// code for driver related jquery popup
 	var jdriverNames = new Array();
 	var jdriverInstanceIds = new Array();
 	var count=0;

 	$(document).ready(function() {		
 		
 		jdriverNames.length = <%=listOfDriver.size()%>;				
 		jdriverInstanceIds.length= <%=listOfDriver.size()%>;
 			
 		
 		<%int j,k=0;
 		for(j =0;j<listOfDriver.size();j++){%>		
 			jdriverNames[<%=j%>] = new Array(2);		
 				<%for(k=0;k<3;k++){%>												
 					jdriverNames[<%=j%>][<%=k%>] = '<%=driverInstanceNames[j][k]%>'				
 				<%}%>
 			jdriverInstanceIds[<%=j%>] = '<%=driverInstanceIds[j]%>'	
 			count ++;							
 		<%}%>	 	 

 		var headersArr = new Array(4);
 		headersArr[0] = '';
 		headersArr[1] = 'Driver Name';
 		headersArr[2] = 'Driver Description';
 		headersArr[3] = 'Driver Type';
 		headersArr[4] = 'Weightage';
 		
 		initializeData(jdriverInstanceIds,jdriverNames,'addDriver','driverDataCheckBoxId',headersArr,'true',jdriverInstanceIds.length);
 	   }
 	);

 	function driverpopup(){	
 		openpopup('driverPopup','driverDataCheckBoxId',jdriverInstanceIds,jdriverNames,'selecteddriverIds');
 	 }

 	function removeDrivers(){
 		removeData('selecteddriverIds','driverDataCheckBoxId');	
 	 }

 	function selectAll(selObj){
 		for(var i=0;i<selObj.options.length;i++){
 			selObj.options[i].selected = true;
 		}
 	}

 	function verifyName() {
 		var searchName = document.getElementById("name").value;
 		isValidName = verifyInstanceName('<%=InstanceTypeConstants.CHARGING_POLICY%>',searchName,'create','','verifyNameDiv');
 	}

 	setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy"/>');	
</script>

<html:form action="/createCGPolicy">

	<html:hidden name="createCGPolicyForm" property="action" />
	<html:hidden name="createCGPolicyForm" property="policyId" />

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
									<td class="table-header">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.create" />
									</td>
								</tr>
								<tr>
									<td valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.name" /> 
													<ec:elitehelp  header="servicepolicy.cgpolicy.name" headerBundle="servicePolicyProperties" text="cgpolicy.name" ></ec:elitehelp>
												</td>
												<td align="left" class="labeltext" valign="top" nowrap="nowrap">
													<html:text styleId="name" property="name" size="30" onkeyup="verifyName();" maxlength="60" style="width:250px" tabindex="1" />
													<font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
												<td align="left" class="labeltext" valign="top">
													<input type="checkbox" name="status" id="status" value="1" checked="true" tabindex="2" />&nbsp;Active
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.desp" />
												</td>
												<td align="left" class="labeltext" valign="top" width="30%" colspan="2">
													<html:textarea styleId="description" property="description" cols="40" rows="2" style="width:250px" tabindex="3" />
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.ruleset" />
													<ec:elitehelp  header="servicepolicy.cgpolicy.ruleset" headerBundle="servicePolicyProperties" text="cgpolicy.ruleset" ></ec:elitehelp>
												</td>
												<td align="left" class="labeltext" valign="top" width="30%" colspan="2">
													<html:textarea styleId="ruleSet" property="ruleSet" cols="40" rows="2" style="width:250px" tabindex="4" /> 
													<font color="#FF0000"> *</font> 
													<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleSet');" tabindex="5" />
												</td>
											</tr>

											<tr>
												<td width="18%" align="left" class="captiontext" valign="top">
													Driver Group<font color="#FF0000">*</font>
													<ec:elitehelp  header="Driver Group" headerBundle="servicePolicyProperties" text="cgpolicy.drivergroup" ></ec:elitehelp>
												</td>
												<td width="30%" align="left" class="labeltext" valign="top">
													<select class="labeltext" name="selecteddriverIds" id="selecteddriverIds" multiple="true" size="5" style="width: 250px;" tabindex="6">
													</select>
												</td>

												<td align="left" class="labeltext" valign="top">
													<input type="button" value="Add " onClick="driverpopup()" class="light-btn" style="width: 75px" tabindex="7" />
													<br/><br /> 
													<input type="button" value="Remove " onclick="removeDrivers()" class="light-btn" style="width: 75px" tabindex="8" />
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.script" /> 
													<ec:elitehelp  header="servicepolicy.cgpolicy.script" headerBundle="servicePolicyProperties" text="servicepolicy.cgpolicy.script" ></ec:elitehelp>
												</td>
												<td align="left" class="labeltext" valign="top" width="30%" colspan="2">
													<html:text property="script" size="30" style="width:250px" maxlength="255" tabindex="9" styleClass="scriptInstAutocomplete"></html:text>
												</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2">
													<input type="button" name="c_btnCreate" id="c_btnCreate2" value="Create" class="light-btn" onclick="validateForm()" tabindex="10"> 
													<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchCGPolicy.do?'" value="Cancel" class="light-btn" tabindex="11">
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
	<div id="driverPopup" style="display: none;" title="Add Drivers">
		<table id="addDriver" name="addDriver" cellpadding="0" cellspacing="0"
			width="100%" class="box">
		</table>
	</div>
</html:form>
<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>