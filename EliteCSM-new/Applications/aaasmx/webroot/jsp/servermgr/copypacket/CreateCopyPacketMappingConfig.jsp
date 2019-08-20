<%@page import="com.elitecore.elitesm.web.servermgr.copypacket.forms.CreateCopyPacketTransMappingForm"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>

<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>

<%
	String basePath = request.getContextPath();	
	CreateCopyPacketTransMappingForm createCopyPacketMappingConfigForm = (CreateCopyPacketTransMappingForm) request.getAttribute("createCopyPacketTransMappingForm");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script language="javascript">

var externalScriptList = [];

<%
if( Collectionz.isNullOrEmpty(createCopyPacketMappingConfigForm.getExternalScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : createCopyPacketMappingConfigForm.getExternalScriptList()){ %>
		externalScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

$(document).ready(function(){
	setTranslationTypeOption();
	
	$("#selectedFromTranslatorType").change(function(){
		setTranslationTypeOption();	
	});
	 
	/* Script Autocomplete */
	setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
});

function setTranslationTypeOption(){

	if($("#selectedFromTranslatorType option:selected").attr("id") == "DIAMETER") {
		$("#selectedToTranslatorType option[id='DIAMETER']").show();
		$("#selectedToTranslatorType option[id='RADIUS']").hide();
		
	}
	if($("#selectedFromTranslatorType option:selected").attr("id") == "RADIUS"){
		 $("#selectedToTranslatorType option[id='RADIUS']").show();
		 $("#selectedToTranslatorType option[id='DIAMETER']").hide();
	}
	

}

var isValidName;

function validateForm(){	
	
		if(isNull(document.forms[0].name.value)){
 			alert('Name must be specified.');
 			return;
 		}else if(!isValidName) {
 			alert('Enter Valid Name');
 			document.forms[0].name.focus();
 			return;
 		}else if(document.forms[0].selectedFromTranslatorType.value == ''){
 			alert('Select \'From\' Translation Type.');
 			return;
 		}else if(document.forms[0].selectedToTranslatorType.value == ''){
 			alert('Select \'To\' Translation Type.');
 			return;
 		}else{
 	 	 	document.forms[0].submit();
 		}	
	}

	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.COPY_PACKET_TRANSLATION_MAPPING_CONFIG%>',searchName,'create','','verifyNameDiv');
		if(isValidName == true){
		isValidTranslationName = verifyInstanceName('<%=InstanceTypeConstants.TRANSLATION_MAPPING_CONFIG%>',searchName,'create','','verifyNameDiv');
		}
	}
	
	setTitle('<bean:message bundle="servermgrResources" key="copypacket.title"/>');
</script>
<%
	CreateCopyPacketTransMappingForm createCopyPacketTransMappingForm = (CreateCopyPacketTransMappingForm) request.getAttribute("createCopyPacketTransMappingForm");
	
%>


<html:form action="/createCopyPacketMappingConfig" styleId="mainform">

	<html:hidden name="createCopyPacketTransMappingForm"
		property="action" />

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
											bundle="servermgrResources" key="copypacket.create" />
									</td>
								</tr>
								<tr>
									<td colspan="4">
										<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0">
											<tr>
												<td align="left" class="captiontext" valign="top" width="20%">
													<bean:message bundle="servermgrResources" 
														key="copypacket.name" />
															<ec:elitehelp headerBundle="servermgrResources" 
																text="copypacket.name" 
																	header="copypacket.name"/>
														
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text property="name" styleId="name" tabindex="1" onkeyup="verifyName();" size="30" maxlength="50" style="width:250px"></html:text>
													<font color="#FF0000">*</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="servermgrResources"
														key="copypacket.description" />
															<ec:elitehelp headerBundle="servermgrResources" 
																text="copypacket.description" 
																	header="copypacket.description"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:textarea styleId="description" tabindex="2" property="description" cols="40" rows="2" style="width:250px" />
												</td>
											</tr>
											<tr>
												<td class="captiontext" valign="top" >
													<bean:message bundle="servermgrResources" 
														key="copypacket.script" />
															<ec:elitehelp headerBundle="servermgrResources" 
																text="copypacket.script" 
																	header="copypacket.script"/>
												</td>
												<td class="labeltext" valign="top" nowrap="nowrap" >
													<html:text property="script" size="30" style="width:250px" maxlength="255" tabindex="3" styleClass="esiScriptAutocomplete"></html:text>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="servermgrResources" 
														key="copypacket.type" />
															<ec:elitehelp headerBundle="servermgrResources" 
																text="transmapping.type" 
																	header="copypacket.type"/> 
												</td>
												<td align="left" class="labeltext" valign="top">
													From: 
													<html:select property="selectedFromTranslatorType" tabindex="4" styleId="selectedFromTranslatorType" style="width:130px">
														<html:option value="">-Select-</html:option>
														<logic:iterate id="translationTypeBean" name="createCopyPacketTransMappingForm" property="fromTranslatorTypeList" type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData">
															<html:option value="<%=translationTypeBean.getTranslatorTypeId()%>" styleId="<%=translationTypeBean.getValue()%>">
																<bean:write name="translationTypeBean" property="name" />
															</html:option>
														</logic:iterate>
													</html:select> 
													<font color="#FF0000"> *</font> 
													To: 
													<html:select tabindex="5" property="selectedToTranslatorType" styleId="selectedToTranslatorType" style="width:130px">
														<html:option value="">-Select-</html:option>
														<logic:iterate id="translationTypeBean" name="createCopyPacketTransMappingForm" property="toTranslatorTypeList" type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData">
															<html:option value="<%=translationTypeBean.getTranslatorTypeId()%>" styleId="<%=translationTypeBean.getValue()%>">
																<bean:write name="translationTypeBean" property="name" />
															</html:option>
														</logic:iterate>
													</html:select> <font color="#FF0000"> *</font>
												</td>
											</tr>
											
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td width="10">&nbsp;</td>
												<td class="btns-td" valign="middle"><input
													type="button" tabindex="6" name="c_btnCreate"
													id="c_btnCreate2" value=" Next " class="light-btn"
												 onclick="validateForm()"> <input
													type="reset" tabindex="7" name="c_btnDeletePolicy"
													value="Cancel" class="light-btn" 
													onclick="javascript:location.href='<%=basePath%>/initSearchCopyPacketMappingConfig.do?/>'"></input>
												</td>
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




