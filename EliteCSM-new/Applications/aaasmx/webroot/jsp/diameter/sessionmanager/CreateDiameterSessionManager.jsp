<%@page import="com.sun.org.apache.xalan.internal.xsltc.compiler.sym"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData"%>
<%@page import="com.elitecore.elitesm.util.constants.ExternalSystemConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants" %>
<%@page import="com.elitecore.elitesm.web.diameter.sessionmanager.form.CreateDiameterSessionManagerForm" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData" %>
<%@ page import="java.util.*"%>
<%@page import="com.elitecore.coreradius.commons.util.constants.RadiusConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData"%>
<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping" %>
<%@page import="com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData" %>
<%@page import="com.elitecore.aaa.diameter.conf.sessionmanager.DiameterSessionManagerConfigurable.DBFailureActions" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%
	String basePath = request.getContextPath();
	List<IDatabaseDSData> lstdatasource = (List<IDatabaseDSData>) request.getAttribute("lstDatasource");
	CreateDiameterSessionManagerForm createDiameterSessionManagerForm = (CreateDiameterSessionManagerForm)request.getAttribute("createDiameterSessionManagerForm");
	List<DiameterSessionManagerData> diameterSessionManagerDataList = createDiameterSessionManagerForm.getDiameterSessionMappingDataList();
	Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingDatasSet = createDiameterSessionManagerForm.getDiameterSessionManagerMappingDataSet();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript"> 
var isValidName;
var mappingIndex=0;

function validateCreate(){
     
   		if(isNull(document.forms[0].name.value)){
			document.forms[0].name.focus();
			alert('Session Manager Name must be specified');
		}else if(!isValidName) {
			alert('Enter Valid Name');
			document.forms[0].name.focus();
			return false;
		}else if(document.forms[0].databaseId.value == '0'){
			alert('Please Select Datasource');	
			document.forms[0].databaseId.focus();
		}else if(isNull(document.forms[0].tablename.value)){
			alert('Table name must be specified');
			document.forms[0].tablename.focus();
			return false;
		}else if(isNull(document.forms[0].sequenceName.value)){
			alert('Sequence name must be specified');
			document.forms[0].sequenceName.focus();
			return false;
		}else if(isNull(document.forms[0].startTimeField.value)){
			alert('Start Time Field must be specified');
			document.forms[0].startTimeField.focus();
			return false;
		}else if(isNull(document.forms[0].lastUpdatedTimeField.value)){
			alert('Last Update Time Field must be specified');
			document.forms[0].lastUpdatedTimeField.focus();
			return false;
		}else if(isNull(document.forms[0].dbQueryTimeOut.value)){
			alert('DB Query Timeout must be specified');
			document.forms[0].dbQueryTimeOut.focus();
			return false;
		}else if(!isValidMapping()){
			return false;
		}else if(!isValidFieldMappings()){
			return false;	
		}else if(!isValidReferringAttributes()){
			return false;	
		}else if(!isvalidBatchMode()){
			return false;			
		}else{
			 addMappingIndex();
		   	 document.forms[0].action.value='Create';
		     document.forms[0].submit();
		}
}
function isvalidBatchMode(){
	var batchOperationCounter=0;
	$('.batchOperations').each(function(){
		if($(this).is(':checked')){
			batchOperationCounter ++ ;
		}
	});
	
	if(batchOperationCounter > 0){
		return true;
	}else{
		if($('#batchEnabled').val() == "true"){
			alert('select at least one Batch Operations');
			return false;
		}else{
			return true;
		}
	}
}
function isValidFieldMappings(){
	var isValidFieldMapping = true;
	$('.dbFieldMappingTable').find('.dbFieldName').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == 0) {
			alert("DB Field Name must be Specified");
			isValidFieldMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidFieldMapping;
}
function isValidReferringAttributes(){
	var isValidFieldMapping = true;
	$('.dbFieldMappingTable').find('.referringAttribute').each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == 0) {
			alert("Referring Attributes must be Specified");
			isValidFieldMapping = false;
			$(this).focus();
			return false;
		}
	});
	return isValidFieldMapping;
}

function isValidMapping() {
	var isValidMapping = true;
	/* validate mapping name */
	$(".mappingNameClass").each(function(){
		var nameValue = $.trim($(this).val());
		if(nameValue.length == 0) {
			alert("Mapping Name must be Specified");
			isValidMapping = false;
			$(this).focus();
			return false;
		}
	});
	
	if(!isValidMapping){
		return false;
	}
	/* validate unique name */
	if(!isUnique("mappingNameClass")) {
		alert("Mapping Name should be unique for each DB Fields Mapping");
		return false;
	}
	
	return isValidMapping;
}

function isUnique(className) {
	$class = $("."+className);
	var arrayData = new Array($class.length);
	var arrayIndex = 0;
	
	$class.each(function(){
		var nameValue = $.trim($(this).val());
		arrayData[arrayIndex++] = nameValue;
		$(this).val(nameValue);
	});
	
	for(var i=0; i<arrayData.length; i++ ) {
		for(var j=i+1; j<arrayData.length; j++ ){
			if(arrayData[i] == arrayData[j]) {
				$class[i].focus();
				return false;
			}
		}
	}
	return true;
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_SESSION_MANAGER%>',searchName,'create','','verifyNameDiv');
}

$(document).ready(function(){
	$('.dbFieldMappingTable td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
	});
	
	$("#batchedDelete").attr("checked","checked");
	
	<%if(diameterSessionManagerMappingDatasSet !=null){
	for(DiameterSessionManagerMappingData diMappingData : diameterSessionManagerMappingDatasSet){%>
		mappingIndex++;
		var defaultTemplateValue = '<%=diMappingData.getMappingName()%>';
		$.ajax({
			   type: "POST",
			   url: "<%=basePath%>/jsp/diameter/sessionmanager/CreateTemplateDBFieldMappings.jsp",
			   async:false,
			   data: {
				   	mappingIndexParam :mappingIndex,
				   	defaultTemplateValue : defaultTemplateValue
			   },	
			   success : function(response){
				   	handleMappingResponse(response);
			   }
		 });
		<% if(diMappingData.getSessionManagerFieldMappingData() !=null){
			for(SessionManagerFieldMappingData fieldMappingData : diMappingData.getSessionManagerFieldMappingData()){%>
					var dbFieldName='<%=fieldMappingData.getDbFieldName()%>';
					var referringAttribute='<%=fieldMappingData.getReferringAttr()%>';
					var dataType='<%=fieldMappingData.getDataType()%>';
					var defaultValue='<%=fieldMappingData.getDefaultValue()%>';
					var htmlSource=
						"<tr><td class='allborder'><input  autocomplete='off' class='dbFieldName noborder' type='text' value='"+dbFieldName+"' name='dbFieldName"+mappingIndex+"' id='dbFieldName"+mappingIndex+"'  maxlength='1000' size='28' style='width:100%' onkeyup='setDbFieldsMapping(this);'/></td>"+
						"<td class='tblrows'><input  class='referringAttribute noborder' type='text' name='referringAttribute"+mappingIndex+"' value='"+referringAttribute+"' id='referringAttribute"+mappingIndex+"' maxlength='1000' size='28' style='width:100%' onkeyup='setReferringAttributes(this);' /></td>"+
						"<td class='tblrows'><select name='dataType"+mappingIndex+"' id='dataType"+mappingIndex+"' size='1' value='"+dataType+"' style='width: 100%;'>"+
						"<option value='<%=Integer.toString(FieldMapping.STRING_TYPE)%>'>String</option>"+
						"<option value='<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>'>Timestamp</option>"+
						"</select></td><td class='tblrows'><input class='noborder' type='text' name='defaultValue"+mappingIndex+"' value='"+defaultValue+"' id='defaultValue"+mappingIndex+"' maxlength='1000' size='30' style='width:100%'/></td>"+
						"<td class='tblrows' align='center' colspan='3'><img value='top' src='<%=request.getContextPath()%>/images/minus.jpg'  class='delete' style='padding-right: 5px; padding-top: 5px;' height='14'  /></td></tr>";
						$('#dbFieldMappingTable'+mappingIndex).append(htmlSource);
						setColumnsOnTextFields();
		<%}
		}%>
	<%}}%>
	
	if(mappingIndex == 0){
		addSessionManagerMappings();
	}
	setColumnsOnTextFields();
	$('#name').focus();
});

function addSessionManagerMappings() {
	mappingIndex++;
	
	$.ajax({
		   type: "POST",
		   url: "<%=basePath%>/jsp/diameter/sessionmanager/AddDBFieldMappings.jsp",
		   async:false,
		   data: {
			   	mappingIndexParam :mappingIndex
		   },	
		   success : function(response){
			   	handleMappingResponse(response);
		   }
	 });
	
}

function addSessionManagerMappingTemplate(tableObjectIds,defaultTemplateId){
	var defaultTemplateValue = $('#' + defaultTemplateId).val();
	
	if(defaultTemplateValue == "NONE"){
		alert('Please Select template from Dropdown');
		$('#' + defaultTemplateId).focus();
		return false;
	}
	
	if(defaultTemplateValue == 'Radius'){
		$.ajax({
			   type: "POST",
			   url: "<%=basePath%>/jsp/diameter/sessionmanager/AddRadiusTemplate.jsp",
			   async:false,
			   data: {
				   	mappingIndexParam :mappingIndex,
				   	defaultTemplateValue : defaultTemplateValue
			   },	
			   success : function(response){
				   	addTemplateRows(tableObjectIds,response);
			   }
		 });
	}else{
		$.ajax({
			   type: "POST",
			   url: "<%=basePath%>/jsp/diameter/sessionmanager/AddDBFieldsTemplate.jsp",
			   async:false,
			   data: {
				   	mappingIndexParam :mappingIndex,
				   	defaultTemplateValue : defaultTemplateValue
			   },	
			   success : function(response){
					addTemplateRows(tableObjectIds,response);
			   }
		 });
	}
}

function addTemplateRows(tableObject,responseData){
	$('#' + tableObject + " > tbody:last").append(responseData);
	setColumnsOnTextFields();
}
	
function handleMappingResponse(response){
	var div = document.getElementById("dbFieldMappingDiv");
	$(div).append(response);
	 var tableId = "table"+mappingIndex;
	var mappingNameId = "mappingName"+mappingIndex;
	setMappingLabeIndex(); 
	setColumnsOnTextFields();
}
function setMappingLabeIndex(){
	$(".mappingLabel").each(function(index, item){
		var mapIndex = index+1;
		$(this).text("Mapping-"+mapIndex);
	});
}
function addDbFieldMapping(mappingTable,templateTableId){
	var tableRowStr = $("#"+templateTableId).find("tr");
	$("#"+mappingTable+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#"+mappingTable+" tr:last").find("input:first").focus();
	setColumnsOnTextFields();
}
function removeComponent(compId){
	$(compId).remove();
	setMappingLabeIndex();
}

function toggleMappingDiv(mappingDivIndex){
	  var imgElement = document.getElementById("toggleImageElement"+mappingDivIndex);
	  if ($("#toggleDivElement"+mappingDivIndex).is(':hidden')) {
          imgElement.src="<%=request.getContextPath()%>/images/top-level.jpg";
     } else {
          imgElement.src="<%=request.getContextPath()%>/images/bottom-level.jpg";
     }
    $("#toggleDivElement"+mappingDivIndex).slideToggle("fast");
}

function retriveTableFieldsForSessionManager(dbId) {
	var dbFieldStr;
	var dbFieldArray = new Array();
	var tableName = document.getElementById("tablename").value;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		data=data.trim();
		dbFieldStr = data.substring(1,data.length-1);
		dbFieldArray = dbFieldStr.split(", ");
		setDBFields("lastUpdatedTimeField",dbFieldArray);
		setDBFields("startTimeField",dbFieldArray);
		setDBFields("dbFieldName",dbFieldArray);
	});	
	return dbFieldArray;
}

function setDBFields(field,columnArray) {
	$('.'+ field).autocomplete({
		source: columnArray
	});
}
function retriveDiameterDictionaryAttributesForSessionManager() {
	var myArray = new Array();
	var dbFieldStr;
	var dbFieldArray;
	var searchNameOrAttributeId="";
	$.post("SearchDiameterAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
		data=data.trim();
		dbFieldStr = data.substring(1,data.length-1);
		dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split("#,");
		var value;
		var label;
		var desc;
		for(var i=0;i<dbFieldArray.length;i++) {
			tmpArray = dbFieldArray[i].split(",");
			value = tmpArray[0].trim();
			label = tmpArray[1];
			var item = new ListItem(value,label); 
			myArray.push(item);
		}	
		
		setDiameterDictionaryReferringAttrib("referringAttribute",myArray);
		return dbFieldArray;
	});
}
function splitReferringAttribute( val ) {
	return val.split( /[,;(]\s*/ );
}
function extractLastReferringAttribute( term ) {
	return splitReferringAttribute( term ).pop();
} 
function setDiameterDictionaryReferringAttrib(txtField,myArray) {
	$('.referringAttribute').autocomplete({	
			source:function( request, response ) {
				response( $.ui.autocomplete.filter(
						myArray, extractLastReferringAttribute( request.term ) ) );
			},
				
			focus: function( event, ui ) {
				return false;
			},
			select: function( event, ui ) {
				var val = this.value;
				var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
				var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
				var bracketIndex=val.lastIndexOf("(") == -1 ? 0 :val.lastIndexOf("(");
				 if(commaIndex == semiColonIndex && bracketIndex == commaIndex ) {
						val = "";
				}else if(commaIndex > semiColonIndex) {
					if(commaIndex < bracketIndex){
						val = val.substring(0,bracketIndex+1);
					}else{
						val = val.substring(0,commaIndex+1); 
					}
				}else if(bracketIndex>commaIndex){
					val = val.substring(0,bracketIndex+1);
				} else {
					val = val.substring(0,semiColonIndex+1);
				}
				this.value = val + ui.item.value ;
				return false;
			}
		});		
}

function setColumnsOnTextFields(){
	var dbId = document.getElementById("databaseId").value;
	retriveTableFieldsForSessionManager(dbId);
	retriveDiameterDictionaryAttributesForSessionManager();
}
function addMappingIndex(){
	$("#sessionManagerForm").append("<input type='hidden' name='mappingIndex' value='"+mappingIndex+"' />");
}

setTitle('<bean:message bundle="sessionmanagerResources" key="sessionmanager.header"/>');
</script>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="3">
										<bean:message bundle="sessionmanagerResources" key="sessionmanager.sessionmanagerconfig" />
									</td>
								</tr>
								<tr>
									<td colspan="3">
									<html:form action="/createDiameterSessionManagerDetails" styleId="sessionManagerForm">
										<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" class="tblheader table-header" valign="top" colspan="4">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.basicdetails" /> 
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.name" />
													<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.name" header="sessionmanager.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text styleId="name" property="name" onkeyup="verifyName();" size="25" maxlength="50" style="width:250px" name="createDiameterSessionManagerForm" tabindex="1" />
													<font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.description" />
													<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.desc" header="sessionmanager.description"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:textarea styleId="description" property="description"  rows="3" cols="40"  name="createDiameterSessionManagerForm" tabindex="2" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.datasource" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.datasource" header="sessionmanager.datasource"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:select property="databaseId" styleId="databaseId" name="createDiameterSessionManagerForm" onchange="setColumnsOnTextFields();" style="width:250px;" tabindex="3">
														<html:option value="0">--Select--</html:option>
														<html:options collection="lstDatasource" property="databaseId" labelProperty="name" name="createDiameterSessionManagerForm" />
													</html:select>
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.tablename" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.tablename" header="sessionmanager.tablename"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text styleId="tablename" property="tablename" size="25" maxlength="30" style="width:250px" onblur="setColumnsOnTextFields();" name="createDiameterSessionManagerForm" tabindex="4" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.sequencename" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.sequencename" header="diameter.sessionmanager.sequencename"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text styleId="sequenceName" property="sequenceName" size="25" maxlength="50" style="width:250px" name="createDiameterSessionManagerForm" tabindex="5" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.starttimefield" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.starttimefield" header="diameter.sessionmanager.starttimefield"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text styleId="startTimeField" property="startTimeField" styleClass="startTimeField" size="25" maxlength="30" style="width:250px" name="createDiameterSessionManagerForm" tabindex="6" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.lastupdatetimefield" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.lastupdatetimefield" header="diameter.sessionmanager.lastupdatetimefield"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text styleId="lastUpdatedTimeField" property="lastUpdatedTimeField" styleClass="lastUpdatedTimeField" size="25" maxlength="30" style="width:250px" name="createDiameterSessionManagerForm" tabindex="7" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.dbquerytimeout" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.dbquerytimeout" header="diameter.sessionmanager.dbquerytimeout"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text styleId="dbQueryTimeOut" property="dbQueryTimeOut" size="25" maxlength="5" style="width:100px" name="createDiameterSessionManagerForm" tabindex="8" />
													<font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.multivaluedelimeter" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.multivaluedelimeter" header="diameter.sessionmanager.multivaluedelimeter"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text styleId="multiValueDelimeter" property="multiValueDelimeter" size="25" maxlength="5" style="width:100px" name="createDiameterSessionManagerForm" tabindex="9" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.dbfailureaction" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.dbfailureaction" header="diameter.sessionmanager.dbfailureaction"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:select name="createDiameterSessionManagerForm" styleId="dbFailureAction" property="dbFailureAction" style="width:100px" tabindex="10">
														<logic:iterate id="dbFailureActionInst"  collection="<%=DBFailureActions.values() %>"> 
															<%String displayText=((DBFailureActions)dbFailureActionInst).name(); %>
															<html:option value="<%=displayText%>"><%=displayText%></html:option>
														</logic:iterate>
													</html:select>
												</td>
												</tr>
											<tr>
												<td align="left" class="tblheader table-header" valign="top" colspan="4">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchmode" /> 
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchmode" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.batchmode" header="diameter.sessionmanager.batchmode"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:select property="batchEnabled" styleId="batchEnabled" name="createDiameterSessionManagerForm" style="width:100px" tabindex="11">
														<html:option value="true">Enabled</html:option>
														<html:option value="false">Disabled</html:option>
													</html:select>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchsize" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.batchsize" header="diameter.sessionmanager.batchsize"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text property="batchSize" styleId="batchSize" name="createDiameterSessionManagerForm" maxlength="5" style="width:100px;" tabindex="12"></html:text>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchinterval" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.batchinterval" header="diameter.sessionmanager.batchinterval"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text property="batchInterval" styleId="batchInterval" name="createDiameterSessionManagerForm" maxlength="4" style="width:100px;" tabindex="13"></html:text>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchquerytimeout" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.batchquerytimeout" header="diameter.sessionmanager.batchquerytimeout"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:text property="batchQueryTimeout" styleId="batchQueryTimeout" name="createDiameterSessionManagerForm" style="width:100px;" tabindex="14"></html:text>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchoperation" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="diameter.sessionmanager.batchoperation" header="diameter.sessionmanager.batchoperation"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="75%" colspan="3">
													<html:checkbox property="batchedInsert" styleId="batchedInsert" styleClass="batchOperations" name="createDiameterSessionManagerForm" tabindex="15"><bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchinsert" /></html:checkbox> 
													<html:checkbox property="batchedUpdate" styleId="batchedUpdate" styleClass="batchOperations" name="createDiameterSessionManagerForm" tabindex="16"><bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchupdate" /></html:checkbox> 
													<html:checkbox property="batchedDelete" styleId="batchedDelete" styleClass="batchOperations" name="createDiameterSessionManagerForm" value="true" tabindex="17"><bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.batchdelete"/></html:checkbox> 
												</td>
											</tr>
											<tr>
												<td align="left" class="tblheader table-header" valign="top" colspan="4">
													<bean:message bundle="sessionmanagerResources" key="diameter.sessionmanager.fieldmappings" /> 
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" style="padding-top: 10px;" colspan="4">
													<input type="button" name="c_btnAddMapping" onclick="addSessionManagerMappings();" id="btnAddMapping" value="Add Mappings" class="light-btn" tabindex="18"/>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" colspan="4">
													<div id="dbFieldMappingDiv"></div>
												</td>
											</tr>
										</table>
									</html:form>
								<!-- end of table -->
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" colspan="3" valign="top">
										&nbsp;</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle"></td>
									<td class="btns-td" valign="middle"><input type="button" name="c_btnCreate"
										onclick="validateCreate();"
										id="c_btnCreate2" value=" Next " class="light-btn"
										tabindex="52"> <input type="reset"
										name="c_btnDeletePolicy"
										onclick="javascript:location.href='<%=basePath%>/searchDiameterSessionManager.do?/>'"
										value="Cancel" class="light-btn" tabindex="53"></td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>