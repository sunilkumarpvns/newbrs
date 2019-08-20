<%@page import="com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.UpdateRadiusServicePolicyForm"%>
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
<%String widgetId=request.getParameter("widgetId");
  String orderNumber=request.getParameter("orderNumber");
  String isAdditional=request.getParameter("isAdditional");
  UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm = (UpdateRadiusServicePolicyForm)request.getSession().getAttribute("updateRadiusServicePolicyForm");
  String jsonData=request.getParameter("jsonData");
%>
<script type="text/javascript">
var formData = <%=jsonData%>;

$.each(formData, function(key,value){
	 var element = $('#'+key+'<%=widgetId%>');
	 if($(element).length > 0 && $(element).is("input")) {
		 if( key == 'handlerName'){
			if( value.length > 0 ){
				$('#authentication-handlerName<%=widgetId%>').attr('size', value.length);
				$('#authentication-handlerName<%=widgetId%>').val(value);
			}
		}else{
			 $(element).val(value);
		}
	}else if($(element).length > 0 && $(element).is("select")) {
		 $(element).val(value);
	}else if(key == 'isHandlerEnabled'){
     	if( value == "true" ){
     		$('#toggle-authhandler_<%=widgetId%>').attr('checked', true);
     		$('#toggle-authhandler_<%=widgetId%>').val('true');
     	}else{
     		$('#toggle-authhandler_<%=widgetId%>').attr('checked', false);
     		$('#toggle-authhandler_<%=widgetId%>').val('false');
     		var handlerObject=$('#toggle-authhandler_<%=widgetId%>').closest('table[class^="handler-class"]');
     		$(handlerObject).find('tr').each(function(){
     			$(this).addClass('disable-toggle-class');
     		});
     	}
	}else if( key == 'handlerName'){
    	if( value.length > 0 ){
    		$('#authentication-handlerName<%=widgetId%>').attr('size', value.length);
    		$('#authentication-handlerName<%=widgetId%>').val(value);
    	}
    }else{
    	if(key === 'selectedAuthMethodTypes'){
    		var authMethodsCommaSepratedWithBracket=value;
    		var authMethodsCommaSepratedStr = authMethodsCommaSepratedWithBracket.substring(1,authMethodsCommaSepratedWithBracket.length-1)
    		var authMethodArray = authMethodsCommaSepratedStr.split(",");
    		var authMethodArrayWithTrimValue=[];
    		$.each( authMethodArray, function( i, val ) {
    			authMethodArrayWithTrimValue.push(val.trim());	  
    		});	
    		var selectedAdditionalDriverIds = $('input[name="selectedAuthMethodTypes"]');
    		$.each(authMethodArrayWithTrimValue, function(key,value){
    			$.each(selectedAdditionalDriverIds,function(){
    				if($(this).val() == value){
    					$(this).attr('checked', true);
    				}
    			});
    		});
    	}	
	}
	 
	 if(key == 'userName'){
		 var element = $('#'+key+'<%=widgetId%>');
		 if($(element).val() == 'Advanced'){
			 $('#userNameExpression'+<%=widgetId%>).attr('readonly', false);
		 }
	 }
});

</script>
</head>
<body>
<form id="form_auth_<%=widgetId%>" name="form_auth_<%=widgetId%>" class="form_auth">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>
<table name="tblmAuthenticationHandler" width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td class="sortableClass">
			<table name="tblmAuthenticationHandler" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" class="tbl-header-bold" valign="top" colspan="2">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td width="96%" lign="left" class="tbl-header-bold" valign="top">
									<input type="text" id="authentication-handlerName<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.authenticationhandler" />" onkeyup="expand(this);" onload="expand(this);" disabled="disabled"/>
								</td>
								<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
									<div class="switch">
									  <input id="toggle-authhandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" disabled="disabled"/>
									  <label for="toggle-authhandler_<%=widgetId%>"></label>
									</div>
								</td>
								<td width="2%" valign="middle" class="tbl-header-bold"  style="padding-right: 10px;" onclick="expandCollapse(this);" >
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
		<td>
			<div id="authenticationHandlerDiv" class="toggleDivs" style="display: block;">
				<table name="tblmAuthenticationHandler" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext left-border" valign="top" width="25%" style="padding-top: 10px;">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.authmethods" /> 
							<ec:elitehelp  header="servicepolicy.authpolicy.authmethods" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.authmethods" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext right-border" valign="top" style="padding-top: 10px;">
							<table border="0" cellspacing="0" cellpadding="0" width="100%" class="authHandlerClass">
								<logic:iterate id="authMethodType" name="updateRadiusServicePolicyForm" property="authMethodTypeDataList" type="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData">
									
									<!-- Http_Digest -->
									
									<logic:equal value="HTTP_DIGEST" name="authMethodType" property="alias">
									<tr>
										<td align="left" class="labeltext" valign="top" width="15%">
											<table width="100%" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td align="left" class="labeltext" valign="top" width="2%">
														<input type="checkbox" name="selectedAuthMethodTypes" class="labeltext" value="<bean:write name="authMethodType" property="name" />" disabled="disabled">
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
													<td align="left" class="labeltext" width="20%">
														<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.digestconfig" /> 
														<ec:elitehelp  header="servicepolicy.authpolicy.digestconfig" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.digestconfig" ></ec:elitehelp>
													</td>
													<td align="left" class="labeltext" width="80%" >
														 <select id="digestConfigId<%=widgetId%>" class="labeltext" name="digestConfigId" style="width: 200px;" disabled="disabled">
															<option value="0">-Select-</option>
															<logic:iterate id="digestConfInst" name="updateRadiusServicePolicyForm" property="digestConfigInstanceDataList" type="com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData">
																<option value="<%=digestConfInst.getName()%>"><%=digestConfInst.getName()%></option>
															</logic:iterate>
														</select> 
													</td>
												</tr>
											</table>
										</td>
									</tr>
									</logic:equal>
									
									<logic:equal value="EAP" name="authMethodType" property="alias">
									<tr>
										<td align="left" class="labeltext" valign="top" width="15%">
											<table width="100%" cellspacing="0" cellpadding="0" border="0">
												<tr>
													<td align="left" class="labeltext" valign="top" width="2%">
														<input type="checkbox" name="selectedAuthMethodTypes" class="labeltext" value="<bean:write name="authMethodType" property="name" />" disabled="disabled">
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
													<td align="left" class="labeltext" width="20%">
														<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.eapconfig" /> 
														<ec:elitehelp header="servicepolicy.authpolicy.eapconfig" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.eapconfig" ></ec:elitehelp>
													</td>
													<td align="left" class="labeltext" width="80%" >
														 <select id="eapConfigId<%=widgetId%>" name="eapConfigId" style="width: 200px;" disabled="disabled">
															<option value="0">-Select-</option>
															<logic:iterate id="eapConfInst" name="updateRadiusServicePolicyForm" property="eapConfigurationList" type="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData">
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
														<input type="checkbox" name="selectedAuthMethodTypes" class="labeltext" value="<bean:write name="authMethodType" property="name" />" disabled="disabled">
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
				<tr>
					<td align="left" class="captiontext left-border" valign="top" width="24%" style="padding-top: 5px;">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.username" />
						<ec:elitehelp header="servicepolicy.authpolicy.username" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.username" note="servicepolicy.authpolicy.username.note" ></ec:elitehelp>
					</td>
					<td align="left" class="labeltext right-border" valign="top" style="padding-top: 5px;">
						<select id="userName<%=widgetId%>" name="userName" class="labeltext advanced-username" style="width: 200px;" onchange="setUsernameExpression(this);" disabled="disabled">
							<option value="NONE">NONE</option>
							<option value="Authenticated-Username">Authenticated-Username</option>
							<option value="CUI">CUI</option>
							<option value="Request">Request</option>
							<option value="Advanced">Advanced</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="left" class="captiontext left-border" valign="top" width="22%" style="padding-top: 5px;">
						<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.advancedusernameexpression" /> 
						<ec:elitehelp  header="radiusservicepolicy.advancedusernameexpression" headerBundle="servicePolicyProperties" text="radiusservicepolicy.advancedusernameexpression" ></ec:elitehelp>
					</td>
					<td align="left" class="labeltext right-border" valign="top" style="padding-top: 5px;">
						<input type="text" id="userNameExpression<%=widgetId%>" name="userNameExpression" class="userNameExpression" size="30" readonly="true" style="width: 200px" tabindex="24" disabled="disabled"/>
					</td>
				</tr>
				<tr>
					<td align="left" class="captiontext left-border bottom-border" valign="top" width="22%" style="padding-top: 5px;">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.usernameresattrs" /> 
						<ec:elitehelp  header="servicepolicy.authpolicy.usernameresattrs" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.usernameresattrs" ></ec:elitehelp>
					</td>
					<td align="left" class="labeltext right-border bottom-border" valign="top" style="padding-top: 5px;">
						<input type="text" id="userNameResponseAttribs<%=widgetId%>" class="userNameResponseAttribs" name="userNameResponseAttribs" size="30" autocomplete="off" onkeyup="setColumnsOnUserNameRespAttrTextFields();" style="width: 200px" tabindex="24" disabled="disabled" />
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