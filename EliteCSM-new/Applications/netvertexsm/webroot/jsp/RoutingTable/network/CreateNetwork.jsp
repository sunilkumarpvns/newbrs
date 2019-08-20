<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.network.form.NetworkManagementForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandOperatorRelData"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List" %>



<%
NetworkManagementForm	networkManagementForm = (NetworkManagementForm)request.getAttribute("networkManagementForm");
List<BrandOperatorRelData> brandOperatorRelDataList = networkManagementForm.getBrandOperatorRelDataList();
%>


<script type="text/javascript">
var isValidName;

function verifyFormat (){
	var searchName = document.getElementById("networkName").value;
	callVerifyValidFormat({instanceType:<%=InstanceTypeConstants.NETWORK_MANAGEMENT%>,isSpaceAllowed:"yes",searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

function verifyName() {
	var searchName = document.getElementById("networkName").value;
	isValidName = verifyInstanceName({instanceType:<%=InstanceTypeConstants.NETWORK_MANAGEMENT%>,isSpaceAllowed:"yes",searchName:searchName,mode:'create',id:''},'verifyNameDiv');
	if(isValidName==true){
		$("#verifyNameDiv").text('');
	}
}
function validate(){
	if(document.forms[0].brandID.value == "0"){
		alert("Brand must be specified.");
	}else if(document.forms[0].operatorID.value == "0"){
		alert("Operator must be specified.");
	}else if(isNull(document.forms[0].networkName.value)){
		alert("Network Name must be specified.");
	}else if(document.forms[0].countryID.value == "0"){
		alert("Country must be specified.");
	}else if(isNull(document.forms[0].mcc.value)){
		alert('MCC must be specified.');
		document.forms[0].mcc.focus();
	}else if(isEcNaN(document.forms[0].mcc.value)){
		alert('MCC must be numeric.');
		document.forms[0].mcc.focus();
	}else if(isNull(document.forms[0].mnc.value)){
		alert('MNC must be specified.');
		document.forms[0].mnc.focus();
	}else if(isEcNaN(document.forms[0].mnc.value)){
		alert('MNC must be numeric.');
		document.forms[0].mnc.focus();
	}else if(!isValidName) {
		alert('Enter Valid Network Name.');
		document.forms[0].networkName.focus();
		return;
	}else{ 
		document.forms[0].submit();	
	}
}

 function setOperator(){
	clearSelectTag();
	var brandIDVal= document.forms[0].brandID.value;
	var count=0;
	document.forms[0].operatorID.options[count]=new Option('--Select--', '0');
	count++;
	<%	
		if(brandOperatorRelDataList != null && brandOperatorRelDataList.size() > 0){
			int k = 1;
			for(int i=0;i<brandOperatorRelDataList.size();i++){				
				BrandOperatorRelData brandOperatorRelData = brandOperatorRelDataList.get(i);
				BrandData brandData = brandOperatorRelData.getBrandData();
				OperatorData operatorData = brandOperatorRelData.getOperatorData();
	%>	
		if(brandIDVal == "<%=brandData.getBrandID()%>"){			
			document.forms[0].operatorID.options[count]=new Option('<%=operatorData.getName()%>', <%=operatorData.getOperatorID()%>);
			count++;
		}
	<%
			}
		}
	%>
}

 
 $(document).ready(function() {	
	 setTitle('<bean:message  bundle="routingMgmtResources" key="network.mgmt.title"/>');
	 setOperator();
		 
	});
 
function clearSelectTag() {
	 var elSel = document.forms[0].operatorID;
	 for (var i = elSel.length - 1; i>=0; i--) {
		      elSel.remove(i);
     }
}

function retriveMCC() {
	var mccDataString;
	var countryIDValue = document.getElementById("countryID").value;
	$.post("FieldRetrievalServlet", {countryID:countryIDValue}, function(data){
		mccDataString = data.substring(1,data.length-3);
		var mccArray = new Array();
		mccArray = mccDataString.split(", ");
		setFields("mcc",mccArray);		
		return mccArray;
	});	
	
}

</script> 	

<html:form action="/networkManagement.do?method=create"> 

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
   <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message  bundle="routingMgmtResources" key="network.create.title"/>
			</td>
		  </tr>
          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%"  align="left" border="0" > 				   	 	
			   	 <tr> 
						<td align="left" class="captiontext" valign="top" width="30%" >
							<bean:message bundle="routingMgmtResources" key="network.brand" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="network.brand"/>','<bean:message bundle="routingMgmtResources" key="network.brand" />')"/>
						</td> 
															
						<td align="left"  valign="top" width="70%"> 
								<html:select name="networkManagementForm" styleId="brandID" tabindex="1" property="brandID" onchange="setOperator()"  size="1" style="width: 220px;">
								<html:option value="0">--Select--</html:option>
									<logic:iterate id="brand"  name="networkManagementForm" property="brandDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData">
										<html:option value="<%=Long.toString(brand.getBrandID())%>"><bean:write name="brand" property="name"/></html:option>
									</logic:iterate>
								</html:select>		
								<font color="#FF0000"> *</font>						 	      
						</td>
				   </tr>
				   
				   <tr> 
						<td align="left" class="captiontext" valign="top" width="30%" >
							<bean:message bundle="routingMgmtResources" key="network.operator" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="network.operator"/>','<bean:message bundle="routingMgmtResources" key="network.operator" />')"/>
						</td> 
															
						<td align="left"  valign="top" width="70%"> 
							<html:select name="networkManagementForm" styleId="operatorID" tabindex="2" property="operatorID" size="1" style="width: 220px;">
							</html:select>	
							<font color="#FF0000"> *</font> 	      
						</td>
				   </tr>		
			   	  
			   	  <tr> 
						<td align="left" class="captiontext" valign="top" >
							<bean:message bundle="routingMgmtResources" key="network.networkname" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="network.networkname"/>','<bean:message bundle="routingMgmtResources" key="network.networkname" />')"/>
						</td> 
						<sm:nvNameField maxLength="32" value="${networkManagementForm.networkName}" id= "networkName" name="networkName"/>
				   </tr>
			   	  
			   	  <tr> 
						<td align="left" class="captiontext" valign="top" width="30%" >
							<bean:message bundle="routingMgmtResources" key="network.technology" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="network.technology"/>','<bean:message bundle="routingMgmtResources" key="network.technology" />')"/>
						</td> 
															
						<td align="left"  valign="top" width="70%"> 
							<html:select name="networkManagementForm" styleId="technology" tabindex="4" property="technology" size="1" style="width: 220px;">
								<html:option value="0">--Select--</html:option>
								<html:option value="GSM">GSM</html:option>
								<html:option value="CDMA">CDMA</html:option>	
								</html:select>	      
						</td>
				   </tr>				   				   			  				   		   	 							 
				  
				  	<tr> 
						<td align="left" class="captiontext" valign="top" width="30%" >
							<bean:message bundle="routingMgmtResources" key="network.country" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="network.country"/>','<bean:message bundle="routingMgmtResources" key="network.country" />')"/>
						</td> 
															
						<td align="left"  valign="top" width="70%"> 
							<html:select name="networkManagementForm" styleId="countryID" tabindex="6" property="countryID" onchange="retriveMCC()" size="1" style="width: 220px;">
								<html:option value="0">--Select--</html:option>
									<logic:iterate id="country"  name="networkManagementForm" property="countryDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData">
										<html:option value="<%=Long.toString(country.getCountryID())%>"><bean:write name="country" property="name"/></html:option>
									</logic:iterate>
								</html:select>	  
								<font color="#FF0000"> *</font>    
						</td>
				   </tr>	
				  
				   <tr> 
						<td align="left" class="captiontext" valign="top" >
							<bean:message bundle="routingMgmtResources" key="network.mcc" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="network.mcc"/>','<bean:message bundle="routingMgmtResources" key="network.mcc" />')"/>
						</td> 
						<td align="left"  valign="top" > 
							<html:text name="networkManagementForm" property="mcc" maxlength="3" tabindex="7" size="5" styleId="mcc"/><font color="#FF0000"> *</font>
						</td> 
				   </tr>
				  
				  <tr>
						<td align="left" class="captiontext" valign="top" >
							<bean:message bundle="routingMgmtResources" key="network.mnc" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="network.mnc"/>','<bean:message bundle="routingMgmtResources" key="network.mnc" />')"/>
						</td> 
						<td align="left"  valign="top" > 
							<html:text name="networkManagementForm" property="mnc" maxlength="3" tabindex="8" size="5" styleId="mnc"/><font color="#FF0000"> *</font>
						</td> 
				   </tr>
				  	
				 <tr>
				  		<td align="left" valign="top" ></td>
						<td ><input type="button" onclick="validate();" value="  Create  " tabindex="10" class="light-btn" />
						<input type="button" value=" Cancel " tabindex="10" class="light-btn" onclick="javascript:location.href='<%=basePath%>/networkManagement.do?method=initSearch'"/></td>
				 </tr>
			   </table>  
			</td> 
		</tr>
		
		<tr><td>&nbsp;</td></tr>		
		</table> 
	  </td> 
	</tr>			
	   <%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>
</html:form>

