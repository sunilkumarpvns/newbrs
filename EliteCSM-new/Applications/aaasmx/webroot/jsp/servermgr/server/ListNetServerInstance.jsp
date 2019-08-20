<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerInstanceForm"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
    List<NetServerInstanceData> netServerTypeList = (List<NetServerInstanceData>)request.getAttribute("netServerTypeList");
    List<NetServerInstanceData> netServerInstanceList = (List<NetServerInstanceData>)request.getAttribute("netServerInstanceList");
    String bLicenseAlert = (String)request.getAttribute("bLicenseAlert");
    String bLicenseAlertForPopup = (String)request.getAttribute("bLicenseAlertForPopup");
    String bLicenseExpireAlert = (String)request.getAttribute("bLicenseExpireAlert");
%>

<script>		
	function removeSMInstance(){
		var isServerSelected = false;
		$("#listServerInstance input[type='checkbox'][name='select']").each(function(){
			if($(this).attr('checked')){
				isServerSelected = true;
				return false;
			}
		});
		if(!isServerSelected){
			alert("At least select Server Instance for remove process");
			return false;
		}
	    var msg;
	    msg = "Are you sure that you want to delete selected Server(s) ?" ;
	    var agree = confirm(msg);
	    if(agree){
			document.forms[0].action.value = 'delete';
			document.forms[0].submit();
		}
	}
	function create(){
		document.forms[0].action.value = 'create';
		document.forms[0].submit();
	}
	function  checkAll(){
	 	if( document.forms[0].toggleAll.checked == true) {
	 		var arrayCheck = document.getElementsByName('select');
		 	for (var i = 0; i < arrayCheck.length ;i++){
		 		arrayCheck[i].checked = true ;
		 	}
	    } else if (document.forms[0].toggleAll.checked == false){
	    	var arrayCheck = document.getElementsByName('select');
			for (var i = 0; i < arrayCheck.length ; i++)
				arrayCheck[i].checked = false ;
		}
	}
	/* setTitle('<bean:message bundle="servermgrResources" key="servermgr.header"/>');	 */
</script>
<%
	List lstNetServerInstanceData = ((ListNetServerInstanceForm) request.getAttribute("listNetServerInstanceForm")).getListServer();
	int iIndex = 0;
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/miscServer" styleId="listServerInstance">
	<html:hidden styleId="" property="checkboxname" value="c_bSelected" />
	<html:hidden name="listNetServerInstanceForm" styleId="" property="action" />
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="4">
										<bean:message bundle="servermgrResources" key="servermgr.serverlist" />
									</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle">
										<input type="button" name="c_btnCreate" onclick="javascript:location.href='<%=basePath%>/initCreateServer.do'" value="Create" class="light-btn" /> 
										<html:button property="c_btnDelete" onclick="removeSMInstance()" value="Delete" styleClass="light-btn" />
									</td>
									<td width="50%">&nbsp;</td>
								</tr>
								<tr>
									<td valign="middle" colspan="3" align="center">
										<table cellpadding="0" cellspacing="0" border="0" width="97%">
											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td colspan="3">
													<table width="100%" cols="8" id="netServerList"
														type="tbl-list" border="0" cellpadding="0" cellspacing="0">
														<tr>
															<td align="center" class="tblheader" valign="top"
																width="5%">
																<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()" />
															</td>
															<td align="center" class="tblheader" valign="top" width="40px">
																<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
															</td>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="servermgrResources" key="servermgr.servername" />
															</td>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="servermgrResources" key="servermgr.address" />
															</td>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="servermgrResources" key="servermgr.servertype" />
															</td>
															<td align="left" class="tblheader" valign="top" width="*">Remark</td>
														</tr>
														<%
											if (lstNetServerInstanceData != null && lstNetServerInstanceData.size() > 0) {
											%>
														<logic:iterate id="netServerInstanceData" name="listNetServerInstanceForm" property="listServer" type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData">
															<tr>
																<td align="center" class="tblfirstcol" valign="top">
																	<input type="checkbox" name="select" value="<bean:write name="netServerInstanceData" property="netServerId"/>" />
																</td>
																<td align="center" class="tblrows" valign="top"><%=(iIndex + 1)%>
																</td>
																<td align="left" class="tblrows" valign="top">
																	<a href="<%=basePath%>/viewNetServerInstance.do?netserverid=<bean:write name="netServerInstanceData" property="netServerId"/>">
																		<bean:write name="netServerInstanceData" property="name" />
																</td>
																<td align="left" class="tblrows" valign="top">
																	<bean:write name="netServerInstanceData" property="adminHost" />:
																	<bean:write name="netServerInstanceData" property="adminPort" />
																</td>

																<td align="left" class="tblrows" valign="top">
																	<logic:iterate id="netServerTypeData" name="netServerTypeList" type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData">
																		<logic:equal name="netServerTypeData" property="netServerTypeId" value="<%=netServerInstanceData.getNetServerTypeId()%>">
																			<bean:write name="netServerTypeData" property="name" />
																		</logic:equal>
																	</logic:iterate>
																</td>
																<td align="left" class="tblrows" valign="top" style="color: red">
																	<%if(netServerInstanceData.getLicenseExpiryDays() != null && netServerInstanceData.getLicenseExpiryDays() <= 30 && netServerInstanceData.getLicenseExpiryDays() > 1) {%>
																		<bean:write name="netServerInstanceData" property="licenseExpiryDays" />&nbsp;days remained toexpire the license. 
																	<%}else if(netServerInstanceData.getLicenseExpiryDays() != null && netServerInstanceData.getLicenseExpiryDays() == 1){%>
																		<bean:write name="netServerInstanceData" property="licenseExpiryDays" />&nbsp;day remained to expire the license. 
																	<%}else if(netServerInstanceData.getLicenseExpiryDays() != null && netServerInstanceData.getLicenseExpiryDays() < 0){%>
																		License Expired 
																	<%}else if(netServerInstanceData.getLicenseExpiryDays() != null && netServerInstanceData.getLicenseExpiryDays() == 0){%>
																		License Will be Expire Today&nbsp; 
																	<%}else{%> &nbsp; <%}%>
																</td>
															</tr>
															<%iIndex += 1;%>
														</logic:iterate>
														<%} else {%>
														<tr>
															<td align="center" class="tblfirstcol" colspan="8">No Records Found.</td>
														</tr>
														<%}%>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle">
										<input type="button" name="c_btnCreate" onclick="javascript:location.href='<%=basePath%>/initCreateServer.do'" value="Create" class="light-btn" /> 
										<html:button property="c_btnDelete" onclick="removeSMInstance()" value="Delete" styleClass="light-btn" /></td>
									<td width="50%">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-gap" width="50%" colspan="2">&nbsp;</td>
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
<script>
setTitle('<bean:message bundle="servermgrResources" key="servermgr.header"/>');	
</script>