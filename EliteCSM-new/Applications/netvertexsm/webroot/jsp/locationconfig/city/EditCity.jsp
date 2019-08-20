
<%@page import="com.elitecore.netvertexsm.web.devicemgmt.form.DeviceManagementForm"%>
<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.web.locationconfig.city.form.CityMgmtForm" %>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData" %>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

<%
	CityMgmtForm cityForm = (CityMgmtForm)request.getAttribute("cityManagementForm");
	List<RegionData> 	regionDataList 		= cityManagementForm.getRegionList();
%>
 
<script type="text/javascript">
function validate(){
	var cityName = document.getElementById("cityName").value;
	if(jQuery.trim(cityName).length==0){
		alert("City Name must be specified.");
		document.forms[0].cityName.focus();
		return false;
	}
	else if(!isValidName) {
			alert('Pleas input valid city name.');
			document.forms[0].cityName.focus();
			return false;
	} else  if(document.getElementById("countryId").value==0){
		document.getElementById("countryId").focus();
		alert("Please select Country.");
		return false;
	} else if (document.getElementById("regionId").value==0){
		document.getElementById("regionId").focus();
		alert("Please select Region.");
		return false;
	}
}
 
$(document).ready(function() {	
	verifyName();
	document.forms[0].cityName.focus();		
});

var isValidName;

function verifyFormat (){
	var regionId 		= '<%=cityForm.getRegionId()%>';
	var searchName 		= document.getElementById("cityName").value;
	callVerifyValidFormat({instanceType:<%=InstanceTypeConstants.CITY%>,isSpaceAllowed:"yes",parentId:regionId,searchName:searchName,mode:'update',id:<%=cityForm.getCityId()%>},'verifyNameDiv');
}

function verifyName() {
	var regionId 	= '<%=cityForm.getRegionId()%>';
	var searchName 	= document.getElementById("cityName").value;
	if(jQuery.trim(searchName).length>0){
		isValidName 	= verifyInstanceName({instanceType:<%=InstanceTypeConstants.CITY%>,isSpaceAllowed:"yes",parentId:regionId,searchName:searchName,mode:'update',id:<%=cityForm.getCityId()%>},'verifyNameDiv');
		if(isValidName==true){
			$("#verifyNameDiv").text('');
		}		
	}
}	

</script> 

<html:form action="/cityManagement.do?method=update" onsubmit="return validate();"> 
<html:hidden name="cityManagementForm" property="cityId"/>
<html:hidden name="cityManagementForm" property="countryId" styleId="countryId" />

<table cellSpacing="0" cellPadding="0" width="100%" border="0">    
	<tr> 
		<td colspan="2" align="right"> 
			 	<table cellpadding="0" cellspacing="0" border="0" width="97%">
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="2"  >	<bean:message  bundle="locationMasterResources" key="city.update.link"/> </td>
			</tr>			   
			 <tr><td>&nbsp;</td></tr>  	
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">            
						<bean:message bundle="locationMasterResources" key="city.name"/>
					</td>
						<sm:nvNameField maxLength="64" size="30" id="cityName" name="cityName" value="${cityManagementForm.cityName }"/>
					</tr>
			  
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">            
						<bean:message bundle="locationMasterResources" key="city.region" />
					</td>
						<td  align="left" class="labeltext" valign="top" width="40%">
						<html:select name="cityManagementForm" styleId="regionId" tabindex="2" property="regionId"  size="1" style="width: 210px;">
									  <html:option value="0">--Select--</html:option>
									   <logic:iterate id="region" name="cityManagementForm" property="regionList" type="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData">
									   	<%if(cityForm.getCountryId()==region.getCountryId()){%>									   	
											<html:option value="<%=Long.toString(region.getRegionId())%>"><bean:write name="region" property="regionName"/></html:option>
										<%}%>										
										</logic:iterate> 
						</html:select><font color="#FF0000"> *</font> 
 						</td>
					</tr>												
			   </table>  
			</td> 
		</tr>

		<tr><td>&nbsp;</td></tr>
	 	<tr> 
			<td align="center" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		</tr> 
			   	 <tr align="center">
				  		<td align="center" valign="top" ></td>
						<td >
						<html:submit value="  Update  " styleClass="light-btn" tabindex="3"/>
						<input type="button" value=" Cancel " tabindex="4" class="light-btn" onclick="javascript:location.href='<%=basePath%>/cityManagement.do?method=initSearch'"/></td>
				 </tr>
 	    <td width="10" class="small-gap">&nbsp;</td> 
    <td class="small-gap" colspan="2">&nbsp;</td> 
  </tr>  
</tr>			
</table>     	 
 </html:form>