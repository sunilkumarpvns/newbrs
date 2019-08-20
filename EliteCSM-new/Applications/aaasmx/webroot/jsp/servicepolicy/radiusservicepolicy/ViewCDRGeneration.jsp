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
	<%
	   String widgetId=request.getParameter("widgetId");
	   String isAdditional=request.getParameter("isAdditional");
	   String orderNumber=request.getParameter("orderNumber");
	   String jsonData=request.getParameter("jsonData");
	   
	   if(isAdditional.equalsIgnoreCase("true")){
		   widgetId=widgetId+"_additionalCDRTable";
	   }else{
		   widgetId=widgetId+"_authenticationCDRTable";
	   }
	%>
	<script type="text/javascript">
	
	function changeValuesForCdrDump(checkbox){
		if($(checkbox).attr('checked')){
			$(checkbox).val('true');
		}else{
			$(checkbox).val('false');
		}
	}
	
	var formData = <%=jsonData%>;
	
	$.each(formData, function(key,value){
		 if(key == 'cdrGenerationDetailsList'){
			 $.each(value, function(jsonKey,jsonValue){
				addNewCDRRow("cdrTemplate_<%=widgetId%>","mappingtblcdr_<%=widgetId%>",true);
				
				//set Ruleset Data
				var rulesetrow = $("#mappingtblcdr_<%=widgetId%>"+" tr:last").find("input[name='ruleset']");
				$(rulesetrow).val(filterRuleset(jsonValue.ruleset));
				
				//primary driver data
				var primaryServerrow =$("#mappingtblcdr_<%=widgetId%>"+" tr:last").find("select[name='primaryDriverId']");
				$(primaryServerrow).append(generateOptionFromJSON(String(jsonValue.secondaryDriverId)));
				$(primaryServerrow).val(jsonValue.primaryDriverId);
				
				//secondary driver data
				var secondaryServerrow =$("#mappingtblcdr_<%=widgetId%>"+" tr:last").find("select[name='secondaryDriverId']");
				$(secondaryServerrow).append(generateOptionFromJSON(String(jsonValue.primaryDriverId)));
				$(secondaryServerrow).val(jsonValue.secondaryDriverId);
				
				//script
				var scriptrow =$("#mappingtblcdr_<%=widgetId%>"+" tr:last").find("input[name='script']");
				$(scriptrow).val(jsonValue.script);
				
				//waitForCDRDump
				var waitForCDRDumpRow = $("#mappingtblcdr_<%=widgetId%>"+" tr:last").find("input:checkbox[name='waitForCDRDump']");
				
				if(jsonValue.waitForCDRDump == 'true'){
		   			$(waitForCDRDumpRow).attr('checked', true);
		   			$(waitForCDRDumpRow).val(jsonValue.waitForCDRDump);
		   	    }else{
		   	    	$(waitForCDRDumpRow).attr('checked', false);
		   	    	$(waitForCDRDumpRow).val(jsonValue.waitForCDRDump);
		   	    }
			}); 
		} else if(key == 'isHandlerEnabled'){
		 	if( value == "true" ){
		 		$('#toggle-cdrhandler_<%=widgetId%>').attr('checked', true);
		 		$('#toggle-cdrhandler_<%=widgetId%>').val('true');
		 	}else{
		 		$('#toggle-cdrhandler_<%=widgetId%>').attr('checked', false);
		 		$('#toggle-cdrhandler_<%=widgetId%>').val('false');
		 		var handlerObject=$('#toggle-cdrhandler_<%=widgetId%>').closest('table[class^="handler-class"]');
		 		$(handlerObject).find('tr').each(function(){
		 			$(this).addClass('disable-toggle-class');
		 		});
		 	}
		}
		 
		if( key == 'handlerName'){
			if( value.length > 0 ){
				 $('#cdrGenhandlerName'+'<%=widgetId%>').attr('size', value.length);
				 $('#cdrGenhandlerName'+'<%=widgetId%>').val(value);
			}
		}
	});

</script>
</head>
<body>
<form id="form_cdrgen_<%=widgetId%>" name="form_cdrgen_<%=widgetId%>" class="form_cdrGeneretaion">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>
<table name="tblmAdditionalCDRGeneration" width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
	<tr style="cursor: pointer;">
		<td align="left" class="tbl-header-bold sortableAdditionalClass" valign="top" colspan="3">
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="96%" align="left" class="tbl-header-bold" valign="top">
						<input type="text" id="cdrGenhandlerName<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle="servicePolicyProperties" key="servicepolicy.proxypolicy.cdrgeneration" />" onkeyup="expand(this);" onload="expand(this);" disabled="disabled"/>
					</td>
					<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
						<div class="switch">
						  <input id="toggle-cdrhandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" disabled="disabled"/>
						  <label for="toggle-cdrhandler_<%=widgetId%>"></label>
						</div>
					</td>
					<td width="2%" valign="middle" class="tbl-header-bold" style="padding-right: 10px;" onclick="expandCollapse(this);">
						<img alt="Expand" class="expand_class" title="Expand" id="cdrAdditionalGenerationImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="cdrDiv" class="toggleDivs" style="display: block;">
				<table name="tblmCDRTbl" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
					<tr>
						<td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
							<input type="button" onclick='addNewCDRRow("cdrTemplate_<%=widgetId%>","mappingtblcdr_<%=widgetId%>",this);' value=" Add Driver " class="light-btn" style="size: 140px" tabindex="3" disabled="disabled"> 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="mappingtblcdr_<%=widgetId%>" class="mappingtblcdr">
								<tr>
									<td align="left" class="tblheader-policy" valign="top" width="19%" id="tbl_attrid">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cdrgen.ruleset" /> 
										<ec:elitehelp header="radiusservicepolicy.cdrgen.ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.cdrgen.ruleset" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cdrgen.primarydriver" /> 
									    <ec:elitehelp header="radiusservicepolicy.cdrgen.primarydriver" headerBundle="servicePolicyProperties" text="radiusservicepolicy.cdrgen.primarydriver" ></ec:elitehelp>
										<span style="float: right;" title="Refresh driver list" class="refresh-driver" onclick="reloadDriverList();">
										</span> 
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cdrgen.secondarydriver" /> 
 									    <ec:elitehelp header="radiusservicepolicy.cdrgen.secondarydriver" headerBundle="servicePolicyProperties" text="radiusservicepolicy.cdrgen.secondarydriver" ></ec:elitehelp>
										<span style="float: right;" title="Refresh driver list" class="refresh-driver" onclick="reloadDriverList();">
										</span> 
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cdrgen.driverscript" /> 
									    <ec:elitehelp header="radiusservicepolicy.cdrgen.driverscript" headerBundle="servicePolicyProperties" text="radiusservicepolicy.cdrgen.driverscript" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader-policy" valign="top" width="19%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cdrgen.waitforcdrdump" /> 
									    <ec:elitehelp header="radiusservicepolicy.cdrgen.waitforcdrdump" headerBundle="servicePolicyProperties" text="radiusservicepolicy.cdrgen.waitforcdrdump" ></ec:elitehelp>
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
			<input class="noborder ruleset" id="ruleset_<%=widgetId%>" type="text" style="width: 100%;" name="ruleset" maxlength="2000" disabled="disabled"/>
		</td>
		<td class="tblrows" width="19%">
			<select name="primaryDriverId" id="primaryDriverId_<%=widgetId%>" class="noborder primaryDriverId" style="width:210px"  onclick="if (typeof(this.selectedIndex) != 'undefined') openDriverWizard(this.selectedIndex,this,mappingtblcdr_<%=widgetId%>);" disabled="disabled">
			</select>
		</td>
		<td class="tblrows" width="19%">
			<select name="secondaryDriverId" id="secondaryDriverId_<%=widgetId%>" class="noborder secondaryDriverId" style="width:210px" onclick="if (typeof(this.selectedIndex) != 'undefined') openDriverWizard(this.selectedIndex,this,mappingtblcdr_<%=widgetId%>);" disabled="disabled">
			</select>
		</td>
		<td class="tblrows" width="19%">
			<input class="noborder script" id="script_<%=widgetId%>" type="text" name="script" style="width: 100%;" maxlength="2000" disabled="disabled"/>
		</td>
		<td class="tblrows" width="19%" >
			<input type="checkbox" id="waitForCDRDump_<%=widgetId%>" value="false" name="waitForCDRDump" class="noborder waitForCDRDump" onclick="changeValuesForCdrDump(this);" disabled="disabled"/> 
		</td>
		<td class='tblrows' align="center" width="5%">
			<img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' disabled="disabled"/>&nbsp;
		</td>
	</tr>
</table>
</body>
</html>