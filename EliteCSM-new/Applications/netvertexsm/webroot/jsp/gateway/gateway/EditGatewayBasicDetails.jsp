<%@page import="com.elitecore.corenetvertex.constants.CommunicationProtocol"%>
<%@ page import="com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData"%>
<%@ page import="com.elitecore.netvertexsm.web.gateway.gateway.form.GatewayForm"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.corenetvertex.constants.PolicyEnforcementMethod"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData" %>

<%
	IGatewayData gatewayData = (IGatewayData)request.getSession().getAttribute("gatewayData");
	GatewayForm gatewayForm = (GatewayForm)request.getAttribute("gatewayForm");
	Set<DiameterProfileData> dimPrSet = gatewayForm.getDiameterProfileSet(); 
%>

<script type="text/javascript">
$(document).ready(function() {	
	verifyName();
	$("#gatewayName").focus();
	$('#description').attr('maxlength','250');
	$('#radius').hide();
	$('#diameter').hide();
});

function validate() {
	if(!isValidName){
		alert('Gateway Name is Invalid.');
		document.forms[0].gatewayName.focus();
		return false;	
	}else{
		return true;
	}
}

function verifyFormat (){
	var searchName = document.getElementById("gatewayName").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.GATEWAY%>',searchName:searchName,mode:'update',id:'<%=gatewayForm.getGatewayId()%>'},'verifyNameDiv');
}

function verifyName() {
	var searchName = document.getElementById("gatewayName").value;
	searchName = $.trim(searchName);
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.GATEWAY%>',searchName:searchName,mode:'update',id:'<%=gatewayForm.getGatewayId()%>'},'verifyNameDiv');
}	


</script>


<html:form action="/editGatewayBasicDetails" onsubmit="return validate()">
<html:hidden property="gatewayId" styleId="gatewayId" name="gatewayForm" />
<bean:define id="gatewayBean" name="gatewayData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData" />
	
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
			  	   <tr> 
						<td align="left" class="tblheader-bold" valign="top" width="100%" colspan="2"> 
							<bean:message bundle="gatewayResources" key="gateway.update" />
						</td> 
					 </tr>
				<tr> 
				    <td width="10" class="small-gap">&nbsp;</td>
				 </tr>		
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="35%">
					<bean:message bundle="gatewayResources" key="gateway.creategateway"/></td>
					  <logic:equal value="<%=CommunicationProtocol.RADIUS.getName()%>" name="gatewayForm" property="commProtocolId">
					    <sm:nvNameField maxLength="40" size="30" id="gatewayName" name="gatewayName" value="${gatewayForm.gatewayName}" />
					  </logic:equal>
					  <logic:equal value="<%=CommunicationProtocol.DIAMETER.getName()%>" name="gatewayForm" property="commProtocolId">
					  <td align="left" class="labeltext" valign="top" width="65%">
						  <html:hidden property="gatewayName" name="gatewayForm" styleId="gatewayName" />
						  <html:text property="gatewayName" name="gatewayForm" size="30" maxlength="40" styleId="gatewayName" disabled="true" tabindex="1"/>
					  </logic:equal>
					  </td>
				  </tr>				 			 
				<tr> 
				    <td width="10" class="small-gap">&nbsp;</td>
				 </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="35%">
					<bean:message bundle="gatewayResources" key="gateway.description" /></td> 					
					<td align="left" class="labeltext" valign="top" width="65%"> 
					    <html:textarea property="description" styleId="description" cols="40" rows="2" tabindex="2" /> 
					</td> 
				  </tr>
				<tr> 
				    <td width="10" class="small-gap">&nbsp;</td>
				 </tr>
				
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="35%">
					<bean:message bundle="gatewayResources" key="gateway.commprotocol"/></td> 
					<td align="left" class="labeltext" valign="top" width="65%">
						 <html:hidden property="commProtocolId" styleId="commProtocolId" ></html:hidden>
                         <html:text  name="gatewayForm" property="commProtocolId" maxlength="40" size="30" disabled="true" tabindex="3"></html:text>								
					</td> 
				  </tr>
				<tr> 
				    <td width="10" class="small-gap">&nbsp;</td>
				 </tr>
	
		<tr>
			<td width="10" class="small-gap">&nbsp;</td>
		</tr>
				<tr> 
					<td align="left" class="labeltext" valign="top"><bean:message bundle="gatewayResources" key="gateway.location"/></td> 
					<td align="left" class="labeltext" valign="top" >
					<html:select name="gatewayForm" styleId="locationId" property="locationId" size="1"  style="width: 210px" tabindex="4">
							<html:option value="0" bundle="gatewayResources" key="gateway.select" />
							<logic:iterate id="locations" name="gatewayForm" property="locationList" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayLocationData">
								<html:option value="<%=Long.toString(locations.getLocationId())%>"><bean:write name="locations" property="locationName"/> </html:option>
							</logic:iterate>
						</html:select>										
					</td> 
				  </tr>				  												  
				<tr> 
				    <td width="10" class="small-gap">&nbsp;</td>
				 </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top">
					<bean:message bundle="gatewayResources" key="gateway.area" /></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text property="areaName" maxlength="40" size="30" styleId="areaName" tabindex="5"/>
					</td>
				  </tr>				  
				<tr> 
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
				</tr>
        		<tr> 
	        		<td  valign="middle" >&nbsp;
            		<td  valign="middle" align="left"> 
		        		<input type="submit" align="left" value="  Update  " tabindex="6" class="light-btn" />
		        		<input type="button" align="left" value="  Cancel  " tabindex="7" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchGateway.do?/>'"/>
		        	</td> 
   		 		</tr>
   		 		<tr> 
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
				</tr>	 				 
						 				  					 
		</table>
		
</html:form>