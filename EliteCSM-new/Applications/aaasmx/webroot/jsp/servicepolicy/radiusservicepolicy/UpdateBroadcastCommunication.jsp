<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
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
	var formData = <%=jsonData%>;
	
	
	$.each(formData, function(key,value){
		 if(key == 'broadcastCommunicationList'){
			 $.each(value, function(jsonKey,jsonValue){
				 addBroadCastingRow('broadcastCommunicationTemplate_<%=widgetId%>','broadcastCommunicationTbl_<%=widgetId%>');
				
				 var findTableObj = $("#broadcastCommunicationTbl_<%=widgetId%>"+" tr:last").parent().parent().parent().parent();
				 
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
				
				var waitForResponserow =$(findTableObj).find("input:checkbox[name='waitForResponse']");
				
				if(jsonValue.waitForResponse == 'true'){
		   			$(waitForResponserow).attr('checked', true);
		   			$(waitForResponserow).val(jsonValue.waitForResponse);
		   	    }else{
		   	    	$(waitForResponserow).attr('checked', false);
		   	    	$(waitForResponserow).val(jsonValue.waitForResponse);
		   	    }
				
				var esiTableObj = $(findTableObj).find('.broadcastServer');
			 
			 	esiTableObj.find('tr.esi-data').each(function(){
			 		$(this).remove();
			 	});
				
				$.each(jsonValue.esiListData, function(esiKey,esiValue){
					addESIData(esiTableObj,esiValue.esiId,esiValue.loadFactor);
				});
				 
			}); 
		} 
		 
		 if( key == 'handlerName'){
			 if( value.length > 0 ){
				$('#broadcastHandlerName'+'<%=widgetId%>').attr('size', value.length);
				$('#broadcastHandlerName'+'<%=widgetId%>').val(value);
				$('#broadcastCommucnicationHiddenHandlerName_'+'<%=widgetId%>').val(value);
			 }
	   	 }
		 
		 if(key == 'isHandlerEnabled'){
			 	if( value == "true" ){
			 		$('#toggle-broadcasthandler_<%=widgetId%>').attr('checked', true);
			 		$('#toggle-broadcasthandler_<%=widgetId%>').val('true');
			 	}else{
			 		$('#toggle-broadcasthandler_<%=widgetId%>').attr('checked', false);
			 		$('#toggle-broadcasthandler_<%=widgetId%>').val('false');
			 		var handlerObject=$('#toggle-broadcasthandler_<%=widgetId%>').closest('table[class^="handler-class"]');
			 		$(handlerObject).find('tr').each(function(){
			 			$(this).addClass('disable-toggle-class');
			 		});
			 	}
		}
	});
</script>
</head>
<body>
<form id="form_broadcastcomm_<%=widgetId%>" name="form_broadcastcomm_<%=widgetId%>" class="form_broadcastcommunication">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>
<table name="tblmBroadCastCommunication" width="100%" border="0" style="background-color: white;" class="handler-class tblmBroadCastCommunication" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td align="left" class="tbl-header-bold sortableClass" valign="top" colspan="3">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="96%" lign="left" class="tbl-header-bold" valign="top">
						<input type="text" id="broadcastHandlerName<%=widgetId%>" name="handlerName" class="handler-name-txt" size="25" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.broadcastcommunication" />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled"/>
						<input type="hidden" id="broadcastCommucnicationHiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.broadcastcommunication" />" />
						<input type="hidden" id="broadcastCommunicationHandlerType_<%=widgetId%>" name="handlerType" class="handlerType" value="BroadcastingCommunication" />
					</td>
					<td>
						<span class="edit_handler_icon" onclick="changeHandlerName(this);" title="Edit Handler Name"></span>
						<span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;"></span>
					</td>
					<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
						<div class="switch">
						  <input id="toggle-broadcasthandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);"/>
						  <label for="toggle-broadcasthandler_<%=widgetId%>"></label>
						</div>
					</td>
					<%-- <td width="1%" align="left" class="tbl-header-bold" style="padding-right: 5px;" valign="middle">
						<img alt="Copy" class="copy_class" title="Copy"  src="<%=request.getContextPath()%>/images/copy_proxy.png" onclick="copyHandler(this);"  height="14" width="14" style="cursor: pointer;"/>
					</td> --%>
					<td width="1%" style="padding-right: 5px" class="tbl-header-bold" valign="middle">
						<img alt="Delete" class="delele_proxy" title="Delete"  src="<%=request.getContextPath()%>/images/delete_proxy.png" onclick="deleteHandler(this);" height="14" width="14" style="cursor: pointer;"/>
					</td>
					<td width="2%" style="padding-right: 10px" class="tbl-header-bold" valign="middle" onclick="expandCollapse(this);">
						<img alt="Expand" class="expand_class" title="Expand" id="broadcastingCommunicationImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="broadcastingCommunicationDiv" class="toggleDivs">
				<table name="tblmBroadcastingComm" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
							<input type="button" onclick="addBroadCastingRow('broadcastCommunicationTemplate_<%=widgetId%>','broadcastCommunicationTbl_<%=widgetId%>')" value=" Add Broadcasting Communication " class="light-btn proxy-com-btn" style="size: 140px" tabindex="3"> 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="broadcastCommunicationTbl_<%=widgetId%>" class="box broadcastCommunicationTbl">
								<tr>
									<td align="left" class="tblheader-policy" valign="top" width="15.34%" id="tbl_attrid">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.ruleset" />
										<ec:elitehelp header="radiusservicepolicy.coadmgeneration.ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.broadcastcommunication.ruleset" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="30%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.servergroup" />
										<ec:elitehelp header="radiusservicepolicy.proxycommunication.servergroup" headerBundle="servicePolicyProperties" text="radiusservicepolicy.broadcastcommunication.servergroup" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="15.34%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.translationmapping" />
										<ec:elitehelp  header="radiusservicepolicy.proxycommunication.translationmapping" headerBundle="servicePolicyProperties" text="radiusservicepolicy.broadcastcommunication.translationmapping" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="15.34%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.script" />
										<ec:elitehelp header="radiusservicepolicy.proxycommunication.script" headerBundle="servicePolicyProperties" text="radiusservicepolicy.broadcastcommunication.script" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="12%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.acceptontimeout" />
										<ec:elitehelp header="radiusservicepolicy.proxycommunication.acceptontimeout" headerBundle="servicePolicyProperties" text="radiusservicepolicy.broadcastcommunication.acceptontimeout" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="12%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.waitforresponse" />
										<ec:elitehelp header="radiusservicepolicy.proxycommunication.waitforresponse" headerBundle="servicePolicyProperties" text="radiusservicepolicy.broadcastcommunication.waitforresponse" ></ec:elitehelp>
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
</body>
<table id="broadcastCommunicationTemplate_<%=widgetId%>" style="display: none">
	<tr class="proxy-row">
		<td class='proxy-table-firstcol' width="15.34%" valign="middle">
			<input class="ruleset" type="text" name="ruleset" style="width: 150px;" maxlength="2000"/>
		</td>
		<td class="" width="30%" valign="middle">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="broadcastServer" class="box broadcastServer">
					<tr>
						<td align="left" class="tblheader-policy" valign="top" width="55%" id="name">
							<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.servername" />
							&nbsp;<span title="Add Server" onclick="addMoreServer(this)" class="add-proxy-server add-more-esi-button" ></span>
						</td>
						<td align="left" class="tblheader-policy" valign="top" width="40%">
							<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.loadfactor" />
						</td>
						<td align="left" class="tblheader-policy" valign="top" width="5%">Remove</td>
			    	</tr>
			    	<tr class="esi-data">
						<td align="left" class="labeltext" valign="top" width="55%" id="tbl_attrid">
							<select name="esiId" class="noborder" style="width:100%;" onchange="setProxyHandlerESIDropDown(this);">
								<option value='0'>--Select--</option>
								<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authBroadcastServerList" type="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData">
									<option value='<bean:write name="obj" property="name"/>'><bean:write name="obj" property="name"/></option>
								</logic:iterate>
							</select>
							<input type="hidden" name="oNumber" value="1"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="40%">
							<select  name="loadFactor" class="noborder" style="width:100%;" >
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
		<td class="tblrowspolicy" width="15.34%" valign="middle"><select
			name="translationMappingName"
			id="translationMappingName_<%=widgetId%>" style="width: 100%;">
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
		</select></td>
		<td class="tblrowspolicy" width="15.34%" valign="middle">
			<input class="esiScriptAutocomplete" id="script_<%=widgetId%>" type="text" name="script" style="width: 100%;" maxlength="2000"/>
		</td>
		<td class="tblrows" width="12%" valign="middle" align="center">
			<input type="checkbox" id="acceptOnTimeout_<%=widgetId%>" value="false" name="acceptOnTimeout" class="noborder acceptOnTimeout" onclick="changeValuesAcceptOnTimeout(this);"/> 
		</td>
		<td class="tblrows" width="12%" valign="middle" align="center">
			<input type="checkbox" id="waitForResponse_<%=widgetId%>" value="false" name="waitForResponse" class="noborder waitForResponse" onclick="changeValuesAcceptOnTimeout(this);"/> 
		</td>
		<td class='tblrows' align="center" valign="middle" width="5%"><img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' />&nbsp;</td>
	</tr>
</table>
</html>