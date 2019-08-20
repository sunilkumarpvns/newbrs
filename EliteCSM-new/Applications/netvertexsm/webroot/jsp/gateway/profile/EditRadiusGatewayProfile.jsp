<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData"%>
<%@ taglib uri="/WEB-INF/config/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/config/tlds/struts-nested.tld" prefix="nested" %>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.corenetvertex.constants.*" %>

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

<script type="text/javascript" >
var jRadiusToPCRFRefArray = new Array();
var jRPCount = 0;
var jPCRFToRadiusRefArray = new Array();
var jPRCount = 0;

function openRadiusToPCRFMapping() {
	$.fx.speeds._default = 1000;
	document.getElementById("popupRadiusToPCRFMapping").style.visibility = "visible";		
	$( "#popupRadiusToPCRFMapping" ).dialog({
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
				var selectedItems = document.getElementsByName("radiusToPCRFMapList");
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
                            	"<td class='tblrows' style='cursor: default' align='center' valign='middle'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='14' /></td></tr>");
                                selectedItems[i].checked=false;
						        $("#"+labelid).attr("style","display:none");
                                jRadiusToPCRFRefArray[jRPCount++] = labelid;
                        }
    					document.getElementById("dpcond").value = "";

    					//delete call
	         			$('#mapReq td img').on('click',function() {
	       				var removalVal = $(this).closest('tr').find('td').eq(0).children().eq(0).val();
	       				for(var d=0;d<jRPCount;d++){
	       					var currentVal = jRadiusToPCRFRefArray[d];	
	       					if(currentVal == removalVal)
		       				{
	       						$("#"+removalVal).removeAttr("style");
	       						jRadiusToPCRFRefArray[d] = '  ';
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
            	$("#pcrf_to_radius_btn").focus();
        	}
        },
    	open: function() {
        	
    	},
    	close: function() {
    		$("#pcrf_to_radius_btn").focus();
    	}				
	});
	$( "#popupRadiusToPCRFMapping" ).dialog("open");
}

function openPCRFToRadiusMapping() {
	$.fx.speeds._default = 1000;
	document.getElementById("popupPCRFToRadiusMapping").style.visibility = "visible";		
	$( "#popupPCRFToRadiusMapping" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 450,
		width: 570,		
		position:["top",100],
		buttons:{					
            'Add': function() {	
				var name = $('#packetMapId').text(); 
				var nameId = $('#packetMapId').val();
				var cond = $('#pdcond').val();
				var selectedItems = document.getElementsByName("pcrfToRadiusMapList");
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
                            "<td class='tblrows' style='cursor: default'  align='center' valign='middle'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td>");
                            selectedItems[i].checked=false;

                            $("#"+labelVal).attr("style","display:none");
                            jPCRFToRadiusRefArray[jPRCount++] = labelid;

                        }
    					document.getElementById("pdcond").value = "";
    					
	         			$('#mapRes td img').on('click',function() {

         				var removalVal = $(this).closest('tr').find('td').eq(0).children().eq(0).val(); 	
	       				for(var d=0;d<jPRCount;d++){
	       					var currentVal = jPCRFToRadiusRefArray[d];	
	       					if(currentVal == removalVal)
		       				{
	       						$("#"+removalVal).removeAttr("style");
	       						jPCRFToRadiusRefArray[d] = '  ';
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
	$( "#popupPCRFToRadiusMapping" ).dialog("open");
	pdcond();
	deleteRows();
}

function  pdcond(){
		$("#pdcond").autocomplete();
		var dbFieldArray = [];
		<%
			List<PCRFKeyConstants> pcrfKeyList =  PCRFKeyConstants.values(PCRFKeyType.RULE);
		   for(PCRFKeyConstants constant:pcrfKeyList){
		%>
				dbFieldArray.push('<%=constant.getVal()%>');					
		<%}%>
		var autocompleter1 = createModel(dbFieldArray);
		expresssionAutoComplete('pdcond',autocompleter1);
	};

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
		
			}

	
function addPCCRuleMappingRow() {
	$("#ruleMappingTable tr:last").after("<tr>" + $("#templateRuleMappingTable").find("tr").html() + "</tr>");
	accessNetworkSuggestion();
	$('#ruleMappingTable').tableDnD();
	$('#ruleMappingTable td img.delete').on('click',function() {
		 $(this).parent().parent().remove();
	});
}


$(document).ready(function(){
	$("#timeout").focus();
	$("#supportedVendorList").attr('maxlength','254');
	//  Value in Array
	setMappingInArray();
	//delete call
		$('#mapReq td img').on('click',function() {
		var removalVal = $(this).closest('tr').find('td').eq(0).children().eq(0).val();
		for(var d=0;d<jRPCount;d++){
			var currentVal = jRadiusToPCRFRefArray[d];
			if($.trim(currentVal) == $.trim(removalVal))
			{
				$("#"+currentVal).removeAttr("style");
				jRadiusToPCRFRefArray[d] = '  ';
				break;
			}
		}
			$(this).closest('tr').next("tr[name='noteTR']").remove();
			$(this).parent().parent().remove();
		});	

	$('table td img.delete').on('click',function() {			
		$(this).parent().parent().remove(); 
	});
	
	$('#mapRes td img').on('click',function() {
		var removalVal = $(this).closest('tr').find('td').eq(0).children().eq(0).val();
		for(var d=0;d<jPRCount;d++){
			var currentVal = jPCRFToRadiusRefArray[d];
			if($.trim(currentVal) == $.trim(removalVal))
			{
				$("#"+currentVal).removeAttr("style");
				jPCRFToRadiusRefArray[d] = '  ';
				break;
			}
		}
			$(this).closest('tr').next("tr[name='noteTR']").remove();
			$(this).parent().parent().remove();
		});	 

	$('table td img.delete').on('click',function() {			
		$(this).parent().parent().remove(); 
	});
	
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName"}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoCompleteUsingCssClass("td input.plcKey",dbFieldArray);
		return dbFieldArray;
	});
	
	supportedVendorList();
	dragAndDrop();
	accessNetworkSuggestion();
});
function dragAndDrop(){
	$('#mapReq').tableDnD();
	$('#mapRes').tableDnD();
	$('#groovyScripts').tableDnD();
	$('#ruleMappingTable').tableDnD();	
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
function validate(){
	callValidateMappingCondition();
	var interimIntervalValue = parseInt(document.forms[0].interimInterval.value);
	if(isNull(document.forms[0].timeout.value)){
		alert('Timeout must be specefied.');
		document.forms[0].timeout.focus();
	}else if(isEcNaN(document.forms[0].timeout.value)){
		alert('Timeout must be numeric.');
		document.forms[0].timeout.focus();
	}else if(isNull(document.forms[0].maxRequestTimeout.value)){
		alert('Max Request Timeout Count must be specified.');
		document.forms[0].maxRequestTimeout.focus();
	}else if(isEcNaN(document.forms[0].maxRequestTimeout.value)){
		alert('Max Request Timeout Count must be numeric.');
		document.forms[0].maxRequestTimeout.focus();
	}else if(isNull(document.forms[0].retryCount.value)){
		alert('Retry Count must be specefied.');
		document.forms[0].retryCount.focus();
	}else if(isEcNaN(document.forms[0].retryCount.value)){
		alert('Retry Count must be positive numeric.');
		document.forms[0].retryCount.focus();
	}else if(isNull(document.forms[0].statusCheckDuration.value)){
		alert('Status Check Duration must be specefied.');
		document.forms[0].statusCheckDuration.focus();
	}else if(isEcNaN(document.forms[0].statusCheckDuration.value)){
		alert('Status Check Duration must be numeric.');
		document.forms[0].statusCheckDuration.focus();
	}else if((document.forms[0].statusCheckDuration.value * 1000) <= document.forms[0].timeout.value){
		alert('Status Check Duration must be greater than Timeout.');
		document.forms[0].statusCheckDuration.focus();
	}else if(isNaturalNumber(interimIntervalValue) == false){
		alert("Interim Interval must be specified and positive number");
		document.forms[0].interimInterval.focus();
	}else if(interimIntervalValue > <%=CommonConstants.MAX_INTERIM_INTERVAL%>) {
		alert('Interim Interval can not be greater than 2880');
		document.forms[0].interimInterval.focus();
	}else if(!validateScriptName()){
		$("#scriptName").focus();
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
		document.forms[0].submit();
	}
}
function validateScriptName(){	
	var flag=true;	
	$("input[name=scriptName]").each(function() {
		
		if($(this).val().trim().length<1 && flag==true){			
			alert("Invalid Script Name.");
			this.focus();
			this.focus();
			flag= false;			
		}
	});	
	return flag;
}

function setMappingInArray(){
	var row = 0;
	$("#mapReq tr").each(function(){
		if(row != 0){
			var radToPCRF = $(this).find("td").eq(0).children().eq(0).val();
			radToPCRF = $.trim(radToPCRF);
			jRadiusToPCRFRefArray[jRPCount++] = radToPCRF;
	 	 	$("#"+radToPCRF).attr("style","display:none");
		}
		row++;
	});
	row = 0;
	$("#mapRes tr").each(function(){
		if(row != 0){
			var PCRFToRadius = $(this).find("td").eq(0).children().eq(0).val();
			PCRFToRadius = $.trim(PCRFToRadius);
			jPCRFToRadiusRefArray[jPRCount++] = PCRFToRadius;
	 	 	$("#"+PCRFToRadius).attr("style","display:none");
		}
		row++;
	});
	
}

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.PROFILE%>',searchName:searchName,mode:'update',id:'<%=gatewayProfileData.getProfileId()%>'},'verifyNameDiv');
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.PROFILE%>',searchName:searchName,mode:'update',id:'<%=gatewayProfileData.getProfileId()%>'},'verifyNameDiv');
}

function addGroovyScript(){
	
	var orderNumArray = document.getElementsByName("orderNumber");
	var currentOrderNumber=1;
	if(orderNumArray!=null && orderNumArray.length>0){
		currentOrderNumber = orderNumArray.length+1;
	}
	$('<tr><td class="tblrows" style="cursor:move"  ><input tabindex="12" class="noborder" type="hidden" name="orderNumber" maxlength="4" value="'+currentOrderNumber+'" style="width: 100%"/>&nbsp;'+currentOrderNumber+'</td>'+
			  '<td class="tblrows"><input tabindex="15" class="noborder" type="text" name="scriptName" maxlength="2048" style="width: 100%"/></td>'+
			  '<td class="tblrows"><input tabindex="15" class="noborder" type="text" name="argument" maxlength="2048" style="width: 100%"/></td>'+			  
			  '<td class="tblrows" align="center"  valign="middle"> <img value="top" tabindex="15" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 1px;" height="14" /></td></tr>').appendTo('#groovyScripts');
	
	$("input[name=scriptName]").each(function() {		
		if($(this).val().trim().length<1){						
			this.focus();			
		}
	});		
	$('#groovyScripts').tableDnD();
	deleteRows();		
}

function clearDiv(){
	$("#errorMsgDiv_supportedVendorList").html("");
}

</script> 	

<div style="margin-left: 2.05em;" class="tblheader-bold"><bean:message bundle="gatewayResources" key="gateway.radius" /></div>
<html:form action="/editRadiusGatewayProfile">
<html:hidden property="action" value="update"/>
<html:hidden property="gatewayProfileName"/>
<html:hidden property="profileId"/>
 <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
			<table width="97%" id="c_tblCrossProductList" align="right" border="0" > 
				<tr> 
						<td class="small-gap"  colspan="3" >&nbsp;</td>
				   </tr>
				<tr> 
					<td align="left" class="labeltext" valign="top" width="20%" >
						<bean:message bundle="gatewayResources" key="gateway.radius.timeout" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.timeout"/>','<bean:message bundle="gatewayResources" key="gateway.radius.timeout" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text  property="timeout" styleId="timeout" maxlength="5" size="30"  styleClass="name" tabindex="1"/><font color="#FF0000"> *</font>
					</td>
				  </tr>
				  <tr>
				  	<td align="left" class="labeltext" valign="top" width="20%">
				  		<bean:message bundle="gatewayResources" key="gateway.radius.maxreqtimeout"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.maxreqtimeout"/>','<bean:message bundle="gatewayResources" key="gateway.radius.maxreqtimeout" />')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top" width="32%"> 
						<html:text  property="maxRequestTimeout" styleId="maxRequestTimeout" maxlength="5" size="30"  styleClass="name" tabindex="2"/><font color="#FF0000"> *</font>
					</td>
				  </tr>
				  <tr>
				  	<td align="left" class="labeltext" valign="top" width="20%">
				  		<bean:message bundle="gatewayResources" key="gateway.radius.retrycount"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.retrycount"/>','<bean:message bundle="gatewayResources" key="gateway.radius.retrycount" />')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top"  width="32%"> 
						<html:text  property="retryCount" styleId="retryCount" maxlength="8" size="30"  styleClass="name" tabindex="3"/><font color="#FF0000"> *</font>
					</td>
				  </tr>
				  <tr>
				  	<td align="left" class="labeltext" valign="top" width="20%" >
				  		<bean:message bundle="gatewayResources" key="gateway.radius.statuscheckduration"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.statuscheckduration"/>','<bean:message bundle="gatewayResources" key="gateway.radius.statuscheckduration" />')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top" width="32%"> 
						<html:text  property="statusCheckDuration" styleId="statusCheckDuration" maxlength="8" size="30"  styleClass="name" tabindex="4"/><font color="#FF0000"> *</font>
					</td>
				  </tr>
				  <tr>
					  <td align="left" class="labeltext" valign="top" width="20%" >
						  <bean:message bundle="gatewayResources" key="gateway.radius.interiminterval"/>
						  	<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.interiminterval"/>','<bean:message bundle="gatewayResources" key="gateway.radius.interiminterval" />')"/>
					  </td>
					  <td align="left" class="labeltext" valign="top" width="32%">
						  <html:text property="interimInterval" styleId="interimInterval" maxlength="4" size="30" styleClass="name" tabindex="5"/><font color="#FF0000"> *</font>
					  </td>
				  </tr>
				  <tr>
				 	<td align="left" class="labeltext" valign="top" width="20%">
				  		<bean:message bundle="gatewayResources" key="gateway.radius.icmppingenabled"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.icmppingenabled"/>','<bean:message bundle="gatewayResources" key="gateway.radius.icmppingenabled" />')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:select  property="icmpPingEnabled" styleId="icmpPingEnabled" style="width: 100;" tabindex="6">
							<html:option value="false">False</html:option>
							<html:option value="true">True</html:option>
						</html:select>
					</td>
				  </tr>			
				 <tr> 
					<td align="left" class="labeltext" valign="top" width="20%">
						<bean:message bundle="gatewayResources" key="gateway.profile.supportedvendorlist" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.supportedvendorlist"/>','<bean:message bundle="gatewayResources" key="gateway.profile.supportedvendorlist" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea property="radiusSupportedVendorList" styleId="supportedVendorList" rows="2" tabindex="7" onblur="clearDiv();"/>
						<div id="errorMsgDiv_supportedVendorList" class="labeltext"></div>
					</td>
				  </tr>
				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="20%">
						<bean:message bundle="gatewayResources" key="gateway.profile.sendacctresponse" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.sendacctresponse"/>','<bean:message bundle="gatewayResources" key="gateway.profile.sendacctresponse" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:select  styleId="sendAccountingResponse" property="sendAccountingResponse" style="width: 100;" tabindex="8">
						 	<html:option value='True'>True</html:option>
						   	<html:option value='False'>False</html:option>
						</html:select>
					</td>
				  </tr>	
				  
				  <tr><td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td></tr>
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
							<input class="accessNetworkType noborder" type="text"  name="ruleMappingCondition" value='<bean:write name="pccRuleMappingBean" property="accessNetworkType" />'  maxlength="200" style="width: 100%" >
						</td>
						<td class="tblrows" align="center" valign="top">
							<img src="<%=basePath%>/images/minus.jpg" class="delete" height="15" >
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
					<td class="tblheader-bold" colspan="3"  height="20%">
						<bean:message bundle="gatewayResources" key="gateway.profile.radius.radiustopcrfpacketmapping" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.radius.radiustopcrfpacketmapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.radius.radiustopcrfpacketmapping" />')"/>
					</td>
				</tr>

				<tr><td width="10" class="small-gap">&nbsp;</td></tr> 
				<tr>
					<td><input type="button" value="Add Mapping" tabindex="12" style="margin-left: 2.2em" class="light-btn" onclick="openRadiusToPCRFMapping()" ></td>
				</tr>
    			<tr><td width="10" class="small-gap">&nbsp;</td></tr>
    
    			<tr> 
	  				<td class="btns-td" valign="middle" colspan="3">  
					<table cellpadding="0" id="mapReq" cellspacing="0" border="0" width="97%" class="">
						<thead>
							<tr>
								<td align="center" style='cursor: default' class="tblheader" valign="top" width="20%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
								<td align="center" style='cursor: default' class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
								<td align="center" style='cursor: default' class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.remove" /></td>
							</tr>
						</thead>
						<tbody>
						<logic:iterate id="packetMapData" name="diameterPacketMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
							<logic:equal name="packetMapData" property="packetMappingData.type" value="<%=ConversionType.GATEWAY_TO_PCRF.getConversionType()%>">									
						 		<tr name="mappingRequest">
                            		<td class='tblfirstcol'> <bean:write name="packetMapData" property="packetMappingData.name"/>&nbsp;<input type='hidden' name='reqPacketMapId' value='<bean:write name="packetMapData" property="packetMappingData.packetMapId"/>'/></td>
                            		<td class='tblrows' ><input tabindex="13" class='noborder' type='text' name='reqCondition' id='reqCondition' maxlength='2000' style='width: 100%' value='<bean:write name="packetMapData" property="condition"/>'/></td>
                            		<td class='tblrows' style='cursor: default' align='center' valign='middle' ><img tabindex="13" src='<%=basePath%>/images/minus.jpg' class='delete' height='14' /></td>
                         		</tr> 
							</logic:equal>							                          
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
				<tr>
					<td class="tblheader-bold" colspan="4" height="20%">
						<bean:message bundle="gatewayResources" key="gateway.profile.radius.pcrftoradiuspacketmapping" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.radius.pcrftoradiuspacketmapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.radius.pcrftoradiuspacketmapping" />')"/>
					</td>
				</tr>

				<tr><td width="10" class="small-gap">&nbsp;</td></tr> 
				<tr>
					<td><input type="button" value="Add Mapping" tabindex="14" id="pcrf_to_radius_btn" style="margin-left: 2.2em" class="light-btn" onclick="openPCRFToRadiusMapping()" ></td>
				</tr>
    			<tr><td width="10" class="small-gap">&nbsp;</td></tr>
    
    			<tr> 
	  				<td class="btns-td" valign="middle" colspan="3">  
						<table cellpadding="0" id="mapRes" cellspacing="0" border="0" width="97%" class="">
						<thead>
						<tr>
							<td align="center" style='cursor: default' class="tblheader" valign="top" width="20%"><bean:message bundle="gatewayResources" key="gateway.profile.name" /></td>
							<td align="center" style='cursor: default' class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="mapping.condition" /></td>
							<td align="center" style='cursor: default' class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.remove" /></td>
						</tr>
						</thead>
						<tbody>
						<logic:iterate id="packetMapData" name="diameterPacketMapList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData">
							<logic:equal name="packetMapData" property="packetMappingData.type" value="<%=ConversionType.PCRF_TO_GATEWAY.getConversionType()%>">
						 		<tr name="mappingResponse">
                            		<td class='tblfirstcol'> <bean:write name="packetMapData" property="packetMappingData.name"/>&nbsp;<input type='hidden' name='resPacketMapId' value='<bean:write name="packetMapData" property="packetMappingData.packetMapId"/>'/></td>
                            		<td class='tblrows' ><input tabindex="14" class='noborder' type='text' name='resCondition' id='resCondition' maxlength='2000' style='width: 100%' value='<bean:write name="packetMapData" property="condition"/>'/></td>
                            		<td class='tblrows' style='cursor: default' align='center' valign='middle'><img  tabindex="14" src='<%=basePath%>/images/minus.jpg' height='14' /></td>
                         		</tr>
							</logic:equal>                         
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
						<!-- GROOVY SCRIPT -->
				<tr><td width="10" class="">&nbsp;</td></tr>	
				<tr>
					<td class="tblheader-bold" colspan="4" height="20%">
						<bean:message bundle="gatewayResources" key="gateway.profile.groovyscript" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="profile.groovy.script"/>','<bean:message bundle="gatewayResources" key="gateway.profile.groovyscript" />')"/>
					</td>
				</tr>
				<!-- groovy start -->
				<tr><td width="10" class="small-gap">&nbsp;</td></tr> 
				<tr>
					<td><input type="button" value="Add Groovy Script" tabindex="15" id="groovy_script_btn" style="margin-left: 2.2em" class="light-btn" onclick="addGroovyScript()" ></td>
				</tr>
    			<tr><td width="10" class="small-gap">&nbsp;</td></tr>
    
     			<tr> 
	  				<td class="btns-td" valign="middle" colspan="3">  
						<table cellpadding="0" id="groovyScripts" cellspacing="0" border="0" width="97%" class="">
						<thead>
						<tr>
							<td align="center" style="cursor:default"  class="tblheader" valign="top" width="10%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.ordernumber" /></td>
							<td align="center" style="cursor:default"  class="tblheader" valign="top" width="30%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.name" /></td>
							<td align="center" style="cursor:default"  class="tblheader" valign="top" width="40%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.argument" /></td>
							<td align="center" style="cursor:default"  class="tblheader" valign="top" width="5%"><bean:message bundle="gatewayResources" key="gateway.profile.groovyscript.remove" /></td>						
						</tr>
						</thead>
						<tbody>
 						<logic:iterate id="groovyScriptData" name="groovyScriptsDataList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData">							
						 		<tr>
                            		<td class='tblrows'><input  type='hidden' name='orderNumber' maxlength='5' style='width: 100%' value='&nbsp;<bean:write name="groovyScriptData" property="orderNumber"/>'/>&nbsp;<bean:write name="groovyScriptData" property="orderNumber"/></td>
                            		<td class='tblrows'><input tabindex="15" class='noborder' type='text' name='scriptName' maxlength='2048' style='width: 100%' value='<bean:write name="groovyScriptData" property="scriptName"/>'/></td>
                            		<td class='tblrows'><input tabindex="15" class='noborder' type='text' name='argument' maxlength='2048' style='width: 100%' value='<bean:write name="groovyScriptData" property="argument"/>'/></td>
                            		<td class='tblrows' style='cursor: default' align='center' valign='middle'><img  tabindex="15" src='<%=basePath%>/images/minus.jpg' class='delete' height='14' /></td>
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
						<!-- end -->
    			<tr> 
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
	  			</tr> 		  					  		  		  		   
	    		<tr>
	    			<td>&nbsp;</td>    
            		<td class="btns-td" align="left" valign="middle" > 
		        		<input type="button" class="light-btn"  onclick="validate();" value=" Update " tabindex="16" styleClass="light-btn" /> 
                		<input type="button" align="left" value="  Cancel  " class="light-btn" tabindex="17" onclick="javascript:location.href='<%=basePath%>/initSearchProfile.do?/>'" />
	        		</td> 
   				</tr> 
		  		
		  		
		  		
		  			
			</table>
		</td>
	</tr>		
</table> 

<div id="popupRadiusToPCRFMapping" title="Add Packet Mapping" style="display: none;">

	<div class="labeltext">&nbsp;&nbsp;&nbsp;Condition &nbsp;&nbsp;&nbsp;<textarea rows="2" cols="50" id="dpcond"></textarea></div>

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
	<logic:iterate id="packetMap" name="editGatewayProfileForm" property="radiusToPCRFPacketMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData">
		<tr id="<bean:write property="packetMapId" name="packetMap" />">
			<td align="center" class="tblfirstcol" id="<bean:write property="packetMapId" name="packetMap" />" >
				<input type="radio" name="radiusToPCRFMapList" id="<bean:write property="packetMapId" name="packetMap" />" value="<bean:write property="name" name="packetMap" />" />
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

 <div id="popupPCRFToRadiusMapping" title="Add Packet Mapping" style="display: none;">

	<div class="labeltext">&nbsp;&nbsp;&nbsp;Condition &nbsp;&nbsp;&nbsp;
	   <textarea rows="2" cols="50" id="pdcond"></textarea>
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
	<logic:iterate id="packetMap" name="editGatewayProfileForm" property="pcrfToRadiusPacketMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData">
		<tr id="<bean:write property="packetMapId" name="packetMap" />">
			<td align="center" class="tblfirstcol">
				<input type="radio" name="pcrfToRadiusMapList" id="<bean:write property="packetMapId" name="packetMap" />" value="<bean:write property="name" name="packetMap" />" />
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
		<td class="allborder" align="center">
			<select name="ruleMappingId" style="width: 98%; height: 20px font-size: 12px; font-family: Arial" >
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
			<img src="<%=basePath%>/images/minus.jpg" class="delete" height="15">
		</td>
	</tr>
</table>
			