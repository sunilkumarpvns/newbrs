 <!-- Import Parameters -->
 <%@page import="com.elitecore.elitesm.web.plugins.forms.UserStatisticPostAuthPluginForm"%>
 <%@ page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
 <%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
 <%@ include file="/jsp/core/includes/common/Header.jsp"%>
 
 <% 
UserStatisticPostAuthPluginForm userStatisticPostAuthPluginForm = (UserStatisticPostAuthPluginForm)request.getAttribute("userStatisticPostAuthPluginForm");
%>
 
<!--Import  Stylesheet  -->
 <link rel="stylesheet" href="<%=request.getContextPath()%>/css/plugin/plugin-button.css" />
 <link rel="stylesheet" href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" />
 <link rel="stylesheet" href="<%=request.getContextPath()%>/css/togglebutton.css">

<style type="text/css"> 
	.ui-state-highlight{ margin:10px !important; background-color: white;} 
	.ui-button{ background-color: transparent !important; border: none; background: none;}
	.ui-dialog-titlebar-close{ background-color: transparent !important; border: none !important; background: none !important;}
	.ui-dialog-titlebar-close:hover{background-color: transparent;border: none;background: none;}
	.ui-button .ui-widget .ui-state-default .ui-corner-all .ui-button-icon-only .ui-dialog-titlebar-close{background-color: transparent; border: none; background: none; }
</style>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/user-stat-post-auth-plugin.js?time"=<%=System.currentTimeMillis()%>"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/user-stat-post-auth-plugin-update.js?time"=<%=System.currentTimeMillis()%>" ></script>
<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js" ></script>
<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>

<html:form action="/updateUserStatisticPostAuthPlugin" styleId="userStatisticPostAuthPlugin">

	<html:hidden name="userStatisticPostAuthPluginForm" property="action" styleId="action" value="update" />
	<html:hidden name="userStatisticPostAuthPluginForm" property="pluginType" styleId="pluginType" />
	<html:hidden name="userStatisticPostAuthPluginForm" property="auditUId" styleId="auditUId" />
	<html:hidden name="userStatisticPostAuthPluginForm" property="pluginInstanceId" styleId="pluginInstanceId" />
	<html:hidden name="userStatisticPostAuthPluginForm" property="userStatPostAuthJson" styleId="userStatPostAuthJson" />
		
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
				<div id="main">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="table-header">
							<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.title" />
						</td>
					</tr>
					 <tr>
							<td  width="100%" colspan="2">
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td class="tblheader-bold" height="20%" colspan="3">
											<bean:message bundle="pluginResources" key="plugin.update.plugininstancedetails" />
										</td>
									</tr>
														
									<tr>
										<td class="captiontext padding-top" width="30%" height="20%">
											<bean:message bundle="pluginResources" key="plugin.instname" />
											<ec:elitehelp headerBundle="pluginResources" text="plugin.name" header="plugin.name"/>
										</td>
										<td class="labeltext padding-top" width="*" height="20%" >
											<html:text styleId="pluginName" onkeyup="verifyName();" property="pluginName" size="40" maxlength="70" style="width:250px" tabindex="1" />
											<font color="#FF0000"> *</font>
											<div id="verifyNameDiv" class="labeltext"></div>
										</td>
										<td class="labeltext padding-top" width="40%" valign="top">
											<html:checkbox styleId="status" property="status" value="1" tabindex="2" />Active
										</td>
									</tr>
									
									<tr>
										<td class="captiontext" width="30%" height="20%">
											<bean:message bundle="pluginResources" key="plugin.description" />
											<ec:elitehelp headerBundle="pluginResources" text="plugin.description" header="plugin.description"/>
										</td>
										<td class="labeltext padding-bottom" width="*" height="20%">
											<html:textarea styleId="description" property="description" cols="40" rows="4" style="width:250px" tabindex="2" />
										</td>
									</tr>
								</table>
											
								<table cellpadding="0" cellspacing="0" border="0" width="100%" id="plugin-mapping-table-first">
									<tr>
										<td align="left" class="tblheader-bold" valign="top" colspan="6">
											<bean:message bundle="pluginResources" key="plugin.radius.userstatisticpostauth.title" />
										</td>
									</tr>
														
								 <!-- Datasource Name -->													
									<tr>
										<td class="captiontext databaseId" width="30%">
											<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.datasourcename" />
											<ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.datasourcename" header="plugin.userstatisticpostauth.datasourcename"/>
										</td>
										<td width="70%" class="labeltext">
											<html:select property="databaseId" styleId="databaseId" onchange="setFieldSuggestion();" style="width:250px" tabindex="1">
											<html:option value="0">Select</html:option>
											<html:optionsCollection name="userStatisticPostAuthPluginForm" property="databaseDSList" label="name" value="databaseId"/>
											</html:select> <font color="#FF0000"> *</font>
										</td>
									</tr>
								 <!-- Table Name -->
									<tr>
										<td class="captiontext tableName" width="30%">
											<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.tablename" />
											<ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.tablename" header="plugin.userstatisticpostauth.tablename"/>
										</td>
										<td width="70%" class="labeltext">
											<html:text styleId="tableName" property="tableName" name="userStatisticPostAuthPluginForm" size="30" maxlength="128" style="width:250px" tabindex="2"  onblur="setFieldSuggestion();"/><font color="#FF0000"> *</font>
											<input type="hidden" name="tblname" id="tblname" value='' />
										</td>
									</tr>
									
									<!-- DB Query Timeout -->
									<tr>
										<td class="captiontext dbQueryTimeout">
										 	<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.dbquerytimeout" />
											<ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.dbquerytimeout" header="plugin.userstatisticpostauth.dbquerytimeout"/>
										</td>
										<td width="70%" class="labeltext">
											<html:text styleId="dbQueryTimeoutInMs" property="dbQueryTimeoutInMs" size="30" maxlength="128" style="width:250px" tabindex="2" onkeydown="validateNumbers(this);"/>
										</td>
									</tr>
									
									<!-- Max Query Timeout Count -->
									<tr>
										<td class="captiontext maxQueryTimeoutCount">
											<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.maxquerytimeoutcount" />
											<ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.maxquerytimeoutcount" header="plugin.userstatisticpostauth.maxquerytimeoutcount"/>
										</td>
										<td width="70%" class="labeltext">
											<html:text styleId="maxQueryTimeoutCount" property="maxQueryTimeoutCount" size="30" maxlength="128" style="width:250px" tabindex="2" onkeydown="validateNumbers(this);"/>
										</td>
									</tr>
													
									<!-- Batch Update Interval -->
									<tr>
										<td class="captiontext batchUpdateInterval">
											<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.batchupdateinterval" />
											<ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.batchupdateinterval" header="plugin.userstatisticpostauth.batchupdateinterval"/>
										</td>
										<td width="70%" class="labeltext">
											<html:text styleId="batchUpdateIntervalInMs" property="batchUpdateIntervalInMs" size="30" maxlength="128" style="width:250px" tabindex="2" onkeydown="validateNumbers(this);" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td  width="100%">
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td width="100%" valign="left" class="tbl-header-bold" valign="top">
											<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.mapping.title" />
										</td>
									</tr>
									<tr>
										<td>
											&nbsp;
										</td>
									</tr>
									 <tr>
									 	<td align="left" class="captiontext" valign="top" colspan="2">
											 <input type="button" value="Add" class="light-btn" onclick="addUserStatPostAuthPlugin('plugin-mapping-table','plugin-mapping-table-template');" autocomplete="off" value=" Add " class="light-btn" style="size: 140px" tabindex="10"/>
										</td>
									</tr>
									 <tr>
										 <td align="left" width="100%" class="captiontext" colspan="3" valign="top">
											 <table width="99%" id="plugin-mapping-table" cellpadding="0" cellspacing="0" border="0">
												 <tr>
													 <td align="left" class="tblheader" valign="top" width="16%">
														 <bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.attributeid" />
														 <ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.attributeid" header="plugin.userstatisticpostauth.attributeid"/>
													 </td>
													  <td align="left" class="tblheader" valign="top" width="16%">
														 <bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.packettype" />
														 <ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.packettype" header="plugin.userstatisticpostauth.packettype"/>
													 </td>
													 <td align="left" class="tblheader" valign="top" width="16%">
														<bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.dbfield" />
														<ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.dbfield" header="plugin.userstatisticpostauth.dbfield"/>
													 </td>
													 <td align="left" class="tblheader" valign="top" width="16%">
														 <bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.datatype" />
														 <ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.datatype" header="plugin.userstatisticpostauth.datatype"/>
													 </td>
													 <td align="left" class="tblheader" valign="top" width="16%">
														 <bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.defaultvalue" />
														 <ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.defaultvalue" header="plugin.userstatisticpostauth.defaultvalue"/>
													 </td>
													 <td align="left" class="tblheader" valign="top" width="16%">
														 <bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.usedictionaryvalue" />
														 <ec:elitehelp headerBundle="pluginResources" text="plugin.userstatisticpostauth.usedictionaryvalue" header="plugin.userstatisticpostauth.usedictionaryvalue"/>
													 </td>
													 <td align="left" class="tblheader" valign="top">
													 	 <bean:message bundle="pluginResources" key="plugin.userstatisticpostauth.remove" />
													 </td>
												</tr>																	
											</table>
										 </td>
									 </tr>
									 <tr>
										 <td>
											 &nbsp;
										 </td>
									 </tr>
									 <tr>
										 <td  width="100%">
											 <p>
												 <table cellpadding="0" cellspacing="0" border="0" width="100%">
													 <tr>
														 <td align="left" style="padding-left: 350px;">
															 <input type="button" id="submitLogger" value="Update" class="light-btn" onclick="validateUserStatisticPlugin()"/>
															 <input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchPlugin.do?'" />
														 </td>
													 </tr>
													 <tr>
														 <td>
															 &nbsp;
														 </td>
													 </tr>
												 </table>
											  </p>
										  </td>
										</tr>
									</table>
								 </td>
							  </tr>
						 </table>
					 </div>
				 </td>
			 </tr>
	   </table>
</html:form>
<!-- Mapping Table Row template -->
<table id="plugin-mapping-table-template" class="plugin-mapping-table-template" style="display: none;">
	<tr id="get">
		<td class="tblfirstcol">
			<input class="noborder attributeId" id="attributeId" name="attributeId" type="text" valign="left" valign="top" width="15%" autocomplete="off" onfocus="setColumnsOnUserIdentity();" onkeyup="checkMultiple(this);" style="width: 100%;"/>
		</td>

		<!-- Packet Type -->
		<td class="tblcol">
			<select  class="noborder" id="packetType" name="packetType" style="width: 100%;">
				<option value="0">Request Packet</option>
				<option value="1">Response Packet</option>
			</select>
		</td>

		<!-- DB Field -->
		<td class="tblfirstcol">
			<input class="noborder dbField"  id="dbField" name="dbField" type="text" valign="left" valign="top" width="15%" old-value="" style="width: 100%;"/>
		</td>

		<!-- Data Type -->
		<td class="tblcol" valign="left" valign="top">
			<select class="noborder dataType" id="dataType" name="dataType" style="width: 100%;">
				<option value="String">String</option>
				<option value="Date">Date</option>
			</select>
		</td>
		<!-- Default Value -->
		<td class="tblcol">
			<input class="noborder defaultValue" id="defaultValue" name="defaultValue" type="text" valign="left" valign="top" width="15%" style="width: 100%;"/>
		</td>

		<!-- Use Dictionary Value -->
		<td class="tblfirstcol">
			<select  class="noborder" id="useDictionaryValue" name="useDictionaryValue" style="width: 100%;">
				<option value="False">False</option>
				<option value="True">True</option>
			</select>
		</td>
		
		<td class="tblfirstcol" align="center" colspan="3" valign="left" valign="top" width="5%">
			<span class="delete remove-proxy-server" onclick="deleteMe(this);"></span>
		</td>
	</tr>
</table>

<script type="text/javascript">
	var isValidName;

	/* Set title to User Statistic Post Auth plugin */
	setTitle('<bean:message bundle="pluginResources" key="plugin.radius.userstatisticpostauth.title" />'); 
	$(document).ready(function(){
		var userStasticPostAuthPluginJson = $('#userStatPostAuthJson').val();
		if (userStasticPostAuthPluginJson.charAt(0) == '[' && userStasticPostAuthPluginJson.charAt(userStasticPostAuthPluginJson.length-1) == ']') {
			userStasticPostAuthPluginJson = userStasticPostAuthPluginJson.substring(1,userStasticPostAuthPluginJson.length-1);
		}
	/* Convert JSON format Data to User Statatistic Post Auth Plugin List */
	initializedUserStatPostAuthPlugins(userStasticPostAuthPluginJson);

	/* Get Array Of Radius Attributes */
	retriveRadiusDictionaryAttributes();
	
	/* Get Array of DBFields from Table */
	setFieldSuggestion();
	
	/* Delete Value Of Already Configured DBField Mapping from Array */
	deleteFromArray();
});
	function verifyName() {
		var searchName = document.getElementById("pluginName").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.PLUGIN%>',searchName,'update','<%=userStatisticPostAuthPluginForm.getPluginInstanceId()%>','verifyNameDiv');
	}
</script>


