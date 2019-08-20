
<script>
	function validateForm(){
		if($("#eapConfigId").val() == '0'){
			alert("EAP Configuration must be specified");
			$("#eapConfigId").focus();
			return;
		}
		if(isEmpty($("#multipleUserIdentity").val())){
			alert("User Identity Attribute must be specified");
			$("#multipleUserIdentity").focus();
			return;
		}
		document.forms[0].submit();
	}
	
	function setColumnsOnMultipleUIdTextFields(){
 		var multipleUserIdVal = document.getElementById("multipleUserIdentity").value;
 		retriveRadiusDictionaryAttributes(multipleUserIdVal,"multipleUserIdentity");
 	}
	
	function setStripUserIdentity(){
 		var stripUserCheckBox = document.getElementById("stripUserCheckBox");
 		var realmPatternComp = document.getElementById("realmPattern");
 		var separatorComp = document.getElementById("realmSeparator");
 		if(stripUserCheckBox.checked ==true){
 			realmPatternComp.disabled=false;
 			separatorComp.disabled=false;
 		}else{
 			realmPatternComp.disabled=true;
 			separatorComp.disabled=true;
 		}
 	}
</script>

<html:form action="/updateEAPPolicyAuthenticationDetail">
	<html:hidden name="updateEAPPolicyForm" styleId="action" property="action" value="update"/>
	<html:hidden name="updateEAPPolicyForm" styleId="policyId" property="policyId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="auditUId" property="auditUId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="name" property="name"/>
	<table  width="100%" >
		<tr>
			<td  class="tblheader-bold" valign="top" colspan="4">
				Update <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.authenticationdetails"/>
			</td>									
		</tr>						
				
		<tr>
			<td class="captiontext">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.eapconfig"/>
				<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.eapconfig" header="servicepolicy.eappolicy.eapconfig"/>
			</td>	
			<td align="left" class="labeltext" valign="top" colspan="3">
				<html:select property="eapConfigId" styleClass="labeltext" styleId="eapConfigId" style="width:130px">
				<html:option value="0">--select--</html:option>
					<html:optionsCollection name="updateEAPPolicyForm" property="eapConfigurationList"  label="name" value="eapId"/>											
				</html:select>
				<font color="#FF0000"> *</font>	
			</td>
		</tr>
																		
		<tr>
			<td align="left" class="captiontext" valign="top" width="29%" >										
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.multipleuid"/>
				<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.usridentity" header="servicepolicy.eappolicy.multipleuid"/>
			</td>
			<td align="left" class="labeltext" valign="top" colspan="3">											
				<html:text property="multipleUserIdentity" styleId="multipleUserIdentity" size="30" onkeyup="setColumnsOnMultipleUIdTextFields();" maxlength="250" style="width:250px;" /><font color="#FF0000"> *</font>
				<!-- <input type="text" name="multipleUserIdentity" id="multipleUserIdentity" size="30" autocomplete="off" onkeyup="setColumnsOnMultipleUIdTextFields();" style="width:250px" value="0:1"/>	
				<font color="#FF0000"> *</font>	 -->																
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top" >											
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.eaphpolicy.casesensitiveuid"/>
				<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.case" header="servicepolicy.eaphpolicy.casesensitiveuid"/>
			</td>
			<td align="left" class="labeltext" valign="top" colspan="3">
				<html:select property="caseSensitiveUserIdentity"  styleClass="labeltext" styleId="caseSensitiveUserIdentity" style="width:130px">
					<html:option value="1">No Change</html:option>
					<html:option value="2">Lower Case</html:option>											
					<html:option value="3">Upper Case</html:option>
				</html:select>											
			</td>
		</tr>
		
		<tr>									  
			<td align="left" class="captiontext" valign="top" >													
				<bean:message bundle="servicePolicyProperties" key="update.user.identity"/>
				<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.useridentity" header="update.user.identity"/>
			</td>											
			<td class="labeltext" colspan="3" valign="top"> 										
        		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="box">
        			<tr>
        				<td class="labeltext">   
        					<html:checkbox property="stripUserIdentity" styleId="stripUserCheckBox" value="true" onclick="setStripUserIdentity()"></html:checkbox>
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.stripuid"/>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.realmseparator" />
							<html:text property="realmSeparator" size="5" maxlength="1" styleId="realmSeparator"/>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        					<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.realmpattern" />
							<html:select property="realmPattern"  styleClass="labeltext" styleId="realmPattern">
        						<html:option value="prefix">Prefix</html:option>
        						<html:option value="suffix">Suffix</html:option>                                            
        					</html:select>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<html:checkbox property="trimUserIdentity" value="true" styleId="trimUserIdentityCheckBox"></html:checkbox>
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.trimuid"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;																							
							<html:checkbox property="trimPassword" value="true" styleId="trimPasswordCheckBox"></html:checkbox>
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.trimpassword"/>												
						</td>
					</tr>
				</table>
			</td>	
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top" width="30%">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.anonymousidentity" /> 
				<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.anonymousidentity" header="servicepolicy.eappolicy.anonymousidentity"/>
			</td>                   
			<td align="left" class="labeltext" valign="top" colspan="2">
				<html:text property="anonymousProfileIdentity" styleId="anonymousProfileIdentity" style="width:250px;"></html:text>
			</td>
		</tr>
		<tr> 
			<td colspan="2" class="small-gap">&nbsp;</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle">&nbsp;</td>
			<td class="btns-td" valign="middle" colspan="2">
				<input type="button" name="c_btnUpdate" id="c_btnUpdate" value="Update" class="light-btn" onclick="validateForm()"> 
				<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchEAPPolicy.do?'" value="Cancel" class="light-btn">
			</td>
		</tr>
	</table>
</html:form>	