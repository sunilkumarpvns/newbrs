<%@ page import="java.util.List"%>
<%@ page
	import="com.elitecore.elitesm.web.servermgr.server.forms.ViewCDRRecordsForm"%>
<%@ page import="java.util.ArrayList"%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<script>
function editCDRRecords(index){
	alert('Press the button value of index is '+index);
	window.open('/editCDRRecords.do?index='+index,'ViewCDRRecord','top=100, left=200, height=300, width=600, scrollbars=yes, status');
}	
</script>
<html:form action="/viewCDRRecords">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="tblheader-bold" colspan="3"><bean:message
								bundle="servermgrResources" key="servermgr.listcdrdetails" /></td>
					</tr>
				</table>
			</td>
		</tr>

		<tr>
			<td valign="middle" colspan="3" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="97%">
					<tr>
						<td class="small-gap" colspan="3">&nbsp;</td>
					</tr>

					<logic:equal name="viewCDRRecordsForm" property="errorCode"
						value="0">
						<%
	List lstCDRRecordsList = ((ViewCDRRecordsForm)request.getSession().getAttribute("viewCDRRecordsForm")).getCDRRecordsList();
	List lstCDRColumns = (List)lstCDRRecordsList.get(0);
	List lstCDRColumnContents = new ArrayList();
	for(int i=1;i<lstCDRRecordsList.size();i++){
		lstCDRColumnContents.add(lstCDRRecordsList.get(i));
	}
%>

						<tr>
							<% 
			  for(int i=0;i<lstCDRColumns.size() && i<5;i++){ %>
							<td align="center" valign="top" width="5%" class="tblheader">
								<%=lstCDRColumns.get(i).toString()%>
							</td>
							<% } %>
							<td align="center" class="tblheader" valign="top" width="5%"><bean:message
									bundle="servermgrResources" key="servermgr.edit" />
						</tr>
						<%

			  	for(int j=1;j<lstCDRRecordsList.size()  && j<100;j++) {
			  		List lstCDR = (List)lstCDRRecordsList.get(j);
		   %>
						<tr>
							<% 
		       for(int k=0;k<lstCDRColumns.size() && k<5;k++){ %>
							<%if(k == 0){ %>
							<td align="center" valign="top" width="5%" class="tblfirstcol">
								<%=(String)lstCDR.get(k)%>
							</td>

							<% } else { %>
							<td align="center" valign="top" width="5%" class="tblrows">
								<%=(String)lstCDR.get(k)%>
							</td>

							<% 
			   		}
			   	  } 
			   %>
							<%--			   <td align="center" class="tblrows"><a href="<%=basePath%>/initViewCDRRecords.do"> <img src="<%=basePath%>/images/edit.jpg" border="0" /> </a></td>  
				   <td align="center" class="tblrows"><a href="<%=basePath%>/initViewCDRRecords.do?index=<%=j%>"><img src="<%=basePath%>/images/edit.jpg" border="0" /></a></td>  
				   <td align="center" class="tblrows"><a href="window.open('/checkIPPool.do?ippoolid='IPP0090','CheckIPPoolWin','top=100, left=200, height=300, width=600, scrollbars=yes, status')"><img src="<%=basePath%>/images/edit.jpg" border="0" /></a></td>--%>
							<td align="center" class="tblrows"><img
								src="<%=basePath%>/images/edit.jpg" border="0"
								onclick="editCDRRecords(<%=j%>)" /></td>
						</tr>
						<%
				}
		  %>
					</logic:equal>
					<logic:notEqual name="viewCDRRecordsForm" property="errorCode"
						value="0">
						<tr>

							<td class="blue-text-bold"><bean:message
									bundle="servermgrResources" key="servermgr.connectionfailure" /><br>
								<bean:message bundle="servermgrResources"
									key="servermgr.admininterfaceip" /> : <bean:write
									name="netServerInstanceData" property="adminHost" /><br>
								<bean:message bundle="servermgrResources"
									key="servermgr.admininterfaceport" /> : <bean:write
									name="netServerInstanceData" property="adminPort" /> &nbsp;</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
					</logic:notEqual>
				</table>
			</td>
		</tr>

	</table>
</html:form>