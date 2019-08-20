<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.TGPPAAAPolicyConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData"%>
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
    String orderNumber=request.getParameter("orderNumber");
%>
</head>
<body>
<form id="form_auth_<%=widgetId%>" name="form_auth_<%=widgetId%>" class="form_auth">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=TGPPAAAPolicyConstant.AUTHENTICATION_HANDLER%>" name="handlerType" id="handlerType" />
<input type="hidden" value="AuthenticationHandler" name="handlerJsName" id="handlerJsName" />

<table name="tblmAuthenticationHandler" width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td class="sortableClass">
			<table name="tblmAuthenticationHandler" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" class="tbl-header-bold" valign="top" colspan="2">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td width="96%" lign="left" class="tbl-header-bold" valign="top">
									<div class="handler-label"><bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.tgppaaapolicy.authenticationhandler' /></div>
									<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.tgppaaapolicy.authenticationhandler' />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled" style="display:none;"/>
									<input type="hidden" id="authhiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.tgppaaapolicy.authenticationhandler' />" />
									<span class="handler-type">[<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.tgppaaapolicy.authenticationhandler' />]</span>
								</td>
								<td align="left" class="tbl-header-bold" valign="top">
									<span class="edit_handler_icon" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="changeHandlerName(this);" <%}%> title="Edit Handler Name" ></span>
									<span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;"></span>
								</td>
								<td width="1%" align="left" class="tbl-header-bold" valign="middle" style="padding-right: 2px;line-height: 9px;">
									<div class="switch">
									  <input id="toggle-authentication_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%>/>
									  <label for="toggle-authentication_<%=widgetId%>"></label>
									</div>
								</td>
								<td width="1%" valign="middle" class="tbl-header-bold" style="padding-right: 5px;">
									<img alt="Delete" class="delele_proxy"  title="Delete" src="<%=request.getContextPath()%>/images/delete_proxy.png" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="deleteHandler(this);" <%}%> height="14" width="14" style="cursor: pointer;" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%>/>
								</td>
								<td width="2%" valign="middle" class="tbl-header-bold" style="padding-right: 10px;" onclick="expandCollapse(this);" >
									<img alt="Expand" class="expand_class" title="Expand" id="authenticationHandlerImg"  src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="left-border right-border">
			<div id="authenticationHandlerDiv" class="toggleDivs">
				<table name="tblmAuthenticationHandler" width="100%" border="0" style="background-color: white;border-bottom: 1px solid #CCC;" class="auth-handler-table" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext" valign="top" width="22%" style="padding-top: 10px;">
							<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.authentication.authmethods" /> 
							<ec:elitehelp  header="tgppaaapolicy.authentication.authmethods" headerBundle="servicePolicyProperties" text="tgppaaapolicy.authentication.authmethods" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" style="padding-top: 10px;">
							<table border="0" cellspacing="0" cellpadding="0" width="100%" class="authHandlerClass">
							<logic:iterate id="authMethodType" name="tgppAAAPolicyForm" property="authMethodTypeDataList" type="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData">
									
									<logic:equal value="EAP" name="authMethodType" property="alias">
									<tr>
										<td align="left" class="labeltext" valign="top" width="15%">
											<table width="100%" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td align="left" class="labeltext" valign="top" width="2%">
														<input type="checkbox" name="supportedAuthenticationMethods" class="labeltext" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> value="<bean:write name="authMethodType" property="name" />" >
													</td>
													<td  align="left" class="labeltext" valign="left">
														<bean:write name="authMethodType" property="name" />
													</td>
												</tr>
											</table>
										</td>
										<td align="left" class="labeltext" valign="top" width="85%">
											<table width="100%" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td align="left" class="labeltext" width="15%">
														<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.authentication.eapconfig" /> 
														<ec:elitehelp header="tgppaaapolicy.authentication.eapconfig" headerBundle="servicePolicyProperties" text="tgppaaapolicy.authentication.eapconfig" ></ec:elitehelp>
													</td>
													<td align="left" class="labeltext" width="85%" >
														<select id="eapConfigId" name="eapConfigId" style="width: 200px;" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%>>
															<option value="0">-Select-</option>
															<logic:iterate id="eapConfInst" name="tgppAAAPolicyForm" property="eapConfigurationList" type="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData">
																<option value="<%=eapConfInst.getName()%>"><%=eapConfInst.getName()%></option>
															</logic:iterate>
														</select>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									</logic:equal>
									
									<%if (!authMethodType.getAlias().equalsIgnoreCase("HTTP_DIGEST") && !authMethodType.getAlias().equalsIgnoreCase("PROXY")
											&& !authMethodType.getAlias().equalsIgnoreCase("EAP")) {%>
									
									<tr>
										<td align="left" class="labeltext" valign="top" width="15%">
											<table width="100%" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td align="left" class="labeltext" valign="top" width="2%">
														<input type="checkbox" name="supportedAuthenticationMethods" class="labeltext" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> value="<bean:write name="authMethodType" property="name" />">
													</td>
													<td  align="left" class="labeltext" valign="left">
														<bean:write name="authMethodType" property="name" />
													</td>
												</tr>
											</table>
										</td>
										<td align="left" class="labeltext" valign="top" width="85%">
											&nbsp;
										</td>
									</tr>
									<%}%>
								</logic:iterate> 
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