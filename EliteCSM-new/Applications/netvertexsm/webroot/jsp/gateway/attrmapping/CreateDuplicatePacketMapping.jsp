<%@page import="com.elitecore.corenetvertex.constants.ProtocolType"%>
<%@page import="com.elitecore.corenetvertex.constants.ConversionType"%>
<%@page import="com.elitecore.corenetvertex.constants.PacketType"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.AttributeMappingData"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.web.gateway.attrmapping.form.PacketMappingForm"%>
<%@ page import="java.util.List" %>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData"%>
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

<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%><style>
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px} 
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style>

<%
	PacketMappingForm packetMappingForm = (PacketMappingForm)request.getAttribute("packetMappingForm");
	PacketMappingData packetMapData = (PacketMappingData)request.getAttribute("packetMappingData");
 
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
	commProtocolTypes.put("COA","Change-Of-Authorization");
	commProtocolTypes.put("DCR","Disconnect-Request");
	commProtocolTypes.put("AR","Access-Request");
	commProtocolTypes.put("AA","Access-Accept");
%>
<script type="text/javascript">

$(document).ready(function(){
	setTitle('<bean:message bundle="gatewayResources" key="mapping.header"/>');
	dragAndDrop();
	$("#name").focus();	
	$('table td img.delete').on('click',function() {
		$(this).parent().parent().remove(); 
	});
	
	<%if(packetMapData.getType() != null && packetMapData.getType().equalsIgnoreCase("PCRF TO GATEWAY")){%>
		$('#addGatewayToPCRF').attr("id", "addPCRFToGateway");
		$("#gatewayToPCRFTable tr").each(function(index){			
			$(this).find("td").eq("2").after($(this).find("td").eq("1"));
		});
	<%}%>
	
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
								'<td class="allborder"><input tabindex="8" class="plcKey" type="hidden" name="orderNumber" maxlength="4" size="4" value="'+currentOrderNumber+'" />&nbsp;'+currentOrderNumber+'</td>'+
								'<td class="tblrows"><input tabindex="8" class="plcKey" type="text" name="policyKey" id="policyKey" maxlength="4000" size="28" style="width:100%"/></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="attribute" id="attribute" maxlength="200" size="28" style="width:100%"/></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="defaultValue" id="defaultValue" maxlength="1000" size="28" style="width:100%"/></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="valueMapping" id="valueMapping" maxlength="1000" size="30" style="width:100%"/></td>'+
								'<td class="tblrows" align="center" ><img tabindex="8" value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" onclick="removeRow('+currentOrderNumber+');"/></td>'+
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
		var tableRowStr = '<tr id="'+currentOrderNumber+'">'+
								'<td class="allborder"><input tabindex="8" class="plcKey" type="hidden" name="orderNumber" maxlength="4" size="4" value="'+currentOrderNumber+'" style="width: 50%"/>'+currentOrderNumber+'</td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="attribute" id="attribute" maxlength="200" size="28" style="width:100%"/></td>'+
								'<td class="tblrows"><input tabindex="8" class="plcKey" type="text" name="policyKey" id="policyKey" maxlength="4000" size="28" style="width:100%"/></td>'+									
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="defaultValue" id="defaultValue" maxlength="1000" size="28" style="width:100%"/></td>'+
								'<td class="tblrows"><input tabindex="8" class="noborder" type="text" name="valueMapping" id="valueMapping" maxlength="1000" size="30" style="width:100%"/></td>'+
								'<td class="tblrows" align="center" ><img value="top" tabindex="8" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" onclick="removeRow('+currentOrderNumber+');"/></td>'+
							'</tr>';			
   		$(tableRowStr).appendTo('#gatewayToPCRFTable');
		$("input[name=attribute]").each(function() {		
			if($(this).val().trim().length<1){						
				this.focus();			
			}
		});
   		retrivePolicyKeySuggestions();
	});
	retrivePolicyKeySuggestions();	
});

function removeRow(id) {
	$("table#gatewayToPCRFTable tr#"+id).remove(); 
	$("table#gatewayToPCRFTable tr#noteTR"+id).remove();  
}
function dragAndDrop(){	
	$('#gatewayToPCRFTable').tableDnD();	
}

function checkMappingOrder(){
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
function retrivePolicyKeySuggestions() {
	var mappingTypeArray = new Array();
	<%if(packetMapData.getType().equalsIgnoreCase("PCRF TO GATEWAY")){%>
		mappingTypeArray[0] = "<%=PCRFKeyType.RESPONSE.getVal()%>";			
	<%}else{%>
		mappingTypeArray[0] = "<%=PCRFKeyType.REQUEST.getVal()%>";		
	<%}%>
	
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoCompleteUsingCssClass("td input.plcKey",dbFieldArray);
		return dbFieldArray;
	});
}

var isValidName;
function validate(){	
	var conversationType ='<%=packetMapData.getType()%>';
	$("tr[name=noteTR]").each(function(){
		$(this).remove();
	});
	var mappingArray = document.getElementsByName("orderNumber");
	var mappingLength = mappingArray.length;
	for(var i = 0; i < mappingLength; i++){
		validateMapping(mappingArray[i].value,conversationType);	
	}
	
	if(isNull(document.forms[0].name.value)){
		alert('Packet Mapping Name must be specified.');
		document.forms[0].name.focus();
		return;
	}else if(!isValidName) {
		alert('Enter Valid Packet Mapping Name.');
		document.forms[0].name.focus();
		return;
	}else if(document.getElementById('gatewayToPCRFTable').getElementsByTagName('tr').length <= 2){
		alert('Add atleast one mapping.');
	}else if(!validateAttribute()){		
		return;		
	}else if(!validatePolicyKeys()){
		return;
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
				return;
			}
		}
		if(checkMappingOrder()){
			var flag=confirm("Packet Mapping Order is changed. Would you like to continue ?");
			if(flag == false){
				return;
			}
		}
		document.forms[0].submit();
				
	} 
}

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.PACKET_MAPPING%>',searchName:searchName,mode:'update',id:'<%=packetMappingForm.getPacketMapId()%>'},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.PACKET_MAPPING%>',searchName:searchName,mode:'update',id:'<%=packetMappingForm.getPacketMapId()%>'},'verifyNameDiv');
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

<table cellpadding="0" cellspacing="0" border="0" width="100%" >
 <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  
  <tr>
  <td width="10">&nbsp;</td> 
    <td width="100%" valign="top" class="box">
<html:form action="/createDuplicatePacketMapping">	
<html:hidden property="action" value="duplicate"/>
<html:hidden name="packetMappingForm" property="type" />
<html:hidden name="packetMappingForm" property="packetType" />
<html:hidden name="packetMappingForm" property="commProtocol" />
<bean:define id="packetMappingDataBean" name="packetMappingData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData" />
    <div style="margin-left: 2.0em;" class="tblheader-bold"><bean:message bundle="gatewayResources" key="mapping.create" /></div> 
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
	<tr>
		<td align="right" class="labeltext" valign="top" class="box" width="80%"> 

  			<table width="100%" align="right" border="0">
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="20%">
					<bean:message bundle="gatewayResources" key="mapping.name"/></td> 
					<sm:nvNameField maxLength="60" size="30" value="${packetMappingDataBean.name }"/>
					<html:hidden styleId="packetMapId" property="packetMapId" />
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="gatewayResources" key="mapping.description"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea property="description" cols="40" rows="3" tabindex="2" />
					</td> 
				  </tr> 
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="mapping.commprotocol" />
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
							<bean:write name="packetMappingDataBean" property="commProtocol"/>
					</td> 
				  </tr> 

				  <tr> 
					<td align="left" class="labeltext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="mapping.conversationtype" />
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%"> 
						<bean:write name="packetMappingDataBean" property="type"/>&nbsp;
					</td> 
				  </tr>
				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="20%">
						<bean:message  bundle="gatewayResources" key="mapping.packettype" />
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<%=commProtocolTypes.get(packetMappingDataBean.getPacketType())%>&nbsp;
					</td> 
				  </tr>				  				   
				  <tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr>
			   </table>  
			</td> 
		  </tr>
		  <tr><td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td></tr>
		  <tr> 
			<td style="margin-left: 2.0em;" class="tblheader-bold" colspan="8">Mapping</td>
		  </tr>		  		  	 		                                          
          <tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" colspan="2">&nbsp;</td>
		  </tr>	
		  <tr id='gatewaytopcrf'> 
		  	<td valign="middle" colspan="3">  
				<table cellpadding="0" id="gatewayToPCRFTable" cellspacing="0" border="0" width="97%">
					<thead>
					<tr id='addGatewayToPCRFButton'> 
          				<td>
            				<input type="button" id="addGatewayToPCRF" value="Add Mapping" tabindex="7" class="light-btn" onclick="">
            			</td>
          			</tr>					
					<tr>
						<td align="center" style="cursor:default" class="tblheader" valign="top" width="5%">Order#</td>
						<td align="center" style="cursor:default" class="tblheader" valign="top" width="20%">Policy Key</td>
						<td align="center" style="cursor:default" class="tblheader" valign="top" width="10%">Attribute</td>						
						<td align="center" style="cursor:default" class="tblheader" valign="top" width="20%">Default Value</td>
						<td align="center" style="cursor:default" class="tblheader" valign="top" width="20%">Value Mapping</td>
						<td align="center" style="cursor:default" class="tblheader" valign="top" width="5%">Remove</td>
					</tr>
					</thead>
			<% int counter=0 , currentOrderNumber=1;%>		
			<logic:iterate id="mappingData" scope="request" name="packetMappingForm"  property="attributeMappings" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.AttributeMappingData">
			
				<%counter++;%>
				<tr id="<%=currentOrderNumber%>">
				<%if(mappingData.getOrderNumber() != null){%>				
            		<td class="allborder"><input  tabindex="8"  class="noborder" type="hidden" value="<%=(mappingData.getOrderNumber())%>" name="orderNumber" maxlength="4" size="4" style="width:100%"/><%=(mappingData.getOrderNumber())%></td>
            	<%}else{%>
            		<td class="allborder"><input tabindex="8" class="noborder" type="hidden" value="<%=counter%>" name="orderNumber" maxlength="4" size="4" style="width:100%"/><%=counter%></td>	
            	<%}if(mappingData.getPolicyKey() != null){%>
        			<td class="tblrows" ><input tabindex="8" class="plcKey" type="text" id="policyKey" value="<%=StringEscapeUtils.escapeHtml(mappingData.getPolicyKey())%>" name="policyKey" maxlength="1000" size="24" style="width:100%"/></td>
        		<%}else{%>
        			<td class="tblrows" ><input tabindex="8" class="plcKey" type="text" id="policyKey" name="policyKey" maxlength="1000" size="24" style="width:100%" value=""/></td>
            	<%}if(mappingData.getAttribute() != null){%>				
            		<td class="tblrows"><input  tabindex="8"  class="noborder" type="text" id="attribute" value="<%=StringEscapeUtils.escapeHtml(mappingData.getAttribute())%>" name="attribute" maxlength="200" size="24" style="width:100%"/></td>
            	<%}else{%>
            		<td class="tblrows"><input tabindex="8" class="noborder" type="text" id="attribute" name="attribute" maxlength="200" size="24" style="width:100%" value=""/></td>	
            	<%}if(mappingData.getDefaultValue()!=null) { %>            		
            		<td class="tblrows" ><input tabindex="8" class="noborder" type="text" id="defaultValue" value="<%=StringEscapeUtils.escapeHtml(mappingData.getDefaultValue())%>" name="defaultValue" maxlength="500" size="24" style="width:100%"/></td>
                <%}else{%>            		
					<td class="tblrows" ><input tabindex="8" class="noborder" type="text" id="defaultValue" name="defaultValue" maxlength="500" size="28" style="width:100%" value=""/></td>
                <%}if(mappingData.getValueMapping()!=null) { %>
            		<td class="tblrows" ><input tabindex="8" class="noborder" type="text" id="valueMapping" name="valueMapping" value="<%=StringEscapeUtils.escapeHtml(mappingData.getValueMapping())%>" name="valueMapping" maxlength="1000" size="28" style="width:100%"/></td>
                <%}else{%>            		
					<td class="tblrows" ><input tabindex="8" class="noborder" type="text" id="valueMapping" name="valueMapping" maxlength="1000" size="30" value=""/></td>
                <%}%>					
            		<td class='tblrows' align="center" style="cursor:default" ><img tabindex="8" src="<%=basePath%>/images/minus.jpg" class="delete" height="15" onclick="removeRow(<%=currentOrderNumber%>);" /></td>
      			</tr>	
      			
				<%currentOrderNumber ++; %>	
			</logic:iterate>
					
				</table>
			</td> 
		  </tr>
		  		  		 
		  <tr><td width="10" class="">&nbsp;</td></tr>		            
   		  
   		  <tr align="center">
				<td style="padding-left: 1.6em">
					<input type="button" property="c_btnCreate" onclick="validate();" value="  Create  " class="light-btn" tabindex="9"/> 
	              		<input type="button" tabindex="10" align="left" value="  Cancel  " class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchMapping.do?/>'"/></td>
				</td>
				<td>&nbsp;</td>
		  </tr>   		   
		</table> 
	  </td> 
	</tr>
	</table>	 
	</html:form> 
	</td>
	</tr>
   <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table> 

