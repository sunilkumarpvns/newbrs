<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>

<style>
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px} 
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKeyForDBField { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style>

<%	
		String[] pcrfKeyArray = (String[])request.getAttribute("pcrfKeyArray");
		String[] dbFieldArray = (String[])request.getAttribute("dbFieldArray");
		String[] dataTypeArray = (String[])request.getAttribute("dataTypeArray");
		String[] defaultValueArray = (String[])request.getAttribute("defaultValueArray");
		String cdrBasePath = request.getContextPath();
%>

<script type="text/javascript">
$(document).ready(function() {
	$('#add').click(function() {
		var tableRowStr = '<tr>'+
								'<td class="allborder"><input class="plcKey" type="text" name="pcrfKeyArray" maxlength="200"  style="width:100%"/></td>'+
								'<td class="tblrows"><input class="plcKeyForDBField" type="text" name="dbFieldArray" maxlength="1000"  style="width:100%"/></td>'+
								'<td class="allborder"><html:select styleClass="noborder" property="dataTypeArray" name="dbCDRDriverForm" style="width:100%;"><html:option value="5">String</html:option><html:option value="4">Date</html:option></html:select></td>'+
								'<td class="tblrows"><input class="noborder" type="text" name="defaultValueArray" maxlength="1000"  style="width:100%"/></td>'+
								'<td class="tblrows" align="center"><img value="top" src="<%=cdrBasePath%>/images/minus.jpg" class="delete"  height="15"  	/></td>'+
							'</tr>';

	   $(tableRowStr).appendTo('#mappingtable');
	   retrieveSuggestionForPCRFKey();
	   var dataSourceID = document.getElementById("databaseDSID").value;
	   retriveTableFieldsForMapping(dataSourceID);
		$('#mappingtable td img.delete').on('click',function() {
			$(this).parent().parent().remove();
		});
	});
	changeReportingType();
	setBatchUpdateOperation();
	setBatchUpdate();
	$('#mappingtable td img.delete').on('click',function() {
		$(this).parent().parent().remove();
	});
});



function retrieveSuggestionForPCRFKey(){
	var mappingTypeArray = new Array();
	mappingTypeArray[0] = "<%=PCRFKeyType.REQUEST.getVal()%>";
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoCompleteUsingCssClass("td input.plcKey",dbFieldArray);
		return dbFieldArray;
	});
}

function retrieveTableNames(dbId){
	var tableFieldStr;
	var tableName = "TABS";
	$.post("FieldRetrievalServlet", {databaseId:dbId, tblName:tableName}, function(data){
	tableFieldStr = data.substring(1,data.length-3);
	var tableFieldArray = new Array();
	tableFieldArray = tableFieldStr.split(", ");			
	setFields("tableName",tableFieldArray);
	return tableFieldArray;
});
	
}

var dbFieldArray = new Array();
function retriveTableFieldsForMapping(dbId) {
	var dbFieldStr;
	var tableName = document.getElementById("tableName").value;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoCompleteUsingCssClass("td input.plcKeyForDBField",dbFieldArray);		
		return dbFieldArray;
	});
}
function checkNullForDBCDRMapping(){
		var pcrfKeyArray = document.getElementsByName("pcrfKeyArray");
		var dbFieldArray = document.getElementsByName("dbFieldArray");
		
		for(var i=0; i<pcrfKeyArray.length;i++){
			if(isNull(pcrfKeyArray[i].value) || isNull(dbFieldArray[i].value)){
				return false;
			}
		} 
		return true;
	}

function validateForm(){	
		if(document.forms[0].databaseDSID.value == 0){
			alert('Select atleast one datasource value ');
		}else if(isEcNaN(document.forms[0].dbQueryTimeout.value)){
	 		alert("DB Query Timeout must be numeric.");
		}else if(isEcNaN(document.forms[0].maxQueryTimeoutCount.value)){
	 		alert("Maximum Query Timeout must be numeric.");
		}else if(isNull(document.forms[0].tableName.value)){
			alert('Table Name must be specified');
		}else if(isNull(document.forms[0].identityField.value)){
			alert('Identity Field must be specified');
		}else if(isNull(document.forms[0].sequenceName.value)){
			alert('Sequence Name must be specified.');
		}else if(isNull(document.forms[0].sessionIDFieldName.value)){
			alert('SessionID Field Name must be specified.');
		}else if(isNull(document.forms[0].createDateFieldName.value)){
			alert('Create Date Field Name must be specified.');
		}else if(isNull(document.forms[0].lastModifiedFieldName.value)){
			alert('Last Modified Field Name must be specified.');
		}else if(isNull(document.forms[0].timeStampformat.value)){
			alert('TimeStamp Field Name must be specified.');
		}else if(document.forms[0].isBatchUpdate.value == 'true' && validateBatchUpdateParams() == false){
			return;
		}else if(document.getElementById('mappingtable').getElementsByTagName('tr').length <= 1){
 			alert('Add atleast one DB CDR Driver mapping.');
 			return false;
 		}else if(checkNullForDBCDRMapping() == false){
 			alert("PCRF Key and DBField can not be null in DB CDR Driver Mappings");
 			return;
 		}else if(checkIfColumnExistsInTable() == false){
 			return;
 		}else{	
			document.forms[0].submit();
 		}
}

function isFieldExistsInTable(value){
		return ($.inArray(value,dbFieldArray) >= 0);
}

function checkIfColumnExistsInTable(){
	var DBKey = "";
	if(document.getElementById('mappingtable').getElementsByTagName('tr').length >= 2){
		var existingDbKey = document.getElementsByName("dbFieldArray");
		for(var i=0;i<existingDbKey.length;i++){
			var value = existingDbKey[i].value;
			if(!isFieldExistsInTable(value)){
				if(i == (existingDbKey.length -1 )){
					DBKey = DBKey + value;
				}else{
					DBKey = DBKey + value + ", ";
				}
			}
		}
	}
		
	if(DBKey != ""){
		if(!confirm("Database Key " + DBKey + " does not contains in Table " + document.forms[0].tableName.value +". Do you want to add field in mapping?")){
			return false;
		} else{
			return true;
		}
	} 
}
	
	function validateBatchUpdateParams(){
		document.forms[0].batchSize.value = $.trim(document.forms[0].batchSize.value);
		
		if(isNull(document.forms[0].batchSize.value)){
			alert('Batch Size must be specified');
			document.forms[0].batchSize.focus();
			return false;
		}else if(isEcNaN(document.forms[0].batchSize.value)){
			alert('Batch Size must be Numeric');
			document.forms[0].batchSize.focus();
			return false;
		} else if(document.forms[0].batchSize.value < 10 || document.forms[0].batchSize.value > 1000){
			alert('Batch Size must be between 10 to 1000');
			document.forms[0].batchSize.focus();
			return false;
		} else if(isNull(document.forms[0].batchUpdateInterval.value)){		
			alert('Batch Update Interval must be specified.');
			document.forms[0].batchUpdateInterval.focus();
			return false;
		}else if(isEcNaN(document.forms[0].batchUpdateInterval.value)){
			alert('Batch Update Interval must be Numeric');
			document.forms[0].batchUpdateInterval.focus();
			return false;
		}else if(document.forms[0].batchUpdateInterval.value < 1 || document.forms[0].batchUpdateInterval.value > 100){
			alert('Batch Update Interval must be between 1 and 100');
			document.forms[0].batchUpdateInterval.focus();
			return false;
		}else if(isNull(document.forms[0].queryTimeout.value)){
			alert('Batch Update Query TimeOut must be specified.');
			document.forms[0].queryTimeout.focus();
			return false;
		}else if(document.forms[0].queryTimeout.value <= 0){
			alert('Batch Update Query TimeOut must be positive.');
			document.forms[0].queryTimeout.focus();
			return false;
		}else if(isEcNaN(document.forms[0].queryTimeout.value)){
			alert('Batch Update Query TimeOut must be Numeric');
			document.forms[0].queryTimeout.focus();
			return false;
		}
		return true;
	}

	function changeReportingType(){
		var reportingType = document.getElementById("reportingType").value;
		if(reportingType == '0') {
			document.getElementById("usageKeyFieldName").disabled = true;
			document.getElementById("inputOctetsFieldName").disabled = true;
			document.getElementById("outputOctetsFieldName").disabled = true;
			document.getElementById("totalOctetsFieldName").disabled = true;
			document.getElementById("usageTimeFieldName").disabled = true;			
		}else{
			document.getElementById("usageKeyFieldName").disabled = false;
			document.getElementById("inputOctetsFieldName").disabled = false;
			document.getElementById("outputOctetsFieldName").disabled = false;
			document.getElementById("totalOctetsFieldName").disabled = false;
			document.getElementById("usageTimeFieldName").disabled = false;
		}	
	}

	function setBatchUpdateOperation(){
		var isStoreAllCDR = document.getElementById("storeAllCDR").value;
		if(isStoreAllCDR == 'false') {
			document.getElementById("isBatchUpdate").disabled = true;
			document.getElementById("isBatchUpdate").value=false;
		}else{
			document.getElementById("isBatchUpdate").disabled = false;
			document.getElementById("isBatchUpdate").value=true;
		}
		setBatchUpdate();
	}
	
function setBatchUpdate() {
	var batchUpdate = document.getElementById("isBatchUpdate").value;
	if(batchUpdate == 'false') {
		document.getElementById("batchSize").disabled = true;
		document.getElementById("batchUpdateInterval").disabled = true;
		document.getElementById("queryTimeout").disabled = true;
	}else{
		document.getElementById("batchSize").disabled = false;
		document.getElementById("batchUpdateInterval").disabled = false;
		document.getElementById("queryTimeout").disabled = false;
	}	
}

function setColumnsOnTextFields(){
	var dbId = document.getElementById("databaseDSID").value;
	retrieveTableNames(dbId);
	retriveTableFields(dbId);
}

function retriveTableFields(dbId) {
	var dbFieldStr;
	var tableName = document.getElementById("tableName").value;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		
		setFields("identityField",dbFieldArray);
		setFields("sessionIDFieldName",dbFieldArray);
		setFields("createDateFieldName",dbFieldArray);
		setFields("lastModifiedFieldName",dbFieldArray);
		setFields("timeStampformat",dbFieldArray);
		setFields("usageKeyFieldName",dbFieldArray);
		setFields("inputOctetsFieldName",dbFieldArray);
		setFields("outputOctetsFieldName",dbFieldArray);
		setFields("totalOctetsFieldName",dbFieldArray);
		setFields("usageTimeFieldName",dbFieldArray);
		
		return dbFieldArray;
	});	
	
}

setTitle('<bean:message bundle="driverResources" key="driver.driver"/>');
</script>

<html:form action="/editDBCDRDriver" styleId="mainform">
<html:hidden name="dbCDRDriverForm" styleId="driverInstanceId" property="driverInstanceId"/>
	<table width="97%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" class="box">				 							 		
				 		<tr>
							<td class="tblheader-bold" valign="top" colspan="4" style="padding-left: 2em">
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.dbcdrdriverdetails" />
							</td>
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" width="40%">
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.datasource" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.datasource"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.datasource" />')"/>
							</td>
							<td align="left" valign="top" width="75%">
								<html:select property="databaseDSID" styleId="databaseDSID" style="width:225px">
									<html:option value="0">Select</html:option>
									<html:optionsCollection name="dbCDRDriverForm" property="databaseDSList" label="name" value="databaseId"/>
								</html:select><font color="#FF0000"> *</font>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" width="30%">
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.dbquerytimeout" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.querytimeout"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.dbquerytimeout" />')"/>																	
							</td>
							<td align="left"  valign="top">
								<html:text styleId="dbQueryTimeout" property="dbQueryTimeout" onkeypress="return isNumberKey(event);" size="2" maxlength="4"/>
							</td>															
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.querytimeoutcount" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.querytimeoutcount"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.querytimeoutcount" />')"/>																	
							</td>
							<td align="left" valign="top" >
								<html:text styleId="maxQueryTimeoutCount" property="maxQueryTimeoutCount" onkeypress="return isNumberKey(event);" size="3" maxlength="6"/>																																																	
							</td>									
						</tr>
									
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="padding-left: 2em">
							<bean:message bundle="driverResources" key="driver.dbcdrdriver.cdrdetails"/></td>
						</tr>
					
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.tablename" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.tablename"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.tablename" />')"/>																	
							</td>
							<td align="left" valign="top" colspan="3">
								<html:text styleId="tableName" property="tableName" onkeypress="setColumnsOnTextFields();" size="20" maxlength="30"/><font color="#FF0000"> *</font>
							</td>
						</tr>
						<tr>
						<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.identityfield" />
									<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.identityfield"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.identityfield" />')"/>																							
								</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="identityField" property="identityField" onkeypress="setColumnsOnTextFields();" size="20" maxlength="30"/><font color="#FF0000"> *</font>
							</td>	
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.sequencename" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.sequencename"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.sequencename" />')"/>																		
							</td>
							<td align="left" valign="top" >
								<html:text styleId="sequenceName" property="sequenceName" size="20" maxlength="30"/><font color="#FF0000"> *</font>
							</td>												
						</tr>		
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.storeallcdr" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.storeallcdr"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.storeallcdr" />')"/>																	
							</td>
							<td align="left" valign="top" colspan="3">
								<html:select property="storeAllCDR" styleId="storeAllCDR" onchange="setBatchUpdateOperation();" style="width: 70px">
									<html:option value="false">False</html:option>		
									<html:option value="true">True</html:option>																						
								</html:select>
							</td>							
						</tr>
					
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="padding-left: 2em">
							<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdate"/></td>
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top" width="25%">
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdate" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.batchupdate"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdate" />')"/>																	
							</td>
							<td align="left" valign="top" width="40%" >
								<html:select property="isBatchUpdate" styleId="isBatchUpdate" onchange="setBatchUpdate();" style="width: 80px">
									<html:option value="false">Disable</html:option>		
									<html:option value="true">Enable</html:option>																						
								</html:select>
							</td>														
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top" width="17%">
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchsize" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.batchsize"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchsize" />')"/>																	
							</td>
							<td align="left" valign="top" >
								<html:text styleId="batchSize" property="batchSize" onkeypress="return isNumberKey(event);" size="7" maxlength="4"/>
							</td>
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdateinterval" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.batchupdateinterval"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdateinterval" />')"/>																	
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="batchUpdateInterval" property="batchUpdateInterval" onkeypress="return isNumberKey(event);" size="7" maxlength="3"/>
							</td>							
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdatequerytimeout" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.querytimeout"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.batchupdatequerytimeout" />')"/>																	
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="queryTimeout" property="queryTimeout" onkeypress="return isNumberKey(event);" size="7" maxlength="4"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="padding-left: 2em">
							<bean:message bundle="driverResources" key="driver.dbcdrdriver.mandatoryfielddetails" /></td>
						</tr>						
						
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.sessionidfieldname" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.sessionidfieldname"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.sessionidfieldname" />')"/>																		
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="sessionIDFieldName" property="sessionIDFieldName" onblur="setColumnsOnTextFields();" size="20" maxlength="30"/><font color="#FF0000"> *</font>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.createdatefieldname" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.createdatefieldname"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.createdatefieldname" />')"/>																	
							</td>
							<td align="left" valign="top" >
								<html:text styleId="createDateFieldName" property="createDateFieldName" onblur="setColumnsOnTextFields();" size="20" maxlength="30"/><font color="#FF0000"> *</font>
							</td>														
						</tr>
						
						<tr>	
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.lastmodifiedfieldname" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.lastmodifiedfieldname"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.lastmodifiedfieldname" />')"/>
							</td>
							<td align="left" valign="top" >
								<html:text styleId="lastModifiedFieldName" property="lastModifiedFieldName" onblur="setColumnsOnTextFields();" size="20" maxlength="30"/><font color="#FF0000"> *</font>
							</td>												
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.timestampfieldname" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.timestampformat"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.timestampfieldname" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="timeStampformat" property="timeStampformat" onblur="setColumnsOnTextFields();" size="20" maxlength="128"/><font color="#FF0000"> *</font>
							</td>							
						</tr>
													
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="padding-left: 2em">
							<bean:message bundle="driverResources" key="driver.csvdriver.usagefields" /></td>
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.csvdriver.reportingtype" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.reportingtype"/>','<bean:message bundle="driverResources" key="driver.csvdriver.reportingtype" />')"/>						        
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:select property="reportingType" styleId="reportingType" onchange="changeReportingType();" style="width: 170px">
									<html:option value="0">--None--</html:option>	
									<html:option value="UM"><bean:message bundle="driverResources" key="driver.dbcdrdriver.reportingtype.usagemonitoring" /></html:option>
								</html:select>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.usagekeyheader" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.usagekeyheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.usagekeyheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="usageKeyFieldName" property="usageKeyFieldName" onblur="setColumnsOnTextFields();" size="15" maxlength="30"/>
							</td>							
						</tr>
												
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.inputoctetsheader" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.inputoctetsheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.inputoctetsheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="inputOctetsFieldName" property="inputOctetsFieldName" onblur="setColumnsOnTextFields();" size="15" maxlength="30"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.outputoctetsheader" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.outputoctetsheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.outputoctetsheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="outputOctetsFieldName" property="outputOctetsFieldName" onblur="setColumnsOnTextFields();" size="15" maxlength="30"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.totalOctetsheader" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.totalOctetsheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.totalOctetsheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="totalOctetsFieldName" property="totalOctetsFieldName" onblur="setColumnsOnTextFields();" size="15" maxlength="30"/>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" >
								<bean:message bundle="driverResources" key="driver.dbcdrdriver.usageTimeheader" />
								<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.usageTimeheader"/>','<bean:message bundle="driverResources" key="driver.csvdriver.usageTimeheader" />')"/>																     
							</td>
							<td align="left" valign="top"  colspan="3">
								<html:text styleId="usageTimeFieldName" property="usageTimeFieldName" onblur="setColumnsOnTextFields();" size="15" maxlength="30"/>
							</td>							
						</tr>		     		     					     			
		     							
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="4" style="padding-left: 2em">
							<bean:message bundle="driverResources" key="driver.dbcdrdriver.mappingdetails" /></td>
						</tr>													 
                   		<tr>                      		
                       		<td align="left" class="captiontext" valign="top" colspan="3" id="button">
                      		 <input type="button" id="add"  value=" Add Mapping" class="light-btn" style="size: 140px"></td>                      		                       		 
                   		</tr>   
                   		<tr>
				        	<td width="10" class="small-gap">&nbsp;</td>
				        	<td class="small-gap" colspan="2">&nbsp;</td>
    					</tr>
                   		<tr>
						   <td width="100%" colspan="4" valign="top" style="padding-left: 2em">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="mappingtable">
								<tr>																
									<td align="left" class="tblheaderfirstcol" valign="top" id="tbl_attrid">
										<bean:message bundle="driverResources" key="driver.csvdriver.pcrfkey" />
										<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.pcrfkey"/>','<bean:message bundle="driverResources" key="driver.csvdriver.pcrfkey" />')"/>										
									</td>
									<td align="left" class="tblheader" valign="top" id="tbl_attrid">
										<bean:message bundle="driverResources" key="driver.dbcdrdriver.dbfield" />
										<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.dbfield"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.dbfield" />')"/>										
									</td>
									<td align="left" class="tblheader" valign="top" id="tbl_attrid">
										<bean:message bundle="driverResources" key="driver.dbcdrdriver.datatype" />
										<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.datatype"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.datatype" />')"/>										
									</td>
									<td align="left" class="tblheader" valign="top" id="tbl_attrid">
										<bean:message bundle="driverResources" key="driver.dbcdrdriver.defvalue" />
										<img src="<%=cdrBasePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="driver.dbcdrdriver.defvalue"/>','<bean:message bundle="driverResources" key="driver.dbcdrdriver.defvalue" />')"/>										
									</td>																  									
									<td align="left" class="tblheaderlastcol" valign="top" width="5%">Remove</td>							  
								</tr>
								
								<logic:iterate id="mappingData" scope="request" name="dbCDRDriverForm"  property="dbcdrFieldMappingDataList" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRFieldMappingData">
									<tr> 
            							<td class="allborder" ><input class="plcKey" type="text" value="<%=mappingData.getPcrfKey()%>" name="pcrfKeyArray" maxlength="200"  style="width:100%"/></td>
            							<%if(mappingData.getDbField() != null){%>
            								<td class="tblrows" ><input class="noborder" type="text" value="<%=mappingData.getDbField()%>" name="dbFieldArray" maxlength="1000"  style="width:100%"/></td>
            							<%}else{%>
            								<td class="tblrows" ><input class="noborder" type="text" value="" name="dbFieldArray" maxlength="1000"  style="width:100%"/></td>
            							<%}%>            							
            							<td class="tblrows" >
            								<html:select styleClass="noborder" value='<%=String.valueOf(mappingData.getDataType())%>' name="dbCDRDriverForm" property="dataTypeArray" style="width: 100%;">
							 					<html:option value="5">String</html:option>
							 					<html:option value="4">Date</html:option>
											</html:select>
            							</td>
            							<%if(mappingData.getDefaultValue() != null){%>
            								<td class="tblrows" ><input class="noborder" type="text" value="<%=mappingData.getDefaultValue()%>" name="defaultValueArray" maxlength="1000" style="width:100%"/></td>
            							<%}else{%>
            								<td class="tblrows" ><input class="noborder" type="text" value="" name="defaultValueArray" maxlength="1000" style="width:100%"/></td>
            							<%}%>
            							<td class="tblrows" align="center"><img src="<%=cdrBasePath%>/images/minus.jpg" class="delete" height="15" onclick="removeMe()" /></td>            					
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
		    	<input type="button" value="Update" class="light-btn" onclick="validateForm()">                
				<input type="reset" onclick="javascript:window.location.href='<%=cdrBasePath%>/initSearchDriverInstance.do?'" value="Cancel" class="light-btn" tabindex="7"/>                                                       
			</td>
		</tr>
		
		<tr>
			<td align="left" class="labeltext" valign="top" colspan="4">&nbsp;</td>
		</tr>     
</table>
</html:form>