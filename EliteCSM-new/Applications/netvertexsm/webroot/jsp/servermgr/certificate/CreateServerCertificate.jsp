<%@page import="com.elitecore.core.commons.tls.constant.PrivateKeyAlgo"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import=" com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.ServerCertificateForm"%>
<%
	ServerCertificateForm serverCertificateForm = (ServerCertificateForm) request.getAttribute("serverCertificateForm");
	request.setAttribute("x509Certificate", serverCertificateForm.getX509Certificate());	
%>
<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message key="server.certificate"/>');
});
var isValidName=false;
function verifyFormat (){
	var searchName = document.getElementById("serverCertificateName").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.SERVER_CERTIFICATE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName(){
	var searchName=document.getElementById("serverCertificateName").value;
	isValidName=verifyInstanceName({instanceType:'<%=InstanceTypeConstants.SERVER_CERTIFICATE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
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

function chkOpen(){
	window.open('<bean:write name="serverCertificateForm" property="x509Certificate"/>');
}
</script>
<html:form action="/serverCertificate?method=create" enctype="multipart/form-data" styleId="certificateForm">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
				<tr>
					<td width="10" class="small-gap">&nbsp;</td>
					<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header">
									<bean:message bundle="servermgrResources" key="servermgr.certificate.create" />
								</td>
							</tr>
							<tr>
								<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="3" class="btns-td">
									<table width="60%" name="c_tblCreateServerCertificate" id="c_tblCreateServerCertificate" align="left" cellSpacing="0" cellPadding="0" border="0">
										<tr>
											<td align="left" class="labeltext" valign="top" width="30%">
												<bean:message bundle="servermgrResources" key="servermgr.certificate.name" /> 
												<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.certificate.name"/>','<bean:message bundle="servermgrResources" key="servermgr.certificate.name"/>')" />
											</td>
											<sm:nvNameField id="serverCertificateName" name="serverCertificateName" maxLength="256" size="30"/> 
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" width="25%">
												<bean:message bundle="servermgrResources" key="servermgr.certificate.publiccert" /> 
												<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.certificate.publiccert"/>','<bean:message bundle="servermgrResources" key="servermgr.certificate.publiccert"/>')" />
											</td>
											<td align="left" class="labeltext" valign="top" nowrap="nowrap">
												<input type="file" name="publicCert" id="publicCert" size="22" class="uploadIPFile" tabindex="2" onchange="showCertificate();" title="Upload"/> 
												<font color="#FF0000"> *</font>
												<%-- <img src="<%=basePath%>/images/i.png" tabindex="3" style="height: 20px;width: 16px" name="Image6" border="0" onclick="window.open('<%=basePath%>/jsp/servermgr/certificate/ViewServerCertificateFile.jsp','CSVWin','top=200, left=200, height=350, width=700,scrollbars=yes, status')"> --%> 
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" width="25%">
												<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekey" /> 
												<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.certificate.privatekey"/>','<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekey"/>')" />
											</td>
											<td align="left" class="labeltext" valign="top" nowrap="nowrap">
												<input type="file" name="privateKey" id="privateKey" size="22" class="uploadIPFile" tabindex="4"/>
												<font color="#FF0000"> *</font>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" width="25%">
												<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekeypassword" /> 
												<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.certificate.privatekeypassword"/>','<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekeypassword"/>')" />
											</td>
											<td align="left" class="labeltext" valign="top" nowrap="nowrap">
												<html:password property="privateKeyPassword" tabindex="5" styleId="privateKeyPassword" name="serverCertificateForm" style="width:250px"/>												
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" width="25%">
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
												<input type="button" name="c_btnNext" tabindex="7" onclick="validateCreate();" value="  Create  " class="light-btn"  />
												<%-- <html:submit styleClass="light-btn" tabindex="7" value="  Create  " onclick="submitData();"></html:submit> --%>&nbsp;&nbsp; 
												<input type="button" name="c_btnCancel" tabindex="8" onclick="javascript:location.href='<%=basePath%>/searchAllCertificate.do?method=search'" value="  Cancel  " class="light-btn">
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</html:form>

