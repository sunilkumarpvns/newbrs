<%@page import="com.elitecore.elitesm.web.plugins.forms.CreateUniversalDiameterPluginForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>

<% String basePath = request.getContextPath(); %>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/togglebutton.css">

<script type="text/javascript" src="<%=request.getContextPath()%>/js/plugin/universal-diameter-plugin.js"></script>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>


<link rel="stylesheet" href="<%=request.getContextPath()%>/css/font/font-awesome.css" />

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<div id="main">
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td class="table-header">
											<bean:message bundle="pluginResources" key="plugin.universalpluginpolicies" />
										</td>
									</tr>
									<tr>
										<td class="" width="100%">
										    <html:form action="/createUniversalDiameterPlugin" styleId="uniAuthPlugin">
												
												<html:hidden name="createUniversalDiameterPluginForm" property="action" value="create" /> 
												<html:hidden name="createUniversalDiameterPluginForm" property="universalPluginPolicyJson" value="" styleId="universalPluginPolicyJson" />
												<html:hidden name="createUniversalDiameterPluginForm" property="pluginName" styleId="pluginName" />
												<html:hidden name="createUniversalDiameterPluginForm" property="description" styleId="description" />
												<html:hidden name="createUniversalDiameterPluginForm" property="pluginType" styleId="pluginType" />
												<html:hidden name="createUniversalDiameterPluginForm" property="status" styleId="status" />
												
												<table width="100%" cellspacing="0" cellpadding="0" border="0">
													<tr>
														<td class="labeltext">
															<div>
																<%@include file="/jsp/servermgr/plugins/universalplugin/UniversalDiameterPlugin.jsp" %>
															</div>
														</td>
													</tr>
												</table>
										 </html:form>
										</td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
	
	<link rel="stylesheet" href="<%=request.getContextPath()%>/js/calender/jquery-ui.css">
	<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js" ></script>
	<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>
	
	<script type="text/javascript">

	/** Setting title in main header bar*/
	setTitle('<bean:message bundle="pluginResources" key="universalplugin.title"/>'); 
	
	/** Adding default universal plugin in policy list into pre plugin list and post plugin list */
	addDefaultUniversalPlugin('plugin-mapping-table','plugin-pre-mapping-table-template','pre-param-list-tbl-mapping','pre-param-list-tbl-template');
	addDefaultUniversalPlugin('plugin-post-mapping-table','plugin-post-mapping-table-template','post-param-list-tbl-mapping','post-param-list-tbl-template');
	
	$(document).ready(function(){
		
		/* Initialized Default Universal Plugin */
		initializeUniversalPlugin();
	
	});
	
	</script> 
	
	<style type="text/css"> 
	
	.ui-state-highlight{
		margin:10px !important;
		background-color: white;
	} 
	
	</style>