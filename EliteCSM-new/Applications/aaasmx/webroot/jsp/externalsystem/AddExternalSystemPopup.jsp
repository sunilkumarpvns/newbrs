<%@page import="com.elitecore.elitesm.web.externalsystem.forms.AddExternalSystemPopupForm"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData"%><script>
function add(){
	
	var weightageArray =  new Array();
	var serverNameArray =  new Array();
	var selectedCheckBox =document.getElementsByName("externalSystems");
	
	if(selectedCheckBox.length==1 && selectedCheckBox.checked  == true){
		
		
		var weightageText = document.getElementById(selectedCheckBox.value);
		var serverObjectName = 'externalSystemName'+selectedCheckBox.value;
		var serverNameObject = document.getElementById(serverObjectName);
		weightageArray[0] = weightageText.value;
    	serverNameArray[0] = serverNameObject.value;
    	
	}else{
		
		for(var i=0;i<selectedCheckBox.length;i++){
			
			
			if(selectedCheckBox[i].checked  == true){
		
				var textbox = 'textBox'+selectedCheckBox[i].value
		     	var weightageText = document.getElementById(textbox);
				var serverObjectName = 'externalSystemName'+selectedCheckBox[i].value;
				var serverNameObject = document.getElementById(serverObjectName);
				weightageArray[i] = weightageText.value;
		    	serverNameArray[i] = serverNameObject.value;
		    	
			}
		}
	}

	window.opener.addItemInCurrentList(selectedCheckBox,weightageArray,serverNameArray);
	window.close();
}



function removeExistingExternalSystem(){
	var parentList = window.opener.currentList ;
	var size = parentList.options.length;
	for(var i=0;i<parentList.options.length;i++){
		var valueString =parentList.options[i].value;
		var splitValue = valueString.split('-');
		
		var trCompName = 'trComp'+splitValue[0];
		var trCompObject =  document.getElementById(trCompName);
		if(trCompObject!=null)
			trCompObject.style.display="none";	
	}
}
function  checkAll(){
 	if( document.forms[0].toggleAll.checked == true) {
 		var selectVars = document.getElementsByName('externalSystems');
	 	for (i = 0; i < selectVars.length;i++){
	 		var trCompName = 'trComp'+selectVars[i].value;
	 		var trCompObject =  document.getElementById(trCompName);
		 	if(trCompObject.style.display!="none"){
				selectVars[i].checked = true ;
		 	}
	 	}
    } else if (document.forms[0].toggleAll.checked == false){
 		var selectVars = document.getElementsByName('externalSystems');	    
		for (i = 0; i < selectVars.length; i++)
			selectVars[i].checked = false ;
	}
}
setTitle('External System');
</script>
<%
	String basePath = request.getContextPath();
	List<ExternalSystemInterfaceInstanceData> externalSystemList = (List) request.getAttribute("externalSystemInstanceList");
	AddExternalSystemPopupForm addExternalSystemPopupForm = (AddExternalSystemPopupForm)request.getAttribute("addExternalSystemPopupForm");
%>

<form name="test">
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
									<td class="table-header">Add External System</td>
								</tr>
								<tr>
									<td colspan="3"><input type="button" value="Add"
										onclick="add()" class="light-btn" /></td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3">
										<table name="c_tblCrossProductList" width="97%"
											cellpadding="0" border="0" cellspacing="0">
											<tr>
												<td class="tblheader" width="5%"><input type="checkbox"
													name="toggleAll" value="checkbox" onclick="checkAll()" /></td>
												<td class="tblheader" width="15%"><bean:message
														bundle="externalsystemResources" key="esi.name" /></td>
												<td class="tblheader" width="20%"><bean:message
														bundle="externalsystemResources" key="esi.address" /></td>
												<td class="tblheader" width="15%"><bean:message
														bundle="externalsystemResources" key="esi.minlocalport" /></td>
												<td class="tblheader" width="20%"><bean:message
														bundle="externalsystemResources"
														key="esi.expiredrequestlimitcount" /></td>
												<%if(addExternalSystemPopupForm.isWeightageSelection()){%>
												<td class="tblheader" width="15%">Weightage</td>
												<%} %>
											</tr>
											<%
									int index=1;
									if(externalSystemList!=null && !externalSystemList.isEmpty()){ %>
											<logic:iterate id="externalSystemInstanceData"
												name="externalSystemInstanceList"
												type="ExternalSystemInterfaceInstanceData">
												<% 
									String strEsiInstanceId = Long.toString(externalSystemInstanceData.getEsiInstanceId());
									String trName = "trComp"+ strEsiInstanceId;
									String externalSystemName = "externalSystemName"+ strEsiInstanceId;
									String textBoxName = "textBox"+ strEsiInstanceId;
									%>
												<tr id="<%=trName%>">
													<td class="tblfirstcol"><input type="checkbox"
														value="<%=strEsiInstanceId%>" name="externalSystems" /></td>
													<td class="tblrows"><bean:write
															name="externalSystemInstanceData" property="name" /></td>
													<input type="hidden" name="<%=externalSystemName%>"
														id="<%=externalSystemName%>"
														value="<bean:write name="externalSystemInstanceData" property="name"/>" />
													<td class="tblrows"><bean:write
															name="externalSystemInstanceData" property="address" /></td>
													<td class="tblrows"><bean:write
															name="externalSystemInstanceData" property="minLocalPort" /></td>
													<td class="tblrows"><bean:write
															name="externalSystemInstanceData"
															property="expiredRequestLimitCount" /></td>
													<%if(addExternalSystemPopupForm.isWeightageSelection()){ %>

													<td class="tblrows"><select name="<%=textBoxName%>"
														id="<%=textBoxName%>" style="width: 75px">
															<%for(int weightage=0; weightage<=10;weightage++){
										if(weightage==1){
										%>
															<option value="<%=weightage%>" selected="selected"><%=weightage%></option>
															<%}else{%>
															<option value="<%=weightage%>"><%=weightage%></option>
															<%}} %>
													</select></td>
													<%}else{%>
													<input type="hidden" value="0" name="<%=textBoxName%>"
														id="<%=textBoxName%>" />
													<%} %>
												</tr>
												<%index++; %>
											</logic:iterate>
											<%}%>
										</table>

									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3"><input type="button" value="Add"
										onclick="add()" class="light-btn" /></td>
								</tr>
								<tr height="2">
									<td></td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</form>
<script>
removeExistingExternalSystem();
</script>

