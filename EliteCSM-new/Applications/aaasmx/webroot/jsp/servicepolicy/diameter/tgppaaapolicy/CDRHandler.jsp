<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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
	$(document).ready(function(){
		setOtherDriverDropDown('form_cdrHandler');
	});
	
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
<form id="form_cdrHandler_<%=widgetId%>" name="form_cdrHandler_<%=widgetId%>" class="form_cdrHandler">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=TGPPAAAPolicyConstant.CDR_HANDLER%>" name="handlerType" id="handlerType" />
<input type="hidden" value="<%=widgetId%>" name="widgetId" id="widgetId_<%=widgetId%>" />
<input type="hidden" value="CDRHandler" name="handlerJsName" id="handlerJsName" />

<table name="tblmCDRHandler" width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td align="left" class="tbl-header-bold sortableAdditionalClass" valign="top" colspan="3">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="96%" align="left" class="tbl-header-bold" valign="top">
						<div class="handler-label"><bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.cdrhandler.title' /></div>
						<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.cdrhandler.title' />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled" style="display: none;"/>
						<input type="hidden" id="cdrhiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.cdrhandler.title' />" />
						<span class="handler-type">[<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.cdrhandler.title' />]</span>
					</td>
					<td align="left" class="tbl-header-bold" valign="top">
						<span class="edit_handler_icon"  title="Edit Handler Name" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %> onclick="changeHandlerName(this);" <%}%>  ></span>
						<span class="save_handler_icon"  title="Save Handler Name" style="display: none;" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="saveHandlerName(this);" <%}%>  ></span>
					</td>
					<td width="1%" align="left" class="tbl-header-bold" valign="middle" style="padding-right: 2px;line-height: 9px;">
						<div class="switch">
						  <input id="toggle-cdrhandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
						  <label for="toggle-cdrhandler_<%=widgetId%>"></label>
						</div>
					</td>
					<td width="1%" valign="middle"  class="tbl-header-bold" style="padding-right: 5px;">
						<img alt="Delete" class="delele_proxy" title="Delete"  src="<%=request.getContextPath()%>/images/delete_proxy.png"  height="14" width="14" style="cursor: pointer;" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="deleteHandler(this);" <%}%>  />
					</td>
					<td width="2%" valign="middle"  class="tbl-header-bold" style="padding-right: 10px;" onclick="expandCollapse(this);">
						<img alt="Expand" class="expand_class" title="Expand" id="cdrAdditionalGenerationImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"   />
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="cdrDiv" class="toggleDivs">
				<table name="tblmCDRTbl" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
							<input type="button" onclick='addNewCDRRow("cdrTemplate_<%=widgetId%>","mappingtblcdr_<%=widgetId%>",false,true);' value=" Add Driver " class="light-btn cdr-btn" style="size: 140px" tabindex="3"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> > 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="mappingtblcdr_<%=widgetId%>" class="mappingtblcdr">
								<tr>
									<td align="left" class="tblheader-policy" valign="top" width="19%" id="tbl_attrid">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.cdrhandler.ruleset" /> 
										<ec:elitehelp header="tgppaaapolicy.cdrhandler.ruleset" headerBundle="servicePolicyProperties" text="tgppaaapolicy.cdrhandler.ruleset" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.cdrhandler.primarydriver" /> 
									    <ec:elitehelp header="tgppaaapolicy.cdrhandler.primarydriver" headerBundle="servicePolicyProperties" text="tgppaaapolicy.cdrhandler.primarydriver" ></ec:elitehelp>
										<span style="float: right;" title="Refresh driver list" class="refresh-driver" onclick="reloadDriverList();">
										</span>  
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.cdrhandler.secondarydriver" /> 
									    <ec:elitehelp header="tgppaaapolicy.cdrhandler.secondarydriver" headerBundle="servicePolicyProperties" text="tgppaaapolicy.cdrhandler.secondarydriver" ></ec:elitehelp>
										  <span style="float: right;" title="Refresh driver list" class="refresh-driver" onclick="reloadDriverList();">
										</span>  
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.cdrhandler.driverscript" /> 
									    <ec:elitehelp header="tgppaaapolicy.cdrhandler.driverscript" headerBundle="servicePolicyProperties" text="tgppaaapolicy.cdrhandler.driverscript" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.cdrhandler.waitforcdrdump" /> 
									    <ec:elitehelp header="tgppaaapolicy.cdrhandler.waitforcdrdump" headerBundle="servicePolicyProperties" text="tgppaaapolicy.cdrhandler.waitforcdrdump" ></ec:elitehelp>
									</td>
										<td align="left" class="tblheader-policy" valign="top" width="5%">Remove</td>
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
<table id="cdrTemplate_<%=widgetId%>" style="display: none">
	<tr>
		<td class='tblfirstcol' width="19%">
			<input class="noborder ruleset" id="ruleset_<%=widgetId%>" type="text" style="width: 100%;" name="ruleset" maxlength="2000"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
		</td>
		<td class="tblrows" width="19%">
			<select name="primaryDriverId" id="primaryDriverId_<%=widgetId%>" class="noborder primaryDriverId" style="width:100%;"  onclick="if (typeof(this.selectedIndex) != 'undefined') openDriverWizard(this.selectedIndex,this,mappingtblcdr_<%=widgetId%>);"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
			</select>
		</td>
		<td class="tblrows" width="19%">
			<select name="secondaryDriverId" id="secondaryDriverId_<%=widgetId%>" class="noborder secondaryDriverId" style="width:100%;" onclick="if (typeof(this.selectedIndex) != 'undefined') openDriverWizard(this.selectedIndex,this,mappingtblcdr_<%=widgetId%>);"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
			</select>
		</td>
		<td class="tblrows" width="19%">
			<input class="noborder script scriptInstAutocomplete" id="script_<%=widgetId%>" type="text" name="driverScript" maxlength="2000" style="width: 100%;"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
		</td>
		<td class="tblrows" width="19%" >
			<input type="checkbox" id="waitForCDRDump_<%=widgetId%>" value="true" checked="checked" name="wait" class="noborder waitForCDRDump" onclick="changeValuesWaitForResponse(this);"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /> 
		</td>
		<td class='tblrows' align="center" width="5%">
			<span class='delete remove-proxy-server'  <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>   onclick="deleteMe(this);" <%}%>  />&nbsp;
		</td>
	</tr>
</table>
</body>
</html>