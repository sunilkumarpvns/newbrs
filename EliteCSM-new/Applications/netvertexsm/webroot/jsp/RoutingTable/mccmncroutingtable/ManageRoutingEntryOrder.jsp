<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.mccmncroutingtable.form.RoutingTableManagementForm"%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

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

<script language = "javascript">

$(document).ready(function(){
	$('#listTable').tableDnD();
});
function submit(){
	document.forms[0].submit();
}
</script>

<%
     RoutingTableManagementForm routingTableManagementForm = (RoutingTableManagementForm) request.getAttribute("routingTableManagementForm");
	 List routingEntryList = routingTableManagementForm.getRoutingTableDataList();
 %>

<html:form action="/routingTableManagement.do?method=manageOrder" >
			
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5"><bean:message  bundle="routingMgmtResources" key="routingtable.manageorder.title"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		    
		  </tr> 			
						<tr>
						<td class="btns-td" valign="middle" colspan="9">
						<table width="300px" border="0" cellpadding="0" cellspacing="0" id="listTable">
						<thead>	
							<tr style='cursor:default'>
							   <td align="center" class="tblheader" style="cursor:default" valign="top" width="25%">
									 <bean:message bundle="routingMgmtResources" key="routingtable.orderno" />
								</td>
								<td align="left" class="tblheader" style="cursor:default" valign="top" width="75%">
									<bean:message bundle="routingMgmtResources" key="routingtable.manageorder.name" />
								</td>
						 </thead>	
							</tr>
<%	
		          
                   if(routingEntryList!=null && routingEntryList.size()>0) { 
%>
					<logic:iterate id="routingTableBean" name="routingTableManagementForm"  property="routingTableDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData">
						<tr>
							<td align="center" class="tblfirstcol" ><bean:write name="routingTableBean" property="orderNumber" />
								<input type='hidden' name='routingEntryId' value="<bean:write name="routingTableBean" property="routingTableId"/>" />
							</td>
							<td align="left" class="tblrows" ><bean:write name="routingTableBean" property="name" />&nbsp;
							</td>
						</tr>
					
					</logic:iterate>												
<%		}else{	%>
					<tr>
						<td align="center" class="tblfirstcol" colspan="7">No Records Found.</td>
					</tr>
<%		}	%>
					</tr>
					</table>
						<label  class="small-text-grey"><bean:message  key="table.ordering.note"/></label> 
					</td>
				</tr>	
			<tr>
				<td colspan="5">&nbsp;</td>
			</tr>
			<tr>
				<td class="btns-td" align="left" valign="top"  >
				 	<input type="button" value=" Save "  class="light-btn" onclick="submit();" />
				 	
				 	<input type="button" value=" Cancel "  class="light-btn" onclick="javascript:location.href='<%=basePath%>/routingTableManagement.do?method=initSearch'"/>
				 	<input type="button" value=" Reset "  class="light-btn" onclick="javascript:location.href='<%=basePath%>/routingTableManagement.do?method=initManageOrder'"/>					
				</td>									
			</tr>
			<tr>
				<td colspan="5">&nbsp;</td>
			</tr>										
		</table>
		</td>
		</tr>
     </table>
  	</td>
	</tr>	   
<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table> 
</html:form>

