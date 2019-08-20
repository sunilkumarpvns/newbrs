<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
function validateUpload(){
	var file = document.getElementById('fileId').value;
	
	if (isNull(file)){
		alert("Please Upload CSV File.");
		document.getElementById('fileId').focus();
		return;	
	}
	document.forms[0].submit();
 }
setTitle('<bean:message bundle="radiusResources" key="radius.bwlist.title"/>');
</script>
<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>

					<td class="box" colspan="2"><html:form action="/createBWList"
							enctype="multipart/form-data">
							<html:hidden name="createBWListForm" styleId="inputMode"
								property="inputMode" value="upload" />
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="5"><bean:message
											bundle="radiusResources" key="radius.bwlist.uploadbwlist" /></td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%"
											height="30%">
											<tr>
												<td colspan="3">&nbsp;</td>
											</tr>


											<tr>
												<td class="small-gap" width="7">&nbsp;</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" width="18%"><bean:message
														bundle="radiusResources" key="radius.bwlist.uploadfile" /></td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<input type="file" name="fileUpload" id="fileId"
													class="labeltext" size="50" /> <bean:message
														bundle="radiusResources" key="radius.bwlist.csvformat" />&nbsp;<a
													href="javascript:void(0)"
													onclick="window.open('<%=basePath%>/jsp/radius/bwlist/BwListCsvPopup.jsp','CSVWin','top=0, left=0, height=350, width=700, scrollbars=yes, status')"><img
														src="<%=basePath%>/images/view.jpg" name="Image6"
														onmouseover="MM_swapImage('Image6','','<%=basePath%>/images/view-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" border="0" id="Image6"></a>
												</td>
											</tr>


											<tr>
												<td colspan="3">&nbsp;</td>
											</tr>





											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" name="c_btnCreate" onclick="validateUpload()"
													id="c_btnCreate2" value=" Upload " class="light-btn">
													<input type="reset" name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/initSearchBWList.do'"
													value="Cancel" class="light-btn"></td>
											</tr>
										</table> </html:form>
									</td>
								</tr>
							</table></td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>

