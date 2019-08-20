<%@ page import="java.util.*" %>





<html:form action="viewEAPTLSConfig">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
	 
	<tr>
		<td align="right" class="labeltext" valign="top" class="box" > 
			<table cellpadding="0" cellspacing="0" border="0" width="100%" >
 				<tr> 
				  	<td align="left" valign="top" width="100%">&nbsp;</td>
				</tr>
				<bean:define id="typeValue" name="viewEAPTLSConfigForm" property="type"></bean:define>
				 <%if("ttls".equals(typeValue)){ %>
				<tr> 
		            <td class="tblheader-bold" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.viewttlsdetails"/></td>
	            </tr>
					<tr>
					   
						<td class="tblfirstcol" width="100%" height="20%" align="center"><bean:message
							bundle="servermgrResources" key="servermgr.eapconfig.ttlsconfigdoesnotexist"/>
						</td>
					    
					</tr>
				<%}else if("peap".equals(typeValue)){ %>
                  
    	            <tr> 
			            <td class="tblheader-bold" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.viewpeapdetails"/></td>
		            </tr>
					<tr>
					   
						<td class="tblfirstcol" width="100%" height="20%" align="center"><bean:message
							bundle="servermgrResources" key="servermgr.eapconfig.peapconfigdoesnotexist"/>
						</td>
					    
					</tr>
				<%}else{ %>
                  
    	            <tr> 
			            <td class="tblheader-bold" height="20%"><bean:message bundle="servermgrResources" key="servermgr.eapconfig.viewtlsdetails"/></td>
		            </tr>
					<tr>
					   
						<td class="tblfirstcol" width="100%" height="20%" align="center"><bean:message
							bundle="servermgrResources" key="servermgr.eapconfig.tlsconfigdoesnotexist"/>
						</td>
					    
					</tr>
				<%} %>	
					<tr>
					   
						<td height="20px">
						 &nbsp;
						</td>
					    
					</tr>
					</table>
				</td>
			</tr>
		</table>
	</td>
</tr>
</table>
		
</html:form>					
					