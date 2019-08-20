<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.certificate.form.CrlCertificateForm"%>
<%@ page import="java.util.List"%>
<%
	CrlCertificateForm crlCertificateForm = (CrlCertificateForm) request.getAttribute("crlCertificateForm");
%>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
$(document).ready(function(){
	$("#crlCertificateName").focus();
});

var isValidName=false;
function verifyFormat (){
	var searchName = document.getElementById("crlCertificateName").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.CRL_CERTIFICATE%>',searchName:searchName,mode:'update',id:'<%=crlCertificateForm.getCrlCertificateId()%>'},'verifyNameDiv');
}
function verifyName(){
	var searchName=document.getElementById("crlCertificateName").value;	
	isValidName=verifyInstanceName({instanceType:'<%=InstanceTypeConstants.CRL_CERTIFICATE%>',searchName:searchName,mode:'update',id:'<%=crlCertificateForm.getCrlCertificateId()%>'},'verifyNameDiv');
}
function validateCreate()
{	
	verifyName();
 	if(isValidForm()) {				
 		return true;
	} else {
		return false;
	}
}
function isValidForm()
{
	if($.trim($('#crlCertificateName').val()).length <= 0){
		alert('Name must be specified');
		$('#crlCertificateName').focus();
		return false;
	}else if(!isValidName) {
		alert('Enter Valid Name');
		$('#crlCertificateName').focus();
		return false;
	}
	
	if($.trim($('#crlCert').val()).length <= 0){
		alert('CRL certificate must be specified');
		$('#crlCert').focus();
		return false;
	}	
	return true;	
}
$(document).ready(function(){
	verifyName();
});
</script>

<html:form action="/crlCertificate?method=update" enctype="multipart/form-data" styleId="certificateForm" onsubmit="return validateCreate();" >
	<html:hidden name="crlCertificateForm" styleId="crlCertificateId" property="crlCertificateId" />
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td valign="middle" colspan="5">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
				
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.update" />
						</td>
					</tr>
					<tr>
						<td colspan="100%">&nbsp;</td>
					</tr>
					<tr>						
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.name" /> 
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.crlcertificate.name"/>','<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.name"/>')" />
						</td>
						<sm:nvNameField id="crlCertificateName" name="crlCertificateName" maxLength="256" size="30" value="${crlCertificateForm.crlCertificateName }" />
						
					</tr>
					<tr>
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="servermgrResources" key="servermgr.crlcertificate" /> 
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="server.crlcertificate.crlcert"/>','<bean:message bundle="servermgrResources" key="servermgr.crlcertificate"/>')" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:file property="crlCert" styleId="crlCert" tabindex="2" size="22" name="crlCertificateForm"></html:file>
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
							<input type="button" name="c_btnCancelPeer" tabindex="8" onclick="javascript:location.href='<%=basePath%>/searchAllCertificate.do?method=search'" value="  Cancel  " class="light-btn">
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