<%@page import="com.elitecore.corenetvertex.constants.CommunicationProtocol"%>
<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.web.gateway.profile.form.CreateProfileForm" %>
<%@ page import="com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.corenetvertex.constants.PCRFKeyValueConstants" %>
<%@ page import="com.elitecore.corenetvertex.constants.GatewayComponent"%>
<script language = "javascript">

$(document).ready(function(){
	setTitle('<bean:message bundle="gatewayResources" key="gatewy.profile.header"/>');
	$("#name").focus();
	$('#description').attr('maxlength','250');
});

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.PROFILE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.PROFILE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

var isValidName;
function validate(){
	if(isNull(document.forms[0].name.value)){
		alert('Gateway Profile Name must be specified.');
	}else if(!isValidName) {
		alert('Enter Valid Gateway Profile Name.');
		document.forms[0].name.focus();
		return;
	}else if(document.forms[0].vendor.value == "0"){
		alert('At least one Vendor must be selected.');
	}else if(document.forms[0].commProtocol.value == "0"){
		alert('At least one Communication Protocol must be selected.');
	}else{
		var type = document.getElementById("gatewayType").value;
		//alert(type);
		document.forms[0].action="initProfileDetail.do?gatewayType="+type;
		document.forms[0].submit();
	}
}
</script>
 <%
 	GatewayComponent[] gatewayComponents = GatewayComponent.values();
 %>
<html:form action="/initProfileDetail"> 
<input type="hidden" name="isInSync" value="CST02" id="isInSync"> 
<input type="hidden" name="action" value="next" id="action"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="gatewayResources" key="gateway.profile.newprofile"/>
			</td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%"  id="c_tblCrossProductList" align="right" border="0" > 
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile"/>','<bean:message bundle="gatewayResources" key="gateway.profile" />')"/>
						</td> 
						<sm:nvNameField maxLength="25" size="30" name="gatewayProfileName"/> 
				  </tr>
				  <tr>
				  	<td align="left" class="labeltext" valign="top" >
				  		<bean:message bundle="gatewayResources" key="gateway.gatewaytype"/>
				  		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.gatewaytype"/>','<bean:message bundle="gatewayResources" key="gateway.gatewaytype" />')"/>
				  	</td> 
					<td align="left" class="labeltext" valign="top"  >
						<html:select  styleId="gatewayType"  property="gatewayType" tabindex="2"  style="width: 206px">
							<%for(GatewayComponent component:gatewayComponents){%>													
								<html:option value="<%=component.value%>"><%=component.value%></html:option>
							<%}%>
						</html:select><font color="#FF0000"> *</font> 
					</td>
				  </tr>				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.vendor"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.vendor"/>','<bean:message bundle="gatewayResources" key="gateway.profile.vendor" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
				 		<html:select name="createProfileForm" styleId="vendor" property="vendorId" size="1" tabindex="3" style="width: 206;">
							<html:option value="0" bundle="gatewayResources" key="gateway.select" />
							<logic:iterate id="vendorlist"  name="createProfileForm" property="vendorList" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.VendorData">
								<html:option value="<%=Long.toString(vendorlist.getVendorId())%>"><bean:write name="vendorlist" property="vendorName"/></html:option>
							</logic:iterate>
						</html:select><font color="#FF0000"> *</font>	
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.commprotocol"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.commprotocol"/>','<bean:message bundle="gatewayResources" key="gateway.commprotocol" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" >
						<html:select name="createProfileForm" styleId="commProtocol" property="commProtocol" tabindex="4" size="1" style="width: 206;">
							<html:option value="0" bundle="gatewayResources" key="gateway.select" />
							<logic:iterate id="interfaces" collection="<%=CommunicationProtocol.values() %>" type="com.elitecore.corenetvertex.constants.CommunicationProtocol">
								<html:option value="<%=interfaces.id%>"><%=interfaces.name %></html:option>
							</logic:iterate>
						</html:select><font color="#FF0000"> *</font>							
					</td> 
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.description"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
					    <html:textarea property="description" cols="40" rows="2" styleId="description" tabindex="5" /> 
					</td> 
				  </tr>			
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.firmware"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.firmware"/>','<bean:message bundle="gatewayResources" key="gateway.profile.firmware" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
					    <html:text property="firmware" maxlength="40" size="30" value="" styleId="firmware" tabindex="6"/> 
					</td> 
				  </tr>								  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.maxthroughput"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.maxthroughput"/>','<bean:message bundle="gatewayResources" key="gateway.profile.maxthroughput" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="maxThroughtput" maxlength="12" size="30" value="" styleId="maxThroughtput" tabindex="7" />
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.bufferbw"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.bufferbw"/>','<bean:message bundle="gatewayResources" key="gateway.profile.bufferbw" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="bufferBandwidth" maxlength="12" size="30" value="" styleId="bufferBandwidth" tabindex="8"/>
					</td>
				  </tr>	
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.maxipcansession"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.maxipcansession"/>','<bean:message bundle="gatewayResources" key="gateway.profile.maxipcansession" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="maxIPCANSession" maxlength="10" size="30" value="" styleId="maxIPCANSession" tabindex="9"/>
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.usagereportingtime"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.usagereportingtime"/>','<bean:message bundle="gatewayResources" key="gateway.profile.usagereportingtime" />')"/>
					</td>
						<td align="left" class="labeltext" valign="top" width="32%" > 
					    	<html:select name="createProfileForm" styleId="usageReportingTime" property="usageReportingTime" style="width: 206;" tabindex="9">
						   		<html:option value='NON-CUMULATIVE'>Non-Cumulative</html:option>
						   		<html:option value='CUMULATIVE'>Cumulative</html:option> 
							</html:select>      
						</td>
				 	 </tr>	
				 <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="gatewayResources" key="gateway.profile.revalidationmode"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="gateway.profile.revalidationmode"/>','<bean:message bundle="gatewayResources" key="gateway.profile.revalidationmode" />')"/>
					</td>
						<td align="left" class="labeltext" valign="top" width="32%" > 
					    	<html:select name="createProfileForm" styleId="revalidationMode" value="Client Initiated" property="revalidationMode" style="width: 206;" tabindex="10">					    	
								<% 
					    			List<PCRFKeyValueConstants> pcrfConstantList = PCRFKeyValueConstants.values(PCRFKeyConstants.REVALIDATION_MODE);
					    			for(PCRFKeyValueConstants constant : pcrfConstantList){		
					    		%>		
						   			<html:option value='<%=constant.val%>' ><%=constant.displayValue%></html:option>
						   		<% } %>	
						   							   		 						   		 
							</html:select>      
						</td>
				 	 </tr>					 	 				  					 
			   </table>  
			</td> 
		  </tr>	 
		  <tr> 
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr>

          <tr> 
	        <td class="btns-td" valign="middle" >&nbsp;</td> 
            <td class="btns-td" valign="middle"  > 
		        <input type="button" onclick="validate();" value="     Next     "  class="light-btn" tabindex="11">
		        <input type="button" align="left" value="  Cancel  " class="light-btn" tabindex="12" onclick="javascript:location.href='<%=basePath%>/initSearchProfile.do?/>'" />                  
	        </td> 
   		  </tr> 
		</table> 
	  </td> 
	</tr> 
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 
</html:form> 
