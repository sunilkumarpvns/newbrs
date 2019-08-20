<%@page import="java.net.URLEncoder"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.DummyResponseParameterData"%>
<%@page import="com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstDetailData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData"%>
<%@page import="com.elitecore.elitesm.web.servermgr.transmapconf.forms.UpdateCrestelRatingTransMapConfigForm"%>
<%@ page import="com.elitecore.elitesm.util.EliteStringEscapeUtils"%>
<%@ page import="com.elitecore.commons.base.Collectionz"%>
<%@ page import="java.util.List"%>

<%
UpdateCrestelRatingTransMapConfigForm updateCrestelRatingTransMapConfigForm = (UpdateCrestelRatingTransMapConfigForm) request.getAttribute("updateCrestelRatingTransMapConfigForm");
TranslationMappingConfData translationMappingConfData = updateCrestelRatingTransMapConfigForm.getTranslationMappingConfData();
%>
<script language="javascript">
 	var dummyParamMainArray = new Array();
 	var dummyParamCount=0;
 	var mappingIndex=0;
 	var flag = false;
	function validateForm(){
		var translationFrom ="<%=translationMappingConfData.getTranslatorTypeFrom().getName()%>" ;
		var dummyResponse = 'false';//document.getElementById("dummyResponse");
		
		if(isValidMapping()) {
			if(!isValidInMessage()) {
				return false;
			} 
		} else {
			return false;
		}
		
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
		 					resultCodeValue = mappingExpression[0].value;
		 					if(resultCodeValue != null && resultCodeValue.startsWith('0:268')) {
								flag = true;
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
			      			$('#dummyParameterMessage').text("Rating Field must be specified.");
			     		}else if(isNull($('#ratingValue').val())){
			     			$('#dummyParameterMessage').text("Rating Value must be specified.");
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
		var myArray = new Array();
		var translationTo = "<%=translationMappingConfData.getTranslatorTypeTo().getName()%>";
		var translationFrom = "<%=translationMappingConfData.getTranslatorTypeFrom().getName()%>";
		var translationToId ="<%=translationMappingConfData.getTranslatorTypeTo().getTranslatorTypeId()%>" ;
		var translationFromId ="<%=translationMappingConfData.getTranslatorTypeFrom().getTranslatorTypeId()%>" ;
		
		var mapIndex = mappingIndex;
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

	function addUpdatedMappingInstance(mappingName ,inMessage,outMessage,defaultMapping,mappingInstanceId,dummyResponse) {
		mappingIndex++;
		var myArray = new Array();
		var translationTo = "<%=translationMappingConfData.getTranslatorTypeTo().getName()%>";
		var translationFrom = "<%=translationMappingConfData.getTranslatorTypeFrom().getName()%>" ;
		var translationToId ="<%=translationMappingConfData.getTranslatorTypeTo().getTranslatorTypeId()%>" ;
		var translationFromId ="<%=translationMappingConfData.getTranslatorTypeFrom().getTranslatorTypeId()%>" ;
		var mapIndex = mappingIndex;
		$.ajax({
			   type: "POST",
			   url: "<%=basePath%>/jsp/servermgr/transmapconf/MessageTypeConfig.jsp",
			   async:false,
			   data:"mappingIndexParam="+mappingIndex+"&mappingName="+mappingName+"&inMessage="+inMessage+"&outMessage="+outMessage+"&defaultMapping="+defaultMapping+"&translationTo="+translationTo+"&translationFrom="+translationFrom+"&translationToId="+translationToId+"&translationFromId="+translationFromId+"&dummyResponse="+dummyResponse, 
			   success : function(response){
				  $("#messageTypeDiv").append(response);
			   }
		});
		
		
		<% for(TranslationMappingInstData translationMappingInstData: translationMappingConfData.getTranslationMappingInstDataList()){%>
		
			if(mappingInstanceId == '<%=translationMappingInstData.getMappingInstanceId()%>'){
				var requestTableId ;
				var responseTableId;
		
		<%	if (Collectionz.isNullOrEmpty(translationMappingInstData.getTranslationMappingInstDetailDataList()) == false) {%>
		
					var checkExpression;
					var defaultValue;
					var valueMapping;
					var mappingExpression;
		
			<%for(TranslationMappingInstDetailData detailData: translationMappingInstData.getTranslationMappingInstDetailDataList()){%>
			
					  checkExpression = '';
					  defaultValue = '';
					  valueMapping = '';
					  mappingExpression = '';

						
					<%if(detailData.getCheckExpression()!=null){%>
						checkExpression = "<%=EliteStringEscapeUtils.escapeHtml(detailData.getCheckExpression())%>";
					<%}%>
					
					<%if(detailData.getMappingExpression()!=null){%>
						mappingExpression = "<%=EliteStringEscapeUtils.escapeJavaScript(detailData.getMappingExpression())%>" ;
					<%}%>
					
					<%if(detailData.getDefaultValue()!=null){%>
					defaultValue = '<%=EliteStringEscapeUtils.escapeHtml(detailData.getDefaultValue())%>';
					<%}%>
					
					<%if(detailData.getValueMapping()!=null){%>
					valueMapping  = '<%=EliteStringEscapeUtils.escapeHtml(detailData.getValueMapping())%>'; 
					<%}%>
				
				<%if(detailData.getMappingTypeId().equals(TranslationMappingConfigConstants.REQUEST_PARAMETERS)){%>
					 requestTableId ='requestTranslationMapTable'+mapIndex;
			         addUpdatedMapInstDetail(requestTableId, checkExpression,mappingExpression,defaultValue,valueMapping);
				
				<%}else{ %>
				 	responseTableId ='responseTranslationMapTable'+mapIndex;
					addUpdatedMapInstDetail(responseTableId,checkExpression,mappingExpression,defaultValue,valueMapping);
				
				<%}%>
				
			<%}%>
			<%}%>
			}
		<%}%>
	
		
	}
	
	function addNewMapping(tableId){
		var checkExpressionId = "checkExpression"+tableId;
		var mappingExpressionId = "mappingExpression"+tableId;
		var defaultValueId = "defaultValue"+tableId;
		var valueMappingId = "valueMapping"+tableId;
		$("#"+tableId+" tr:last").after("<tr>"+
										"<td class='tblfirstcol'><input name="+checkExpressionId+" type='text' value='*' style='width: 100%;'/></td>"+
										"<td class='tblrows'> <input name="+mappingExpressionId+" type='text' value='' style='width: 100%;'/> </td>"+
										"<td class='tblrows'> <input name="+defaultValueId+" type='text' value='' style='width: 100%;'/> </td>"+
										"<td class='tblrows'> <input name="+valueMappingId+" type='text' value='' style='width: 100%;' /> </td>"+
										"<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>"+
										"</tr>");
	}
	function addUpdatedMapInstDetail(tableId, checkExpression,mappingExpression,defaultValue,valueMapping){
		var checkExpressionId = "checkExpression"+tableId;
		var mappingExpressionId = "mappingExpression"+tableId;
		var defaultValueId = "defaultValue"+tableId;
		var valueMappingId = "valueMapping"+tableId;
		$("#"+tableId+" tr:last").after("<tr>"+
										"<td class='tblfirstcol'><input name="+checkExpressionId+" type='text' value='"+checkExpression+"' style='width: 100%;' /></td>"+
										"<td class='tblrows'> <input name="+mappingExpressionId+" type='text' value='"+mappingExpression+"' style='width: 100%;' /> </td>"+
										"<td class='tblrows'> <input name="+defaultValueId+" type='text' value='"+defaultValue+"' style='width: 100%;' /> </td>"+
										"<td class='tblrows'> <input name="+valueMappingId+" type='text' value='"+valueMapping+"' style='width: 100%;' /> </td>"+
										"<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();' height='15' /></td>"+
										"</tr>");
	}
	
	$(document).ready(function(){
			var checkBoxes = document.getElementsByName("selectedDefaultMapping");
			for(var i=0;i<checkBoxes.length;i++){
				checkBoxes[i].checked=true;
			}
			
			var mappingName;
			var inMessage;
			var outMessage;
			var defaultMapping;
			var mappingInstanceId;
			var dummyResponse;
		
		<% for(TranslationMappingInstData translationMappingInstData: translationMappingConfData.getTranslationMappingInstDataList()){%>
			
			mappingName = '';
			
			<% if (translationMappingInstData.getMappingName() != null) { %>
				mappingName = '<%=URLEncoder.encode(translationMappingInstData.getMappingName())%>';
			<% } %>
		
			inMessage='';
			<%if(translationMappingInstData.getInMessage()!=null){%>
			inMessage ='<%=URLEncoder.encode(translationMappingInstData.getInMessage())%>';
			<%}%>
			
			
			outMessage='';
			<%if(translationMappingInstData.getOutMessage()!=null){%>
			outMessage ='<%=URLEncoder.encode(translationMappingInstData.getOutMessage())%>';
			<%}%>
			
			
			defaultMapping = '<%=translationMappingInstData.getDefaultMapping()%>';
			mappingInstanceId ='<%=translationMappingInstData.getMappingInstanceId()%>';
			dummyResponse = '<%=translationMappingInstData.getDummyResponse()%>';
			addUpdatedMappingInstance(mappingName ,inMessage,outMessage,defaultMapping,mappingInstanceId,dummyResponse);
		<%}%>
		
			var checkExpression;
			var defaultValue;
			var valueMapping;
			var mappingExpression;
	
		<% for(TranslationMappingInstDetailData detailData: translationMappingConfData.getDefaultTranslationMappingDetailDataList()){%>
		
		
			  checkExpression = '';
			  defaultValue = '';
			  valueMapping = '';
			  mappingExpression = '';
				
					<%if(detailData.getCheckExpression()!=null){%>
						checkExpression ='<%=EliteStringEscapeUtils.escapeHtml(detailData.getCheckExpression())%>';
					<%}%>
					
					<%if(detailData.getMappingExpression()!=null){%>
						mappingExpression = "<%=EliteStringEscapeUtils.escapeJavaScript(detailData.getMappingExpression())%>";
					<%}%>
					
					<%if(detailData.getDefaultValue()!=null){%>
					defaultValue = '<%=EliteStringEscapeUtils.escapeHtml(detailData.getDefaultValue())%>';
					<%}%>
					
					<%if(detailData.getValueMapping()!=null){%>
					valueMapping  = '<%=EliteStringEscapeUtils.escapeHtml(detailData.getValueMapping())%>';
					<%}%>
			
			<%if(detailData.getMappingTypeId().equals(TranslationMappingConfigConstants.REQUEST_PARAMETERS)){%>
			addUpdatedMapInstDetail('requestTranslationMapTable0', checkExpression,mappingExpression,defaultValue,valueMapping);
			<%}else{ %>
			addUpdatedMapInstDetail('responseTranslationMapTable0',checkExpression,mappingExpression,defaultValue,valueMapping);
			<%}
		}%>
		
		getUpdatedDummyResponseParams();
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
	function getUpdatedDummyResponseParams(){
		
		<% for(DummyResponseParameterData parameterData: translationMappingConfData.getDummyResponseParameterDataList()){%>
		
		var transRatingField = '<%=parameterData.getOutField()%>';
		var transRatingValue = '<%=parameterData.getValue()%>';
		
		$("#tableDummyResponseParams tr:last").after("<tr>"+
				"<td class='tblfirstcol'>" + transRatingField + "<input type='hidden' name = 'transRatingField' value='" + transRatingField + "' style='width: 100%;'/>" +"</td>"+
				"<td class='tblrows'>" + transRatingValue + "&nbsp" + "<input type='hidden' name = 'transRatingValue' value='" + transRatingValue + "' style='width: 100%;'/>" +"</td>"+
				"<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
		
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
	
		<%}%>
		
		
		
	}
	function toggleDummyParameters(){
		  var imgElement = document.getElementById("toggleDummyParamImageElement");
		  if ($("#toggleDummyParamDivElement").is(':hidden')) {
	            imgElement.src="<%=request.getContextPath()%>/images/top-level.jpg";
	       } else {
	            imgElement.src="<%=request.getContextPath()%>/images/bottom-level.jpg";
	       }
	      $("#toggleDummyParamDivElement").slideToggle("fast");
	}
</script>



<html:form action="/updateCrestelRatingTransMapConfig"
	styleId="mainform" onsubmit="return validateForm();">

	<html:hidden name="updateCrestelRatingTransMapConfigForm"
		property="action" value="save" />
	<html:hidden name="UpdateCrestelRatingTransMapConfigForm"
		property="translationMapConfigId"
		value="<%=translationMappingConfData.getTranslationMapConfigId()%>" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="10" class="small-gap">&nbsp;</td>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td width="10">&nbsp;</td>
			<td width="100%" colspan="2" valign="top">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0">
					<tr>
						<td class="tblheader-bold" colspan="4"><bean:message
								bundle="servermgrResources" key="translationmapconf.update" /></td>
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
											name="updateCrestelRatingTransMapConfigForm" tabindex="1">
											<html:option value="0">--select--</html:option>
											<html:optionsCollection
												name="updateCrestelRatingTransMapConfigForm"
												property="baseTranslationMappingConfDataList" label="name"
												value="translationMapConfigId" />
										</html:select></td>
								</tr>
							</table>
						</td>
					<tr>
					<tr>
						<td colspan="4">
							<table width="100%" name="c_tblCrossProductList"
								id="c_tblCrossProductList" align="right" border="0"
								cellpadding="0" cellspacing="0">
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
					</tr>
					<tr>
						<td colspan="4">
							<table cellpadding="0" cellspacing="0" width="100%"
								id="c_tblCrossProductList" align="right" border="0">
								<tr>
									<td align=left class="tblheader-bold" valign=top colspan=2>
										<bean:message bundle="servermgrResources"
											key="translationmapconf.defaultmapping" />
									</td>
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
							<table cellpadding="0" cellspacing="0" width="100%">
								<tr>
									<td align="right" width="100%">
										<div id="toggleDivElement0">
											<table width="100%" id="c_tblCrossProductList" align="right"
												border="0">
												<tr>
													<td class="labeltext">&nbsp;</td>
												</tr>
												<tr>
													<td class=labeltext valign=top colspan='4' align=right>
														<table border="0" width="95%" class="box">
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
																	tabindex="3" class="light-btn"
																	onclick="addNewMapping('requestTranslationMapTable0')"
																	value="Add New Mapping" /></td>
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
																	tabindex="4" class="light-btn"
																	onclick="addNewMapping('responseTranslationMapTable0')"
																	value="Add New Mapping" /></td>
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
									<td class=labeltext>&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="4">

							<table cellpadding="0" cellspacing="0" width="100%"
								id="c_tblCrossProductList" align="right" border="0">
								<tr>
									<td align=left class=tblheader-bold valign=top colspan=2>
										<bean:message bundle="servermgrResources"
											key="translationmapconf.dummyresponseparams" />
									</td>
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
								<table width="100%" id="c_tblCrossProductList" align="right"
									border="0">
									<tr>
										<td class="labeltext">&nbsp;</td>
									</tr>

									<tr>
										<td colspan="4" align="right">
											<table cellpadding="0" cellspacing="0" border="0" width="95%">
												<tr>
													<td class="labeltext" colspan="4"><input type="button"
														tabindex="5" class="light-btn"
														onclick="popupDummyParameters()" value="Add New Mapping" />
													</td>
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
																<td align="left" class="tblheader" valign="top">Remove</td>
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
						<td class="btns-td" valign="middle" colspan="4"><input
							type="submit" tabindex="6" name="c_btnupdate" id="c_btnupdate2"
							value="Update" class="light-btn" tabindex="6"> <input
							type="reset" tabindex="7" name="c_btnDeletePolicy"
							onclick="javascript:window.location.href='<%=basePath%>/viewTranslationMappingConfigBasicDetail.do?translationMapConfigId=<%=translationMappingConfData.getTranslationMapConfigId()%>'"
							value="Cancel" class="light-btn" tabindex="7" /></td>
					</tr>
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
					type="text" tabindex="8" id="ratingField" size="40" /><font
					color="#FF0000">*</font></td>
			</tr>
			<tr>
				<td align="left" class="labeltext"><bean:message
						bundle="servermgrResources" key="translationmapconf.value" /></td>
				<td align="left" class="labeltext" valign="top"><input
					type="text" tabindex="9" id="ratingValue" size="40" /><font
					color="#FF0000">*</font></td>
			</tr>
		</table>
	</div>

</html:form>






