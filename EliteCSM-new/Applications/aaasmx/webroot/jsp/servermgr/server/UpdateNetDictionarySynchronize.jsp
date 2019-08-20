<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page
	import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@page
	import="com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData"%>


<%@ page import="org.apache.struts.util.MessageResources"%>
<%//      MessageResources messageResources = getResources(request,"resultMessageResources");%>
<% 
	String localBasePath = request.getContextPath();

    List<DictionaryData> radiusDictionaryList=(List<DictionaryData>)request.getSession().getAttribute("radiusDictionaryList");
    List<DiameterdicData> diameterDictionaryList=(List<DiameterdicData>)request.getSession().getAttribute("diameterDictionaryList");
    
%>




<script>
	function validateSynchronize(){
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.updatenetdictionarysynchronizejsp.update.query"/>';        
//        msg = "All Services and its Details within this Server would be Updated. Do you like to continue ? "
        var agree = confirm(msg);
        if(agree){
            javascript:location.href='<%=localBasePath%>/synchronizeNetDictionary.do?netserverid=<bean:write name="netServerInstanceBean" property="netServerId"/>';
    		//document.forms[0].submit();
        }
	}
	
	
</script>



<html:form action="/synchronizeNetDictionary">
	<html:hidden name="updateNetDictionarySynchronizeForm" styleId="action"
		property="action" />
	<html:hidden name="updateNetDictionarySynchronizeForm"
		styleId="netServerId" property="netServerId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<bean:define id="netServerInstanceBean" name="netServerInstanceData"
			scope="request"
			type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData" />
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="tblheader-bold" colspan="2"><bean:message
								bundle="servermgrResources"
								key="servermgr.synchronizedictionarydetails" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%"><bean:message
								bundle="servermgrResources" key="servermgr.admininterfaceip" /></td>
						<td align="left" class="labeltext" valign="top" width="32%"><bean:write
								name="netServerInstanceBean" property="adminHost" /> <!--  <input type="text" name="adminip" value="192.168.1.1"/> -->
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="10%"><bean:message
								bundle="servermgrResources" key="servermgr.admininterfaceport" /></td>
						<td align="left" class="labeltext" valign="top" width="32%"><bean:write
								name="netServerInstanceBean" property="adminPort" /> <!-- <input type="text" name="adminport" value="8090" /> -->
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>

					<!--Display Dictionary List -->

					<tr>

						<td align="left" class="labeltext" valign="top" colspan="2">
							<table width="70%" cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td valign="top" class="captiontext">
										<table id="radiusDic" class="box" cellpadding="0"
											cellspacing="0" border="0" width="90%">
											<tr>
												<td colspan="2" class="table-header">Radius Dictionary</td>
											</tr>
											<tr>
												<td>
													<table cellpadding="0" cellspacing="0" width="100%">
														<tr>
															<td class="tblfirstcol">Name</td>
															<td class="tblrows">VendorId</td>
														</tr>
														<logic:iterate id="radiusDicBean"
															name="radiusDictionaryList"
															type="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData">

															<tr>
																<td class="tblfirstcol"><%=radiusDicBean.getName()%></td>
																<td class="tblrows"><%=radiusDicBean.getVendorId()%></td>
															</tr>

														</logic:iterate>

													</table>
												</td>
											</tr>
										</table>
									</td>
									<td valign="top">
										<table id="diameterDic" class="box" cellpadding="0"
											cellspacing="0" width="100%">
											<tr>
												<td colspan="2" class="table-header" align="center">Diameter
													Dictionary</td>
											</tr>
											<tr>
												<td>
													<table cellpadding="0" cellspacing="0" width="100%">
														<tr>
															<td class="tblfirstcol">Name</td>
															<td class="tblrows">VendorId</td>
														</tr>
														<logic:iterate id="diameterDicBean"
															name="diameterDictionaryList"
															type="com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData">

															<tr>
																<td class="tblfirstcol"><%=diameterDicBean.getVendorName() %></td>
																<td class="tblrows"><%=diameterDicBean.getVendorId()%></td>
															</tr>

														</logic:iterate>

													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td></td>
		</tr>

		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%" align="right" class="captiontext">
					<tr>
						<td class="small-text-grey">Note : All Dictionaries within
							this Server would be Updated.</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle"><input type="button"
				name="c_btnSynchronize" onclick="validateSynchronize()"
				id="c_btnSynchronize" value=" Synchronize To" class="light-btn">
				<%-- <input type="button" name="c_btnSynchronizeBack" onclick="validateSynchronizeBack()" id="c_btnSynchronizeBack"  value=" Synchronize From "  class="light-btn">--%>
				<input type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=localBasePath%>/viewNetServerInstance.do?netserverid=<bean:write name="netServerInstanceBean" property="netServerId"/>'"
				value="Cancel" class="light-btn"></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
	</table>
</html:form>