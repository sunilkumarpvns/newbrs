<%@page import="com.elitecore.netvertexsm.web.gateway.gateway.form.RadiusGatewayForm"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List" %> 
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData"%>
<%@ page import="com.elitecore.corenetvertex.constants.PolicyEnforcementMethod"%>
<%
RadiusGatewayForm radiusGatewayForm = (RadiusGatewayForm)request.getAttribute("radiusGatewayForm");
%>
<script type="text/javascript">

$(document).ready(function(){
	$("#sharedSecret").focus();
	validateConnectionURL();
});

function validate() {
	
	if(isNull(document.forms[0].sharedSecret.value)){
		alert('Shared secret must be specefied.');
		document.forms[0].sharedSecret.focus();
		return false;
	}else if(isNull(document.forms[0].connectionUrl.value)){
		alert('Connection URL must be specified.');
		document.forms[0].connectionUrl.focus();
		return false;
	}else if(!isValidConnectionURL){
		alert('Enter Valid Connection URL');
		document.forms[0].connectionUrl.focus();
		return false;
	}else if(isNull(document.forms[0].minLocalPort.value)){
		alert('Minimum Local Port must be specefied.');
		document.forms[0].minLocalPort.focus();
		return false;
	}else if(isEcNaN(document.forms[0].minLocalPort.value)){
		alert('Minimum Local Port must be numeric.');
		document.forms[0].minLocalPort.focus();
		return false;
	}else{
		return true;
	}	
}

function validateConnectionURL() {	
	
	connectionURL = $.trim(document.forms[0].connectionUrl.value);
	if(connectionURL.length!=0){
		isValidConnectionURL = verifyConnectionURL({instanceType:'<%=InstanceTypeConstants.RADIUS_GATEWAY%>',searchName:connectionURL,mode:'update',id:'<%=radiusGatewayForm.getGatewayId()%>'},'verifyConnectionURLDiv');
	}
}
	
</script>
<html:form action="/editRadiusGateway" onsubmit="return validate()">
<html:hidden property="gatewayId" styleId="gatewayId" name="radiusGatewayForm" />
<html:hidden property="action" styleId="action" name="radiusGatewayForm"  value="save"/>
<bean:define id="gatewayBean" name="gatewayData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData" />
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		  	<tr> 
				<td align="left" class="tblheader-bold" valign="top" width="100%" colspan="2"> 
					<bean:message bundle="gatewayResources" key="gateway.update" />
				</td> 
			 </tr>			 
			 
			    <tr>  
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="gatewayResources" key="gateway.radius.sharedsecret"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.sharedsecret"/>','<bean:message bundle="gatewayResources" key="gateway.radius.sharedsecret" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text name="radiusGatewayForm" property="sharedSecret" styleId="sharedSecret" maxlength="40" size="30" styleClass="name" tabindex="2"/><font color="#FF0000"> *</font></td>
				</tr>
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="35%">
						<label >
						<bean:message bundle="gatewayResources" key="gateway.ipaddress"/></label>
					</td> 
					<td align="left" class="labeltext" valign="top" width="65%"> 
						<html:text name="radiusGatewayForm" styleId="connectionUrl" onchange="" property="connectionUrl" maxlength="45" size="30"  tabindex="2" onblur="validateConnectionURL();" /><font color="#FF0000"> *</font>
						<div id="verifyConnectionURLDiv" class="labeltext"></div>
					</td> 
				  </tr>					
				  <tr>
				  	<td align="left" class="labeltext" valign="top" >
				  		<bean:message bundle="gatewayResources" key="gateway.radius.minlocalport"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.minlocalport"/>','<bean:message bundle="gatewayResources" key="gateway.radius.minlocalport" />')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text name="radiusGatewayForm" property="minLocalPort" styleId="minLocalPort" maxlength="8" size="8"  styleClass="name" tabindex="3"/><font color="#FF0000"> *</font>
					</td>
				  </tr>				 	
		 	<tr> 
			<td align="left" class="labeltext" valign="top">
			<bean:message bundle="gatewayResources" key="gateway.profile"/></td> 
			<td align="left" class="labeltext" valign="top" > 						
					<html:select name="radiusGatewayForm"  styleId="gatewayProfileId" property="gatewayProfileId" size="1"  style="width: 210px" tabindex="4" >								
						<logic:iterate id="profiles"  name="radiusGatewayForm" property="gatewayProfileList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData">
							<html:option value="<%=Long.toString(profiles.getProfileId())%>"><bean:write name="profiles" property="profileName"/> </html:option>
						</logic:iterate>
					</html:select>																														
				</td> 
			 </tr> 
			<logic:notEmpty name="gatewayBean" property="policyEnforcementMethodName"> 
			<tr> 
				<td align="left" class="labeltext" valign="top">
				<bean:message bundle="gatewayResources" key="gateway.policy.enforcement.method"/></td> 
				<td align="left" class="labeltext" valign="top">						
						<% List<PolicyEnforcementMethod> methodList=null; %>
						<html:select name="gatewayBean" styleId="policyEnforcementMethodName" property="policyEnforcementMethodName" size="1"  style="width: 210px" tabindex="5">																											
							<% methodList=PolicyEnforcementMethod.getRadiusMethods();                             		
								if(methodList!=null && methodList.size()>0){							
									for(PolicyEnforcementMethod method:methodList){
							%>								
										<html:option value="<%=method.getVal()%>"><%=method.getVal()%></html:option>
							<%		}
								}
							%>								
						</html:select>					
				</td> 
			 </tr>	
			 </logic:notEmpty>	  				
        		<tr> 
	        		<td valign="middle" >&nbsp;</td>
            		<td  valign="middle" align="left"> 
		        		<input type="submit" align="left"  value="  Update  " tabindex="6" class="light-btn" />
		        		<input type="button" align="left" value="  Cancel  " tabindex="7" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchGateway.do?/>'"/>
		        	</td> 
   		 		</tr>
   		 		<tr> 
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  		</tr>
		</table>
		<html:hidden property="gatewayName" styleId="gatewayName" name="gatewayBean" />
</html:form>    	
	