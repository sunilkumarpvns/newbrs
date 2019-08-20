<%@include file="/jsp/core/includes/common/Header.jsp" %>

<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyConstants"%>
<%@page import="com.elitecore.corenetvertex.constants.SupportedStandard"%>
<%@page import="com.elitecore.corenetvertex.constants.UMStandard"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.DefaultAttributeMappingData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData" %>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%> 
<%@page import="com.elitecore.corenetvertex.constants.ChargingRuleInstallMode"%>
<%@page import="com.elitecore.netvertexsm.web.gateway.profile.form.CreateProfileForm"%>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyValueConstants"%>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyConstants"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<%-- <script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script> --%>
<script>
var validateUrl = '<%=basePath%>/validate.do?';
var basePath = '<%=basePath%>';
</script>
<script language="javascript" src="<%=request.getContextPath()%>/js/pccrulemapping.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/validateMapping.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

<style> 
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.bottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style> 

<%
String defaultpolicykeyfordynamicmapping ="{\"0:432\"=\"PCCRule.ChargingKey\";\"10415:1005\"=\"PCCRule.Name\";\"10415:1016\"={\"10415:1028\"=\"PCCRule.QCI\";\"10415:1025\"=\"PCCRule.GBRDL\";\"10415:1026\"=\"PCCRule.GBRUL\";\"10415:515\"=\"PCCRule.MBRDL\";\"10415:516\"=\"PCCRule.MBRUL\";\"10415:1034\"={\"10415:1046\"=\"PCCRule.PriorityLevel\";\"10415:1047\"=\"PCCRule.PEC\";\"10415:1048\"=\"PCCRule.PEV\"}};\"10415:1010\"=\"PCCRule.Precedence\";\"10415:1066\"=\"PCCRule.MonitoringKey\";\"10415:1009\"=\"PCCRule.OnlineCharging\";\"10415:1008\"=\"PCCRule.OfflineCharging\";\"10415:511\"=\"PCCRule.FlowStatus\";\"10415:1058\"={\"10415:507\"=\"PCCRule.ServiceDataFlow\"}}";
String defaultvaluemappingfordynamicmapping = "";
String defaultattributefordynamicmapping = "";

String defaultpolicykeyforstaticmapping = "PCCRule.Name";
String defaultattributeforstaticmapping = "10415:1005";
Boolean requestForDuplicate = (Boolean)request.getAttribute("requestForDuplicate");
%>



<script type="text/javascript">

var jDiameterToPCRFRefArray = new Array();
var jDPCount = 0;
var jPCRFToDiameterRefArray = new Array();
var jPDCount = 0;

$(document).ready(function(){
		setTitle('<bean:message bundle="gatewayResources" key="pccrulemapping.header"/> ');
		$('table td img.delete').on('click',function() {

		$(this).parent().parent().remove();
	
	});
	var mappingTypeArray = new Array();	
	mappingTypeArray[0] = "<%=PCRFKeyType.PCC_RULE.getVal()%>";
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		pccruleautocomplete("textarea.plcKey",dbFieldArray);
		return dbFieldArray;
	});
	$('#name').focus();
});


function policyKey(){
	var mappingTypeArray = new Array();	
	mappingTypeArray[0] = "<%=PCRFKeyType.PCC_RULE.getVal()%>";
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		pccruleautocomplete("textarea.plcKey",dbFieldArray);
		return dbFieldArray;
	});
}

function addRow(type) {
	if(type=='static') 
		$('<tr name="staticMapping"><td class="allborder"><input tabindex="4" class="noborder" type="text" name="attributeS" id="attribute" maxlength="200" style="width: 100%" /></td>'+
		  '<td class="tblrows" valign="top"><textarea cols="60" tabindex="4" rows="1" name="policyKeyS" id="policyKey" class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;"></textarea></td>'+
		  '<td class="tblrows"><input tabindex="4" class="noborder" type="text" name="defaultValueS" id="defaultValue" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows" valign="top"><textarea class="plcKey"  rows="1" cols="30" tabindex="4"  name="valueMappingS" id="valueMapping" maxlength="1000" style="overflow: hidden; border:none; text-align: justify;"></textarea></td>'+
		  '<td class="tblrows" align="center"  valign="middle"><img value="top"  tabindex="4" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" onclick="" /></td></tr>').appendTo('#'+type);
	else
		$('<tr name="dynamicMapping"><td class="allborder"><input  tabindex="10" class="noborder" type="text" name="attributeD" id="attribute" maxlength="200" style="width: 100%" /></td>'+
		  '<td class="tblrows" valign="top"><textarea tabindex="10" cols="60" rows="2" name="policyKeyD" id="policyKey" class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;"  ></textarea></td>'+
		  '<td class="tblrows"><input tabindex="10" class="noborder" type="text" name="defaultValueD" id="defaultValue" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows" valign="top"><textarea rows="2" cols="30" class="plcKey"  tabindex="10"  name="valueMappingD" id="valueMapping" value="<%=defaultvaluemappingfordynamicmapping%>" maxlength="1000" style="overflow: hidden; border: none; text-align: justify;"></textarea></td>'+
		  '<td class="tblrows" align="center" valign="middle"><img value="top" tabindex="10" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" /></td></tr>').appendTo('#'+type);

	 policyKey();
	$('table td img.delete').on('click',function() {

		$(this).parent().parent().remove();

	});
}



 function validate(){
	 callvalidatePCCRuleMapping();
	 if(isNull(document.forms[0].name.value)){
		alert('Rule Mapping Name must be specified.');
	}else if(!isValidName) {
		alert('Enter Valid Rule Mapping Name.');
		document.forms[0].name.focus();
		return;
	}else if(!validateStaticAttribute()){		
		return;		
	}else if(!validateStaticPolicyKey()){
		return;
	}else if(!validateDynamicAttribute()){		
		return;		
	}else if(!validateDynamicPolicyKey()){
		return;
	}else{
		var value = checkForNote();
		var confirmSubmit = true;
		if(value != null){
			if(value == "No Live Server Found" || value == "Timeout Occur"){
				confirmSubmit = confirm(message.serverMessage);
			}else{
				confirmSubmit = confirm(message.mappingMessage);
			}
			if(confirmSubmit == false){
				return;
			}
		}
		document.forms[0].submit();
	}
}
 var isValidName;
 function verifyFormat (){
		var searchName = document.getElementById("name").value;
		callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.PCCRULE_MAPPING%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
	}
 function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.PCCRULE_MAPPING%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

	

function validateStaticPolicyKey(){
	var flag=true;
	$("textarea[name=policyKeyS]").each(function() {
		if($(this).val().trim().length<1){
			alert("Static PCCRule Mapping :\n\nAttribute or Policy Key should not be empty.");
			flag= false;
			return flag;
		}
	});
	return flag;
}
function validateStaticAttribute(){
	var flag=true;
	$("input[name=attributeS]").each(function(){
		if($(this).val().trim().length<1){
			alert("Static PCCRule Mapping :\n\nAttribute or Policy Key should not be empty.");			 
			flag= false;
			return flag;
		}
	});
	return flag;
} 

function validateDynamicPolicyKey(){
	var flag=true;
	$("textarea[name=policyKeyD]").each(function() {
		if($(this).val().trim().length<1){
			alert("Dynamic PCCRule Mapping :\n\nAttribute or Policy Key should not be empty.");
			flag= false;
			return flag;
		}
	});
	return flag;
}
function validateDynamicAttribute(){
	var flag=true;
	$("input[name=attributeD]").each(function(){
		if($(this).val().trim().length<1){
			alert("Dynamic PCCRule Mapping :\n\nAttribute or Policy Key should not be empty.");			 
			flag= false;
			return flag;
		}
	});
	return flag;
} 

</script> 	

<html:form action="/pccRuleManagement.do?method=create">
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			Create PCC Rule Mapping
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"></td> 
		  </tr>
		   <tr>
		   <td width="100%" colspan="2" valign="top" class="noborder">
		   <table id="pccrulemappingtableid" cellSpacing="0" cellPadding="0" width="100%" border="0"> 
					<tr>
						<td width="100%" colspan="2" valign="top" >
							<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="mainTable">
								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">name</td>
									<sm:nvNameField maxLength="60" size="30" value="${pccRuleMappingForm.name}"/>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="20%"><bean:message bundle="gatewayResources"	key="mapping.description" /></td>
									<td align="left" class="labeltext" valign="top" width="*"><html:textarea property="description" cols="40" rows="3"  tabindex="2" /></td>
									</tr>
								<tr>
								<tr> 
							    	<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
							  	</tr> 
								<td colspan="3">
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td class=tblheader-bold colspan="5" style="padding-left: 2em"><bean:message bundle="gatewayResources" key="pccrulemapping.static" /> 
													<img src="<%=basePath%>/images/help-hover.jpg" height="12" 	width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.staticpccrulemapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.diameter.staticpccrulemapping" />')" />
												</td>
											</tr>
											<tr> 
								          	<td class="" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 2.3em; font-weight: bold" >
								            	<input type="button" name="add" value="Add Mapping" tabindex="3" class="light-btn" onclick="addRow('static')"></td>
								          	</tr>
								          <tr>
											<td width="10" class="small-gap">&nbsp;</td>
											<td class="small-gap" colspan="2">&nbsp;</td>
										  </tr>	
												 
										  <tr> 
										  	<td class="btns-td" valign="middle" colspan="3">  
												<table cellpadding="0" id="static" cellspacing="0" border="0" width="97%">
													<tr>
														<td align="center" class="tblheader" valign="top" width="10%">Attribute</td>
														<td align="center" class="tblheader" valign="top" width="40%">Policy Key</td>
														<td align="center" class="tblheader" valign="top" width="20%">Default Value</td>
														<td align="center" class="tblheader" valign="top" width="20%">Value Mapping</td>
														<td align="center" class="tblheader" valign="top" width="5%">Remove</td>
													</tr>
										  		<% if(requestForDuplicate !=null && requestForDuplicate){ %>
										  		<logic:iterate id="staticMap" name="pccRuleMappingForm" property="pccRuleMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData">
													<%	if(staticMap.getType().equalsIgnoreCase("STATIC")) {%>									
														<tr>
															<td align="left" class="tblfirstcol" valign="top">
																<input tabindex="4" class='noborder' type='text' name='attributeS' style="width: 100%" value="<%=StringEscapeUtils.escapeHtml(staticMap.getAttribute())%>"/></td>
															<td align="left" class="tblrows" valign="top">
																<textarea tabindex="4" cols="40" rows="1" name="policyKeyS" class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;"  ><%=StringEscapeUtils.escapeHtml(staticMap.getPolicyKey().trim())%></textarea>
															</td>
															<td align="left" class="tblrows" valign="top">
																<%  if(staticMap.getDefaultValue()==null || staticMap.getDefaultValue().trim().equalsIgnoreCase("null")){ %>
																 	<input tabindex="4" class='noborder' type='text' name='defaultValueS' style="width: 100%" value=""/></td>
																<% }else{%>
																	<input tabindex="4" class='noborder' type='text' name='defaultValueS' style="width: 100%" value="<%=StringEscapeUtils.escapeHtml(staticMap.getDefaultValue())%>"/></td>
																<% } %>															
															<td align="left" class="tblrows" valign="top">
																<% if(staticMap.getValueMapping()==null ||staticMap.getValueMapping().trim().equalsIgnoreCase("null")){ %>
																	<textarea class="plcKey"  rows="1" cols="26" tabindex="4"  name="valueMappingS" maxlength="1000" style="overflow: hidden; border:none; text-align: justify;"></textarea></td>
																<% }else{ %>
																	<textarea class="plcKey"  rows="1" cols="26" tabindex="4"  name="valueMappingS" maxlength="1000" style="overflow: hidden; border:none; text-align: justify;" value="<%=StringEscapeUtils.escapeHtml(staticMap.getValueMapping())%>"><%=StringEscapeUtils.escapeHtml(staticMap.getValueMapping())%></textarea></td>
																<% } %>														
															<td class="tblrows" align="center" valign="middle">
																<img tabindex="4" src="<%=basePath%>/images/minus.jpg" class="delete" height="14" onclick="removeMe('1')"/>
															</td>
														</tr>
													<% } %>								
												</logic:iterate>
												</table>
											<%}else{ %>
												<tr>
													<td class="allborder">
														<input tabindex="4" class="noborder" type="text" name="attributeS" maxlength="100" style="width: 100%" value="<%=defaultattributeforstaticmapping%>" />
													</td>
				                                    <td class="tblrows" valign="top">
				                                        	<textarea  rows="1" cols="60" tabindex="4"  name="policyKeyS" id="policyKey" class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;" ><%=StringEscapeUtils.escapeHtml(defaultpolicykeyforstaticmapping.trim())%></textarea>
				                                    </td> 
				                                    <td class="tblrows">
				                                    	<input tabindex="4" class="noborder" type="text" name="defaultValueS" maxlength="1000" style="width: 100%"/>
				                                    </td>
			                                        <td class="tblrows" valign="top">
			                                            	<textarea class="plcKey"  rows="1" cols="30" tabindex="4"  name="valueMappingS" id="valueMapping" maxlength="1000" style="overflow: hidden; border:none; text-align: justify;"></textarea>
			                                            </td>
			                                            <td class="tblrows" align="center"  valign="middle"><img value="top"  tabindex="4" src="<%=basePath%>/images/minus.jpg" class="delete" height="14" /></td>
			                                         </tr>
												</table>
											</td> 
										  </tr>
										  <%} %>	
											<tr>
												<td width="10" class="">&nbsp;</td>
											</tr>
											<tr>
												<td class=tblheader-bold colspan="5" style="padding-left: 2em"> 
													<bean:message bundle="gatewayResources" key="pccrulemapping.dynamic" /> 
													<img src="<%=basePath%>/images/help-hover.jpg" height="12" 	width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.dyanamicpccrulemapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.diameter.dyanamicpccrulemapping" />')" />
												</td
											</tr>
											<tr>
												<td width="10" class="small-gap">&nbsp;</td>
											</tr>
											<tr>
												<td width="10" class="small-gap">&nbsp;</td>
											</tr>
											<tr>
											<td class="" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 2.3em; font-weight: bold">
												<input type="button" name="add" value="Add Mapping" tabindex="9" class="light-btn" onclick="addRow('dynamic')">
											</td>
											</tr>
											<tr>
													<td width="10" class="small-gap">&nbsp;</td>
													<td class="small-gap" colspan="2">&nbsp;</td>
											</tr>
											<tr>
         											<td class="btns-td" valign="middle" colspan="3">
																<table cellpadding="0" id="dynamic" cellspacing="0"
																	border="0" width="97%">
																	<tr>
																		<td align="center" class="tblheader" valign="top" width="10%">Attribute</td>
																		<td align="center" class="tblheader" valign="top" width="40%">Policy Key</td>
																		<td align="center" class="tblheader" valign="top" width="20%">Default Value</td>
																		<td align="center" class="tblheader" valign="top" width="20%">Value Mapping</td>
																		<td align="center" class="tblheader" valign="top" width="5%">Remove</td>
																	</tr>


													<%if(requestForDuplicate != null && requestForDuplicate){ %>
         												<logic:iterate id="dynamicMap" name="pccRuleMappingForm" property="pccRuleMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData">
															<%if(dynamicMap.getType().equalsIgnoreCase("DYNAMIC")) { %>				
															<tr>
																<td align="left" class="tblfirstcol" valign="top">
																	<input tabindex="10"  class='noborder' type='text' name='attributeD' maxlength="100" style="width: 100%"  value="<%=StringEscapeUtils.escapeHtml(dynamicMap.getAttribute())%>"/>
																</td>
																<td align="left" class="tblrows" valign="top">
																	<textarea tabindex="10" cols="40" rows="2" name="policyKeyD" class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;"  ><%=StringEscapeUtils.escapeHtml(dynamicMap.getPolicyKey().trim())%></textarea>
																</td>
																<td align="left" class="tblrows" valign="top">
																	<% if(dynamicMap.getDefaultValue()==null ||dynamicMap.getDefaultValue().trim().equalsIgnoreCase("null")){%>
																		<input tabindex="10" class='noborder' type='text' name='defaultValueD' maxlength="1024" style="width: 100%" value=""/></td>
																	<%}else{%>
																		<input tabindex="10" class='noborder' type='text' name='defaultValueD' maxlength="1024" style="width: 100%" value="<%=StringEscapeUtils.escapeHtml(dynamicMap.getDefaultValue())%>"/></td>
																	<%}%>																			
																<td align="left" class="tblrows" valign="top">
																	<% if(dynamicMap.getValueMapping()==null ||dynamicMap.getValueMapping().trim().equalsIgnoreCase("null")){%>
																		<textarea class="plcKey" rows="2" cols="26" tabindex="10"  name="valueMappingD" value="" maxlength="1000" style="overflow: hidden;border: none; text-align: justify; "></textarea> </td>
																	<%}else{%>
																		<textarea class="plcKey" rows="2" cols="26" tabindex="10"  name="valueMappingD" value="<%=StringEscapeUtils.escapeHtml(dynamicMap.getValueMapping())%>" maxlength="1000" style="overflow: hidden;border: none; text-align: justify; "><%=StringEscapeUtils.escapeHtml(dynamicMap.getValueMapping())%></textarea>
																	<%}%>																			
																</td>
																<td class="tblrows" align="center" valign="middle">
																	<img  tabindex="10" src="<%=basePath%>/images/minus.jpg" class="delete" height="14" onclick="removeMe('1')"/>
																</td>
															</tr>
														<%}%>					
													</logic:iterate>
													<%}else{ %>
													<tr>
													<td class="allborder">
														<input  tabindex="10" class="noborder" type="text" name="attributeD" value="<%=defaultattributefordynamicmapping%>" maxlength="100" style="width: 100%" />
													</td>
			  										<td class="tblrows" valign="top">
			  											<textarea rows="2" cols="42" tabindex="10"  name="policyKeyD" class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;"  ><%=StringEscapeUtils.escapeHtml(defaultpolicykeyfordynamicmapping.trim())%></textarea>
			  										</td>
			  										<td class="tblrows">
			  											<input tabindex="10" class="noborder" type="text" name="defaultValueD" maxlength="1000" style="width: 100%"/>
			  										</td>
			  										<td class="tblrows" valign="top">
			  											<textarea class="plcKey" rows="2" cols="25" tabindex="10"  name="valueMappingD" value="<%=defaultvaluemappingfordynamicmapping%>" maxlength="1000" style="overflow: hidden;border: none; text-align: justify; "></textarea>
			  										<td class="tblrows" align="center" valign="middle">
			  											<img value="top" tabindex="10" src="<%=basePath%>/images/minus.jpg" class="delete" height="14" />
			  										</td>
			  									</tr>
			  									<%} %>
											</table>
													</td>
												</tr>
												<tr>
												<td width="10" class="small-gap">&nbsp;</td>
												<td class="small-gap" colspan="2">&nbsp;</td>
												</tr>
												<tr>
												<td width="10" class="small-gap">&nbsp;</td>
												<td class="small-gap" colspan="2">&nbsp;</td>
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
					<tr><td width="10" class="">&nbsp;</td></tr>	
					<tr>
						<td width="10" class="small-gap">&nbsp;</td>
					</tr>
					<tr>    
            	<td class="btns-td" align="left" valign="middle" style="padding-left: 300px;" >
            
		        <input type="button" onclick="validate();" tabindex="15" value=" Create " class="light-btn" />
		        <input type="button" align="left" value="  Cancel  " class="light-btn" tabindex="16" onclick="javascript:location.href='<%=basePath%>/pccRuleManagement.do?method=initSearch'"/> 
		     	</td>
	 		</tr> 
		</table> 
	  </td> 
	</tr> 
  	<%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
</html:form> 
