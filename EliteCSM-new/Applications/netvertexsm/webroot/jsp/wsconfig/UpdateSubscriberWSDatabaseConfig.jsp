<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@page import="com.elitecore.netvertexsm.web.wsconfig.sprmgmt.forms.SubscriberWSDatabaseConfigForm"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData"%>

<%
	SubscriberWSDatabaseConfigForm subscriberWSDatabaseConfigForm = (SubscriberWSDatabaseConfigForm)request.getAttribute("subscriberWSDatabaseConfigForm");
%>


<script>

function setColumnsOnTextFields(){
	var dbId = document.getElementById("databaseId").value;
	retriveTableFields(dbId);
}

function setColumnsForTables(){
	setColumnsOnTextFields();
}

function retriveTableFields(dbId) {
	var dbFieldStr;
	var tableName = document.getElementById("tableName").value;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		setFields("field",dbFieldArray);
		setFields("userIdentityFieldName",dbFieldArray);
		setFields("dbfield",dbFieldArray);
		return dbFieldArray;
	});	
	
}

$(document).ready(function(){
	setTitle('<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig"/>');
	$("#databaseId").focus();	
	if(document.getElementById("tempLogicalFieldName").value.trim().length<1){
		makeDefaultSelect();
	}	
});


function validate() {  
    if(isNull(document.forms[0].databaseId.value)){		
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
  	}else if(isNull(document.forms[0].primaryKeyColumn.value)){
  		 alert('Primary Key Column must be specified');
      	 document.forms[0].primaryKeyColumn.focus();
  	}else if(isNull(document.forms[0].sequenceName.value)){
  		 alert('Sequence Name must be specified');
      	 document.forms[0].sequenceName.focus();
  	}else if(document.forms[0].usageMonitoringDatabaseId.value == '0'){
  		alert('Please Select Usage Monitoring Database Datasource');
		document.forms[0].dynaSprDatabaseId.focus();		
	}else{
    	 document.forms[0].checkAction.value = 'Update';
       	 document.forms[0].submit();
	}
}

function validateName(val)
{
	var test1 = /(^[A-Za-z0-9-_]*$)/;
	var regexp =new RegExp(test1);
	if(regexp.test(val)){
		return true;
	}
	return false;
}
function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}

function makeDefaultSelect(){	
	var mySelect = document.getElementById('logicalFieldName');  
	var options = mySelect.options; // this returns the option element array	
	for(var i = 0; i < options.length; i++){  
	    var val = options[i].text;  
	      if(val == 'IMSI'){                   
	             options[i].selected ="selected";  
	             break;  
	      }  
	}
}

</script>
<html:form action="/subscriberWSDatabaseConfig" >
   	<html:hidden property="checkAction"/>
	<html:hidden property="wsConfigId"/>
   	<input type="hidden" name="tempLogicalFieldName" id="tempLogicalFieldName" value="<bean:write name="subscriberWSDatabaseConfigForm"  property="subscriberIdentity"/>"/>
	<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
  		<tr>
  			<td width="10" class="small-gap">&nbsp;</td> 
		 	<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box" colspan="2">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr> 
						<td colspan="3">
							<table width="97%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" cellSpacing="0" cellPadding="0" border="0">
								<tr> 
					          		<td class="tblheader-bold" colspan="4" height="20%" style=""><bean:message bundle="webServiceConfigResources" key="webservice.sprconfig" /></td>
					          	</tr>
					          	
	
								<tr>
									<td align="left" class="labeltext" valign="top" width="35%">
										<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.datasource" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.dbconfig.datasource"/>','<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.datasource" />')"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="32%" colspan="3">
										<bean:define id="lstDatasource" name="subscriberWSDatabaseConfigForm" property="lstDatasource"></bean:define>
										<html:select name="subscriberWSDatabaseConfigForm" styleId="databaseId" property="databaseId" size="1" onchange="setColumnsOnTextFields();">
											<html:option value="">
											<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.select" />
											</html:option>
											<html:options collection="lstDatasource" property="databaseId" labelProperty="name" />
										</html:select><font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" >
										<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.tablename" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.dbconfig.tablename"/>','<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.tablename" />')"/>
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" >
										<html:text styleId="tableName" property="tableName" size="40" maxlength="30" onblur="setColumnsForTables();" style="width:250px"/> <font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" >
										<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.uidfieldname" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.dbconfig.uidfieldname"/>','<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.uidfieldname" />')"/>
									</td>
										<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="userIdentityFieldName" property="userIdentityFieldName" size="40" maxlength="30" onfocus="setColumnsOnTextFields();" style="width:250px"/> <font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" >
										<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.recordfetchlimit" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.dbconfig.recordfetchlimit"/>','<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.recordfetchlimit" />')"/>
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="recordFetchLimit" property="recordFetchLimit" size="15" maxlength="10" style="width:250px"/> <font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" >
										<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.primarykeycolumn" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.dbconfig.primarykeycolumn"/>','<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.primarykeycolumn" />')"/>
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" >
										<html:text styleId="primaryKeyColumn" property="primaryKeyColumn" size="40" maxlength="30" onblur="setColumnsForTables();" style="width:250px"/> <font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" >
										<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.sequencename" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.dbconfig.sequencename"/>','<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.sequencename" />')"/>
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" >
										<html:text styleId="sequenceName" property="sequenceName" size="40" maxlength="30" onblur="setColumnsForTables();" style="width:250px"/> <font color="#FF0000"> *</font>
									</td>
								</tr>
							
								
								<tr> 
					          		<td class="tblheader-bold" colspan="4" height="20%" style=""><bean:message bundle="webServiceConfigResources" key="webservice.bodcdrconfig" /></td>
					          	</tr>		
					          	
					          	
							  	<tr>
									<td align="left" class="labeltext" valign="top" width="25%">
										<bean:message bundle="webServiceConfigResources" key="webservice.bodcdrdriverid" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.bodcdrdriverid"/>','<bean:message bundle="webServiceConfigResources" key="webservice.bodcdrdriverid" />')"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="32%" colspan="3">
										<bean:define id="driverData" name="subscriberWSDatabaseConfigForm" property="driverInstanceDatas"></bean:define>
										<html:select name="subscriberWSDatabaseConfigForm" styleId="bodCDRDriverId" property="bodCDRDriverId" size="1">
											<html:option value="">
											<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.select" />
											</html:option>
											<html:options collection="driverData" property="driverInstanceId" labelProperty="name" />
										</html:select>
									</td>
								</tr>
								<!-- <tr> 
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
							  	</tr> -->
							  	<tr> 
					          		<td class="tblheader-bold" colspan="4" height="20%" style=""><bean:message bundle="webServiceConfigResources" key="webservice.dynasprconfig" /></td>
					          	</tr>
					          	<!-- <tr>
									<td align="left" class="labeltext" colspan="3" valign="top">&nbsp;</td>
								</tr> -->
								<tr>
									<td align="left" class="labeltext" valign="top" width="25%">
										<bean:message bundle="webServiceConfigResources" key="webservice.dynasprdatabaseds" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.dynasprdatabaseds"/>','<bean:message bundle="webServiceConfigResources" key="webservice.dynasprdatabaseds" />')"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="32%" colspan="3">
										<html:select name="subscriberWSDatabaseConfigForm" styleId="dynaSprDatabaseId" property="dynaSprDatabaseId" size="1" >
											<html:option value="0">
												<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.select" />
											</html:option>
											<html:options collection="lstDatasource" property="databaseId" labelProperty="name" />
										</html:select>
									</td>
								</tr>
								
								
								<tr> 
					          		<td class="tblheader-bold" colspan="4" height="20%" style="">
					          			<bean:message bundle="webServiceConfigResources" key="webservice.usagemonitoringconfiguration" />
					          		</td>
					          	</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="25%">
										<bean:message bundle="webServiceConfigResources" key="webservice.usagemonitoringdatabaseds" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" 
										onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.usagemonitoringdatabaseds"/>',
										'<bean:message bundle="webServiceConfigResources" key="webservice.usagemonitoringdatabaseds" />')"/>
									</td>

									<td align="left" class="labeltext" valign="top" width="32%" colspan="3">
										<html:select name="subscriberWSDatabaseConfigForm" styleId="usageMonitoringDatabaseId" property="usageMonitoringDatabaseId" size="1" >
											<html:option value="0">
												<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.select" />
											</html:option>
											<html:options collection="lstDatasource" property="databaseId" labelProperty="name" />
										</html:select><font color="#FF0000"> *</font>
									</td>
								</tr>
								
							  	<tr> 
					          		<td class="tblheader-bold" colspan="4" height="20%" style=""><bean:message bundle="webServiceConfigResources" key="webservice.authenticate.api" /></td>
					          	</tr>
					          	<tr>
									<td align="left" class="labeltext" colspan="3" valign="top">&nbsp;</td>
								</tr>							  	
								<tr>
									<td align="left" class="labeltext" valign="top" width="25%">
										<bean:message bundle="webServiceConfigResources" key="webservice.subscriberidentity" />	
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="webservice.subscriberidentity"/>','<bean:message bundle="webServiceConfigResources" key="webservice.subscriberidentity" />')"/>									
									</td>
									<td align="left" class="labeltext" valign="top" width="32%" colspan="3">										
										<html:select name="subscriberWSDatabaseConfigForm" styleId="subscriberIdentity" property="subscriberIdentity" size="1"  >											
											<logic:iterate id="logicalName" name="dbFieldNameSet" >																				
												<html:option value="<%=logicalName.toString()%>"><bean:write name="logicalName"/></html:option>
											</logic:iterate>																						
										</html:select>
									</td>
								</tr>							  	
					          	<tr>
									<td align="left" class="labeltext" colspan="3" valign="top">&nbsp;</td>
								</tr>							  	
								<tr>
									<td align="center" valign="middle" colspan="3">
										<input type="button" name="c_btnUpdate" onclick="validate();" id="c_btnCreate2" value="   Update   " class="light-btn">&nbsp;&nbsp; 
										<input type="reset" name="c_btnCancel"  onclick="javascript:location.href='serverGroupManagement.do?method=initSearch'" value=" Cancel " class="light-btn">&nbsp;&nbsp;
										<input type="button" name="c_btnRefresh"  onclick="javascript:location.href='initSubscriberWSDatabaseConfig.do?checkAction=refresh'" value=" Refresh " class="light-btn">
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" colspan="3" valign="top">&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
	</table>
</html:form>
<script>
setColumnsOnTextFields();    
</script>


