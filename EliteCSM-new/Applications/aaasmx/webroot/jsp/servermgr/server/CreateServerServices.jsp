<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData"%>
<%@page
	import="com.elitecore.elitesm.web.servermgr.server.forms.CreateServerForm"%>
<%@page import="java.util.List"%>
<jsp:directive.page
	import="com.elitecore.elitesm.util.constants.BaseConstant" />
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
    CreateServerForm createServerForm = (CreateServerForm) request.getAttribute("createServerForm");
    List lstServiceType = createServerForm.getLstServiceType();
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>

<script>
function  checkAll(){
	var arrayCheck = document.getElementsByName('selectedServices');
	
 	if( document.forms[0].toggleAll.checked == true) {
	 	for (i = 0; i < arrayCheck.length;i++){
			arrayCheck[i].checked = true ;
		}
    } else if (document.forms[0].toggleAll.checked == false){
		for (i = 0; i < arrayCheck.length; i++){
			arrayCheck[i].checked = false ;
		}
	}
}
function validateCreate(){
	document.forms[0].submit();
}
function goPrevious(){
	history.go(-1);
}

setTitle('<bean:message bundle="servermgrResources" key="servermgr.service"/>');	
</script>

<html:form action="/createServer">
	<html:hidden styleId="isInSync" property="isInSync"
		value="<%=BaseConstant.HIDE_STATUS_ID%>" />
	<html:hidden styleId="action" property="action" value="create" />
	<html:hidden styleId="action" property="netServerType"
		value="<%=createServerForm.getNetServerType()%>" />
	<html:hidden styleId="action" property="javaHome"
		value="<%=createServerForm.getJavaHome()%>" />
	<html:hidden styleId="action" property="description"
		value="<%=createServerForm.getDescription()%>" />
	<html:hidden styleId="action" property="adminInterfaceIP"
		value="<%=createServerForm.getAdminInterfaceIP()%>" />
	<html:hidden styleId="action" property="adminInterfacePort"
		value="<%=Integer.toString(createServerForm.getAdminInterfacePort())%>" />
	<html:hidden styleId="action" property="name"
		value="<%=createServerForm.getName()%>" />
	<html:hidden styleId="action" property="staff"
		value="<%=createServerForm.getStaff()%>" />
	<html:hidden styleId="action" property="serverHome"
		value="<%=createServerForm.getServerHome()%>" />
	<html:hidden styleId="action" property="status"
		value="<%=createServerForm.getStatus()%>" />


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
											bundle="servermgrResources" key="servermgr.addservice" /> <%--<img src="<%=basePath%>/images/open.jpg" border="0" name="closeopen"></td> --%>
								</tr>
								<tr>
									<td colspan="3">
										<table width="98%" name="c_tblCrossProductList"
											id="c_tblCrossProductList" align="left" border="0"
											cellpadding="0" cellspacing="0" class="captiontext">

											<%--
				   if(lstServiceType!=null){
				  
 	                int size = lstServiceType.size();
 	                
				  	for(int index=0;index<size;index++){
				  	
				  	INetServiceTypeData serviceTypeData = (INetServiceTypeData)lstServiceType.get(index);%>
				  	<tr>
						<td align="center" class="tblfirstcol" valign="top" width="8%" >
								<input type="checkbox" name="selected" id="<%=(index+1) %>" value="<%=serviceTypeData.getNetServiceTypeId()%>">
						</td>
				  		<td align="left" class="tblrows" valign="top"  >
				  		<%=serviceTypeData.getName()%>
				  		</td>
				  	</tr>
				 <%}
				 } --%>
											<%
				
				   if(lstServiceType!=null){%>
											<tr>
												<td align="center" class="tblheader" valign="top" width="8%">
													<input type="checkbox" name="toggleAll" id="toggleAll"
													value="checkbox" onclick="checkAll()" />
												</td>
												<td align="left" class="tblheader-bold" valign="top"><bean:message
														bundle="servermgrResources" key="servermgr.serialnumber" /></td>
												<td align="left" class="tblheader-bold" valign="top"><bean:message
														bundle="servermgrResources" key="servermgr.servicetype" /></td>
												<td align="left" class="tblheader-bold" valign="top"><bean:message
														bundle="servermgrResources" key="servermgr.description" /></td>
											</tr>
											<%int index=1; %>
											<logic:iterate id="netServiceType"
												type="com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData"
												name="createServerForm" property="lstServiceType">
												<tr>
													<td align="center" class="tblfirstcol" valign="top"
														width="8%"><html:multibox name="createServerForm"
															property="selectedServices" styleId="selected">
															<bean:write name="netServiceType"
																property="netServiceTypeId" />
														</html:multibox></td>
													<td align="left" class="tblrows" valign="top" width="8%">
														<%=index++%>.
													</td>
													<td align="left" class="tblrows" valign="top"><bean:write
															name="netServiceType" property="name" /></td>
													<td align="left" class="tblrows" valign="top"><bean:write
															name="netServiceType" property="description" /></td>

												</tr>
											</logic:iterate>

											<%}else{%>
											<tr>
												<td align="center" class="tblheader-bold" valign="top"><bean:message
														bundle="servermgrResources" key="servermgr.servicetype" /></td>
											</tr>
											<tr>
												<td align="center" class="tblfirstcol" valign="top"
													colspan="2">No Records Found.</td>
											</tr>
											<%} %>
										</table>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>

									<td class="btns-td" valign="middle"><input type="button"
										name="c_btnPrevious" onclick="goPrevious()" id="c_btnPrevious"
										value=" Previous " class="light-btn" tabindex="6"> <input
										type="button" name="c_btnCreate" onclick="validateCreate()"
										id="c_btnCreate2" value=" Create " class="light-btn"
										tabindex="6"> <input type="reset"
										name="c_btnDeletePolicy"
										onclick="javascript:location.href='<%=basePath%>/listNetServerInstance.do?/>'"
										value="Cancel" class="light-btn" tabindex="7"></td>
									<td class="btns-td" valign="middle">&nbsp;</td>
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


