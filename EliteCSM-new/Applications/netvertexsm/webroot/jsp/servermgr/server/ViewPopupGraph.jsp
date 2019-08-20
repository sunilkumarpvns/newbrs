<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ViewPopupGraphForm" %>
<%
	List<String> lstGraphList = (List<String>)request.getAttribute("lstGraphList");
	List<String> lstTitleList = (List<String>)request.getAttribute("lstTitleList");
	System.out.println("Graph List : "+lstGraphList);
	String netServerId = (String)request.getAttribute("netServerId");
    
    
	ViewPopupGraphForm viewPopupGraphForm=(ViewPopupGraphForm)request.getAttribute("viewPopupGraphForm");
%>
<script>

var timerID = null;
var timerRunning = false;
var refreshTime = <%=viewPopupGraphForm.getRefreshTime()%>;
	
function ajaxFunction()
{
	document.forms[0].submit();  
}
function renderImage()
{
	timerID = window.setTimeout('ajaxFunction()', refreshTime*1000);
	timerRunning = true;
}
function refreshImage() 
{
	refreshTime = document.viewPopupGraphForm.refreshTime.value;
	if(timerID == null)
	{
		timerID = window.setTimeout('ajaxFunction()',refreshTime*1000);
	}else
	{
		timerID = window.clearTimeout(timerID)
		timerID = window.setTimeout('ajaxFunction()',refreshTime*1000);
	}
}
function refreshInstantImage()
{
	
	refreshTime = document.viewPopupGraphForm.refreshTime.value;
	if(timerID == null)
	{
		timerID = window.setTimeout('ajaxFunction()',refreshTime*1000);
	}else
	{
		timerID = window.clearTimeout(timerID); 
		timerID = window.setTimeout('ajaxFunction()',refreshTime*1000);
	}
}
document.onload = renderImage();
</script>
<html:form action="<%=viewPopupGraphForm.getActionName()%>">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<html:hidden name="viewPopupGraphForm" styleId="netServerId" property="netServerId"/>
<html:hidden name="viewPopupGraphForm" styleId="entityId" property="entityId"/>
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  
  <tr>
    <td width="2" height="179">&nbsp;</td>
    <td width="100%" valign="top" class="box" height="179">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" height="140">
		<tr>
          <td height="2" class="table-header">GRAPH </td>
        </tr>
		<tr>
		  <td class="small-gap" height="2">&nbsp;</td>
		</tr>
			<tr>
	          <td valign="top" >
	            <table width="97%" border="0" cellspacing=0 cellpadding=0 height="75" align="right">
	            	<tr>
	            	 &nbsp;	
	    	        <td align="left" class="labeltext" valign="top"><bean:message bundle="servermgrResources" key="servermgr.graphchartdetail"/>
	    	        &nbsp;
				    <html:select name="viewPopupGraphForm" styleId="status" property="status" onchange="ajaxFunction();">
							<html:option value="0"><bean:message bundle="servermgrResources" key="servermgr.dailygraphdetails"/></html:option>
				    		<html:option value="1"><bean:message bundle="servermgrResources" key="servermgr.weeklygraphdetails"/></html:option>
				    		<html:option value="2"><bean:message bundle="servermgrResources" key="servermgr.monthlygraphdetails"/></html:option>
				    		<html:option value="3"><bean:message bundle="servermgrResources" key="servermgr.yearlygraphdetails"/></html:option>
				    </html:select> 
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<bean:message bundle="servermgrResources" key="servermgr.autorefreshtime"/>
			    		&nbsp;
					 	<html:select name="viewPopupGraphForm" styleId="refreshTime" property="refreshTime" onchange="refreshImage();" >
				    		<% for(int i=6;i<=30;i++) { %>
							<html:option value="<%=""+i%>"><bean:message bundle="servermgrResources" key="<%="servermgr.dailygraphdetails"+i%>" /></html:option>
				    		<%} %>
				    	</html:select>
				    </td>
				    <td align="right" >	   
			    		<input type="button" value="Refresh" onclick="ajaxFunction();"/>
			    	</td>
			    </tr>
	               <tr><td colspan="5">&nbsp;</td></tr>
		            <tr> 
					  <td class="tblheader-bold" colspan="5" height="20%"><%=viewPopupGraphForm.getGraphTitle()%></td>
					  <td colspan="1">&nbsp;</td>
		           
		            </tr>
		            <tr>
				      <td class="small-gap" colspan="5" >&nbsp;</td>
				    </tr>
		      		<%for(int i=0;i<lstGraphList.size();i++) { %>				    
		      		<tr>

			      	<td valign="top" align="left" colspan="5">
							<img id="graphImage0" name="graphImage0" src="<%=basePath%>/servlet/ImageFormationServlet?img=<%=lstGraphList.get(i)%>" /> 
			      	</td>

		      		</tr>
			      	<%}%>		      		
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
