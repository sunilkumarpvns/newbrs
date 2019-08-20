<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-nested.tld" prefix="nested" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0" height=""> 
	<tr>	
		<td height="35"">
		</td>
	</tr>
	<tr>	
		<td align="center">
		<table width="80%" cellpadding="0" cellspacing="0" border="0" class="orange-border"">
			<tr height="20px">
				<td align="left" class="table-org-column" colspan="3">&nbsp;&nbsp;&nbsp;<b>Error:</b></td>
			</tr>			
			 <tr height="50">  
	                <td  colspan="3" align="center">
						<ul id="content-body-messages" >
							<logic:messagesPresent>
							           <html:messages bundle="resultMessageResources" id="errorMsg">
							               <bean:write name="errorMsg" /><br>
							           </html:messages>
							           <html:messages bundle="resultMessageResources" id="warn_key" name="warn_key">
							               <bean:write name="warn_key" /><br>
							           </html:messages>
							</logic:messagesPresent>
						</ul>
					</td> 
             </tr>			
             <tr> 
					<td  valign="top" align="center" width="100%">&nbsp;</td>
			 </tr>	  
             <tr> 
                  <td align="center"> 
                    <input type="button" name="Button2" value="Ok" class="orange-btn" onclick="history.go(-1)"> 
                  </td>
             </tr>
             <tr> 
                <td  width="100%"> &nbsp;</td>
             </tr>
		</table>
		</td>
	</tr>
</table>