 <!-- Import Parameters -->
 <%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
 
 <!-- Include Files List -->
 <%@ include file="/jsp/core/includes/common/Header.jsp"%>

 <style type="text/css"> 
		
		.ui-state-highlight{
			margin:10px !important;
			background-color: white;
		} 
		
		.ui-button{
			background-color: transparent !important;
			border: none;
			background: none;
		}
		
		.ui-dialog-titlebar-close{
			background-color: transparent !important; 
			border: none !important;
			background: none !important;
		}
		
		.ui-dialog-titlebar-close:hover{
			background-color: transparent;
			border: none;
			background: none;
		}
		.ui-button .ui-widget .ui-state-default .ui-corner-all .ui-button-icon-only .ui-dialog-titlebar-close{
			background-color: transparent;
			border: none;
			background: none;
		}
	
  </style>

 <!--Import  Stylesheet  -->
 <link rel="stylesheet" href="<%=request.getContextPath()%>/css/plugin/plugin-button.css" />
 <link rel="stylesheet" href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" />
 <link rel="stylesheet" href="<%=request.getContextPath()%>/css/togglebutton.css">
 
 <!-- Import Javascript -->
 <script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/universal-plugin.js"></script>
 <script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js" ></script>
 <script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>

<html:form action="/updateUniversalAcctPlugin" styleId="uniPlugin">

	<html:hidden name="updateUniversalAcctPluginForm" property="action" value="update" />
	<html:hidden name="updateUniversalAcctPluginForm" property="universalPluginPolicyJson" styleId="universalPluginPolicyJson" />
	<html:hidden name="updateUniversalAcctPluginForm" property="pluginType" styleId="pluginType" />
	<html:hidden name="updateUniversalAcctPluginForm" property="auditUId" styleId="auditUId" />
	<html:hidden name="updateUniversalAcctPluginForm" property="pluginInstanceId" styleId="pluginInstanceId" />
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
		<tr>
			<td class="tblheader-bold" height="20%" colspan="3">
				<bean:message bundle="pluginResources" key="plugin.update.plugininstancedetails" />
			</td>
		</tr>
		<tr>
			<td class="captiontext padding-top" width="25%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.instname" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.name" header="plugin.name"/>
			</td>
			<td class="labeltext padding-top" width="22%" height="20%" >
				<html:text styleId="pluginName" onkeyup="verifyName();" property="pluginName" size="40" maxlength="70" style="width:250px" tabindex="1" />
				<font color="#FF0000"> *</font>
				<div id="verifyNameDiv" class="labeltext"></div>
			</td>
			<td class="labeltext padding-top" width="40%">
				<html:checkbox styleId="status" property="status" value="1" tabindex="2" />Active
			</td>
		</tr>
		<tr>
			<td class="captiontext" width="25%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.description" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.description" header="plugin.description"/>
			</td>
			<td class="labeltext padding-bottom" width="*" height="20%">
				<html:textarea styleId="description" property="description" cols="40" rows="4" style="width:250px" tabindex="2" />
			</td>
		</tr>
		<tr>
			<td class="tblheader-bold" width="25%" height="20%" colspan="3">
				<bean:message bundle="pluginResources" key="plugin.universalpluginpolicies" />
			</td>
		</tr>
		<tr>
			<td colspan="3">
				<table width="100%" cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td>
							 <div class="plugin-box">
				                <table width="100%" cellspacing="0" cellpadding="0" border="0">
				                	<tr>
				                		<td>
											<div class="table-header plugin-table-header-css captiontext" style="background-color: #D9E6F6;">
												<bean:message bundle="pluginResources" key="plugin.acct.universalpreacctpolicy.list" />
											</div>
											<div class="captiontext"  style="padding: 5px;">
												<table width="100%" cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td style="padding:5px;padding-left: 10px;">
															<input type="button" value="Add Policy" class="light-btn" onclick="addUniversalPlugin('plugin-mapping-table','plugin-pre-mapping-table-template','pre-param-list-tbl-mapping','pre-param-list-tbl-template');" />
														</td>
													</tr>
												</table>
												<table width="100%" cellspacing="0" cellpadding="0" border="0" id="plugin-mapping-table">
													<tbody class="parent sortableClass ui-sortable">
													</tbody>
												</table>
											</div>
										</td>
				                	</tr>
				                 </table>
				              </div><!-- /.plugin-box -->
						 </td>
					</tr>
					<tr>
						<td>
							 <div class="plugin-box">
				                <table width="100%" cellspacing="0" cellpadding="0" border="0">
				                	<tr>
				                		<td>
											<div class="table-header plugin-table-header-css captiontext" style="background-color: #D9E6F6;">
												<bean:message bundle="pluginResources" key="plugin.acct.universalpostacctpolicy.list" />
											</div>
											<div class="captiontext"  style="padding: 5px;">
												<table width="100%" cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td style="padding:5px;padding-left: 10px;">
															<input type="button" value="Add Policy" class="light-btn" onclick="addUniversalPlugin('plugin-post-mapping-table','plugin-post-mapping-table-template','post-param-list-tbl-mapping','post-param-list-tbl-template');" />
														</td>
													</tr>
												</table>
												<table width="100%" cellspacing="0" cellpadding="0" border="0" id="plugin-post-mapping-table">
													<tbody class="parent sortableClass ui-sortable">
													</tbody>
												</table>
											</div>
										</td>
				                	</tr>
				                 </table>
				              </div><!-- /.plugin-box -->
						</td>
					</tr>
					<tr>
						<td align="left" style="padding-left: 350px;">
							<input type="button" value="Update" class="light-btn" onclick="validatePluginForm();"/> 
							<input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchPlugin.do?'" />
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>

<!-- External include files list -->
<%@include file="/jsp/servermgr/plugins/universalplugin/UpdatePreAcctPlugin.jsp" %>
<%@include file="/jsp/servermgr/plugins/universalplugin/UpdatePostAcctPlugin.jsp" %>

<script type="text/javascript">
	
	var isValidName;
	
	$(document).ready(function(){
		/* Initialized Default Universal Plugin */
		initializeUniversalPlugin();
		var universalPluginPolicyJson = $('#universalPluginPolicyJson').val();
		
		if (universalPluginPolicyJson.charAt(0) == '[' && universalPluginPolicyJson.charAt(universalPluginPolicyJson.length-1) == ']') {
			universalPluginPolicyJson = universalPluginPolicyJson.substring(1,universalPluginPolicyJson.length-1);
		}
		/* Convert JSON format Data to Universal Plugin List */
		initializedPlugins(universalPluginPolicyJson);
	});
	function verifyName() {
		var searchName = document.getElementById("pluginName").value;
		var pluginInstId = $('#pluginInstanceId').val();
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.PLUGIN%>',searchName,'update',pluginInstId,'verifyNameDiv');
	}
	
</script>
