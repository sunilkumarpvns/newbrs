<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants" %>
<%@ page import="com.elitecore.netvertexsm.web.locationconfig.region.form.RegionMgmtForm"%>
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


<script type="text/javascript">
var isValidName =  true;
var idVal=0;	
$(document).ready(function(){	
	setTitle('<bean:message  bundle="locationMasterResources" key="region.management.title"/>');
	$("#countryId").focus();
	$('table td img.delete').on('click',function() {
		idVal--;
		$(this).parent().parent().remove(); 
	});

	$('#addRegion').click(function() {
		idVal++;
		if(isValidName==true || idVal==1){						
			
			var tableRowStr = '<tr>'+
									'<td align="center" class="tblfirstcol" valign="middle" width="30%"><input type="text" size="25" maxlength="64"  id="'+idVal+'" name="region" value="" tabindex="3" onkeyup="verifyName(this);" onblur="verifyName(this);" /><div id="verifyNameDiv'+idVal+'" name="verifyNameDiv" class="labeltext"></td>'+
									'<td class="tblrows" align="center" colspan="3" width="10%"><img value="top" tabindex="3" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" onclick="$(this).parent().parent().remove();"/></td>'+
							'</tr>';		
		   	$(tableRowStr).appendTo('#regionTable');
		   	$("#"+idVal).focus();		   	
		}else{
			idVal--;
			if(idVal!=0){
				alert("Please provide valid region name.");
			}			
			document.getElementById(idVal).focus();			
		}
	});	
	
});

function onCountryChange(){
	checkDuplicateRegion();
}

function checkForAlreayExistErrorMessage(){
	var divTags = document.getElementsByName("verifyNameDiv");
	var regionTags = document.getElementsByName("region");
	var alreadyExistFlag=0;
	for(var k=0; k<divTags.length; k++){
		alreadyExistFlag = divTags[k].innerHTML.indexOf("Already Exists.");
		if(alreadyExistFlag!=-1){
			alert("Region already exist.");
			regionTags[k].focus();
			return true;
		}
	}
}

function checkDuplicateRegion(){
	var regionField = document.getElementsByName("region");	
	for(var k=0; k<regionField.length; k++){									
		verifyName(regionField[k]);
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
	}
	var regionNames = document.getElementsByName("region");		
	for(var i=0; i<regionNames.length; i++){
		if(isNull(regionNames[i].value)){						
			alert("Please provide region name.");
			regionNames[i].focus();
			return false;
		}
		verifyName(regionNames[i]);	
	}
	var regionNames = document.getElementsByName("region");		
	for(var i=0; i<regionNames.length; i++){
		for(var j=i+1; j<regionNames.length; j++){
			if(regionNames[i].value == regionNames[j].value){						
				alert("Duplicate region found !");
				regionNames[i].focus();
				return false;
			}				
		}
	}	
	if(!isValidName){
		var regionElement = document.getElementById(idVal);		
		alert("Region already exist.");
		regionElement.focus();
		return false;
	}
	
	if(regionNames.length==0){
		alert("No region found !");
		return false;
	}
}

function verifyFormat (element){
	var countryId 		= document.getElementById("countryId").value;
	var searchName 		= jQuery.trim(element.value);
	var verifyNameDiv 	= "verifyNameDiv"+element.id;
		callVerifyValidFormat({instanceType:<%=InstanceTypeConstants.REGION%>,isSpaceAllowed:"yes",parentId:countryId,searchName:searchName,mode:'create',id:''},verifyNameDiv);
}

function verifyName(element) {
	var countryId 		= document.getElementById("countryId").value;
	var searchName 		= jQuery.trim(element.value);
	var verifyNameDiv 	= "verifyNameDiv"+element.id;	
	if(jQuery.trim(searchName).length>0 ){
		isValidName = verifyInstanceName({instanceType:<%=InstanceTypeConstants.REGION%>,isSpaceAllowed:"yes",parentId:countryId,searchName:searchName,mode:'create',id:''},verifyNameDiv);		
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
<html:form action="/regionManagement.do?method=create" onsubmit="return validate();"> 
     
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
 	
	<tr> 
		<td class="table-header" colspan="4">
			<bean:message bundle="locationMasterResources" key="region.create" /> 
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
				   	  	<td align="left" class="labeltext" valign="top"  >
								<bean:message bundle="locationMasterResources" key="region.country.name" />
						</td>
						<td width="40" class="small-gap">&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td align="left" class="labeltext" valign="top" > 
							<html:select name="regionMgmtForm" styleId="countryId" tabindex="1" property="countryId" onchange="onCountryChange();" size="1" style="width: 220px;">
										  <html:option value="0">--Select--</html:option>
										   <logic:iterate id="country" name="regionMgmtForm" property="countryList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData">
											<html:option value="<%=Long.toString(country.getCountryID())%>"><bean:write name="country" property="name"/></html:option>
										</logic:iterate>  
							</html:select><font color="#FF0000"> *</font> 	      
						</td>
				 </tr>
				 <tr>
				 		<td width="10" class="small-gap">&nbsp;</td>
				 </tr>
				 <tr>
				 		<td width="10" class="small-gap">&nbsp; </td>
				 </tr>
			    
		  <tr>
		  	<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		  </tr>
		   		  	 		                              
          <tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" colspan="2">&nbsp;</td>
		  </tr>	
		  <tr id='RegionInfo'> 
		  	<td valign="middle" colspan="3">  
				<table cellpadding="0" id="regionTable" cellspacing="0" border="0" width="40%" class="">
					<tr> 
			<td style="margin-left: 2.0em;" class="tblheader-bold" colspan="8">
				<bean:message bundle="locationMasterResources" key="region.information" />			
			</td>
		  </tr>	
					<tr> 
          				<td>
            				<input type="button" id="addRegion" value="Add Region" tabindex="2" class="light-btn" onclick="">
            			</td>
          			</tr>					
					<tr class="L">
						<td align="center" class="tblheaderfirstcol" valign="top" width="30%">Regions</td>
						<td align="center" class="tblheaderlastcol" valign="top" width="10%">Remove</td>
					</tr>
			</table>
			</td> 
		  </tr>
	      <tr>
			<td width="10" class="small-gap">&nbsp;<div id="verifyNameDiv1" class="labeltext"></div></td>
			<td class="small-gap" >&nbsp;<div id="verifyNameDivNew" class="labeltext"></div></td>
		  </tr>
		  <tr><td width="10" class="">&nbsp;</td></tr>		            
   		  
   		  <tr align="center">
				<td colspan="3" align="left">
				<html:submit value="  Create  " styleClass="light-btn" tabindex="4"/>&nbsp;&nbsp;&nbsp;
	         		<input type="button" property="c_btnCreate" onclick="javascript:location.href='<%=basePath%>/regionManagement.do?method=initSearch'" value="  Cancel  " class="light-btn" tabindex="5"/>&nbsp;</td>
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

