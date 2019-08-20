<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
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
<form id="frm_concurrency_<%=widgetId%>" name="frm_concurrency_<%=widgetId%>"  class="form_concurrency">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>
<table name="tblmSessionManager" width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td class="sortableClass">
			<table name="tblmSessionManager" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" class="tbl-header-bold" valign="top" colspan="2">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td width="96%" align="left" class="tbl-header-bold" valign="top">
									<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.concurrencyhandler" />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled"/>
									<input type="hidden" id="coccurrencyHiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.concurrencyhandler" />" />
									<input type="hidden" id="coccurrencyHandlerType_<%=widgetId%>" name="handlerType" class="handlerType" value="ConcurrencyHandler" />
								</td>
								<td class="editable_icon_bg">
									<span class="edit_handler_icon" onclick="changeHandlerName(this);" title="Edit Handler Name"></span>
									<span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;"></span>
								</td>
								<td width="1%" align="left" class="tbl-header-bold" valign="middle" style="padding-right: 2px;line-height: 9px;">
									<div class="switch">
									  <input id="toggle-concurrencyhandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);"/>
									  <label for="toggle-concurrencyhandler_<%=widgetId%>"></label>
									</div>
								</td>
								<td width="1%" align="left" class="tbl-header-bold" style="padding-right: 5px;" valign="middle">
									<img alt="Delete" class="delele_proxy" title="Delete"  src="<%=request.getContextPath()%>/images/delete_proxy.png" onclick="deleteHandler(this);" height="14" width="14" style="cursor: pointer;"/>
								</td>
								<td width="2%" valign="middle" class="tbl-header-bold" align="left" style="padding-right: 10px;" onclick="expandCollapse(this);">
									<img alt="Expand" class="expand_class" title="Expand" id="sessionManagerImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
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
			<div id="sessionManagerDiv" class="toggleDivs">
				<table name="tblmSessionManager" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0" class="tblmSessionManager">
					<tr>
						<td align="left" class="captiontext left-border bottom-border right-border">
							<table cellspacing="0" cellpadding="0" border="0" width="60%" style="padding-top: 10px;">
								<tr>
									<td class="tblheader-policy" width="50%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.sessionmanager.ruleset" />
										<ec:elitehelp  header="radiusservicepolicy.sessionmanager.ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.sessionmanager.ruleset" ></ec:elitehelp>
									</td>
									<td class="tblheader-policy" width="50%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.sessionmanager" />
										<ec:elitehelp header="servicepolicy.proxypolicy.sessionmanager" headerBundle="servicePolicyProperties" text="radiusservicepolicy.sessionmanager" ></ec:elitehelp>
									</td>
								</tr>
								<tr>
									<td class="proxy-table-firstcol" width="50%" style="border-right: 1px solid #CCC;">
										<input class="ruleset" type="text" name="ruleset" id="ruleset_<%=widgetId%>" maxlength="2000" style="width: 99%;"/>
									</td>
									<td class="labeltext right-border tblrows" width="50%">
										<select id="sessionManagerId" name="sessionManagerId" style="width:200px;" class="sessionManagerClass">
											<option value="0">--Select--</option>
											<logic:iterate id="sessionMgrInstance" property="sessionManagerInstanceDataList" name="createRadiusServicePolicyForm" type="com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData">
												<option value="<%=sessionMgrInstance.getName()%>"><%=sessionMgrInstance.getName()%></option>
											</logic:iterate>
										</select>
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
</form>
</body>
</html>