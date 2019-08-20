<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<%
    String basePath = request.getContextPath();
%>
<script>
	function validateCreate(){
	if(isNull(document.forms[0].errorProcessingFile.value)){
		alert('ErrorProcessing file must be specified');
	}else{
	
	
		msg = 'Are you sure '+ document.forms[0].fileName.value +' have been uploaded.';
	    var agree = confirm(msg);
	    if(agree){
		document.forms[0].action.value='upload';	
		document.forms[0].submit();
		}
	}
}
	
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>

		<td colspan="2">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">

				<tr>
					<td width="81%" background="<%=basePath%>/images/popup-bkgd.jpg"
						valign="top">&nbsp;</td>
					<td width="3%"><img src="<%=basePath%>/images/popup-curve.jpg"></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a
						href="#"><img src="<%=basePath%>/images/refresh.jpg"
							name="Image1" onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/refresh-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a
						href="#" onclick="window.print()"><img
							src="<%=basePath%>/images/print.jpg" name="Image2"
							onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/print-hover.jpg',1)"
							border="0"> </a></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a
						href="#"><img src="<%=basePath%>/images/aboutus.jpg"
							name="Image3" onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/aboutus-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="3%"><a
						href="#"><img src="<%=basePath%>/images/help.jpg"
							name="Image4" onMouseOut="MM_swapImgRestore()"
							onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/help-hover.jpg',1)"
							border="0"></a></td>
					<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="4%">&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table name="MainTable" id="MainTable" cellSpacing="0" cellPadding="0"
	width="100%" border="0">

	<tr>
		<td width="10">&nbsp;</td>
		<td width="100%" colspan="3" valign="top" class="box">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">

				<tr>
					<td class="table-header" valign="bottom" colspan="3"><bean:message
							bundle="servermgrResources"
							key="servermgr.fileinformation.uploadfile" /></td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
				</tr>

				<tr>
					<td valign="top" width="97%"><html:form
							action="/uploadErrorProcessingFile" enctype="multipart/form-data">
							<html:hidden name="uploadErrorProecssingFileForm"
								styleId="action" property="action" />
							<html:hidden name="uploadErrorProecssingFileForm"
								styleId="netServerId" property="netServerId" />
							<html:hidden name="uploadErrorProecssingFileForm"
								styleId="fileName" property="fileName" />
							<html:hidden name="uploadErrorProecssingFileForm"
								styleId="deviceName" property="deviceName" />
							<html:hidden name="uploadErrorProecssingFileForm"
								styleId="serviceType" property="serviceType" />


							<table width="97%" border="0" cellspacing="0" cellpadding="0"
								height="15%" align="right">
								<tr>
									<td class="labeltext" valign="top" width="20%" height="15%"><bean:message
											bundle="servermgrResources"
											key="servermgr.fileinformation.filename" /></td>
									<td class="labeltext" width="60%" valign="top" height="15%"><bean:write
											name="uploadErrorProecssingFileForm" property="fileName"
											scope="request" /></td>
								</tr>
								<tr>
									<td colspan="2" class="small-gap">&nbsp;</td>
								</tr>


								<tr>
									<td class="labeltext" valign="top" width="20%" height="15%"><bean:message
											bundle="servermgrResources"
											key="servermgr.fileinformation.uploadfilename" /></td>
									<td class="labeltext" width="60%" valign="top" height="15%"><html:file
											name="uploadErrorProecssingFileForm"
											property="errorProcessingFile" size="35" /></td>
								</tr>
								<tr>
									<td colspan="2" class="small-gap">&nbsp;</td>
								</tr>


								<tr>
									<td colspan="2"><input type="button" name="c_btnCreate"
										onclick="validateCreate()" id="c_btnCreate2" value="Upload"
										class="light-btn"> <input type="reset"
										name="c_btnDeletePolicy" onclick="window.close();"
										value="Cancel" class="light-btn"></td>
								</tr>
								<tr>
									<td colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-text-grey" colspan="2">Note : This
										operation will upload file Following Information.</td>
								</tr>
								<tr>
									<td colspan="2" class="small-gap">&nbsp;</td>
								</tr>
								<tr>
									<td class="small-text-grey" width="10%"></td>
									<td class="small-text-grey">File
										Name&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;<b>'<bean:write
												name="uploadErrorProecssingFileForm" property="fileName"
												scope="request" />'
									</b> .
									</td>
								</tr>
								<tr>
									<td class="small-text-grey" width="10%"></td>
									<td class="small-text-grey">ServiceType :&nbsp;&nbsp;<b>'<bean:write
												name="uploadErrorProecssingFileForm" property="serviceType"
												scope="request" />'
									</b> .
									</td>
								</tr>
								<tr>
									<td class="small-text-grey" width="10%"></td>
									<td class="small-text-grey">Device Name :&nbsp;<b>'<bean:write
												name="uploadErrorProecssingFileForm" property="deviceName"
												scope="request" />'
									</b> .
									</td>
								</tr>
								<tr>
									<td colspan="2">&nbsp;</td>
								</tr>
							</table>
						</html:form></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="small-gap">&nbsp;</td>
	</tr>
</table>
<table cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td colspan="2" class="small-gap">&nbsp;</td>
	</tr>

	<tr>
		<td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
		<td class="small-gap" width="1%"><img
			src="<%=basePath%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>



