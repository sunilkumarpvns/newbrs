<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>






<%
    String path = request.getContextPath();
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="staffBean" name="staffData" scope="request" type="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="StaffResources" key="staff.viewinformation"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="staffBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources"  key="staff.status" /></td>
				<logic:equal name="staffBean" property="commonStatusId" value="CST01">
				  <td class="tblcol" width="70%" height="20%"><img src="<%=path%>/images/active.jpg"/></td>
				</logic:equal>
				<logic:notEqual name="staffBean" property="commonStatusId" value="CST01">
	  			 <td class="tblcol" width="70%" height="20%"><img src="<%=path%>/images/deactive.jpg"/></td>
				</logic:notEqual>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.username" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="staffBean" property="username"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.birthdate" /></td>
			<td class="tblcol" width="705" height="20%"><%=EliteUtility.dateToString(staffBean.getBirthDate(), ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT))%></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="StaffResources" key="staff.emailaddress" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="staffBean" property="emailAddress"/>&nbsp;</td>
          </tr>   
          <tr> 
             <td class="tblfirstcol" width="30%" height="20%" valign="top"><bean:message bundle="StaffResources" key="staff.accessgroups" /></td>
             <td class="tblcol" width="70%" height="20%">
             <%int i = 1; %>
             <logic:iterate id="groupData" name="groupDataList"  type="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData">
	            <%=i++%>.&nbsp; <bean:write name="groupData" property="name"/><br/>
	            
             </logic:iterate>
             </td>
          </tr>         
        </table>
		</td>
    </tr>
</table>
