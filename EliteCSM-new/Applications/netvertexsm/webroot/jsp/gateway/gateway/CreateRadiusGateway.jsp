<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.corenetvertex.constants.PolicyEnforcementMethod"%>
<%@ page import="com.elitecore.corenetvertex.constants.GatewayTypeConstant"%>
<%@ page import="java.util.List" %>

<%
	String actionToDo = (String) request.getAttribute("action");
	if(!(actionToDo!=null && actionToDo.equalsIgnoreCase("duplicate"))){
		actionToDo = " Create ";
	}
%>
<style> 
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
</style> 

<script>

$(document).ready(function(){
	setTitle('<bean:message bundle="gatewayResources" key="gateway.header" />');
	$("#sharedSecret").focus();
	validateConnectionURL();
});

function validate() {
	if(isNull(document.forms[0].sharedSecret.value)){
		alert('Shared secret must be specified.');
		document.forms[0].sharedSecret.focus();
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
	}else if(isEcNaN(document.forms[0].minLocalPort.value)){
		alert('Minimum Local Port must be numeric.');
		document.forms[0].minLocalPort.focus();
	}else{
		document.forms[0].submit();
	}
}

function validateConnectionURL() {	
	
	connectionURL = $.trim(document.forms[0].connectionUrl.value);
	if(connectionURL.length!=0){
		isValidConnectionURL = verifyConnectionURL({instanceType:'<%=InstanceTypeConstants.RADIUS_GATEWAY%>',searchName:connectionURL,mode:'create',id:''},'verifyConnectionURLDiv');
	}
}
function goBack(){
	history.back();
}
</script>
 
<html:form action="/createRadiusGateway">
<html:hidden property="action" styleId="action" name="radiusGatewayForm"  value="save"/>
<table cellpadding="0" cellspacing="0" border="0" width="100%"> 
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			<bean:message bundle="gatewayResources" key="gateway.radius" /></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" > 
				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="35%">
						<bean:message bundle="gatewayResources" key="gateway.radius.sharedsecret" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.sharedsecret"/>','<bean:message bundle="gatewayResources" key="gateway.radius.sharedsecret" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="65%" >
						<html:text name="radiusGatewayForm" property="sharedSecret" styleId="sharedSecret" maxlength="40" size="30"   styleClass="name" tabindex="1"/><font color="#FF0000"> *</font>
					</td>
				  </tr>
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="35%">
						<label >
						<bean:message bundle="gatewayResources" key="gateway.ipaddress"/></label>
					</td> 
					<td align="left" class="labeltext" valign="top" width="65%"> 
						<html:text name="radiusGatewayForm" styleId="connectionUrl" onchange="" onblur="validateConnectionURL();" property="connectionUrl" maxlength="45" size="30"  tabindex="2" /><font color="#FF0000"> *</font>
						<div id="verifyConnectionURLDiv" class="labeltext"></div>
					</td> 
				  </tr>					  
				  <tr>
				  	<td align="left" class="labeltext" valign="top" >
				  		<bean:message bundle="gatewayResources" key="gateway.radius.minlocalport"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.radius.minlocalport"/>','<bean:message bundle="gatewayResources" key="gateway.radius.minlocalport" />')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text name="radiusGatewayForm" property="minLocalPort" styleId="minLocalPort" maxlength="8" size="8"   styleClass="name" tabindex="3"/><font color="#FF0000"> *</font>
					</td>
				  </tr>
				 <%	
				 	List<PolicyEnforcementMethod> methodList=null; 							                    
					methodList=PolicyEnforcementMethod.getRadiusMethods();//getEnforcementMethods(GatewayTypeConstant.RADIUS);
					if(methodList!=null && methodList.size()>0){
				 %>  				  
				  <tr id="policyEnfMethod" > 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.policy.enforcement.method"/></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="radiusGatewayForm" styleId="policyEnforcementMethodName" property="policyEnforcementMethodName" size="1"  style="width: 210px" tabindex="3">                          		                          						
							<% 	for(PolicyEnforcementMethod method:methodList){ %>								
										<html:option value="<%=method.getVal()%>"><%=method.getVal()%></html:option>
							<%	} %>															
						</html:select><font color="#FF0000"> *</font>											
					</td>
				  </tr>
				  <% } %>				 	
				  <tr> 
						<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  		  </tr> 
          		  <tr > 
	        			<td  valign="middle" >&nbsp;</td> 
            			<td  valign="middle"  > 
            				<input type="button" value=" Previous"  onclick="goBack();" class="light-btn" tabindex="4" />
		        			<input type="button" onclick="validate();" value=" <%=actionToDo%> "  class="light-btn" tabindex="4" /> 
                			<input type="button" onclick="javascript:location.href='<%=basePath%>/initSearchGateway.do?/>'" value=" Cancel " class="light-btn"  tabindex="5"/> 
	        			</td> 
   		  		  </tr> 
   		  	      <tr> 
						<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
				  </tr>								  			  
			   </table>  
			</td> 
		  </tr>	 

		</table> 
	  </td> 
	</tr> 
  <%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
</html:form> 

