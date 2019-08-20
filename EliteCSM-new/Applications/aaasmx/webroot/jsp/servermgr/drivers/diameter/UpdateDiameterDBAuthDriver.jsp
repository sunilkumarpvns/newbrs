<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.*"%>
<%@ page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>

<%
		DriverInstanceData driverInstanceData = (DriverInstanceData)request.getSession().getAttribute("driverInstance");
%>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script>
$(document).ready(function() {
	
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
	
	setFieldSuggestion();
	enableAll();
	
});	
function validateForm(){	

		if(isNull(document.forms[0].driverinstname.value)){
		alert('Name must be specified');
		}else if(!isValidName) {
			alert('Enter Valid Driver Name');
			document.forms[0].driverinstname.focus();
			return;
		}else if(document.forms[0].databaseId.value == 0){
 			alert('Select atleast one datasource value ');
 		}else if(isNull(document.forms[0].tableName.value)){
 			alert('Table Name must be specified.');
 		}else if(isNull(document.forms[0].querytimeout.value)){
 			document.forms[0].querytimeout.focus();
 			alert('DB Query Timeout must be specified.');
 		}else if(!isPositiveNumber(document.forms[0].querytimeout.value)){
 			document.forms[0].querytimeout.focus();
 			alert('DB Query Timeout must be zero or positive number.');
 		}else if(isNull(document.forms[0].timeoutcount.value)){
 			document.forms[0].timeoutcount.focus();
 			alert('Maximum Query Timeout Count must be specified.');
 		}else if(!isPositiveNumber(document.forms[0].timeoutcount.value)){
 			document.forms[0].timeoutcount.focus();
 			alert('Maximum Query Timeout Count must be zero or positive number.');
 		}else if(isNull(document.forms[0].profileLookupColumn.value)){
 	 		alert('Profile Lookup Column must be specified.');
 	 		document.forms[0].profileLookupColumn.focus();
 		}else if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 2){
 	 		alert('At least one mapping must be there.');
 		}else{ 	 		
 			if(isValidLogicalNameMapping("mappingtbl","logicalnmVal","dbfieldVal")){
				document.forms[0].action.value = 'update';
				document.forms[0].submit();
			} 	 		
 		}		
	
	}
	
var isValidName = true;
function verifyName() {
	var searchName = document.getElementById("driverinstname").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
}

function setColumnsOnUserIdentity(){
	var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
	retriveDiameterDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
}
function setFieldSuggestion(){
	$(retriveTableFields(document.getElementById("databaseId").value,document.getElementById("tableName").value,"dbfieldVal"));	
	$(retriveTableFields(document.getElementById("databaseId").value,document.getElementById("tableName").value,"profileLookupColumn"));	
}
function  setAutoCompleteData(dbFieldObject){
	$(dbFieldObject).autocomplete({
	source : $( "#dbfieldVal" ).autocomplete( "option", "source" ),
});
	
function setAutoCompleteDataOnProfileLookup(profileLookupColumnObj){
	$(profileLookupColumnObj).autocomplete({
		source : $( "#profileLookupColumn" ).autocomplete( "option", "source" ),
	});
}
}
</script>
<html:form action="/updateDiameterDBAuthDriver">

	<html:hidden property="action" value="update" />
	<html:hidden property="itemIndex" />
	<html:hidden property="driverInstanceId" />
	<html:hidden property="auditUId" styleId="auditUId"/>

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
						<td align="left" class="captiontext" valign="top" width="24%">
							<bean:message bundle="driverResources" key="driver.name" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.name" header="driver.name"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								tabindex="1" styleId="driverinstname" onkeyup="verifyName();"
								property="driverInstanceName" size="30" maxlength="60"
								style="width:250px" /><font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources"
								key="driver.description" />
									<ec:elitehelp headerBundle="driverResources" 
										text="createdriver.description" header="driver.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								tabindex="2" styleId="driverinstdesc"
								property="driverInstanceDesc" size="30" maxlength="60"
								style="width:250px" /></td>
					</tr>



					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources" key="driver.driverdetails" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources"
								key="driver.dbauthdriver.ds" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="dbauthdriver.ds" header="driver.dbauthdriver.ds"/> 
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:select
								tabindex="3" property="databaseId" styleId="databaseId" onchange="setFieldSuggestion()"
								style="width:200px">
								<html:option value="0">Select</html:option>
								<html:optionsCollection name="updateDiameterDBAuthDriverForm"
									property="databaseDSList" label="name" value="databaseId" />
							</html:select></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources"
								key="driver.tablename" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="dbauthdriver.tblname" header="driver.tablename"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								tabindex="4" styleId="tableName" property="tableName" size="30"
								maxlength="128" style="width:250px" /><font color="#FF0000">
								*</font></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources"
								key="driver.querytimeout" /> 
									<bean:message
										key="general.seconds" /> 
											<ec:elitehelp headerBundle="driverResources" 
												text="dbauthdriver.dbquerytimeout" header="driver.querytimeout"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								tabindex="5" styleId="querytimeout" property="dbQueryTimeout"
								size="20" maxlength="10" style="width:250px" /><font
							color="#FF0000"> *</font></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources"
								key="driver.querytimeoutcount" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="dbauthdriver.maxquerytimeout" header="driver.querytimeoutcount"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%"><html:text
								tabindex="6" styleId="timeoutcount"
								property="maxQueryTimeoutCount" size="20" maxlength="10"
								style="width:250px" /><font color="#FF0000"> *</font></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources"
								key="driver.useridentityattributes" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="driver.usridentity" header="driver.useridentityattributes"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<input type="text" tabindex="7" name="userIdentityAttributes"
							id="userIdentityAttributes" size="60" maxlength="256"
							autocomplete="off" onkeyup="setColumnsOnUserIdentity();"
							style="width: 250px"
							value="<bean:write name="updateDiameterDBAuthDriverForm" property="userIdentityAttributes"/>" />
						</td>
					</tr>
					
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.profilelookupcolumn" />
							<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.profilelookup" header="driver.profilelookupcolumn"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%">
							<html:text styleId="profileLookupColumn" property="profileLookupColumn" size="30" maxlength="30" style="width:250px" tabindex="5" onfocus="setAutoCompleteDataOnProfileLookup(this);" />
							<font color="#FF0000">*</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="driverResources" key="driver.fieldmap" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="2"><input
							type="button" tabindex="8"  onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);' value=" Add "
							class="light-btn"></td>
					</tr>

				</table>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td align="left" class="labeltext" colspan="2" valign="top"
							width="98%">
							<table width="97%" id="mappingtbl" cellpadding="0"
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
										<bean:message bundle="driverResources"
											key="driver.defaultvalue" />
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
											<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  id="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="10" onfocus="setAutoCompleteData(this);" value='<bean:write name="obj" property="dbField"/>' onfocus="setAutoCompleteData(this);" /></td>
											<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10"  value='<bean:write name="obj" property="defaultValue"/>'/></td>
											<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10" value='<bean:write name="obj" property="valueMapping"/>' /></td>
											<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
										</tr>
								</logic:iterate>
							</table>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle"><input tabindex="9"
							type="button" name="c_btnCreate" id="c_btnCreate2"
							value=" Update " class="light-btn" onclick="validateForm()">
							<input type="reset" tabindex="10" name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
							value="Cancel" class="light-btn"></td>
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
			<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal" id="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="10" onfocus="setAutoCompleteData(this);"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10"/></td>
			<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
  		</tr>
	</table>

