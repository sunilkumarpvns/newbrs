<jsp:directive.page import="com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestParamData"/>
<jsp:directive.page import="java.util.Set"/>
<jsp:directive.page import="java.util.List"/>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%> 

<%
    String basePath = request.getContextPath(); 
    Set radParamList = (Set)request.getAttribute("radParamList");
    List hostNameList = (List)request.getAttribute("hostNameList");    
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script> 
function popup(mylink, windowname)
			{
			
				if (! window.focus)return true;
					var href;
				if (typeof(mylink) == 'string')
   					href=mylink;
				else
   					href=mylink.href;
   					
   				href += "hostAddress="+document.forms[0].hostAddress.value;

				window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
				
				return false;
			}
		setTitle('RadiusTest');       
 </script>
 
 <html:form action="/sendRadiusPacket">
  <html:hidden styleId="hostAddress" property="hostAddress"/>
 
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
  			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  				&nbsp;
  			</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td class="table-header" colspan="4">VIEW RADIUS PACKET</td>
                                        </tr>
                                        <tr >
                                                <td class="small-gap" colspan="3" >&nbsp;</td> 
                                         </tr> 
                                          <tr> 
                                                <td colspan="3" >
                                                        <table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%" >
                                                         <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="25%" ><strong>Packet Name</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <bean:write name="viewRadiusTestForm" property="name"/>
                                                              </td>
                                                        </tr>
                                                        <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>Radius Server Host</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <bean:write name="viewRadiusTestForm" property="adminHost"/>
                                                              </td>
                                                        </tr>
                                                        <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>Radius Server Port</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <bean:write name="viewRadiusTestForm" property="adminPort"/>
                                                              </td>
                                                        </tr>
                                                        <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>Reply Timeout (sec)</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <bean:write name="viewRadiusTestForm" property="reTimeOut"/>
                                                              </td>
                                                        </tr>
                                                         <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>Retries</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <bean:write name="viewRadiusTestForm" property="retries"/>
                                                              </td>
                                                        </tr>
                                                        
                                                         <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>Radius Secret Key</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
                                                                       <bean:write name="viewRadiusTestForm" property="scecretKey"/>
                                                              </td>
                                                        </tr>
                                                        <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>Chap Password</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
																	<logic:equal name="viewRadiusTestForm" property="isChap" value="Y">
																		<img  src="<%=basePath%>/images/tick.jpg"  alt="View"  border="0" >
																	</logic:equal>
																	<logic:equal name="viewRadiusTestForm" property="isChap" value="N">
																		<img  src="<%=basePath%>/images/cross.jpg"  alt="View"  border="0" >
																	</logic:equal>
																	
                                                              </td>
                                                        </tr>
                                                        <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>User Name</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
																			 <bean:write name="viewRadiusTestForm" property="userName"/>
                                                              </td>
                                                        </tr>
                                                         <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>Request Type</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
																			 <bean:write name="viewRadiusTestForm" property="serviceType"/>
																			 <logic:equal name="viewRadiusTestForm" property="serviceType" value="User Define">
																			 	[<strong><bean:write name="viewRadiusTestForm" property="requestType"/></strong>]
																			 </logic:equal>
                                                              </td>
                                                        </tr>
                                                        
                                                        <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>Client Ip</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
																     <bean:write name="viewRadiusTestForm" property="hostAddress"/>			 
                                                              </td>
                                                        </tr>
                                                        
                                                        <tr > 
                                                              <td align="left" class="captiontext" valign="top" width="18%" ><strong>Client Port</strong></td> 
                                                              <td align="left" class="labeltext" valign="top" colspan="2" >
																			 <bean:write name="viewRadiusTestForm" property="hostPort"/>
                                                              </td>
                                                        </tr>
                                                        
   
                                                        <tr > 
                                                              <td align="left" class="labeltext" colspan="3" valign="top"> &nbsp; </td> 
                                                        	</tr>
                                                         <% if(radParamList != null && radParamList.size() > 0) { %>                                                    
                                                        <tr>	
																<td align="center" class="labeltext" colspan="3" valign="top">
																	<table width="98%" cellpadding="0" cellspacing="0" border="0">
																		<tr>
								  											  <td align="left" class="tblheader" valign="top" width="10%" >Sr. No.</td>
																			  <td align="left" class="tblheader" valign="top" width="45%" >Parameter Name</td>
																			  <td align="left" class="tblheader" valign="top" width="45%" >Parameter Value</td>
																		</tr>
																		<%	int index = 0; %>
																		<logic:iterate id="objRadParamData" name="radParamList" type="RadiusTestParamData" >
																		<tr>
								  												<td align="left" class="tblfirstcol"><%=(index+1)%></td>
								  												<td align="left" class="tblrows"><bean:write name="objRadParamData" property="name"/></td>
								  												<td align="left" class="tblrows"><bean:write name="objRadParamData" property="value"/></td>
								  										</tr>
								  										<% index++; %>
								  										</logic:iterate>
								  									</table>
								  								</td>				
								  							</tr>
								  							<% } %>	
                                                          	<tr > 
                                                              <td align="left" class="labeltext" colspan="3" valign="top"> &nbsp; </td> 
                                                        	</tr>
                                                         </table> 
                                                         <table width="100%">
																		<tr>
																			<%String id = request.getParameter("fieldId");%>
																			<td align="center" class="btns-td" valign="middle" width="50%"> 
																				<input type="button" value="    Edit    "  class="light-btn" onclick="javascript:location.href='<%=basePath%>/initUpdateRadiusPacket.do?fieldId=<%=id%>'" /> 
																				<input type="button" value="  Send  "  class="light-btn" onClick="return popup('<%=basePath%>/sendRadiusPacket.do?','notes')"/> 
																			</td>
																			<td width="50%">&nbsp;
																			</td>
																		</tr>
														</table>				
                                                </td> 
                                        </tr> 
                              </table>
                     </td>
               </tr>
        <%@ include file="/jsp/core/includes/common/Footer.jsp" %>
</table>
</td>
</tr>
</table>		
</html:form>