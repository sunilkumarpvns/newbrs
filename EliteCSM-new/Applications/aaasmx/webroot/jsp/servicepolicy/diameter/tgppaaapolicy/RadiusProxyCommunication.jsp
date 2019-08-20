<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.TGPPAAAPolicyConstant"%>
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
<script type="text/javascript">
	$('.acceptOnTimeout').each(function(){
		$(this).val('false');
	});
	
	function changeValuesAcceptOnTimeout(checkbox){
		if($(checkbox).attr('checked')){
			$(checkbox).val('true');
		}else{
			$(checkbox).val('false');
		}
	}
</script>
</head>
<body>
<form id="form_radiusproxycomm_<%=widgetId%>" name="form_radiusproxycomm_<%=widgetId%>" class="form_proxycommunication">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=TGPPAAAPolicyConstant.PROXY_COMMUNICATION_HANDLER%>" name="handlerType" id="handlerType" />
<input type="hidden" value="<%=widgetId%>" name="widgetId" id="widgetId_<%=widgetId%>" />
<input type="hidden" value="RadiusProxyCommunication" name="handlerJsName" id="handlerJsName" />

<table name="tblmRadiusProxyCommunication" width="100%" border="0" style="background-color: white;" class="handler-class tblmRadiusProxyCommunication" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td align="left" class="tbl-header-bold sortableClass" valign="top" colspan="3">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="96%" lign="left" class="tbl-header-bold" valign="top">
						<div class="handler-label"></div>
						<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="30" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.tgppaaapolicy.radiusproxy.title' />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled"/>
						<input type="hidden" id="radProxyhiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.tgppaaapolicy.radiusproxy.title' />" />
						<span class="handler-type">[<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.tgppaaapolicy.radiusproxy.title' />]</span>
					</td>
					<td align="left" class="tbl-header-bold" valign="top">
						<span class="edit_handler_icon"  title="Edit Handler Name" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="changeHandlerName(this);" <%}%>  ></span>
						<span class="save_handler_icon"   title="Save Handler Name" style="display: none;" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="saveHandlerName(this);" <%}%>  ></span>
					</td>
					<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
						<div class="switch">
						  <input id="toggle-proxycommunication_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
						  <label for="toggle-proxycommunication_<%=widgetId%>"></label>
						</div>
					</td>
					<td width="1%" style="padding-right: 5px" class="tbl-header-bold" valign="middle">
						<img alt="Delete" class="delele_proxy" title="Delete" src="<%=request.getContextPath()%>/images/delete_proxy.png"  height="14" width="14" style="cursor: pointer;" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="deleteHandler(this);" <%}%>  />
					</td>
					<td width="2%" style="padding-right: 10px" class="tbl-header-bold" valign="middle" onclick="expandCollapse(this);">
						<img alt="Expand" class="expand_class"  title="Expand" id="proxyCommunicationImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"  />
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="radiusProxyCommunicationDiv" class="toggleDivs">
				<table name="tblmRadiusProxyCommunication" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
							<input type="hidden" name="protocol" value="RADIUS" id="protocol_<%=widgetId%>"/> 
							<table width="100%" cellspacing="0" cellpadding="0" border="0" >
								<tr>
									<td class="labeltext" width="20%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.priorityresultcode" />
										<ec:elitehelp  header="tgppaaapolicy.tgppaaapolicy.priorityresultcode" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.priorityresultcode" ></ec:elitehelp>
									</td>
									<td class="labeltext" width="*">
										<input class="priorityResultCodes"  pattern="[0-9,]*" title="Please enter a valid priority result code(Comma Seperated Digit Only)." type="text" name="priorityResultCodes" id="priorityResultCodes_<%=widgetId%>" style="width: 250px;" onkeypress="validateCommasNumber(event)"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
							<input type="button" onclick="addProxyPolicyRow('radiusProxyCommunicationTemplate_<%=widgetId%>','radiusProxyCommunicationTbl_<%=widgetId%>',this);" value=" Add Proxy Communication " class="light-btn proxy-com-btn" style="size: 140px" tabindex="3"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> > 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="radiusProxyCommunicationTbl_<%=widgetId%>" class="proxyCommunicationTbl">
								<tr>
									<td align="left" class="tblheader-policy" valign="top" width="19%" id="tbl_attrid">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.ruleset" />
										<ec:elitehelp  header="tgppaaapolicy.tgppaaapolicy.ruleset" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.ruleset" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.translationmapping" />
										<ec:elitehelp header="tgppaaapolicy.tgppaaapolicy.translationmapping" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.translationmapping" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.proxygroup" />
										<ec:elitehelp header="tgppaaapolicy.tgppaaapolicy.proxygroup" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.proxygroup" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.script" />
										<ec:elitehelp header="tgppaaapolicy.tgppaaapolicy.script" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.script" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.acceptedresultcode" />
										<ec:elitehelp header="tgppaaapolicy.tgppaaapolicy.acceptedresultcode" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.acceptedresultcode" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="5%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.remove" />
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
<table id="radiusProxyCommunicationTemplate_<%=widgetId%>" style="display: none">
	<tr>
		<td class='proxy-table-firstcol' width="19%" valign="middle">
			<input class="ruleset noborder" type="text" name="ruleset" id="ruleset_<%=widgetId%>" maxlength="2000" style="width: 100%;"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
		</td>
		<td class="tblrowspolicy" width="19%" valign="middle" style="border-left: 1px solid #CCCCCC;">
			<select name="translationMappingName" id="translationMappingName_<%=widgetId%>" style="width: 100%;" class="noborder"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
				<option value="">--Select--</option>
				<optgroup label="Translation Mapping" class="labeltext">
					<logic:iterate id="translationMapping" name="tgppAAAPolicyForm" property="diaToradiusTranslationMappingConfDataList" type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
						<option value="<%=translationMapping.getName()%>" class="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></option>
					</logic:iterate>
				</optgroup>

				<optgroup label="Copy Packet Mapping" class="labeltext">
					<logic:iterate id="copyPacketMapping" name="tgppAAAPolicyForm" property="diaToradiusCopyPacketMappingConfDataList" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData">
						<option value="<%=copyPacketMapping.getName()%>" styleClass="<%=ConfigConstant.COPY_PACKET_MAPPING%>"><%=copyPacketMapping.getName()%></option>
					</logic:iterate>
				</optgroup>
			</select>
		</td>
		<td class="tblrowspolicy" width="19%" valign="middle">
			<select name="peerGroupId" id="peerGroupId_<%=widgetId%>" style="width: 100%;" class="noborder"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
				<option value="">--Select--</option>
				<logic:iterate id="esiGroup" name="tgppAAAPolicyForm" property="radiusESIGroupDataList" type=" com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData">
					<option value="<%=esiGroup.getEsiGroupName()%>"><%=esiGroup.getEsiGroupName()%></option>
				</logic:iterate>
			</select>
		</td>
		<td class="tblrowspolicy" width="19%" valign="middle">
			<input class="noborder esiScriptAutocomplete" id="script_<%=widgetId%>" type="text" name="script" style="width: 100%;"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
		</td>
		<td class="tblrowspolicy" width="19%" valign="middle" align="left">
			<input type="text" id="acceptedResultCode_<%=widgetId%>" name="acceptedResultCode" class="acceptedResultCode noborder" pattern="[0-9,]*" title="Please enter a valid accepted result code(Comma Seperated Digit Only)."  style="width: 100%" onkeypress="validateCommasNumber(event)"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /> 
		</td>
		<td class='tblrows' align="center" valign="middle" width="5%">
			<span class='delete remove-proxy-server'  <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>   onclick="deleteMe(this);" <%}%>  />&nbsp;
		</td>
	</tr>
</table>
</html>