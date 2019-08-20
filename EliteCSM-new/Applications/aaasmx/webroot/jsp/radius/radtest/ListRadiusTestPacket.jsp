<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.List"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestData"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
	List lstRadiusTest = (List)request.getAttribute("radiusTestList");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script language="javascript">

	
	function  checkAll(){
	 	if( document.forms[0].toggleAll.checked == true) {
	 		var selectVars = document.getElementsByName('select');
	 		
		 	for (i = 0; i < selectVars.length;i++)
				selectVars[i].checked = true ;
				
	    } else if (document.forms[0].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('select');	    
	 		
			for (i = 0; i < selectVars.length; i++)
				selectVars[i].checked = false ;
		}
	}
	
	function submitRemove() {
		var selectFlag = false;
		var selectVars = document.getElementsByName('select');
		
		for (i = 0; i < selectVars.length;i++) {
			if(selectVars[i].checked == true) {
				selectFlag = true;
				break;
			}
		}
		
		if(!selectFlag) {
			alert("Select at least one packet to be deleted");
		} else {		
			document.forms[0].checkAction.value = 'Delete';
			document.forms[0].submit();
		}	
	}
	setTitle('RadiusTest');
</script>

<html:form action="/miscRadiusPacket">
	<html:hidden property="checkAction" />
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
									<td class="table-header" colspan="4">RADIUS PACKET LIST</td>
								</tr>

								<tr>
									<td class="btns-td" valign="middle"><input type="button"
										name="c_btnCreate"
										onclick="javascript:location.href='<%=basePath%>/initCreateRadiusPacket.do'"
										value="Create" class="light-btn" /> <input type="button"
										onclick="submitRemove()" name="Delete" value="Delete"
										class="light-btn" /></td>
									<td width="50%">&nbsp;</td>
								</tr>

								<tr>
									<td class="btns-td" valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td class="small-gap" colspan="3">&nbsp;</td>
											</tr>

											<tr>
												<td colspan="3">
													<table width="99%" cols="8" id="netServerList"
														type="tbl-list" border="0" cellpadding="0" cellspacing="0">
														<tr>
															<td align="center" class="tblheader" valign="top"
																width="4%"><input type="checkbox" name="toggleAll"
																onclick="checkAll()" /></td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Sr.No.</td>
															<td align="left" class="tblheader" valign="top"
																width="15%">Packet Name</td>
															<td align="left" class="tblheader" valign="top" width="*">
																Server Host</td>
															<td align="left" class="tblheader" valign="top"
																width="10%">Server Port</td>
															<td align="left" class="tblheader" valign="top"
																width="10%">User Name</td>
															<td align="center" class="tblheader" valign="top"
																width="40px">Edit</td>
														</tr>

														<%int iIndex = 0;%>
														<% if (lstRadiusTest != null && lstRadiusTest.size() > 0) { %>

														<logic:iterate id="objRadiusData" name="radiusTestList"
															type="RadiusTestData">
															<tr>
																<td align="center" class="tblfirstcol" valign="top">
																	<input type="checkbox" name="select"
																	value="<bean:write name="objRadiusData" property="ntradId"/>" />
																</td>

																<td align="center" class="tblfirstcol" valign="top">
																	<%=(iIndex + 1)%>
																</td>

																<td align="left" class="tblrows" valign="top"><a
																	href="viewRadiusPacket.do?fieldId=<bean:write name="objRadiusData" property="ntradId"/>"><bean:write
																			name="objRadiusData" property="name" /></a></td>

																<td align="left" class="tblrows" valign="top"><bean:write
																		name="objRadiusData" property="adminHost" /></td>

																<td align="left" class="tblrows" valign="top"><bean:write
																		name="objRadiusData" property="adminPort" /></td>

																<td align="left" class="tblrows" valign="top"><bean:write
																		name="objRadiusData" property="userName" /></td>

																<td align="center" class="tblrows" valign="center">
																	<a
																	href="initUpdateRadiusPacket.do?fieldId=<bean:write name="objRadiusData" property="ntradId"/>"><img
																		src="<%=basePath%>/images/edit.jpg" alt="Edit"
																		border="0"></a>
																</td>
															</tr>
															<% iIndex += 1;%>
														</logic:iterate>

														<% } else {	%>

														<tr>
															<td align="center" class="tblfirstcol" colspan="8">
																No Records Found.</td>
														</tr>

														<% } %>

													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle"><input type="button"
										name="c_btnCreate"
										onclick="javascript:location.href='<%=basePath%>/initCreateRadiusPacket.do'"
										value="Create" class="light-btn" /> <input type="button"
										onclick="submitRemove()" name="Delete" value="Delete"
										class="light-btn" /></td>

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