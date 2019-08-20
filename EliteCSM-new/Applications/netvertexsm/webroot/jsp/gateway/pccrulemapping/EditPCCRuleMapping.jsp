<%@ page import="com.elitecore.corenetvertex.constants.ProtocolType"%>
<%@ page import="com.elitecore.corenetvertex.constants.ConversionType"%>
<%@ page import="com.elitecore.corenetvertex.constants.PacketType"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.AttributeMappingData"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.netvertexsm.web.gateway.pccrulemapping.form.PCCRuleMappingForm"%>
<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>

<% 
    PCCRuleMappingForm pccRuleMappingForm=(PCCRuleMappingForm)request.getAttribute("pccRuleMappingForm");
	String defaultpolicykeyfordynamicmapping ="{\"0:432\"=\"PCCRule.ChargingKey\";\"10415:1005\"=\"PCCRule.Name\";\"10415:1016\"={\"10415:1028\"=\"PCCRule.QCI\";\"10415:1025\"=\"PCCRule.GBRDL\";\"10415:1026\"=\"PCCRule.GBRUL\";\"10415:515\"=\"PCCRule.MBRDL\";\"10415:516\"=\"PCCRule.MBRUL\";\"10415:1034\"={\"10415:1046\"=\"PCCRule.PriorityLevel\";\"10415:1047\"=\"PCCRule.PEC\";\"10415:1048\"=\"PCCRule.PEV\"}};\"10415:1010\"=\"PCCRule.Precedence\";\"10415:1066\"=\"PCCRule.MonitoringKey\";\"10415:1009\"=\"PCCRule.OnlineCharging\";\"10415:1008\"=\"PCCRule.OfflineCharging\";\"10415:511\"=\"PCCRule.FlowStatus\";\"10415:1058\"={\"10415:507\"=\"PCCRule.ServiceDataFlow\"}}";
	String defaultvaluemappingfordynamicmapping = "";
	String defaultattributefordynamicmapping = "10415:1003";

	String defaultpolicykeyforstaticmapping = "PCCRule.Name";
	String defaultattributeforstaticmapping = "10415:1005";

	
%>

<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>
<script>
var validateUrl = '<%=basePath%>/validate.do?';
var basePath = '<%=basePath%>';
</script>
<script language="javascript" src="<%=request.getContextPath()%>/js/pccrulemapping.js"></script>
<script language="javascript" 	src="<%=request.getContextPath()%>/js/validateMapping.js"></script>
<style> 
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.bottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style> 
<script type="text/javascript">

var jDiameterToPCRFRefArray = new Array();
var jDPCount = 0;
var jPCRFToDiameterRefArray = new Array();
var jPDCount = 0;

$(document).ready(function(){
		verifyName();
		$('table td img.delete').on('click',function() {
		$(this).closest('tr').next().remove();
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
		  '<td class="tblrows" valign="top"><textarea tabindex="4" cols="35" rows="1" name="policyKeyS" id="policyKey" class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;"  ></textarea></td>'+
		  '<td class="tblrows"><input tabindex="4" class="noborder" type="text" name="defaultValueS" id="defaultValue" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows" valign="top"><textarea class="plcKey"  rows="1" cols="35" tabindex="4"  name="valueMappingS" id="valueMapping" maxlength="1000" style="overflow: hidden; border:none; text-align: justify;"></textarea></td>'+
		  '<td class="tblrows" align="center"  valign="middle"><img value="top"  tabindex="4" src="<%=basePath%>/images/minus.jpg" class="delete" height="14" /></td></tr>').appendTo('#'+type);
	else
		$('<tr name="dynamicMapping"><td class="allborder"><input  tabindex="10" class="noborder" type="text" name="attributeD" id="attribute" maxlength="200" style="width: 100%" /></td>'+
		  '<td class="tblrows" valign="top"><textarea cols="35" tabindex="10" rows="1" name="policyKeyD" id="policyKey" class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;"></textarea></td>'+
		  '<td class="tblrows"><input tabindex="10" class="noborder" type="text" name="defaultValueD" id="defaultValue" maxlength="1024" style="width: 100%"/></td>'+
		  '<td class="tblrows" valign="top"><textarea class="plcKey" rows="2" cols="35" tabindex="10"  name="valueMappingD" id="valueMapping" value="" maxlength="1000" style="overflow: hidden;border: none; text-align: justify; "></textarea></td>'+
		  '<td class="tblrows" align="center" valign="middle"><img value="top" tabindex="10" src="<%=basePath%>/images/minus.jpg" class="delete" height="14" /></td></tr>').appendTo('#'+type);		
	policyKey();
	$('table td img.delete').on('click',function() {
		//$(this).closest('tr').next().remove();
		$(this).parent().parent().remove();
	});
}


 function validate() {
	 callvalidatePCCRuleMapping();
	 if(isNull(document.forms[0].name.value)){
			alert('Packet Mapping Name must be specified.');
		}else if(!isValidName) {
			alert('Enter Valid Packet Mapping Name.');
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
 function verifyFormat (){
		var searchName = document.getElementById("name").value;
		callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.PCCRULE_MAPPING%>',searchName:searchName,mode:'update',id:'<%=pccRuleMappingForm.getMappingId()%>'},'verifyNameDiv');
}
 function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.PCCRULE_MAPPING%>',searchName:searchName,mode:'update',id:'<%=pccRuleMappingForm.getMappingId()%>'},'verifyNameDiv');
	}
	var isValidName;

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


    
    
<html:form action="/pccRuleManagement.do?method=update">	
   <html:hidden name="pccRuleMappingForm" property="mappingId"/>
   	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	 <tr>
	  <td valign="top" align="right"> 
		<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		<bean:define id="packetMappingDataBean" name="ruleMappingData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData" />
		<tr>
			<td align="right" class="labeltext" valign="top" class="box" > 
				<table cellpadding="0" cellspacing="0" border="0" width="100%" >	
				  <tr>
				  	<td style="margin-left: 2.0em;" colspan="5" class="tblheader-bold">View PCC Rule Mapping</td>
				  </tr>
		          <tr> 
		            <td class="tbllabelcol" width="20%" height="20%"><bean:message bundle="gatewayResources" key="mapping.name"/></td>
		            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="packetMappingDataBean" property="name"/>&nbsp;</td>
		          </tr>

		          <tr> 
		            <td class="tbllabelcol" width="20%" height="20%"><bean:message bundle="gatewayResources" key="mapping.description"/></td>
		            <td class="tblcol" width="70%" height="20%" colspan="3"><bean:write name="packetMappingDataBean" property="description"/>&nbsp;</td>
		          </tr>
				
		        </table>		        
			</td>
		</tr>
	</table>
			</td>
	    </tr>
	</table>
<br>
   
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
	<tr><td class="tblheader-bold" colspan="2">Update PCC Rule Mapping</td></tr>
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="gatewayResources" key="mapping.name"/></td> 
					<sm:nvNameField maxLength="60" size="30" value="${pccRuleMappingForm.name}"/>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="gatewayResources" key="mapping.description"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea property="description" cols="40" rows="3" tabindex="2" />
					</td> 
				  </tr>    
		  <tr><td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td></tr>
		  <tr> 
			<td class="tblheader-bold" colspan="8">Mapping</td>
		  </tr>		  		  	 		                              
          
          <tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" colspan="2">&nbsp;</td>
		  </tr>	
			<tr id="pccrules">
			 <td colspan="2" >
				 <table cellpadding="0" cellspacing="0" border="0" width="100%">
		  			<tr>
		  				<td class=tblheader-bold colspan="2" >
		  					<bean:message bundle="gatewayResources" key="pccrulemapping.static" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.staticpccrulemapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.diameter.staticpccrulemapping" />')"/>
		  				</td>
		  			</tr>
		  			<tr>
		  				<td width="10" class="small-gap">&nbsp;</td>
		  				<td class="small-gap" >&nbsp;</td>
		  			</tr>
					<tr> 
		  				<td valign="middle" colspan="2">  
							<table cellpadding="0" id="static" cellspacing="0" border="0" width="97%" >
							<tr> 
				          		<td><input type="button" name="add" value="Add Mapping" tabindex="3" class="light-btn" onclick="addRow('static')"></td>
					        </tr>
							
								<tr>
									<td align="center" class="tblheader" valign="top" width="20%">Attribute</td>
									<td align="center" class="tblheader" valign="top" width="20%">Policy Key</td>
									<td align="center" class="tblheader" valign="top" width="20%">Default Value</td>
									<td align="center" class="tblheader" valign="top" width="30%">Value Mapping</td>
									<td align="center" class="tblheader" valign="top" width="5%">Remove</td>
								</tr>
								<logic:iterate id="staticMap" name="pccRuleMappingForm" property="pccRuleMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData">
								<%	if(staticMap.getType().equalsIgnoreCase("STATIC")) {%>									
													<tr name="staticMapping">
														<td align="left" class="tblfirstcol" valign="top">
															<input tabindex="4" class='noborder' type='text' name='attributeS' id="attribute" style="width: 100%" value="<%=StringEscapeUtils.escapeHtml(staticMap.getAttribute())%>"/></td>
														<td align="left" class="tblrows" valign="top">
														<textarea tabindex="4" cols="35" rows="1" name="policyKeyS" id="policyKey" class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;"  ><%=StringEscapeUtils.escapeHtml(staticMap.getPolicyKey().trim())%></textarea>
															</td>
														<td align="left" class="tblrows" valign="top">
															<%  if(staticMap.getDefaultValue()==null || staticMap.getDefaultValue().trim().equalsIgnoreCase("null")){ %>
																 	<input tabindex="4" class='noborder' type='text' name='defaultValueS' id='defaultValue' style="width: 100%" value=""/></td>
															<% }else{%>
																	<input tabindex="4" class='noborder' type='text' name='defaultValueS' id='defaultValue' style="width: 100%" value="<%=StringEscapeUtils.escapeHtml(staticMap.getDefaultValue())%>"/></td>
															<% } %>															
														<td align="left" class="tblrows" valign="top">
															<% if(staticMap.getValueMapping()==null ||staticMap.getValueMapping().trim().equalsIgnoreCase("null")){ %>
																<textarea class="plcKey"  rows="1" cols="35" tabindex="4"  name="valueMappingS" id="valueMapping" maxlength="1000" style="overflow: hidden; border:none; text-align: justify;"></textarea></td>
															<% }else{ %>
																	<textarea class="plcKey"  rows="1" cols="26" tabindex="4"  name="valueMappingS" maxlength="1000" style="overflow: hidden; border:none; text-align: justify;" value="<%=StringEscapeUtils.escapeHtml(staticMap.getValueMapping())%>"><%=StringEscapeUtils.escapeHtml(staticMap.getValueMapping())%></textarea></td>
															<% } %>														
														<td class="tblrows" align="center" valign="middle">
															<img tabindex="4" src="<%=basePath%>/images/minus.jpg" class="delete" height="14" onclick="removeMe('1')"/></td>
													</tr>
								<% } %>								
								</logic:iterate>
							</table>
						</td> 
		 			 </tr>	
					  <tr><td width="10" class="">&nbsp;</td></tr>		
		  			  <tr>
		  			  	<td class=tblheader-bold  colspan="2" >
		  			  		<bean:message bundle="gatewayResources" key="pccrulemapping.dynamic" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.diameter.dyanamicpccrulemapping"/>','<bean:message bundle="gatewayResources" key="gateway.profile.diameter.dyanamicpccrulemapping" />')"/>
		  			  	</td>
		  			  </tr>
					  <tr>
					  	<td width="10" class="small-gap">&nbsp;</td>
					  	<td class="small-gap" >&nbsp;</td>
					  </tr>

					  <tr> 
					  	<td valign="middle" colspan="2">  
							<table cellpadding="0" id="dynamic" cellspacing="0" border="0" width="97%" >
					  			<tr> 
			          				<td><input type="button" name="add" value="Add Mapping" tabindex="9" class="light-btn" onclick="addRow('dynamic')"></td>
			          			</tr>							
								<tr>
									<td align="center" class="tblheaderfirstcol" valign="top" width="20%">Attribute</td>
									<td align="center" class="tblheader" valign="top" width="20%">Policy Key</td>
									<td align="center" class="tblheader" valign="top" width="20%">Default Value</td>
									<td align="center" class="tblheader" valign="top" width="30%">Value Mapping</td>
									<td align="center" class="tblheaderlastcol" valign="top" width="5%">Remove</td>
								</tr>
							<logic:iterate id="dynamicMap" name="pccRuleMappingForm" property="pccRuleMappingList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData">
							<%if(dynamicMap.getType().equalsIgnoreCase("DYNAMIC")) { %>				
								<tr name="dynamicMapping">
									<td align="left" class="tblfirstcol" valign="top">
										<input tabindex="10"  class='noborder' type='text' name='attributeD' id='attribute' maxlength="100" style="width: 100%"  value="<%=StringEscapeUtils.escapeHtml(dynamicMap.getAttribute())%>"/></td>
									<td align="left" class="tblrows" valign="top">
									<textarea tabindex="10" cols="35" rows="2" name="policyKeyD" id='policyKey' class="plcKey"  maxlength="4000" style="overflow: hidden;border: none; text-align: justify;"  ><%=StringEscapeUtils.escapeHtml(dynamicMap.getPolicyKey().trim())%></textarea>
									</td>
									<td align="left" class="tblrows" valign="top">
										<% 
											if(dynamicMap.getDefaultValue()==null ||dynamicMap.getDefaultValue().trim().equalsIgnoreCase("null")){
										%>
											<input tabindex="10" class='noborder' type='text' name='defaultValueD' id='defaultValue' maxlength="1024" style="width: 100%" value=""/></td>
										<%	
											}else{
										%>
											<input tabindex="10" class='noborder' type='text' name='defaultValueD' id='defaultValue' maxlength="1024" style="width: 100%" value="<%=StringEscapeUtils.escapeHtml(dynamicMap.getDefaultValue())%>"/></td>
										<%																    	
											}
										%>																			
									<td align="left" class="tblrows" valign="top">
										<% 
											if(dynamicMap.getValueMapping()==null ||dynamicMap.getValueMapping().trim().equalsIgnoreCase("null")){
										%>
											<textarea class="plcKey" rows="2" cols="35" tabindex="10"  name="valueMappingD" id="valueMapping" value="" maxlength="1000" style="overflow: hidden;border: none; text-align: justify; "></textarea> </td>
										<%	
											}else{
										%>
											<textarea class="plcKey" rows="2" cols="26" tabindex="10"  name="valueMappingD" value="<%=StringEscapeUtils.escapeHtml(dynamicMap.getValueMapping())%>" maxlength="1000" style="overflow: hidden;border: none; text-align: justify; "><%=StringEscapeUtils.escapeHtml(dynamicMap.getValueMapping())%></textarea></td>
										<%																    	
											}
										%>																			
									<td class="tblrows" align="center" valign="middle">
										<img  tabindex="10" src="<%=basePath%>/images/minus.jpg" class="delete" height="14" onclick="removeMe('1')"/></td>
								</tr>
			<%				}				 %>					
							</logic:iterate>
							</table>
							
						</td> 
					  </tr>

				  </table>
				</td>
			</tr>	
			 	<tr> 
					<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td> 
		 		</tr>
        		<tr> 
	        		<td class="btns-td" valign="middle" align="center"> 
		        		<input type="button" align="left" id="updateBtn" onclick="validate();" value="  Update  " class="light-btn" tabindex="11" />
		        		<input type="button" align="left" tabindex="12" value="  Cancel  " class="light-btn" onclick="javascript:location.href='<%=basePath%>/pccRuleManagement.do?method=initSearch'"/>
		        	</td> 
   		 		</tr>
   		 		 				 
			 </table> 
		</td> 
	</tr>		 				 				  					 
</table>
</html:form> 
	

