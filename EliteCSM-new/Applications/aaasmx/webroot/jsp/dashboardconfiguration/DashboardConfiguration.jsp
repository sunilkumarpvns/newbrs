<%@page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterData" %>
<%@ page import="com.elitecore.elitesm.web.core.system.systemparameter.forms.ViewSystemParameterForm" %>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData" %>
<%@ page import="java.util.List" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.core.system.dashboardconfiguration.forms.DashboardConfigurationForm" %>
<% String basePath = request.getContextPath();%>
<%DashboardConfigurationForm dashboardConfigurationForm = (DashboardConfigurationForm) request.getAttribute("dashboardConfigurationForm"); %>

<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/dashboardmenu.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/managedashboard.css" />
<script type="text/javascript" src="<%=basePath%>/js/dashboard/dashboard-configuration.js"></script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
setTitle('<bean:message key="dashboardconfiguration.parameter"/>');
</script>
<script type="text/javascript">
	function validate(){
		if($('#databaseId').val() == '0'){
			alert('please select datasource from dropdown');
			return false;
		}else if(isEmpty($('#maxTabs').val())){
			alert('Maximum numbers of tabs must be specified');
			document.forms[0].secretkey.focus();
			return false;
		}else if($('#maxTabs').val() > 3){
			alert('You are not allowed to create more than 3 dashboard.');
			document.forms[0].maxTabs.focus();
			return false;
		}else if($('#maxTabs').val() < 1){
			alert('Minimum one dashboard is required.');
			document.forms[0].maxTabs.focus();
			return false;
		}
		else{
			document.forms[0].submit();
		}
	}
</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
	  <td style="padding-left: 9px;">
   		 <table cellpadding="0" cellspacing="0" border="0" width="100%">
		  		<%-- <tr>
					<td class="sDashboardWidgetHeaderBarMain ui-widget-header">
						<span class="manageheader">
							<bean:message bundle="dashboardResources" key="dashboard.configuration" />
						</span>
					</td>
				</tr> --%>
  		  	    <tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
				 	<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td valign="middle" colspan="3" >
							<table cellpadding="0" cellspacing="0" border="0" width="100%" >
								<tr>
									<td colspan="3"  style="padding-left: 20px;padding-top: 20px;padding-right: 20px;" >
										<html:form action="/dashboardConfiguration.do?method=saveDashboardConfiguration">
										 	<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" >
												<tr>
													<td class="ui-widget-header widgetheader" colspan="2" style="font-size: 12px;color: white;background-color: rgb(217, 230, 246);">		
														<bean:message bundle="dashboardResources" key="dashboard.general.configuration" />
													</td>
												</tr>
												<tr>
													<td colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td align="right" width="35%">
														<label class="dashboardLabel">
															<bean:message bundle="dashboardResources" key="dashboard.general.databaseds" /> :
														</label>
													</td>
													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="65%">
														<html:hidden property="dashboardConfigId" styleId="dashboardConfigId"/>
														<html:select property="databaseId" styleId="databaseId" style="width:250px" tabindex="1">
															<html:option value="0">--Select--</html:option>
															<html:optionsCollection name="dashboardConfigurationForm" property="databaseDSList" label="name" value="databaseId" />
														</html:select>
													</td>
												</tr>
												<tr>
													<td align="right"></td>
													<td align="left" style="padding-left: 5px;">	
														<label class="dashboardHelpTag">
															<bean:message bundle="dashboardResources" key="dashboard.general.databaseds.desc" />
														</label>	
													</td>
												</tr>
												<tr>
													<td align="right" width="35%">
														<label class="dashboardLabel">
															<bean:message bundle="dashboardResources" key="dashboard.general.maxtabs" /> :
														</label>
													</td>
													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="65%">
														<html:text property="maxTabs" styleId="maxTabs" style="width:250px"></html:text>
													</td>
												</tr>
												<tr>
													<td align="right"></td>
													<td align="left" style="padding-left: 5px;">	
														<label class="dashboardHelpTag">
															<bean:message bundle="dashboardResources" key="dashboard.general.maxtabs.desc" />
														</label>	
													</td>
												</tr>
												<tr>
													<td align="right" width="30%">
														<label class="dashboardLabel">
															<bean:message bundle="dashboardResources" key="dashboard.general.maxwebsocket" /> :
														</label>
													</td>
													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
															<html:text property="maxWebSockets" styleId="maxWebSockets" style="width:250px"></html:text>
													</td>
												</tr>
												<tr>
													<td align="right"></td>
													<td align="left" style="padding-left: 5px;">	
														<label class="dashboardHelpTag">
															<bean:message bundle="dashboardResources" key="dashboard.general.maxwebsocket.desc" /> 
														</label>	
													</td>
												</tr>
												<tr>
													<td align="right" width="30%">
														<label class="dashboardLabel">
															<bean:message bundle="dashboardResources" key="dashboard.general.maxconcurrentaccess" />: 
														</label>
													</td>
													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
															<html:text property="maxConcurrentAccess" styleId="maxConcurrentAccess" style="width:250px"></html:text>
													</td>
												</tr>
												<tr>
													<td align="right">
													</td>
													<td align="left" style="padding-left: 5px;">	
														<label class="dashboardHelpTag">
															<bean:message bundle="dashboardResources" key="dashboard.general.maxconcurrentaccess.desc" /> 
														</label>	
													</td>
												</tr>
												<tr>
													<td align="right" width="30%">
														<label class="dashboardLabel">
																<bean:message bundle="dashboardResources" key="dashboard.general.maxwidgetspertab" />: 
														</label>
													</td>
													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
														<html:text property="maxWidgets" styleId="maxWidgets" style="width:250px"></html:text>
													</td>
												</tr>
												<tr>
													<td align="right"></td>
													<td align="left" style="padding-left: 5px;">	
														<label class="dashboardHelpTag">
															<bean:message bundle="dashboardResources" key="dashboard.general.maxwidgetspertab.desc" />: 
														</label>	
													</td>
												</tr>
												<tr>
													<td colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td  align="center"></td>
													<td  align="left" style="padding-left: 50px;">
														<input type="button" id="saveButton" value="Save" class="light-btn" onclick="validate();"/>
													</td>
												</tr>
												<tr>
													<td colspan="2">&nbsp;</td>
												</tr>
										</table>
										</html:form>
									</td>
								</tr>
								<tr>
									<td valign="middle" colspan="3" >
										<table cellpadding="0" cellspacing="0" border="0" width="100%" >
											<tr>
												<td colspan="3"  style="padding-left: 20px;padding-right: 20px;padding-bottom: 20px" >
													 <table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="padding-bottom: 20px;" >
															<tr>
																<td class="ui-widget-header widgetheader" colspan="2" style="font-size: 12px;color: white;background-color: rgb(217, 230, 246);">		
																	<bean:message bundle="dashboardResources" key="dashboard.widget.configuration" />
																</td>
															</tr>
															<tr>
																<td colspan="2">&nbsp;</td>
															</tr>
															<tr>
																<td colspan="2" align="center">
																	<table width="80%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  >
																		<tr>
																			<td class="labeltext" style="font-size: 12px;color: #015198;font-weight: bold;">
																				<bean:message bundle="dashboardResources" key="dashboard.custom.widget" />
																			</td>
																		</tr>
																		<tr>
																			<td align="left" style="padding-left: 3px;" >
																				<label class="dashboardHelpTag">
																					<bean:message bundle="dashboardResources" key="dashboard.customwidget.configuration" />
																				</label>
																			</td>
																		</tr>
																		<%if(dashboardConfigurationForm.getCustomWidgetList() != null && dashboardConfigurationForm.getCustomWidgetList().size() > 0){ %>
																		<logic:iterate id="widgetTemplateData" name="dashboardConfigurationForm" property="customWidgetList" type="com.elitecore.elitesm.web.dashboard.json.WidgetTemplateData">
																		<tr>
																			<td align="left" style="padding-left: 3px;padding-top: 10px;">
																				<table  width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: rgb(238,238,238);padding: 5px;" >
																					<tr>
																						<td width="2%"><img src="<%=basePath%>/images/component.png"/></td>
																						<td  width="20%" class="labeltext" style="font-size: 12px;" onclick="showWidgetContent(${widgetTemplateData.widgteTemplateId});"><span  style="cursor: pointer;"><bean:write property="title" name="widgetTemplateData"/></span></td>
																						<td width="68%"><label class="dashboardHelpTag"><bean:write property="description" name="widgetTemplateData"/></label></td>
																						<td style="text-align: right;padding-right: 0;" width="10%">
																							<img src="<%=basePath%>/images/confedit.png" height="18px" width="18px" onclick="getCustomWidgetConf(${widgetTemplateData.widgteTemplateId});"/>
																						</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																			<td colspan="4"">
																				 <div  style="display: none;padding-left: 3px;" id="div${widgetTemplateData.widgteTemplateId}">
																			        <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="box" style="background-color: white;padding-left: 5px;">
																						<tr>
																							<td colspan="2">&nbsp;</td>
																						</tr>
																						<tr>
																							<td align="right" width="30%">
																								<label class="dashboardLabel">
																									<bean:message bundle="dashboardResources" key="dashboard.widget.refreshinterval" />:
																								</label>
																							</td>
																							<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																								<input type="text" id="${widgetTemplateData.widgteTemplateId}refreshInterval" name="refreshInterval" title="widgteName" class="dashboardTextClass" placeholder="Enter Refresh Interval">
																							</td>
																						</tr>
																						<tr>
																							<td align="right">
																							</td>
																							<td align="left" style="padding-left: 5px;">	
																								<label class="dashboardHelpTag">
																									<bean:message bundle="dashboardResources" key="dashboard.widget.refreshinterval.desc" />
																								</label>	
																							</td>
																						</tr>
																						<tr>
																							<td align="right" width="30%">
																								<label class="dashboardLabel">
																									<bean:message bundle="dashboardResources" key="dashboard.widget.active" />:
																								</label>
																							</td>
																							<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																								<select id="${widgetTemplateData.widgteTemplateId}active" NAME="active">
																										<option value="Yes">Yes</option>
																										<option value="No">No</option>
																								</select>
																							</td>
																						</tr>
																						<tr>
																							<td align="right">
																							</td>
																							<td align="left" style="padding-left: 5px;">	
																									<label class="dashboardHelpTag">
																										<bean:message bundle="dashboardResources" key="dashboard.widget.active.desc" />
																									</label>	
																							</td>
																						</tr>
																						<tr>
																							<td align="right" colspan="2">&nbsp;
																							</td>
																						</tr>
																						<tr>
																							<td align="right">
																							</td>
																							<td align="left" style="padding-left: 5px;">	
																								<input type="button" class="light-btn" value="Save" id="${widgetTemplateData.widgteTemplateId}SaveBtn" onclick="saveWidgetConf(${widgetTemplateData.widgteTemplateId});"/>
																								<input type="button" class="light-btn" value="Cancel" id="${widgetTemplateData.widgteTemplateId}CancelBtn" onclick="cancelWidgetConf(${widgetTemplateData.widgteTemplateId})"/>
																							</td>
																						</tr>
																						<tr>
																								<td align="right" colspan="2">&nbsp;
																								</td>
																						</tr>
																						</table>
																					</div>
																				</td>
																		</tr>
																		
																		<%-- START : Edit Configuration page --%>
																		
																		<tr>
																			<td colspan="4"">
																				 <div  style="display: none;padding-left: 3px;" id="editdiv${widgetTemplateData.widgteTemplateId}">
																			        <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="box" style="background-color: white;padding-left: 5px;">
																						<tr>
																								<td colspan="2">&nbsp;</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																														 	<bean:message bundle="dashboardResources" key="dashboard.customwidget.name" />:
																														 </label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<input type="text" name="WIDGETNAME" id="title${widgetTemplateData.widgteTemplateId}" style="width:250px"/> 
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.name.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.desc" />:
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<textarea rows="5" cols="40" name="WIDGETDESC" id="description${widgetTemplateData.widgteTemplateId}" style="width:250px"></textarea>
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.desc.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.thumbnail" />:
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<input type="text" name="WIDGETTHUMBNAIL" id="thumbnail${widgetTemplateData.widgteTemplateId}" style="width:250px"/> 
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.thumbnail.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetjsp" />: 
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<input type="text" name="WIDGETJSP" id="jspUrl${widgetTemplateData.widgteTemplateId}" style="width:250px"/> 
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetjsp.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel"><bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetconfjsp" /> : </label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<input type="text" name="WIDGETCONFJSP" id="configJspUrl${widgetTemplateData.widgteTemplateId}" style="width:250px"/> 
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																																<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetconfjsp.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetgroovy" /> : 
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<input type="text" name="WIDGETGROOVY" id="widgetGroovy${widgetTemplateData.widgteTemplateId}" style="width:250px"/> 
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetgroovy.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td>
																														&nbsp;
																													</td>
																												</tr>
																												<tr>
																													<td align="left" colspan="2" style="padding-left: 300px;">
																														<input type="button" class="light-btn" value="Update" style="background-color: rgb(133, 180, 231);" onclick="updateCustomWidget(${widgetTemplateData.widgteTemplateId});" />
																														<input type="button" class="light-btn" value="Cancel" style="background-color: rgb(133, 180, 231);" onclick="cancelCustomWidget(${widgetTemplateData.widgteTemplateId});" />
																													</td>
																												</tr>
																												<tr>
																													<td colspan="2">&nbsp;</td>
																												</tr>
																						</table>
																					</div>
																				</td>
																		</tr>
																		
																		
																		<%-- END : Configuration page --%>
																		
																		</logic:iterate>
																		<%} %>
																		<tr>
																			<td  style="padding-left: 3px;padding-top: 10px;">
																				<table id="customWidgetTable" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: rgb(238,238,238);padding: 5px;" >
																					<tr>
																						<td width="2%"><img src="<%=basePath%>/images/component.png"/></td>
																						<td width="20%" class="labeltext" style="font-size: 12px;">
																							<span onclick="customWidget();" style="cursor: pointer;">
																								<bean:message bundle="dashboardResources" key="dashboard.create.customwidget" />
																							</span>
																						</td>
																						<td width="68%">
																							<label class="dashboardHelpTag">
																								<bean:message bundle="dashboardResources" key="dashboard.create.customwidget.dashboard" />
																							</label>
																						</td>
																						<td style="text-align: right;padding-right: 0;">
																							<img src="<%=basePath%>/images/myimage.png" height="20px" width="20px" style="display: none;" id="collapseId" onclick="hideTable();"/>
																						</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																		<tr>
																				<td align="left" style="padding-left: 3px;">
																					<div id="customWidget" style="display: none;">
																					<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: white;padding: 5px;" >
																						<tr>
																							<td align="center">
																									<html:form action="/dashboardConfiguration.do?method=createCustomWidget">
																										 <table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" >
																												<tr>
																													<td colspan="2">&nbsp;</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.name" />
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<html:text property="widgetName" styleId="widgetName" style="width:250px"></html:text>
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.name.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.desc" />:
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<html:textarea property="widgetDesc" styleId="widgetDesc" style="width:250px" rows="3" cols="40"></html:textarea>
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.desc.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.thumbnail" />: 
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<html:text property="widgetThumbnail" styleId="widgetThumbnail" style="width:250px"></html:text>
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.thumbnail.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetjsp" />: 
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<html:text property="widgetJsp" styleId="widgetJsp" style="width:250px"></html:text>
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetjsp.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetconfjsp" />:
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<html:text property="widgetConfJsp" styleId="widgetConfJsp" style="width:250px"></html:text>
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetconfjsp.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td align="right" width="30%">
																														<label class="dashboardLabel">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetgroovy" />:
																														</label>
																													</td>
																													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																														<html:text property="widgetGroovy" styleId="widgetGroovy" style="width:250px"></html:text>
																													</td>
																												</tr>
																												<tr>
																													<td align="right">
																													</td>
																													<td align="left" style="padding-left: 5px;">	
																														<label class="dashboardHelpTag">
																															<bean:message bundle="dashboardResources" key="dashboard.customwidget.widgetgroovy.desc" />
																														</label>	
																													</td>
																												</tr>
																												<tr>
																													<td>
																														&nbsp;
																													</td>
																												</tr>
																												<tr>
																													<td align="left" colspan="2" style="padding-left: 300px;">
																														<html:submit value="Add" styleClass="light-btn" style="background-color: rgb(133, 180, 231);"></html:submit>
																														<input type="button" class="light-btn" value="Cancel" style="background-color: rgb(133, 180, 231);" onclick="hideTable();"oncl/>
																													</td>
																												</tr>
																												<tr>
																													<td colspan="2">&nbsp;</td>
																												</tr>
																										</table>
																							    </html:form>
																							</td>
																						</tr>
																					</table>
																					</div>
																				</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr>
																<td colspan="2">&nbsp;</td>
															</tr>
															<tr>
																<td colspan="2" align="center">
																	<table width="80%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  >
																		<tr>
																			<td class="labeltext" style="font-size: 12px;color: #015198;font-weight: bold;">
																				<bean:message bundle="dashboardResources" key="dashboard.system.widgets" />
																			</td>
																		</tr>
																		<tr>
																			<td align="left" style="padding-left: 3px;" >
																			<label class="dashboardHelpTag">
																				<bean:message bundle="dashboardResources" key="dashboard.system.widgets.desc" />
																			 </label>
																			 </td>
																		</tr>
																		<tr>
																			<td align="left" style="padding-left: 3px;padding-top: 10px;">
																				<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: rgb(238,238,238);padding: 5px;border-color: pink;" >
																					<tr>
																						<td class="dashboardHelpTag" colspan="3">
																							<bean:message bundle="dashboardResources" key="dashboard.system.widget.details" />
																						</td>
																					</tr>
																					<tr>
																						<td colspan="3">&nbsp;</td>
																					</tr>
																					<tr>
																						<td colspan="3" style="color: #015198;cursor:pointer;font-weight: bold;font-size: 11px;text-decoration: underline;">
																							<span onclick="enableTable();">
																								<bean:message bundle="dashboardResources" key="dashboard.system.showhide" />
																							</span>
																						</td>
																					</tr>
																				</table>
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
															
															<tr>
																<td colspan="2">&nbsp;</td>
															</tr>
															<tr>
																<td colspan="2">
																		<div id="widgetDiv" style="display: none;">
																								<table width="80%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  align="center" id="systemWidgetTable">
																									<%if(dashboardConfigurationForm.getWidgetTemplateDataList() != null && dashboardConfigurationForm.getWidgetTemplateDataList().size() > 0){ %>																			<%if(dashboardConfigurationForm.getCustomWidgetList() != null && dashboardConfigurationForm.getCustomWidgetList().size() > 0) %>
																											<logic:iterate id="widgetTemplateData" name="dashboardConfigurationForm" property="widgetTemplateDataList" type="com.elitecore.elitesm.web.dashboard.json.WidgetTemplateData">
																											<tr>
																												<td align="left" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="left" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="dashboardbox" style="background-color:#85B4E7;padding: 2px;" >
																														<tr class="nestedRow">
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="35%" class="labeltext" style="font-size: 12px;" align="left" onclick="showWidgetContent(${widgetTemplateData.widgteTemplateId});">
																																<span  style="cursor: pointer;">
																																	<bean:write name="widgetTemplateData" property="title" />
																																</span>
																															</td>
																															<td width="55%">
																																<label class="dashboardHelpTag" align="left">
																																	<bean:write name="widgetTemplateData" property="description" />
																																</label>
																															</td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked" />
																															</td>
																														</tr>
																														<tr>
																															<td colspan="4">
																																<div  style="display: none;" id="div${widgetTemplateData.widgteTemplateId}">
																																	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="dashboardbox" style="background-color: white;">
																																		<tr>
																																			<td colspan="2">&nbsp;</td>
																																		</tr>
																																		<tr>
																																			<td align="right" width="30%">
																																				<label class="dashboardLabel">
																																					<bean:message bundle="dashboardResources" key="dashboard.widget.refreshinterval" /> :
																																				</label>
																																			</td>
																																			<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																																				<input type="text" id="${widgetTemplateData.widgteTemplateId}refreshInterval" name="refreshInterval" title="widgteName" class="dashboardTextClass" placeholder="Enter Refresh Interval">
																																			</td>
																																		</tr>
																																		<tr>
																																			<td align="right">
																																			</td>
																																			<td align="left" style="padding-left: 5px;">	
																																				<label class="dashboardHelpTag">
																																					<bean:message bundle="dashboardResources" key="dashboard.widget.refreshinterval.desc" />
																																				</label>	
																																			</td>
																																		</tr>
																																		<tr>
																																			<td align="right" width="30%">
																																				<label class="dashboardLabel">
																																					<bean:message bundle="dashboardResources" key="dashboard.widget.active" />:
																																				</label>
																																			</td>
																																			<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																																				<select id="${widgetTemplateData.widgteTemplateId}active" NAME="active">
																																					<option value="Yes">Yes</option>
																																					<option value="No">No</option>
																																				</select>
																																			</td>
																																		</tr>
																																		<tr>
																																			<td align="right">
																																			</td>
																																			<td align="left" style="padding-left: 5px;">	
																																				<label class="dashboardHelpTag">
																																					<bean:message bundle="dashboardResources" key="dashboard.widget.active.desc" />
																																				</label>	
																																			</td>
																																		</tr>
																																		<tr>
																																			<td align="right" colspan="2">&nbsp;
																																			</td>
																																		</tr>
																																		<tr>
																																			<td align="right">
																																			</td>
																																			<td align="left" style="padding-left: 5px;">	
																																				<input type="button" class="light-btn" value="Save" id="${widgetTemplateData.widgteTemplateId}SaveBtn" onclick="saveWidgetConf(${widgetTemplateData.widgteTemplateId});"/>
																																				<input type="button" class="light-btn" value="Cancel" id="${widgetTemplateData.widgteTemplateId}CancelBtn" onclick="cancelWidgetConf(${widgetTemplateData.widgteTemplateId})"/>
																																			</td>
																																		</tr>
																																		<tr>
																																			<td align="right" colspan="2">&nbsp;
																																			</td>
																																		</tr>
																																	</table>
																																</div>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											</logic:iterate>
																											<%} %>	
																											<tr>
																												<td colspan="4">&nbsp;</td>
																											</tr>
																										</table>
																					</div>
																</td>
															</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
				</table>
			</td>
		</tr>
	</table>
</td>
</tr>
<tr>
	<td><%@ include file="/jsp/core/includes/common/Footer.jsp" %></td>
</tr>
</table>
