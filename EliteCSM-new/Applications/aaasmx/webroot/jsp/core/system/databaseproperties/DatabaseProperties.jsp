<%@page import="com.elitecore.elitesm.web.core.system.dbpropertiesconfiguration.form.DatabasePropertiesForm"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>

<%
	DatabasePropertiesForm databasePropertiesForm = (DatabasePropertiesForm) request.getAttribute("databasePropertiesForm");
%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/databaseproperties/database.properties.js"></script>

<script type="text/javascript">

/*** This function is used to check that using configured properties we can establish connection or not. */
function testConnection() {

	if (validateParameter()) {

		var connectionUrl = document.forms[0].connectionUrl.value;
		var dbUsername = document.forms[0].dbUsername.value;
		var dbPassword = document.forms[0].dbPassword.value;
		var isValidate = false;
		var errorMessage = "";
		$.ajax({
				url : '<%=request.getContextPath()%>/databaseProperties.do?method=validate',
				type : 'POST',
				data : {
					connectionUrl : connectionUrl,
					dbUsername : dbUsername,
					dbPassword : dbPassword
				},
				async : false,
				success : function(result) {
					var obj = JSON.parse(result);
					if (obj.flag == true) {
						isValidate = true;
					} else {
						errorMessage = obj.message;
						isValidate = false;
					}
				},
				error : function(textStatus, errorThrown) {
					isValidate = false;
				}
		});

		if (isValidate) {
			successDialog.dialog("open");
		} else {
			if (errorMessage != '') {
				$('#errorReason').text(errorMessage);
				failureDialog.dialog("open");
			}
		}
	}
}

function cancelForm() {
	$('#databasePropertiesForm').attr('action','<%=request.getContextPath()%>/listNetServerInstance.do');
	$('#databasePropertiesForm').submit();
}

</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="box" cellpadding="0" cellspacing="0" border="0" width="100%">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header" colspan="6">
									<bean:message bundle="StaffResources" key="database.properties.title" />
								</td>
							</tr>
							<tr>
								<td>
								<html:form styleId="databasePropertiesForm" action="/databaseProperties.do?method=update">
									<table width="100%" cellspacing="0" cellpadding="0" border="0">
										<tr>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td class="captiontext"  valign="top">
												<bean:message bundle="StaffResources" key="database.properties.connectionurl" />
												<ec:elitehelp headerBundle="StaffResources" text="database.properties.connectionurl" header="database.properties.connectionurl"/>
											</td>
											<td valign="top">
												<html:textarea styleId="connectionUrl" property="connectionUrl" name="databasePropertiesForm" style="width:550px;"></html:textarea>
												<font color="#FF0000"> *</font>
											</td>
										</tr>
										<tr>
											<td class="captiontext">
												<bean:message bundle="StaffResources" key="database.properties.username" />
												<ec:elitehelp headerBundle="StaffResources" text="database.properties.username" header="database.properties.username"/>
											</td>
											<td>
												 <html:text styleId="dbUsername" property="dbUsername" name="databasePropertiesForm" styleClass="textbox-width-class"></html:text>
												 <font color="#FF0000"> *</font>
											</td>
										</tr>
										<tr>
											<td class="captiontext">
												<bean:message bundle="StaffResources" key="database.properties.password" />
												<ec:elitehelp headerBundle="StaffResources" text="database.properties.password" header="database.properties.password"/>
											</td>
											<td>
												<html:password styleId="dbPassword" property="dbPassword" name="databasePropertiesForm" styleClass="textbox-width-class"></html:password>
												<font color="#FF0000"> *</font>
											</td>
										</tr>
										<tr>
											<td class="captiontext">
												<bean:message bundle="StaffResources" key="database.properties.showsql" />
												<ec:elitehelp headerBundle="StaffResources" text="database.properties.showsql" header="database.properties.showsql"/>
											</td>
											<td>
												<html:select styleId="showSQL" property="showSQL" name="databasePropertiesForm">
													<html:option value="false">False</html:option>
													<html:option value="true">True</html:option>
												</html:select>
											</td>
										</tr>
										<tr>
											<td class="captiontext">
												<bean:message bundle="StaffResources" key="database.properties.formatsql" />
												<ec:elitehelp headerBundle="StaffResources" text="database.properties.formatsql" header="database.properties.formatsql"/>
											</td>
											<td>
												<html:select styleId="formatSQL" property="formatSQL" name="databasePropertiesForm">
													<html:option value="false">False</html:option>
													<html:option value="true">True</html:option>
												</html:select>
											</td>
										</tr>
										<tr>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td>&nbsp;</td>
											<td align="left">
											    <input type="button" value=" Test Connection " class="light-btn" onclick="testConnection();" />
												<input type="button" id="updateButton" value=" Update " class="light-btn" onclick="validateForm();" />
												<input type="button" id="cancelButton" value=" Cancel " class="light-btn" onclick="cancelForm();" />
											</td>
										</tr>
										<tr>
											<td>&nbsp;</td>
										</tr>
										<tr>
											<td colspan="2" class="captiontext"><font style="font-size: 12px;color:red;">** It is mandatory to logout the Server Manager after updating the database.properties file</font></td>
										</tr>
									</table>
									</html:form>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<div id="failure-dialog" title="Failure" style="display: none;">
	 <table>
		 	<tr>
		 		<td colspan="2" class="dialog-font">Error occurred while establishing connection...</td>
		 	</tr>
		 	<tr>
		 		<td class="dialog-font">Reason:</td>
		 	</tr>
		 	<tr>
		 		<td>&nbsp;</td>
		 		<td id="errorReason" class="dialog-font" style="color:red;"></td>
		 	</tr>
	 </table>
</div>

<div id="success-dialog" title="Success" style="display: none;">
	 <table>
		 	<tr>
		 		<td class="dialog-font"><i class="success-tick"></i></td>
		 		<td class="dialog-font">Test connection succeeded</td>
		 	</tr>
	 </table>
</div>

<script type="text/javascript">
	setTitle('<bean:message bundle="StaffResources" key="database.properties.titlebar"/>');
</script>