<%@page import="com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterHSSAuthDriverForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.*"%>
<%@ page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData" %>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.CommandCode" %>

<%
		DriverInstanceData driverInstanceData = (DriverInstanceData)request.getSession().getAttribute("driverInstance");
		UpdateDiameterHSSAuthDriverForm updateDiameterHSSAuthDriverForm = (UpdateDiameterHSSAuthDriverForm)request.getAttribute("diameterHSSAuthDriverForm");
%>
<script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/radius-hss-driver.js"></script>
<script language="javascript">

	var commandCodeList = [];
	
	<%for(CommandCode commandCode:CommandCode.VALUES){%>
		commandCodeList.push({'value':'<%=commandCode.code%>','label':'[<%=commandCode.code%>] <%=commandCode.name()%>'});
	<%}%>

 	$(document).ready(function() {
		
		var logicalNameData =eval(('<bean:write name="logicalNameData"/>').replace(new RegExp("&quot;", 'g'),"\""));
		/* set Logical Data JsonObject */
		setLogicalNameData(logicalNameData);
		
		/* set Logical Name drop down for default value */ 
		setLogicalnameDropDown("mappingtbl","logicalnmVal",true);
		
		/* bind click event of delete image */
		$('#mappingtbl td img.delete').live('click',function() {
			 $(this).parent().parent().remove(); 
			 setLogicalnameDropDown("mappingtbl","logicalnmVal",true); /* set Logical Name drop down after remove row */
		});
		
		$('#addPeers td img.delete').live('click',function() {
			 $(this).parent().parent().remove(); 
		});
		
		$("#peerPopup").dialog({
			modal: true,
			autoOpen: false,		
			height: "auto",
			width: 700,
			buttons:{					
				        'Add': function() {
				        		var idArray=[];
				        		var nameArray=[];
				        		var index=0,isExist=false;
				        		
				        		var addPeerOption = [];
				        		
				        		 $('#addPeers .peerId').each(function(){
				        			 var peerId=$(this).val();
				        			 addPeerOption.push(peerId);
				        		});
				        		
				        		if(hasDuplicates(addPeerOption)){
				        			alert('Duplicate Peer name found');
				        			return false;
				        		}
				        		
				        		$('#selectedPeerIds').find('option').remove();
				        		 $('#addPeers .peerId').each(function(){
				        			 var peerId=$(this).val();
				        			 if(peerId == "0"){
				        				 isExist=true;
				        			}
				        		});
				        		 if(isExist == true){
				        			 alert('Please select peer name');
				        			 return false;
				        		 }else{
				        			 $('#addPeers .peerId').each(function(){
						        			var peerId=$(this).val();
						        			var peerIdText = $(":selected",this).text();

						        			idArray.push(peerId);
						        			nameArray.push(peerIdText);
						        		});
						        		
						        		 $('#addPeers .weightage').each(function(){
					        				var weightageId = $(":selected",this).text();
											$("#selectedPeerIds").append("<option id="+ idArray[index]+" value="+ idArray[index] +"("+weightageId+ ") class=labeltext> "+nameArray[index]+"- w"+weightageId+" </option>");
						        			index++;
					        			});
				                     $(this).dialog('close');
				        		 }
				        		
						},                
						Cancel: function() {
						$(this).dialog('close');
						}
					},
			open: function() {
			
			},
			close: function() {
				
			}				
		});
		
		setSuggestionForCommandCode(commandCodeList);
	});	
 	
 	function setSuggestionForCommandCode(commandCodeArray) {
		 $( ".commandCode").autocomplete({	
				source:commandCodeArray });
	}
 	
	function addNewRowForPeer(templateTableId, tableId,logicalName,multipleAllowFunctionality){
		var tableRowStr = $("#"+templateTableId).find("tr");
  		$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
  		if($("#"+tableId+" select").size() > 0){
  	  		$("#"+tableId+" tr:last").find("select:first").focus();
  		}else{
  			$("#"+tableId+" tr:last").find("input:first").focus();
  		}
	}
 	
 	function selectAll(selObj){
 		for(var i=0;i<selObj.options.length;i++){
 			selObj.options[i].selected = true;
 		}
 	}

 	
 	function validateForm(){
 		
 		if(isNull(document.forms[0].driverinstname.value)){
 			alert('Driver Name must be specified');
 			document.forms[0].driverinstname.focus();
 			return;
 		}
 	 
 		if(!isValidName) {
 			alert('Enter Valid Driver Name');
 			document.forms[0].driverinstname.focus();
 			return;
 		}
 		
 		if(!validateName(document.forms[0].driverinstname.value)){
 			alert('Driver Name should have following characters. A-Z, a-z, 0-9, _ and - ');
 			document.forms[0].driverinstname.focus();
 			return;
 		}
 		
 		if(isNull(document.forms[0].applicationid.value)){
 			alert('3GPP Application Id must be specified');
 			document.forms[0].applicationid.focus();
 			return;
 		}
 		
 		if(isNull(document.forms[0].commandCode.value)){
 			alert('Command Code must be specified');
 			document.forms[0].commandCode.focus();
 			return;
 		}
 		

	 	var selectedPeerIds=document.getElementById("selectedPeerIds");	
	
		if(selectedPeerIds.options.length > 0){
			selectAll(selectedPeerIds);
			if(isValidLogicalNameMapping("mappingtbl", "logicalnmVal", "dbfieldVal")){
		 		document.forms[0].submit();
			}
		}else{
				alert('Peer must be specified');	
				return;
		 }
 	}
 	
 	function validateName(val){
 		var test1 = /(^[A-Za-z0-9-_]*$)/;
 		var regexp =new RegExp(test1);
 		if(regexp.test(val)){
 			return true;
 		}
 		return false;
 	}
 	
 	function addPeers(){
 		$("#peerId").val("0");
 		$("#weightage").val("");
 		$("#peerPopup").dialog("open");
 	}
 	
 	function removePeers(){
 		var id = "#selectedPeerIds option:selected";	
 		var removeElement = $(id).attr('id');
 		
 		$('#addPeers tr').each(function(){
 			if(removeElement == $(this).find('select').val()){
 				$(this).remove();
 			}
 		});
 		
  		$(id).each(function(){
  		     $(this).remove();				      
  	    });
 	}
 	
 	function hasDuplicates(array) {
 	    var valuesSoFar = {};
 	    for (var i = 0; i < array.length; ++i) {
 	        var value = array[i];
 	        if (Object.prototype.hasOwnProperty.call(valuesSoFar, value)) {
 	            return true;
 	        }
 	        valuesSoFar[value] = true;
 	    }
 	    return false;
 	}
 	
 	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 	
 	
 	$(document).ready(function(){
 		<%
		List<DiameterPeerRelData> driverListObj = updateDiameterHSSAuthDriverForm.getDiameterPeerRelDataList();												
		if(driverListObj != null){
			for(int i = 0;i<driverListObj.size();i++){
																
				DiameterPeerRelData dataObj = driverListObj.get(i);
				String name = " ";
				if(dataObj.getWeightage() != null)
					name = dataObj.getWeightage().toString();
				name = dataObj.getDiameterPeerData().getName() + " - w" + name;		
				String value = dataObj.getPeerUUID() + "(" + dataObj.getWeightage() + ")";
				String ids = dataObj.getPeerUUID();
			%>																												
				addNewRowForPeerInit("peerMappingTable","addPeers","peerId",true,'<%=ids%>','<%=dataObj.getWeightage()%>');
			<%}
		}%>
 	});
 	
 	function addNewRowForPeerInit(templateTableId, tableId,logicalName,multipleAllowFunctionality,ids,weightage){
		var tableRowStr = $("#"+templateTableId).find("tr");
		
		var selectobj = $("#"+tableId+" tr:last").find('select:first').val();
		if(selectobj == "0"){
			$("#"+tableId+" tr:last").remove();
		}
		
  		$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
  		if($("#"+tableId+" select").size() > 0){
  	  		$("#"+tableId+" tr:last").find("select:first").focus();
  		}else{
  			$("#"+tableId+" tr:last").find("input:first").focus();
  		}
  		var selectInput = $("#" + tableId +" tr:last").find('.peerId');
  		$(selectInput).val(ids);
  		
  		var selectInputWeightage = $("#" + tableId +" tr:last").find('.weightage');
  		$(selectInputWeightage).val(weightage);
	}
</script>


<script>
	var isValidName;

	function verifyName() {
		var searchName = document.getElementById("driverinstname").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
	}
	
	$(document).ready(function(){

		$('#mappingtbl td img.delete').live('click',function() {	
				 $(this).parent().parent().remove(); 
		});	
	});	
	
	function splitDbFields( val ) {
 		return val.split( /[,;]\s*/ );
 	}

 	function extractLastDbFields( term ) {
 		return splitDbFields( term ).pop();
 	}
	
	var myArray = new Array();
 	function retriveDiameterDictionaryAttributesForText() {
 		
 		var dbFieldStr;
 		var dbFieldArray;
 		var searchNameOrAttributeId = "";
 		$.post("SearchDiameterAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
 			dbFieldStr = data.substring(1,data.length-3);
 			dbFieldArray = new Array();
 			dbFieldArray = dbFieldStr.split("#,");
 			var value;
 			var label;
 			var desc;
 			for(var i=0;i<dbFieldArray.length;i++) {
 				tmpArray = dbFieldArray[i].split(",");
 				value = tmpArray[0].trim();
 				label = tmpArray[1];
 				var item = new ListItem(value,label); 
 				myArray.push(item);
 			}	
 		});
 		
 	}
 	
 	function  setAutoCompleteData(dbFieldObject){
 		retriveDiameterDictionaryAttributesForText();
 		$( dbFieldObject ).bind( "keydown", function( event ) {
 				if ( event.keyCode === $.ui.keyCode.TAB &&
 					$( this ).autocomplete( "instance" ).menu.active ) {
 					event.preventDefault();
 				}
 		 }).autocomplete({
 			minLength: 0,
 			source: function( request, response ) {
 				response( $.ui.autocomplete.filter(
 						myArray, extractLastDbFields( request.term ) ) );
 			},
 			focus: function() {
 				return false;
 			},
 			select: function( event, ui ) {
 				var val = this.value;
 				var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
 				var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
 				 if(commaIndex == semiColonIndex) {
 						val = "";
 				}  else if(commaIndex > semiColonIndex) {
 						val = val.substring(0,commaIndex+1); 
 				} else if(semiColonIndex !=0 && semiColonIndex > commaIndex){
 					val = val.substring(0,semiColonIndex+1); 
 				}	 
 				this.value = val + ui.item.value ;
 				return false;
 			}
 		});
 	}
 	
	
</script>

<html:form action="/initUpdateDiameterHssDriver">

	<html:hidden property="action" value="update"/>
	<html:hidden property="driverRelatedId" />
	<html:hidden property="auditUId" styleId="auditUId"/>
	<html:hidden property="driverInstanceId" styleId="driverInstanceId"/>
	<html:hidden property="hssauthdriverid" styleId="hssauthdriverid"/>

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
			<table cellpadding="0" cellspacing="0" border="0" width="100%" height="15%">

						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="3">
								<bean:message bundle="driverResources" key="driver.driverinstancedetails" />
							</td>
						</tr>
		
						<tr>
							<td align="left" class="captiontext" valign="top" width="30%">
								<bean:message bundle="driverResources" key="driver.instname" />
								<ec:elitehelp headerBundle="driverResources" text="createdriver.name" header="driver.instname"/>
							</td>
							<td align="left" class="labeltext" valign="top" width="65%">
								<html:text styleId="driverinstname" onkeyup="verifyName();" property="driverInstanceName"  name="diameterHSSAuthDriverForm" size="30" maxlength="60" style="width:250px" tabindex="1"/>
								<font color="#FF0000"> *</font>
								<div id="verifyNameDiv" class="labeltext"></div>	
							</td>
						</tr>
		
						<tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="driverResources" key="driver.instdesc" />
								<ec:elitehelp headerBundle="driverResources" text="createdriver.description" header="driver.instdesc"/>
							</td>
							<td align="left" class="labeltext" valign="top" width="65%">
								<html:textarea property="driverInstanceDesc" styleId="driverInstanceDesc" name="diameterHSSAuthDriverForm" style="width:250px" tabindex="2"></html:textarea>
							</td>
						</tr>
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="3">
								<bean:message bundle="driverResources" key="driver.diameterhssdriver" />
							</td>
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="driverResources" key="driver.hssdriver.applicationid" /> 
								<ec:elitehelp headerBundle="driverResources" text="driver.hssdriver.applicationid" header="driver.hssdriver.applicationid"/>
							</td>
							<td align="left" class="labeltext" valign="top" width="70%">
								 <html:text styleId="applicationid" name="diameterHSSAuthDriverForm"  property="applicationid"  style="width:250px" tabindex="1" />
								 <font color="#FF0000"> *</font>
							</td>							
						</tr>
					    <tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="driverResources" key="driver.hssdriver.commandcode" /> 
								<ec:elitehelp headerBundle="driverResources" text="driver.hssdriver.commandcode" header="driver.hssdriver.commandcode"/>
							</td>
							<td align="left" class="labeltext" valign="top" width="70%">
								 <html:text property="commandCode" name="diameterHSSAuthDriverForm" styleId="commandCode" style="width:250px" tabindex="2" styleClass="commandCode"></html:text>
								 <font color="#FF0000"> *</font>
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="driverResources" key="driver.hssdriver.requesttimeout" />
								<ec:elitehelp headerBundle="driverResources" text="driver.hssdriver.requesttimeout" header="driver.hssdriver.requesttimeout"/>
							</td>
							<td align="left" class="labeltext" valign="top" width="70%">
								 <html:text styleId="requesttimeout" name="diameterHSSAuthDriverForm"  property="requesttimeout"  style="width:250px" tabindex="6" />
							</td>							
						</tr>
						
						
						
						<tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="driverResources" key="driver.hssdriver.useridentityattribute" /> 
								<ec:elitehelp headerBundle="driverResources" text="driver.hssdriver.useridentityattribute" header="driver.hssdriver.useridentityattribute"/>						 
							</td>
							<td align="left" class="labeltext" valign="top" width="70%">
								<html:text styleId="userIdentityAttributes" property="userIdentityAttributes" name="diameterHSSAuthDriverForm"  style="width:250px" tabindex="7" />
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="driverResources" key="driver.hhsdriver.nooftriplets" /> 
								<ec:elitehelp headerBundle="driverResources" text="driver.hssdriver.nooftriplets" header="driver.hhsdriver.nooftriplets"/>
							</td>
							<td align="left" class="labeltext" valign="top" width="70%">
								<html:text styleId="noOfTriplets" property="noOfTriplets" name="diameterHSSAuthDriverForm"  style="width:250px" tabindex="5" />
							</td>							
						</tr>		 		
						
					
						 <tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="driverResources" key="driver.hhsdriver.additionalattributes" /> 
								<ec:elitehelp headerBundle="driverResources" text="driver.hhsdriver.additionalattributes" header="driver.hhsdriver.additionalattributes"/>
							</td>
							<td align="left" class="labeltext" valign="top" width="70%">
								<html:textarea styleId="additionalAttributes" name="diameterHSSAuthDriverForm" tabindex="6" property="additionalAttributes" cols="50" rows="2" style="width:250px"/>
							</td>							
						</tr>
						 <tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="driverResources" key="driver.hhspeerconfiguration" /> 
								<ec:elitehelp headerBundle="driverResources" text="driver.hhspeerconfiguration" header="driver.hhspeerconfiguration"/>
							</td>
							<td align="left" class="labeltext" valign="top" width="70%">
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td>
											<select class="labeltext" name="selectedPeerIds" id="selectedPeerIds" multiple="true" size="5" style="width: 250px;">
											
												<%
													
												List<DiameterPeerRelData> driverList = updateDiameterHSSAuthDriverForm.getDiameterPeerRelDataList();												
												if(driverList != null){
													for(int i = 0;i<driverList.size();i++){
																										
														DiameterPeerRelData data = driverList.get(i);
														String nm = " ";
														if(data.getWeightage() != null)
															nm = data.getWeightage().toString();
														nm = data.getDiameterPeerData().getName() + " - w" + nm;		
														String value = data.getPeerUUID() + "(" + data.getWeightage() +")";
														String ids = data.getPeerUUID();
													%>																												
														<option id="<%=ids%>" class="labeltext" value="<%=value%>" ><%=nm%></option>
													<%}
												}%>
											
											</select>
										</td>
										<td style="padding-left: 5px;">
											<input type="button" value="Add " onClick="addPeers()"  class="light-btn"  style="width: 75px"/><br/>
											<br/>
											<input type="button" value="Remove "  onclick="removePeers()" class="light-btn" style="width: 75px"/>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="3">
								<bean:message bundle="driverResources" key="driver.diameterhhsfieldmap" />
							</td>
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top" colspan="2">
								<input type="button" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);' value=" Add " class="light-btn" style="size: 140px" tabindex="9">
							</td>
					   </tr>
					   <tr>
							<td align="left" width="100%" class="captiontext" colspan="3" valign="top">
								<table width="97%" id="mappingtbl" cellpadding="0" cellspacing="0" border="0">
									<tr>
										<td align="left" class="tblheader" valign="top" width="23%">
											<bean:message bundle="driverResources" key="driver.diameter.logicalname" />
											<ec:elitehelp headerBundle="driverResources"  text="driver.hssdriver.logicalname" header="driver.diameter.logicalname"/>
								    	</td>
								    	<td align="left" class="tblheader" valign="top"  width="23%">
											<bean:message bundle="driverResources" key="driver.hssdriver.attributeids" />
											<ec:elitehelp headerBundle="driverResources" text="driver.hssdriver.attributeid" header="driver.hssdriver.attributeids"/>
										</td>
										<td align="left" class="tblheader" valign="top"  width="23%">
											<bean:message bundle="driverResources" key="driver.defaultvalue" /> 
											<ec:elitehelp headerBundle="driverResources"  text="driver.hssdriver.defaultvalue" header="driver.defaultvalue"/>
										</td>
										<td align="left" class="tblheader" valign="top"  width="23%">
											<bean:message bundle="driverResources" key="driver.valuemapping" />
											<ec:elitehelp headerBundle="driverResources" text="driver.hssdriver.valuemapping" header="driver.valuemapping"/>
									   </td>
							           <td align="left" class="tblheader" valign="top"width="5%">Remove</td>
									</tr>
									 <logic:iterate id="obj" name="diameterHSSAuthDriverForm" property="hssAuthDriverFieldMapDataList"  type="com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverFieldMapData">
										<tr>
											<td class="allborder">
												<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
													<option value='<bean:write name="obj" property="nameValuePoolData.value"/>'><bean:write name="obj" property="nameValuePoolData.name"/> </option>
												</select>
											</td>
											<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="10" value='<bean:write name="obj" property="attributeIds"/>' onfocus="setAutoCompleteData(this);"/></td>
											<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="11" value='<bean:write name="obj" property="defaultValue"/>'/></td>
											<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="12" value='<bean:write name="obj" property="valueMapping"/>'/></td>
											<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="13"/></td>
										</tr>
									</logic:iterate> 
								</table>
							</td>
					   </tr>
						<tr>
							<td class="btns-td" valign="middle">&nbsp;</td>
							<td class="btns-td" valign="middle" colspan="2">
								<input type="button" name="c_btnCreate" id="c_btnCreate2" value=" Update " class="light-btn" onclick="validateForm()" tabindex="14"/> 
								<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'" value="Cancel" class="light-btn" tabindex="15">
							</td>
						</tr>
			  </table>
			</td>
		</tr>
	</table>
</html:form>
<script>
setColumnsOnTextFields();    
</script>
<!-- Mapping Table Row template -->
	<table style="display:none;" id="dbMappingTable">
		<tr>
			<td class="allborder">
				<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="16" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
				</select>
			</td>
			<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="17" onfocus="setAutoCompleteData(this);"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="18"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="19"/></td>
			<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="20"/></td>
  		</tr>
	</table>
	 <div id="peerPopup" style="display: none;" title="Add Peers">
		<input type="button" onclick='addNewRowForPeer("peerMappingTable","addPeers","peerId",true);' value=" Add " class="light-btn" style="size: 140px" tabindex="9">
		<table id="addPeers" name="addPeers" cellpadding="0" cellspacing="0" width="100%" class="box"> 	
			<tr>
				<td align="left" class="tblheader" valign="top" width="30%">
					<bean:message bundle="driverResources" key="driver.hssdriver.peername" />
						<ec:elitehelp headerBundle="driverResources" 
							text="driver.peername" header="driver.hssdriver.peername"/>
				</td>
				<td align="left" class="tblheader" valign="top" width="70%">
					 <bean:message bundle="driverResources" key="driver.hssdriver.weightage" />
					 	<ec:elitehelp headerBundle="driverResources" 
					 		text="driver.weightage" header="driver.hssdriver.weightage"/>
				</td>
				<td align="left" class="tblheader" valign="top">Remove</td>
			</tr>
			<tr>
				<td class="allborder">
					<select id="peerId" name="peerId" style="width: 200px;" class="noborder peerId">
						<option value="0">--select--</option>
						<logic:iterate id="obj" name="peerList" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
							<option value="<bean:write name="obj" property="peerUUID"/>">
								<bean:write name="obj" property="name"/>
							</option>
						</logic:iterate>
					</select>
				</td>
				<td class="tblrows">
					<select id="weightage" name="weightage" class="noborder weightage" style="width: 100px;">
						<%for(int i=0;i<=10;i++){%>
							<option value="<%=i%>"><%=i%></option>
						<%}%>
					</select>
				</td>
				<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="20"/></td>
  	
			</tr>
		</table>
	</div>	
	<!-- Mapping Table Row template -->
	<table style="display:none;" id="peerMappingTable">
		<tr>
				<td class="allborder">
					<select id="peerId" name="peerId" style="width: 200px;" class="noborder peerId">
						<option value="0">--select--</option>
						<logic:iterate id="obj" name="peerList" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
							<option value="<bean:write name="obj" property="peerUUID"/>">
								<bean:write name="obj" property="name"/>
							</option>
						</logic:iterate>
					</select>
				</td>
				<td class="tblrows">
					<select id="weightage" name="weightage" class="noborder weightage" style="width: 100px;">
						<%for(int i=0;i<=10;i++){%>
							<option value="<%=i%>"><%=i%></option>
						<%}%>
					</select>
				</td>
				<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="20"/></td>
  	
			</tr>
	</table>
</html>