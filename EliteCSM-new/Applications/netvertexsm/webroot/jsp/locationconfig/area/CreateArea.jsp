<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants" %>
<%@ page import="com.elitecore.netvertexsm.web.locationconfig.area.form.AreaMgmtForm" %>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData" %>

<script type="text/javascript" 	src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" 	src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<link 	rel="stylesheet" 		href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

<style>
.allborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; line-height: 19px}
.allborderwithtop { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; line-height: 19px} 
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.plcKey { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style>
<%
	AreaMgmtForm 	  		areaMasterForm 	= (AreaMgmtForm)request.getAttribute("areaMasterForm");
	List<RegionData> 	  	regionDataList 	= areaMasterForm.getListRegionData();
	List<CityData> 		  	cityDataList 	= areaMasterForm.getListCityData();
	List<NetworkData> 	listNetworkData = areaMasterForm.getListNetworkData();
%>

<script type="text/javascript">
var idVal=0;
$(document).ready(function(){
	$("#area").focus();
	$('table td img.delete').on('click',function() {
		$(this).parent().parent().remove(); 
	});
	$('#addLac').click(function() {
		idVal++;
		var orderNumArray = document.getElementsByName("orderNumber");
		var currentOrderNumber=1;
		if(orderNumArray!=null && orderNumArray.length>0){
			for(var i=0; i<orderNumArray.length; i++){
				currentOrderNumber = orderNumArray[i].value;	
			}
			currentOrderNumber++;
		}
		var tableRowStr = '<tr id="lacData" >'+
								'<td align="center" class="tblfirstcol" valign="middle" width="15%"><input type="text" size="6" maxlength="6" name="lacCode" id="'+idVal+'" value="" tabindex="10" /></td>'+
								'<td align="center" class="tblrows" valign="middle" width="25%"><textarea cols="18" rows="2" name="CI"  tabindex="10" maxlength="4000" ></textarea></td>'+
								'<td align="center" class="tblrows" valign="middle" width="25%"><textarea cols="18" rows="2" name="SAC" tabindex="10" maxlength="4000" ></textarea></td>'+								
								'<td align="center" class="tblrows" valign="middle" width="25%"><textarea cols="18" rows="2" name="RAC" tabindex="10" maxlength="4000" ></textarea></td>'+ 					
								'<td class="tblrows" align="center" colspan="3" width="5%"><img value="top" tabindex="8" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" onclick="$(this).parent().parent().remove();"/></td>'+
						'</tr>';
	   $(tableRowStr).appendTo('#3GPPLacInformationTable');
		$("#"+idVal).focus();					
	});
	
	if($("#networkId").val()==0){
		document.getElementById("addLac").disabled=true;
		document.getElementById("addLac").className="light-btn-disabled";
		var id = "lacData";
		$("tr").each(function(){
		    if($(this).attr("id") == id){
		        $(this).remove();
		        return;
		    }
		});
	}else{
		document.getElementById("addLac").disabled=false;
		document.getElementById("addLac").className="light-btn";
	}	
	onCountryChange();
});

function onNetworkChange(){
	if($("#networkId").val()==0){
		document.getElementById("addLac").disabled=true;
		document.getElementById("addLac").className="light-btn-disabled";
		var id = "lacData";
		$("tr").each(function(){
		    if($(this).attr("id") == id){
		        $(this).remove();
		        return;
		    }
		});
	}else{
		document.getElementById("addLac").disabled=false;
		document.getElementById("addLac").focus();
		document.getElementById("addLac").className="light-btn";
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
	setNetworkData(countryIdJS);
	setCitySelectEmpty();
	onNetworkChange();
}
function setCitySelectEmpty(){
	var citySelectOp = document.getElementById('cityId');
	citySelectOp.options.length = 0;
	citySelectOp.options[0] = new Option ("--Select--","0");	
	citySelectOp.options[0].selected="true"
}

function setNetworkData(countryIdJS){
	
	var networkSelect = document.getElementById('networkId');
	networkSelect.options.length = 0;
	networkSelect.options[0] = new Option ("--Select--","0");	
	networkSelect.options[0].selected="true";	
	var index = 1;
	<%for(NetworkData networkData:listNetworkData){%>
			if(countryIdJS==<%=networkData.getCountryID()%>){				
				networkSelect.options[index] = new Option ('<%=networkData.getNetworkName()%>','<%=networkData.getNetworkID()%>');
				index++;
			}			
	<%}%>		
}

function onStateChange(){
	var regionIdJS = document.getElementById("regionId").value;
	var citySelect = document.getElementById('cityId');
	citySelect.options.length = 0;
	citySelect.options[0] = new Option ("--Select--","0");	
	citySelect.options[0].selected="true";	
	var index = 1;
	<%for(CityData cityData:cityDataList){%>
			if(regionIdJS==<%=cityData.getRegionId()%>){				
				citySelect.options[index] = new Option('<%=cityData.getCityName()%>','<%=cityData.getCityId()%>');
				index++;
			}			
	<%}%>			
}


var isValidName;
function validate(){
	
		var ci_sac_rac_pattern = /^\d*[0-9]([\;|\,]\d*[0-9])*(\,|\;)?$/;
		// OLD WORKING : var ci_sac_rac_pattern = /^\d*[0-9]([\;|\,]\d*[0-9])*?$/;
		var lacRowFlag = true;
		var lacArray = document.getElementsByName("lacCode");
		var ciArray	 = document.getElementsByName("CI");
		var sacArray = document.getElementsByName("SAC");
		var racArray = document.getElementsByName("RAC");				
		
		if(isNull(document.forms[0].area.value)){
			alert("Location Name must be specified.");
			document.forms[0].area.focus();			
			return;
		}else if(!isValidName) {
			alert('Enter Valid Location Name');
			document.forms[0].area.focus();
			return;
		}else if(document.forms[0].countryId.value=='0') {
			alert('Please select Country.');
			document.forms[0].countryId.focus();
			return;
		}else if(document.forms[0].regionId.value=='0') {
			alert('Please select State.');
			document.forms[0].regionId.focus();
			return;
		}else if(document.forms[0].cityId.value=='0') {
			alert('Please select City.');
			document.forms[0].cityId.focus();
			return;
		}else if(lacArray.length>0){
			
			for(var i=0; i<ciArray.length; i++){
				if(lacArray[i].value.trim().length==0){
					alert("Please provide LAC's data.");
					return;
				}
				if(!isNull(ciArray[i].value) && !ci_sac_rac_pattern.test(ciArray[i].value)){
					alert("Invalid CIs value.");
					return;
				}
				if(!isNull(sacArray[i].value) && !ci_sac_rac_pattern.test(sacArray[i].value)){
					alert("Invalid SACs value.");
					return;
				}
				if(!isNull(racArray[i].value) &&  !ci_sac_rac_pattern.test(racArray[i].value)){
					alert("Invalid RACs value.");
					return;
				}											
				
				if(ciArray[i].value!=null && ciArray[i].value.trim().length>0 && lacArray[i].value.trim().length==0){
					lacArray[i].value='';																					
					alert("Please input LACs value.");
					lacRowFlag = false;
					return;
				}
				if(sacArray[i].value!=null && sacArray[i].value.trim().length>0 && ciArray[i].value.trim().length==0){
					ciArray[i].value='';
					alert("Please input CIs value.");
					lacRowFlag = false;
					return;
				}
				if(racArray[i].value!=null && racArray[i].value.trim().length>0 && sacArray[i].value.trim().length==0){
					sacArray[i].value='';
					alert("Please input SACs value.");
					lacRowFlag = false;
					return;
				}
			}
			if(lacRowFlag == true){
				document.forms[0].submit();	
			}
		}else{			
			document.forms[0].submit();
		}
}

function verifyFormat (){
	var searchName = document.getElementById("area").value;
	callVerifyValidFormat({instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,isSpaceAllowed:"yes",parentId:cityId,searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

function verifyName() {
	var cityId 		= document.getElementById("cityId").value;
	var searchName 	= document.getElementById("area").value;
	if(jQuery.trim(searchName).length>0){
		isValidName = verifyInstanceName({instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,isSpaceAllowed:"yes",parentId:cityId,searchName:searchName,mode:'create',id:''},'verifyNameDiv');
		
		if(isValidName==true){
			$("#verifyNameDiv").text('');
		} 
	}
}
$(document).ready(function() {	
	setTitle('<bean:message  bundle="locationMasterResources" key="area.management.title"/>');
 	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,propertyName:"param1"}, function(data){
 		dbFieldStr = data.substring(1,data.length-3);
 		var dbFieldArray = new Array();
 		dbFieldArray = dbFieldStr.split(", ");
 	 	var paramArray = new Array(); 	 	
 		for(var i=0; i<dbFieldArray.length; i++){
 			if(jQuery.trim(dbFieldArray[i]).length>0 && dbFieldArray[i]!='null'){ 				
 				paramArray+=dbFieldArray[i]; 				
 				if((i+1)<=dbFieldArray.length && dbFieldArray[i+1]!=null){
 					paramArray+=",";
 				}
 			}
 		} 		
 		paramArray = paramArray.split(","); 		
 		$("#param1").autocomplete(paramArray, {
 			minChars: 0,
 			max: 100
 		});
 		return dbFieldArray;
 	});	
 	
 	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,propertyName:"param2"}, function(data){
 		dbFieldStr = data.substring(1,data.length-3);
 		var dbFieldArray = new Array();
 		dbFieldArray = dbFieldStr.split(", ");
 		var paramArray = new Array();
 		for(var i=0; i<dbFieldArray.length; i++){
 			if(jQuery.trim(dbFieldArray[i]).length>0 && dbFieldArray[i]!='null'){
 				paramArray+=dbFieldArray[i]; 				
 				if((i+1)<=dbFieldArray.length && dbFieldArray[i+1]!=null){
 					paramArray+=",";
 				}
 			}
 		} 
 		paramArray = paramArray.split(",");
 		$("#param2").autocomplete(paramArray, {
 			minChars: 0,
 			max: 100
 		});
 		return dbFieldArray;
 	});	
 	
 	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,propertyName:"param3"}, function(data){
 		dbFieldStr = data.substring(1,data.length-3);
 		var dbFieldArray = new Array();
 		dbFieldArray = dbFieldStr.split(", ");
 		var paramArray = new Array();
 		for(var i=0; i<dbFieldArray.length; i++){
 			if(jQuery.trim(dbFieldArray[i]).length>0 && dbFieldArray[i]!='null'){
 				paramArray+=dbFieldArray[i]; 				
 				if((i+1)<=dbFieldArray.length && dbFieldArray[i+1]!=null){
 					paramArray+=",";
 				}
 			}
 		} 
 		paramArray = paramArray.split(",");
 		$("#param3").autocomplete(paramArray, {
 			minChars: 0,
 			max: 100
 		});
 		return dbFieldArray;
 	});	
 	
 });

</script>

</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%" >
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
  <tr>
  	<td width="10">&nbsp;</td> 
    <td width="100%" valign="top" class="box">
<html:form action="/areaManagement.do?method=create">		
     
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr> 
		<td class="table-header" colspan="4">
			<bean:message bundle="locationMasterResources" key="area.create" /> 
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
		<td align="right" class="labeltext" valign="top" class="box" width="80%"> 

  			<table width="100%" align="right" border="0">
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="area.name" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.name"/>','<bean:message bundle="locationMasterResources" key="area.name" />')"/></td> 
					<sm:nvNameField size="30" id="area" name="area"/>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="city.country.name" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.country"/>','<bean:message bundle="locationMasterResources" key="city.country.name" />')"/></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMasterForm" styleId="countryId" tabindex="2" property="countryId"  size="1" style="width: 220px;" onchange="onCountryChange();"  onkeypress="enableDisableLacButton();" >
									  <html:option value="0">--Select--</html:option>
									  <logic:iterate id="country" name="areaMasterForm" property="listCountryData" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData">
										<html:option value="<%=Long.toString(country.getCountryID())%>"><bean:write name="country" property="name"/></html:option>
									  </logic:iterate> 
						</html:select><font color="#FF0000"> *</font> 	      
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="area.region" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.region"/>','<bean:message bundle="locationMasterResources" key="area.region" />')"/></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMasterForm" styleId="regionId" tabindex="3" property="regionId"  size="1" style="width: 220px;" onchange="onStateChange()" >
									  <html:option value="0">--Select--</html:option>
						</html:select><font color="#FF0000"> *</font> 	      
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="city.title" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.city"/>','<bean:message bundle="locationMasterResources" key="city.title" />')"/></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMasterForm" styleId="cityId" tabindex="4" property="cityId" onchange="verifyName();"  size="1" style="width: 220px;">
									  <html:option value="0">--Select--</html:option>
						</html:select><font color="#FF0000"> *</font> 	      
					</td>
				</tr>
				<tr> 
					<td align="left" class="labeltext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="area.param1" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.param1"/>','<bean:message bundle="locationMasterResources" key="area.param1" />')"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text styleId="param1" property="param1"  onkeyup="" maxlength="64" size="30" tabindex="5" />&nbsp;						
					</td> 
				  </tr>
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="area.param2" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.param2"/>','<bean:message bundle="locationMasterResources" key="area.param2" />')"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text styleId="param2" property="param2"  onkeyup="" maxlength="64" size="30" tabindex="6" />&nbsp;						
					</td> 
				  </tr>
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="area.param3" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.param3"/>','<bean:message bundle="locationMasterResources" key="area.param3" />')"/></td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text styleId="param3" property="param3"  onkeyup="" maxlength="64" size="30" tabindex="7" />&nbsp;						
					</td> 
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="area.network" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.network"/>','<bean:message bundle="locationMasterResources" key="area.network" />')"/></td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMasterForm" styleId="networkId" tabindex="8" property="networkId"  size="1" style="width: 220px;" onchange="onNetworkChange();" >
									  <html:option value="0">--Select--</html:option>

						</html:select><font color="#FF0000"></font> 	      
					</td>
				  </tr>  				  
				  <tr><td width="10" class="small-gap">&nbsp;</td></tr><tr><td width="10" class="small-gap">&nbsp;</td></tr>
			   </table>  
			</td> 
		  </tr>
		  <tr>
		  	<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		  </tr>
		  <tr> 
			<td style="margin-left: 2.0em;" class="tblheader-bold" colspan="8"><bean:message  bundle="locationMasterResources" key="area.3gpp.lcac.info"/></td>
		  </tr>		  		  	 		                              
          <tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" colspan="2">&nbsp;</td>
		  </tr>	
		  <tr id='3GPPLocationAttributes'> 
		  	<td valign="middle" colspan="3">  
				<table cellpadding="0" id="3GPPLacInformationTable" cellspacing="0" border="0" width="90%" class="">
					<tr> 
          				<td>
            				<input type="button" id="addLac" value="Add LACs" tabindex="9" class="light-btn-disabled" onclick="">
            			</td>
          			</tr>					
					<tr class="L">
						<td align="center" class="tblheaderfirstcol" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="area.lacs" />	</td>
						<td align="center" class="tblheader" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="area.cis" />	</td>
						<td align="center" class="tblheader" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="area.sacs" />	</td>
						<td align="center" class="tblheader" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="area.racs" />	</td>
						<td align="center" class="tblheaderlastcol" valign="top" width="5%"><bean:message bundle="locationMasterResources" key="area.remove" /></td>
					</tr>
			</table>
			</td> 
		  </tr>
		    <tr>
		  	<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		  </tr>
		  <tr> 
			<td style="margin-left: 2.0em;" class="tblheader-bold" colspan="8"><bean:message  bundle="locationMasterResources" key="area.wifi.ap.info"/></td>
		  </tr>		  		  	 		                              
          
          <tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" >&nbsp;</td>
		  </tr>
		  
		  <tr id='WifiAPsInformation'> 
		  	<td valign="middle" colspan="3">  
				<table cellpadding="0" id="WiFiAPsInformationTable" cellspacing="0" border="0" width="80%">
					<tr>
						<td align="left"  class="labeltext" valign="middle" width="12%"><bean:message bundle="locationMasterResources" key="area.called.staion.ids" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.called.staion.ids"/>','<bean:message bundle="locationMasterResources" key="area.called.staion.ids" />')"/></td>
						<td align="center" class="" valign="top" width="45%"><textarea  cols="40" rows="2"  name="strCallingStationIds"  maxlength="256" tabindex="11" ></textarea></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="middle" width="12%"><bean:message bundle="locationMasterResources" key="area.wifi.ap.ssids" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.wifi.ap.ssids"/>','<bean:message bundle="locationMasterResources" key="area.wifi.ap.ssids" />')"/></td>
						<td align="center" class="" valign="top" width="45%"><textarea cols="40" rows="2" name="strWiFiSSIDs" maxlength="4000"  tabindex="12"></textarea></td>
					</tr>
				</table>
			</td> 
		  </tr>
		  	 
		  <tr><td width="10" class="">&nbsp;</td></tr>		            
   		  
   		  <tr align="center">
				<td >
					<input type="button" property="c_btnCreate" onclick="validate();" value="  Create  " class="light-btn" tabindex="13"/>&nbsp;&nbsp;&nbsp;
					<input type="button" property="c_btnCreate" onclick="javascript:location.href='<%=basePath%>/areaManagement.do?method=initSearch'" value="  Cancel  " class="light-btn" tabindex="14"/> 
	         	</td>
				<td>&nbsp;</td>
		  </tr>
   		   
		</table> 
	  </td> 
	</tr>
	</table>	 
	</html:form> 
	</td>
	</tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 

