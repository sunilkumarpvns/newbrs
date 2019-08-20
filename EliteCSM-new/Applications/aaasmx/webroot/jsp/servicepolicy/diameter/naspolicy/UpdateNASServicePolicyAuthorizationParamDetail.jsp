<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyAuthorizationParamsForm"%>
<script>
function validate(){
	if( $('#diameterConcurrency').val() == '0' && $('#additionalDiameterConcurrency').val() != '0'){
		alert('Diameter Concurrency must be specified');
		$('#diameterConcurrency').focus();
	}else if( !(isNull( $('#defaultSessionTimeout').val() )) && isNaN($('#defaultSessionTimeout').val()) ){
		alert('Default Session Timeout must be numeric ');
		$('#defaultSessionTimeout').focus();
		return;
	}else if( !checkForDot() ){
		$('#defaultSessionTimeout').focus();
		return false;
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
</script>
<%
	UpdateNASServicePolicyAuthorizationParamsForm nasPolicyForm = (UpdateNASServicePolicyAuthorizationParamsForm) request.getAttribute("nasPolicyForm");;
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<html:form action="/updateNASServicePolicyAuthorizationParamDetail">
<html:hidden name="updateNASServicePolicyAuthorizationParamsForm" styleId="nasPolicyId" property="nasPolicyId"/>
<html:hidden name="updateNASServicePolicyAuthorizationParamsForm" styleId="action" property="action" value = "update"/>	  
<html:hidden name="updateNASServicePolicyAuthorizationParamsForm" styleId="auditUId" property="auditUId"/>	 
<html:hidden name="updateNASServicePolicyAuthorizationParamsForm" styleId="name" property="name"/>	 
	<tr> 
      <td valign="top" align="left"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
			<tr>
				<td  class="tblheader-bold" valign="top"  width="27%" colspan="4">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.authenticationdetails"/>
				</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" nowrap="nowrap" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.wimax" /> 
					<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.wimax" header="servicepolicy.naspolicy.wimax"/>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="3" width="70%">
					<html:select property="wimax" styleClass="labeltext" styleId="wimax" tabindex="18">
						<html:option value="true">Enabled</html:option>
						<html:option value="false">Disabled</html:option>
					</html:select>
				</td>
			</tr>
			<tr>
				<td class="captiontext" valign="top"  nowrap="nowrap" width="30%">
					Diameter Policy											
				</td>
				<td class="labeltext" width="70%"  colspan="2" valign="top" >
					<table  style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
						<tr>
							<td  class="labeltext" valign="top">
								<html:checkbox property="rejectOnCheckItemNotFound"  styleClass="labeltext" styleId="rejectOnCheckItemNotFound"/>
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rejectoncheckitemnotfound"/>
								<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.rejectoncheck" header="servicepolicy.authpolicy.rejectoncheckitemnotfound"/>
							</td>
						</tr>
						
						<tr>
							<td class="labeltext" valign="top">
								<html:checkbox property="rejectOnRejectItemNotFound"  styleClass="labeltext" styleId="rejectOnRejectItemNotFound"/>
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.rejectonrejectitemnotfound"/>
								<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.rejectoncheck" header="servicepolicy.authpolicy.rejectoncheckitemnotfound"/>
							</td>
						</tr>
						
						<tr>
							<td class="labeltext" valign="top" width="40%">
								<html:checkbox property="actionOnPolicyNotFound"  styleClass="labeltext" styleId="actionOnPolicyNotFound"/>
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.acceptonpolicynotfound"/>
								<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.rejectoncheck" header="servicepolicy.authpolicy.rejectoncheckitemnotfound"/>
							</td>
						</tr>
						
					</table>		
				</td>
			</tr> 
			<tr>
				<td align="left" class="captiontext" valign="top"  nowrap="nowrap" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.gracepolicy" /> 
					<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.gracepolicy" header="servicepolicy.naspolicy.gracepolicy"/>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="3" width="70%">
					<html:select property="gracePolicy" styleClass="labeltext" styleId="gracePolicy" tabindex="18" style="width:175px;">
						<html:option value="">--select--</html:option>
							<logic:iterate id="gracePolicyInst" name="updateNASServicePolicyAuthorizationParamsForm" property="gracePolicyList" type="com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData">
								<html:option value="<%=gracePolicyInst.getName()%>"><%=gracePolicyInst.getName()%></html:option>
							</logic:iterate>
					</html:select>
				</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top"  nowrap="nowrap" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.diameterconcurrency" /> 
					<ec:elitehelp  header="servicepolicy.naspolicy.diameterconcurrency" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.diameterconcurrency" ></ec:elitehelp>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="3" width="70%">
					<html:select property="diameterConcurrency" styleClass="labeltext" styleId="diameterConcurrency" tabindex="18" style="width:175px;">
						<html:option value="0">--select--</html:option>
						<logic:iterate id="diameterConcurrencyInst" name="updateNASServicePolicyAuthorizationParamsForm" property="diameterConcurrencyDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData">
								<html:option value="<%=String.valueOf(diameterConcurrencyInst.getDiaConConfigId())%>"><%=diameterConcurrencyInst.getName()%></html:option>
						</logic:iterate>
					</html:select>
				</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top"  nowrap="nowrap" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.additionaldiameterconcurrency" /> 
					<ec:elitehelp  header="servicepolicy.naspolicy.additionaldiameterconcurrency" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.additionaldiameterconcurrency" ></ec:elitehelp>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="3" width="70%">
					<html:select property="additionalDiameterConcurrency" styleClass="labeltext" styleId="additionalDiameterConcurrency" tabindex="18" style="width:175px;">
						<html:option value="0">--select--</html:option>
						<logic:iterate id="additionalDiameterConcurrencyInst" name="updateNASServicePolicyAuthorizationParamsForm" property="diameterConcurrencyDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData">
							<html:option value="<%=String.valueOf(additionalDiameterConcurrencyInst.getDiaConConfigId())%>"><%=additionalDiameterConcurrencyInst.getName()%></html:option>
						</logic:iterate>
					</html:select>
				</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top"  nowrap="nowrap" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.defaultsessiontimeout" /> 
					<ec:elitehelp  header="servicepolicy.naspolicy.defaultsessiontimeout" headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.defaultsessiontimeout" ></ec:elitehelp>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="3" width="70%">
					<html:text styleId="defaultSessionTimeout" property="defaultSessionTimeout" name="updateNASServicePolicyAuthorizationParamsForm" maxlength="10" style="width:175px;" />
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="btns-td" valign="middle">
					&nbsp;
				</td>
				<td class="btns-td" valign="middle" colspan="3">
					<input type="button" value="Update "  class="light-btn" onclick="validate();"/>
					<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewNASServicePolicyDetail.do?nasPolicyId=<%=nasPolicyForm.getNasPolicyId()%>'" value="Cancel" class="light-btn">
				</td>
			</tr>
		</table>
		</td>
		</tr>
</html:form>
</table>							