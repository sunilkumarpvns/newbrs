<%@page import="com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData"%>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility" %> 
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant" %>
 


<%
	String viewStaffPath = request.getContextPath();
	IStaffData iStaffData = (IStaffData) request.getAttribute("staffData");
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="staffBean" name="staffData" scope="request" type="com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData" />
	<tr>
		<td valign="top" align="right">
			<table width="97%" border="0" cellspacing="0" cellpadding="0"
				height="15%">
				<tr>
					<td class="tblheader-bold" colspan="2" height="20%">
						<bean:message key="staff.viewinformation" />
					</td>
				</tr>
				<tr>
					<td class="tbllabelcol" width="30%" height="20%">
						<bean:message key="staff.name" />
					</td>
					<td class="tblcol" width="70%" height="20%">
						<bean:write name="staffBean" property="name" />
					</td>
				</tr>
				<tr>
					<td class="tbllabelcol" width="30%" height="20%">
						<bean:message key="staff.username" />
					</td>
					<td class="tblcol" width="70%" height="20%">
						<bean:write name="staffBean" property="userName" />&nbsp;
					</td>
				</tr>

				<tr>
					<td class="tbllabelcol" width="30%" height="30%">
						<bean:message key="staff.profilepicture" />
					</td>
					<td class="tblcol" width="70%" height="30%">
						<html:img action="profilePicture?staffId=${staffId}" height="100" width="100"/>
					</td>
				</tr>
				<tr>
					<td class="tbllabelcol" width="30%" height="20%">
						<bean:message key="staff.status" />
					</td>
					<logic:equal name="staffBean" property="commonStatusId" value="CST01">
						<td class="tblcol" width="70%" height="20%">
							<img src="<%=viewStaffPath%>/images/active.jpg" /> <bean:message key="staff.active"/> 
						</td>
					</logic:equal>
					<logic:notEqual name="staffBean" property="commonStatusId" value="CST01">
						<td class="tblcol" width="70%" height="20%">
							<img src="<%=viewStaffPath%>/images/deactive.jpg" /> <bean:message key="staff.inactive"/>
						</td>
					</logic:notEqual>
				</tr>
				<tr>
					<td class="tbllabelcol" width="30%" height="20%">
						<bean:message key="general.createddate" />
					</td>
					<td class="tblcol" width="70%" height="20%">
						<%=EliteUtility.dateToString(staffBean.getCreateDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tbllabelcol" width="30%" height="20%">
						<bean:message key="general.lastmodifieddate" />
					</td>
					<td class="tblcol" width="70%" height="20%">
						<%=EliteUtility.dateToString(staffBean.getLastModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT)) %>&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tblheader-bold" colspan="2" height="20%">
						<bean:message key="staff.contacedetails" />
					</td>
				</tr>
				<tr>
					<td class="tbllabelcol" width="30%" height="20%">
						<bean:message key="staff.phonenumber" />
					</td>
					<td class="tblcol" width="70%" height="20%">
						<bean:write name="staffBean" property="phone" />&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tbllabelcol" width="30%" height="20%">
						<bean:message key="staff.mobilenumber" />
					</td>
					<td class="tblcol" width="70%" height="20%">
						<bean:write name="staffBean" property="mobile" />&nbsp;
					</td>
				</tr>
				<tr>
					<td class="tbllabelcol" width="30%" height="20%">
						<bean:message key="staff.emailaddress" />
					</td>
					<td class="tblcol" width="70%" height="20%">
						<bean:write name="staffBean" property="emailAddress" />&nbsp;
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
