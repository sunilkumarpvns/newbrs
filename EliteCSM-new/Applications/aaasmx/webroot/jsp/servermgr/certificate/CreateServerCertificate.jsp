<%@page import="com.elitecore.core.commons.tls.constant.PrivateKeyAlgo"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.certificate.forms.ServerCertificateForm"%>
<%
    String basePath = request.getContextPath();
	ServerCertificateForm serverCertificateForm = (ServerCertificateForm) request.getAttribute("serverCertificateForm");
	request.setAttribute("x509Certificate", serverCertificateForm.getX509Certificate());	
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript">

var isValidName=false;
function verifyName(){
	var searchName=document.getElementById("serverCertificateName").value;
	isValidName=verifyInstanceName('<%=InstanceTypeConstants.SERVER_CERTIFICATE%>',searchName,'create','','verifyNameDiv');
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
setTitle('<bean:message key="server.certificate"/>');
</script>
<html:form action="/serverAllCertificates?method=create" enctype="multipart/form-data" styleId="certificateForm">
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH)%>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>		
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
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
												<ec:elitehelp headerBundle="servermgrResources" 
												text="server.certificate.name" header="servermgr.certificate.name"/>
											</td>
											<td align="left" class="labeltext" valign="top"	nowrap="nowrap">
												<html:text name="serverCertificateForm" tabindex="1" styleId="serverCertificateName" property="serverCertificateName" size="30" maxlength="256" onblur="verifyName();" style="width:250px" />
											 
													<font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" width="25%">
												<bean:message bundle="servermgrResources" key="servermgr.certificate.publiccert" /> 
												<ec:elitehelp headerBundle="servermgrResources" 
												text="server.certificate.publiccert" header="servermgr.certificate.publiccert"/>
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
												<ec:elitehelp headerBundle="servermgrResources" 
												text="server.certificate.privatekey" header="servermgr.certificate.privatekey"/>
											</td>
											<td align="left" class="labeltext" valign="top" nowrap="nowrap">
												<input type="file" name="privateKey" id="privateKey" size="22" class="uploadIPFile" tabindex="4"/>
												<font color="#FF0000"> *</font>
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" width="25%">
												<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekeypassword" /> 
												<ec:elitehelp headerBundle="servermgrResources" 
												text="server.certificate.privatekeypassword" header="servermgr.certificate.privatekeypassword"/>
											</td>
											<td align="left" class="labeltext" valign="top" nowrap="nowrap">
												<html:password property="privateKeyPassword" tabindex="5" styleId="privateKeyPassword" name="serverCertificateForm" style="width:250px"/>												
											</td>
										</tr>
										<tr>
											<td align="left" class="labeltext" valign="top" width="25%">
												<bean:message bundle="servermgrResources" key="servermgr.certificate.privatekeyalgo" /> 
												<ec:elitehelp headerBundle="servermgrResources" 
												text="server.certificate.privatekeyalgorithm" header="servermgr.certificate.privatekeyalgo"/>
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
												<input type="button" name="c_btnCancel" tabindex="8" onclick="javascript:location.href='serverAllCertificates.do?method=initSearch'" value="  Cancel  " class="light-btn">
											</td>
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
</html:form>

