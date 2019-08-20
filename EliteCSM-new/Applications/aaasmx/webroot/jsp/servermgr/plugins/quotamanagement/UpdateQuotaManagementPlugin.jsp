 <!-- Import Parameters -->
 <%@page import="com.elitecore.elitesm.web.plugins.forms.QuotaManagementPluginForm"%>
 <%@ page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
 <%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
 <%@ include file="/jsp/core/includes/common/Header.jsp"%>
 
<% 
	QuotaManagementPluginForm quotaManagementPluginForm = (QuotaManagementPluginForm)request.getSession().getAttribute("quotaManagementPluginForm");
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

<script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/quota-mgt-plugin.js?time"=<%=System.currentTimeMillis()%>"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/quota-mgt-plugin-update.js?time"=<%=System.currentTimeMillis()%>" ></script>
<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js" ></script>
<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>

<html:form action="/updateQuotaManagementPlugin" styleId="quotaManagementPlugin">

	<html:hidden name="quotaManagementPluginForm" property="action" styleId="action" value="update" />
	<html:hidden name="quotaManagementPluginForm" property="pluginType" styleId="pluginType" />
	<html:hidden name="quotaManagementPluginForm" property="auditUId" styleId="auditUId" />
	<html:hidden name="quotaManagementPluginForm" property="pluginInstanceId" styleId="pluginInstanceId" />
	<html:hidden name="quotaManagementPluginForm" property="quotaMgtJson" styleId="quotaMgtJson" />
							
	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
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
		<tr>
			<td class="tblheader-bold" colspan="3">
				<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.title" />
			</td>
		</tr>
		<tr>
			<td class="labeltext" colspan="3">
				<div>
					<table width="100%" cellspacing="0" cellpadding="0" border="0">
						<tr>
							<td style="padding:5px;padding-left: 10px;padding-top: 10px;">
								<input type="button" value="Add Policy" class="light-btn" onclick="addQuotaMgtPlugin('plugin-mapping-table','plugin-mapping-table-template');" />
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
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>
				<input type="button" id="submitLogger" value="Update" class="light-btn" onclick="validateQuotaMgtPlugin()"/>
				<input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchPlugin.do?'" />
			</td>
		</tr>
	</table>
	
<!-- External include files list -->
<%@include file="/jsp/servermgr/plugins/quotamanagement/QuotaMgtPlugin.jsp" %>
	
</html:form>

<script type="text/javascript">
	
	var isValidName;
	
	/* Set title to Quota management plugin */
	setTitle('<bean:message bundle="pluginResources" key="plugin.radius.quotamanagement.title" />'); 
	
	$( document ).ready(function() {
		/* Initialized Default Universal Plugin */
		initializeQuotaMgtPlugin();
		
		var quotaMgtJson=$('#quotaMgtJson').val();
	
		if(quotaMgtJson.charAt(0) == '[' && quotaMgtJson.charAt(quotaMgtJson.length-1) == ']'){
			quotaMgtJson=quotaMgtJson.substring(1,quotaMgtJson.length-1);
		}
		/* Convert JSON format Data to Quota Management Plugin List */
		initializedQuotaMgtPlugins(quotaMgtJson);
	    
	});
		
	function verifyName() {
		var searchName = document.getElementById("pluginName").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.PLUGIN%>',searchName,'update','<%=quotaManagementPluginForm.getPluginInstanceId()%>','verifyNameDiv');
	}
	
</script>


