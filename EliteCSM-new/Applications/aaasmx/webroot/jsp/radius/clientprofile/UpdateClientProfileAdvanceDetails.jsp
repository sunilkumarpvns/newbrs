<%@page import="com.elitecore.elitesm.web.radius.clientprofile.forms.UpdateClientProfileForm"%>

<%
UpdateClientProfileForm updateClientProfileForm = (UpdateClientProfileForm)request.getAttribute("updateClientProfileForm");
%>
<script>

function validateUpdate()
{
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
function setColumnsOnUserIdAttrTextFields(){
	var userIdAttrVal = document.getElementById("userIdentities").value;
	retriveRadiusDictionaryAttributes(userIdAttrVal,"userIdentities");
}
</script>

<html:form action="/updateClientProfile">

	<html:hidden name="updateClientProfileForm" styleId="action" property="action" value="update" />
	<html:hidden name="updateClientProfileForm" styleId="profileId" property="profileId" />
	<html:hidden name="updateClientProfileForm" styleId="viewType" property="viewType" value="other" />
	<html:hidden name="updateClientProfileForm" styleId="auditUId" property="auditUId" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="middle" colspan="2">

				<table cellpadding="0" cellspacing="0" border="0" width="100%">

					<tr>
						<td class="tblheader-bold" colspan="3">
							<bean:message bundle="radiusResources" key="radius.clientprofile.updateadvancedetails" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.dnslist" /> 
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.dnslist" header="radius.clientprofile.dnslist"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text tabindex="1" styleId="dnsList" property="dnsList" size="25" maxlength="50" style="width:219px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.useridentity" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.useridentity" header="radius.clientprofile.useridentity"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<input type="text" tabindex="2" name="userIdentities"
							id="userIdentities" size="30"
							value="<%=(updateClientProfileForm.getUserIdentities() != null ) ? updateClientProfileForm.getUserIdentities() : ""%>"
							autocomplete="off" onkeyup="setColumnsOnUserIdAttrTextFields();"
							style="width: 219px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.prepaidstandard" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.prepaidstd" header="radius.clientprofile.prepaidstandard"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="prepaidStandard" tabindex="3"
								property="prepaidStandard" size="25" maxlength="50"
								style="width:219px" />

						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.clientpolicy" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.policy" header="radius.clientprofile.clientpolicy"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="clientPolicy" tabindex="4"
								property="clientPolicy" size="25" maxlength="50"
								style="width:219px" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.hotlinepolicy" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.hotlinepolicy" header="radius.clientprofile.hotlinepolicy"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="hotlinePolicy" tabindex="5"
								property="hotlinePolicy" size="25" maxlength="50"
								style="width:219px" />

						</td>
					</tr>

					<%-- tr>
									<td align="left" class="labeltext" valign="top" width="14%">
										Framed Pool
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" >
										<html:text styleId="framedPool" property="framedPool" size="25" maxlength="50" />
										
									</td>
								</tr--%>

					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.dhcpaddress" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.dhcpaddress" header="radius.clientprofile.dhcpaddress"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="dhcpAddress" tabindex="6"
								property="dhcpAddress" size="25" maxlength="50"
								style="width:219px" />

						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.haaddress" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.haaddress" header="radius.clientprofile.haaddress"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="haAddress" tabindex="7" property="haAddress"
								size="25" maxlength="50" style="width:219px" />

						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.multiclassattrib" />
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.multiclassattrib" header="radius.clientprofile.multiclassattrib"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:select styleId="multipleClassAttribute" tabindex="8"
								property="multipleClassAttribute" size="1" style="width: 120px;">
								<html:option value="YES">Yes</html:option>
								<html:option value="NO">No</html:option>
							</html:select>
						</td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top"></td>
						<td align="left" class="labeltext" valign="top" width="32%">
							<html:checkbox tabindex="9" property="filterUnsupportedVsa"
								styleId="filterUnsupportedVsa"></html:checkbox> Filter
							Unsupported VSA
						</td>
					</tr>
					<tr>
						<td colspan="3" class="tblheader-bold" valign="top" align="left">
							<bean:message bundle="radiusResources" key="radius.clientprofile.dynaauthconfig"/>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.port"/>
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.port" header="radius.clientprofile.port"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<logic:equal value="0" property="dynaAuthPort" name="updateClientProfileForm">
								<html:text styleId="dynaAuthPort" tabindex="10" property="dynaAuthPort" size="5" maxlength="5" style="width:50px" value=""/>
							</logic:equal>
							<logic:notEqual value="0" property="dynaAuthPort" name="updateClientProfileForm">
								<html:text styleId="dynaAuthPort" tabindex="10" property="dynaAuthPort" size="5" maxlength="5" style="width:50px" />
							</logic:notEqual>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.coasupportedattribute"/>
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.coasupportedattribute" 
							header="radius.clientprofile.coasupportedattribute"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="coaSupportedAttributes" tabindex="11" property="coaSupportedAttributes" size="25" maxlength="50" style="width:219px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.coaunsupportedattribute"/>
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.coaunsupportedattribute" 
							header="radius.clientprofile.coaunsupportedattribute"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="coaUnsupportedAttributes" tabindex="12" property="coaUnsupportedAttributes" size="25" maxlength="50" style="width:219px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.dmsupportedattribute"/>
							<ec:elitehelp headerBundle="radiusResources" text="clientprofile.dmsupportedattribute" 
							header="radius.clientprofile.dmsupportedattribute"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="dmSupportedAttributes" tabindex="13" property="dmSupportedAttributes" size="25" maxlength="50" style="width:219px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="14%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.dmunsupportedattribute"/>
							<ec:elitehelp headerBundle="radiusResources" 
							text="clientprofile.dmunsupportedattribute" 
							header="radius.clientprofile.dmunsupportedattribute"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="dmUnsupportedAttributes" name="updateClientProfileForm" tabindex="14" property="dmUnsupportedAttributes" size="25" maxlength="50" style="width:219px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top">&nbsp;</td>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>
					<tr>

						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2"><input
							type="button" name="c_btnCreate" tabindex="15"
							onclick="validateUpdate()" id="c_btnCreate2" value="Update"
							class="light-btn"> <input type="reset"
							name="c_btnDeletePolicy" tabindex="16"
							onclick="javascript:location.href='<%=basePath%>/viewClientProfile.do?viewType=basic&profileId=<%=updateClientProfileForm.getProfileId()%>'"
							value="   Cancel   " class="light-btn" /></td>

					</tr>

				</table>
			</td>
		</tr>
	</table>
</html:form>

