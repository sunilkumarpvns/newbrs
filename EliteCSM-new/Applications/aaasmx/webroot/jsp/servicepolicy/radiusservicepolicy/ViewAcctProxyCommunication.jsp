<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
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

function changeValuesAcceptOnTimeout(checkbox){
	if($(checkbox).attr('checked')){
		$(checkbox).val('true');
	}else{
		$(checkbox).val('false');
	}
}

var formData =<%=jsonData%>;

$.each(formData, function(key,value){
	 if(key == 'proxyCommunicationList'){
		 $.each(value, function(jsonKey,jsonValue){
			 addProxyPolicyRow('proxyCommunicationTemplate_<%=widgetId%>','proxyCommunicationTbl_<%=widgetId%>');
			
			 var findTableObj = $("#proxyCommunicationTbl_<%=widgetId%>"+" tr:last").parent().parent().parent().parent();
			 
			//set Ruleset Data
			var rulesetrow = $(findTableObj).find("input[name='ruleset']");
			
			$(rulesetrow).val(filterRuleset(jsonValue.ruleset));
				
			//set translationMappingName
			var translationMappingNamerow = $(findTableObj).find("select[name='translationMappingName']");
			$(translationMappingNamerow).val(jsonValue.translationMappingName);
				
			//set Script
			var scriptrow = $(findTableObj).find("input[name='script']");
			$(scriptrow).val(jsonValue.script);
			
			var acceptOnTimeoutrow =$(findTableObj).find("input:checkbox[name='acceptOnTimeout']");
			
			if(jsonValue.acceptOnTimeout == 'true'){
	   			$(acceptOnTimeoutrow).attr('checked', true);
	   			$(acceptOnTimeoutrow).val(jsonValue.acceptOnTimeout);
	   	    }else{
	   	    	$(acceptOnTimeoutrow).attr('checked', false);
	   	    	$(acceptOnTimeoutrow).val(jsonValue.acceptOnTimeout);
	   	    }
			
			var esiTableObj = $(findTableObj).find('.proxyCommServer');
		 
		 	esiTableObj.find('tr.esi-data').each(function(){
		 		$(this).remove();
		 	});
			
			$.each(jsonValue.esiListData, function(esiKey,esiValue){
				addESIData(esiTableObj,esiValue.esiId,esiValue.loadFactor);
			});
			 
		}); 
	} 
	 
	 if(key == 'isHandlerEnabled'){
		 	if( value == "true" ){
		 		$('#toggle-acctproxyhandler_<%=widgetId%>').attr('checked', true);
		 		$('#toggle-acctproxyhandler_<%=widgetId%>').val('true');
		 	}else{
		 		$('#toggle-acctproxyhandler_<%=widgetId%>').attr('checked', false);
		 		$('#toggle-acctproxyhandler_<%=widgetId%>').val('false');
		 		var handlerObject=$('#toggle-acctproxyhandler_<%=widgetId%>').closest('table[class^="handler-class"]');
		 		$(handlerObject).find('tr').each(function(){
		 			$(this).addClass('disable-toggle-class');
		 		});
		 	}
	}
	 
	if( key == 'handlerName'){
		if( value.length > 0 ){
			 $('#acctProxySequencialhandlerName'+'<%=widgetId%>').attr('size', value.length);
			 $('#acctProxySequencialhandlerName'+'<%=widgetId%>').val(value);
		}
	}
});

</script>
</head>
<body>
<form id="form_proxycomm_<%=widgetId%>" name="form_proxycomm_<%=widgetId%>" class="form_proxycommunication">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>
<table name="tblmProxyCommunication" width="100%" border="0" style="background-color: white;" class="handler-class tblmProxyCommunication" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td align="left" class="tbl-header-bold sortableClass" valign="top" colspan="3">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="96%" lign="left" class="tbl-header-bold" valign="top">
						<input type="text" id="acctProxySequencialhandlerName<%=widgetId%>" name="handlerName" class="handler-name-txt" size="21" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.proxycommunication" />" onkeyup="expand(this);" onload="expand(this);" disabled="disabled"/>
					</td>
					<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
						<div class="switch">
						  <input id="toggle-acctproxyhandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" disabled="disabled"/>
						  <label for="toggle-acctproxyhandler_<%=widgetId%>"></label>
						</div>
					</td>
					<td width="2%" style="padding-right: 10px" class="tbl-header-bold" valign="middle" onclick="expandCollapse(this);">
						<img alt="Expand" class="expand_class" title="Expand" id="proxyCommunicationImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="proxyCommunicationDiv" class="toggleDivs" style="display: block;">
				<table name="tblmProxyCommunication" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
							<input type="button" onclick="addProxyPolicyRow('proxyCommunicationTemplate_<%=widgetId%>','proxyCommunicationTbl_<%=widgetId%>');" value=" Add Proxy Communication " class="light-btn proxy-com-btn" style="size: 140px" tabindex="3" disabled="disabled"> 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="proxyCommunicationTbl_<%=widgetId%>" class="proxyCommunicationTbl">
								<tr>
									<td align="left" class="tblheader-policy" valign="top" width="17.5%" id="tbl_attrid">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.ruleset" />
										<ec:elitehelp  header="radiusservicepolicy.proxycommunication.ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.proxycommunication.ruleset" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="25%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.servergroup" />
										<ec:elitehelp header="radiusservicepolicy.proxycommunication.servergroup" headerBundle="servicePolicyProperties" text="radiusservicepolicy.proxycommunication.servergroup" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="17.5%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.translationmapping" />
										<ec:elitehelp header="radiusservicepolicy.proxycommunication.translationmapping" headerBundle="servicePolicyProperties" text="radiusservicepolicy.proxycommunication.translationmapping" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="17.5%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.script" />
										<ec:elitehelp header="radiusservicepolicy.proxycommunication.script" headerBundle="servicePolicyProperties" text="radiusservicepolicy.proxycommunication.script" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="17.5%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.acceptontimeout" />
										<ec:elitehelp header="radiusservicepolicy.proxycommunication.acceptontimeout" headerBundle="servicePolicyProperties" text="radiusservicepolicy.proxycommunication.acceptontimeout" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="5%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.remove" />
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
<table id="proxyCommunicationTemplate_<%=widgetId%>" style="display: none">
	<tr class="proxy-row">
		<td class='proxy-table-firstcol' width="17.67%" valign="middle">
			<input class="ruleset" type="text" name="ruleset" id="ruleset_<%=widgetId%>" maxlength="2000" style="width: 100%;" disabled="disabled"/>
		</td>
		<td class="" width="25%" valign="middle">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="proxyCommServer_<%=widgetId%>" class="box proxyCommServer">
					<tr>
						<td align="left" class="tblheader-policy" valign="top" width="55%" id="name">
							<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.servername" />
							&nbsp;<span title="Add Server" class="add-proxy-server add-more-esi-button"></span>
						</td>
						<td align="left" class="tblheader-policy" valign="top" width="40%">
							<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.loadfactor" />
						</td>
						<td align="left" class="tblheader-policy" valign="top" width="5%">Remove</td>
			    	</tr>
			    	<tr class="esi-data">
						<td align="left" class="labeltext" valign="top" width="55%" id="tbl_attrid">
							<select name="esiId" class="noborder" style="width:100%;" onchange="setProxyHandlerESIDropDown(this);" disabled="disabled">
								<option value='0'>--Select--</option>
								<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctESIList" type="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData">
									<option value='<bean:write name="obj" property="esiInstanceId"/>'><bean:write name="obj" property="name"/></option>
								</logic:iterate>
							</select>
							<input type="hidden" name="oNumber" value="1"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="40%">
							<select  name="loadFactor" class="noborder" style="width:100%;" disabled="disabled">
								<option value="0">0</option>
								<option value="1" selected="selected">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
								<option value="6">6</option>
								<option value="7">7</option>
								<option value="8">8</option>
								<option value="9">9</option>
								<option value="10">10</option>
							</select>
						</td>
						<td align="center" class="labeltext" valign="top" width="5%">
							<span class='delete remove-proxy-server'/>&nbsp;
						</td>
			    	</tr>
				</table>
		</td>
			<td class="tblrowspolicy" width="17.67%" valign="middle">
			 <select name="translationMappingName"
				id="translationMappingName_<%=widgetId%>" style="width: 100%;" disabled="disabled">
					<option value="">--Select--</option>
					<optgroup label="Translation Mapping" class="labeltext">
						<logic:iterate id="translationMapping"
							name="updateRadiusServicePolicyForm"
							property="translationMappingConfDataList"
							type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
							<option
								value="<%=translationMapping.getName()%>"
								class="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></option>
						</logic:iterate>

					</optgroup>

					<optgroup label="Copy Packet Mapping" class="labeltext">
						<logic:iterate id="copyPacketMapping"
							name="updateRadiusServicePolicyForm"
							property="copyPacketMappingConfDataList"
							type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData">
							<option
								value="<%=copyPacketMapping.getName()%>"
								styleClass="<%=ConfigConstant.COPY_PACKET_MAPPING%>"><%=copyPacketMapping.getName()%></option>
						</logic:iterate>
					</optgroup>
			</select>

			</td>
			<td class="tblrowspolicy" width="17.67%" valign="middle">
			<input class="" id="script_<%=widgetId%>" type="text" name="script" style="width: 100%;" maxlength="2000" disabled="disabled"/>
		</td>
		<td class="tblrows" width="12%" valign="middle" align="center">
			<input type="checkbox" id="acceptOnTimeout_<%=widgetId%>" value="false" name="acceptOnTimeout" class="noborder acceptOnTimeout" onclick="changeValuesAcceptOnTimeout(this);" disabled="disabled"/> 
		</td>
		<td class='tblrows' align="center" valign="middle" width="5%">
			<img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' />&nbsp;
		</td>
	</tr>
</table>
</body>
</html>