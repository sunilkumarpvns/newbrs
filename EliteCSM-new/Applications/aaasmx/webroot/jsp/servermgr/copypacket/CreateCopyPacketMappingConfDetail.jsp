<%@ page import="com.elitecore.diameterapi.diameter.translator.operations.PacketOperations"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.copypacket.forms.CreateCopyPacketTransMappingForm"%>
<%@ page import ="com.elitecore.diameterapi.diameter.translator.operations.PacketOperations" %>	
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ include file="/jsp/servermgr/copypacket/CopyPacketConfiguration.jsp" %>
<%@ page import="com.elitecore.elitesm.web.servermgr.copypacket.forms.CreateCopyPacketMappingConfDetailForm"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Copy Packet Mapping</title>

<script src="<%=request.getContextPath()%>/jquery/OrderTable/jquery.tablednd.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

<style>
.autoSuggest {
	font-family: Arial;
	
}
.validateDummyParamValue{
font-family : Arial;
}
.validateOutField{
font-family : Arial;
}
.tempCSS{
	font-family : Arial;
}


td.details-control {
	background: url('images/plus.jpg') no-repeat center center;
	cursor: pointer;
	width: 20px;
}

tr.shown td.details-control {
	View background: url('images/minus.jpg') no-repeat center center;
	width: 20px;
}

td.hoverClass{
background: url('images/navigate.png') no-repeat ;
background-position:12px 6px; 

}

 td.hoverClass:hover{
	
	background: url('images/DragNDrop.png') no-repeat ;
	background-position:12px 6px;
	cursor:pointer;
	
} 
</style>


<%
	String basePath = request.getContextPath();
	CreateCopyPacketTransMappingForm createCopyPacketConfigForm = (CreateCopyPacketTransMappingForm) request
			.getSession().getAttribute("copyPacketMappingConfForm");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript">
	var dummyResp = 0;
	var mappingIndex=0;
 	var translationFrom ="<%=createCopyPacketConfigForm.getSelectedFromTranslatorTypeData().getName()%>" ;
	$(document).ready(function(){
		
		
		if(translationFrom == 'Diameter'){
			retriveDiameterDictionaryDataAttributes();
		}else{
			retriveRadiusDictionaryAttributesForReferingAtrributes();
		}
	});
	function validateForm() {
		var isMappingName = true;
		var isInExpression = true;
		var isOutfield = true;
		var isValue = true;
 		$(".mappingNameClass").each(function(){
			var mappingName = $.trim($(this).val());
			if(mappingName.length == 0 || mappingName == null) {
				alert("Mapping Name must be Specified"); 
				isMappingName = false;
				$(this).focus();
				return false;
			}
 		});
 		
 		$(".mappingInMessage").each(function(){
			var inExpression = $.trim($(this).val());
			if(inExpression.length == 0 || inExpression == null) {
				alert("In Expression must be Specified");
				isInExpression = false;
				$(this).focus();
				return false;
		}
 		});
 		
 		
		if(!isMappingName){
				return false;
		}
		
		if (!isInExpression) {
			return false;
		}
		
 		$(".validateOutField").each(function(){
			var outFieldName = $.trim($(this).val());
			if(outFieldName.length == 0 || outFieldName == null) {
				alert("Out Field of Dummy Response Parameter must be specified");
				isOutfield = false;
				$(this).focus();
				return false;
			}
 		});
 	
 		$(".validateDummyParamValue").each(function(){
			var dummyParamVal = $.trim($(this).val());
			if(dummyParamVal.length == 0 || dummyParamVal == null) {
				alert("Value of Dummy Response Parameter must be specified");
				isValue = false;
				$(this).focus();
				return false;
			}
 		});
 		if(isValue == false || isOutfield == false){
 			return false;
 		}
 		
		var isDestinationExpr = true;
		var i = 0;
		var flag = false;
 		$(".defaultMappingCSS").each(function(){
 			i=i+1;
			if($(this).is(':checked') && flag == false) {
				$(".tempCSSdefaultRequestMapping0").each(function(){
					var destExpr = $.trim($(this).val());
					if(destExpr.length == 0 || destExpr == null) {
						alert("Destination Expression must be Specified Default Request Parameter");
						isDestinationExpr = false;
						$(this).focus();
						return false;
					}
				});
				$(".tempCSSdefaultResponseMapping0").each(function(){
					var destExpr = $.trim($(this).val());
					if(destExpr.length == 0 || destExpr == null) {
						alert("Destination Expression must be Specified Default Response Parameter");
						isDestinationExpr = false;
						$(this).focus();
						return false;
					}
				});
				flag = true;
				
			}else if($(this).is(':checked') == false ) {
				$(".tempCSSrequestMapping"+i).each(function(){
					var destExpr = $.trim($(this).val());
					if(destExpr.length == 0 || destExpr == null) {
						alert("Destination Expression must be Specified for Request Mapping - " + i);
						isDestinationExpr = false;
						$(this).focus();
						return false;
					}
				});
				$(".tempCSSresponseMapping"+i).each(function(){
					var destExpr = $.trim($(this).val());
					if(destExpr.length == 0 || destExpr == null) {
						alert("Destination Expression must be Specified for Response Mapping - " + i);
						isDestinationExpr = false;
						$(this).focus();
						return false;
					}
				});
				
				
			}
		});
 		
 	
 		var isMapping = true;
 		var elements = document.getElementsByClassName("mappingNameClass");

 		if(elements.length == 0){
 			alert("Mapping must be specified.");
 			isMapping = false;
 		}
		if(!isMapping){
			return false;
		}
   		
 		
 		if(!isDestinationExpr){
 			return false;
 		}
		
		if(!isValidMapping()){
			return false;
		}
		addMappingIndex();
	 	document.forms[0].submit();
   	}
	
	String.prototype.startsWith = function(str){
		return (this.match("^"+str)==str);
	};
	
	function isValidMapping() {
		var isValidMapping = true;
		if(!isUnique("mappingNameClass")) {
			alert("Mapping Name should be unique for each Copy Packet Transaltion Mapping");
			return false;
		}
		 if(!isUnique("mappingInMessage")) {
			alert("In Message should be unique for each Copy Packet Transaltion Mapping");
			return false;
		} 
		return isValidMapping;
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
	function removeComponent(compId){
		$(compId).remove();
		setMappingLabeIndex();
	}
	
/* Custom function for help tags (Only applicable for dynamic elements( For eg: in Copy Packet and Translation Mapping))*/
    
    var helpTagArray    = {
							'Operation':'<bean:message bundle="descriptionResources" key="copypacket.operation"/>',
							'checkExpression':'<bean:message bundle="descriptionResources" key="copypacket.checkexpression"/>',
                            'destinationExpression':'<bean:message bundle="descriptionResources" key="copypacket.destinationexpression"/>',
                            'sourceExpression':'<bean:message bundle="descriptionResources" key="copypacket.sourceexpression"/>',
                            'defaultValue':'<bean:message bundle="descriptionResources" key="copypacket.defaultvalue"/>',
                            'valueMapping':'<bean:message bundle="descriptionResources" key="copypacket.valuemapping"/>'
                          };
    
    var cautionTagArray = {
    						'Operation':'<bean:message bundle="descriptionCautionResources" key="copypacket.operation.caution"/>',
                            'checkExpression':'<bean:message bundle="descriptionCautionResources" key="copypacket.checkexpression.caution"/>',
                            'destinationExpression':'<bean:message bundle="descriptionCautionResources" key="copypacket.destinationexpression.caution"/>',
                            'sourceExpression':'<bean:message bundle="descriptionCautionResources" key="copypacket.sourceexpression.caution"/>',
                            'defaultValue':'<bean:message bundle="descriptionCautionResources" key="copypacket.defaultvalue.caution"/>',
                            'valueMapping':'<bean:message bundle="descriptionCautionResources" key="copypacket.valuemapping.caution"/>'
                          };
    
    var exampleTagArray = {
    						'Operation':'<bean:message bundle="descriptionExampleResources" key="copypacket.operation.example"/>',
                            'checkExpression':'<bean:message bundle="descriptionExampleResources" key="copypacket.checkexpression.example"/>',
                            'destinationExpression':'<bean:message bundle="descriptionExampleResources" key="copypacket.destinationexpression.example"/>',
                            'sourceExpression':'<bean:message bundle="descriptionExampleResources" key="copypacket.sourceexpression.example"/>',
                            'defaultValue':'<bean:message bundle="descriptionExampleResources" key="copypacket.defaultvalue.example"/>',
                            'valueMapping':'<bean:message bundle="descriptionExampleResources" key="copypacket.valuemapping.example"/>'
                          };
    
    var noteTagArray =    {
    						'Operation':'<bean:message bundle="descriptionNoteResources" key="copypacket.operation.note"/>',
    						'checkExpression':'<bean:message bundle="descriptionNoteResources" key="copypacket.checkexpression.note"/>',
                            'destinationExpression':'<bean:message bundle="descriptionNoteResources" key="copypacket.destinationexpression.note"/>',
                            'sourceExpression':'<bean:message bundle="descriptionNoteResources" key="copypacket.sourceexpression.note"/>',
                            'defaultValue':'<bean:message bundle="descriptionNoteResources" key="copypacket.defaultvalue.note"/>',
                            'valueMapping':'<bean:message bundle="descriptionNoteResources" key="copypacket.valuemapping.note"/>'
                          };
    
    var tipTagArray =     {    
    						'Operation':'<bean:message bundle="descriptionTipResources" key="copypacket.operation.tip"/>',
                            'checkExpression':'<bean:message bundle="descriptionTipResources" key="copypacket.checkexpression.tip"/>',
                            'destinationExpression':'<bean:message bundle="descriptionTipResources" key="copypacket.destinationexpression.tip"/>',
                            'sourceExpression':'<bean:message bundle="descriptionTipResources" key="copypacket.sourceexpression.tip"/>',
                            'defaultValue':'<bean:message bundle="descriptionTipResources" key="copypacket.defaultvalue.tip"/>',
                            'valueMapping':'<bean:message bundle="descriptionTipResources" key="copypacket.valuemapping.tip"/>'
                          };
console.log(helpTagArray);
    
    function openHelpDialog(caller){
    
            var attrIds = $(caller).attr('id');
            var description = '';
            var headerName = '';
            var tip = '';
            var note = '';
            var caution = '';
            var example = '';
            
            if(attrIds == 'Operation'){
                
          	  	headerName = 'Operation';  
          	  	description = helpTagArray.Operation;
                tip = tipTagArray.Operation;
                note = noteTagArray.Operation;
                caution = cautionTagArray.Operation;
                example = exampleTagArray.Operation;
            
            }else if(attrIds == 'checkExpression'){
                  
            	  headerName = 'Check Expression';  
            	  description = helpTagArray.checkExpression;
                  tip = tipTagArray.checkExpression;
                  note = noteTagArray.checkExpression;
                  caution = cautionTagArray.checkExpression;
                  example = exampleTagArray.checkExpression;
                  
            }else if(attrIds == 'destinationExpression'){
                  
            	  headerName = 'Destination Expression';  
            	  description=helpTagArray.destinationExpression;
                  tip = tipTagArray.destinationExpression;
                  note = noteTagArray.destinationExpression;
                  caution = cautionTagArray.destinationExpression;
                  example = exampleTagArray.destinationExpression;
                  
            }else if(attrIds == 'sourceExpression'){
                
            	  headerName = 'Source Expression';  
            	  description = helpTagArray.sourceExpression;
                  tip = tipTagArray.sourceExpression;
                  note = noteTagArray.sourceExpression;
                  caution = cautionTagArray.sourceExpression;
                  example = exampleTagArray.sourceExpression;
                  
            }else if(attrIds == 'defaultValue'){
                
            	  headerName = 'Default Value';  
            	  description = helpTagArray.defaultValue;
                  tip = tipTagArray.defaultValue;
                  note = noteTagArray.defaultValue;
                  caution = cautionTagArray.defaultValue;
                  example = exampleTagArray.defaultValue;
                  
            }else if(attrIds == 'valueMapping'){
                  
            	  headerName = 'Value Mapping';  
            	  description = helpTagArray.valueMapping;
                  tip = tipTagArray.valueMapping;
                  note = noteTagArray.valueMapping;
                  caution = cautionTagArray.valueMapping;
                  example = exampleTagArray.valueMapping;
                  
            }
    
            openHelp(caller,headerName,headerName,description,tip,note,caution,example);
    
    }

	function addNewMapping(tableId) {
		var currentOrderNumber=1;
		var orderNumArray = document.getElementsByName("orderNumber"+tableId);
		 if(orderNumArray!=null && orderNumArray.length>0){
			
			currentOrderNumber = orderNumArray.length + 1;
			
		} 
		var operation = "operation" + tableId;
		var checkExpression = "checkExpression" + tableId;
		var destinationExpression = "destinationExpression" + tableId;
		var sourceExpression = "sourceExpression" + tableId;
		var defaultValue = "defaultValue" + tableId;
		var valueMapping = "valueMapping" + tableId;
		var operationTemp = "operation" + "tempTable";
		var checkExpressionTemp = "checkExpression" + "tempTable";
		var destinationExpressionTemp = "destinationExpression" + "tempTable";
		var sourceExpressionTemp = "sourceExpression" + "tempTable";
		var defaultValueTemp = "defaultValue" + "tempTable";
		var valueMappingTemp = "valueMapping" + "tempTable";
		var orderNumber = "orderNumber"+tableId;
		var tableRow = "tableRow" + i;
		var operationId = "ddlOperation" + i;
		var checkExprId = "txtCheckExpr" + i;
		var destinationExprId = "txtDestinationExpr" + i;
		var sourceExprId = "txtSourceExpr" + i;
		var defaultValueId = "txtDefaultValue" + i;
		var valueMappingId = "txtValueMapping" + i;
		var imageId = "imgExpand" + i;
		var tempCSS = "tempCSS" + tableId;
		var autoSuggest = "autoSuggest";
		var tempRow = "tempRow" + i;
		var currentOrder = currentOrderNumber;	
		var btnCancel = "btnCancel" + i;
			
		var rowsdata = '<tr  align="left"  value='+currentOrderNumber+' id='+tableRow+'><td colspan="8" align="left"><table width="100%" border="0" cellspacing="0" cellpadding="0" ><tr><td><table width="100%" border="0" cellspacing="0" cellpadding="0" ><tr><td class="tblfirstcol" width="11%"  align="left"><select id="'+operationId+'" name="'+operation+'" style="min-width:99%;min-height:22px;width:99%;" tabindex="4">'
			+'<%for(PacketOperations packetOperationData : PacketOperations.values()){%>'
			+'<option value="<%=((PacketOperations)packetOperationData).operation%>"><%=((PacketOperations)packetOperationData).operation%></option>'
			+'<%} %>'
			+ '</select></td>'
			+ '<td class="tblrowdata" width="16%" align="left"><textarea name="'+checkExpression+'"  class="'+autoSuggest+'" id="'+checkExprId+'"  style="min-width:99%;min-height:24px;height:24px;width:104px;" rows="1" tabindex="4"/></td>'
			+ '<td  class="tblrowdata" width="16%" align="left"> <textarea  name="'+destinationExpression+'" class="'+autoSuggest+' '+tempCSS+'" id="'+destinationExprId+'" style="min-width:99%;min-height:24px;height:24px;width:104px;" rows="1" tabindex="4"/> </td>'
			+ '<td class="tblrowdata" width="15%" align="left"> <textarea name="'+sourceExpression+'"  value=""  class="'+autoSuggest+'" id="'+sourceExprId+'" style="min-width:99%;min-height:24px;height:24px;width:104px;" rows="1" tabindex="4"/> </td>'
			+ '<td class="tblrowdata" width="15%" align="left"> <textarea name="'+defaultValue+'"  class="'+autoSuggest+'" id="'+defaultValueId+'" style="min-width:99%;min-height:24px;height:24px;width:104px;" rows="1" tabindex="4"/> </td>'
			+ '<td class="tblrowdata" width="15%" align="left"> <textarea name="'+valueMapping+'"  id="'+valueMappingId+'" style="min-width:99%;min-height:24px;height:24px;width:104px;" rows="1" tabindex="4"/> </td>'
			+ '<td align="center" class="tblrowdata" width="4%"><img src="<%=request.getContextPath()%>/images/minus.jpg" onclick="removeRow('+i+');"  height="14" style="padding-right: 15px; padding-top: 5px; padding-left:15px; padding-bottom:5px; cursor:pointer;" tabindex="4"/></td>'
			+ '<td align="center" class="tblrowdata" width="4%"><img src="<%=request.getContextPath()%>/images/plus.jpg" class="details-control" id="'
			+ imageId
			+ '" onclick="toggleMe('
			+ i
			+ ');" style="padding-right: 15px; padding-top: 5px; padding-left:15px; padding-bottom:5px; cursor:pointer;" height="14" tabindex="4"/></td>'
			+ '</tr>'
			+ '<tr id="tempRow'+i+'" style="display: none;" class="tblrowdata"><td colspan="8"><table width="100%" class="tblrows" cellspacing="0" cellpadding="0" border="0"><tr><td class="labeltext" colspan="7">Operation<span class="elitehelp" id="Operation" onclick="openHelpDialog(this);">?</span></td><td><select id="tempDdlOperation'+i+'" name="'+operationTemp+'">'
			+'<%for(PacketOperations packetOperationData : PacketOperations.values()){%>'
			+'<option value="<%=((PacketOperations)packetOperationData).operation%>"><%=((PacketOperations)packetOperationData).operation%></option>'
			+'<%} %>'
			+ '</select></td></tr>'
			+ '<tr><td class="labeltext" colspan="7">Check Expression<span class="elitehelp" id="checkExpression" onclick="openHelpDialog(this);">?</span></td><td><textarea id="tempCheckExpr'+i+'" name="'+checkExpressionTemp+'" class="'+autoSuggest+'" tabindex="4"  cols="100" rows="1" style="min-width:99%;min-height:24px;height:24px;width:600px;" /></td></tr>'
			+ '<tr><td class="labeltext" colspan="7">Destination Expression<span class="elitehelp" id="destinationExpression" onclick="openHelpDialog(this);">?</span></td><td><textarea id="tempDestinationExpr'+i+'"  name="'+destinationExpressionTemp+'"  class="'+autoSuggest+'" tabindex="4"  cols="100" rows="1" style="min-width:99%;min-height:24px;height:24px;width:600px;" /></td></tr>'
			+ '<tr><td class="labeltext" colspan="7">Source Expression<span class="elitehelp" id="sourceExpression" onclick="openHelpDialog(this);">?</span></td><td><textarea id="tempSourceExpr'+i+'" name="'+sourceExpressionTemp+'"  class="'+autoSuggest+'" tabindex="4"  cols="100" rows="1" style="min-width:99%;min-height:24px;height:24px;width:600px;" /></td></tr>'
			+ '<tr><td class="labeltext" colspan="7">Default Value<span class="elitehelp" id="defaultValue" onclick="openHelpDialog(this);">?</span></td><td><textarea id="tempDefaultValue'+i+'" name="'+defaultValueTemp+'"  tabindex="4"  cols="100" rows="1" style="min-width:99%;min-height:24px;height:24px;width:600px;"/></td></tr>'
			+ '<tr><td class="labeltext" colspan="7">Value Mapping<span class="elitehelp" id="valueMapping" onclick="openHelpDialog(this);">?</span></td><td><textarea id="tempValueMapping'+i+'" name="'+valueMappingTemp+'"  tabindex="4"  cols="100" rows="1" style="min-width:99%;min-height:24px;height:24px;width:600px;"/></td></tr>'
			+ '<tr><td align="center" colspan="7" tabindex="4"></td><td align="left" colspan="7" tabindex="4"><input type="button" id="btnAdd" name="btnAdd" value="Add" class="light-btn" onclick="addData('
			+ i
			+ ');"> <input type="button" id="btnCancel'
			+ i
			+ '" name='+btnCancel+' value="Cancel" class="light-btn" onclick="toggleMe('
			+ i
			+ ')"></td></tr><tr><td class="labeltext" style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;" colspan="7"></td></tr>'
			+ '</table></td></tr></table></td></tr></table></td><td width="4%" class="hoverClass"></td></tr><input type="hidden" value='+currentOrder+' name='+orderNumber+' />';
			

	$("#"+tableId ).append(rowsdata);
	i++;
	if(translationFrom == 'Diameter'){
		retriveDiameterDictionaryDataAttributes();
	}else{
		retriveRadiusDictionaryAttributesForReferingAtrributes();
	}
	$("#"+tableId).tableDnD();

	}; 
	
	function addType() {
		mappingIndex++;
		var translationTo = "<%=createCopyPacketConfigForm.getSelectedToTranslatorTypeData().getName()%>";
		var translationFrom ="<%=createCopyPacketConfigForm.getSelectedFromTranslatorTypeData().getName()%>" ;
		var translationToId ="<%=createCopyPacketConfigForm.getSelectedToTranslatorType()%>" ;
		var translationFromId ="<%=createCopyPacketConfigForm.getSelectedFromTranslatorType()%>" ;
		$.ajax({
			   type: "POST",
			   url: "<%=basePath%>/jsp/servermgr/copypacket/DynamicCopyPacketData.jsp",
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
		if(translationFrom == 'Diameter'){
			retriveDiameterDictionaryDataAttributes();
		}else{
			retriveRadiusDictionaryAttributesForReferingAtrributes();
		}
		
	}
		

	function addDummyResponse(tableId){
		var outfield = "outField"+tableId ;
		var value = "value"+tableId ;
		var outFieldId = "outField" + tableId + dummyResp;
		var valueId = "value" + tableId + dummyResp;
		$("#"+tableId+" tr:last").after("<tr>"+
				"<td class='tblfirstcol'><input name="+outfield+" class='autosuggest validateOutField' type='text' id="+outFieldId+"  style='width: 100%;' /></td>"+
				"<td class='tblrowdata'> <input name="+value+" class='validateDummyParamValue' type='text' id="+valueId+"  style='width: 100%;' /> </td>"+
				"<td class='tblrowdata' align='center'> <img src='<%=request.getContextPath()%>/images/minus.jpg' onclick='javascript:$(this).parent().parent().remove();'  style='padding-right: 5px; padding-top: 5px; cursor:pointer;' height='14' /></td>"+
				"</tr>");
		
		if(translationFrom == 'Diameter'){
			retriveDiameterDictionaryDataAttributes();
		}else{
			retriveRadiusDictionaryAttributesForReferingAtrributes();
		}
		

	} 
	setTitle('<bean:message bundle="servermgrResources" key="copypacket.title"/>');
</script>
</head>
<body>
<html:form action="/createCopyPacketMappingConfDetail"
	styleId="mainform">

	<html:hidden name="createCopyPacketMappingConfDetailForm"
		property="action" value="save" />
<table width="100%">
<tr><td>
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="7">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header">Create Copy Packet Mapping Config</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="4">&nbsp;</td>
								</tr>

								<tr>
									<td colspan="4">

										<table width="100%" name="c_tblCrossProductList"
											id="c_tblCrossProductList" align="right" border="0">
											<tr>
												<td class="captiontext"><input type="button" id="btnAddMapping"
													value="Add Mapping" class="light-btn" tabindex="1" onclick="addType()"/></td>
											</tr>
									
								<tr>
												<td align="right">
													<div id="messageTypeDiv"></div>
												</td>
											</tr>
								<tr>
									<td class="labeltext">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="4">

										<table cellpadding="0" cellspacing="0" width="100%"
											id="c_tblCrossProductList" align="right" border="0">
											<tr>
												<td align=left class=tblheader-bold valign=top colspan=2>
													Default Mapping</td>
												<td class="tblheader-bold" align="right" width="15px"><img
													id="toggleImageElement0" onclick="toggleMappingDiv('0')"
													src="images/top-level.jpg" /></td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="4">

										<div id="toggleDivElement0">

											<table width="100%" id="c_tblCrossProductList" align="right"
												border="0">


												<tr>
													<td class="labeltext"
														style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
												</tr>

												<tr>
													<td align="right" class="labeltext" valign="top"
														colspan="4">
														<table cellpadding="0" cellspacing="0" class="box"
															width="100%">
															<tr>
																<td class="table-header">Request Parameters</td>
															</tr>
															<tr>
																<td class="labeltext"
																	style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
															</tr>
															<tr>
																<td class="labeltext"><input type="button"
																	class="light-btn" value="Add New Mapping" tabindex="3"
																	onclick="addNewMapping('defaultRequestMapping0')" /></td>
															</tr>
															<tr>
																<td class="labeltext"
																	style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
															</tr>
															<tr>
																<td class="labeltext" width="100%">
																	<table id="defaultRequestMapping0" cellpadding="0"
																		cellspacing="0" border="0" width="100%">
																		<thead width="100%">
																		<!-- <table border="0" cellspacing="0" cellpadding="0" width="100%"> -->
																		<tr>
																		
																				<td align=left class=tblheader valign=top width="11%">
																					<bean:message
																						bundle="servermgrResources"
																							key="copypacket.parameter.operation" /> 
																								<ec:elitehelp headerBundle="servermgrResources" 
																									text="copypacket.parameter.operation" 
																										header="copypacket.parameter.operation"/>
 																				</td>
																			<td align=left class=tblheader valign=top width="16%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.checkexpression" />
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.checkexpression" 
																								header="copypacket.parameter.checkexpression"/> 
																			</td>
																			<td align=left class=tblheader valign=top width="16%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.destinationexpression" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.destinationexpression" 
																								header="copypacket.parameter.destinationexpression"/>
																			</td>
																			<td align=left class=tblheader valign=top width="15%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.sourceexpression" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.sourceexpression" 
																								header="copypacket.parameter.sourceexpression"/>
																			</td>
																			<td align=left class=tblheader valign=top width="15%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.defaultvalue" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.defaultvalue" 
																								header="copypacket.parameter.defaultvalue"/>
																			</td>
																			<td align=left class=tblheader valign=top width="15%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.valuemapping" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.valuemapping" 
																								header="copypacket.parameter.valuemapping"/>
																			</td>
																			<td align="left" class="tblheader" valign="top"
																				width="4%">Remove</td>
																			<td align="left" class="tblheader" valign="top"
																				width="4%">Expand</td>
																				<td align="left" class="tblheader" valign="top"
																				width="4%">Order</td>
																		</thead>
																		<tbody></tbody>
																	</table>
																</td>
															</tr>
															<tr>
																<td class="labeltext"
																	style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
															</tr>
														</table>
													</td>
												</tr>

												<tr>
													<td class="labeltext"
														style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
												</tr>
												<tr>
													<td colspan="4" align="right">
														<table cellpadding="0" cellspacing="0" class="box"
															width="100%">
															<tr>
																<td class="table-header">Response Parameter</td>
															</tr>
															<tr>
																<td class="labeltext"
																	style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
															</tr>
															<tr>
																<td class="labeltext"><input type="button"
																	class="light-btn" value="Add New Mapping" tabindex="5"
																	onclick="addNewMapping('defaultResponseMapping0')" /></td>
															</tr>
															<tr>
																<td class="labeltext"
																	style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
															</tr>
															<tr>
																<td class="labeltext">
																	<table id="defaultResponseMapping0" cellpadding="0"
																		cellspacing="0" border="0" width="100%">
																		<thead>
																		
																		<tr>
																			<td align=left class=tblheader valign=top width="11%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.operation" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.operation" 
																								header="copypacket.parameter.operation"/>
																			</td>
																			<td align=left class=tblheader valign=top width="16%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.checkexpression" />
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.checkexpression" 
																								header="copypacket.parameter.checkexpression"/>																			
																			</td>
																			<td align=left class=tblheader valign=top width="16%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.destinationexpression" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.destinationexpression" 
																								header="copypacket.parameter.destinationexpression"/>
																			</td>
																			<td align=left class=tblheader valign=top width="15%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.sourceexpression" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.sourceexpression" 
																								header="copypacket.parameter.sourceexpression"/>																			
																			</td>
																			<td align=left class=tblheader valign=top width="15%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.defaultvalue" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.defaultvalue" 
																								header="copypacket.parameter.defaultvalue"/>																			
																			</td>
																			<td align=left class=tblheader valign=top width="15%">
																				<bean:message bundle="servermgrResources"
																					key="copypacket.parameter.valuemapping" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.parameter.valuemapping" 
																								header="copypacket.parameter.valuemapping"/>										
																			</td>
																			<td align="left" class="tblheader" valign="top"
																				width="4%">Remove</td>
																			<td align="left" class="tblheader" valign="top"
																				width="4%">Expand</td>
																				<td align="left" class="tblheader" valign="top"
																				width="4%">Order</td>
																		</tr>
																		</thead>
																		<tbody></tbody>
																	</table>
																</td>
															</tr>

															<tr>
																<td class="labeltext"
																	style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
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

										<table cellpadding="0" cellspacing="0" width="100%"
											id="c_tblCrossProductList" align="right" border="0">
											<tr>
												<td align=left class=tblheader-bold valign=top colspan=2>Dummy
													Response Parameters</td>
												<td class="tblheader-bold" align="right" width="15px"><img
													id="toggleDummyParamImageElement"
													src="images/bottom-level.jpg" onclick="toggleDummyParameters()"/></td>
											</tr>
										</table>
									</td>
								</tr>

								<tr>
									<td colspan="4">
										<div id="toggleDummyParamDivElement" style="display:none;" >
											<table width="100%" id="c_tblCrossProductList" align="right"
												border="0">
												<tr>
													<td colspan="4" align="right">
														<table cellpadding="0" cellspacing="0" border="0"
															width="95%">
															<tr>
																<td class="labeltext"
																	style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
															</tr>
															<tr>
															
																<td class="labeltext" colspan="4" tabindex="6"><input
																	type="button" class="light-btn" value="Add New Mapping" onclick="addDummyResponse('tableDummyResponseParams');"/></td>
															</tr>
															<tr>
																<td class="labeltext"
																	style="padding-right: 15px; padding-left: 15px; padding-top: 7px; padding: bottom:7px;"></td>
															</tr>
															<tr>
																<td class="labeltext" colspan="4">
																	<table id="tableDummyResponseParams" cellpadding="0"
																		cellspacing="0" border="0" width="70%">
																		<tr>
																			<td align="left" class="tblheader" valign="top" width="48%">
																				<bean:message bundle="servermgrResources" 
																					key="copypacket.dummyesponseparameters.outfield" /> 
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.dummyesponseparameters.outfield" 
																								header="copypacket.dummyesponseparameters.outfield"/>
																			</td>
																			<td align="left" class="tblheader" valign="top" width="47%">
																				<bean:message bundle="servermgrResources" 
																					key="copypacket.dummyesponseparameters.value" />
																						<ec:elitehelp headerBundle="servermgrResources" 
																							text="copypacket.dummyesponseparameters.value" 
																								header="copypacket.dummyesponseparameters.value"/>
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
										tabindex="6" onclick="return validateForm()"> <input
										type="reset" name="c_btnDeletePolicy"
										onclick="javascript:window.location.href='<%=basePath%>/initSearchCopyPacketMappingConfig.do?'"
										value="Cancel" class="light-btn" tabindex="7" /></td>
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
	<td width="7">&nbsp;</td>
	
	</tr></table></td></tr></table>

</html:form>
</body>
</html>