<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.TGPPAAAPolicyConstant"%>
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
	String orderNumber=request.getParameter("orderNumber");
%>
</head>
<body>
<form id="frm_<%=widgetId%>" name="frm_<%=widgetId%>"  class="form_authorization">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=TGPPAAAPolicyConstant.AUTHORIZATION_HANDLER%>" name="handlerType" id="handlerType" />
<input type="hidden" value="AuthorizationHandler" name="handlerJsName" id="handlerJsName" />

<table name="tblmAuthorizationHandler" width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
	<tbody class='subchild'>
		<tr style="cursor: pointer;">
			<td align="left" class="tbl-header-bold sortableClass" valign="top" colspan="2">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td width="96%" align="left" class="tbl-header-bold" valign="top">
							<div class="handler-label"><bean:message bundle='servicePolicyProperties' key='servicepolicy.proxypolicy.authorizationhandler' /></div>
							<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle='servicePolicyProperties' key='servicepolicy.proxypolicy.authorizationhandler' />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled" style="display: none;"/>
							<input type="hidden" id="authorizationhiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle='servicePolicyProperties' key='servicepolicy.proxypolicy.authorizationhandler' />" />
							<span class="handler-type">[<bean:message bundle='servicePolicyProperties' key='servicepolicy.proxypolicy.authorizationhandler' />]</span>
						</td>
						<td align="left" class="tbl-header-bold" valign="top">
							<span class="edit_handler_icon"  title="Edit Handler Name" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="changeHandlerName(this);" <%}%>  ></span>
							<span class="save_handler_icon"  title="Save Handler Name" style="display: none;" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="saveHandlerName(this);"  <%}%>  ></span>
						</td>
						<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
							<div class="switch">
							  <input id="toggle-authorization_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
							  <label for="toggle-authorization_<%=widgetId%>"></label>
							</div>
						</td>
						<td width="1%" valign="middle" class="tbl-header-bold" style="padding-right:5px;">
							<img alt="Delete" class="delele_proxy" title="Delete"  src="<%=request.getContextPath()%>/images/delete_proxy.png" height="14" width="14" style="cursor: pointer;" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="deleteHandler(this);" <%}%>  />
						</td>
						<td width="2%" valign="middle" class="tbl-header-bold" style="padding-right:10px;" onclick="expandCollapse(this);">
							<img alt="Expand" class="expand_class" title="Expand" id="authorizationHandlerImg"  src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%>  />
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
												<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.authorization.wimaxenabled" /> 
												<ec:elitehelp  header="tgppaaapolicy.authorization.wimaxenabled" headerBundle="servicePolicyProperties" text="tgppaaapolicy.authorization.wimaxenabled" ></ec:elitehelp>
											</td>
											<td valign="top" class="labeltext" align="left" style="padding-top: 10px;">
												<select id="wimaxEnabled_<%=widgetId%>" name="wimaxEnabled" class="labeltext" style="width: 200px" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
													<option value="false">False</option>
													<option value="true">True</option>
												</select>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.authorization.defaultsessiontimeout" />
												<ec:elitehelp header="tgppaaapolicy.authorization.defaultsessiontimeout" headerBundle="servicePolicyProperties" text="tgppaaapolicy.authorization.defaultsessiontimeout" ></ec:elitehelp>
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<input type="text" id="defaultSessionTimeoutInSeconds_<%=widgetId%>" name="defaultSessionTimeoutInSeconds" size="10" style="width: 200px;" maxlength="10" value="600" onkeypress="validateOnlyNumbers(event)" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.authorization.diameterpolicy" /> 
												<ec:elitehelp  header="tgppaaapolicy.authorization.diameterpolicy" headerBundle="servicePolicyProperties" text="tgppaaapolicy.authorization.diameterpolicy" ></ec:elitehelp>
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<table width="100%" cellspacing="0" cellpadding="0" border="0" class="box">
													<tr>
														<td class="labeltext" style="padding-top: 5px;">
															<input type="checkbox" name="rejectOnCheckItemNotFound" id="rejectOnCheckItemNotFound_<%=widgetId%>" class="diameterPolicy" onchange="changeDiameterPolicyVal(this);" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
															<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.authorization.rejectoncheckitemnotfound" />
														</td>
													</tr>
													<tr>
														<td class="labeltext" style="padding-top: 5px;">
															<input type="checkbox" id="rejectOnRejectItemNotFound_<%=widgetId%>" name="rejectOnRejectItemNotFound" class="diameterPolicy" onchange="changeDiameterPolicyVal(this);" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
															<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.authorization.rejectonrejectitemnotfound" />
														</td>
													</tr>
													<tr>
														<td class="labeltext" style="padding-top: 5px;">
															<input type="checkbox" id="acceptOnPolicyOnFound_<%=widgetId%>" name="acceptOnPolicyOnFound" class="diameterPolicy"  onchange="changeDiameterPolicyVal(this);" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
															<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.authorization.acceptonpolicynotfound" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.authorization.gracepolicy" /> 
												<ec:elitehelp header="tgppaaapolicy.authorization.gracepolicy" headerBundle="servicePolicyProperties" text="tgppaaapolicy.authorization.gracepolicy" ></ec:elitehelp>
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<select id="gracePolicy_<%=widgetId%>" name="gracePolicy" class="labeltext" style="width: 200px;" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
													<option value=""><bean:message key="general.select" /></option>
													<logic:iterate id="gracePolicyInst" name="tgppAAAPolicyForm" property="gracePolicyList" type="com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData">
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