<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.*"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.driver.radius.forms.UpdateRadiusDBAuthDriverForm"%>

<%
		
		DriverInstanceData driverInstanceData = (DriverInstanceData)request.getSession().getAttribute("driverInstanceData");
%>

<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/radius-dbauth-driver.js"></script>

<script>

$(document).ready(function() {
	enableAll();
	setFieldSuggestion();
	
	var logicalNameData =eval(('<bean:write name="logicalNameData"/>').replace(new RegExp("&quot;", 'g'),"\""));
	/* set Logical Data JsonObject */
	setLogicalNameData(logicalNameData);
	
	/* set Logical Name drop down for default value */ 
	setLogicalnameDropDown("mappingtbl","logicalnmVal",true);
	
	/* bind click event of delete image */
	$('#mappingtbl td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
		 setLogicalnameDropDown("mappingtbl","logicalnmVal",true); /* set Logical Name drop down after remove row */
	});
	setAction("update");
});	


var isValidName = true;
function verifyName() {
	var searchName = document.getElementById("driverinstname").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
}

function validateName(){
	if(isNull(document.forms[0].driverinstname.value)){
		alert('Name must be specified');
		document.forms[0].driverinstname.focus();
		return false;
	}else if(!isValidName) {
 		alert('Enter Valid Driver Name');
 		document.forms[0].driverinstname.focus();
 		return false;
 	}
	return true;
}

</script>
<html:form action="/updateRadiusDBAuthDriver">

	<html:hidden property="action" value="update" />
	<html:hidden property="itemIndex" />
	<html:hidden property="driverInstanceId" />
	<html:hidden property="auditUId" styleId="auditUId" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="15%">

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources"
								key="driver.driverinstancedetails" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="23%">
							<bean:message bundle="driverResources" key="driver.name" /> 
							<ec:elitehelp headerBundle="driverResources" 
							text="createdriver.name" header="driver.name"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap"><html:text
								styleId="driverinstname" onkeyup="verifyName();"
								property="driverInstanceName" size="30" maxlength="60"
								style="width:250px" tabindex="1" /><font color="#FF0000">
								*</font>
							<div id="verifyNameDiv" class="labeltext"></div></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.description" />
							<ec:elitehelp headerBundle="driverResources" 
							text="createdriver.description" header="driver.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								styleId="driverinstdesc" property="driverInstanceDesc" size="30"
								maxlength="60" style="width:250px" tabindex="2" /></td>
					</tr>



					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources" key="driver.driverdetails" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.dbauthdriver.ds" /> 
							<ec:elitehelp headerBundle="driverResources" 
							text="dbauthdriver.ds" header="driver.dbauthdriver.ds"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<html:select property="databaseId" styleId="databaseId"
								style="width:30%" onchange="setFieldSuggestion()"
								tabindex="3">
								<html:option value="0">Select</html:option>
								<html:optionsCollection name="updateRadiusDBAuthDriverForm"
									property="databaseDSList" label="name" value="databaseId" />
							</html:select>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.tablename" /> 
							<ec:elitehelp headerBundle="driverResources" 
							text="dbauthdriver.tblname" header="driver.tablename"/>						
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								styleId="tableName" property="tableName" size="30"
								maxlength="128" style="width:250px" tabindex="4" /><font
							color="#FF0000"> *</font></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.querytimeout" /> 
							<ec:elitehelp headerBundle="driverResources" 
							text="dbauthdriver.dbquerytimeout" header="driver.querytimeout"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								styleId="querytimeout" property="dbQueryTimeout" size="20"
								maxlength="10" style="width:250px" tabindex="5" /><font
							color="#FF0000"> *</font></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.querytimeoutcount" />
							<ec:elitehelp headerBundle="driverResources" 
							text="dbauthdriver.maxquerytimeout" header="driver.querytimeoutcount"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								styleId="timeoutcount" property="maxQueryTimeoutCount" size="20"
								maxlength="10" style="width:250px" tabindex="6" /><font
							color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.profilelookupcolumn" />
							<ec:elitehelp headerBundle="driverResources" 
							text="dbauthdriver.profilelookup" header="driver.profilelookupcolumn"/>
						</td>
						<td align="left" class="labeltext" valign="top"><html:text
								styleId="profileLookupColumn" property="profileLookupColumn"
								size="30" maxlength="30" style="width:250px" tabindex="7" /><font
							color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.useridentityattributes" /> 
							<ec:elitehelp headerBundle="driverResources" 
							text="driver.usridentity" header="driver.useridentityattributes"/>
						</td>
						<td align="left" class="labeltext" valign="top"><input
							type="text" name="userIdentityAttributes"
							id="userIdentityAttributes" size="20" maxlength="256"
							autocomplete="off" onkeyup="setColumnsOnUserIdentity();"
							style="width: 250px" tabindex="8"
							value="<bean:write name="updateRadiusDBAuthDriverForm" property="userIdentityAttributes"/>" />
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="6">
							<bean:message bundle="driverResources"
								key="driver.dbauthdriver.cacheparameterdetails" />
						</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top"></td>
						<td align="left" class="labeltext" valign="top"><html:checkbox
								styleId="cacheable" property="cacheable" onclick="enableAll();"
								tabindex="9"></html:checkbox>
								<bean:message bundle="driverResources" key="driver.cacheable" /> 
								<ec:elitehelp headerBundle="driverResources" 
								text="dbauthdriver.cacheable" header="driver.cacheable"/>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="27%">
							<bean:message bundle="driverResources" key="driver.primarykeycolumn" /> 
							<ec:elitehelp headerBundle="driverResources" 
							text="dbauthdriver.primarykeycolumn" header="driver.primarykeycolumn"/>
						</td>
						<td align="left" class="labeltext" valign="top"><html:text
								styleId="primaryKeyColumn" property="primaryKeyColumn" size="20"
								maxlength="30" style="width:250px" tabindex="10" /></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="27%">
							<bean:message bundle="driverResources" key="driver.sequencename" /> 
							<ec:elitehelp headerBundle="driverResources" 
							text="dbauthdriver.sequencename" header="driver.sequencename"/>
						</td>
						<td align="left" class="labeltext" valign="top"><html:text
								styleId="sequenceName" property="sequenceName" size="20"
								maxlength="30" style="width:250px" tabindex="11" /></td>
					</tr>

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources" key="driver.fieldmap" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="2"><input
							type="button" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);' value=" Add "
							class="light-btn" tabindex="12"></td>
					</tr>

				</table>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td colspan="2" valign="top" width="97%">
							<table width="98%" id="mappingtbl" cellpadding="0"
								cellspacing="0" border="0" class="captiontext">
								<tr>
									<td align="left" class="tblheader" valign="top">
										<bean:message bundle="driverResources" key="driver.logicalname" />
										<ec:elitehelp headerBundle="driverResources" 
										text="dbauthdriver.logicalname" header="driver.logicalname"/>
									</td>
									<td align="left" class="tblheader" valign="top">
										<bean:message bundle="driverResources" key="driver.dbfield" />
										<ec:elitehelp headerBundle="driverResources" 
										text="dbauthdriver.dbattribute" header="driver.dbfield"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="15%">
										<bean:message bundle="driverResources" key="driver.defaultvalue" /> 
										<ec:elitehelp headerBundle="driverResources" 
										text="dbauthdriver.defaultvalue" header="driver.defaultvalue"/>
									</td>
									<td align="left" class="tblheader" valign="top">
										<bean:message bundle="driverResources" key="driver.valuemapping" />
										<ec:elitehelp headerBundle="driverResources" 
										text="dbauthdriver.valuemapping" header="driver.valuemapping"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
								</tr>
								<logic:iterate id="obj" name="dbFieldMapList" type=" com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData">
								<tr>
									<td class="allborder">
										<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
											<option value='<bean:write name="obj" property="nameValuePoolData.value"/>'><bean:write name="obj" property="nameValuePoolData.name"/> </option>
										</select>
									</td>
									<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="10" onfocus="setAutoCompleteData(this);" value='<bean:write name="obj" property="dbField"/>' /></td>
									<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10" value='<bean:write name="obj" property="defaultValue"/>' /></td>
									<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10" value='<bean:write name="obj" property="valueMapping"/>' /></td>
									<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
								</tr>
								</logic:iterate>
							</table>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle"><input type="button"
							name="c_btnCreate" id="c_btnCreate2" value=" Update "
							class="light-btn" onclick="validateForm()" tabindex="13">
							<input type="reset" name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
							value="Cancel" class="light-btn" tabindex="14"></td>
					</tr>
				</table>

			</td>
		</tr>


	</table>
	
</html:form>

<!-- Mapping Table Row template -->
	<table style="display:none;" id="dbMappingTable">
		<tr>
			<td class="allborder">
				<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
				</select>
			</td>
			<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="10" onfocus="setAutoCompleteData(this);"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10"/></td>
			<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
  		</tr>
	</table>