<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.INetServerConfigMapData"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.INetServerStartupConfigData"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>






<script>

function validateCreate(){ 
if(isNull(document.forms[0].shellPrompt.value)){
		document.forms[0].shellPrompt.focus();
		alert('Shell Prompt must be specified');
	}else if(isNull(document.forms[0].userName.value)){
		document.forms[0].userName.focus();
		alert('User Name must be specified');
	}else if(isNull(document.forms[0].password.value)){
		document.forms[0].password.focus();
		alert('password must be specified');
	}else if(isNull(document.forms[0].communicationPort.value)){
		document.forms[0].communicationPort.focus();
		alert('Communication Port be specified');
	}else if(isNull(document.forms[0].failureMsg.value)){
		document.forms[0].failureMsg.focus();
		alert('Failure Msg be specified');
	}else if(isNull(document.forms[0].operationTimeOut.value) == false  && IsNumeric(document.forms[0].operationTimeOut.value) == false ){
		document.forms[0].operationTimeOut.focus();
		alert('Operatiron Timeout value is not proper');
	}else if(isNull(document.forms[0].loginPrompt.value)){
		document.forms[0].loginPrompt.focus();
		alert('Login Prompt must be specified');
	}else if(isNull(document.forms[0].passwordPrompt.value)){
		document.forms[0].passwordPrompt.focus();
		alert('Password Prompt must be specified');
	}else if(isNull(document.forms[0].shell.value)){
		document.forms[0].shell.focus();
		alert('Shell must be specified');
	}else{
		document.forms[0].submit();
	}

}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0"
	height="15%" align="right">
	<tr>
		<td valign="top" align="right">


			<table width="100%" border="0" cellspacing="0" cellpadding="0"
				height="15%">

				<html:form action="/updateNetserverStartupConfig">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="tblheader-bold" colspan="4"><bean:message
								bundle="servermgrResources"
								key="servermgr.server.connectiondetail" /></td>
					</tr>
					<html:hidden styleId="netServerId" property="netServerId" />

					<tr>
						<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								bundle="servermgrResources" key="servermgr.server.protocol" /></td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:select styleId="protocol" property="protocol">
								<html:option value="Telnet"></html:option>
							</html:select>

						</td>
					</tr>
					<%--
                                                        
                                                        <tr > 
                                                              <td align="left" class="labeltext" valign="top" width="18%" ><bean:message bundle="servermgrResources"  key="servermgr.server.shell"/></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <html:text property="shell" size="25" maxlength="15"/><font color="#FF0000"> *</font> 
                                                              </td>
                                                        </tr>
                                                        
                                                         --%>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								bundle="servermgrResources" key="servermgr.server.shellprompt" /></td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="shellPrompt" property="shellPrompt" size="1"
								maxlength="1" /><font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								bundle="servermgrResources"
								key="servermgr.server.communicationport" /></td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="communicationPort"
								property="communicationPort" size="10" /><font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								bundle="servermgrResources" key="servermgr.server.username" /></td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="userName" property="userName" size="25" /><font
							color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								bundle="servermgrResources" key="servermgr.server.password" /></td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:password styleId="password" property="password" size="25" /><font
							color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								bundle="servermgrResources"
								key="servermgr.server.operationtimeout" /></td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="operationTimeOut" property="operationTimeOut"
								size="10" maxlength="15" /><font color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top" width="2%"><bean:message
								bundle="servermgrResources" key="servermgr.server.failuremsg" /></td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="failureMsg" property="failureMsg"
								value="ogin incorrect" size="25" /><font color="#FF0000">
								*</font>
						</td>
					</tr>

					<tr>

						<td colspan="4" width="100%" align="right">
							<table width="100%" border="0" cellspacing="0" cellpadding="0"
								height="15%" align="right">

								<tr>
									<td class="tblheader-bold" colspan="5"><bean:message
											bundle="servermgrResources" key="servermgr.liveserverdetails" /></td>
								</tr>

								<tr>
									<td align="left" class="tblfirstcol" valign="top" width="2%">1.</td>
									<td align="left" class="tblcol" valign="top" width="5%"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect" />
									</td>
									<td align="left" class="tblfirstcol" valign="top" width="18%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text
											styleId="loginPrompt" property="loginPrompt" size="25" /><font
										color="#FF0000">*</font>
									</td>
									<td align="left" class="tblcol" valign="top" width="5%"><bean:message
											bundle="servermgrResources" key="servermgr.server.send" /></td>
									<td align="left" class="tblfirstcol" valign="top" width="18%"><bean:message
											bundle="servermgrResources" key="servermgr.server.send.1" />
									</td>
								</tr>

								<tr>
									<td align="left" class="tblfirstcol" valign="top">2.</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect" />
									</td>
									<td align="left" class="tblfirstcol" valign="top">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text
											styleId="passwordPrompt" property="passwordPrompt" size="25" /><font
										color="#FF0000">*</font></td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send" /></td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send.2" />
									</td>
								</tr>

								<tr>
									<td align="left" class="tblfirstcol" valign="top">3.</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect" />
									</td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect.3" />
									</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send" /></td>
									<td align="left" class="tblfirstcol" valign="top"><html:text
											styleId="shell" property="shell" size="25" maxlength="15" /><font
										color="#FF0000"> *</font></td>
								</tr>

								<tr>
									<td align="left" class="tblfirstcol" valign="top">4.</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect" />
									</td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect.4" />
									</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send" /></td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send.4" />
									</td>
								</tr>

								<tr>
									<td align="left" class="tblfirstcol" valign="top">5.</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect" />
									</td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect.5" />
									</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send" /></td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send.5" />
									</td>
								</tr>

								<tr>
									<td align="left" class="tblfirstcol" valign="top">6.</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect" />
									</td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect.6" />
									</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send" /></td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send.6" />
									</td>
								</tr>



								<tr>
									<td align="left" class="tblfirstcol" valign="top">7.</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect" />
									</td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.expect.8" />
									</td>
									<td align="left" class="tblcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send" /></td>
									<td align="left" class="tblfirstcol" valign="top"><bean:message
											bundle="servermgrResources" key="servermgr.server.send.8" />
									</td>
								</tr>
							</table>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2"><input
							type="button" name="c_btnCreate" onclick="validateCreate()"
							id="c_btnCreate2" value="Update" class="light-btn"> <input
							type="reset" name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>'" value="Cancel"
							class="light-btn"></td>
					</tr>



				</html:form>
			</table>
</table>



