<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>

<%
    List listAccessGroupList = (List)request.getAttribute("listAccessGroupList");
    List listSubscribedGroup = (List)request.getAttribute("listSubscribedGroup");
%>

<script type="text/javascript" src="<%=basePath%>/js/dualList.js">
</script>
<script> 
	function selectedAllOptions(selObj){
		for(var i=0;i<selObj.options.length;i++){
			selObj.options[i].selected = true;
		}
		if(isNull(document.forms[0].selected.value)){
			alert('User must belong to atleast one group with specific role');
		}else{
			document.forms[0].submit();
		}
	}
	$(document).ready(function(){
		setTitle('<bean:message key="staff.staff" />');
	});
</script>
<html:form action="/createStaffDetail" enctype="multipart/form-data" styleId="createStaffForm">
	<html:hidden name="createStaffForm" styleId="staffId"
		property="staffId" />
	<html:hidden name="createStaffForm" styleId="name" property="name" />
	<html:hidden name="createStaffForm" styleId="userName"
		property="userName" />
	<html:hidden name="createStaffForm" styleId="createDate"
		property="createDate" />
	<html:hidden name="createStaffForm" styleId="password"
		property="password" />
	<html:hidden name="createStaffForm" styleId="status" property="status" />
	<html:hidden name="createStaffForm" styleId="emailAddress"
		property="emailAddress" />
	<html:hidden name="createStaffForm" styleId="mobile"
		property="mobile" />
	<html:hidden name="createStaffForm" styleId="phone"
		property="phone" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">&nbsp;</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0"
				width="100%">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="table-header" colspan="5"><bean:message
						key="staff.createstaff" /></td>
				</tr>
				<tr>
					<td class="small-gap" colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td class="btns-td" valign="middle" colspan="3">
					<table cellpadding="0" cellspacing="0" border="0" width="100%"
						height="30%">
						<tr>
							<td align="left" class="tblheader-bold" colspan="3"><bean:message
								key="staff.assignaccessgroup" /></td>
						</tr>

						<tr>
							<td>
							<table width="30%" align="left" style="padding-left: 5em">
								<tr>
									<td valign="top" align="left" width="200">
									<html:select
										styleId="available" property="available" multiple="true"
										size="10" style="width: 200;">
										<html:options collection="listAccessGroupList"
											property="roleId" labelProperty="name" />
									</html:select></td>
									<td align="center" width="10%">
									<p><a href="#"><img
										src="<%=basePath%>/images/add-all.jpg" alt="Add All"
										onMouseOver="MM_swapImage('c_btnMoveAllRight','','<%=basePath%>/images/add-all-hover.jpg',1)"
										border="0" onMouseOut="MM_swapImgRestore()"
										name="c_btnMoveAllRight" width="21" height="19"
										onclick="moveDualList(document.forms[0].available,document.forms[0].selected,true)">
									</a></p>
									<p><a href="#"><img src="<%=basePath%>/images/add.jpg"
										alt="Add"
										onMouseOver="MM_swapImage('c_btnMoveRight11','','<%=basePath%>/images/add-hover.jpg',1)"
										border="0" onMouseOut="MM_swapImgRestore()"
										name="c_btnMoveRight11" width="21" height="18"
										onclick="moveDualList(document.forms[0].available,document.forms[0].selected,false)">
									</a></p>
									<p><a href="#"><img
										src="<%=basePath%>/images/remove.jpg" alt="Remove"
										onMouseOver="MM_swapImage('c_btnMoveLeft11','','<%=basePath%>/images/remove-hover.jpg',1)"
										border="0" onMouseOut="MM_swapImgRestore()"
										name="c_btnMoveLeft11" width="21" height="18"
										onclick="moveDualList(document.forms[0].selected,document.forms[0].available,false)">
									</a></p>
									<p><a href="#"><img
										src="<%=basePath%>/images/remove-all.jpg" alt="Remove All"
										onMouseOver="MM_swapImage('c_btnMoveLeftAll','','<%=basePath%>/images/remove-all-hover.jpg',1)"
										border="0" onMouseOut="MM_swapImgRestore()"
										name="c_btnMoveLeftAll" width="21" height="19"
										onclick="moveDualList(document.forms[0].selected,document.forms[0].available,true)">
									</a></p>
									</td>
									<td valign="top" align="right" width="200"><html:select
										styleId="selected" property="selected" multiple="true"
										size="10" style="width: 200;">
										<html:options collection="listSubscribedGroup"
											property="roleId" labelProperty="name" />
									</html:select></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle" align="left"><input
								type="button" name="c_btnBack" onclick="history.back();"
								value=" << Back  " class="light-btn"> <input
								type="button" name="c_btnCreate"
								onclick="selectedAllOptions(document.forms[0].selected)"
								id="c_btnCreate2" value="Create" class="light-btn"> <input
								type="reset" name="c_btnDeletePolicy"
								onclick="javascript:location.href='<%=basePath%>/searchStaff.do?/>'"
								value="Cancel" class="light-btn"></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
	</table>
</html:form>
