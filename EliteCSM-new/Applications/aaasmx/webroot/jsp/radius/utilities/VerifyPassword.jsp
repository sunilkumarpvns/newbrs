<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.utilities.forms.VerifyPasswordForm"%>
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
	VerifyPasswordForm verifyPasswordForm = (VerifyPasswordForm) request.getAttribute("verifyPasswordForm");
	String encryptType = null;
	boolean disabled = true;
	if(verifyPasswordForm!=null)
	{
		encryptType = verifyPasswordForm.getEncryptType();
		if(encryptType.equalsIgnoreCase("CHAP")){
			disabled = false;
		} 
	}
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript">

function setPapInputs()
{
	//alert("setPAP Inputs");
	var paraTitle = '<bean:message bundle="radiusResources" key="utilities.verifypwd.pappara" />';
	var element = document.getElementById("paraTitle");
	element.innerHTML=paraTitle;
	document.forms[0].reqAuthenticator.disabled = "";
	document.forms[0].sharedSecret.disabled = "";
	document.forms[0].chapChallenge.disabled = "disabled";
	document.forms[0].chapPassword.disabled = "disabled";
	document.forms[0].chapChallenge.value = "";
	document.forms[0].chapPassword.value = "";
	document.forms[0].digestRealm.disabled = "disabled";
	document.forms[0].digestNonce.disabled = "disabled";
	document.forms[0].digestMethod.disabled = "disabled";
	document.forms[0].digestUri.disabled = "disabled";
	document.forms[0].digestQoP.disabled = "disabled";
	document.forms[0].digestAlgorithm.disabled = "disabled";
	document.forms[0].digestBody.disabled = "disabled";
	document.forms[0].digestCNonce.disabled ="disabled";
	document.forms[0].digestNonceCount.disabled = "disabled";
	document.forms[0].digestUserName.disabled = "disabled";
}

function setChapInputs()
{
	//alert("setChap Inputs");
	var paraTitle = '<bean:message bundle="radiusResources" key="utilities.verifypwd.chappara" />';
	var element = document.getElementById("paraTitle");
	element.innerHTML=paraTitle;
	document.forms[0].reqAuthenticator.disabled = "";
	document.forms[0].sharedSecret.value = "";
	document.forms[0].sharedSecret.disabled = "disabled";
	document.forms[0].chapChallenge.disabled = "";
	document.forms[0].chapPassword.disabled = "";
	document.forms[0].digestRealm.disabled = "disabled";
	document.forms[0].digestNonce.disabled = "disabled";
	document.forms[0].digestMethod.disabled = "disabled";
	document.forms[0].digestUri.disabled = "disabled";
	document.forms[0].digestQoP.disabled = "disabled";
	document.forms[0].digestAlgorithm.disabled = "disabled";
	document.forms[0].digestBody.disabled = "disabled";
	document.forms[0].digestCNonce.disabled ="disabled";
	document.forms[0].digestNonceCount.disabled = "disabled";
	document.forms[0].digestUserName.disabled = "disabled";
}
function setDigestInputs()
{
	//alert("setDigest Inputs");
	var paraTitle = '<bean:message bundle="radiusResources" key="utilities.verifypwd.digestpara" />';
	var element = document.getElementById("paraTitle");
	element.innerHTML=paraTitle;
	
	document.forms[0].sharedSecret.disabled = "disabled";
	document.forms[0].reqAuthenticator.disabled = "disabled";
	document.forms[0].chapChallenge.disabled = "disabled";
	document.forms[0].chapPassword.disabled = "disabled";
	document.forms[0].digestRealm.disabled = "";
	document.forms[0].digestNonce.disabled = "";
	document.forms[0].digestUri.disabled = "";
	document.forms[0].digestMethod.disabled = "";
	document.forms[0].digestQoP.disabled = "";
	document.forms[0].digestAlgorithm.disabled = "";
	document.forms[0].digestBody.disabled = "";
	document.forms[0].digestCNonce.disabled = "";
	document.forms[0].digestNonceCount.disabled = "";
	document.forms[0].digestUserName.disabled = "";
}
function reset()
{
	
	var encryptType = '<%=encryptType%>';
	//alert(encryptType);
	if(encryptType=='PAP'){
		
		setPapInputs();
	}
	else if(encryptType=='CHAP'){
		
		setChapInputs();
	}
	else if(encryptType == 'Digest'){
		
		setDigestInputs();
	}
	else{
		setPapInputs();
	}
	
}

function validate()
{
	
	
	if(document.forms[0].encryptType[0].checked){
		if(document.forms[0].sharedSecret.value.trim()==''){
			alert("Please, enter Shared Secret.");
			return;
		}
		if(document.forms[0].reqAuthenticator.value.trim() == ''){
			alert("Please, enter Request Authenticator.");
			return;
		}
		if(document.forms[0].userPassword.value.trim() == ''){
			alert("Please, enter User Password.");
			return;
		}
		document.forms[0].submit();
	}
	else if(document.forms[0].encryptType[1].checked)
	{
		if(document.forms[0].chapChallenge.value.trim()=='' && document.forms[0].reqAuthenticator.value.trim() == ''){
			alert("Please, enter Request Authenticator or CHAP Challenge.");
			return;
		}
		if(document.forms[0].userPassword.value.trim() == ''){
			alert("Please, enter User Password.");
			return;
		}
		if(document.forms[0].chapPassword.value.trim() == ''){
			alert("Please, enter CHAP Password.");
			return;
		}
		document.forms[0].submit();
	}
	else if(document.forms[0].encryptType[2].checked)
	{
		
		document.forms[0].submit();
	}
	
}
setTitle('<bean:message bundle="radiusResources" key="utilities.verifypwd"/>');
</script>

<html:form action="/verifyPassword" onreset="javascript:reset();">
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header" colspan="4"><bean:message
											bundle="radiusResources" key="utilities.verifypwd" /></td>
								</tr>

								<tr>
									<td class="small-gap" width="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3" class="tblheader-bold"><bean:message
											bundle="radiusResources"
											key="utilities.verifypwd.encrypttype" /></td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top"><html:radio
											property="encryptType" styleId="encryptType" value="PAP"
											onclick="javascript: setPapInputs();" tabindex="1">
											<bean:message bundle="radiusResources"
												key="utilities.verifypwd.pap" />
										</html:radio></td>
									<td align="left" class="labeltext" valign="top"><html:radio
											property="encryptType" styleId="encryptType" value="CHAP"
											tabindex="2" onclick="javascript: setChapInputs();">
											<bean:message bundle="radiusResources"
												key="utilities.verifypwd.chap" />
										</html:radio></td>
									<td align="left" class="labeltext" valign="top"><html:radio
											property="encryptType" styleId="encryptType" value="Digest"
											tabindex="3" onclick="javascript: setDigestInputs();">
											<bean:message bundle="radiusResources"
												key="utilities.verifypwd.digest" />
										</html:radio></td>
								</tr>
								<tr>
									<td class="small-gap" width="3">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" colspan="3" class="tblheader-bold"
										valign="top" width="100%">
										<div name="paraTitle" id="paraTitle">
											<%if(encryptType!=null && encryptType.equalsIgnoreCase("CHAP")){ %>
											<bean:message bundle="radiusResources"
												key="utilities.verifypwd.chappara" />
											<%}else if(encryptType!=null && encryptType.equalsIgnoreCase("Digest")) { %>
											<bean:message bundle="radiusResources"
												key="utilities.verifypwd.digestpara" />
											<%} else { %>
											<bean:message bundle="radiusResources"
												key="utilities.verifypwd.pappara" />
											<%} %>
										</div>
									</td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
											key="utilities.verifypwd.sharedsecret" /> 
											<ec:elitehelp headerBundle="radiusResources" 
											text="utilities.verifypwd.sharedsecret" header="utilities.verifypwd.sharedsecret"/>
									</td>
									<td colspan="2"><html:text property="sharedSecret"
											size="35" tabindex="4"></html:text></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.reqauth" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.reqauth" header="utilities.verifypwd.reqauth"/>	
									</td>
									<td colspan="2" align="left"><html:text
											property="reqAuthenticator" size="35"></html:text></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.userpwd" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.userpwd" header="utilities.verifypwd.userpwd"/>
									</td>
									<td colspan="2" align="left"><html:text
											property="userPassword" size="35" tabindex="5"></html:text></td>
								</tr>
								<div name="chapPart" id="chapPart" />
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.chapchallenge" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.chapchallenge" header="utilities.verifypwd.chapchallenge"/>
									</td>
									<td colspan="2" align="left"><html:text
											property="chapChallenge" size="35" tabindex="6"></html:text>
									</td>
								</tr>

								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources" key="utilities.verifypwd.chappwd" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.chappwd" header="utilities.verifypwd.chappwd"/>	
									</td>
									<td colspan="2" align="left"><html:text
											property="chapPassword" size="35" tabindex="7"></html:text></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digestrealm" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digestrealm" header="utilities.verifypwd.digestrealm"/>	
									</td>
									<td colspan="2" align="left"><html:text
											property="digestRealm" size="35" tabindex="8"></html:text></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digestnonce" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digestnonce" header="utilities.verifypwd.digestnonce"/>
									</td>
									<td colspan="2" align="left"><html:text
											property="digestNonce" size="35" tabindex="9"></html:text></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digestmethod" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digestmethod" header="utilities.verifypwd.digestmethod"/>	
									</td>
									<td colspan="2" align="left"><html:text
											property="digestMethod" size="35" tabindex="10"></html:text>
									</td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digesturi" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digesturi" header="utilities.verifypwd.digesturi"/>
									</td>
									<td colspan="2" align="left"><html:text
											property="digestUri" size="35" tabindex="11"></html:text></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digestqop" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digestqop" header="utilities.verifypwd.digestqop"/>
									</td>
									<td colspan="2" align="left"><html:select
											property="digestQoP" style="width:130px" tabindex="12">
											<html:option bundle="radiusResources"
												key="utilities.verifypwd.auth" value="auth"></html:option>
											<html:option bundle="radiusResources"
												key="utilities.verifypwd.authint" value="auth-int"></html:option>
										</html:select></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digestalgorithm" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digestalgorithm" header="utilities.verifypwd.digestalgorithm"/> 
									</td>
									<td colspan="2" align="left"><html:select
											property="digestAlgorithm" style="width:130px" tabindex="13">
											<html:option bundle="radiusResources"
												key="utilities.verifypwd.md5" value="MD5"></html:option>
											<html:option bundle="radiusResources"
												key="utilities.verifypwd.md5sess" value="MD5-SESS"></html:option>
										</html:select></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digestbody" />
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digestbody" header="utilities.verifypwd.digestbody"/>	
									</td>
									<td colspan="2"><html:text property="digestBody" size="35"
											tabindex="14"></html:text></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digestcnonce" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digestcnonce" header="utilities.verifypwd.digestcnonce"/>	
									</td>
									<td colspan="2"><html:text property="digestCNonce"
											size="35" tabindex="15"></html:text></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digestnoncecount" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digestnoncecount" header="utilities.verifypwd.digestnoncecount"/>
									</td>
									<td colspan="2"><html:text property="digestNonceCount"
											size="35" tabindex="16"></html:text></td>
								</tr>
								<tr>
									<td width="25%" align="left" class="captiontext" valign="top">
										<bean:message bundle="radiusResources"
										key="utilities.verifypwd.digestusername" /> 
										<ec:elitehelp headerBundle="radiusResources" 
										text="utilities.verifypwd.digestusername" header="utilities.verifypwd.digestusername"/>
									</td>
									<td colspan="2"><html:text property="digestUserName"
											size="35" tabindex="17"></html:text></td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr class="captiontext">
									<td colspan="3" class="grey-text">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Note:
										In case of CHAP encrypt type, either Request Authenticator or
										CHAP challenge is required.</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle" colspan="3"><input
										type="button" tabindex="18" value="Verify Password"
										property="update" onclick="javascript:validate()"
										class="light-btn" /> <input type="reset" tabindex="19"
										name="c_btnDeletePolicy"
										onclick="javascript:location.href='<%=basePath%>/initVerifyPassword.do?/>'"
										value="Cancel" class="light-btn"></td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle" colspan="3">
										<div name="msgId" id="msgId" /> <%if(verifyPasswordForm!=null){ %>
										<bean:message bundle="radiusResources"
											key="utilities.verifypwd.msg" /> <b> <%=verifyPasswordForm.getMsgPassword() %></b>
										<%}%>
										</div>
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

<script language="javascript">
window.onload =reset(); 
</script>