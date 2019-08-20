<%@page import="com.elitecore.corenetvertex.constants.GatewayComponent"%>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyConstants"%>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyValueConstants"%>
<%@page import="com.elitecore.corenetvertex.constants.SupportedStandard"%>
<%@page import="com.elitecore.corenetvertex.constants.UMStandard"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.DefaultAttributeMappingData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData" %>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData"%>
<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%> 
<%@page import="com.elitecore.corenetvertex.constants.ChargingRuleInstallMode"%>
<script type="text/javascript" src="jquery/libs/expressionlibrary/prototype.js"></script>
<script type="text/javascript" src="jquery/libs/expressionlibrary/jquery-2.1.0.min.js"></script>
<script type="text/javascript" src="jquery/libs/expressionlibrary/jquery-ui-min.js"></script>
<script type="text/javascript" src="jquery/libs/expressionlibrary/autocompleter.js"></script>
<script type="text/javascript" src="jquery/libs/expressionlibrary/expressionlibrary.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<script>
var validateUrl = '<%=basePath%>/validate.do?';
var basePath = '<%=basePath%>';
var image = '<%=request.getContextPath()%>/images/tick.jpg';
</script>
<script language="javascript" 	src="<%=request.getContextPath()%>/js/validateMapping.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

<style> 
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.bottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style> 
<%
	String gatewayType=(String)request.getAttribute("gatewayType");
	
%>
<script type="text/javascript">

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
		position:["top",100],
		buttons:{					
            'Add': function() {	
				var name = $('#packetMapId').text(); 
				var nameId = $('#packetMapId').val();
				var cond = $('#dpcond').val();
				var selectedItems = document.getElementsByName("diameterToPCRFMapList");
     			var i = 0;							
				var flag = true;
				if(flag){	

                    for(var i=0; i<selectedItems.length; i++) {
    					if(selectedItems[i].checked == true) {
                            var labelVal = selectedItems[i].value;
                            var labelid = selectedItems[i].id;
                            
                            	$("#mapReq").append("<tr name='mappingRequest'>"+
                            	"<td class='tblfirstcol'>" + labelVal +"<input type='hidden' name='reqPacketMapId' value='" + labelid + "'/></td>"+
                            	"<td class='tblrows' ><input class='noborder' type='text' id='reqCondition' name='reqCondition' maxlength='2000' style='width: 100%' value='" + cond + "'/></td>"+
                            	"<td class='tblrows' align='center' valign='middle'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='14' onclick='removeDPRow(\""+labelid+"\")' /></td></tr>");
                                selectedItems[i].checked=false;
                                $("#"+labelid).attr("style","display:none");
                                jDiameterToPCRFRefArray[jDPCount++] = labelid;
                        }
    					document.getElementById("dpcond").value = "";

    					//delete call
	         			$('#mapReq td img.delete').on('click',function() {
	       				var removalVal = $(this).closest('tr').find('td:eq(0)').text(); 	
	       				for(var d=0;d<jDPCount;d++){
	       					var currentVal = jDiameterToPCRFRefArray[d];	
	       					if(currentVal == removalVal)
		       				{
	       						$("#"+removalVal).removeAttr("style");
	       						jDiameterToPCRFRefArray[d] = '  ';
	       						break;
	       					}
	       				}
	       				$(this).closest('tr').next("tr[name='noteTR']").remove();
	       				$(this).parent().parent().remove();
	       				});		         						          					          	
	         			$('#mapReq').tableDnD();
		          		$(this).dialog('close');	
		          		 			          		         		
		         	}	
         		}
				$("#reqCondition").focus();
            },                
		    Cancel: function() {
            	$(this).dialog('close');
            	$("#pcrf_to_gateway_btn").focus();
        	}
        },
    	open: function() {
    		
    	},
    	close: function() {
    		$("#pcrf_to_gateway_btn").focus();
    	}				
	});
	$( "#popupDiameterToPCRFMapping" ).dialog("open");
}
function sessionLookupKey() {
	var sessionId = new Array();
	<% for(PCRFKeyConstants keyConstants : PCRFKeyConstants.values(PCRFKeyType.REQUEST)){%>
	 		sessionId.push('<%=keyConstants.getVal()%>');
	<%}%> 
	commonAutoComplete("sessionLookupKey",sessionId);
}
function checkGatewayComponent() {
	<%if((gatewayType.equalsIgnoreCase(GatewayComponent.APPLICATION_FUNCTION.value)) || (gatewayType.equalsIgnoreCase(GatewayComponent.DRA.value))){ %>
		$('#LookupKey').show();
	<%}else{%>
		$('#LookupKey').hide();
	<%}%>
}

function removeDPRow(rowId)
{	
	$("#"+rowId).removeAttr("style");    
 	$('#mapReq td img.delete').on('click',function() {
		 $(this).parent().parent().remove();
	}); 
}	

function addPCCRuleMappingRow() {
	$("#ruleMappingTable tr:last").after("<tr>" + $("#templateRuleMappingTable").find("tr").html() + "</tr>");
	accessNetworkSuggestion();
	$('#ruleMappingTable').tableDnD();
	deleteRows();
}



function openPCRFToDiameterMapping() {
	$.fx.speeds._default = 1000;
	document.getElementById("popupPCRFToDiameterMapping").style.visibility = "visible";		
	$( "#popupPCRFToDiameterMapping" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 450,
		width: 570,	
		position:["top",100],
		buttons:{					
            'Add': function() {	
				var name = $('#packetMapId').text(); 
				var nameId = $('#packetMapId').val();
				var cond = $('#pdcondition').val();
				var selectedItems = document.getElementsByName("pcrfToDiameterMapList");
     			var i = 0;							
				var flag = true;												 	
               
				if(flag){	

                    for(var i=0; i<selectedItems.length; i++) {
    					if(selectedItems[i].checked == true) {
                            var labelVal = selectedItems[i].value;
                            var labelid = selectedItems[i].id;
                            $("#mapRes").append("<tr name='mappingResponse'>"+
                            "<td class='tblfirstcol' >" + labelVal +"<input type='hidden' name='resPacketMapId' value='" + labelid + "'/></td>"+
                            "<td class='tblrows' ><input class='noborder' type='text' id='resCondition' name='resCondition' maxlength='200' style='width: 100%' value='" + cond + "'/></td>"+
                            "<td class='tblrows'  align='center' valign='middle'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' onclick='removePDRow(\""+labelid+"\")' /></td></tr>");
                            selectedItems[i].checked=false;
                            $("#"+labelid).attr("style","display:none");
                            jPCRFToDiameterRefArray[jPDCount++] = labelid;
                        }
    					document.getElementById("pdcondition").value = "";
    					
	         			$('#mapRes td img.delete').on('click',function() {

         				var removalVal = $(this).closest('tr').find('td:eq(0)').text(); 	
	       				for(var d=0;d<jPDCount;d++){
	       					var currentVal = jPCRFToDiameterRefArray[d];	
	       					if(currentVal == removalVal)
		       				{
	       						$("#"+removalVal).removeAttr("style");
	       						jPCRFToDiameterRefArray[d] = '  ';
	       						break;
	       					}
	       				}
	       				$(this).closest('tr').next("tr[name='noteTR']").remove();
	       				$(this).parent().parent().remove();
	       				});	
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
	pdcondition();
}

function  pdcondition(){
		$("#pdcondition").autocomplete();
		var dbFieldArray = [];
		<%
			List<PCRFKeyConstants> pcrfKeyList =  PCRFKeyConstants.values(PCRFKeyType.RULE);
		   for(PCRFKeyConstants constant:pcrfKeyList){
		%>
				dbFieldArray.push('<%=constant.getVal()%>');					
		<%}%>
		var autocompleter1 = createModel(dbFieldArray);
		expresssionAutoComplete('pdcondition',autocompleter1);
	};

function removePDRow(rowId)
{

	$("#"+rowId).removeAttr("style");    
 	$('#mapRes td img.delete').on('click',function() {
		 $(this).parent().parent().remove();
	}); 	
}

function removePCCRuleMappingRow(rowId){

	$("#"+rowId).removeAttr("style");    
 	$('#ruleMappingTable td img.delete').on('click',function() {
		 $(this).parent().parent().remove();
	}); 	
}

$(document).ready(function(){
	setTitle('<bean:message bundle="gatewayResources" key="gatewy.profile.header" />');
	$('#timeout').focus();
	$("#cerAvps").attr('maxlength','2000');
	$("#dprAvps").attr('maxlength','2000');
	$("#dwrAvps").attr('maxlength','2000'); 
	 	
	$('table td img.delete').on('click',function() {
		$(this).parent().parent().remove(); 
	});
	$("#supportedVendorList").attr('maxlength','254');
	$('#add').click(function() {
		$('<tr><td class="allborder"><input class="noborder" type="text" name="attribute" maxlength="200" style="width :100%"/>'+
		  '</td><td class="tblrows"><input class="plcKey" type="text" name="policyKey" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows" colspan="3"  align="center" valign="middle"><img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px;" height="14" onclick="remove(main)" /></td></tr>').appendTo('#map');
		policyKey();
	});

	var mappingTypeArray = new Array();	
	mappingTypeArray[0] = "<%=PCRFKeyType.PCC_RULE.getVal()%>";
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoCompleteUsingCssClass("td input.plcKey",dbFieldArray);
		return dbFieldArray;
	});
	supportedVendorList();
	sessionLookupKey();
	$('#supportedStandard').change(function() {
		
		var id = $(this).val();
		if(id==1 || id==2 || id==3) {
			$('#pccrules').show();
		}else if(id==4) {
			$('#pccrules').hide();
		}
	});
	dragAndDrop();
	checkGatewayComponent();
	
	
});
function dragAndDrop(){
	$('#mapReq').tableDnD();
	$('#mapRes').tableDnD();
	$('#ruleMappingTable').tableDnD();
}

function deleteRows(){
	    $('#groovyScripts td img.delete').on('click',function() {
			$(this).parent().parent().remove();
		});
		$('#static td img.delete').on('click',function() {
			$(this).parent().parent().remove();
		});
		$('#dynamic td img.delete').on('click',function() {
			$(this).parent().parent().remove();
		});
		$('#ruleMappingTable td img.delete').on('click',function() {
			 $(this).parent().parent().remove();
		}); 
	
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

function addRow(type) {
	if(type=='static') 
		$('<tr><td class="allborder"><input tabindex="26" class="noborder" type="text" name="attributeS" maxlength="200" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="26" class="plcKey" type="text" name="policyKeyS" maxlength="4000" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="26" class="noborder" type="text" name="defaultValueS" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="26" class="noborder" type="text" name="valueMappingS" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows" align="center"  valign="middle"><img value="top"  tabindex="26" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px" height="14" /></td></tr>').appendTo('#'+type);
	else
		$('<tr><td class="allborder"><input  tabindex="27" class="noborder" type="text" name="attributeD" maxlength="200" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="27" class="plcKey" type="text" name="policyKeyD" maxlength="4000" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="27" class="noborder" type="text" name="defaultValueD" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows"><input tabindex="27" class="noborder" type="text" name="valueMappingD" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows" align="center" valign="middle"><img value="top" tabindex="27" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px;" height="14" /></td></tr>').appendTo('#'+type);		
	policyKey();
	deleteRows();
}
function addGroovyScript(){
	var orderNumArray = document.getElementsByName("orderNumber");
	var currentOrderNumber=1;
	if(orderNumArray!=null && orderNumArray.length>0){
		currentOrderNumber = orderNumArray.length+1;
	}
	$('<tr><td class="allborder"><input tabindex="30"  class="noborder" type="hidden" name="orderNumber" maxlength="200" value="'+currentOrderNumber+'" style="width: 100%"/>'+currentOrderNumber+'</td>'+
			  '<td class="tblrows"><input tabindex="30" class="plcKey" type="text" name="scriptName" maxlength="2048" style="width: 100%"/></td>'+
			  '<td class="tblrows"><input tabindex="30" class="noborder" type="text" name="argument" maxlength="2048" style="width: 100%"/></td>'+			  
			  '<td class="tblrows" align="center" valign="middle"><img value="top" tabindex="30" src="<%=basePath%>/images/minus.jpg" class="delete" height="14" /></td></tr>').appendTo('#groovyScripts');
	
	
	$("input[name=scriptName]").each(function() {		
		if($(this).val().trim().length<1){						
			this.focus();			
		}
	});
	$('#groovyScripts').tableDnD();
	deleteRows();

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
	}else if(document.getElementById('isCustomSyAppId').checked==true && isNull(document.forms[0].sy.value)){
		alert('Custom Sy App Id must be specified.\nPlease specify it in format "Vendor Id:Application Id".');
		document.forms[0].sy.focus();
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
	}else if(document.getElementById('isCustomSyAppId').checked==true && !isValidAppId(document.forms[0].sy.value)){
		alert('Invalid Sy Application Id.\nPlease specify it in format "Vendor Id:Application Id".');
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
		enableAll();
		document.forms[0].submit();
		return true;
	}
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
function clearDiv(){
	$("#errorMsgDiv_supportedVendorList").html("");
}

</script> 	

<html:form action="/profileDetail">

	<html:hidden name="createProfileForm" styleId="gatewayProfileName" property="gatewayProfileName" />
	<html:hidden name="createProfileForm" styleId="gatewayProfileName" property="gatewayType" />
	<html:hidden name="createProfileForm" styleId="vendor" property="vendorId"/>
	<html:hidden name="createProfileForm" styleId="commProtocol" property="commProtocol"/>
	<html:hidden name="createProfileForm" styleId="description" property="description" />
	<html:hidden name="createProfileForm" styleId="maxThroughtput" property="maxThroughtput" />
	<html:hidden name="createProfileForm" styleId="bufferBandwidth" property="bufferBandwidth" />
	<html:hidden name="createProfileForm" styleId="firmware" property="firmware" />
	<html:hidden name="createProfileForm" styleId="maxIPCANSession" property="maxIPCANSession" />
 	<html:hidden name="createProfileForm" styleId="usageReportingTime" property="usageReportingTime" />
 	<html:hidden name="createProfileForm" styleId="revalidationMode" property="revalidationMode" />
 	
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			<bean:message bundle="gatewayResources" key="gateway.diameter" /></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" id="c_tblCrossProductList" align="right" border="0">			   	  			    
			   	  
			   	 <tr> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.diameter.timeout"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameter.timeout"/>','<bean:message bundle="gatewayResources" key="gateway.diameter.timeout" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text  property="timeout" styleId="timeout" maxlength="10" size="30" styleClass="name" tabindex="1"/><font color="#FF0000"> *</font>
					</td>
				  </tr>
				
				  
				  <tr>
					<td class="captiontext">
						<bean:message bundle="gatewayResources" key="gateway.diameterattribute.sessioncleanupon" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.sessioncleanupon"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.sessioncleanupon" />')"/>
					</td>
					<td class="labeltext" nowrap="nowrap">
						<html:checkbox property="sessionCleanUpCER" value="true" tabindex="3"></html:checkbox>
							<bean:message bundle="gatewayResources" key="gateway.diameterattribute.sessioncleanupcer" />
								&nbsp;&nbsp;&nbsp;
							<html:checkbox property="sessionCleanUpDPR" value="true" tabindex="4"></html:checkbox>
								<bean:message bundle="gatewayResources" key="gateway.diameterattribute.sessioncleanupdpr" />
					</td>
				</tr>
				
				<tr>
					<td align="left" class="captiontext" valign="top">
					  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.ceravps" />
					  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.ceravps"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.ceravps" />')"/>
					</td>
					<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
						<html:textarea styleId="cerAvps" name="createProfileForm" property="cerAvps" cols="50" rows="2" style="width:250px" tabindex="5"/>
					</td>
				</tr>
				
				<tr>
					<td align="left" class="captiontext" valign="top">
					  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.dpravps" />
					  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.dpravps"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.dpravps" />')"/>
					</td>
					<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
						<html:textarea styleId="dprAvps" name="createProfileForm" property="dprAvps" cols="50" rows="2" style="width:250px" tabindex="6"/>
					</td>
				</tr>
				
				<tr>
					<td align="left" class="captiontext" valign="top">
					  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.dwravps" />
					  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.dwravps"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.dwravps" />')"/>
					</td>
					<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
						<html:textarea styleId="dwrAvps" name="createProfileForm" tabindex="7" property="dwrAvps" cols="50" rows="2" style="width:250px"/>
					</td>
				</tr>
		<tr> 
			<td class=tblheader-bold colspan="5" style="padding-left: 2em">
				<bean:message bundle="gatewayResources" key="gateway.diameterattribute.connectionparameters" />
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.connectionparameters"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.connectionparameters" />')"/>
			</td>	
		  </tr>
		  	  	
		<tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr>
		<tr>
			<td align="left" class="captiontext" valign="top">
			  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.senddprcloseevent" />
			  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.senddprcloseevent"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.senddprcloseevent"/>')"/>
			</td>
			<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
				<html:select name="createProfileForm" styleId="sendDPRCloseEvent" tabindex="8" property="sendDPRCloseEvent" >
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
				<html:text property="socketReceiveBufferSize" maxlength="8" tabindex="9" styleId="socketReceiveBufferSize"/>
			</td>
		</tr> 
		
		<tr>
			<td class="captiontext">
				<bean:message bundle="gatewayResources" key="gateway.diameterattribute.socketsendbuffersize" />
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.socketsendbuffersize"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.socketsendbuffersize"/>')"/>
			</td>
			<td class="labeltext" nowrap="nowrap">
				<html:text property="socketSendBufferSize" name="createProfileForm" tabindex="10" maxlength="8" styleId="socketSendBufferSize"/>
			</td>
		</tr>
		<tr>
			<td class="captiontext">
				<bean:message bundle="gatewayResources" key="gateway.diameterattribute.tcpnagleAlgorithm" />
				<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.tcpnagleAlgorithm"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.tcpnagleAlgorithm"/>')"/>
			</td>
			<td class="labeltext" nowrap="nowrap">
				<html:select property="tcpNagleAlgorithm" name="createProfileForm" tabindex="11">
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
			<td align="left" class="" valign="top" width="50px" >
				<html:checkbox  property="isDWRGatewayLevel" styleId="isDWRGatewayLevel" tabindex="12" onchange="setDWInterval()"/>&nbsp;
				<html:text  property="dwrInterval" maxlength="10" size="30" tabindex="13" styleId="dwInterval" disabled="true"  />
			</td>
		</tr>
				  		
		<tr>
			<td align="left" class="captiontext" valign="top">
			  <bean:message bundle="gatewayResources" key="gateway.diameterattribute.dwrduration.initconnection" />
			  <img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.dwrduration.initconnection"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.dwrduration.initconnection"/>')"/>
			</td>
			<td align="left" class="labeltext" valign="top"  nowrap="nowrap">
				<html:text name="createProfileForm" styleId="initConnectionDuration" property="initConnectionDuration" maxlength="10" tabindex="14"/>
			</td>
		</tr>
				  <tr> 
					<td class=tblheader-bold colspan="5" style="padding-left: 2em">
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
						<html:checkbox  property="isCustomGxAppId" styleId="isCustomGxAppId" tabindex="16" onchange="setGx()" value="true"/>&nbsp;
						<html:text  property="gxApplicationId" maxlength="60" size="30" styleId="gx" tabindex="17" disabled="true" /><label class="small-text-grey"> Vendor Id : Application Id </label>
					</td>
				  </tr>
				  
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customgyappid" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.customgyappid"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.customgyappid" />')"/>
					</td> 
					<td align="left" class="" valign="top" width="50px" > 
						<html:checkbox  property="isCustomGyAppId" styleId="isCustomGyAppId" tabindex="18" onchange="setGy()" value="true"/>&nbsp;
						<html:text  property="gyApplicationId" maxlength="60" size="30" tabindex="19" styleId="gy" disabled="true" /><label class="small-text-grey"> Vendor Id : Application Id </label>
					</td>
				  </tr>
				  
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="gateway.diameterattribute.customrxappid" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.customrxappid"/>','<bean:message bundle="gatewayResources" key="gateway.diameterattribute.customrxappid" />')"/>
					</td> 
					<td align="left" class="" valign="top" width="50px" > 
						<html:checkbox  property="isCustomRxAppId" styleId="isCustomRxAppId" tabindex="20" onchange="setRx()" value="true"/>&nbsp;
						<html:text  property="rxApplicationId" maxlength="60" size="30" tabindex="21" styleId="rx" disabled="true"/><label class="small-text-grey"> Vendor Id : Application Id </label>
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
				  
				  <tr id="pccRule">
				  	<td align="left" class="captiontext" valign="top" width="10%">
				  		<bean:message bundle="gatewayResources" key="gateway.pccprovision"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.pccprovision"/>','<bean:message bundle="gatewayResources" key="gateway.pccprovision" />')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top" width="32%"> 
						<html:select styleId="pccProvision"  property="pccProvision" tabindex="25"   style="width: 206px">													
							<option value="0">All On Network Entry</option>
							<option value="1">First On Network Entry</option>
							<option value="2">None On Network Entry</option>
						</html:select> 
					</td>
				  </tr>
				  
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="15%">
						<bean:message bundle="gatewayResources" key="gateway.profile.multichargingruleenabled" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.multichargingruleenabled"/>','<bean:message bundle="gatewayResources" key="gateway.profile.multichargingruleenabled" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="20%" > 
						<html:select name="createProfileForm" styleId="multiChargingRuleEnabled" property="multiChargingRuleEnabled" tabindex="26" style="width:120px">
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
						<html:select name="createProfileForm" styleId="supportedStandard"  property="supportedStandard" value="3" tabindex="27" style="width:120px">
								<html:option value="0">Select</html:option>
							<%
							 SupportedStandard[] st = SupportedStandard.values();							
							 for(SupportedStandard standard : st){%>
								<html:option value="<%=String.valueOf(standard.getId())%>"><%=standard.getName()%></html:option>
							<%}%>
						</html:select>
					</td>
				  </tr>				  				  

				   <tr> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.diameter.profile.umstandards" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.umstandard"/>','<bean:message bundle="gatewayResources" key="gateway.diameter.profile.umstandards" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:select name="createProfileForm" styleId="umStandard"  property="umStandard" value="1" tabindex="28" style="width:120px">
							<%
								UMStandard[] umStdArray = UMStandard.values();
								for(UMStandard umStd:umStdArray){
							%>
								<html:option value="<%=String.valueOf(umStd.value)%>"><%=umStd.displayValue%></html:option>
							<%  } %>
						</html:select>
					</td>
				  </tr>
				<tr> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.supportedvendorlist" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.supportedvendorlist"/>','<bean:message bundle="gatewayResources" key="gateway.profile.supportedvendorlist" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea property="supportedVendorList" styleId="supportedVendorList" rows="2"  cols="50"  tabindex="29" style="width:250px" onkeypress="supportedVendorList();" onblur="clearDiv();" />
						<div id="errorMsgDiv_supportedVendorList" class="labeltext"></div>
					</td>
				  </tr>				 		  				  				 
				  
				  <!-- Session lookup key -->	
				  <tr id="LookupKey"> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.diameter.profile.sessionlookupkey" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.sessionlookupkey"/>','<bean:message bundle="gatewayResources" key="gateway.diameter.profile.sessionlookupkey" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="sessionLookupKey" styleId="sessionLookupKey" style="width:250px" maxlength="100"></html:text><font color="#FF0000"> *</font><br>
						<label><font size="1px" color="RED"><bean:message bundle="gatewayResources" key="gateway.diameter.profile.sessionlookupkey.note" /></font></label>
					</td>
				  </tr>	
				  
		 </table>  
	</td> 
  </tr>	
 <tr><td width="10" class="">&nbsp;</td></tr>
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
		<td class="tblheader-bold" colspan="5" style="padding-left: 2em" height="20%">
			<bean:message bundle="gatewayResources" key="gateway.profile.diameter.diametertopcrfpacketmapping" />
			<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.diametertopcrfpacketmapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.diameter.diametertopcrfpacketmapping" />')"/>
		</td>
	</tr>

	<tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr> 
	<tr>
		<td><input type="button" value="Add Mapping" tabindex="31"  style="margin-left: 2.2em" class="light-btn" onclick="openDiameterToPCRFMapping()" ></td>
	</tr>
    <tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr>
    
    <tr> 
	  	<td class="btns-td" valign="middle" colspan="3">
	  	  
			<table cellpadding="0" id="mapReq" cellspacing="0" border="0" width="97%" class="">
				<thead>
				<tr>
					<td align="center" class="tblheader" valign="top" width="20%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
					<td align="center" class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
					<td align="center" class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.remove" /></td>
				</tr>
				</thead>
				<tbody>
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
	<tr>
		<td class="tblheader-bold" colspan="4" style="padding-left: 2em" height="20%">
			<bean:message bundle="gatewayResources" key="gateway.profile.diameter.pcrftodiameterpacketmapping" />
			<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.pcrftodiameterpacketmapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.diameter.pcrftodiameterpacketmapping" />')"/>
		</td></tr>

	<tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr> 
	<tr>
		<td><input type="button" value="Add Mapping" tabindex="32" id="pcrf_to_gateway_btn" style="margin-left: 2.2em" class="light-btn" onclick="openPCRFToDiameterMapping()" ></td>
	</tr>
    <tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr>
    
    <tr> 
	  	<td class="btns-td" valign="middle" colspan="3">  
			<table cellpadding="0" id="mapRes" cellspacing="0" border="0" width="97%" class="">
			<thead>
				<tr>
					<td align="center" class="tblheader" valign="top" width="20%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
					<td align="center" class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
					<td align="center" class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.remove" /></td>
				</tr>
			</thead>
			<tbody>
			</tbody>
			</table>
		</td> 
	</tr>	
	<tr>
		<td class="captiontext"  colspan="3">
			<label  class="small-text-grey"><bean:message  key="table.ordering.note"/> </label> 
		</td>
	</tr>	
	<!--  groovy start -->
	<tr>
		<td class="tblheader-bold" colspan="4" style="padding-left: 2em" height="20%">
			<bean:message bundle="gatewayResources" key="gateway.profile.groovyscript" />
			<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="profile.groovy.script"/>','<bean:message bundle="gatewayResources" key="gateway.profile.groovyscript" />')"/>
		</td>
	</tr>

	<tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr> 
	<tr>
		<td><input type="button" value="Add Groovy Script" id="groovy_script_btn" tabindex="33" style="margin-left: 2.2em" class="light-btn" onclick="addGroovyScript()" ></td>
	</tr>
    <tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr>

    <tr> 
	  	<td class="btns-td" valign="middle" colspan="3">  
			<table cellpadding="0" id="groovyScripts" cellspacing="0" border="0" width="97%" class="">
				<thead>
				<tr>				
					<td align="center" class="tblheader" valign="top" width="10%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.ordernumber" /></td>
					<td align="center" class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.name" /></td>
					<td align="center" class="tblheader" valign="top" width="50%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.argument" /></td>	
					<td align="center" class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.remove" /></td>
				</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</td> 
	</tr>	
	<tr>
		<td  class="captiontext"  colspan="3">	
				<label  class="small-text-grey"><bean:message  key="table.ordering.note"/></label> 
		</td>
	</tr>
	<!--  groovy end -->
	
      <tr> 
		<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
	  </tr> 		  			
			 
	    <tr>    
            <td class="btns-td" align="left" valign="middle" style="padding-left: 300px;" > 
		        <input type="button" onclick="validate();"  value=" Create " tabindex="34" class="light-btn" /> 
                <input type="button" align="left" value="  Cancel  " class="light-btn" tabindex="35" onclick="javascript:location.href='<%=basePath%>/initSearchProfile.do?/>'"/>
	        </td> 
   		</tr> 
		  
		</table> 
	  </td> 
	</tr> 
  	<%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 



<div id="popupDiameterToPCRFMapping" title="Add Packet Mapping" style="display: none;">

	<div class="labeltext">&nbsp;&nbsp;&nbsp;Condition &nbsp;&nbsp;&nbsp;
	<textarea rows="2" cols="50" id="dpcond"></textarea></div>

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
	<logic:iterate id="packetMap" name="createProfileForm" property="diameterToPCRFPacketMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData">
		<tr id="<bean:write property="packetMapId" name="packetMap" />">
			<td align="center" class="tblfirstcol" id="<bean:write property="packetMapId" name="packetMap" />" >
				<input type="radio" name="diameterToPCRFMapList" id="<bean:write property="packetMapId" name="packetMap" />" value="<bean:write property="name" name="packetMap" />" />
			</td>	
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



<div id="popupPCRFToDiameterMapping" title="Add Packet Mapping" style="display: none;">

	<div class="labeltext">&nbsp;&nbsp;&nbsp;Condition &nbsp;&nbsp;&nbsp;
		<textarea rows="2" cols="50"  id="pdcondition"></textarea>
		<br>&nbsp;&nbsp;&nbsp;<label  class="small-text-grey"><bean:message bundle="descriptionResources" key="expression.possibleoperators"/></label>
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
	<logic:iterate id="packetMap" name="createProfileForm" property="pcrfToDiameterPacketMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData">
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

</html:form> 
<table id="templateRuleMappingTable" style="display: none;">
	<tr>
		<td align="center" class="tblrows" style="border-left: 1px solid #CCC;">
			<select name="ruleMappingIds" style="width: 98%; height: 20px; font-size: 12px; font-family: Arial">
			 <logic:iterate id="ruleMapping" name="createProfileForm" property="ruleMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData">
				 	<option value="<bean:write property="ruleMappingId" name="ruleMapping" />">
				 		<bean:write name="ruleMapping" property="name" />
				 	</option>
				</logic:iterate> 
			</select>		
		</td>
		<td class="tblrows">
			<input class="accessNetworkType noborder" type="text"  name="ruleMappingCondition" maxlength="4000" style="width: 100%;"  >
			
		</td>
		<td class="tblrows" align="center" valign="middle">
			<img src="<%=basePath%>/images/minus.jpg" class="delete" height="15" onclick="removePDRow()">
		</td>
	</tr>
</table>

