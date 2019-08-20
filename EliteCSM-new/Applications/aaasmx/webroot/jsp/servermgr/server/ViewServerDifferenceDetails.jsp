 <%@ page import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.ViewRadiusPolicyForm" %>
 <% 
 	String localBasePath = request.getContextPath();
 %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td class="btns-td" valign="middle" colspan="3">
                <table cellpadding="0" cellspacing="0" border="0" width="100%" >
        		<tr> 
					<td class="tblheader-bold" colspan="3">Configuration Differences</td>
				</tr>
                
                  <tr>
                    <td colspan="3">
                      <table width="100%" cols="8" id = "listTable" type="tbl-list" border="0" cellpadding="0" cellspacing="0">
                        <tr>
                          <td align="center" class="tblheader" valign="top" width="4%">
                            <input type="checkbox" value="checkbox" />
                          </td>
                          <td align="center" class="tblheader" valign="top" width="5%" >Sr.No.</td>
                          <td align="left" class="tblheader" valign="top" width="30%" >Service</td>
                          <td align="left" class="tblheader" valign="top" width="20%" >Service Type</td>                          
                          <td align="center" class="tblheader" valign="top" width="15%" >Configured<br> in Server</td>
                          <td align="center" class="tblheader" valign="top" width="15%" >Configured<br> in Database</td>
                          <td align="center" class="tblheader" valign="top" width="15%">Configuration<br> Matches</td>
                        </tr>
                        <tr>
                          <td align="center" class="tblfirstcol" valign="top" >
                            <input type="checkbox" value="checkbox" />
                          </td>
                          <td align="center" class="tblfirstcol" valign="top" >1</td>
                          <td align="left" class="tblrows" valign="top" >IPPool Service Instance</td>
                          <td align="left" class="tblrows" valign="top" >IP Pool Service</td>                          
                          <td align="center" class="tblrows" valign="center" ><img src="<%=localBasePath%>/images/tick.jpg" name="new" border="0" /></td>
                          <td align="center" class="tblrows" valign="center" ><img src="<%=localBasePath%>/images/tick.jpg" name="new" border="0" /></td>
                          <td align="center" class="tblrows" valign="center" ><img src="<%=localBasePath%>/images/cross.jpg" name="new" border="0" /></td>                          
                        </tr>
                        <tr>
                          <td align="center" class="tblfirstcol" valign="top" >
                            <input type="checkbox" value="checkbox" />
                          </td>
                          <td align="center" class="tblfirstcol" valign="top" >2</td>
                          <td align="left" class="tblrows" valign="top" >Parlay Charging Service Instance</td>
                          <td align="left" class="tblrows" valign="top" >Parlay Charging Service</td>                                                    
                          <td align="center" class="tblrows" valign="center" ><img src="<%=localBasePath%>/images/tick.jpg" name="new" border="0" /></td>
                          <td align="center" class="tblrows" valign="center" ><img src="<%=localBasePath%>/images/cross.jpg" name="new" border="0" /></td>
                          <td align="center" class="tblrows" valign="center" ><img src="<%=localBasePath%>/images/cross.jpg" name="new" border="0" /></td>                          
                        </tr>
                        
                      </table>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>

	<tr> 
      <td  >&nbsp;</td>
    </tr>
   	<tr > 
        <td class="btns-td" valign="middle"  >

          	<input type="button" name="c_btnDifference"  onclick="window.open('<%=localBasePath%>/jsp/servermgr/ViewServiceDifferenceDetail.jsp','ConfigDifference','top=0, left=0, height=350, width=700, scrollbars=yes, status')"  id="c_btnUPdate"  value="Show Difference"  class="light-btn">                   
         	<input type="button" name="c_btnUpdateServer" id="c_btnUPdate"  value="Update Server"  class="light-btn">                           
          	<input type="button" name="c_btnUpdateDatabase" id="c_btnUPdate"  value="Update Database"  class="light-btn">                                     	
          	<input type="reset" name="c_btnDeletePolicy" value="Cancel" class="light-btn"> 
        </td>
	</tr>
	<tr> 
      <td  >&nbsp;</td>
    </tr>
	
</table>
