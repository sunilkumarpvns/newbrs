
<html:form action="/viewStaffAdvanceDetail" >    
<html:hidden name="viewStaffAdvanceDetailForm" property="action" />	
<html:hidden name="viewStaffAdvanceDetailForm" property="staffId" />	

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="staffBean" name="staffData" scope="request" type="com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData" />
	    <tr> 
	      <td valign="top" align="right"> 
	        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
	        </table>
			</td>
	    </tr>
</table>
</html:form>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
