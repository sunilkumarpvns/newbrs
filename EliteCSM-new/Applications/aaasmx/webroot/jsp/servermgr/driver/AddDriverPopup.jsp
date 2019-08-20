
<%@page
	import="com.elitecore.elitesm.web.driver.forms.AddDriverPopupForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>




<%@page import="java.util.List"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%
	String basePath = request.getContextPath();
	List<DriverInstanceData> driverInstanceDataList = (List) request.getAttribute("driverInstanceDataList");
%>



<script>
function add(){
	
	var serverNameArray =  new Array();
	var wieghtageArray = new Array();
	var selectedCheckBox = document.getElementsByName("driverInstances");
	
	
	if(selectedCheckBox.length==1 && selectedCheckBox.checked  == true){
	
     	var weightageText = document.getElementById(selectedCheckBox.value);
		var serverObjectName = 'driverName'+selectedCheckBox.value;
		var serverNameObject = document.getElementById(serverObjectName);
		wieghtageArray[0] = weightageText.value;
    	serverNameArray[0] = serverNameObject.value;
	}else{
		for(var i=0;i<selectedCheckBox.length;i++){
			if(selectedCheckBox[i].checked  == true){					
			var textBox =  'wtg'+selectedCheckBox[i].value;			
			var weightageText = document.getElementById(textBox);			
			var serverObjectName = 'driverName'+selectedCheckBox[i].value;
			var serverNameObject = document.getElementById(serverObjectName);
			wieghtageArray[i] = weightageText.value;
	    	serverNameArray[i] = serverNameObject.value;
			}
		}
	}	
	window.opener.addItemInDriverCurrentList(selectedCheckBox,serverNameArray,wieghtageArray);
	window.close();
}
function  checkAll(){
 	if( document.forms[0].toggleAll.checked == true) {
 		var selectVars = document.getElementsByName('driverInstances');
	 	for (i = 0; i < selectVars.length;i++){
			var trCompName = 'trComp'+selectVars[i].value;
			var trCompObject =  document.getElementById(trCompName);
		 	if(trCompObject.style.display!="none"){
				selectVars[i].checked = true ;
		 	}
	 	}
    } else if (document.forms[0].toggleAll.checked == false){
 		var selectVars = document.getElementsByName('driverInstances');	    
		for (i = 0; i < selectVars.length; i++)
			selectVars[i].checked = false ;
	}
}
function removeExistingDrivers(){
//	alert("removing called");
	var parentMainDriverList = window.opener.mainDriverList ;
	var parentFallbackDriverList = window.opener.fallbackDriverList ;
	
	
	if(parentMainDriverList!=null){
	for(var i=0;i<parentMainDriverList.options.length;i++){
		var trCompName = 'trComp'+parentMainDriverList.options[i].value;
		var trCompObject =  document.getElementById(trCompName);
		if(trCompObject!=null)
			trCompObject.style.display="none";	
	}
	}
	if(parentFallbackDriverList!=null){
		for(var i=0;i<parentFallbackDriverList.options.length;i++){
			var trCompName = 'trComp'+parentFallbackDriverList.options[i].value;
			var trCompObject =  document.getElementById(trCompName);
			if(trCompObject!=null)
				trCompObject.style.display="none";	
		}
	}
}
setTitle('Service Driver');
</script>
<%
   AddDriverPopupForm addDriverPopupForm = (AddDriverPopupForm)request.getAttribute("addDriverPopupForm");
%>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="7">&nbsp;</td>
			<td width="821" colspan="2">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td width="7">&nbsp;</td>
						<td width="100%" colspan="2"><%@ include
								file="/jsp/core/includes/common/HeaderBar.jsp"%>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" width="7">&nbsp;</td>
		</tr>
		<tr>
			<td width="10">&nbsp;</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0"
				width="100%">
				<form action="">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>

							<td class="table-header" colspan="3">Add Driver</td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
						<tr>
							<td width="10"></td>
							<td colspan="2"><input type="button" value="Add"
								onclick="javascript:add();" class="light-btn" /></td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
						<tr>
							<td width="10"></td>
							<td colspan="2">

								<table name="c_tblCrossProductList" width="100%" border="0"
									cellpadding="0" cellspacing="0">
									<tr>
										<td align="center" class="tblheader" valign="top" width="1%">
											<input type="checkbox" name="toggleAll" value="checkbox"
											onclick="checkAll()" />
										</td>
										<td class="tblheader">Name</td>
										<td class="tblheader">Description</td>
										<%if(addDriverPopupForm.isWeightageSelection()){ %>
										<td class="tblheader">Weightage</td>
										<%}%>
									</tr>
									<%
									int index=1;
									if(driverInstanceDataList!=null && !driverInstanceDataList.isEmpty()){ %>
									<logic:iterate id="driverInstanceData"
										name="driverInstanceDataList" type="DriverInstanceData">
										<% 
									String trName = "trComp"+ driverInstanceData.getDriverInstanceId();
									String driverName = "driverName"+ driverInstanceData.getDriverInstanceId();
									String weightageInput = "wtg" + driverInstanceData.getDriverInstanceId();
									%>
										<input type="hidden" name="<%=driverName%>"
											id="<%=driverName%>"
											value="<%=driverInstanceData.getName()%>" />
										<tr id="<%=trName%>">
											<td class="tblfirstcol"><input type="checkbox"
												value="<%=driverInstanceData.getDriverInstanceId()%>"
												name="driverInstances" /></td>
											<td align="left" class="tblrows"><bean:write
													name="driverInstanceData" property="name" /></td>
											<td align="left" class="tblrows"><%=EliteUtility.formatDescription(driverInstanceData.getDescription())%>&nbsp;</td>
											<%if(addDriverPopupForm.isWeightageSelection()){ %>
											<td align="left" class="tblrows"><select
												name="<%=weightageInput%>" id="<%=weightageInput%>"
												style="width: 75px">
													<%for(int weightage=0; weightage<=10;weightage++){
													if(weightage==1){
													%>
													<option value="<%=weightage%>" selected="selected"><%=weightage%></option>
													<%}else{%>
													<option value="<%=weightage%>"><%=weightage%></option>
													<%}
													} %>
											</select></td>
											<%}else{%>
											<input type="hidden" value="0" name="<%=weightageInput%>"
												id="<%=weightageInput%>" />
											<%} %>
										</tr>
										<%index++; %>
									</logic:iterate>
									<%} %>
								</table>

							</td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
						<tr>
							<td width="10"></td>
							<td colspan="2"><input type="button" value="Add"
								onclick="javascript:add();" class="light-btn" /></td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
					</table>
				</form>
			</td>
		</tr>
	</table>
</table>
<script>
 removeExistingDrivers();
 </script>
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
