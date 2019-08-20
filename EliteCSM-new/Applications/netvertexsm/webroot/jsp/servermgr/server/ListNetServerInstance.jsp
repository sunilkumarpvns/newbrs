<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerTypeData"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ListNetServerInstanceForm"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >

<%
    List netServerTypeList = (List)request.getAttribute("netServerTypeList");
%>

<script>

	$(document).ready(function(){
		$("#c_btnCreate").focus();
		checkAll(document.getElementsByName("toggleAll")[0]);
		
	});	
	
	var checkBoxArray = document.getElementsByName("select");
	function removeRecord(){		
	    var selectVar = false;
	    for (var i = 0; i < checkBoxArray.length; i++){
			if(checkBoxArray[i].checked == true){
				selectVar = true;
			}
	 	}
	    if(selectVar == false){
	        alert('At least select one Server Instance for remove process');
	    }else{		
		    var msg;
		    msg = "Are your sure to delete selected Server(s) ? ";
		    var agree = confirm(msg);
		    if(agree){
				document.forms[0].action.value = 'delete';
				document.forms[0].submit();
			}
	    }
	}
	function create(){
		document.forms[0].action.value = 'create';
		document.forms[0].submit();
	}

	
</script>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	
  	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

	<%
	            List lstNetServerInstanceData = ((ListNetServerInstanceForm) request.getAttribute("listNetServerInstanceForm")).getListServer();
	            int iIndex = 0;
	%>
	<tr>
		<td width="7">
			&nbsp;
		</td>
		<td cellpadding="0" class="box" cellspacing="0" border="0" valign="top" width="100%">
			<html:form action="/miscServer">
				<html:hidden styleId="" property="checkboxname" value="c_bSelected" />
				<html:hidden name="listNetServerInstanceForm" styleId="" property="action" />
				<table width="100%">
					<tr>
						<td class="table-header" colspan="3">
							<bean:message bundle="servermgrResources"
								key="servermgr.serverlist" />
						</td>
					</tr>
					<tr class="vspace">
						<td class="btns-td" valign="middle">
							<input type="button" id="c_btnCreate" name="c_btnCreate"
								onclick="javascript:location.href='<%=basePath%>/initCreateServer.do'"
								value="Create" class="light-btn" />						
							<html:button property="c_btnDelete" onclick="removeRecord()"
								value="Delete" styleClass="light-btn" />
						</td>
						<td width="50%">
							&nbsp;
						</td>
					</tr>
					<tr class="vspace">
						<td class="btns-td" valign="middle" colspan="3">
							<table id="dataTable" cellpadding="0" cellspacing="0" border="0" style="min-width: 100%">
								 
								<tr>
									<td colspan="3">
										<table width="100%" cols="8" id="netServerList"
											type="tbl-list" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="center" class="tblheaderfirstcol" valign="top" width="4%">
													<input type="checkbox" name="toggleAll" value="checkbox"
														onclick="checkAll(this)" />
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message bundle="servermgrResources"
														key="servermgr.servername" />
												</td>
												<td align="left" class="tblheader" valign="top" width="20%">
													<bean:message bundle="servermgrResources"
														key="servermgr.address" />
												</td>
												<td align="left" class="tblheader" valign="top" width="20%">
													<bean:message bundle="servermgrResources"
														key="servermgr.servertype" />
												</td>
												<td align="left" class="tblheaderlastcol" valign="top" width="30%">
													<bean:message bundle="servermgrResources"
														key="servermgr.description" />
												</td>
											</tr>
											<%
											if (lstNetServerInstanceData != null && lstNetServerInstanceData.size() > 0) {
												int i=0;
											%>
											<logic:iterate id="netServerInstanceData"
												name="listNetServerInstanceForm" property="listServer"
												type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData">
												<tr id="dataRow" name="dataRow" >
													<td align="center" class="tblfirstcol" valign="top">
														<input type="checkbox" name="select"  value="<bean:write name="netServerInstanceData" property="netServerId"/>" onclick="onOffHighlightedRow(<%=i++%>,this)"  />
													</td>
													<td align="left" class="tblrows" valign="top">
														<a href="<%=basePath%>/viewNetServerInstance.do?netserverid=<bean:write name="netServerInstanceData" property="netServerId"/>"><bean:write
																name="netServerInstanceData" property="name" />
													</td>
													<td align="left" class="tblrows" valign="top">
														<bean:write name="netServerInstanceData" property="adminHost"/>:<bean:write name="netServerInstanceData" property="adminPort"/>
													</td>

													<td align="left" class="tblrows" valign="top">
														<logic:iterate id="netServerTypeData"
															name="netServerTypeList"
															type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData">
															<logic:equal name="netServerTypeData"
																property="netServerTypeId"
																value="<%=netServerInstanceData.getNetServerTypeId()%>">
																<bean:write name="netServerTypeData" property="name" />
															</logic:equal>
														</logic:iterate>
													</td>
													<td align="left" class="tblrows" valign="top"><%=EliteUtility.formatDescription(netServerInstanceData.getDescription())%>&nbsp;</td>
												</tr>
												<%
												iIndex += 1;
												%>
											</logic:iterate>
											<%
											} else {
											%>
											<tr>
												<td align="center" class="tblfirstcol" colspan="8">
													No Records Found.
												</td>
											</tr>
											<%
											}
											%>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr class="vspace">
						<td class="btns-td" valign="middle">
							<input type="button" name="c_btnCreate"
								onclick="javascript:location.href='<%=basePath%>/initCreateServer.do'"
								value="Create" class="light-btn" />
							<html:button property="c_btnDelete" onclick="removeRecord()"
								value="Delete" styleClass="light-btn" />
						</td>
						<td width="50%">
							&nbsp;
						</td>
					</tr>					 
				</table>
			</html:form>
		</td>
	</tr>
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

