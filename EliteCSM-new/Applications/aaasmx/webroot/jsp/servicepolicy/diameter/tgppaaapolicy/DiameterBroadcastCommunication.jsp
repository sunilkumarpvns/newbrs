<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.TGPPAAAPolicyConstant"%>
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
	String orderNumber=request.getParameter("orderNumber");
%>
<script type="text/javascript">
	
 	function changeValuesWaitForResponse(checkbox){
		if($(checkbox).val() == 'true'){
			$(checkbox).val('false');
		}else{
			$(checkbox).val('true');
		}
	}
	
	
	</script>

</head>
<body>
<form id="form_radiusbroadcastcomm_<%=widgetId%>" name="form_radiusbroadcastcomm_<%=widgetId%>" class="form_Broadcastcommunication">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=TGPPAAAPolicyConstant.BROADCAST_COMMUNICATION_HANDLER%>" name="handlerType" id="handlerType" />
<input type="hidden" value="<%=widgetId%>" name="widgetId" id="widgetId_<%=widgetId%>" />
<input type="hidden" value="DiameterBroadcastingCommunication" name="handlerJsName" id="handlerJsName" />

<table name="tblmRadiusBroadCastCommunication" width="100%" border="0" style="background-color: white;" class="handler-class tblmRadiusBroadCastCommunication" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td align="left" class="tbl-header-bold sortableClass" valign="top" colspan="3">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="96%" lign="left" class="tbl-header-bold" valign="top">
						<div class="handler-label"></div>
						<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="25" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.diameterbroadcast.title' />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled"/>
						<input type="hidden" id="diaBroadcasthiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.diameterbroadcast.title' />" />
						<span class="handler-type">[<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.diameterbroadcast.title' />]</span>
					</td>
					<td align="left" class="tbl-header-bold" valign="top">
						<span class="edit_handler_icon"  title="Edit Handler Name" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="changeHandlerName(this);" <%}%>  ></span>
						<span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="saveHandlerName" <%}%> (this);" ></span>
					</td>
					<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
						<div class="switch">
						  <input id="toggle-radiusbroadcastcommunication_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
						  <label for="toggle-radiusbroadcastcommunication_<%=widgetId%>"></label>
						</div>
					</td>
					<td width="1%" style="padding-right: 5px" class="tbl-header-bold" valign="middle">
						<img alt="Delete" class="delele_proxy" title="Delete"  src="<%=request.getContextPath()%>/images/delete_proxy.png"  height="14" width="14" style="cursor: pointer;" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="deleteHandler(this);" <%}%>  />
					</td>
					<td width="2%" style="padding-right: 10px" class="tbl-header-bold" valign="middle" onclick="expandCollapse(this);">
						<img alt="Expand" class="expand_class"  title="Expand" id="broadcastingCommunicationImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="radiusBroadcastingCommunicationDiv" class="toggleDivs">
				<table name="tblmRadiusBroadcastingComm" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
							<input type="hidden" name="protocol" value="DIAMETER" id="protocol_<%=widgetId%>"/> 
							<table width="100%" cellspacing="0" cellpadding="0" border="0" >
								<tr>
									<td class="labeltext" width="20%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.priorityresultcode" />
										<ec:elitehelp  header="tgppaaapolicy.tgppaaapolicy.priorityresultcode" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.priorityresultcode" ></ec:elitehelp>
									</td>
									<td class="labeltext" width="*">
										<input class="priorityResultCodes" type="text" name="priorityResultCodes" id="priorityResultCodes_<%=widgetId%>" style="width: 250px;"  pattern="[0-9,]*" title="Please enter a valid priority result code(Comma Seperated Digit Only)." onkeypress="validateCommasNumber(event)"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
									</td>
								</tr>
								<tr>
									<td class="labeltext" width="25%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.resultcode" />
										<ec:elitehelp  header="tgppaaapolicy.tgppaaapolicy.resultcode" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.resultcode" ></ec:elitehelp>
									</td>
									<td class="labeltext" width="*">
										<input class="priorityResultCodes" type="text" name="resultCodeWhenNoEntrySelected" id="resultCodeWhenNoEntrySelected_<%=widgetId%>" style="width: 250px;" onkeypress="validateOnlyNumbers(event)" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> value="0"/>
										<font color="#FF0000">*</font>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
							<input type="button" onclick="addBroadCastingRow('radiusBroadcastCommunicationTemplate_<%=widgetId%>','broadcastCommunicationTbl_<%=widgetId%>',this)" value=" Add Broadcasting Communication " class="light-btn broadcast-com-btn" style="size: 140px" tabindex="3"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> > 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="broadcastCommunicationTbl_<%=widgetId%>" class="box broadcastCommunicationTbl">
								<tr>
									<td align="left" class="tblheader-policy" valign="top" width="15.83%" id="tbl_attrid">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.ruleset" />
										<ec:elitehelp header="radiusservicepolicy.coadmgeneration.ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.broadcastcommunication.ruleset" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="15.83%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.translationmapping" />
										<ec:elitehelp  header="radiusservicepolicy.proxycommunication.translationmapping" headerBundle="servicePolicyProperties" text="radiusservicepolicy.broadcastcommunication.translationmapping" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="15.83%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.peergroup" />
										<ec:elitehelp header="tgppaaapolicy.tgppaaapolicy.peergroup" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.peergroup" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="15.83%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication.script" />
										<ec:elitehelp header="radiusservicepolicy.proxycommunication.script" headerBundle="servicePolicyProperties" text="radiusservicepolicy.broadcastcommunication.script" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="15.83%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.tgppaaapolicy.ignoredresultcode" />
										<ec:elitehelp header="tgppaaapolicy.tgppaaapolicy.ignoredresultcode" headerBundle="servicePolicyProperties" text="tgppaaapolicy.tgppaaapolicy.ignoredresultcode" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="15.83%">
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
<table id="radiusBroadcastCommunicationTemplate_<%=widgetId%>" style="display: none">
	<tr>
		<td class='proxy-table-firstcol' width="15.83%" valign="middle">
			<input class="ruleset noborder" type="text" name="ruleset" style="width: 100%;" maxlength="2000"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
		</td>
		<td class="tblrowspolicy" width="15.83%" valign="middle" style="border-left: 1px solid #CCCCCC;">
			<select name="translationMappingName" id="translationMappingName_<%=widgetId%>" style="width: 100%;" class="noborder"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
				<option value="">--Select--</option>
				<optgroup label="Translation Mapping" class="labeltext">
					<logic:iterate id="translationMapping" name="tgppAAAPolicyForm"	property="diaTodiaTranslationMappingConfDataList" type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
						<option value="<%=translationMapping.getName()%>" class="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></option>
					</logic:iterate>

				</optgroup>

				<optgroup label="Copy Packet Mapping" class="labeltext">
					<logic:iterate id="copyPacketMapping" name="tgppAAAPolicyForm" property="diaTodiaCopyPacketMappingConfDataList" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData">
						<option value="<%=copyPacketMapping.getName()%>" styleClass="<%=ConfigConstant.COPY_PACKET_MAPPING%>"><%=copyPacketMapping.getName()%></option>
					</logic:iterate>
				</optgroup>
			</select>
		</td>
		<td class="tblrowspolicy" width="15.83%" valign="middle">
			<select name="peerGroupId" id="peerGroupId_<%=widgetId%>" style="width: 100%;" class="noborder"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
				<option value="">--Select--</option>
				<logic:iterate id="peerGroup" name="tgppAAAPolicyForm" property="diameterPeerGroupDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup">
					<option value="<%=peerGroup.getPeerGroupName()%>"><%=peerGroup.getPeerGroupName()%></option>
				</logic:iterate>
			</select>
		</td>
		<td class="tblrowspolicy" width="15.83%" valign="middle">
			<input class="noborder" id="script_<%=widgetId%>" type="text" name="script" style="width: 100%;" maxlength="2000"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
		</td>
		<td class="tblrowspolicy" width="15.83%" valign="middle" align="center">
			<input type="text" id="acceptedResultCode_<%=widgetId%>" name="acceptedResultCode" class="acceptedResultCode noborder"  style="width: 100%"  pattern="[0-9,]*" title="Please enter a valid accepted result code(Comma Seperated Digit Only)." onkeypress="validateCommasNumber(event)"   <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /> 
		</td>
		<td class="tblrows" width="15.83%" valign="middle" align="center">
			<input type="checkbox" id="waitForResponse_<%=widgetId%>" value="true" checked="checked" name="waitForResponse" class="noborder waitForResponse" onclick="changeValuesWaitForResponse(this);"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /> 
		</td>
		<td class='tblrows' align="center" valign="middle" width="5%">
			<span class='delete remove-proxy-server'  <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>   onclick="deleteMe(this);" <%}%>  />&nbsp;
		</td>
	</tr>
</table>
</html>