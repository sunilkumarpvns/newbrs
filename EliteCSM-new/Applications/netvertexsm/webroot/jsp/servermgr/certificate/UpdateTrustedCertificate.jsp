<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.TrustedCertificateForm"%>
<%@ page import="java.util.List"%>
<%
	TrustedCertificateForm trustedCertificateForm = (TrustedCertificateForm) request.getAttribute("trustedCertificateForm");
%>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
$(document).ready(function(){
	verifyName();
	$("#trustedCertificateName").focus();
});

var isValidName=false;
function verifyFormat (){
	var searchName = document.getElementById("trustedCertificateName").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.TRUSTED_CERTIFICATE%>', searchName:searchName,mode:'update',id:'<%=trustedCertificateForm.getTrustedCertificateId()%>'},'verifyNameDiv');
}
function verifyName(){
	var searchName = document.getElementById("trustedCertificateName").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.TRUSTED_CERTIFICATE%>', searchName:searchName,mode:'update',id:'<%=trustedCertificateForm.getTrustedCertificateId()%>'},'verifyNameDiv');
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

<html:form action="/trustedCertificate?method=update" enctype="multipart/form-data" styleId="certificateForm" onsubmit="return validateCreate();" >
	<html:hidden name="trustedCertificateForm" styleId="trustedCertificateId" property="trustedCertificateId" />
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td valign="middle" colspan="5">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
				
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.update" />
						</td>
					</tr>
					<tr>
						<td colspan="100%">&nbsp;</td>
					</tr>
					<tr>						
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.name" /> 
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.trustedcertificate.name"/>','<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.name"/>')" />
						</td>
						<sm:nvNameField id="trustedCertificateName" name="trustedCertificateName" maxLength="256" size="30" value="${trustedCertificateForm.trustedCertificateName }" />
					</tr>
					<tr>
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate" /> 
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.trustedcertificate.trustedcert"/>','<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate"/>')" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:file property="trustedCert" styleId="trustedCert" tabindex="2" size="22" name="trustedCertificateForm"></html:file>
							<font color="#FF0000"> *</font>
							<%-- <img src="<%=basePath%>/images/i.png" tabindex="3" style="height: 20px;width: 16px" name="Image6" border="0" onclick="window.open('<%=basePath%>/jsp/servermgr/certificate/ViewTrustedCertificateFile.jsp','CSVWin','top=200, left=200, height=350, width=700,scrollbars=yes, status')"> --%>  
						</td>
					</tr>						
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>			
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2">						
							<html:submit styleClass="light-btn" styleId="c_btnCreate2" tabindex="7"  value="  Update  "  />&nbsp;&nbsp;							
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