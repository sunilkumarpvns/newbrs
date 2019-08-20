<%@ page import="java.util.List" %> 
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData"%>
<%@ page import="com.elitecore.corenetvertex.constants.PolicyEnforcementMethod"%>
<%@ page import="com.elitecore.netvertexsm.web.gateway.gateway.form.DiameterGatewayForm" %>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%
	IGatewayData gatewayData = (IGatewayData)request.getAttribute("gatewayData");
	String connectionURL = (String) gatewayData.getConnectionUrl();
	DiameterGatewayForm diameterGatewayForm = (DiameterGatewayForm)request.getAttribute("diameterGatewayForm");
	List<DiameterProfileData> diameterProfileDataList = diameterGatewayForm.getDiameterProfileDataList();
	int supportedStd=0;
	String name ="";
%>
<script type="text/javascript">

var connectionURL = null; 
var isValidName = false;
var isValidConnectionURL = false;
$(document).ready(function(){
	verifyName();
	validateConnectionURL();
	$("#connectionUrl").focus();	 	
		<% for(DiameterProfileData profileData:diameterProfileDataList ){
				if(diameterGatewayForm.getGatewayProfileId() == profileData.getProfileId()){				
					supportedStd = profileData.getSupportedStandard(); %>								
			<%  }
		 } %>		 
});

function setAsterisk(){
	connectionURL = document.forms[0].connectionUrl.value;
	if(jQuery.trim(connectionURL).length==0){
		document.getElementById("star").innerHTML="*";
	}else{
		document.getElementById("star").innerHTML="";
	}
}

function validate() {
		connectionURL = document.forms[0].connectionUrl.value;
		validateConnectionURL();
		var hostIdentityFlag = isNull(document.forms[0].hostIdentity.value);
		var requestTimeout 	= document.forms[0].requestTimeout.value;				
		var timeoutPattern	= /^(0|[1-9][\d]{3,4})$/;
		var timeoutRegexObj = new RegExp(timeoutPattern);
		var requestTimeoutFlag = timeoutRegexObj.test(requestTimeout);

		var retransmissionCount = document.forms[0].retransmissionCount.value;
		var retraCountPattern   = /^[0-3]$/;
		var retranCountRegexObj = new RegExp(retraCountPattern);		
		var retransmissionCountFlag = retranCountRegexObj.test(retransmissionCount); 
		
		if(connectionURL.indexOf("/")!=-1 && hostIdentityFlag!=true){
			alert("Host Identity not allowed with subnetmask in Connection URL");
			document.forms[0].hostIdentity.focus();
			return false;
			
		} else if(connectionURL.indexOf("-")!=-1  && hostIdentityFlag!=true){
			alert("Host Identity not allowed with IP-Range in Connection URL");
			document.forms[0].hostIdentity.focus();
			return false;
			
		} else if((connectionURL=='null' || connectionURL==null || jQuery.trim(connectionURL).length==0) &&  hostIdentityFlag==true){
			
			if(hostIdentityFlag){
				alert('Host Identity must be specified');
				document.forms[0].hostIdentity.focus();
				return false;
				
			} else  if(!isValidName){
				alert('Host Identity is Invalid');
				document.forms[0].hostIdentity.focus();
				return false;
					
			}
			
		}else if(!isValidConnectionURL){
			alert('Enter Valid Connection URL');
			document.forms[0].connectionUrl.focus();
			return false;
		}else if(!isValidName){
			alert('Host Identity is Invalid');
			document.forms[0].hostIdentity.focus();
			return false;
				
		}else if(isNull(document.forms[0].realm.value)){			
			alert('Realm must be specified');
			document.forms[0].realm.focus();
			return false;
			
		} 
		
		if(jQuery.trim(requestTimeout).length==0){			
			document.forms[0].requestTimeout.value=3000;
		}else{					
			if(requestTimeoutFlag==false){
				alert('Invalid Request Timeout \nPossible values are \'0 or 1000 to 10000\' ');
				document.forms[0].requestTimeout.focus();
				return false;				
			}else if(!(requestTimeout==0 || (requestTimeout>=1000 && requestTimeout<=10000))){
				alert('Invalid Request Timeout \nPossible values are \'0 or 1000 to 10000\' ');
				document.forms[0].requestTimeout.focus();
				return false;		
			} 
		}
		
		if(jQuery.trim(retransmissionCount).length==0){			
			document.forms[0].retransmissionCount.value=0;
		}else{
			if(!retransmissionCountFlag){
					alert("Invalid Retransmission Count \nPossible values are: 0 to 3");
					document.forms[0].retransmissionCount.focus();
					return false;
			}else{
					document.forms[0].submit();
			}
		}
}

function verifyFormat (){
	var searchName = document.getElementById("hostIdentity").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.DIAMETER_GATEWAY%>',searchName:searchName,mode:'update',id:'<%=diameterGatewayForm.getGatewayId()%>'},'verifyNameDiv');
}
function verifyName() {	
	var searchHostIdentity = document.getElementById("hostIdentity").value;
	searchHostIdentity = $.trim(searchHostIdentity);
	connectionURL = document.forms[0].connectionUrl.value;
	if(connectionURL.indexOf("/")!=-1 || connectionURL.indexOf("-")!=-1 || jQuery.trim(connectionURL).length>0){			
		isValidName = true;
	} 
	if($.trim(searchHostIdentity).length!=0)
	{
		isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.DIAMETER_GATEWAY%>',searchName:searchHostIdentity,mode:'update',id:'<%=diameterGatewayForm.getGatewayId()%>'},'verifyNameDiv');
		if(isValidName == true){
			isValidConnectionURL = true;
			document.getElementById("verifyConnectionURLDiv").innerHTML = "";
	}
}
}
function validateConnectionURL() {	
	var searchHostIdentity = $.trim(document.getElementById("hostIdentity").value);
	var connectionURL = $.trim(document.forms[0].connectionUrl.value);
	if(connectionURL.length!=0 && $.trim(searchHostIdentity).length == 0){
		isValidConnectionURL = verifyConnectionURL({instanceType:'<%=InstanceTypeConstants.DIAMETER_GATEWAY_CONNECTION_URL%>',searchName:connectionURL,mode:'update',id:'<%=diameterGatewayForm.getGatewayId()%>'},'verifyConnectionURLDiv');
	}else{
		isValidConnectionURL = true;
		document.getElementById("verifyConnectionURLDiv").innerHTML = "";
	}
}

function getPolicyEnfMethods(profileId){	
	clearPolicyEnfMethodsSelectTag();
	var supportedStd = null;
	
	<%
		int supportedStandard = 0;	
		for(DiameterProfileData profileData:diameterProfileDataList  ){						
			%>				
			if(profileId==<%=profileData.getProfileId()%>)
			{				
				<% supportedStandard = profileData.getSupportedStandard(); %>				
				supportedStd = <%=profileData.getSupportedStandard()%>;				
			}
			<%
		}%>		
		<% 
			List<PolicyEnforcementMethod> supportedDiamSceMethodList = PolicyEnforcementMethod.getDiameterSCEMethods();
			List<PolicyEnforcementMethod> supportedDiamMethodList = PolicyEnforcementMethod.getDiameterMethods();
			List<PolicyEnforcementMethod> supportedMethodList = null;
		 %>
		if(supportedStd!=null && parseInt(supportedStd)==4)
		{				 
			<%
			if(supportedDiamSceMethodList!=null && supportedDiamSceMethodList.size()>0){
				for(PolicyEnforcementMethod method:supportedDiamSceMethodList){%>								
					$("#policyEnforcementMethodName").append("<option value='<%=method.getVal()%>'><%=method.getVal()%></option>");
					<%				
				}
			}					
			%>				
		}else 
		{				
			<%
			if(supportedDiamMethodList!=null && supportedDiamMethodList.size()>0){
				for(PolicyEnforcementMethod method:supportedDiamMethodList){%>								
					$("#policyEnforcementMethodName").append("<option value='<%=method.getVal()%>'><%=method.getVal()%></option>");
					<%				
				}
			}					
			%>				
		} 

}
function clearPolicyEnfMethodsSelectTag(){ 
	document.forms[0].policyEnforcementMethodName.options.length=0;	 
}

</script>
<html:form action="/editDiameterGateway" onsubmit="return validate();"> 
<html:hidden property="gatewayId" styleId="gatewayId" name="diameterGatewayForm" />
<html:hidden property="action" styleId="action" name="diameterGatewayForm"  value="save"/>
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
						<label >
						<bean:message bundle="gatewayResources" key="gateway.ipaddress"/></label>
					</td> 
					<td align="left" class="labeltext" valign="top" width="65%"> 
						<html:text name="diameterGatewayForm" styleId="connectionUrl" onblur="setAsterisk();" onchange="" property="connectionUrl" maxlength="45" size="30"  tabindex="1" onkeyup="validateConnectionURL();" /><font color="#FF0000" id="star"></font>	
						<div id="verifyConnectionURLDiv" class="labeltext"></div>
					</td> 
				  </tr>					  				  
				 <tr>
					<td align="left" class="labeltext" valign="top" width="35%"> 
						<bean:message bundle="gatewayResources" key="gateway.diameter.hostidentity"/></td> 
						<sm:nvNameField maxLength="40" size="30" id="hostIdentity" name="hostIdentity" value="${diameterGatewayForm.hostIdentity}"/>
				</tr>
				<tr> 
				    <td width="10" class="small-gap">&nbsp;</td>
				 </tr>				
				<tr>
					<td align="left" class="labeltext" valign="top"  width="35%">
						<bean:message bundle="gatewayResources" key="gateway.diameter.realm"/></td> 
					<td align="left" class="labeltext" valign="top"  width="65%"> 
						<html:text name="diameterGatewayForm" property="realm" maxlength="40" size="30"  tabindex="3"/><font color="#FF0000"> *</font></td>
				</tr>
				<tr> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.localaddress" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.localaddress"/>','<bean:message bundle="gatewayResources" key="gateway.localaddress" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text property="localAddress" maxlength="64" size="30" styleId="localAddress" tabindex="4"/>
					</td>
				  </tr>	
				<tr> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.request.timeout" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.request.timeout"/>','<bean:message bundle="gatewayResources" key="gateway.request.timeout" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
					<%
						if(diameterGatewayForm.getRequestTimeout()==null){
							diameterGatewayForm.setRequestTimeout(0L);
						}
						if(diameterGatewayForm.getRetransmissionCount()==null){
							diameterGatewayForm.setRetransmissionCount(0);
						}
					%>
						<html:text name="diameterGatewayForm" property="requestTimeout" maxlength="5" size="6" styleId="requestTimeout" tabindex="4"/>
					</td>
				  </tr>	
				<tr> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.retransmission.count" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.retransmission.count"/>','<bean:message bundle="gatewayResources" key="gateway.retransmission.count" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text name="diameterGatewayForm"  styleId="retransmissionCount" property="retransmissionCount" size="6"  maxlength="1" tabindex="5" />
					</td>
				</tr>	
				  
				<tr> 
				    <td width="10" class="small-gap">&nbsp;</td>
				 </tr>
			  	<tr> 
					<td align="left" class="labeltext" valign="top">
					<bean:message bundle="gatewayResources" key="gateway.profile"/></td> 
					<td align="left" class="labeltext" valign="top" > 						
							<html:select name="diameterGatewayForm"  styleId="profileId" property="gatewayProfileId" size="1"  style="width: 210px" tabindex="5" onchange="getPolicyEnfMethods(this.value);" >								
								<logic:iterate id="profiles"  name="diameterGatewayForm" property="gatewayProfileList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData">
									<html:option value="<%=Long.toString(profiles.getProfileId())%>"><bean:write name="profiles" property="profileName"/> </html:option>
								</logic:iterate>
							</html:select>																												
					</td> 
				  </tr> 
				  <!-- Alternate host identity -->
				   <tr> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.alternatehost" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.alternatehost"/>','<bean:message bundle="gatewayResources" key="gateway.alternatehost" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						 <html:select name="diameterGatewayForm" styleId="alternateHost" property="alternateHostId" size="1"  style="width: 210px" tabindex="6">
							<html:option value="0" bundle="gatewayResources" key="gateway.select" />
							<logic:iterate id="alternateHost" name="diameterGatewayForm" property="alternateHostList" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData">
								<html:option value="<%=Long.toString(alternateHost.getGatewayId())%>"><bean:write name="alternateHost" property="gatewayName"/> </html:option>
							</logic:iterate>		
						</html:select>							
					</td>
				  </tr>	  

				 <logic:notEmpty name="gatewayBean" property="policyEnforcementMethodName">
				  <tr> 
					<td align="left" class="labeltext" valign="top">
					<bean:message bundle="gatewayResources" key="gateway.policy.enforcement.method"/></td> 
					<td align="left" class="labeltext" valign="top">						
						<html:select name="gatewayBean" styleId="policyEnforcementMethodName" property="policyEnforcementMethodName" size="1"   style="width: 210px" tabindex="7">
							<% List<PolicyEnforcementMethod> methodList= null;
								if(supportedStd==4){ 
									methodList= PolicyEnforcementMethod.getDiameterSCEMethods();
								}
								else{ 
									methodList= PolicyEnforcementMethod.getDiameterMethods(); 
								}								                             	
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
				<tr> 
				    <td width="10" class="small-gap">&nbsp;</td>
				 </tr>
				 </logic:notEmpty>				
        		<tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;
            		<td class="btns-td" valign="middle" align="left"> 
		        		<input type="submit" align="left" value="  Update  " tabindex="7" class="light-btn" />
		        		<input type="button" align="left" value="  Cancel  " tabindex="8" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchGateway.do?/>'"/></td> 
   		 		</tr>
   		 		 				 
						 				  					 
		</table>
<html:hidden property="gatewayName" styleId="gatewayName" name="gatewayBean" />		
</html:form> 
<script>
setInit();
</script>   	
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>