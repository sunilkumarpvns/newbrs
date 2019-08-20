<%@page import="com.elitecore.corenetvertex.constants.CommunicationProtocol"%>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.corenetvertex.constants.PolicyEnforcementMethod"%>
<%@ page import="java.util.List" %>

<script type="text/javascript">

$(document).ready(function() {	
	setTitle('<bean:message bundle="gatewayResources" key="gateway.header"/>');
	var id = document.getElementById("commProtocolId").value;		
	$('#description').attr('maxlength','250');
	$('#radius').hide();
	$('#diameter').hide();
	setProfileLabel(id);
	verifyName();
		
	$('#commProtocolId').change(function() {
		var id = $(this).val();		
		setProfileLabel(id);
	});
	$("#gatewayName").focus();
});
function setProfileLabel(id){
	 
	if(id=="<%=CommunicationProtocol.RADIUS.id%>") {
		$('#radius').show();
		$('#diameter').hide();
		$("#radiusGatewayProfileId").focus();
	}else if(id=="<%=CommunicationProtocol.DIAMETER.id%>") {
		$('#diameter').show();
		$('#radius').hide();
		$("#diameterGatewayProfileId").focus();
	}else{
		$('#diameter').hide();
		$('#radius').hide();
	}
}

function validate(){		
			
		if(!isValidName){
			alert('Gateway Name is Invalid.');
			document.forms[0].gatewayName.focus();
			return false;	
		}else if(document.forms[0].commProtocolId.value == "0"){
			alert('Atleast one Communication Protocol must be selected.');
			document.forms[0].commProtocolId.focus();
			return false;
		}else if(document.forms[0].commProtocolId.value == "DTY001"){
			if(document.forms[0].radiusGatewayProfileId.value == "0") {
				alert("Atleast one Gateway Profile must be selected.");
				document.forms[0].radiusGatewayProfileId.focus();
				return false;
			}else{
				return true;
			}
		}else if(document.forms[0].commProtocolId.value == "DTY002"){
			if(document.forms[0].diameterGatewayProfileId.value == "0") {
				alert("Atleast one Gateway Profile must be selected.");
				document.forms[0].diameterGatewayProfileId.focus();
				return false;
			}else{
				return true;
			}
		}		
}
function verifyFormat (){
	var searchName = document.getElementById("gatewayName").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.GATEWAY%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("gatewayName").value;
	searchName = $.trim(searchName);
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.GATEWAY%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

</script>

 
<html:form action="/createGateway" onsubmit="return validate()">	
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			<bean:message bundle="gatewayResources" key="gateway.gateway"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3">
			
			   <table width="97%" id="c_tblCrossProductList" align="right" border="0" >
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<label >
						<bean:message bundle="gatewayResources" key="gateway.creategateway"/></label>
					</td> 
					<sm:nvNameField maxLength="40" size="30" id="gatewayName" name="gatewayName"/>
				  </tr> 			   
			   	 
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="gatewayResources" key="gateway.description" /></td> 					
					<td align="left" class="labeltext" valign="top" width="32%" > 
					    <html:textarea property="description" cols="40" rows="2" tabindex="2" styleId="description" /> 
					</td> 
				  </tr>						 		 

				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="gatewayResources" key="gateway.commprotocol"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%"> 
						<html:select name="gatewayForm" styleId="commProtocolId" property="commProtocolId" size="1"  style="width: 210px" tabindex="3" >							
							<logic:iterate id="commProtocols" collection="<%=CommunicationProtocol.values() %>"  type="com.elitecore.corenetvertex.constants.CommunicationProtocol">
								<html:option value="<%=commProtocols.id%>"><%=commProtocols.name %></html:option>
							</logic:iterate>
						</html:select><font color="#FF0000"> *</font>											
					</td>
				  </tr>				  	 			 
				  <tr id="radius"> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.radius.profile"/></td> 
					<td align="left" class="labeltext" valign="top" >
						<html:select name="gatewayForm" styleId="radiusGatewayProfileId" property="radiusGatewayProfileId" size="1"  style="width: 210px" tabindex="6">
							<html:option value="0" bundle="gatewayResources" key="gateway.select" />
							<logic:iterate id="profiles"  name="gatewayForm" property="radiusProfileList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData">
								<html:option value="<%=Long.toString(profiles.getProfileId())%>"><bean:write name="profiles" property="profileName"/> </html:option>
							</logic:iterate>
						</html:select><font color="#FF0000"> *</font>						
					</td> 
				  </tr>
				  <tr id="diameter"> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.diameter.profile"/></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="gatewayForm" styleId="diameterGatewayProfileId" property="diameterGatewayProfileId" size="1"  style="width: 210px" tabindex="7">
							<html:option value="0" bundle="gatewayResources" key="gateway.select" />
							<logic:iterate id="profiles"  name="gatewayForm" property="diameterProfileList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData">
								<html:option value="<%=Long.toString(profiles.getProfileId())%>"><bean:write name="profiles" property="profileName"/> </html:option>
							</logic:iterate>
						</html:select>	<font color="#FF0000"> *</font>													
					</td> 
				  </tr>				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.location"/></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="gatewayForm" styleId="locationId" property="locationId" size="1"  style="width: 210px" tabindex="8">
							<html:option value="0" bundle="gatewayResources" key="gateway.select" />
							<logic:iterate id="locations" name="gatewayForm" property="locationList" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayLocationData">
								<html:option value="<%=Long.toString(locations.getLocationId())%>"><bean:write name="locations" property="locationName"/> </html:option>
							</logic:iterate>
						</html:select>										
					</td> 
				  </tr>				  												  
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
					<bean:message bundle="gatewayResources" key="gateway.area" /></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text property="areaName" maxlength="40" size="30" value="" styleId="areaName" tabindex="9"/>
					</td>
				  </tr>				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  		  </tr> 
			   		<tr > 
			        <td valign="middle" >&nbsp;</td> 
		            <td  valign="middle" >		            	
				        <input type="submit" property="c_btnCreate"  value="   Next   " class="light-btn" tabindex="10"/> 
		                <input type="button" tabindex="11" align="left" value="  Cancel  " class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchGateway.do?/>'"/></td>
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
	  <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
</html:form> 

