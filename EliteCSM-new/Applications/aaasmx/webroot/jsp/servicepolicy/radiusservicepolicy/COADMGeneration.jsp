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
	
	if(isAdditional.equalsIgnoreCase("true")){
		   widgetId=widgetId+"_additional";
	}else{
		   widgetId=widgetId+"_authentication";
	}
%>
<script type="text/javascript">
function changeValuesAcceptOnTimeOut(checkbox){
	if($(checkbox).attr('checked')){
		$(checkbox).val('true');
	}else{
		$(checkbox).val('false');
	}
}
</script>
</head>
<body>
<form id="form_cdrdmgen_<%=widgetId%>" name="form_cdrdmgen_<%=widgetId%>" class="form_coaDMGeneretaion">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>
<table name="tblmCOADMGeneration" width="100%" border="0" style="background-color: white;" class="handler-class tblmCOADMGeneration" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td align="left" class="tbl-header-bold sortableAdditionalClass" valign="top" colspan="3">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="96%" align="left" class="tbl-header-bold" valign="top">
						<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.coadmgeneration" />" onkeyup="expand(this);" onload="expand(this);" disabled="disabled"/>
						<input type="hidden" id="coaDmHiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.coadmgeneration" />" />
						<input type="hidden" id="coaDmHandlerType_<%=widgetId%>" name="handlerType" class="handlerType" value="coaDMGen" />
					</td>
					<td class="editable_icon_bg">
						<span class="edit_handler_icon" onclick="changeHandlerName(this);" title="Edit Handler Name"></span>
						<span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;"></span>
					</td>
					<td width="1%" align="left" class="tbl-header-bold" valign="middle" style="padding-right: 2px;line-height: 9px;">
						<div class="switch">
						  <input id="toggle-coadmhandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);"/>
						  <label for="toggle-coadmhandler_<%=widgetId%>"></label>
						</div>
					</td>
					<td width="1%" valign="middle" class="tbl-header-bold" style="padding-right: 5px;">
						<img alt="Delete" class="delele_proxy" title="Delete"  src="<%=request.getContextPath()%>/images/delete_proxy.png" onclick="deleteHandler(this);" height="14" width="14" style="cursor: pointer;"/>
					</td>
					<td width="2%" valign="middle" class="tbl-header-bold" style="padding-right: 10px;" onclick="expandCollapse(this);">
						<img alt="Expand" class="expand_class"  title="Expand" id="coadmgenerationImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="coadmgenerationDiv" class="toggleDivs">
				<table name="tblmcoadmgenerationTbl" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="mappingtblcoadmgeneration">
									<tr>
										<td align="left" class="" colspan="2" style="">
											<table cellspacing="0" cellpadding="0" border="0" width="100%">
												<tr>
													<td  colspan="4" style="padding-top: 10px;">
														<input type="button" onclick='addAttributeMappingRow("coaDMGenAttribTemplate_<%=widgetId%>","coaDmGeneration_<%=widgetId%>");'  value=" Add Attribute Mapping " class="light-btn coa-dm-btn" style="size: 140px" tabindex="3"> 
													</td>
													<td class="labeltext" align="right" style="padding-top: 10px;">
														<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.coadmgeneration.scheduleafter" />
														<input type="text" style="width: 50px;text-align: right;" value="2000" name="scheduleAfterInMillis" class="scheduleAfterInMillis"/>
														<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.coadmgeneration.ms" />
														<ec:elitehelp header="radiusservicepolicy.coadmgeneration.scheduleafter" headerBundle="servicePolicyProperties" text="radiusservicepolicy.coadmgeneration.scheduleafter" ></ec:elitehelp>												
													</td>
												</tr>
												<tr>
													<td align="left" valign="top" id="button" class="toggleDivs" colspan="5">
														<table width="100%" cellspacing="0" cellpadding="0" border="0" id="coaDmGeneration_<%=widgetId%>" class="coaDmGenerationtbl">
															<tr>
																<td class="tblheader-policy" width="30%" valign="top">
																	<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.ruleset" />
																	<ec:elitehelp header="servicepolicy.authpolicy.ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.coadmgeneration.ruleset" ></ec:elitehelp>
																</td>
																<td class="tblheader-policy" width="30%" valign="top">
																	<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.coadmgeneration.packettype" />
																	<ec:elitehelp header="radiusservicepolicy.coadmgeneration.packettype" headerBundle="servicePolicyProperties" text="radiusservicepolicy.coadmgeneration.packettype" ></ec:elitehelp>												
																</td>
																<td class="tblheader-policy" width="30%" valign="top">
																	<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.coadmgeneration.translationmapping" />
																	<ec:elitehelp header="radiusservicepolicy.coadmgeneration.translationmapping" headerBundle="servicePolicyProperties" text="radiusservicepolicy.coadmgeneration.translationmapping" ></ec:elitehelp>												
																</td>
																<td class="tblheader-policy" width="10%" valign="top">
																	<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.coadmgeneration.remove" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
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
<table id="coaDMGenAttribTemplate_<%=widgetId%>" style="display: none">
	<tr>
		<td class='tblfirstcol' width="30%">
			<input class="noborder ruleset" id="ruleset_<%=widgetId%>" type="text" style="width: 100%;" name="ruleset" maxlength="2000"/>
		</td>
		<td class="tblrows" width="30%">
			<select class="packetType noborder" id="packetType_<%=widgetId%>" style="width: 100%;" id="packetType" name="packetType" onchange="setPacketTypeForCOADM(this);">
				<option value="0">--Select--</option>
				<option value="COA Request">COA Request</option>
				<option value="Disconnect Request">Disconnect Request</option>
			</select>
		</td>
		<td class="tblrows" width="30%">
			<select class="translationMapping noborder"  name="translationMapping" id="translationMapping_<%=widgetId%>" style="width: 100%;">
					<option value="">--Select--</option>
					<optgroup label="Translation Mapping" class="labeltext">
						<logic:iterate id="translationMapping" name="createRadiusServicePolicyForm" property="translationMappingConfDataList" type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
							<option value="<%=translationMapping.getName()%>" class="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></option>
						</logic:iterate>
					</optgroup>
	
					<optgroup label="Copy Packet Mapping" class="labeltext">
						<logic:iterate id="copyPacketMapping" name="createRadiusServicePolicyForm" property="copyPacketMappingConfDataList" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData">
							<option value="<%=copyPacketMapping.getName()%>" styleClass="<%=ConfigConstant.COPY_PACKET_MAPPING%>"><%=copyPacketMapping.getName()%></option>
						</logic:iterate>
					</optgroup>
			</select>
		</td>
		<td class='tblrows' align="center" width="10%" valign="middle"><img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' style="vertical-align: middle;"/>&nbsp;</td>
	</tr>
</table>
</html>