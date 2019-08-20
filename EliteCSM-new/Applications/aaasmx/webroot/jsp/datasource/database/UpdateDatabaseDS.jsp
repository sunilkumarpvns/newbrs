<%@page
	import="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData"%>
<%@page
	import="com.elitecore.elitesm.web.datasource.database.forms.UpdateDatabaseDSDetailForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>

<%
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
	}else if(isNull(document.forms[0].timeout.value)){
	    document.forms[0].timeout.focus();
		alert('Timeout  must be specified');
    }else if(!(isNumber(document.forms[0].timeout.value))){
       document.forms[0].timeout.focus();
       alert('Timeout value  must be Numeric');
    }else if(isNull(document.forms[0].statusCheckDuration.value)){
	    document.forms[0].statusCheckDuration.focus();
		alert('Status Check Duration  must be specified');
    }else if(!(isNumber(document.forms[0].statusCheckDuration.value))){
       document.forms[0].statusCheckDuration.focus();
       alert('Status Check Duration value  must be Numeric');
    }else if(isNull(document.forms[0].maximumPool.value)){
	    document.forms[0].maximumPool.focus();
		alert('MaximumPool  must be specified');
    }else if(!(isNumber(document.forms[0].maximumPool.value))){
       document.forms[0].maximumPool.focus();
       alert('MaximumPool value  must be Numeric');
    }else if(isNull(document.forms[0].minimumPool.value)){
	    document.forms[0].minimumPool.focus();
		alert('MinimumPool  must be specified');
    }else if(!(isNumber(document.forms[0].minimumPool.value))){
       document.forms[0].minimumPool.focus();
       alert('MinimumPool value  must be Numeric');
    }else if(minPoolSize > maxPoolSize){
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

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DATABASE_DATASOURCE%>',searchName,'update','<%=iDatabaseDSData.getDatabaseId()%>','verifyNameDiv');
}
</script>

<html:form action="/updateDatabaseDS">

	<html:hidden name="updateDatabaseDSDetailForm" styleId="action"
		property="action" value="update" />
	<html:hidden name="updateDatabaseDSDetailForm" styleId="databaseId"
		property="databaseId" />
	<html:hidden name="updateDatabaseDSDetailForm" styleId="auditUId"
		property="auditUId" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%"
		align="right">
		<tr>
			<td class="box" valign="middle" colspan="2">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="30%">
					<tr>
						<td class="tblheader-bold" colspan="3"><bean:message
								bundle="datasourceResources"
								key="database.datasource.updateDatabaseDatasource" /></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="datasourceResources" key="database.datasource.datasourcename" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="dbdatasource.name" 
							header="database.datasource.datasourcename" /> 
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2"
							width="82%"><html:text styleId="name" tabindex="1"
								onkeyup="verifyName();" property="name" size="25" maxlength="50"
								style="width:250px" /> <font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="datasourceResources" key="database.datasource.connectionurl" />
							<ec:elitehelp headerBundle="datasourceResources" text="dbdatasource.url" 
							header="database.datasource.connectionurl"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2"
							width="82%">	
							<html:textarea property="connectionUrl" rows="3" cols="3" styleId="connectionUrl" tabindex="2" style="width:85%"></html:textarea> <font color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="datasourceResources" key="database.datasource.username" /> 
							<ec:elitehelp headerBundle="datasourceResources" 
							text="dbdatasource.username" header="database.datasource.username" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2"
							width="82%"><html:text styleId="userName" tabindex="3"
								property="userName" size="25" maxlength="50" style="width:250px" />
							<font color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="datasourceResources" key="database.datasource.password" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="dbdatasource.password" 
							header="database.datasource.password" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:password styleId="password" tabindex="4"
								property="password" size="25" maxlength="50" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="datasourceResources" key="database.datasource.timeout" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="dbdatasource.timeout" 
							header="database.datasource.timeout" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="timeout" tabindex="5" property="timeout"
								size="25" maxlength="50" style="width:250px" /> <font
							color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="datasourceResources" key="database.datasource.statuscheckduration" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="dbdatasource.statuscheckduration" 
							header="database.datasource.statuscheckduration"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="statusCheckDuration" tabindex="6"
								property="statusCheckDuration" size="25" maxlength="50"
								style="width:250px" /> <font color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="datasourceResources" key="database.datasource.minimumPool" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="dbdatasource.minconnection" 
							header="database.datasource.minimumPool" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2"
							width="82%"><html:text styleId="minimumPool" tabindex="7"
								property="minimumPool" size="25" maxlength="50"
								style="width:250px" /> <font color="#FF0000"> *</font></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="datasourceResources" key="database.datasource.maximumPool" /> 
							<ec:elitehelp headerBundle="datasourceResources" text="dbdatasource.maxconnection" 
							header="database.datasource.maximumPool" />	
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2"
							width="82%"><html:text styleId="maximumPool" tabindex="8"
								property="maximumPool" size="25" maxlength="50"
								style="width:250px" /> <font color="#FF0000"> *</font></td>
					</tr>

					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2"><input
							type="button" tabindex="9" name="c_btnCreate"
							onclick="validateUpdate()" id="c_btnCreate2" value="Update"
							class="light-btn"> <input type="reset" tabindex="10"
							name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/initSearchDatabaseDS.do?/>'"
							value="Cancel" class="light-btn"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>

