<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants" %>
<%@ page import="com.elitecore.netvertexsm.web.locationconfig.city.form.CityMgmtForm" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData" %>
<%@ page import="java.util.*" %>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

<style>
	.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; line-height: 19px}
	.allborderwithtop { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; line-height: 19px} 
	.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
	.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
	.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style>

<%
	CityMgmtForm 	  	cityManagementForm 	= (CityMgmtForm)request.getAttribute("cityManagementForm");
	List<RegionData> 	regionDataList 		= cityManagementForm.getRegionList();
%>
<script type="text/javascript">
var isValidName =  true;
var idVal=0;	
$(document).ready(function(){
	setTitle('<bean:message  bundle="locationMasterResources" key="city.management.title"/> Management');
	$("#countryId").focus();
	$('table td img.delete').on('click',function() {
		idVal--;
		$(this).parent().parent().remove(); 
	});
	$('#addCity').click(function() {
		idVal++;
		if(isValidName==true || idVal==1){	
			var tableRowStr = '<tr name="cityRow">'+
								'<td align="center" class="tblfirstcol" valign="middle" width="30%"><input type="text" size="45" id="'+idVal+'" name="city" value="" onblur="verifyName(this);" onkeyup="verifyFormat(this);" tabindex="4" /><div id="verifyNameDiv'+idVal+'" name="verifyNameDiv" class="labeltext"></td>'+
								'<td class="tblrows" align="center" colspan="3" width="10%"><img value="top" tabindex="4" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" onclick="$(this).parent().parent().remove();"/></td>'+
						'</tr>';						
	   		$(tableRowStr).appendTo('#CityTable');
	   		$("#"+idVal).focus();
		}else{
			idVal--;
			if(idVal!=0){
				alert("Please provide valid city name.");
			}			
			document.getElementById(idVal).focus();
		}
	});	
});

function onRegionChange(){
	checkDuplicateCity();
}

function checkForAlreayExistErrorMessage(){
	var divTags  = document.getElementsByName("verifyNameDiv");
	var cityField = document.getElementsByName("city");
	var alreadyExistFlag=0;
	for(var k=0; k<divTags.length; k++){
		alreadyExistFlag = divTags[k].innerHTML.indexOf("Already Exists.");
		if(alreadyExistFlag!=-1){
			alert("City already exist.");			
			cityField[k].focus();
			return true;
		}
	}
}

function checkDuplicateCity(){
	var cityField = document.getElementsByName("city");	
	for(var k=0; k<cityField.length; k++){									
		verifyName(cityField[k]);
	}
}

function validate(){		
	if(checkForAlreayExistErrorMessage()){
		return false;
	}	
	if(document.getElementById("countryId").value==0){
		document.getElementById("countryId").focus();
		alert("Please select Country.");
		return false;
	} else if (document.getElementById("regionId").value==0){
		document.getElementById("regionId").focus();
		alert("Please select Region.");
		return false;
	}	
	
	var cityNames = document.getElementsByName("city");		
	for(var i=0; i<cityNames.length; i++){
		if(isNull(cityNames[i].value)){						
			alert("Please provide city name.");
			cityNames[i].focus();
			return false;
		}	
	}
	
	var cityNames = document.getElementsByName("city");		
	for(var i=0; i<cityNames.length; i++){
		for(var j=i+1; j<cityNames.length; j++){
			if($.trim(cityNames[i].value) == $.trim(cityNames[j].value)){						
				alert("Duplicate city found !");
				cityNames[i].focus();
				return false;
			}				
		}
	}	
		
	if(cityNames.length==0){
		alert("No city found !");
		return false;
	}	
	
	if(!isValidName){
		var cityElement = document.getElementById(idVal);		
		alert("City already exist.");
		cityElement.focus();
		return false;
	}
}

function onCountryChange(){
	var countryIdJS = document.getElementById("countryId").value;
	var regionSelect = document.getElementById('regionId');
	regionSelect.options.length = 0;
	regionSelect.options[0] = new Option ("--Select--","0");	
	regionSelect.options[0].selected="true";	
	var index = 1;
	<%for(RegionData regionData:regionDataList){%>
			if(countryIdJS==<%=regionData.getCountryId()%>){				
				regionSelect.options[index] = new Option ('<%=regionData.getRegionName()%>','<%=regionData.getRegionId()%>');
				index++;
			}			
	<%}%>
}

function verifyFormat (element){
	var regionId 		= document.getElementById("regionId").value;
	var searchName 		= element.value;
	var verifyNameDiv 	= "verifyNameDiv"+element.id;
	callVerifyValidFormat({instanceType:<%=InstanceTypeConstants.CITY%>,isSpaceAllowed:"yes",parentId:regionId,searchName:searchName,mode:'create',id:''},verifyNameDiv);
}

function verifyName(element) {
	
	var regionId 		= document.getElementById("regionId").value;
	var searchName 		= element.value;
	var verifyNameDiv 	= "verifyNameDiv"+element.id;	
	if(jQuery.trim(searchName).length>0 ){
		isValidName = verifyInstanceName({instanceType:<%=InstanceTypeConstants.CITY%>,isSpaceAllowed:"yes",parentId:regionId,searchName:searchName,mode:'create',id:''},verifyNameDiv);
		if(isValidName==true){
			$("#"+verifyNameDiv).text('');
		}
	}
	return isValidName;
}

</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%" >
  <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
  <tr>
  	<td width="10">&nbsp;</td> 
    <td width="100%" valign="top" class="box">
<html:form action="/cityManagement.do?method=create" onsubmit="return validate();">	
   
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr> 
		<td class="table-header" colspan="4">
			<bean:message bundle="locationMasterResources" key="city.create" /> 
		</td>
	</tr>
    <tr>
		<td valign="top" align="right"> 
		<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		 		<tr> 
					 <td width="10" class="small-gap">&nbsp;</td>
				</tr>
				 <tr>
				 		<td width="10" class="small-gap">&nbsp;</td>
				 </tr> 
				 <tr>
			   	 	<td align="left" class="labeltext" valign="top" width="25%"  >
							<bean:message bundle="locationMasterResources" key="region.country" />
					</td> 
				 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="cityManagementForm" styleId="countryId" tabindex="1" property="countryId"  size="1" style="width: 220px;" onchange="onCountryChange();" >
									  <html:option value="0">--Select--</html:option>
									   <logic:iterate id="country" name="cityManagementForm" property="countryList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData">
										<html:option value="<%=Long.toString(country.getCountryID())%>"><bean:write name="country" property="name"/></html:option>
									</logic:iterate> 
						</html:select><font color="#FF0000"> *</font> 	      
					</td>
				 </tr>
				 <tr>
			   	 	<td align="left" class="labeltext" valign="top" width="25%" >
							<bean:message bundle="locationMasterResources" key="city.region" />
					</td> 
					 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="cityManagementForm" styleId="regionId" tabindex="2" property="regionId"  onchange="onRegionChange();" size="1" style="width: 220px;">
									  <html:option value="0">--Select--</html:option>
<%-- 									   <logic:iterate id="region" name="cityManagementForm" property="regionList" type="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData">
										<html:option value="<%=Long.toString(region.getRegionId())%>"><bean:write name="region" property="regionName"/></html:option>
									</logic:iterate> 
 --%>						</html:select><font color="#FF0000"> *</font> 	      
					</td>
				</tr>
			 
	<tr>
		<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
	</tr>
     <tr>
		<td width="10" class="small-gap">&nbsp;</td>
		<td class="small-gap" colspan="2">&nbsp;</td>
	 </tr>	
	 <tr id='CityInfo'> 
		<td valign="middle" colspan="3">  
				<table cellpadding="0" id="CityTable" cellspacing="0" border="0" width="40%" class="">
				<tr> 
					<td style="margin-left: 2.0em;" class="tblheader-bold" colspan="8"><bean:message bundle="locationMasterResources" key="city.information" /></td>
				</tr>	
					<tr> 
          				<td>
            				<input type="button" id="addCity" value="Add City" tabindex="3" class="light-btn" onclick="">
            			</td>
          			</tr>					
					<tr class="L">
						<td align="center" class="tblheaderfirstcol" valign="top" width="30%"><bean:message bundle="locationMasterResources" key="city.cities" /></td>
						<td align="center" class="tblheaderlastcol" valign="top" width="10%"><bean:message bundle="datasourceResources" key="general.public.remove"/></td>
					</tr>
			</table>
			</td> 
		  </tr>
	      <tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" >&nbsp;</td>
		  </tr>
		  <tr><td width="10" class="">&nbsp;</td></tr>		            
   		  
   		  <tr>
				<td colspan="3" align="left">
					<html:submit value="  Create  " styleClass="light-btn" tabindex="5"/>&nbsp;&nbsp;&nbsp;
					<input type="button" property="c_btnCreate" onclick="javascript:location.href='<%=basePath%>/cityManagement.do?method=initSearch'" value="  Cancel  " class="light-btn" tabindex="6"/> 
	         	</td>
				 
		  </tr>
   		   
		</table> 
	  </td> 
	</tr>
	</table>	 
	</html:form> 
	</td>
	</tr>
 <%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 

