<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.TrustedCertificateForm"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%
	TrustedCertificateForm trustedCertificateForm=(TrustedCertificateForm) request.getAttribute("trustedCertificateForm");
%>
<script>
$(document).ready(function(){
	$("#trustedCertificateName").focus();
	setTitle('<bean:message key="server.trustedcertificate"/>');
});

var isValidName=false;
function verifyFormat (){
	var searchName = document.getElementById("trustedCertificateName").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.TRUSTED_CERTIFICATE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName(){
	var searchName = document.getElementById("trustedCertificateName").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.TRUSTED_CERTIFICATE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');	
}
function validateCreate()
{	
	verifyName();
 	if(isValidForm()) {				
		return true;
	}else{
		return false;
	} 
}
function isValidForm()
{
	if($.trim($('#trustedCertificateName').val()).length <= 0){
		alert('Name must be specified');
		$('#trustedCertificateName').focus();
		return false;
	}else if(!isValidName) {
		alert('Enter Valid Name');
		$('#trustedCertificateName').focus();
		return false;
	}
	
	if($.trim($('#trustedCert').val()).length <= 0){
		alert('Trusted certificate must be specified');
		$('#trustedCert').focus();
		return false;
	}	
	return true;	
}
</script>
<html:form action="/trustedCertificate?method=create" enctype="multipart/form-data" styleId="certificateForm" onsubmit="return validateCreate();" >
<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
				<tr>
					<td width="10" class="small-gap">&nbsp;</td>
					<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header">
									<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.create" />
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
												<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.name" /> 
												<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.trustedcertificate.name"/>','<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.name"/>')" />
											</td>
											<sm:nvNameField id="trustedCertificateName" name="trustedCertificateName" maxLength="256" size="30" /> 
										</tr>
										<!-- 	<tr>
											<td colspan="3">&nbsp;</td>
										</tr> -->
										<tr>
											<td align="left" class="labeltext" valign="top" width="25%">
												<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate" /> 
												<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.trustedcertificate.trustedcert"/>','<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate"/>')" />
											</td>
											<td align="left" class="labeltext" valign="top" nowrap="nowrap">
												<input type="file" name="trustedCert" id="trustedCert" size="22" class="uploadIPFile" tabindex="2" onchange="showCertificate();"/>
												<font color="#FF0000"> *</font>
			 									<%-- <img src="<%=basePath%>/images/i.png" tabindex="3" style="height: 20px;width: 16px" name="Image6" border="0" onclick="window.open('<%=basePath%>/jsp/servermgr/certificate/ViewServerCertificateFile.jsp','CSVWin','top=200, left=200, height=350, width=700,scrollbars=yes, status')"> --%>											
											</td> 
										</tr> 																			
										<tr>
											<td colspan="3">&nbsp;</td>
										</tr>
										<tr>
											<td class="btns-td" valign="middle">&nbsp;</td>
											<td class="btns-td" valign="middle" colspan="2">
												<html:submit styleClass="light-btn" styleId="c_btnCreate2" tabindex="7"  value="  Create  "  />												 											
												<input type="button" name="c_btnCancelPeer" tabindex="8" onclick="javascript:location.href='<%=basePath%>/searchAllCertificate.do?method=search'" value="  Cancel  " class="light-btn">
											</td>
										</tr>
									</table> 
								</td>
								</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>
</html:form>