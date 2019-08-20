<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.datasource.database.form.UpdateDatabaseDSDetailForm"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.netvertexsm.datamanager.datasource.database.data.IDatabaseDSData"%>

<%
   String localBasePath = request.getContextPath();
   UpdateDatabaseDSDetailForm updateDatabaseDSDetailForm=(UpdateDatabaseDSDetailForm)request.getAttribute("updateDatabaseDSDetailForm");
   IDatabaseDSData iDatabaseDSData = (IDatabaseDSData)request.getAttribute("databaseDSData");
%>


<script>
var isValidName;
function validateUpdate()
{
	 var minPoolSize=parseInt(document.forms[0].minimumPool.value);
	 var maxPoolSize=parseInt(document.forms[0].maximumPool.value);
	  
	if(isNull(document.forms[0].name.value)){
		document.forms[0].name.focus();
		alert('DataSourceName must be specified');
	}else if(!isValidName) {
 			alert('Enter Valid DataSource Name');
 			document.forms[0].name.focus();
 			return;
 	}else if(isNull(document.forms[0].connectionUrl.value)){
		document.forms[0].connectionUrl.focus();
		alert('ConnectionUrl must be specified');
	}else if(isNull(document.forms[0].userName.value)){
		document.forms[0].userName.focus();
		alert('UserName must be specified');
	}else if(isNull(document.forms[0].password.value)){
		document.forms[0].password.focus();
		alert('Password must be specified');
	}/* else if(isNull(document.forms[0].timeout.value)){
	    document.forms[0].timeout.focus();
		alert('Timeout  must be specified');
    }else if(!(isNumber(document.forms[0].timeout.value))){
       document.forms[0].timeout.focus();
       alert('Timeout value  must be Numeric');
    } */else if(isNull(document.forms[0].statusCheckDuration.value)){
	    document.forms[0].statusCheckDuration.focus();
		alert('Status Check Duration  must be specified');
    }else if(!(isNumber(document.forms[0].statusCheckDuration.value))){
       document.forms[0].statusCheckDuration.focus();
       alert('Status Check Duration value  must be Numeric');
    }
    else if(isNull(document.forms[0].minimumPool.value)){
	    document.forms[0].minimumPool.focus();
		alert('MinimumPool  must be specified');
    }else if(!(isNumber(document.forms[0].minimumPool.value))){
       document.forms[0].minimumPool.focus();
       alert('MinimumPool value  must be non negative numeric value');
    }else if((minPoolSize > 0) ==false ){
        document.forms[0].minimumPool.focus();
        alert('MinimumPool value  must be greater than 0');
    }

    else if(isNull(document.forms[0].maximumPool.value)){
	    document.forms[0].maximumPool.focus();
		alert('MaximumPool  must be specified');
    }else if(!(isNumber(document.forms[0].maximumPool.value))){
       document.forms[0].maximumPool.focus();
       alert('MaximumPool value  must be non negative numeric value');
    }else if((maxPoolSize > 0) ==false ){
        document.forms[0].maximumPool.focus();
        alert('MaximumPool value  must be greater than 0');
    } else if(minPoolSize > maxPoolSize){
       document.forms[0].minimumPool.focus();
       alert('MinimumPool value must be less than MaximumPool value');
    }
    else{
    	document.forms[0].submit();
	}
    

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

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.DATABASE_DATASOURCE%>',searchName:searchName,mode:'update',id:'<%=iDatabaseDSData.getDatabaseId()%>'},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	
	if(searchName == "<%=ConfigConstant.NETVERTEX_SERVER_DB%>"){
		$('#verifyNameDiv').html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Reserved for internal purpose.</font>");
		isValidName = false;
	}else{
		isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.DATABASE_DATASOURCE%>',searchName:searchName,mode:'update',id:'<%=iDatabaseDSData.getDatabaseId()%>'},'verifyNameDiv');
	}
	
}
$(document).ready(function(){
	verifyName();
});
</script>

<html:form action="/updateDatabaseDS">

<html:hidden name="updateDatabaseDSDetailForm" styleId="action" property="action" value="update"/>
<html:hidden name="updateDatabaseDSDetailForm" styleId="databaseId" property="databaseId"/>  

		<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
					
					<tr>
						<td class="small-gap" colspan="2">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td class="box" valign="middle" colspan="2">
							<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
						        <tr>
									<td class="tblheader-bold" colspan="3">
										<bean:message bundle="datasourceResources" key="database.datasource.updateDatabaseDatasource" />
									</td>
						       </tr> 			
								<tr>
									<td align="left" class="labeltext" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="database.datasource.datasourcename" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="datasourceResources" key="database.datasource.datasourcename"/>','<bean:message bundle="datasourceResources" key="database.datasource.datasourcename" />')"/>
									</td>
									<sm:nvNameField maxLength="50" size="25" value="${updateDatabaseDSDetailForm.name}"/>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="database.datasource.connectionurl" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="connectionUrl" property="connectionUrl" size="50" maxlength="2000" tabindex="2" />
										<font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="database.datasource.username" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="userName" property="userName" size="25" maxlength="50" tabindex="3" />
										<font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="database.datasource.password" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<html:password styleId="password" property="password" size="25" maxlength="50" tabindex="4" />
										<font color="#FF0000"> *</font>
									</td>
								</tr>
								
								<!-- <tr>
									<td align="left" class="labeltext" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="database.datasource.timeout" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<html:text styleId="timeout" property="timeout" size="25" onkeypress="return isNumberKey(event);" maxlength="10" tabindex="5" />
										<font color="#FF0000"> *</font>
									</td>
								</tr> -->
								
								<tr>
									<td align="left" class="labeltext" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="database.datasource.statuscheckduration" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<html:text styleId="statusCheckDuration" property="statusCheckDuration" onkeypress="return isNumberKey(event);" size="25" maxlength="20" tabindex="6" />
										<font color="#FF0000"> *</font>
									</td>
								</tr>
																							
								<tr>
									<td align="left" class="labeltext" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="database.datasource.minimumPool" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="minimumPool" property="minimumPool" onkeypress="return isNumberKey(event);" size="25" maxlength="10" tabindex="7" />
										<font color="#FF0000"> *</font>
									</td>
								</tr>
								
								<tr>
									<td align="left" class="labeltext" valign="top" width="30%">
										<bean:message bundle="datasourceResources" key="database.datasource.maximumPool" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="maximumPool" property="maximumPool" onkeypress="return isNumberKey(event);" size="25" maxlength="10" tabindex="8" />
										<font color="#FF0000"> *</font>
									</td>
								</tr>
								
								<tr>
									<td class="btns-td" valign="middle">
										&nbsp;
									</td>
									<td class="btns-td" valign="middle" colspan="2">
										<input type="button" name="c_btnCreate" onclick="validateUpdate()" id="c_btnCreate2" value="Update" class="light-btn" tabindex="9">
										<input type="reset" tabindex="10" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/initSearchDatabaseDS.do?/>'" value="Cancel" class="light-btn"> 
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
</html:form>

