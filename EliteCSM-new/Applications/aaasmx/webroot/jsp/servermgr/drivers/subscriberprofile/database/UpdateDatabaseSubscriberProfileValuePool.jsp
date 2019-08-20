<%@page
	import="com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.UpdateDatabaseSubscriberProfileFieldForm"%>

<%    
    String localBasePath = request.getContextPath();  
%>

<script>

function popup(mylink, windowname)
			{
				if (! window.focus)return true;
					var href;
				if (typeof(mylink) == 'string')
   					href=mylink;
				else
   					href=mylink.href;
   					
   				//alert(mylink)
				window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
				
				return false;
			}       

</script>

<%
	int index = 0;
	int j = 0;
	UpdateDatabaseSubscriberProfileFieldForm updateDatabaseSubscriberProfileFieldForm = (UpdateDatabaseSubscriberProfileFieldForm) request
	.getAttribute("updateDatabaseSubscriberProfileFieldForm");
	
%>

<html:form action="/updateDatabaseSubscriberProfileField">

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
						<td align="center" class="tblheader" valign="top" width="2%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.serialnumber" />
						</td>

						<td align="left" class="tblheader" valign="top" width="27%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.displayname" />
						</td>
						<td align="left" class="tblheader" valign="top" width="27%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.fieldname" />
						</td>

						<td align="left" class="tblheader" valign="top" width="13%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.adddatafieldtype" />
						</td>
						<td align="right" class="tblheader" valign="top" width="13%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.adddatafieldlength" />
						</td>


						<td align="center" class="tblheader" valign="top" width="13%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.poolvalue" />
						</td>
					</tr>

					<%  if (updateDatabaseSubscriberProfileFieldForm.getLstDatasourceSchema() != null) {
				        System.out.println("updateDatabaseSubscriberProfileFieldForm Form : "
						+ updateDatabaseSubscriberProfileFieldForm.getLstDatasourceSchema().size());

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

							<td align="left" class="tblleftlinerows" width="2%"><%=(index + 1)%>
								&nbsp;</td>
							<td align="left" valign="top" width="27%" class="tblleftlinerows">
								<bean:write name="updateDatabaseSubscriberProfileFieldForm"
									property="<%=strDisplayName%>" />&nbsp;
							</td>
							<td align="left" valign="top" width="27%" class="tblleftlinerows">
								<bean:write name="updateDatabaseSubscriberProfileFieldForm"
									property="<%=strFieldName%>" />&nbsp;
							</td>

							<td align="left" valign="top" width="13%" class="tblleftlinerows">
								<%if(updateDatabaseSubscriberProfileFieldForm.getDataType(j).equalsIgnoreCase("VARCHAR2")) {%>
								<%--<bean:write name="updateDatabaseSubscriberProfileFieldForm" property="<%=strDataType%>"/>&nbsp;
							--%> STRING &nbsp; <%}else{%> <bean:write
									name="updateDatabaseSubscriberProfileFieldForm"
									property="<%=strDataType%>" />&nbsp; <%} %>
							</td>

							<td align="right" valign="top" width="13%"
								class="tblleftlinerows"><bean:write
									name="updateDatabaseSubscriberProfileFieldForm"
									property="<%=strLength%>" />&nbsp;</td>


							<td align="center" valign="top" width="13%"
								class="tblleftlinerows"><a href="javascript:void(0)"
								onclick="window.open('<%=localBasePath%>/updateValuePool.do?fieldId=<bean:write name="updateDatabaseSubscriberProfileFieldForm" property="<%=strFieldId%>"/>','CSVWin','top=0, left=0, height=350, width=700, scrollbars=yes, status')"><img
									src="<%=localBasePath%>/images/lookup.jpg" name="Image521"
									border=0 id="Image5"
									onMouseOver="MM_swapImage('Image521','','<%=localBasePath%>/images/lookup-hover.jpg',1)"
									onMouseOut="MM_swapImgRestore()"></a></td>

							<%j++;%>
							<%index++;%>
						</tr>
					</logic:iterate>
					<%}%>
					<td valign="top" align="right">
						<%--<tr>
						<td align="left" class="labeltext" valign="top" colspan="2" width="5%">
							<input type="button" value="Update" property="addNode" onclick="validateUpdate()" class="light-btn" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2" width="92%">
							<input type="button" value=" Cancel " property="" onclick="" class="light-btn" />
						</td>
					</tr>
					--%>
					<tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>