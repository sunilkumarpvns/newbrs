<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>


<table width="100%" border="0" cellspacing="0" cellpadding="0" height="">
 
	<tr>	
		<td height="35"">
		</td>
	</tr>
	<tr>	
		<td align="center">
		<table width="80%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td align="left" class="tableboldheader" colspan="3">&nbsp;&nbsp;&nbsp;Error:</td>
			</tr>
			
			 <tr height="50">  
	                <td class="tblfirstcol" colspan="3" align="center">
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
					<td class="labeltext" valign="top" align="center" width="100%">
					 &nbsp;
					</td>
			</tr>	  
                <tr> 
                  <td align="center" class="labeltext"> 
                    <input type="button" name="Button2" value="Cancel" class="sspbutton" onclick="history.go(-1)"> 
                  </td>
                </tr>
			
		</table>
		</td>
	</tr>
</table>