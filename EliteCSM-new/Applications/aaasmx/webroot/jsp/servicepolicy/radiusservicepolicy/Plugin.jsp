<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/popcalendar.css" >
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@taglib prefix="ec" uri="/elitetags" %>
<html>
<head>
<%
	String widgetId=request.getParameter("widgetId");
	String isAdditional=request.getParameter("isAdditional");
	String orderNumber=request.getParameter("orderNumber");
	
	if(isAdditional.equalsIgnoreCase("true")){
		   widgetId=widgetId+"_additional";
	}else{
		   widgetId=widgetId+"_authentication";
	}
%>
</head>
<body>
<form id="form_plugin_<%=widgetId%>" name="form_plugin_<%=widgetId%>" class="form_pluginHandler">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>

<table name="tblmPlugin" width="100%" border="0" style="background-color: white;" class="handler-class tblmPlugin-container" cellspacing="0" cellpadding="0">
	<tr  style="cursor: pointer;">
		<td align="left" class="tbl-header-bold sortableAdditionalClass" valign="top" colspan="3">
			<table name="tblmPlugin" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" class="tbl-header-bold" valign="top" colspan="3">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td width="96%" lign="left" class="tbl-header-bold" valign="top">
									<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.plugin" />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled"/>
									<input type="hidden" id="pluginHiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.plugin" />" />
									<input type="hidden" id="pluginHandlerType_<%=widgetId%>" name="handlerType" class="handlerType" value="Plugin" />
								</td>
								<td class="editable_icon_bg">
							
									<span class="edit_handler_icon" onclick="changeHandlerName(this);" title="Edit Handler Name"></span>
									<span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;"></span>
								</td>
								<td width="1%" align="left" class="tbl-header-bold" valign="middle" style="padding-right: 2px;line-height: 9px;">
									<div class="switch">
									  <input id="toggle-pluginhandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);"/>
									  <label for="toggle-pluginhandler_<%=widgetId%>"></label>
									</div>
								</td>
								<td width="1%" style="padding-right: 5px;" class="tbl-header-bold" valign="middle">
									<img alt="Delete" class="delele_proxy" title="Delete"  src="<%=request.getContextPath()%>/images/delete_proxy.png" onclick="deleteHandler(this);" height="14" width="14" style="cursor: pointer;"/>
								</td>
								<td width="2%" style="padding-right: 10px;" class="tbl-header-bold" valign="middle" onclick="expandCollapse(this);">
									<img alt="Expand" class="expand_class" title="Expand" id="pluginImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;background-position: 5px 5px; "/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="pluginDivIds" class="toggleDivs">
				<table name="tblmPlugin" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
							<input type="button" onclick='addNewPluginRow("pluginTemplate_<%=widgetId%>","mappingtblplugin_<%=widgetId%>");' value=" Add Plugin " class="light-btn plugin-btn" style="size: 140px" tabindex="3"> 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="mappingtblplugin_<%=widgetId%>" class="mappingtblplugin">
								<tr>
									<td align="left" class="tblheader-policy" valign="top" width="23.75%" id="tbl_attrid">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.ruleset" />
										<ec:elitehelp header="radiusservicepolicy.plugin.ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.plugin.ruleset" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="23.75%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.plugin" />
									    <ec:elitehelp header="radiusservicepolicy.plugin.plugin" headerBundle="servicePolicyProperties" text="radiusservicepolicy.plugin.plugin" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="23.75%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.pluginarguments" />
									    <ec:elitehelp header="radiusservicepolicy.plugin.pluginarguments" headerBundle="servicePolicyProperties" text="radiusservicepolicy.plugin.pluginarguments" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="23.75%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.responsetype" />
									    <ec:elitehelp header="radiusservicepolicy.plugin.responsetype" headerBundle="servicePolicyProperties" text="radiusservicepolicy.plugin.responsetype" ></ec:elitehelp>
									</td>
									<td align="center" class="tblheader-policy" valign="top" width="5%">Remove</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
</table>
</form>
<table id="pluginTemplate_<%=widgetId%>" style="display: none">
	<tr>
		<td class='tblfirstcol' width="23.75%">
			<input class="noborder ruleset" id="ruleset_<%=widgetId%>" type="text" style="width: 100%;" name="ruleset" maxlength="2000"/>
		</td>
		<td class="tblrows" width="23.75%">
			<input type="text" id="plugin_<%=widgetId%>" name="pluginName" class="noborder plugin" onfocus="setAutoCompleteDataforPlugin(this);" style="width: 100%;"/>
		</td>
		<td class="tblrows" width="23.75%">
			<input type="text" id="pluginArgument_<%=widgetId%>" name="pluginArgument" class="noborder pluginArgument" style="width: 100%;" maxlength="4000"/>
		</td>
		<td class="tblrows" width="23.75%">
			<select name="requestType" id="requestType_<%=widgetId%>" class="noborder requestType" style="width:120px">
				<option value="false">Request</option>
				<option value="true">Response</option>
			</select>
		</td>
		<td class='tblrows' align="center" width="5%" style="margin-top: 5px;">
			<img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' />&nbsp;
		</td>
	</tr>
</table>
</body>
</html>