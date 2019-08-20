<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<script>
setTitle('<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig"/>');
$(document).ready(function() {
	retriveTableFields();
			
	/* bind click event of delete image */
	$('#wsKeyMappingTable td img.delete').live('click',function() {
				       				 $(this).parent().parent().remove(); 
				       			  });					          		
	});

/* array use for auto complete columns name */
var dbFieldArray = new Array();
function retriveTableFields() {
	var dbId = document.getElementById("databaseId").value;
	var tableName = document.getElementById("tableName").value;

	if($.trim(tableName).length != 0  && dbId != '0') {
	var dbFieldStr;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		data=data.trim();
		dbFieldStr = data.substring(1,data.length-1);
		dbFieldArray = dbFieldStr.split(", ");
			setAutoComplete();
	});	
	}
}
	
function setAutoComplete() {
	$(".fieldAutoComplete").autocomplete({
		source: dbFieldArray
	});
}

function addMapping(){
	var tableRowStr = $("#mappingTemplateTable").find("tr");
	$("#wsKeyMappingTable tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
	$("#wsKeyMappingTable tr:last").find("input:first").focus();
	setAutoComplete();
}


function validate()
{   
    if(document.forms[0].databaseId.value == '0'){
		document.forms[0].databaseId.focus();
		alert('Please Select Datasource');
	}else if(isNull(document.forms[0].tableName.value)){
    	document.forms[0].tableName.focus();
        alert('TableName must be specified');
    }else if(isNull(document.forms[0].userIdentityFieldName.value)){
    	 alert('User Identity Field must be specified');
        document.forms[0].userIdentityFieldName.focus();
    }else if(isNull(document.forms[0].recordFetchLimit.value)){
   		 alert('Record Fetch Limit must be specified');
     	document.forms[0].recordFetchLimit.focus();
 	}else if(!isNumber(document.forms[0].recordFetchLimit.value)){
  		 alert('Record Fetch Limit must be numeric');
      	document.forms[0].recordFetchLimit.focus();
  	}else if(!isEmptyById("sequenceName") && isEmptyById("primaryKeyColumn")){
  		 alert('Primary Key must be specified when Sequence Name specified.');
       	 document.forms[0].primaryKeyColumn.focus();
  	}else if(isEmptyById("sequenceName") && !isEmptyById("primaryKeyColumn")){
  		 alert('Sequence Name must be specified when Primary Key specified.');
       	 document.forms[0].sequenceName.focus();
  	}else if(!(isKeyMappingEmpty())){
  		alert('At least one Key mapping must be required');
  		document.forms[0].addButton.focus();
  	}else{
  		if(!isValidMapping()){
  			return false;
  		}
  		$("#updateSubscriberWSDatabaseConfigForm input[type='checkbox'][name='requestCheckBox']").each(function(){
  			var value = $(this).attr('checked');
  			$('#updateSubscriberWSDatabaseConfigForm').append('<input type="hidden" name="request" value='+value+' >');
  		});
  		$("#updateSubscriberWSDatabaseConfigForm input[type='checkbox'][name='responseCheckBox']").each(function(){
  			var value = $(this).attr('checked');
  			$('#updateSubscriberWSDatabaseConfigForm').append('<input type="hidden" name="response" value="'+value+'" >');
  		});
    	document.forms[0].submit();
	}
}
function isKeyMappingEmpty(){
	var keyMapping = $('#wsKeyMappingTable tr').length;
	if(keyMapping == 1){
		return false;
	}
	return true;
}
function isValidMapping(){
	
	var isValid = true;
	if(!isFieldExisInTable(document.forms[0].userIdentityFieldName.value)){
		alert("User Identity Column '"+document.forms[0].userIdentityFieldName.value+"' not exists in Table "+document.forms[0].tableName.value);
		$(this).focus();
	return false;
}
	
	if(!isFieldExisInTable(document.forms[0].primaryKeyColumn.value) && !isEmptyById("primaryKeyColumn")){
		alert("Primary Key Column '"+document.forms[0].primaryKeyColumn.value+"' not exists in Table "+document.forms[0].tableName.value);
		isValid = false;
		$(this).focus();
		return false;
	}

	
	$("#updateSubscriberWSDatabaseConfigForm input[name='wsKey']").each(function(){
		if(isEmpty($(this).val())){
			alert("WS Key must be specified in Key Mapping.");
			isValid = false;
			$(this).focus();
			return false;
								}
			       			  });					
	
	if(!isValid)
		return false;
	
	$("#updateSubscriberWSDatabaseConfigForm input[name='dbField']").each(function(index ,item){
		if(isEmpty(item.value)){
			alert("DB Field must be specified in Key Mapping.");
			isValid = false;
			$(this).focus();
			return false;
			         	}	         				    		         			   				         			          				          		
		if(!isFieldExisInTable(item.value) && item.value != "*"){
			alert("DB Field Column '"+item.value+"' not exists in Table "+document.forms[0].tableName.value);
			isValid = false;
			$(this).focus();
			return false;
		         	}		         								
        	
	});
	return isValid;
}	

function isFieldExisInTable(value){
	return ($.inArray(value,dbFieldArray) >= 0);
}
</script>

<html:form action="/updateSubscriberWSDatabaseConfig" styleId="updateSubscriberWSDatabaseConfigForm" >
	<html:hidden property="action" value="update"/>
	<html:hidden property="wsConfigId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header">
										<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.update" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" cellSpacing="0" cellPadding="0" border="0">
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.datasource" /> 
													<ec:elitehelp headerBundle="webServiceConfigResources" text="webservice.dbconfig.datasource" 
													header="webservice.dbconfig.datasource"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%" colspan="3">
													<bean:define id="lstDatasource" name="updateSubscriberWSDatabaseConfigForm" property="lstDatasource"></bean:define> 
													<html:select name="updateSubscriberWSDatabaseConfigForm" styleId="databaseId" property="databaseId" tabindex="1" size="1" onchange="retriveTableFields();">
														<html:option value="0">
															<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.select" />
														</html:option>
														<html:options collection="lstDatasource" property="databaseId" labelProperty="name" />
													</html:select>
													<font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.tablename" /> 
													<ec:elitehelp headerBundle="webServiceConfigResources" text="webservice.dbconfig.tablename" 
													header="webservice.dbconfig.tablename" />
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
													<html:text tabindex="2" styleId="tableName" property="tableName" size="40" maxlength="2000" onblur="retriveTableFields();" style="width:250px" /> 
													<font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.uidfieldname" /> 
													<ec:elitehelp headerBundle="webServiceConfigResources" text="webservice.dbconfig.uidfieldname" 
													header="webservice.dbconfig.uidfieldname" />
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
													<html:text styleId="userIdentityFieldName" tabindex="3" property="userIdentityFieldName" size="40" maxlength="30"  style="width:250px" styleClass="fieldAutoComplete" /> 
													<font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.recordfetchlimit" />
													<ec:elitehelp headerBundle="webServiceConfigResources" 
													text="webservice.dbconfig.recordfetchlimit" 
													header="webservice.dbconfig.recordfetchlimit"/> 
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
													<html:text styleId="recordFetchLimit" tabindex="4" property="recordFetchLimit" size="15" maxlength="10" style="width:250px" /> 
													<font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.primarykeycolumn" /> 
													<ec:elitehelp headerBundle="webServiceConfigResources" 
													text="webservice.dbconfig.primarykeycolumn" 
													header="webservice.dbconfig.primarykeycolumn"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
													<html:text styleId="primaryKeyColumn" tabindex="5" property="primaryKeyColumn" size="40" maxlength="30"  style="width:250px" styleClass="fieldAutoComplete" /> 
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="18%">
													<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.sequencename" />
													<ec:elitehelp headerBundle="webServiceConfigResources" 
													text="webservice.dbconfig.sequencename" 
													header="webservice.dbconfig.sequencename" /> 
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
													<html:text styleId="sequenceName" tabindex="6" property="sequenceName" size="40" maxlength="30"  style="width:250px" /> 
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" colspan="3" valign="top">&nbsp;</td>
											</tr>
											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="3">
													<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.keymap" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" colspan="2">
													<input id="addButton" type="button" onclick="addMapping()" value=" Add " tabindex="7" class="light-btn">
												</td>
											</tr>
											<tr>
												<td colspan="3">
													<!--  attribute table -->
													<table cellpadding="0" cellspacing="0" border="0" width="98%" class="captiontext">
														<tr>
															<td align="left" class="labeltext" colspan="2" valign="top" width="100%">
																<table width="65%"  cellpadding="0" cellspacing="0" border="0" id="wsKeyMappingTable">
																	<tr>
																		<td align="left" class="tblheader" valign="top" width="20%">
																			<bean:message bundle="webServiceConfigResources" 
																			key="webservice.dbconfig.wskey" />
																			<ec:elitehelp headerBundle="webServiceConfigResources" 
																			text="webservice.dbconfig.wskey" 
																			header="webservice.dbconfig.wskey" /> 
																		</td>
																		<td align="left" class="tblheader" valign="top" width="20%">
																			<bean:message bundle="webServiceConfigResources" 
																			key="webservice.dbconfig.dbfield" /> 
																			<ec:elitehelp headerBundle="webServiceConfigResources" 
																			text="webservice.dbconfig.dbfield" 
																			header="webservice.dbconfig.dbfield"/>
																		</td>
																		<td align="left" class="tblheader" valign="top" width="10%">
																			<bean:message bundle="webServiceConfigResources" 
																			key="webservice.dbconfig.request" /> 
																			<ec:elitehelp headerBundle="webServiceConfigResources" 
																			text="webservice.dbconfig.request" 
																			header="webservice.dbconfig.request" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="10%">
																			<bean:message bundle="webServiceConfigResources" 
																			key="webservice.dbconfig.response" /> 
																			<ec:elitehelp headerBundle="webServiceConfigResources" 
																			text="webservice.dbconfig.response" 
																			header="webservice.dbconfig.response"/>
																		</td>
																		<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
											</tr>

																	<logic:iterate id="mappingData" name="updateSubscriberWSDatabaseConfigForm" property="wsKeyMappingList" type="com.elitecore.elitesm.datamanager.wsconfig.data.WSKeyMappingData">
											<tr class="tr-rows">
																			<td class="allborder"><input type="text" name="wsKey" value='<bean:write name="mappingData" property="wsKey"/>' class="noborder" style="width:100%"/></td>
																			<td class="tblrows"><input type="text" name="dbField" value='<bean:write name="mappingData" property="dbField"/>' class="fieldAutoComplete noborder" style="width:100%" /></td>
																			<td class="tblrows">
																				<logic:equal value="true" name="mappingData" property="request">
																					<input type="checkbox" name="requestCheckBox" checked="checked" class="noborder" style="width:100%" />
																				</logic:equal>
																				<logic:notEqual value="true" name="mappingData" property="request">
																					<input type="checkbox" name="requestCheckBox"  class="noborder" style="width:100%" />
																				</logic:notEqual>	
															</td>
																			<td class="tblrows">
																				<logic:equal value="true" name="mappingData" property="response">
																					<input type="checkbox" name="responseCheckBox" checked="checked" class="noborder" style="width:100%" />
																				</logic:equal>
																				<logic:notEqual value="true" name="mappingData" property="response">
																					<input type="checkbox" name="responseCheckBox"  class="noborder" style="width:100%" />
																				</logic:notEqual>	
															</td>
																			<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
														</tr>
																	</logic:iterate>
													</table>
												</td>
											</tr>
													</table>
												</td>
											</tr>
											
											
											<tr>
												<td align="left" class="labeltext" colspan="3" valign="top">
													&nbsp;</td>
											</tr>
											<tr>
												<td class="captiontext" colspan="3">
													<input tabindex="9" type="button" name="c_btnUpdate" onclick="validate();" id="c_btnCreate2" value="   Update   " class="light-btn">&nbsp;&nbsp;
													<input type="reset" tabindex="10" name="c_btnCancel" onclick="javascript:location.href='initSubscriberWSDatabaseConfig.do'" value=" Cancel " class="light-btn">&nbsp;&nbsp; 
													<input type="button" tabindex="11" name="c_btnRefresh" onclick="javascript:location.href='initSubscriberWSDatabaseConfig.do?action=refresh'" value=" Refresh " class="light-btn">
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<table style="display:none;" id="mappingTemplateTable">
			<tr>
		<td class="allborder"><input type="text" name="wsKey"  class="noborder" style="width:100%"/></td>
		<td class="tblrows"><input type="text" name="dbField"  class="fieldAutoComplete noborder" style="width:100%" /></td>
		<td class="tblrows"><input type="checkbox" name="requestCheckBox"  class="noborder" style="width:100%" checked="checked" /></td>
		<td class="tblrows"><input type="checkbox" name="responseCheckBox" class="noborder" style="width:100%" checked="checked"/></td>
		<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
			</tr>
		</table>

