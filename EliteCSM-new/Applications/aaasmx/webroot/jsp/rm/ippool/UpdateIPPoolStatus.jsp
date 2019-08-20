<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.List"%>






<% 
	String localBasePath = request.getContextPath();
%>

<script>
	var dFormat;
	dFormat = 'dd-MMM-yyyy';	
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		//Get format from system parameter document.form[0].
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 

	}

function validateUpdate()
{
	if(document.forms[0].reason.value == ''){
		alert('Reason must be specified');
	}else{
		document.forms[0].submit();
	}
}
</script>

<html:form action="/updateIPPool">
	<html:hidden  styleId="action" property="action" value="changeStatus" />
	<html:hidden styleId="ipPoolId" property="ipPoolId" />
	<html:hidden styleId="status" property="status" />
	<html:hidden styleId="auditUId" property="auditUId" />
	<html:hidden styleId="name" property="name" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		height="15%" align="right">
		<tr>
			<td class="tblheader-bold" colspan="3">
				<bean:message bundle="ippoolResources" key="ippool.updatedetails" />
			</td>
		</tr>
		<tr>
			<logic:equal name="ipPoolForm" property="status" value="CST01">
				<td width="30%" height="20%" class="captiontext" valign="top">
					<bean:message bundle="ippoolResources" key="ippool.active" /></td>
			</logic:equal>
			<logic:notEqual name="ipPoolForm" property="status" value="CST01">
				<td width="30%" height="20%" class="labeltext" valign="top">
					<bean:message bundle="ippoolResources" key="ippool.inactive" /></td>
			</logic:notEqual>
			<td width="70%" height="20%" class="labeltext" valign="top">&nbsp;</td>
		</tr>
		<tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="3">
				<bean:message bundle="ippoolResources" key="ippool.reasonforchange" />
			</td>
		</tr>
		<tr>
			<td width="30%" height="20%" class="captiontext" valign="top">
				<bean:message bundle="ippoolResources" key="ippool.reason" /></td>
			<td width="70%" height="20%" class="labeltext" valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>
							<textarea rows="4" cols="30" name="reason" style="width: 250px"></textarea>
							<%-- <html:textarea  tabindex="1" styleId="reason" property="reason" cols="30" rows="4" style="width:250px" /> --%>
							<font color="#FF0000"> *</font></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle">&nbsp;</td>
			<td class="btns-td" valign="middle">
				<input type="button" tabindex="2" name="c_btnCreate" onclick="validateUpdate()" value="Update" class="light-btn"> 
				<input type="reset" tabindex="3" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/viewIPPool.do?ipPoolId=<bean:write name="ipPoolForm" property="ipPoolId"/>'" value="Cancel" class="light-btn">
			</td>
		</tr>
	</table>
</html:form>
