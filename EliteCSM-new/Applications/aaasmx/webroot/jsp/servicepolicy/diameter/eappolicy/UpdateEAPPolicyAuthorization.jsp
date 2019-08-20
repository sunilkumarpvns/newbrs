<script type="text/javascript">
function validateForm(){
	if( $('#diameterConcurrency').val() == '0' && $('#additionalDiameterConcurrency').val() != '0'){
		alert('Diameter Concurrency must be specified');
		$('#diameterConcurrency').focus();
	}else if( !(isNull( $('#defaultSessionTimeout').val() )) && isNaN($('#defaultSessionTimeout').val()) ){
 		alert('Default Session Timeout must be numeric ');
 		$('#defaultSessionTimeout').focus();
 		return;
	}else if( !checkForDot() ){
		$('#defaultSessionTimeout').focus();
		return;
	}else{
		document.forms[0].submit();
	}
}

function checkForDot(){
	if( !(isNull( $('#defaultSessionTimeout').val() ))) {
		var defaultValue = $('#defaultSessionTimeout').val();
		if (defaultValue.indexOf(".") !== -1) {
			alert('Floating point value not allowed in Default Session Timeout ');
			return false;
		}
		return true;
	}else{
		return true;
	}
}

function setDiameterPolicyData(obj){
	if ($(obj).attr('checked')) {
		$(obj).val('true');
	} else {
		$(obj).val('false');
	}
}
</script>

<html:form action="/updateEAPPolicyAuthorizationDetail">
	<html:hidden name="updateEAPPolicyForm" styleId="action" property="action" value="update"/>
	<html:hidden name="updateEAPPolicyForm" styleId="policyId" property="policyId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="auditUId" property="auditUId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="name" property="name"/>
<table width="100%">
<tr>
	<td align="left" class="tblheader-bold" valign="top" colspan="4">
		Update <bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.authorizationdetails" />
	</td>
</tr>
<tr>
	<td align="left" class="captiontext" valign="top" nowrap="nowrap">
		<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.wimax" /> 
		<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.wimax" header="servicepolicy.eappolicy.wimax"/>
	</td>
	<td align="left" class="labeltext" valign="top" colspan="3">
		<html:select property="wimax" styleClass="labeltext" styleId="wimax" tabindex="18">
			<html:option value="true">Enabled</html:option>
			<html:option value="false">Disabled</html:option>
		</html:select>
	</td>
</tr>						
<tr>
	<td align="left" class="captiontext" valign="top" width="29%">											
		Diameter Policy 
	</td>
	<td align="left" class="labeltext" valign="top" colspan="3">
		<table width="100%" class="box" >
			<tr>
				<td class="labeltext" width="40%">
					<html:checkbox property="rejectOnCheckItemNotFound" styleId="rejectOnCheckItemNotFound" onclick="setDiameterPolicyData(this);">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.rejectoncheckitemnotfound" />
						<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.rejectoncheck" header="servicepolicy.eappolicy.rejectoncheckitemnotfound"/>
					</html:checkbox>
				</td>
				<td></td>
			</tr>
			<tr>
				<td class="labeltext">
					<html:checkbox property="rejectOnRejectItemNotFound" styleId="rejectOnRejectItemNotFound" onclick="setDiameterPolicyData(this);">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.rejectonrejectitemnotfound" />
						<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.rejectonreject" header="servicepolicy.eappolicy.rejectonrejectitemnotfound"/>
					</html:checkbox>
				</td>
				<td></td>
			</tr>
			<tr>
				<td class="labeltext">
					<html:checkbox property="actionOnPolicyNotFound" styleId="actionOnPolicyNotFound" onclick="setDiameterPolicyData(this);">
						<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.acceptonpolicynotfound" />
						<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.actionpolicy" header="servicepolicy.eappolicy.acceptonpolicynotfound"/>
					</html:checkbox>
				</td>
				<td></td>
			</tr>
		</table>											
	</td>
</tr>
<tr>
	<td align="left" class="captiontext" valign="top"  nowrap="nowrap">
		<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.gracepolicy" /> 
		<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.gracepolicy" header="servicepolicy.eappolicy.gracepolicy"/>
	</td>
	<td align="left" class="labeltext" valign="top" colspan="3">
		<html:select property="gracePolicy" styleClass="labeltext" styleId="gracePolicy" tabindex="18" style="width:175px;">
			<html:option value="">--select--</html:option>
				<logic:iterate id="gracePolicyInst" name="updateEAPPolicyForm" property="gracePolicyList" type="com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData">
					<html:option value="<%=gracePolicyInst.getName()%>"><%=gracePolicyInst.getName()%></html:option>
				</logic:iterate>
		</html:select>
	</td>
</tr>
<tr>
	<td align="left" class="captiontext" valign="top"  nowrap="nowrap">
		<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.diameterconcurrency" /> 
		<ec:elitehelp  header="servicepolicy.eappolicy.diameterconcurrency" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.diameterconcurrency" ></ec:elitehelp>
	</td>
	<td align="left" class="labeltext" valign="top" colspan="3">
		<html:select property="diameterConcurrency" styleClass="labeltext" styleId="diameterConcurrency" tabindex="18" style="width:175px;">
			<html:option value="0">--select--</html:option>
				<logic:iterate id="diameterConcurrencyInst" name="updateEAPPolicyForm" property="diameterConcurrencyDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData">
					<html:option value="<%=String.valueOf(diameterConcurrencyInst.getDiaConConfigId())%>"><%=diameterConcurrencyInst.getName()%></html:option>
				</logic:iterate>
		</html:select>
	</td>
</tr>
<tr>
	<td align="left" class="captiontext" valign="top"  nowrap="nowrap">
		<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.additionaldiameterconcurrency" /> 
		<ec:elitehelp  header="servicepolicy.eappolicy.additionaldiameterconcurrency" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.additionaldiameterconcurrency" ></ec:elitehelp>
	</td>
	<td align="left" class="labeltext" valign="top" colspan="3">
		<html:select property="additionalDiameterConcurrency" styleClass="labeltext" styleId="additionalDiameterConcurrency" tabindex="18" style="width:175px;">
			<html:option value="0">--select--</html:option>
				<logic:iterate id="diameterConcurrencyInst" name="updateEAPPolicyForm" property="diameterConcurrencyDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData">
					<html:option value="<%=String.valueOf(diameterConcurrencyInst.getDiaConConfigId())%>"><%=diameterConcurrencyInst.getName()%></html:option>
				</logic:iterate>
		</html:select>
	</td>
</tr>

<tr>
	<td align="left" class="captiontext" valign="top"  nowrap="nowrap">
		<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.defaultsessiontimeout" /> 
		<ec:elitehelp  header="servicepolicy.eappolicy.defaultsessiontimeout" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.defaultsessiontimeout" ></ec:elitehelp>
	</td>
	<td align="left" class="labeltext" valign="top" colspan="3">
		<html:text styleId="defaultSessionTimeout" property="defaultSessionTimeout" name="updateEAPPolicyForm" maxlength="10" style="width:175px;"/>
	</td>
</tr>

<tr> 
	<td colspan="2" class="small-gap">&nbsp;</td>
</tr>

<tr>
	<td class="btns-td" valign="middle">&nbsp;</td>
	<td class="btns-td" valign="middle" colspan="2">
		<input type="button" name="c_btnUpdate" id="c_btnUpdate" value="Update" class="light-btn"  onclick="validateForm()"> 
		<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchEAPPolicy.do?'" value="Cancel" class="light-btn">
	</td>
</tr>

</table>
</html:form>							