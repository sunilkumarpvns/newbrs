<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.corenetvertex.constants.PolicyEnforcementMethod"%>
<%@ page import="com.elitecore.corenetvertex.constants.GatewayTypeConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.web.gateway.gateway.form.DiameterGatewayForm" %>

<%
	DiameterGatewayForm diameterGatewayForm = (DiameterGatewayForm) request.getAttribute("diameterGatewayForm");
	String actionToDo = (String) request.getAttribute("action");
	if(!(actionToDo!=null && actionToDo.equalsIgnoreCase("duplicate"))){
		actionToDo = " Create ";
	}
%>
<style> 
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
</style> 

<script>	
	var isValidName = false;
	var connectionURL = null;
	var isValidConnectionURL = false;
	$(document).ready(function(){
		setTitle('<bean:message bundle="gatewayResources" key="gateway.header" />');
		$("#connectionUrl").focus();		
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
		verifyName();
		setAsterisk();				
		validateConnectionURL();
		connectionURL = document.forms[0].connectionUrl.value;
		var hostIdentityFlag = isNull(document.forms[0].hostIdentity.value);
		
		var requestTimeout = document.forms[0].requestTimeout.value;				
		var timeoutPattern=/^(0|[1-9][\d]{3,4})$/;
		var timeoutRegexObj=new RegExp(timeoutPattern);
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
			
		} else if(jQuery.trim(connectionURL).length==0 &&  hostIdentityFlag==true){
			
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
		} else if(isNull(document.forms[0].realm.value)){			
			alert('Realm must be specified');
			document.forms[0].realm.focus();
			return false;
			
		} else  if(!isValidName){
			alert('Host Identity is Invalid');
			document.forms[0].hostIdentity.focus();
			return false;
		
		} 
		
		if(jQuery.trim(requestTimeout).length==0){			
				document.forms[0].requestTimeout.value=3000;
		}else{					
			if(requestTimeoutFlag==false){
				alert('Invalid Request Timeout \nPossible values are \'0 or 1000 to 10000\' ');
				document.forms[0].requestTimeout.focus();
				return;				
			}else if(!(requestTimeout==0 || (requestTimeout>=1000 && requestTimeout<=10000))){
				alert('Invalid Request Timeout \nPossible values are \'0 or 1000 to 10000\' ');
				document.forms[0].requestTimeout.focus();
				return;		
			} 
		}
		
		if(jQuery.trim(retransmissionCount).length==0){
			document.forms[0].retransmissionCount.value = 0;
		}else{
			if(!retransmissionCountFlag){
				alert("Invalid Retransmission Count\nPossible values are: 0 to 3");
				document.forms[0].retransmissionCount.focus();
				return false;
			}else{
				document.forms[0].submit();
			}
		}
	}
	function verifyFormat (){
		setAsterisk();
		var searchName = document.getElementById("hostIdentity").value;
		callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.DIAMETER_GATEWAY%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
	}
	
	function verifyName() {	
		
		connectionURL = document.forms[0].connectionUrl.value;
		var searchHostIdentity = document.getElementById("hostIdentity").value;
		searchHostIdentity = $.trim(searchHostIdentity);
		
		if(connectionURL.indexOf("/")!=-1 || connectionURL.indexOf("-")!=-1 || jQuery.trim(connectionURL).length>0){			
			isValidName = true;
		} 
		if($.trim(searchHostIdentity).length!=0)
		{
			isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.DIAMETER_GATEWAY%>',searchName:searchHostIdentity,mode:'create',id:''},'verifyNameDiv');
			if(isValidName == true){
				isValidConnectionURL = true;
				document.getElementById("verifyConnectionURLDiv").innerHTML = "";
		}
	}
	
	}
	
	function goBack(){
		history.back();
	}
	function validateConnectionURL() {	
		var searchHostIdentity = $.trim(document.getElementById("hostIdentity").value);
		var connectionURL = $.trim(document.forms[0].connectionUrl.value);
		if(connectionURL.length!=0 && $.trim(searchHostIdentity).length == 0){
			isValidConnectionURL = verifyConnectionURL({instanceType:'<%=InstanceTypeConstants.DIAMETER_GATEWAY_CONNECTION_URL%>',searchName:connectionURL,mode:'create',id:''},'verifyConnectionURLDiv');
		}else{
			isValidConnectionURL = true;
			document.getElementById("verifyConnectionURLDiv").innerHTML = "";
		}
	}
</script>
 
<html:form action="/createDiameterGateway">
 <html:hidden property="action" styleId="action" name="diameterGatewayForm"  value="save"/>
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			<bean:message bundle="gatewayResources" key="gateway.diameter"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" > 
				  <tr> 					 					 
				  </tr>
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="30%">
						<label >
						<bean:message bundle="gatewayResources" key="gateway.ipaddress"/></label>
					</td> 
					<td align="left" class="labeltext" valign="top" width="70%"> 
						<html:text styleId="connectionUrl" onchange="" property="connectionUrl" maxlength="45" size="30"  tabindex="1" onblur="verifyName();" />		
						<div id="verifyConnectionURLDiv" class="labeltext"></div>				
					</td> 
				  </tr>				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="gatewayResources" key="gateway.diameter.hostidentity"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text name="diameterGatewayForm" property="hostIdentity" styleId="hostIdentity"  onblur="verifyName();" maxlength="40" size="30"   styleClass="name" onfocus="setAsterisk();" tabindex="1" onkeyup="validateConnectionURL();" />
						<font color="#FF0000" id="star"></font>
						<div id="verifyNameDiv" class="labeltext"></div>
					</td> 
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="gatewayResources" key="gateway.diameter.realm"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text name="diameterGatewayForm" property="realm" styleId="realm" maxlength="40" size="30"   styleClass="name" tabindex="2"/><font color="#FF0000"> *</font>
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.localaddress" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.localaddress"/>','<bean:message bundle="gatewayResources" key="gateway.localaddress" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text property="localAddress" maxlength="64" size="30" styleId="localAddress" tabindex="3"/>
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="gatewayResources" key="gateway.request.timeout" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.request.timeout"/>','<bean:message bundle="gatewayResources" key="gateway.request.timeout" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" >
						<%
							Long timeoutVal = diameterGatewayForm.getRequestTimeout();
							if(timeoutVal!=null && timeoutVal!=3000){
						%>
							<html:text name="diameterGatewayForm" property="requestTimeout"  maxlength="5" size="6" styleId="requestTimeout" tabindex="3"/>
						<% }else{ %>
							<html:text name="diameterGatewayForm" property="requestTimeout"  maxlength="5" size="6" styleId="requestTimeout" value="3000" tabindex="3"/>
						<% } %>
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.retransmission.count" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.diameterattribute.retransmission.count"/>','<bean:message bundle="gatewayResources" key="gateway.retransmission.count" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<%
							Integer retranCount =  diameterGatewayForm.getRetransmissionCount();
							if(retranCount!=null && retranCount!=0){
						%>
							<html:text name="diameterGatewayForm" styleId="retransmissionCount" property="retransmissionCount" size="6" maxlength="1" tabindex="4" />
						<%}else{ %>
							<html:text name="diameterGatewayForm" styleId="retransmissionCount" property="retransmissionCount" size="6" maxlength="1" tabindex="4" value="0" />						
						<%}%>										
					</td>
				  </tr>	
				 <!-- alternate host --> 
				 <tr> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.alternatehost" />
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.alternatehost"/>','<bean:message bundle="gatewayResources" key="gateway.alternatehost" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						 <html:select name="diameterGatewayForm" styleId="alternateHostId" property="alternateHostId" size="1"  style="width: 210px" tabindex="4">
							<html:option value="0" bundle="gatewayResources" key="gateway.select" />
							<logic:iterate id="alternateHost" name="diameterGatewayForm" property="alternateHostList" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData">
								<html:option value="<%=Long.toString(alternateHost.getGatewayId())%>"><%=alternateHost.getGatewayName()%></html:option>
							</logic:iterate>		
						</html:select>							
					</td>
				  </tr>	  
				  
				  <bean:define id="gatewayProfileBean" name="gatewayProfileData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData" />
				  <%	List<PolicyEnforcementMethod> methodList=null; %>				  
				  <logic:equal value="4"  name="gatewayProfileBean" property="diameterProfileData.supportedStandard" >       	           	       				
				  <% 							                    
						methodList=PolicyEnforcementMethod.getDiameterSCEMethods();//getEnforcementMethods(GatewayTypeConstant.CISCOSCE);
				  %>
				  </logic:equal>
				  <logic:notEqual value="4"  name="gatewayProfileBean" property="diameterProfileData.supportedStandard" >       	           	       				
				  <% 							                    
						methodList=PolicyEnforcementMethod.getDiameterMethods();//getEnforcementMethods(GatewayTypeConstant.DIAMETER);
				  %>
				  </logic:notEqual>
				  <%
						if(methodList!=null && methodList.size()>0){
				  %>       
				  <tr id="policyEnfMethod" > 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.policy.enforcement.method"/></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="diameterGatewayForm" styleId="policyEnforcementMethodName" property="policyEnforcementMethodName" size="1"  style="width: 210px" tabindex="4">                     		                          							
							<% 																																																					
								for(PolicyEnforcementMethod method:methodList){
									if(method==PolicyEnforcementMethod.STANDARD){
							%>								
								<option value="<%=method.getVal()%>" selected="selected"><%=method.getVal()%></option>
							<%	
							}else{
							%>	
								<option value="<%=method.getVal()%>"><%=method.getVal()%></option>
							<%}}	%>															
						</html:select><font color="#FF0000"> *</font>											
					</td>
				  </tr>				
				  <%}%>  					  			  			  
			   </table>  
			</td> 
		  </tr>		  	 
		  <tr> 
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
          <tr> 
	        <td class="btns-td" valign="middle" colspan="2" >&nbsp;</td>	        
            <td class="btns-td" valign="middle"  >             
            	<input type="button" value=" Previous"  onclick="goBack();" class="light-btn" tabindex="5" />
		        <input type="button" value=" <%=actionToDo%> " onclick="validate();"  class="light-btn" tabindex="5" /> 
                <input type="button" onclick="javascript:location.href='<%=basePath%>/initSearchGateway.do?/>'" value=" Cancel " class="light-btn" tabindex="6" /> 
	        </td> 
   		  </tr> 
		</table> 
	  </td> 
	</tr> 
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
</html:form> 

