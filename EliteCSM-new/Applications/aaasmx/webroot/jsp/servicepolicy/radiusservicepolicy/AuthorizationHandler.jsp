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
		   widgetId=widgetId+"_additional_authorization";
	}else{
		   widgetId=widgetId+"_authentication_authorization";
	} 
%>
</head>
<body>
<form id="frm_<%=widgetId%>" name="frm_<%=widgetId%>"  class="form_authorization">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>
<table name="tblmAuthorizationHandler" width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
	<tbody class='subchild'>
		<tr style="cursor: pointer;">
			<td align="left" class="tbl-header-bold sortableClass" valign="top" colspan="2">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td width="96%" align="left" class="tbl-header-bold" valign="top">
							<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.authorizationhandler" />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled"/>
							<input type="hidden" id="authoRizationHiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.authorizationhandler" />" />
							<input type="hidden" id="authHandlerType_<%=widgetId%>" name="handlerType" class="handlerType" value="AuthorizationHandler" />
						</td>
						<td class="editable_icon_bg">
							<span class="edit_handler_icon" onclick="changeHandlerName(this);" title="Edit Handler Name"></span>
							<span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;"></span>
						</td>
						<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
							<div class="switch">
							  <input id="toggle-authorization_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" />
							  <label for="toggle-authorization_<%=widgetId%>"></label>
							</div>
						</td>
						<td width="1%" valign="middle" class="tbl-header-bold" style="padding-right:5px;">
							<img alt="Delete" class="delele_proxy" title="Delete"  src="<%=request.getContextPath()%>/images/delete_proxy.png" onclick="deleteHandler(this);" height="14" width="14" style="cursor: pointer;"/>
						</td>
						<td width="2%" valign="middle" class="tbl-header-bold" style="padding-right:10px;" onclick="expandCollapse(this);">
							<img alt="Expand" class="expand_class" title="Expand" id="authorizationHandlerImg"  src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
						</td>
					</tr>
				</table>
			</td>
		</tr> 
		<tr>
			<td>
				<div id="authorizationHandlerDiv" class="toggleDivs">
						<table name="tblmAuthorizationHandler" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
							<tr>
								<td align="left" class="captiontext left-border bottom-border" valign="top" width="50%">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td valign="top" class="labeltext" align="left" style="padding-top: 10px;">
												<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.wimaxenabled" /> 
												<ec:elitehelp  header="servicepolicy.authpolicy.wimaxenabled" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.wimaxenabled" ></ec:elitehelp>
											</td>
											<td valign="top" class="labeltext" align="left" style="padding-top: 10px;">
												<select id="wimaxEnabled" name="wimaxEnabled" class="labeltext" style="width: 200px">
													<option value="false">False</option>
													<option value="true">True</option>
												</select>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.threegppenabled" /> 
												<ec:elitehelp  header="servicepolicy.authpolicy.threegppenabled" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.threegppenabled" ></ec:elitehelp>
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<select id="threeGPPEnabled" name="threeGPPEnabled" class="labeltext" style="width: 200px">
													<option value="false">False</option>
													<option value="true">True</option>
												</select>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.defaultsessiontimeout" />
												<ec:elitehelp header="servicepolicy.authpolicy.defaultsessiontimeout" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.defaultsessiontimeout" ></ec:elitehelp>
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<input type="text" id="defaultSessionTimeout" name="defaultSessionTimeout" size="10" style="width: 200px;" maxlength="10" value="600"/>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.radiuspolicy" /> 
												<ec:elitehelp  header="servicepolicy.authpolicy.radiuspolicy" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.radiuspolicy" ></ec:elitehelp>
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<table width="100%" cellspacing="0" cellpadding="0" border="0" class="box">
													<tr>
														<td class="labeltext" style="padding-top: 5px;">
															<input type="checkbox" name="rejectOnCheckItemNotFound" id="rejectOnCheckItemNotFound" class="radiusPolicy radiusPolicychkgroup" value="false"  onclick="changeValueOfRadiusPolicyGroup(this);" />
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.rejectoncheckitemnotfound" />
														</td>
													</tr>
													<tr>
														<td class="labeltext" style="padding-top: 5px;">
															<input type="checkbox" id="rejectOnRejectItemNotFound" name="rejectOnRejectItemNotFound" class="radiusPolicy radiusPolicychkgroup" value="false"  onclick="changeValueOfRadiusPolicyGroup(this);" />
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.rejectonrejectitemnotfound" />
														</td>
													</tr>
													<tr>
														<td class="labeltext" style="padding-top: 5px;">
															<input type="checkbox" id="actionOnPolicyNotFound" name="actionOnPolicyNotFound" class="radiusPolicy radiusPolicychkgroup" value="false" onclick="changeValueOfRadiusPolicyGroup(this);" />
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.acceptonpolicynotfound" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.gracepolicy" /> 
												<ec:elitehelp header="servicepolicy.authpolicy.gracepolicy" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.gracepolicy" ></ec:elitehelp>
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<select id="gracePolicyId" name="gracePolicyId" class="labeltext" style="width: 200px;">
													<option value=""><bean:message key="general.select" /></option>
													<logic:iterate id="gracePolicyInst" name="createRadiusServicePolicyForm" property="gracePolicyList" type="com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData">
														<option value="<%=gracePolicyInst.getName()%>"><%=gracePolicyInst.getName()%></option>
													</logic:iterate>
												</select>
											</td>
										</tr>
									</table>
								</td>
									<td align="left" class="captiontext right-border bottom-border" valign="top" width="50%">
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 10px;">&nbsp;
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 10px;">&nbsp;
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 10px;">&nbsp;
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 10px;">&nbsp;
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 10px;">&nbsp;
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 10px;">&nbsp;
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
	</tbody>
</table>
</form>
</body>
</html>