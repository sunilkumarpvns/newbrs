<%@page import="com.elitecore.diameterapi.core.stack.constant.OverloadAction"%>
<%@page
	import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.*"%>

<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@page
	import="com.elitecore.elitesm.web.diameter.routingconfig.forms.SearchDiameterRoutingConfForm"%>

<% 
List lstDiameterRoutingTable = searchDiameterRoutingConfForm.getListDiameterRoutingTable();
int count=1;
int total=0;
%>
<html:form action="/miscDiameterRoutingConf" styleId="miscForm">
	<html:hidden name="searchDiameterRoutingConfForm" styleId="action"
		property="action" />
	<html:hidden name="searchDiameterRoutingConfForm" styleId="pageNumber"
		property="pageNumber" />
	<html:hidden name="searchDiameterRoutingConfForm" styleId="totalPages"
		property="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="searchDiameterRoutingConfForm"
		styleId="totalRecords" property="totalRecords"
		value="<%=strTotalRecords%>" />
	<table width="100%" border="0" cellSpacing="0" cellPadding="0">
		<tr>
			<td align="left">
				<table cellSpacing="0" cellPadding="0" width="99%" border="0">
					<tr>
						<td class="table-header" width="50%"><bean:message
								bundle="diameterResources" key="routingconf.routingtablelist" />
						</td>

						<td align="right" class="blue-text" valign="middle" width="50%">

						</td>
					</tr>
					<tr>

						<td></td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle"><html:button
								property="c_btnDelete" onclick="removeData()" value="   Delete   "
								styleClass="light-btn" /> <html:button property="c_btnshowall"
								onclick="showall()" value="   Show All   "
								styleClass="light-btn" /></td>
						<td class="btns-td" align="right"></td>
					</tr>
					<tr height="2">
						<td></td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="2">
							<table width="100%" border="0" cellpadding="0" cellspacing="0"
								id="listTable">
								<tr>
									<td align="center" class="tblheader" valign="top" width="1%">
										<input type="checkbox" name="toggleAll" value="checkbox"
										onclick="checkAll()" />
									</td>
									<td align="center" class="tblheader" valign="top" width="40px"><bean:message
											bundle="diameterResources" key="routingconf.serialnumber" /></td>
									<td align="left" class="tblheader" valign="top"><bean:message
											bundle="diameterResources" key="routingconf.tablename" /></td>
									<td align="left" class="tblheader" valign="top"><bean:message
											bundle="diameterResources" key="routingconf.overloadaction" /></td>
									<td align="left" class="tblheader" valign="top"><bean:message
											bundle="diameterResources" key="routingconf.resultcode" /></td>
									<td align="left" class="tblheader" valign="top"><bean:message
											bundle="diameterResources" key="routingconf.routingscript" /></td>
									<td align="left" class="tblheader" valign="top"><bean:message
											bundle="diameterResources" key="routingconf.totalrouting" /></td>
									<td align="center" class="tblheader" valign="top" width="40px"><bean:message
											bundle="diameterResources" key="diameterpeerprofile.edit" /></td>
								</tr>
								<%	if(lstDiameterRoutingTable!=null && lstDiameterRoutingTable.size()>0){%>
								<logic:iterate id="diameterRoutingTableBean"
									name="searchDiameterRoutingConfForm"
									property="listDiameterRoutingTable"
									type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData">
									<bean:define id="tableid" name="diameterRoutingTableBean"
										property="routingTableId" type="java.lang.String" />
									<%	
									    total=0;
									%>
									<tr>
										<td align="center" class="tblfirstcol"><input
											type="checkbox" name="select"
											value="<bean:write name="diameterRoutingTableBean" property="routingTableId"/>" />
										</td>
										<td align="center" class="tblrows"><%=count%></td>
										<td align="left" class="tblrows"><bean:write
												name="diameterRoutingTableBean" property="routingTableName" />
											&nbsp;</td>
										<td align="left" class="tblrows"><bean:write
												name="diameterRoutingTableBean" property="overloadAction" />
											&nbsp;</td>
											<logic:equal name="diameterRoutingTableBean" property="overloadAction" value="<%= OverloadAction.REJECT.val %>" >
											<td align="left" class="tblrows"><bean:write
												name="diameterRoutingTableBean" property="resultCode" />
											&nbsp;</td></logic:equal>
											<logic:notEqual name="diameterRoutingTableBean" property="overloadAction" value="<%= OverloadAction.REJECT.val %>" >
											<td align="left" class="tblrows"> 
											&nbsp;</td></logic:notEqual>
										<logic:iterate id="diameterRoutingConfBean"
											name="searchDiameterRoutingConfForm"
											property="listDiameterRoutingConf"
											type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData">
											<logic:equal name="diameterRoutingConfBean"
												property="routingTableId" value="${tableid}">
												<%total=total+1; %>
											</logic:equal>
										</logic:iterate>
										<td align="left" class="tblrows">
											<bean:write name="diameterRoutingTableBean" property="routingScript" />
										</td>
										<td align="left" class="tblrows"><%=total%> &nbsp;</td>
										<td align="center" class="tblrows"><a
											href="<%=basePath%>/diameterRoutingTable.do?actionType=tablewiserouting&routingTableId=<bean:write name="diameterRoutingTableBean" property="routingTableId"/>">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit"
												border="0">
										</a></td>
									</tr>
									<% count=count+1; %>
									<% iIndex += 1; %>
								</logic:iterate>

								<%	}else{
						%>
								<tr>
									<td align="center" class="tblfirstcol" colspan="100%"><bean:message
											bundle="diameterResources"
											key="diameterpeerprofile.norecordsmsg" /></td>
								</tr>
								<%	}%>
							</table>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle"><html:button
								property="c_btnDelete" onclick="removeData()" value="   Delete   "
								styleClass="light-btn" /> <html:button property="c_btnshowall"
								onclick="showall()" value="   Show All   "
								styleClass="light-btn" /></td>
						<td class="btns-td" align="right"></td>
					</tr>
					<tr height="2">
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
