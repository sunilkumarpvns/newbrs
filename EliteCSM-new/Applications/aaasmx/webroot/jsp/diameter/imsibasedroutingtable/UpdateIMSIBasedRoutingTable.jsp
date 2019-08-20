<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.UpdateDiameterPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData" %>
<%@ page import="com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData" %>

<%
	IMSIBasedRoutingTableForm imsiBasedRoutingTableForm = (IMSIBasedRoutingTableForm)request.getAttribute("imsiBasedRoutingTableForm");
	IMSIBasedRoutingTableData imsiBasedRoutingTableData = (IMSIBasedRoutingTableData)request.getAttribute("imsiBasedRoutingTableData");
%>
<style type="text/css">
	.ui-dialog .ui-dialog-titlebar-close {
	    background: none !important;
	    border: none;
	    cursor:pointer;
	}
</style>

<script type="text/javascript">

var isValidName;
var oTable;
function validate()
{
	
	var imsiIdentityAttributes = $('#imsiIdentityAttributes').val();
	if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return;
	}else if(imsiIdentityAttributes.length == 0){
		alert('Enter IMSI Identity Attribute');
		document.forms[0].imsiIdentityAttributes.focus();
		return;
	}
	document.forms[0].action.value="update";
	document.forms[0].submit();
}
	
function  checkAll(){
	var arrayCheck = document.getElementsByName('select');

	if( document.forms[0].toggleAll.checked == true) {
		for (i = 0; i < arrayCheck.length;i++)
			arrayCheck[i].checked = true ;
	} else if (document.forms[0].toggleAll.checked == false){
		for (i = 0; i < arrayCheck.length; i++)
			arrayCheck[i].checked = false ;
	} 
}

function verifyName() {
 	var searchName = document.getElementById("imsiBasedRoutingTableName").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.IMSI_BASED_ROUTING_TABLE%>',searchName,'update','<%=imsiBasedRoutingTableData.getRoutingTableId()%>','verifyNameDiv');
	if(isValidName){
		var routingTableIdStr;
		$.ajax({url:'<%=request.getContextPath()%>/initUpdateIMSIBasedRouting.do',
	         type:'POST',
	         data:{action:'retriveMSISDNData',routingTableName:searchName},
	         async:false,
	         success: function(routingId){
	        	if(routingId == "0"){
	        		routingTableIdStr = 0;
	        	}else{
	        		routingTableIdStr = routingId;
	        	}
	         }
	    });
		if(routingTableIdStr != 0){
			$('#verifyNameDiv').html("<img src='<%=request.getContextPath()%>/images/cross.jpg'/> <font color='#FF0000'>Already Exists.</font>");
			
		}
	}
}

function validateImport(){
	var fileName = $("#fileUpload").val();

    if(fileName) { 
    	document.forms[0].action.value="importCSV";    	
        return true;
    } else { 
        alert("Please upload file before import");
   		return false;
    }
}
function enabledDiameterPeerDropDown(){
	document.getElementById("diameterPeer").disabled = false;
}
function disabledDiameterPeerDropDown(){
	document.getElementById("diameterPeer").disabled = true;
}
function addIMSIMappings(fieldMapId,imsiRangeVal,primaryPeerVal,secondaryPeerVal,tagValue){
	
	var tableRowStr = $("#imsiTableTemplate").find("tr");
	$("#imsiTableMapping").append("<tr>"+$(tableRowStr).html()+"</tr>");
	var tableObj1= $('#imsiTableMapping').find("tr:last");
	
	var checkBoxObj = $(tableObj1).find("input:checkbox[name='select']");
	$(checkBoxObj).attr('id',fieldMapId);
	$(checkBoxObj).attr('value',fieldMapId);
	
	var hiddenFieldObj = $(tableObj1).find("input[name='hidden_fieldmap']");
	$(hiddenFieldObj).attr('value',fieldMapId);
	
	var imsiRangeObj = $(tableObj1).find("input[name='imsiRange']");
	$(imsiRangeObj).attr('value',imsiRangeVal);
	$(imsiRangeObj).val(imsiRangeVal);
	$(imsiRangeObj).addClass('imsiRangeClass');
	
	var primaryPeerObj=$(tableObj1).find("select[name='primaryPeer']");
	$(primaryPeerObj).val(primaryPeerVal);
	$(primaryPeerObj).addClass('primaryPeerClass');
	
	var secondaryPeerObj=$(tableObj1).find("select[name='secondaryPeer']");
	$(secondaryPeerObj).val(secondaryPeerVal);
	
	var tagObj = $(tableObj1).find("input[name='tag']");
	$(tagObj).val(tagValue);
	
}

function addEntries(){
	var imsiRange = $('#imsiRange').val();
	var primaryPeer = $('#primaryPeer').val();
	var secondaryPeer = $('#secondaryPeer').val();
	var tag=$('#tag').val();
	var routingTableName = $('#imsiBasedRoutingTableName').val();
	var auditUId = $('#auditUId').val();
	
	var primarySelect = $("<select/>");
	 $('#primaryPeer option').each(function(){
		 var option = $("<option/>");
		 $(option).text($(this).text());
		 $(option).val($(this).val());
		 if($(this).val() == primaryPeer) {
			 $(option).attr("selected","selected");
		 }
		 $(primarySelect).append(option);
	 });
	 
	
	 var secondarySelect = $("<select/>");
	 $('#secondaryPeer option').each(function(){
		 var option = $("<option/>");
		 $(option).text($(this).text());
		 $(option).val($(this).val());
		 if($(this).val() == secondaryPeer) {
			 $(option).attr("selected","selected");
		 }
		 $(secondarySelect).append(option);
	 });
	
	 if(imsiRange.length == 0){
		 alert('Please Enter IMSI Range');
		 $('#imsiRange').focus();
		 return false;
	 }else if(primaryPeer == "0"){
		 alert('Please Select Primary Peer');
		 $('#primaryPeer').focus();
		 return false;
	 }else if( $('#primaryPeer').val() == $('#secondaryPeer').val() ){
		 alert('Primary Peer and Secondary Peer must be different');
		 $('#secondaryPeer').focus();
		 return false;
	 }else{
		$.ajax({url:'<%=request.getContextPath()%>/initUpdateIMSIBasedRouting.do',
	         type:'POST',
	         data:{action:'addEntries',routingTableId:'<%=imsiBasedRoutingTableData.getRoutingTableId()%>',imsiRange:imsiRange,primaryPeer:primaryPeer,secondaryPeer:secondaryPeer,tag:tag,routingTableName:routingTableName,auditUId:auditUId},
	         async:false,
	         success: function(mappingId){
	        	 oTable.fnAddData( [
	    		            		"<td style='font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px;' width='1%'><input type='checkbox' name='select' id='"+mappingId+"' value='"+mappingId+"'/><input type='hidden' name='hidden_fieldmap' value='"+mappingId+"' /></td> ",
	    		            		"<td class='allborder' width='24.75%'><input type='text' style='width:100%' class='noborder' name='imsiRange' value='"+imsiRange+"'/></td>",
	    		            		"<td class='tblrows' width='24.75%'><select style='width:100%;' onchange='validatePrimaryPeer(this);' class='noborder data-table-primary-peer primaryPeerClass' name='primaryPeer'>"+ $(primarySelect).html() + "</select></td>",
	    		            		"<td class='tblrows' width='24.75%'><select style='width:100%;' onchange='validateSecondaryPeer(this);' class='noborder data-table-secondary-peer' name='secondaryPeer'>"+ $(secondarySelect).html()+"</select></td>",
	    		            		"<td class='tblrows' width='24.75%'><input type='text' style='width:100%' class='noborder' name='tag' value='"+tag+"'/></td>"] );
	         
	        	
	        	$('#imsiRange').val("");
	        	$('#primaryPeer').val("0");
	        	$('#secondaryPeer').val("0");
	        	$('#tag').val("");
	        	
	        	oTable.fnPageChange( 'last' );
	         }
	    });
	}
}
$(document).ready(function(){
	<%  Set<IMSIFieldMappingData> imsiFieldMappingDataSet = imsiBasedRoutingTableForm.getImsiFieldMappingSet();
		if(imsiFieldMappingDataSet != null && imsiFieldMappingDataSet.size() > 0){
			for(IMSIFieldMappingData imsiFieldMappingData : imsiFieldMappingDataSet){%>
				addIMSIMappings('<%=imsiFieldMappingData.getImsiFieldMapId()%>','<%=(imsiFieldMappingData.getImsiRange() == null ? "" :imsiFieldMappingData.getImsiRange())%>','<%=(imsiFieldMappingData.getPrimaryPeerName() == null ? "0" : imsiFieldMappingData.getPrimaryPeerName())%>','<%=(imsiFieldMappingData.getSecondaryPeerName() == null ? "0" :imsiFieldMappingData.getSecondaryPeerName())%>','<%=(imsiFieldMappingData.getTag() == null ? "" :imsiFieldMappingData.getTag())%>');
		<%}
	}%>
	
	
	$( ".data-table-primary-peer" ).change(function() {
		  var secondaryPeerObj = $(this).closest("tr").find('.data-table-secondary-peer');
		  if( $(secondaryPeerObj).val() != "0" && $(this).val() != "0"){
			  if( $(secondaryPeerObj).val() == $(this).val() ){
				  alert('Primary Peer and Secondary Peer must be different');
				  $(this).val("0");
				  $(this).focus();
				  return false;
			  }
		  }
	});
	
	$( ".data-table-secondary-peer" ).change(function() {
		 var primaryPeerObj = $(this).closest("tr").find('.data-table-primary-peer');
		 if( $(primaryPeerObj).val() != "0" && $(this).val() != "0"){
			 if( $(primaryPeerObj).val() == $(this).val() ){
				  alert('Primary Peer and Secondary Peer must be different');
				  $(this).val("0");
				  $(this).focus();
				  return false;
			  }
		 }
	});


	oTable = $("#imsiTableMapping").dataTable({
	    "bPaginate":true,
	    "sPaginationType":"full_numbers",
	    "aLengthMenu": [[10, 25, 100, -1], [10, 25, 100, "All"]],
	    "iDisplayLength": 10,
	    "sDom": 'Rlfrtip',
	    "columns": [
	                null,
	                { "orderDataType": "dom-text", type: 'string'},
	                { "orderDataType": "dom-select"},
	                { "orderDataType": "dom-select" },
	                {"orderDataType" : "dom-text"}
	            ],
	     "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
	                $('td:eq(0)', nRow).addClass( "allborder" );
	                $('td:eq(1),td:eq(2),td:eq(3),td:eq(4)', nRow).addClass( "allborder" );
	              },
	     "aoColumns": [
	        	   { "bSortable": false },
	                  null,
	                  null,
	                  null,
	                  null
	                ]
	});
	
	$('input:radio[id=exportAll]').prop('checked', true);
	
});


	/* Create an array with the values of all the input boxes in a column */
	$.fn.dataTable.ext.order['dom-text'] = function  ( settings, col )
	{
	    return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) {
	        return $('input', td).val();
	    } );
	}
	 
	/* Create an array with the values of all the input boxes in a column, parsed as numbers */
	$.fn.dataTable.ext.order['dom-text-numeric'] = function  ( settings, col )
	{
	    return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) {
	        return $('input', td).val() * 1;
	    } );
	}
	 
	/* Create an array with the values of all the select options in a column */
	$.fn.dataTable.ext.order['dom-select'] = function  ( settings, col )
	{
	    return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) {
	        return $('select', td).val();
	    } );
	}
	 
	/* Create an array with the values of all the checkboxes in a column */
	$.fn.dataTable.ext.order['dom-checkbox'] = function  ( settings, col )
	{
	    return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) {
	        return $('input', td).prop('checked') ? '1' : '0';
	    } );
	}

	function exportData(){
		var radioData = document.querySelector('input[name="exportGroup"]:checked').value;
		if(radioData == 'exportPeerWise'){
			var peerId = $('#diameterPeer').val();
			if(peerId == '0'){
				alert('Please Select Peer from Dropdown');
				$('#diameterPeer').focus();
			}else{
				location.href='<%=basePath%>/exportIMSIConfig.do?routingTableId=<%=imsiBasedRoutingTableData.getRoutingTableId()%>&peerUUID='+peerId;
			}
		}else{
			location.href='<%=basePath%>/exportIMSIConfig.do?routingTableId=<%=imsiBasedRoutingTableData.getRoutingTableId()%>';
		}
	}
	
	function setAVPsData(){
		var imsiIdentityAttributes = document.getElementById("imsiIdentityAttributes").value;
		retriveDiameterDictionaryAttributesDatas("imsiIdentityAttributes");
	}

	function retriveDiameterDictionaryAttributesDatas(txtFields) {
		var myArray = new Array();
		var dbFieldStr;
		var dbFieldArray;
		var searchNameOrAttributeId="";
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
			setAVPsDataList(txtFields,myArray);
			return dbFieldArray;
		});
	}
	
	function splitDbFields( val ) {
		return val.split( /[,;]\s*/ );
	}
	
	function extractLastDbFields( term ) {
		return splitDbFields( term ).pop();
	}

	function  setAVPsDataList(dbFieldObject,arrayList){
		
		 $( '#' + dbFieldObject ).bind( "keydown", function( event ) {
				if ( event.keyCode === $.ui.keyCode.TAB &&
					$( this ).autocomplete( "instance" ).menu.active ) {
					event.preventDefault();
				}
		 }).autocomplete({
			minLength: 0,
			source: function( request, response ) {
				response( $.ui.autocomplete.filter(
					arrayList, extractLastDbFields( request.term ) ) );
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
	
	function updateRoutingEntries(){
		
		var selectArray = document.getElementsByName('select');
		
		if(selectArray.length > 1){
			var isFlag=true;
			$(".imsiRangeClass").each(function(key,value){
					if($(this).val().length == 0){
						alert("Please enter IMSI Range");
						$(this).focus();
						isFlag=false;
						return false;
					}
					
			});
			
			$(".primaryPeerClass").each(function(key,value){
				if($(this).val() == "0"){
					alert("Please Primary Peer");
					$(this).focus();
					isFlag=false;
					return false;
				}
			});
			
			if(isFlag){
				document.forms[0].action.value="updateEntries";
				document.forms[0].submit();
			}
		}
		else{
			alert("No records found for update operation! ");
		}
	}
	
	function deleteRoutingEntries(){
		var selectArray = document.getElementsByName('select');
		
		if(selectArray.length>0){
			var b = true;
			for (i=0; i<selectArray.length; i++){

		 		 if (selectArray[i].checked == false){  			
		 		 	b=false;
		 		 }
		 		 else{
		 		 	
					b=true;
					break;
				}
			}
			
			if(b==false){
				alert("Selection Required To Perform Delete Operation.");
			}else{
				var r=confirm("This will delete the selected items. Do you want to continue ?");
					if (r==true)
		  			{
						document.forms[0].action.value="deleteEntries";
						document.forms[0].submit();
		  			}
			}
		}
		else{
			alert("No Records Found For Delete Operation! ");
		}
	}
	
	function validatePrimaryPeer(primaryPeer){
		  var secondaryPeerObj = $(primaryPeer).closest("tr").find('.data-table-secondary-peer');
		  if( $(secondaryPeerObj).val() != "0" && $(primaryPeer).val() != "0"){
			  if( $(secondaryPeerObj).val() == $(primaryPeer).val() ){
				  alert('Primary Peer and Secondary Peer must be different');
				  $(primaryPeer).val("0");
				  $(primaryPeer).focus();
				  return false;
			  }
		  }
	}
	
	function validateSecondaryPeer(secondaryPeer){
		 var primaryPeerObj = $(secondaryPeer).closest("tr").find('.data-table-primary-peer');
		 if( $(primaryPeerObj).val() != "0" && $(secondaryPeer).val() != "0"){
			 if( $(primaryPeerObj).val() == $(secondaryPeer).val() ){
				  alert('Primary Peer and Secondary Peer must be different');
				  $(secondaryPeer).val("0");
				  $(thsecondaryPeeris).focus();
				  return false;
			  }
		 }
	}
</script>
<LINK REL="stylesheet" TYPE="text/css" href="<%=request.getContextPath()%>/js/datatable/jquery.dataTables.css"/> 
 <script src="<%=request.getContextPath()%>/js/datatable/jquery-1.11.1.min.js"></script> 
<script src="<%=request.getContextPath()%>/js/datatable/jquery.dataTables.min.js"></script>
<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js "></script>
<html:form action="/initUpdateIMSIBasedRouting" styleId="imsiBasedRoutingTableForm" enctype="multipart/form-data">

	<html:hidden name="imsiBasedRoutingTableForm" styleId="action" property="action" value="update" />
	<html:hidden name="imsiBasedRoutingTableForm" styleId="routingTableId" property="routingTableId" />
	<html:hidden name="imsiBasedRoutingTableForm" styleId="auditUId" property="auditUId" />
		
	<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">

		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td valign="middle" colspan="5">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
					<tr>
						<td class="table-header" colspan="5">
							<bean:message bundle="diameterResources" key="imsibasedroutingtable.title" />
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="diameterResources" key="imsibasedroutingtable.title" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="imsibasedroutingtable.routingtablename" /> 
							<ec:elitehelp headerBundle="diameterResources"  text="imsibasedroutingtable.routingtablename" header="imsibasedroutingtable.routingtablename"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text styleId="imsiBasedRoutingTableName" tabindex="1" property="imsiBasedRoutingTableName" size="30" onkeyup="verifyName();" maxlength="60" style="width:250px" />
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="imsibasedroutingtable.imsiidentityattribute" /> 
							<ec:elitehelp headerBundle="diameterResources"  text="imsibasedroutingtable.imsiidentityattribute" header="imsibasedroutingtable.imsiidentityattribute"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text styleId="imsiIdentityAttributes" onfocus="setAVPsData();" tabindex="1" property="imsiIdentityAttributes" size="30"  maxlength="60" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>
							<input type="button" value=" Update " class="light-btn" onclick="validate()" tabindex="20" />
							<input type="button" name="cancel" id="cancelbtn" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchSubscriberRoutingTable.do?method=initSearch'"/>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="diameterResources" key="imsibasedroutingtable.addnewentries" />
						</td>
					</tr>
					<tr>
						<td colspan="2" style="padding-top: 10px;">
								<table width="100%" cellspacing="0" cellpadding="0" border="0"> 
									<tr>
										<td class="captiontext" width="25%">
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.imsirange" /> 
											<ec:elitehelp headerBundle="diameterResources"  text="routingconf.imsibasedrouting.imsirange" header="routingconf.imsibasedrouting.imsirange" note="routingconf.imsibasedrouting.imsirange"/>
										</td>
										<td class="labeltext">
											<input type="text" name="imsiRangeData" id="imsiRange"  style="width: 250px;"/>
											<font color="#FF0000"> *</font>
										</td>
									</tr>
									<tr>
										<td class="captiontext" width="25%">
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.primarypeer" /> 
											<ec:elitehelp headerBundle="diameterResources"  text="routingconf.imsibasedrouting.primarypeer" header="routingconf.imsibasedrouting.primarypeer"/>
										</td>
										<td class="labeltext">
											<select name="primaryPeerData" id="primaryPeer" style="width: 120px;" >
												<option value="0">-Select-</option>
												<logic:iterate id="peerData" name="diameterPeerDataList" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
													<option value="<%=peerData.getName()%>"><%=peerData.getName() %></option>
												</logic:iterate>
											</select>
											<font color="#FF0000"> *</font>
										</td>
									</tr>
									<tr>
										<td class="captiontext" width="25%">
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.secondarypeer" /> 
											<ec:elitehelp headerBundle="diameterResources"  text="routingconf.imsibasedrouting.secondarypeer" header="routingconf.imsibasedrouting.secondarypeer"/>
										</td>
										<td class="labeltext">
											<select name="secondaryPeerData" id="secondaryPeer" style="width: 120px;">
												<option value="0">-Select-</option>
												<logic:iterate id="peerData" name="diameterPeerDataList" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
													<option value="<%=peerData.getName()%>"><%=peerData.getName() %></option>
												</logic:iterate>
											</select>
										</td>
									</tr>
									<tr>
										<td class="captiontext" width="25%">
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.tag" /> 
											<ec:elitehelp headerBundle="diameterResources"  text="routingconf.imsibasedrouting.tag" header="routingconf.imsibasedrouting.tag"/>
										</td>
										<td class="labeltext">
											<input type="text" name="tagData" id="tag"  style="width: 250px;"/>
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
									<tr>
										<td class="captiontext" width="25%">
										</td>
										<td class="labeltext">
											<input type="button" value=" Add Entry " class="light-btn" onclick="addEntries();"/> 
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
									</tr>
								</table>							
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="diameterResources" key="imsibasedroutingtable.routingentries" />
						</td>
					</tr>
					<tr>
						<td class="captiontext captiontext">
							<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.export" />
							<ec:elitehelp headerBundle="diameterResources"  text="routingconf.imsibasedrouting.export" header="routingconf.imsibasedrouting.export"/>
						</td>
						<td class="labeltext" style="padding-top: 10px;">
							<input type="radio" name="exportGroup" id="exportAll" value="exportAll"  onclick="disabledDiameterPeerDropDown();" >All</input>
							<input type="radio" name="exportGroup"  id="exportPeerWise" value="exportPeerWise" onclick="enabledDiameterPeerDropDown();">Export Peer Wise</input>&nbsp;&nbsp;&nbsp;&nbsp;
								<select name="diameterPeer" id="diameterPeer" disabled="disabled" style="width: 100px;">
									<option value="0">-Select-</option>
									<logic:iterate id="peerData" name="diameterPeerDataList" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
										<option value="<%=peerData.getPeerUUID()%>"><%=peerData.getName() %></option>
									</logic:iterate>
								</select>
								&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="button" name="exportEntries" value=" Export " class="light-btn export" onclick="exportData();"/>
						</td>
					</tr>
					<tr>
						<td class="captiontext captiontext" >
							<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.import" />
							<ec:elitehelp headerBundle="diameterResources"  text="routingconf.imsibasedrouting.import" header="routingconf.imsibasedrouting.import"/>
						</td>
						<td class="labeltext">
							<input type="file" name="fileUpload" id="fileUpload" class="uploadIPFile" tabindex="8" style="width: 252px;"/>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="submit" name="Update" value=" Import "  onclick="return validateImport();" class="light-btn" /> 
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="captiontext">
							<input type="button" name="edit" id="editBtn" value=" Update " class="light-btn" onclick="updateRoutingEntries();"/>
							<input type="button" name="delete" id="deleteBtn" value=" Delete " class="light-btn" onclick="deleteRoutingEntries();"/>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="captiontext">
							<div id="dvData">
							<table width="100%" cellspacing="0" cellpadding="0" border="0" id="imsiTableMapping" class="imsiTableMapping display" >
								<thead>
									<tr>	
										<td class="tblheader table-header-toggle" width="1%">	
											<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()" />
										</td>
										<td class="tblheader" width="24.75%">	
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.imsirange" />
										</td>
										<td class="tblheader" width="24.75%">	
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.primarypeer" />
										</td>
										<td class="tblheader" width="24.75%">	
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.secondarypeer" />
										</td>
										<td class="tblheader" width="24.75%">	
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.tag" />
										</td>
									</tr>
								</thead>
								<tboby>
								</tboby>
							</table>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>

<table width="100%" cellspacing="0" cellpadding="0" border="0" style="display: none;" id="imsiTableTemplate" class="imsiTableTemplate">
				<tr>	
					<td class="allborder" width="1%">
						<input type="checkbox" name="select" id="chk_" value=""/>
						<input type="hidden" name="hidden_fieldmap" value="" />
					</td> 
					<td class="allborder" width="24.75%">	
						<input type="text"  style="width:100%" class="noborder" name="imsiRange"/> 
			 		</td>
					<td class="tblrows" width="24.75%">
						<select name="primaryPeer" style="width: 100%;" class="noborder data-table-primary-peer">
							<option value="0">-Select-</option>
							<logic:iterate id="peerData" name="diameterPeerDataList" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
								<option value="<%=peerData.getName()%>"><%=peerData.getName() %></option>
							</logic:iterate>
						</select>
					</td>
					<td class="tblrows" width="24.75%">
						<select name="secondaryPeer" style="width: 100%;" class="noborder data-table-secondary-peer">
							<option value="0">-Select-</option>
							<logic:iterate id="peerData" name="diameterPeerDataList" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
								<option value="<%=peerData.getName()%>"><%=peerData.getName() %></option>
							</logic:iterate>
						</select>
					</td>
					<td class="tblrows" width="24.75%">	
						<input type="text"  style="width:100%" class="noborder" name="tag"/> 
			 		</td>
				</tr>
			</table>
