<%@ page import="com.elitecore.corenetvertex.constants.ProtocolType"%>
<%@ page import="com.elitecore.corenetvertex.constants.ConversionType"%>
<%@ page import="com.elitecore.corenetvertex.constants.PacketType"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.web.gateway.attrmapping.form.PacketMappingForm"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<script>
var validateUrl = '<%=basePath%>/validate.do?';
var basePath = '<%=basePath%>';
</script>
<script language="javascript" 	src="<%=request.getContextPath()%>/js/validateMapping.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>
<style>
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px} 
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style>

<script type="text/javascript">
<%
	Map<String,String> commProtocolTypes= new HashMap<String,String>();
	commProtocolTypes.put("CCR","Credit-Control-Request");
	commProtocolTypes.put("CCA","Credit-Control-Response");
	commProtocolTypes.put("RAR","Re-Auth-Request");
	commProtocolTypes.put("RAA","Re-Auth-Response");
	commProtocolTypes.put("ASR","Abort-Session-Request");
	commProtocolTypes.put("ASA","Abort-Session-Response");	
	commProtocolTypes.put("AAR","Authenticate-Authorize-Request");
	commProtocolTypes.put("AAA","Authenticate-Authorize-Response");
	commProtocolTypes.put("STR","Session-Termination-Request");
	commProtocolTypes.put("STA","Session-Termination-Response");
	commProtocolTypes.put("ACR","Accounting-Request");
	commProtocolTypes.put("COA","Change-of-Authorization");
	commProtocolTypes.put("DCR","Disconnect-Request");
	commProtocolTypes.put("AR","Access-Request");
	commProtocolTypes.put("AA","Access-Accept");
%>
$(document).ready(function(){
	
	setTitle('<bean:message bundle="gatewayResources" key="mapping.header"/>');
	$("#name").focus();
	$("#description").attr('maxlength','255');
	document.getElementById("requestMapping").checked=true;
	var i = $('table').size() + 1;
	
	$('#addGatewayToPCRF').click(function() {
		var orderNumArray = document.getElementsByName("orderNumber");
		var currentOrderNumber=1;
		if(orderNumArray!=null && orderNumArray.length>0){
			for(var i=0; i<orderNumArray.length; i++){
				currentOrderNumber = orderNumArray[i].value;	
			}
			currentOrderNumber++;
		}
		var tableRowStr = '<tr id="'+currentOrderNumber+'">'+
								'<td class="allborder"><input tabindex="8" class="noborder" type="hidden" name="orderNumber" maxlength="4" size="4" value="'+currentOrderNumber+'" style="width: 50%"/>&nbsp;'+currentOrderNumber+'</td>'+
								'<td class="tblrows"><input tabindex="8" class="plcKey" type="text" name="policyKey" id="policyKey" maxlength="4000" size="28" style="width:100%" /></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="attribute" id="attribute" maxlength="200" size="28" style="width:100%" /></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="defaultValue" id="defaultValue" maxlength="1000" size="28" style="width:100%" /></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="valueMapping" id="valueMapping" maxlength="1000" size="30" style="width:100%" /></td>'+
								'<td class="tblrows" align="center" colspan="3"><img value="top" tabindex="8" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" onclick="removeRow('+currentOrderNumber+')"/></td>'+
							'</tr>';		
	   $(tableRowStr).appendTo('#gatewayToPCRFTable');
		$("input[name=policyKey]").each(function() {		
			if($(this).val().trim().length<1){						
				this.focus();			
			}
		});
		
	   retrivePolicyKeySuggestions();			   
	});
	
	$('#addPCRFToGateway').click(function() {
		var orderNumArray = document.getElementsByName("orderNumber");
		var currentOrderNumber=1;
		if(orderNumArray!=null && orderNumArray.length>0){
			for(var i=0; i<orderNumArray.length; i++){
				currentOrderNumber = orderNumArray[i].value;	
			}
			currentOrderNumber++;
		}
		var tableRowStr = '<tr id="'+currentOrderNumber+'" >'+
								'<td class="allborder"><input tabindex="8" class="plcKey" type="hidden" name="orderNumber" maxlength="4" size="4" value="'+currentOrderNumber+'" style="width: 100%"/>&nbsp;'+currentOrderNumber+'</td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="attribute" id="attribute" maxlength="200" size="28" style="width:100%" /></td>'+
								'<td class="tblrows"><input tabindex="8" class="plcKey" type="text" name="policyKey" id="policyKey" maxlength="4000" size="28" style="width:100%" /></td>'+									
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="defaultValue" id="defaultValue" maxlength="1000" size="28" style="width:100%" /></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="valueMapping" id="valueMapping" maxlength="1000" size="30" style="width:100%" /></td>'+
								'<td class="tblrows" align="center" colspan="3"><img value="top" tabindex="8" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" onclick="removeRow('+currentOrderNumber+')"/></td>'+
							'</tr>';			
   		$(tableRowStr).appendTo('#gatewayToPCRFTable');
		$("input[name=attribute]").each(function() {		
			if($(this).val().trim().length<1){						
				this.focus();			
			}
		});
   		retrivePolicyKeySuggestions();   		
	});
	
	
	$("input:radio[name='type'] ").change(function(){
		changeProtocol($('input:radio[name=commProtocol]:checked').val());
		var requestMappingType = document.getElementById("requestMapping").checked;
		var responseMappingType = document.getElementById("responseMapping").checked;
		if(requestMappingType){
			$('#addGatewayToPCRFButton').show();
			$('#addPCRFToGatewayButton').hide();
		}else if(responseMappingType){
			$('#addGatewayToPCRFButton').hide();
			$('#addPCRFToGatewayButton').show();
		}
		
		$("#gatewayToPCRFTable tr").each(function(index){		
			$(this).find("td").eq("2").after($(this).find("td").eq("1"));
		});
	});
	$('#addPCRFToGatewayButton').hide();
	changeProtocol($('input:radio[name=commProtocol]:checked').val());	
	dragAndDrop();
	 
});

function removeRow(id) {
	$("table#gatewayToPCRFTable tr#"+id).remove(); 
	$("table#gatewayToPCRFTable tr#noteTR"+id).remove();  
}

function dragAndDrop(){
	$('#gatewayToPCRFTable').tableDnD();	
}

function hideAndShowGatewayToPCRF(){
	var requestMappingType = document.getElementById("requestMapping").checked;
	var responseMappingType = document.getElementById("responseMapping").checked;
	if(requestMappingType){
		$('#pcrftogateway').hide();
		$('#gatewaytopcrf').show();
		$('#addGatewayToPCRFButton').show();
		$('#addPCRFToGatewayButton').hide();
	}else if(responseMappingType){
		$('#pcrftogateway').show();
		$('#gatewaytopcrf').hide();
		$('#gatewaytopcrf').show();
		$('#addGatewayToPCRFButton').hide();
		$('#addPCRFToGatewayButton').show();
	}
}

function changeProtocol(commProtocol){
	if(commProtocol == 'RADIUS'){
		var requestMappingType = document.getElementById("requestMapping").checked;
		var responseMappingType = document.getElementById("responseMapping").checked;
		if(requestMappingType){
			clearSelectTag();
			<%
				List<PacketType> radiusRequestValues = PacketType.getPacketTypeList(ProtocolType.RADIUS, ConversionType.GATEWAY_TO_PCRF);
				for(PacketType packetType : radiusRequestValues){
			%>
				$("#packetType").append("<option value='<%=packetType.getPacketType()%>'><%=commProtocolTypes.get(packetType.getPacketType())%></option>");				
			<%
				}
			%>							
		}else if(responseMappingType){
			clearSelectTag();
			<%
				List<PacketType> radiusResponseValues = PacketType.getPacketTypeList(ProtocolType.RADIUS, ConversionType.PCRF_TO_GATEWAY);
				for(PacketType packetType : radiusResponseValues){
			%>
				$("#packetType").append("<option value='<%=packetType.getPacketType()%>'><%=commProtocolTypes.get(packetType.getPacketType())%></option>");
			<%
				}
			%>
		}
	}else if(commProtocol == 'DIAMETER'){
		var requestMappingType = document.getElementById("requestMapping").checked;
		var responseMappingType = document.getElementById("responseMapping").checked;
		if(requestMappingType){
			clearSelectTag();
			<%
				List<PacketType> diameterRequestValues = PacketType.getPacketTypeList(ProtocolType.DIAMETER, ConversionType.GATEWAY_TO_PCRF);
				for(PacketType packetType : diameterRequestValues){
			%>
				$("#packetType").append("<option value='<%=packetType.getPacketType()%>'><%=commProtocolTypes.get(packetType.getPacketType())%></option>");				
			<%
				}
			%>							
		}else if(responseMappingType){
			clearSelectTag();
			<%
				List<PacketType> diameterResponseValues = PacketType.getPacketTypeList(ProtocolType.DIAMETER, ConversionType.PCRF_TO_GATEWAY);
				for(PacketType packetType : diameterResponseValues){
			%>
				$("#packetType").append("<option value='<%=packetType.getPacketType()%>'><%=commProtocolTypes.get(packetType.getPacketType())%></option>");
			<%
				}
			%>
		}
	}
}

function clearSelectTag() {
	document.forms[0].packetType.options.length=0;
}

function retrivePolicyKeySuggestions() {
	var mappingTypeArray = new Array();
	if(document.forms[0].requestMapping.checked){
		mappingTypeArray[0] = "<%=PCRFKeyType.REQUEST.getVal()%>";		
	}else{
		mappingTypeArray[0] = "<%=PCRFKeyType.RESPONSE.getVal()%>";		
	}
	
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-2);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoCompleteUsingCssClass("td input.plcKey",dbFieldArray);		
		return dbFieldArray;
	});
}

var isValidName;
function validate(){
	verifyName();
	var conversationType = null;
	if(document.getElementById("requestMapping").checked){
		conversationType = document.getElementById("requestMapping").value;
	}else{
		conversationType = document.getElementById("responseMapping").value
	}
	$("tr[name=noteTR]").each(function(){
		$(this).remove();
	});
	var mappingArray = document.getElementsByName("orderNumber");
	var mappingLength = mappingArray.length;
	for(var i = 0; i < mappingLength; i++){
		validateMapping(mappingArray[i].value,conversationType);	
	}
	var flag = false;
	if(isNull(document.forms[0].name.value)){
		alert('Packet Mapping Name must be specified.');
		document.forms[0].name.focus();
	}else if(!isValidName) {
		alert('Enter Valid Packet Mapping Name.');
		document.forms[0].name.focus();		
	}else if(!(document.forms[0].radiusCommProtocol.checked) && !(document.forms[0].diameterCommProtocol.checked)){
		alert('At least one Communication Protocol must be selected.');
	}else if(document.forms[0].packetType.value == "0"){
		alert('At least one Packet Type must be selected.');
	}else if(!(document.forms[0].requestMapping.checked) && !(document.forms[0].responseMapping.checked)){
		alert('At least one Mapping Type must be selected.');
	}else if(document.getElementById('gatewayToPCRFTable').getElementsByTagName('tr').length <= 1){
		alert('Add at least one mapping.');
	}else if(!validateAttribute()){				
	}else if(!validatePolicyKeys()){		
	}else{
		var value = checkForNote();
		var confirmSubmit = true;
		if(value != null){
			if(value == "No Live Server Found" || value == "Timeout Occur"){
				confirmSubmit = confirm(message.serverMessage);
			}else{
				confirmSubmit = confirm(message.mappingMessage);
			}
			if(confirmSubmit == false){
				return flag;
			}
		}
		flag = true;
	} 
	return flag;
}

function value(len) {
	var val = document.getElementById("plcKey").value;
	if(val=="DeviceType") {
		$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.DEVICE_TYPE%>,propertyName:"deviceName"}, function(data){
			dbFieldStr = data.substring(1,data.length-3);
			var dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split(", ");
			while(len != 0) {
				$("#test"+len).autocomplete(dbFieldArray, {
					minChars: 0,
					max: 100
				});
				len = len - 1;
			}
			return dbFieldArray;
		});
	}
	if(val=="AccessNetwork") {
		$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.ACCESS_NETWORK_TYPE%>,propertyName:"accessNetworkName"}, function(data){
			dbFieldStr = data.substring(1,data.length-3);
			var dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split(", ");
			while(len != 0) {
				$("#test"+len).autocomplete(dbFieldArray, {
					minChars: 0,
					max: 100
				});
				len = len - 1;
			}
			return dbFieldArray;
		});
	}
	if(val=="SericeType") {
		$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.SERVICE_TYPE%>,propertyName:"serviceName"}, function(data){
			dbFieldStr = data.substring(1,data.length-3);
			var dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split(", ");
			while(len != 0) {
				$("#test"+len).autocomplete(dbFieldArray, {
					minChars: 0,
					max: 100
				});
				len = len - 1;
			}
			return dbFieldArray;
		});
	}
}

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.PACKET_MAPPING%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.PACKET_MAPPING%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

function validatePolicyKeys(){
	var flag=true;
	$("input[name=policyKey]").each(function() {
		if($(this).val().trim().length<1){
			this.focus();
			alert("PolicyKey should not be empty.");
			flag= false;
			return flag;
		}
	});
	return flag;
}
function validateAttribute(){
	var flag=true;
	$("input[name=attribute]").each(function(){
		if($(this).val().trim().length<1){
			this.focus();
			alert("Attribute should not be empty.");			 
			flag= false;
			return flag;
		}
	});
	return flag;
}
</script>

<html:form action="/createMapping" onsubmit="return validate();" >	
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			<bean:message bundle="gatewayResources" key="mapping.create"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3">
			
			   <table width="97%" id="c_tblCrossProductList" align="right" border="0" >
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="gatewayResources" key="mapping.name"/></td>
					<sm:nvNameField maxLength="60" size="30"/> 
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="gatewayResources" key="mapping.description"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea property="description" cols="40" rows="3" tabindex="2" />
					</td> 
				  </tr>    
				  <tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr>
				  <tr>
					 <td align="left" class="labeltext" valign="top" width="20%">
					 	<bean:message bundle="gatewayResources" key="mapping.commprotocol"/></td>
					 <td align="left" class="labeltext" valign="top" width="53%">
					 	<html:radio styleId="radiusCommProtocol"  onchange="changeProtocol('RADIUS');" property="commProtocol" value="RADIUS" tabindex="3" />RADIUS&nbsp;&nbsp;&nbsp;&nbsp;
						<html:radio styleId="diameterCommProtocol" onchange="changeProtocol('DIAMETER');" property="commProtocol" value="DIAMETER" tabindex="3" />Diameter
					 </td>
				  </tr>
				  <tr>
					 <td align="left" class="labeltext" valign="top" width="20%">
					 	<bean:message bundle="gatewayResources" key="mapping.conversationtype"/></td>
					 <td align="left" class="labeltext" valign="top" width="53%" id="gtype">
						<html:radio styleId="requestMapping" property="type"  value="GATEWAY TO PCRF" tabindex="4"  />GATEWAY TO PCRF
						<html:radio styleId="responseMapping" property="type"  value="PCRF TO GATEWAY" tabindex="4" />PCRF TO GATEWAY
					 </td>
				  </tr>	
				  
				  <tr><td width="10" class="small-gap">&nbsp;</td></tr>
				  <tr>
				  	<td align="left" class="labeltext" valign="top" width="10%">
				  		<bean:message bundle="gatewayResources" key="mapping.packettype"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="gatewayResources" key="mapping.packettype"/>','<bean:message bundle="gatewayResources" key="mapping.packettype"/>')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top" width="20%" >
						<html:select name="packetMappingForm" styleId="packetType" property="packetType" onchange="retrivePolicyKeySuggestions();" tabindex="5">	
						</html:select><font color="#FF0000"> *</font>
					</td>
				  </tr>
				  <tr><td width="10" class="small-gap">&nbsp;</td></tr>				  
			   </table>  
			</td> 
		  </tr>
		  <tr><td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td></tr>
		  <tr> 
			<td class=tblheader-bold colspan="5" style="padding-left: 2em">Mapping</td>
		  </tr>
		  		  	 
		  <tr><td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td></tr> 
		  
		  <tr id="addGatewayToPCRFButton"> 
          	<td class="" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 2.3em; font-weight: bold" >
            	<input type="button" id="addGatewayToPCRF" value="Add Mapping" tabindex="7" class="light-btn" onclick=""></td>
          </tr>
          
          <tr id="addPCRFToGatewayButton"> 
          	<td class="" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 2.3em; font-weight: bold" >
            	<input type="button" id="addPCRFToGateway" value="Add Mapping" tabindex="7" class="light-btn" onclick=""></td>
          </tr>
          
          <tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" colspan="2">&nbsp;</td>
		  </tr>	
				 
		  <tr id='gatewaytopcrf'> 
		  	<td class="btns-td" valign="middle" colspan="3">  
				<table cellpadding="0" id="gatewayToPCRFTable" cellspacing="0" border="0" width="97%" class="">
					<thead>
					<tr class='L'>
						<td align="center" style="cursor:default" class="tblheader" valign="top" width="7%">Order#</td>
						<td align="center" class="tblheader" valign="top" width="20%">Policy Key</td>
						<td align="center" class="tblheader" valign="top" width="20%">Attribute</td>
						<td align="center" class="tblheader" valign="top" width="20%">Default Value</td>
						<td align="center" class="tblheader" valign="top" width="30%">Value Mapping</td>
						<td align="center" class="tblheader" valign="top" width="5%">&nbsp;Remove&nbsp;&nbsp;</td>
					</tr>
					</thead>
				</table>
			</td> 
		  </tr>
		  				 
		  <tr><td width="10" class="">&nbsp;</td></tr>
		  
          <tr align="center" > 
	        <td>&nbsp;</td> 
            <td class="btns-td" valign="middle"  > 
            	<html:submit styleClass="light-btn" tabindex="9"  value="  Create  "  />		        
                <input type="button" tabindex="10" align="center" value="  Cancel  " class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchMapping.do?/>'"/></td>
	        </td> 
   		  </tr> 
		</table> 
	  </td> 
	</tr> 
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
</html:form> 

