<%@page import="com.elitecore.netvertexsm.web.servermgr.drivers.csvdriver.form.CSVDriverForm"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVStripFieldMapData"%>
<script type="text/javascript" 	src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" 	src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<link 		rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>
<%@page import="com.elitecore.netvertexsm.util.constants.TimeUnits"%>

<%
		String[] headerArray = (String[])request.getAttribute("headerArray");
		String[] pcrfKeyArray = (String[])request.getAttribute("pcrfKeyArray");
		String[] stripPcrfKeyArray = (String[])request.getAttribute("stripPcrfKeyArray");
		String[] patternArray = (String[])request.getAttribute("patternArray");
		String[] seperatorArray = (String[])request.getAttribute("seperatorArray");
%>

<script type="text/javascript">
function validateForm(){
		if(isNull(document.forms[0].fileName.value)){
			alert('File Name must be specified.');
		}else if(isNull(document.forms[0].location.value)){
			alert('Location must be specified');
		}else if(document.forms[0].allocatingProtocol.value.toLowerCase() != "Local".toLowerCase() && isNull(document.forms[0].address.value)){
				alert('Address must be specified');
	 	}else if(validateRollingUnit()) {
			return;
		}else if(isNull(document.forms[0].failOvertime.value)){ 
			alert('Fail Over Time must be specified');
		}else if( isEcNaN(document.forms[0].failOvertime.value)){
			alert('Fail Over Time must be positive numeric');
	 	}else if(validateHeader()){
 		         return;
 	 	}else if(validatePCRFKey()){
 		    return;
 		}else if(validateStripPCRFKey()){
		    return;
 		}else{
 	 		document.forms[0].submit();
 	 	}		
}
var dbFieldArray=[];
$(document).ready(function() {			
	$("#headerMain").focus();
	$('td img.delete').on('click',function() {
		$(this).parent().parent().remove(); 
	});
	var mappingTypeArray = new Array();
    mappingTypeArray[0] = "<%=PCRFKeyType.REQUEST.getVal()%>";
    $.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
	dbFieldStr = data.substring(1,data.length-3);
	dbFieldArray = dbFieldStr.split(", ");
	policyKey();
	return dbFieldArray;
});
	changeReportingType();
	setRollingType();	
});

$(document).ready(function(){
	dragAndDrop();
	changeReportingType();
});


function popup() {
	var orderNumArray = document.getElementsByName("orderNumber");
		var currentOrderNumber=1;
		if(orderNumArray!=null && orderNumArray.length>0){
			currentOrderNumber = orderNumArray.length+1;
	}
	var tableRows = $("#mappingtable tr:last").index();
	if(tableRows <= 0){
		$("#mappingtable tbody").after('<tr><td align="center" class="tblfirstcol" valign="top" width="8%" id="orderNumber" name="orderNumber"> '+currentOrderNumber+'</td>' + $("#popupfeildMapping").find("tr").html() + '</tr>');
	}else{
		$("#mappingtable tr:last").after('<tr><td align="center" class="tblfirstcol" valign="top" width="8%" id="orderNumber" name="orderNumber"> '+currentOrderNumber+'</td>' + $("#popupfeildMapping").find("tr").html() + '</tr>');
	}
	policyKey();
	dragAndDrop();
}
	 
		
function policyKey() {
		commonAutoCompleteUsingCssClass("td input.pcrfkey",dbFieldArray);
}
function strippopup(){
	$("#mappingtbl2 tr:last").after("<tr>" + $("#stripmapping").find("tr").html() + "</tr>");
	policyKey();
}

function validateHeader() {
	var flag = false;
	var headerVals = document.getElementsByName("headerVal");
	if (headerVals.length > 0) {
		for ( var j = 0; j < headerVals.length - 1; j++) {
			if (headerVals[j].value == null || headerVals[j].value == "") {
				alert('Header Value Must be specified');
				flag = true;
				return flag;
			}
			for ( var k = j + 1; k < headerVals.length-1; k++) {
				if (headerVals[j].value == headerVals[k].value) {
					flag = true;
					alert('Duplicate Entry for header '+ headerVals[j].value + ' in Field Mapping');
					return flag;
				}
			}
		}
	}
	return flag;
}
	function validateRollingUnit() {
		var fileRolligType = $('#fileRollingType').val();
		var rollingUnit = $('#rollingUnitOther').val();
		 if(fileRolligType=='1'){
	           return false;
	        }
		if (isNull(rollingUnit)) {
			alert('Rolling Unit must be specified');
			return true;
		}
		if (isEcNaN(rollingUnit)) {
			alert('Rolling Unit must be positive numeric');
			return true;
		}
	}

function validatePCRFKey() {
	var flag = false;
	var pcrfKeyVals = document.getElementsByName("pcrfKeyVal");
	if (pcrfKeyVals.length > 0) {
		for ( var j = 0; j < pcrfKeyVals.length - 1; j++) {
			if (pcrfKeyVals[j].value == null || pcrfKeyVals[j].value == "") {
				alert('PCRF Key Must be specified in Field Mapping');
				flag = true;
				return flag;
			}
			for ( var k = j + 1; k < pcrfKeyVals.length-1; k++) {
				if (pcrfKeyVals[j].value == pcrfKeyVals[k].value) {
					flag = true;
						alert('Duplicate Mapping for PCRF Key '
								+ pcrfKeyVals[j].value + ' in Field Mapping');
					return flag;
				}
			}
		}
	}
	return flag;
}
function validateStripPCRFKey() {
	var flag = false;
	var stripPcrfKeys = document.getElementsByName("stripPcrfKeyArray");
	
	if (stripPcrfKeys.length > 0) {
		for ( var j = 0; j < stripPcrfKeys.length - 1; j++) {
				if (stripPcrfKeys[j].value == null
						|| stripPcrfKeys[j].value == "") {
				alert('PCRF Key Must be specified in Strip Pattern Mapping');
				flag = true;
				return flag;
			}
			for ( var k = j + 1; k < stripPcrfKeys.length-1; k++) {
				if (stripPcrfKeys[j].value == stripPcrfKeys[k].value) {
					flag = true;
					alert('Duplicate Mapping for PCRF Key '
								+ stripPcrfKeys[j].value
								+ ' in Strip Pattern Mapping');
					return flag;
				}
			}
		}
	}
	return flag;
}

function changeReportingType(){
	var reportingType = document.getElementById("reportingType").value;
	var headerValue = document.getElementById("headerMain").value;
	if(headerValue == 'false') {
		disableHeader();	
	}else{
		if(reportingType == '0'){
			disableHeader();
		}else{
			enableHeader();
		} 
	}	
}
function disableHeader(){
 	document.getElementById("usageKeyHeader").disabled = true;
	document.getElementById("inputOctetsHeader").disabled = true;
	document.getElementById("outputOctetsHeader").disabled = true;
	document.getElementById("totalOctetsHeader").disabled = true;
	document.getElementById("usageTimeHeader").disabled = true;	
}
function enableHeader(){
	document.getElementById("usageKeyHeader").disabled = false;
	document.getElementById("inputOctetsHeader").disabled = false;
	document.getElementById("outputOctetsHeader").disabled = false;
	document.getElementById("totalOctetsHeader").disabled = false;
	document.getElementById("usageTimeHeader").disabled = false;
}
function trimvalue(field){
    field.value=$.trim(field.value);
} 

function setRollingType(){
    var rollingtype=$('#fileRollingType').val();
    if( rollingtype== '2' || rollingtype=='3'){
             $('#timebasedrolling').hide();
             $('#nontimebasedrolling').show();
     }else{
   	  $('#timebasedrolling1').innerHtml=$("#rollingtypetable tr:last").html();
   	    $('#timebasedrolling').show();
           $('#nontimebasedrolling').hide();
         }

}

function dragAndDrop(){
	$('#mappingtable').tableDnD();
}

</script>

<html:form action="/editCSVDriver" styleId="mainform">
<html:hidden name="csvDriverForm" styleId="driverInstanceId" property="driverInstanceId"/>
	<table width="97%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" >				 							 	 
				 		<tr>
							<td class="tblheader-bold" valign="top" colspan="4" style="">
								<bean:message bundle="driverResources" key="driver.csvdriver.cdrdetails" />
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" >
							<bean:message bundle="driverResources" key="driver.csvdriver.header" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12"  style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.header"/>','<bean:message bundle="driverResources" key="driver.csvdriver.header" />')"/>										
							</td>
							<td align="left" valign="top"  colspan="3">
							<html:select property="header" styleId="headerMain" style="width: 70px" tabindex="1" onchange="changeReportingType();">
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>
							</html:select>								
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" width="35%">
							<bean:message bundle="driverResources" key="driver.csvdriver.delimeter" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.delimiter"/>','<bean:message bundle="driverResources" key="driver.csvdriver.delimeter" />')"/>																	
							</td>
							<td align="left"  valign="top">
								<html:text styleId="delimiter" property="delimiter" size="6" maxlength="1" tabindex="2"/>
							</td>															
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.csvdriver.cdrtimestampformat" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.cdrtimestamp"/>','<bean:message bundle="driverResources" key="driver.csvdriver.cdrtimestampformat" />')"/>																	
							</td>
							<td align="left" valign="top" >
								<html:text styleId="cdrTimestampFormat" property="cdrTimestampFormat" size="15" maxlength="128" tabindex="3"/>																																																	
							</td>									
						</tr>
									
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="">
							<bean:message bundle="driverResources" key="driver.filedetail"/></td>
						</tr>
					
						<tr>
							<td align="left" class="labeltext" valign="top" >
							<bean:message bundle="driverResources" key="driver.filename" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.filename"/>','<bean:message bundle="driverResources" key="driver.filename" />')"/>																	
							</td>
							<td align="left" valign="top" colspan="3">
								<html:text styleId="fileName" property="fileName" size="20" maxlength="64" onblur="trimvalue(this);" tabindex="4"/><font color="#FF0000"> *</font></td>
						</tr>
						<tr>
						<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.location" />
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.location"/>','<bean:message bundle="driverResources" key="driver.location" />')"/>																							
								</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="location" property="location" size="20" maxlength="512" style="width:250px" onblur="trimvalue(this);" tabindex="5"/><font color="#FF0000"> *</font>
							</td>	
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.prefixFileName" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.prefixfilename"/>','<bean:message bundle="driverResources" key="driver.defaultdirname" />')"/>																		
							</td>
							<td align="left" valign="top" >
								<html:text styleId="prefixFileName" property="prefixFileName" size="20" maxlength="64" onblur="trimvalue(this);" tabindex="6"/>
							</td>												
						</tr>		
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.defaultdirname" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.defaultfoldername"/>','<bean:message bundle="driverResources" key="driver.defaultdirname" />')"/>																	
							</td>
							<td align="left" valign="top" colspan="3">
								<html:text styleId="defaultDirName" property="defaultDirName" size="20" maxlength="64" onblur="trimvalue(this);" tabindex="7"/>
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.foldername" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.foldername"/>','<bean:message bundle="driverResources" key="driver.foldername" />')"/>																	
							</td>
							<td align="left" valign="top" >
								<html:text styleId="folderName" property="folderName" size="20" maxlength="64" onblur="trimvalue(this);" tabindex="8"/>
							</td>	
							
						</tr>
						
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="">
							<bean:message bundle="driverResources" key="driver.filerollingdetail"/></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
								<bean:message bundle="driverResources" key="driver.filerollingtype" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.rollingtype"/>','<bean:message bundle="driverResources" key="driver.filerollingtype" />')"/>																	
							</td>
							<td align="left" valign="top" width="40%" >
								<html:select name="csvDriverForm" property="fileRollingType" styleId="fileRollingType"  style="width: 130px" onchange="setRollingType();" tabindex="9">
									<html:option value="1">Time-Based(min)</html:option>
									<html:option value="2">Size-Based(kb)</html:option>
									<html:option value="3">Record-Based(No. of records)</html:option>	
								</html:select>
							</td>														
						</tr>
						<tr id="nontimebasedrolling">
								<td align="left" class="labeltext" valign="top" width="17%">
									<bean:message bundle="driverResources" key="driver.rollingUnit" />
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.rollingunit"/>','<bean:message bundle="driverResources" key="driver.rollingUnit" />')"/>																	
								</td>
								<td align="left" valign="top" >
									<html:text styleId="rollingUnitOther" property="rollingUnitOther" size="10" maxlength="12" onblur="trimvalue(this);" tabindex="10"/>
								</td>
							</tr>
							<tr id="timebasedrolling">
								<td align="left" class="labeltext" valign="top" width="17%" >
									<bean:message bundle="driverResources" key="driver.rollingUnit" />
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.rollingunit"/>','<bean:message bundle="driverResources" key="driver.rollingUnit" />')"/>																	
								</td>
								<td align="left" valign="top" >
									<html:select name="csvDriverForm" property="rollingUnit" styleId="rollingUnit"  style="width: 130px" tabindex="11">
										<% for(TimeUnits unit : TimeUnits.values()){%>
										<html:option value='<%=unit.key %>'><%=unit.val %></html:option>
										<% }%>
									</html:select>
								</td>
							</tr>
						<tr>
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.range" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.sequencerange"/>','<bean:message bundle="driverResources" key="driver.range" />')"/>																	
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="range" property="range" size="10" maxlength="40" style="width:200px" tabindex="12"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.pos" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.sequenceposition"/>','<bean:message bundle="driverResources" key="driver.pos" />')"/>																
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:select name="csvDriverForm" property="sequencePosition" styleId="sequencePosition"  style="width: 70px" tabindex="13">
									<html:option value="suffix">Suffix</html:option>		
									<html:option value="prefix">Prefix</html:option>																				
								</html:select>
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.global" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.sequenceglob"/>','<bean:message bundle="driverResources" key="driver.global" />')"/>																	
							</td>
							<td align="left" valign="top" colspan="3"> 								
								<html:select name="csvDriverForm" property="globalization" styleId="global"  style="width: 70px" tabindex="14">
									<html:option value="false">False</html:option>		
									<html:option value="true">True</html:option>																						
								</html:select>
							</td>
									
						</tr>
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="">
							<bean:message bundle="driverResources" key="driver.filetransferdetails" /></td>
						</tr>

						<tr>
							<td align="left" class="labeltext" valign="top" >
							<bean:message bundle="driverResources" key="driver.allocatingprotocol" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.protocol"/>','<bean:message bundle="driverResources" key="driver.allocatingprotocol" />')"/>										
							
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:select name="csvDriverForm" property="allocatingProtocol" styleId="allocatingProtocol" style="width: 70px" tabindex="15">
									<html:option value="LOCAL">Local</html:option>
									<html:option value="FTP">FTP</html:option>
									<html:option value="SMTP">SMTP</html:option>																	
								</html:select>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.address" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.address"/>','<bean:message bundle="driverResources" key="driver.address" />')"/>										
							</td>
							<td align="left" valign="top" colspan="4">
								<html:text styleId="address" property="address" size="20" maxlength="255" tabindex="16"/>
								<font color="#999999"> Host : Port </font>
							</td>
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.remoteLocation" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.remoteloc"/>','<bean:message bundle="driverResources" key="driver.remoteLocation" />')"/>																		
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="remoteLocation" property="remoteLocation" size="29" maxlength="512" style="width:250px" onblur="trimvalue(this);" tabindex="17"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.username" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.username"/>','<bean:message bundle="driverResources" key="driver.username" />')"/>																	
							</td>
							<td align="left" valign="top" >
								<html:text styleId="userName" property="userName" size="15" maxlength="64" onblur="trimvalue(this);" tabindex="18"/>
							</td>														
						</tr>
						<tr>	
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.password" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.password"/>','<bean:message bundle="driverResources" key="driver.password" />')"/>
							</td>
							<td align="left" valign="top" >
								<html:password styleId="password" property="password" size="15" maxlength="64" tabindex="19"/>
							</td>												
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.postoperation" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.postoperation"/>','<bean:message bundle="driverResources" key="driver.password" />')"/>																        
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:select name="csvDriverForm" property="postOperation" styleId="postOperation" style="width: 75px" tabindex="20">
									<html:option value="rename">Rename</html:option>	
									<html:option value="archive">Archive</html:option>
									<html:option value="delete">Delete</html:option>																							
								</html:select>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top">
								<bean:message bundle="driverResources" key="driver.archiveloc" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.archiveloc"/>','<bean:message bundle="driverResources" key="driver.archiveloc" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="archiveLocation" property="archiveLocation" size="29" maxlength="255" style="width:250px" onblur="trimvalue(this);" tabindex="21"/>
							</td>							
						</tr>
					
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.failovertime" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.failovertime"/>','<bean:message bundle="driverResources" key="driver.failovertime" />')"/>																    
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="failOvertime" property="failOvertime" size="5" maxlength="10" onblur="trimvalue(this);" tabindex="22"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="">
							<bean:message bundle="driverResources" key="driver.csvdriver.usagefields" /></td>
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.csvdriver.reportingtype" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.reportingtype"/>','<bean:message bundle="driverResources" key="driver.csvdriver.reportingtype" />')"/>						        
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:select name="csvDriverForm" property="reportingType" styleId="reportingType" onchange="changeReportingType();" style="width: 130px" tabindex="23">
									<html:option value="0">--None--</html:option>	
									<html:option value="UM">Usage Monitoring</html:option>
								</html:select>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.csvdriver.usagekeyheader" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.usagekeyheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.usagekeyheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="usageKeyHeader" property="usageKeyHeader" size="15" maxlength="30" onblur="trimvalue(this);" tabindex="24" />
							</td>							
						</tr>
												
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.csvdriver.inputoctetsheader" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.inputoctetsheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.inputoctetsheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="inputOctetsHeader" property="inputOctetsHeader" size="15" maxlength="30" onblur="trimvalue(this);" tabindex="25"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.csvdriver.outputoctetsheader" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.outputoctetsheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.outputoctetsheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="outputOctetsHeader" property="outputOctetsHeader"   size="15" maxlength="30" onblur="trimvalue(this);" tabindex="26"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.csvdriver.totalOctetsheader" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.totalOctetsheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.totalOctetsheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="totalOctetsHeader" property="totalOctetsHeader" size="15" maxlength="30" onblur="trimvalue(this);" tabindex="27"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" >
								<bean:message bundle="driverResources" key="driver.csvdriver.usageTimeheader" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.usageTimeheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.usageTimeheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="usageTimeHeader" property="usageTimeHeader" size="15" maxlength="30" tabindex="28"/>
							</td>							
						</tr>		     		     					     			
		     							
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="">
							<bean:message bundle="driverResources" key="driver.csvdriver.csvfieldmap" /></td>
						</tr>													 
                   		<tr>                      		
                       		<td align="left" class="" valign="top" colspan="3" id="button">
                      		 <input type="button" onclick="popup()"  value=" Add Mapping" class="light-btn" style="size: 140px" tabindex="29"/> </td>                      		                       		 
                   		</tr>   
                   		<tr>
				        	<td width="10" class="small-gap">&nbsp;</td>
				        	<td class="small-gap" colspan="2">&nbsp;</td>
    					</tr>
                   		<tr>
						   <td width="100%" colspan="4" valign="top" style="">
							<table cellSpacing="0" cellPadding="0" width="80%" border="0" id="mappingtable">
							<thead>
							<tr>								
								<td align="left" class="tblheaderfirstcol" valign="top" width="8%" > <bean:message bundle="driverResources" key="driver.csvdriver.ordernumber" /> 
								<td align="left" class="tblheaderfirstcol" valign="top" width="40%" ><bean:message bundle="driverResources" key="driver.csvdriver.header" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.header"/>','<bean:message bundle="driverResources" key="driver.csvdriver.header" />')"/>										
								</td>
								<td align="left" class="tblheader" valign="top"  id="tbl_attrid" width="42%"><bean:message bundle="driverResources" key="driver.csvdriver.pcrfkey" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.pcrfkey"/>','<bean:message bundle="driverResources" key="driver.csvdriver.pcrfkey" />')"/>										
								</td>																  									
								<td align="left" class="tblheaderlastcol" valign="top" width="10%">Remove</td>							  
							</tr>
							</thead>
							<tbody>
					<logic:iterate id="csvFieldMapBean" name="csvDriverForm" property="csvFieldMapSet" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVFieldMapData">
	                   <tr>
	                   	<td class="tblfirstcol" align="center" width="8%" id="orderNumber" name="orderNumber"> <bean:write name="csvFieldMapBean" property="orderNumber" />  
						</td>
						<td class="tblrows" align="center" width="40%">
								<input class="noborder" type="text" value='<bean:write name="csvFieldMapBean" property="header" />' name="headerVal" maxlength="255" style="width: 95%" onblur="trimvalue(this);" tabindex="29" />
								<font color="#FF0000"> *</font>	
						</td>
						<td class="tblrows" width="42%">
								<input class="pcrfkey noborder" type="text" value='<bean:write name="csvFieldMapBean" property="pcrfKey" />' name="pcrfKeyVal" maxlength="255" style="width: 95%" onblur="trimvalue(this);" tabindex="29"/>
								<font color="#FF0000"> *</font>	
						</td>
						<td class="tblrows" align="center" valign="top" width="10%">
							<img src="<%=basePath%>/images/minus.jpg" class="delete" height="15" tabindex="29">
						</td>
					</tr>
				</logic:iterate> 
				</tbody>
				</table>
				</td>	
		</tr>
		<tr>
			<td  colspan="3" valign="top" > 	
				<label  class="small-text-grey"><bean:message  key="table.ordering.note"/></label> 
			</td>
		</tr>
		<tr>
        	<td width="10" class="small-gap">&nbsp;</td>
        	<td class="small-gap" colspan="2">&nbsp;</td>
    	</tr>
    	<tr>
        	<td width="10" class="small-gap">&nbsp;</td>
        	<td class="small-gap" colspan="2">&nbsp;</td>
    	</tr>
    	<tr>
        	<td width="10" class="small-gap">&nbsp;</td>
        	<td class="small-gap" colspan="2">&nbsp;</td>
    	</tr>
     	
     	<tr>	
			<td align="left" class="tblheader-bold" valign="top" colspan="4" style="">
			<bean:message bundle="driverResources" key="driver.csvstripmapping"/></td>
		</tr>													 
       
       <tr>
			<td align="left" class="" valign="top" colspan="4">
            <input type="button" onclick="strippopup()"  value=" Add Strip Pattern Mapping"  class="light-btn" tabindex="30"/> </td>                      		                       		 
       </tr>
         <tr>
			<td width="10" class="small-gap">&nbsp;</td>
		    <td class="small-gap" colspan="2">&nbsp;</td>
    	</tr>
     	
		<tr>
			   <td  align="left" width="100%" colspan="4" valign="top" style="">
					<table cellSpacing="0" cellPadding="0" width="80%" border="0" id="mappingtbl2">
						<tr>
							<td align="left" class="tblheaderfirstcol" valign="top"  id="tbl_attrid" width="45%"><bean:message bundle="driverResources" key="driver.csvdriver.pcrfkey" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="csvdriver.pcrfkey"/>','<bean:message bundle="driverResources" key="driver.csvdriver.pcrfkey" />')"/>																    
							</td>																  								
							<td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="driverResources" key="driver.pattern" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="classiccsvdriver.pattern"/>','Pattern')"/>																    
							</td>
							<td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="driverResources" key="driver.seperator" />
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="classiccsvdriver.separator"/>','Separator')"/>																    
							</td>  	
							<td align="left" class="tblheaderlastcol" valign="top" width="10%" >Remove</td>							  
						</tr>
				    <logic:iterate id="csvStripFieldMapBean" name="csvDriverForm" property="csvStripFieldMapDataSet" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVStripFieldMapData">
					<tr>
						<td class="tblfirstcol" align="center" width="40%">
							<input class="pcrfkey noborder" type="text" value='<bean:write name="csvStripFieldMapBean" property="pcrfKey" />' name="stripPcrfKeyArray" maxlength="255" style="width :95%;" onblur="trimvalue(this);" tabindex="30" />
							<font color="#FF0000"> *</font>	
						</td>
						<td class="tblrows" width="25%">
							 <select name="stripPatternArray" style="width:100%;" tabindex="30" >
							 
							 	<%if(csvStripFieldMapBean.getPattern().equals("suffix")){%>
									<option value="suffix" selected="selected">Suffix</option>		
								<%}else{%>
									<option value="suffix">Suffix</option>
								<%}%>
								
							 	<%if(csvStripFieldMapBean.getPattern().equals("prefix")){%>
									<option value="prefix" selected="selected">Prefix</option>
								<%}else{%>
									<option value="prefix">Prefix</option>
								<%}%>
								
							</select>	
						</td>
						<td class="tblrows" align="left" width="25%">
							<input class="noborder" type="text" value='<bean:write name="csvStripFieldMapBean" property="separator" />' name="stripSeperatorArray" maxlength="1" style="width :40%;" tabindex="30" />
						</td>
						<td class="tblrows" align="center" valign="top" width="10%">
							<img src="<%=basePath%>/images/minus.jpg" class="delete" height="15" tabindex="30">
						</td>
					</tr>
				   </logic:iterate> 
				</table>
			</td>	
		</tr>	
		<tr>
        	<td width="10" class="small-gap">&nbsp;</td>
        	<td class="small-gap" colspan="2">&nbsp;</td>
    	</tr>
		<tr> 
		  	<td class="btns-td" valign="middle" align="center" colspan="4">
		    	<input type="button" value="Update" class="light-btn" onclick="validateForm();" tabindex="31"/>                
				<input type="reset" onclick="javascript:window.location.href='<%=basePath%>/initSearchDriverInstance.do?'" value="Cancel" class="light-btn" tabindex="32"/>                                                       
			</td>
		</tr>
		
		  <tr>
			<td align="left" class="labeltext" valign="top" colspan="4">&nbsp;</td>
		  </tr> 
      </table>
</html:form>
<table id="stripmapping" style="display: none;">
   		<tr>
			<td align="left" class="tblfirstcol" valign="top"  width="45%">
				<input type="text" name="stripPcrfKeyArray" class="pcrfkey" maxlength="255" id="stripPcrfKey" style="width:95%;" autocomplete="off" onblur="trimvalue(this);" tabindex="30"/>
				<font color="#FF0000"> *</font>	
			</td>							
			<td align="left" class="tblrows" valign="top" width="25%">
			    <select name="stripPatternArray" style="width:100%;" tabindex="30">
					<option value="suffix">Suffix</option>		
					<option value="prefix">Prefix</option>
				</select>																														
			</td>							
			<td align="left" class="tblrows" valign="top" width="25%" >
				<input type="text" id="seperator" size="5" maxlength="1" name="stripSeperatorArray" style="width:40%;" tabindex="30"/>
			</td>
			<td class='tblrows' align='center' width="10%">
				<img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' tabindex="30" onclick="$(this).parent().parent().remove();"/>
			</td>							
		</tr>
	</table>
<table id="popupfeildMapping" style="display: none;">       					
		<tr>
			<td align="left" class="tblfirstcol" valign="top" width="40%" >
						<input type="text" id="headerVal" name="headerVal" maxlength="255" style="width:95%;" onblur="trimvalue(this);" tabindex="29"/>
						<font color="#FF0000"> *</font>			
			</td>
			<td align="left" class="tblrows" valign="top"  width="42%">
						<input type="text" name="pcrfKeyVal" class="pcrfkey" maxlength="255" id="pcrfKey" style="width:95%;" autocomplete="off" onblur="trimvalue(this);" tabindex="29"/>
						<font color="#FF0000"> *</font>									
			</td>
			<td class='tblrows' align='center'  width="20%" ><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' tabindex="29" onclick="$(this).parent().parent().remove();"/></td>
		</tr>												
 </table>	