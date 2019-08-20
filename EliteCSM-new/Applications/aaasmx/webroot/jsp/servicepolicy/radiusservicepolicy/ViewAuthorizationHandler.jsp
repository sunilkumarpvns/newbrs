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
	String jsonData=request.getParameter("jsonData");
	
	if(isAdditional.equalsIgnoreCase("true")){
		   widgetId=widgetId+"_additionalTable";
	}else{
		   widgetId=widgetId+"_authenticationTable";
	} 
%>
<script type="text/javascript">
$(document).ready(function(){
	 $(".authHandlerCheckbox").change(function(){
	    if($(this).attr('checked')){
	          $(this).val('true');
	     }else{
	          $(this).val('false');
	     }
	}); 
});

var formData = <%=jsonData%>;

$.each(formData, function(key,value){
	 var element = $('#'+key+'<%=widgetId%>');
	 if($(element).length > 0 && $(element).is("input:checkbox")){
   	 	 if(value == "true"){
   			$(element).attr('checked', true);
   			$(element).val('true');
   	    }else{
   	    	$(element).attr('checked', false);
   	    	$(element).val('false');
   	    }
	}else if($(element).length > 0 && $(element).is("input")) {
		if( key == 'handlerName'){
			if( value.length > 0 ){
				 $('#authorization-handlerName<%=widgetId%>').attr('size', value.length);
				 $('#authorization-handlerName<%=widgetId%>').val(value);
			}
		}else{
			 $(element).val(value);
		}
	}else if($(element).length > 0 && $(element).is("select")) {
		 $(element).val(value);
	}else if(key == 'isHandlerEnabled'){
	 	if( value == "true" ){
	 		$('#toggle-authorization_<%=widgetId%>').attr('checked', true);
	 		$('#toggle-authorization_<%=widgetId%>').val('true');
	 	}else{
	 		$('#toggle-authorization_<%=widgetId%>').attr('checked', false);
	 		$('#toggle-authorization_<%=widgetId%>').val('false');
	 		var handlerObject=$('#toggle-authorization_<%=widgetId%>').closest('table[class^="handler-class"]');
	 		$(handlerObject).find('tr').each(function(){
	 			$(this).addClass('disable-toggle-class');
	 		});
	 	}
	}else if( key == 'handlerName'){
		if( value.length > 0 ){
			 $('#authorization-handlerName<%=widgetId%>').attr('size', value.length);
			 $('#authorization-handlerName<%=widgetId%>').val(value);
		}
	}
});
</script>
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
							<input type="text" id="authorization-handlerName<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.authorizationhandler" />" onkeyup="expand(this);" onload="expand(this);" disabled="disabled"/>
						</td>
						<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
							<div class="switch">
							  <input id="toggle-authorization_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" disabled="disabled"/>
							  <label for="toggle-authorization_<%=widgetId%>"></label>
							</div>
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
				<div id="authorizationHandlerDiv" class="toggleDivs" style="display: block;">
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
												<select id="wimaxEnabled<%=widgetId%>" name="wimaxEnabled" class="labeltext" style="width: 200px" disabled="disabled">
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
												<select id="threeGPPEnabled<%=widgetId%>" name="threeGPPEnabled" class="labeltext" style="width: 200px" disabled="disabled">
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
												<input type="text" id="defaultSessionTimeout<%=widgetId%>" name="defaultSessionTimeout" size="10" style="width: 200px;" maxlength="10" value="600" disabled="disabled"/>
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
															<input type="checkbox" name="rejectOnCheckItemNotFound" id="rejectOnCheckItemNotFound<%=widgetId%>" class="authHandlerCheckbox" disabled="disabled"/>
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.rejectoncheckitemnotfound" />
														</td>
													</tr>
													<tr>
														<td class="labeltext" style="padding-top: 5px;">
															<input type="checkbox" id="rejectOnRejectItemNotFound<%=widgetId%>" name="rejectOnRejectItemNotFound" class="authHandlerCheckbox" disabled="disabled"/>
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.rejectonrejectitemnotfound" />
														</td>
													</tr>
													<tr>
														<td class="labeltext" style="padding-top: 5px;">
															<input type="checkbox" id="actionOnPolicyNotFound<%=widgetId%>" name="actionOnPolicyNotFound" class="authHandlerCheckbox" disabled="disabled"/>
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.acceptonpolicynotfound" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;" width="47%">
												<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.gracepolicy" /> 
												<ec:elitehelp header="servicepolicy.authpolicy.gracepolicy" headerBundle="servicePolicyProperties" text="servicepolicy.authpolicy.gracepolicy" ></ec:elitehelp>
											</td>
											<td align="left" class="labeltext" valign="top" style="padding-top: 5px;">
												<select id="gracePolicyId<%=widgetId%>" name="gracePolicyId" class="labeltext" style="width: 200px;" disabled="disabled">
													<option value=""><bean:message key="general.select" /></option>
													<logic:iterate id="gracePolicyInst" name="updateRadiusServicePolicyForm" property="gracePolicyList" type="com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData">
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