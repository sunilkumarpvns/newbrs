<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

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


<% /* --- Start of Page Header --- only edit module name*/ %>

<html:form action="/createDictionary" enctype="multipart/form-data">
	<html:hidden name="createDictionaryForm" styleId="action"
		property="action" value="add" />
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
									<td class="table-header" colspan="4"><bean:message
											bundle="radiusResources" key="dictionary.create.dictionary" />
									</td>
								</tr>
								<tr>
									<td>
										<table width="100%" border="0" cellspacing="0" cellpadding="0"
											height="15%">

											<tr>
												<td class="captiontext" width="20%" height="20%">
													<bean:message bundle="radiusResources" key="dictionary.dictionary.file" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="dictionary.file" header="dictionary.dictionary.file"/>
												</td>
												<td class="labeltext" width="80%" height="20%"><html:file
														name="createDictionaryForm" property="dictionaryFile"
														tabindex="1" size="40" style="font-family: Verdana; " />
													&nbsp;&nbsp;<html:checkbox name="createDictionaryForm"
														tabindex="2" property="commonStatusId" value="CST01" />&nbsp;<bean:message
														bundle="radiusResources" key="dictionary.active" /> <%-- Hiding Sample Dic link (Also Hided in Update dic Page)
	             		&nbsp;&nbsp;&nbsp;<bean:message bundle="radiusResources" key="dictionary.sample.dictionary" />
						<a  href="javascript:void(0)"  onclick="window.open('<%=basePath%>/jsp/radius/dictionary/dictionary.sample','CSVWin','top=0, left=0, height=350, width=700, scrollbars=yes, status')" > <img  src="<%=basePath%>/images/view.jpg"  name="Image61"  onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/view-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  border="0"  id="Image61" ></a>
			            --%></td>
											</tr>

											<tr>
												<td class="captiontext" width="20%" height="20%">
													<bean:message bundle="radiusResources" key="dictionary.description" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="dictionary.description" header="dictionary.description"/>
												</td>
												<td class="labeltext" width="80%" height="20%"><html:textarea
														tabindex="3" styleId="description" property="description"
														cols="30" rows="4" style="width:250px" /></td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle"><input
													type="button" tabindex="4" name="c_btnCreate"
													onclick="validateCreate()" id="c_btnCreate2" value="Create"
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


