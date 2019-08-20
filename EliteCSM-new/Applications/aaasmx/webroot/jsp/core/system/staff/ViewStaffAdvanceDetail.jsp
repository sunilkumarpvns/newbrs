




<html:form action="/viewStaffAdvanceDetail" >    
<html:hidden name="viewStaffAdvanceDetailForm" property="action" />	
<html:hidden name="viewStaffAdvanceDetailForm" property="staffId" />	

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="staffBean" name="staffData" scope="request" type="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData" />
	    <tr> 
	      <td valign="top" align="right"> 
	        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
	          <tr> 
	            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="StaffResources" key="staff.addressdetails"/></td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.address1" /></td>
	            <td class="tblcol" width="70%" height="20%"><bean:write name="staffBean" property="address1"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.address2" /></td>
	            <td class="tblcol" width="70%" height="20%"><bean:write name="staffBean" property="address2"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.city" /></td>
	            <td class="tblcol" width="70%" height="20%"><bean:write name="staffBean" property="city"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.state" /></td>
	             <td class="tblcol" width="70%" height="20%"><bean:write name="staffBean" property="state"/>&nbsp;</td>
	          </tr>      
	          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.country" /></td>
	             <td class="tblcol" width="70%" height="20%"><bean:write name="staffBean" property="country"/>&nbsp;</td>
	          </tr> 
	          <tr> 
	            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="StaffResources" key="staff.contacedetails"/></td>
	          </tr>       
	          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.phonenumber" /></td>
	            <td class="tblcol" width="70%" height="20%"><bean:write name="staffBean" property="phone"/>&nbsp;</td>
	          </tr>     
	          <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.mobilenumber" /></td>
	            <td class="tblcol" width="70%" height="20%"><bean:write name="staffBean" property="mobile"/>&nbsp;</td>
	          </tr>
	        </table>
			</td>
	    </tr>
</table>
</html:form>