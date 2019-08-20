<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

<% String basePath = request.getContextPath(); %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/togglebutton.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/plugin/plugin-button.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/quota-mgt-plugin.js?time"=<%=System.currentTimeMillis()%>"></script>
<style type="text/css"> 
	.ui-state-highlight{ margin:10px !important; background-color: white;} 
	.ui-button{ background-color: transparent !important; border: none; background: none;}
	.ui-dialog-titlebar-close{ background-color: transparent !important; border: none !important; background: none !important;}
	.ui-dialog-titlebar-close:hover{background-color: transparent;border: none;background: none;}
	.ui-button .ui-widget .ui-state-default .ui-corner-all .ui-button-icon-only .ui-dialog-titlebar-close{background-color: transparent; border: none; background: none; }
</style>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<div id="main">
								<html:form action="createQuotaManagementPlugin" styleId="quotaManagementPlugin" method="post">
					
								<html:hidden name="quotaManagementPluginForm" property="action" value="create" /> 
								<html:hidden name="quotaManagementPluginForm" property="pluginName" styleId="pluginName" />
								<html:hidden name="quotaManagementPluginForm" property="description" styleId="description" />
								<html:hidden name="quotaManagementPluginForm" property="status" styleId="status" />
								<html:hidden name="quotaManagementPluginForm" property="pluginType" styleId="pluginType" />
								<html:hidden name="quotaManagementPluginForm" property="quotaMgtJson" styleId="quotaMgtJson" />
								<html:hidden name="quotaManagementPluginForm" property="status" styleId="status" />
					
								 <table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td class="table-header" colspan="2">
											<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.title" />
										</td>
									</tr>
									<tr>
										<td class="labeltext">
											<div>
												<%@include file="/jsp/servermgr/plugins/quotamanagement/QuotaManagementPlugin.jsp" %>
											</div>
										</td>
									</tr>
								</table> 
								</html:form>
							</div>
						</td>
					</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%> 
				</table>
			</td>
		</tr>
	</table>
	
	<!-- Required JS and CSS List for Quota Management Plugin -->
	<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js" ></script>
	<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>
	
	<script type="text/javascript">
		
		/** Setting title in main header bar*/
		setTitle('<bean:message bundle="pluginResources" key="plugin.radius.quotamanagement.title" />');
	
		/* Initialize default quota management plugin */
		$(document).ready(function(){
			
			/* Adding initial quota manager entry into list */
			addDefaultQuotaMgtPlugin('plugin-mapping-table','plugin-mapping-table-template');
			
			/* Initialize sortable for quota manager */
			initializeQuotaMgtPlugin();
			
		});
		
	</script>
	
	<style type="text/css"> 
	
		.ui-state-highlight{margin:10px !important;background-color: white;} 
	
	</style>