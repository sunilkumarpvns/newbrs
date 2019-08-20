





<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ViewSupportedRFCForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData"%>
<%@ page import="java.util.List"%>
<%
	String localBasePath = request.getContextPath();
	INetServerInstanceData localNetServerInstanceData = (INetServerInstanceData)request.getAttribute("netServerInstanceData");
%>

<%
	ViewSupportedRFCForm viewSupportedRFCForm = (ViewSupportedRFCForm)request.getAttribute("viewSupportedRFCForm");
	List supportedRFC = ((ViewSupportedRFCForm)request.getAttribute("viewSupportedRFCForm")).getSupportedRFC();
	int iIndex = 0;

%>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		
		<tr> 
    		<td>&nbsp;</td>
  		</tr>
  		<logic:equal name="viewSupportedRFCForm"
						property="errorCode" value="0">
  		<tr>
  			<td valign="top" align="right"> 
 					<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
 					
 						<tr>
 							<td>
 								<table width="100%" border="0" cellspacing="0" cellpadding="0">
 									<tr> 
					    				<td class="small-text-grey" colspan="3">Note: The star ( * ) indicates the partial implementation of the RFC.</td>
					  				</tr>		
 									<tr> 
										<td width="2%" class="tblheader-bold" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.view.supportedRFCList"/></td>
							  		</tr>
							  		
		
					                <tr>	
					                	<%--<td align="left" class="tblheader" valign="top" width="1%"><bean:message bundle="servermgrResources" key="servermgr.select"/></td>
					                    --%><td align="left" class="tblheader" valign="top" width="2%"><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
					                    <td align="left" class="tblheader" valign="top" width="15%"><bean:message bundle="servermgrResources" key="servermgr.RFCList.RFC.label"/></td>
					                    <td align="left" class="tblheader" valign="top" width="83%"><bean:message bundle="servermgrResources" key="servermgr.RFCList.RFCdetail.label"/></td>
					                </tr>
					                
					                <%
										if(supportedRFC != null){
									%>
										 <logic:iterate id="liveSNMPRFCdata" name="viewSupportedRFCForm" property="supportedRFC" type="com.elitecore.netvertexsm.web.servermgr.server.form.LiveSNMPRFCData">
									<tr>
						                  <%--<td align="left" class="tblfirstcol" valign="top">
						                  	<input type="checkbox" name="selected" id="<%=(iIndex+1) %>" value="<bean:write name="supportedRFCs" property="name"/>">
						                  </td>
									      --%><td align="left" class="tblfirstcol" valign="top" width="5%"><%=(iIndex+1) %></td>
									      
						                  <td align="left" class="tblrows" valign="top" width="25%">
						                  	<bean:write name="liveSNMPRFCdata" property="name"/>
						                  </td>
						                  <td align="left" class="tblcol" valign="top" width="70%">
						                  	<bean:write name="liveSNMPRFCdata" property="description"/>
						                  </td>
						            </tr>                
									<% iIndex += 1; %>
										   </logic:iterate>
									<%
										}else{
									%>
									<tr>
									     <td align="center" class="tblfirstcol" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.norecordsfound"/></td>
									</tr>
									<%
										}
									%>	
 									<tr> 
					    				<td colspan="3" height="20">&nbsp;</td>
					  				</tr>	

					  				<tr> 
					    				<td colspan="3">
						    				<table class="tblheader" width="100%">
						    					<tr>
						    						<td>
							    						<b><u>Disclaimer:</u></b><br/> 
							    						Actual Standard fulfillment and Functionalities varies depending on network topology, deployment requirement and commercials.
							    					</td>
							    				</tr>
						    				</table>
					    				</td>
					  				</tr>
									<tr> 
					    				<td colspan="3" height="20">&nbsp;</td>
					  				</tr>	
 								</table>
 							</td>
 						</tr>		
 					
 					</table>
       		</td>			
  		
  		</tr>
  		
  		</logic:equal>
  		
  		<logic:notEqual name="viewSupportedRFCForm"
			property="errorCode" value="0">
			
			
		<tr> 
			<td>&nbsp;</td>
		</tr>
  			
  		<tr>
     		<td valign="top" align="right"> 
       			<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >	
					<tr>
						<td class="blue-text-bold">
							<bean:message bundle="servermgrResources"
								key="servermgr.connectionfailure" />
							<br>
							<bean:message bundle="servermgrResources"
								key="servermgr.admininterfaceip" />
							:
							<bean:write name="netServerInstanceData" property="adminHost" />
							<br>
							<bean:message bundle="servermgrResources"
								key="servermgr.admininterfaceport" />
							:
							<bean:write name="netServerInstanceData" property="adminPort" />
							&nbsp;
						</td>
					</tr>
				</table>
			</td>
		</tr>
									
		<tr>
			<td>&nbsp;</td>
		</tr>
		</logic:notEqual> 
		
	</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
