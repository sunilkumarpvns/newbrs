<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.TGPPAAAPolicyConstant"%>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/popcalendar.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/tgpp-aaa-policy/tgpp-aaa-policy.css" >

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
	   String orderNumber=request.getParameter("orderNumber");
	 %>
</head>
<body>
<form id="frm_concurrency_<%=widgetId%>" name="frm_concurrency_<%=widgetId%>"  class="form_concurrency">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=TGPPAAAPolicyConstant.CONCURRENCY_HANDLER%>" name="handlerType" id="handlerType" />
<input type="hidden" value="<%=widgetId %>" name="widgetId" id="widgetId" />
<input type="hidden" value="ConcurrencyHandler" name="handlerJsName" id="handlerJsName" />

<table name="tblmSessionManagement" width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td class="sortableClass">
			<table name="tblmSessionManagement" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" class="tbl-header-bold" valign="top" colspan="2">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td width="96%" align="left" class="tbl-header-bold" valign="top">
									<div class="handler-label"></div>
									<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.concurrencyhandler" />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" />
									<input type="hidden" id="ConcurrencyHiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.concurrencyhandler" />" />
									<span class="handler-type">[<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.handlername.concurrencyhandler' />]</span>
								</td>
								<td  align="left" class="tbl-header-bold" valign="top">
									<span class="edit_handler_icon" title="Edit Handler Name" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="changeHandlerName(this);" <%}%> <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%>  ></span>
									<span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;"></span>
								</td>
								<td width="1%" align="left" class="tbl-header-bold" valign="middle" style="padding-right: 2px;line-height: 9px;">
									<div class="switch">
									  <input id="toggle-ConcurrencyHandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%>/>
									  <label for="toggle-ConcurrencyHandler_<%=widgetId%>"></label>
									</div>
								</td>
								<td width="1%" align="left" class="tbl-header-bold" style="padding-right: 5px;" valign="middle">
									<img alt="Delete" class="delele_proxy" title="Delete"  src="<%=request.getContextPath()%>/images/delete_proxy.png" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="deleteHandler(this);" <%}%> height="14" width="14" style="cursor: pointer;" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%>/>
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
			<div id="sessionManagementDiv" class="toggleDivs">
				<table name="tblmSessionManagement" width="100%" border="0" style="background-color: white;" class="tblconcurrency_handler" cellspacing="0" cellpadding="0" id="tblmSessionManagement">
					<tr>
						<td align="left" class="captiontext left-border bottom-border right-border">
							<table cellspacing="0" cellpadding="0" border="0" width="60%" style="padding-top: 10px;" id="concurrency_<%=widgetId%>">
								<tr>
									<td class="tblheader-policy" width="50%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.concurrencyhandler.ruleset" />
										<ec:elitehelp  header="radiusservicepolicy.sessionmanager.ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.sessionmanager.ruleset" ></ec:elitehelp>
									</td>
									<td class="tblheader-policy" width="50%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.concurrencyhandler.diameterconcurrencypolicy" />
										<ec:elitehelp header="tgppaaapolicy.concurrencyhandler.diameterconcurrencypolicy" headerBundle="servicePolicyProperties" text="tgppaaaservicepolicy.Diameterconcurrencypolicy" ></ec:elitehelp>
									</td>
								</tr>
								<tr>
									<td class="proxy-table-firstcol" width="50%" style="border-right: 1px solid #CCC;">
										<input class="ruleset" type="text" name="ruleset" id="ruleset_<%=widgetId%>" maxlength="2000" style="width: 99%;" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%>/>
									</td>
									<td class="labeltext right-border tblrows" width="50%" >
										<%if("true".equals(request.getParameter("isViewPage").toString())){ %>
										   	 <html:select property="diaConConfigId" name="tgppAAAPolicyForm" styleId="diaConConfigId" style="width:200px;"  disabled="true" >
													<html:option value="0">--Select--</html:option>
													<html:optionsCollection property="diaconcurrencyInstanceDataList" name="tgppAAAPolicyForm" value="name" label="name"  />
											  </html:select> 
										<% } else {%>        
										     <html:select property="diaConConfigId" name="tgppAAAPolicyForm" styleId="diaConConfigId" style="width:200px;" onchange="removeValidation(this)">
													<html:option value="0">--Select--</html:option>
													<html:optionsCollection property="diaconcurrencyInstanceDataList" name="tgppAAAPolicyForm" value="name" label="name" />
										     </html:select>
									    <% }%>					
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



										