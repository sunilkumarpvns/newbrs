<%@page import="com.elitecore.corenetvertex.constants.GatewayComponent"%>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyConstants"%>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyValueConstants"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData" %>
<%@ page import="com.elitecore.netvertexsm.web.gateway.profile.form.CreateProfileForm" %>
<%@ page import="com.elitecore.netvertexsm.web.gateway.profile.form.EditGatewayProfileForm" %>
<%@ page import="com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager" %>
<%@ page import="java.util.List" %> 
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.corenetvertex.constants.ChargingRuleInstallMode"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData" %>
<%@ page import="com.elitecore.corenetvertex.constants.UMStandard" %>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData" %>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
var validateUrl = '<%=basePath%>/validate.do?';
var basePath = '<%=basePath%>';
var image = '<%=request.getContextPath()%>/images/tick.jpg';
</script>
<script language="javascript" 	src="<%=request.getContextPath()%>/js/validateMapping.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

<% 
	EditGatewayProfileForm editGatewayProfileForm = (EditGatewayProfileForm)request.getAttribute("editGatewayProfileForm");
 	GatewayProfileData gatewayProfileData1 = (GatewayProfileData)request.getAttribute("gatewayProfileData");
	int standard = editGatewayProfileForm.getSupportedStandard();
%>

<script type="text/javascript">
	var sessionLookupKeyValue = null;
	var jDiameterToPCRFRefArray = new Array();
	var jDPCount = 0;
	var jPCRFToDiameterRefArray = new Array();
	var jPDCount = 0;
	
	function openDiameterToPCRFMapping() {
		$.fx.speeds._default = 1000;
		document.getElementById("popupDiameterToPCRFMapping").style.visibility = "visible";		
		$( "#popupDiameterToPCRFMapping" ).dialog({
			modal: true,
			autoOpen: false,		
			height: 450,
			width: 570,	
			position: ["top",100],
			buttons:{					
	            'Add': function() {	
					var name = $('#packetMapId').text(); 
					var nameId = $('#packetMapId').val();
					var cond = $('#dpcond').val();
					var selectedItems = document.getElementsByName("diameterToPCRFMapList");
	     			var i = 0;							
					var flag = true;
					var basepath='<%=basePath%>';
					if(flag){	
	
	                    for(var i=0; i<selectedItems.length; i++) {
	    					if(selectedItems[i].checked == true) {
	                            var labelVal = selectedItems[i].value;
	                            var labelid = selectedItems[i].id;
	                            
	                            	$("#mapReq").append("<tr name='mappingRequest'>"+
	    	                            	"<td class='tblfirstcol' valign='top'>" + labelVal+ 
	    	                            	"<input type='hidden' name='reqPacketMapId' value='" + labelid + "'/></td>"+
	    	                            	"<td class='tblrows' valign='top'><input class='noborder' type='text' style='width: 100%'  id='reqCondition' name='reqCondition' maxlength='200' size='70' value='" + cond + "'/></td>"+
	    	                            	"<td class='tblrows' style='cursor:default' align='center' valign='middle'><img src='"+basepath+"/images/minus.jpg' height='14' onclick='removeDPRow(\""+labelid+"\")'></td>"+
	    	                            	"</tr>");
	                            	
	                                selectedItems[i].checked=false;
	                                $("#"+labelid).attr("style","display:none");
	                                jDiameterToPCRFRefArray[jDPCount++] = labelid;
	                        }
	    					document.getElementById("dpcond").value = "";
			          		$('#mapReq').tableDnD();
			          			
			          	}	
	         		}
					$(this).dialog('close');
					$("#reqBtn2").focus();
	            },                
			    Cancel: function() {
	            	$(this).dialog('close');
	            	$("#reqBtn2").focus();
	        	}
	        },
	    	open: function() {
	        	
	    	},
	    	close: function() {
	    		$("#reqBtn2").focus();
	    	}				
		});
		$( "#popupDiameterToPCRFMapping" ).dialog("open");
	}

	function removeDPRow(rowId)
	{
		$("#"+rowId).removeAttr("style");
	 	$('#mapReq td img').on('click',function() {
	 		$(this).closest('tr').next("tr[name='noteTR']").remove();
			$(this).parent().parent().remove();
		});
	}	

	function addPCCRuleMappingRow() {
		$("#ruleMappingTable tr:last").after("<tr>" + $("#templateRuleMappingTable").find("tr").html() + "</tr>");
		accessNetworkSuggestion();
		$('#ruleMappingTable').tableDnD();
		$('#ruleMappingTable td img.delete').on('click',function() {
			 $(this).parent().parent().remove();
		});
	}
		
	

	function openPCRFToDiameterMapping() {
		$.fx.speeds._default = 1000;
		document.getElementById("popupPCRFToDiameterMapping").style.visibility = "visible";		
		$( "#popupPCRFToDiameterMapping" ).dialog({
			modal: true,
			autoOpen: false,		
			height: 450,
			width: 570,	
			position: ["top",100],
			buttons:{					
	            'Add': function() {	
					var name = $('#packetMapId').text(); 
					var nameId = $('#packetMapId').val();
					var cond = $('#pdcondition').val();
					var selectedItems = document.getElementsByName("pcrfToDiameterMapList");
	     			var i = 0;							
					var flag = true;												 	
					var basepath='<%=basePath%>';
					if(flag){	
	
	                    for(var i=0; i<selectedItems.length; i++) {
	    					if(selectedItems[i].checked == true) {
	                            var labelVal = selectedItems[i].value;
	                            var labelid = selectedItems[i].id;
	                           $("#mapRes").append("<tr name='mappingResponse'>"+
	    	                            	"<td class='tblfirstcol' valign='top'>" + labelVal+ 
	    	                            	"<input type='hidden' name='resPacketMapId' value='" + labelid + "'/></td>"+
	    	                            	"<td class='tblrows' valign='top'><input class='noborder' type='text' style='width: 100%' id='resCondition' name='resCondition' maxlength='200' size='70' value='" + cond + "'/>"+
	    	                            	"</td>"+
	    	                            	"<td class='tblrows' style='cursor:default' align='center' valign='middle'><img src='"+basepath+"/images/minus.jpg' height='14' onclick='removePDRow(\""+labelid+"\")'></td>"+
	    	                            	"</tr>");
	                            selectedItems[i].checked=false;
	                            $("#"+labelid).attr("style","display:none");
	                            jPCRFToDiameterRefArray[jPDCount++] = labelid;
	                        }
	    					document.getElementById("pdcondition").value = "";
	    					$('#mapRes').tableDnD();
			          		$(this).dialog('close');	
			          		 			          		         		
			         	}	
	         		}
					$("#resCondition").focus();
	            },                
			    Cancel: function() {
	            	$(this).dialog('close');
	            	$("#groovy_script_btn").focus();
	        	}
	        },
	    	open: function() {
	        	
	    	},
	    	close: function() {
	    		$("#groovy_script_btn").focus();
	    	}				
		});
		$( "#popupPCRFToDiameterMapping" ).dialog("open");
		setTextAreaTag(document.getElementById("pdcondition"));
		setTextKeyTag(document.getElementById("policyKeys"));
		loadAll();
	}

	function removePDRow(rowId)
	{
		$("#"+rowId).removeAttr("style");
		 	$('#mapRes td img').on('click',function() {
		 		$(this).closest('tr').next("tr[name='noteTR']").remove();
				$(this).parent().parent().remove();
		});
	}



$(document).ready(function(){
	sessionLookupKeyValue = document.forms[0].sessionLookupKey.value;
	$("#timeout").focus();
	$("#supportedVendorList").attr('maxlength','254');
	
	$("#cerAvps").attr('maxlength','2000');
	$("#dprAvps").attr('maxlength','2000');
	$("#dwrAvps").attr('maxlength','2000');
	 	
	
	for ( var i = 0; i < document.forms[0].supportedStandard.options.length; i++ ) {
        if (document.forms[0].supportedStandard.options[i].value == '<%=standard%>' ) {
        	document.forms[0].supportedStandard.options[i].selected = true;
        	
        	if('<%=standard%>' == 4)
        		$('#pccrules').hide();
            break;
        }
    }
	
	
	$('table td img.delete').on('click',function() {
		$(this).parent().parent().remove(); 
	});
	
	$('#supportedStandard').change(function() {
		var id = $(this).val();
		if(id==1 || id==2 || id==3) {
			$('#pccrules').show();
		}else if(id==4) {
			$('#pccrules').hide();
		}
	});
	sessionLookupKey();
	supportedVendorList();
	dragAndDrop();
	setEnabledDisabled('isDWRGatewayLevel','dwInterval');
	setEnabledDisabled('isCustomGxAppId','gx');
	setEnabledDisabled('isCustomGyAppId','gy');
	setEnabledDisabled('isCustomRxAppId','rx');
	setEnabledDisabled('isCustomS9AppId','s9');
	setEnabledDisabled('isCustomSyAppId','sy');
	accessNetworkSuggestion();
	checkGatewayComponent();
});
function checkGatewayComponent() {
	<%if((gatewayProfileData1.getGatewayType().equalsIgnoreCase(GatewayComponent.APPLICATION_FUNCTION.value)) || (gatewayProfileData1.getGatewayType().equalsIgnoreCase(GatewayComponent.DRA.value))){ %>
		$('#LookupKey').show();
	<%}else{%>
		$('#LookupKey').hide();
	<%}%>
}

function sessionLookupKey() {
	var sessionId = new Array();
	<% for(PCRFKeyConstants keyConstants : PCRFKeyConstants.values(PCRFKeyType.REQUEST)){%>
	 		sessionId.push('<%=keyConstants.getVal()%>');
	<%}%> 
	commonAutoComplete("sessionLookupKey",sessionId);
}

function addRow(type) {
	if(type=='static') 
		$('<tr><td class="allborder"><input tabindex="26" class="noborder" type="text" name="attributeS" maxlength="200" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="26" class="plcKey" type="text" name="policyKeyS" maxlength="4000" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="26" class="noborder" type="text" name="defaultValueS" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="26" class="noborder" type="text" name="valueMappingS" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows" align="center"  valign="middle"><img tabindex="26" value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px;" height="14" /></td></tr>').appendTo('#'+type);
	else
		$('<tr><td class="allborder"><input tabindex="27" class="noborder" type="text" name="attributeD" maxlength="200" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="27" class="plcKey" type="text" name="policyKeyD" maxlength="4000" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="27 class="noborder" type="text" name="defaultValueD" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="27 class="noborder" type="text" name="valueMappingD" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows" align="center" valign="middle"><img tabindex="27" value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px;" height="14" /></td></tr>').appendTo('#'+type);		
	policyKey();
}

function policyKey() {
	var mappingTypeArray = new Array();	
	mappingTypeArray[0] = "<%=PCRFKeyType.PCC_RULE.getVal()%>";
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoCompleteUsingCssClass("td input.plcKey",dbFieldArray);
		return dbFieldArray;
	});
}
function accessNetworkSuggestion(){
	var dbFieldArray = new Array();
	var index = 0;
	<%
		List<PCRFKeyValueConstants> dataList =  PCRFKeyValueConstants.values(PCRFKeyConstants.CS_ACCESS_NETWORK);
		Object[]  dataArray = dataList.toArray();
		for(PCRFKeyValueConstants constant:dataList){
	%>
			dbFieldArray[index++]='<%=constant.val%>';						
	<%}%>
	$(".accessNetworkType").each(function(){
		var id = "accessNetworkType_"+ (index++) + new Date().getTime();
		$(this).attr("id", id);
		commonAutoCompleteMultiple(id, dbFieldArray);
			
	});
}

function dragAndDrop(){
	$('#mapReq').tableDnD();
	$('#mapRes').tableDnD();
	$('#groovyScripts').tableDnD();	
	$('#ruleMappingTable').tableDnD();
}


function hidePopupWindowRow(rowId){
	$("#"+rowId).attr("style","display:none");
}
function enableAll(){
	document.getElementById("gx").disabled = false;
	document.getElementById("gy").disabled = false;
	document.getElementById("rx").disabled = false;
	document.getElementById("s9").disabled = false;
	document.getElementById("sy").disabled = false;
	document.getElementById("dwInterval").disabled = false;
}
function setEnabledDisabled(checkBoxId, txtBoxId){
	if(document.getElementById(checkBoxId).checked==false){
		document.getElementById(txtBoxId).disabled = true;
	}else{
		document.getElementById(txtBoxId).disabled = false;
	}
}
	
function setDWInterval(){
	setEnabledDisabled('isDWRGatewayLevel','dwInterval');
}
function setGx(){
	setEnabledDisabled('isCustomGxAppId','gx');
}
function setGy(){
	setEnabledDisabled('isCustomGyAppId','gy');
}
function setRx(){
	setEnabledDisabled('isCustomRxAppId','rx');
}
function setS9(){
	setEnabledDisabled('isCustomS9AppId','s9');
}
function setSy(){
	setEnabledDisabled('isCustomSyAppId','sy');
}
function validate() {
	callValidateMappingCondition();
	if(isNull(document.forms[0].timeout.value)){
		alert('Timeout must be specefied.');
		document.forms[0].timeout.focus();
		return false;
	}else if(isEcNaN(document.forms[0].timeout.value)){
		alert('Timeout must be numeric.');
		document.forms[0].timeout.focus();
		return false;
	}else if(isNull(document.forms[0].sessionLookupKey.value)){
		alert('SessionLookupKey must be specified.');
		document.forms[0].sessionLookupKey.focus();
		return false;
	}else if(document.getElementById('isDWRGatewayLevel').checked==true && isNull(document.forms[0].dwInterval.value)){
		alert('DW Interval must be specified.');
		document.forms[0].dwInterval.focus();
		return false;
	}else if(document.getElementById('isDWRGatewayLevel').checked==true && isEcNaN(document.forms[0].dwInterval.value)){
		alert('DW Interval must be numeric.');
		document.forms[0].dwInterval.focus();
		return false;
	}else if(document.getElementById('isCustomGxAppId').checked==true && isNull(document.forms[0].gx.value)){
		alert('Custom Gx App Id must be specified.\nPlease specify it in format "Vendor Id:Application Id". ');
		document.forms[0].gx.focus();
		return false;
	}else if(document.getElementById('isCustomGyAppId').checked==true && isNull(document.forms[0].gy.value)){
		alert('Custom Gy App Id must be specified.\nPlease specify it in format "Vendor Id:Application Id".');
		document.forms[0].gy.focus();
		return false;
	}else if(document.getElementById('isCustomRxAppId').checked==true && isNull(document.forms[0].rx.value)){
		alert('Custom Rx App Id must be specified.\nPlease specify it in format "Vendor Id:Application Id".');
		document.forms[0].rx.focus();
		return false;
	}else if(document.getElementById('isCustomS9AppId').checked==true && isNull(document.forms[0].s9.value)){
		alert('Custom S9 App Id must be specified.\nPlease specify it in format "Vendor Id:Application Id".');
		document.forms[0].s9.focus();
		return false;
	}else if(document.getElementById('isCustomGxAppId').checked==true && !isValidAppId(document.forms[0].gx.value)){
		alert('Invalid Gx Application Id.\nPlease specify it in format "Vendor Id:Application Id".');
		document.forms[0].gx.focus();
		return false;
	}else if(document.getElementById('isCustomGyAppId').checked==true && !isValidAppId(document.forms[0].gy.value)){
		alert('Invalid Gy Application Id.\nPlease specify it in format "Vendor Id:Application Id".');
		document.forms[0].gy.focus();
		return false;
	}else if(document.getElementById('isCustomRxAppId').checked==true && !isValidAppId(document.forms[0].rx.value)){
		alert('Invalid Rx Application Id.\nPlease specify it in format "Vendor Id:Application Id".');
		document.forms[0].rx.focus();
		return false;
	}else if(document.getElementById('isCustomS9AppId').checked==true && !isValidAppId(document.forms[0].s9.value)){
		alert('Invalid S9 Application Id.\nPlease specify it in format "Vendor Id:Application Id".');
		document.forms[0].s9.focus();
		return false;
	}else if(isNull(document.forms[0].socketReceiveBufferSize.value) == false && isIntegerNumber(document.forms[0].socketReceiveBufferSize.value) == false){
		alert('Socket Receive Buffer Size must be numeric.');
		document.forms[0].socketReceiveBufferSize.focus();
		return false;
	}else if(isNull(document.forms[0].socketSendBufferSize.value) == false && isIntegerNumber(document.forms[0].socketSendBufferSize.value) == false){
		alert('Socket Send Buffer Size must be numeric.');
		document.forms[0].socketSendBufferSize.focus();
		return false;
	}else if(document.getElementById('isCustomSyAppId').checked==true && !isValidAppId(document.forms[0].sy.value)){
		alert('Invalid Sy Application Id.\nPlease specify it in format "Vendor Id:Application Id".');
		document.forms[0].sy.focus();
		return false;		
	}else if(isNull(document.forms[0].socketReceiveBufferSize.value)== false &&
			 isIntegerNumber(document.forms[0].socketReceiveBufferSize.value)==false){
		alert('Socket Receive Buffer Size must be numeric.');
		document.forms[0].socketReceiveBufferSize.focus();
		return false;
	}else if(isNull(document.forms[0].socketSendBufferSize.value)== false &&
			 isIntegerNumber(document.forms[0].socketSendBufferSize.value)==false){
		alert('Socket Send Buffer Size must be numeric.');
		document.forms[0].socketSendBufferSize.focus();
		return false;
	}else if(isEcNaN(document.forms[0].initConnectionDuration.value)){
		alert('Init Connection Duration must be numeric.');
		document.forms[0].initConnectionDuration.focus();
		return false;
	}else if(!validateStaticAttribute()){		
		return;		
	}else if(!validateStaticPolicyKey()){
		return;
	}else if(!validateDynamicAttribute()){		
		return;		
	}else if(!validateDynamicPolicyKey()){
		return;
	}else if(!validateOrderNumber()){
		return;
	}else if(!validateScriptName()){
		return;
	}else{
		var value = checkForNote();
		var confirmSubmit = true;
		if(value != null){
			if(value == "No Live Server Found" || value == "Timeout Occur"){
				confirmSubmit = confirm(message.serverMessage);
			}else{
				confirmSubmit = confirm(message.conditionMessage);
			}
			if(confirmSubmit == false){
				return;
			}
		}
		if(checkGroovyScriptOrder()){		
			var flag = confirm("Groovy Script Order is changed. Would you like to continue ?");
			if(flag == false){
				return;	
			}
		}
		enableAll();
		document.forms[0].submit();
		return true;
	}
}

function checkGroovyScriptOrder(){
	var orderNumArray = document.getElementsByName("orderNumber");
	var isOrderChange=false;
	if(orderNumArray!=null && orderNumArray.length>0){
		for(var i=1; i<orderNumArray.length; i++){
			/* alert(i+"=="+orderNumArray[i-1].value); */
			if(parseInt(i)!=parseInt(orderNumArray[i-1].value)){	
		 		isOrderChange=true;
		 		break;
		 	}
		}		
	}	
	return isOrderChange;
}

function isValidAppId(val){
	var tempArray = val.split(':');
	if(tempArray.length==2){
		if(isEcNaN(tempArray[0]) || isEcNaN(tempArray[1]) ){
			return false;
		}else{
			return true;
		}
	}
	return false;
}
 	
function validateStaticPolicyKey(){
	var flag=true;
	$("input[name=policyKeyS]").each(function() {
		if($(this).val().trim().length<1){
			this.focus();
			alert("Static PCCRule Mapping :\n\nAttribute or Policy Key should not be empty.");
			flag= false;
			return flag;
		}
	});
	return flag;
}
function validateStaticAttribute(){
	var flag=true;
	$("input[name=attributeS]").each(function(){
		if($(this).val().trim().length<1){
			this.focus();
			alert("Static PCCRule Mapping :\n\nAttribute or Policy Key should not be empty.");			 
			flag= false;
			return flag;
		}
	});
	return flag;
} 

function validateDynamicPolicyKey(){
	var flag=true;
	$("input[name=policyKeyD]").each(function() {
		if($(this).val().trim().length<1){
			alert("Dynamic PCCRule Mapping :\n\nAttribute or Policy Key should not be empty.");
			flag= false;
			return flag;
		}
	});
	return flag;
}
function validateDynamicAttribute(){
	var flag=true;
	$("input[name=attributeD]").each(function(){
		if($(this).val().trim().length<1){
			alert("Dynamic PCCRule Mapping :\n\nAttribute or Policy Key should not be empty.");			 
			flag= false;
			return flag;
		}
	});
	return flag;
} 

function addGroovyScript(){
	var orderNumArray = document.getElementsByName("orderNumber");
	var currentOrderNumber=1;
	if(orderNumArray!=null && orderNumArray.length>0){
		for(var i=0; i<orderNumArray.length; i++){
			currentOrderNumber = orderNumArray[i].value;	
		}
		currentOrderNumber++;
	}
	$('<tr><td class="tblrows"><input tabindex="30"  class="noborder" type="hidden" name="orderNumber" maxlength="4" value="'+currentOrderNumber+'" style="width: 100%"/>&nbsp;'+currentOrderNumber+'</td>'+
			  '<td class="tblrows"><input tabindex="30" class="noborder" type="text" name="scriptName" maxlength="2048" style="width: 100%"/></td>'+
			  '<td class="tblrows"><input tabindex="30" class="noborder" type="text" name="argument" maxlength="2048" style="width: 100%"/></td>'+			  
			  '<td class="tblrows" align="center"  valign="middle"><img value="top" tabindex="30" src="<%=basePath%>/images/minus.jpg" class="delete" onclick="$(this).parent().parent().remove();"  height="14" /></td></tr>').appendTo('#groovyScripts');
	
	$("input[name=scriptName]").each(function() {		
		if($(this).val().trim().length<1){						
			this.focus();			
		}
	});


}
function validateScriptName(){	
	var flag=true;	
	$("input[name=scriptName]").each(function() {		
		if($(this).val().trim().length<1 && flag==true){			
			alert("Invalid Script Name.");
			this.focus();
			flag= false;			
		}
	});
	return flag;
}
function validateOrderNumber(){	
	var flag=true;
	var orderNumArray = document.getElementsByName("orderNumber");
	$("input[name=orderNumber]").each(function() {
		if($(this).val().trim().length<1 || !isInteger($(this).val().trim())){
			alert("Groovy Script Order# must be a Positive Integer.");
			this.focus();
			flag= false;
			return flag;
		}
	});
	if(orderNumArray!=null && orderNumArray.length>0){
		for(var i=0; i<orderNumArray.length-1; i++){			
			if(orderNumArray[orderNumArray.length-1].value.trim()==orderNumArray[i].value.trim()){
				alert("Duplicate Groovy Script Order# Found.");
				flag=false;
			}
		}
	}
	return flag;
}
function clearDiv(){
	$("#errorMsgDiv_supportedVendorList").html("")
}

</script> 	


    
    
<html:form action="/editDiameterGatewayProfile">
<html:hidden property="action" value="update"/>
<html:hidden property="gatewayProfileName"/>
<html:hidden property="profileId"/>
<table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding-left: 1.6em">
    <tr>
		<td valign="top" align="right"> 
			<table width="100%" id="c_tblCrossProductList" align="right" border="0" > 						  			
				<tr> 
					<td align="left" class="labeltext" valign="top"  colspan="2">&nbsp;</td> 
		 		</tr>		 		
				<tr> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.diameter.timeout"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameter.timeout"/>','<bean:message bundle="gatewayResources" key="gateway.diameter.timeout" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text  property="timeout" styleId="timeout" maxlength="60" size="30"  styleClass="name" tabindex="1"/><font color="#FF0000"> *</font>
					</td>
				  </tr>
				  <tr>
					<td class="captiontext">
						<bean:message bundle="gatewayResources" key="gateway.diameterattribute.sessioncleanupon" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.sessioncleanupon"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.sessioncleanupon" />')"/>
					</td>
					<td class="labeltext" nowrap="nowrap">
						<html:checkbox property="sessionCleanUpCER" value="true" tabindex="2"></html:checkbox>
							<bean:message bundle="gatewayResources" key="gateway.diameterattribute.sessioncleanupcer" />
								&nbsp;&nbsp;&nbsp;
							<html:checkbox property="sessionCleanUpDPR" value="true" tabindex="3"></html:checkbox>
								<bean:message bundle="gatewayResources" key="gateway.diameterattribute.sessioncleanupdpr" />
					</td>
				</tr>
				
				<tr>
					<td align="left" class="captiontext" valign="top">
					  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.ceravps" />
					  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.ceravps"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.ceravps" />')"/>
					</td>
					<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
						<html:textarea styleId="cerAvps" name="editGatewayProfileForm" property="cerAvps" cols="50" rows="2" style="width:250px" tabindex="4"/>
					</td>
				</tr>
				
				<tr>
					<td align="left" class="captiontext" valign="top">
					  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.dpravps" />
					  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.dpravps"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.dpravps" />')"/>
					</td>
					<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
						<html:textarea styleId="dprAvps" name="editGatewayProfileForm" property="dprAvps" cols="50" rows="2" style="width:250px" tabindex="5"/>
					</td>
				</tr>
				
				<tr>
					<td align="left" class="captiontext" valign="top">
					  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.dwravps" />
					  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.dwravps"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.dwravps" />')"/>
					</td>
					<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
						<html:textarea styleId="dwrAvps" name="editGatewayProfileForm" tabindex="6" property="dwrAvps" cols="50" rows="2" style="width:250px"/>
					</td>
				</tr>
		<tr> 
			<td class="tblheader-bold" colspan="2" >
				<bean:message bundle="gatewayResources" key="gateway.diameterattribute.connectionparameters" />
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.connectionparameters"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.connectionparameters" />')"/>
			</td>	
  		</tr>
				
		<tr>
			<td align="left" class="captiontext" valign="top">
			  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.senddprcloseevent" />
			  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.senddprcloseevent"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.senddprcloseevent"/>')"/>
			</td>
			<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
				<html:select name="editGatewayProfileForm" styleId="sendDPRCloseEvent" tabindex="7" property="sendDPRCloseEvent" >
					<html:option value="false">False</html:option>
					<html:option value="true">True</html:option>
				</html:select>
			</td>
		</tr>
		
		<tr>
			<td class="captiontext">
				<bean:message bundle="gatewayResources" key="gateway.diameterattribute.socketreceivebuffersize" />
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.socketreceivebuffersize"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.socketreceivebuffersize"/>')"/>
			</td>
			<td class="labeltext" nowrap="nowrap">
				<html:text property="socketReceiveBufferSize" maxlength="8" tabindex="8" styleId="socketReceiveBufferSize"/>
			</td>
		</tr> 
		
		<tr>
			<td class="captiontext">
				<bean:message bundle="gatewayResources" key="gateway.diameterattribute.socketsendbuffersize" />
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.socketsendbuffersize"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.socketsendbuffersize"/>')"/>
			</td>
			<td class="labeltext" nowrap="nowrap">
				<html:text property="socketSendBufferSize" name="editGatewayProfileForm" tabindex="9" maxlength="8" styleId="socketSendBufferSize"/>
			</td>
		</tr>
		<tr>
			<td class="captiontext">
				<bean:message bundle="gatewayResources" key="gateway.diameterattribute.tcpnagleAlgorithm" />
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.tcpnagleAlgorithm"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.tcpnagleAlgorithm"/>')"/>
			</td>
			<td class="labeltext" nowrap="nowrap">
				<html:select property="tcpNagleAlgorithm" name="editGatewayProfileForm" tabindex="10">
					<html:option value="true">True</html:option>
					<html:option value="false">False</html:option>
				</html:select>
			</td>
		</tr> 
		
		<tr> 
			<td align="left" class="captiontext" valign="top" width="10%">
				<bean:message  bundle="gatewayResources" key="gateway.diameterattribute.dwrduration" />
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.dwrduration"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.dwrduration" />')"/>
			</td> 
			<td align="left" class="labeltext" valign="top" width="50px" >
				<html:checkbox  property="isDWRGatewayLevel" styleId="isDWRGatewayLevel" tabindex="11" onchange="setDWInterval()"/>&nbsp;
				<html:text  property="dwrInterval" maxlength="10" size="30" tabindex="12" styleId="dwInterval" disabled="true"  />
			</td>
		</tr>
		
		<tr>
			<td align="left" class="captiontext" valign="top">
			  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.dwrduration.initconnection" />
			  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.dwrduration.initconnection"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.dwrduration.initconnection"/>')"/>
			</td>
			<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
				<html:text name="editGatewayProfileForm" styleId="initConnectionDuration" property="initConnectionDuration" maxlength="10" tabindex="13"/>
			</td>
		</tr>
				  <tr> 
					<td class=tblheader-bold colspan="5" >
						<bean:message bundle="gatewayResources" key="gateway.diameterattribute.applicationparameters" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.applicationparameters"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.applicationparameters" />')"/>
					</td>	
		  		  </tr>
				  	
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customgxappid" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.customgxappid"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.customgxappid" />')"/>
					</td> 
					<td align="left" class="" valign="top" width="50px" > 
						<html:checkbox  property="isCustomGxAppId" styleId="isCustomGxAppId" tabindex="15" onchange="setGx()"/>&nbsp;
						<html:text  property="gxApplicationId" maxlength="60" size="30" styleId="gx" tabindex="16" disabled="true" /><label class="small-text-grey"> Vendor Id : Application Id </label>
					</td>
				  </tr>
				  
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customgyappid" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.customgyappid"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.customgyappid" />')"/>
					</td> 
					<td align="left" class="" valign="top" width="50px" > 
						<html:checkbox  property="isCustomGyAppId" styleId="isCustomGyAppId" tabindex="17" onchange="setGy()"/>&nbsp;
						<html:text  property="gyApplicationId" maxlength="60" size="30" tabindex="18" styleId="gy" disabled="true" /><label class="small-text-grey"> Vendor Id : Application Id </label>
					</td>
				  </tr>
				  
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customrxappid" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.customrxappid"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.customrxappid" />')"/>
					</td> 
					<td align="left" class="" valign="top" width="50px" > 
						<html:checkbox  property="isCustomRxAppId" styleId="isCustomRxAppId" tabindex="19" onchange="setRx()"/>&nbsp;
						<html:text  property="rxApplicationId" maxlength="60" size="30" tabindex="20" styleId="rx" disabled="true"/><label class="small-text-grey"> Vendor Id : Application Id </label>
					</td>
				  </tr>
				  
				  <tr>
					<td align="left" class="captiontext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customs9appid" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.customs9appid"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.customs9appid" />')"/>
					</td> 
					<td align="left" class="" valign="top" width="50px" > 
						<html:checkbox  property="isCustomS9AppId" styleId="isCustomS9AppId" tabindex="22" onchange="setS9()" value="true"/>&nbsp;
						<html:text  property="s9ApplicationId" maxlength="60" size="30" tabindex="23" styleId="s9" disabled="true"/><label class="small-text-grey"> Vendor Id : Application Id </label>
					</td>
				  </tr>

				  <tr>
					<td align="left" class="captiontext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customsyappid" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.customsyappid"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.customsyappid" />')"/>
					</td> 
					<td align="left" class="" valign="top" width="50px" >
						<html:checkbox  property="isCustomSyAppId" styleId="isCustomSyAppId" tabindex="23" onchange="setSy()" value="true"/>&nbsp;
						<html:text  property="syApplicationId" maxlength="60" size="30" tabindex="24" styleId="sy" disabled="true"/><label class="small-text-grey"> Vendor Id : Application Id </label> 
					</td>
				  </tr>
				  
				  <tr>
				  	<td align="left" class="captiontext" valign="top" width="10%">
				  		<bean:message bundle="gatewayResources" key="gateway.pccprovision"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.pccprovision"/>','<bean:message bundle="gatewayResources" key="gateway.pccprovision" />')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top" width="32%"> 
						<html:select property="pccProvision" styleId="pccProvision" tabindex="21" style="width: 206px">													
							<html:option value="0" >All On Network Entry</html:option>
							<html:option value="1">First On Network Entry</html:option>
							<html:option value="2">None On Network Entry</html:option>
						</html:select> 
					</td>
				</tr>
				
				<tr> 
					<td align="left" class="captiontext" valign="top" width="15%">
						<bean:message bundle="gatewayResources" key="gateway.profile.multichargingruleenabled" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.multichargingruleenabled"/>','<bean:message bundle="gatewayResources" key="gateway.profile.multichargingruleenabled" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="20%" > 
						<html:select styleId="multiChargingRuleEnabled" property="multiChargingRuleEnabled" tabindex="22" style="width:120px">
							<%
								ChargingRuleInstallMode[] values = ChargingRuleInstallMode.values();
								for(int i=0;i<values.length;i++){
							%>
								<html:option value='<%=String.valueOf(values[i].val)%>'><%=values[i].name()%></html:option>
							<%}%>
						</html:select>
					</td>					
				  </tr>
				  
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.supportedstandardlist" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.supportedstandardlist"/>','<bean:message bundle="gatewayResources" key="gateway.profile.supportedstandardlist" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:select  styleId="supportedStandard"  property="supportedStandard" tabindex="23">
							<html:option value="0">Select</html:option>
							<option value="1">Release 7</option>
							<option value="2">Release 8</option>
							<option value="3">Release 9</option>
							<option value="4">Cisco SCE</option>
						</html:select>
					</td>
				 </tr>
				   <tr> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.diameter.profile.umstandards" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.umstandard"/>','<bean:message bundle="gatewayResources" key="gateway.diameter.profile.umstandards" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" >
						    <html:select  styleId="umStandard"  property="umStandard"  tabindex="24" style="width:120px">
						  	<%
							UMStandard[] umStdArray = UMStandard.values();
							for(UMStandard umStd:umStdArray){%>
									<html:option value="<%=String.valueOf(umStd.value)%>"><%=umStd.displayValue%></html:option>
							<%}%>  
						    </html:select> 
					</td>
				  </tr>				  				  
				 
				   <tr> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.supportedvendorlist" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.supportedvendorlist"/>','<bean:message bundle="gatewayResources" key="gateway.profile.supportedvendorlist" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%"> 
						<html:textarea property="diameterSupportedVendorList" styleId="supportedVendorList" rows="2" onblur="clearDiv()" tabindex="25" />
						<div id="errorMsgDiv_supportedVendorList" class="labeltext"></div>
					</td>
				  </tr>
				 
				  <tr id="LookupKey"> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.diameter.profile.sessionlookupkey" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.sessionlookupkey"/>','<bean:message bundle="gatewayResources" key="gateway.diameter.profile.sessionlookupkey" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="sessionLookupKey" maxlength="100" styleId="sessionLookupKey" style="width:250px" ></html:text><font color="#FF0000"> *</font><br/>
						<label><font size="1px" color="RED"><bean:message bundle="gatewayResources" key="gateway.diameter.profile.sessionlookupkey.note" /></font></label>
					</td>
				  </tr>		
				  							 				  				  
				
<tr id="pccrules"><td colspan="3">
 <table cellpadding="0" cellspacing="0" border="0" width="100%">
 	<tr>
		<td class="tblheader-bold" colspan="5" style="padding-left: 2em" height="20%">
			<bean:message bundle="gatewayResources" key="pccrulemapping"/>
			<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.dyanamicpccrulemapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.diameter.dyanamicpccrulemapping" />')"/>
		</td>
	</tr>

	<tr><td width="10" class="small-gap">&nbsp;</td></tr>
	<tr>
		<td><input type="button" value="Add Mapping" tabindex="31"  style="margin-left: 2.2em" class="light-btn" onclick="addPCCRuleMappingRow()" ></td>
	</tr>
    <tr><td width="10" class="small-gap">&nbsp;</td></tr>
    <tr> 
	  	<td class="btns-td" valign="middle" colspan="3">
	  	  
			<table cellpadding="0" id="ruleMappingTable" cellspacing="0" border="0" width="97%" class="">
				<thead>
				<tr>
					<td align="center" class="tblheader" valign="top" width="20%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
					<td align="center" class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="gateway.profile.accessnetworktype" /></td>
					<td align="center" class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.remove" /></td>
				</tr>
				</thead>
				<tbody>
				<logic:iterate id="pccRuleMappingBean" name="editGatewayProfileForm" property="gatewayProfileRuleMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData">
					<tr>
						<td class="allborder" align="center">
							<html:select  styleClass="noborder" name="pccRuleMappingBean" property="ruleMappingId" tabindex="7"  style="width: 100%;">
								 		<logic:iterate id="ruleMapping" name="editGatewayProfileForm" property="ruleMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData">
											<html:option value="<%=Long.toString(ruleMapping.getRuleMappingId())%>"><bean:write name="ruleMapping" property="name" /></html:option>
										</logic:iterate>
					       </html:select>
						</td>
						<td class="tblrows">
							<input class="accessNetworkType noborder" type="text" value='<bean:write name="pccRuleMappingBean" property="accessNetworkType" />' name="ruleMappingCondition" maxlength="4000" style="width: 100%" >
						</td>
						<td class="tblrows" align="center" valign="middle">
							<img src="<%=basePath%>/images/minus.jpg" class="delete" height="15" onclick="removePDRow()">
						</td>
					</tr>
				</logic:iterate> 
						</tbody>
			</table>
			
		</td> 
	</tr>
	<tr>
		<td  class="captiontext"  colspan="3">	
				<label  class="small-text-grey"><bean:message  key="table.ordering.note"/></label> 
		</td>
	</tr>
	<tr><td width="10" class="">&nbsp;</td></tr>
	</table>
	</td>
	</tr>
	 		<tr>
				<td  colspan="2" class="tblheader-bold" height="20%" >
					<bean:message bundle="gatewayResources" key="gateway.profile.diameter.diametertopcrfpacketmapping" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.diametertopcrfpacketmapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.diameter.diametertopcrfpacketmapping" />')"/>
				</td>
			</tr>	

			  	<td valign="middle"  colspan="2">  
					<table cellpadding="0" id="mapReq" cellspacing="0" border="0" width="97%" style="padding-left: 1.6em">
							<thead>
    						<tr><td  colspan="2"><input type="button" id="reqBtn1" value="Add Mapping" tabindex="28" class="light-btn" onclick="openDiameterToPCRFMapping()"></td></tr>
		    				<tr> 							
								<tr>
									<td align="center" style="cursor:default" class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
									<td align="center" style="cursor:default" class="tblheader" valign="top" width="60%"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
									<td align="center" style="cursor:default" class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.remove" /></td>
								</tr>
							</thead>
							<tbody>
							<logic:iterate id="packetMap" name="editGatewayProfileForm" property="profilePacketDPMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
								<tr name="mappingRequest">
									<td align="left" class="tblfirstcol" valign="top"><bean:write name="packetMap" property="packetMappingData.name" />
										<input  tabindex="28" type='hidden' name='reqPacketMapId' value='<bean:write name="packetMap" property="packetMapId" />'/>
									</td>	
									<td align="left" class="tblrows" valign="top">
										<input tabindex="28" class='noborder' type='text' name='reqCondition' id="reqCondition" maxlength='200' style="width: 100%" value='<bean:write name="packetMap" property="condition" />'/></td>
									<td style="cursor:default" class="tblrows"  align="center" valign="middle">
										<img src="<%=basePath%>/images/minus.jpg" style="padding-right: 5px; padding-top: 5px;" height="14" onclick="removeDPRow('<bean:write name="packetMap" property="packetMappingData.packetMapId" />')"/></td>
								</tr>
							</logic:iterate>
							</tbody>
					</table>
				</td> 
				
			</tr>	
			 <tr>
				<td class="small-text-grey" colspan="2" style="padding-left: 2.5em">
					<bean:message  key="table.ordering.note"/> 
				</td>
			</tr>
			<tr><td  colspan="2" width="10" class="">&nbsp;</td></tr>		
			<tr> 
		       <td  colspan="2" class="tblheader-bold"  height="20%" >
		       		<bean:message bundle="gatewayResources" key="gateway.profile.diameter.pcrftodiameterpacketmapping" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.pcrftodiameterpacketmapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.diameter.pcrftodiameterpacketmapping" />')"/>
		       </td>
		    </tr>

		    <tr> 
			  	<td valign="middle"  colspan="2">  
					<table cellpadding="0" id="mapRes" cellspacing="0" border="0" width="97%" style="padding-left: 1.6em">
						<thead>
		    <tr>
				<td  colspan="2"><input type="button" id="reqBtn2" value="Add Mapping" tabindex="29" class="light-btn" onclick="openPCRFToDiameterMapping()"></td>
			</tr>						
							<tr>
								<td align="center" style="cursor:default" class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
								<td align="center" style="cursor:default" class="tblheader" valign="top" width="60%"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
								<td align="center" style="cursor:default" class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.remove" /></td>
							</tr>
						</thead>
						<tbody>
						<logic:iterate id="packetMap" name="editGatewayProfileForm" property="profilePacketPDMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
							<tr name="mappingResponse">
								<td align="left" class="tblfirstcol" valign="top"><bean:write name="packetMap" property="packetMappingData.name" />
									<input  tabindex="29" type='hidden' name='resPacketMapId' value='<bean:write name="packetMap" property="packetMapId" />'/>
								</td>	
								<td align="left" class="tblrows" valign="top">
									<input  tabindex="29" class='noborder' type='text' name='resCondition' id="resCondition" maxlength='200' style="width: 100%" value='<bean:write name="packetMap" property="condition" />'/>
								</td>
								<td style="cursor:default" class="tblrows"  align="center" valign="middle">
									<img  tabindex="29" src="<%=basePath%>/images/minus.jpg" style="padding-right: 5px; padding-top: 5px;" height="14" onclick="removePDRow('<bean:write name="packetMap" property="packetMappingData.packetMapId" />')"/></td>
								</td>
							</tr>
						</logic:iterate>
						</tbody>
					</table>
				</td> 
			</tr>	
			<tr>
			 	<td class="small-text-grey" colspan="2" style="padding-left: 2.5em">
				  <bean:message  key="table.ordering.note"/> 
			  	</td>
			 </tr>
				<!-- groovy start -->
			<tr> 
		       <td  colspan="2" class="tblheader-bold"  height="20%" >
		       		<bean:message bundle="gatewayResources" key="gateway.profile.groovyscript" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="profile.groovy.script"/>','<bean:message bundle="gatewayResources" key="gateway.profile.groovyscript" />')"/>
		       </td>
		    </tr>				
				<tr><td width="10" class="small-gap">&nbsp;</td></tr> 
				<tr>
					<td><input type="button" id="groovy_script_btn" value="Add Groovy Script" tabindex="30" style="margin-left: 2.2em" class="light-btn" onclick="addGroovyScript()" ></td>
				</tr>
    			<tr><td width="10" class="small-gap">&nbsp;</td></tr>
    
     			<tr> 
	  				<td class="btns-td" valign="middle" colspan="3">  
						<table cellpadding="0" id="groovyScripts" cellspacing="0" border="0" width="97%" class="">
						<thead>
						<tr>
							<td align="center" style="cursor:default" class="tblheader" valign="top" width="10%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.ordernumber" /></td>
							<td align="center" style="cursor:default"  class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.name" /></td>
							<td align="center" style="cursor:default"  class="tblheader" valign="top" width="40%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.argument" /></td>
							<td align="center" style="cursor:default"  class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.remove" /></td>						
						</tr>
						</thead>
						<tbody>
 						<logic:iterate id="groovyScriptData" name="groovyScriptsDataList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData">							
						 <tr>
                         	<td class='tblrows'><input  type='hidden' name='orderNumber' maxlength='5' style='width: 100%' value='&nbsp;<bean:write name="groovyScriptData" property="orderNumber"/>'/>&nbsp;<bean:write name="groovyScriptData" property="orderNumber"/></td>
                         	<td class='tblrows'><input tabindex="30" class='noborder' type='text' name='scriptName' maxlength='2048' style='width: 100%' value='<bean:write name="groovyScriptData" property="scriptName"/>'/></td>
                         	<td class='tblrows'><input tabindex="30" class='noborder' type='text' name='argument' maxlength='2048' style='width: 100%' value='<bean:write name="groovyScriptData" property="argument"/>'/></td>
                         	<td class='tblrows' style='cursor: default' align='center' valign='middle'><img  tabindex="30" src='<%=basePath%>/images/minus.jpg' class='delete' height='14' /></td>
                         </tr>							                        
						</logic:iterate>
 						</tbody>
						</table>
					</td> 
				</tr>	  
				<tr>
					<td  class="captiontext"  colspan="3">	
						<label  class="small-text-grey"><bean:message  key="table.ordering.note"/></label> 
					</td>
				</tr>		 			
				<!-- groovy end -->	 			 
    	 		<tr> 
					<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td> 
		 		</tr>
        		<tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;
	        			<html:hidden property="profileId" styleId="profileId" name="editGatewayProfileForm" />
	        		</td> 
            		<td class="btns-td" valign="middle" align="left"> 
		        		<input type="button" align="left" id="updateBtn" onclick="validate();"  value="  Update  " class="light-btn" tabindex="31" />
		        		<input type="button" align="left" value="  Cancel  " class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchProfile.do?/>'" tabindex="32"/>
		        	</td> 
   		 		</tr>
   		 		 				 
			 </table> 
		</td> 
	</tr>		 				 				  					 
</table>
</html:form>    	
	


<div id="popupDiameterToPCRFMapping" title="Add Packet Mapping" style="display: none;">
	<div class="labeltext">&nbsp;&nbsp;&nbsp;Condition &nbsp;&nbsp;&nbsp;<textarea rows="2" cols="50"  id='dpcond'></textarea></div>

	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center" class="tblheader" valign="top" width="1%">&nbsp;</td>
			<td align="left" class="tblheader" valign="top" width="18%">Name</td>
			<td align="left" class="tblheader" valign="top" width="15%">CommPro & Type</td>
			<td align="left" class="tblheader" valign="top" width="30%">Description</td>
		</tr>
	<logic:iterate id="packetMap" name="editGatewayProfileForm" property="diameterToPCRFPacketMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData">
		<tr id="<bean:write property="packetMapId" name="packetMap" />">
			<td align="center" class="tblfirstcol">
				<input type="radio" name="diameterToPCRFMapList" id="<bean:write property="packetMapId" name="packetMap" />" value="<bean:write property="name" name="packetMap" />" />
			<td align="left" class="tblrows">
				<bean:write name="packetMap" property="name" />&nbsp;</td>
			<td align="left" class="tblrows">
				<bean:write name="packetMap" property="packetType" />,&nbsp;
				<bean:write name="packetMap" property="type" />&nbsp;</td>
			<td align="left" class="tblrows">
				<bean:write name="packetMap" property="description" />&nbsp;</td>
		</tr>
	</logic:iterate>
	</table>
</div>
<!-- Hiding Rows -->
	
	<logic:iterate id="packetMap" name="editGatewayProfileForm" property="profilePacketDPMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
			<script>
			hidePopupWindowRow('<bean:write name="packetMap" property="packetMappingData.packetMapId" />');
			jDiameterToPCRFRefArray[jDPCount++] = '<bean:write name="packetMap" property="packetMappingData.packetMapId" />';
			</script>
    </logic:iterate>


<div id="popupPCRFToDiameterMapping" title="Add Packet Mapping" style="display: none;">

	<div class="labeltext">&nbsp;&nbsp;&nbsp;Condition &nbsp;&nbsp;&nbsp;
		<textarea rows="2" cols="50" id="pdcondition" ></textarea>
		<select id='policyKeys' hidden='false'>
		<% for(PCRFKeyConstants keyConstants : PCRFKeyConstants.values(PCRFKeyType.RULE)){%>
			  <option value="<%= keyConstants.getVal()%>" ><%= keyConstants.getVal()%></option>
		<%}%>
		</select>
		<br>
		&nbsp;&nbsp;&nbsp;<label  class="small-text-grey"><bean:message bundle="descriptionResources" key="expression.possibleoperators"/></label>
	</div>

	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center" class="tblheader" valign="top" width="1%">
				&nbsp;</td>
			<td align="left" class="tblheader" valign="top" width="18%">
				Name</td>
			<td align="left" class="tblheader" valign="top" width="15%">
				CommPro & Type</td>
			<td align="left" class="tblheader" valign="top" width="30%">
				Description</td>
		</tr>
	<logic:iterate id="packetMap" name="editGatewayProfileForm" property="pcrfToDiameterPacketMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData">
		<tr id="<bean:write property="packetMapId" name="packetMap" />">
			<td align="center" class="tblfirstcol">
				<input type="radio" name="pcrfToDiameterMapList" id="<bean:write property="packetMapId" name="packetMap" />" value="<bean:write property="name" name="packetMap" />" />
			<td align="left" class="tblrows">
				<bean:write name="packetMap" property="name" />&nbsp;</td>
			<td align="left" class="tblrows">
				<bean:write name="packetMap" property="packetType" />,&nbsp;
				<bean:write name="packetMap" property="type" />&nbsp;</td>
			<td align="left" class="tblrows">
				<bean:write name="packetMap" property="description" />&nbsp;</td>
		</tr>
	</logic:iterate>
	</table>
</div>


<!-- Hiding Rows -->
	
	<logic:iterate id="packetMap" name="editGatewayProfileForm" property="profilePacketPDMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
			<script>
			hidePopupWindowRow('<bean:write name="packetMap" property="packetMappingData.packetMapId" />');
			jPCRFToDiameterRefArray[jPDCount++] = '<bean:write name="packetMap" property="packetMappingData.packetMapId" />';
			</script>
    </logic:iterate>

<table id="templateRuleMappingTable" style="display: none;">
	<tr>
		<td class="allborder" align="center" >
			<select name="ruleMappingId" style="width: 98%; height: 20px; font-size: 12px; font-family: Arial" >
				 <logic:iterate id="ruleMapping" name="editGatewayProfileForm" property="ruleMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData">
				 	<option value="<bean:write property="ruleMappingId" name="ruleMapping" />">
				 		<bean:write name="ruleMapping" property="name" />
				 	</option>
				</logic:iterate> 
			</select>		
		</td>
		<td class="tblrows">
			<input class="accessNetworkType noborder" type="text"  name="ruleMappingCondition" maxlength="4000" style="width: 100%" >
		</td>
		<td class="tblrows" align="center" valign="middle">
			<img src="<%=basePath%>/images/minus.jpg" class="delete" height="15" onclick="removePDRow()">
		</td>
	</tr>
</table>    