<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>






<%
    String basePath = request.getContextPath();
%>
<style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>
<script>

function validateSearch(){
	document.searchRadiusPolicyForm.pageNumber.value = 1;
	document.searchRadiusPolicyForm.submit();
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}
function navigate(direction, pageNumber ){
	alert('Page Number : '+pageNumber);
	document.searchRadiusPolicyForm.pageNumber.value = pageNumber;
	document.searchRadiusPolicyForm.submit();
}

function show(){
	//alert('Accessing the show function');
	document.miscRadiusPolicyForm.action.value = 'show';	
	//javascript:location.href='<%=basePath%>/miscRadiusPolicy.do';
	//alert('Action : '+document.forms[0].action.value);	
	document.miscRadiusPolicyForm.submit();
}
function hide(){
//	alert('Accessing the hide function');
	document.miscRadiusPolicyForm.action.value = 'hide';
//	javascript:location.href='<%=basePath%>/miscRadiusPolicy.do';	
//	alert('Action :'+document.forms[0].action.value);
	document.miscRadiusPolicyForm.submit();
}
function remove(){
	document.miscRadiusPolicyForm.action.value = 'delete';
//	javascript:location.href='<%=basePath%>/miscRadiusPolicy.do';	
	document.miscRadiusPolicyForm.submit();
}	
setTitle('Server Configuration');	
</script>

<% /* --- Start of Page Header --- only edit module name*/ %>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td width="7">&nbsp;</td>
		<td width="821" colspan="2">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="7">&nbsp;</td>
					<td width="100%" colspan="2">
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="small-gap" width="7">&nbsp;</td>
	</tr>

	<% /* --- End of Page Header --- 
      --- Module specific code starts from below.*/ %>


	<tr>
		<td width="10">&nbsp;</td>
		<td width="100%" colspan="2" valign="top" class="box">

			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<tr>
					<td class="table-header" colspan="5">Search Networ Services<img
						src="<%=basePath%>/images/open.jpg" border="0" name="closeopen"></td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="3">
						<table width="97%" name="c_tblCrossProductList"
							id="c_tblCrossProductList" align="right" border="0">
							<tr>
								<td align="left" class="labeltext" valign="top" width="10%">Server</td>
								<td align="left" class="labeltext" valign="top" width="32%">
									<select name="serverId">
										<option value="0" selected>Select Server</option>
										<option value="1">Demo Server</option>
										<option value="2">Test Server</option>
								</select>
								</td>
							</tr>
							<tr>
								<td align="left" class="labeltext" valign="top" width="10%">Service
								</td>
								<td align="left" class="labeltext" valign="top" width="5%">
									<select name="serviceId">
										<option value="1" selected>All</option>
										<option value="2">IPPool Service</option>
										<option value="3">Parlay Service</option>
								</select>
								</td>
							</tr>
							<tr>
								<td class="btns-td" valign="middle">&nbsp;</td>
								<td align="left" class="labeltext" valign="top" width="5%">
									<input type="button" name="Search" width="5%"
									name="RadiusPolicyName"
									Onclick="validateSearch(document.searchRadiusForm,'list')"
									value="   Search   " class="light-btn" /> <input type="button"
									name="Reset" onclick="reset();" value="   Reset    "
									class="light-btn">
								</td>
							</tr>

						</table>
					</td>
				</tr>
				<tr>
					<td width="10">&nbsp;</td>
					<table cellSpacing="0" cellPadding="0" width="100%" border="0">

						<tr>
							<td class="btns-td" valign="middle" colspan="9">
								<table width="100%" border="0" cellpadding="0" cellspacing="0"
									id="listTable">
									<tr>
										<td align="right" class="tblheader" valign="top" width="5%">Sr.No.</td>
										<td align="left" class="tblheader" valign="top" width="25%">Name</td>
										<td align="left" class="tblheader" valign="top" width="20%">Alias</td>
										<td align="left" class="tblheader" valign="top" width="30%">Description</td>
									</tr>
									<tr>
										<td align="right" class="tblfirstcol" valign="top">1</td>
										<td align="left" class="tblrows" valign="top"><a
											href='<%=basePath%>/jsp/servermgr/ServiceContainer.jsp'>IPPool
												Service</a></td>
										<td align="left" class="tblrows" valign="top">ip_pool_service</td>
										<td align="left" class="tblrows" valign="top">Description
											: IPPool Service</td>
									</tr>
									<tr>
										<td align="right" class="tblfirstcol" valign="top">2</td>
										<td align="left" class="tblrows" valign="top">Parlay
											Charging Service</td>
										<td align="left" class="tblrows" valign="top">parlay-charging-service</td>
										<td align="left" class="tblrows" valign="top">Description
											: Parlay Charging Service</td>
									</tr>
								</table>
							</td>
						</tr>

						<tr height="2">
							<td colspan="7"></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>

					</table>
				</tr>


			</table>
</table>


<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
