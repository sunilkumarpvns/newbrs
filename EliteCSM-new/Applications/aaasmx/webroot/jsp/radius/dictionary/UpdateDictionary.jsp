<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.dictionary.forms.ViewDictionaryForm"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>

<%
    String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
function validateCreate(){
	if(isNull(document.forms[0].dictionaryFile.value)){
		alert('Dictionary file must be specified');
	}else{
		document.forms[0].submit();
	}

}
setTitle('<bean:message bundle="radiusResources" key="dictionary.module.name"/>');
</script>

<html:form action="/updateDictionary" enctype="multipart/form-data">
	<html:hidden name="updateDictionaryForm" styleId="action"
		property="action" value="update" />
	<html:hidden name="updateDictionaryForm" styleId="createdByStaffId"
		property="createdByStaffId" />
	<html:hidden name="updateDictionaryForm" styleId="createDate"
		property="createDate" />
	<html:hidden name="updateDictionaryForm" styleId="dictionaryId"
		property="dictionaryId" />
	<html:hidden name="updateDictionaryForm" styleId="vendorId"
		property="vendorId" />

	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header"><bean:message
											bundle="radiusResources" key="dictionary.update.dictionary" />
									</td>
								</tr>
								<tr>
									<td>
										<table width="100%" border="0" cellspacing="0" cellpadding="0"
											height="15%">

											<tr>
												<td class="captiontext" width="20%" height="20%"><bean:message
														bundle="radiusResources" key="dictionary.name" /> <img
													src="<%=basePath%>/images/help-hover.jpg" height="12"
													width="12" style="cursor: pointer"
													onclick="parameterDescription('<bean:message bundle="descriptionResources" key="dictionary.name"/>','<bean:message bundle="radiusResources" key="dictionary.name"/>')" />

												</td>
												<td class="labeltext" width="80%" height="20%"><bean:write
														name="updateDictionaryForm" property="name" /></td>
											</tr>

											<tr>
												<td class="captiontext" width="20%" height="20%"><bean:message
														bundle="radiusResources" key="dictionary.dictionary.file" />
													<img src="<%=basePath%>/images/help-hover.jpg" height="12"
													width="12" style="cursor: pointer"
													onclick="parameterDescription('<bean:message bundle="descriptionResources" key="dictionary.file"/>','<bean:message bundle="radiusResources" key="dictionary.dictionary.file"/>')" />

												</td>
												<td class="labeltext" width="80%" height="20%"><html:file
														tabindex="1" name="updateDictionaryForm"
														property="dictionaryFile" accept="text/xml" size="40" />
													&nbsp;&nbsp;<html:checkbox name="updateDictionaryForm"
														tabindex="2" styleId="commonStatusId"
														property="commonStatusId" value="CST01" />&nbsp;<bean:message
														bundle="radiusResources" key="dictionary.active" /> <%--  Hiding Sample Dic link (Also Hided in Create dic Page)
						&nbsp;&nbsp;&nbsp;<bean:message bundle="radiusResources" key="dictionary.sample.dictionary" />
						<a  href="javascript:void(0)"  onclick="window.open('/jsp/radius/dictionary/dictionary.sample','CSVWin','top=0, left=0, height=350, width=700, scrollbars=yes, status')" > <img  src="<%=basePath%>/images/view.jpg"  name="Image61"  onmouseover="MM_swapImage('Image61','','/images/view-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  border="0"  id="Image61" ></a>
				 --%></td>
											</tr>

											<tr>
												<td class="captiontext" width="15%" height="20%"><bean:message
														bundle="radiusResources" key="dictionary.description" /></td>
												<td class="labeltext" width="85%" height="20%"><html:textarea
														styleId="description" tabindex="3" property="description"
														cols="30" rows="4" style="width:250px" /></td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle"><input
													type="button" tabindex="4" name="c_btnCreate"
													onclick="validateCreate()" id="c_btnCreate2" value="Update"
													class="light-btn"> <input type="reset" tabindex="5"
													name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/listDictionary.do'"
													value="Cancel" class="light-btn"></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>