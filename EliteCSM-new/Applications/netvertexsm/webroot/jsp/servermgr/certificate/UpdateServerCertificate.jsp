<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.ServerCertificateForm"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.core.commons.tls.constant.PrivateKeyAlgo"%>

<% ServerCertificateForm serverCertificateForm = (ServerCertificateForm) request.getAttribute("serverCertificateForm"); %>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
$(document).ready(function(){
	verifyName();
});
var isValidName=false;

function verifyFormat (){
	var searchName = document.getElementById("serverCertificateName").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.SERVER_CERTIFICATE%>', searchName:searchName,mode:'update',id:'<%=serverCertificateForm.getServerCertificateId()%>'},'verifyNameDiv');
}
function verifyName(){
	var searchName=document.getElementById("serverCertificateName").value;
	isValidName=verifyInstanceName({instanceType:'<%=InstanceTypeConstants.SERVER_CERTIFICATE%>', searchName:searchName,mode:'update',id:'<%=serverCertificateForm.getServerCertificateId()%>'},'verifyNameDiv');
}
function validateCreate()
{	
 	if(isValidForm()) {				
		$("#certificateForm").submit();
	} 
}
function isValidForm()
{
	if($.trim($('#serverCertificateName').val()).length <= 0){
		alert('Name must be specified');
		$('#serverCertificateName').focus();
		return false;
	}else if(!isValidName) {
		alert('Enter Valid Name');
		$('#serverCertificateName').focus();
		return false;
	}
	
	if($.trim($('#publicCert').val()).length <= 0){
		alert('Public certificate must be specified');
		$('#publicCert').focus();
		return false;
	}
	if($.trim($('#privateKey').val()).length <= 0){
		alert('Private key must be specified');
		$('#privateKey').focus();
		return false;
	}	
	if($.trim($('#privateKeyAlgorithm').val()).length <= 0){
		alert('Private key Algorithm must be specified');
		$('#privateKeyAlgorithm').focus();
		return false;
	}
	return true;	
}
</script>

<html:form action="/serverCertificate?method=update" enctype="multipart/form-data" styleId="certificateForm">
	<html:hidden name="serverCertificateForm" styleId="serverCertificateId" property="serverCertificateId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td valign="middle" colspan="5">
				<table cellpadding="0" cellspacing="0" border="0" width="97%" height="30%" align="right">
				
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="servermgrResources" key="servermgr.certificate.update" />
						</td>
					</tr>
					<tr>
						<td colspan="100%">&nbsp;</td>
					</tr>
					<tr>						
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="servermgrResources" key="servermgr.certificate.name" /> 
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.certificate.name"/>','<bean:message bundle="servermgrResources" key="servermgr.certificate.name"/>')" />
						</td>
						<!-- <td align="left" class="labeltext" valign="top"	nowrap="nowrap">
							<html:text name="serverCertificateForm" tabindex="1" styleId="serverCertificateName" property="serverCertificateName" size="30" maxlength="256" onblur="verifyName();" style="width:250px" />
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td> -->
					<sm:nvNameField id="serverCertificateName" name="serverCertificateName" maxLength="256" size="30" value="${serverCertificateForm.serverCertificateName }" />
					</tr>
					<tr>
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="servermgrResources" key="servermgr.certificate.publiccert" /> 
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.certificate.publiccert"/>','<bean:message bundle="servermgrResources" key="servermgr.certificate.publiccert"/>')" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:file property="publicCert" styleId="publicCert" tabindex="2" size="22" name="serverCertificateForm"></html:file>
							<font color="#FF0000"> *</font>
						</td>
					</tr> 
					<tr>
						<td align="left" class="btns-td" valign="top" width="25%">
					 		<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekey" /> 
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.certificate.privatekey"/>','<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekey"/>')" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:file property="privateKey" styleId="privateKey" tabindex="4" size="22" name="serverCertificateForm"></html:file>
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekeypassword" /> 
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.certificate.privatekeypassword"/>','<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekeypassword"/>')" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:password property="privateKeyPassword" tabindex="5" styleId="privateKeyPassword" name="serverCertificateForm" style="width:250px"></html:password>												
						</td>
					</tr>
					<tr>
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekeyalgo" /> 
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.certificate.privatekeyalgorithm"/>','<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekeyalgo"/>')" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							 <html:select property="privateKeyAlgorithm" styleClass="labeltext" styleId="privateKeyAlgorithm" style="width: 134px;" tabindex="6">
							 	<logic:iterate id="privateKeyAlgoInst" name="serverCertificateForm" collection="<%=PrivateKeyAlgo.values()%>" >
									<html:option value="<%=((PrivateKeyAlgo)privateKeyAlgoInst).name%>"><%=((PrivateKeyAlgo)privateKeyAlgoInst).toString()%></html:option>	
								</logic:iterate>
							</html:select>
							<font color="#FF0000"> *</font>
						</td>											
					</tr>										
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>			
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2">
						<input type="button" name="c_btnNext" tabindex="7" onclick="validateCreate();" value="  Update  " class="light-btn"  />
							<%-- <html:submit styleClass="light-btn" tabindex="7" value="  Update  " ></html:submit> --%>&nbsp;&nbsp; 
							<input type="button" name="c_btnCancel" tabindex="8" onclick="javascript:location.href='<%=basePath%>/searchAllCertificate.do?method=initSearch'" value="  Cancel  " class="light-btn">
						</td>
					</tr>
				</tr>
			</table>
		</td>
	</tr>
</table>		
</html:form>
<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>