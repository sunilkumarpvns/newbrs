<%@page import="com.elitecore.elitesm.web.radius.clientprofile.forms.UpdateClientProfileForm"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData"%>

<% 
UpdateClientProfileForm updateClientForm= (UpdateClientProfileForm)request.getAttribute("updateClientProfileForm");
RadiusClientProfileData radiusClientProfileData = (RadiusClientProfileData)request.getAttribute("radiusClientProfileData");
List<VendorData> lstVendorDataTemp = updateClientForm.getLstVendorData();
int len=lstVendorDataTemp.size();
String[]  vendorNames = new String[len];
String[]  vendorInstanceIds = new String[len];
for(int i=0;i<len;i++){
	   
	   VendorData vendorData=lstVendorDataTemp.get(i);
	   vendorNames[i]=vendorData.getVendorName();
	   vendorInstanceIds[i]=vendorData.getVendorInstanceId();
	   
}

List<VendorData> supportedVendors=(List<VendorData>)request.getAttribute("supportedVendorLstBean");
int selLen=supportedVendors.size();
%>

<script>
var isValidName;

var jVendorNames = new Array();
var jVendorInstanceIds = new Array();
var count=0;

function validateUpdate()
{
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
		 var supportedVendorList=document.getElementById("selectedSupportedVendorIds");
		 selectAll(supportedVendorList);
	     document.forms[0].submit();
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
		
		var supportedVendorList=document.getElementById("selectedSupportedVendorIds");
		hideSelectedVendors();
	
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

function hideSelectedVendors(){

	$("#selectedSupportedVendorIds option").each(function(){
		
	     var rowid="#row"+$(this).val();
	     $(rowid).attr("style","display:none");							
     
   });	
}

function verifyName() {
	var searchName = document.getElementById("profileName").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.TRUSTED_CLIENT_PROFILE%>',searchName,'update','<%=radiusClientProfileData.getProfileId()%>','verifyNameDiv');
}
</script>

<html:form action="/updateClientProfile">

	<html:hidden name="updateClientProfileForm" styleId="action" property="action" value="update" />
	<html:hidden name="updateClientProfileForm" styleId="profileId" property="profileId" />
	<html:hidden name="updateClientProfileForm" styleId="viewType" property="viewType" value="basic" />
	<html:hidden name="updateClientProfileForm" styleId="auditUId" property="auditUId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%" align="left">
		<tr>
			<td valign="middle" colspan="2">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%" class="box">
					<tr>
						<td class="tblheader-bold" colspan="3"><bean:message
								bundle="radiusResources"
								key="radius.clientprofile.updatebasicdetails" /></td>
					</tr>
					<tr>
						<td class="small-gap" colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="24%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.clientprofilename" /> 
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.name" header="radius.clientprofile.clientprofilename"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text tabindex="1" name="updateClientProfileForm"
								styleId="profileName" onkeyup="verifyName();"
								property="profileName" size="25" maxlength="50"
								style="width:250px" /> <font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div> &nbsp;
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" valign="top" width="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.description" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.description" header="radius.clientprofile.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:textarea styleId="description" tabindex="2"
								property="description" cols="50" rows="4" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.clienttypename" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.type" header="radius.clientprofile.clienttypename"/>	
						</td>
						<td align="left" class="labeltext" valign="top"><bean:define
								id="lstClientType" name="updateClientProfileForm"
								property="lstClientType"></bean:define> <html:select
								tabindex="3" name="updateClientProfileForm"
								styleId="clientTypeId" property="clientTypeId" size="1"
								style="width: 130px;">
								<html:option value="0">--Select--</html:option>
								<html:options collection="lstClientType" property="clientTypeId"
									labelProperty="clientTypeName" />
							</html:select><font color="#FF0000"> *</font></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.vendorname" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.vendor" header="radius.clientprofile.vendorname"/>
						</td>
						<td align="left" class="labeltext" valign="top"><bean:define
								id="lstVendorData" name="updateClientProfileForm"
								property="lstVendorData"></bean:define> <html:select
								name="updateClientProfileForm" tabindex="4"
								styleId="vendorInstanceId" property="vendorInstanceId" size="1"
								style="width: 130px;">
								<html:option value="">--Select--</html:option>
								<html:options collection="lstVendorData"
									property="vendorInstanceId" labelProperty="vendorName" />
							</html:select><font color="#FF0000"> *</font></td>
					</tr>
					<tr>

						<td align="left" class="captiontext" valign="top" width="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.supportedvendorlist" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.vendorlist" header="radius.clientprofile.supportedvendorlist"/>
						</td>
						<td width="10%" align="left" class="labeltext" valign="top"
							width="32%"><select multiple="multiple" tabindex="5"
							name="selectedSupportedVendorIds" id="selectedSupportedVendorIds"
							style="height: 100px; width: 250px">
								<%
								for(int l=0;l<selLen;l++){
	                            VendorData vendorData=supportedVendors.get(l); %>

								<option id="<%=vendorData.getVendorInstanceId()%>"
									value="<%=vendorData.getVendorInstanceId()%>" class="labeltext"><%=vendorData.getVendorName()%></option>

								<%} %>
						</select></td>



						<td align="left" class="labeltext" valign="top" width="32%" style="padding-top: 25px">
							<input type="button" tabindex="6" value="Add " onClick="popup()"
							class="light-btn" style="width: 75px;" /><br /> <br /> <input
							type="button" tabindex="7" value="Remove "
							onclick="removeFromSelectedVendors()" class="light-btn"
							style="width: 75px;" />
						</td>

					</tr>


					<tr>
						<td align="left" class="labeltext" valign="top">&nbsp;</td>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>
					

					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2"><input
							type="button" tabindex="8" name="c_btnCreate"
							onclick="validateUpdate()" id="c_btnCreate2" value="Update"
							class="light-btn"> <input type="reset" tabindex="9"
							name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/viewClientProfile.do?viewType=basic&profileId=<%=updateClientForm.getProfileId()%>'"
							value="   Cancel   " class="light-btn" />
							<div id="popupContact" style="display: none;"
								title="Add Supported Vendors">
								<table id="suppvendortableid" name="suppvendortableid">
								</table>
							</div></td>

					</tr>

				</table>
			</td>
		</tr>
	</table>
</html:form>

