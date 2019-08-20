
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData"%>
<%@page
	import="com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.UpdateValuePoolForm"%>







<%@ page import="java.util.*"%>

<script>

function closeWin()
{
  window.close();
}
function deleteSubmit(index)
{   
	document.forms[0].valuePoolAction.value = 'Remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();	
}

function addnode(){
	
	document.forms[0].valuePoolAction.value = 'add';
	document.forms[0].submit();
}

function validateUpdate(){
	
		document.forms[0].valuePoolAction.value='update';	
		document.forms[0].submit();
		//window.close();
	
}

function validateRadio(){
		
		document.forms[0].valuePoolAction.value = '';
		document.forms[0].submit();
	
}

function updateSQLPool(){

		document.forms[0].valuePoolAction.value = 'updateSQL';
		document.forms[0].submit();
		//window.close();
}

</script>


<%
    String basePath = request.getContextPath();    
%>
<%
	int index = 0;
	int j = 0;
	UpdateValuePoolForm updateValuePoolForm = (UpdateValuePoolForm) request.getAttribute("updateValuePoolForm");
	IDatasourceSchemaData datasourceSchemaData = (IDatasourceSchemaData)request.getAttribute("datasourceSchemaData");
	System.out.println("STATUS CHECK------------ : "+updateValuePoolForm.getStatus());
%>

<html:form action="/updateValuePool">
	<html:hidden name="updateValuePoolForm" styleId="valuePoolAction"
		property="valuePoolAction" />
	<html:hidden name="updateValuePoolForm" styleId="paramId"
		property="paramId" />
	<html:hidden name="updateValuePoolForm" styleId="itemIndex"
		property="itemIndex" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td colspan="2">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="81%" background="<%=basePath%>/images/popup-bkgd.jpg"
							valign="top">&nbsp;</td>
						<td width="3%"><img
							src="<%=basePath%>/images/popup-curve.jpg"></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="3%"><a href="#"><img
								src="<%=basePath%>/images/refresh.jpg" name="Image1"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/refresh-hover.jpg',1)"
								border="0"></a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="3%"><a href="#" onclick="window.print()"><img
								src="<%=basePath%>/images/print.jpg" name="Image2"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/print-hover.jpg',1)"
								border="0"> </a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="3%"><a href="#"><img
								src="<%=basePath%>/images/aboutus.jpg" name="Image3"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/aboutus-hover.jpg',1)"
								border="0"></a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="3%"><a href="#"><img
								src="<%=basePath%>/images/help.jpg" name="Image4"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/help-hover.jpg',1)"
								border="0"></a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="4%">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<logic:equal name="updateValuePoolForm" property="navigationCode"
		value="0">
		<table name="MainTable" id="MainTable" cellSpacing="0" cellPadding="0"
			width="100%" border="0">
			<tr>
				<td width="10">&nbsp;</td>
				<td width="100%" colspan="3" valign="top" class="box">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0">
						<tr>
							<td class="table-header" valign="bottom" colspan="3"><bean:message
									bundle="radiusResources" key="radiuspolicy.addedPoolValue" /><font
								size="1"> [ <%= datasourceSchemaData.getFieldName() %> ]
							</font></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="4">
								<table width="97%" align="right" border="0" cellpadding="0"
									cellspacing="0">
									<tr>
										<td align="left" class="labeltext" valign="top" width="5%">


											<html:radio name="updateValuePoolForm" styleId="status"
												property="status" value="valuePool"
												onclick="validateRadio()" />Value Pool <html:radio
												name="updateValuePoolForm" styleId="status"
												property="status" value="query" onclick="validateRadio()" />Other(Query)

										</td>
									</tr>
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>
								</table>
							</td>
						</tr>

						<%
					if(updateValuePoolForm.getStatus().equalsIgnoreCase("valuePool")){
					%>
						<tr>
							<td colspan="5">
								<table width="97%" align="right" border="0" cellpadding="0"
									cellspacing="0">
									<tr>
										<td align="left" valign="top" width="5%"><input
											type="button" value="   Add   " property="addNode"
											onclick="addnode()" class="light-btn" /></td>
									</tr>
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>
									<tr>
										<td align="center" class="tblheader" valign="top" width="2%">
											<bean:message key="general.serialnumber" />
										</td>
										<td align="left" class="tblheader" valign="top" width="20%">
											<bean:message bundle="radiusResources"
												key="updateValuePool.name.label" />
										</td>
										<td align="left" class="tblheader" valign="top" width="23%">
											<bean:message bundle="radiusResources"
												key="updateValuePool.value.label" />
										</td>
										<td align="center" class="tblheader" valign="top" width="18%">
											<bean:message bundle="radiusResources"
												key="radiuspolicy.valuePool.remove" />
										</td>
									</tr>
									<%  if (updateValuePoolForm.getDbdsDatasourceParamPoolList() != null) {
									        System.out.println("updateDatabaseDatasourceFieldForm Form : "
											+ updateValuePoolForm.getDbdsDatasourceParamPoolList().size());

				       			 %>

									<logic:iterate id="dbdsParamPoolBean"
										name="updateValuePoolForm"
										property="dbdsDatasourceParamPoolList"
										type="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDBSubscriberProfileParamPoolValueData">

										<tr>

											<%	
												String strSerialNumber = "serialNumber[" + j + "]";
												String strName = "name[" + j + "]";
												String strValue = "value[" + j + "]";
											%>
											<td align="left" class="tblleftlinerows"><%=(index + 1)%>
											</td>

											<td align="left" valign="top" width="15%"
												class="tblleftlinerows"><html:text
													name="updateValuePoolForm" size="38" maxlength="100"
													styleId="<%=strName%>" property="<%=strName%>" /></td>
											<td align="left" valign="top" width="30%"
												class="tblleftlinerows"><html:text
													name="updateValuePoolForm" size="38" maxlength="50"
													styleId="<%=strValue%>" property="<%=strValue%>" /></td>
											<td align="center" class="tblleftlinerows"><img
												src="<%=basePath%>/images/minus.jpg"
												onclick="deleteSubmit('<%=index%>')" border="0" /></td>
											<%j++;%>
											<%index++;%>
										</tr>




									</logic:iterate>
									<%}%>
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td align="left" valign="top" width="7%"><input
											type="button" value="Update" property="addNode"
											onclick="validateUpdate()" class="light-btn" /></td>
										<td align="left" valign="top" class="labeltext"><input
											type="button" value=" Cancel " onclick="closeWin()"
											class="light-btn" /></td>
									</tr>

								</table>
							</td>
						</tr>

						<%
					}else{
					%><tr>
							<td colspan="7">
								<table width="97%" align="right" border="0" cellpadding="0"
									cellspacing="0">
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>
									<tr>
										<td align="left" class="labeltext" valign="bottom" width="20%"><bean:message
												bundle="radiusResources" key="datasource.addquery.label" /></td>
									</tr>
									<tr>
										<td align="left" class="labeltext" valign="bottom"><html:textarea
												styleId="queryString" property="queryString" cols="55"
												rows="5" /></td>

									</tr>
									<tr>
										<td align="left" valign="top" class="labeltext"><input
											type="button" value="Update" class="light-btn"
											onclick="updateSQLPool()" /></td>

									</tr>
								</table>
							</td>
						</tr>


						<%
					}
					%>
					</table>
				</td>
			</tr>

		</table>


	</logic:equal>

	<logic:equal name="updateValuePoolForm" property="navigationCode"
		value="1">
		<table name="MainTable" id="MainTable" cellSpacing="0" cellPadding="0"
			width="100%" border="0">
			<tr>
				<td width="10">&nbsp;</td>
				<td width="100%" colspan="3" valign="top" class="box">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0">
						<tr>
							<td class="table-header" valign="bottom" colspan="3"><bean:message
									bundle="radiusResources" key="radiuspolicy.addedPoolValue" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="5">
								<table width="97%" align="right" border="0" cellpadding="0"
									cellspacing="0">
									<tr>
										<td align="left" class="labeltext" valign="top" width="5%">
											<html:radio name="updateValuePoolForm" styleId="status"
												property="status" value="valuePool"
												onclick="validateRadio()" />Value Pool <html:radio
												name="updateValuePoolForm" styleId="status"
												property="status" value="query" onclick="validateRadio()" />Other(Query)

										</td>
									</tr>
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>
								</table>
							</td>
						</tr>

						<tr>
							<td colspan="5">
								<table width="97%" align="right" border="0" cellpadding="0"
									cellspacing="0">

									<tr>
										<td align="left" valign="top" width="5%"><input
											type="button" value="   Add   " property="addNode"
											onclick="addnode()" class="light-btn" /></td>
									</tr>

									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>


									<tr>
										<td align="center" class="tblheader" valign="top" width="2%">
											<bean:message key="general.serialnumber" />
										</td>
										<td align="left" class="tblheader" valign="top" width="20%">
											<bean:message bundle="radiusResources"
												key="updateValuePool.name.label" />
										</td>
										<td align="left" class="tblheader" valign="top" width="23%">
											<bean:message bundle="radiusResources"
												key="updateValuePool.value.label" />
										</td>
										<td align="center" class="tblheader" valign="top" width="18%">
											<bean:message bundle="radiusResources"
												key="radiuspolicy.valuePool.remove" />
										</td>
									</tr>

									<%  if (updateValuePoolForm.getDbdsDatasourceParamPoolList() != null) {
									        System.out.println("updateDatabaseDatasourceFieldForm Form : "
											+ updateValuePoolForm.getDbdsDatasourceParamPoolList().size());

				       			 %>

									<logic:iterate id="dbdsParamPoolBean"
										name="updateValuePoolForm"
										property="dbdsDatasourceParamPoolList"
										type="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDBSubscriberProfileParamPoolValueData">

										<tr>

											<%	//String strToggleAll = "toggleAll[" + j + "]";
												String strSerialNumber = "serialNumber[" + j + "]";
												String strName = "name[" + j + "]";
												String strValue = "value[" + j + "]";
											%>


											<td align="left" class="tblleftlinerows"><%=(index + 1)%>
											</td>

											<td align="left" valign="top" width="15%"
												class="tblleftlinerows"><html:text
													name="updateValuePoolForm" size="38" maxlength="100"
													styleId="<%=strName%>" property="<%=strName%>" /></td>
											<td align="left" valign="top" width="30%"
												class="tblleftlinerows"><html:text
													name="updateValuePoolForm" size="38" maxlength="50"
													styleId="<%=strValue%>" property="<%=strValue%>" /></td>
											<td align="center" class="tblleftlinerows"><img
												src="<%=basePath%>/images/minus.jpg"
												onclick="deleteSubmit('<%=index%>')" border="0" /></td>
											<%j++;%>
											<%index++;%>
										</tr>




									</logic:iterate>
									<%}%>
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td align="left" valign="top" width="7%"><input
											type="button" value="Update" property="addNode"
											onclick="validateUpdate()" class="light-btn" /></td>
										<td align="left" valign="top" class="labeltext"><input
											type="button" value=" Cancel " property=""
											onclick="closeWin()" class="light-btn" /></td>
									</tr>


								</table>
							</td>
						</tr>



					</table>
				</td>
			</tr>
		</table>


	</logic:equal>

	<logic:equal name="updateValuePoolForm" property="navigationCode"
		value="2">
		<table name="MainTable" id="MainTable" cellSpacing="0" cellPadding="0"
			width="100%" border="0">
			<tr>
				<td width="10">&nbsp;</td>
				<td width="100%" colspan="3" valign="top" class="box">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0">
						<tr>
							<td class="table-header" valign="bottom" colspan="3"><bean:message
									bundle="radiusResources" key="radiuspolicy.addedPoolValue" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="4">
								<table width="97%" align="right" border="0" cellpadding="0"
									cellspacing="0">
									<tr>
										<td align="left" class="labeltext" valign="top" width="5%">
											<html:radio name="updateValuePoolForm" styleId="status"
												property="status" value="valuePool"
												onclick="validateRadio()" />Value Pool <html:radio
												name="updateValuePoolForm" styleId="status"
												property="status" value="query" onclick="validateRadio()" />Other(Query)

										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="7">
								<table width="97%" align="right" border="0" cellpadding="0"
									cellspacing="0">
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>

									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>
									<tr>
										<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
									</tr>
									<tr>
										<td align="left" class="labeltext" valign="bottom" width="20%"><bean:message
												bundle="radiusResources" key="datasource.addquery.label" /></td>
									</tr>
									<tr>
										<td align="left" class="labeltext" valign="bottom"><html:textarea
												styleId="queryString" property="queryString" cols="55"
												rows="5" /></td>

									</tr>
									<tr>
										<td align="left" valign="top" class="labeltext"><input
											type="button" value="Update" class="light-btn"
											onclick="updateSQLPool()" /></td>

									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>

		</table>


	</logic:equal>

</html:form>


<table cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td colspan="2" class="small-gap">&nbsp;</td>
	</tr>

	<tr>
		<td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
		<td class="small-gap" width="1%"><img
			src="<%=basePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>



