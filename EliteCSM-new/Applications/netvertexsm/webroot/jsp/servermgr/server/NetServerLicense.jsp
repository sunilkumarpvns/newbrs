<%@page import="com.elitecore.license.base.commons.LicenseTypeConstants"%>





<% 
	String localBasePath = request.getContextPath();
	Long netServerId = (Long) request.getAttribute("netServerId");
%>

<script>
    
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%">
       
	        <tr>
			  	<td colspan="5">&nbsp;</td>
			</tr>
			  <tr>
					<td class="tblheader-bold" colspan="5" height="20%">
							<bean:message bundle="servermgrResources" key="servermgr.license" />
					</td>
				</tr>
			 
			<logic:equal name="initNetServerLicenseForm" scope="request" property="errorCode" value="0">
		     			<tr>
							<td align="left" class="tblheader" valign="top" width="5%">
								<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
							</td>
							<td align="left" class="tblheader" valign="top" width="20%">
								<bean:message bundle="servermgrResources" key="servermgr.license.name" />
							</td>
							<td align="left" class="tblheader" valign="top" width="7%">
								<bean:message bundle="servermgrResources" key="servermgr.license.version" />
							</td>
							<td align="left" class="tblheader" valign="top" width="30%">
								<bean:message bundle="servermgrResources" key="servermgr.license.validity" />
							</td>
						</tr>
						
						<%
						int iIndex = 0;
						%>
						
						<logic:iterate id="licData" name="lstLicenseData" type="com.elitecore.license.base.LicenseData">
						<%
						iIndex++;
						%>
							<tr>
								<td align="left" class="tblfirstcol" valign="top">
									<%=iIndex%>
								</td>
								<td align="left" class="tblrows" valign="top">
									<bean:write name="licData" property="displayName" />&nbsp;
								</td>
								
								<td align="left" class="tblrows" valign="top">
									<bean:write name="licData" property="version" />&nbsp;
								</td>
								
								<td align="left" class="tblrows" valign="top" valign="top">&nbsp;
								
								<logic:notEqual value="<%=LicenseTypeConstants.NODE%>" name="licData" property="type">
									<%String strValue = "-";
									if(licData.getValue()!=null){
									strValue = licData.getValue().toString();
									if(strValue.contains("-1")){
										strValue = "Unlimited";
									}
									}
									%>
									<%=strValue%>
									
								</logic:notEqual>
								
								<logic:equal value="<%=LicenseTypeConstants.NODE%>" name="licData" property="type">
								<%String strValue = "-";
								if(licData.getValue()!=null){
									strValue = licData.getValue().toString();
									if(strValue.contains("-1")){
										strValue = "Unlimited";
									}else{
										String[] temp = strValue.split(":");
										if(temp.length>0 && temp[0]!=null){
											strValue="IP = " + temp[0];
										}
									}
								}
								%>
								<%=strValue%>
								</logic:equal>							
											
								</td>
							</tr>
						</logic:iterate>
						
						<logic:empty name="lstLicenseData" >
						<tr>         
      						<td  colspan="5">&nbsp;</td>
      	 				 </tr>
						<tr>         
      						<td class="small-text-grey" colspan="5">Note : License Information not available. Please Contact System Administrator.</td>
      	 				 </tr>
						</logic:empty>
						
          <%iIndex =0; %>
  	   	  <tr>         
      		<td colspan="5">&nbsp;</td>
      	  </tr>
		  <%--<tr>         
      		<td class="small-text-grey" colspan="3">Note : </td>
      	  </tr>
      	  --%>
      	  <tr> 
      <td valign="left" colspan="5" >
      	 <input type="button" name="c_btndownloadlickey" id="c_btndownloadlickey"  value="Download License key"  class="light-btn" onclick="javascript:location.href='<%=localBasePath%>/downloadLicensePublickey.do?netserverid=<%=netServerId %>'" />                                           
    	 <input type="button" name="c_btnupladlic" id="c_btnupladlic" value="Upload License" class="light-btn"  onclick="javascript:location.href='<%=localBasePath%>/uploadLicenseAction.do?netserverid=<%=netServerId %>'" />  
    	 <input type="button" name="c_btnupladlic" id="c_btndownloadlic" value="Download License" class="light-btn"  onclick="javascript:location.href='<%=localBasePath%>/downloadLicense.do?netserverid=<%=netServerId %>'" />
    	 <input type="reset" name="c_btncancellic" id="c_btncancellic" value="Cancel" class="light-btn"  onclick="javascript:location.href='<%=localBasePath%>/viewNetServerInstance.do?netserverid=<%=netServerId %>'"/>        
      </td>
	</tr>      	  
     <tr> 
      <td  colspan="5">&nbsp;</td>
    </tr>
	</logic:equal>
   		<logic:equal name="initNetServerLicenseForm" scope="request" property="errorCode" value="-1">
   			<tr>
				<td  colspan="5">
					&nbsp;
				</td>
			</tr>
   		<tr>
				<td class="blue-text-bold" colspan="3">	<bean:message bundle="servermgrResources" key="servermgr.connectionfailure" />
					<br> <bean:message bundle="servermgrResources" key="servermgr.admininterfaceip" />
						:<bean:write name="netServerInstanceData" property="adminHost" />
					<br> <bean:message bundle="servermgrResources" key="servermgr.admininterfaceport" />
						: <bean:write name="netServerInstanceData" property="adminPort" />
					&nbsp;
				</td>
			</tr>
			<tr>
				<td  colspan="5">
					&nbsp;
				</td>
			</tr>
   		</logic:equal>
   		
     </table>
	  </td>
    </tr>
</table>