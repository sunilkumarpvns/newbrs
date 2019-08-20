<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
   String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script><!--
var isValidName;

function validateCreate()
{

	
	if(isNull(document.forms[0].name.value)){
		document.forms[0].name.focus();
		alert('Name must be specified');
	}else if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();
		return;
	}else if(isNull(document.forms[0].realm.value)){
		document.forms[0].realm.focus();
		alert('Digest Realm must be specified');
	}else if(isNull(document.forms[0].defaultQoP.value)){
		document.forms[0].defaultQoP.focus();
		alert('Default Digest QoP must be specified');
	}else if(isNull(document.forms[0].defaultAlgo.value)){
	    document.forms[0].defaultAlgo.focus();
		alert('Default Digest Algorithm  must be specified');
    }else if((isNull(document.forms[0].opaque.value))){
       document.forms[0].opaque.focus();
       alert(' Digest Opaque  value  must be Specified');
    }else if(!(isNumber(document.forms[0].defaultNonceLength.value))){
      	document.forms[0].defaultNonceLength.focus();
      	alert('Default Digest Nonce Length  value  must be Numeric');
    }else if((isNull(document.forms[0].defaultNonce.value))){
       document.forms[0].defaultNonce.focus();
       alert('Default Digest Nonce value  must be specified');
    }else{
	
	document.forms[0].submit();
    }
}	

function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIGEST_CONFIG%>',searchName,'create','','verifyNameDiv');
}
setTitle('<bean:message bundle="digestconfResources" key="digestconf.header"/>');
</script>

<html:form action="/createDigestConf">
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
									<td class="table-header"><bean:message
											bundle="digestconfResources"
											key="digestconf.createDigestConf" /></td>
								</tr>

								<tr>
									<td class="small-gap" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="30%">

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="digestconfResources" key="digestconf.name" />
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.name" header="digestconf.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="name" tabindex="1" property="name"
														onkeyup="verifyName();" size="25" maxlength="50"
														style="width:250px" /> <font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top" width="25%" valign="top">
													<bean:message bundle="digestconfResources" key="digestconf.description" />
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.description" header="digestconf.description"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:textarea styleId="description" tabindex="2"
														property="description" cols="50" rows="4"
														style="width:250px" />
												</td>
											</tr>



											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="digestconfResources" key="digestconf.realm" /> 
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.realm" header="digestconf.realm"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="realm" tabindex="3" property="realm"
														size="25" maxlength="50" style="width:250px" /> <font
													color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="digestconfResources" key="digestconf.defaultqop" />
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.qop" header="digestconf.defaultqop"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:select
														name="createDigestConfForm" tabindex="4"
														styleId="defaultQoP" property="defaultQoP" size="1"
														value="false" style="width:130px">
														<html:option value="auth">Auth</html:option>
														<html:option value="auth-int">Auth-Int</html:option>
													</html:select><font color="#FF0000"> *</font></td>

											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="digestconfResources" key="digestconf.defaultalgo" />
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.defaultalgo" header="digestconf.defaultalgo"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="defaultAlgo" tabindex="5"
														property="defaultAlgo" size="20" maxlength="20"
														style="width:250px" /> <font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="digestconfResources" key="digestconf.opaque" /> 
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.opaque" header="digestconf.opaque"/> 
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="opaque" property="opaque" tabindex="6"
														size="25" maxlength="50" style="width:250px" /> <font
													color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" " valign="top">
													<bean:message bundle="digestconfResources" key="digestconf.defaultnoncelength" /> 
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.noncelength" header="digestconf.defaultnoncelength"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="defaultNonceLength" tabindex="7"
														property="defaultNonceLength" size="25" maxlength="50"
														style="width:250px" /> <font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="digestconfResources" key="digestconf.defaultnonce" />
													<ec:elitehelp headerBundle="digestconfResources" 
													text="digestconf.digestnonce" header="digestconf.defaultnonce"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:text styleId="defaultNonce" tabindex="8"
														property="defaultNonce" size="10" maxlength="10"
														style="width:250px" /> <font color="#FF0000"> *</font>
												</td>
											</tr>
											<td align="left" class="captiontext" valign="top">
												<bean:message bundle="digestconfResources" key="digestconf.drafaaasipenable" /> 
												<ec:elitehelp headerBundle="digestconfResources" 
												text="digestconf.draftsterman" header="digestconf.drafaaasipenable"/>
											</td>
											<td align="left" class="labeltext" valign="top"><html:select
													name="createDigestConfForm" tabindex="9"
													styleId="draftAAASipEnable" property="draftAAASipEnable"
													size="1" value="false" style="width:130px">
													<html:option value="true">True</html:option>
													<html:option value="false">False</html:option>
												</html:select><font color="#FF0000"> *</font></td>
											<tr>


											</tr>



											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" tabindex="10" name="c_btnCreate"
													onclick="validateCreate()" id="c_btnCreate2" value="Create"
													class="light-btn"> <input type="reset"
													tabindex="11" name="c_btnCancel"
													onclick="javascript:location.href='<%=basePath%>/initSearchDigestConf.do?/>'"
													value="Cancel" class="light-btn"></td>
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

