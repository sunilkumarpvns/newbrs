<%@ page import="java.util.List"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>

<%
            List listAccessGroupList = (List) request.getAttribute("listAccessGroupList");
            List listSubscribedGroup = (List) request.getAttribute("listSubscribedGroup");
            IStaffData iStaffData = (IStaffData)request.getAttribute("staffData");
%>
<link rel='stylesheet' href="<%=basePath%>/css/ui.multiselect.css" type='text/css' media='screen' />
<script type='text/javascript' src="<%=basePath%>/jquery/js/jquery-1.4.2.min.js"></script>
<script type='text/javascript' src="<%=basePath%>/jquery/js/jquery-ui-1.8.5.custom.min.js"></script>
<script type='text/javascript' src="<%=basePath%>/js/ui.multiselect.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/dualList.js"></script>

<style type="text/css">
	.add-all{color: white !important;}
	.remove-all{color: white !important;}
</style>

<script>

function selectedAllOptions(){
	
	var noOfSelectedRule=parseInt($("#selected option:selected").length);
	if(noOfSelectedRule <= 0){
		alert('Select At least one access group');
		return false;
	}else{
		document.forms[0].submit();
	}
}

jQuery(document).ready(function() {
	$("#selected").multiselect({
	    dividerLocation: .5
	}).change(function (e) {
	    //output values.
	    console.log($('#colors').val());
	});
	
});

function verifyName() {
	var searchName = document.getElementById("name").value;
	verifyInstanceName('<%=InstanceTypeConstants.ACCESS_GROUP%>',searchName,'update','<%=iStaffData.getStaffId()%>','verifyNameDiv');
}
</script>

<html:form action="/updateAccessGroup">
	<html:hidden name="updateAccessGroupForm" styleId="action" property="action"  value="update"/>
	<html:hidden name="updateAccessGroupForm" styleId="staffId" property="staffId" />
	<html:hidden name="updateAccessGroupForm" styleId="auditUId" property="auditUId" />
	<html:hidden name="updateAccessGroupForm" styleId="auditName" property="auditName" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right" colspan="3">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="10">
							<bean:message bundle="StaffResources" key="staff.assignaccessgroup" />
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="10">&nbsp;</td>
					</tr>
					<tr>
						<td>
							<table width="50%" align="center">
									<tr>
										<td>
											<html:select name="updateAccessGroupForm" property="selected" styleId="selected" multiple="multiple" styleClass="multiselect" style="display:none;width: 500px; height: 180px;">
												<logic:iterate id="accessGroupListBean" name="listAccessGroupList"  type="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData"  scope="request">
													 <option value="<%=accessGroupListBean.getGroupId()%>" ><%=accessGroupListBean.getName()%></option>
												</logic:iterate>
												<logic:iterate id="subscribedGroupBean" name="listSubscribedGroup" type="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData"  scope="request">
													 <option value="<%=subscribedGroupBean.getGroupId()%>" selected="selected"><%=subscribedGroupBean.getName()%></option>
												</logic:iterate> 
											</html:select>
										</td>
									</tr>
									<tr>
										<td class='small-text-grey'>Note : Drag and Drop to assign Access Group.</td>
									</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle" align="center">
				<input type="button" name="c_btnCreate" tabindex="3" onclick="selectedAllOptions()" value="Update" class="light-btn"> 
				<input type="reset" name="c_btnDeletePolicy" tabindex="4" onclick="javascript:location.href='<%=basePath%>/initSearchStaff.do?/>'" value="Cancel" class="light-btn">
			</td>
		</tr>
	</table>
</html:form>