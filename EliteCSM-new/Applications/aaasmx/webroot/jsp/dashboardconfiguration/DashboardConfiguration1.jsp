<%@page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.SystemParameterData" %>
<%@ page import="com.elitecore.elitesm.web.core.system.systemparameter.forms.ViewSystemParameterForm" %>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData" %>
<%@ page import="java.util.List" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/calender/jquery-ui.css"> 
<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js"></script>
<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js"></script>
<script>
setTitle('<bean:message key="dashboardconfiguration.parameter"/>');
</script>
<script>
var fadeIn=true;
function enableTable(){
	if(fadeIn == true){
		$('#widgetDiv').fadeIn(2000);
		fadeIn=false;
	}else{
		$('#widgetDiv').css('display','none');
		fadeIn=true;
	}
	
}
function customWidget(){
	$('#collapseId').css('display','block');
	$('#customWidgetTable').css('background-color','#85B4E7');
	$('#customWidget').fadeIn(2000);
}
function hideTable(){
	$('#customWidgetTable').css('background-color','#EEEEEE');
	$('#customWidget').css('display','none');
	$('#collapseId').css('display','none');
}

</script>
 <script>
$(function() {
	$( "#tabs" ).tabs();
});
</script>
<style type="text/css">
#widgetItems tr:hover{ background-color:#709FD1;cursor: pointer; }
.ui-tabs .ui-tabs-nav{
height:25px;
}
.ui-helper-reset{
line-height: 0.8;
}
.ui-widget-header {
background-color: rgb(238,238,238);
}
.ui-tabs .ui-tabs-nav li{
margin:0;
}
</style>
<% String basePath = request.getContextPath();%>

<html:form action="/viewSystemParameter">
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
  			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  				&nbsp;
  			</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td class="table-header" colspan="4" style="font-size: 14px;color: rgb(60, 120, 181);background-color: rgb(238,238,238);">		
							<bean:message key="dashboardconfiguration.general" />
						</td>
					</tr>
					
					<tr>
						<td valign="middle" colspan="3" >
							<table cellpadding="0" cellspacing="0" border="0" width="100%" >
								<tr>
									<td colspan="3"  style="padding-left: 20px;padding-top: 20px;padding-right: 20px;" >
										 <table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" >
												<tr>
													<td class="table-header" colspan="2" style="font-size: 12px;color: rgb(60, 120, 181);background-color: rgb(238,238,238);">		
														Dashboard General Configuration
													</td>
												</tr>
												<tr>
													<td colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td align="right" width="35%">
														<label class="dashboardLabel">Max. No of Tabs :</label>
													</td>
													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="65%">
															<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="5">
													</td>
												</tr>
												<tr>
													<td align="right">
													</td>
													<td align="left" style="padding-left: 5px;">	
														<label class="dashboardHelpTag">Maximum no of Tabs for Dashboard</label>	
													</td>
												</tr>
												<tr>
													<td align="right" width="30%">
														<label class="dashboardLabel">Max. No of WebSockets : </label>
													</td>
													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
															<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="50">
													</td>
												</tr>
												<tr>
													<td align="right">
													</td>
													<td align="left" style="padding-left: 5px;">	
														<label class="dashboardHelpTag">Maximum no of Websockets</label>	
													</td>
												</tr>
												<tr>
													<td align="right" width="30%">
														<label class="dashboardLabel">Max. No of Concurrent Access : </label>
													</td>
													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
															<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="2">
													</td>
												</tr>
												<tr>
													<td align="right">
													</td>
													<td align="left" style="padding-left: 5px;">	
														<label class="dashboardHelpTag">Maximum  No of Concurrent Access from single IP</label>	
													</td>
												</tr>
												<tr>
													<td align="right" width="30%">
														<label class="dashboardLabel">Max. widgets to show per tab : </label>
													</td>
													<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
															<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" value="7">
													</td>
												</tr>
												<tr>
													<td align="right">
													</td>
													<td align="left" style="padding-left: 5px;">	
														<label class="dashboardHelpTag">Maximum widgets to show per tab</label>	
													</td>
												</tr>
												<tr>
													<td colspan="2">&nbsp;</td>
												</tr>
												<tr>
													<td  align="center">
													</td>
													<td  align="left" style="padding-left: 50px;">
														<input type="button" value="Save"> 
													</td>
												</tr>
												<tr>
													<td colspan="2">&nbsp;</td>
												</tr>
										</table>
									</td>
								</tr>
								
								<tr>
									<td valign="middle" colspan="3" >
										<table cellpadding="0" cellspacing="0" border="0" width="100%" >
											<tr>
												<td colspan="3"  style="padding: 20px;">
													 <table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" >
														<tr>
																<td class="table-header" colspan="2" style="font-size: 12px;color: rgb(60, 120, 181);background-color: rgb(238,238,238);">		
																	Widgets Configuration
																</td>
															</tr>
															<tr>
																<td colspan="2">&nbsp;</td>
															</tr>
														 <tr>
														 	<td align="center" style="padding-bottom:20px;">
															 	<div id="tabs" style="width: 80%;" align="center">
																	<ul>
																		<li>
																			<a href="#tabs-1">
																				<span style="color: rgb(60, 120, 181);font-weight: bold;">Custom Widget</span>
																			</a>
																		</li>
																		<li>
																			<a href="#tabs-2">
																				<span style="color: rgb(60, 120, 181);font-weight: bold;">System Widget</span>
																			</a>
																		</li>
																    </ul>
																	<div id="tabs-1">
																			 <table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  >
																				<tr>
																					<td>
																						<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  >
																							
																								<tr>
																								<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 3px;">
																									<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: rgb(238,238,238);padding: 5px;" >
																										<tr>
																											<td width="2%"><img src="<%=basePath%>/images/component.png"/></td>
																											<td width="20%" class="labeltext" style="font-size: 12px;"><span  style="cursor: pointer;">Groovy Widget 1</span></td>
																											<td width="68%"><label class="dashboardHelpTag">Maximum no of Tabs for Dashboard</label></td>
																											<td width="10%" style="text-align: right;padding-right: 0;">
																												<img src="<%=basePath%>/images/myimage.png" height="20px" width="20px" style="display: none;"  />
																											</td>
																										</tr>
																									</table>
																								</td>
																							</tr>
																							<tr>
																								<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 3px;">
																									<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: rgb(238,238,238);padding: 5px;" >
																										<tr>
																											<td width="2%"><img src="<%=basePath%>/images/component.png"/></td>
																											<td width="20%" class="labeltext" style="font-size: 12px;"><span  style="cursor: pointer;">Groovy Widget 1</span></td>
																											<td width="68%"><label class="dashboardHelpTag">Maximum no of Tabs for Dashboard</label></td>
																											<td width="10%" style="text-align: right;padding-right: 0;">
																												<img src="<%=basePath%>/images/myimage.png" height="20px" width="20px" style="display: none;"  />
																											</td>
																										</tr>
																									</table>
																								</td>
																							</tr>
																							<tr>
																								<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 3px;">
																									<table id="customWidgetTable" align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: rgb(238,238,238);padding: 5px;" >
																										<tr>
																											<td width="2%"><img src="<%=basePath%>/images/component.png"/></td>
																											<td width="20%" class="labeltext" style="font-size: 12px;"><span onclick="customWidget();" style="cursor: pointer;">Create your Custom Widget</span></td>
																											<td width="68%"><label class="dashboardHelpTag">Maximum no of Tabs for Dashboard</label></td>
																											<td width="10%" style="text-align: right;padding-right: 0;">
																												<img src="<%=basePath%>/images/myimage.png" height="20px" width="20px" style="display: none;" id="collapseId" onclick="hideTable();"/>
																											</td>
																										</tr>
																									</table>
																								</td>
																							</tr>
																							<tr>
																								<td align="left" style="padding-left: 10px;padding-right: 10px;padding-bottom: 10px;"  >
																									 <div id="customWidget" style="display: none;">
																												<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"   style="background-color: white;" >
																													<tr>
																														<td align="center">
																															 <table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" >
																																	<tr>
																																		<td align="right" width="30%">
																																			<label class="dashboardLabel">Widget Name :</label>
																																		</td>
																																		<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%" class="labeltext">
																																				<input type="text" id="widgteName"  name="widgteName" title="widgteName" class="dashboardTextClass" placeholder="Enter your Widget Name">
																																		</td>
																																	</tr>
																																	<tr>
																																		<td align="right">
																																		</td>
																																		<td align="left" style="padding-left: 5px;">	
																																			<label class="dashboardHelpTag">Enter your Widget Name</label>	
																																		</td>
																																	</tr>
																																	<tr>
																																		<td align="right" width="30%">
																																			<label class="dashboardLabel">Widget Thumbnail : </label>
																																		</td>
																																		<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%" class="labeltext">
																																				<input type="file"/>
																																		</td>
																																	</tr>
																																	<tr>
																																		<td align="right">
																																		</td>
																																		<td align="left" style="padding-left: 5px;">	
																																			<label class="dashboardHelpTag">Browse your file</label>	
																																		</td>
																																	</tr>
																																	<tr>
																																		<td align="right" width="30%">
																																			<label class="dashboardLabel">Widgte Jsp : </label>
																																		</td>
																																		<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%" class="labeltext">
																																				<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" placeholder="Enter your Jsp file path">
																																		</td>
																																	</tr>
																																	<tr>
																																		<td align="right">
																																		</td>
																																		<td align="left" style="padding-left: 5px;">	
																																			<label class="dashboardHelpTag">Enter your JSP Path</label>	
																																		</td>
																																	</tr>
																																	<tr>
																																		<td align="right" width="30%">
																																			<label class="dashboardLabel">Widgte Grrovy : </label>
																																		</td>
																																		<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%" class="labeltext">
																																				<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass"  placeholder="Enter your Groovy file path">
																																		</td>
																																	</tr>
																																	<tr>
																																		<td align="right">
																																		</td>
																																		<td align="left" style="padding-left: 5px;">	
																																			<label class="dashboardHelpTag">Enter your Groovy Path</label>	
																																		</td>
																																	</tr>
																																	<tr>
																																		<td>
																																			&nbsp;
																																		</td>
																																	</tr>
																																	<tr>
																																		<td align="left" colspan="2" style="padding-left: 300px;">
																																			<input type="button" value="Add" style="background-color: rgb(133, 180, 231);font-size: 12px;" class="labeltext" />
																																			<input type="button" value="Cancel" style="background-color: rgb(133, 180, 231);font-size: 12px;" class="labeltext"/>
																																		</td>
																																	</tr>
																																	<tr>
																																		<td colspan="2">&nbsp;</td>
																																	</tr>
																															</table>
																														
																														</td>
																													</tr>
																												</table>
																												</div> 
																												</td>
																												</tr>
																											</table>
																										</td>
																									</tr>
																								</table>
																	</div>
																	<div id="tabs-2">
																	<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  >
																				<tr>
																					<td>
																						<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  >
																							<tr>
																								<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 10px;">
																									<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: rgb(238,238,238);padding: 5px;border-color: pink;" >
																										<tr>
																											<td class="dashboardHelpTag" colspan="3">This Plugins are Integral part of your system Dashboard.They can not be uninstalled. Disabling or Removing them will have serious effetc,and may render Dashboard inoperable.Do not make changes here unless instricted by Admin Support.</td>
																										</tr>
																										<tr>
																											<td colspan="3">&nbsp;</td>
																										</tr>
																										<tr>
																											<td colspan="3" style="color: #015198;cursor:pointer;font-weight: bold;font-size: 11px;text-decoration: underline;">
																												<span onclick="enableTable();">Show/Hide Dashboard Widget</span>
																											</td>
																										</tr>
																									</table>
																								</td>
																							</tr>
																							<tr>
																								<td>&nbsp;</td>
																							</tr>
																							<tr>
																								<td align="left">
																									<div id="widgetDiv" style="display: none;">
																										<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  >
																											<tr>
																												<td align="left" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="left" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;"><span  style="cursor: pointer;">Reply Messages</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display the total number of reply messages generated by EliteAAA.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											<tr>
																												<td align="left" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="left" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Authentication Service</span></td>
																															<td width="68%"><label class="dashboardHelpTag" align="left">Display Authentication Counters for Multiple EliteAAA Instances.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											<tr>
																												<td align="left" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="left" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Accounting Service</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display Accounting Counters for Multiple EliteAAA Instances.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											<tr>
																												<td align="left" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="left" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span style="cursor: pointer;">ESI Request Summary</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display statistics of all Radius ESI.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											<tr>
																												<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Radius External System</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display Graph of Success,Failure and timeout request for configured ESI.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											<tr>
																												<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top:2px;">
																													<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Clients Statistics</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display total request statistics for all clients configured in AAA.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											<tr>
																												<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Result-Code Statistics</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display graph of result-code categories detail per realm.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											<tr>
																												<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Peer Statistics</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display statistics of all Diameter Peers.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											<tr>
																												<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Realm Statistics</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display statistics of Diameter Request/Answer per realm.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											<tr>
																												<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Server</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display graph of CPU,Memory,TPS and Thread of configured Instance.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											<tr>
																												<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Alerts</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display list of alerts generated by All the instances.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											<tr>
																												<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">High Response Time</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display graph of high response time respective to timestamp.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											<tr>
																												<td align="center" style="padding-left: 10px;padding-right: 10px;padding-top: 2px;">
																													<table  align="center" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color:#85B4E7;padding: 2px;" >
																														<tr>
																															<td width="2%" style="padding-left: 5px;padding-right: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																															<td width="18%" class="labeltext" style="font-size: 12px;" align="left"><span  style="cursor: pointer;">Dead/Alive Details</span></td>
																															<td width="68%"><label class="dashboardHelpTag">Display graph of total number of dead/alive of configured ESI with timestamp.</label></td>
																															<td style="text-align: right;padding-right: 0;" width="10%">
																																<input type="checkbox" checked="checked"/>
																															</td>
																														</tr>
																													</table>
																												</td>
																											</tr>
																											
																											
																											
																										</table>
																									
																									
																									
																									<%-- <div style="text-align: right;"><img src="<%=basePath%>/images/minus.jpg"/></div> --%>
																									<%-- <table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box"  id="widgetItems" style="background-color: rgb(238,238,238);">
																										
																										
																										
																										
																										
																										
																										
																										
																										
																										<tr  bgcolor="#85B4E7">
																											<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																											<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Alerts</td>
																											<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display list of alerts generated by All the instances.</td>
																											<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																										</tr>
																										<tr  bgcolor="#85B4E7">
																											<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																											<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">High Response Time</td>
																											<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display graph of high response time respective to timestamp.</td>
																											<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																										</tr>
																										<tr  bgcolor="#85B4E7">
																											<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																											<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Dead/Alive Details</td>
																											<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display graph of total number of dead/alive of configured ESI with timestamp.</td>
																											<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																										</tr>
																									</table> --%>
																									</div>
																												</td>
																												</tr>
																											</table>
																										</td>
																									</tr>
																								</table>
																	
																	
																	</div>
																</div>
														 	</td>
														 </tr>
													 </table>
												</td>
												
												<%-- <td colspan="3"  style="padding: 20px;" >
													 <table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" >
															<tr>
																<td class="table-header" colspan="2" style="font-size: 12px;color: rgb(60, 120, 181);background-color: rgb(238,238,238);">		
																	Widgets Configuration
																</td>
															</tr>
															<tr>
																<td colspan="2">&nbsp;</td>
															</tr>
															<tr>
																<td colspan="2" align="center">
																	<table width="80%" type="tbl-list" border="0" cellpadding="0" cellspacing="0"  >
																		<tr>
																			<td class="labeltext" style="font-size: 12px;color: #015198;font-weight: bold;">Custom Widgets</td>
																		</tr>
																		<tr>
																			<td align="left" style="padding-left: 3px;" ><label class="dashboardHelpTag">Configure Custom widgets for Dashboard </label></td>
																		</tr>
																		<tr>
																			<td align="left" style="padding-left: 3px;padding-top: 10px;">
																				<table id="customWidgetTable" width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: rgb(238,238,238);padding: 5px;" >
																					<tr>
																						<td><img src="<%=basePath%>/images/component.png"/></td>
																						<td class="labeltext" style="font-size: 12px;"><span onclick="customWidget();" style="cursor: pointer;">Create your Custom Widget</span></td>
																						<td><label class="dashboardHelpTag">Maximum no of Tabs for Dashboard</label></td>
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
																								 <table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" >
																										<tr>
																											<td colspan="2">&nbsp;</td>
																										</tr>
																										<tr>
																											<td align="right" width="30%">
																												<label class="dashboardLabel">Widget Name :</label>
																											</td>
																											<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																													<input type="text" id="widgteName" name="widgteName" title="widgteName" class="dashboardTextClass" placeholder="Enter your Widget Name">
																											</td>
																										</tr>
																										<tr>
																											<td align="right">
																											</td>
																											<td align="left" style="padding-left: 5px;">	
																												<label class="dashboardHelpTag">Enter your Widget Name</label>	
																											</td>
																										</tr>
																										<tr>
																											<td align="right" width="30%">
																												<label class="dashboardLabel">Widget Thumbnail : </label>
																											</td>
																											<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																													<input type="file"/>
																											</td>
																										</tr>
																										<tr>
																											<td align="right">
																											</td>
																											<td align="left" style="padding-left: 5px;">	
																												<label class="dashboardHelpTag">Browse your file</label>	
																											</td>
																										</tr>
																										<tr>
																											<td align="right" width="30%">
																												<label class="dashboardLabel">Widgte Jsp : </label>
																											</td>
																											<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																													<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass" placeholder="Enter your Jsp file path">
																											</td>
																										</tr>
																										<tr>
																											<td align="right">
																											</td>
																											<td align="left" style="padding-left: 5px;">	
																												<label class="dashboardHelpTag">Enter your JSP Path</label>	
																											</td>
																										</tr>
																										<tr>
																											<td align="right" width="30%">
																												<label class="dashboardLabel">Widgte Grrovy : </label>
																											</td>
																											<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
																													<input type="text" id="d-fname" name="d-fname" title="To date" class="dashboardTextClass"  placeholder="Enter your Groovy file path">
																											</td>
																										</tr>
																										<tr>
																											<td align="right">
																											</td>
																											<td align="left" style="padding-left: 5px;">	
																												<label class="dashboardHelpTag">Enter your Groovy Path</label>	
																											</td>
																										</tr>
																										<tr>
																											<td>
																												&nbsp;
																											</td>
																										</tr>
																										<tr>
																											<td align="left" colspan="2" style="padding-left: 300px;">
																												<input type="button" value="Add" style="background-color: rgb(133, 180, 231);"/>
																												<input type="button" value="Cancel" style="background-color: rgb(133, 180, 231);"/>
																											</td>
																										</tr>
																										<tr>
																											<td colspan="2">&nbsp;</td>
																										</tr>
																								</table>
																							
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
																			<td class="labeltext" style="font-size: 12px;color: #015198;font-weight: bold;">System Widgets</td>
																		</tr>
																		<tr>
																			<td align="left" style="padding-left: 3px;" ><label class="dashboardHelpTag">Configure Custom widgets for Dashboard </label></td>
																		</tr>
																		<tr>
																			<td align="left" style="padding-left: 3px;padding-top: 10px;">
																				<table width="100%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box" style="background-color: rgb(238,238,238);padding: 5px;border-color: pink;" >
																					<tr>
																						<td class="dashboardHelpTag" colspan="3">This Plugins are Integral part of your system Dashboard.They can not be uninstalled. Disabling or Removing them will have serious effetc,and may render Dashboard inoperable.Do not make changes here unless instricted by Admin Support.</td>
																					</tr>
																					<tr>
																						<td colspan="3">&nbsp;</td>
																					</tr>
																					<tr>
																						<td colspan="3" style="color: #015198;cursor:pointer;font-weight: bold;font-size: 11px;text-decoration: underline;">
																							<span onclick="enableTable();">Show/Hide Dashboard Widget</span>
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
																<td colspan="2" align="center">
																		<div id="widgetDiv" style="display: none;">
																							<div style="text-align: right;"><img src="<%=basePath%>/images/minus.jpg"/></div>
																							<table width="80%" type="tbl-list" border="0" cellpadding="0" cellspacing="0" class="box"  id="widgetItems" style="background-color: rgb(238,238,238);">
																								<tr bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Reply Messages</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display the total number of reply messages generated by EliteAAA.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Authentication Service</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display Authentication Counters for Multiple EliteAAA Instances.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Accounting Service</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display Accounting Counters for Multiple EliteAAA Instances.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">ESI Request Summary</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display statistics of all Radius ESI.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Radius External System</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display Graph of Success,Failure and timeout request for configured ESI.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Clients Statistics</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display total request statistics for all clients configured in AAA.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Result-Code Statistics</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display graph of result-code categories detail per realm.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Peer Statistics</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display statistics of all Diameter Peers.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Realm Statistics</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display statistics of Diameter Request/Answer per realm.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Server</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display graph of CPU,Memory,TPS and Thread of configured Instance.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Alerts</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display list of alerts generated by All the instances.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">High Response Time</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display graph of high response time respective to timestamp.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																								<tr  bgcolor="#85B4E7">
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;padding-left: 5px;"><img src="<%=basePath%>/images/component.png"/></td>
																									<td style="padding: 2;border-bottom: 1px solid #CCCCCC;">Dead/Alive Details</td>
																									<td class="dashboardHelpTag" style="padding: 2;border-bottom: 1px solid #CCCCCC;">Display graph of total number of dead/alive of configured ESI with timestamp.</td>
																									<td style="text-align: right;padding: 2;border-bottom: 1px solid #CCCCCC;"><input type="checkbox" checked="checked"/></td>
																								</tr>
																							</table>
																							</div>
																</td>
															</tr>
													</table>
												</td> --%>
											</tr>
										</table>
									</td>
								</tr>
					 <tr>
                        <td  colspan="3" >&nbsp;</td>
                    </tr> 
				</table>
			</td>
		</tr>
		<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
	</table>
</td>
</tr>
</table>
</html:form>