<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.radius.clientprofile.forms.CreateClientProfileForm"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
   String basePath = request.getContextPath();
   CreateClientProfileForm createClientProfileForm=(CreateClientProfileForm)request.getAttribute("createClientProfileForm");
   List<VendorData> lstVendorDataTemp = createClientProfileForm.getLstVendorData();
   int len=lstVendorDataTemp.size();
   String[]  vendorNames = new String[len];
   String[]  vendorInstanceIds = new String[len];
   for(int i=0;i<len;i++){
	   
	   VendorData vendorData=lstVendorDataTemp.get(i);
	   vendorNames[i]=vendorData.getVendorName();
	   vendorInstanceIds[i]=vendorData.getVendorInstanceId(); 
   }
   
   String clientTypeId=(String)request.getParameter("clientTypeId");
   String vendorInstanceId=(String)request.getParameter("vendorInstanceId");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>

var isValidName;

var jVendorNames = new Array();
var jVendorInstanceIds = new Array();
var count=0;
$(document).ready(function() {
	var vendorId='<%=vendorInstanceId%>';
	var clientTypeId='<%=clientTypeId%>';
});
function validateCreate()
{
	 var supportedVendorList=document.getElementById("selectedSupportedVendorIds");
	 selectAll(supportedVendorList);
	
	if(isNull(document.forms[0].profileName.value)){
		document.forms[0].profileName.focus();
		alert('Client Profile Name must be specified');
	}else if(!isValidName) {
		alert('Enter Valid Client Profile Name');
		document.forms[0].profileName.focus();
		return;
	}else if(document.forms[0].clientTypeId.value=="0"){
		document.forms[0].clientTypeId.focus();
		alert('Client Type must be selected.');
	}else if(document.forms[0].vendorInstanceId.value=="0"){
        document.forms[0].vendorInstanceId.focus();
        alert('Vendor Name must be selected.');
	}else{
		var verifyDHCP,verifyHA;
		verifyDHCP=document.forms[0].dhcpAddress.value;
		verifyHA=document.forms[0].haAddress.value;
		if(!(verifyDHCP == "0.0.0.0" || verifyDHCP == "254.255.255.254" || verifyDHCP == "")){
			var varifyIpAddress=validateIP(verifyDHCP);
			if(varifyIpAddress == false){
				  alert('Enter Valid DHCP Address');
				  document.forms[0].dhcpAddress.focus();
				  return;
			}
		} 
		if(!(verifyHA == "")){
			var varifyIpAdd=validateIP(verifyHA);
			if(varifyIpAdd == false){
				  alert('Enter Valid HA Address');
				  document.forms[0].haAddress.focus();
				  return;
			}
		}
		
		$('#clientPolicy').val(jQuery.trim($('#clientPolicy').val()));
		$('#hotlinePolicy').val(jQuery.trim($('#hotlinePolicy').val()));
		
		if(!isEmpty($("#dynaAuthPort").val())){
			var verifyPort=validatePortNumber($("#dynaAuthPort").val());
			if(verifyPort==false){
				alert('Please verify DynaAuth Port !!');
				document.forms[0].dynaAuthPort.focus();
				return;
			}
		}
		
	     document.forms[0].submit();
    }
}	
function validatePortNumber(txt){
	// check for valid numeric port	 
	if(IsNumeric(txt) == true){
		if(txt >= 0 && txt<=65535){
			return(true);
		}else{
			return(false);
		}		
	}else{
		return(false);
	}
}
function validateIP(ipaddress){
	var ip=/((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
		if(ip.test(ipaddress)){
			   return true;
		   }else{
			   return false;
		   }
}
function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}


function popup(){

	document.getElementById("popupContact").style.visibility = "visible";		
	$("#popupContact").dialog({
		modal: true,
		autoOpen: false,		
		height: 260,
		width: 500,
		buttons:{					
			        'Add': function() {

                        var selectedItems=document.getElementsByName("vendorDataCheckBoxId");                        
                        $(this).dialog('close');

                        
                        for(var i=0;i<=selectedItems.length;i++)
                        {
							if(selectedItems[i].checked == true)
                            {   
                                var labelVal=$("#"+jVendorInstanceIds[i]).val();
                               	$("#selectedSupportedVendorIds").append("<option id="+ jVendorInstanceIds[i] +" value="+ jVendorInstanceIds[i] +" class=labeltext> "+labelVal+" </option>");
								$("#row"+jVendorInstanceIds[i]).attr("style","display:none");							
								selectedItems[i].checked=false;
                               	
                            } 
                            
                        }
					},                
					Cancel: function() {
					$(this).dialog('close');
					}
				},
		open: function() {
		
		},
		close: function() {
			
		}				
		});
		$("#popupContact").dialog("open");
 }





$(document).ready(function() {		
		
		jVendorNames.length = <%=lstVendorDataTemp.size()%>						
		jVendorInstanceIds.length= <%=lstVendorDataTemp.size()%>
			
		<%int j =0;				
		for(j =0;j<len;j++){%>								
	
		jVendorNames[<%=j%>] = '<%=vendorNames[j]%>'
	    jVendorInstanceIds[<%=j%>] = '<%=vendorInstanceIds[j]%>'	
			count ++;							
		<%}%>	 	 
		
	 
		var table = document.getElementById("suppvendortableid");
	
		for(var i = 0;i<count;i++){
			
			var row = table.insertRow(i);
			row.id = "row" + jVendorInstanceIds[i];
			row.style.visibility = 'visible';
	            
			    var cell1 = row.insertCell(0);
			    var element1 = document.createElement("input");
			    element1.type = "checkbox";
			    element1.name = "vendorDataCheckBoxId"
			    element1.id = jVendorInstanceIds[i];
			    element1.value = jVendorNames[i];
			    
			    cell1.appendChild(element1);
			   
			    var cell2 = row.insertCell(1);
			    cell2.innerHTML = jVendorNames[i];
			    cell2.className="labeltext";
	
		}
	
	       }
);


function removeFromSelectedVendors(){

	
	$("#selectedSupportedVendorIds option:selected").each(function(){
	     var rowid="#row"+$(this).val();
	     $(rowid).removeAttr("style");
	     $(this).remove();				
      
    });
	
	
 }


function selectAll(selObj){
	for(var i=0;i<selObj.options.length;i++){
		selObj.options[i].selected = true;
	}
}

function setColumnsOnUserIdAttrTextFields(){
	var userIdAttrVal = document.getElementById("userIdentities").value;
	retriveRadiusDictionaryAttributes(userIdAttrVal,"userIdentities");
}

function verifyName() {
	var searchName = document.getElementById("profileName").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.TRUSTED_CLIENT_PROFILE%>',searchName,'create','','verifyNameDiv');
}
setTitle('Client Profile');
</script>

<html:form action="/createClientProfile">
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header">Create Client Profile</td>
								</tr>

								<tr>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td valign="middle" colspan="2">
										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="30%">

											<tr>
												<td align="left" class="captiontext" valign="top" wsidth="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.clientprofilename" /> 
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.name" header="radius.clientprofile.clientprofilename"/>
												</td>	
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="profileName" tabindex="1"
														property="profileName" onkeyup="verifyName();" size="25"
														maxlength="50" style="width:250px" /> <font
													color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.description" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.description" header="radius.clientprofile.description"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:textarea styleId="description" tabindex="2"
														property="description" cols="50" rows="4"
														style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.clienttypename" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.type" header="radius.clientprofile.clienttypename"/>												
												</td>
												<td align="left" class="labeltext" valign="top"><bean:define
														id="lstClientType" name="createClientProfileForm"
														property="lstClientType"></bean:define> <html:select
														name="createClientProfileForm" tabindex="3"
														styleId="clientTypeId" property="clientTypeId" size="1"
														style="width:130px">
														<html:option value="0">--Select--</html:option>
														<html:options collection="lstClientType"
															property="clientTypeId" labelProperty="clientTypeName" />
													</html:select><font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.vendorname" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.vendor" header="radius.clientprofile.vendorname"/>												
												</td>
												<td align="left" class="labeltext" valign="top"><bean:define
														id="lstVendorData" name="createClientProfileForm"
														property="lstVendorData"></bean:define> <html:select
														name="createClientProfileForm" tabindex="4"
														styleId="vendorInstanceId" property="vendorInstanceId"
														size="1" style="width:130px">
														<html:option value="">--Select--</html:option>
														<html:options collection="lstVendorData"
															property="vendorInstanceId" labelProperty="vendorName" />
													</html:select><font color="#FF0000"> *</font></td>
											</tr>



											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.dnslist" /> 
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.dnslist" header="radius.clientprofile.dnslist"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text tabindex="5" styleId="dnsList"
														property="dnsList" size="25" maxlength="50"
														style="width:250px" />

												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.useridentity" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.useridentity" header="radius.clientprofile.useridentity"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<%-- 	<html:text styleId="userIdentities" property="userIdentities" size="25" maxlength="50" />  --%>
													<input type="text" tabindex="6" name="userIdentities"
													id="userIdentities" size="30" autocomplete="off"
													onkeyup="setColumnsOnUserIdAttrTextFields();"
													style="width: 250px" />
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top"  width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.prepaidstandard" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.prepaidstd" header="radius.clientprofile.prepaidstandard"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="prepaidStandard" tabindex="7"
														property="prepaidStandard" size="25" maxlength="50"
														style="width:250px" />

												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.clientpolicy" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.policy" header="radius.clientprofile.clientpolicy"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="clientPolicy" tabindex="8"
														property="clientPolicy" size="25" maxlength="50"
														style="width:250px" />

												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top"
													width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.hotlinepolicy" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.hotlinepolicy" header="radius.clientprofile.hotlinepolicy"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="hotlinePolicy" tabindex="9"
														property="hotlinePolicy" size="25" maxlength="50"
														style="width:250px" />
												</td>
											</tr>

											<%-- tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										Framed Pool
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="clientprofile.framedpool"/>','Framed Pool')"/>										
		
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" >
										<html:text styleId="framedPool" property="framedPool" size="25" maxlength="50" />
										
									</td>
								</tr--%>

											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.dhcpaddress" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.dhcpaddress" header="radius.clientprofile.dhcpaddress"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="dhcpAddress" tabindex="10"
														property="dhcpAddress" size="25" maxlength="50"
														style="width:250px" />
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.haaddress" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.haaddress" header="radius.clientprofile.haaddress"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="haAddress" tabindex="11"
														property="haAddress" size="25" maxlength="50"
														style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.multiclassattrib" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.multiclassattrib" header="radius.clientprofile.multiclassattrib"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:select name="createClientProfileForm" tabindex="12"
														styleId="multipleClassAttribute"
														property="multipleClassAttribute" size="1"
														style="width:130px">
														<html:option value="YES">Yes</html:option>
														<html:option value="NO">No</html:option>
													</html:select>
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top"></td>
												<td align="left" class="labeltext" valign="top" width="2%">
													<html:checkbox property="filterUnsupportedVsa"
														tabindex="13" styleId="filterUnsupportedVsa"></html:checkbox>
													Filter Unsupported VSA
												</td>
											</tr>

											<!-- 		
						<tr>
				    
							<td align="left" class="labeltext" valign="top" width="15%">
							Supported Vendor List
							</td>
							<td align="left" class="labeltext" valign="top">
							    <html:select name="createClientProfileForm" property="selectedSupportedVendorIdList" multiple="true">
								  <html:optionsCollection name="createClientProfileForm" property="lstVendorData" value="vendorInstanceId" label="vendorName"/>
								</html:select>
							</td>
							
						
					  </tr>
					  
					   -->

											<tr>

												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.supportedvendorlist" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.vendorlist" header="radius.clientprofile.supportedvendorlist"/>
												</td>
												<td width="2%" align="left" class="labeltext" valign="top">
												<select multiple="multiple" tabindex="14"
													name="selectedSupportedVendorIds"
													id="selectedSupportedVendorIds"
													style="height: 100px; width: 250px">
												</select></td>

												<td align="left" class="labeltext" valign="top" width="32%"
													style="padding-top: 25px"><input type="button" value="Add "
													tabindex="15" onClick="popup()" class="light-btn"
													style="width: 75px" /><br /> <br /> <input type="button"
													value="Remove " tabindex="16"
													onclick="removeFromSelectedVendors()" class="light-btn"
													style="width: 75px" /></td>

											</tr>
											<tr>
												<td colspan="3" class="tblheader-bold" valign="top" align="left">
													<bean:message bundle="radiusResources" key="radius.clientprofile.dynaauthconfig"/>
												</td>
											</tr>
											
											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.port"/>
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.port" header="radius.clientprofile.port"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="dynaAuthPort" tabindex="17" property="dynaAuthPort" size="5" maxlength="5" style="width:50px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.coasupportedattribute"/>
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.coasupportedattribute" 
													header="radius.clientprofile.coasupportedattribute"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="coaSupportedAttributes" tabindex="18" property="coaSupportedAttributes" size="25" maxlength="50" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.coaunsupportedattribute"/>
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.coaunsupportedattribute" 
													header="radius.clientprofile.coaunsupportedattribute"/>
												</td>	
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="coaUnsupportedAttributes" tabindex="19" property="coaUnsupportedAttributes" size="25" maxlength="50" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.dmsupportedattribute"/>
													<ec:elitehelp headerBundle="radiusResources" text="clientprofile.dmsupportedattribute" 
													header="radius.clientprofile.dmsupportedattribute"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="dmSupportedAttributes" tabindex="20" property="dmSupportedAttributes" size="25" maxlength="50" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="15%">
													<bean:message bundle="radiusResources" key="radius.clientprofile.dmunsupportedattribute"/>
													<ec:elitehelp headerBundle="radiusResources" 
													text="clientprofile.dmunsupportedattribute" 
													header="radius.clientprofile.dmunsupportedattribute"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="dmUnsupportedAttributes" tabindex="21" property="dmUnsupportedAttributes" size="25" maxlength="50" style="width:250px" />
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" tabindex="22" name="c_btnCreate"
													onclick="validateCreate()" id="c_btnCreate2" value="Create"
													class="light-btn"> <input type="reset"
													tabindex="23" name="c_btnCancel"
													onclick="javascript:location.href='<%=basePath%>/initSearchClientProfile.do?/>'"
													value="Cancel" class="light-btn"></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
	<div id="popupContact" style="display: none;"
		title="Add Supported Vendors">

		<table id="suppvendortableid" name="suppvendortableid">
		</table>
	</div>
</html:form>