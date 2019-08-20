<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.web.sessionmanager.forms.CreateSessionManagerForm"%>
<%@page import="com.elitecore.elitesm.util.constants.ExternalSystemConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>

<%
	String basePath=request.getContextPath();
	String[] sessionManagerInstanceIds = (String[])request.getAttribute("sessionManagerInstanceIds");
	String[][] sessionManagerInstanceNames =  (String[][])request.getAttribute("sessionManagerInstanceNames");
	List<ExternalSystemInterfaceInstanceData> sessionManagerServerList = (List<ExternalSystemInterfaceInstanceData>)request.getAttribute("sessionManagerInstanceList");	
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<script>
	var isValidName;

   function validate(){
	  if(isNull(document.forms[0].name.value)){
			document.forms[0].name.focus();
			alert('SessionManager Name must be specified');
		}else if(!isValidName) {
			alert('Enter Valid Name');
			document.forms[0].name.focus();
			return false;
		}else{
         document.forms[0].submit();
	  }	    
   }

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.SESSION_MANAGER%>',searchName,'create','','verifyNameDiv');
}
setTitle('<bean:message bundle="sessionmanagerResources" key="sessionmanager.header"/>');
</script>

<%
	CreateSessionManagerForm createSessionManagerForm = (CreateSessionManagerForm)request.getAttribute("createSessionManagerForm");
	String esiUrl = basePath+"/initAddExtenalSystemPopup.do?externalSystemTypeId="+ExternalSystemConstants.SESSION_MANAGER;
%>

<html:form action="/createSessionManager">
	<html:hidden name="createSessionManagerForm" styleId="action" property="action" />
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="3">
										<bean:message bundle="sessionmanagerResources" key="sessionmanager.createsessionmanager" />
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top">&nbsp;</td>
									<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
								</tr>

								<tr>
									<td>
										<table name="c_tblCrossProductList" width="100%" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.name" />
													<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.name" header="sessionmanager.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" nowrap="nowrap">
													<html:text styleId="name" property="name" onkeyup="verifyName();" size="25" maxlength="50" style="width:250px" name="createSessionManagerForm" tabindex="11" />
													<font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%" valign="top">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.description" />
													<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.desc"  header="sessionmanager.description"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:textarea styleId="description" name="createSessionManagerForm" property="description" cols="50" rows="4" style="width:250px" tabindex="12" />
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
											</tr>
											<tr>
												<td class="small-gap" width="16%">&nbsp;</td>
												<td align="left" class="labeltext" valign="top">
													<input type="button" name="c_btnNext" onclick=" validate();" value=" Next " class="light-btn" tabindex="21" /> 
													<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchSessionManager.do?/>'" value="Cancel" class="light-btn" tabindex="23">
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
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
