<table width="100%" border="0" cellspacing="0" cellpadding="0">
<% 
	String localBasePath = request.getContextPath();
%>

    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
				<tr> 
					<td class="tblheader-bold" colspan="2">Synchronize Service Details</td>
				</tr>
				<tr> 
					<td align="left" class="labeltext" valign="top"  colspan="2">&nbsp;</td>
				</tr>	

				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">Admin IP</td>
					<td align="left" class="labeltext" valign="top" width="32%" >192.168.1.1
						<!--  <input type="text" name="adminip" value="192.168.1.1"/> -->
					</td>
				</tr>								
				<tr>
					<td align="left" class="labeltext" valign="top" width="10%">Admin Port</td>
					<td align="left" class="labeltext" valign="top" width="32%" >8090
						<!-- <input type="text" name="adminport" value="8090" /> -->
					</td>
				</tr>								

        </table>
		</td>
    </tr>

	<tr> 
      <td  >&nbsp;</td>
    </tr>

	<tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
			<tr>         
      			<td class="small-text-grey">Note : All Configurations for this Service would be Updated.</td>
      		</tr>
      	</table>
      </td>
    </tr>
	<tr> 
      <td  >&nbsp;</td>
    </tr>
    
   	<tr > 
        <td class="btns-td" valign="middle"  >
          	<input type="button" name="c_btnSynchronize"  onclick="javascript:location.href='<%=localBasePath%>/jsp/servermgr/ServiceContainer.jsp'"  id="c_btnSynchronize"  value=" Synchronize "  class="light-btn">                   
          	<input type="reset" name="c_btnDeletePolicy" value="Cancel" class="light-btn"> 
        </td>
	</tr>
	<tr> 
      <td  >&nbsp;</td>
    </tr>
	
</table>
