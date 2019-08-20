<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.*"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData" %>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.CommandCode" %>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusHSSAuthDriverForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData" %>

<%
	String basePath = request.getContextPath();
	List dbfeildMapList = (List)session.getAttribute("dbfeildMapList");
	CreateRadiusHSSAuthDriverForm createRadiusHSSAuthDriverForm = (CreateRadiusHSSAuthDriverForm) request.getAttribute("createRadiusHSSAuthDriverForm");
	List<TranslationMappingConfData> translationMappingList=createRadiusHSSAuthDriverForm.getTranslationMappingList();
	List<DiameterPeerData> peerList=createRadiusHSSAuthDriverForm.getDiameterPeerDatas();
	
%>

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
 		if(this.action != "create" && !validateName()){
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
	 }
 }
 	
 	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 	
	
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

 	function splitDbFields( val ) {
 		return val.split( /[,;]\s*/ );
 	}

 	function extractLastDbFields( term ) {
 		return splitDbFields( term ).pop();
 	}
 	
 	var myArray = new Array();
 	function retriveRadiusDictionaryAttributesForText() {
 		
 		var dbFieldStr;
 		var dbFieldArray;
 		var searchNameOrAttributeId = "";
 		$.post("SearchRadiusAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
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
 		retriveRadiusDictionaryAttributesForText();
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
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/createRadiusHSSAuthDriver">
	<html:hidden name="createRadiusHSSAuthDriverForm" property="action" value="create" />
	<html:hidden name="createRadiusHSSAuthDriverForm" property="driverInstanceName" />
	<html:hidden name="createRadiusHSSAuthDriverForm" property="driverDesp" />
	<html:hidden name="createRadiusHSSAuthDriverForm" property="driverRelatedId" />

	<html:hidden property="itemIndex" />

	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							 <table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header"><bean:message
											bundle="driverResources" key="driver.createradiushssauthDriver" />
									</td>
								</tr>
								<tr>
									<td>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="tblheader-bold" valign="top" colspan="6">
													<bean:message bundle="driverResources" key="driver.radiushssdriver" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.hssdriver.applicationid" /> 
														<ec:elitehelp headerBundle="driverResources" 
															text="driver.hssdriver.applicationid" header="driver.hssdriver.applicationid"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													 <html:text styleId="applicationid" name="createRadiusHSSAuthDriverForm"  property="applicationid"  style="width:250px" tabindex="1" />
													 <font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.hssdriver.commandcode" /> 
														<ec:elitehelp headerBundle="driverResources" 
															text="driver.hssdriver.commandcode" header="driver.hssdriver.commandcode"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													 <html:text property="commandCode" name="createRadiusHSSAuthDriverForm" styleId="commandCode" style="width:250px" tabindex="2" styleClass="commandCode"></html:text>
													 <font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.hssdriver.requesttimeout" />
														<ec:elitehelp headerBundle="driverResources" 
															text="driver.hssdriver.requesttimeout" header="driver.hssdriver.requesttimeout"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													 <html:text styleId="requesttimeout" name="createRadiusHSSAuthDriverForm"  property="requesttimeout"  style="width:250px" tabindex="3" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="27%">
													<bean:message bundle="driverResources" key="driver.hssdriver.useridentityattribute" /> 
														<ec:elitehelp headerBundle="driverResources" text="driver.hssdriver.useridentityattribute" 
															header="driver.hssdriver.useridentityattribute"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="userIdentityAttributes" property="userIdentityAttributes" name="createRadiusHSSAuthDriverForm"  style="width:250px" tabindex="4" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="27%">
													<bean:message bundle="driverResources" key="driver.hhsdriver.nooftriplets" />
													<ec:elitehelp headerBundle="driverResources" 
													text="driver.hssdriver.nooftriplets" header="driver.hhsdriver.nooftriplets"/> 
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:text styleId="noOfTriplets" property="noOfTriplets" name="createRadiusHSSAuthDriverForm"  style="width:250px" tabindex="5" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="27%">
													<bean:message bundle="driverResources" key="driver.hhsdriver.additionalattributes" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="driver.hhsdriver.additionalattributes" header="driver.hhsdriver.additionalattributes"/>
												</td>
												<td align="left" class="labeltext" valign="top">
													<html:textarea styleId="additionalAttributes" name="createRadiusHSSAuthDriverForm" tabindex="6" property="additionalAttributes" cols="50" rows="2" style="width:250px"/>
												</td>
											</tr>
											<tr>
												<td width="20%" align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.hhspeerconfiguration" /> 
													<ec:elitehelp headerBundle="driverResources" 
													text="driver.hhspeerconfiguration" header="driver.hhspeerconfiguration"/>
												</td>
												<td  width="25%" align="left" class="labeltext" valign="top">
													<select class="labeltext" name="selectedPeerIds" id="selectedPeerIds" multiple="true" size="5" style="width: 250px;" tabindex="7">
													</select>												
												</td>
														
												<td align="left"  valign="top" style="padding-top: 10px;">
											   		<input type="button" value="Add " onClick="addPeers()"  tabindex="8"
											   		class="light-btn"  style="width: 75px"/><br/>
											   		<br/>
											   		<input type="button" value="Remove "  onclick="removePeers()" class="light-btn" style="width: 75px" tabindex="9"/>
												</td>
											</tr>
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													colspan="3"><bean:message bundle="driverResources"
														key="driver.radiushhsfieldmap" /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													colspan="2"><input type="button" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);'
													value=" Add " class="light-btn" style="size: 140px"
													tabindex="10"></td>
											</tr>

											<tr>
												<td align="left" width="100%" class="captiontext"
													colspan="3" valign="top">
													<table width="97%" id="mappingtbl" cellpadding="0"
														cellspacing="0" border="0">
														<tr>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.logicalname" />
																	<ec:elitehelp headerBundle="driverResources"
																		text="dbauthdriver.logicalname" header="driver.logicalname"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.hssdriver.attributeids" />
																	<ec:elitehelp headerBundle="driverResources" 
																		text="dbauthdriver.dbattribute" header="driver.hssdriver.attributeids"/>
															</td>
															<td align="left" class="tblheader" valign="top" width="15%">
																<bean:message bundle="driverResources" key="driver.defaultvalue" /> 
																	<ec:elitehelp headerBundle="driverResources" 
																		text="dbauthdriver.defaultvalue" header="driver.defaultvalue"/>
															</td>
															<td align="left" class="tblheader" valign="top">
																<bean:message bundle="driverResources" key="driver.valuemapping" />
																<ec:elitehelp headerBundle="driverResources" 
																text="dbauthdriver.valuemapping" header="driver.valuemapping"/>
															</td>
															<td align="left" class="tblheader" valign="top"width="5%">Remove</td>
														</tr>
														<logic:iterate id="obj" name="defaultMapping" type="com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverFieldMapData">
														<tr>
															<td class="allborder">
																<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
																	<option value='<bean:write name="obj" property="nameValuePoolData.value"/>'><bean:write name="obj" property="nameValuePoolData.name"/> </option>
																</select>
															</td>
															<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  maxlength="1000"  size="28" style="width:100%" tabindex="11" value='<bean:write name="obj" property="attributeIds"/>' onfocus="setAutoCompleteData(this);"/></td>
															<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="12"/></td>
															<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="13"/></td>
															<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="14"/></td>
														</tr>
														</logic:iterate>
													</table>
												</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" name="c_btnCreate" id="c_btnCreate2"
													value=" Create " class="light-btn" onclick="validateForm()"
													tabindex="15"> <input type="reset"
													name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
													value="Cancel" class="light-btn" tabindex="16"></td>
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
 <!-- Mapping Table Row template -->
	<table style="display:none;" id="dbMappingTable">
		<tr>
			<td class="allborder">
				<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="17" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
				</select>
			</td>
			<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="18" onfocus="setAutoCompleteData(this);"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="19"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="20"/></td>
			<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="21"/></td>
  		</tr>
	</table>
	 <div id="peerPopup" style="display: none;" title="Add Peers">
		<input type="button" onclick='addNewRowForPeer("peerMappingTable","addPeers","peerId",true);' value=" Add " class="light-btn" style="size: 140px" tabindex="22">
		<table id="addPeers" name="addPeers" cellpadding="0" cellspacing="0" width="100%" class="box"> 	
			<tr>
				<td align="left" class="tblheader" valign="top" width="30%">
					<bean:message bundle="driverResources" key="driver.hssdriver.peername" />
						<ec:elitehelp headerBundle="driverResources" 
							text="driver.peername" header="driver.hssdriver.peername"/>
				</td>
				<td align="left" class="tblheader" valign="top" width="70%">
					 <bean:message bundle="driverResources" key="driver.hssdriver.weightage"/>
					 	<ec:elitehelp headerBundle="driverResources" 
					 		text="driver.weightage" header="driver.hssdriver.weightage"/>
				</td>
				<td align="left" class="tblheader" valign="top">Remove</td>
			</tr>
			<tr>
				<td class="allborder">
					<select id="peerId" name="peerId" style="width: 200px;" class="noborder peerId" tabindex="23">
						<option value="0">--select--</option>
						<logic:iterate id="obj" name="peerList" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
							<option value="<bean:write name="obj" property="peerUUID"/>">
								<bean:write name="obj" property="name"/>
							</option>
						</logic:iterate>
					</select>
				</td>
				<td class="tblrows">
					<select id="weightage" name="weightage" class="noborder weightage" style="width: 100px;" tabindex="24">
						<%for(int i=0;i<=10;i++){%>
							<option value="<%=i%>"><%=i%></option>
						<%}%>
					</select>
				</td>
				<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="25"/></td>
  	
			</tr>
		</table>
	</div>	
	<!-- Mapping Table Row template -->
	<table style="display:none;" id="peerMappingTable">
		<tr>
				<td class="allborder">
					<select id="peerId" name="peerId" style="width: 200px;" class="noborder peerId" tabindex="26">
						<option value="0">--select--</option>
						<logic:iterate id="obj" name="peerList" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
							<option value="<bean:write name="obj" property="peerUUID"/>">
								<bean:write name="obj" property="name"/>
							</option>
						</logic:iterate>
					</select>
				</td>
				<td class="tblrows">
					<select id="weightage" name="weightage" class="noborder weightage" style="width: 100px;" tabindex="27">
						<%for(int i=0;i<=10;i++){%>
							<option value="<%=i%>"><%=i%></option>
						<%}%>
					</select>
				</td>
				<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="28"/></td>
  	
			</tr>
	</table>
	<script>
		setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 
	</script>