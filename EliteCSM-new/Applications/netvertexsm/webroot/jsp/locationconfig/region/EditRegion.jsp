<%@ page import="com.elitecore.netvertexsm.web.devicemgmt.form.DeviceManagementForm"%>
<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.web.locationconfig.region.form.RegionMgmtForm" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>
 <%
 	RegionMgmtForm regionMgmtForm = (RegionMgmtForm)request.getAttribute("regionMgmtForm");  
 %>
<script type="text/javascript">

function validate(){
	var regionName = document.getElementById("regionName").value;
	if(jQuery.trim(regionName).length==0){
		alert("Region Name must be specified.");
		document.forms[0].regionName.focus();
		return false;
	}
	
	if(!isValidName) {
			alert("Region already exist.");
			document.forms[0].regionName.focus();
			return false;
	}	
 	
}
 
$(document).ready(function() {		
	document.forms[0].regionName.focus();
});


function verifyFormat (){
	var countryId 		= '<%=regionMgmtForm.getCountryId()%>';
	var searchName 		= document.getElementById("regionName").value;
	callVerifyValidFormat({instanceType:<%=InstanceTypeConstants.REGION%>,isSpaceAllowed:"yes",parentId:countryId,searchName:searchName,mode:'update',id:'<%=regionMgmtForm.getRegionId()%>'},'verifyNameDiv');
}

function verifyName() {
	var countryId 	= '<%=regionMgmtForm.getCountryId()%>';
	var searchName 	= document.getElementById("regionName").value;
	if(jQuery.trim(searchName).length>0 ){
		isValidName 	= verifyInstanceName({instanceType:<%=InstanceTypeConstants.REGION%>,isSpaceAllowed:"yes",parentId:countryId,searchName:searchName,mode:'update',id:'<%=regionMgmtForm.getRegionId()%>'},'verifyNameDiv');
		if(isValidName==true){
			$("#verifyNameDiv").text('');
		}
	}
}

</script> 

<html:form action="/regionManagement.do?method=update" onsubmit="return validate();"> 
<html:hidden name="regionManagementForm" property="regionId"/>
<html:hidden name="regionManagementForm" property="countryId"/>

<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 

   
	<tr> 
		<td colspan="2" align="right"> 
			 	<table cellpadding="0" cellspacing="0" border="0" width="97%">
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="2"  >	<bean:message  bundle="locationMasterResources" key="region.update.link"/> </td>
			</tr>			   
			   	<tr><td>&nbsp;</td></tr>
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="16%">            
						<bean:message bundle="locationMasterResources" key="region.name"/>
					</td>
						<sm:nvNameField maxLength="64" size="30" id="regionName" name="regionName" value="${regionManagementForm.regionName}" />
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
						<input type="button" value=" Cancel " tabindex="4" class="light-btn" onclick="javascript:location.href='<%=basePath%>/regionManagement.do?method=initSearch'"/></td>
				 </tr>
 	    <td width="10" class="small-gap">&nbsp;</td> 
    <td class="small-gap" colspan="2">&nbsp;</td> 
  </tr>  
</tr>			
</table>     	 
 </html:form>