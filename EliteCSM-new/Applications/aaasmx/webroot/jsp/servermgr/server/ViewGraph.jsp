<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData" %>




<%@ page import="com.elitecore.elitesm.web.servermgr.server.forms.ViewGraphForm"%>
<%
	String base = request.getContextPath();
	
    INetServerInstanceData netServerData = (INetServerInstanceData)request.getAttribute("netServerInstanceData");
	String packetReceivedSummary         = base+"/viewPacketReceivedSummary.do?netserverid="+netServerData.getNetServerId();
	String ladapDataSource				 = base+"/viewLDAPSummary.do?netserverid="+netServerData.getNetServerId(); 
	String responseTime                  = base+"/viewResponseTime.do?netserverid="+netServerData.getNetServerId();
	System.out.println("Server ID : "+netServerData.getNetServerId());
%>
<%
	ViewGraphForm viewGraphForm = (ViewGraphForm)request.getAttribute("viewGraphForm");
	List listEntity = ((ViewGraphForm)request.getAttribute("viewGraphForm")).getListEntity();
	int iIndex = 0;

%>

<script type="text/javascript">

	function popupGraph(url, winName){
	
		//	var win1 = window.open('viewResponseTimePopupGraph.do?netserverid=<%=netServerData.getNetServerId()%>','CSVWin1','top=100, left=100, height=450, width=800, scrollbars=yes, status');
		var win1 = window.open(url,winName,'top=100, left=100, height=450, width=800, scrollbars=yes, status');
		win1.focus();
	
	}
	function popup(mylink, windowname)
	{
		
		if (! window.focus)
			return true;
		
		var href;
		if (typeof(mylink) == 'string')
			href=mylink;
		else
			href=mylink.href;
						
		windowname= windowname.substring(14,windowname.length - 4);					
		windowname='test';

		var winDic = window.open(href, windowname, 'width=800,height=450,left=100,top=100,scrollbars=yes');
		winDic.focus();
		
		return false;
	}

</script>

<html:form action="/viewGraph">
<html:hidden name="viewGraphForm" styleId="netServerId" property="netServerId"/>
	<table border="0" width="100%" cellspacing="0" cellpadding="0">
	
		<tr>
			<td class="table-header" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.graph"/></td>
		</tr>
		<tr>
	    	<td class="btns-td" valign="middle" colspan="3">
	    		<table cellpadding="0" cellspacing="0" border="0" width="100%" >
	    			
	    			<tr> 
    					<td>&nbsp;</td>
  					</tr>
  					
  					
  					
					<tr>
						<td valign="top" align="right"> 
 					<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
 						<tr>
								<td>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
									
										   <tr>	
		                					<td align="left" class="tblheader" valign="top" width="2%"><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
		                    				<td align="left" class="tblheader" valign="top"><bean:message bundle="servermgrResources" key="servermgr.graph.entity.name.label"/></td>
		                    				
		                			   </tr>
		                			   
		                			   <%
										if(listEntity != null){
									   %>
									   		
									   	<logic:iterate id="liveGraphdata" name="viewGraphForm" property="listEntity" type="com.elitecore.elitesm.web.servermgr.server.forms.LiveGraphData">
											<tr>
								                  <td align="left" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
											      <td align="left" class="tblcol" valign="top">
								                      	<a href='<%=base%>/popUpGraph.do?entityId=<bean:write name="liveGraphdata" property="id"/>&netserverid=<%=netServerData.getNetServerId() %>' onClick="return popup(this, '<%=liveGraphdata.getId()%>')"><bean:write name="liveGraphdata" property="name"/></a>
								                  </td>
								            </tr>                
											<% iIndex += 1; %>
							  			 </logic:iterate>
									   
									   
									   <%
									   }else{
									   %>
									   		<tr>
						     						<td align="center" class="tblfirstcol" colspan="2"><bean:message bundle="servermgrResources" key="servermgr.norecordsfound"/></td>
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
						
					
					
					
	    			
	    			<%--<tr>
	                	<td align="center" class="tblheader" valign="top" width="2%" ><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
	                   	<td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="servermgrResources" key="servermgr.graphname"/></td>
	                </tr>	
	           		<tr>
		                <td align="center" class="tblfirstcol" valign="top" width="2%" ><bean:message bundle="servermgrResources" key="servermgr.one"/></td> 
		               <td align="left" class="tblcol" valign="top" width="25%" ><a href="javascript:void(0)"  onclick="popupGraph('viewResponseTimePopupGraph.do?netserverid=<%=netServerData.getNetServerId()%>','CSVWin1')"><bean:message bundle="servermgrResources" key="servermgr.responsetime"/></a></td> 
	                 </tr>	
	                 <tr>
		                <td align="center" class="tblfirstcol" valign="top" width="2%" ><bean:message bundle="servermgrResources" key="servermgr.two"/></td> 
		               <td align="left" class="tblcol" valign="top" width="25%" ><a href="javascript:void(0)"  onclick="popupGraph('viewPacketReceivedPopupGraph.do?netserverid=<%=netServerData.getNetServerId()%>','CSVWin2')"><bean:message bundle="servermgrResources" key="servermgr.requestdistribution"/></a></td> 
	                 </tr>
	                 <tr>
		                <td align="center" class="tblfirstcol" valign="top" width="2%" ><bean:message bundle="servermgrResources" key="servermgr.three"/></td> 
		               <td align="left" class="tblcol" valign="top" width="25%" ><a href="javascript:void(0)"  onclick="popupGraph('viewQueueSizePopupGraph.do?netserverid=<%=netServerData.getNetServerId()%>','CSVWin3')"><bean:message bundle="servermgrResources" key="servermgr.queuesize"/></a></td> 
	                 </tr>
	                 <tr>
		                <td align="center" class="tblfirstcol" valign="top" width="2%" ><bean:message bundle="servermgrResources" key="servermgr.four"/></td> 
		               <td align="left" class="tblcol" valign="top" width="25%" ><a href="javascript:void(0)"  onclick="popupGraph('viewTotalDuplicateReqPopupGraph.do?netserverid=<%=netServerData.getNetServerId()%>','CSVWin4')"><bean:message bundle="servermgrResources" key="servermgr.duplicate"/></a></td> 
	                 </tr>
	
				 --%></table>
			 </td>
	      </tr>
	</table>	
</html:form>	



 

