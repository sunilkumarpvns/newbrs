<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="com.elitecore.elitesm.web.servermgr.service.forms.ViewServiceGraphForm"%>





<%
ViewServiceGraphForm charForm = (ViewServiceGraphForm)request.getAttribute("viewServiceGraph");
List chartList = charForm.getChartList();

int index=1;

%>

<script>
function popup(mylink, windowname)
{
	
	if (! window.focus)return true;
		var href;
	if (typeof(mylink) == 'string')
					href=mylink;
	else
					href=mylink.href;
					
	//alert(mylink)
	window.open(href, windowname, 'width=850,height=450,left=150,top=100,scrollbars=yes');
	
	return false;
} 




</script>
	<table cellpadding="0" cellspacing="0" border="0" width="100%" >    
	<tr>
     <td valign="top" align="right">  
       <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
       	  <tr>
		  	<td>&nbsp;</td>
		  </tr>
	       <tr> 
			<td class="tblheader-bold" colspan="3">Online Graphs</td>
		  </tr>		

			<tr>
				<td colspan="3">
					<table name="c_tblCrossProductList" width="100%" border="0" cellpadding="0" cellspacing="0" >
					
					<tr>
					<td class="tblheader" width="5%"><bean:message key="general.serialnumber"/></td>
					<td class="tblheader" width="35%"><bean:message key="general.name" /></td>
					<td class="tblheader" ><bean:message key="general.description" /></td>
					</tr>
					<%if(chartList!=null){ %>
					<logic:iterate id="chartTypeBean" name="viewServiceGraph" property="chartList" type="com.elitecore.elitesm.web.servermgr.ChartTypeBean">
						
						<tr>
						<td width="5%" class="tblfirstcol" valign="top"><%=index%></td>
					    <td width="20%"  class="tblrows"><a href="<%=basePath%>/jsp/livemonitoring/livemonitoring.jsp?serverId=<%=chartTypeBean.getServerId()%>&graphType=<%=chartTypeBean.getChartType()%>" onclick="return popup(this, '<%=chartTypeBean.getChartType()%>')"><bean:write name="chartTypeBean" property="name"/></a></td>
					    <td  class="tblrows"><%=EliteUtility.formatDescription(chartTypeBean.getDescription())%>&nbsp;</td>
						<%index++;%>
						<tr>
						
					</logic:iterate>
					 <%}else{ %>
					 <tr>
					 <td class="tblfirstcol" colspan="3" align="center" >No Record Found.</td>
					 </tr>
					 <%} %>
					 
					</table>
					 <table>
					 	<div id="mydiv" style="display: none" title="Graph">
					 	
					 	</div>
					 </table>
				</td>
			</tr>
					
	</table>
			</td>
			</tr>
				<tr> 
      <td>&nbsp;</td>
    </tr>
	</table>