<%@page import="com.elitecore.elitesm.util.constants.PluginTypesConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginServiceTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginTypesData"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
%>
<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript1.2">
 var isValidName;

 	var obj=top.frames["mainFrame"].document.getElementById('contentIframe');
	
 	function validateForm(){
 		if(isNull(document.forms['nextPlugin'].name.value)){
 			alert('Name must be specified');
 		}else if(!isValidName) {
 			alert('Enter Valid Plugin Name');
 			document.forms['nextPlugin'].name.focus();
 			return;
 		}else  if(document.forms['nextPlugin'].selectedPlugin.value == 0){
 			alert('Select atleast one plugin.');
 		}else if(isRadiusTransactionLoggerEnabled()){
 			alert('System allows you to create only one Radius Transaction Logger.');
 			return false;
 		}else if(isDiameterTransactionLoggerEnabled()){
 			alert('System allows you to create only one Diameter Transaction Logger.');
 			return false;
 		}else{
 			document.forms['nextPlugin'].action.value = 'next';
 	 		document.forms['nextPlugin'].submit();	
 		}	
	}

 	function verifyName() {
 		var searchName = document.getElementById("name").value;
 		isValidName = verifyInstanceName('<%=InstanceTypeConstants.PLUGIN%>',searchName,'create','','verifyNameDiv');
 	}
 	
 	if(obj != null){
	 	if(obj.id !== "contentIframe"){
			setTitle('<bean:message bundle="pluginResources" key="plugin.title"/>'); 		
		}
 	}
 	
 	function isRadiusTransactionLoggerEnabled(){
 		var isRadiusTransactionLoggerEnabled = '<bean:write name="createPluginForm" property="radiusTransationLoggerEnabled" />';
 		if( isRadiusTransactionLoggerEnabled == 'true' && ($('#selectedPlugin').val() == '<%=PluginTypesConstants.RADIUS_TRANSACTION_LOGGER%>')){
 			return true;
 		}else{
 			return false;
 		}
 	}
 	
 	function isDiameterTransactionLoggerEnabled(){
		var isDiameterTransactionLoggerEnabled = '<bean:write name="createPluginForm" property="diameterTransactionLoggerEnabled" />';
		if( isDiameterTransactionLoggerEnabled == 'true' && ($('#selectedPlugin').val() == '<%=PluginTypesConstants.DIAMETER_TRANSACTION_LOGGER%>')){
 			return true;
 		}else{
 			return false;
 		}
 	}
 	
</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<html:form action="/nextCreatePlugin" styleId="nextPlugin">
	<html:hidden name="createPluginForm" property="action" value="next" />
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header">
										<bean:message bundle="pluginResources" key="plugin.title" />
									</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">

											<tr>
												<td align="left" class="labeltext" valign="top" width="18%">
													<bean:message bundle="pluginResources" key="plugin.name" />
													<ec:elitehelp headerBundle="pluginResources" text="plugin.name" header="plugin.name"/>
												</td>

												<td align="left" class="labeltext" valign="top" nowrap="nowrap">
													<html:text styleId="name" onkeyup="verifyName();" property="name" size="40" maxlength="70" style="width:250px" tabindex="1" />
													<font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
												<td class="labeltext" width="*" valign="top">
													<html:checkbox property="status" styleId="status" value="1" tabindex="2"></html:checkbox>
													&nbsp;Active
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top" width="18%">
													<bean:message bundle="pluginResources" key="plugin.description" />
													<ec:elitehelp headerBundle="pluginResources" text="plugin.description" header="plugin.description"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="25%">
													<html:textarea styleId="description" property="description" cols="40" rows="4" style="width:250px" tabindex="2" />
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" width="18%">
													<bean:message bundle="pluginResources" key="plugin.plugintype" /> 
													<ec:elitehelp headerBundle="pluginResources" text="plugin.plugintype" header="plugin.plugintype"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="25%">
													 <html:select name="createPluginForm" styleId="selectedPlugin" property="selectedPlugin" style="width:250px" tabindex="3">
														<html:option value="0">Select</html:option>
														<logic:iterate id="objservice" name="createPluginForm"
															type="PluginServiceTypeData" property="pluginServiceList">
															<optgroup label="<%=objservice.getDisplayName() %>"
																class="labeltext">
																<logic:iterate id="objdriver" name="objservice"
																	type="PluginTypesData" property="pluginTypeSet">
																		<html:option value="<%=objdriver.getPluginTypeId()%>"><%=objdriver.getDisplayName() %>
																		</html:option>
																</logic:iterate>
															</optgroup>
														</logic:iterate>
													</html:select> 
												</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2">
													<input type="button" name="c_btnCreate" id="c_btnCreate2" value="Next" class="light-btn" onclick="validateForm()" tabindex="4" /> 
													<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/searchPlugin.do'" value="Cancel" class="light-btn" tabindex="5" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>

<script>
setTitle('<bean:message bundle="pluginResources" key="plugin.title"/>'); 		
$('#status').attr('checked', true);
</script>
