<%@page import="com.elitecore.elitesm.web.servermgr.transmapconf.forms.CreateTranslationMappingConfigForm"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.servermgr.transmapconf.forms.CreateCrestelRatingTransMapConfigForm"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
	CreateTranslationMappingConfigForm translationMappingConfigForm = (CreateTranslationMappingConfigForm) request
			.getSession().getAttribute("translationMappingConfigForm");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script language="javascript">
 	var dummyParamMainArray = new Array();
 	var dummyParamCount=0;
 	var mappingIndex=0;
 	var flag = false;
	function validateForm(){	
		var translationFrom ="<%=translationMappingConfigForm.getSelectedFromTranslatorTypeData().getName()%>" ;
		if(isValidMapping()) {
			if(!isValidInMessage()) {
				return false;
			} 
		} else {
			return false;
		}
		
		var dummyResponse = 'false';
		
		if(dummyResponse.value  ==  'false'){
		
		 	if(translationFrom.toLowerCase() == "diameter") {
				var isDefaultMapping = false;
				if (mappingIndex == 0)
					isDefaultMapping = true;
					
		 		for(var i=1;i<=mappingIndex;i++) {
		 				flag = false;
			 			var checkBox = document.getElementById("defaultMappingChkBox"+i);
			 			
			 			if(checkBox != null && !checkBox.checked) {
							var mappingExpression = document.getElementsByName("mappingExpression"+"responseTranslationMapTable"+i);
							if(mappingExpression != null && mappingExpression.length > 0) {
			 					for(var j=0;j<mappingExpression.length;j++) {
				 					var resultCodeValue = mappingExpression[j].value;
									if(resultCodeValue !=null && resultCodeValue.startsWith('0:268')) {
										flag = true;
									}
								}
							}
					 		if(!flag){
								alert("Enter Mapping Experssioin for Result Code[0:268]  in Response parameters for Mapping-"+mappingIndex);
								return false;
							}	
									
			 			}else{
			 				isDefaultMapping = true;
			 			}
		 		}
		 		if(isDefaultMapping){
		 				mappingExpression = document.getElementsByName("mappingExpression"+"responseTranslationMapTable0");
						if(mappingExpression != null && mappingExpression.length > 0) {
		 					for(var j=0;j<mappingExpression.length;j++) {
		 						resultCodeValue = mappingExpression[j].value;
			 					if(resultCodeValue != null && resultCodeValue.startsWith('0:268')) {
									flag = true;
								}
		 					}
						}
						if(!flag){
							alert("Enter Mapping Experssioin for Result Code[0:268] in Response parameters for Default Mapping");
							return false;
						}	
		 		}
		 	}
		}
		addMappingIndex();
	 	document.forms[0].submit();
	}
	
	String.prototype.startsWith = function(str){
		return (this.match("^"+str)==str);
	};
	
	function isValidMapping() {
		var isValidMapping = true;
		/* validate mapping name */
		$(".mappingNameClass").each(function(){
			var nameValue = $.trim($(this).val());
			if(nameValue.length == 0) {
				alert("Mapping Name must be Specified");
				isValidMapping = false;
				$(this).focus();
				return false;
	}
		});
		if(!isValidMapping){
			return false;
		}
		/* validate unique name */
		if(!isUnique("mappingNameClass")) {
			alert("Mapping Name should be unique for each mapping");
			return false;
		}
		
		return isValidMapping;
	}
	
	/*Validate In message */
	function isValidInMessage() {
		var isValidInMessage = true;
		$(".mappingInMessage").each(function() {
			var nameValue = $.trim($(this).val());
			if (nameValue.length == 0) {
				alert("In Message must be Specified");
				isValidInMessage = false;
				$(this).focus();
				return false;
			}
		});

		if (!isValidInMessage) {
			return false;
		}
		/* validate unique name */
		if (!isUnique("mappingInMessage")) {
			alert("In Message should be unique for each mapping");
			return false;
		}
		
		return isValidInMessage;
	}
	
	function isUnique(className) {
		$class = $("."+className);
		var arrayData = new Array($class.length);
		var arrayIndex = 0;
		
		$class.each(function(){
			var nameValue = $.trim($(this).val());
			arrayData[arrayIndex++] = nameValue;
			$(this).val(nameValue);
		});
		
		for(var i=0; i<arrayData.length; i++ ) {
			for(var j=i+1; j<arrayData.length; j++ ){
				if(arrayData[i] == arrayData[j]) {
					$class[i].focus();
					return false;
				}
			}
		}
		return true;
	}
	
	function addMappingIndex(){
		$("#mainform").append("<input type='hidden' name='mappingIndex' value='"+mappingIndex+"' />");
	}
	
	function popupDummyParameters() {	
		$('#dummyParameterMessage').text("");
		$.fx.speeds._default = 1000;
		document.getElementById("divDummyResponseParams").style.visibility = "visible";		
		$( "#divDummyResponseParams" ).dialog({
			modal: true,
			autoOpen: false,		
			height: 175,
			width: 500,		
			buttons:{					
                'Add': function() {

						var transRatingField = $('#ratingField').val();
						var transRatingValue = $('#ratingValue').val(); 
			      										          		
			      		if(isNull($('#ratingField').val())){
			      			$('#dummyParameterMessage').text("Out Field must be specified.");
			     		}else if(isNull($('#ratingValue').val())){
			     			$('#dummyParameterMessage').text("Value must be specified.");
			     		}else{	
			     			
			     			var i = 0;							
							var flag = true;												 	
							if(document.getElementById('tableDummyResponseParams').getElementsByTagName('tr').length >= 2){								
								for(i=0;i<dummyParamMainArray.length;i++){									
									var value = dummyParamMainArray[i];																		
									if(value == transRatingField){
										$('#dummyParameterMessage').text("Mapping with this value is already present");
										flag = false;
										break;
									}
								}
							}	
							if(flag){
				         		$("#tableDummyResponseParams tr:last").after("<tr><td class='tblfirstcol'>" + transRatingField + "<input type='hidden' name = 'transRatingField' value='" + transRatingField + "'/>" +"</td><td class='tblrows'>" + transRatingValue + "&nbsp" + "<input type='hidden' name = 'transRatingValue' value='" + transRatingValue + "'/>" +"</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");

				         		$('#tableDummyResponseParams td img.delete').live('click',function() {	
				         			 var removalVal = $(this).closest('tr').find('td:eq(0)').text(); 						 	 								
					     				for(var d=0;d<dummyParamCount;d++){					
					     					var currentVal = dummyParamMainArray[d];										
					     					if(currentVal == removalVal){
					     						dummyParamMainArray[d] = '  ';
					     						break;
					     					}
					     				}	
					   				 $(this).parent().parent().remove(); 
					   			});
	
				         		dummyParamMainArray[dummyParamCount] = transRatingField;				          		
			     				dummyParamCount = dummyParamCount + 1;
			     				$('#ratingField').val("");
			     				$('#ratingValue').val("");
			     				$(this).dialog('close');
							}
							
			         					         			         				    		         			   				         			          				          		
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
		$( "#divDummyResponseParams" ).dialog("open");
		
	}	
	
	function removeComponent(compId){
		$(compId).remove();
		setMappingLabeIndex();
	}
	
	
	function addType() {
		mappingIndex++;
		var translationTo = "<%=translationMappingConfigForm.getSelectedToTranslatorTypeData().getName()%>";
		var translationFrom ="<%=translationMappingConfigForm.getSelectedFromTranslatorTypeData().getName()%>" ;
		var translationToId ="<%=translationMappingConfigForm.getSelectedToTranslatorType()%>" ;
		var translationFromId ="<%=translationMappingConfigForm.getSelectedFromTranslatorType()%>" ;
		
		$.ajax({
			   type: "POST",
			   url: "<%=basePath%>/jsp/servermgr/transmapconf/MessageTypeConfig.jsp",
			   async:false,
			   data: {
				   	mappingIndexParam :mappingIndex,
				    translationTo : translationTo,
				   	translationFrom : translationFrom,
				   	translationToId :translationToId,
				   	translationFromId :translationFromId
			   },	
			   success : function(response){
				   	handleMappingResponse(response);
			   }
		 });
		
	}
		
	function handleMappingResponse(response){
		var div = document.getElementById("messageTypeDiv");
		$(div).append(response);
		var tableId = "table"+mappingIndex;
		var mappingNameId = "mappingName"+mappingIndex;
		document.getElementById(tableId).scrollIntoView();
		document.getElementById(mappingNameId).focus();
		setMappingLabeIndex();
	}
	function setMappingLabeIndex(){
		$(".mappingLabel").each(function(index, item){
			var mapIndex = index+1;
			$(this).text("Mapping-"+mapIndex);
		});
	}

	function addNewMapping(tableId){
		var checkExpressionId = "checkExpression"+tableId;
		var mappingExpressionId = "mappingExpression"+tableId;
		var defaultValueId = "defaultValue"+tableId;
		var valueMappingId = "valueMapping"+tableId;
		$("#"+tableId+" tr:last").after("<tr>"+
										"<td class='tblfirstcol'><input name="+checkExpressionId+" type='text' value='*' style='width: 100%;' /></td>"+
										"<td class='tblrows'> <input name="+mappingExpressionId+" type='text' value='' style='width: 100%;' /> </td>"+
										"<td class='tblrows'> <input name="+defaultValueId+" type='text' value='' style='width: 100%;' /> </td>"+
										"<td class='tblrows'> <input name="+valueMappingId+" type='text' value='' style='width: 100%;' /> </td>"+
										"<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>"+
										"</tr>");
	}
	$(document).ready(function(){
		var checkBoxes = document.getElementsByName("selectedDefaultMapping");
		for(var i=0;i<checkBoxes.length;i++){
			checkBoxes[i].checked=true;
		}
	});

	function setDefaultMapping(componentIndex){
		var checkBox = document.getElementById("defaultMappingChkBox"+componentIndex);
	
		if(!checkBox.checked){
			$("#div"+componentIndex).show();//("fast"); 
		}else{
			$("#div"+componentIndex).hide();//slideToggle("fast");	
		}
	}
	
	function toggleMappingDiv(mappingDivIndex){
		  var imgElement = document.getElementById("toggleImageElement"+mappingDivIndex);
		  if ($("#toggleDivElement"+mappingDivIndex).is(':hidden')) {
	            imgElement.src="<%=request.getContextPath()%>/images/top-level.jpg";
	       } else {
	            imgElement.src="<%=request.getContextPath()%>/images/bottom-level.jpg";
	       }
	      $("#toggleDivElement"+mappingDivIndex).slideToggle("fast");
	}
	
	function toggleDummyParameters(){
		  var imgElement = document.getElementById("toggleDummyParamImageElement");
		  if ($("#toggleDummyParamDivElement").is(':hidden')) {
	            imgElement.src="<%=request.getContextPath()%>/images/top-level.jpg";
	       } else {
	            imgElement.src = "<%=request.getContextPath()%>/images/bottom-level.jpg";
		}
		$("#toggleDummyParamDivElement").slideToggle("fast");
	}
	setTitle('<bean:message bundle="servermgrResources" key="translationmapconf.title"/>');
	</script>
<%
	CreateCrestelRatingTransMapConfigForm createCrestelRatingTransMapConfigForm = (CreateCrestelRatingTransMapConfigForm) request
			.getAttribute("createCrestelRatingTransMapConfigForm");
%>


<html:form action="/createCrestelRatingTransMapConfig"
	styleId="mainform">

	<html:hidden name="createCrestelRatingTransMapConfigForm"
		property="action" value="save" />

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
											bundle="servermgrResources" key="translationmapconf.create" /></td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="4">&nbsp;</td>
								</tr>

								<tr>
									<td>
										<table>
											<tr>
												<td align="left" class="captiontext" valign="top"><bean:message
														bundle="servermgrResources"
														key="translationmapconf.basetranslationmapping" /></td>
												<td align="left" class="labeltext" valign="top"><html:select
														property="baseTranslateConfigId"
														name="createCrestelRatingTransMapConfigForm" tabindex="1">
														<html:option value="0">--select--</html:option>
														<html:optionsCollection
															name="translationMappingConfigForm"
															property="baseTranslationMappingConfDataList"
															label="name" value="translationMapConfigId" />
													</html:select></td>
											</tr>
										</table>
									</td>
								<tr>
									<td colspan="4">

										<table width="100%" name="c_tblCrossProductList"
											id="c_tblCrossProductList" align="right" border="0">
											<tr>
												<td class="captiontext"><input type="button"
													onclick="addType()" value="Add Mapping" class="light-btn"
													tabindex="2" /></td>
											</tr>

											<tr>
												<td align="right">
													<div id="messageTypeDiv"></div>
												</td>
											</tr>
										</table>

									</td>
								<tr>
									<td colspan="4">

										<table cellpadding="0" cellspacing="0" width="98%"
											id="c_tblCrossProductList" align="right" border="0">
											<tr>
												<td align=left class=tblheader-bold valign=top colspan=2>
													Default Mapping</td>
												<td class="tblheader-bold" align="right" width="15px"><img
													alt="bottom" id="toggleImageElement0"
													onclick="toggleMappingDiv('0')"
													src="<%=request.getContextPath()%>/images/top-level.jpg" /></td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="4">

										<div id="toggleDivElement0">

											<table width="97%" id="c_tblCrossProductList" align="right"
												border="0">


												<tr>
													<td class="labeltext">&nbsp;</td>
												</tr>

												<tr>
													<td align="right" class="labeltext" valign="top"
														colspan="4">
														<table cellpadding="0" cellspacing="0" class="box"
															width="95%">
															<tr>
																<td class="table-header"><bean:message
																		bundle="servermgrResources"
																		key="translationmapconf.reqparams" /></td>
															</tr>
															<tr>
																<td class="labeltext">&nbsp;</td>
															</tr>
															<tr>
																<td class="labeltext"><input type="button"
																	class="light-btn"
																	onclick="addNewMapping('requestTranslationMapTable0')"
																	value="Add New Mapping" tabindex="3" /></td>
															</tr>
															<tr>
																<td class="labeltext">&nbsp;</td>
															</tr>
															<tr>
																<td class="labeltext">
																	<table id="requestTranslationMapTable0" cellpadding="0"
																		cellspacing="0" border="0" width="100%">
																		<tr>
																			<td align=left class=tblheader valign=top>
																				<bean:message
																					bundle="servermgrResources"
																						key="translationmapconf.checkexpression" /> 
																							<ec:elitehelp headerBundle="servermgrResources" 
																								text="transmapping.checkexp" 
																									header="translationmapconf.checkexpression"/>
																			</td>
																			<td align=left class=tblheader valign=top>
																				<bean:message bundle="servermgrResources"
																					key="translationmapconf.mappingexression" /> 
																						<ec:elitehelp headerBundle="servermgrResources"
																							text="transmapping.mappingexp" 
																								header="translationmapconf.mappingexression"/>
																			</td>
																			<td align=left class=tblheader valign=top>
																				<bean:message bundle="servermgrResources"
																					key="translationmapconf.defaultvalue" />
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="transmapping.defaultvalue" 
																								header="translationmapconf.defaultvalue"/>
																			</td>
																			<td align=left class=tblheader valign=top>
																				<bean:message bundle="servermgrResources"
																					key="translationmapconf.valuemapping" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="transmapping.valuemapping" 
																								header="translationmapconf.valuemapping"/>
																			</td>
																			<td align="left" class="tblheader" valign="top"
																				width="5%">Remove</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr>
																<td class="labeltext">&nbsp;</td>
															</tr>
														</table>
													</td>
												</tr>

												<tr>
													<td class="labeltext">&nbsp;</td>
												</tr>
												<tr>
													<td colspan="4" align="right">
														<table cellpadding="0" cellspacing="0" class="box"
															width="95%">
															<tr>
																<td class="table-header"><bean:message
																		bundle="servermgrResources"
																		key="translationmapconf.respparams" /></td>
															</tr>
															<tr>
																<td class="labeltext">&nbsp;</td>
															</tr>
															<tr>
																<td class="labeltext"><input type="button"
																	class="light-btn"
																	onclick="addNewMapping('responseTranslationMapTable0')"
																	value="Add New Mapping" tabindex="4" /></td>
															</tr>
															<tr>
																<td class="labeltext">&nbsp;</td>
															</tr>
															<tr>
																<td class="labeltext">
																	<table id="responseTranslationMapTable0"
																		cellpadding="0" cellspacing="0" border="0"
																		width="100%">
																		<tr>
																			<td align=left class=tblheader valign=top>
																				<bean:message bundle="servermgrResources"
																					key="translationmapconf.checkexpression" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="transmapping.checkexp" 
																								header="translationmapconf.checkexpression"/>
																			</td>
																			<td align=left class=tblheader valign=top>
																				<bean:message bundle="servermgrResources"
																					key="translationmapconf.mappingexression" /> 
																						<ec:elitehelp headerBundle="servermgrResources"
																							text="transmapping.mappingexp" 
																								header="translationmapconf.mappingexression"/>
																			</td>
																			<td align=left class=tblheader valign=top>
																				<bean:message bundle="servermgrResources"
																					key="translationmapconf.defaultvalue" />
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="transmapping.defaultvalue" 
																								header="translationmapconf.defaultvalue"/>
																			</td>
																			<td align=left class=tblheader valign=top>
																				<bean:message bundle="servermgrResources"
																					key="translationmapconf.valuemapping" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="transmapping.valuemapping" 
																								header="translationmapconf.valuemapping"/>
																			</td>
																			<td align="left" class="tblheader" valign="top"
																				width="5%">Remove</td>
																		</tr>
																	</table>
																</td>
															</tr>

															<tr>
																<td class="labeltext">&nbsp;</td>
															</tr>
														</table>

													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr>
								<tr>
									<td class="labeltext">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="4">

										<table cellpadding="0" cellspacing="0" width="97%"
											id="c_tblCrossProductList" align="right" border="0">
											<tr>
												<td align=left class=tblheader-bold valign=top colspan=2><bean:message
														bundle="servermgrResources"
														key="translationmapconf.dummyresponseparams" /></td>
												<td class="tblheader-bold" align="right" width="15px"><img
													alt="bottom" id="toggleDummyParamImageElement"
													onclick="toggleDummyParameters()"
													src="<%=request.getContextPath()%>/images/bottom-level.jpg" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="4">
										<div id="toggleDummyParamDivElement" style="display: none;">
											<table width="97%" id="c_tblCrossProductList" align="right"
												border="0">
												<tr>
													<td colspan="4" align="right">
														<table cellpadding="0" cellspacing="0" border="0"
															width="95%">
															<tr>
																<td class="labeltext" colspan="4"><input
																	type="button" class="light-btn"
																	onclick="popupDummyParameters()"
																	value="Add New Mapping" /></td>
															</tr>

															<tr>
																<td class="labeltext" colspan="4">
																	<table id="tableDummyResponseParams" cellpadding="0"
																		cellspacing="0" border="0" width="70%">
																		<tr>
																			<td align="left" class="tblheader" valign="top">
																				<bean:message bundle="servermgrResources"
																					key="translationmapconf.outfield" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="transmapping.outfield" 
																								header="translationmapconf.outfield"/>
																			</td>
																			<td align="left" class="tblheader" valign="top">
																				<bean:message bundle="servermgrResources"
																					key="translationmapconf.value" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="transmapping.value" 
																								header="translationmapconf.value"/>
																			</td>
																			<td align="left" class="tblheader" valign="top"
																				width="5%">Remove</td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>

													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>

								<tr>
									<td class="btns-td" valign="left" colspan="4"
										style="padding-left: 35"><input tabindex="5"
										type="button" value="Previous " onclick="history.go(-1)"
										class="light-btn" /> <input type="button" name="c_btnCreate"
										id="c_btnCreate2" value="Create" class="light-btn"
										tabindex="6" onclick="validateForm()"> <input
										type="reset" name="c_btnDeletePolicy"
										onclick="javascript:window.location.href='<%=basePath%>/initSearchTranslationMappingConfig.do?'"
										value="Cancel" class="light-btn" tabindex="7" /></td>
								</tr>
							</table>

						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
	<div id="divDummyResponseParams" style="display: none;"
		title="Add Dummy Response Parameters">
		<table width="100%" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" class="labeltext" colspan="2"><label
					id="dummyParameterMessage" style="color: red;"> </label></td>
			</tr>
			<tr>
				<td align="left" class="labeltext"><bean:message
						bundle="servermgrResources" key="translationmapconf.outfield" /></td>
				<td align="left" class="labeltext" valign="top" width="75%"><input
					tabindex="8" type="text" id="ratingField" size="40" /><font
					color="#FF0000">*</font></td>
			</tr>
			<tr>
				<td align="left" class="labeltext"><bean:message
						bundle="servermgrResources" key="translationmapconf.value" /></td>
				<td align="left" class="labeltext" valign="top"><input
					tabindex="9" type="text" id="ratingValue" size="40" /><font
					color="#FF0000">*</font></td>

			</tr>
		</table>
	</div>

</html:form>




