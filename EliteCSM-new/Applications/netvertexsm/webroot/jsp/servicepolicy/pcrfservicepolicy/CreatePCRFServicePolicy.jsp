<%@page import="com.elitecore.corenetvertex.constants.CommunicationProtocol"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.corenetvertex.constants.GatewayComponent"%>
<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyConstants"%>
<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>
<%@ page import="com.elitecore.corenetvertex.constants.ServicePolicyActions"%>
<%@ page import="com.elitecore.corenetvertex.constants.SyMode"%>
<%@ page import="com.elitecore.corenetvertex.constants.UnknownUserAction"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form.CreatePCRFServicePolicyForm"%>
<%@ page import="java.util.List"%>
<script type="text/javascript" src="jquery/libs/expressionlibrary/prototype.js"></script>
<script type="text/javascript" src="jquery/libs/expressionlibrary/jquery-2.1.0.min.js"></script>
<script type="text/javascript" src="jquery/libs/expressionlibrary/jquery-ui-min.js"></script>
<script type="text/javascript" src="jquery/libs/expressionlibrary/autocompleter.js"></script>
<script type="text/javascript" src="jquery/libs/expressionlibrary/expressionlibrary.js"></script>
<script>
    var validateUrl = '<%=basePath%>/validate.do?';
var basePath = '<%=basePath%>';
var image = '<%=request.getContextPath()%>/images/tick.jpg';
</script>
<script language="javascript" 	src="<%=request.getContextPath()%>/js/validateMapping.js"></script>
<%
	CreatePCRFServicePolicyForm createPcrfServicePolicyForm = (CreatePCRFServicePolicyForm) request.getAttribute("createPCRFServiceForm");
	List<DriverInstanceData> driverInstanceList = createPcrfServicePolicyForm.getDriverInstanceList();	 
	Long[] driverId= new Long[driverInstanceList.size()];
	for(int i=0; i<driverInstanceList.size(); i++) {
		driverId[i] = driverInstanceList.get(i).getDriverInstanceId();
	}
%>

<style>
.main-css{
	height: 8px;
}
.plusMinus{
	font-size:17px;padding-right:5px;vertical-align: top;position: inherit;float: right;
}
</style>

<script language = "javascript">


function openCDRDriverPopup(){
	$.fx.speeds._default = 1000;
	document.getElementById("popupFieldMappingForCDRDriver").style.visibility = "visible";
	$( "#popupFieldMappingForCDRDriver" ).dialog({
		modal: true,
		autoOpen: false,
		height: 300,
		width: 500,
		position: ["top",150],
		buttons:{
            'Add': function() {
            	var selectedItems = document.getElementsByName("cdrDriverBox");
            	$(this).dialog('close');

                for(var i=0;i<selectedItems.length;i++) {
					if(selectedItems[i].checked == true) {
                        var csvLabelVal=selectedItems[i].value;
                        var csvLabelid=selectedItems[i].id;
                        var weightageID = "weightage"+csvLabelid;
                        var weightageValue=$('#'+weightageID).val();
                        csvLabelVal = csvLabelVal + "-W-" + weightageValue;
                        if (!($("#row"+csvLabelid).css('display')=='none')){
		                    $("#cdrDriverInstance").append("<option name='cdrDriverInstance' id='"+ csvLabelid +"' value='"+ csvLabelid +"' class=labeltext> "+csvLabelVal+" </option>");
		                    $("#row"+csvLabelid).attr("style","display:none");
							selectedItems[i].checked=false;							
                        }                                
                    }
                }
                $("#removeButtonForCDR").focus();
            },
		    Cancel: function() {
            	document.getElementById("toggleAllCDRDriver").checked = false;
            	$(this).dialog('close');
            	$("#removeButtonForCDR").focus();
        	}
        },
    	open: function() {
        	document.getElementById("toggleAllCDRDriver").checked = false;
        	var selectedItems = document.getElementsByName("cdrDriverBox");
        	var count = 0;
        	for(var i=0;i<selectedItems.length;i++) {
        		selectedItems[i].checked=false;
        		var cdrID = selectedItems[i].id;
        		if (($("#row"+cdrID).is(':hidden'))){        			
        			count++;
        		}
        	}
        	if(count == selectedItems.length){
        	var tableRowStr = '<tr id="noRecordFoundRowId">'+
				'<td  class="labeltext" colspan="4" align="center" style="color: red;">No CDR Driver found to add</td>'+
				'</tr>';
        		 $(tableRowStr).appendTo('#listTableForCDRDriver');
        		 $("#toggleAllCDRDriver").hide();
        	}else{
        		$("#toggleAllCDRDriver").show();
        	}
    	},
    	close: function() {
    		document.getElementById("toggleAllCDRDriver").checked = false;
    		$("#noRecordFoundRowId").remove();
    		$("#removeButtonForCDR").focus();
    	}
	});
	$( "#popupFieldMappingForCDRDriver" ).dialog("open");
}

function openSyGatewayPopup(){	
	$.fx.speeds._default = 1000;
	document.getElementById("popupFieldMappingForSyGateways").style.visibility = "visible";
	$( "#popupFieldMappingForSyGateways" ).dialog({
		modal	: true,
		autoOpen: false,
		height	: 300,
		width	: 500,
		position: ["top",150],
		buttons	:{
            'Add': function() {
            	var selectedItems = document.getElementsByName("syGatewayCheckBox");
                $(this).dialog('close');

                for(var i=0;i<selectedItems.length;i++) {
					if(selectedItems[i].checked == true) {
                        //var labelVal=$("#box").val();
                        var csvLabelVal	   = selectedItems[i].value;
                        var csvLabelid	   = selectedItems[i].id;
                        var weightageID    = "weightage"+csvLabelid;
                        var weightageValue = $('#'+weightageID).val();
                        csvLabelVal 	   = csvLabelVal + "-W-" + weightageValue;
                        if (!($("#row"+csvLabelid).css('display')=='none')){
		                    $("#syGatewayInstance").append("<option name='syGatewayInstance' id='"+ csvLabelid +"' value='"+ csvLabelid +"' class=labeltext> "+csvLabelVal+" </option>");
		                    $("#row"+csvLabelid).attr("style","display:none");
							selectedItems[i].checked=false;							
                        }                                
                    }
                }
                $("#removeButtonForSyGateway").focus(); 
            },
		    Cancel: function() {
		    	$(this).dialog('close');
            	 document.getElementById("toggleAllSyGateways").checked = false;            	
            	$("#removeButtonForSyGateway").focus();
        	}
        },
    	open: function() {
        	document.getElementById("toggleAllSyGateways").checked = false;
        	var selectedItems = document.getElementsByName("syGatewayCheckBox");
        	var count = 0;
        	for(var i=0;i<selectedItems.length;i++) {
        		selectedItems[i].checked=false;
        		var syID = selectedItems[i].id;
        		if (($("#row"+syID).is(':hidden'))){
        			count++;
        		}
        	}
        	if(count == selectedItems.length){
        	var tableRowStr = '<tr id="noSyGatewayRecordFoundRowId">'+
				'<td  class="labeltext" colspan="4" align="center" style="color: red;">No Sy Gateway found</td>'+
				'</tr>';
        		 $(tableRowStr).appendTo('#listTableForSyGateways');
        		 $("#toggleAllSyGateways").hide();
        	}else{
        		$("#toggleAllSyGateways").show();
        	} 
    	},
    	close: function() {
    		document.getElementById("toggleAllSyGateways").checked = false;
    		$("#noSyGatewayRecordFoundRowId").remove();
    		$("#removeButtonForSyGateway").focus(); 
    	}
	});
	$( "#popupFieldMappingForSyGateways" ).dialog("open");
}

function removeFromSelectedSyGateways(){
	$("#syGatewayInstance option:selected").each(function(){
	     var rowid=$(this).val();
	     $(rowid).removeAttr("style");
	     $(this).remove();
	     $("#syGatewayInstance option[value='rowid']").remove();
	     $("#row"+rowid).attr("style","visible");
    });
}


function removeFromSelectedCDRDriver(){
	$("#cdrDriverInstance option:selected").each(function(){
	     var rowid=$(this).val();
	     $(rowid).removeAttr("style");
	     $(this).remove();
	     $("#cdrDriverInstance option[value='rowid']").remove();
	     $("#row"+rowid).attr("style","visible");
    });
}

$(document).ready(function() {
	setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.pcrfpolicy"/>');
	$("#name").focus();	
	$("#description").attr('maxlength','255');
	$("#ruleset").attr('maxlength','4000');
	
	var mappingTypeArray = new Array();
	mappingTypeArray[0] = "<%=PCRFKeyType.REQUEST.getVal()%>";
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoComplete("identityAttribute",dbFieldArray);
	     return dbFieldArray;
	});
	disableAllOnDropRequest();
});
$(function(){						
		$("#ruleset").autocomplete();
		var dbFieldArray = [];
		<%
			List<PCRFKeyConstants> dataListForRule =  PCRFKeyConstants.values(PCRFKeyType.RULE);
		   for(PCRFKeyConstants constant:dataListForRule){
		%>
				dbFieldArray.push('<%=constant.getVal()%>');					
		<%}%>
		var autocompleter1 = createModel(dbFieldArray);
		expresssionAutoComplete('ruleset',autocompleter1);
	});

function hideOrShowpkgData(){
	var unknownUserAction = document.getElementById("unknownuseraction").value; 	
	if(unknownUserAction == '1' ){
		document.getElementById("pkgDataId").disabled = false;
	}else if(unknownUserAction == '2' || unknownUserAction == '3'){		
		document.getElementById("pkgDataId").disabled = true;
		document.getElementById("pkgDataId").selectedIndex='0';
	}
}

function disableAllOnDropRequest(){
	var action = document.getElementById("action").value;
	if(action == '2'){
		document.getElementById("cdrDriverInstance").disabled = true;
		document.getElementById("syGatewayInstance").disabled = true;
		document.getElementById("syMode").disabled = true;
		document.getElementById("identityAttribute").disabled = true;	
		document.getElementById("unknownuseraction").disabled = true;
		document.getElementById("pkgDataId").disabled = true;
		document.getElementById("addButtonForCDR").disabled = true;
		document.getElementById("addButtonForCDR").setAttribute("class", "light-btn-disabled");
		document.getElementById("addButtonForSyGateway").disabled = true;
		document.getElementById("addButtonForSyGateway").setAttribute("class", "light-btn-disabled");		
		document.getElementById("removeButtonForCDR").disabled = true;
		document.getElementById("removeButtonForCDR").setAttribute("class", "light-btn-disabled");
		document.getElementById("removeButtonForSyGateway").disabled = true;
		document.getElementById("removeButtonForSyGateway").setAttribute("class", "light-btn-disabled");
	}else{
		
		document.getElementById("cdrDriverInstance").disabled = false;
		document.getElementById("syGatewayInstance").disabled = false;		
		document.getElementById("syMode").disabled = false;
		document.getElementById("identityAttribute").disabled = false;
		document.getElementById("unknownuseraction").disabled = false;
		document.getElementById("addButtonForCDR").disabled = false;
		document.getElementById("addButtonForCDR").setAttribute("class", "light-btn");
		document.getElementById("addButtonForSyGateway").disabled = false;
		document.getElementById("addButtonForSyGateway").setAttribute("class", "light-btn");
		document.getElementById("removeButtonForCDR").disabled = false;
		document.getElementById("removeButtonForCDR").setAttribute("class", "light-btn");		
		document.getElementById("removeButtonForSyGateway").disabled = false;
		document.getElementById("removeButtonForSyGateway").setAttribute("class", "light-btn");				
		hideOrShowpkgData();
	}
}

function checkAllCDRDRiver(){
 	if(document.getElementById("toggleAllCDRDriver").checked == true) {
 		var selectVars = document.getElementsByName("cdrDriverBox");
	 	for (var i = 0; i <selectVars.length;i++){
			selectVars[i].checked = true;
	 	}
    } else if (document.getElementById("toggleAllCDRDriver").checked == false){
 		var selectVars = document.getElementsByName("cdrDriverBox");
		for (var i = 0; i < selectVars.length; i++){
			selectVars[i].checked = false ;
		}
	}
}

function checkAllSygateways(){
 	if(document.getElementById("toggleAllSyGateways").checked == true) {
 		var selectVars = document.getElementsByName("syGatewayCheckBox");
	 	for (var i = 0; i <selectVars.length;i++){
			selectVars[i].checked = true;
	 	}
    } else if (document.getElementById("toggleAllCDRDriver").checked == false){
 		var selectVars = document.getElementsByName("syGatewayCheckBox");
		for (var i = 0; i < selectVars.length; i++){
			selectVars[i].checked = false ;
		}
	}
}

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.PCRF_SERVICE_POLICY%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.PCRF_SERVICE_POLICY%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function selectAll(selObj){

	for(var i=0;i<selObj.options.length;i++){
		selObj.options[i].selected = true;
	}
}

function addHiddenWeightage(selObj){
	for(var i=0;i<selObj.options.length;i++){
		 var csvLabelValue = selObj.options[i].text;
		 var weightage = csvLabelValue.substring(csvLabelValue.lastIndexOf("-")+1,csvLabelValue.length);
		 var input = document.createElement("input");
         input.setAttribute("type", "hidden");
         input.setAttribute("name", "weightage");
         input.setAttribute("value", weightage);
         document.getElementById("myForm").appendChild(input);
	}
}


function addHiddenSyGatewayWeightage(selObj){
	for(var i=0;i<selObj.options.length;i++){
		 var csvLabelValue = selObj.options[i].text;
		 var weightage = csvLabelValue.substring(csvLabelValue.lastIndexOf("-")+1,csvLabelValue.length);
		 var input = document.createElement("input");
         input.setAttribute("type", "hidden");
         input.setAttribute("name", "syGatewayWeightage");
         input.setAttribute("value", weightage);
         document.getElementById("myForm").appendChild(input);
	}
}


var isValidName;
function validate(){
	var supportedCDRDriverList	= document.getElementById("cdrDriverInstance");
	var supportedSyGatewayList	= document.getElementById("syGatewayInstance");
	if(isNull(document.forms[0].name.value)){
		alert("Name must be specified.");
		document.forms[0].name.focus();
	}else if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return;
	}else if(document.forms[0].action.value =='1' && document.forms[0].identityAttribute.value.trim().length==0){		
		alert("Identity Attribute must be specified.");
		document.forms[0].identityAttribute.focus();
		return;
	}else if(document.forms[0].action.value=='1'){
		if(document.forms[0].unknownuseraction.value == "1"){
			if(document.forms[0].pkgDataId.value == "0"){
				alert("Base Package must be specified for Unknown Users.");
			}else{
				selectAll(supportedCDRDriverList);
				selectAll(supportedSyGatewayList);
				addHiddenWeightage(supportedCDRDriverList);
				addHiddenSyGatewayWeightage(supportedSyGatewayList);
				document.forms[0].submit();
			}
		}else{
			selectAll(supportedCDRDriverList);
			selectAll(supportedSyGatewayList);
			addHiddenWeightage(supportedCDRDriverList);
			addHiddenSyGatewayWeightage(supportedSyGatewayList);
			document.forms[0].submit();
		}
	}else if(document.forms[0].action.value=='1' && document.forms[0].unknownuseraction.value == "1"){
		if(document.forms[0].pkgDataId.value == "0"){
			alert("Base Package must be specified for Unknown Users.");
		}else{
			selectAll(supportedCDRDriverList);
			selectAll(supportedSyGatewayList);
			addHiddenWeightage(supportedCDRDriverList);
			addHiddenSyGatewayWeightage(supportedSyGatewayList);
			document.forms[0].submit();
		}
	}else{
		selectAll(supportedCDRDriverList);
		selectAll(supportedSyGatewayList);
		addHiddenWeightage(supportedCDRDriverList);
		addHiddenSyGatewayWeightage(supportedSyGatewayList);
		document.forms[0].submit();
	}
}


</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
			<tr>
				<td width="10">
					&nbsp;
				</td>
				<td class="box" cellpadding="0" cellspacing="0" border="0" width="100%"> 
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td class="table-header" colspan="6" >
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.pcrfpolicy.create"/>
							</td>
						</tr>
						<tr>
							<td colspan="3">
							<html:form action="/createPCRFPolicy" styleId="myForm">
								<table width="100%" border="0" >
									<tr>
										<td align="left" class="tblheader-bold" valign="top" colspan="3"  width="27%">
											<bean:message bundle="servicePolicyProperties" key="basic.details"/>
										</td>
									</tr>
									 
									<tr>
									<td colspan="3" style="padding-left: 10px; padding-right: 10px;">
									<table id="mappingtbl" border="0" width="100%">

									<tr>
										<td align="left" class="labeltext" valign="top"  width="30%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.name" />
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="servicepolicy.name"/>','<bean:message bundle="servicePolicyProperties" key="servicepolicy.name" />')"/>											
										</td>
										<sm:nvNameField maxLength="200" size="32" value="${createPCRFServiceForm.name }">&nbsp;&nbsp;<input type="checkbox" name="status" id="status" value="1" checked="true"/>&nbsp;Active</sm:nvNameField>
										
									</tr>

									<tr>
										<td align="left" class="labeltext" valign="top">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.description" />
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="servicepolicy.description"/>','<bean:message bundle="servicePolicyProperties" key="servicepolicy.description" />')"/>
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3">
											<html:textarea property="description"  styleId="description" rows="2" cols="60" tabindex="2"/>
										</td>
									</tr>
									<tr>
										
										<td align="left" class="labeltext" valign="top" >
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.ruleset"/>
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" 
											onclick="parameterDescription('<bean:message bundle="descriptionResources" key="servicepolicy.ruleset"/>'
											  + '<bean:message bundle="descriptionResources" key="expression.possibleoperators"/>'
											,'<bean:message bundle="servicePolicyProperties" key="servicepolicy.ruleset" />')"/>
											
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3" >
											<html:textarea property="ruleset" styleId="ruleset" rows="3" cols="50" tabindex="3" />
											<input type="button" value="validate" class="light-btn" tabindex="4" id="validateBtn" height="15%" onclick="validateCondition('ruleset');" style="vertical-align:top;margin-top: 20px;">
										</td>
										
										<td>
										<select id="policyKeys" hidden="true">
											<% for(PCRFKeyConstants keyConstants : PCRFKeyConstants.values(PCRFKeyType.RULE)){%>
												<option value="<%= keyConstants.getVal()%>" ><%= keyConstants.getVal()%></option>
											<%}%>
										</select>
										</td>
										 
									</tr>				
									<tr class="labeltext" >
										<td  align="right" valign="top" colspan="1">
										</td>
										<td  align="left" valign="top" colspan="2">	
											&nbsp;<label  class="small-text-grey"><bean:message bundle="descriptionResources" key="expression.possibleoperators"/></label>
										</td>
									</tr>
									<tr class="labeltext" >
										<td  align="right" valign="top" colspan="1">
										</td>
										<td  align="left" valign="top" colspan="2" name="validCond" class="small-text-grey" id="validCond"></td>
									</tr>
									 <tr> 
										<td align="left" class="labeltext" valign="top" >
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.action"/>
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="servicepolicy.action"/>','<bean:message bundle="servicePolicyProperties" key="servicepolicy.action" />')"/>											
										</td> 
										<td align="left" class="labeltext" valign="top"  > 
											<html:select property="action" styleId="action" onchange="disableAllOnDropRequest();" style="width: 180px" tabindex="4">
												<%
													ServicePolicyActions[] values = ServicePolicyActions.values();
																							for(int i=0;i<values.length;i++){
												%>
													<html:option value='<%=String.valueOf(values[i].getId())%>'><%=values[i].getName()%></html:option>
												<%
													}
												%>
										</html:select>										
										</td>					
				  					</tr>
				  					<tr>
										<td align="left" class="labeltext" valign="top" width="30%"  >
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.idattribute" />
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="servicepolicy.idattribute"/>','<bean:message bundle="servicePolicyProperties" key="servicepolicy.idattribute" />')"/>
										</td>
										<td colspan="3" align="left" class="labeltext" valign="top" width="75%"  nowrap="nowrap" >
											<html:text property="identityAttribute" maxlength="300" size="30" styleId="identityAttribute" style="width:250px" tabindex="5"/>
											<font color="#FF0000"> *</font>											
										</td>
										 
									</tr>	
				  					
									<tr>
										<td align="left" class="labeltext" valign="top" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.unknownuseraction"/>
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="servicepolicy.unknownuseraction"/>','<bean:message bundle="servicePolicyProperties" key="servicepolicy.unknownuseraction" />')"/>
										</td> 
											<td align="left" class="labeltext" valign="top" colspan="3" >
												<html:select name="createPCRFServiceForm" property="unknownUserAction" styleId="unknownuseraction" onchange="hideOrShowpkgData();" style="width:180px" tabindex="6">
													<%
														List<UnknownUserAction> valuesList = UnknownUserAction.getObjectList();
														for( UnknownUserAction constant : valuesList){
													%>
														<html:option value="<%=Integer.toString(constant.getVal())%>"><%=constant.getName()%></html:option>
													<%  } %>											
												</html:select> 
											</td> 
									</tr>
									<tr id="allowunknownuser">
										<td align="left" class="labeltext" valign="top" >
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.pkgdata"/>
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="servicepolicy.pkgdata"/>','<bean:message bundle="servicePolicyProperties" key="servicepolicy.pkgdata" />')"/>
										</td>
											<td align="left" class="labeltext" valign="top">
												<html:select name="createPCRFServiceForm" styleId="pkgDataId" property="pkgId" style="width:180px" tabindex="6">
													<html:option value="0" bundle="servicePolicyProperties" key="general.select" />
													<logic:iterate id="pkgData"  name="createPCRFServiceForm" property="pkgDataList" type="com.elitecore.corenetvertex.pkg.PkgData">
														<html:option value="<%=pkgData.getId()%>"><%=pkgData.getName()%></html:option>
													</logic:iterate>
												</html:select>								      											
											</td>		
	   								 </tr>				  					

								</table>
									</td>
									</tr>
									<tr>
										<td align="left" style="cursor:pointer;" class="tblheader-bold" valign="top"  width="27%" colspan="3"  title="Sy Gateway Configuration"  ><bean:message bundle="servicePolicyProperties" key="servicepolicy.syconfiguration"/></td>
									</tr>
									<tr>
									<td colspan="3" style="padding-left: 10px; padding-right: 10px;">
									<table id="mappingtbl" border="0" width="100%">
									<tr id="syConfigRow">
										<td align="left" class="labeltext" valign="top"  width="30%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.sy.gateways"/>
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateways.sy"/>','<bean:message bundle="servicePolicyProperties" key="servicepolicy.sy.gateways" />')"/>
										</td>
										<td align="left" class="labeltext" valign="top" width="35%" colspan="2"  >
											<select multiple="multiple" id="syGatewayInstance" name="syGatewayInstance" style="height: 100px; width: 250px" tabindex="11">
							    			</select>
										</td>
										<td align="left" class="labeltext" valign="top"  >
										   <input type="button" id="addButtonForSyGateway" value="   Add   " onClick="javascript:openSyGatewayPopup();" class="light-btn" style="width: 75px" tabindex="12"/><br/>
										   <br/>
										   <input type="button" id="removeButtonForSyGateway" value=" Remove "  onclick="removeFromSelectedSyGateways()" class="light-btn" style="width: 75px" tabindex="13"/>
						    			</td>						    				
									</tr>

									<tr>
										<td align="left" class="labeltext" valign="top"   >											
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.sy.mode"/>
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.sy.mode"/>','<bean:message bundle="servicePolicyProperties" key="servicepolicy.sy.mode"/>')"/> 																						
										</td>
										<td align="left" class="labeltext" valign="top"  colspan="3">
											<html:select name="createPCRFServiceForm" property="syMode"  value="PUSH" styleId="syMode" style="width:150px" tabindex="14">
												<%for(SyMode mode : SyMode.values()){%>																								
													<html:option value="<%=mode.name()%>"><%=mode.name()%></html:option>
												<%}%>
											</html:select>
										</td>
										<td  ></td>
									</tr>	
									</table>
									</td>
									</tr>
																	
									<tr>
										<td align="left" class="tblheader-bold" valign="top"  width="27%" 
										style="cursor: pointer;" title="CDR Configuration" colspan="3"   class="tblheaderfirstcol" style="cursor: pointer;" width="100%" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.cdrconfiguration"/></td>
									</tr>
									<tr>
									<td colspan="3" style="padding-left: 10px; padding-right: 10px;">
									<table id="mappingtbl" border="0" width="100%">

									<tr >
										<td align="left" class="labeltext" valign="top"  width="30%">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.cdrdriver"/>
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="servicepolicy.cdrdriver"/>','<bean:message bundle="servicePolicyProperties" key="servicepolicy.cdrdriver" />')"/>
										</td>
										<td align="left" class="labeltext" valign="top" width="35%" >
											<select multiple="multiple" id="cdrDriverInstance" name="cdrDriverInstance" style="height: 100px; width: 250px" tabindex="15">
							    			</select>
										</td>
										<td align="left" class="labeltext" valign="top"  >
										   <input type="button" id="addButtonForCDR" value="   Add   " onClick="javascript:openCDRDriverPopup();" class="light-btn" style="width: 75px" tabindex="16"/><br/>
										   <br/>
										   <input type="button" id="removeButtonForCDR" value=" Remove "  onclick="removeFromSelectedCDRDriver()" class="light-btn" style="width: 75px" tabindex="17"/>
						    			</td>						    				
									</tr>
									</table>
									</td>
									</tr>
									 <tr>
										<td class="btns-td" valign="middle">
											&nbsp;
										</td>
										<td>&nbsp;</td>
										<td class="btns-td" valign="middle" >
											<input type="button" value="Create"  class="light-btn" onclick="validate()"tabindex="20"/>
											<input type="button" align="left" value="  Cancel  " class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchPCRFService.do?/>'" tabindex="21"/>
										</td>
									</tr>
								</table>
								</html:form>
							</td>
						</tr>
					</table>
				</td>
			</tr>
	
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
 </table>
 </table>
</table>

<div id="popupFieldMappingForCDRDriver" title="Add CDR Driver" style="display: none;">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTableForCDRDriver">
		<tr>
			<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
				<input type="checkbox" id="toggleAllCDRDriver" name="toggleAllCDRDriver" value="checkbox" onclick="javascript:checkAllCDRDRiver()" />
			</td>
			<td align="left" class="tblheader" valign="top" width="20%">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.cdrdriverinstance" />
			</td>
			<td align="left" class="tblheader" valign="top" width="20%">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.cdrdrivertype" />
			</td>
			<td align="center" class="tblheaderlastcol" valign="top" width="20%">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.weightage" />
			</td>			
		</tr>
	<logic:iterate id="cdrDriverInstance" name="createPCRFServiceForm" property="cdrDriverInstanceDataList" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData">
	<%
		String trName = "trComp"+ cdrDriverInstance.getDriverInstanceId();
		String driverName = "driverName"+ cdrDriverInstance.getDriverInstanceId();
		String weightageInput = "wtg" + cdrDriverInstance.getDriverInstanceId();
	%>
		<tr id="row<bean:write property="driverInstanceId" name="cdrDriverInstance" />">
			<td align="center" class="tblfirstcol">
				<input type="checkbox" name="cdrDriverBox" id="<bean:write property="driverInstanceId" name="cdrDriverInstance" />" value="<bean:write property="name" name="cdrDriverInstance" />" />
			</td>
			<td align="left" class="tblrows">
				<bean:write name="cdrDriverInstance" property="name" />&nbsp;
			</td>
			<td align="left" class="tblrows">
				<bean:write name="cdrDriverInstance" property="driverTypeData.name" />&nbsp;
			</td>
			<td align="center" class="tblrows">
				<select id="weightage<bean:write property="driverInstanceId" name="cdrDriverInstance" />">
					<%for(int i=0;i<=10;i++){%>
						<option value="<%=String.valueOf(i)%>"><%=i%></option>
					<%}%>
				</select>
			</td>
		</tr>
	</logic:iterate>
	</table>
</div>

<div id="popupFieldMappingForSyGateways" title="Add Sy Gateway" style="display: none;">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTableForSyGateways">
		<tr>
			<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
				<input type="checkbox" id="toggleAllSyGateways" name="toggleAllSyGateways" value="checkbox" onclick="javascript:checkAllSygateways()" />
			</td>
			<td align="left" class="tblheader" valign="top" width="20%">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.sy.gatewayname" />
			</td>
			<td align="left" class="tblheader" valign="top" width="20%">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.sy.gatewaytype" />
			</td>
			<td align="center" class="tblheaderlastcol" valign="top" width="20%">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.weightage" />
			</td>			
		</tr>	
		<logic:iterate id="gatewayBean" name="createPCRFServiceForm" property="gatewayList" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"  >
				<logic:equal name="gatewayBean" property="commProtocol" value="<%=CommunicationProtocol.DIAMETER.id %>">
					<%
						boolean displayOCS = false;
						boolean displayDRA = false;
					%>
					<bean:define id="gatewayProfileDataBean" name="gatewayBean" property="gatewayProfileData" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData"  />
					<logic:equal name="gatewayProfileDataBean" property="gatewayType" value="<%=GatewayComponent.OCS.value%>" >
						<% displayOCS = true; %>
					</logic:equal>
					<logic:equal name="gatewayProfileDataBean" property="gatewayType" value="<%=GatewayComponent.DRA.value%>" >
						<% displayDRA = true; %>
					</logic:equal>					
					<%if(displayOCS || displayDRA){%>
					<tr id="row<bean:write name="gatewayBean"  property="gatewayId" />">
						<td align="center" class="tblfirstcol" valign="top" width="1%">
							<input type="checkbox" name="syGatewayCheckBox" id="<bean:write name="gatewayBean"  property="gatewayId" />"  value="<bean:write name="gatewayBean" property="gatewayName" />" />
						</td>
						<td align="left" class="tblrows" valign="top" width="20%">
							<bean:write name="gatewayBean" property="gatewayName" />
						</td>
						<td align="left" class="tblrows" valign="top" width="20%">
							<bean:write name="gatewayProfileDataBean" property="gatewayType" />
						</td>
						<td align="center" class="tblrows" valign="top" width="20%">
							<select id="weightage<bean:write name="gatewayBean"  property="gatewayId" />">
								<%for(int i=1;i<=10;i++){%>
									<option value="<%=i%>"><%=i%></option>
								<%}%>
							</select>
						</td>			
					</tr>					
					<%}%>
				</logic:equal>
		</logic:iterate>
	</table>
</div>

