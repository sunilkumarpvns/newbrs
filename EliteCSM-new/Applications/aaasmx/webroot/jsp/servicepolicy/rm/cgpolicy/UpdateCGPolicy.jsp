<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ServiceTypeConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.UpdateCGPolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyDriverRelationData"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>

<%
		//code for driver jquery popup
		UpdateCGPolicyForm updateCGPolicyForm = (UpdateCGPolicyForm)request.getAttribute("policyForm");
		DriverBLManager driverManager = new DriverBLManager();
		List<DriverInstanceData> listOfDriver = driverManager.getDriverInstanceList(ServiceTypeConstants.CHARGING_GATEWAY_SERVICE);
		
		String[] driverInstanceIds = new String [listOfDriver.size()];
		String[][] driverInstanceNames = new String[listOfDriver.size()][3]; 
		
		for(int i=0;i<listOfDriver.size();i++){
			DriverInstanceData data = listOfDriver.get(i);				
				driverInstanceNames[i][0] = String.valueOf(data.getName());
				driverInstanceNames[i][1] = String.valueOf(data.getDescription());
				driverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
			driverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
		}
		
		CGPolicyData cgPolicyData = (CGPolicyData)request.getAttribute("cgPolicyData");
%>


<script type="text/javascript" language="javascript"
	src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript"
	src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
setExpressionData("Diameter");
var driverScriptList = [];

<% 
if( Collectionz.isNullOrEmpty(updateCGPolicyForm.getDriverScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : updateCGPolicyForm.getDriverScriptList()){ %>
		driverScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

var isValidName;

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

 		var headersArr = new Array(5);
 		headersArr[0] = '';
 		headersArr[1] = 'Driver Name';
 		headersArr[2] = 'Driver Description';
 		headersArr[3] = 'Driver Type';
 		headersArr[4] = 'Weightage';
 		
 		initializeData(jdriverInstanceIds,jdriverNames,'addDriver','driverDataCheckBoxId',headersArr,'true',jdriverInstanceIds.length);
 		hideSelectedData('selecteddriverIds','driverDataCheckBoxId');
 		
 		 /* Script Autocomplete */
		setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
 		
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
 		isValidName = verifyInstanceName('<%=InstanceTypeConstants.CHARGING_POLICY%>',searchName,'update','<%=cgPolicyData.getPolicyId()%>','verifyNameDiv');
 	}
 </script>

<html:form action="/updateCGPolicy">

	<html:hidden name="updateCGPolicyForm" styleId="action" property="action" value="update" />
	<html:hidden name="updateCGPolicyForm" styleId="policyId" property="policyId" />
	<html:hidden name="updateCGPolicyForm" styleId="auditUId" property="auditUId" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
		<tr>
			<td class="box" valign="middle" colspan="5">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="30%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="servicePolicyProperties"
								key="servicepolicy.cgpolicy" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.name" />
							<ec:elitehelp  header="servicepolicy.cgpolicy.name" headerBundle="servicePolicyProperties" text="cgpolicy.name" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" width="18%"
							nowrap="nowrap"><html:text styleId="name" property="name"
								size="30" onkeyup="verifyName();" maxlength="60"
								style="width:250px" tabindex="1" /><font color="#FF0000">
								*</font>
							<div id="verifyNameDiv" class="labeltext"></div></td>
							<td align="left" class="labeltext" valign="top"><html:checkbox
								styleId="status" property="status" value="1" tabindex="2" />Active
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="servicePolicyProperties"
								key="servicepolicy.cgpolicy.desp" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:textarea styleId="description" property="description"
								cols="40" rows="2" style="width:250px" tabindex="2" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.ruleset" /> 
							<ec:elitehelp  header="servicepolicy.cgpolicy.ruleset" headerBundle="servicePolicyProperties" text="cgpolicy.ruleset" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:textarea styleId="ruleSet" property="ruleSet" cols="40"
								rows="2" style="width:250px" tabindex="3" /> <font
							color="#FF0000"> *</font> <img alt="Expression"
							src="<%=basePath%>/images/lookup.jpg"
							onclick="popupExpression('ruleSet');" tabindex="4" />
						</td>
					</tr>

					<tr>
						<td width="20%" align="left" class="captiontext" valign="top">
							Driver Group<font color="#FF0000">*</font> 
							<ec:elitehelp  header="Driver Group" headerBundle="servicePolicyProperties" text="cgpolicy.drivergroup" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top"><html:select
								property="selecteddriverIds" multiple="true"
								styleId="selecteddriverIds" size="5" style="width: 250;"
								tabindex="5">

								<%
													
												List<CGPolicyDriverRelationData> driverList = updateCGPolicyForm.getDriversList();												
												if(driverList != null){
													for(int i = 0;i<driverList.size();i++){																										
														CGPolicyDriverRelationData data = driverList.get(i);
														String nm = " ";
														if(data.getWeightage() != null)
															nm = data.getWeightage().toString();
														nm = data.getDriverData().getName() + "-W-" + nm;		
														String value = data.getDriverInstanceId() + "-" + data.getWeightage();
													%>
								<html:option value="<%=value%>"><%=nm%></html:option>
								<%}
												}%>
							</html:select></td>

						<td align="left" class="labeltext" valign="top"><input
							type="button" value="Add " onClick="driverpopup()"
							class="light-btn" style="width: 75px" tabindex="6" /><br /> <br />
							<input type="button" value="Remove " onclick="removeDrivers ()"
							class="light-btn" style="width: 75px" tabindex="7" /></td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.cgpolicy.script" /> 
							<ec:elitehelp  header="servicepolicy.cgpolicy.script" headerBundle="servicePolicyProperties" text="servicepolicy.cgpolicy.script" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" width="30%" colspan="2">
							<html:text property="script" size="30" style="width:250px" maxlength="255" tabindex="8" styleClass="scriptInstAutocomplete"></html:text>
						</td>
					</tr>

					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2"><input
							type="button" name="c_btnCreate" onclick="validateForm()"
							id="c_btnCreate2" value="Update" class="light-btn" tabindex="9">
							<input type="reset" name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/initViewCGPolicy.do?policyId=<%=updateCGPolicyForm.getPolicyId()%>'"
							value="Cancel" class="light-btn" tabindex="10"></td>
					</tr>
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