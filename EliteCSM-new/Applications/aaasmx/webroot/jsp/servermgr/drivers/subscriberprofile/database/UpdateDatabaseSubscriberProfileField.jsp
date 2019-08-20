




<%@page
	import="com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.UpdateDatabaseSubscriberProfileFieldForm"%>
<script>
function deleteSubmit(index)
{   
	document.forms[0].action.value = 'Remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();	
}

function addnode(){
	document.forms[0].action.value = 'addDetail';
	document.forms[0].submit();
}

function validateUpdate(){
	
		document.forms[0].action.value='update';	
		alert('If Field Name or Field Type or Field Length is/are blank then corresponding row would not be considered...');
		document.forms[0].submit();
	
}
function  checkAll(){
	
	var arrayCheck = document.getElementsByName('select');
 	if( document.forms[0].toggleAll.checked == true) {
	 	for (i = 0; i < arrayCheck.length;i++)
			arrayCheck[i].checked = true ;
	} else if (document.forms[0].toggleAll.checked == false){
			for (i = 0; i < arrayCheck.length; i++)
				arrayCheck[i].checked = false ;
	}
}
</script>

<%
	int index = 0;
	int j = 0;
	UpdateDatabaseSubscriberProfileFieldForm updateDatabaseSubscriberProfileFieldForm = (UpdateDatabaseSubscriberProfileFieldForm) request.getAttribute("updateDatabaseSubscriberProfileFieldForm");
%>

<html:form action="/updateDatabaseSubscriberProfileField">
	<html:hidden name="updateDatabaseSubscriberProfileFieldForm"
		styleId="action" property="action" />
	<html:hidden name="updateDatabaseSubscriberProfileFieldForm"
		styleId="dbAuthId" property="dbAuthId" />
	<html:hidden name="updateDatabaseSubscriberProfileFieldForm"
		styleId="driverInstanceId" property="driverInstanceId" />
	<html:hidden name="updateDatabaseSubscriberProfileFieldForm"
		styleId="itemIndex" property="itemIndex" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="table-header"></td>
		</tr>
		<tr>
			<td valign="top" align="right">

				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="15%">

					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="6"
							width="97%"><bean:message bundle="driverResources"
								key="subscriberprofile.database.upade.fielddetail" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2"
							width="5%"><input type="button" value="   Add   "
							property="addNode" onclick="addnode()" class="light-btn" /></td>
						<td align="left" class="labeltext" valign="top" colspan="2"
							width="92%"><input type="button" value="Remove" property=""
							onclick="deleteSubmit()" class="light-btn" /></td>
					</tr>
					<tr>
						<td align="center" class="tblheader" valign="top" width="2%">
							<html:checkbox styleId="toggleAll" property="toggleAll"
								value="checkbox" onclick="checkAll()" />
						</td>

						<td align="center" class="tblheader" valign="top" width="5%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.serialnumber" />
						</td>
						<td align="left" class="tblheader" valign="top" width="30%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.displayname" />
						</td>
						<td align="left" class="tblheader" valign="top" width="30%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.fieldname" />
						</td>

						<td align="left" class="tblheader" valign="top" width="15%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.adddatafieldtype" />
						</td>
						<td align="left" class="tblheader" valign="top" width="15%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.adddatafieldlength" />
						</td>
					</tr>


					<%  if (updateDatabaseSubscriberProfileFieldForm.getLstDatasourceSchema() != null) {

				        %>

					<logic:iterate id="dataSourceSchemaBean"
						name="updateDatabaseSubscriberProfileFieldForm"
						property="lstDatasourceSchema"
						type="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData">
						<tr>

							<%	String strToggleAll = "toggleAll[" + j + "]";
								String strSerialNumber = "serialNumber[" + j + "]";
								String strFieldName = "fieldName[" + j + "]";
								String strDataType = "dataType[" + j + "]";
								String strLength = "length[" + j + "]";
								String strFieldId = "fieldId[" + j + "]";
								String strDisplayName = "displayName[" + j + "]";
								

								%>

							<html:hidden name="updateDatabaseSubscriberProfileFieldForm"
								styleId="<%=strSerialNumber%>" property="<%=strSerialNumber%>" />
							<html:hidden name="updateDatabaseSubscriberProfileFieldForm"
								styleId="<%=strFieldId%>" property="<%=strFieldId%>" />

							<td align="center" class="tblleftlinerows" valign="top"><input
								type="checkbox" name="select" value="<%=index%>" /></td>

							<td align="left" class="tblleftlinerows"><%=(index + 1)%></td>

							<td align="left" valign="top" class="tblleftlinerows"><html:text
									name="updateDatabaseSubscriberProfileFieldForm" size="23"
									maxlength="100" styleId="<%=strDisplayName%>"
									property="<%=strDisplayName%>" style="width:100%" /></td>
							<td align="left" valign="top" class="tblleftlinerows"><html:text
									name="updateDatabaseSubscriberProfileFieldForm" size="23"
									maxlength="50" styleId="<%=strFieldName%>"
									property="<%=strFieldName%>" style="width:100%" /></td>
							<td align="left" valign="top" class="tblleftlinerows"><html:select
									name="updateDatabaseSubscriberProfileFieldForm"
									styleId="<%=strDataType%>" property="<%=strDataType%>"
									style="width:100%">
									<logic:iterate id="dataTypeId" property="dataTypeSet"
										name="updateDatabaseSubscriberProfileFieldForm"
										type="java.lang.String">

										<html:option value="<%=dataTypeId%>"><%=dataTypeId%></html:option>

									</logic:iterate>
								</html:select></td>
							<td align="right" valign="top" class="tblleftlinerows"><html:text
									name="updateDatabaseSubscriberProfileFieldForm" size="10"
									maxlength="10" styleId="<%=strLength%>"
									property="<%=strLength%>" style="width:100%" /></td>


							<%j++;%>
							<%index++;%>
						</tr>
					</logic:iterate>
					<%}%>
					<td valign="top" align="right">
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2"
							width="5%"><input type="button" value="Update"
							property="addNode" onclick="validateUpdate()" class="light-btn" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2"
							width="92%"><input type="button" value=" Cancel "
							onclick="javascript:location.href='<%=basePath%>/searchDatabaseSubscriberProfileData.do?driverInstanceId=<%=updateDatabaseSubscriberProfileFieldForm.getDriverInstanceId()%>&dbAuthId=<%=updateDatabaseSubscriberProfileFieldForm.getDbAuthId()%>'"
							property="" onclick="" class="light-btn" /></td>
					</tr>
					<tr>
				</table>
			</td>
		</tr>
	</table>

</html:form>