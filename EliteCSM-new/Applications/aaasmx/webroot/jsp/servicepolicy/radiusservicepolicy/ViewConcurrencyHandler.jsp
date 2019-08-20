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
		   widgetId=widgetId+"_additional";
	   }else{
		   widgetId=widgetId+"_authentication";
	   }
	%>
	<script type="text/javascript">
	var formData = <%=jsonData%>;

	$.each(formData, function(key,value){
		 var element = $('#'+key+'<%=widgetId%>');
		 if($(element).length > 0 && $(element).is("select")) {
			 $(element).val(value);
	     }
		 if($(element).length > 0 && $(element).attr('class') === 'sessionruleset'){
			 $(element).val(filterRuleset(value));
		 }
		 
		 if(key == 'isHandlerEnabled'){
			 	if( value == "true" ){
			 		$('#toggle-concurrencyhandler_<%=widgetId%>').attr('checked', true);
			 		$('#toggle-concurrencyhandler_<%=widgetId%>').val('true');
			 	}else{
			 		$('#toggle-concurrencyhandler_<%=widgetId%>').attr('checked', false);
			 		$('#toggle-concurrencyhandler_<%=widgetId%>').val('false');
			 		var handlerObject=$('#toggle-concurrencyhandler_<%=widgetId%>').closest('table[class^="handler-class"]');
			 		$(handlerObject).find('tr').each(function(){
			 			$(this).addClass('disable-toggle-class');
			 		});
		 	}
		}
		 
		if( key == 'handlerName'){
			if( value.length > 0 ){
				 $('#concurrencyhandlerName'+'<%=widgetId%>').attr('size', value.length);
				 $('#concurrencyhandlerName'+'<%=widgetId%>').val(value);
			}
		}
		 
	});
	</script>
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
									<input type="text" id="concurrencyhandlerName<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.concurrencyhandler" />" onkeyup="expand(this);" onload="expand(this);" disabled="disabled"/>
								</td>
								<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
									<div class="switch">
									  <input id="toggle-concurrencyhandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" disabled="disabled"/>
									  <label for="toggle-concurrencyhandler_<%=widgetId%>"></label>
									</div>
								</td>
								<td width="2%" valign="middle" class="tbl-header-bold" align="left" style="padding-right: 10px;" onclick="expandCollapse(this);">
									<img alt="Expand"  class="expand_class" title="Expand" id="sessionManagerImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
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
			<div id="sessionManagerDiv" class="toggleDivs" style="display: block;">
				<table name="tblmSessionManager" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
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
										<input class="sessionruleset" type="text" name="ruleset" id="ruleset<%=widgetId%>" maxlength="2000" style="width: 100%;" disabled="disabled"/>
									</td>
									<td class="labeltext right-border tblrows" width="50%">
										<select id="sessionManagerId<%=widgetId%>" name="sessionManagerId" style="width:200px;" disabled="disabled">
											<option value="0">--Select--</option>
											<logic:iterate id="sessionMgrInstance" property="sessionManagerInstanceDataList" name="updateRadiusServicePolicyForm" type="com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData">
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