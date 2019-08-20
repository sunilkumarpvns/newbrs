<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.certificate.forms.ServerCertificateForm"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%
	String basePath=request.getContextPath();
	ServerCertificateForm trustedCertificateForm=(ServerCertificateForm) request.getAttribute("trustedCertificateForm");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
	setTitle('<bean:message key="server.trustedcertificate"/>');

var isValidName=false;
function verifyName(){
	var searchName=document.getElementById("trustedCertificateName").value;
	isValidName=verifyInstanceName('<%=InstanceTypeConstants.TRUSTED_CERTIFICATE%>',searchName,'create','','verifyNameDiv');	
}
function validateCreate()
{	
 	if(isValidForm()) {				
		$("#certificateForm").submit();
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
<html:form action="/serverAllCertificates?method=createTrusted" enctype="multipart/form-data" styleId="certificateForm">
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
												<ec:elitehelp headerBundle="servermgrResources" 
												text="server.trustedcertificate.name" header="servermgr.trustedcertificate.name"/>
											</td>
											<td align="left" class="labeltext" valign="top"	nowrap="nowrap">
												<html:text name="trustedCertificateForm" property="trustedCertificateName" styleId="trustedCertificateName" size="30" tabindex="1" maxlength="256" onblur="verifyName();" style="width:250px" />
												<font color="#FF0000"> *</font>
												<div id="verifyNameDiv" class="labeltext"></div>											
											</td> 
										</tr>
										<!-- 	<tr>
											<td colspan="3">&nbsp;</td>
										</tr> -->
										<tr>
											<td align="left" class="labeltext" valign="top" width="25%">
												<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate" /> 
												<ec:elitehelp headerBundle="servermgrResources" 
												text="server.trustedcertificate.trustedcert" header="servermgr.trustedcertificate"/>
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
												<input type="button" name="c_btnNext" tabindex="7" onclick="validateCreate();" value="  Create  " class="light-btn"  />&nbsp;&nbsp;  
												<%-- <html:submit styleClass="light-btn" tabindex="7" value="  Create  " ></html:submit> --%><%-- onclick="submitData();" --%> 
												<input type="button" name="c_btnCancelPeer" tabindex="8" onclick="javascript:location.href='serverAllCertificates.do?method=initSearch'" value="  Cancel  " class="light-btn">
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